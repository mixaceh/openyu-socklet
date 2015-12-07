package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

public class AccountBootstrapTest {

	@Test
	public void main() {
		ServerBootstrapBak
				.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-account.xml" });
	}

}
