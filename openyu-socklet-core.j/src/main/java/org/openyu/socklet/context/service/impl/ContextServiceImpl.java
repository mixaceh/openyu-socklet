package org.openyu.socklet.context.service.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.openyu.commons.enumz.EnumHelper;
import org.openyu.commons.lang.event.EventCaster;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.commons.util.AssertHelper;
import org.openyu.commons.util.CollectionHelper;
import org.openyu.socklet.acceptor.service.event.RelationEvent;
import org.openyu.socklet.acceptor.service.event.RelationListener;
import org.openyu.socklet.config.vo.ListenerConfig;
import org.openyu.socklet.config.vo.impl.ListenerConfigImpl;
import org.openyu.socklet.context.service.ContextService;
import org.openyu.socklet.context.service.event.ContextAttributeEvent;
import org.openyu.socklet.context.service.event.ContextAttributeListener;
import org.openyu.socklet.context.service.event.ContextEvent;
import org.openyu.socklet.context.service.event.ContextListener;
import org.openyu.socklet.message.vo.ModuleType;
import org.openyu.socklet.session.service.event.SessionEvent;
import org.openyu.socklet.session.service.event.SessionListener;
import org.openyu.socklet.session.vo.Session;
import org.openyu.socklet.socklet.service.SockletService;
import org.openyu.socklet.socklet.vo.SockletConfig;
import org.openyu.socklet.socklet.vo.impl.SockletConfigImpl;

/**
 * 本文服務
 */
public class ContextServiceImpl extends BaseServiceSupporter implements ContextService {

	private static final long serialVersionUID = 8260056540916926080L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(ContextServiceImpl.class);

	/**
	 * master/slave1...
	 */
	private String id;

	private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>(10);

	private Map<String, String> initParameters = new ConcurrentHashMap<String, String>(10);

	// ModuleType,SockletConfig -> (CoreModuleType.FOUR_SYMBO,sockletConfig)
	// private Map<ModuleType, SockletConfig> sockletConfigs = new
	// ConcurrentHashMap<ModuleType, SockletConfig>();

	// ModuleType,SockletService ->
	// (CoreModuleType.FOUR_SYMBOL,fourSymbolSocklet)
	/**
	 * 在此acceptor的sockletService
	 */
	private Map<ModuleType, SockletService> sockletServices = new ConcurrentHashMap<ModuleType, SockletService>();

	/**
	 * 在其它relation acceptor的sockletService
	 */
	private Map<ModuleType, SockletService> relationServices = new ConcurrentHashMap<ModuleType, SockletService>();

	// <session.id=sender,session>
	private Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	private transient EventCaster contextListeners;

	private transient EventCaster attributeListeners;

	private transient EventCaster sessionListeners;

	private transient EventCaster relationListeners;

	// -------------------------------------------------

	// [slave1]
	private String acceptorContext;

	/**
	 * 模組類別
	 */
	@SuppressWarnings("rawtypes")
	private Class moduleTypeClass;

	/**
	 * 訊息類別
	 */
	@SuppressWarnings("rawtypes")
	private Class messageTypeClass;

	public ContextServiceImpl(String id) {
		this.id = id;
	}

	public ContextServiceImpl() {
		this(null);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setModuleTypeClass(Class moduleTypeClass) {
		this.moduleTypeClass = moduleTypeClass;
	}

	public void setMessageTypeClass(Class messageTypeClass) {
		this.messageTypeClass = messageTypeClass;
	}

	public void setAttribute(String name, Object value) {
		if (value != null) {
			Object oldValue = null;
			oldValue = attributes.put(name, value);
			if (oldValue == null) {
				fireAttributeAdded(name, value);
			} else {
				fireAttributeReplaced(name, value);
			}
		} else {
			removeAttribute(name);
		}
	}

	public Object getAttribute(String name) {
		synchronized (attributes) {
			return attributes.get(name);
		}
	}

	public void removeAttribute(String name) {
		Object oldValue = null;
		oldValue = attributes.remove(name);
		if (oldValue != null) {
			fireAttributeRemoved(name, oldValue);
		}
	}

	public String getInitParameter(String name) {
		return (String) initParameters.get(name);
	}

	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(initParameters.keySet());
	}

	public void log(String message, Throwable throwable) {
		LOGGER.error(message, throwable);
	}

	public void log(String msg) {
		LOGGER.info(msg);
	}

	public Map<String, String> getInitParameters() {
		return initParameters;
	}

