package org.openyu.socklet.bootstrap.client;

import java.text.MessageFormat;

import org.openyu.commons.bootstrap.supporter.BootstrapSupporter;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.commons.lang.RuntimeHelper;
import org.openyu.commons.util.AssertHelper;
import org.openyu.commons.util.ByteUnit;
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
			createApplicationContext(args);
			// 建構客戶端控制器
			getClientControl();
			// 建構客戶端服務
			getClientService();
			// --------------------------------------------------
			// 啟動
			doStart();
			// --------------------------------------------------
			long endTime = System.currentTimeMillis();
			long durTime = endTime - begTime;
			//
			RuntimeHelper.gc();
			usedMemory = Math.max(usedMemory, (RuntimeHelper.usedMemory() - memory));
			double usedMemoryMB = NumberHelper.round(ByteUnit.BYTE.toMB(usedMemory), 2);
			//
			if (started){
				String msgPattern = "[{0}] start in {1} ms, {2} bytes ({3} MB) memory used";
				StringBuilder msg = new StringBuilder(
						MessageFormat.format(msgPattern, id, durTime, usedMemory, usedMemoryMB));
				LOGGER.info(msg.toString());
				//
				// ThreadHelper.loop(50);
			}
			else{
				LOGGER.error("[" + id + "] started fail");
			}
				
		} catch (Exception e) {
			clientControl.setVisible(false);
			LOGGER.error(new StringBuilder("[" + id + "] Exception encountered during main()").toString(), e);
		}
	}

	/**
	 * 建構applicationContext
	 * 
	 * @param args
	 */
	protected static void createApplicationContext(String args[]) {
		// 加入spring設定檔 from args
		AssertHelper.notNull(args, new StringBuilder().append("The Args must not be null").toString());
		//
		applicationContext = new ClassPathXmlApplicationContext(args);
		AssertHelper.notNull(applicationContext,
				new StringBuilder().append("The ApplicationContext must not be null").toString());
	}

	/**
	 * 建構客戶端控制器
	 * 
	 * @param args
	 * @throws Exception
	 */
	protected static void getClientControl() {
		clientControl = applicationContext.getBean(ClientControl.class);
		AssertHelper.notNull(clientControl,
				new StringBuilder().append("The ClientControl must not be null").toString());
	}

	/**
	 * 建構客戶端控制器
	 * 
	 * @param args
	 * @throws Exception
	 */
	protected static void getClientService() {
		clientService = applicationContext.getBean(ClientService.class);
		AssertHelper.notNull(clientService,
				new StringBuilder().append("The ClientService must not be null").toString());
	}

	protected static void doStart() throws Exception {
		// id
		id = clientControl.getId();
		clientControl.setVisible(true);
		// 啟動
		started = true;
	}
}
