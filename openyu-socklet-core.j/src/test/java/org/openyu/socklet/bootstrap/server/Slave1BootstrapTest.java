package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

public class Slave1BootstrapTest {

	@Test
	public void main() {
		ServerBootstrap.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-slave1.xml" });
	}

}
