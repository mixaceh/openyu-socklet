package org.openyu.socklet.login.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.login.service.LoginService;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.vo.Message;

public class LoginServiceImpl extends BaseServiceSupporter implements LoginService
{
	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;

	public LoginServiceImpl()
	{

	}

	public void authorize(String accountId, String authKey, String ip)
	{
		System.out.println("onAuthorize...");
		Message message = messageService.createMessage(CoreModuleType.LOGIN,
			CoreModuleType.ACCOUNT, CoreMessageType.ACCOUNT_AUTHORIZE_FROM_LOGIN_REQUEST);
		message.addString(accountId);
		message.addString(authKey);
		messageService.addMessage(message);
	}

}
