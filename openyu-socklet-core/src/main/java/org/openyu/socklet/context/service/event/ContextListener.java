package org.openyu.socklet.context.service.event;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import org.openyu.commons.lang.event.BaseListener;
import org.openyu.socklet.config.vo.ListenerConfig;
import org.openyu.socklet.socklet.ex.SockletException;

/**
 * 本文監聽器
 * 
 * ContextService用的listener
 */
public interface ContextListener extends BaseListener {

	/**
	 * 本文已初始化
	 * 
	 * @param contextEvent
	 */
	void contextInitialized(ContextEvent contextEvent);

	/**
	 * 本文已銷毀化
	 * 
	 * @param contextEvent
	 */
	void contextDestroyed(ContextEvent contextEvent);

	void init(ListenerConfig listenerConfig) throws SockletException;

	ListenerConfig getListenerConfig();

	void setListenerConfig(ListenerConfig listenerConfig);

	String getInitParameter(String name);

	Enumeration<String> getInitParameterNames();

	Map<String, String> getInitParameters();

	void setInitParameters(Map<String, String> initParameters);

	/**
	 * 想要註冊的acceptor id, ex:master,slave1
	 * 
	 * @return
	 */
	Set<String> getAcceptors();

	void setAcceptors(Set<String> acceptors);

}
