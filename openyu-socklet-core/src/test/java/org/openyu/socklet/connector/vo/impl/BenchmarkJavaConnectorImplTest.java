package org.openyu.socklet.connector.vo.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.connector.vo.JavaConnector;
import org.openyu.socklet.connector.vo.supporter.GenericReceiverSupporter;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.vo.Message;

public class BenchmarkJavaConnectorImplTest extends CoreTestSupporter {

	// ---------------------------------------------------
	// native
	// ---------------------------------------------------

	// ---------------------------------------------------
	// native
	// ---------------------------------------------------
	public static class NativeTest extends BenchmarkJavaConnectorImplTest {

		// for benchmarkReturn
		private static long returnBeg = 0;

		private static AtomicLong messageCounter = new AtomicLong(0);

		private static AtomicLong byteCounter = new AtomicLong(0);

		/**
		 * 訊息接收者
		 */
		protected static NativeReceiver nativeReceiver = new NativeReceiver();

		public static class NativeReceiver extends GenericReceiverSupporter {

			private static final long serialVersionUID = 9025630102068228806L;

			public void receive(Message message) {
				System.out.println(message);
				//
				CoreMessageType messageType = (CoreMessageType) message
						.getMessageType();

				switch (messageType) {
				// stress
				case FOUR_SYMBOL_BENCHMARK_RETURN_RESPONSE: {
					String userId = message.getString(0);
					byte[] bytes = message.getByteArray(1);
					//
					stressReturn(userId, bytes);
					break;
				}
				default: {
					break;
				}
				}
			}

			public void stressReturn(String userId, byte[] bytes) {
				//
				messageCounter.incrementAndGet();
				byteCounter.addAndGet(ByteHelper.toByteArray(userId).length);
				byteCounter.addAndGet(bytes.length);
				//
				long end = System.currentTimeMillis();
				long dur = (end - returnBeg);
				double result = NumberHelper.round(byteCounter.get()
						/ (double) dur, 2);
				double kresult = NumberHelper.round(
						(byteCounter.get() / (double) 1024)
								/ (dur / (double) 1000), 2);
				double mbresult = NumberHelper.round((byteCounter.get()
						/ (double) 1024 / (double) 1024)
						/ (dur / (double) 1000), 2);
				//
				System.out.println("send/receive: " + messageCounter.get()
						+ " messages, " + byteCounter.get() + " bytes / " + dur
						+ " ms. = " + result + " BYTES/MS, " + kresult
						+ " K/S, " + mbresult + " MB/S");
			}
		}

