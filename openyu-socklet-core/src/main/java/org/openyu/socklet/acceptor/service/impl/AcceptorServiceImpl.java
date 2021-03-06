package org.openyu.socklet.acceptor.service.impl;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.common.util.StringHelper;
import org.jgroups.Channel;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openyu.commons.enumz.EnumHelper;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.ClassHelper;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.commons.nio.NioHelper;
import org.openyu.commons.security.AuthKeyService;
import org.openyu.commons.security.anno.DefaultAuthKeyService;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.commons.thread.ThreadService;
import org.openyu.commons.thread.supporter.BaseRunnableSupporter;
import org.openyu.commons.thread.supporter.TriggerQueueSupporter;
import org.openyu.commons.util.ConfigHelper;
import org.openyu.socklet.acceptor.anno.AcceptorThreadService;
import org.openyu.socklet.acceptor.net.socklet.AcceptorMessageType;
import org.openyu.socklet.acceptor.net.socklet.AcceptorModuleType;
import org.openyu.socklet.acceptor.service.AcceptorService;
import org.openyu.socklet.acceptor.service.ServerService;
import org.openyu.socklet.connector.vo.AcceptorConnector;
import org.openyu.socklet.connector.vo.GenericConnector;
import org.openyu.socklet.connector.vo.GenericRelation;
import org.openyu.socklet.connector.vo.RelationConnector;
import org.openyu.socklet.connector.vo.impl.AcceptorConnectorImpl;
import org.openyu.socklet.connector.vo.impl.GenericRelationImpl;
import org.openyu.socklet.connector.vo.impl.RelationConnectorImpl;
import org.openyu.socklet.connector.vo.supporter.GenericReceiverSupporter;
import org.openyu.socklet.context.service.impl.ContextServiceImpl;
import org.openyu.socklet.message.anno.DefaultMessageService;
import org.openyu.socklet.message.anno.DefaultProtocolService;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.MessageType;
import org.openyu.socklet.message.vo.ModuleType;
import org.openyu.socklet.message.vo.PriorityType;
import org.openyu.socklet.message.vo.ClusterMessage;
import org.openyu.socklet.message.vo.impl.MessageImpl;
import org.openyu.socklet.message.vo.impl.ClusterMessageImpl;
import org.openyu.socklet.socklet.service.SockletService;

/**
 * 接收器服務
 * 
 * SockletAceptor(master,slave1-...),可依多個port, 啟動多個SockletServer(socket server)
 * 
 * +-多個 ExtSockletContext(SockletEngine),合併context,engine
 * 
 * 改為單個ExtSockletContext
 * 
 * +-多個ExtSocklet(module)
 * 
 * +-單個ExtSockletConfig
 * 
 * +-單個ExtSockletRequest
 * 
 * +-單個ExtSockletResponse
 * 
 * +-多個SockletServer(實體socket server)
 * 
 * 刪除 ServerConf replace by spring *.xml
 * 
 * applicationContext-gss.xml(servicemap.ini)
 * 
 * applicationContext-global,zone1.xml(network.ini)
 * 
 * AceptorService啟動順序
 * 
 * +-單ContextService
 * 
 * +-多ServerService
 * 
 * +--relationServers
 * 
 * +--clientServers
 */
public class AcceptorServiceImpl extends BaseServiceSupporter implements AcceptorService {

	private static final long serialVersionUID = 3758237740118194791L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(AcceptorServiceImpl.class);

	/**
	 * 線程服務
	 */
	@AcceptorThreadService
	protected transient ThreadService threadService;

	/**
	 * 訊息服務
	 */
	@DefaultMessageService
	protected transient MessageService messageService;

	/**
	 * 協定服務
	 */
	@DefaultProtocolService
	protected transient ProtocolService protocolService;

	/**
	 * 認證碼服務
	 */
	@DefaultAuthKeyService
	protected transient AuthKeyService authKeyService;

	/**
	 * master/slave1...
	 */
	private String id;

	/**
	 * 取contextServiceImpl,因會用到內部方法
	 */
	protected ContextServiceImpl contextServiceImpl;

	// relationServers
	private List<String> relationServers = new LinkedList<String>();

	/**
	 * 主動連其他slave時,要用幾條socket連
	 */
	private static final int RELATION_CLIENT_COUNT = 1;

	// clientServers
	private List<String> clientServers = new LinkedList<String>();

	// 內部server,供內部server之間的連線
	// 127.0.0.1:3300,sockletServer
	private Map<String, ServerService> relationServices = new ConcurrentHashMap<String, ServerService>();

	// 外部server,供client之間的連線
	// 127.0.0.1:10300,sockletServer
	private Map<String, ServerService> clientServices = new ConcurrentHashMap<String, ServerService>();

	// 來自於內部其他server的client連線
	// <salve1,PassiveRelation>
	private Map<String, GenericRelation> passiveRelations = new ConcurrentHashMap<String, GenericRelation>();

	// 來自於外部client的連線
	// <sender,acceptorConnector>
	private Map<String, AcceptorConnector> acceptorConnectors = new ConcurrentHashMap<String, AcceptorConnector>();

	// 其他server上同步的client
	// <sender,acceptorConnector>
	private Map<String, AcceptorConnector> syncClients = new ConcurrentHashMap<String, AcceptorConnector>();

	private int maxClient;

	// ------------------------------------------------
	// cluster
	private String cluster;

	// cluster 頻道
	private Channel clusterChannel;

	private static List<String> clusterIds = new LinkedList<String>();

	// ------------------------------------------------

	// 連到其他的internal server
	// slave1...n
	// private List<String> relations = new LinkedList<String>();

	// <slave1,List<127.0.0.1:3300,127.0.0.1:3301>>
	// {login=[127.0.0.1:3101, 127.0.0.1:3100],account=[127.0.0.1:3001,
	// 127.0.0.1:3000]}
	private Map<String, List<String>> relations = new LinkedHashMap<String, List<String>>();

	// 主動連線到其他server的RelationClient
	// <master,InitiativeRelation>
	private Map<String, GenericRelation> initiativeRelations = new ConcurrentHashMap<String, GenericRelation>();

	// ------------------------------------------------

	private Map<String, String> initParameters = new ConcurrentHashMap<String, String>();

	/**
	 * 模組類別名稱
	 */
	private String moduleTypeName;

	/**
	 * 模組類別
	 */
	@SuppressWarnings("rawtypes")
	protected Class moduleTypeClass;

	/**
	 * 訊息類別名稱
	 */
	private String messageTypeName;

