package org.openyu.socklet.connector.vo.impl;

import org.junit.Test;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.connector.vo.RelationConnector;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;

public class RelationConnectorImplTest extends CoreTestSupporter
{

	@Test
	public void start()
	{
		//salve2 -> slave1:3110
		RelationConnector relationClient = new RelationConnectorImpl("slave2:99:localhost:3110",
			CoreModuleType.class, CoreMessageType.class, protocolService, "localhost", 3110);
		relationClient.setReceiver(receiver);
		relationClient.start();

		System.out.println("id: " + relationClient.getId());//slave2:99:localhost:3110
		System.out.println("sender: " + relationClient.getSender());//slave2:99:localhost:3110
		System.out.println("acceptor: " + relationClient.getAcceptor());//slave1
		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void start3()
	{
		// --------------------------------------------------
		//client 1
		//salve2 -> slave1:3110
		// --------------------------------------------------
		RelationConnector relationClient = new RelationConnectorImpl("slave2:99:localhost:3110",
			CoreModuleType.class, CoreMessageType.class, protocolService, "localhost", 3110);
		relationClient.setReceiver(receiver);
		relationClient.start();

		// --------------------------------------------------
		//client 2
		//salve3 -> slave1:3110
		// --------------------------------------------------
		RelationConnector relationClient2 = new RelationConnectorImpl("slave3:99:localhost:3110",
			CoreModuleType.class, CoreMessageType.class, protocolService, "localhost", 3110);
		relationClient2.setReceiver(receiver);
		relationClient2.start();

		// --------------------------------------------------
		//client 3
		//salve4 -> slave1:3110
		// --------------------------------------------------
		RelationConnector relationClient3 = new RelationConnectorImpl("slave4:99:localhost:3110",
			CoreModuleType.class, CoreMessageType.class, protocolService, "localhost", 3110);
		relationClient3.setReceiver(receiver);
		relationClient3.start();

		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void startStress()
	{
		for (int i = 0; i < 11; i++)
		{
			//salve2 -> slave1:3110
			RelationConnector relationClient = new RelationConnectorImpl("slave2:" + i
					+ ":localhost:3110", CoreModuleType.class, CoreMessageType.class,
				protocolService, "localhost", 3110);
			relationClient.setReceiver(receiver);
			relationClient.start();
		}
		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void send()
	{
		//client -> slave1:4110
		JavaConnectorImpl javaConnector = new JavaConnectorImpl("TEST_ROLE_1", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4110);
		javaConnector.setReceiver(receiver);
		javaConnector.start();

		// --------------------------------------------------
		//salve2 -> slave1:3110
		// --------------------------------------------------
		RelationConnector relationClient = new RelationConnectorImpl("slave2:99:localhost:3110",
			CoreModuleType.class, CoreMessageType.class, protocolService, "localhost", 3110);
		relationClient.start();

		// --------------------------------------------------
		// message 1, 聊天請求 salve2->salve1.TEST_ROLE_1
		// --------------------------------------------------
		// from [TEST_ROLE_2] (11403) CHAT_MESSAGE_REQUEST (11000) CORE => (11400) CHAT to []
		Message message = messageService.createMessage(CoreModuleType.CHAT, CoreModuleType.CLIENT,
			CoreMessageType.CHAT_SAY_REPONSE, "TEST_ROLE_1");
		message.setSender("slave2");
		message.setCategoryType(CategoryType.MESSAGE_RELATION);
		//
		message.addInt(1);// 頻道類型
		message.addString("Hello world");// 聊天內容
		message.addString("<hr/>");// html
		//
		relationClient.send(message);
		//
		ThreadHelper.sleep(3 * 1000);
	}
}
