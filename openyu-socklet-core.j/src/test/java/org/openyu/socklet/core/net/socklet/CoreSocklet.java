package org.openyu.socklet.core.net.socklet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.socklet.core.service.CoreService;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;

public class CoreSocklet extends SockletServiceSupporter
{
	private static transient final Logger log = LogManager.getLogger(CoreSocklet.class);

	@Autowired
	@Qualifier("coreService")
	protected transient CoreService coreService;

	public CoreSocklet()
	{
		
	}

	public void service(Message request)
	{
		//訊息
		CoreMessageType messageType = (CoreMessageType) request.getMessageType();
		switch (messageType)
		{
			case CORE_CONNECT_REQUEST:
			{
				String roleId = request.getString(0);
				coreService.onConnect(roleId);
				break;
			}
			case CORE_DISCONNECT_REQUEST:
			{
				String roleId = request.getString(0);
				coreService.onDisconnect(roleId);
				break;
			}
			default:
				log.warn("Can't resolve: " + request);
				break;
		}
	}

}
