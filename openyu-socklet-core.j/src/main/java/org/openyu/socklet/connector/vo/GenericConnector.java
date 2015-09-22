package org.openyu.socklet.connector.vo;

import java.nio.channels.SocketChannel;

import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.model.BaseModel;
import org.openyu.socklet.message.vo.Message;

/**
 * 泛化客戶端
 * 
 * GenericConnector
 * 
 * +-AcceptorConnector (client->slave1)
 * 
 * -------------------------------------------
 * 
 * +-SocketConnector socket連線客戶端
 * 
 * +-FlashConnector (client->slave1),模擬flash的policy連線用客戶端
 * 
 * +-JavaConnector (client->slave1),java連線用客戶端
 * 
 * +-RelationConnector (slave2->slave1),server間連線用的客戶端
 * 
 */
public interface GenericConnector extends BaseModel {

	// flash -> server, length=22
	String FLASH_INPUT_POLICY = "<policy-file-request/>";

	byte[] FLASH_INPUT_POLICY_BYTES = ByteHelper
			.toByteArray(FLASH_INPUT_POLICY);

	// server -> flash, length=87
	String FLASH_OUTPUT_POLICY = "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>";

	byte[] FLASH_OUTPUT_POLICY_BYTES = ByteHelper
			.toByteArray(FLASH_OUTPUT_POLICY);

	// flash as1,as2,as3 注意結尾加 "\0"
	String FLASH_EOF = "\0";// length=1

	byte[] FLASH_EOF_BYTES = ByteHelper.toByteArray(FLASH_EOF);

	/**
	 * id
	 * 
	 * sun.nio.ch.SelectionKeyImpl@19e09a4
	 * 
	 * @return
	 */
	String getId();

	void setId(String id);

	/**
	 * 是否啟動
	 * 
	 * @return
	 */
	boolean isStarted();

	void setStarted(boolean started);

	/**
	 * 開啟
	 * 
	 * open
	 */
	void start();

	/**
	 * 關閉
	 * 
	 * close
	 */
	void shutdown();

	/**
	 * 接收,byte[]
	 * 
	 * @return
	 */
	byte[] receive();

	/**
	 * 發送,byte[]
	 * 
	 * @param values
	 * @return
	 */
	int send(byte[]... values);

	/**
	 * 發送,message
	 * 
	 * @param message
	 * @return
	 */
	int send(Message message);

	/**
	 * 取得讀取者,message
	 * 
	 * @return
	 */
	GenericReceiver getReceiver();

	/**
	 * 設定讀取者
	 * 
	 * @param receiver
	 */
	void setReceiver(GenericReceiver receiver);

	/**
	 * socket
	 * 
	 * @return
	 */
	SocketChannel getSocketChannel();

	void setSocketChannel(SocketChannel socketChannel);

	/**
	 * 發送者
	 * 
	 * TEST_ROLE_1
	 * 
	 * slave2:0:localhost:3000
	 * 
	 * @return
	 */
	String getSender();

	void setSender(String sender);

	/**
	 * 接收器,表示連線acceptor的id
	 * 
	 * acceptor=master
	 * 
	 * @return
	 */
	String getAcceptor();

	void setAcceptor(String acceptor);

	/**
	 * 是否合法連線,started=true
	 * 
	 * @return
	 */
	boolean isValid();

	/**
	 * socket client 連接器類型
	 * 
	 * @return
	 */
	ConnectorType getConnectorType();

	void setConnectorType(ConnectorType connectorType);

	@SuppressWarnings("rawtypes")
	Class getModuleTypeClass();

	@SuppressWarnings("rawtypes")
	void setModuleTypeClass(Class moduleTypeClass);

	@SuppressWarnings("rawtypes")
	Class getMessageTypeClass();

	@SuppressWarnings("rawtypes")
	void setMessageTypeClass(Class messageTypeClass);

	/**
	 * 重試次數, 0=無限
	 *
	 * @return
	 */
	int getRetryNumber();

	void setRetryNumber(int retryNumber);

	/**
	 * 重試暫停毫秒
	 *
	 * @return
	 */
	long getRetryPauseMills();

	void setRetryPauseMills(long retryPauseMills);

	/**
	 * 接收緩衝區大小
	 * 
	 * @return
	 */
	int getReceiveBufferSize();

	void setReceiveBufferSize(int receiveBufferSize);

	/**
	 * 發送緩衝區大小
	 * 
	 * @return
	 */
	int getSendBufferSize();

	void setSendBufferSize(int sendBufferSize);

	/**
	 * 已重試次數
	 * 
	 * @return
	 */
	int getTries();

	void setTries(int tries);
	
	int addTries();


}
