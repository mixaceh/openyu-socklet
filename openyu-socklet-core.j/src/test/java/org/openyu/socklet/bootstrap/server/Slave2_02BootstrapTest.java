package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

import org.openyu.socklet.bootstrap.server.ServerBootstrap;

public class Slave2_02BootstrapTest {

	@Test
	public void main() {
		ServerBootstrap
				.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-slave2_02.xml" });
	}

}