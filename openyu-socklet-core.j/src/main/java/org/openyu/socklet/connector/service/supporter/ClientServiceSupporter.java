package org.openyu.socklet.connector.service.supporter;

import org.openyu.commons.lang.ClassHelper;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.commons.thread.ThreadService;
import org.openyu.commons.thread.supporter.TriggerQueueSupporter;
import org.openyu.socklet.connector.service.ClientService;
import org.openyu.socklet.connector.vo.JavaConnector;
import org.openyu.socklet.connector.vo.impl.JavaConnectorImpl;
import org.openyu.socklet.connector.vo.supporter.GenericReceiverSupporter;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 客戶端服務
 */
public abstract class ClientServiceSupporter extends BaseServiceSupporter implements ClientService {

	private static final long serialVersionUID = -1548288681963437153L;

	private static final transient Logger LOGGER = LoggerFactory.getLogger(ClientServiceSupporter.class);

	/**
	 * 線程服務
	 */
	@Autowired
	@Qualifier("threadService")
	protected transient ThreadService threadService;

	/**
	 * 訊息服務
	 */
	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;

	/**
	 * 協定服務
	 */
	@Autowired
	@Qualifier("protocolService")
	protected transient ProtocolService protocolService;
	/**
	 * id,sender
	 */
	protected String id;

	/**
	 * 模組類別名稱
	 */
	protected String moduleTypeName;

	/**
	 * 模組類別
	 */
	@SuppressWarnings("rawtypes")
	protected Class moduleTypeClass;

	/**
	 * 訊息類別名稱
	 */
	protected String messageTypeName;

	/**
	 * 訊息類別
	 */
	@SuppressWarnings("rawtypes")
	protected Class messageTypeClass;

	/**
	 * ip
	 */
	protected String ip;

	/**
	 * port
	 */
	protected int port;

	/**
	 * java客戶端
	 */
	protected JavaConnector javaConnector;

	/**
	 * send
	 * 
	 * tcp, client->server, MESSAGE_CLIENT
	 * 
	 * messageType=FOUR_SYMBOL_PLAY_REQUEST
	 */

	private SendQueue<Message> sendQueue;

	/**
	 * receive
	 * 
	 * tcp, server->client, MESSAGE_SERVER
	 * 
	 * messageType=FOUR_SYMBOL_PLAY_REQUEST
	 */

	private ReceiveQueue<Message> receiveQueue;

	public ClientServiceSupporter() {
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

	/**
	 * id,sender
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	/**
	 * ip
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * port
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public JavaConnector getJavaConnector() {
		return javaConnector;
	}

	public void setJavaConnector(JavaConnector javaConnector) {
		this.javaConnector = javaConnector;
	}

	/**
	 * 啟動
	 */
	@Override
	protected void doStart() throws Exception {
		javaConnector = new JavaConnectorImpl(moduleTypeClass, messageTypeClass, protocolService);
		// javaConnector.setThreadService(threadService);
		ClientServiceReceiver clientServiceReceiver = new ClientServiceReceiver();
		javaConnector.setReceiver(clientServiceReceiver);
		//
		javaConnector.setId(id);
		javaConnector.setIp(ip);
		javaConnector.setPort(port);
		//
		javaConnector.start();// 啟動連線
		// ----------------------------------------------
		sendQueue = new SendQueue<Message>(threadService);
		sendQueue.start();
		//
		receiveQueue = new ReceiveQueue<Message>(threadService);
		receiveQueue.start();
	}

	@Override
	protected void doShutdown() throws Exception {
		if (javaConnector != null) {
			javaConnector.shutdown();
		}
	}

	/**
	 * 訊息接收者
	 */
	protected class ClientServiceReceiver extends GenericReceiverSupporter {

		private static final long serialVersionUID = -405160333954290319L;

		public ClientServiceReceiver() {
		}

		public void receive(Message message) {
			receiveQueue.offer(message);
		}
	}

	/**
	 * 加入訊息,發給server
	 * 
	 * @param message
	 * @return
	 */
	public boolean addMessage(Message message) {
		boolean result = false;
		// MESSAGE_CLIENT
		CategoryType categoryType = message.getCategoryType();
		if (CategoryType.MESSAGE_CLIENT == categoryType) {
			result = sendQueue.offer(message);
		} else {
			LOGGER.error("Undefined: " + categoryType + ", " + message);
		}
		return result;
	}

	protected class SendQueue<E> extends TriggerQueueSupporter<Message> {

		public SendQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			if (javaConnector != null && javaConnector.isValid()) {
				javaConnector.send(e);
			}
		}
	}

	protected class ReceiveQueue<E> extends TriggerQueueSupporter<Message> {

		public ReceiveQueue(ThreadService threadService) {
			super(threadService);
		}

		@Override
		protected void doExecute(Message e) throws Exception {
			service(e);
		}
	}

}
