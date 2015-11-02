package org.openyu.socklet.bootstrap.client;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.openyu.commons.bootstrap.supporter.BootstrapSupporter;
import org.openyu.commons.lang.ArrayHelper;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.connector.control.ClientControl;
import org.openyu.socklet.connector.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * client啟動器
 */
public final class ClientBootstrap extends BootstrapSupporter {

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(ClientBootstrap.class);

	/**
	 * client id, sender
	 */
	private static String id;

	/**
	 * 判斷是否啟動
	 */
	private static boolean started;

	/**
	 * 客戶端控制器
	 */
	private static ClientControl clientControl;

	/**
	 * 客戶端服務
	 */
	private static ClientService clientService;

	public ClientBootstrap() {
	}

	public static boolean isStarted() {
		return started;
	}

	/**
	 * 從main直接啟動
	 * 
	 * class path
	 * 
	 * org/openyu/socklet/bootstrap/client/applicationContext-client1.xml
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		if (!started) {
			long start = System.nanoTime();
			try {
				// 建構applicationContext
				buildApplicationContext(args);
				// 建構客戶端控制器
				buildClientControl();
				// 建構客戶端服務
				buildClientService();
				// 啟動
				doStart();
			} catch (Exception ex) {
				ex.printStackTrace();
				started = false;
			}
			long dur = System.nanoTime() - start;
			dur = TimeUnit.NANOSECONDS.toMillis(dur);
			//
			String msgPattern = "[{0}] start in {1} ms";
			StringBuilder msg = new StringBuilder(MessageFormat.format(
					msgPattern, id, dur));
			//
			if (started) {
				LOGGER.info(msg.toString());
				//
				ThreadHelper.loop(50);
			} else {
				LOGGER.error("[" + id + "] started fail");
			}
		}
	}

	/**
	 * 建構applicationContext
	 * 
	 * @param args
	 */
	protected static void buildApplicationContext(String args[]) {
		// 加入spring設定檔 from args
		if (ArrayHelper.isEmpty(args)) {
			throw new IllegalArgumentException("The Args must not be null");
		}
		applicationContext = new ClassPathXmlApplicationContext(args);
		//
		if (applicationContext == null) {
			throw new IllegalArgumentException(
					"The ApplicationContext must not be null");
		}
	}

	/**
	 * 建構客戶端控制器
	 * 
	 * @param args
	 * @throws Exception
	 */
	protected static void buildClientControl() throws Exception {
		clientControl = applicationContext.getBean(ClientControl.class);
		//
		if (clientControl == null) {
			throw new IllegalArgumentException(
					"The ClientControl must not be null");
		}
	}

	/**
	 * 建構客戶端控制器
	 * 
	 * @param args
	 * @throws Exception
	 */
	protected static void buildClientService() throws Exception {
		clientService = applicationContext.getBean(ClientService.class);
		//
		if (clientService == null) {
			throw new IllegalArgumentException(
					"The ClientService must not be null");
		}
	}

	protected static void doStart() {
		// id
		id = clientControl.getId();
		clientControl.setVisible(true);
		// 啟動
		started = true;
	}
}
