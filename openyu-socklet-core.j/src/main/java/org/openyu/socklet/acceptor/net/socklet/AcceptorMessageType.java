package org.openyu.socklet.acceptor.net.socklet;

import org.openyu.socklet.message.vo.MessageType;

/**
 * 訊息
 * 
 * 第1位數 => 1=系統用, 2=子功能用
 * 
 * 第2,3位數模組 => 01-99
 * 
 * 第4,5位數訊息,=> 01-99, request=01-49, response=50-99
 * 
 */
public enum AcceptorMessageType implements MessageType
{
//	//---------------------------------------------------
//	// 握手請求
//	//---------------------------------------------------
//	/**
//	 * 客戶端握手
//	 * 
//	 * 0, byte[], 認證碼
//	 */
//	HANDSHAKE_CLIENT_REQUEST(10001),
//
//	/**
//	 * 關係端握手
//	 * 
//	 * 0, byte[], 認證碼
//	 */
//	HANDSHAKE_RELATION_REQUEST(10002),
//
//	//---------------------------------------------------
//	// 握手回應
//	//---------------------------------------------------
//	/**
//	 * 服務端握手
//	 * 
//	 * 0, byte[], 認證碼
//	 */
//	HANDSHAKE_SERVER_CLIENT_REQUEST(10051),

	//---------------------------------------------------
	// 同步請求
	//---------------------------------------------------
	/**
	 * 同步客戶端連線
	 * 
	 * 0, String, client sender
	 * 
	 * 1, String, client acceptor
	 * 
	 * 2, byte[], 認證碼
	 * 
	 * 3, boolean, 是否握手成功
	 */
	ACCEPTOR_SYNC_CLIENT_CONNECT_REQUEST(101001),

	/**
	 * 同步客戶端斷線
	 * 
	 * 0, String, client sender
	 * 
	 */
	ACCEPTOR_SYNC_CLIENT_DISCONNECT_REQUEST(101002), 
	
	//
	;

	private final int intValue;

	private AcceptorMessageType(int intValue)
	{
		this.intValue = intValue;
	}

	public int getValue()
	{
		return intValue;
	}
}
