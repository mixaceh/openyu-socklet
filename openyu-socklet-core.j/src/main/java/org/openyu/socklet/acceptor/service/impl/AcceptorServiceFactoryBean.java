package org.openyu.socklet.acceptor.service.impl;

import org.openyu.commons.service.supporter.BaseServiceFactorySupporter;
import org.openyu.socklet.acceptor.service.AcceptorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AcceptorService工廠
 */
public final class AcceptorServiceFactoryBean<T extends AcceptorService>
		extends BaseServiceFactorySupporter<AcceptorService> {

	private static final long serialVersionUID = 7441283283901230776L;

	private static final transient Logger LOGGER = LoggerFactory.getLogger(AcceptorServiceFactoryBean.class);


	public static final  String MODULE_TYPE_NAME = "moduleTypeName";

	public static final  String MESSAGE_TYPE_NAME = "messageTypeName";

	/**
	 * 所有屬性
	 */
	public static final String[] ALL_PROPERTIES = {};

	public AcceptorServiceFactoryBean() {
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
			
			System.out.println(extendedProperties);

			/**
			 * injectiion
			 */

			// 啟動
			result.start();
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
