package org.openyu.socklet.context.service.event;

import java.util.EventListener;

import org.openyu.commons.lang.event.EventDispatchable;
import org.openyu.commons.lang.event.supporter.BaseEventSupporter;

public class ContextEvent extends BaseEventSupporter implements
		EventDispatchable {

	private static final long serialVersionUID = 2950050325032620218L;

	public static final int INITIALIZED = 0;

	public static final int DESTROYED = 1;

	/**
	 * 本文id
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
	public ContextEvent(Object source, int type, String id) {
		super(source, type);
		this.id = id;
	}

	/**
	 * 取得本文id
	 * 
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	public void dispatch(EventListener listener) {
		switch (getType()) {
		case INITIALIZED:
			((ContextListener) listener).contextInitialized(this);
			break;
		case DESTROYED:
			((ContextListener) listener).contextDestroyed(this);
			break;
		}
	}
}
