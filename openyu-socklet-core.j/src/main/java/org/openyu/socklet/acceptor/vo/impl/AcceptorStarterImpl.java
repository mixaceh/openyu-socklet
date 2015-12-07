package org.openyu.socklet.acceptor.vo.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import org.openyu.commons.model.supporter.BaseModelSupporter;
import org.openyu.socklet.acceptor.service.AcceptorService;
import org.openyu.socklet.acceptor.vo.AcceptorStarter;

@Deprecated
public class AcceptorStarterImpl extends BaseModelSupporter implements
		AcceptorStarter {

	private static final long serialVersionUID = -7726187889378711025L;

	private String id;

	private AcceptorService acceptorService;

	public AcceptorStarterImpl() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AcceptorService getAcceptorService() {
		return acceptorService;
	}

	public void setAcceptorService(AcceptorService acceptorService) {
		this.acceptorService = acceptorService;
	}

	public boolean equals(Object object) {
		if (!(object instanceof AcceptorStarterImpl)) {
			return false;
		}
		if (this == object) {
			return true;
		}
		AcceptorStarterImpl other = (AcceptorStarterImpl) object;
		return new EqualsBuilder().append(id, other.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	public Object clone() {
		AcceptorStarterImpl copy = null;
		copy = (AcceptorStarterImpl) super.clone();
		return copy;
	}
}
