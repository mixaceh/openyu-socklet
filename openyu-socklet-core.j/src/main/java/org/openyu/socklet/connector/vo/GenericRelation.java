package org.openyu.socklet.connector.vo;

import java.util.Map;

import org.openyu.commons.bean.BaseBean;
import org.openyu.socklet.message.vo.Message;

/**
 * 泛化關聯
 */
public interface GenericRelation extends BaseBean
{

	/**
	 * id
	 * 
	 * slave1 連 master
	 * 
	 * id=master
	 * 
	 * @return
	 */
	String getId();

	void setId(String id);

	/**
	 * 泛化客戶端
	 * 
	 * sender=slave2:0:localhost:3000, GenericClient
	 * 
	 * @return
	 */
	Map<String, GenericConnector> getClients();

	void setClients(Map<String, GenericConnector> clients);

	/**
	 * 取得訊息讀取者
	 * 
	 * @return
	 */
	GenericReceiver getReceiver();

	/**
	 * 設定訊息讀取者
	 * 
	 * @param receiver
	 */
	void setReceiver(GenericReceiver receiver);

	/**
	 * 發送訊息
	 * 
	 * @param message
	 * @return
	 */
	int send(Message message);

	/**
	 * 取得下一個隨機連線
	 * 
	 * @return
	 */
	GenericConnector getNextClient();

	/**
	 * 是否已連線
	 * 
	 * @return
	 */
	boolean isConnected();

	void setConnected(boolean connected);

}
