package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

public class Slave2BootstrapTest {

	@Test
	public void main() {
		AcceptorBootstrap.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-slave2.xml" });
	}

}
