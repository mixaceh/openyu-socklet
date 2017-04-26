package org.openyu.socklet.connector.vo.supporter;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.ObjectHelper;
import org.openyu.commons.nio.NioHelper;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.commons.thread.supporter.BaseRunnableSupporter;
import org.openyu.commons.thread.supporter.TriggerQueueSupporter;
import org.openyu.socklet.connector.vo.SocketConnector;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * socket連線客戶端
 */
public abstract class SocketConnectorSupporter extends GenericConnectorSupporter implements SocketConnector {

	private static final long serialVersionUID = 4044263207033989263L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(SocketConnectorSupporter.class);

	protected String ip;

	protected int port;

	/**
	 * 驗證碼
	 */
	protected byte[] authKey;

	protected boolean readAuthKey;

	/**
	 * 握手
	 */
	protected boolean sendHandshake;

	protected boolean handshake;

	protected boolean readHandshake;

	// ------------------------------------------------------------
	/**
	 * 監聽毫秒
	 */
	protected static final long LISTEN_MILLS = 1L;

	/**
	 * 監聽 selector
	 */
	private SelectionKeyRunner selectionKeyRunner;
	// ------------------------------------------------
	// read
	// ------------------------------------------------
	/**
	 * 讀取key佇列
	 */
	private ReadKeyQueue<SelectionKey> readKeyQueue;

