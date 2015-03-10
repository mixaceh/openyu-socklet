package org.openyu.socklet.message.vo.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openyu.commons.enumz.BooleanEnum;
import org.openyu.commons.enumz.CharEnum;
import org.openyu.commons.enumz.DoubleEnum;
import org.openyu.commons.enumz.FloatEnum;
import org.openyu.commons.enumz.IntEnum;
import org.openyu.commons.enumz.LongEnum;
import org.openyu.commons.enumz.StringEnum;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.StringHelper;
import org.openyu.commons.model.supporter.BaseModelSupporter;
import org.openyu.commons.util.CollectionHelper;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.MessageType;
import org.openyu.socklet.message.vo.ModuleType;
import org.openyu.socklet.message.vo.PriorityType;

public class MessageImpl extends BaseModelSupporter implements Message {

	private static final long serialVersionUID = 8570316403610592401L;

	/**
	 * id, 唯一碼
	 * 
	 * use UUID.randomUUID()
	 * 
	 * @return
	 */
	private String id;

	/**
	 * 時間戳
	 */
	private long timeStamp;

	private ModuleType srcModule;

	private ModuleType destModule;

	private MessageType messageType;

	private CategoryType categoryType;

	private PriorityType priorityType;

	private List<byte[]> contents = new LinkedList<byte[]>();

	private List<Class<?>> classes = new LinkedList<Class<?>>();

	private String sender;

	private List<String> receivers = new LinkedList<String>();

	public MessageImpl(CategoryType categoryType, PriorityType priorityType) {
		this.categoryType = categoryType;
		this.priorityType = priorityType;
		//
		this.id = UUID.randomUUID().toString();
		this.timeStamp = System.nanoTime();
	}

	/**
	 * server side用
	 *
	 * @param categoryType
	 * @param priorityType
	 * @param srcModule
	 * @param destModule
	 * @param messageType
	 * @param receivers
	 */
	public MessageImpl(CategoryType categoryType, PriorityType priorityType,
			ModuleType srcModule, ModuleType destModule,
			MessageType messageType, List<String> receivers) {
		this(categoryType, priorityType);
		//
		this.srcModule = srcModule;
		this.destModule = destModule;
		this.messageType = messageType;
		//
		this.receivers = receivers;
	}

	public MessageImpl(CategoryType categoryType, PriorityType priorityType,
			ModuleType srcModule, ModuleType destModule, MessageType messageType) {
		this(categoryType, priorityType);
		//
		this.srcModule = srcModule;
		this.destModule = destModule;
		this.messageType = messageType;
	}

