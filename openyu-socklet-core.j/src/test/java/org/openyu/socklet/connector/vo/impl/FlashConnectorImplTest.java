package org.openyu.socklet.connector.vo.impl;

import org.junit.Test;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.connector.vo.FlashConnector;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;

import org.openyu.socklet.message.vo.Message;

public class FlashConnectorImplTest extends CoreTestSupporter
{

	@Test
	public void start()
	{
		//client -> slave1:4110
		FlashConnector connector = new FlashConnectorImpl("TEST_ROLE_1", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4110);
		connector.setReceiver(receiver);
		connector.start();
		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void start3()
	{
		// --------------------------------------------------
		//client 1
		//client -> slave1:4110
		// --------------------------------------------------
		FlashConnector connector = new FlashConnectorImpl("TEST_ROLE_1", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4110);
		connector.setReceiver(receiver);
		connector.start();

		// --------------------------------------------------
		//client 2
		//client -> slave1:4110
		// --------------------------------------------------
		FlashConnector connector2 = new FlashConnectorImpl("TEST_ROLE_2", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4110);
		connector2.setReceiver(receiver);
		connector2.start();

		// --------------------------------------------------
		//client 3
		//client -> slave1:4110
		// --------------------------------------------------
		FlashConnector connector3 = new FlashConnectorImpl("TEST_ROLE_3", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4110);
		connector.setReceiver(receiver);
		connector3.start();

		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void startStress()
	{
		for (int i = 0; i < 10; i++)
		{
			//client -> slave1:4110
			FlashConnector connector = new FlashConnectorImpl("TEST_ROLE_" + i, CoreModuleType.class,
				CoreMessageType.class, protocolService, "localhost", 4110);
			connector.setReceiver(receiver);
			connector.start();
			ThreadHelper.sleep(50);
		}
		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void send()
	{
		// --------------------------------------------------
		//client 1
		//client -> slave1:4110
		// --------------------------------------------------
		FlashConnector connector = new FlashConnectorImpl("TEST_ROLE_1", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4110);
		connector.setReceiver(receiver);
		connector.start();

		// --------------------------------------------------
		//message 1, play請求
		// --------------------------------------------------
		Message message = messageService.createClient(connector.getId(),
			CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
		message.addInt(1);//0, 玩幾次,//bytes.length=4
		//
		connector.send(message);

		// --------------------------------------------------
		//message 2, 獎勵公告區請求
		// --------------------------------------------------
		message = messageService.createClient(connector.getId(),
			CoreMessageType.FOUR_SYMBOL_REWARD_BOARD_REQUEST);
		//
		connector.send(message);

		// --------------------------------------------------
		//message 3, 單擊放入包包請求,當server未實做時,server會出現error訊息
		// --------------------------------------------------
		message = messageService.createClient(connector.getId(),
			CoreMessageType.FOUR_SYMBOL_PUT_ONE_IN_BAG_REQUEST);
		//
		connector.send(message);

		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void send2()
	{
		// --------------------------------------------------
		//client 1
		//client -> slave1:4110
		// --------------------------------------------------
		FlashConnectorImpl connector = new FlashConnectorImpl("TEST_ROLE_1", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4110);
		connector.setReceiver(receiver);
		connector.start();

		// --------------------------------------------------
		//message 1
		// --------------------------------------------------
		Message message = messageService.createClient(connector.getId(),
			CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
		message.addInt(1);//0, 玩幾次,//bytes.length=4
		//
		connector.send(message);

		// --------------------------------------------------
		//client 2
		//client -> slave1:4111
		// --------------------------------------------------
		FlashConnectorImpl connector2 = new FlashConnectorImpl("TEST_ROLE_2", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4111);
		connector2.setReceiver(receiver);
		connector2.start();

		// --------------------------------------------------
		//message 1
		// --------------------------------------------------
		message = messageService.createClient(connector2.getId(),
			CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
		message.addInt(10);//0, 玩幾次,//bytes.length=4
		//
		connector2.send(message);
		//
		ThreadHelper.sleep(3 * 1000);
	}
}
