package org.openyu.socklet.message.vo;

import org.openyu.commons.model.BaseModel;

public interface Packet<V> extends BaseModel {

	byte[] getByteArray();

	void setByteArray(byte[] byteArray);

	HeadType getHeadType();

	void setHeadType(HeadType headType);

	int getLength();

	void setLength(int length);

	V getBody();

	void setBody(V body);

}
