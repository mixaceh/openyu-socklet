package org.openyu.socklet.bootstrap.client.bak;

import org.junit.Test;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.bootstrap.client.ClientBootstrap;

public class Client2BootstrapTest {

	@Test
	public void main() {
		ClientBootstrap
				.main(new String[] { "org/openyu/socklet/bootstrap/client/appConfig-client2.xml" });
		//
		if (ClientBootstrap.isStarted()) {
			ThreadHelper.loop(50);
		}
	}

}
