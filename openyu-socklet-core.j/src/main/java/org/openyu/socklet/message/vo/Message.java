package org.openyu.socklet.message.vo;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.openyu.commons.enumz.BooleanEnum;
import org.openyu.commons.enumz.CharEnum;
import org.openyu.commons.enumz.DoubleEnum;
import org.openyu.commons.enumz.FloatEnum;
import org.openyu.commons.enumz.IntEnum;
import org.openyu.commons.enumz.LongEnum;
import org.openyu.commons.enumz.StringEnum;
import org.openyu.commons.model.BaseModel;

//訊息傳遞
//String,int,long,float,double -> byte[]
//byte[] -> String,int,long,float,double
public interface Message extends BaseModel {

	// //category
	// public static final byte CATEGORY_SIGNAL = 1; //StreamType.CLIENT
	//
	// public static final byte CATEGORY_MESSAGE = 2;//StreamType.SERVER
	//
	// public static final byte CATEGORY_FILE = 3;//StreamType.SERVER
	//
	// public static final byte CATEGORY_INTERNAL = 4; //StreamType.SERVER

	/**
	 * id, 唯一碼
	 * 
	 * use UUID.randomUUID()
	 * 
	 * @return
	 */
	String getId();

	void setId(String id);

	/**
	 * 時間戳
	 * 
	 * @return
	 */
	long getTimeStamp();

	/**
	 * 來源模組
	 * 
	 * @return
	 */
	ModuleType getSrcModule();

	void setSrcModule(ModuleType srcModule);

	/**
	 * 目的模組
	 * 
	 * @return
	 */
	ModuleType getDestModule();

	void setDestModule(ModuleType destModule);

	/**
	 * 訊息類別,用於決定到socklet後,邏輯處理的轉發,如同 method
	 * 
	 * @return
	 */
	MessageType getMessageType();

	void setMessageType(MessageType messageType);

	/**
	 * 種類類別
	 * 
	 * @return
	 */
	CategoryType getCategoryType();

	void setCategoryType(CategoryType categoryType);

	/**
	 * 優先權類別
	 * 
	 * @return
	 */
	PriorityType getPriorityType();

	void setPriorityType(PriorityType priorityType);

	// ---------------------------------------------------
	/**
	 * 發送者
	 * 
	 * @return
	 */
	String getSender();

	void setSender(String sender);

	/**
	 * 接收者
	 * 
	 * @return
	 */
	List<String> getReceivers();

	void setReceivers(List<String> receivers);

	boolean addReceiver(String receiver);

	boolean removeReceiver(String receiver);

	/**
	 * 接收者
	 * 
	 * @return
	 */
	String getReceiver();

	/**
	 * 訊息內容
	 * 
	 * @return
	 */
	List<byte[]> getContents();

	void setContents(List<byte[]> contents);

	// --------------------------------------------------
	// 加入
	// --------------------------------------------------
	void addBoolean(BooleanEnum value);

	void addBoolean(Boolean value);

	void addBoolean(boolean value);

	void addChar(CharEnum value);

	void addChar(Character value);

	void addChar(char value);

	void addString(StringEnum value);

	void addString(String value);

	void addInt(IntEnum value);

	void addInt(Integer value);

	void addInt(int value);

	void addLong(LongEnum value);

	void addLong(Long value);

	void addLong(long value);

	void addFloat(FloatEnum value);

	void addFloat(Float value);

	void addFloat(float value);

	void addDouble(DoubleEnum value);

	void addDouble(Double value);

	void addDouble(double value);

	void addByteArray(byte[] value);

	void addObject(Serializable value);

	// --------------------------------------------------
	// 取得
	// --------------------------------------------------
	boolean getBoolean(int index);

	char getChar(int index);

	String getString(int index);

	int getInt(int index);

	long getLong(int index);

	float getFloat(int index);

	double getDouble(int index);

	byte[] getByteArray(int index);

	<T> T getObject(int index);

	/**
	 * 訊息內容[]相對應的class
	 * 
	 * @return
	 */
	List<Class<?>> getClasses();

	Class<?> getClass(int index);
}