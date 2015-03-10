package org.openyu.socklet.connector.service.supporter;

import org.openyu.commons.lang.ClassHelper;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
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
public abstract class ClientServiceSupporter extends BaseServiceSupporter
		implements ClientService {

	private static final long serialVersionUID = -1548288681963437153L;

	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(ClientServiceSupporter.class);
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

	private SendQueue<Message> sendQueue = new SendQueue<Message>();

	/**
	 * receive
	 * 
	 * tcp, server->client, MESSAGE_SERVER
	 * 
	 * messageType=FOUR_SYMBOL_PLAY_REQUEST
	 */

	private ReceiveQueue<Message> receiveQueue = new ReceiveQueue<Message>();

	/**
	 * 是否啟動
	 */
	protected boolean started;

	public ClientServiceSupporter() {
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
	public void start() {
		try {
			if (!started) {
				javaConnector = new JavaConnectorImpl(moduleTypeClass,
						messageTypeClass, protocolService);
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
				started = javaConnector.isStarted();
				// ----------------------------------------------

				threadService.submit(sendQueue);
				threadService.submit(receiveQueue);

				//
				// if (started)
				// {
				// log.info("[" + id + "] has been started");
				// }
				// else
				// {
				// log.error("[" + id + "] started fail");
				// }
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			started = false;
			LOGGER.error("[" + id + "] started fail");
		}
	}

	/**
	 * 是否已啟動
	 * 
	 * @return
	 */
	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public void shutdown() {
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

	protected class SendQueue<E> extends TriggerQueueSupporter<E> {
		public SendQueue() {
		}

		public void process(E e) {
			if (javaConnector != null && javaConnector.isValid()) {
				javaConnector.send((Message) e);
			}
		}
	}

	protected class ReceiveQueue<E> extends TriggerQueueSupporter<E> {
		public ReceiveQueue() {
		}

		public void process(E e) {
			service((Message) e);
		}
	}

}
