package org.openyu.socklet.message.service;

import java.util.List;
import org.openyu.commons.service.BaseService;
import org.openyu.commons.thread.TriggerQueue;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.MessageType;
import org.openyu.socklet.message.vo.ModuleType;

/**
 * 訊息服務,處理send message
 */
public interface MessageService extends BaseService {

	/**
	 * 建構訊息, MESSAGE_CLIENT, 模擬client side用
	 * 
	 * @param sender
	 * @param messageType
	 * @return
	 */
	Message createClient(String sender, MessageType messageType);

	/**
	 * 建構訊息, MESSAGE_SERVER, server side用
	 * 
	 * @param srcModule
	 * @param destModule
	 * @param messageType
	 * @return
	 */
	Message createMessage(ModuleType srcModule, ModuleType destModule,
			MessageType messageType);

	/**
	 * 建構訊息, MESSAGE_SERVER, server side用
	 * 
	 * @param srcModule
	 * @param destModule
	 * @param messageType
	 * @param receiver
	 * @return
	 */
	Message createMessage(ModuleType srcModule, ModuleType destModule,
			MessageType messageType, String receiver);

	/**
	 * 建構訊息, MESSAGE_SERVER, server side用
	 * 
	 * @param srcModule
	 * @param destModule
	 * @param messageType
	 * @param receivers
	 * @return
	 */
	Message createMessage(ModuleType srcModule, ModuleType destModule,
			MessageType messageType, List<String> receivers);

	/**
	 * 建構訊息, MESSAGE_SNYC, server side用
	 * 
	 * @param srcModule
	 * @param destModule
	 * @param messageType
	 * @return
	 */
	Message createSync(ModuleType srcModule, ModuleType destModule,
			MessageType messageType);

	/**
	 * 加入訊息
	 * 
	 * @param message
	 * @return
	 */
	boolean addMessage(Message message);

	/**
	 * 加入訊息
	 * 
	 * @param messages
	 * @return
	 */
	boolean addMessages(List<Message> messages);

	// /**
	// * 移除訊息
	// *
	// * @param message
	// * @return
	// */
	// boolean removeMessage(CategoryType categoryType, Message message);

	// /**
	// * 依類別取得訊息
	// *
	// * @param categoryType
	// * @return
	// */
	// Queue<Message> getMessages(CategoryType categoryType);

	/**
	 * 設定發給client的訊息佇列
	 * 
	 * @param clientQueue
	 */
	void setClientQueue(TriggerQueue<Message> clientQueue);

	/**
	 * 設定發給server的訊息佇列
	 * 
	 * @param serverQueue
	 */
	void setServerQueue(TriggerQueue<Message> serverQueue);

	/**
	 * 設定發給sync同步的訊息佇列
	 * 
	 * @param syncQueue
	 */

	void setSyncQueue(TriggerQueue<Message> syncQueue);

}
