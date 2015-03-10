package org.openyu.socklet.session.service.event;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import org.openyu.commons.lang.event.BaseListener;
import org.openyu.socklet.config.vo.ListenerConfig;
import org.openyu.socklet.socklet.ex.SockletException;

/**
 * 會話監聽器
 */
public interface SessionListener extends BaseListener {

	/**
	 * 會話已建立
	 * 
	 * @param sessionEvent
	 */
	void sessionCreated(SessionEvent sessionEvent);

	/**
	 * 會話已銷毀
	 * 
	 * @param sessionEvent
	 */
	void sessionDestroyed(SessionEvent sessionEvent);

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
