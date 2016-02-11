package org.openyu.socklet.session.service.event;

import java.util.EventListener;

import org.openyu.commons.lang.event.EventDispatchable;
import org.openyu.commons.lang.event.supporter.BaseEventSupporter;
import org.openyu.socklet.session.vo.Session;

public class SessionEvent extends BaseEventSupporter implements
		EventDispatchable {

	private static final long serialVersionUID = -2254137309444305445L;

	public static final int CREATED = 0;

	public static final int DESTROYED = 1;

	private Session session;

	/**
	 * 
	 * @param source
	 *            ContextService
	 * @param type
	 * @param session
	 */
	public SessionEvent(Object source, int type, Session session) {
		super(source, type);
		this.session = session;
	}

	/**
	 * 取得session
	 * 
	 * @return
	 */
	public Session getSession() {
		return this.session;
	}

	public void dispatch(EventListener listener) {
		switch (getType()) {
		case CREATED:
			((SessionListener) listener).sessionCreated(this);
			break;
		case DESTROYED:
			((SessionListener) listener).sessionDestroyed(this);
			break;
		}
	}
}
