package org.openyu.socklet.context.service;

import java.util.Enumeration;
import java.util.Map;

import org.openyu.commons.service.BaseService;
import org.openyu.socklet.message.vo.ModuleType;
import org.openyu.socklet.session.vo.Session;
import org.openyu.socklet.socklet.service.SockletService;

/**
 * 本文服務
 */
public interface ContextService extends BaseService
{

	String KEY = ContextService.class.getName();

	/**
	 * master,slave1...n
	 *
	 * @return
	 */
	String getId();

	void setId(String id);

	void setAttribute(String name, Object object);

	Object getAttribute(String name);

	void removeAttribute(String name);

	String getInitParameter(String name);

	Enumeration<String> getInitParameterNames();

	void log(String msg);

	void log(String message, Throwable throwable);

	//
	Map<String, String> getInitParameters();

	void setInitParameters(Map<String, String> initParameters);

	Map<String, Object> getAttributes();

	void setAttributes(Map<String, Object> attributes);

	//
	void clearAttributes();

//	void setSockletConfigs(Map<ModuleType, SockletConfig> sockletConfigs);
//
//	Map<ModuleType, SockletConfig> getSockletConfigs();

	Map<ModuleType, SockletService> getSockletServices();

	void setSockletServices(Map<ModuleType, SockletService> sockletServices);

	Map<ModuleType, SockletService> getRelationServices();

	void setRelationServices(Map<ModuleType, SockletService> relationServices);

	Map<String, Session> getSessions();

	void setSessions(Map<String, Session> sessions);
}

/*
 * org.apache.catalina.core.ApplicationContextFacade@15b8520
 * org.apache.catalina.connector.RequestFacade@18105e8
 * org.apache.catalina.connector.ResponseFacade@1aacd5f
 * org.apache.catalina.session.StandardSessionFacade@913dc1
 */