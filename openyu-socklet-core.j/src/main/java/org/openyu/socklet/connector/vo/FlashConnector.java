package org.openyu.socklet.connector.vo;

/**
 * 模擬flash client
 */
public interface FlashConnector extends SocketConnector
{

	//flash security domain
	boolean isSendFlashPolicy();

	boolean isFlashPolicy();

	boolean isReadFlashPolicy();
}