package org.openyu.socklet.bootstrap.server;

import org.junit.Test;

public class StandaloneBootstrapTest {

	@Test
	public void main() {
		StandaloneBootstrapBak
				.main(new String[] { "org/openyu/socklet/bootstrap/server/applicationContext-slave1.xml" });
	}

}
