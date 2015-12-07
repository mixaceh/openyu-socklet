package org.openyu.socklet.acceptor.service.impl;

import java.util.LinkedList;
import java.util.Set;

import org.openyu.commons.nio.NioHelper;
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

	public static final String ID = "id";

	public static final String INSTANCE_ID = "instanceId";

	public static final String OUTPUT_ID = "outputId";

	public static final String RELATION_SERVERS = "relationServers";

	public static final String CLIENT_SERVERS = "clientServers";

	public static final String MAX_CLIENT = "maxClient";

	public static final String CLUSTER = "cluster";

	public static final String RELATIONS = "relations";

	public static final String RELATION_RETRY_NUMBER = "relationRetryNumber";

	public static final String RELATION_RETRY_PAUSEMILLS = "relationRetryPauseMills";

	public static final String INIT_PARAMETERS = "initParameters";

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

			result.setModuleTypeName(extendedProperties.getString(MODULE_TYPE_NAME, null));
			result.setMessageTypeName(extendedProperties.getString(MESSAGE_TYPE_NAME, null));

			/**
			 * extendedProperties
			 */
			Set<String> keys = extendedProperties.keySet();
			for (String key : keys) {
				if (key.startsWith((acceptorId + "."))) {
					// System.out.println(key); // account.id
					// 第1個"."分割符
					int pos = key.indexOf(".");
					if (pos < 0) {
						continue;
					}
					// id
					StringBuilder prop = new StringBuilder();
					prop.append(key.substring(pos + 1));
					if (ID.equals(prop.toString())) {
						result.setId(extendedProperties.getString(key, null));
					} else if (INSTANCE_ID.equals(prop.toString())) {
						result.setInstanceId(extendedProperties.getString(key, null));
					} else if (OUTPUT_ID.equals(prop.toString())) {
						result.setOutputId(extendedProperties.getString(key, null));
					} else if (MAX_CLIENT.equals(prop.toString())) {
						result.setMaxClient(extendedProperties.getInt(key, 0));
					} else if (CLUSTER.equals(prop.toString())) {
						result.setCluster(extendedProperties.getString(key, null));
					} else if (RELATION_RETRY_NUMBER.equals(prop.toString())) {
						result.setRelationRetryNumber(extendedProperties.getInt(key, NioHelper.DEFAULT_RETRY_NUMBER));
					} else if (RELATION_RETRY_PAUSEMILLS.equals(prop.toString())) {
						result.setRelationRetryPauseMills(
								extendedProperties.getLong(key, NioHelper.DEFAULT_RETRY_PAUSE_MILLS));
					} else {
						// 第2個"."分割符
						int pos2nd = prop.indexOf(".");
						if (pos2nd < 0) {
							continue;
						}
						// account.relationServers.0 -> relationServers
						// clientServers/relations/initParameters
						StringBuilder prop2nd = new StringBuilder(prop.substring(0, pos2nd));

						// account.relationServers.0 -> 0
						// account.relationServers.1 -> 1
						// account.initParameters.debug -> debug
						// 0/1/debug
						StringBuilder prop3rd = new StringBuilder(prop.substring(pos2nd + 1));
						if (RELATION_SERVERS.equals(prop2nd.toString())) {
							result.getRelationServers().add(extendedProperties.getString(key, null));
						} else if (CLIENT_SERVERS.equals(prop2nd.toString())) {
							result.getClientServers().add(extendedProperties.getString(key, null));
						} else if (RELATIONS.equals(prop2nd.toString())) {
							// master.relations.0=account
							// master.relations.1=login
							// result.getRelations().add(extendedProperties.getString(key,
							// null));
							String relation = extendedProperties.getString(key, null);
							result.getRelations().put(relation, new LinkedList<String>());
						} else if (INIT_PARAMETERS.equals(prop2nd.toString())) {
							// account.initParameters.debug -> debug
							result.getInitParameters().put(prop3rd.toString(), extendedProperties.getString(key, null));
						}
					}
				}
			}
			//
			if (result.getId() == null) {
				LOGGER.error(new StringBuilder("Can't resolve acceptorId: ").append(acceptorId).toString());
				return null;
			}
			// 處理relations
			// master.relations.0=account
			// master.relations.1=login
			// -->
			// relations={login=[127.0.0.1:3101, 127.0.0.1:3100],
			// account=[127.0.0.1:3001, 127.0.0.1:3000]}
			for (String relation : result.getRelations().keySet()) {
				for (String key : keys) {
					if (key.startsWith((relation + "."))) {
						// 第1個"."分割符
						int pos = key.indexOf(".");
						if (pos < 0) {
							continue;
						}
						StringBuilder prop = new StringBuilder();
						prop.append(key.substring(pos + 1));

						// 第2個"."分割符
						int pos2nd = prop.indexOf(".");
						if (pos2nd < 0) {
							continue;
						}
						// account.relationServers.0 -> relationServers
						StringBuilder prop2nd = new StringBuilder(prop.substring(0, pos2nd));
						if (RELATION_SERVERS.equals(prop2nd.toString())) {
							result.getRelations().get(relation).add(extendedProperties.getString(key, null));
						}
					}
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
