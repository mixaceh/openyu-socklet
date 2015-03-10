package org.openyu.socklet.message.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.openyu.commons.lang.StringHelper;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.commons.thread.TriggerQueue;
import org.openyu.commons.util.CollectionHelper;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.MessageType;
import org.openyu.socklet.message.vo.ModuleType;
import org.openyu.socklet.message.vo.PriorityType;
import org.openyu.socklet.message.vo.impl.MessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 訊息服務,處理send message
 */
public class MessageServiceImpl extends BaseServiceSupporter implements
		MessageService

{
	private static final long serialVersionUID = 7704523919913561474L;

	/** The Constant LOGGER. */
	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(MessageServiceImpl.class);

	/**
	 * tcp, server->client, MESSAGE_SERVER,MESSAGE_RELATION
	 * 
	 * srcModule=FOUR_SYMBOL, destModule= CLIENT
	 * 
	 * @see org.openyu.socklet.acceptor.service.impl.acceptorServiceImpl.sendClientQueue
	 */
	private TriggerQueue<Message> clientQueue;

	/**
	 * tcp, server->server, MESSAGE_SERVER
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=CHAT, destModule= FOUR_SYMBOL
	 * 
	 * srcModule=CLIENT, destModule= CORE
	 * 
	 * @see org.openyu.socklet.acceptor.service.impl.acceptorServiceImpl.sendServerQueue
	 */
	private TriggerQueue<Message> serverQueue;

	/**
	 * 
	 * udp, server->relation, MESSAGE_SYNC
	 * 
	 * destModule!=CLIENT
	 * 
	 * srcModule=ROLE, destModule= ROLE
	 * 
	 * @see org.openyu.socklet.acceptor.service.impl.acceptorServiceImpl.sendSyncQueue
	 */
	private TriggerQueue<Message> syncQueue;

	/**
	 * 判斷 destModue=CLIENT用
	 */
	private static final String CLIENT = "CLIENT";

	public MessageServiceImpl() {
	}

	public void setClientQueue(TriggerQueue<Message> clientQueue) {
		this.clientQueue = clientQueue;
	}

	public void setServerQueue(TriggerQueue<Message> serverQueue) {
		this.serverQueue = serverQueue;
	}

	public void setSyncQueue(TriggerQueue<Message> syncQueue) {
		this.syncQueue = syncQueue;
	}

	/**
	 * * 建構訊息, MESSAGE_CLIENT, 模擬client side用
	 * 
	 * @param sender
	 * @param messageType
	 * @return
	 */
	public Message createClient(String sender, MessageType messageType) {
		Message result = new MessageImpl(CategoryType.MESSAGE_CLIENT,
				PriorityType.MEDIUM, sender, messageType);
		return result;
	}

	/**
	 * 建構訊息, MESSAGE_SERVER, server side用
	 * 
	 * @param srcModule
	 * @param destModule
	 * @param messageType
	 * @return
	 */
	public Message createMessage(ModuleType srcModule, ModuleType destModule,
			MessageType messageType) {
		return createMessage(srcModule, destModule, messageType, (String) null);
	}

	/**
	 * 建構訊息, MESSAGE_SERVER, server side用
	 * 
	 * @param srcModule
	 * @param destModule
	 * @param messageType
	 * @param receiver
	 * @return
	 */
	public Message createMessage(ModuleType srcModule, ModuleType destModule,
			MessageType messageType, String receiver) {
		List<String> receivers = new LinkedList<String>();
		if (StringHelper.notBlank(receiver)) {
			receivers.add(receiver);
		}
		return createMessage(srcModule, destModule, messageType, receivers);
	}

	/**
	 * 建構訊息, MESSAGE_SERVER, server side用
	 * 
	 * @param srcModule
	 * @param destModule
	 * @param messageType
	 * @param receivers
	 * @return
	 */
	public Message createMessage(ModuleType srcModule, ModuleType destModule,
			MessageType messageType, List<String> receivers) {
		List<String> safeReceivers = CollectionHelper.safeGet(receivers);
		Message result = new MessageImpl(CategoryType.MESSAGE_SERVER,
				PriorityType.MEDIUM, srcModule, destModule, messageType,
				safeReceivers);
		return result;
	}

	/**
	 * 建構訊息, MESSAGE_SNYC, server side用
	 * 
	 * @param srcModule
	 * @param destModule
	 * @param messageType
	 * @return
	 */
	public Message createSync(ModuleType srcModule, ModuleType destModule,
			MessageType messageType) {
		Message result = new MessageImpl(CategoryType.MESSAGE_SYNC,
				PriorityType.MEDIUM, srcModule, destModule, messageType);
		return result;
	}

	/**
	 * 加入訊息
	 * 
	 * @param message
	 * @return
	 */
	public boolean addMessage(Message message) {
		boolean result = false;

		/**
		 * tcp, server->server, MESSAGE_SERVER
		 * 
		 * destModule!=CLIENT
		 * 
		 * srcModule=CHAT, destModule= FOUR_SYMBOL
		 * 
		 * srcModule=CLIENT, destModule= CORE
		 */
		if (CategoryType.MESSAGE_SERVER.equals(message.getCategoryType())
				&& !CLIENT.equals(message.getDestModule().name())) {
			if (serverQueue == null) {
				// TODO debug用
				System.out.println(message);
			} else {
				result = serverQueue.offer(message);
			}
		}

		/**
		 * 
		 * udp, server->relation, MESSAGE_SYNC
		 * 
		 * destModule!=CLIENT
		 * 
		 * srcModule=ROLE, destModule= ROLE
		 */
		else if (CategoryType.MESSAGE_SYNC.equals(message.getCategoryType())
				&& !CLIENT.equals(message.getDestModule().name())) {
			if (syncQueue == null) {
				// TODO debug用
				System.out.println(message);
			} else {
				result = syncQueue.offer(message);
			}
		}

		/**
		 * tcp, server->client, MESSAGE_SERVER,MESSAGE_RELATION
		 * 
		 * srcModule=FOUR_SYMBOL, destModule= CLIENT
		 */
		else if (CLIENT.equals(message.getDestModule().name())) {
			if (clientQueue == null) {
				// TODO debug用
				System.out.println(message);
			} else {
				result = clientQueue.offer(message);
			}
		} else {
			LOGGER.error("Undefined: " + message.getCategoryType() + ", "
					+ message);
		}
		return result;
	}

	/**
	 * 加入訊息
	 * 
	 * @param messages
	 * @return
	 */
	public boolean addMessages(List<Message> messages) {
		boolean result = false;
		for (Message message : messages) {
			if (message == null) {
				continue;
			}
			result |= addMessage(message);
		}
		return result;
	}

	// /**
	// * 移除訊息
	// *
	// * @param message
	// * @return
	// */
	// public boolean removeMessage(CategoryType categoryType, Message message)
	// {
	// boolean result = false;
	// Queue<Message> messages = getMessages(categoryType);
	// if (messages != null)
	// {
	// List<Message> removeMessages = new LinkedList<Message>();
	// for (Message entry : messages)
	// {
	// if (entry.getMessageType().equals(message.getMessageType()))
	// {
	// removeMessages.add(entry);
	// }
	// }
	// //移除訊息編號
	// for (Message removeEntry : removeMessages)
	// {
	// result |= messages.remove(removeEntry);
	// }
	// }
	// return result;
	// }

	// /**
	// * 依類別取得訊息
	// *
	// * @param categoryType
	// * @return
	// */
	// public Queue<Message> getMessages(CategoryType categoryType)
	// {
	// Queue<Message> result = new ConcurrentLinkedQueue<Message>();
	// switch (categoryType)
	// {
	// case MESSAGE_SERVER:
	// {
	// result = sendServers;
	// break;
	// }
	// case MESSAGE_SYNC:
	// {
	// result = sendSyncs;
	// break;
	// }
	// case MESSAGE_CLIENT:
	// {
	// result = sendClients;
	// break;
	// }
	// default:
	// {
	// log.warn("undefined: " + categoryType);
	// break;
	// }
	// }
	// return result;
	// }
}
