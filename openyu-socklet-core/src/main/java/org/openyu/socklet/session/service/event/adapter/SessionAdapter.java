package org.openyu.socklet.session.service.event.adapter;

import org.openyu.socklet.session.service.event.SessionEvent;
import org.openyu.socklet.session.service.event.supporter.SessionListenerSupporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 會話轉接器
 */
public class SessionAdapter extends SessionListenerSupporter {

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(SessionAdapter.class);

	public SessionAdapter() {
	}

	/**
	 * 會話已建立
	 * 
	 * @param sessionEvent
	 */
	public void sessionCreated(SessionEvent sessionEvent) {
	}

	/**
	 * 會話已銷毀
	 * 
	 * @param sessionEvent
	 */
	public void sessionDestroyed(SessionEvent sessionEvent) {
	}

}
