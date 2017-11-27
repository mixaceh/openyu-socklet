package org.openyu.socklet.bootstrap.server;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.bootstrap.supporter.BootstrapSupporter;
import org.openyu.commons.lang.ArrayHelper;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.commons.lang.RuntimeHelper;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.commons.util.AssertHelper;
import org.openyu.commons.util.ByteUnit;
import org.openyu.commons.util.MemoryHelper;
import org.openyu.socklet.acceptor.service.AcceptorService;
import org.openyu.socklet.connector.vo.AcceptorConnector;

/**
 * 接收器啟動器
 */
public final class AcceptorBootstrap extends BootstrapSupporter {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(AcceptorBootstrap.class);

	/**
	 * 是否啟動
	 */
	private static boolean started;

	/**
	 * 接收器id
	 */
	private static String id;

	/**
	 * 實例id
	 */
	private static String instanceId;

	/**
	 * 輸出id
	 */
	private static String outputId;

	/**
	 * 接收器服務
	 */
	private static AcceptorService acceptorService;

	private AcceptorBootstrap() {
		throw new SecurityException(
				new StringBuilder().append(AcceptorBootstrap.class.getName()).append(" can not construct").toString());
	}

	public static boolean isStarted() {
		return started;
	}

	/**
	 * 從main直接啟動,並不是在web下啟動
	 * 
	 * class path
	 * 
	 * org/openyu/socklet/bootstrap/server/applicationContext-slave1.xml
	 * 
	 * ThreadHelper.loop(50);
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			if (started) {
				throw new IllegalStateException(new StringBuilder().append(AcceptorBootstrap.class.getSimpleName())
						.append(" was already started").toString());
			}
			//
			double usedMemory = 0d;
			long begTime = System.currentTimeMillis();
			// 計算所耗費的記憶體(bytes)
			RuntimeHelper.gc();
			// 原本的記憶體
			long memory = RuntimeHelper.usedMemory();

			// 建構applicationContext
			buildApplicationContext(args);

			// 取得接受器服務
			buildAcceptorService();

			// 啟動
			doStart();
			//
			long endTime = System.currentTimeMillis();
			long durTime = endTime - begTime;
			//
			RuntimeHelper.gc();
			usedMemory = Math.max(usedMemory, (RuntimeHelper.usedMemory() - memory));
			double kb = NumberHelper.round(ByteUnit.BYTE.toKiB(usedMemory), 1);
			double mb = NumberHelper.round(ByteUnit.BYTE.toMiB(usedMemory), 1);
			double sizeOf = NumberHelper.round(ByteUnit.BYTE.toMiB(MemoryHelper.sizeOf(applicationContext)), 1);
			//
			if (started) {
				String MEMORY_PATTERN = "[{0}] ({1}) ({2}) start in {3} ms, {4} bytes, {5} KB, {6} MB, sizeOf: {7} MB memory used";
				StringBuilder msg = new StringBuilder(MessageFormat.format(MEMORY_PATTERN, id, instanceId, outputId,
						durTime, usedMemory, kb, mb, sizeOf));
				LOGGER.info(msg.toString());
				// loop
				ThreadHelper.loop(50);
			} else {
				LOGGER.error("[" + id + "] (" + instanceId + ") (" + outputId + ") started fail");
			}
		} catch (Exception e) {
			started = false;
			LOGGER.error(new StringBuilder("Exception encountered during main()").toString(), e);
			// 結束
			System.exit(0);
		}
	}

	/**
	 * 建構applicationContext
	 * 
	 * @param args
	 */
	protected static void buildApplicationContext(String args[]) {
		// spring 設定檔
		List<String> configLocations = new LinkedList<String>();
		configLocations.add(DEFAULT_APPLICATION_CONTEXT_XML);

		// 從args加入spring設定檔
		if (ArrayHelper.notEmpty(args)) {
			for (String arg : args) {
				// class path
				// org/openyu/socklet/bootstrap/server/applicationContext-slave1.xml
				configLocations.add(arg);
			}
		}
		//
		applicationContext = new ClassPathXmlApplicationContext(
				(String[]) configLocations.toArray(new String[configLocations.size()]));
		//
		AssertHelper.notNull(applicationContext, "The ApplicationContext must not be null");
	}

	/**
	 * 取第一個接收器服務
	 * 
	 * @return
	 */
	protected static void buildAcceptorService() {
		Map<String, AcceptorService> acceptorServices = applicationContext.getBeansOfType(AcceptorService.class);
		for (AcceptorService entry : acceptorServices.values()) {
			acceptorService = entry;
			break;
		}
		//
		AssertHelper.notNull(acceptorService, "The AcceptorService must not be null");
	}

