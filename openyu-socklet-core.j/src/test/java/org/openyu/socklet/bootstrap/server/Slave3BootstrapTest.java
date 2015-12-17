package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

public class Slave3BootstrapTest {

	@Test
	public void main() {
		AcceptorBootstrap.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-slave3.xml" });
	}

}
