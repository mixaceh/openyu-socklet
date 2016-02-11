package org.openyu.socklet.chat.net.socklet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.socklet.chat.service.ChatService;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatSocklet extends SockletServiceSupporter {

	private static final long serialVersionUID = -2755668339150482533L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(ChatSocklet.class);

	@Autowired
	@Qualifier("chatService")
	protected transient ChatService chatService;

	public ChatSocklet() {

	}

	public void service(Message message) {
		// 訊息
		CoreMessageType messageType = (CoreMessageType) message.getMessageType();
		// 角色code
		String roleCode = message.getSender();

		switch (messageType) {
		case CHAT_SAY_REQUEST: {
			int channel = message.getInt(0);
			String text = message.getString(1);
			String html = message.getString(2);
			chatService.onSay(roleCode, channel, text, html);
			break;
		}
		default:
			LOGGER.error("Can't resolve: " + message);
			break;
		}
	}
}
