package org.openyu.socklet.connector.vo;

/**
 * socket連線客戶端
 */
public interface SocketConnector extends GenericConnector
{
	/**
	 * 想要連線的server ip
	 * 
	 * @return
	 */
	String getIp();

	void setIp(String ip);

	/**
	 * 想要連線的server port
	 * 
	 * @return
	 */
	int getPort();

	void setPort(int port);

	// authKey
	byte[] getAuthKey();

	void setAuthKey(byte[] authKey);

	boolean isReadAuthKey();

	void setReadAuthKey(boolean readAuthKey);

	// handshake
	boolean isSendHandshake();

	void setSendHandshake(boolean sendHandshake);

	boolean isHandshake();

	void setHandshake(boolean handshake);

	void setReadHandshake(boolean readHandshake);

	boolean isReadHandshake();

}
