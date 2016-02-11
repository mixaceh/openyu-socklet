package org.openyu.socklet.acceptor.service.event;

import java.util.EventListener;

import org.openyu.commons.lang.event.EventDispatchable;
import org.openyu.commons.lang.event.supporter.BaseEventSupporter;

public class RelationEvent extends BaseEventSupporter implements
		EventDispatchable {

	private static final long serialVersionUID = -2254137309444305445L;

	public static final int CONNECTED = 0;

	public static final int DISCONNECTED = 1;

	public static final int REFUSED = 2;

	/**
	 * 關連id
	 */
	private String id;

	/**
	 * 
	 * @param source
	 *            ContextService
	 * @param type
	 * 
	 * @param id
	 */
	public RelationEvent(Object source, int type, String id) {
		super(source, type);
		this.id = id;
	}

	/**
	 * 取得關連id
	 * 
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	public void dispatch(EventListener listener) {
		switch (getType()) {
		case CONNECTED:
			((RelationListener) listener).relationConnected(this);
			break;
		case DISCONNECTED:
			((RelationListener) listener).relationDisconnected(this);
			break;
		case REFUSED:
			((RelationListener) listener).relationRefused(this);
			break;
		}
	}
}