	public void setInitParameters(Map<String, String> initParameters) {
		this.initParameters = initParameters;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void clearAttributes() {
		Map<String, Object> oldValue = new LinkedHashMap<String, Object>(attributes);
		attributes.clear();
		fireAttributeRemoved("all", oldValue.values());
	}

	// public Map<ModuleType, SockletConfig> getSockletConfigs() {
	// return sockletConfigs;
	// }
	//
	// public void setSockletConfigs(Map<ModuleType, SockletConfig>
	// sockletConfigs) {
	// this.sockletConfigs = sockletConfigs;
	// }

	public Map<ModuleType, SockletService> getSockletServices() {
		return sockletServices;
	}

	public void setSockletServices(Map<ModuleType, SockletService> sockletServices) {
		this.sockletServices = sockletServices;
	}

	public Map<ModuleType, SockletService> getRelationServices() {
		return relationServices;
	}

	public void setRelationServices(Map<ModuleType, SockletService> relationServices) {
		this.relationServices = relationServices;
	}

	public Map<String, Session> getSessions() {
		return sessions;
	}

	public void setSessions(Map<String, Session> sessions) {
		this.sessions = sessions;
	}

	@Override
	protected void doStart() throws Exception {
		this.acceptorContext = "[" + id + "] ";
		// ----------------------------------------------
		// ContextListener
		// ----------------------------------------------
		// Future<List<String>> future = threadService.submit(new
		// InitContextListenerCaller());
		// List<String> result = future.get();
		initContextListener();
		// ----------------------------------------------
		// ContextAttributeListener
		// ----------------------------------------------
		// Future<List<String>> future = threadService.submit(new
		// InitContextAttributeListenerCaller());
		// List<String> result = future.get();
		initContextAttributeListener();
		// ----------------------------------------------
		// SessionListener
		// ----------------------------------------------
		// Future<List<String>> future = threadService.submit(new
		// InitSessionListenerCaller());
		// List<String> result = future.get();
		initSessionListener();

		// ----------------------------------------------
		// RelationListener
		// ----------------------------------------------
		// Future<List<String>> future = threadService.submit(new
		// InitRelationListenerCaller());
		// List<String> result = future.get();
		initRelationListener();

		// ----------------------------------------------
		// SockletService
		// ----------------------------------------------
		// Future<List<String>> future = threadService.submit(new
		// InitSockletServiceCaller());
		// List<String> result = future.get();
		initSockletService();
		//
		fireContextInitialized(id);
	}

	@Override
	protected void doShutdown() throws Exception {
		try {
			int size = 0;
			if (contextListeners != null && (size = contextListeners.size()) > 0) {
				fireContextDestroyed(id);
				contextListeners.clear();
				LOGGER.info(acceptorContext + "ContextListener [" + size + "] Had been removed");
			}
		} catch (Exception e) {
			LOGGER.error(new StringBuilder("Exception encountered during remove ContextListener").toString(), e);
			throw e;
		}
		//
		try {
			int size = 0;
			if (attributeListeners != null && (size = attributeListeners.size()) > 0) {
				attributeListeners.clear();
				LOGGER.info(acceptorContext + "ContextAttributeListener [" + size + "] Had been removed.");
			}
		} catch (Exception e) {
			LOGGER.error(new StringBuilder("Exception encountered during remove ContextAttributeListener").toString(),
					e);
			throw e;
		}
		//
		try {
			int size = 0;
			if (sessionListeners != null && (size = sessionListeners.size()) > 0) {
				sessionListeners.clear();
				LOGGER.info(acceptorContext + "SessionListener [" + size + "] Had been removed");
			}
		} catch (Exception e) {
			LOGGER.error(new StringBuilder("Exception encountered during remove SessionListener").toString(), e);
			throw e;
		}
		//
		try {
			int size = 0;
			if (relationListeners != null && (size = relationListeners.size()) > 0) {
				relationListeners.clear();
				LOGGER.info(acceptorContext + "RelationListener [" + size + "] Had been removed");
			}
		} catch (Exception e) {
			LOGGER.error(new StringBuilder("Exception encountered during remove RelationListener").toString(), e);
			throw e;
		}
		//
		try {
			if (CollectionHelper.notEmpty(sockletServices)) {
				for (SockletService sockletService : sockletServices.values()) {
					sockletService.shutdown();
				}
				//
				int size = sockletServices.size();
				sockletServices.clear();
				LOGGER.info(acceptorContext + "SockletService [" + size + "] Had been removed");
			}
		} catch (Exception e) {
			LOGGER.error(new StringBuilder("Exception encountered during remove SockletService").toString(), e);
			throw e;
		}
	}

	// // ------------------------------------------------
	// // can't hotfix
	// // ------------------------------------------------
	// protected class InitContextListenerCaller implements
	// Callable<List<String>> {
	//
	// public List<String> call() throws Exception {
	// return initContextListener();
	// }
	// }

	// ------------------------------------------------
	// ya!!! we can hotfix
	// ------------------------------------------------
	/**
	 * 初始 ContextListener
	 * 
	 * @return
	 */
	protected List<String> initContextListener() throws Exception {
		List<String> result = new LinkedList<String>();
		//
		Map<String, ContextListener> beans = applicationContext.getBeansOfType(ContextListener.class);
		for (ContextListener contextListener : beans.values()) {
			try {
				// ListenerConfig
				ListenerConfig listenerConfig = new ListenerConfigImpl(contextListener);
				listenerConfig.setInitParameters(contextListener.getInitParameters());

				// System.out.println(contextListener);
				boolean contained = contextListener.getAcceptors().contains(id);// master,slave1...n
				if (contained) {
					// init
					contextListener.init(listenerConfig);
					// add listener
					addContextListener(contextListener);
					result.add(contextListener.getClass().getSimpleName());
				}
			} catch (BeanIsAbstractException e) {
			} catch (Exception e) {
				LOGGER.error(new StringBuilder("Exception encountered during initContextListener()").toString(), e);
				throw e;
			}
		}
		//
		if (CollectionHelper.notEmpty(result)) {
			LOGGER.info(
					acceptorContext + "ContextListener [" + result.size() + "], " + result + " Had been initialized");
		}
		return result;
	}

	// // ------------------------------------------------
	// // can't hotfix
	// // ------------------------------------------------
	// protected class InitContextAttributeListenerCaller implements
	// Callable<List<String>> {
	// public List<String> call() throws Exception {
	// return initContextAttributeListener();
	// }
	// }

	// ------------------------------------------------
	// ya!!! we can hotfix
	// ------------------------------------------------
	/**
	 * 初始 ContextAttributeListener
	 * 
	 * @return
	 */
	protected List<String> initContextAttributeListener() throws Exception {
		List<String> result = new LinkedList<String>();
		//
		Map<String, ContextAttributeListener> beans = applicationContext.getBeansOfType(ContextAttributeListener.class);
		for (ContextAttributeListener contextAttributeListener : beans.values()) {
			try {
				// ListenerConfig
				ListenerConfig listenerConfig = new ListenerConfigImpl(contextAttributeListener);
				listenerConfig.setInitParameters(contextAttributeListener.getInitParameters());

				// System.out.println(contextAttributeListener);
				boolean contained = contextAttributeListener.getAcceptors().contains(id);// master,slave1...n
				if (contained) {
					// init
					contextAttributeListener.init(listenerConfig);
					// add listener
					addContextAttributeListener(contextAttributeListener);
					result.add(contextAttributeListener.getClass().getSimpleName());
				}
			} catch (BeanIsAbstractException e) {
			} catch (Exception e) {
				LOGGER.error(
						new StringBuilder("Exception encountered during initContextAttributeListener()").toString(), e);
				throw e;
			}
		}
		//
		if (CollectionHelper.notEmpty(result)) {
			LOGGER.info(acceptorContext + "ContextAttributeListener [" + result.size() + "], " + result
					+ " Had been initialized");
		}
		return result;
	}

	// ------------------------------------------------
	// can't hotfix
	// ------------------------------------------------
	// protected class InitSessionListenerCaller implements
	// Callable<List<String>> {
	// public List<String> call() throws Exception {
	// return initSessionListener();
	// }
	// }

	// ------------------------------------------------
	// ya!!! we can hotfix
	// ------------------------------------------------
	/**
	 * 初始 SessionListener
	 * 
	 * @return
	 */
	protected List<String> initSessionListener() throws Exception {
		List<String> result = new LinkedList<String>();
		//
		Map<String, SessionListener> beans = applicationContext.getBeansOfType(SessionListener.class);
		for (SessionListener sessionListener : beans.values()) {
			try {
				// ListenerConfig
				ListenerConfig listenerConfig = new ListenerConfigImpl(sessionListener);
				listenerConfig.setInitParameters(sessionListener.getInitParameters());

				// System.out.println(sessionListener);
				boolean contained = sessionListener.getAcceptors().contains(id);// master,slave1...n
				if (contained) {
					// init
					sessionListener.init(listenerConfig);
					// add listener
					addSessionListener(sessionListener);
					result.add(sessionListener.getClass().getSimpleName());
				}
			} catch (BeanIsAbstractException e) {
			} catch (Exception e) {
				LOGGER.error(new StringBuilder("Exception encountered during initSessionListener()").toString(), e);
				throw e;
			}
		}
		//
		if (CollectionHelper.notEmpty(result)) {
			LOGGER.info(
					acceptorContext + "SessionListener [" + result.size() + "], " + result + " Had been initialized");
		}
		return result;
	}

	// // ------------------------------------------------
	// // can't hotfix
	// // ------------------------------------------------
	// protected class InitRelationListenerCaller implements
	// Callable<List<String>> {
	// public List<String> call() throws Exception {
	// return initRelationListener();
	// }
	// }

	// ------------------------------------------------
	// ya!!! we can hotfix
	// ------------------------------------------------
	/**
	 * 初始 RelationListener
	 * 
	 * @return
	 */
	protected List<String> initRelationListener() throws Exception {
		List<String> result = new LinkedList<String>();
		//
		Map<String, RelationListener> beans = applicationContext.getBeansOfType(RelationListener.class);
		for (RelationListener relationListener : beans.values()) {
			try {
				boolean contained = relationListener.getAcceptors().contains(id);// master,slave1...n
				if (contained) {
					// add listener
					addRelationListener(relationListener);
					result.add(relationListener.getClass().getSimpleName());
				}
			} catch (BeanIsAbstractException e) {
			} catch (Exception e) {
				LOGGER.error(new StringBuilder("Exception encountered during initRelationListener()").toString(), e);
				throw e;
			}
		}
		//
		if (CollectionHelper.notEmpty(result)) {
			LOGGER.info(
					acceptorContext + "RelationListener [" + result.size() + "], " + result + " Had been initialized");
		}
		return result;
	}

	// // ------------------------------------------------
	// // can't hotfix
	// // ------------------------------------------------
	// protected class InitSockletServiceCaller implements
	// Callable<List<String>> {
	// public List<String> call() throws Exception {
	// return initSockletService();
	// }
	// }

	// ------------------------------------------------
	// ya!!! we can hotfix
	// ------------------------------------------------
	/**
	 * 初始 SockletService
	 * 
	 * @return
	 */
	protected List<String> initSockletService() throws Exception {
		List<String> result = new LinkedList<String>();
		//
		Map<String, SockletService> beans = applicationContext.getBeansOfType(SockletService.class);
		for (SockletService sockletService : beans.values()) {
			try {
				// SockletConfig
				SockletConfig sockletConfig = new SockletConfigImpl(sockletService);

				// 模組類別,id=模組
				@SuppressWarnings("unchecked")
				ModuleType moduleType = (ModuleType) EnumHelper.nameOf(moduleTypeClass, sockletService.getId());
				AssertHelper.notNull(moduleType,
						new StringBuilder().append("The ModuleType must not be null").toString());

				// 在此acceptor的sockletService
				boolean contained = sockletService.getAcceptors().contains(id);// master,slave1...n
				if (contained) {
					sockletServices.put(moduleType, sockletService);
					result.add(sockletService.getId());
				}
				// 在其它relation的sockletService
				else {
					sockletService.setSockletConfig(sockletConfig);
					relationServices.put(moduleType, sockletService);
				}
			} catch (BeanIsAbstractException e) {
			} catch (Exception e) {
				LOGGER.error(new StringBuilder("Exception encountered during initSockletService()").toString(), e);
				throw e;
			}
		}
		//
		if (CollectionHelper.notEmpty(result)) {
			LOGGER.info(
					acceptorContext + "SockletService [" + result.size() + "], " + result + " Had been initialized");
		}
		return result;
	}

	// -------------------------------------------------
	// ContextAttributeListener
	// -------------------------------------------------
	public synchronized void addContextAttributeListener(ContextAttributeListener listener) {
		attributeListeners = EventCaster.add(attributeListeners, listener);
	}

	public synchronized void removeContextAttributeListener(ContextAttributeListener listener) {
		attributeListeners = EventCaster.remove(attributeListeners, listener);
	}

	public ContextAttributeListener[] getContextAttributeListeners() {
		ContextAttributeListener[] result = null;
		if (attributeListeners != null) {
			result = (ContextAttributeListener[]) attributeListeners.getListeners(ContextAttributeListener.class);
		}
		return result;
	}

	protected void fireContextAttributeEvent(int type, String key, Object value) {
		if (attributeListeners != null) {
			ContextAttributeEvent contextAttributeEvent = new ContextAttributeEvent(this, type, key, value);
			attributeListeners.dispatch(contextAttributeEvent);
		}
	}

	protected void fireAttributeAdded(String key, Object value) {
		fireContextAttributeEvent(ContextAttributeEvent.ATTRIBUTE_ADDED, key, value);
	}

	protected void fireAttributeRemoved(String key, Object value) {
		fireContextAttributeEvent(ContextAttributeEvent.ATTRIBUTE_REMOVED, key, value);
	}

	protected void fireAttributeReplaced(String key, Object value) {
		fireContextAttributeEvent(ContextAttributeEvent.ATTRIBUTE_REPLACED, key, value);
	}

	// -------------------------------------------------
	// ContextAttributeListener
	// -------------------------------------------------
	public synchronized void addContextListener(ContextListener listener) {
		contextListeners = EventCaster.add(contextListeners, listener);
	}

	public synchronized void removeContextListener(ContextListener listener) {
		contextListeners = EventCaster.remove(contextListeners, listener);
	}

	public ContextListener[] getContextListeners() {
		ContextListener[] result = null;
		if (contextListeners != null) {
			result = (ContextListener[]) contextListeners.getListeners(ContextListener.class);
		}
		return result;
	}

	protected void fireContextEvent(int type, String contextId) {
		if (contextListeners != null) {
			contextListeners.dispatch(new ContextEvent(this, type, contextId));
		}
	}

	public void fireContextInitialized(String contextId) {
		fireContextEvent(ContextEvent.INITIALIZED, contextId);
	}

	public void fireContextDestroyed(String contextId) {
		fireContextEvent(ContextEvent.DESTROYED, contextId);
	}

	// -------------------------------------------------
	// SessionListener
	// -------------------------------------------------

	public synchronized void addSessionListener(SessionListener listener) {
		sessionListeners = EventCaster.add(sessionListeners, listener);
	}

	public synchronized void removeSessionListener(SessionListener listener) {
		sessionListeners = EventCaster.remove(sessionListeners, listener);
	}

	public SessionListener[] getSockletSessionListeners() {
		SessionListener[] result = null;
		if (sessionListeners != null) {
			result = (SessionListener[]) sessionListeners.getListeners(SessionListener.class);
		}
		return result;
	}

	protected void fireSessionEvent(int type, Session session) {
		if (sessionListeners != null) {
			sessionListeners.dispatch(new SessionEvent(this, type, session));
		}
	}

	public void fireSessionCreated(Session session) {
		fireSessionEvent(SessionEvent.CREATED, session);
	}

	public void fireSessionDestroyed(Session session) {
		fireSessionEvent(SessionEvent.DESTROYED, session);
	}

	// -------------------------------------------------
	// RelationListener
	// -------------------------------------------------
	public synchronized void addRelationListener(RelationListener listener) {
		relationListeners = EventCaster.add(relationListeners, listener);
	}

	public synchronized void removeRelationListener(RelationListener listener) {
		relationListeners = EventCaster.remove(relationListeners, listener);
	}

	public RelationListener[] getRelationListeners() {
		RelationListener[] result = null;
		if (relationListeners != null) {
			result = (RelationListener[]) relationListeners.getListeners(RelationListener.class);
		}
		return result;
	}

	protected void fireRelationEvent(int type, String relationId) {
		if (relationListeners != null) {
			relationListeners.dispatch(new RelationEvent(this, type, relationId));
		}
	}

	public void fireRelationConnected(String relationId) {
		fireRelationEvent(RelationEvent.CONNECTED, relationId);
	}

	public void fireRelationDisconnected(String relationId) {
		fireRelationEvent(RelationEvent.DISCONNECTED, relationId);
	}

	public void fireRelationRefused(String relationId) {
		fireRelationEvent(RelationEvent.REFUSED, relationId);
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", id);
		builder.append("attributes", attributes);
		builder.append("initParameters", initParameters);
		//
		builder.append("sockletServices", sockletServices);
		return builder.toString();
	}

}