		@Test
		// send: 10000 messages, 102519000 bytes / 724547 ms. = 141.49 BYTES/MS,
		// 138.18 K/S, 0.13 MB/S
		//
		// receive: 10000 messages, 102519000 bytes / 720439 ms. = 142.3
		// BYTES/MS, 138.97 K/S, 0.14 MB/S
		public void nativeSend() throws Exception {
			final int NUM_OF_THREADS = 100;
			final int NUM_OF_TIMES = 100;
			final int LENGTH_OF_BYTES = 10 * 1024;// 10k
			//
			final AtomicLong timesCounter = new AtomicLong(0);
			final AtomicLong byteCounter = new AtomicLong(0);
			//
			ExecutorService service = Executors
					.newFixedThreadPool(NUM_OF_THREADS);
			long beg = System.currentTimeMillis();
			for (int i = 0; i < NUM_OF_THREADS; i++) {
				//
				final String userId = "TEST_USER_" + i;
				service.submit(new Runnable() {
					public void run() {
						try {
							//
							for (int i = 0; i < NUM_OF_TIMES; i++) {
								byte[] buff = ByteHelper
										.randomByteArray(LENGTH_OF_BYTES);
								//
								JavaConnector connector = null;
								try {
									connector = new JavaConnectorImpl(userId,
											CoreModuleType.class,
											CoreMessageType.class,
											protocolService, "localhst", 10300);

									// 自訂訊息接收者
									connector.setReceiver(nativeReceiver);
									connector.start();
									//
									Message message = messageService
											.createClient(
													userId,
													CoreMessageType.FOUR_SYMBOL_BENCHMARK_REQUEST);
									message.addString(userId);
									message.addByteArray(buff);
									//
									connector.send(message);
									System.out.println("I[" + userId + "] M["
											+ i + "]");
									//
									timesCounter.incrementAndGet();
									byteCounter.addAndGet(ByteHelper
											.toByteArray(userId).length);
									byteCounter.addAndGet(buff.length);
									//
									ThreadHelper.sleep(50);
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
									ThreadHelper.sleep(3 * 1000);
									if (connector != null) {
										connector.shutdown();
									}
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
						}
						//
					}
				});
				ThreadHelper.sleep(50);
			}
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			//
			long end = System.currentTimeMillis();
			long dur = (end - beg);
			double result = NumberHelper.round(
					byteCounter.get() / (double) dur, 2);
			double kresult = NumberHelper
					.round((byteCounter.get() / (double) 1024)
							/ (dur / (double) 1000), 2);
			double mbresult = NumberHelper.round((byteCounter.get()
					/ (double) 1024 / (double) 1024)
					/ (dur / (double) 1000), 2);
			//
			System.out.println("send: " + timesCounter.get() + " messages, "
					+ byteCounter.get() + " bytes / " + dur + " ms. = "
					+ result + " BYTES/MS, " + kresult + " K/S, " + mbresult
					+ " MB/S");
		}

		@Test
		// send/receive: 10000 messages, 102519000 bytes / 1010746 ms. = 101.42
		// BYTES/MS, 99.04 K/S, 0.1 MB/S
		public void nativeSendReturn() throws Exception {
			final int NUM_OF_THREADS = 100;
			final int NUM_OF_TIMES = 100;
			final int LENGTH_OF_BYTES = 10 * 1024;// 10k
			//
			final AtomicLong timesCounter = new AtomicLong(0);
			final AtomicLong byteCounter = new AtomicLong(0);
			//
			ExecutorService service = Executors
					.newFixedThreadPool(NUM_OF_THREADS);
			returnBeg = System.currentTimeMillis();
			for (int i = 0; i < NUM_OF_THREADS; i++) {
				//
				final String userId = "TEST_USER_" + i;
				service.submit(new Runnable() {
					public void run() {
						try {
							//
							for (int i = 0; i < NUM_OF_TIMES; i++) {
								byte[] buff = ByteHelper
										.randomByteArray(LENGTH_OF_BYTES);
								//
								JavaConnector connector = null;
								try {
									connector = new JavaConnectorImpl(userId,
											CoreModuleType.class,
											CoreMessageType.class,
											protocolService, "localhost", 10300);

									// 自訂訊息接收者
									connector.setReceiver(nativeReceiver);
									connector.start();
									//
									Message message = messageService
											.createClient(
													userId,
													CoreMessageType.FOUR_SYMBOL_BENCHMARK_RETURN_REQUEST);
									message.addString(userId);
									message.addByteArray(buff);
									//
									connector.send(message);
									System.out.println("I[" + userId + "] M["
											+ i + "]");
									//
									timesCounter.incrementAndGet();
									byteCounter.addAndGet(ByteHelper
											.toByteArray(userId).length);
									byteCounter.addAndGet(buff.length);
									ThreadHelper.sleep(50);
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
									ThreadHelper.sleep(10 * 1000);
									if (connector != null) {
										connector.shutdown();
									}
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
						}
						//
					}
				});
				ThreadHelper.sleep(50);
			}
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			//
			long end = System.currentTimeMillis();
			long dur = (end - returnBeg);
			double result = Math.round(byteCounter.get() / (double) dur);
			double kresult = Math.round((byteCounter.get() / (double) 1024)
					/ (dur / (double) 1000));
			double mbresult = NumberHelper.round((byteCounter.get()
					/ (double) 1024 / (double) 1024)
					/ (dur / (double) 1000), 2);
			//
			System.out.println("send: " + timesCounter.get() + " messages, "
					+ byteCounter.get() + " bytes / " + dur + " ms. = "
					+ result + " BYTES/MS, " + kresult + " K/S, " + mbresult
					+ " MB/S");

		}
	}
}
