package org.openyu.socklet.account.net.socklet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.socklet.account.service.AccountService;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountSocklet extends SockletServiceSupporter {

	private static final long serialVersionUID = -7539704097964233121L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(AccountSocklet.class);

	@Autowired
	@Qualifier("accountService")
	protected transient AccountService accountService;

	public AccountSocklet() {

	}

	public void service(Message message) {
		// 訊息
		CoreMessageType messageType = (CoreMessageType) message.getMessageType();
		switch (messageType) {
		case ACCOUNT_AUTHORIZE_REQUEST: {
			String accountId = message.getString(0);
			String password = message.getString(1);
			accountService.authorize(accountId, password);
			break;
		}
		case ACCOUNT_AUTHORIZE_FROM_LOGIN_REQUEST: {
			String accountId = message.getString(0);
			String authKey = message.getString(1);
			accountService.authorizeFromLogin(accountId, authKey);
			break;
		}
		default: {
			LOGGER.warn("Can't resolve: " + message);
			break;
		}
		}
	}

}
