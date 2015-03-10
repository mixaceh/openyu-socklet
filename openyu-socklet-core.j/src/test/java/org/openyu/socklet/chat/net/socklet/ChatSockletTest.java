package org.openyu.socklet.chat.net.socklet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.message.vo.Message;

public class ChatSockletTest extends CoreTestSupporter
{

	@Test
	public void CHAT_SAY_REQUEST()
	{
		final String ROLE_ID = "TEST_ROLE_1";
		//
		Message message = messageService.createClient(ROLE_ID, CoreMessageType.CHAT_SAY_REQUEST);
		message.addInt(1);//頻道類型
		message.addString("Hello world");//聊天內容
		message.addString("<hr/>");//html
		//
		chatSocklet.service(message);
	}

	//--------------------------------------------------
	// 啟動slave1,模擬真正連線
	//--------------------------------------------------
	public static class AcceptorTest extends CoreTestSupporter
	{
		@Before
		public void setUp() throws Exception
		{
			final String ROLE_ID = "TEST_ROLE_1";
			//連線到slave1, localhost:4110
			javaConnector.setId(ROLE_ID);
			javaConnector.setIp("localhost");
			javaConnector.setPort(4110);
			javaConnector.start();
		}

		@After
		public void tearDown() throws Exception
		{
			ThreadHelper.sleep(3 * 1000);
		}

		@Test
		public void CHAT_SAY_REQUEST()
		{
			Message message = messageService.createClient(javaConnector.getId(),
				CoreMessageType.CHAT_SAY_REQUEST);
			message.addInt(1);//頻道類型
			message.addString("Hello world");//聊天內容
			message.addString("<hr/>");//html
			//
			javaConnector.send(message);
		}
	}
}