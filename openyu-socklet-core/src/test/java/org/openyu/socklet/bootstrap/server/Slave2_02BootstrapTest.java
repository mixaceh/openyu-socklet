package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

public class Slave2_02BootstrapTest {

	@Test
	public void main() {
		AcceptorBootstrap.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-slave2_02.xml" });
	}

}
