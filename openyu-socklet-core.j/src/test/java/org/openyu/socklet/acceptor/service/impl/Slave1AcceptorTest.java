package org.openyu.socklet.acceptor.service.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.thread.ThreadHelper;

public class Slave1AcceptorTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	private static AcceptorServiceImpl slave1Acceptor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"testContext-thread.xml", //
				"testContext-security.xml", //
				"org/openyu/socklet/message/testContext-message.xml", //
				"org/openyu/socklet/acceptor/testContext-slave1.xml",//
		});
		//
		slave1Acceptor = (AcceptorServiceImpl) applicationContext.getBean("slave1Acceptor");
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void slave1Acceptor() {
		System.out.println(slave1Acceptor);
		assertNotNull(slave1Acceptor);
		//
		ThreadHelper.sleep(5 * 1000);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void close() {
		System.out.println(slave1Acceptor);
		assertNotNull(slave1Acceptor);
		applicationContext.close();
		// 多次,不會丟出ex
		applicationContext.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void refresh() {
		System.out.println(slave1Acceptor);
		assertNotNull(slave1Acceptor);
		applicationContext.refresh();
		// 多次,不會丟出ex
		applicationContext.refresh();
	}

}
