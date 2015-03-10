package org.openyu.socklet.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.openyu.commons.security.AuthKeyService;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.socklet.account.service.AccountService;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.vo.Message;

public class AccountServiceImpl extends BaseServiceSupporter implements
		AccountService {

	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;
	
	@Autowired
	@Qualifier("authKeyService")
	protected transient AuthKeyService authKeyService;
	

	public AccountServiceImpl() {
	}

	public void authorize(String accountId, String password) {
		System.out.println("onAuthorize...");
		// check by db
		String authKey = checkAccount(accountId, password);
		//
		if (authKey != null) {
			Message message = messageService.createMessage(
					CoreModuleType.ACCOUNT, CoreModuleType.LOGIN,
					CoreMessageType.LOGIN_AUTHORIZE_FROM_ACCOUNT_REQUEST);
			message.addString(accountId);
			message.addString(authKey);
			message.addString("10.0.0.1");
			messageService.addMessage(message);
		}
	}

	public String checkAccount(String accountId, String password) {
		return authKeyService.randomKey();
	}

	public void authorizeFromLogin(String accountId, String authKey) {
		sendAuthorize(ErrorType.NO_ERROR, accountId, authKey);
	}

	public void sendAuthorize(ErrorType errorType, String accountId,
			String authKey) {
		Message message = messageService.createMessage(CoreModuleType.ACCOUNT,
				CoreModuleType.CLIENT,
				CoreMessageType.ACCOUNT_AUTHORIZE_REPONSE, accountId);

		message.addInt(errorType.getValue());// 0, errorType 錯誤碼

		switch (errorType) {
		// 沒有錯誤,才發其他欄位訊息
		case NO_ERROR:
			message.addString(accountId);
			message.addString(authKey);
			message.addString("127.0.0.1");// TODO server ip
			message.addInt(3000);// TODO server port
			break;

		default:
			break;
		}
		//
		messageService.addMessage(message);
	}

}
