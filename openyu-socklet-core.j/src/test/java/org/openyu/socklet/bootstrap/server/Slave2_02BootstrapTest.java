package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

import org.openyu.socklet.bootstrap.server.ServerBootstrapBak;

public class Slave2_02BootstrapTest {

	@Test
	public void main() {
		ServerBootstrapBak
				.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-slave2_02.xml" });
	}

}