	/**
	 * 從web上啟動,或其他已經建構的applicationContext來啟動
	 * 
	 * @param applicationContext
	 */
	public static void start(ApplicationContext applicationContext) {
		AssertHelper.notNull(applicationContext, "The ApplicationContext must not be null");
		//
		try {
			if (started) {
				throw new IllegalStateException(new StringBuilder().append(AcceptorBootstrap.class.getSimpleName())
						.append(" was already started").toString());
			}
			double usedMemory = 0d;
			long begTime = System.currentTimeMillis();
			// 計算所耗費的記憶體(bytes)
			RuntimeHelper.gc();
			// 原本的記憶體
			long memory = RuntimeHelper.usedMemory();
			// 取得applicationContext
			AcceptorBootstrap.applicationContext = applicationContext;

			// 取得接受器服務
			buildAcceptorService();

			// 啟動
			doStart();
			//
			long endTime = System.currentTimeMillis();
			long durTime = endTime - begTime;
			//
			RuntimeHelper.gc();
			usedMemory = Math.max(usedMemory, (RuntimeHelper.usedMemory() - memory));
			double usedMemoryMB = NumberHelper.round(ByteUnit.BYTE.toMB(usedMemory), 2);
			//
			if (started) {
				String msgPattern = "[{0}] ({1}) ({2}) start in {3} ms, {4} bytes ({5} MB) memory used";
				StringBuilder msg = new StringBuilder(
						MessageFormat.format(msgPattern, id, instanceId, outputId, durTime, usedMemory, usedMemoryMB));
				LOGGER.info(msg.toString());
			} else {
				LOGGER.error("[" + id + "] (" + instanceId + ") (" + outputId + ") started fail");
			}
		} catch (Exception e) {
			started = false;
			LOGGER.error(new StringBuilder("Exception encountered during start()").toString(), e);
		}
	}

	/**
	 * 內部啟動
	 * 
	 * @throws Exception
	 */
	protected static void doStart() throws Exception {
		started = acceptorService.isStarted();
		// 這裡不判斷是否有啟動
		id = acceptorService.getId();
		instanceId = acceptorService.getInstanceId();
		outputId = acceptorService.getOutputId();
	}

	public static AcceptorService getAcceptorService() {
		return acceptorService;
	}

	/**
	 * 取得接收器id, 有判斷是否啟動
	 * 
	 * @return
	 */
	public static String getId() {
		if (started) {
			return id;
		}
		return null;
	}

	/**
	 * 取得接收器實例id, 有判斷是否啟動
	 * 
	 * @return
	 */

	public static String getInstanceId() {
		if (started) {
			return instanceId;
		}
		return null;
	}

	/**
	 * 取得接收器輸出id, 有判斷是否啟動
	 * 
	 * @return
	 */
	public static String getOutputId() {
		if (started) {
			return outputId;
		}
		return null;
	}

	/**
	 * 取得server ip, 有判斷是否啟動
	 * 
	 * @param sender
	 * @return
	 */
	public static String getServerIp(String sender) {
		if (started) {
			AcceptorConnector connector = acceptorService.getAcceptorConnector(sender);
			if (connector != null) {
				return connector.getServerIp();
			}
		}
		return null;
	}

	/**
	 * 取得server port, 有判斷是否啟動
	 * 
	 * @param sender
	 * @return
	 */
	public static int getServerPort(String sender) {
		if (started) {
			AcceptorConnector connector = acceptorService.getAcceptorConnector(sender);
			if (connector != null) {
				return connector.getServerPort();
			}
		}
		return 0;
	}

	/**
	 * 取得client ip, 有判斷是否啟動
	 * 
	 * @param sender
	 * @return
	 */
	public static String getClientIp(String sender) {
		if (started) {
			AcceptorConnector connector = acceptorService.getAcceptorConnector(sender);
			if (connector != null) {
				return connector.getClientIp();
			}
		}
		return null;
	}

	/**
	 * 取得client port, 有判斷是否啟動
	 * 
	 * @param sender
	 * @return
	 */
	public static int getClientPort(String sender) {
		if (started) {
			AcceptorConnector connector = acceptorService.getAcceptorConnector(sender);
			if (connector != null) {
				return connector.getClientPort();
			}
		}
		return 0;
	}

	public static Map<String, List<String>> getRelations() {
		if (started) {
			return acceptorService.getRelations();
		}
		return new LinkedHashMap<String, List<String>>();
	}

}
