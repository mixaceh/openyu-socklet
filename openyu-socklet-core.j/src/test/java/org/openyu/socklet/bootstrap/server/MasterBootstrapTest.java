package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

public class MasterBootstrapTest {

	@Test
	public void main() {
		ServerBootstrapBak
				.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-master.xml" });
	}

}
