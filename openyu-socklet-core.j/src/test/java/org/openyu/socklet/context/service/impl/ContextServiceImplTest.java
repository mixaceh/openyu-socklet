package org.openyu.socklet.context.service.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextServiceImplTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	private static ContextServiceImpl contextServiceImpl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"testContext-thread.xml", //
				"org/openyu/socklet/context/testContext-context.xml",//
		});

		contextServiceImpl = (ContextServiceImpl) applicationContext.getBean("contextService");
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0, concurrency = 1)
	public void contextServiceImpl() {
		System.out.println(contextServiceImpl);
		assertNotNull(contextServiceImpl);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void close() {
		System.out.println(contextServiceImpl);
		assertNotNull(contextServiceImpl);
		applicationContext.close();
		// 多次,不會丟出ex
		applicationContext.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void refresh() {
		System.out.println(contextServiceImpl);
		assertNotNull(contextServiceImpl);
		applicationContext.refresh();
		// 多次,不會丟出ex
		applicationContext.refresh();
	}
}
