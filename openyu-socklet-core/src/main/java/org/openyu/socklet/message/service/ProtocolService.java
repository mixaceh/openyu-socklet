package org.openyu.socklet.message.service;

import java.io.Serializable;
import java.util.List;

import org.openyu.commons.security.SecurityType;
import org.openyu.commons.service.BaseService;
import org.openyu.commons.util.ChecksumType;
import org.openyu.commons.util.CompressType;
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
	 * 是否檢查碼
	 * 
	 * @return
	 */
	boolean isChecksum();

	void setChecksum(boolean checksum);

	/**
	 * 檢查碼類別
	 * 
	 * @return
	 */
	ChecksumType getChecksumType();

	void setChecksumType(ChecksumType checksumType);

	/**
	 * 檢查碼key
	 * 
	 * @return
	 */
	String getChecksumKey();

	void setChecksumKey(String checksumKey);

	/**
	 * 是否加密
	 * 
	 * @return
	 */
	boolean isSecurity();

	void setSecurity(boolean security);

	/**
	 * 加密類別
	 * 
	 * @return
	 */
	SecurityType getSecurityType();

	void setSecurityType(SecurityType securityType);

	/**
	 * 加密key
	 * 
	 * @return
	 */
	String getSecurityKey();

	void setSecurityKey(String securityKey);

	/**
	 * 是否壓縮
	 * 
	 * @return
	 */
	boolean isCompress();

	void setCompress(boolean compress);

	/**
	 * 壓縮類別
	 * 
	 * @return
	 */
	CompressType getCompressType();

	void setCompressType(CompressType compressType);

	// ------------------------------------------
	// handshake
	// ------------------------------------------
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

	// ------------------------------------------
	// assemble
	// ------------------------------------------
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
	 * @param moduleTypeClass
	 * @param messageTypeClass
	 * @return
	 */
	<T extends Enum<T>, U extends Enum<U>> List<Message> disassemble(
			byte[] values, Class<T> moduleTypeClass, Class<U> messageTypeClass);

	// ------------------------------------------
	// 2014/12/15
	// ------------------------------------------
	byte[] encode(HeadType headType);

	byte[] encode(HeadType headType, boolean body);

	byte[] encode(HeadType headType, char body);

	byte[] encode(HeadType headType, String body);

	byte[] encode(HeadType headType, String body, String charsetName);

	byte[] encode(HeadType headType, byte body);

	byte[] encode(HeadType headType, short body);

	byte[] encode(HeadType headType, int body);

	byte[] encode(HeadType headType, long body);

	byte[] encode(HeadType headType, float body);

	byte[] encode(HeadType headType, double body);

	byte[] encode(HeadType headType, byte[] body);

	byte[] encode(HeadType headType, Serializable body);

	Packet<byte[]> decode(byte[] values);
}
