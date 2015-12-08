package org.openyu.socklet.core;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.thread.ThreadService;
import org.openyu.socklet.account.net.socklet.AccountSocklet;
import org.openyu.socklet.account.service.AccountService;
import org.openyu.socklet.chat.net.socklet.ChatSocklet;
import org.openyu.socklet.chat.service.ChatService;
import org.openyu.socklet.connector.vo.JavaConnector;
import org.openyu.socklet.connector.vo.impl.JavaConnectorImpl;
import org.openyu.socklet.connector.vo.supporter.GenericReceiverSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.core.net.socklet.CoreSocklet;
import org.openyu.socklet.core.service.CoreService;
import org.openyu.socklet.fourSymbol.net.socklet.FourSymbolSocklet;
import org.openyu.socklet.fourSymbol.service.FourSymbolService;
import org.openyu.socklet.login.net.socklet.LoginSocklet;
import org.openyu.socklet.login.service.LoginService;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.vo.Message;

public class CoreTestSupporter extends BaseTestSupporter {

	protected static ThreadService threadService;

	protected static MessageService messageService;

	protected static ProtocolService protocolService;

	protected static JavaConnector javaConnector;

	/**
	 * 訊息接收者
	 */
	protected static CoreMessageReceiver receiver = new CoreMessageReceiver();

	// acount server
	protected static AccountService accountService;

	protected static AccountSocklet accountSocklet;

	// login server
	protected static LoginService loginService;

	protected static LoginSocklet loginSocklet;

	// slave server
	protected static CoreService coreService;

	protected static CoreSocklet coreSocklet;

	protected static ChatService chatService;

	protected static ChatSocklet chatSocklet;

	protected static FourSymbolService fourSymbolService;

	protected static FourSymbolSocklet fourSymbolSocklet;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] {
				"applicationContext-init.xml",//
				"applicationContext-bean.xml", //
				"applicationContext-message.xml", //
//				"testContext-thread.xml", //
//				"testContext-security.xml", //
//				"org/openyu/socklet/message/testContext-message.xml", //
				"applicationContext-acceptor.xml",//
				//biz
				"testContext-service.xml",//
		});
		threadService = (ThreadService) applicationContext
				.getBean("threadService");
		messageService = (MessageService) applicationContext
				.getBean("messageService");
		protocolService = (ProtocolService) applicationContext
				.getBean("protocolService");

		// ---------------------------------------------------
		// client
		// ---------------------------------------------------
		javaConnector = new JavaConnectorImpl(CoreModuleType.class,
				CoreMessageType.class, protocolService);
		// javaConnector.setThreadService(threadService);
		javaConnector.setReceiver(receiver);
		//
		accountService = (AccountService) applicationContext
				.getBean("accountService");
		accountSocklet = (AccountSocklet) applicationContext
				.getBean("accountSocklet");
		//
		loginService = (LoginService) applicationContext
				.getBean("loginService");
		loginSocklet = (LoginSocklet) applicationContext
				.getBean("loginSocklet");
		//
		coreService = (CoreService) applicationContext.getBean("coreService");
		coreSocklet = (CoreSocklet) applicationContext.getBean("coreSocklet");
		//
		chatService = (ChatService) applicationContext.getBean("chatService");
		chatSocklet = (ChatSocklet) applicationContext.getBean("chatSocklet");
		//
		fourSymbolService = (FourSymbolService) applicationContext
				.getBean("fourSymbolService");
		fourSymbolSocklet = (FourSymbolSocklet) applicationContext
				.getBean("fourSymbolSocklet");

	}

	// --------------------------------------------------

	public static class BeanTest extends CoreTestSupporter {

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

		// --------------------------------------------------

		@Test
		public void accountService() {
			System.out.println(accountService);
			assertNotNull(accountService);
		}

		@Test
		public void accountSocklet() {
			System.out.println(accountSocklet);
			assertNotNull(accountSocklet);
		}

		// --------------------------------------------------

		@Test
		public void loginService() {
			System.out.println(loginService);
			assertNotNull(loginService);
		}

		@Test
		public void loginSocklet() {
			System.out.println(loginSocklet);
			assertNotNull(loginSocklet);
		}

		// --------------------------------------------------
		@Test
		public void coreService() {
			System.out.println(coreService);
			assertNotNull(coreService);
		}

		@Test
		public void coreSocklet() {
			System.out.println(coreSocklet);
			assertNotNull(coreSocklet);
		}

		@Test
		public void chatService() {
			System.out.println(chatService);
			assertNotNull(chatService);
		}

		@Test
		public void chatSocklet() {
			System.out.println(chatSocklet);
			assertNotNull(chatSocklet);
		}

		@Test
		public void fourSymbolService() {
			System.out.println(fourSymbolService);
			assertNotNull(fourSymbolService);
		}

		@Test
		public void fourSymbolSocklet() {
			System.out.println(fourSymbolSocklet);
			assertNotNull(fourSymbolSocklet);
		}
	}

	// public static void printMessage(Queue<Message> messages)
	// {
	// for (Message message : messages)
	// {
	// printMessage(message);
	// }
	// }
	//
	// public static void printMessage(Message message)
	// {
	// System.out.println(message);
	// //
	// StringBuilder content = new StringBuilder();
	// List<byte[]> contents = message.getContents();
	// for (int i = 0; i < contents.size(); i++)
	// {
	// Object object = null;
	// //
	// Class<?> clazz = message.getClass(i);
	// if (String.class.equals(clazz))
	// {
	// object = message.getString(i);
	// }
	// else if (Integer.class.equals(clazz))
	// {
	// object = message.getInt(i);
	// }
	// else if (Long.class.equals(clazz))
	// {
	// object = message.getLong(i);
	// }
	// else if (Float.class.equals(clazz))
	// {
	// object = message.getFloat(i);
	// }
	// else if (Double.class.equals(clazz))
	// {
	// object = message.getDouble(i);
	// }
	// else if (byte[].class.equals(clazz))
	// {
	// object = message.getBytes(i);
	// }
	// //
	// content.append(object);
	// if (i < contents.size() - 1)
	// {
	// content.append(StringHelper.COMMA + " ");
	// }
	// }
	// //
	// // log.info(content.toString());
	// System.out.println(content.toString());
	// }

	public static class CoreMessageReceiver extends GenericReceiverSupporter {

		private static final long serialVersionUID = 9025630102068228806L;

		public void receive(Message message) {
			System.out.println(message);
		}
	}

}
