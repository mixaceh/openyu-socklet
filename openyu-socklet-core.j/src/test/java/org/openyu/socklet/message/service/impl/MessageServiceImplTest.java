package org.openyu.socklet.message.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.vo.Message;

public class MessageServiceImplTest extends CoreTestSupporter
{

	@Test
	//client->server
	public void createClient()
	{
		final String ROLE_CODE = "TEST_ROLE";
		Message result = null;
		//
		int count = 1000000;
		long beg = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			//from [TEST_ROLE] MESSAGE_CLIENT, (21003) FOUR_SYMBOL_PLAY_REQUEST to []
			//1
			result = messageService.createClient(ROLE_CODE,
				CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
			result.addInt(1);

		}
		long end = System.currentTimeMillis();
		System.out.println(count + " times: " + (end - beg) + " mills. ");
		//
		System.out.println(result);
		assertNotNull(result);
	}

	//	@Test
	//	//server->relation
	//	public void createRelation()
	//	{
	//		final String ROLE_CODE = "TEST_ROLE";
	//		Message result = null;
	//		//
	//		int count = 1000000;
	//		long beg = System.currentTimeMillis();
	//		for (int i = 0; i < count; i++)
	//		{
	//			//from [slave2] MESSAGE_RELATION, (11453) CHAT_SAY_REPONSE, (11400) CHAT => (19900) CLIENT to [TEST_ROLE]
	//			//1, Hello world, <hr/>
	//			result = messageService.createRelation("slave2", CoreModuleType.CHAT,
	//				CoreModuleType.CLIENT, CoreMessageType.CHAT_SAY_REPONSE, ROLE_CODE);
	//			result.addInt(1);// 頻道類型
	//			result.addString("Hello world");// 聊天內容
	//			result.addString("<hr/>");// html
	//		}
	//		long end = System.currentTimeMillis();
	//		System.out.println(count + " times: " + (end - beg) + " mills. ");
	//		//
	//		System.out.println(result);
	//		assertNotNull(result);
	//	}

	@Test
	//server->client
	public void createMessage2Client()
	{
		final String ROLE_CODE = "TEST_ROLE";
		Message result = null;
		//
		int count = 1000000;
		long beg = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			//此時還沒有sender, 在acceptorServer上才會設定
			//from [null] MESSAGE_SERVER, (11251) ACCOUNT_COIN_REPONSE, (11200) ACCOUNT => (19900) CLIENT to [TEST_ROLE]
			//1000
			result = messageService.createMessage(CoreModuleType.ACCOUNT, CoreModuleType.CLIENT,
				CoreMessageType.ACCOUNT_COIN_REPONSE, ROLE_CODE);
			result.addInt(1000);
		}

		long end = System.currentTimeMillis();
		System.out.println(count + " times: " + (end - beg) + " mills. ");
		//
		System.out.println(result);
		assertNotNull(result);
	}

	@Test
	//server->server
	public void createMessage2Server()
	{
		final String ROLE_CODE = "TEST_ROLE";
		Message result = null;
		//
		int count = 1000000;
		long beg = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			//仿client發訊息,其實是從server的 CLIENT 模組,發給 CORE 模組
			//此時還沒有sender, 在acceptorServer上才會設定
			//from [null] MESSAGE_SERVER, (11001) CORE_ROLE_CONNECT_REQUEST, (19900) CLIENT => (11000) CORE to []
			//TEST_ROLE
			result = messageService.createMessage(CoreModuleType.CLIENT, CoreModuleType.CORE,
				CoreMessageType.CORE_CONNECT_REQUEST, (String) null);
			result.addString(ROLE_CODE);
		}

		long end = System.currentTimeMillis();
		System.out.println(count + " times: " + (end - beg) + " mills. ");
		//
		System.out.println(result);
		assertNotNull(result);
	}

	@Test
	//server->client
	public void addMessage2Client()
	{
		final String ROLE_CODE = "TEST_ROLE";
		boolean result = false;
		//
		int count = 10;
		long beg = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			//from [null] MESSAGE_SERVER, (21053) FOUR_SYMBOL_PLAY_RESPONSE, (21000) FOUR_SYMBOL => (19900) CLIENT to [TEST_ROLE]
			//0
			Message message = messageService.createMessage(CoreModuleType.FOUR_SYMBOL,
				CoreModuleType.CLIENT, CoreMessageType.FOUR_SYMBOL_PLAY_RESPONSE, ROLE_CODE);
			message.addInt(0);
			result = messageService.addMessage(message);
		}
		long end = System.currentTimeMillis();
		System.out.println(count + " times: " + (end - beg) + " mills. ");

		System.out.println(result);
		assertTrue(result);
	}

	//	@Test
	//	public void addMessage2Relation()
	//	{
	//		final String ROLE_CODE = "TEST_ROLE";
	//		boolean result = false;
	//		//
	//		int count = 10;
	//		long beg = System.currentTimeMillis();
	//		for (int i = 0; i < count; i++)
	//		{
	//			//from [slave2] MESSAGE_RELATION, (11453) CHAT_SAY_REPONSE, (11400) CHAT => (19900) CLIENT to [TEST_ROLE]
	//			//1, Hello world, <hr/>
	//			Message message = messageService.createRelation("slave2", CoreModuleType.CHAT,
	//				CoreModuleType.CLIENT, CoreMessageType.CHAT_SAY_REPONSE, ROLE_CODE);
	//			message.addInt(1);// 頻道類型
	//			message.addString("Hello world");// 聊天內容
	//			message.addString("<hr/>");// html
	//			result = messageService.addMessage(message);
	//		}
	//		long end = System.currentTimeMillis();
	//		System.out.println(count + " times: " + (end - beg) + " mills. ");
	//
	//		System.out.println(result);
	//		assertTrue(result);
	//		//
	//		assertEquals(count, messageService.getSendClients().size());
	//	}

	@Test
	// server->server
	public void addMessage2Server()
	{
		final String ROLE_CODE = "TEST_ROLE";
		boolean result = false;
		//
		int count = 10;
		long beg = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			//仿client發訊息,其實是從CLIENT模組,發給CORE模組
			//from [null] MESSAGE_SERVER, (11001) CORE_ROLE_CONNECT_REQUEST, (19900) CLIENT => (11000) CORE to []
			//TEST_ROLE
			Message message = messageService.createMessage(CoreModuleType.CLIENT,
				CoreModuleType.CORE, CoreMessageType.CORE_CONNECT_REQUEST, (String) null);
			message.addString(ROLE_CODE);
			result = messageService.addMessage(message);

		}
		long end = System.currentTimeMillis();
		System.out.println(count + " times: " + (end - beg) + " mills. ");

		System.out.println(result);
		assertTrue(result);
	}

}
