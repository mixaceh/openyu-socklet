package org.openyu.socklet.login.service.impl;

import org.junit.Test;

import org.openyu.socklet.core.CoreTestSupporter;

public class LoginServiceImplTest extends CoreTestSupporter {

	@Test
	public void authorize() {
		final String ACCOUNT_ID = "TEST_ACCOUNT_1";
		final String AUTH_KEY = "b91150d8b5608535e969b6c9a61fbb5c";
		final String IP = "10.0.0.1";
		//
		loginService.authorize(ACCOUNT_ID, AUTH_KEY, IP);
	}

}
