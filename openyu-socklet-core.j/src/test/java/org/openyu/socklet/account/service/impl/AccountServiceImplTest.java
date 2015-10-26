package org.openyu.socklet.account.service.impl;

import javax.crypto.SecretKey;

import org.junit.Test;
import org.openyu.commons.lang.EncodingHelper;
import org.openyu.commons.security.SecurityHelper;
import org.openyu.socklet.account.service.AccountService.ErrorType;
import org.openyu.socklet.core.CoreTestSupporter;

public class AccountServiceImplTest extends CoreTestSupporter {

	@Test
	public void authorize() {
		final String ACCOUNT_ID = "TEST_ACCOUNT_1";
		final String ASSIGN_KEY = "FarFarAway";
		final String ALGORITHM = "HmacMD5";
		SecretKey secretKey = SecurityHelper.createSecretKey(ASSIGN_KEY,
				ALGORITHM);
		byte[] buff = SecurityHelper.mac("1111", secretKey, ALGORITHM);
		final String PASSWORD = EncodingHelper.encodeHex(buff);
		System.out.println(PASSWORD);
		//
		accountService.authorize(ACCOUNT_ID, PASSWORD);
	}

	@Test
	public void checkAccount() {
		final String ACCOUNT_ID = "TEST_ACCOUNT_1";
		// b5f01d3a0898d8016b5633edfe6106b0
		final String ASSIGN_KEY = "FarFarAway";
		final String ALGORITHM = "HmacMD5";
		SecretKey secretKey = SecurityHelper.createSecretKey(ASSIGN_KEY,
				ALGORITHM);
		byte[] buff = SecurityHelper.mac("1111", secretKey, ALGORITHM);
		final String PASSWORD = EncodingHelper.encodeHex(buff);
		System.out.println(PASSWORD);
		//
		String authKey = accountService.checkAccount(ACCOUNT_ID, PASSWORD);
		System.out.println(authKey);
	}

	@Test
	public void authorizeFromLogin() {
		final String ACCOUNT_ID = "TEST_ACCOUNT_1";
		final String AUTH_KEY = "b91150d8b5608535e969b6c9a61fbb5c";
		//
		accountService.authorizeFromLogin(ACCOUNT_ID, AUTH_KEY);
	}

	@Test
	public void sendAuthorize() {
		final String ACCOUNT_ID = "TEST_ACCOUNT_1";
		final String AUTH_KEY = "b91150d8b5608535e969b6c9a61fbb5c";
		//
		accountService.sendAuthorize(ErrorType.NO_ERROR, ACCOUNT_ID, AUTH_KEY);
	}

}
