package org.openyu.socklet.core.net.socklet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.socklet.core.service.CoreService;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreSocklet extends SockletServiceSupporter {

	private static final long serialVersionUID = 3693643778642322714L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(CoreSocklet.class);

	@Autowired
	@Qualifier("coreService")
	protected transient CoreService coreService;

	public CoreSocklet() {

	}

	public void service(Message message) {
		// 訊息
		CoreMessageType messageType = (CoreMessageType) message.getMessageType();
		switch (messageType) {
		case CORE_CONNECT_REQUEST: {
			String roleId = message.getString(0);
			coreService.onConnect(roleId);
			break;
		}
		case CORE_DISCONNECT_REQUEST: {
			String roleId = message.getString(0);
			coreService.onDisconnect(roleId);
			break;
		}
		default:
			LOGGER.warn("Can't resolve: " + message);
			break;
		}
	}

}
