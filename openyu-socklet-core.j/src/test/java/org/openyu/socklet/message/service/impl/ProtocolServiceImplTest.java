package org.openyu.socklet.message.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;

import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.lang.SystemHelper;
import org.openyu.socklet.acceptor.net.socklet.AcceptorMessageType;
import org.openyu.socklet.acceptor.net.socklet.AcceptorModuleType;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.HeadType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.PriorityType;
import org.openyu.socklet.message.vo.impl.MessageImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@AxisRange(min = 0, max = 1)
@BenchmarkMethodChart(filePrefix = "benchmark/org.openyu.socklet.message.service.impl.ProtocolServiceImplTest")
@BenchmarkHistoryChart(filePrefix = "benchmark/org.openyu.socklet.message.service.impl.ProtocolServiceImplTest")
public class ProtocolServiceImplTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	private static MessageServiceImpl messageServiceImpl;

	private static ProtocolServiceImpl protocolServiceImpl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"org/openyu/socklet/message/testContext-message.xml",//
		});

		messageServiceImpl = (MessageServiceImpl) applicationContext.getBean("messageService");
		protocolServiceImpl = (ProtocolServiceImpl) applicationContext.getBean("protocolService");
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0, concurrency = 1)
	public void protocolServiceImpl() {
		System.out.println(protocolServiceImpl);
		assertNotNull(protocolServiceImpl);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void close() {
		System.out.println(protocolServiceImpl);
		assertNotNull(protocolServiceImpl);
		applicationContext.close();
		// 多次,不會丟出ex
		applicationContext.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void refresh() {
		System.out.println(protocolServiceImpl);
		assertNotNull(protocolServiceImpl);
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
		Message result = messageServiceImpl.createClient(ROLE_ID, CoreMessageType.FOUR_SYMBOL_PLAY_REQUEST);
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

		Message result = messageServiceImpl.createMessage(CoreModuleType.FOUR_SYMBOL, CoreModuleType.CLIENT,
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
		Message result = messageServiceImpl.createMessage(CoreModuleType.CORE, CoreModuleType.CHAT,
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
		Message result = messageServiceImpl.createMessage(CoreModuleType.CHAT, CoreModuleType.CLIENT,
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
		result = protocolServiceImpl.handshake(CategoryType.HANDSHAKE_CLIENT, authKey.getBytes(), sender);
		//
		System.out.println("length: " + result.length);// 73
		SystemHelper.println(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 1, 0, 0, 0, 73, 65, 20, -68, 0, 0, 0, 56, -16, 41, 125, -54, -120, -92,
				78, -38, -122, 19, -64, 16, 39, -1, -31, -90, -24, 100, -72, -83, -29, -6, 45, -54, -18, -117, 71, 91,
				-67, -84, 95, -28, -29, -52, -85, -117, -114, 90, 117, -120, -64, -86, 92, 96, 46, 31, 81, 126, 109,
				-112, -19, -12, -70, 14, -56, 17, 35, -122 };

		assertArrayEquals(expecteds, result);

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

		byte[] value = protocolServiceImpl.handshake(CategoryType.HANDSHAKE_CLIENT, AUTH_KEY.getBytes(), SENDER);
		//
		Message result = null;
		//
		result = protocolServiceImpl.dehandshake(value);
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
		result = protocolServiceImpl.handshake(CategoryType.HANDSHAKE_SERVER, authKey.getBytes(), sender);
		System.out.println("length: " + result.length);// 73
		SystemHelper.println(result);

		byte[] expecteds = new byte[] { 0, 0, 0, 1, 0, 0, 0, 73, 65, 20, -68, 0, 0, 0, 56, -16, 41, 53, -112, -83, 4,
				98, 93, -84, -33, 84, -50, 105, 7, 52, -122, 53, 10, -51, -83, 93, -78, 119, -101, -23, 63, 122, -31,
				-7, -62, 48, -115, -98, -77, 123, -104, 118, 58, -69, -21, -101, 42, -74, 12, -111, -63, -13, 41, -120,
				-41, 110, 112, 102, 103, 54, 53, -60, 0 };

		assertArrayEquals(expecteds, result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// round: 0.05
	// 10000 times: 130 mills. 0_0_0
	// 10000 times: 765 mills. c_s_c
	public void dehandshakeServer() {
		String AUTH_KEY = "aacc8964324738c27c07746e3ea81aff";
		String SENDER = "slave1";

		byte[] value = protocolServiceImpl.handshake(CategoryType.HANDSHAKE_SERVER, AUTH_KEY.getBytes(), SENDER);
		Message result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.dehandshake(value);
		}
		System.out.println(result);
		System.out.println(result.getSender());// slave1

		assertEquals(SENDER, result.getSender());
		assertEquals(AUTH_KEY, result.getString(0));
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// round: 0.09
	public void handshakeRelation() {
		String authKey = "aacc8964324738c27c07746e3ea81aff";
		String sender = "slave2";
		byte[] result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.handshake(CategoryType.HANDSHAKE_RELATION, authKey.getBytes(), sender);
		}
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// round: 0.05
	public void dehandshakeRelation() {
		String AUTH_KEY = "aacc8964324738c27c07746e3ea81aff";
		String SENDER = "slave2";

		byte[] value = protocolServiceImpl.handshake(CategoryType.HANDSHAKE_RELATION, AUTH_KEY.getBytes(), SENDER);
		Message result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.dehandshake(value);
		}
		System.out.println(result);
		System.out.println(result.getCategoryType());
		System.out.println(result.getSender());// slave2

		assertEquals(SENDER, result.getSender());
		assertEquals(AUTH_KEY, result.getString(0));
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// round: 0.09
	public void handshakeRelation2() {
		byte[] authKey = new byte[] { 48, 49, 97, 55, 54, 51, 99, 98, 97, 51, 100, 98, 54, 101, 50, 98, 51, 49, 54, 97,
				48, 98, 55, 98, 48, 102, 101, 54, 97, 52, 97, 48 };
		String sender = "slave2:0:127.0.0.1:3110";
		byte[] result = null;
		//
		final int COUNT = 1;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.handshake(CategoryType.HANDSHAKE_RELATION, authKey, sender);
		}
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
	}

	@Test
	// 10000 times: 3513 mills.
	public void assembleClient2Server() throws Exception {
		Message message = mockClientToServer();
		//
		byte[] result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.assemble(message);
		}
		//
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
		assertNotNull(result);

		// 輸出成ser檔
		// OutputStream out =
		// IoHelper.createOutputStream(ConfigHelper.getSerDir()
		// + "/assembleClient2Server.ser");
		// out.write(result);
		// out.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// 10000 times: 902 mills.
	public void disassembleClient2Server() {
		Message message = mockClientToServer();
		byte[] value = protocolServiceImpl.assemble(message);
		// SystemHelper.println(value);
		//
		List<Message> result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.disassemble(value, CoreModuleType.class, CoreMessageType.class);
		}
		assertTrue(result.size() > 0);
		//
		for (Message entry : result) {
			System.out.println(entry);
			assertEquals(1, entry.getInt(0));// 0, 玩幾次
		}
	}

	@Test
	// 10000 times: 5709 mills.
	public void assembleServer2Client() throws Exception {
		Message message = mockServerToClient();
		//
		byte[] result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.assemble(message);
		}
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
		assertNotNull(result);

		// 輸出成ser檔
		// OutputStream out =
		// IoHelper.createOutputStream(ConfigHelper.getSerDir()
		// + "/assembleServer2Client.ser");
		// out.write(result);
		// out.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// 10000 times: 1113 mills.
	public void disassembleServer2Client() {
		Message message = mockServerToClient();
		byte[] value = protocolServiceImpl.assemble(message);
		//
		List<Message> result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.disassemble(value, CoreModuleType.class, CoreMessageType.class);
		}
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
	// 10000 times: 3375 mills.
	public void assembleServer2Server() {
		Message message = mockServerToServer();
		//
		byte[] result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.assemble(message);
		}
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
		assertNotNull(result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// 10000 times: 768 mills.
	public void disassembleServer2Server() {
		Message message = mockServerToServer();
		byte[] value = protocolServiceImpl.assemble(message);
		// SystemHelper.println(value);
		//
		List<Message> result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.disassemble(value, CoreModuleType.class, CoreMessageType.class);
		}
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
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// 10000 times: 3276 mills.
	public void assembleServer2Relation() {
		Message message = mockServerToRelation();
		//
		byte[] result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.assemble(message);
		}
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
		assertNotNull(result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// 10000 times: 790 mills.
	public void disassembleServer2Relation() {
		Message message = mockServerToRelation();
		byte[] value = protocolServiceImpl.assemble(message);
		//
		List<Message> result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.disassemble(value, CoreModuleType.class, CoreMessageType.class);

		}
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
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// 10000 times: 792 mills.
	public void assembleAcceptor2Relation() {
		Message message = mockAcceptorToRelation();
		//
		byte[] result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.assemble(message);
		}
		System.out.println("length: " + result.length);
		SystemHelper.println(result);
		assertNotNull(result);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// 10000 times: 766 mills.
	public void disassembleAcceptor2Relation() {
		Message message = mockAcceptorToRelation();
		byte[] value = protocolServiceImpl.assemble(message);
		// SystemHelper.println(value);
		//
		List<Message> result = null;
		//
		final int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.disassemble(value, AcceptorModuleType.class, AcceptorMessageType.class);
		}
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
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// round: 0.06, GC: 2
	// round: 0.05, GC: 2
	public void encode() {
		byte[] result = null;
		//
		final int COUNT = 1000000;
		for (int i = 0; i < COUNT; i++) {
			result = protocolServiceImpl.encode(HeadType.HANDSHAKE);
		}
		// 0, 0, 0, 1, 0, 0, 0, 12, 0, 0, 0, 1
		System.out.println("length: " + result.length);// 12
		SystemHelper.println(result);
		assertNotNull(result);
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