	/**
	 * 建構Message, 模擬client side用
	 *
	 * @param categoryType
	 * @param priorityType
	 * @param sender
	 * @param messageType
	 * @return
	 */
	public MessageImpl(CategoryType categoryType, PriorityType priorityType,
			String sender, MessageType messageType) {
		this(categoryType, priorityType);
		//
		this.sender = sender;
		this.messageType = messageType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public ModuleType getSrcModule() {
		return srcModule;
	}

	public void setSrcModule(ModuleType origModule) {
		this.srcModule = origModule;
	}

	public ModuleType getDestModule() {
		return destModule;
	}

	public void setDestModule(ModuleType destModule) {
		this.destModule = destModule;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public CategoryType getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(CategoryType categoryType) {
		this.categoryType = categoryType;
	}

	public PriorityType getPriorityType() {
		return priorityType;
	}

	public void setPriorityType(PriorityType priorityType) {
		this.priorityType = priorityType;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public List<String> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<String> receivers) {
		this.receivers = receivers;
	}

	public List<byte[]> getContents() {
		return contents;
	}

	public void setContents(List<byte[]> contents) {
		this.contents = contents;
	}

	public boolean addReceiver(String receiver) {
		boolean result = false;
		if (receivers != null) {
			result = receivers.add(receiver);
		}
		return result;
	}

	public boolean removeReceiver(String receiver) {
		boolean result = false;
		if (CollectionHelper.notEmpty(receivers)) {
			result = receivers.remove(receiver);
		}
		return result;
	}

	public String getReceiver() {
		String result = null;
		if (CollectionHelper.notEmpty(receivers)) {
			result = receivers.get(0);
		}
		return result;
	}

	public void addBoolean(BooleanEnum value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(BooleanEnum.class);
	}

	public void addBoolean(boolean value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(boolean.class);
	}

	public void addBoolean(Boolean value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(Boolean.class);
	}

	public void addChar(CharEnum value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(CharEnum.class);
	}

	public void addChar(Character value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(Character.class);
	}

	public void addChar(char value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(char.class);
	}

	public void addString(StringEnum value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(StringEnum.class);
	}

	public void addString(String value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(String.class);
	}

	public void addInt(IntEnum value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(IntEnum.class);
	}

	public void addInt(Integer value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(Integer.class);
	}

	public void addInt(int value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(int.class);
	}

	public void addLong(LongEnum value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(LongEnum.class);
	}

	public void addLong(Long value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(Long.class);
	}

	public void addLong(long value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(long.class);
	}

	public void addFloat(FloatEnum value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(FloatEnum.class);
	}

	public void addFloat(Float value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(Float.class);
	}

	public void addFloat(float value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(float.class);
	}

	public void addDouble(DoubleEnum value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(DoubleEnum.class);
	}

	public void addDouble(Double value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(Double.class);
	}

	public void addDouble(double value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(double.class);
	}

	public void addByteArray(byte[] value) {
		contents.add(value);
		classes.add(byte[].class);
	}

	public void addObject(Serializable value) {
		contents.add(ByteHelper.toByteArray(value));
		classes.add(Object.class);
	}

	public boolean getBoolean(int index) {
		byte[] value = contents.get(index);
		return ByteHelper.toBoolean(value);
	}

	public char getChar(int index) {
		byte[] value = contents.get(index);
		return ByteHelper.toChar(value);
	}

	public String getString(int index) {
		byte[] value = contents.get(index);
		return ByteHelper.toString(value);
	}

	public int getInt(int index) {
		byte[] value = contents.get(index);
		return ByteHelper.toInt(value);
	}

	public long getLong(int index) {
		byte[] value = contents.get(index);
		return ByteHelper.toLong(value);
	}

	public float getFloat(int index) {
		byte[] value = contents.get(index);
		return ByteHelper.toFloat(value);
	}

	public double getDouble(int index) {
		byte[] value = contents.get(index);
		return ByteHelper.toDouble(value);
	}

	public byte[] getByteArray(int index) {
		return contents.get(index);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(int index) {
		byte[] value = contents.get(index);
		return (T) ByteHelper.toObject(value);
	}

	public List<Class<?>> getClasses() {
		return classes;
	}

	public Class<?> getClass(int index) {
		return classes.get(index);
	}

	// T[94] from [slave1] (152203) CHAT_CHAT_REPONSE => CLIENT to [TEST_ROLE]
	// 0, 1000, 1000, 632, 13, 1, POTION_0001, 1, 251, 13, 1
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this,
				ToStringStyle.SIMPLE_STYLE);
		StringBuilder buff = new StringBuilder();

		// thrad id
		buff.append("T[" + Thread.currentThread().getId() + "]");

		// 發送者
		if (sender != null) {
			buff.append(" from [" + sender + "]");
		}

		// message.append(categoryType + ", ");

		// //來源模組
		// if (srcModule != null)
		// {
		// //message.append(", (" + srcModule.intValue() + ") " + srcModule);
		// buff.append(", " + srcModule);
		// }
		// 訊息編號,類別
		if (messageType != null) {
			buff.append(" (" + messageType.getValue() + ") " + messageType);
		}
		// 目的模組
		if (destModule != null) {
			// message.append(" => (" + destModule.intValue() + ") " +
			// destModule);
			buff.append(" => " + destModule);
		}

		// 接收者
		if (!CollectionHelper.isEmpty(receivers)) {
			buff.append(" to " + receivers);
		}

		// content
		if (sender == null) {
			StringBuilder content = new StringBuilder();
			for (int i = 0; i < contents.size(); i++) {
				Object object = null;
				//
				Class<?> clazz = getClass(i);
				if (BooleanEnum.class.equals(clazz)
						|| Boolean.class.equals(clazz)
						|| boolean.class.equals(clazz)) {
					object = getBoolean(i);
				} else if (CharEnum.class.equals(clazz)
						|| Character.class.equals(clazz)
						|| char.class.equals(clazz)) {
					object = getChar(i);
				} else if (StringEnum.class.equals(clazz)
						|| String.class.equals(clazz)) {
					object = getString(i);
				} else if (IntEnum.class.equals(clazz)
						|| Integer.class.equals(clazz)
						|| int.class.equals(clazz)) {
					object = getInt(i);
				} else if (LongEnum.class.equals(clazz)
						|| Long.class.equals(clazz) || long.class.equals(clazz)) {
					object = getLong(i);
				} else if (FloatEnum.class.equals(clazz)
						|| Float.class.equals(clazz)
						|| float.class.equals(clazz)) {
					object = getFloat(i);
				} else if (DoubleEnum.class.equals(clazz)
						|| Double.class.equals(clazz)
						|| double.class.equals(clazz)) {
					object = getDouble(i);
				} else if (byte[].class.equals(clazz)) {
					object = getByteArray(i);
				}
				// object
				else if (Object.class.equals(clazz)) {
					object = getObject(i);
				} else {
					// just for pretty
				}
				//
				content.append(object);
				if (i < contents.size() - 1) {
					content.append(StringHelper.COMMA);
				}
			}

			// 內容,TEST_ROLE,測試角色,50000,1,200000,0
			if (content.length() > 0) {
				buff.append(StringHelper.LF);
				buff.append(content);
			}
		}
		//
		builder.append(buff);
		return builder.toString();
	}

	public boolean equals(Object object) {
		if (!(object instanceof MessageImpl)) {
			return false;
		}
		if (this == object) {
			return true;
		}
		MessageImpl other = (MessageImpl) object;
		return new EqualsBuilder().append(id, other.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	public Object clone() {
		MessageImpl copy = null;
		copy = (MessageImpl) super.clone();
		copy.contents = clone(contents);
		copy.classes = clone(classes);
		copy.receivers = clone(receivers);
		return copy;
	}

}
