package org.openyu.socklet.message.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;

import org.openyu.commons.io.IoHelper;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.lang.SystemHelper;
import org.openyu.commons.util.ConfigHelper;
import org.openyu.socklet.acceptor.net.socklet.AcceptorMessageType;
import org.openyu.socklet.acceptor.net.socklet.AcceptorModuleType;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.HeadType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.Packet;
import org.openyu.socklet.message.vo.PriorityType;
import org.openyu.socklet.message.vo.impl.MessageImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@AxisRange(min = 0, max = 1)
@BenchmarkMethodChart(filePrefix = "benchmark/org.openyu.socklet.message.service.impl.ProtocolServiceImplTest")
@BenchmarkHistoryChart(filePrefix = "benchmark/org.openyu.socklet.message.service.impl.ProtocolServiceImplTest")
public class ProtocolServiceImplTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	private static MessageService messageService;

	private static ProtocolService protocolService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"org/openyu/socklet/message/testContext-message.xml",//
		});

		messageService = (MessageServiceImpl) applicationContext.getBean("messageService");
		protocolService = (ProtocolServiceImpl) applicationContext.getBean("protocolService");
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0, concurrency = 1)
	public void protocolServiceImpl() {
		System.out.println(protocolService);
		assertNotNull(protocolService);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void close() {
		System.out.println(protocolService);
		assertNotNull(protocolService);
		applicationContext.close();
		// 多次,不會丟出ex
		applicationContext.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void refresh() {
		System.out.println(protocolService);
		assertNotNull(protocolService);
		applicationContext.refresh();
		// 多次,不會丟出ex
		applicationContext.refresh();
	}

	/**
	 * 模擬client發送的訊息
	 * 
	 * @return
	 */
	public static Message mockClientToServer() {
		final String ROLE_ID = "TEST_ROLE";// bytes.length=9
		Message result = messageService.createClient(ROLE_ID, CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
		result.addInt(1);// 0, 玩幾次
		return result;
	}

	/**
	 * 模擬server發送client的訊息
	 * 
	 * @return
	 */
	public static Message mockServerToClient() {
		final String ROLE_ID = "TEST_ROLE";// bytes.length=9
		List<String> receivers = new LinkedList<String>();
		receivers.add(ROLE_ID);
		receivers.add(ROLE_ID + "_1");

		Message result = messageService.createMessage(CoreModuleType.FOUR_SYMBOL, CoreModuleType.CLIENT,
				CoreMessageType.FOUR_SYMBOL_PLAY_RESPONSE, receivers);
		result.setSender("slave1");
		//
		result.addInt(0);// 0, errorCode 錯誤碼
		result.addInt(1);// 1, 每日已轉生次數

		// //塞個大的字串
		// StringBuilder buff = new StringBuilder();
		// //最大就65535,一個欄位 addString
		// for (int i = 0; i < 64 * 1024 - 1; i++)//32k,64k
		// {
		// buff.append('a');
		// }
		// //
		// //System.out.println("buff.length: " + buff.length());//65535
		// result.addString(buff.toString());
		// System.out.println(Short.MAX_VALUE);//32767
		return result;
	}

	/**
	 * 模擬server發送server的訊息
	 * 
	 * @return
	 */
	public static Message mockServerToServer() {
		List<String> receviers = new LinkedList<String>();
		receviers.add("TEST_ROLE");
		receviers.add("TEST_ROLE_2");
		Message result = messageService.createMessage(CoreModuleType.CORE, CoreModuleType.CHAT,
				CoreMessageType.CHAT_SAY_REQUEST, receviers);
		result.setSender("slave1");
		//
		result.addInt(1);// 頻道類型
		result.addString("Hello world");// 聊天內容
		result.addString("<hr/>");// html
		return result;
	}

	/**
	 * 模擬server發送relation的訊息
	 * 
	 * @return
	 */
	public static Message mockServerToRelation() {
		Message result = messageService.createMessage(CoreModuleType.CHAT, CoreModuleType.CLIENT,
				CoreMessageType.CHAT_SAY_REPONSE, "TEST_ROLE_1");

		result.setSender("slave2");
		result.setCategoryType(CategoryType.MESSAGE_RELATION);
		//
		result.addInt(1);// 頻道類型
		result.addString("Hello world");// 聊天內容
		result.addString("<hr/>");// html
		return result;
	}

	/**
	 * 模擬server發送relation的訊息
	 * 
	 * @return
	 */
	public static Message mockAcceptorToRelation() {
		Message result = new MessageImpl(CategoryType.MESSAGE_ACCEPTOR, PriorityType.URGENT);
		result.setMessageType(AcceptorMessageType.ACCEPTOR_SYNC_CLIENT_CONNECT_REQUEST);
		result.setSender("slave1");
		//
		result.addString("TEST_ROLE_1");
		result.addString("slave1");
		result.addByteArray(new byte[32]);
		result.addBoolean(true);
		return result;
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// #issue: use ByteArrayOutputStream
	// 1000000 times: 16034 mills.
	// round: 15.96, GC: 433
	//
	// #fix: use ByteBuffer
	// 1000000 times: 15663 mills.
	// round: 15.55, GC: 413
	//
	// #fix: use unsafe byte[]
	// 1000000 times: 16705 mills.
	// round: 16.69, GC: 410
	public void handshakeClient() throws Exception {
		String authKey = "aacc8964324738c27c07746e3ea81aff";
		String sender = "TEST_ROLE";
		byte[] result = null;
		//
		result = protocolService.handshake(CategoryType.HANDSHAKE_CLIENT, authKey.getBytes(), sender);
		//
		System.out.println("length: " + result.length);// 73
		SystemHelper.println(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 1, 0, 0, 0, 73, 65, 20, -68, 0, 0, 0, 56, -16, 41, 125, -54, -120, -92,
				78, -38, -122, 19, -64, 16, 39, -1, -31, -90, -24, 100, -72, -83, -29, -6, 45, -54, -18, -117, 71, 91,
				-67, -84, 95, -28, -29, -52, -85, -117, -114, 90, 117, -120, -64, -86, 92, 96, 46, 31, 81, 126, 109,
				-112, -19, -12, -70, 14, -56, 17, 35, -122 };

		Assert.assertArrayEquals(expecteds, result);

		// 輸出成ser檔
		// OutputStream out =
		// IoHelper.createOutputStream(ConfigHelper.getSerDir()
		// + "/handshakeClient.ser");
		// out.write(result);
		// out.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// #issue: use ByteArrayOutputStream
	// 1000000 times: 16351 mills.
	// round: 16.31, GC: 474
	//
	// #fix: use unsafe byte[]
	// 1000000 times: 16990 mills.
	// round: 17.02, GC: 474
	public void dehandshakeClient() {
		String AUTH_KEY = "aacc8964324738c27c07746e3ea81aff";
		String SENDER = "TEST_ROLE";

		byte[] value = protocolService.handshake(CategoryType.HANDSHAKE_CLIENT, AUTH_KEY.getBytes(), SENDER);
		//
		Message result = null;
		//
		result = protocolService.dehandshake(value);
		System.out.println(result);
		System.out.println(result.getSender());// TEST_ROLE_1
		//
		assertEquals(SENDER, result.getSender());
		assertEquals(AUTH_KEY, result.getString(0));
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// round: 0.04
	public void handshakeServer() {
		String authKey = "aacc8964324738c27c07746e3ea81aff";
		String sender = "slave1";
		byte[] result = null;
		//
		result = protocolService.handshake(CategoryType.HANDSHAKE_SERVER, authKey.getBytes(), sender);
		System.out.println("length: " + result.length);// 73
		SystemHelper.println(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 1, 0, 0, 0, 73, 65, 20, -68, 0, 0, 0, 56, -16, 41, 53, -112, -83, 4,
				98, 93, -84, -33, 84, -50, 105, 7, 52, -122, 53, 10, -51, -83, 93, -78, 119, -101, -23, 63, 122, -31,
				-7, -62, 48, -115, -98, -77, 123, -104, 118, 58, -69, -21, -101, 42, -74, 12, -111, -63, -13, 41, -120,
				-41, 110, 112, 102, 103, 54, 53, -60, 0 };

		Assert.assertArrayEquals(expecteds, result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// round: 0.05
	// 10000 times: 130 mills. 0_0_0
	// 10000 times: 765 mills. c_s_c
	public void dehandshakeServer() {
		String AUTH_KEY = "aacc8964324738c27c07746e3ea81aff";
		String SENDER = "slave1";

		byte[] value = protocolService.handshake(CategoryType.HANDSHAKE_SERVER, AUTH_KEY.getBytes(), SENDER);
		Message result = null;
		//
		result = protocolService.dehandshake(value);
		System.out.println(result.getSender());// slave1
		assertEquals(SENDER, result.getSender());
		assertEquals(AUTH_KEY, result.getString(0));
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// round: 0.09
	public void handshakeRelation() {
		String authKey = "aacc8964324738c27c07746e3ea81aff";
		String sender = "slave2";
		byte[] result = null;
		//
		result = protocolService.handshake(CategoryType.HANDSHAKE_RELATION, authKey.getBytes(), sender);
		System.out.println("length: " + result.length);
		SystemHelper.println(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 1, 0, 0, 0, 73, 65, 20, -68, 0, 0, 0, 56, -16, 41, 48, 69, -28, 43,
				-90, -56, -14, 109, 43, 19, -99, 4, -53, 24, -87, 40, -51, -83, 93, -78, 119, -101, -23, 63, 122, -31,
				-7, -62, 48, -115, -98, -77, 123, -104, 118, 58, -69, -21, -101, 42, 52, 30, 89, 0, -121, 114, -103, 64,
				110, 112, 102, 103, 54, 53, -60, 0 };

		Assert.assertArrayEquals(expecteds, result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// round: 0.05
	public void dehandshakeRelation() {
		String AUTH_KEY = "aacc8964324738c27c07746e3ea81aff";
		String SENDER = "slave2";

		byte[] value = protocolService.handshake(CategoryType.HANDSHAKE_RELATION, AUTH_KEY.getBytes(), SENDER);
		Message result = null;
		//
		result = protocolService.dehandshake(value);
		System.out.println(result);
		System.out.println(result.getCategoryType());
		System.out.println(result.getSender());// slave2

		assertEquals(SENDER, result.getSender());
		assertEquals(AUTH_KEY, result.getString(0));
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// round: 0.09
	public void handshakeRelationByteArray() {
		byte[] authKey = new byte[] { 48, 49, 97, 55, 54, 51, 99, 98, 97, 51, 100, 98, 54, 101, 50, 98, 51, 49, 54, 97,
				48, 98, 55, 98, 48, 102, 101, 54, 97, 52, 97, 48 };
		String sender = "slave2:0:127.0.0.1:3110";
		byte[] result = null;
		//
		result = protocolService.handshake(CategoryType.HANDSHAKE_RELATION, authKey, sender);
		System.out.println("length: " + result.length);
		SystemHelper.println(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 1, 0, 0, 0, 89, 81, 20, -68, 0, 0, 0, 72, -16, 57, -21, -121, 122,
				-114, -2, 42, -100, 8, -111, -100, 56, -9, -123, -126, -125, -69, 101, 40, -45, -72, 91, 95, -53, -65,
				125, -62, -54, 51, -95, -1, 87, -119, -15, -122, -110, 110, 47, 104, 52, -87, 113, 9, -35, 73, 68, -14,
				-118, -5, -36, 98, -74, 124, -59, 93, 122, 87, -26, -25, 116, 103, 75, 57, -2, -84, 101, -122, 90, 40,
				-25, -93, -69, 18 };

		Assert.assertArrayEquals(expecteds, result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 100)
	// round: 0.24 [+- 0.00], round.block: 0.00 [+- 0.00], round.gc: 0.00 [+-
	// 0.00], GC.calls: 0, GC.time: 0.00, time.total: 0.24, time.warmup: 0.00,
	// time.bench: 0.24

	// security+compress
	// round: 0.45 [+- 0.00], round.block: 0.29 [+- 0.02], round.gc: 0.00 [+-
	// 0.00], GC.calls: 1, GC.time: 0.01, time.total: 0.46, time.warmup: 0.00,
	// time.bench: 0.46
	public void assembleClientToServer() throws Exception {
		Message message = mockClientToServer();
		//
		byte[] result = null;
		//
		result = protocolService.assemble(message);
		System.out.println("length: " + result.length);
		SystemHelper.println(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 11, 0, 0, 0, 60, 0, 0, 0, 52, 9, 18, 0, 0, 0, 40, -16, 25, 102, -16,
				-90, 24, 124, -116, -61, 119, -102, 46, 76, -73, -76, -75, 24, -93, -57, 28, -102, -33, 103, 92, 125,
				33, -15, 87, 11, 108, -13, 18, -1, -35, -54, 25, -35, -25, -2, 58, 112, -70 };
		//
		Assert.assertArrayEquals(expecteds, result);

		// 輸出成ser檔
		// OutputStream out =
		// IoHelper.createOutputStream(ConfigHelper.getSerDir() +
		// "/assembleClientToServer.ser");
		// out.write(result);
		// out.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// round: 0.45 [+- 0.01], round.block: 0.29 [+- 0.02], round.gc: 0.00 [+-
	// 0.00], GC.calls: 1, GC.time: 0.01, time.total: 0.46, time.warmup: 0.00,
	// time.bench: 0.46
	public void disassembleClientToServer() {
		Message message = mockClientToServer();
		byte[] value = protocolService.assemble(message);
		// SystemHelper.println(value);
		//
		List<Message> result = null;
		//
		result = protocolService.disassemble(value, CoreModuleType.class, CoreMessageType.class);
		assertTrue(result.size() > 0);
		//
		for (Message entry : result) {
			System.out.println(entry);
			assertEquals(1, entry.getInt(0));// 0, 玩幾次
		}
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 10000 times: 5709 mills.
	public void assembleServerToClient() throws Exception {
		Message message = mockServerToClient();
		//
		byte[] result = null;
		//
		result = protocolService.assemble(message);
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
		assertNotNull(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 11, 0, 0, 0, 92, 0, 0, 0, 84, -68, 20, 0, 0, 0, 72, -16, 57, -104, -21,
				-25, 107, 32, -16, 9, -33, 15, 17, -54, 124, 47, -94, -65, 26, -73, 119, 101, -69, 91, -121, 87, -53,
				-57, -35, -23, -122, -68, -68, -108, -26, -127, 17, 73, 57, 65, -40, 36, -84, -46, -61, -93, -45, 1,
				-121, 64, 95, 34, 54, -121, -106, 71, -75, -30, -99, 124, 76, -4, -75, -20, 107, -34, 43, 36, -104,
				-102, -98, 33, 60, 40, 83 };

		Assert.assertArrayEquals(expecteds, result);

		// 輸出成ser檔
		// OutputStream out =
		// IoHelper.createOutputStream(ConfigHelper.getSerDir()
		// + "/assembleServerToClient.ser");
		// out.write(result);
		// out.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 10000 times: 1113 mills.
	public void disassembleServerToClient() {
		Message message = mockServerToClient();
		byte[] value = protocolService.assemble(message);
		//
		List<Message> result = null;
		//
		result = protocolService.disassemble(value, CoreModuleType.class, CoreMessageType.class);
		assertTrue(result.size() > 0);
		//
		for (Message entry : result) {
			System.out.println(entry);
			// assertNotNull(entry.getContents());
			System.out.println(entry.getInt(0));
			assertEquals(0, entry.getInt(0));// 0, errorCode 錯誤碼

			System.out.println(entry.getInt(1));
			assertEquals(1, entry.getInt(1));// 1, 每日已轉生次數
		}
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 10000 times: 3375 mills.
	public void assembleServerToServer() {
		Message message = mockServerToServer();
		//
		byte[] result = null;
		//
		result = protocolService.assemble(message);
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
		assertNotNull(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 11, 0, 0, 0, 108, 0, 0, 0, 100, -68, 20, 0, 0, 0, 88, -16, 73, 17, -21,
				-20, -86, -2, -39, 35, 46, -91, 63, 15, 93, -85, 58, 23, -36, -125, 108, -21, -20, 86, -78, -118, 46,
				-57, -35, -23, -122, -68, -68, -108, -26, -127, 17, 73, 57, 65, -40, 36, -84, 91, 9, -5, -24, 6, 41,
				-100, 19, 115, 19, -29, -78, 77, -87, -68, -92, -28, 57, 53, 102, 120, -16, -115, -126, -69, -81, 18,
				-34, -36, -57, -16, 78, -62, 20, -116, 78, -34, -29, 109, 119, -44, 50, -26, -44, 17, 94, 124, -35 };

		Assert.assertArrayEquals(expecteds, result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 10000 times: 768 mills.
	public void disassembleServerToServer() {
		Message message = mockServerToServer();
		byte[] value = protocolService.assemble(message);
		// SystemHelper.println(value);
		//
		List<Message> result = null;
		//
		result = protocolService.disassemble(value, CoreModuleType.class, CoreMessageType.class);
		assertNotNull(result);
		//
		for (Message entry : result) {
			System.out.println(entry);
			assertNotNull(entry.getContents());
			assertEquals(1, entry.getInt(0));// 0, 頻道類型
			assertEquals("Hello world", entry.getString(1));// 1, //聊天內容
			assertEquals("<hr/>", entry.getString(2));// 2, //html
		}
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 10000 times: 3276 mills.
	public void assembleServerToRelation() {
		Message message = mockServerToRelation();
		//
		byte[] result = null;
		//
		result = protocolService.assemble(message);
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
		assertNotNull(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 11, 0, 0, 0, 100, 0, 0, 0, 92, -68, 20, 0, 0, 0, 80, -16, 65, -125, 84,
				98, -42, -107, 18, -6, -65, -41, 87, 67, -59, -13, 63, 85, 97, -71, -42, 97, -35, 76, 5, -81, 6, -11,
				90, -21, 91, 68, 126, 122, 75, -26, -38, 75, -20, -125, 49, 104, -109, 112, -88, -17, -105, -106, -27,
				105, 8, 90, -65, -75, -16, 13, -11, 27, -99, -99, 96, -60, -119, -31, -30, 114, -30, -10, 121, 30, 54,
				99, -7, -124, -46, 63, -54, -21, -29, -100, 87, 24, -84 };

		Assert.assertArrayEquals(expecteds, result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 10000 times: 790 mills.
	public void disassembleServerToRelation() {
		Message message = mockServerToRelation();
		byte[] value = protocolService.assemble(message);
		//
		List<Message> result = null;
		//
		result = protocolService.disassemble(value, CoreModuleType.class, CoreMessageType.class);
		assertNotNull(result);
		//
		for (Message entry : result) {
			System.out.println(entry);
			assertNotNull(entry.getContents());
			assertEquals(1, entry.getInt(0));// 0, 頻道類型
			assertEquals("Hello world", entry.getString(1));// 1, //聊天內容
			assertEquals("<hr/>", entry.getString(2));// 2, //html
		}
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 10000 times: 792 mills.
	public void assembleAcceptorToRelation() {
		Message message = mockAcceptorToRelation();
		//
		byte[] result = null;
		//
		result = protocolService.assemble(message);
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
		assertNotNull(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 11, 0, 0, 0, 96, 0, 0, 0, 88, -68, 20, 0, 0, 0, 88, -4, 41, 103, 121,
				-85, 10, -14, 2, -81, 110, -21, -33, 108, -48, -35, 78, 63, -37, -11, -44, 10, -120, 48, 70, -125, 40,
				-102, 46, 76, -73, -76, -75, 24, -93, 82, 96, 5, -6, -87, 108, -62, -111, -19, 123, -7, 103, -60, -125,
				-27, 50, -67, -45, 54, 7, 67, 76, -61, -18, 8, 0, -16, 1, 66, 17, -55, -75, -128, 15, -60, -58, -124,
				-73, -103, -92, -105, -117, -81, 10 };

		Assert.assertArrayEquals(expecteds, result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 10000 times: 766 mills.
	public void disassembleAcceptorToRelation() {
		Message message = mockAcceptorToRelation();
		byte[] value = protocolService.assemble(message);
		// SystemHelper.println(value);
		//
		List<Message> result = null;
		//
		result = protocolService.disassemble(value, AcceptorModuleType.class, AcceptorMessageType.class);
		assertNotNull(result);
		//
		for (Message entry : result) {
			System.out.println(entry);
			assertNotNull(entry.getContents());
			assertEquals("TEST_ROLE_1", entry.getString(0));// 0, sender
			assertEquals("slave1", entry.getString(1));// 1, acceptor
			assertEquals(32, entry.getByteArray(2).length);// 2, byte[32]
			assertEquals(true, entry.getBoolean(3));// 3, true
		}
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// round: 0.06, GC: 2
	// round: 0.05, GC: 2
	public void encode() {
		byte[] result = null;
		//
		result = protocolService.encode(HeadType.HANDSHAKE);
		// 0, 0, 0, 1, 0, 0, 0, 12, 0, 0, 0, 1
		System.out.println("length: " + result.length);// 12
		SystemHelper.println(result);
		assertNotNull(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 1, 0, 0, 0, 12, 0, 0, 0, 1 };

		Assert.assertArrayEquals(expecteds, result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// round: 0.06, GC: 2
	// round: 0.05, GC: 2
	public void decode() {
		byte[] value = protocolService.encode(HeadType.HANDSHAKE);
		Packet<byte[]> result = null;
		//
		result = protocolService.decode(value);
		System.out.println(result);
		//
		assertEquals(HeadType.HANDSHAKE, result.getHeadType());
		assertEquals(12, result.getLength());

		byte[] expecteds = new byte[] { 0, 0, 0, 1, 0, 0, 0, 12, 0, 0, 0, 1 };

		Assert.assertArrayEquals(expecteds, result.getBody());
	}
}

// @Test
// //1000000 times: 736 mills.
// //1000000 times: 710 mills.
// //1000000 times: 703 mills.
// public void sync()
// {
// String authKey = "aacc8964324738c27c07746e3ea81aff";
// boolean handshake = true;
// String sender = "TEST_ROLE_1";
// String acceptor = "slave2";
// byte[] result = null;
// //
// int COUNT = 1000000;
// long beg = System.currentTimeMillis();
// for (int i = 0; i < COUNT; i++)
// {
// result = protocolServiceImpl.sync(CategoryType.SYNC_CONNECT,
// authKey.getBytes(),
// handshake,
// sender, acceptor);
//
// }
// long end = System.currentTimeMillis();
//
//
// System.out.println(result.length);
// SystemHelper.println(result);
// }
//
// @Test
// //1000000 times: 619 mills.
// //1000000 times: 682 mills.
// //1000000 times: 635 mills.
// public void disSync()
// {
// String authKey = "aacc8964324738c27c07746e3ea81aff";
// boolean handshake = true;
// String sender = "TEST_ROLE_1";
// String acceptor = "slave2";
// byte[] value = protocolServiceImpl.sync(CategoryType.SYNC_CONNECT,
// authKey.getBytes(),
// handshake, sender, acceptor);
// //
// Message result = null;
// //
// int COUNT = 1000000;
// long beg = System.currentTimeMillis();
// for (int i = 0; i < COUNT; i++)
// {
// result = protocolServiceImpl.disSync(value);
//
// }
// long end = System.currentTimeMillis();
//
//
// printMessage(result);
// assertEquals(sender, result.getSender());
// }
