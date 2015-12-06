package org.openyu.socklet.acceptor.service.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.socklet.acceptor.service.AcceptorService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AcceptorServiceFactoryBeanTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	private static AcceptorService acceptorService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"testContext-thread.xml", //
				"testContext-security.xml", //
				"org/openyu/socklet/message/testContext-message.xml", //
				"org/openyu/socklet/acceptor/testContext-acceptor-factory-bean.xml",//

		});
		acceptorService = (AcceptorService) applicationContext.getBean("acceptorServiceFactoryBean");
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void acceptorService() {
		System.out.println(acceptorService);
		assertNotNull(acceptorService);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void close() {
		System.out.println(acceptorService);
		assertNotNull(acceptorService);
		applicationContext.close();
		// 多次,不會丟出ex
		applicationContext.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void refresh() {
		System.out.println(acceptorService);
		assertNotNull(acceptorService);
		applicationContext.refresh();
		// 多次,不會丟出ex
		applicationContext.refresh();
	}
}