	// ------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public SocketConnectorSupporter(String id, Class moduleTypeClass, Class messageTypeClass,
			ProtocolService protocolService) {
		super(id, moduleTypeClass, messageTypeClass, protocolService);
	}

	@SuppressWarnings("rawtypes")
	public SocketConnectorSupporter(Class moduleTypeClass, Class messageTypeClass, ProtocolService protocolService) {
		this(null, moduleTypeClass, messageTypeClass, protocolService);
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

	public byte[] getAuthKey() {
		return authKey;
	}

	public void setAuthKey(byte[] authKey) {
		this.authKey = authKey;
	}

	public boolean isReadAuthKey() {
		return readAuthKey;
	}

	public void setReadAuthKey(boolean readAuthKey) {
		this.readAuthKey = readAuthKey;
	}

	public boolean isSendHandshake() {
		return sendHandshake;
	}

	public void setSendHandshake(boolean sendHandshake) {
		this.sendHandshake = sendHandshake;
	}

	public boolean isHandshake() {
		return handshake;
	}

	public void setHandshake(boolean handshake) {
		this.handshake = handshake;
	}

	public boolean isReadHandshake() {
		return readHandshake;
	}

	public void setReadHandshake(boolean readHandshake) {
		this.readHandshake = readHandshake;
	}

	public void start() {
		try {
			if (!started) {
				sender = id;
				selector = Selector.open();

				// socketChannel = SocketChannel.open();
				// socketChannel.configureBlocking(false);
				// InetSocketAddress address =
				// NioHelper.createInetSocketAddress(ip, port);
				// socketChannel.connect(address);
				// // socketChannel.socket().setKeepAlive(true);//
				// socketChannel.register(selector, SelectionKey.OP_CONNECT);

				socketChannel = createSocketChannel();
				//
				int select = selector.select();
				if (select > 0) {
					Set<SelectionKey> selectionKeys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = selectionKeys.iterator();
					while (iterator.hasNext()) {
						SelectionKey selectionKey = iterator.next();
						iterator.remove();
						// ---------------------------------------------
						processConnect(selectionKey);// 處理OP_CONNECT
						// ---------------------------------------------
					}
				}
			}
		} catch (ConnectException e) {
			NioHelper.close(selector);
			NioHelper.close(socketChannel);
			started = false;
			// LOGGER.error("[" + id + "] can't connect to [" + ip + ":" + port
			// + "]");
			// ex.printStackTrace();
		} catch (Exception e) {
			NioHelper.close(selector);
			NioHelper.close(socketChannel);
			started = false;

			// 遠端主機已強制關閉一個現存的連線
			// LOGGER.error("[" + id + "]", ex);
			LOGGER.error(new StringBuilder("[" + id + "] Exception encountered during start()").toString(), e);
		}
	}

	/**
	 * 建立連線
	 * 
	 * @return
	 * @throws Exception
	 */
	protected SocketChannel createSocketChannel() throws Exception {
		SocketChannel result = SocketChannel.open();
		result.configureBlocking(false);
		InetSocketAddress address = NioHelper.createInetSocketAddress(ip, port);
		result.connect(address);
		result.register(selector, SelectionKey.OP_CONNECT);

		// 2016/02/11 設置TCP_NODELAY屬性,禁用Nagle算法
		// result.socket().setTcpNoDelay(true);
		return result;
	}

	/**
	 * 處理OP_CONNECT
	 * 
	 * @param selectionKey
	 * @throws Exception
	 */
	protected void processConnect(SelectionKey selectionKey) throws Exception {
		// ---------------------------------------------
		// isConnectable
		// ---------------------------------------------
		if (selectionKey.isConnectable()) {
			if (socketChannel.isConnectionPending()) {
				// started = socketChannel.finishConnect();
				// started = NioHelper.waitConnect(socketChannel,
				// waitConnectMills);

				boolean connected = false;
				for (;;) {
					try {
						connected = socketChannel.finishConnect();
						// 連線成功
						if (connected) {
							started = true;
							break;
						}
					} catch (ConnectException e) {
						// 當連線失敗時,重試
						// socketChannel== selectionKey.channel()

						// 要改成
						// 1.selectionKey.cancel()
						// 2.重建socketChannel

						// 1.selectionKey.cancel()
						selectionKey.cancel();
						// socketChannel.close();//
						// 因為沒開啟,isClosed=true,所以也無法close

						// 2.重建socketChannel
						socketChannel = createSocketChannel();

						addTries();
						// [1/3] time(s) Failed to get the session
						LOGGER.warn("[" + tries + "/" + (retryNumber != 0 ? retryNumber : "INFINITE")
								+ "] time(s) Failed to get the connection");
						// 0=無限
						if (retryNumber != 0 && tries >= retryNumber) {
							break;
						}
						// 重試暫停毫秒
						long pauseMills = NioHelper.retryPause(tries, retryPauseMills);
						ThreadHelper.sleep(pauseMills);
						LOGGER.info("Retrying connect to [" + ip + ":" + port + "]. Already tried [" + (tries + 1) + "/"
								+ (retryNumber != 0 ? retryNumber : "INFINITE") + "] time(s)");

					}
				}
				//
				if (!started) {
					LOGGER.error("Can't connect to [" + ip + ":" + port + "]");
					throw new ConnectException("Connection refused");
				} else {
					// listen
					selectionKeyRunner = new SelectionKeyRunner();
					selectionKeyRunner.start();

					// read,selectionKey
					readKeyQueue = new ReadKeyQueue<SelectionKey>();
					readKeyQueue.start();

					// keepAlive
					keepAliveRunner = new KeepAliveRunner();
					keepAliveRunner.start();
					//
					connectable(selectionKey);
					socketChannel.register(selector, SelectionKey.OP_READ);
				}
			}
		}
	}

	/**
	 * 監聽 selector
	 */
	protected class SelectionKeyRunner extends BaseRunnableSupporter {

		public SelectionKeyRunner() {
		}

		@Override
		protected void doRun() throws Exception {
			while (true) {
				try {
					if (isShutdown()) {
						break;
					}
					listen();
					ThreadHelper.sleep(LISTEN_MILLS);
				} catch (ClosedChannelException ex) {
					started = false;
				} catch (CancelledKeyException ex) {
					started = false;
				} catch (Exception ex) {
					// ex.printStackTrace();
					started = false;
				}
			}
		}
	}

	/**
	 * readAuthKey -> authKey=8129262b08dfed4be7b8dac63b61b266
	 * 
	 * sendHandshake -> readHandshake -> handshake=true
	 * 
	 * readMessage
	 */
	protected void listen() throws Exception {
		int select = selector.select();
		if (select > 0) {
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				iterator.remove();
				// ---------------------------------------------
				process(selectionKey);
				// ---------------------------------------------
			}
		}
	}

	/**
	 * 處理OP_READ,OP_WRITE
	 * 
	 * @param selectionKey
	 * @throws Exception
	 */
	protected void process(SelectionKey selectionKey) throws Exception {
		// ---------------------------------------------
		// isReadable
		// ---------------------------------------------
		if (selectionKey.isReadable()) {
			// readable(selectionKey);
			readKeyQueue.offer(selectionKey);

			// socketChannel.register(selector, SelectionKey.OP_WRITE);
			// selectionKey.interestOps(SelectionKey.OP_WRITE);
		}

		// // ---------------------------------------------
		// // isWritable
		// // ---------------------------------------------
		// if (selectionKey.isWritable())
		// {
		// writable(selectionKey);
		// socketChannel.register(selector, SelectionKey.OP_READ);
		// //selectionKey.interestOps(SelectionKey.OP_READ);
		// }
	}

	/**
	 * 讀取key佇列
	 */
	protected class ReadKeyQueue<E> extends TriggerQueueSupporter<E> {

		public ReadKeyQueue() {
		}

		@Override
		protected void doExecute(E e) throws Exception {
			try {
				readable((SelectionKey) e);
			} catch (ClosedChannelException ex) {
				started = false;
				readKeyQueue.shutdown();
			} catch (CancelledKeyException ex) {
				started = false;
				readKeyQueue.shutdown();
			} catch (Exception ex) {
				// ex.printStackTrace();
				started = false;
				readKeyQueue.shutdown();
			}
		}
	}

	// ---------------------------------------------
	protected abstract void connectable(SelectionKey selectionKey) throws Exception;

	protected abstract void readable(SelectionKey selectionKey) throws Exception;

	protected abstract void writable(SelectionKey selectionKey) throws Exception;

	// ---------------------------------------------

	protected void receiveAuthKey(byte[] bytes) {
		readAuthKey = true;
		if (bytes != null) {
			authKey = bytes;
		}
		//
		// System.out.println("readAuthKey: " + ByteHelper.toString(authKey));
	}

	protected void receiveHandshake(byte[] bytes) {
		readHandshake = true;
		Message message = protocolService.dehandshake(bytes);
		if (message != null) {
			// 檢查認證碼是否與client發出去的相同
			byte[] serverAuthkey = message.getByteArray(0);
			if (serverAuthkey == null || serverAuthkey.length != 32 || !ObjectHelper.equals(serverAuthkey, authKey)) {
				LOGGER.warn("Authkey is invalid");
				return;
			}
			//
			if (CategoryType.HANDSHAKE_SERVER.equals(message.getCategoryType())) {
				handshake = true;
				acceptor = message.getSender();
				LOGGER.info("[" + id + "] Connected to [" + acceptor + "]");
			}
		}
		//
		// System.out.println("handshake: " + handshake);
	}

	protected void receiveMessage(byte[] bytes) {
		// 接收者
		if (receiver == null) {
			LOGGER.warn("[" + id + "] Has no receiver to receive message");
			return;
		}
		//
		@SuppressWarnings("unchecked")
		List<Message> messages = protocolService.disassemble(bytes, moduleTypeClass, messageTypeClass);
		//
		for (Message message : messages) {
			if (message == null) {
				continue;
			}
			// 導向到receiver接收者
			receiver.receive(message);
		}
	}

	// ---------------------------------------------
	public void shutdown() {
		try {
			if (started) {
				super.shutdown();
				//
				authKey = null;
				readAuthKey = false;
				sendHandshake = false;
				handshake = false;
				readHandshake = false;
				//
				selectionKeyRunner.shutdown();
				readKeyQueue.shutdown();
				keepAliveRunner.shutdown();
			}
		} catch (Exception e) {
			started = false;
			LOGGER.error(new StringBuilder("[" + id + "] Exception encountered during shutdown()").toString(), e);
		}
	}

	/**
	 * 當未完成握手時,最多等待幾毫秒
	 * 
	 * @param millis
	 */
	protected void waitHandshake(long millis) {
		long beg = System.currentTimeMillis();
		long end = 0;
		//
		while (!handshake) {
			end = System.currentTimeMillis();
			//
			if ((end - beg) >= millis) {
				break;
			}
			ThreadHelper.sleep(1 * 1000);// 間隔一秒
		}
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.appendSuper(super.toString());
		builder.append("ip", ip);
		builder.append("port", port);
		builder.append("readAuthKey", readAuthKey);
		builder.append("authKey", ByteHelper.toString(authKey));
		builder.append("sendHandshake", sendHandshake);
		builder.append("readHandshake", readHandshake);
		builder.append("handshake", handshake);
		return builder.toString();
	}
}
