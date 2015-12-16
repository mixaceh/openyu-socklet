package org.openyu.socklet.bootstrap.client;

import org.junit.Test;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.bootstrap.client.ClientBootstrap;

public class ClientSlave3BootstrapTest {

	@Test
	public void main() {
		ClientBootstrap
				.main(new String[] { "org/openyu/socklet/bootstrap/client/applicationContext-client-slave3.xml" });
		//
		if (ClientBootstrap.isStarted()) {
			ThreadHelper.loop(50);
		}
	}

}
