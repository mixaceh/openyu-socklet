package org.openyu.socklet.core.net.socklet;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openyu.commons.enumz.EnumHelper;
import org.openyu.commons.junit.supporter.BaseTestSupporter;

public class CoreMessageTypeTest extends BaseTestSupporter
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{}

	@Before
	public void setUp() throws Exception
	{}

	@After
	public void tearDown() throws Exception
	{}

	@Test
	//verified
	public void values()
	{
		for (CoreMessageType entry : CoreMessageType.values())
		{
			System.out.println(entry + ", " + entry.getValue());
		}
		assertTrue(CoreMessageType.values().length > 0);
	}

	@Test
	//1000000 times: 590 mills. 
	//1000000 times: 585 mills. 
	//1000000 times: 591 mills. 
	//verified	
	public void checkUnique()
	{
		List<CoreMessageType> result = null;
		int count = 1000000;

		long beg = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			result = EnumHelper.checkUnique(CoreMessageType.class);
		}
		long end = System.currentTimeMillis();
		System.out.println(count + " times: " + (end - beg) + " mills. ");

		System.out.println(result);
		assertTrue(result.size() == 0);
	}
}
