package org.openyu.socklet.message.vo.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openyu.commons.lang.CloneHelper;
import org.openyu.commons.lang.ObjectHelper;
import org.openyu.commons.model.supporter.BaseModelSupporter;
import org.openyu.socklet.message.vo.HeadType;
import org.openyu.socklet.message.vo.Packet;

public class PacketImpl<V> extends BaseModelSupporter implements Packet<V> {

	private static final long serialVersionUID = 4368356947791296711L;

	private long timeStamp;

	private byte[] byteArray;

	private HeadType headType;

	private int length;

	private V body;

	public PacketImpl() {
		this(null, null, 0, null);
	}

	public PacketImpl(byte[] byteArray, HeadType headType, int length, V body) {
		this.timeStamp = System.nanoTime();
		this.byteArray = byteArray;
		this.headType = headType;
		this.length = length;
		this.body = body;
	}

	public byte[] getByteArray() {
		return byteArray;
	}

	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	public HeadType getHeadType() {
		return headType;
	}

	public void setHeadType(HeadType headType) {
		this.headType = headType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public V getBody() {
		return body;
	}

	public void setBody(V body) {
		this.body = body;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object object) {
		if (!(object instanceof PacketImpl)) {
			return false;
		}
		if (this == object) {
			return true;
		}
		PacketImpl<V> other = (PacketImpl<V>) object;
		return new EqualsBuilder().append(timeStamp, other.timeStamp)
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(timeStamp).toHashCode();
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this,
				ToStringStyle.SIMPLE_STYLE);
		builder.append("headType", headType);
		builder.append("length", length);
		builder.append("byteArray", "{ " + ObjectHelper.toString(byteArray)
				+ " }");
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		PacketImpl<V> copy = null;
		copy = (PacketImpl<V>) super.clone();
		copy.byteArray = CloneHelper.clone(byteArray);
		return copy;
	}
}
