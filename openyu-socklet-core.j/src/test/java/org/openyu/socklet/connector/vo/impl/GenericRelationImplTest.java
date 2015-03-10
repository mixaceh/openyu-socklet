package org.openyu.socklet.connector.vo.impl;

import org.junit.Test;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.connector.vo.GenericRelation;
import org.openyu.socklet.connector.vo.JavaConnector;
import org.openyu.socklet.connector.vo.RelationConnector;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;

public class GenericRelationImplTest extends CoreTestSupporter
{

	@Test
	/**
	 * 啟動slave1
	 * 
	 * 啟動r1 on s1
	 *  
	 * 模擬s2 -> s1
	 */
	public void send()
	{
		//client -> slave1:4110
		JavaConnector r1 = new JavaConnectorImpl("TEST_ROLE_1", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4110);
		r1.setReceiver(receiver);
		r1.start();
		//
		String relationId = "slave1";
		GenericRelation initiativeRelation = new GenericRelationImpl(relationId);

		//salve2 -> slave1:4110
		//同時多連線連到server
		for (int i = 0, count = 1; i < count; i++)
		{
			String relationClientId = "slave2" + ":" + i + ":";
			RelationConnector relationClient = new RelationConnectorImpl(relationClientId
					+ "localhost:4110", CoreModuleType.class, CoreMessageType.class,
				protocolService, "localhost", 4110);
			relationClient.start();
			initiativeRelation.getClients().put(relationClient.getId(), relationClient);
		}

		//salve2 -> slave1:4111
		//同時多連線連到server
		for (int i = 0, count = 1; i < count; i++)
		{
			String relationClientId = "slave2" + ":" + i + ":";
			RelationConnector relationClient = new RelationConnectorImpl(relationClientId
					+ "localhost:4111", CoreModuleType.class, CoreMessageType.class,
				protocolService, "localhost", 4111);
			relationClient.start();
			initiativeRelation.getClients().put(relationClient.getId(), relationClient);
		}
		ThreadHelper.sleep(3 * 1000);

		//send,發送多訊息
		for (int i = 0, count = 10; i < count; i++)
		{
			Message message = messageService.createMessage(CoreModuleType.CHAT,
				CoreModuleType.CLIENT, CoreMessageType.CHAT_SAY_REPONSE, "TEST_ROLE_1");
			message.setSender("slave2");
			message.setCategoryType(CategoryType.MESSAGE_RELATION);
			//
			message.addInt(1);// 頻道類型
			message.addString("Hello world");// 聊天內容
			message.addString("<hr/>");// html
			initiativeRelation.send(message);
		}

		//send,發送多訊息,不過r2沒連s2,所以r2不會收到訊息
		for (int i = 0, count = 10; i < count; i++)
		{
			Message message = messageService.createMessage(CoreModuleType.CHAT,
				CoreModuleType.CLIENT, CoreMessageType.CHAT_SAY_REPONSE, "TEST_ROLE_2");
			message.setSender("slave2");
			message.setCategoryType(CategoryType.MESSAGE_RELATION);
			//
			message.addInt(1);// 頻道類型
			message.addString("Hello world");// 聊天內容
			message.addString("<hr/>");// html
			initiativeRelation.send(message);
		}
		//
		ThreadHelper.sleep(3 * 1000);
		//from [slave2] MESSAGE_RELATION, (11453) CHAT_SAY_REPONSE, (11400) CHAT => (19900) CLIENT to [TEST_ROLE_1]

	}
}
