package org.openyu.socklet.chat.service.impl;

import org.junit.Test;

import org.openyu.socklet.chat.service.ChatService.ErrorType;
import org.openyu.socklet.core.CoreTestSupporter;

public class ChatServiceImplTest extends CoreTestSupporter
{

	@Test
	public void onInitialize()
	{
		final String ROLE_ID = "TEST_ROLE_1";
		chatService.onInitialize(ROLE_ID);
	}

	@Test
	public void sendInitialize()
	{
		final String ROLE_ID = "TEST_ROLE_1";
		chatService.sendInitialize(ROLE_ID);
	}

	@Test
	public void onSay()
	{
		final String ROLE_ID = "TEST_ROLE_1";
		chatService.onSay(ROLE_ID, 1, "Hello world", "<hr/>");
	}

	@Test
	public void sendSay()
	{
		final String ROLE_ID = "TEST_ROLE_1";
		chatService.sendSay(ErrorType.NO_ERROR, ROLE_ID, 1, "Hello world", "<hr/>");
	}

}
