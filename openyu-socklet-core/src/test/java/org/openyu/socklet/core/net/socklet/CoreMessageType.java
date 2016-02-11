package org.openyu.socklet.core.net.socklet;

import org.openyu.socklet.message.vo.MessageType;

/**
 * 訊息
 * 
 * 第1位數 => 1=系統用, 2=子功能用
 * 
 * 第2,3位數模組 => 01-99
 * 
 * 第4,5位數訊息 => 01-99, request=01-49, response=50-99
 * 
 */
public enum CoreMessageType implements MessageType {
	// ---------------------------------------------------
	// spec server 110-119
	// ---------------------------------------------------

	// ---------------------------------------------------
	// ACCOUNT請求
	// ---------------------------------------------------
	/**
	 * 授權請求
	 * 
	 * ACCOUNT.ACCOUNT_AUTHORIZE_REQUEST
	 * 
	 * -> LOGIN.LOGIN_AUTHORIZE_REQUEST
	 * 
	 * -> ACCOUNT.ACCOUNT_AUTHORIZE_FROM_LOGIN_REQUEST
	 * 
	 * -> CLIENT.ACCOUNT_AUTHORIZE_REPONSE
	 * 
	 * 0, String, 帳戶id
	 * 
	 * 1, String, 密碼
	 * 
	 */
	ACCOUNT_AUTHORIZE_REQUEST(110001),

	/**
	 * 授權請求來自於login server
	 * 
	 * 0, String, 帳戶id
	 * 
	 * 1, String, 認證碼
	 */
	ACCOUNT_AUTHORIZE_FROM_LOGIN_REQUEST(110002),

	// ---------------------------------------------------
	// ACCOUNT回應
	// ---------------------------------------------------
	/**
	 * 授權回應
	 * 
	 * 0, int, ErrorType 錯誤碼
	 * 
	 * 1, String, 帳戶id
	 * 
	 * 2, String, 認證碼
	 * 
	 * 3, String, server ip
	 * 
	 * 4, String, server port
	 */
	ACCOUNT_AUTHORIZE_REPONSE(110052),

	/**
	 * 帳戶儲值
	 * 
	 * 0, int, 儲值總額
	 */
	ACCOUNT_COIN_REPONSE(110061),

	/**
	 * 帳戶累計儲值
	 * 
	 * 0, int, 累計儲值總額
	 */
	ACCOUNT_ACCU_COIN_REPONSE(110062),

	// ---------------------------------------------------
	// LOGIN請求
	// ---------------------------------------------------

	/**
	 * 驗證
	 * 
	 * 0, String, 帳戶id
	 * 
	 * 1, String, 認證碼
	 * 
	 * 2, String, ip
	 */
	LOGIN_AUTHORIZE_FROM_ACCOUNT_REQUEST(111001),

	/**
	 * 角色連線
	 * 
	 * 0, String, 帳號id
	 * 
	 * 1, String, 角色id
	 */
	LOGIN_CONNECT_REQUEST(111002),

	/**
	 * 角色斷線
	 * 
	 * 0, String, 帳號id
	 * 
	 * 1, String, 角色id
	 */
	LOGIN_DISCONNECT_REQUEST(111003),

	// ---------------------------------------------------
	// LOGIN回應
	// ---------------------------------------------------

	// ---------------------------------------------------
	// master 120-149
	// ---------------------------------------------------

	// ---------------------------------------------------
	// slave 150-198
	// ---------------------------------------------------

	// ---------------------------------------------------
	// CORE請求
	// ---------------------------------------------------
	/**
	 * 角色連線
	 * 
	 * 0, String, 角色id
	 */
	CORE_CONNECT_REQUEST(150001),

	/**
	 * 角色斷線
	 * 
	 * 0, String, 角色id
	 */
	CORE_DISCONNECT_REQUEST(150002),

	// ---------------------------------------------------
	// CORE回應
	// ---------------------------------------------------

	// ---------------------------------------------------
	// 角色回應
	// ---------------------------------------------------
	ROLE_EXP_REPONSE(151051), // 角色經驗

	/**
	 * 角色等級
	 * 
	 * 0, int, 提升到的等級
	 */
	ROLE_LEVEL_REPONSE(151052),

	/**
	 * 角色金幣
	 * 
	 * 0, long, 金幣總額
	 */
	ROLE_GOLD_REPONSE(151053),

