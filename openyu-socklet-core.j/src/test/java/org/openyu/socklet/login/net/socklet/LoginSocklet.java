package org.openyu.socklet.login.net.socklet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.login.service.LoginService;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginSocklet extends SockletServiceSupporter {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(LoginSocklet.class);

	@Autowired
	@Qualifier("loginService")
	protected transient LoginService loginService;

	public LoginSocklet() {

	}

	public void service(Message message) {
		// 訊息
		CoreMessageType messageType = (CoreMessageType) message.getMessageType();
		switch (messageType) {
		case LOGIN_AUTHORIZE_FROM_ACCOUNT_REQUEST: {
			String accountId = message.getString(0);
			String authKey = message.getString(1);
			String ip = message.getString(2);
			loginService.authorize(accountId, authKey, ip);
			break;
		}
		default: {
			LOGGER.warn("Can't resolve: " + message);
			break;
		}
		}
	}

}
