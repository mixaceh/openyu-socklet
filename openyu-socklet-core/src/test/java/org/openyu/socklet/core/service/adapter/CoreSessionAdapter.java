package org.openyu.socklet.core.service.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.session.service.event.SessionEvent;
import org.openyu.socklet.session.service.event.adapter.SessionAdapter;
import org.openyu.socklet.session.vo.Session;

public class CoreSessionAdapter extends SessionAdapter {

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(CoreSessionAdapter.class);

	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;

	public CoreSessionAdapter() {
	}

	public void sessionCreated(SessionEvent sessionEvent) {
		Session session = sessionEvent.getSession();
		String sender = session.getId();
		LOGGER.info("[" + sender + "] Session created");
	}

	public void sessionDestroyed(SessionEvent sessionEvent) {
		Session session = sessionEvent.getSession();
		String sender = session.getId();
		LOGGER.info("[" + sender + "] Session destroyed");
	}

}
