package org.openyu.socklet.acceptor.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.context.service.ContextService;
import org.openyu.socklet.context.service.impl.ContextServiceImpl;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.service.MessageService;

public class AcceptorServiceImplTest extends CoreTestSupporter
{

	private static AcceptorServiceImpl masterAcceptor;

	private static AcceptorServiceImpl slave1Acceptor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		applicationContext = new ClassPathXmlApplicationContext(new String[] {
				"META-INF/applicationContext-commons-core.xml",//
				"META-INF/applicationContext-socklet-core.xml",//
				"applicationContext-acceptor.xml",//
		});

		messageService = (MessageService) applicationContext.getBean("messageService");
		protocolService = (ProtocolService) applicationContext.getBean("protocolService");
		//
		masterAcceptor = (AcceptorServiceImpl) applicationContext.getBean("masterAcceptor");
		slave1Acceptor = (AcceptorServiceImpl) applicationContext.getBean("slave1Acceptor");
	}

	@Test
	public void masterAcceptor()
	{
		System.out.println(masterAcceptor);
		assertNotNull(masterAcceptor);
	}

	@Test
	public void slave1Acceptor()
	{
		System.out.println(slave1Acceptor);
		assertNotNull(slave1Acceptor);
	}

	/**
	 * masterAcceptor
	 */
	public static class MasterAcceptor extends AcceptorServiceImplTest
	{

		@Test
		public void start()
		{
			masterAcceptor.start();
			ThreadHelper.sleep(5 * 1000);
			//
			assertTrue(masterAcceptor.isStarted());
		}

		@Test
		public void startContextService()
		{
			ContextService contextService = new ContextServiceImpl(masterAcceptor.getId(),
				masterAcceptor);
			contextService.start();
			ThreadHelper.sleep(5 * 1000);
			//
			assertTrue(contextService.isStarted());
		}
	}

	/**
	 * slave1Acceptor
	 */
	public static class Slave1Acceptor extends AcceptorServiceImplTest
	{

		@Test
		public void start()
		{
			slave1Acceptor.start();
			ThreadHelper.sleep(5 * 1000);
			//
			assertTrue(slave1Acceptor.isStarted());
		}

		@Test
		public void startContextService()
		{
			ContextService contextService = new ContextServiceImpl(slave1Acceptor.getId(),
				slave1Acceptor);
			ThreadHelper.sleep(5 * 1000);
			//
			assertTrue(contextService.isStarted());
		}

//		@Test
//		public void startClusterChannel()
//		{
//			slave1Acceptor.setCluster("TEST_CLUSTER_CHANNEL");
//			slave1Acceptor.setStarted(true);
//
//			Thread thread = new Thread(new Runnable()
//			{
//				public void run()
//				{
//					slave1Acceptor.startClusterChannel();
//				}
//			});
//			thread.start();
//			//
//			ThreadHelper.sleep(10 * 1000);
//			assertNotNull(slave1Acceptor.getClusterChannel());
//		}

//		@Test
//		public void startRelationChannel()
//		{
//			masterAcceptor.start();
//			ThreadHelper.sleep(3 * 1000);
//			//
//			slave1Acceptor.setStarted(true);
//
//			Thread thread = new Thread(new Runnable()
//			{
//				public void run()
//				{
//					slave1Acceptor.startRelations();
//				}
//			});
//			thread.start();
//			//
//			ThreadHelper.sleep(10 * 1000);
//			int size = slave1Acceptor.getRelationClients().size();
//			System.out.println(size + ", " + slave1Acceptor.getRelationClients());
//			assertTrue(slave1Acceptor.getRelationClients().size() > 0);
//		}

		@Test
		public void shutdown()
		{
			slave1Acceptor.start();
			ThreadHelper.sleep(5 * 1000);
			//
			slave1Acceptor.shutdown();
			ThreadHelper.sleep(5 * 1000);

			assertFalse(slave1Acceptor.isStarted());
		}
	}

}
