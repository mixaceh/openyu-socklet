package org.openyu.socklet.message.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.vo.Message;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

public class MessageServiceImplTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	private static MessageService messageService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"org/openyu/socklet/message/testContext-message.xml",//
		});

		messageService = (MessageServiceImpl) applicationContext.getBean("messageService");
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0, concurrency = 1)
	public void messageServiceImpl() {
		System.out.println(messageService);
		assertNotNull(messageService);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void close() {
		System.out.println(messageService);
		assertNotNull(messageService);
		applicationContext.close();
		// 多次,不會丟出ex
		applicationContext.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void refresh() {
		System.out.println(messageService);
		assertNotNull(messageService);
		applicationContext.refresh();
		// 多次,不會丟出ex
		applicationContext.refresh();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// client->server
	public void createClient() {
		final String ROLE_CODE = "TEST_ROLE";
		Message result = null;
		//
		// from [TEST_ROLE] MESSAGE_CLIENT, (21003) FOUR_SYMBOL_PLAY_REQUEST
		// to []
		// 1
		result = messageService.createClient(ROLE_CODE, CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
		result.addInt(1);
		//
		System.out.println(result);
		assertNotNull(result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// server->client
	public void createMessageToClient() {
		final String ROLE_CODE = "TEST_ROLE";
		Message result = null;
		//
		// 此時還沒有sender, 在acceptorServer上才會設定
		// from [null] MESSAGE_SERVER, (11251) ACCOUNT_COIN_REPONSE, (11200)
		// ACCOUNT => (19900) CLIENT to [TEST_ROLE]
		// 1000
		result = messageService.createMessage(CoreModuleType.ACCOUNT, CoreModuleType.CLIENT,
				CoreMessageType.ACCOUNT_COIN_REPONSE, ROLE_CODE);
		result.addInt(1000);
		//
		System.out.println(result);
		assertNotNull(result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// server->server
	public void createMessageToServer() {
		final String ROLE_CODE = "TEST_ROLE";
		Message result = null;
		//
		// 仿client發訊息,其實是從server的 CLIENT 模組,發給 CORE 模組
		// 此時還沒有sender, 在acceptorServer上才會設定
		// from [null] MESSAGE_SERVER, (11001) CORE_ROLE_CONNECT_REQUEST,
		// (19900) CLIENT => (11000) CORE to []
		// TEST_ROLE
		result = messageService.createMessage(CoreModuleType.CLIENT, CoreModuleType.CORE,
				CoreMessageType.CORE_CONNECT_REQUEST, (String) null);
		result.addString(ROLE_CODE);
		//
		System.out.println(result);
		assertNotNull(result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// server->client
	public void addMessageToClient() {
		final String ROLE_CODE = "TEST_ROLE";
		boolean result = false;
		//
		// from [null] MESSAGE_SERVER, (21053) FOUR_SYMBOL_PLAY_RESPONSE,
		// (21000) FOUR_SYMBOL => (19900) CLIENT to [TEST_ROLE]
		// 0
		Message message = messageService.createMessage(CoreModuleType.FOUR_SYMBOL, CoreModuleType.CLIENT,
				CoreMessageType.FOUR_SYMBOL_PLAY_RESPONSE, ROLE_CODE);
		message.addInt(0);
		result = messageService.addMessage(message);
		//
		System.out.println(result);
		assertTrue(result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// server->server
	public void addMessageToServer() {
		final String ROLE_CODE = "TEST_ROLE";
		boolean result = false;
		//
		// 仿client發訊息,其實是從CLIENT模組,發給CORE模組
		// from [null] MESSAGE_SERVER, (11001) CORE_ROLE_CONNECT_REQUEST,
		// (19900) CLIENT => (11000) CORE to []
		// TEST_ROLE
		Message message = messageService.createMessage(CoreModuleType.CLIENT, CoreModuleType.CORE,
				CoreMessageType.CORE_CONNECT_REQUEST, (String) null);
		message.addString(ROLE_CODE);
		result = messageService.addMessage(message);
		//
		System.out.println(result);
		assertTrue(result);
	}
}
