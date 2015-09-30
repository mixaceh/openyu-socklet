package org.openyu.socklet.acceptor.ex;

import org.openyu.commons.lang.ex.BaseRuntimeException;

public class AcceptorException extends BaseRuntimeException {

	private static final long serialVersionUID = 8710195996416878107L;

	public AcceptorException(Throwable cause) {
		super(cause);
	}

	public AcceptorException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcceptorException(String message) {
		super(message);
	}
}
