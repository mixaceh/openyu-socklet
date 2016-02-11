package org.openyu.socklet.acceptor.service.event.adapter;

import org.openyu.socklet.acceptor.service.event.RelationEvent;
import org.openyu.socklet.acceptor.service.event.supporter.RelationListenerSupporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 關連轉接器
 */
public class RelationAdapter extends RelationListenerSupporter {

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(RelationAdapter.class);

	public RelationAdapter() {
	}

	/**
	 * 關聯主動/被動連線時用
	 * 
	 * @param relationEvent
	 */
	public void relationConnected(RelationEvent relationEvent) {
	}

	/**
	 * 關聯主動/被動斷線時用
	 * 
	 * @param relationEvent
	 */
	public void relationDisconnected(RelationEvent relationEvent) {
	}

	/**
	 * 關聯只有主動連線失敗時用
	 * 
	 * @param relationEvent
	 */
	public void relationRefused(RelationEvent relationEvent) {
	}

}
