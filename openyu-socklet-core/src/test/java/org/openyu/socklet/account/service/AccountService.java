package org.openyu.socklet.account.service;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openyu.commons.enumz.IntEnum;
import org.openyu.commons.service.BaseService;

public interface AccountService extends BaseService
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

	/**
	 * 授權請求
	 * 
	 * @param accountId
	 * @param password
	 */
	void authorize(String accountId, String password);

	String checkAccount(String accountId, String password);

	/**
	 * 授權請求來自於login server
	 * 
	 * @param accountId
	 * @param authKey
	 */
	void authorizeFromLogin(String accountId, String authKey);

	/**
	 * 授權回應
	 * 
	 * @param errorType
	 * @param accountId
	 * @param authKey
	 */
	void sendAuthorize(ErrorType errorType, String accountId, String authKey);
}
