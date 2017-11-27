package org.openyu.socklet.bootstrap.client;

import java.text.MessageFormat;

import org.openyu.commons.bootstrap.supporter.BootstrapSupporter;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.commons.lang.RuntimeHelper;
import org.openyu.commons.util.AssertHelper;
import org.openyu.commons.util.ByteUnit;
import org.openyu.commons.util.MemoryHelper;
import org.openyu.socklet.connector.control.ClientControl;
import org.openyu.socklet.connector.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * client啟動器
 */
public final class ClientBootstrap extends BootstrapSupporter {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);

	/**
	 * 是否啟動
	 */
	private static boolean started;

	/**
	 * 客戶端id, sender
	 */
	private static String id;

	/**
	 * 客戶端控制器
	 */
	private static ClientControl clientControl;

	/**
	 * 客戶端服務
	 */
	private static ClientService clientService;

	private ClientBootstrap() {
		throw new SecurityException(
				new StringBuilder().append(ClientBootstrap.class.getName()).append(" can not construct").toString());
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
		try {
			if (started) {
				throw new IllegalStateException(new StringBuilder().append(ClientBootstrap.class.getSimpleName())
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

			// 建構客戶端控制器
			buildClientControl();
			// 建構客戶端服務
			buildClientService();
			// --------------------------------------------------
			// 啟動
			doStart();
			// --------------------------------------------------
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
				String MEMORY_PATTERN = "[{0}] start in {1} ms, {2} bytes , {3} KB, {4} MB, sizeOf: {5} MB memory used";
				StringBuilder msg = new StringBuilder(
						MessageFormat.format(MEMORY_PATTERN, id, durTime, usedMemory, kb, mb, sizeOf));
				LOGGER.info(msg.toString());
				//
				// ThreadHelper.loop(50);
			} else {
				LOGGER.error("[" + id + "] started fail");
			}

		} catch (Exception e) {
			clientControl.setVisible(false);
			LOGGER.error(new StringBuilder("[" + id + "] Exception encountered during main()").toString(), e);
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
		// 從args加入spring設定檔
		AssertHelper.notNull(args, new StringBuilder().append("The Args must not be null").toString());
		//
		applicationContext = new ClassPathXmlApplicationContext(args);
		//
		AssertHelper.notNull(applicationContext,
				new StringBuilder().append("The ApplicationContext must not be null").toString());
	}

	/**
	 * 建構客戶端控制器
	 * 
	 * @param args
	 * @throws Exception
	 */
	protected static void buildClientControl() {
		clientControl = applicationContext.getBean(ClientControl.class);
		//
		AssertHelper.notNull(clientControl,
				new StringBuilder().append("The ClientControl must not be null").toString());
	}

	/**
	 * 建構客戶端控制器
	 * 
	 * @param args
	 * @throws Exception
	 */
	protected static void buildClientService() {
		clientService = applicationContext.getBean(ClientService.class);
		//
		AssertHelper.notNull(clientService,
				new StringBuilder().append("The ClientService must not be null").toString());
	}

	/**
	 * 內部啟動
	 * 
	 * @throws Exception
	 */
	protected static void doStart() throws Exception {
		id = clientControl.getId();
		clientControl.setVisible(true);
		// TODO 啟動
		started = true;
	}
}
