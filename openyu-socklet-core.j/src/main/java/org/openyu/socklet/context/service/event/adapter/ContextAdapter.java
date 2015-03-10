package org.openyu.socklet.context.service.event.adapter;

import org.openyu.socklet.context.service.event.ContextEvent;
import org.openyu.socklet.context.service.event.supporter.ContextListenerSupporter;

/**
 * 本文轉接器
 */
public class ContextAdapter extends ContextListenerSupporter {

	public ContextAdapter() {
	}

	/**
	 * 本文已初始化
	 * 
	 * @param contextEvent
	 */
	public void contextInitialized(ContextEvent contextEvent) {
	}

	/**
	 * 本文已銷毀化
	 * 
	 * @param contextEvent
	 */
	public void contextDestroyed(ContextEvent contextEvent) {
	}

}
