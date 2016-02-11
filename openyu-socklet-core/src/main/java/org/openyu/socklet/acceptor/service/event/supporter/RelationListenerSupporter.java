package org.openyu.socklet.acceptor.service.event.supporter;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.openyu.commons.lang.event.supporter.BaseListenerSupporter;
import org.openyu.socklet.acceptor.service.event.RelationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 關連監聽支援器
 */
public abstract class RelationListenerSupporter extends BaseListenerSupporter
		implements RelationListener {

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(RelationListenerSupporter.class);

	private Set<String> acceptors = new LinkedHashSet<String>();

	public RelationListenerSupporter() {
	}

	public Set<String> getAcceptors() {
		return acceptors;
	}

	public void setAcceptors(Set<String> acceptors) {
		this.acceptors = acceptors;
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("acceptors", acceptors);
		return builder.toString();
	}
}
