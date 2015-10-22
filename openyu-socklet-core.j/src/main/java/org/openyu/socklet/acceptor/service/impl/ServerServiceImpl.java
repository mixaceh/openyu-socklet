package org.openyu.socklet.acceptor.service.impl;

import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.ObjectHelper;
import org.openyu.commons.lang.SystemHelper;
import org.openyu.commons.nio.NioHelper;
import org.openyu.commons.security.AuthKey;
import org.openyu.commons.security.AuthKeyService;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.commons.thread.supporter.BaseRunnableSupporter;
import org.openyu.commons.thread.supporter.TriggerQueueSupporter;
import org.openyu.socklet.acceptor.service.ServerService;
import org.openyu.socklet.connector.vo.AcceptorConnector;
import org.openyu.socklet.connector.vo.ConnectorType;
import org.openyu.socklet.connector.vo.GenericConnector;
import org.openyu.socklet.connector.vo.GenericRelation;
import org.openyu.socklet.connector.vo.impl.AcceptorConnectorImpl;
import org.openyu.socklet.connector.vo.impl.GenericRelationImpl;
import org.openyu.socklet.context.service.impl.ContextServiceImpl;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.session.vo.Session;
import org.openyu.socklet.session.vo.impl.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 伺服器服務,3條thread
 *
 * listen ok
 *
 * read ok
 *
 * write 目前暫時沒用到,以後有用再說吧
 */
public class ServerServiceImpl extends BaseServiceSupporter implements ServerService {

	private static final long serialVersionUID = 5754645204772568156L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(ServerServiceImpl.class);

	/**
	 * localhost:3100
	 */
	private String id;

	// localhost
	private String ip;

	// 3100
	private int port;

	// 5 ,counter=866
	// 50 ,counter=980
	// 100,counter=1055
	private static final int DEFAULT_BACKLOG = 100;

	private int maxClient;

	// ------------------------------------------------
	// 是否當內部server
	private boolean relationServer;

	// 取acceptorService實作,因會用到acceptor上的內部方法
	private AcceptorServiceImpl acceptorService;

	private ContextServiceImpl contextService;

	private ProtocolService protocolService;

	private AuthKeyService authKeyService;

	/**
	 * 模組類別
	 */
	@SuppressWarnings("rawtypes")
	private Class moduleTypeClass;

	/**
	 * 訊息類別
	 */
	@SuppressWarnings("rawtypes")
	private Class messageTypeClass;

	/**
	 * 是否已啟動
	 */
	private boolean started;

	private ServerSocketChannel serverChannel;

	private AtomicInteger counter = new AtomicInteger(0);

	// ------------------------------------------------
	// aceptor id, master,slave1...n
	private String acceptorId;

	// [slave1][3100]
	private String acceptorServer;

	private Selector selector;

	// <localhost:3100,sockletServer>
	private Map<String, ServerService> serverServices = new ConcurrentHashMap<String, ServerService>();

	// 來自於內部其他server的client連線
	private Map<String, GenericRelation> passiveRelations = new ConcurrentHashMap<String, GenericRelation>();

	// 來自於外部client的連線
	// <client.sender,client>
	private Map<String, AcceptorConnector> acceptorConnectors = new ConcurrentHashMap<String, AcceptorConnector>();

	// ------------------------------------------------
	// listen
	// ------------------------------------------------
	/**
	 * 監聽毫秒
	 */
	private static final long LISTEN_MILLS = 1L;

	// ------------------------------------------------
	// read
	// ------------------------------------------------
	/**
	 * 讀取key佇列
	 */
	private ReadKeyQueue<SelectionKey> readKeyQueue = new ReadKeyQueue<SelectionKey>();

	// ------------------------------------------------
	// write 目前暫時沒用到,以後有用再說吧
	// ------------------------------------------------

