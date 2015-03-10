package org.openyu.socklet.chat.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.socklet.chat.service.ChatService;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.vo.Message;

public class ChatServiceImpl extends BaseServiceSupporter implements ChatService
{
	private static transient final Logger log = LogManager.getLogger(ChatServiceImpl.class);

	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;

	public ChatServiceImpl()
	{}

	/**
	 * 處理角色連線時初始化
	 * 
	 * @param role
	 */
	public void onInitialize(String roleId)
	{
		System.out.println("onInitialize...");
		// 初始化
		sendInitialize(roleId);
	}

	/**
	 * 處理初始化回應
	 * 
	 * @param role
	 */
	public void sendInitialize(String roleId)
	{
		System.out.println("sendInitialize...");
	}

	public void onSay(String roleId, int channel, String text, String html)
	{
		sendSay(ErrorType.NO_ERROR, roleId, channel, text, html);
	}

	public void sendSay(ErrorType errorType, String roleId, int channel, String text, String html)
	{
		List<String> receivers = new LinkedList<String>();
		receivers.add(roleId);//TEST_ROLE_2,s2
		receivers.add("TEST_ROLE_1");//s1
		receivers.add("TEST_ROLE_3");//s3
		//
		Message message = messageService.createMessage(CoreModuleType.CHAT, CoreModuleType.CLIENT,
			CoreMessageType.CHAT_SAY_REPONSE, receivers);
		message.addInt(errorType.getValue());//0
		//沒錯誤才發以下欄位
		if (ErrorType.NO_ERROR.equals(errorType))
		{
			message.addInt(channel);//1
			message.addString(text);//2
			message.addString(html);//3
			message.addString(roleId);//4
		}
		//
		messageService.addMessage(message);
	}

}
