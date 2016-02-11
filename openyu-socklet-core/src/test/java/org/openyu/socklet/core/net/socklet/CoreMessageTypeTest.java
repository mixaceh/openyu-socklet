package org.openyu.socklet.core.net.socklet;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.openyu.commons.enumz.EnumHelper;
import org.openyu.commons.junit.supporter.BaseTestSupporter;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

public class CoreMessageTypeTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void values() {
		for (CoreMessageType entry : CoreMessageType.values()) {
			System.out.println(entry + ", " + entry.getValue());
		}
		assertTrue(CoreMessageType.values().length > 0);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	// 1000000 times: 590 mills.
	// 1000000 times: 585 mills.
	// 1000000 times: 591 mills.
	// verified
	public void checkUnique() {
		List<CoreMessageType> result = null;
		result = EnumHelper.checkDuplicate(CoreMessageType.class);

		System.out.println(result);
		assertTrue(result.size() == 0);
	}
}
