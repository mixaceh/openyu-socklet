package org.openyu.socklet.connector.vo;

import org.openyu.commons.enumz.ByteEnum;

/**
 * socket client 連接器類型
 * 
 * client/server
 */
public enum ConnectorType implements ByteEnum
{
	/**
	 * flash, client發送給socklet server byte[0]=1
	 * 
	 * FLASH_SOCKLET
	 */
	FLASH_SOCKLET((byte) 1),

	/**
	 * java, client發送給socklet server byte[0]=2
	 */
	JAVA_SOCKLET((byte) 2),

	/**
	 * java relation, client發送給socklet server byte[0]=3
	 */
	RELATION_SOCKLET((byte) 3),

	//
	;

	private final byte byteValue;

	private ConnectorType(byte byteValue)
	{
		this.byteValue = byteValue;
	}

	public byte getValue()
	{
		return byteValue;
	}

}
