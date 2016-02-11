package org.openyu.socklet.session.vo.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.openyu.commons.model.supporter.BaseModelSupporter;
import org.openyu.socklet.session.vo.Session;

public class SessionImpl extends BaseModelSupporter implements Session
{

	private static final long serialVersionUID = 4821037679527077439L;

	private String id;

	private Map<String, Object> attributes = new LinkedHashMap<String, Object>(10);

	private boolean invalidate = false;

	public SessionImpl(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setAttribute(String name, Object value)
	{
		if (value != null)
		{
			this.attributes.put(name, value);
		}
		else
		{
			this.attributes.remove(name);
		}
	}

	public Object getAttribute(String name)
	{
		return attributes.get(name);
	}

	public void removeAttribute(String name)
	{
		attributes.remove(name);
	}

	public void invalidate()
	{
		attributes.clear();
		this.invalidate = true;
	}

	public void setAttributes(Map<String, Object> attributes)
	{
		this.attributes = attributes;
	}

	public Map<String, Object> getAttributes()
	{
		return attributes;
	}

	public void clearAttributes()
	{
		attributes.clear();
	}

	public boolean equals(Object object)
	{
		if (!(object instanceof SessionImpl))
		{
			return false;
		}
		SessionImpl other = (SessionImpl) object;
		return new EqualsBuilder().append(id, other.getId()).isEquals();
	}

	public int hashCode()
	{
		return new HashCodeBuilder().append(id).toHashCode();
	}

	public String toString()
	{
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", id);
		builder.append("attributes", attributes);
		return builder.toString();
	}

}
