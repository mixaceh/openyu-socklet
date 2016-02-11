package org.openyu.socklet.session.vo;

import java.util.Map;
import org.openyu.commons.model.BaseModel;

public interface Session extends BaseModel
{

	String KEY = Session.class.getName();

	String getId();

	void setId(String id);

	void setAttribute(String name, Object value);

	Object getAttribute(String name);

	void removeAttribute(String name);

	void invalidate();

	void setAttributes(Map<String, Object> attributes);

	Map<String, Object> getAttributes();

	void clearAttributes();
}
