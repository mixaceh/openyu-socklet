package org.openyu.socklet.login.service;

import org.openyu.commons.service.BaseService;

public interface LoginService extends BaseService
{
	void authorize(String accountId, String authKey, String ip);
}