	public ServerServiceImpl(String id, boolean inernal, AcceptorServiceImpl acceptorService) {
		this.applicationContext = acceptorService.getApplicationContext();
		this.threadService = acceptorService.getThreadService();
		//
		this.id = id;
		this.relationServer = inernal;
		this.acceptorService = acceptorService;
		this.contextService = acceptorService.contextService;
		this.protocolService = acceptorService.protocolService;
		this.authKeyService = acceptorService.authKeyService;
		this.moduleTypeClass = acceptorService.moduleTypeClass;
		this.messageTypeClass = acceptorService.messageTypeClass;
		this.acceptorId = acceptorService.getId();
		this.acceptorConnectors = acceptorService.getAcceptorConnectors();
		this.passiveRelations = acceptorService.getPassiveRelations();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxClient() {
		return maxClient;
	}

	public void setMaxClient(int maxCounter) {
		this.maxClient = maxCounter;
	}

	public int getCounter() {
		return counter.get();
	}

	public void start() {
		try {
			if (!started) {
				// [slave1][3000]
				// acceptorServer = "[" + acceptorId + "][" + port + "] ";
				// [slave1]
				acceptorServer = "[" + acceptorId + "] ";
				/*
				 * OP_READ: 1 OP_WRITE: 4 OP_CONNECT: 8 OP_ACCEPT: 16
				 */
				serverChannel = ServerSocketChannel.open();
				serverChannel.configureBlocking(false);
				ServerSocket serverSocket = serverChannel.socket();
				InetSocketAddress address = NioHelper.createInetSocketAddress(ip, port);
				// serverChannel.socket().setReuseAddress(true);
				// serverChannel.socket().bind(address, DEFAULT_BACKLOG);
				serverSocket.bind(address, DEFAULT_BACKLOG);
				selector = Selector.open();
				serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				// ----------------------------------------------
				started = true;
				// ----------------------------------------------
				if (started) {
					// System.out.println("serverKey accept:" +
					// serverKey.interestOps() + " "
					// + serverKey.readyOps());//16 0

					// 加入已啟動的servers
					serverServices = (relationServer ? acceptorService.getRelationServerServices()
							: acceptorService.getClientServerServices());
					serverServices.put(id, this);

					// listen,selector
					threadService.submit(new ListenRunner());

					// read,selectionKey
					threadService.submit(readKeyQueue);

					// RelationServer [3110]
					// ClientServer [4110]

					LOGGER.info(acceptorServer + (relationServer ? "RelationServer" : "ClientServer") + " [" + port
							+ "] Had been started");
				} else {
					LOGGER.error(acceptorServer + (relationServer ? "RelationServer" : "ClientServer") + " [" + port
							+ "] Started fail");
				}
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
			started = false;
			LOGGER.error("[" + acceptorServer + "] Started fail", ex);
		}
	}

	/**
	 * 監聽
	 */
	protected class ListenRunner extends BaseRunnableSupporter {

		public void execute() {
			while (true) {
				try {
					if (!started) {
						break;
					}
					listen();
					ThreadHelper.sleep(LISTEN_MILLS);
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
			//
			LOGGER.info("Break of " + getClass().getSimpleName());
		}
	}

	/**
	 * 監聽
	 */
	protected void listen() {
		try {
			// select(timeout)和select()的選擇過程是阻塞的,其他線程如果想終止這個過程，就可以調用wakeup來喚醒
			int select = selector.select();
			if (select == 0) {
				return;
			}
			//
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = selectionKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				iterator.remove();
				// ---------------------------------------------
				process(selectionKey);
				// ---------------------------------------------
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 處理 selectionKey
	 *
	 * @param selectionKey
	 */
	protected void process(SelectionKey selectionKey) {
		try {
			ServerSocketChannel server = null;
			SocketChannel client = null;
			// ---------------------------------------------
			// isAcceptable
			// ---------------------------------------------
			// if ( (selectionKey.readyOps() & SelectionKey.OP_ACCEPT) ==
			// SelectionKey.OP_ACCEPT) {
			if (selectionKey.isAcceptable()) {
				// clientChannel
				// SocketChannel clientChannel = serverChannel.accept();

				server = (ServerSocketChannel) selectionKey.channel();
				client = server.accept();
				client.configureBlocking(false);
				//
				int nowCounter = counter.get();
				if (nowCounter >= getMaxClient()) {
					// 127.0.0.1:64237
					LOGGER.error(acceptorServer + "counter[" + nowCounter + "] > max[" + getMaxClient() + "], "
							+ client.socket().getRemoteSocketAddress() + " Can't connect server");
					NioHelper.close(selectionKey);
				}
				// ---------------------------------------------
				// create client and attach
				// ---------------------------------------------
				else {
					SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
					// System.out.println("selectionKey..."+selectionKey);//都是同一個
					// System.out.println("clientKey..."+clientKey);//每次都不同
					// 取mem位址當id,sun.nio.ch.SelectionKeyImpl@19e09a4
					String clientId = String.valueOf(clientKey);
					AcceptorConnector acceptorConnector = new AcceptorConnectorImpl(clientId, moduleTypeClass,
							messageTypeClass, protocolService);
					acceptorConnector.setAcceptor(acceptorId);// slave1
					acceptorConnector.setServer(id);// localhost:3100
					// acceptorConnector.setSelectionKey(clientKey);
					// acceptorConnector.setSocketChannel(clientChannel);

					// server ip,port
					// ServerSocket serverSocket = serverChannel.socket();
					ServerSocket serverSocket = server.socket();
					acceptorConnector.setServerIp(serverSocket.getInetAddress().getHostAddress());
					acceptorConnector.setServerPort(serverSocket.getLocalPort());

					// client ip,port
					Socket clientSocket = client.socket();
					acceptorConnector.setClientIp(clientSocket.getInetAddress().getHostAddress());
					acceptorConnector.setClientPort(clientSocket.getPort());
					// acceptorConnector.start();
					//
					clientKey.attach(acceptorConnector);
					counter.incrementAndGet();

					// LOGGER.info("C[" + counter.get() + "] connected from ["
					// + acceptorConnector.getClientIp() + ":"
					// + acceptorConnector.getClientPort() + "]");
				}
			}

			// ---------------------------------------------
			// isReadable
			// ---------------------------------------------
			if (selectionKey.isReadable()) {
				// 塞到佇列中,多緒處理
				readKeyQueue.offer(selectionKey);

				// 不註冊write了,比較快些,以後再看情況
				// clientChannel.register(selector, SelectionKey.OP_WRITE,
				// acceptorConnector);
			}

			// ---------------------------------------------
			// isWritable
			// ---------------------------------------------
			// if (selectionKey.isWritable())
			// {
			//
			// clientChannel.register(selector, SelectionKey.OP_READ,
			// acceptorConnector);
			// }
		} catch (CancelledKeyException ex) {
			// ex.printStackTrace();
			//
			AcceptorConnector acceptorConnector = (AcceptorConnector) selectionKey.attachment();
			if (acceptorConnector != null) {
				close(acceptorConnector);
			}
			NioHelper.close(selectionKey);
		}
		// 當client自行中斷
		catch (ClosedChannelException ex) {
			// ex.printStackTrace();
			//
			AcceptorConnector acceptorConnector = (AcceptorConnector) selectionKey.attachment();
			if (acceptorConnector != null) {
				close(acceptorConnector);
			}
			NioHelper.close(selectionKey);
		} catch (IOException ex) {
			// 遠端主機已強制關閉一個現存的連線
			// System.out.println(ex.getMessage());
			// ex.printStackTrace();
			//
			AcceptorConnector acceptorConnector = (AcceptorConnector) selectionKey.attachment();
			if (acceptorConnector != null) {
				close(acceptorConnector);
			}
			NioHelper.close(selectionKey);
		} catch (Exception ex) {
			ex.printStackTrace();
			// close(selectionKey);
		}
	}

	/**
	 * 讀取key佇列
	 */
	protected class ReadKeyQueue<E> extends TriggerQueueSupporter<E> {

		public ReadKeyQueue() {
		}

		public void process(E e) {
			read((SelectionKey) e);
		}
	}

	/**
	 * 讀取
	 *
	 * @param selectionKey
	 */
	protected void read(SelectionKey selectionKey) {
		AcceptorConnector acceptorConnector = (AcceptorConnector) selectionKey.attachment();
		// SocketChannel clientChannel = acceptorConnector.getSocketChannel();
		SocketChannel client = (SocketChannel) selectionKey.channel();
		if (!acceptorConnector.isStarted()) {
			acceptorConnector.setSocketChannel(client);
			acceptorConnector.start();
		}
		//
		byte[] bytes = acceptorConnector.receive();
		// System.out.println("bytes......" + bytes);

		// ---------------------------------------------
		// receive flashPolicy, FLASH_INPUT_POLICY=22+1
		// 當flash第一次連線時,跨網域時,會發送 <policy-file-request/>\0=23
		// ---------------------------------------------
		if (!acceptorConnector.isReadFlashPolicy()
				&& acceptorConnector.getConnectorType() != ConnectorType.FLASH_SOCKLET && bytes != null
				&& bytes.length == 23) {
			receiveFlashPolicy(acceptorConnector, bytes);

			// ---------------------------------------------
			// send flashPolicy, 當client為flash的policy時
			// ---------------------------------------------
			if (acceptorConnector.getConnectorType() == ConnectorType.FLASH_SOCKLET
					&& !acceptorConnector.isSendFlashPolicy()) {
				sendFlashPolicy(acceptorConnector);
				// System.out.println("sendFlashPolicy");
				// System.out.println(acceptorConnector.getClientIp() + ":"
				// + acceptorConnector.getClientPort());
				// 斷開policy連線
				closeSelf(acceptorConnector);
				counter.decrementAndGet();
			}
		}

		// ---------------------------------------------
		// receive already,判斷哪種client
		// byte[0]=1,flash client
		// byte[0]=2,java client
		// @see JavaConnectorImpl.sendAlready, java client用
		// ---------------------------------------------
		else if (!acceptorConnector.isReadAlready() && bytes != null && bytes.length == 1) {
			receiveAlready(acceptorConnector, bytes[0]);

			// ---------------------------------------------
			// send authKey
			// ---------------------------------------------
			if (acceptorConnector.isReadAlready() && !acceptorConnector.isSendAuthKey()
					&& acceptorConnector.getAuthKey() == null) {
				sendAuthKey(acceptorConnector);
			}
		}
		// ---------------------------------------------
		// receive handshake, >= 38
		// !=23 => 當flash第一次連線時,跨網域時,會發送 <policy-file-request/>\0=23
		// ---------------------------------------------
		else if (!acceptorConnector.isReadHandshake() && !acceptorConnector.isHandshake()
				&& acceptorConnector.getAuthKey() != null && bytes != null && bytes.length != 23
				&& bytes.length >= 38) {
			receiveHandshake(acceptorConnector, bytes);
		}
		// ---------------------------------------------
		// receive message
		// ---------------------------------------------
		else if (acceptorConnector.getAuthKey() != null && acceptorConnector.isHandshake() && bytes != null) {
			receiveMessage(acceptorConnector, bytes);
		}
	}

	/**
	 * flash policy請求
	 *
	 * @param acceptorConnector
	 */
	protected void receiveFlashPolicy(AcceptorConnector acceptorConnector, byte[] bytes) {
		acceptorConnector.setReadFlashPolicy(true);
		// <policy-file-request/>\0 => <policy-file-request/>
		byte[] buffs = ByteHelper.getByteArray(bytes, 0, bytes.length - 1);
		String flashPolicy = ByteHelper.toString(buffs);

		// <policy-file-request/>
		if (GenericConnector.FLASH_INPUT_POLICY.equalsIgnoreCase(flashPolicy)) {
			acceptorConnector.setConnectorType(ConnectorType.FLASH_SOCKLET);
		}
		// log.info(acceptorServer + "readFlashPolicy: " +
		// acceptorConnector.isFlashPolicy());
	}

	/**
	 * 發送flash policy回應
	 *
	 * @param acceptorConnector
	 */
	protected void sendFlashPolicy(AcceptorConnector acceptorConnector) {
		int write = acceptorConnector.send(GenericConnector.FLASH_OUTPUT_POLICY_BYTES);
		if (write > 0) {
			acceptorConnector.setSendFlashPolicy(true);
		}
		//
		// log.info(acceptorServer + "sendFlashPolicy: " +
		// acceptorConnector.isSendFlashPolicy());
	}

	/**
	 * 發送認證碼回應
	 *
	 * @param acceptorConnector
	 */
	protected void sendAuthKey(AcceptorConnector acceptorConnector) {
		// 建構認證key
		AuthKey authKey = authKeyService.createAuthKey();
		// <"sun.nio.ch.SelectionKeyImpl@19e09a4","0dc8579d1b7669924019f97f138bc4c3">
		authKeyService.addAuthKey(acceptorConnector.getId(), authKey);
		//
		byte[] bytes = ByteHelper.toByteArray(authKey.getId());
		int write = acceptorConnector.send(bytes);
		// int write = acceptorConnector.send(ByteHelper.toByteArray(true));
		// int write =
		// acceptorConnector.send(ByteHelper.toByteArray(1500.123f));
		// int write =
		// acceptorConnector.send(ByteHelper.toByteArray(1500.123d));
		if (write > 0) {
			acceptorConnector.setAuthKey(bytes);
			acceptorConnector.setSendAuthKey(true);
		}
		// log.info(acceptorServer + "sendAuthKey: " +
		// acceptorConnector.isSendAuthKey()+", "+authKey.getId());
	}

	/**
	 * 已準備請求,判斷哪種client
	 *
	 * @param acceptorConnector
	 */
	protected void receiveAlready(AcceptorConnector acceptorConnector, byte buff) {
		// flash client
		if (buff == ConnectorType.FLASH_SOCKLET.getValue()) {
			acceptorConnector.setReadFlashPolicy(true);
			acceptorConnector.setConnectorType(ConnectorType.FLASH_SOCKLET);
			acceptorConnector.setSendFlashPolicy(true);
			//
			acceptorConnector.setReadAlready(true);
		}
		// java client
		else if (buff == ConnectorType.JAVA_SOCKLET.getValue()) {
			acceptorConnector.setConnectorType(ConnectorType.JAVA_SOCKLET);
			acceptorConnector.setReadAlready(true);
		}
		// java relation client
		else if (buff == ConnectorType.RELATION_SOCKLET.getValue()) {
			acceptorConnector.setConnectorType(ConnectorType.RELATION_SOCKLET);
			acceptorConnector.setReadAlready(true);
		} else {
			// other client
		}
		//
		// log.info(acceptorServer + "readAlready: " +
		// acceptorConnector.isReadAlready());
	}

	/**
	 * 握手請求
	 *
	 * @param acceptorConnector
	 * @param bytes
	 */
	protected void receiveHandshake(AcceptorConnector acceptorConnector, byte[] bytes) {
		// SelectionKey selectionKey = acceptorConnector.getSelectionKey();
		//
		acceptorConnector.setReadHandshake(true);
		// check authKey is expired or not
		// id=sun.nio.ch.SelectionKeyImpl@19e09a4
		AuthKey authKey = authKeyService.getAuthKey(acceptorConnector.getId());
		if (authKey == null) {
			closeSelf(acceptorConnector);
			//
			LOGGER.warn(acceptorServer + "AuthKey expired");
			return;
		} else if (authKey.isExpired()) {
			authKeyService.removeAuthKey(acceptorConnector.getId());
			closeSelf(acceptorConnector);
			//
			LOGGER.warn(acceptorServer + "AuthKey expired");
			return;
		}
		//
		Message message = protocolService.dehandshake(bytes);
		// client/server handshake
		if (message != null) {
			// 檢查認證碼是否與server發出去的相同
			byte[] clientAuthkey = message.getByteArray(0);
			if (clientAuthkey == null || clientAuthkey.length != 32
					|| !ObjectHelper.equals(clientAuthkey, ByteHelper.toByteArray(authKey.getId()))) {
				LOGGER.warn(acceptorServer + "Authkey is invalid");
				return;
			}
		} else {
			authKeyService.removeAuthKey(acceptorConnector.getId());
			closeSelf(acceptorConnector);
			//
			// SystemHelper.println(bytes);
			// System.out.println("receiveHandshake: " + message);
			LOGGER.warn(acceptorServer + "Has no handshake message");
			SystemHelper.println(bytes);
			return;
		}

		// ---------------------------------------------
		// HANDSHAKE_CLIENT
		// ---------------------------------------------
		if (CategoryType.HANDSHAKE_CLIENT.equals(message.getCategoryType())) {
			// TEST_ROLE_1
			String sender = message.getSender();
			authKeyService.removeAuthKey(acceptorConnector.getId());
			//
			AcceptorConnector existClient = acceptorConnectors.get(sender);
			// 檢查是否重覆連線,同sender,但來自於不同連線
			if (existClient != null && !acceptorConnector.getId().equals(existClient.getId())) {
				// 已經存在的client
				close(existClient);
				// 剛連上的client
				closeSelf(acceptorConnector);
				//
				LOGGER.warn(acceptorServer + "Already exist client [" + existClient.getSender() + "]");
				return;
			}
			// 握手成功
			else {
				acceptorConnector.setHandshake(true);
				acceptorConnector.setSender(sender);
				acceptorConnectors.put(acceptorConnector.getSender(), acceptorConnector);

				// ---------------------------------------------
				// 握手成功後
				// ---------------------------------------------
				// send client connect,by udp
				// ---------------------------------------------
				// 發送客戶端連線請求
				acceptorService.sendSyncClientConnect(acceptorConnector);
				// ---------------------------------------------
				// create session
				// ---------------------------------------------
				createSession(acceptorConnector);
				//
				LOGGER.info("C[" + counter.get() + "]" + acceptorServer + "Connected from ["
						+ acceptorConnector.getSender() + "]");
			}
		}

		// ---------------------------------------------
		// HANDSHAKE_RELATION
		// ---------------------------------------------
		else if (CategoryType.HANDSHAKE_RELATION.equals(message.getCategoryType())) {
			// slave2:0:localhost:3000
			String sender = message.getSender();
			String[] buff = sender.split(":");
			if (buff.length == 4) {
				authKeyService.removeAuthKey(acceptorConnector.getId());
				// slave2 -> slave1
				String relationId = buff[0]; // slave2
				GenericRelation passiveRelation = passiveRelations.get(relationId);
				if (passiveRelation == null) {
					passiveRelation = new GenericRelationImpl(relationId);
					passiveRelations.put(passiveRelation.getId(), passiveRelation);
				}

				// 檢查是否重覆連線,同sender,但來自於不同連線
				GenericConnector existClient = passiveRelation.getClients().get(sender);
				// 已經存在的client
				if (existClient != null && !acceptorConnector.getId().equals(existClient.getId())) {
					close(existClient);
					// 剛連上的client
					closeSelf(acceptorConnector);
					//
					LOGGER.warn(acceptorServer + "Already exist relation client [" + existClient.getSender() + "]");
					return;
				}
				// 握手成功
				else {
					acceptorConnector.setHandshake(true);
					acceptorConnector.setSender(sender);
					passiveRelation.getClients().put(acceptorConnector.getSender(), acceptorConnector);
					// ---------------------------------------------
					// 握手成功後
					// ---------------------------------------------
					// passiveRelation.getId //slave2
					//
					// 當被其他acceptor連上或再度連上時,同步此acceptor的client
					// #issue 會發多次,因有多條relation socket
					// #fix isConnected判斷
					if (!passiveRelation.isConnected()) {
						// 網路層同步,所有slave都會收到
						for (AcceptorConnector currentClient : acceptorService.getAcceptorConnectors().values()) {
							acceptorService.sendSyncClientConnect(currentClient);
						}

						// 邏輯層同步,用relationEvent處理
						contextService.fireRelationConnected(relationId);
						//
						passiveRelation.setConnected(true);
						LOGGER.info(acceptorServer + "Connected from [" + passiveRelation.getId() + "]");
					}
				}
			} else {
				authKeyService.removeAuthKey(acceptorConnector.getId());
				closeSelf(acceptorConnector);
				//
				LOGGER.error(acceptorServer + "Wrong relation [" + sender + "] format, ex: [slave2:0:localhost:3000]");
			}
		}
		// handshake fail
		else {
			authKeyService.removeAuthKey(acceptorConnector.getId());
			closeSelf(acceptorConnector);
			//
			LOGGER.info(acceptorServer + "Handshake failed");
		}

		// ---------------------------------------------
		// send handshake
		// ---------------------------------------------
		sendHandshake(acceptorConnector, message);
	}

	/**
	 * 發送握手回應
	 *
	 * @param acceptorConnector
	 * @param message
	 */
	protected void sendHandshake(AcceptorConnector acceptorConnector, Message message) {
		if (!acceptorConnector.isSendHandshake() && acceptorConnector.isHandshake()) {
			message.setCategoryType(CategoryType.HANDSHAKE_SERVER);
			byte[] bytes = protocolService.handshake(message.getCategoryType(), acceptorConnector.getAuthKey(),
					acceptorService.getId());// sender表server本身
			int write = acceptorConnector.send(bytes);
			// 握手成功,且已通知client
			if (write > 0) {
				acceptorConnector.setSendHandshake(true);
			}
		}
		// log.info(acceptorServer + "sendHandshake: " +
		// acceptorConnector.isSendHandshake());
	}

	/**
	 * 訊息請求
	 *
	 * 1.tcp, client->server, MESSAGE_CLIENT
	 *
	 * srcModule=CLIENT, destModule= FOUR_SYMBOL
	 *
	 *
	 * 2.tcp, server->relation, MESSAGE_RELATION
	 *
	 * destModule!=CLIENT
	 *
	 * srcModule=slave1.CHAT, destModule= slave2.FOUR_SYMBOL
	 *
	 * srcModule=slave1.CLIENT, destModule= slave2.CORE
	 *
	 * @param acceptorConnector
	 * @param bytes
	 */

	protected void receiveMessage(AcceptorConnector acceptorConnector, byte[] bytes) {
		@SuppressWarnings("unchecked")
		List<Message> messages = protocolService.disassemble(bytes, moduleTypeClass, messageTypeClass);
		// System.out.println(acceptorId);
		// System.out.println("messages: " + messages);

		// receiveClients
		// receiveRelations
		acceptorService.addMessages(messages);
	}

	/**
	 * 建立session
	 *
	 * @param genericConnector
	 */
	protected void createSession(GenericConnector genericConnector) {
		Session session = new SessionImpl(genericConnector.getSender());
		// session.id=sender
		contextService.getSessions().put(session.getId(), session);
		contextService.fireSessionCreated(session);
		// System.out.println("createSession: " + contextService.getSessions());
	}

	/**
	 * 銷毀session
	 *
	 * @param genericConnector
	 */
	protected void destroySession(GenericConnector genericConnector) {
		// session.id=sender
		Session session = contextService.getSessions().remove(genericConnector.getSender());
		contextService.fireSessionDestroyed(session);
		// System.out.println("destroySession: " +
		// contextService.getSessions());
	}

	// TODO 須調整
	public void shutdown() {
		try {
			if (started) {
				NioHelper.close(selector);
				NioHelper.close(serverChannel);
				//
				counter.set(0);
				// 從已啟動的servers移除
				serverServices.remove(id);
				//
				readKeyQueue.setCancel(true);
				//
				started = false;
				LOGGER.info(acceptorServer + "Had been shutdown");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			started = false;
			LOGGER.error(acceptorServer + "Shutdown fail");
		}

	}

	/**
	 * 關閉key
	 */
	public boolean close(GenericConnector genericConnector) {
		boolean result = false;
		try {
			// AcceptorConnector acceptorConnector = (AcceptorConnector)
			// selectionKey
			// .attachment();
			String sender = genericConnector.getSender();
			if (genericConnector != null && sender != null) {
				// acceptorConnectors
				boolean contains = acceptorConnectors.containsKey(sender);
				if (contains) {
					genericConnector.shutdown();
					acceptorConnectors.remove(sender);
					result = true;
					//
					counter.decrementAndGet();
					// 發送客戶端斷線請求
					acceptorService.sendSyncClientDisconnect(genericConnector);
					// 銷毀session
					destroySession(genericConnector);
					//
					LOGGER.info("C[" + counter.get() + "]" + acceptorServer + "Disconnect [" + sender + "]");
				}
				// passiveRelations
				else {
					// 當relation的socket都斷光時,就移除吧
					List<String> removeRelations = new LinkedList<String>();
					for (GenericRelation passiveRelation : passiveRelations.values()) {
						boolean relationContains = passiveRelation.getClients().containsKey(sender);
						if (relationContains) {
							genericConnector.shutdown();
							passiveRelation.getClients().remove(sender);
							counter.decrementAndGet();

							// relation的socket都斷了
							if (passiveRelation.getClients().size() == 0) {
								// 網路層同步,沒做任何事

								// 邏輯層同步,用relationEvent處理
								contextService.fireRelationDisconnected(passiveRelation.getId());
								//
								passiveRelation.setConnected(false);
								removeRelations.add(passiveRelation.getId());
							}
							//
							LOGGER.info("C[" + counter.get() + "]" + acceptorServer + "Disconnect ["
									+ genericConnector.getSender() + "]");
							break;
						}
					}
					// 當relation的socket都斷光時,就移除吧
					for (String relationId : removeRelations) {
						passiveRelations.remove(relationId);
					}
				}
				//
				// NioHelper.close(selectionKey);
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
		return result;
	}

	protected void closeSelf(AcceptorConnector acceptorConnector) {
		acceptorConnector.shutdown();
		LOGGER.info("C[" + counter.get() + "]" + acceptorServer + "Disconnect " + acceptorConnector.getClientIp() + ":"
				+ acceptorConnector.getClientPort());
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", id);
		builder.append("ip", ip);
		builder.append("port", port);
		builder.append("maxClient", maxClient);
		builder.append("started", started);
		return builder.toString();
	}

}

//
// int write = 0;
// while (writeBuffer.hasRemaining())
// {
// write = clientChannel.write(writeBuffer);
//
// //
// if (write < 0)
// {
// //throw new EOFException();
// break;
// }
// if (write == 0)
// {
// selectionKey.interestOps(selectionKey.interestOps()
// | SelectionKey.OP_WRITE);
// selector.wakeup();
// break;
// }
// }
//

// ServerSocket serverSocket = serverChannel.socket();
// System.out.println(serverSocket.getInetAddress());///127.0.0.1
// System.out.println(serverSocket.getInetAddress().getHostName());//serial.alcohol-soft.com
// System.out.println(serverSocket.getInetAddress().getHostAddress());//127.0.0.1
// System.out.println(serverSocket.getLocalPort());//4110
// //
// Socket socket = clientChannel.socket();
// System.out.println(socket.getInetAddress());//127.0.0.1
// System.out.println(socket.getInetAddress().getHostName());//serial.alcohol-soft.com
// System.out.println(socket.getInetAddress().getHostAddress());//127.0.0.1
// System.out.println(socket.getPort());//54368
// //
// System.out.println(socket.getInetAddress().getLocalHost().getHostAddress());//192.168.0.100
// System.out.println(socket.getLocalSocketAddress());///127.0.0.1:4110
// System.out.println(socket.getRemoteSocketAddress());//serial.alcohol-soft.com/127.0.0.1:54368
