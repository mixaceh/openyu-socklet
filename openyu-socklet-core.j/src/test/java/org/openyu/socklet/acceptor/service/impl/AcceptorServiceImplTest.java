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
import org.openyu.socklet.acceptor.service.AcceptorService;

public class AcceptorServiceImplTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	private static AcceptorService masterAcceptor;

	private static AcceptorService slave1Acceptor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"applicationContext-bean.xml", //
				"applicationContext-i18n.xml", //
				"applicationContext-acceptor.xml", //
				//
				"org/openyu/socklet/acceptor/testContext-acceptor.xml",//
		});
		//
		masterAcceptor = (AcceptorService) applicationContext.getBean("masterAcceptor");
		slave1Acceptor = (AcceptorService) applicationContext.getBean("slave1Acceptor");
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void masterAcceptor() {
		System.out.println(masterAcceptor);
		assertNotNull(masterAcceptor);
		//
		ThreadHelper.sleep(3 * 1000);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void close() {
		System.out.println(masterAcceptor);
		assertNotNull(masterAcceptor);
		applicationContext.close();
		// 多次,不會丟出ex
		applicationContext.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void refresh() {
		System.out.println(masterAcceptor);
		assertNotNull(masterAcceptor);
		applicationContext.refresh();
		// 多次,不會丟出ex
		applicationContext.refresh();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void slave1Acceptor() {
		System.out.println(slave1Acceptor);
		assertNotNull(slave1Acceptor);
		//
		ThreadHelper.sleep(5 * 1000);
	}

}
