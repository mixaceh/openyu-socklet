package org.openyu.socklet.account.net.socklet;

import javax.crypto.SecretKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openyu.commons.lang.EncodingHelper;
import org.openyu.commons.security.SecurityHelper;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.message.vo.Message;

public class AccountSockletTest extends CoreTestSupporter {

	@Test
	public void ACCOUNT_AUTHORIZE_REQUEST() {
		final String ACCOUNT_ID = "TEST_ACCOUNT_1";
		final String ASSIGN_KEY = "FarFarAway";
		final String ALGORITHM = "HmacMD5";
		// b5f01d3a0898d8016b5633edfe6106b0
		SecretKey secretKey = SecurityHelper.createSecretKey(ASSIGN_KEY,
				ALGORITHM);
		byte[] buff = SecurityHelper.mac("1111", secretKey, ALGORITHM);
		final String PASSWORD = EncodingHelper.encodeHex(buff);
		System.out.println(PASSWORD);
		//
		Message message = messageService.createClient(ACCOUNT_ID,
				CoreMessageType.ACCOUNT_AUTHORIZE_REQUEST);
		message.addString(ACCOUNT_ID);
		message.addString(PASSWORD);
		accountSocklet.service(message);
	}

	// --------------------------------------------------
	// 啟動account,login,模擬真正連線
	// --------------------------------------------------
	public static class AcceptorTest extends CoreTestSupporter {
		@Before
		public void setUp() throws Exception {
			final String ACCOUNT_ID = "TEST_ACCOUNT_1";
			// 連線到account, localhost:4000
			javaConnector.setId(ACCOUNT_ID);
			javaConnector.setIp("localhost");
			javaConnector.setPort(4000);
			javaConnector.start();
		}

		@After
		public void tearDown() throws Exception {
			ThreadHelper.sleep(3 * 1000);
		}

		@Test
		public void ACCOUNT_AUTHORIZE_REQUEST() {
			final String ACCOUNT_ID = "TEST_ACCOUNT_1";
			final String ASSIGN_KEY = "FarFarAway";
			final String ALGORITHM = "HmacMD5";
			// b5f01d3a0898d8016b5633edfe6106b0
			SecretKey secretKey = SecurityHelper.createSecretKey(ASSIGN_KEY,
					ALGORITHM);
			byte[] buff = SecurityHelper.mac("1111", secretKey, ALGORITHM);
			final String PASSWORD = EncodingHelper.encodeHex(buff);
			System.out.println(PASSWORD);
			//
			Message message = messageService.createClient(ACCOUNT_ID,
					CoreMessageType.ACCOUNT_AUTHORIZE_REQUEST);
			message.addString(ACCOUNT_ID);
			message.addString(PASSWORD);
			//
			javaConnector.send(message);
		}
	}

}