package org.openyu.socklet.account.net.socklet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.socklet.account.service.AccountService;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;

public class AccountSocklet extends SockletServiceSupporter
{
	private static transient final Logger log = LogManager.getLogger(AccountSocklet.class);

	@Autowired
	@Qualifier("accountService")
	protected transient AccountService accountService;

	public AccountSocklet()
	{

	}

	public void service(Message request)
	{
		//訊息
		CoreMessageType messageType = (CoreMessageType) request.getMessageType();
		switch (messageType)
		{
			case ACCOUNT_AUTHORIZE_REQUEST:
			{
				String accountId = request.getString(0);
				String password = request.getString(1);
				accountService.authorize(accountId, password);
				break;
			}
			case ACCOUNT_AUTHORIZE_FROM_LOGIN_REQUEST:
			{
				String accountId = request.getString(0);
				String authKey = request.getString(1);
				accountService.authorizeFromLogin(accountId, authKey);
				break;
			}
			default:
				log.warn("Can't resolve: " + request);
				break;
		}
	}

}
