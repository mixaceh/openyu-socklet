package org.openyu.socklet.connector.vo.impl;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.connector.vo.JavaConnector;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.vo.Message;

public class JavaConnectorImplTest extends CoreTestSupporter {

	@Test
	public void start() {
		// client -> slave1:10300
		JavaConnector javaConnector = new JavaConnectorImpl("TEST_ROLE_1",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 10300);
		javaConnector.setReceiver(receiver);
		javaConnector.setRetryNumber(3);
		javaConnector.start();
		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void startSameServer() {
		// --------------------------------------------------
		// client 1
		// client -> slave1:10300
		// --------------------------------------------------
		JavaConnector javaConnector = new JavaConnectorImpl("TEST_ROLE_1",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 10300);
		javaConnector.setReceiver(receiver);
		javaConnector.start();

		// --------------------------------------------------
		// client 2
		// client -> slave1:10300
		// --------------------------------------------------
		JavaConnectorImpl javaConnector2 = new JavaConnectorImpl("TEST_ROLE_2",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 10300);
		javaConnector2.setReceiver(receiver);
		javaConnector2.start();

		// --------------------------------------------------
		// client 3
		// client -> slave1:10300
		// --------------------------------------------------
		JavaConnectorImpl javaConnector3 = new JavaConnectorImpl("TEST_ROLE_3",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 10300);
		javaConnector3.setReceiver(receiver);
		javaConnector3.start();
		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void mockStart() {
		String name = Thread.currentThread().getName();
		// client -> slave1:10300
		JavaConnector javaConnector = new JavaConnectorImpl(
				"TEST_ROLE_" + name, CoreModuleType.class,
				CoreMessageType.class, protocolService, "localhost", 10300);
		javaConnector.setReceiver(receiver);
		javaConnector.start();
		System.out.println("[" + name + "]");
		//
		ThreadHelper.sleep(5 * 60 * 1000);
	}

	@Test
	public void mocktStartWithMultiThread() {
		// 80,110,130,290
		// mem=512m 735
		// mem=768m 950
		// client -> slave1:10300
		for (int i = 0; i < 500; i++) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					mockStart();
				}
			});
			thread.setName("T-" + i);
			thread.start();
			//
			ThreadHelper.sleep(50);
		}
		//
		ThreadHelper.sleep(5 * 60 * 1000);
	}

	@Test
	public void send() {
		// --------------------------------------------------
		// client 1
		// client -> slave1:10300
		// --------------------------------------------------
		JavaConnector javaConnector = new JavaConnectorImpl("TEST_ROLE_1",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 10300);
		javaConnector.setReceiver(receiver);
		javaConnector.start();
		// --------------------------------------------------
		ThreadHelper.sleep(3 * 1000);

		// --------------------------------------------------
		// message 1, play請求
		// --------------------------------------------------
		Message message = messageService.createClient(javaConnector.getId(),
				CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
		message.addInt(1);// 0, 玩幾次,//bytes.length=4
		//
		int sended = javaConnector.send(message);
		System.out.println("sended: " + sended);
		// --------------------------------------------------
		// message 2, 獎勵公告區請求
		// --------------------------------------------------
		message = messageService.createClient(javaConnector.getId(),
				CoreMessageType.FOUR_SYMBOL_REWARD_BOARD_REQUEST);
		//
		javaConnector.send(message);
		System.out.println("sended: " + sended);
		// --------------------------------------------------
		// message 3, 單擊放入包包請求,當server未實作時,server會出現error訊息
		// --------------------------------------------------
		message = messageService.createClient(javaConnector.getId(),
				CoreMessageType.FOUR_SYMBOL_PUT_ONE_IN_BAG_REQUEST);
		//
		javaConnector.send(message);
		System.out.println("sended: " + sended);
		//
		ThreadHelper.sleep(5 * 1000);
	}

	@Test
	public void sendToMultiServer() {
		// --------------------------------------------------
		// client 1
		// client -> slave1:10300
		// --------------------------------------------------
		JavaConnector javaConnector = new JavaConnectorImpl("TEST_ROLE_1",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 10300);
		javaConnector.setReceiver(receiver);
		javaConnector.start();
		ThreadHelper.sleep(3 * 1000);

		// --------------------------------------------------
		// message 1
		// --------------------------------------------------
		Message message = messageService.createClient(javaConnector.getId(),
				CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
		message.addInt(1);// 0, 玩幾次,//bytes.length=4
		//
		int sended = javaConnector.send(message);
		System.out.println("sended: " + sended);
		// --------------------------------------------------
		// client 2
		// client -> slave1:4111
		// --------------------------------------------------
		JavaConnector javaConnector2 = new JavaConnectorImpl("TEST_ROLE_2",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 4111);
		javaConnector2.setReceiver(receiver);
		javaConnector2.start();
		ThreadHelper.sleep(3 * 1000);

		// --------------------------------------------------
		// message 1
		// --------------------------------------------------
		message = messageService.createClient(javaConnector2.getId(),
				CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
		message.addInt(10);// 0, 玩幾次,//bytes.length=4
		//
		sended = javaConnector2.send(message);
		System.out.println("sended: " + sended);
		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	public void startDiffServer() {
		// --------------------------------------------------
		// client 1
		// client -> slave1:10300
		// --------------------------------------------------
		JavaConnector javaConnector = new JavaConnectorImpl("TEST_ROLE_1",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 10300);
		javaConnector.start();

		// --------------------------------------------------
		// client 2
		// client -> slave2:4120
		// --------------------------------------------------
		JavaConnector javaConnector2 = new JavaConnectorImpl("TEST_ROLE_2",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 4120);
		javaConnector2.start();

		// --------------------------------------------------
		// client 3
		// client -> slave3:4130
		// --------------------------------------------------
		JavaConnector javaConnector3 = new JavaConnectorImpl("TEST_ROLE_3",
				CoreModuleType.class, CoreMessageType.class, protocolService,
				"localhost", 4130);
		javaConnector3.start();
	}
}
