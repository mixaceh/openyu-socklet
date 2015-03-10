package org.openyu.socklet.connector.control.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.connector.service.ClientService;
import org.openyu.socklet.connector.control.ClientControl;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.Message;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClientControlImplTest extends BaseTestSupporter {

	protected static MessageService messageService;

	protected static ProtocolService protocolService;

	protected static ClientControl clientControl;

	protected static ClientService clientService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "org/openyu/socklet/bootstrap/client/applicationContext-client1.xml",//
				});
		messageService = (MessageService) applicationContext
				.getBean("messageService");
		protocolService = (ProtocolService) applicationContext
				.getBean("protocolService");
		//
		clientControl = (ClientControl) applicationContext
				.getBean("clientControl");
		clientService = (ClientService) applicationContext
				.getBean("clientService");
	}

	// --------------------------------------------------

	public static class BeanTest extends ClientControlImplTest {

		@Test
		public void messageService() {
			System.out.println(messageService);
			assertNotNull(messageService);
		}

		@Test
		public void protocolService() {
			System.out.println(protocolService);
			assertNotNull(protocolService);
		}

		@Test
		public void clientControl() {
			System.out.println(clientControl);
			assertNotNull(clientControl);
		}

		@Test
		public void clientService() {
			System.out.println(clientService);
			assertNotNull(clientService);
		}
	}

	@Test
	public void setVisible() {
		clientControl.setVisible(true);
		//
		ThreadHelper.sleep(30 * 1000);
	}

	@Test
	public void send() {
		final String ROLE_ID = "TEST_ROLE";
		clientControl.setVisible(true);
		ThreadHelper.sleep(2 * 1000);
		//
		for (int i = 0; i < 3; i++) {
			Message message = messageService.createClient(ROLE_ID,
					CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
			message.addInt(1);// 0, 玩幾次,//bytes.length=4
			clientService.addMessage(message);
		}
		//
		ThreadHelper.sleep(30 * 1000);
	}

}