	/**
	 * 訊息類別
	 */
	@SuppressWarnings("rawtypes")
	protected Class messageTypeClass;

	// ------------------------------------------------
	// 訊息處理
	// ------------------------------------------------
	/**
	 * 判斷 destModue=CLIENT
	 */
	private static final String CLIENT = "CLIENT";

	/**
	 * 監聽cluster毫秒
	 */
	private static final long CLUSTER_LISTEN_MILLS = 5 * 1000L;

	/**
	 * 監聽client毫秒
	 */
	private static final long CLIENT_LISTEN_MILLS = 3 * 1000L;

	/**
	 * send
	 * 
	 * tcp, server->client, MESSAGE_SERVER,MESSAGE_RELATION
	 * 
	 * srcModule=FOUR_SYMBOL, destModule= CLIENT
	 */
	private transient SendClientQueue<Message> sendClientQueue;

	/**
	 * receive
	 * 
	 * tcp, client->server, MESSAGE_CLIENT
	 * 
	 * srcModule=CLIENT, destModule= FOUR_SYMBOL
	 */
	private transient ReceiveClientQueue<Message> receiveClientQueue;

	/**
	 * send
	 * 
	 * tcp, server->server, MESSAGE_SERVER
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=CHAT, destModule= FOUR_SYMBOL
	 * 
	 * srcModule=CLIENT, destModule= CORE
	 */
	private transient SendServerQueue<Message> sendServerQueue;

	/**
	 * send
	 * 
	 * tcp, server->relation, MESSAGE_RELATION
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=slave1.CHAT, destModule= slave2.FOUR_SYMBOL
	 * 
	 * srcModule=slave1.CLIENT, destModule= slave2.CORE
	 */
	private transient SendRelationQueue<RelationMessage> sendRelationQueue;

	/**
	 * receive
	 * 
	 * tcp, server->relation, MESSAGE_RELATION
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=slave1.CHAT, destModule= slave2.FOUR_SYMBOL
	 * 
	 * srcModule=slave1.CLIENT, destModule= slave2.CORE
	 * 
	 */
	private transient ReceiveRelationQueue<Message> receiveRelationQueue;

	/**
	 * send
	 * 
	 * udp, server->relation, MESSAGE_ACCEPTOR
	 */
	private transient SendAcceptorQueue<Message> sendAcceptorQueue;

	/**
	 * receive
	 * 
	 * udp, server->relation, MESSAGE_ACCEPTOR
	 */
	private transient ReceiveAcceptorQueue<Message> receiveAcceptorQueue;

	/**
	 * send
	 * 
	 * udp, server->relation, MESSAGE_SYNC
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=ROLE, destModule= ROLE
	 */
	private transient SendSyncQueue<Message> sendSyncQueue;

	/**
	 * receive
	 * 
	 * udp, server<-relation, MESSAGE_SYNC
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=ROLE, destModule= ROLE
	 */
	private transient ReceiveSyncQueue<Message> receiveSyncQueue;

	// runner
	private transient ClusterListenRunner clusterListenRunner;

	private transient ClientListenRunner clientListenRunner;

	private transient PassiveListenRunner passiveListenRunner;

	private transient InitiativeListenRunner initiativeListenRunner;

	/**
	 * 實例id
	 * 
	 * id=slave1 -> instanceId=01
	 * 
	 * id=slave1_02 -> instanceId=02
	 */
	private String instanceId;

	/**
	 * 輸出id
	 */
	private String outputId;

	// /**
	// * 重試次數, 0=無限
	// */
	// private int retryNumber = NioHelper.DEFAULT_RETRY_NUMBER;
	//
	// /**
	// * 重試暫停毫秒
	// */
	// private long retryPauseMills = NioHelper.DEFAULT_RETRY_PAUSE_MILLS;

	/**
	 * relation重試次數, 0=無限
	 */
	private int relationRetryNumber = NioHelper.DEFAULT_RETRY_NUMBER;

	/**
	 * relation重試暫停毫秒
	 */
	private long relationRetryPauseMills = NioHelper.DEFAULT_RETRY_PAUSE_MILLS;

	/**
	 * 已重試次數
	 */
	protected int relationTries;

	public AcceptorServiceImpl() {
	}

