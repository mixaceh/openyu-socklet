package org.openyu.socklet.message.vo.impl;

import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.openyu.commons.lang.ObjectHelper;
import org.openyu.commons.model.supporter.BaseModelSupporter;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.ClusterMessage;

public class ClusterMessageImpl extends BaseModelSupporter implements
		ClusterMessage {

	private static final long serialVersionUID = -2892711543545884124L;

	/**
	 * id, 唯一碼
	 * 
	 * use UUID.randomUUID()
	 * 
	 * @return
	 */
	private String id;

	/**
	 * 時間戳
	 */
	private long timeStamp;

	private CategoryType categoryType;

	private byte[] body;

	public ClusterMessageImpl(CategoryType categoryType, byte[] body) {
		this.categoryType = categoryType;
		this.body = body;
		//
		this.id = UUID.randomUUID().toString();
		this.timeStamp = System.currentTimeMillis();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public CategoryType getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(CategoryType categoryType) {
		this.categoryType = categoryType;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("categoryType", categoryType);
		builder.append("body", ObjectHelper.toString(body));
		return builder.toString();
	}

	public Object clone() {
		ClusterMessageImpl copy = null;
		copy = (ClusterMessageImpl) super.clone();
		copy.body = clone(body);
		return copy;
	}
}
