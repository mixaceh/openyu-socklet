package org.openyu.socklet.fourSymbol.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.socklet.core.net.socklet.CoreMessageType;
import org.openyu.socklet.core.net.socklet.CoreModuleType;
import org.openyu.socklet.fourSymbol.service.FourSymbolService;
import org.openyu.socklet.message.service.MessageService;
import org.openyu.socklet.message.vo.Message;

public class FourSymbolServiceImpl extends BaseServiceSupporter implements FourSymbolService
{
	private static transient final Logger log = LogManager.getLogger(FourSymbolServiceImpl.class);

	@Autowired
	@Qualifier("messageService")
	protected transient MessageService messageService;

	public FourSymbolServiceImpl()
	{

	}

	/**
	 * 處理角色連線時初始化
	 * 
	 * @param role
	 */
	public void onInitialize(String roleId)
	{
		//重置每日凌晨零点,可以玩的次數
		onDailyTimes(roleId);

		//初始化
		sendInitialize(roleId);
	}

	/**
	 * 處理初始化回應
	 * 
	 * @param role
	 */
	public void sendInitialize(String roleId)
	{

		Message message = messageService.createMessage(CoreModuleType.FOUR_SYMBOL,
			CoreModuleType.CLIENT, CoreMessageType.FOUR_SYMBOL_INITIALIZE_REPONSE, roleId);

		//每日已玩的次數
		message.addInt(5);
		//轉生結果
		message.addString("111");
		//			
		messageService.addMessage(message);
	}

	/**
	 * 處理play的請求
	 * 
	 * @param role 角色
	 * @param times 玩幾次
	 */
	public List<String> onPlay(String roleId, Integer times)
	{
		List<String> result = new LinkedList<String>();
		for (int i = 0; i < times; i++)
		{
			result.add(i + 1 + "11"); //111,211,311...
		}
		sendPlay(ErrorType.NO_ERROR, roleId, result);
		return result;
	}

	@Override
	public CheckResult checkPlay(String roleId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 處理play的回應
	 * 
	 * @param errorType 錯誤類別
	 * @param role 角色
	 * @param fourSymbolResults
	 */
	public void sendPlay(ErrorType errorType, String roleId, List<String> fourSymbolResults)
	{

		Message message = messageService.createMessage(CoreModuleType.FOUR_SYMBOL,
			CoreModuleType.CLIENT, CoreMessageType.FOUR_SYMBOL_PLAY_RESPONSE, roleId);

		message.addInt(errorType.getValue());//0, errorCode 錯誤碼

		switch (errorType)
		{
		//沒有錯誤,才發其他欄位訊息
			case NO_ERROR:
				message.addInt(fourSymbolResults.size());//2 ,結果總數(會有多次轉生結果):
				for (String fourSymbolResult : fourSymbolResults)
				{
					//轉生結果
					message.addString(fourSymbolResult);
				}
				break;
		}
		//	
		messageService.addMessage(message);
	}

	/**
	 * 處理中獎公告區請求
	 */
	public void onRewardBoard(String roleId)
	{
		sendRewardBoard(roleId);
	}

	/**
	 * 處理中獎公告區回應
	 */
	public void sendRewardBoard(String roleId)
	{

		Message message = messageService.createMessage(CoreModuleType.FOUR_SYMBOL,
			CoreModuleType.CLIENT, CoreMessageType.FOUR_SYMBOL_REWARD_BOARD_REPONSE, roleId);
		//
		message.addInt(2);//獲獎訊息總數
		//
		message.addString("TEST_ROLE_2");//角色id
		message.addInt(1);//獎勵等級
		//
		message.addString("TEST_ROLE_3");//角色id
		message.addInt(2);//獎勵等級
		//
		messageService.addMessage(message);
	}

	@Override
	public void onRewardArea(String roleId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void sendRewardArea(String roleId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Integer> onPutOneInBag(String roleId, String itemId, Integer amount)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendPutOneInBag(ErrorType errorType, String roleId, String itemId, Integer amount)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Integer> onPutAllInBag(String roleId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendPutAllInBag(ErrorType errorType, String roleId, Map<String, Integer> rewards)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDailyTimes(String roleId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDailyTimes()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResetDailyTimes(String roleId)
	{
		// TODO Auto-generated method stub

	}

}
