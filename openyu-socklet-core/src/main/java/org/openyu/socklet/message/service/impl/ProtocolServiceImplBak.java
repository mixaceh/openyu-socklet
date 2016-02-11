package org.openyu.socklet.message.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openyu.commons.enumz.EnumHelper;
import org.openyu.commons.io.IoHelper;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.commons.lang.StringHelper;
import org.openyu.commons.security.SecurityType;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.commons.util.ChecksumType;
import org.openyu.commons.util.CompressType;
import org.openyu.commons.util.ChecksumProcessor;
import org.openyu.commons.util.CompressProcessor;
import org.openyu.commons.util.impl.ChecksumProcessorImpl;
import org.openyu.commons.util.impl.CompressProcessorImpl;
import org.openyu.commons.security.SecurityProcessor;
import org.openyu.commons.security.impl.SecurityProcessorImpl;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.MessageType;
import org.openyu.socklet.message.vo.ModuleType;
import org.openyu.socklet.message.vo.PriorityType;
import org.openyu.socklet.message.vo.impl.MessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 協定服務
 * 
 * 組成/反組byte[]
 */
public abstract class ProtocolServiceImplBak extends BaseServiceSupporter implements
		ProtocolService {

	private static final long serialVersionUID = 7562819271771546706L;

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(ProtocolServiceImplBak.class);

	/** 檢查碼 */
	private ChecksumProcessor checksumable = new ChecksumProcessorImpl();

	/** 安全性 */
	private SecurityProcessor securityable = new SecurityProcessorImpl();

	/** 壓縮 */
	private CompressProcessor compressable = new CompressProcessorImpl();

	public ProtocolServiceImplBak() {
	}

	/**
	 * 是否檢查碼
	 * 
	 * @return
	 */
	public boolean isChecksum() {
		return checksumable.isChecksum();
	}

	public void setChecksum(boolean checksum) {
		checksumable.setChecksum(checksum);
	}

	/**
	 * 檢查碼類別
	 * 
	 * @return
	 */
	public ChecksumType getChecksumType() {
		return checksumable.getChecksumType();
	}

	public void setChecksumType(ChecksumType checksumType) {
		checksumable.setChecksumType(checksumType);
	}

	/**
	 * 檢查碼key
	 * 
	 * @return
	 */
	public String getChecksumKey() {
		return checksumable.getChecksumKey();
	}

	public void setChecksumKey(String checksumKey) {
		checksumable.setChecksumKey(checksumKey);
	}

	/**
	 * 檢查碼類別列舉
	 * 
	 * @return
	 */
	public Set<ChecksumType> getChecksumTypes() {
		return checksumable.getChecksumTypes();
	}

	public void setChecksumTypes(Set<ChecksumType> checksumTypes) {
		checksumable.setChecksumTypes(checksumTypes);
	}

	/**
	 * 檢查碼
	 * 
	 * @param checksumTypeValue
	 *            檢查碼類別
	 * @see ChecksumType
	 * @param values
	 * @return
	 */
	public long checksum(String checksumTypeValue, byte[] values) {
		return checksumable.checksum(checksumTypeValue, values);
	}

	/**
	 * 檢查碼
	 * 
	 * @param checksumTypeValue
	 *            檢查碼類別
	 * @see ChecksumType
	 * @param values
	 * @return
	 */
	public long checksum(int checksumTypeValue, byte[] values) {
		return checksumable.checksum(checksumTypeValue, values);
	}

	/**
	 * 檢查碼
	 * 
	 * @param values
	 * @return
	 */
	public long checksum(byte[] values) {
		return checksumable.checksum(values);
	}

	/**
	 * 是否加密
	 * 
	 * @return
	 */
	public boolean isSecurity() {
		return securityable.isSecurity();
	}

	public void setSecurity(boolean security) {
		securityable.setSecurity(security);
	}

	/**
	 * 加密類別
	 * 
	 * @return
	 */
	public SecurityType getSecurityType() {
		return securityable.getSecurityType();
	}

	public void setSecurityType(SecurityType securityType) {
		securityable.setSecurityType(securityType);
	}

	/**
	 * 加密key
	 * 
	 * @return
	 */
	public String getSecurityKey() {
		return securityable.getSecurityKey();
	}

	public void setSecurityKey(String securityKey) {
		securityable.setSecurityKey(securityKey);
	}

	/**
	 * 安全性類別列舉
	 * 
	 * @return
	 */
	public Set<SecurityType> getSecurityTypes() {
		return securityable.getSecurityTypes();
	}

	public void setSecurityTypes(Set<SecurityType> securityTypes) {
		securityable.setSecurityTypes(securityTypes);
	}

	/**
	 * 加密
	 * 
	 * @param securityTypeValue
	 *            安全性類別
	 * @see SecurityType
	 * @param values
	 * @return
	 */
	public byte[] encrypt(String securityTypeValue, byte[] values) {
		return securityable.encrypt(securityTypeValue, values);
	}

	/**
	 * 加密
	 * 
	 * @param values
	 * @return
	 */
	public byte[] encrypt(byte[] values) {
		return securityable.encrypt(values);
	}

	/**
	 * 解密
	 * 
	 * @param securityTypeValue
	 *            安全性類別
	 * @see SecurityType
	 * @param values
	 * @return
	 */
	public byte[] decrypt(String securityTypeValue, byte[] values) {
		return securityable.decrypt(securityTypeValue, values);
	}

	/**
	 * 解密
	 * 
	 * @param securityType
	 *            安全性類別
	 * @see SecurityType
	 * @param values
	 * @return
	 */
	public byte[] decrypt(SecurityType securityType, byte[] values) {
		return securityable.decrypt(values);
	}

	/**
	 * 是否壓縮
	 * 
	 * @return
	 */
	public boolean isCompress() {
		return compressable.isCompress();
	}

	public void setCompress(boolean compress) {
		compressable.setCompress(compress);
	}

	/**
	 * 壓縮類別
	 * 
	 * @return
	 */
	public CompressType getCompressType() {
		return compressable.getCompressType();
	}

	public void setCompressType(CompressType compressType) {
		compressable.setCompressType(compressType);
	}

	/**
	 * 壓縮類別列舉
	 * 
	 * @return
	 */
	public Set<CompressType> getCompressTypes() {
		return compressable.getCompressTypes();
	}

	public void setCompressTypes(Set<CompressType> compressTypes) {
		compressable.setCompressTypes(compressTypes);
	}

	/**
	 * 壓縮
	 * 
	 * @param compressTypeValue
	 *            壓縮類別
	 * @see CompressType
	 * @param values
	 * @return
	 */
	public byte[] compress(String compressTypeValue, byte[] values) {
		return compressable.compress(compressTypeValue, values);
	}

	/**
	 * 壓縮
	 * 
	 * @param compressTypeValue
	 *            壓縮類別
	 * @see CompressType
	 * @param values
	 * @return
	 */
	public byte[] compress(int compressTypeValue, byte[] values) {
		return compressable.compress(compressTypeValue, values);
	}

	/**
	 * 壓縮
	 * 
	 * @param compressType
	 *            壓縮類別
	 * @see CompressType
	 * @param values
	 * @return
	 */
	public byte[] compress(byte[] values) {
		return compressable.compress(values);
	}

	/**
	 * 解壓
	 * 
	 * @param compressTypeValue
	 *            壓縮類別
	 * @see CompressType
	 * @param values
	 * @return
	 */
	public byte[] uncompress(String compressTypeValue, byte[] values) {
		return compressable.uncompress(compressTypeValue, values);
	}

	/**
	 * 解壓
	 * 
	 * @param compressTypeValue
	 *            壓縮類別
	 * @see CompressType
	 * @param values
	 * @return
	 */
	public byte[] uncompress(int compressTypeValue, byte[] values) {
		return compressable.uncompress(compressTypeValue, values);
	}

	/**
	 * 解壓
	 * 
	 * @param compressTypeValue
	 *            壓縮類別
	 * @see CompressType
	 * @param values
	 * @return
	 */
	public byte[] uncompress(byte[] values) {
		return compressable.uncompress(values);
	}

	/**
	 * 握手協定,組成byte[]
	 * 
	 * @param categoryType
	 * @param authKey
	 * @param sender
	 * @return
	 */
	public byte[] handshake(CategoryType categoryType, byte[] authKey,
			String sender) {
		byte[] result = new byte[0];
		//
		ByteArrayOutputStream out = null;
		// ------------------------------------------
		// checksum, 2013/11/10
		// ------------------------------------------
		ByteArrayOutputStream checksumOut = null;
		try {
			if (authKey == null || authKey.length != 32) {
				LOGGER.error("AuthKey is invalid");
				return result;
			}
			//
			if (StringHelper.isBlank(sender)) {
				LOGGER.error("Sender is blank");
				return result;
			}
			//
			out = new ByteArrayOutputStream();
			checksumOut = new ByteArrayOutputStream();
			{
				// head
				// 0-1,38(short) 6+32
				// 2,2(byte)category
				// 3,3(byte)priority

				// content
				// 4起為authKey內容,長度32
				// 4-35,(byte[32])

				// 36-37,(short)sender.length
				// 38,sender
				// 39-40,(short)checksum.length
				// 41-81,checksum(HmacSHA1=40)

				// ------------------------------------------
				// head
				// ------------------------------------------
				// categoryType
				byte[] categoryTypeBytes = ByteHelper.toByteArray(categoryType
						.getValue());
				checksumOut.write(categoryTypeBytes);

				// priorityType
				byte[] priorityTypeBytes = ByteHelper
						.toByteArray(PriorityType.URGENT.getValue());
				checksumOut.write(priorityTypeBytes);

				// ------------------------------------------
				// content
				// ------------------------------------------
				// authkey
				// totalBytes = ArrayHelper.add(totalBytes, authKey);
				checksumOut.write(authKey);

				// sender
				byte[] senderLengthBytes = ByteHelper.toShortByteArray(sender
						.length());
				// totalBytes = ArrayHelper.add(totalBytes, senderLengthBytes);
				checksumOut.write(senderLengthBytes);

				byte[] senderBytes = ByteHelper.toByteArray(sender);
				// totalBytes = ArrayHelper.add(totalBytes, senderBytes);
				checksumOut.write(senderBytes);

				long checksum = checksum(checksumOut.toByteArray());
				byte[] checksumBytes = ByteHelper.toByteArray(checksum);

				// System.out.println("checksum: "+checksum);
				int checksumLength = checksumBytes.length;
				byte[] checksumLengthBytes = ByteHelper
						.toShortByteArray(checksumLength);

				// 資料總長度
				int totalLength = 2 // totalLength
						+ 1 // categoryTypeLength
						+ 1// priorityTypeLength
						+ 32// authkeyLength
						+ (2 + sender.length())// sender
						+ (2 + checksumLength);// checksum
				byte[] totalLengthBytes = ByteHelper
						.toShortByteArray(totalLength);

				// ------------------------------------------
				// out
				// ------------------------------------------
				out.write(totalLengthBytes);
				out.write(categoryTypeBytes);
				out.write(priorityTypeBytes);
				out.write(authKey);
				out.write(senderLengthBytes);
				out.write(senderBytes);
				out.write(checksumLengthBytes);
				out.write(checksumBytes);
				//
				result = out.toByteArray();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IoHelper.close(out);
			IoHelper.close(checksumOut);
		}

		return result;
	}

	/**
	 * 反握手協定,反組byte[]
	 * 
	 * @param bytes
	 * @return
	 */
	public Message dehandshake(byte[] bytes) {
		Message result = null;
		// old: 0, 49, 1, 1, 97, 97, 99, 99, destModuleType56, 57, 54, 52, 51,
		// 50, 52, 55, 51, 56, 99, 50, 55, 99, 48, 55, 55, 52, 54, 101, 51, 101,
		// 97, 56, 49, 97, 102, 102, 0, 11, 84, 69, 83, 84, 95, 82, 79, 76, 69,
		// 95, 49
		// new: 0, 91, 1, 1, 97, 97, 99, 99, 56, 57, 54, 52, 51, 50, 52, 55, 51,
		// 56, 99, 50, 55, 99, 48, 55, 55, 52, 54, 101, 51, 101, 97, 56, 49, 97,
		// 102, 102, 0, 11, 84, 69, 83, 84, 95, 82, 79, 76, 69, 95, 49, 0, 40,
		// 98, 49, 99, 49, 100, 56, 50, 52, 97, 100, 56, 48, 57, 57, 48, 50,
		// 100, 99, 49, 57, 54, 49, 101, 97, 48, 102, 101, 53, 99, 100, 54, 101,
		// 53, 55, 98, 57, 101, 98, 56, 55
		if (bytes == null) {
			LOGGER.error("Bytes is null");
			return result;
		}

		// checksum, 2013/11/10
		// 收到bytes後,計算的checksum值
		ByteArrayOutputStream expectedChecksumOut = null;
		try {
			// ------------------------------------------
			// head
			// ------------------------------------------
			int pos = 0;
			// 資料總長度
			int totalLength = ByteHelper.fromShortInt(ByteHelper.getByteArray(
					bytes, pos, 2));// 0-1
			pos += 2;
			// System.out.println("totalLength: " + totalLength);

			// 實際收的資料長度
			if (bytes.length < 36) {
				LOGGER.error("Data[" + bytes.length + "] < 36 bytes");
				return result;
			}
			// 實際收的資料長度 < 總資料長度
			else if (bytes.length < totalLength) {
				LOGGER.error("Data[" + bytes.length + "] < [" + totalLength
						+ "] bytes");
				return result;
			}

			expectedChecksumOut = new ByteArrayOutputStream();
			//
			byte[] categoryTypeBytes = ByteHelper.getByteArray(bytes, pos, 1);
			byte categoryTypeValue = ByteHelper.toByte(categoryTypeBytes);// 2
			// System.out.println("category: " + categoryValue);
			CategoryType categoryType = EnumHelper.valueOf(CategoryType.class,
					categoryTypeValue);
			expectedChecksumOut.write(categoryTypeBytes);
			pos += 1;
			//
			byte[] priorityTypeBytes = ByteHelper.getByteArray(bytes, pos, 1);
			byte priorityTypeValue = ByteHelper.toByte(priorityTypeBytes);// 3
			// System.out.println("priority: " + priorityValue);
			PriorityType priorityType = EnumHelper.valueOf(PriorityType.class,
					priorityTypeValue);
			expectedChecksumOut.write(priorityTypeBytes);
			pos += 1;

			// ------------------------------------------
			// CategoryType.HANDSHAKE
			// ------------------------------------------
			if (CategoryType.HANDSHAKE_CLIENT.equals(categoryType)
					|| CategoryType.HANDSHAKE_RELATION.equals(categoryType)
					|| CategoryType.HANDSHAKE_SERVER.equals(categoryType)) {
				// ------------------------------------------
				// content
				// ------------------------------------------
				byte[] authkey = ByteHelper.getByteArray(bytes, pos, 32);
				expectedChecksumOut.write(authkey);
				pos += 32;

				// sender
				byte[] senderLengthBytes = ByteHelper.getByteArray(bytes, pos,
						2);
				int senderLength = ByteHelper.fromShortInt(senderLengthBytes);
				expectedChecksumOut.write(senderLengthBytes);
				pos += 2;
				//
				byte[] senderBytes = ByteHelper.getByteArray(bytes, pos,
						senderLength);
				StringBuilder sender = new StringBuilder();
				sender.append(ByteHelper.toString(senderBytes));
				expectedChecksumOut.write(senderBytes);
				pos += senderLength;

				// 實際的checksum
				int checksumLength = ByteHelper.fromShortInt(ByteHelper
						.getByteArray(bytes, pos, 2));
				pos += 2;
				//
				byte[] checksumBytes = ByteHelper.getByteArray(bytes, pos,
						checksumLength);
				pos += checksumLength;
				if (checksumBytes == null) {
					LOGGER.error("Checksum is null");
					return result;
				}
				long checksum = ByteHelper.toLong(checksumBytes);
				// System.out.println(checksum);

				// 預期的checksum
				byte[] expectedChecksumBytes = ByteHelper
						.toByteArray(checksum(expectedChecksumOut.toByteArray()));
				long expectedChecksum = ByteHelper
						.toLong(expectedChecksumBytes);
				// System.out.println(expectedChecksum);

				if (expectedChecksum != checksum) {
					LOGGER.error("Expected checksum [" + expectedChecksum
							+ "] is not equal [" + checksum + "]");
					return result;
				}

				// message
				// ------------------------------------------
				result = new MessageImpl(categoryType, priorityType);
				// ------------------------------------------
				result.setSender(sender.toString());
				//
				result.addByteArray(authkey);// 0,String
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IoHelper.close(expectedChecksumOut);
		}
		return result;
	}

	/**
	 * 訊息協定,組成byte[]
	 * 
	 * @param message
	 * @return
	 * 
	 *         CategoryType.MESSAGE_CLIENT
	 * 
	 *         messageType,sender
	 * 
	 *         CategoryType.MESSAGE_SERVER,MESSAGE_RELATION,MESSAGE_SYNC
	 * 
	 *         messageType,sender,srcModule,destModule,receivers
	 */
	public byte[] assemble(Message message) {
		byte[] result = new byte[0];
		//
		if (message != null) {
			ByteArrayOutputStream out = null;
			ByteArrayOutputStream contentLengthOut = null;
			ByteArrayOutputStream contentOut = null;
			// ------------------------------------------
			// checksum, 2013/11/12
			// ------------------------------------------
			ByteArrayOutputStream checksumContentLengthOut = null;
			ByteArrayOutputStream checksumContentOut = null;
			ByteArrayOutputStream checksumOut = null;
			try {
				// 訊息
				Message cloneMessage = (Message) message.clone();
				// debug
				// System.out.println(cloneMessage);

				List<byte[]> contents = cloneMessage.getContents();
				out = new ByteArrayOutputStream();

				// lengthOut:
				// 1.長度為4,資料為4 ->(short)byte[] 長度變2,資料為4
				// 2.長度為4,資料為2 ->(short)byte[] 長度變2,資料為2
				// 將int型態 length -> 變為short型態, 且轉為 byte[]存入
				contentLengthOut = new ByteArrayOutputStream();
				contentOut = new ByteArrayOutputStream();
				//
				checksumContentLengthOut = new ByteArrayOutputStream();
				checksumContentOut = new ByteArrayOutputStream();
				checksumOut = new ByteArrayOutputStream();
				{
					// 內容
					for (byte[] content : contents) {
						try {
							byte[] contentLengthBytes = ByteHelper
									.toShortByteArray(content.length);
							contentLengthOut.write(contentLengthBytes);
							checksumContentLengthOut.write(contentLengthBytes);

							contentOut.write(content);
							checksumContentOut.write(content);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

					// ------------------------------------------
					// MESSAGE_CLIENT
					// ------------------------------------------
					// head
					// 0-1,(short),totalLength=31, 8+內容各長度的總長度+內容總長度=總長度
					// =>改為:0-3,(int),totalLength=33, 10+內容各長度的總長度+內容總長度=總長度

					// 4, (byte) ,category=11
					// 5, (byte) ,priority=3
					// 6-9,(int) ,messageType=21003

					// 10-11,(short),sender.length=9,TEST_ROLE長度,原為int會變成 short
					// 12-20,(String),sender=TEST_ROLE

					// ------------------------------------------
					// MESSAGE_SERVER
					// ------------------------------------------
					// 21-24,(int),srcModule.intValue
					// 25-28,(int),destModule.intValue
					// 29-30,(short),receivers.size
					// 31-32,(short),receive.length=9,TEST_ROLE長度,原為int會變成 short
					// 33-41,(String),receive=TEST_ROLE

					// ------------------------------------------
					// content
					// ------------------------------------------
					// 21-22,(short),contents.length,內容個數

					// contentLengthOut
					// 23-24,(short),times.length=2,玩幾次長度,原為int會變成 short

					// contentOut
					// 25-28,(int),times=1玩幾次
					// 0, 0, 0, 29, 10, 3, 0, 3, 52, 83, 0, 9, 84, 69, 83, 84,
					// 95, 82, 79, 76, 69, 0, 1, 0, 4, 0, 0, 0, 1

					String sender = cloneMessage.getSender();
					// 資料總長度
					int totalLength = (4 + 1 + 1 + 4) + (2 + sender.length())
							+ (2 + contentLengthOut.size() + contentOut.size())
							// checksum
							+ 8;

					// ------------------------------------------
					// MESSAGE_RELATION
					// MESSAGE_SERVER
					// MESSAGE_SYNC
					// ------------------------------------------
					if (CategoryType.MESSAGE_RELATION.equals(cloneMessage
							.getCategoryType())
							|| CategoryType.MESSAGE_SERVER.equals(cloneMessage
									.getCategoryType())
							|| CategoryType.MESSAGE_SYNC.equals(cloneMessage
									.getCategoryType())) {
						// 接收者
						int receiverLength = 0;
						for (String receiver : cloneMessage.getReceivers()) {
							receiverLength += 2 + receiver.length();
						}
						// 總長度
						totalLength = (4 + 1 + 1 + 4)
								+ (2 + sender.length())
								+ (4 + 4)
								+ (2 + receiverLength)
								+ (2 + contentLengthOut.size() + contentOut
										.size())
								// checksum
								+ 8;
					}
					// System.out.println("assemble totalLength..." +
					// totalLength);

					// ------------------------------------------
					// head
					// ------------------------------------------
					out.write(ByteHelper.toByteArray(totalLength));

					// categoryType
					byte[] categoryTypeBytes = ByteHelper
							.toByteArray(cloneMessage.getCategoryType()
									.getValue());
					out.write(categoryTypeBytes);
					checksumOut.write(categoryTypeBytes);

					// priorityType
					byte[] priorityTypeBytes = ByteHelper
							.toByteArray(cloneMessage.getPriorityType()
									.getValue());
					out.write(priorityTypeBytes);
					checksumOut.write(priorityTypeBytes);

					// messageType
					byte[] messageTypeBytes = ByteHelper
							.toByteArray(cloneMessage.getMessageType()
									.getValue());
					out.write(messageTypeBytes);
					checksumOut.write(messageTypeBytes);

					// sender
					byte[] senderLengthBytes = ByteHelper
							.toShortByteArray(sender.length());
					out.write(senderLengthBytes);
					checksumOut.write(senderLengthBytes);

					byte[] senderBytes = ByteHelper.toByteArray(sender);
					out.write(senderBytes);
					checksumOut.write(senderBytes);
					//
					if (CategoryType.MESSAGE_SERVER.equals(message
							.getCategoryType())
							|| CategoryType.MESSAGE_RELATION.equals(message
									.getCategoryType())
							|| CategoryType.MESSAGE_SYNC.equals(message
									.getCategoryType())) {
						// 來源模組
						byte[] srcModuleTypeBytes = ByteHelper
								.toByteArray(cloneMessage.getSrcModule()
										.getValue());
						out.write(srcModuleTypeBytes);
						checksumOut.write(srcModuleTypeBytes);

						// 目的模組
						byte[] destModuleTypeBytes = ByteHelper
								.toByteArray(cloneMessage.getDestModule()
										.getValue());
						out.write(destModuleTypeBytes);
						checksumOut.write(destModuleTypeBytes);

						// 接收者大小
						int receiversSize = cloneMessage.getReceivers().size();
						byte[] receiversSizeBytes = ByteHelper
								.toShortByteArray(receiversSize);
						out.write(receiversSizeBytes);
						checksumOut.write(receiversSizeBytes);
						//
						for (String receiver : cloneMessage.getReceivers()) {
							byte[] receiverLengthBytes = ByteHelper
									.toShortByteArray(receiver.length());
							out.write(receiverLengthBytes);
							checksumOut.write(receiverLengthBytes);

							byte[] receiverBytes = ByteHelper
									.toByteArray(receiver);
							out.write(receiverBytes);
							checksumOut.write(receiverBytes);
						}
					}
					// ------------------------------------------
					// content
					// ------------------------------------------
					int contentsSize = contents.size();
					byte[] contentsSizeBytes = ByteHelper
							.toShortByteArray(contentsSize);
					out.write(contentsSizeBytes);
					checksumOut.write(contentsSizeBytes);
					//
					contentLengthOut.writeTo(out);
					checksumContentLengthOut.writeTo(checksumOut);

					contentOut.writeTo(out);
					checksumContentOut.writeTo(checksumOut);

					// 檢查碼
					long checksum = checksum(checksumOut.toByteArray());
					// System.out.println(checksum);
					byte[] checksumBytes = ByteHelper.toByteArray(checksum);
					out.write(checksumBytes);
					//
					result = out.toByteArray();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				IoHelper.close(out);
				IoHelper.close(contentLengthOut);
				IoHelper.close(contentOut);
				IoHelper.close(checksumContentLengthOut);
				IoHelper.close(checksumContentOut);
				IoHelper.close(checksumOut);
			}
		}
		return result;
	}

	/**
	 * 反訊息協定,反組byte[]
	 * 
	 * @param bytes
	 * @param moduleEnumType
	 * @param messageEnumType
	 * @return
	 */
	public <T extends Enum<T>, U extends Enum<U>> List<Message> disassemble(
			byte[] bytes, Class<T> moduleEnumType, Class<U> messageEnumType) {
		List<Message> result = new LinkedList<Message>();
		// 0, 29, 1, 3, 0, 3, 0, 9, 0, 4, 0, 4, 84, 69, 83, 84, 95, 82, 79, 76,
		// 69, 0, 0, 82, 11, 0, 0, 0, 1
		if (bytes == null) {
			LOGGER.warn("bytes: is null");
			return result;
		}
		//
		try {
			int pos = 0;
			int dataLength = 0;
			while (pos < bytes.length
					&& (dataLength = ByteHelper.toInt(ByteHelper.getByteArray(
							bytes, pos, 4))) < bytes.length - pos + 1) {
				// System.out.println("pos: " + tempLength + ", " + pos);
				Message message = disassemble(pos, bytes, moduleEnumType,
						messageEnumType);
				if (message != null) {
					result.add(message);
				}
				//
				pos += dataLength;
				// System.out.println("pos: " + dataLength + ", " + pos);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 訊息協定,反組byte[]中某一段的訊息
	 * 
	 * @param bytes
	 * @param moduleEnumType
	 * @param messageEnumType
	 * @return
	 */
	protected <T extends Enum<T>, U extends Enum<U>> Message disassemble(
			int pos, byte[] bytes, Class<T> moduleEnumType,
			Class<U> messageEnumType) {
		Message result = null;
		// checksum, 2013/11/10
		// 收到bytes後,計算的checksum值
		ByteArrayOutputStream expectedChecksumOut = null;
		try {
			// ------------------------------------------
			// head
			// ------------------------------------------
			// int pos = 0;
			// 資料長度
			int totalLength = ByteHelper.toInt(ByteHelper.getByteArray(bytes,
					pos, 4));// pos=0
			pos += 4;
			// System.out.println("disassemble totalLength..." + totalLength);

			// 實際收的資料長度
			if (bytes.length < 12) {
				LOGGER.error("Data[" + bytes.length + "] < 12 bytes");
				return result;
			}
			// 實際收的資料長度 < 資料長度
			else if (bytes.length < totalLength) {
				LOGGER.error("Data[" + bytes.length + "] < [" + totalLength
						+ "] bytes");
				return result;
			}

			expectedChecksumOut = new ByteArrayOutputStream();

			// categoryType
			byte[] categoryTypeBytes = ByteHelper.getByteArray(bytes, pos, 1);
			byte categoryTypeValue = ByteHelper.toByte(categoryTypeBytes);// pos=4
			CategoryType categoryType = EnumHelper.valueOf(CategoryType.class,
					categoryTypeValue);
			// System.out.println("categoryValue: " + categoryValue);
			expectedChecksumOut.write(categoryTypeBytes);
			pos += 1;

			// priorityType
			byte[] priorityTypeBytes = ByteHelper.getByteArray(bytes, pos, 1);
			byte priorityTypeValue = ByteHelper.toByte(priorityTypeBytes);// pos=5
			// System.out.println("priorityValue: " + priorityValue);
			PriorityType priorityType = EnumHelper.valueOf(PriorityType.class,
					priorityTypeValue);
			expectedChecksumOut.write(priorityTypeBytes);
			pos += 1;

			// messageType
			byte[] messageTypeBytes = ByteHelper.getByteArray(bytes, pos, 4);
			int messageTypeValue = ByteHelper.toInt(messageTypeBytes);// pos=6
			// System.out.println("messageIntValue: " + messageIntValue);
			MessageType messageType = (MessageType) EnumHelper.valueOf(
					messageEnumType, messageTypeValue);
			expectedChecksumOut.write(messageTypeBytes);
			pos += 4;

			// sender
			byte[] senderLengthBytes = ByteHelper.getByteArray(bytes, pos, 2);
			int senderLength = ByteHelper.fromShortInt(senderLengthBytes);// pos=10
			// System.out.println("senderLength: " + senderLength);
			expectedChecksumOut.write(senderLengthBytes);
			pos += 2;
			//
			byte[] senderBytes = ByteHelper.getByteArray(bytes, pos,
					senderLength);
			StringBuilder sender = new StringBuilder();
			sender.append(ByteHelper.toString(senderBytes));// pos=12
			// System.out.println("sender: " + sender);
			expectedChecksumOut.write(senderBytes);
			pos += senderLength;

			ModuleType srcModuleType = null;
			ModuleType destModuleType = null;
			List<String> receivers = new LinkedList<String>();
			// ------------------------------------------
			// MESSAGE_RELATION
			// MESSAGE_SERVER
			// MESSAGE_SYNC
			// ------------------------------------------
			if (CategoryType.MESSAGE_RELATION.equals(categoryType)
					|| CategoryType.MESSAGE_SERVER.equals(categoryType)
					|| CategoryType.MESSAGE_SYNC.equals(categoryType)) {
				// 來源模組
				byte[] srcModuleTypeBytes = ByteHelper.getByteArray(bytes, pos,
						4);
				int srcModuleTypeValue = ByteHelper.toInt(srcModuleTypeBytes);
				// System.out.println("srcModuleTypeValue: " +
				// srcModuleTypeValue);
				srcModuleType = (ModuleType) EnumHelper.valueOf(moduleEnumType,
						srcModuleTypeValue);
				expectedChecksumOut.write(srcModuleTypeBytes);
				pos += 4;

				// 目的模組
				byte[] destModuleTypeBytes = ByteHelper.getByteArray(bytes,
						pos, 4);
				int destModuleTypeValue = ByteHelper.toInt(destModuleTypeBytes);
				// System.out.println("destModuleTypeValue: " +
				// destModuleTypeValue);
				destModuleType = (ModuleType) EnumHelper.valueOf(
						moduleEnumType, destModuleTypeValue);
				expectedChecksumOut.write(destModuleTypeBytes);
				pos += 4;

				// 接收者大小
				byte[] receiversSizeBytes = ByteHelper.getByteArray(bytes, pos,
						2);
				int receiversSize = ByteHelper.fromShortInt(receiversSizeBytes);
				// System.out.println("receiversSize: " + receiversSize);
				expectedChecksumOut.write(receiversSizeBytes);
				pos += 2;
				for (int i = 0; i < receiversSize; i++) {
					byte[] receiverLengthBytes = ByteHelper.getByteArray(bytes,
							pos, 2);
					int receiverLength = ByteHelper
							.fromShortInt(receiverLengthBytes);
					expectedChecksumOut.write(receiverLengthBytes);
					pos += 2;
					// System.out.println("receiverLength: " + receiverLength);
					//
					byte[] receiverBytes = ByteHelper.getByteArray(bytes, pos,
							receiverLength);
					StringBuilder receiver = new StringBuilder();
					receiver.append(ByteHelper.toString(receiverBytes));
					expectedChecksumOut.write(receiverBytes);
					pos += receiverLength;
					// System.out.println("receiver: " + receiver);
					receivers.add(receiver.toString());
				}
			}

			// ------------------------------------------
			// content
			// ------------------------------------------
			byte[] contentsSizeBytes = ByteHelper.getByteArray(bytes, pos, 2);
			int contentsSize = ByteHelper.fromShortInt(contentsSizeBytes);// pos=21
			// System.out.println("contentsSize: " + contentsSize);
			expectedChecksumOut.write(contentsSizeBytes);
			pos += 2;

			int[] contentLength = new int[contentsSize];
			for (int i = 0; i < contentsSize; i++) {
				byte[] contentLengthBytes = ByteHelper.getByteArray(bytes, pos,
						2);
				contentLength[i] = ByteHelper.fromShortInt(contentLengthBytes);
				expectedChecksumOut.write(contentLengthBytes);
				pos += 2;
				// System.out.println("contentLength: " + contentLength[i]);
			}

			List<byte[]> contents = new LinkedList<byte[]>();
			for (int i = 0; i < contentsSize; i++) {
				// System.out.println(pos);
				byte[] content = ByteHelper.getByteArray(bytes, pos,
						contentLength[i]);
				expectedChecksumOut.write(content);
				pos += contentLength[i];
				contents.add(content);
			}

			byte[] checksumBytes = ByteHelper.getByteArray(bytes, pos, 8);
			pos += 8;
			if (checksumBytes == null) {
				LOGGER.error("Checksum is null");
				return result;
			}
			long checksum = ByteHelper.toLong(checksumBytes);
			// System.out.println("checksum: " + checksum);

			// 檢查碼
			long expectedChecksum = checksum(expectedChecksumOut.toByteArray());
			// System.out.println("expectedChecksum: " + expectedChecksum);
			if (expectedChecksum != checksum) {
				LOGGER.error("Expected checksum [" + expectedChecksum
						+ "] is not equal [" + checksum + "]");
				return result;
			}

			// ------------------------------------------
			// MESSAGE_CLIENT
			// ------------------------------------------
			if (CategoryType.MESSAGE_CLIENT.equals(categoryType)) {
				// ------------------------------------------
				result = new MessageImpl(categoryType, priorityType,
						sender.toString(), messageType);
				// ------------------------------------------

				// destModule,編號來自於messageIntValue,前3碼+"00",如:150001->150000
				int destModuleTypeValue = NumberHelper.toInt(NumberHelper
						.toString(messageTypeValue, "##0").substring(0, 3)
						+ "000");
				// System.out.println("destModuleIntValue: " +
				// destModuleIntValue);
				destModuleType = (ModuleType) EnumHelper.valueOf(
						moduleEnumType, destModuleTypeValue);
				result.setDestModule(destModuleType);

				// contents
				for (int i = 0; i < contents.size(); i++) {
					result.addByteArray(contents.get(i));
				}
				// result.add(message);
			}
			// ------------------------------------------
			// MESSAGE_RELATION
			// MESSAGE_SERVER
			// MESSAGE_SYNC
			// ------------------------------------------
			else if (CategoryType.MESSAGE_RELATION.equals(categoryType)
					|| CategoryType.MESSAGE_SERVER.equals(categoryType)
					|| CategoryType.MESSAGE_SYNC.equals(categoryType)) {
				// ------------------------------------------
				result = new MessageImpl(categoryType, priorityType,
						srcModuleType, destModuleType, messageType, receivers);
				result.setSender(sender.toString());
				// ------------------------------------------
				// contents
				for (int i = 0; i < contents.size(); i++) {
					result.addByteArray(contents.get(i));
				}
				// result.add(message);
			}
			// 無來源,目的模組
			else if (CategoryType.MESSAGE_ACCEPTOR.equals(categoryType)) {
				// ------------------------------------------
				result = new MessageImpl(categoryType, priorityType,
						sender.toString(), messageType);
				// ------------------------------------------

				// contents
				for (int i = 0; i < contents.size(); i++) {
					result.addByteArray(contents.get(i));
				}
			} else {
				LOGGER.error("Unsupported protocol");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	// /**
	// * checksum
	// *
	// * @param values
	// * @param assignKey
	// *
	// * @param algorithm
	// * @return
	// */
	// protected byte[] handshakeChecksum(byte[] values, String assignKey,
	// String algorithm) {
	// byte[] result = null;
	// //
	// if (ByteHelper.isNotEmpty(values) && algorithm != null) {
	// SecretKey secretKey = SecurityHelper.createSecretKey(assignKey,
	// algorithm);
	// result = SecurityHelper.encryptMac(values, secretKey, algorithm);
	// }
	// return result;
	// }

}

// ByteBuffer , 1000000 times: 736 mills.
// ByteArrayOutputStream, 1000000 times: 619 mills.
//
// 其實ByteBuffer快一點點,與ByteArrayOutputStream效率差不多
// 但若ByteBuffer.allocate 分配太大,則會明顯變慢
// public byte[] sync(CategoryType categoryType, byte[] authKey, boolean
// handshake, String sender,
// String acceptor)
// {
// byte[] result = new byte[0];
// //
// try
// {
// if (authKey == null || authKey.length != 32)
// {
// log.warn("authKey: is invalid");
// return result;
// }
// //
// if (!handshake)
// {
// log.warn("handshake: is false");
// return result;
// }
// //
// if (StringHelper.isBlank(sender))
// {
// log.warn("sender: is blank");
// return result;
// }
// //
// if (StringHelper.isBlank(acceptor))
// {
// log.warn("acceptor: is blank");
// return result;
// }
// //
// {
// //資料總長度
// int totalLength = 4 + 32 + 1 + (2 + sender.length()) + (2 +
// acceptor.length());
// ByteBuffer buff = ByteBuffer.allocate(totalLength);
// //head
// //0-1,38(short) 6+32
// //2,2(byte)category
// //3,3(byte)priority
//
// //content
// //4起為authKey內容
// //4-35,(byte[32])
//
// //36,handshake
//
// //37-38,(short)sender.length
// //39,sender
//
// //40-41,(short)acceptor.length
// //42,acceptor
//
// //------------------------------------------
// //head
// //------------------------------------------
// buff.put(ByteHelper.toShortBytes(totalLength));
// buff.put(ByteHelper.toByteArray(categoryType.byteValue()));
// buff.put(ByteHelper.toByteArray(PriorityType.URGENT.byteValue()));
//
// //------------------------------------------
// //content
// //------------------------------------------
// //authkey
// buff.put(authKey);
// //handshake
// buff.put(ByteHelper.toByteArray(handshake));
// //sender
// buff.put(ByteHelper.toShortBytes(sender.length()));
// buff.put(ByteHelper.toByteArray(sender));
// //acceptor
// buff.put(ByteHelper.toShortBytes(acceptor.length()));
// buff.put(ByteHelper.toByteArray(acceptor));
// //
// buff.flip();
// result = new byte[buff.limit()];
// buff.get(result);
// }
// }
// catch (Exception ex)
// {
// ex.printStackTrace();
// }
//
// return result;
// }
//
// /**
// * 0, String, authkey
// *
// * 1, boolean, handshake
// *
// * 2, String, acceptor
// */
// public Message disSync(byte[] bytes)
// {
// Message result = null;
// //0, 58, 11, 1, 97, 97, 99, 99, 56, 57, 54, 52, 51, 50, 52, 55, 51, 56, 99,
// 50, 55, 99, 48, 55, 55, 52, 54, 101, 51, 101, 97, 56, 49, 97, 102, 102, 1, 0,
// 6, 115, 108, 97, 118, 101, 50, 0, 11, 84, 69, 83, 84, 95, 82, 79, 76, 69, 95,
// 49
// if (bytes == null)
// {
// log.warn("bytes: is null");
// return result;
// }
// //
// try
// {
// //------------------------------------------
// //head
// //------------------------------------------
// int pos = 0;
// //資料總長度,最少要36
// int totalLength = ByteHelper.toShortInt(ByteHelper.read(bytes, pos, pos +=
// 2));
// //System.out.println("totalLength: " + totalLength);
//
// //實際收的資料長度
// if (bytes.length < 36)
// {
// log.warn("data[" + bytes.length + "] < 36 bytes");
// return result;
// }
// //實際收的資料長度 < 總資料長度
// else if (bytes.length < totalLength)
// {
// log.warn("data[" + bytes.length + "] < [" + totalLength + "] bytes");
// return result;
// }
// //
// byte categoryValue = ByteHelper.toByte(ByteHelper.read(bytes, pos, pos +=
// 1));//1
// //System.out.println("category: " + categoryValue);
// CategoryType categoryType = EnumHelper.valueOf(CategoryType.class,
// categoryValue);
//
// byte priorityValue = ByteHelper.toByte(ByteHelper.read(bytes, pos, pos +=
// 1));//3
// //System.out.println("priority: " + priorityValue);
// PriorityType priorityType = EnumHelper.valueOf(PriorityType.class,
// priorityValue);
//
// //------------------------------------------
// // CategoryType.SYNC_CLIENT_CONNECT
// //------------------------------------------
// if (CategoryType.SYNC_CONNECT.equals(categoryType)
// || CategoryType.SYNC_DISCONNECT.equals(categoryType))
// {
// //------------------------------------------
// //content
// //------------------------------------------
// //authkey
// byte[] authkey = ByteHelper.read(bytes, pos, 32);
// pos += 32;
//
// //hndshake
// byte[] handshake = ByteHelper.read(bytes, pos, 1);
// pos += 1;
//
// //sender
// int senderLength = ByteHelper.toShortInt(ByteHelper.read(bytes, pos, 2));
// pos += 2;
// //
// byte[] sender = ByteHelper.read(bytes, pos, senderLength);
// pos += senderLength;
//
// //acceptor
// int acceptorLength = ByteHelper.toShortInt(ByteHelper.read(bytes, pos, 2));
// pos += 2;
// //
// byte[] acceptor = ByteHelper.read(bytes, pos, acceptorLength);
// pos += acceptorLength;
//
// //message
// //------------------------------------------
// result = new MessageImpl(categoryType, priorityType);
// //------------------------------------------
// result.setSender(ByteHelper.toString(sender));
// //
// result.addBytes(authkey);//0,String
// result.addBytes(handshake);//1,boolean
// result.addBytes(acceptor);//2,String
// }
// }
// catch (Exception ex)
// {
// ex.printStackTrace();
// }
// return result;
// }
