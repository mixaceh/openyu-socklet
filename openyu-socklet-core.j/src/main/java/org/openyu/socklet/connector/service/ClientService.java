package org.openyu.socklet.connector.service;

import org.openyu.commons.service.BaseService;
import org.openyu.socklet.message.vo.Message;

/**
 * 客戶端服務
 */
public interface ClientService extends BaseService {

	/**
	 * id,sender
	 * 
	 * @return
	 */
	String getId();

	void setId(String id);

	/**
	 * 加入訊息,發給server
	 * 
	 * @param message
	 * @return
	 */
	boolean addMessage(Message message);

	/**
	 * 服務,收到來自於server的訊息
	 * 
	 * @param message
	 */
	void service(Message message);

	/**
	 * 啟動
	 */
	void start();

	/**
	 * 是否已啟動
	 * 
	 * @return
	 */
	boolean isStarted();

	void setStarted(boolean started);

	/**
	 * 關閉
	 */
	void shutdown();

	/**
	 * ip
	 * 
	 * @return
	 */
	String getIp();

	void setIp(String ip);

	/**
	 * port
	 * 
	 * @return
	 */
	int getPort();

	public void setPort(int port);

	/**
	 * 模組類別名稱
	 * 
	 * @return
	 */
	String getModuleTypeName();

	void setModuleTypeName(String moduleTypeName);

	/**
	 * 訊息類別名稱
	 * 
	 * @return
	 */
	String getMessageTypeName();

	void setMessageTypeName(String messageTypeName);

}
