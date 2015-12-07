package org.openyu.socklet.bootstrap.server;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.bootstrap.supporter.BootstrapSupporter;
import org.openyu.commons.lang.ArrayHelper;
import org.openyu.commons.lang.RuntimeHelper;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.commons.util.ByteUnit;
import org.openyu.commons.util.CollectionHelper;
import org.openyu.socklet.acceptor.service.AcceptorService;

/**
 * Acceptor啟動器
 */
public final class ServerBootstrap extends BootstrapSupporter {
	private static transient final Logger LOGGER = LoggerFactory.getLogger(ServerBootstrap.class);

	/**
	 * acceptor id
	 */
	private static String id;

	private static String instanceId;

	/**
	 * 判斷是否啟動
	 */
	private static boolean started;

	/**
	 * 所有acceptorService
	 */
	private static Map<String, AcceptorService> acceptorServices = new LinkedHashMap<String, AcceptorService>();

	public ServerBootstrap() {
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
		if (!started) {
			// 計算所耗費的記憶體(bytes)
			RuntimeHelper.gc();
			long begUsedMemory = RuntimeHelper.usedMemory();
			long begTime = System.nanoTime();
			try {
				// 建構applicationContext
				buildApplicationContext(args);
				// 建構acceptorService
				buildAcceptorServices();
				// 啟動
				doStart();
			} catch (Exception e) {
				started = false;
				LOGGER.error(new StringBuilder("Exception encountered during main()").toString(), e);
			}
			//
			long durTime = System.nanoTime() - begTime;
			durTime = TimeUnit.NANOSECONDS.toMillis(durTime);
			//
			RuntimeHelper.gc();
			double durUsedMemory = RuntimeHelper.usedMemory() - begUsedMemory;
			durUsedMemory = ByteUnit.BYTE.toMB(durUsedMemory);
			//
			String msgPattern = "[{0}] ({1}) start in {2} ms, memory used {3} MB";
			StringBuilder msg = new StringBuilder(
					MessageFormat.format(msgPattern, id, instanceId, durTime, durUsedMemory));
			//
			if (started) {
				LOGGER.info(msg.toString());
				//
				ThreadHelper.loop(50);
			} else {
				LOGGER.error("[" + id + "] (" + instanceId + ") started fail");
			}
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

		// 加入spring設定檔 from args
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
		if (applicationContext == null) {
			throw new IllegalArgumentException("The ApplicationContext must not be null");
		}
	}

	/**
	 * 建構acceptorService
	 */
	protected static void buildAcceptorServices() {
		acceptorServices = applicationContext.getBeansOfType(AcceptorService.class);
		//
		if (CollectionHelper.isEmpty(acceptorServices)) {
			throw new IllegalArgumentException("The AcceptorServices must not be null or empty");
		}
	}

	/**
	 * 從web上啟動,或其他已經建構的applicationContext來啟動
	 * 
	 * @param applicationContext
	 */
	public static void start(ApplicationContext applicationContext) {
		long start = System.nanoTime();
		try {
			ServerBootstrap.applicationContext = applicationContext;
			// 建構acceptorService
			buildAcceptorServices();
			//
			doStart();
		} catch (Exception e) {
			started = false;
			LOGGER.error(new StringBuilder("Exception encountered during start()").toString(), e);
		}
		//
		long dur = System.nanoTime() - start;
		dur = TimeUnit.NANOSECONDS.toMillis(dur);
		//
		if (started) {
			LOGGER.info("[" + id + "] (" + instanceId + ") start in " + dur + " ms");
		} else {
			LOGGER.error("[" + id + "] (" + instanceId + ") started fail");
		}
	}

	/**
	 * 內部啟動
	 */
	protected static void doStart() throws Exception {
		for (AcceptorService acceptorService : acceptorServices.values()) {
			started = acceptorService.isStarted();
			//
			id = acceptorService.getId();
			instanceId = acceptorService.getInstanceId();
			break;
		}
	}
}
