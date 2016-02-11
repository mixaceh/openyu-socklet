package org.openyu.socklet.context.service.event;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import org.openyu.commons.lang.event.BaseListener;
import org.openyu.socklet.config.vo.ListenerConfig;
import org.openyu.socklet.socklet.ex.SockletException;

/**
 * ContextService.attributes用的listener
 */
public interface ContextAttributeListener extends BaseListener
{

	void attributeAdded(ContextAttributeEvent contextAttributeEvent);

	void attributeRemoved(ContextAttributeEvent contextAttributeEvent);

	void attributeReplaced(ContextAttributeEvent contextAttributeEvent);

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
