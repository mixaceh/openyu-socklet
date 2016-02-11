package org.openyu.socklet.socklet.vo.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.openyu.commons.model.supporter.BaseModelSupporter;
import org.openyu.socklet.socklet.service.SockletService;
import org.openyu.socklet.socklet.vo.SockletConfig;

public class SockletConfigImpl extends BaseModelSupporter implements SockletConfig
{
	private static final long serialVersionUID = -5818941939245948427L;

	private String id;

	private Map<String, String> initParameters = new LinkedHashMap<String, String>(10);

	private SockletService sockletService;

	public SockletConfigImpl(SockletService sockletService)
	{
		this.sockletService = sockletService;
		//
		setId(sockletService.getId());
		setInitParameters(sockletService.getInitParameters());
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getInitParameter(String name)
	{
		return (String) initParameters.get(name);
	}

	public Enumeration<String> getInitParameterNames()
	{
		return Collections.enumeration(initParameters.keySet());
	}

	public Map<String, String> getInitParameters()
	{
		return initParameters;
	}

	public void setInitParameters(Map<String, String> initParameters)
	{
		this.initParameters = initParameters;
	}

	public SockletService getSockletService()
	{
		return sockletService;
	}

	public void setSockletService(SockletService sockletService)
	{
		this.sockletService = sockletService;
	}

	public String toString()
	{
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", id);
		builder.append("initParameters", initParameters);
		return builder.toString();
	}

}
