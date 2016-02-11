package org.openyu.socklet.fourSymbol.net.socklet;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.fourSymbol.service.FourSymbolService;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.socklet.service.supporter.SockletServiceSupporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FourSymbolSocklet extends SockletServiceSupporter {

	private static final long serialVersionUID = 604837354035457070L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(FourSymbolSocklet.class);

	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;

	@Autowired
	@Qualifier("fourSymbolService")
	protected transient FourSymbolService fourSymbolService;

	// for stress test
	private long beg = 0;

	private AtomicLong messageCounter = new AtomicLong(0);

	private AtomicLong byteCounter = new AtomicLong(0);

	public FourSymbolSocklet() {

	}

	public void service(Message message) {
		// 訊息
		CoreMessageType messageType = (CoreMessageType) message.getMessageType();
		// 角色id
		String roleId = message.getSender();

		switch (messageType) {
		case FOUR_SYMBOL_PLAY_REQUEST: {
			int times = message.getInt(0);
			fourSymbolService.onPlay(roleId, times);
			break;
		}
		case FOUR_SYMBOL_REWARD_BOARD_REQUEST: {
			fourSymbolService.onRewardBoard(roleId);
			break;
		}
			// stress
		case FOUR_SYMBOL_BENCHMARK_REQUEST: {
			String userId = message.getString(0);
			byte[] bytes = message.getByteArray(1);
			stress(userId, bytes);
			break;
		}
			// stress
		case FOUR_SYMBOL_BENCHMARK_RETURN_REQUEST: {
			String userId = message.getString(0);
			byte[] bytes = message.getByteArray(1);
			stressReturn(userId, bytes);
			break;
		}

		default: {
			LOGGER.error("Can't resolve: " + message);
			break;
		}
		}
	}

	public void stress(String userId, byte[] bytes) {
		if (beg == 0) {
			beg = System.currentTimeMillis();
		}
		//
		messageCounter.incrementAndGet();
		byteCounter.addAndGet(ByteHelper.toByteArray(userId).length);
		byteCounter.addAndGet(bytes.length);
		//
		long end = System.currentTimeMillis();
		long dur = (end - beg);
		double result = Math.round(byteCounter.get() / (double) dur);
		double kresult = Math.round((byteCounter.get() / (double) 1024) / (dur / (double) 1000));
		//
		System.out.println("receive: " + messageCounter.get() + " messages, " + byteCounter.get() + " bytes / " + dur
				+ " ms. = " + result + " BYTES/MS, " + kresult + " K/S");
	}

	public void stressReturn(String userId, byte[] bytes) {
		if (beg == 0) {
			beg = System.currentTimeMillis();
		}
		//
		messageCounter.incrementAndGet();
		byteCounter.addAndGet(ByteHelper.toByteArray(userId).length);
		byteCounter.addAndGet(bytes.length);
		//
		long end = System.currentTimeMillis();
		long dur = (end - beg);
		double result = NumberHelper.round(byteCounter.get() / (double) dur, 2);
		double kresult = NumberHelper.round((byteCounter.get() / (double) 1024) / (dur / (double) 1000), 2);
		double mbresult = NumberHelper
				.round((byteCounter.get() / (double) 1024 / (double) 1024) / (dur / (double) 1000), 2);
		//
		System.out.println("receive: " + messageCounter.get() + " messages, " + byteCounter.get() + " bytes / " + dur
				+ " ms. = " + result + " BYTES/MS, " + kresult + " K/S, " + mbresult + " MB/S");
		//
		sendStressReturn(userId, bytes);
	}

	public Message sendStressReturn(String userId, byte[] bytes) {
		Message message = messageService.createMessage(CoreModuleType.FOUR_SYMBOL, CoreModuleType.CLIENT,
				CoreMessageType.FOUR_SYMBOL_BENCHMARK_RETURN_RESPONSE, userId);
		//
		message.addString(userId);
		message.addByteArray(bytes);
		//
		messageService.addMessage(message);
		return message;
	}
}
