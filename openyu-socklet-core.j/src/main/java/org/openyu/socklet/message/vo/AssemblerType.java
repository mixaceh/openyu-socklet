package org.openyu.socklet.message.vo;

import org.openyu.commons.enumz.IntEnum;

/**
 * 串流類別
 */
public enum AssemblerType implements IntEnum
{
	/**
	 * 來自於client
	 */
	CLIENT(1),

	/**
	 * 伺服器間
	 */
	SERVER(2),

	/**
	 * 控制器
	 */
	CONTROL(3),

	/**
	 * 握手
	 */
	HANDSHAKE(4),

	/**
	 * 文字
	 */
	PLAIN(5),

	/**
	 * 文字utf8
	 */
	PLAIN_UTF8(6),

	/**
	 * 文字url
	 */
	PLAIN_URL(7),

	/**
	 * 二進位
	 */
	BINARY(8),

	/**
	 * 二進位ascii
	 */
	BINARY_ASCII(9),

	;
	private final int intValue;

	private AssemblerType(int intValue)
	{
		this.intValue = intValue;
	}

	public int getValue()
	{
		return intValue;
	}
}
