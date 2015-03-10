package org.openyu.socklet.acceptor.net.socklet;

import org.openyu.socklet.message.vo.ModuleType;

/**
 * 1=系統用
 * 
 * 2=子功能用
 */
public enum AcceptorModuleType implements ModuleType
{

	/**
	 * acceptor
	 */
	ACCEPTOR(101000),

	;

	private final int intValue;

	private AcceptorModuleType(int intValue)
	{
		this.intValue = intValue;
	}

	public int getValue()
	{
		return intValue;
	}
}
