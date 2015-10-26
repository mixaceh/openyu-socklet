package org.openyu.socklet.blank;

import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlankSocklet extends SockletServiceSupporter {

	private static final long serialVersionUID = 3693643778642322714L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(BlankSocklet.class);

	public BlankSocklet() {

	}

	public void service(Message message) {
		// 訊息
		CoreMessageType messageType = (CoreMessageType) message.getMessageType();
		switch (messageType) {
		case CORE_CONNECT_REQUEST: {
			System.out.println(message);
			break;
		}
		case CORE_DISCONNECT_REQUEST: {
			System.out.println(message);
			break;
		}
		default:
			LOGGER.warn("Can't resolve: " + message);
			break;
		}
	}

}
