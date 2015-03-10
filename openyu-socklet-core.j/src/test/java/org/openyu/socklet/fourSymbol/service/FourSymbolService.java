package org.openyu.socklet.fourSymbol.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import org.openyu.commons.enumz.IntEnum;
import org.openyu.commons.service.BaseService;

public interface FourSymbolService extends BaseService
{

	// --------------------------------------------------
	/**
	 * 錯誤類別
	 */
	public enum ErrorType implements IntEnum
	{
		/**
		 * 未知
		 */
		UNKNOWN(-1),

		/**
		 * 沒有錯誤
		 */
		NO_ERROR(0),

		/**
		 * 角色不存在
		 */
		ROLE_NOT_EXIST(1),

		/**
		 * 超過每日次數
		 */
		OVER_PLAY_DAILY_TIMES(11),

		/**
		 * 獎勵道具區滿了
		 */
		REWARD_ITEM_FULL(12),

		/**
		 * 等級不足
		 */
		LEVLE_NOT_ENOUGH(13),

		/**
		 * 金幣不足
		 */
		GOLD_NOT_ENOUGH(14),

		/**
		 * 儲值不足
		 */
		COIN_NOT_ENOUGH(15),

		/**
		 * vip不足
		 */
		VIP_NOT_ENOUGH(16),

		/**
		 * 包包滿了
		 */
		BAG_FULL(17),

		/**
		 * 消耗金幣失敗
		 */
		SPEND_GLOD_FAIL(21),

		/**
		 * 消耗儲值失敗
		 */
		SPEND_COIN_FAIL(22),

		;

		private final int intValue;

		private ErrorType(int intValue)
		{
			this.intValue = intValue;
		}

		public int getValue()
		{
			return intValue;
		}

		public String toString()
		{
			ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
			builder.append("name", "(" + intValue + ") " + super.toString());
			return builder.toString();
		}
	}

	// --------------------------------------------------
	/**
	 * 檢查類別
	 */
	public enum CheckType implements IntEnum
	{
		/**
		 * 一般
		 */
		GENERAL(1),

		/**
		 * 金幣
		 */
		GOLD(2),

		/**
		 * 儲值
		 */
		COIN(3),

		;

		private final int intValue;

		private CheckType(int intValue)
		{
			this.intValue = intValue;
		}

		public int getValue()
		{
			return intValue;
		}

		public String toString()
		{
			ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
			builder.append("name", "(" + intValue + ") " + super.toString());
			return builder.toString();
		}
	}

	// --------------------------------------------------
	/**
	 * 檢查結果
	 */
	public interface CheckResult
	{
		/**
		 * 錯誤類別
		 * 
		 * @return
		 */
		ErrorType getErrorType();

		void setErrorType(ErrorType errorType);

		/**
		 * 檢查類別
		 * 
		 * @return
		 */
		CheckType getCheckType();

		void setCheckType(CheckType checkType);
	}

	// --------------------------------------------------

	/**
	 * 處理角色連線時初始化
	 * 
	 * @param role
	 */
	void onInitialize(String roleId);

	/**
	 * 發送初始化回應
	 * 
	 * @param role
	 */
	void sendInitialize(String roleId);

	// --------------------------------------------------
	// play流程
	// --------------------------------------------------
	// request -> check -> reponse
	// --------------------------------------------------

	/**
	 * 處理play的請求
	 * 
	 * @param role 角色
	 * @param times 玩幾次
	 */
	List<String> onPlay(String roleId, Integer times);

	/**
	 * 檢查play的條件
	 * 
	 * @param role 角色
	 * @return
	 */
	CheckResult checkPlay(String roleId);

	/**
	 * 發送play的回應
	 * 
	 * @param errorType 錯誤類別
	 * @param role 角色
	 * @param fourSymbolResults
	 */
	void sendPlay(ErrorType errorType, String roleId, List<String> fourSymbolResults);

	/**
	 * 處理獎勵公告區請求
	 * 
	 * @param role
	 */
	void onRewardBoard(String roleId);

	/**
	 * 發送獎勵公告區回應
	 * 
	 * @param role
	 */
	void sendRewardBoard(String roleId);

	/**
	 * 處理獎勵區請求
	 * 
	 * @param role
	 */
	void onRewardArea(String roleId);

	/**
	 * 發送獎勵區回應
	 * 
	 * @param role
	 */
	void sendRewardArea(String roleId);

	/**
	 * 處理單擊放入包包請求
	 * 
	 * @param role
	 * @param itemCode
	 * @param amount
	 */
	Map<String, Integer> onPutOneInBag(String roleId, String itemId, Integer amount);

	/**
	 * 發送單擊放入包包回應
	 * 
	 * @param errorType
	 * @param role
	 * @param itemCode
	 * @param amount
	 */
	void sendPutOneInBag(ErrorType errorType, String roleId, String itemId, Integer amount);

	/**
	 * 處理所有獎勵區放入包包請求
	 * 
	 * @param role
	 */
	Map<String, Integer> onPutAllInBag(String roleId);

	/**
	 * 發送所有獎勵區放入包包回應
	 * 
	 * @param errorType
	 * @param role
	 * @param rewardArea
	 */
	void sendPutAllInBag(ErrorType errorType, String roleId, Map<String, Integer> rewards);

	/**
	 * 重置每日凌晨零点,可以玩的次數
	 * 
	 * @param role
	 * @return
	 */
	void onDailyTimes(String roleId);

	/**
	 * 處理線上所有玩家,重置每日凌晨零点,可以玩的次數
	 * 
	 * @return
	 */
	void onDailyTimes();

	/**
	 * 發送重置每日凌晨零点,可以玩的次數回應
	 * 
	 * @param role
	 */
	void sendResetDailyTimes(String roleId);
}
