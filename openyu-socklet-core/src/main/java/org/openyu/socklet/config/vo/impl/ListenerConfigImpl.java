package org.openyu.socklet.config.vo.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.openyu.commons.lang.event.BaseListener;
import org.openyu.commons.model.supporter.BaseModelSupporter;
import org.openyu.socklet.config.vo.ListenerConfig;

public class ListenerConfigImpl extends BaseModelSupporter implements ListenerConfig
{

	private static final long serialVersionUID = 8016300894522946576L;

	private BaseListener baseListener;

	private Map<String, String> initParameters = new LinkedHashMap<String, String>(10);

	public ListenerConfigImpl(BaseListener baseListener)
	{
		this.baseListener = baseListener;
	}

	public String getInitParameter(String name)
	{
		return (String) initParameters.get(name);
	}

	public Enumeration<String> getInitParameterNames()
	{
		return Collections.enumeration(initParameters.keySet());
	}

	public void setInitParameters(Map<String, String> initParameters)
	{
		this.initParameters = initParameters;
	}

	public Map<String, String> getInitParameters()
	{
		return initParameters;
	}

	public String toString()
	{
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("initParameters", initParameters);
		return builder.toString();
	}

}
