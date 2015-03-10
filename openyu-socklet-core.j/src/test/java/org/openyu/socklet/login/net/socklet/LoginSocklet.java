package org.openyu.socklet.login.net.socklet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.login.service.LoginService;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;

public class LoginSocklet extends SockletServiceSupporter
{
	private static transient final Logger log = LogManager.getLogger(LoginSocklet.class);

	@Autowired
	@Qualifier("loginService")
	protected transient LoginService loginService;

	public LoginSocklet()
	{

	}

	public void service(Message request)
	{
		//訊息
		CoreMessageType messageType = (CoreMessageType) request.getMessageType();
		switch (messageType)
		{
			case LOGIN_AUTHORIZE_FROM_ACCOUNT_REQUEST:
			{
				String accountId = request.getString(0);
				String authKey = request.getString(1);
				String ip = request.getString(2);
				loginService.authorize(accountId, authKey, ip);
				break;
			}
			default:
				log.warn("Can't resolve: " + request);
				break;
		}
	}

}
