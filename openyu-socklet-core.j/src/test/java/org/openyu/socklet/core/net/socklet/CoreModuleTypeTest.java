package org.openyu.socklet.core.net.socklet;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openyu.commons.enumz.EnumHelper;
import org.openyu.commons.junit.supporter.BaseTestSupporter;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

public class CoreModuleTypeTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void values() {
		for (CoreModuleType entry : CoreModuleType.values()) {
			System.out.println(entry + ", " + entry.getValue());
		}
		assertTrue(CoreModuleType.values().length > 0);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 1000000 times: 261 mills.
	// 1000000 times: 259 mills.
	// 1000000 times: 252 mills.
	// verified
	public void checkUnique() {
		List<CoreModuleType> result = null;
		result = EnumHelper.checkDuplicate(CoreModuleType.class);

		System.out.println(result);
		assertTrue(result.size() == 0);
	}
}
