package org.openyu.socklet.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.socklet.core.service.CoreService;
import org.openyu.socklet.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreServiceImpl extends BaseServiceSupporter implements CoreService {

	private static final long serialVersionUID = -8717537864535577700L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(CoreServiceImpl.class);

	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;

	public CoreServiceImpl() {

	}

	@Override
	protected void doStart() throws Exception {

	}

	@Override
	protected void doShutdown() throws Exception {

	}

	/**
	 * 角色連線時
	 * 
	 * @param roleId
	 */
	public void onConnect(String roleId) {
		LOGGER.info("[" + roleId + "] connect");
	}

	/**
	 * 角色斷線時
	 * 
	 * @param roleId
	 */
	public void onDisconnect(String roleId) {
		LOGGER.info("[" + roleId + "] disconnect");
	}

}
