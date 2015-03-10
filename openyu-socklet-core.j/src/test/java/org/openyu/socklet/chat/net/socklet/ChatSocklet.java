package org.openyu.socklet.chat.net.socklet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.socklet.chat.service.ChatService;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;

public class ChatSocklet extends SockletServiceSupporter
{
	private static transient final Logger log = LogManager.getLogger(ChatSocklet.class);

	@Autowired
	@Qualifier("chatService")
	protected transient ChatService chatService;

	public ChatSocklet()
	{

	}

	public void service(Message request)
	{
		//訊息
		CoreMessageType messageType = (CoreMessageType) request.getMessageType();
		//角色code
		String roleCode = request.getSender();

		switch (messageType)
		{
			case CHAT_SAY_REQUEST:
			{
				int channel = request.getInt(0);
				String text = request.getString(1);
				String html = request.getString(2);
				chatService.onSay(roleCode, channel, text, html);
				break;
			}
			default:
				log.error("Can't resolve: " + request);
				break;
		}
	}
}
