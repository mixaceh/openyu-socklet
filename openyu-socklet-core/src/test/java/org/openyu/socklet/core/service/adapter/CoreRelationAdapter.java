package org.openyu.socklet.core.service.adapter;

import org.openyu.socklet.acceptor.service.event.RelationEvent;
import org.openyu.socklet.acceptor.service.event.adapter.RelationAdapter;
import org.openyu.socklet.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CoreRelationAdapter extends RelationAdapter {

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(CoreRelationAdapter.class);

	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;

	public CoreRelationAdapter() {
	}

	public void relationConnected(RelationEvent relationEvent) {
		String relationId = relationEvent.getId();
		LOGGER.info("[" + relationId + "] Connected");
	}

	public void relationDisconnected(RelationEvent relationEvent) {
		String relationId = relationEvent.getId();
		LOGGER.info("[" + relationId + "] Disconnected");
	}

	public void relationRefused(RelationEvent relationEvent) {
		String relationId = relationEvent.getId();
		LOGGER.info("[" + relationId + "] Refused");
	}
}
