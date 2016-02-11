package org.openyu.socklet.message.vo.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.openyu.commons.enumz.BooleanEnum;
import org.openyu.commons.enumz.IntEnum;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.PriorityType;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

public class MessageImplTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	@Test
	public void addBoolean() {
		Message message = new MessageImpl(CategoryType.MESSAGE_SERVER,
				PriorityType.MEDIUM);

		boolean value = true;
		message.addBoolean(value);// true
		//
		message.addBoolean((BooleanType) null);// false
		message.addBoolean(BooleanType._1);// true
		//
		message.addBoolean((Boolean) null);// false
		message.addBoolean(new Boolean(true));// true
		//
		System.out.println(message);
		assertEquals(true, message.getBoolean(0));
		assertEquals(false, message.getBoolean(1));
		assertEquals(true, message.getBoolean(2));
		assertEquals(false, message.getBoolean(3));
		assertEquals(true, message.getBoolean(4));
	}

	@Test
	public void addInt() {
		Message message = new MessageImpl(CategoryType.MESSAGE_SERVER,
				PriorityType.MEDIUM);

		int value = 1;
		message.addInt(value);// 1
		//
		message.addInt((IntegerType) null);// 0
		message.addInt(IntegerType._1);// 1
		//
		message.addInt((Integer) null);// 0
		message.addInt(new Integer(2));// 2
		//
		System.out.println(message);
		assertEquals(1, message.getInt(0));
		assertEquals(0, message.getInt(1));
		assertEquals(1, message.getInt(2));
		assertEquals(0, message.getInt(3));
		assertEquals(2, message.getInt(4));
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 1, concurrency = 1)
	// round: 0.05, GC: 5
	//
	// 加了id, timeStamp後, 會有點變慢, 不過差異不大, 不打緊, 就加吧
	// round: 0.76, GC: 44
	public void message() {
		Message result = null;
		//
		final int COUNT = 1000000;
		for (int i = 0; i < COUNT; i++) {
			result = new MessageImpl(CategoryType.MESSAGE_SERVER,
					PriorityType.MEDIUM);
		}
		System.out.println(result.getId());
		System.out.println(result.getTimeStamp());
		//
		assertEquals(CategoryType.MESSAGE_SERVER, result.getCategoryType());
	}

	/**
	 * Boolean類別
	 */
	public enum BooleanType implements BooleanEnum {
		/**
		 * 
		 */
		_1(true), _2(false), _3(true), _4(false)

		;
		private final boolean booleanValue;

		private BooleanType(boolean booleanValue) {
			this.booleanValue = booleanValue;
		}

		public boolean getValue() {
			return booleanValue;
		}

	}

	/**
	 * Integer類別
	 */
	public enum IntegerType implements IntEnum {
		/**
		 * 1-5級
		 */
		_1(1), _2(2), _3(3), _4(4), _5(5),

		/**
		 * 6-10級
		 */
		_6(6), _7(7), _8(8), _9(9), _10(10),

		;
		private final int intValue;

		private IntegerType(int intValue) {
			this.intValue = intValue;
		}

		public int getValue() {
			return intValue;
		}

	}

}
