package org.openyu.socklet.acceptor.service.impl;

import java.util.Set;

import org.openyu.commons.security.AuthKeyService;
import org.openyu.commons.security.anno.DefaultAuthKeyService;
import org.openyu.commons.service.supporter.BaseServiceFactorySupporter;
import org.openyu.commons.thread.ThreadService;
import org.openyu.socklet.acceptor.anno.AcceptorThreadService;
import org.openyu.socklet.acceptor.service.AcceptorService;
import org.openyu.socklet.message.anno.DefaultMessageService;
import org.openyu.socklet.message.anno.DefaultProtocolService;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.service.ProtocolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AcceptorService工廠
 */
public final class AcceptorServiceFactoryBean<T extends AcceptorService>
		extends BaseServiceFactorySupporter<AcceptorService> {

	private static final long serialVersionUID = 7441283283901230776L;

	private static final transient Logger LOGGER = LoggerFactory.getLogger(AcceptorServiceFactoryBean.class);
	/**
	 * 線程服務
	 */
	@AcceptorThreadService
	protected transient ThreadService threadService;

	/**
	 * 訊息服務
	 */
	@DefaultMessageService
	protected transient MessageService messageService;

	/**
	 * 協定服務
	 */
	@DefaultProtocolService
	protected transient ProtocolService protocolService;

	/**
	 * 認證碼服務
	 */
	@DefaultAuthKeyService
	protected transient AuthKeyService authKeyService;

	public static final String MODULE_TYPE_NAME = "moduleTypeName";

	public static final String MESSAGE_TYPE_NAME = "messageTypeName";

	private String acceptorId;

	/**
	 * 所有屬性
	 */
	public static final String[] ALL_PROPERTIES = {};

	public AcceptorServiceFactoryBean() {
	}

	public String getAcceptorId() {
		return acceptorId;
	}

	public void setAcceptorId(String acceptorId) {
		this.acceptorId = acceptorId;
	}

	/**
	 * 建構
	 * 
	 * @return
	 */
	protected AcceptorService createService() throws Exception {
		AcceptorServiceImpl result = null;
		try {
			result = new AcceptorServiceImpl();
			//
			result.setApplicationContext(applicationContext);
			result.setBeanFactory(beanFactory);
			result.setResourceLoader(resourceLoader);
			//
			result.setCreateInstance(true);
			/**
			 * extendedProperties
			 */
			result.setModuleTypeName(extendedProperties.getString(MODULE_TYPE_NAME, null));
			result.setMessageTypeName(extendedProperties.getString(MESSAGE_TYPE_NAME, null));
			//

			Set<String> keys = extendedProperties.keySet();
			for (String key : keys) {
				if (key.startsWith((acceptorId + "."))) {
					System.out.println(key);
				}
			}

			/**
			 * injectiion
			 */
			result.setThreadService(threadService);
			result.setMessageService(messageService);
			result.setProtocolService(protocolService);
			result.setAuthKeyService(authKeyService);

			// 啟動
			// result.start();
		} catch (Exception e) {
			LOGGER.error(new StringBuilder("Exception encountered during createService()").toString(), e);
			try {
				result = (AcceptorServiceImpl) shutdownService();
			} catch (Exception sie) {
				throw sie;
			}
			throw e;
		}
		return result;
	}
}
