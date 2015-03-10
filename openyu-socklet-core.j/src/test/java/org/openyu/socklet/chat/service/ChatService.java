package org.openyu.socklet.chat.service;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import org.openyu.commons.enumz.IntEnum;
import org.openyu.commons.service.BaseService;

public interface ChatService extends BaseService {

	// --------------------------------------------------
	/**
	 * 錯誤類別
	 */
	public enum ErrorType implements IntEnum {
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

		;

		private final int intValue;

		private ErrorType(int intValue) {
			this.intValue = intValue;
		}

		public int getValue() {
			return intValue;
		}

		public String toString() {
			ToStringBuilder builder = new ToStringBuilder(this,
					ToStringStyle.SIMPLE_STYLE);
			builder.append("name", "(" + intValue + ") " + super.toString());
			return builder.toString();
		}
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

	/**
	 * 處理說話請求
	 */
	void onSay(String roleId, int channel, String text, String html);

	/**
	 * 發送說話回應
	 */
	void sendSay(ErrorType errorType, String roleId, int channel, String text, String html);

}
