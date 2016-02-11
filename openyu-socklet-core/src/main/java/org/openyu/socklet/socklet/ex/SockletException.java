package org.openyu.socklet.socklet.ex;

import org.openyu.commons.lang.ex.BaseException;

public class SockletException extends BaseException
{

	private static final long serialVersionUID = 7248390143933907369L;

	private Throwable rootCause;

	public SockletException()
	{
		super();
	}

	public SockletException(String message)
	{
		super(message);
	}

	public SockletException(String message, Throwable rootCause)
	{
		super(message);
		this.rootCause = rootCause;
	}

	public SockletException(Throwable rootCause)
	{
		super(rootCause.getLocalizedMessage());
		this.rootCause = rootCause;
	}

	public Throwable getRootCause()
	{
		return rootCause;
	}
}
