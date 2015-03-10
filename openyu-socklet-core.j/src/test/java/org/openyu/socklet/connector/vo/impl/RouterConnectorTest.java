package org.openyu.socklet.connector.vo.impl;

import org.junit.Test;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.connector.vo.JavaConnector;
import org.openyu.socklet.core.CoreTestSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.vo.Message;

public class RouterConnectorTest extends CoreTestSupporter
{

	@Test
	//r1 on s1, r2 on s2, r3 on s3
	//
	//r2 -> s2
	//s2 -> s1(r1在s1 轉發到s1上),s2 -> s3(r3在s3 轉發到s3上)
	public void send()
	{
		//r1 -> slave1:4110
		JavaConnector r1 = new JavaConnectorImpl("TEST_ROLE_1", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4110);
		r1.setReceiver(receiver);
		r1.start();

		//r2 -> slave2:4120
		JavaConnector r2 = new JavaConnectorImpl("TEST_ROLE_2", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4120);
		r2.setReceiver(receiver);
		r2.start();

		//r3 -> slave3:4130
		JavaConnector r3 = new JavaConnectorImpl("TEST_ROLE_3", CoreModuleType.class,
			CoreMessageType.class, protocolService, "localhost", 4130);
		r3.setReceiver(receiver);
		r3.start();

		//r2發訊息,r1,r3應該都要收到
		Message message = messageService.createClient(r2.getId(), CoreMessageType.CHAT_SAY_REQUEST);
		message.addInt(1);//頻道類型
		message.addString("Hello world");//聊天內容
		message.addString("<hr/>");//html
		//
		r2.send(message);

		ThreadHelper.sleep(3 * 1000);
		//
	}

}
