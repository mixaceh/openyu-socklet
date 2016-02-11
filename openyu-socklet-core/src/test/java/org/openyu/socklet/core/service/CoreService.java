package org.openyu.socklet.core.service;

import org.openyu.commons.service.BaseService;

public interface CoreService extends BaseService
{
	/**
	 * 角色連線時
	 * @param roleId
	 */
	void onConnect(String roleId);

	/**
	 * 角色斷線時
	 * @param roleId
	 */
	void onDisconnect(String roleId);
}
