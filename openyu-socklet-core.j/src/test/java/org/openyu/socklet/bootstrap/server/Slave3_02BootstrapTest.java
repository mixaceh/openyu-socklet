package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

public class Slave3_02BootstrapTest {

	@Test
	public void main() {
		ServerBootstrap.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-slave3_02.xml" });
	}

}
