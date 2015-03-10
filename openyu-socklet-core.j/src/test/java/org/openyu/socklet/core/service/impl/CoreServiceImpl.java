package org.openyu.socklet.core.service.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.socklet.core.service.CoreService;
import org.openyu.socklet.message.service.MessageService;

public class CoreServiceImpl extends BaseServiceSupporter implements CoreService
{
	private static transient final Logger log = LogManager.getLogger(CoreServiceImpl.class);

	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;

	public CoreServiceImpl()
	{

	}

	/**
	 * 角色連線時
	 * 
	 * @param roleId
	 */
	public void onConnect(String roleId)
	{
		log.info("[" + roleId + "] connect");
	}

	/**
	 * 角色斷線時
	 * 
	 * @param roleId
	 */
	public void onDisconnect(String roleId)
	{
		log.info("[" + roleId + "] disconnect");
	}
}
