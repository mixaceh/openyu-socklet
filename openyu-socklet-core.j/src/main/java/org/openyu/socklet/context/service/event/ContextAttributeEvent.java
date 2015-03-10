package org.openyu.socklet.context.service.event;

import java.util.EventListener;

import org.openyu.commons.lang.event.EventDispatchable;
import org.openyu.commons.lang.event.supporter.BaseEventSupporter;

public class ContextAttributeEvent extends BaseEventSupporter implements
		EventDispatchable {

	private static final long serialVersionUID = -3372946637436591911L;

	public static final int ATTRIBUTE_ADDED = 1;

	public static final int ATTRIBUTE_REMOVED = 2;

	public static final int ATTRIBUTE_REPLACED = 3;

	private String key;

	private Object value;

	/**
	 * 
	 * @param source
	 *            ContextService
	 * @param type
	 * @param key
	 * @param value
	 */
	public ContextAttributeEvent(Object source, int type, String key,
			Object value) {
		super(source, type);
		this.key = key;
		this.value = value;
	}

	/**
	 * 取得key
	 * 
	 * @return
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * 取得value
	 * 
	 * @return
	 */
	public Object getValue() {
		return this.value;
	}

	public void dispatch(EventListener listener) {
		switch (getType()) {
		case ATTRIBUTE_ADDED:
			((ContextAttributeListener) listener).attributeAdded(this);
			break;
		case ATTRIBUTE_REMOVED:
			((ContextAttributeListener) listener).attributeRemoved(this);
			break;
		case ATTRIBUTE_REPLACED:
			((ContextAttributeListener) listener).attributeReplaced(this);
			break;
		}
	}
}
