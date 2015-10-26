package org.openyu.socklet.core.service.impl;

import org.junit.Test;

import org.openyu.socklet.core.CoreTestSupporter;

public class CoreServiceImplTest extends CoreTestSupporter {

	@Test
	public void onConnect() {
		final String ROLE_ID = "TEST_ROLE_1";
		coreService.onConnect(ROLE_ID);
	}

	@Test
	public void onDisconnect() {
		final String ROLE_ID = "TEST_ROLE_1";
		// 連線
		coreService.onConnect(ROLE_ID);

		// 再斷線
		coreService.onDisconnect(ROLE_ID);
	}

}