	ROLE_ATTRIBUTE_REPONSE(151054), // 角色屬性

	// ---------------------------------------------------
	// 聊天請求
	// ---------------------------------------------------
	/**
	 * 說話請求
	 * 
	 * 0, int, 頻道類型
	 * 
	 * 1, String, 聊天內容
	 * 
	 * 2, String, html
	 */
	CHAT_SAY_REQUEST(152003),

	// ---------------------------------------------------
	// 聊天回應
	// ---------------------------------------------------
	/**
	 * 初始回應
	 */
	CHAT_INITIALIZE_REPONSE(152051),

	/**
	 * 說話回應
	 * 
	 * 0, int, errorCode 錯誤碼
	 * 
	 * 1, int, 頻道類型
	 * 
	 * 2, String, 聊天內容
	 * 
	 * 3, String, html
	 * 
	 * 4, String, 發送者id
	 * 
	 * 5, String, 發送者名稱
	 * 
	 * 6, long 訊息消失的時間
	 */
	CHAT_SAY_REPONSE(152053),

	// ---------------------------------------------------
	// 四象請求
	// ---------------------------------------------------
	/**
	 * 玩的請求
	 * 
	 * int, 0, 玩的次數
	 */
	FOUR_SYMBOL_PLAY_REQUEST(210003),

	FOUR_SYMBOL_REWARD_BOARD_REQUEST(210006), // 獎勵公告區請求

	FOUR_SYMBOL_REWARD_AREA_REQUEST(210007), // 獎勵區請求

	FOUR_SYMBOL_PUT_ONE_IN_BAG_REQUEST(210008), // 單擊放入包包請求

	FOUR_SYMBOL_PUT_ALL_IN_BAG_REQUEST(210009), // 所有放入包包請求

	/**
	 * 0, String, 發送者id
	 * 
	 * 1, byte[], 位元陣列
	 */
	FOUR_SYMBOL_BENCHMARK_REQUEST(210198),

	/**
	 * 0, String, 發送者id
	 * 
	 * 1, byte[], 位元陣列
	 */
	FOUR_SYMBOL_BENCHMARK_RETURN_REQUEST(210199),

	// ---------------------------------------------------
	// 四象回應
	// ---------------------------------------------------
	/**
	 * 初始回應
	 */
	FOUR_SYMBOL_INITIALIZE_REPONSE(210051),

	/**
	 * 玩的回應
	 * 
	 * 0, int, errorCode 錯誤碼
	 * 
	 * 1, int, 每日已轉生次數
	 * 
	 * 2, int,結果總數(會有多次轉生結果)
	 * 
	 * {
	 * 
	 * 3, String,轉生結果
	 * 
	 * 4, int , 獎勵等級
	 * 
	 * 5, int , 道具總數
	 * 
	 * {
	 * 
	 * 6, String, 道具code
	 * 
	 * 7, int , 道具數量
	 * 
	 * }}
	 */
	FOUR_SYMBOL_PLAY_RESPONSE(210053),

	FOUR_SYMBOL_RESET_DAILY_TIMES_REPONSE(210054), // 每日次數重置回應

	FOUR_SYMBOL_PLAY_BOARD_MESSAGE_REPONSE(210055), // play獲獎訊息回應

	FOUR_SYMBOL_REWARD_BOARD_REPONSE(210056), // 獎勵公告區回應

	FOUR_SYMBOL_REWARD_AREA_REPONSE(210057), // 獎勵區回應

	FOUR_SYMBOL_PUT_ONE_IN_BAG_REPONSE(210058), // 單擊放入包包回應

	FOUR_SYMBOL_PUT_ALL_IN_BAG_REPONSE(210059), // 所有放入包包回應

	/**
	 * 0, String, 發送者id
	 * 
	 * 1, byte[], 位元陣列
	 */
	FOUR_SYMBOL_BENCHMARK_RETURN_RESPONSE(210299),

	// ---------------------------------------------------
	// 卜卦
	// ---------------------------------------------------
	DIVINATION_PLAY_REQUEST(211003), // 玩的請求

	;

	private final int intValue;

	private CoreMessageType(int intValue) {
		this.intValue = intValue;
	}

	public int getValue() {
		return intValue;
	}
}
