package org.openyu.socklet.core.net.socklet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.message.vo.Message;

public class CoreSockletTest extends CoreTestSupporter
{

	@Test
	public void CORE_CONNECT_REQUEST()
	{
		final String ROLE_ID = "TEST_ROLE_1";

		// 仿client發訊息,其實是由server發過來的
		Message message = messageService.createMessage(CoreModuleType.CLIENT, CoreModuleType.CORE,
			CoreMessageType.CORE_CONNECT_REQUEST);
		message.addString(ROLE_ID);
		coreSocklet.service(message);
	}

	@Test
	public void CORE_DISCONNECT_REQUEST()
	{
		final String ROLE_ID = "TEST_ROLE_1";

		// 仿client發訊息,其實是由server發過來的
		// 連線
		Message message = messageService.createMessage(CoreModuleType.CLIENT, CoreModuleType.CORE,
			CoreMessageType.CORE_CONNECT_REQUEST);
		message.addString(ROLE_ID);
		coreSocklet.service(message);

		// 仿client發訊息,其實是由server發過來的
		// 再斷線
		message = messageService.createMessage(CoreModuleType.CLIENT, CoreModuleType.CORE,
			CoreMessageType.CORE_DISCONNECT_REQUEST);
		message.addString(ROLE_ID);
		coreSocklet.service(message);
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
		public void CORE_CONNECT_REQUEST()
		{
			final String ROLE_ID = "TEST_ROLE_1";

			// 仿client發訊息,其實是由server發過來的
			Message message = messageService.createMessage(CoreModuleType.CLIENT,
				CoreModuleType.CORE, CoreMessageType.CORE_CONNECT_REQUEST);
			message.setSender("slave1");// TODO sender要自己塞,麻煩
			message.addString(ROLE_ID);
			//
			javaConnector.send(message);
		}

		@Test
		public void CORE_DISCONNECT_REQUEST()
		{
			final String ROLE_ID = "TEST_ROLE_1";

			// 仿client發訊息,其實是由server發過來的
			// 連線
			Message message = messageService.createMessage(CoreModuleType.CLIENT,
				CoreModuleType.CORE, CoreMessageType.CORE_CONNECT_REQUEST);
			message.setSender("slave1");// TODO sender要自己塞,麻煩
			message.addString(ROLE_ID);
			//
			javaConnector.send(message);

			// 仿client發訊息,其實是由server發過來的
			// 再斷線
			message = messageService.createMessage(CoreModuleType.CLIENT, CoreModuleType.CORE,
				CoreMessageType.CORE_DISCONNECT_REQUEST);
			message.setSender("slave1");// TODO sender要自己塞,麻煩
			message.addString(ROLE_ID);
			//
			javaConnector.send(message);
		}
	}
}