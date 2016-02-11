package org.openyu.socklet.connector.vo;

/**
 * AcceptorService.acceptorConnectors用的,來自於外部client的連線
 * 並無實際連線,只是為了應對client在server建構出來的acceptorClient
 * 
 * 類似自定的session,TODO 考慮將原有的session與acceptorConnector合併
 */
public interface AcceptorConnector extends GenericConnector
{

	/**
	 * socket 伺服器
	 * 
	 * localhost:3100
	 * 
	 * @return
	 */
	String getServer();

	void setServer(String server);

	// flashPolicy
	boolean isSendFlashPolicy();

	void setSendFlashPolicy(boolean sendFlashPolicy);

//	boolean isFlashPolicy();
//
//	void setFlashPolicy(boolean flashPolicy);

	boolean isReadFlashPolicy();

	void setReadFlashPolicy(boolean readFlashPolicy);

	// already
	boolean isReadAlready();

	void setReadAlready(boolean readAlready);

	// authKey
	byte[] getAuthKey();

	void setAuthKey(byte[] authKey);

	boolean isSendAuthKey();

	void setSendAuthKey(boolean sendAuthKey);

	// handshake
	boolean isSendHandshake();

	void setSendHandshake(boolean sendHandshake);

	boolean isHandshake();

	void setHandshake(boolean handshake);

	void setReadHandshake(boolean readHandshake);

	boolean isReadHandshake();

	/**
	 * server ip
	 * 
	 * @return
	 */
	String getServerIp();

	void setServerIp(String serverIp);

	/**
	 * server port
	 * 
	 * @return
	 */
	int getServerPort();

	void setServerPort(int serverPort);

	/**
	 * client ip
	 * 
	 * @return
	 */
	String getClientIp();

	void setClientIp(String clientIp);

	/**
	 * client port
	 * 
	 * @return
	 */
	int getClientPort();

	void setClientPort(int clientPort);
}