	public void setThreadService(ThreadService threadService) {
		this.threadService = threadService;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setProtocolService(ProtocolService protocolService) {
		this.protocolService = protocolService;
	}

	public void setAuthKeyService(AuthKeyService authKeyService) {
		this.authKeyService = authKeyService;
	}

	/**
	 * 模組類別名稱
	 * 
	 * @return
	 */
	public String getModuleTypeName() {
		return moduleTypeName;
	}

	public void setModuleTypeName(String moduleTypeName) {
		this.moduleTypeName = moduleTypeName;
		this.moduleTypeClass = ClassHelper.forName(moduleTypeName);
	}

	@SuppressWarnings("rawtypes")
	public Class getModuleTypeClass() {
		return moduleTypeClass;
	}

	/**
	 * 訊息類別名稱
	 * 
	 * @return
	 */
	public String getMessageTypeName() {
		return messageTypeName;
	}

	public void setMessageTypeName(String messageTypeName) {
		this.messageTypeName = messageTypeName;
		this.messageTypeClass = ClassHelper.forName(messageTypeName);
	}

	@SuppressWarnings("rawtypes")
	public Class getMessageTypeClass() {
		return messageTypeClass;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getRelationServers() {
		return relationServers;
	}

	public void setRelationServers(List<String> relationServers) {
		this.relationServers = relationServers;
	}

	public Map<String, ServerService> getRelationServerServices() {
		return relationServices;
	}

	public Map<String, GenericRelation> getPassiveRelations() {
		return passiveRelations;
	}

	public List<String> getClientServers() {
		return clientServers;
	}

	public void setClientServers(List<String> clientServers) {
		this.clientServers = clientServers;
	}

	public Map<String, ServerService> getClientServerServices() {
		return clientServices;
	}

	public Map<String, AcceptorConnector> getAcceptorConnectors() {
		return acceptorConnectors;
	}

	public AcceptorConnector getAcceptorConnector(String sender) {
		AcceptorConnector result = acceptorConnectors.get(sender);
		if (result == null) {
			// 同步
			result = syncClients.get(sender);
		}
		return result;
	}

	public Map<String, AcceptorConnector> getSyncClients() {
		return syncClients;
	}

	public int getMaxClient() {
		return maxClient;
	}

	public void setMaxClient(int maxClient) {
		this.maxClient = maxClient;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public Channel getClusterChannel() {
		return clusterChannel;
	}

	public Map<String, List<String>> getRelations() {
		return relations;
	}

	public void setRelations(Map<String, List<String>> relations) {
		this.relations = relations;
	}

	public Map<String, GenericRelation> getInitiativeRelations() {
		return initiativeRelations;
	}

	public Map<String, String> getInitParameters() {
		return initParameters;
	}

	public void setInitParameters(Map<String, String> initParameters) {
		this.initParameters = initParameters;
	}

	public int getCounter() {
		int counter = 0;
		for (ServerService serverService : clientServices.values()) {
			counter += serverService.getCounter();
		}

		return counter;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getOutputId() {
		return outputId;
	}

	public void setOutputId(String outputId) {
		this.outputId = outputId;
	}

	// public int getRetryNumber() {
	// return retryNumber;
	// }
	//
	// public void setRetryNumber(int retryNumber) {
	// this.retryNumber = retryNumber;
	// }
	//
	// public long getRetryPauseMills() {
	// return retryPauseMills;
	// }
	//
	// public void setRetryPauseMills(long retryPauseMills) {
	// this.retryPauseMills = retryPauseMills;
	// }

	public int getRelationRetryNumber() {
		return relationRetryNumber;
	}

	public void setRelationRetryNumber(int relationRetryNumber) {
		this.relationRetryNumber = relationRetryNumber;
	}

	public long getRelationRetryPauseMills() {
		return relationRetryPauseMills;
	}

	public void setRelationRetryPauseMills(long relationRetryPauseMills) {
		this.relationRetryPauseMills = relationRetryPauseMills;
	}

	// ------------------------------------------------
	// start step by step
	// ------------------------------------------------
	// 1.internals
	// 2.externals
	// 3.context
	// 4.cluster
	// 5.connect
	@Override
	protected void doStart() throws Exception {
		// ------------------------------------------------
		// tcp message
		// ------------------------------------------------
		// client
		sendClientQueue = new SendClientQueue<Message>(threadService);
		sendClientQueue.start();
		messageService.setClientQueue(sendClientQueue);
		//
		receiveClientQueue = new ReceiveClientQueue<Message>(threadService);
		receiveClientQueue.start();

		// server
		sendServerQueue = new SendServerQueue<Message>(threadService);
		sendServerQueue.start();
		messageService.setServerQueue(sendServerQueue);

		// relation
		sendRelationQueue = new SendRelationQueue<RelationMessage>(threadService);
		sendRelationQueue.start();
		//
		receiveRelationQueue = new ReceiveRelationQueue<Message>(threadService);
		receiveRelationQueue.start();

		// ------------------------------------------------
		// udp message
		// ------------------------------------------------
		// aceptor
		sendAcceptorQueue = new SendAcceptorQueue<Message>(threadService);
		sendAcceptorQueue.start();
		//
		receiveAcceptorQueue = new ReceiveAcceptorQueue<Message>(threadService);
		receiveAcceptorQueue.start();

		// sync
		sendSyncQueue = new SendSyncQueue<Message>(threadService);
		sendSyncQueue.start();
		messageService.setSyncQueue(sendSyncQueue);

		receiveSyncQueue = new ReceiveSyncQueue<Message>(threadService);
		receiveSyncQueue.start();

		// ------------------------------------------------
		// ContextService,須等內部完全啟動後,再繼續
		// ------------------------------------------------
		contextServiceImpl = new ContextServiceImpl(id);
		//
		contextServiceImpl.setApplicationContext(applicationContext);
		contextServiceImpl.setBeanFactory(beanFactory);
		contextServiceImpl.setResourceLoader(resourceLoader);
		//
		contextServiceImpl.setInitParameters(initParameters);
		contextServiceImpl.setModuleTypeClass(moduleTypeClass);
		contextServiceImpl.setMessageTypeClass(messageTypeClass);
		// 啟動
		contextServiceImpl.start();

		// cluster
		startCluster();
		clusterListenRunner = new ClusterListenRunner(threadService);
		clusterListenRunner.start();

		// ------------------------------------------------
		// ServerService
		// ------------------------------------------------
		startRelationServers();
		startClientServers();

		// acceptorConnectors
		clientListenRunner = new ClientListenRunner(threadService);
		clientListenRunner.start();
		// passiveRelations
		passiveListenRunner = new PassiveListenRunner(threadService);
		passiveListenRunner.start();
		// initiativeRelations
		buildInitiativeRelations();
		initiativeListenRunner = new InitiativeListenRunner(threadService);
		initiativeListenRunner.start();
	}

	// ------------------------------------------------
	// shutdown step by step
	// ------------------------------------------------
	// 3.context
	// 2.externals
	// 1.internals
	// 4.cluster
	// 5.connect
	@Override
	protected void doShutdown() throws Exception {
		// acceptorConnectors
		for (AcceptorConnector acceptorConnector : acceptorConnectors.values()) {
			ServerService serverService = clientServices.get(acceptorConnector.getServer());
			serverService.close(acceptorConnector);
		}

		// passiveRelations
		for (GenericRelation passiveRelation : passiveRelations.values()) {
			for (GenericConnector relationClient : passiveRelation.getClients().values()) {
				if (relationClient instanceof AcceptorConnector) {
					AcceptorConnector acceptorConnector = (AcceptorConnector) relationClient;
					ServerService serverService = relationServices.get(acceptorConnector.getServer());
					serverService.close(relationClient);
				}
			}
		}

		// clientServerServices
		for (Map.Entry<String, ServerService> entry : clientServices.entrySet()) {
			String key = entry.getKey();
			ServerService serverService = (ServerService) entry.getValue();
			serverService.shutdown();
			clientServices.remove(key);
		}

		// relationServerServices
		for (Map.Entry<String, ServerService> entry : relationServices.entrySet()) {
			ServerService serverService = entry.getValue();
			serverService.shutdown();
			relationServices.remove(entry.getKey());
		}

		// cluster
		try {
			if (clusterChannel != null && clusterChannel.isConnected()) {
				clusterChannel.close();
				clusterChannel = null;
				clusterIds.clear();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// context
		contextServiceImpl.shutdown();

		// queue
		sendClientQueue.shutdown();
		receiveClientQueue.shutdown();
		sendServerQueue.shutdown();
		sendRelationQueue.shutdown();
		receiveRelationQueue.shutdown();
		//
		sendAcceptorQueue.shutdown();
		receiveAcceptorQueue.shutdown();
		sendSyncQueue.shutdown();
		receiveSyncQueue.shutdown();

		// listen
		clusterListenRunner.shutdown();
		clientListenRunner.shutdown();
		passiveListenRunner.shutdown();
		initiativeListenRunner.shutdown();
	}

	protected void clusterSend(org.jgroups.Message msg) {
		try {
			if (clusterChannel != null && clusterChannel.isConnected()) {
				clusterChannel.send(msg);
			} else {
				LOGGER.error("[" + id + "] ClusterChannel Had not been started");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 提供給內部連線
	 */
	protected void startRelationServers() throws Exception {
		// ------------------------------------------------
		// relationServers, 127.0.0.1:3300, 127.0.0.1:3301
		// ------------------------------------------------
		// 內部不用加密
		for (String ipPort : relationServers) {
			if (StringHelper.isEmpty(ipPort)) {
				continue;
			}
			//
			String[] buff = ipPort.split(":");
			// [0]=ip
			// [1]=port
			if (buff.length == 2) {
				ServerServiceImpl serverService = new ServerServiceImpl(ipPort, true);
				//
				serverService.setApplicationContext(applicationContext);
				serverService.setBeanFactory(beanFactory);
				serverService.setResourceLoader(resourceLoader);
				//
				serverService.setThreadService(threadService);
				serverService.setMessageService(messageService);
				serverService.setProtocolService(protocolService);
				serverService.setAuthKeyService(authKeyService);
				serverService.setAcceptorServiceImpl(this);
				serverService.setContextServiceImpl(contextServiceImpl);
				//
				serverService.setIp(buff[0]);
				int port = NumberHelper.toInt(buff[1]);
				serverService.setPort(port);
				//
				int maxCounter = (int) NumberHelper.divide(maxClient, relationServers.size());
				serverService.setMaxClient(maxCounter);
				serverService.setModuleTypeClass(moduleTypeClass);
				serverService.setMessageTypeClass(messageTypeClass);
				//
				serverService.start();
			} else {
				LOGGER.error(
						"[" + id + "]" + " Wrong [" + ipPort + "] format, ex: [127.0.0.1:3300] or [localhost:3300]");
			}
		}
	}

	/**
	 * 提供給外部連線
	 */
	protected void startClientServers() throws Exception {
		// ------------------------------------------------
		// clientServers, 127.0.0.1:10300, 127.0.0.1:10301
		// ------------------------------------------------
		if (clientServers == null || clientServers.isEmpty()) {
			LOGGER.warn("[" + id + "]" + " ClientServer is null, not been started");
			return;
		}

		// ------------------------------------------------
		// clientServers, localhost:2100
		// ------------------------------------------------
		// 外部,可選加密通訊
		for (String ipPort : clientServers) {
			if (StringHelper.isEmpty(ipPort)) {
				continue;
			}
			//
			String[] buff = ipPort.split(":");
			// [0]=ip
			// [1]=port
			if (buff.length == 2) {
				ServerServiceImpl serverService = new ServerServiceImpl(ipPort, false);
				//
				serverService.setApplicationContext(applicationContext);
				serverService.setBeanFactory(beanFactory);
				serverService.setResourceLoader(resourceLoader);
				//
				serverService.setThreadService(threadService);
				serverService.setMessageService(messageService);
				serverService.setProtocolService(protocolService);
				serverService.setAuthKeyService(authKeyService);
				serverService.setAcceptorServiceImpl(this);
				serverService.setContextServiceImpl(contextServiceImpl);
				//
				serverService.setIp(buff[0]);
				int port = NumberHelper.toInt(buff[1]);
				serverService.setPort(port);
				//
				int maxCounter = (int) NumberHelper.divide(maxClient, relationServers.size());
				serverService.setMaxClient(maxCounter);
				serverService.setModuleTypeClass(moduleTypeClass);
				serverService.setMessageTypeClass(messageTypeClass);
				//
				serverService.start();
			} else {
				LOGGER.error(
						"[" + id + "]" + " Wrong [" + ipPort + "] format, ex: [localhost:2000] or [127.0.0.1:2000]");
			}
		}
	}

	/**
	 * 主動關係連線 slave2 -> slave1
	 */
	protected void buildInitiativeRelations() {
		for (Map.Entry<String, List<String>> entry : relations.entrySet()) {
			String destAcceptorId = entry.getKey();
			for (String ipPort : entry.getValue()) {
				String[] buff = ipPort.split(":");
				// [0]=ip
				// [1]=port
				if (buff.length == 2) {
					buildRelation(destAcceptorId, buff[0], NumberHelper.toInt(buff[1]));
				} else {
					LOGGER.error("[" + id + "]" + " Wrong [" + destAcceptorId + "] format, ex: [slave1]");
				}
			}
		}
	}

	/**
	 * 連線關係, slave1
	 * 
	 * slave1.relationServers.0=127.0.0.1:3300
	 * 
	 * @param destAcceptorId
	 * @param ip
	 * @param port
	 */
	protected void buildRelation(String destAcceptorId, String ip, int port) {
		// 訊息接收者
		InitiativeReceiver initiativeReceiver = new InitiativeReceiver();
		// 主動關係連線
		GenericRelation initiativeRelation = initiativeRelations.get(destAcceptorId);
		if (initiativeRelation == null) {
			initiativeRelation = new GenericRelationImpl(destAcceptorId);
			initiativeRelations.put(destAcceptorId, initiativeRelation);
		}
		//
		for (int i = 0; i < RELATION_CLIENT_COUNT; i++) {
			// slave2:0:127.0.0.1:3300, 來源id:index:目的ip:port
			String relationClientId = id + ":" + i + ":" + ip + ":" + port;
			// System.out.println(relationClientId);
			boolean contains = initiativeRelation.getClients().containsKey(relationClientId);
			if (!contains) {
				// slave2:0:127.0.0.1:3300
				RelationConnector relationConnector = new RelationConnectorImpl(relationClientId, moduleTypeClass,
						messageTypeClass, protocolService, ip, port);
				relationConnector.setReceiver(initiativeReceiver);
				relationConnector.setRetryNumber(1);
				relationConnector.setRetryPauseMills(1 * 1000L);
				//
				initiativeRelation.getClients().put(relationClientId, relationConnector);
			}
		}
	}

	/**
	 * 監聽主動端連線
	 */
	protected class InitiativeListenRunner extends BaseRunnableSupporter {

		public InitiativeListenRunner(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doRun() throws Exception {
			while (true) {
				try {
					if (isShutdown()) {
						break;
					}
					initiativeListen();
					ThreadHelper.sleep(CLIENT_LISTEN_MILLS);
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 監聽主動端連線
	 */
	protected void initiativeListen() {
		int relationsSize = initiativeRelations.size();
		for (GenericRelation initiativeRelation : initiativeRelations.values()) {
			boolean clientStarted = false;
			String relationId = initiativeRelation.getId();
			int clientSize = initiativeRelation.getClients().size();
			for (GenericConnector genericConnector : initiativeRelation.getClients().values()) {
				// 當沒有連上時,嘗試重連
				// [login] account, login:0:127.0.0.1:3001, true
				// LOGGER.info("[" + id + "] connected " + relationId + ", " +
				// genericConnector.getId() + ", "
				// + genericConnector.isStarted());
				if (genericConnector.isStarted()
						// 0=無限
						|| (relationRetryNumber != 0
								&& relationTries >= (relationRetryNumber * relationsSize * clientSize))) {
					continue;
				} else {
					genericConnector.setTries(0);
					//
					relationTries++;
					int buffTries = (relationTries % (relationsSize * clientSize) == 0
							? relationTries / (relationsSize * clientSize)
							: (relationTries / (relationsSize * clientSize)) + 1);
					ThreadHelper.sleep(NioHelper.retryPause(buffTries, relationRetryPauseMills));
					LOGGER.info("Retrying connect to [" + relationId + "]. Already tried [" + buffTries + "/"
							+ (relationRetryNumber != 0 ? relationRetryNumber : "N") + "] time(s).");
				}
				//
				genericConnector.start();
				// 連線成功
				if (genericConnector.isStarted()) {
					if (!initiativeRelation.isConnected()) {
						// 網路層同步,所有slave都會收到
						for (AcceptorConnector currentClient : acceptorConnectors.values()) {
							sendSyncClientConnect(currentClient);
						}

						initiativeRelation.setConnected(true);
						// 邏輯層同步,用relationEvent處理
						contextServiceImpl.fireRelationConnected(relationId);
					}
				}
				// 無法連線
				else {
					contextServiceImpl.fireRelationRefused(relationId);
				}

				//
				clientStarted |= genericConnector.isStarted();
				ThreadHelper.sleep(50L);
			}

			// relation的socket都斷了
			if (!clientStarted && initiativeRelation.isConnected()) {
				// 網路層同步,沒做任何事

				initiativeRelation.setConnected(false);
				// 邏輯層同步,用relationEvent處理
				contextServiceImpl.fireRelationDisconnected(relationId);
			}
		}
	}

	protected void startCluster() {
		try {
			if (cluster != null) {
				clusterChannel = new JChannel();
				clusterChannel.setReceiver(new ClusterReceiver());
				clusterChannel.setDiscardOwnMessages(true);// 自己本身發出的訊息不接收
				clusterListen();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 監聽cluster連線
	 */
	protected class ClusterListenRunner extends BaseRunnableSupporter {

		public ClusterListenRunner(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doRun() throws Exception {
			while (true) {
				try {
					if (isShutdown()) {
						break;
					}
					ThreadHelper.sleep(CLUSTER_LISTEN_MILLS);
					clusterListen();
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 監聽cluster連線
	 */
	protected void clusterListen() {
		try {
			if (clusterChannel != null && !clusterChannel.isConnected()) {
				clusterChannel.connect(cluster);
				clusterIds.add(id);
				//
				StringBuilder buff = new StringBuilder();
				buff.append("ClusterChannel Had [");
				buff.append(clusterChannel.getView().size());
				buff.append("] members, acceptor[");
				int size = clusterIds.size();
				for (int i = 0; i < size; i++) {
					buff.append(clusterIds.get(i));
					if (i < size - 1) {
						buff.append(", ");
					}
				}
				buff.append("], ");
				buff.append(clusterChannel.getView());
				//
				LOGGER.info(buff.toString());
			}
		} catch (Exception ex) {
			LOGGER.error("[" + id + "] ClusterChannel Started fail", ex);
		}
	}

	/**
	 * 加入訊息到queue
	 * 
	 * @param message
	 * @return
	 */
	protected boolean addMessage(Message message) {
		boolean result = false;
		//
		CategoryType categoryType = message.getCategoryType();
		// System.out.println("categoryType: " + categoryType);
		// System.out.println("getDestModule: " + message.getDestModule());
		// MESSAGE_CLIENT
		if (CategoryType.MESSAGE_CLIENT == categoryType) {
			if (message.getSrcModule() == null) {
				@SuppressWarnings("unchecked")
				ModuleType moduleType = (ModuleType) EnumHelper.valueOf(moduleTypeClass, CLIENT);
				message.setSrcModule(moduleType);
			}
			result = receiveClientQueue.offer(message);
		}
		// MESSAGE_RELATION
		else if (CategoryType.MESSAGE_RELATION == categoryType && !CLIENT.equals(message.getDestModule().name())) {
			// result = receiveRelations.offer(message);
			result = receiveRelationQueue.offer(message);
		}
		// MESSAGE_RELATION,dest=CLIENT
		else if (CategoryType.MESSAGE_RELATION == categoryType && CLIENT.equals(message.getDestModule().name())) {
			messageService.addMessage(message);
		} else {
			LOGGER.warn("Undefined: " + categoryType + ", " + message);
		}
		// System.out.println(message);
		return result;
	}

	/**
	 * 加入訊息到queue
	 * 
	 * @param messages
	 * @return
	 */
	protected boolean addMessages(List<Message> messages) {
		boolean result = false;
		for (Message message : messages) {
			if (message == null) {
				continue;
			}
			result |= addMessage(message);
		}
		return result;
	}

	protected class SendClientQueue<E> extends TriggerQueueSupporter<Message> {

		public SendClientQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			sendClient(e);
		}
	}

	/**
	 * 發送client訊息
	 * 
	 * tcp, server->client, MESSAGE_SERVER,MESSAGE_RELATION
	 * 
	 * srcModule=FOUR_SYMBOL, destModule= CLIENT
	 */
	protected void sendClient(Message message) {
		// slave1...n
		// MESSAGE_SERVER
		if (CategoryType.MESSAGE_SERVER.equals(message.getCategoryType())) {
			message.setSender(id);
		}
		// 若MESSAGE_RELATION,會有sender
		else if (CategoryType.MESSAGE_RELATION.equals(message.getCategoryType())) {
			//
		}

		if (ConfigHelper.isDebug()) {
			System.out.println(message);
		}
		//
		int receiverSize = message.getReceivers().size();
		// 當有receiver時
		if (receiverSize > 0) {
			for (String receiver : message.getReceivers()) {
				// 本地acceptorConnector
				AcceptorConnector acceptorConnector = acceptorConnectors.get(receiver);
				if (acceptorConnector != null) {
					if (!acceptorConnector.isValid()) {
						continue;
					}
					// clone message
					Message cloneMessage = (Message) message.clone();
					cloneMessage.getReceivers().clear();
					cloneMessage.addReceiver(receiver);// 只放自己的receiver
					//
					byte[] buff = protocolService.assemble(cloneMessage);
					int write = acceptorConnector.send(buff);
					if (write > 0) {
						// just for debug
					}
				}
				// 不在本地的client,轉發到其他relation的client
				else {
					acceptorConnector = syncClients.get(receiver);
					if (acceptorConnector != null) {
						if (!acceptorConnector.isValid()) {
							LOGGER.warn("[" + receiver + "] Has no connection to send");
							continue;
						}
						// clone message
						Message cloneMessage = (Message) message.clone();
						cloneMessage.getReceivers().clear();
						cloneMessage.addReceiver(receiver);
						cloneMessage.setSender(id);
						cloneMessage.setCategoryType(CategoryType.MESSAGE_RELATION);
						//
						RelationMessage relationMessage = new RelationMessage(acceptorConnector.getAcceptor(),
								cloneMessage);
						// sendRelations.offer(relationMessage);
						sendRelationQueue.offer(relationMessage);
					} else {
						LOGGER.warn("[" + receiver + "] Has no connection to send");
						continue;
					}
				}
			}
		}
	}

	/**
	 * 接收client訊息
	 */
	protected class ReceiveClientQueue<E> extends TriggerQueueSupporter<Message> {

		public ReceiveClientQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			dispatch(e);
		}
	}

	/**
	 * relation訊息
	 */
	protected static class RelationMessage {
		private String acceptor;

		private Message message;

		public RelationMessage(String acceptor, Message message) {
			this.acceptor = acceptor;
			this.message = message;
		}

		public String getAcceptor() {
			return acceptor;
		}

		public void setAcceptor(String acceptor) {
			this.acceptor = acceptor;
		}

		public Message getMessage() {
			return message;
		}

		public void setMessage(Message message) {
			this.message = message;
		}
	}

	/**
	 * 派發訊息
	 * 
	 * @param message
	 */
	protected void dispatch(Message message) {
		@SuppressWarnings("unchecked")
		Enum<?> destModule = EnumHelper.valueOf(moduleTypeClass, message.getDestModule().getValue());
		// System.out.println("destModule: " + destModule);
		SockletService sockletService = contextServiceImpl.getSockletServices().get(destModule);
		// System.out.println("sockletService: " + sockletService);
		// 若在此acceptor的sockletService,則執行service
		if (message.getSender() == null) {
			message.setSender(id);
		}
		//
		if (sockletService != null) {
			if (ConfigHelper.isDebug()) {
				System.out.println(message);
			}

			// 1.會有先後順序的問題,可能需依client的thread來執行
			// sockletService.threadService.submit(
			// new ServiceRunner(sockletService, message));

			// 2.子緒處理,但只要有一個module堵塞,就卡住了
			// sockletService.service(message);

			// 3.Socklet子緒處理
			sockletService.addMessage(message);
		}
		// 若不在此acceptor的sockletService,則轉發到其他acceptor
		else {
			sockletService = contextServiceImpl.getRelationServices().get(destModule);
			if (sockletService != null) {
				for (String acceptor : sockletService.getAcceptors()) {
					Message cloneMessage = (Message) message.clone();
					cloneMessage.setSender(id);
					cloneMessage.setCategoryType(CategoryType.MESSAGE_RELATION);
					//
					if (ConfigHelper.isDebug()) {
						System.out.println(cloneMessage);
					}

					RelationMessage relationMessage = new RelationMessage(acceptor, cloneMessage);
					// sendRelations.offer(relationMessage);
					sendRelationQueue.offer(relationMessage);
				}
			}
			// 都找不到時,就是沒有socklet可處理
			else {
				// System.out.println(acceptorConnector.getSender());
				LOGGER.error("Can't resolve: " + message);
			}
		}
	}

	/**
	 * 發送server訊息
	 * 
	 * tcp, server->server, MESSAGE_SERVER
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=CHAT, destModule= FOUR_SYMBOL
	 * 
	 * srcModule=CLIENT, destModule= CORE
	 */
	protected class SendServerQueue<E> extends TriggerQueueSupporter<Message> {

		public SendServerQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			dispatch(e);
		}
	}

	// --------------------------------------------------
	// MESSAGE_RELATION
	// --------------------------------------------------
	/**
	 * 發送relation訊息
	 */
	protected class SendRelationQueue<E> extends TriggerQueueSupporter<RelationMessage> {

		public SendRelationQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(RelationMessage e) throws Exception {
			sendRelation(e);
		}
	}

	/**
	 * 發送relation訊息
	 * 
	 * 轉發message到relation server上
	 * 
	 * @param relationMessage
	 */
	protected void sendRelation(RelationMessage relationMessage) {
		try {
			String acceptor = relationMessage.getAcceptor();
			Message message = relationMessage.getMessage();
			if (relationMessage == null || acceptor == null || message == null) {
				return;
			}
			// 先找主動關係連線
			GenericRelation initiativeRelation = initiativeRelations.get(acceptor);
			// System.out.println("initiativeRelation: " + initiativeRelation);
			if (initiativeRelation != null && initiativeRelation.isConnected()) {
				int write = initiativeRelation.send(message);
				if (write > 0) {
					// just for debug
				}
				// System.out.println("write: " + write);
			} else {
				// 再找被動關係連線
				GenericRelation passiveRelation = passiveRelations.get(acceptor);
				// System.out.println("passiveRelation: " + passiveRelation);
				if (passiveRelation != null && passiveRelation.isConnected()) {
					passiveRelation.send(message);
					// System.out.println("write: " + write);
				} else {
					LOGGER.error("Can't resolve: " + message);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 接收relation訊息
	 * 
	 * tcp, server->relation, MESSAGE_RELATION
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=slave1.CHAT, destModule= slave2.FOUR_SYMBOL
	 * 
	 * srcModule=slave1.CLIENT, destModule= slave2.CORE
	 */
	protected class ReceiveRelationQueue<E> extends TriggerQueueSupporter<Message> {

		public ReceiveRelationQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			dispatch(e);
		}
	}

	// --------------------------------------------------
	// MESSAGE_ACCEPTOR
	// --------------------------------------------------
	protected class SendAcceptorQueue<E> extends TriggerQueueSupporter<Message> {

		public SendAcceptorQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			sendAcceptor(e);
		}
	}

	/**
	 * 發送acceptor
	 * 
	 * @param message
	 */
	protected void sendAcceptor(Message message) {
		// slave1...n
		// MESSAGE_ACCEPTOR
		if (CategoryType.MESSAGE_ACCEPTOR.equals(message.getCategoryType())) {
			message.setSender(id);
		}
		//
		byte[] buff = protocolService.assemble(message);
		if (buff != null) {
			ClusterMessage clusterMessage = new ClusterMessageImpl(CategoryType.MESSAGE_ACCEPTOR, buff);
			org.jgroups.Message msg = new org.jgroups.Message(null, null, clusterMessage);
			clusterSend(msg);
		}
	}

	/**
	 * 接收acceptor訊息
	 * 
	 * udp,server->relation, MESSAGE_ACCEPTOR
	 */
	protected class ReceiveAcceptorQueue<E> extends TriggerQueueSupporter<Message> {

		public ReceiveAcceptorQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			receiveAcceptor(e);
		}
	}

	protected void receiveAcceptor(Message message) {
		AcceptorMessageType messageType = (AcceptorMessageType) message.getMessageType();
		switch (messageType) {
		case ACCEPTOR_SYNC_CLIENT_CONNECT_REQUEST: {
			String clientSender = message.getString(0);
			String clientAcceptor = message.getString(1);
			byte[] authKey = message.getByteArray(2);
			boolean handshake = message.getBoolean(3);
			String serverIp = message.getString(4);
			int serverPort = message.getInt(5);
			String clientIp = message.getString(6);
			int clientPort = message.getInt(7);
			syncClientConnect(clientSender, clientAcceptor, authKey, handshake, serverIp, serverPort, clientIp,
					clientPort);
			break;
		}
		case ACCEPTOR_SYNC_CLIENT_DISCONNECT_REQUEST: {
			String clientSender = message.getString(0);
			syncClientDisconnect(clientSender);
			break;
		}
		default: {
			break;
		}
		}
	}

	/**
	 * 建構Message MESSAGE_ACCEPTOR, server side用
	 * 
	 * @param messageType
	 * @return
	 */
	protected Message createAcceptor(MessageType messageType) {
		Message result = new MessageImpl(CategoryType.MESSAGE_ACCEPTOR, PriorityType.MEDIUM, id, messageType);
		return result;
	}

	/**
	 * 發送同步客戶端連線
	 * 
	 * @param acceptorConnector
	 */
	protected void sendSyncClientConnect(AcceptorConnector acceptorConnector) {
		Message message = createAcceptor(AcceptorMessageType.ACCEPTOR_SYNC_CLIENT_CONNECT_REQUEST);
		//
		message.addString(acceptorConnector.getSender());// client sender
		message.addString(acceptorConnector.getAcceptor());// client acceptor
		message.addByteArray(acceptorConnector.getAuthKey());// 認證碼
		message.addBoolean(acceptorConnector.isHandshake());// 是否握手成功
		//
		message.addString(acceptorConnector.getServerIp());
		message.addInt(acceptorConnector.getServerPort());
		message.addString(acceptorConnector.getClientIp());
		message.addInt(acceptorConnector.getClientPort());
		//
		sendAcceptorQueue.offer(message);
	}

	/**
	 * 收到同步客戶端連線
	 * 
	 * @param clientSender
	 * @param clientAcceptor
	 * @param authKey
	 * @param handshake
	 * @param serverIp
	 * @param serverPort
	 * @param clientIp
	 * @param clientPort
	 */
	protected void syncClientConnect(String clientSender, String clientAcceptor, byte[] authKey, boolean handshake,
			String serverIp, int serverPort, String clientIp, int clientPort) {
		AcceptorConnector acceptorConnector = new AcceptorConnectorImpl(ByteHelper.toString(authKey));// 認證碼當id
		acceptorConnector.setSender(clientSender);
		acceptorConnector.setAcceptor(clientAcceptor);
		acceptorConnector.setAuthKey(authKey);
		acceptorConnector.setHandshake(handshake);
		//
		acceptorConnector.setServerIp(serverIp);
		acceptorConnector.setServerPort(serverPort);
		acceptorConnector.setClientIp(clientIp);
		acceptorConnector.setClientPort(clientPort);

		// 仿一個client連線
		syncClients.put(acceptorConnector.getSender(), acceptorConnector);
		// log.info("收到acceptor同步連線: " + acceptorConnector.getSender());
	}

	/**
	 * 發送同步客戶端斷線
	 * 
	 * @param acceptorConnector
	 */
	protected void sendSyncClientDisconnect(GenericConnector genericConnector) {
		Message message = createAcceptor(AcceptorMessageType.ACCEPTOR_SYNC_CLIENT_DISCONNECT_REQUEST);
		//
		message.addString(genericConnector.getSender());// client sender
		//
		sendAcceptorQueue.offer(message);
	}

	/**
	 * 收到同步客戶端斷線
	 * 
	 * @param clientSender
	 */
	protected void syncClientDisconnect(String clientSender) {
		if (clientSender != null) {
			syncClients.remove(clientSender);
		}
		// log.info("收到acceptor同步斷線: " + clientSender);
	}

	// --------------------------------------------------
	// MESSAGE_SYNC
	// --------------------------------------------------
	/**
	 * 發送同步訊息
	 * 
	 * udp,server->relation, MESSAGE_SYNC
	 * 
	 * srcModule=CHAT, destModule= CHAT
	 * 
	 * sender=slave1...n
	 */
	protected class SendSyncQueue<E> extends TriggerQueueSupporter<Message> {

		public SendSyncQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			sendSync(e);
		}
	}

	/**
	 * 發送同步訊息
	 * 
	 * udp, server->relation, MESSAGE_SYNC
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=ROLE, destModule= ROLE
	 * 
	 * @param message
	 */
	protected void sendSync(Message message) {
		// slave1...n
		// MESSAGE_SYNC
		if (CategoryType.MESSAGE_SYNC.equals(message.getCategoryType())) {
			message.setSender(id);
		}

		if (ConfigHelper.isDebug()) {
			System.out.println(message);
		}

		// 若只有一個member,cluster就不用發訊息了
		if (clusterChannel == null || !clusterChannel.isConnected() || clusterChannel.getView().size() < 2) {
			return;
		}
		//
		byte[] buff = protocolService.assemble(message);
		if (buff != null) {
			ClusterMessage syncMessage = new ClusterMessageImpl(CategoryType.MESSAGE_SYNC, buff);
			org.jgroups.Message msg = new org.jgroups.Message(null, null, syncMessage);
			clusterSend(msg);
		}
	}

	protected class ReceiveSyncQueue<E> extends TriggerQueueSupporter<Message> {

		public ReceiveSyncQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			receiveSync(e);
		}
	}

	/**
	 * 接收同步訊息
	 * 
	 * udp,server<-relation, MESSAGE_SYNC
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=slave1.CHAT, destModule= slave2.FOUR_SYMBOL
	 * 
	 * srcModule=slave1.CLIENT, destModule= slave2.CORE
	 * 
	 * @param message
	 * @return
	 */
	protected void receiveSync(Message message) {
		@SuppressWarnings("unchecked")
		Enum<?> destModule = EnumHelper.valueOf(moduleTypeClass, message.getDestModule().getValue());
		// System.out.println("destModule: " + destModule);
		SockletService sockletService = contextServiceImpl.getSockletServices().get(destModule);
		// System.out.println("sockletService: " + sockletService);
		// 若在此acceptor的sockletService,則執行service
		if (sockletService != null) {
			if (ConfigHelper.isDebug()) {
				System.out.println(message);
			}

			// 1.會有先後順序的問題,可能需依client的thread來執行
			// sockletService.threadService.submit(
			// new ServiceRunner(sockletService, message));

			// 2.子緒處理,但只要有一個module堵塞,就卡住了
			// sockletService.service(message);

			// 3.Socklet子緒處理,目前採用此方式 2013/01/01
			sockletService.addMessage(message);
		}
		// ThreadHelper.sleep(LOOP_MILLS);
		else {
			// 找不到時,就是沒有socklet可處理
			LOGGER.error("Can't resolve: " + message);
		}
	}

	/**
	 * 主動接收者
	 */
	protected class InitiativeReceiver extends GenericReceiverSupporter {

		private static final long serialVersionUID = 4173116010451013673L;

		public void receive(Message message) {
			// from [account] MESSAGE_RELATION, (11101) LOGIN_AUTHORIZE_REQUEST,
			// (11000) ACCOUNT => (11100) LOGIN to []
			// System.out.println("InitiativeReceiver: " + message);
			try {
				addMessage(message);
			} catch (Exception e) {
				LOGGER.error(new StringBuilder("Exception encountered during receive()").toString(), e);
			}
		}
	}

	/**
	 * 監聽客戶端連線
	 */
	protected class ClientListenRunner extends BaseRunnableSupporter {

		public ClientListenRunner(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doRun() throws Exception {
			while (true) {
				try {
					if (isShutdown()) {
						break;
					}
					//
					clientListen();
					ThreadHelper.sleep(CLIENT_LISTEN_MILLS);
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 監聽客戶端連線
	 */
	protected void clientListen() {
		// acceptorConnectors
		for (AcceptorConnector acceptorConnector : acceptorConnectors.values()) {
			boolean disconnected = false;
			try {
				// check by read
				int read = acceptorConnector.getSocketChannel().read(ByteBuffer.allocate(1));
				// System.out.println("listenClient..."+acceptorConnector.getSender()+"
				// "+read);
				// 斷線了
				if (read == -1) {
					disconnected = true;
				}
			} catch (Exception ex) {
				// ex.printStackTrace();
				disconnected = true;
			}
			// 若斷線就關閉client連線
			if (disconnected) {
				ServerService serverService = clientServices.get(acceptorConnector.getServer());
				serverService.close(acceptorConnector);
			}
		}
	}

	/**
	 * 監聽被動端連線
	 */
	protected class PassiveListenRunner extends BaseRunnableSupporter {

		public PassiveListenRunner(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doRun() throws Exception {
			while (true) {
				try {
					if (isShutdown()) {
						break;
					}
					passiveListen();
					ThreadHelper.sleep(CLIENT_LISTEN_MILLS);
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 監聽被動端連線
	 */
	protected void passiveListen() {
		// passiveRelations
		for (GenericRelation passiveRelation : passiveRelations.values()) {
			for (GenericConnector genericConnector : passiveRelation.getClients().values()) {
				try {
					// check by read
					genericConnector.getSocketChannel().read(ByteBuffer.allocate(1));
				} catch (Exception ex) {
					// ex.printStackTrace();
					if (genericConnector instanceof AcceptorConnector) {
						AcceptorConnector acceptorConnector = (AcceptorConnector) genericConnector;
						ServerService serverService = clientServices.get(acceptorConnector.getServer());
						if (serverService != null) {
							serverService.close(acceptorConnector);
						} else {
							serverService = relationServices.get(acceptorConnector.getServer());
							if (serverService != null) {
								serverService.close(acceptorConnector);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * cluster接收者
	 */
	protected class ClusterReceiver extends ReceiverAdapter {

		public void receive(org.jgroups.Message msg) {
			Object object = msg.getObject();
			if (object instanceof ClusterMessage) {
				ClusterMessage clusterMessage = (ClusterMessage) object;
				CategoryType categoryType = clusterMessage.getCategoryType();
				switch (categoryType) {
				// acceptor用
				case MESSAGE_ACCEPTOR: {
					byte[] buff = clusterMessage.getBody();
					//
					List<Message> messages = protocolService.disassemble(buff, AcceptorModuleType.class,
							AcceptorMessageType.class);
					for (Message message : messages) {
						try {
							receiveAcceptorQueue.offer(message);
						} catch (Exception e) {
							LOGGER.error(new StringBuilder("Exception encountered during receive()").toString(), e);
						}
					}
					break;
				}
					// 邏輯用
				case MESSAGE_SYNC: {
					byte[] buff = clusterMessage.getBody();
					//
					@SuppressWarnings("unchecked")
					List<Message> messages = protocolService.disassemble(buff, moduleTypeClass, messageTypeClass);
					for (Message message : messages) {
						try {
							receiveSyncQueue.offer(message);
						} catch (Exception e) {
							LOGGER.error(new StringBuilder("Exception encountered during receive()").toString(), e);
						}
					}
					break;
				}
				default: {
					break;
				}
				}
			}
			// 留給以後有新的message定義用
			// else if (object instanceof xxx){

			// }
		}
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", id);
		builder.append("instanceId", instanceId);
		builder.append("outputId", outputId);
		builder.append("relationServers", relationServers);
		builder.append("clientServers", clientServers);
		builder.append("maxClient", maxClient);
		//
		builder.append("cluster", cluster);
		builder.append("relations", relations);
		builder.append("initParameters", initParameters);
		//
		builder.append("relationRetryNumber", relationRetryNumber);
		builder.append("relationRetryPauseMills", relationRetryPauseMills);
		return builder.toString();
	}

}
