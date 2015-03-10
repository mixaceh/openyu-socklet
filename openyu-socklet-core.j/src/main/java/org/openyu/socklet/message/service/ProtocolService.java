package org.openyu.socklet.message.service;

import java.io.Serializable;
import java.util.List;
import org.openyu.commons.service.BaseService;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.HeadType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.Packet;

/**
 * 協定服務
 * 
 * 組成/反組byte[]
 */
public interface ProtocolService extends BaseService {

	/**
	 * 握手協定,組成byte[]
	 * 
	 * @param categoryType
	 * @param authKey
	 * @param sender
	 * @return
	 */
	byte[] handshake(CategoryType categoryType, byte[] authKey, String sender);

	/**
	 * 反握手協定,反組byte[]
	 * 
	 * @param values
	 * @return
	 */
	Message dehandshake(byte[] values);

	/**
	 * 訊息協定,組成byte[]
	 * 
	 * @param message
	 * @return
	 */
	byte[] assemble(Message message);

	/**
	 * 反訊息協定,反組byte[]
	 * 
	 * @param values
	 * @param moduleEnumType
	 * @param messageEnumType
	 * @return
	 */
	<T extends Enum<T>, U extends Enum<U>> List<Message> disassemble(
			byte[] values, Class<T> moduleEnumType, Class<U> messageEnumType);

	// ------------------------------------------
	// 2014/12/15
	// ------------------------------------------
	byte[] encode(HeadType headType);

	byte[] encode(HeadType headType, boolean body);

	byte[] encode(HeadType headType, char body);

	byte[] encode(HeadType headType, String body);

	public byte[] encode(HeadType headType, String body, String charsetName);

	byte[] encode(HeadType headType, byte body);

	byte[] encode(HeadType headType, short body);

	byte[] encode(HeadType headType, int body);

	byte[] encode(HeadType headType, long body);

	byte[] encode(HeadType headType, float body);

	byte[] encode(HeadType headType, double body);

	byte[] encode(HeadType headType, byte[] body);

	byte[] encode(HeadType headType, Serializable body);

	Packet<?> decode(byte[] values);
}
