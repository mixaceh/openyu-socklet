package org.openyu.socklet.message.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.openyu.commons.enumz.EnumHelper;
import org.openyu.commons.io.IoHelper;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.commons.lang.StringHelper;
import org.openyu.commons.mark.Magicer;
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
import org.openyu.commons.enumz.IntEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 協定服務
 * 
 * 組成/反組byte[]
 */
public abstract class ProtocolServiceImplBak2 extends BaseServiceSupporter implements
		ProtocolService {

	private static final long serialVersionUID = 7562819271771546706L;

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(ProtocolServiceImplBak2.class);

	/** 檢查碼 */
	private ChecksumProcessor checksumProcessor = new ChecksumProcessorImpl();

	/** 安全性 */
	private SecurityProcessor securityProcessor = new SecurityProcessorImpl();

	/** 壓縮 */
	private CompressProcessor compressProcessor = new CompressProcessorImpl();

	public ProtocolServiceImplBak2() {
	}

	/**
	 * 是否檢查碼
	 * 
	 * @return
	 */
	public boolean isChecksum() {
		return checksumProcessor.isChecksum();
	}

	public void setChecksum(boolean checksum) {
		checksumProcessor.setChecksum(checksum);
	}

	/**
	 * 檢查碼類別
	 * 
	 * @return
	 */
	public ChecksumType getChecksumType() {
		return checksumProcessor.getChecksumType();
	}

	public void setChecksumType(ChecksumType checksumType) {
		checksumProcessor.setChecksumType(checksumType);
	}

	/**
	 * 檢查碼key
	 * 
	 * @return
	 */
	public String getChecksumKey() {
		return checksumProcessor.getChecksumKey();
	}

	public void setChecksumKey(String checksumKey) {
		checksumProcessor.setChecksumKey(checksumKey);
	}

	/**
	 * 是否加密
	 * 
	 * @return
	 */
	public boolean isSecurity() {
		return securityProcessor.isSecurity();
	}

	public void setSecurity(boolean security) {
		securityProcessor.setSecurity(security);
	}

	/**
	 * 加密類別
	 * 
	 * @return
	 */
	public SecurityType getSecurityType() {
		return securityProcessor.getSecurityType();
	}

	public void setSecurityType(SecurityType securityType) {
		securityProcessor.setSecurityType(securityType);
	}

	/**
	 * 加密key
	 * 
	 * @return
	 */
	public String getSecurityKey() {
		return securityProcessor.getSecurityKey();
	}

	public void setSecurityKey(String securityKey) {
		securityProcessor.setSecurityKey(securityKey);
	}

	/**
	 * 是否壓縮
	 * 
	 * @return
	 */
	public boolean isCompress() {
		return compressProcessor.isCompress();
	}

	public void setCompress(boolean compress) {
		compressProcessor.setCompress(compress);
	}

	/**
	 * 壓縮類別
	 * 
	 * @return
	 */
	public CompressType getCompressType() {
		return compressProcessor.getCompressType();
	}

	public void setCompressType(CompressType compressType) {
		compressProcessor.setCompressType(compressType);
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
		if (categoryType == null) {
			throw new IllegalArgumentException(
					"The CategoryType must not be null");
		}
		//
		if (authKey == null || authKey.length != 32) {
			throw new IllegalArgumentException("The AuthKey is invalid");
		}
		//
		if (StringHelper.isBlank(sender)) {
			throw new IllegalArgumentException("Sender is blank");
		}
		//
		ByteArrayOutputStream out = null;
		ByteArrayOutputStream headOut = null;
		ByteArrayOutputStream dataOut = null;
		try {
			out = new ByteArrayOutputStream();
			headOut = new ByteArrayOutputStream();
			dataOut = new ByteArrayOutputStream();

			// out = headOut + dataOut
			// dataOut = infoOut + contentOut + (checksum)
			// ------------------------------------------

			// ------------------------------------------
			// head
			// ------------------------------------------
			// 0-1, 61(short)
			// 2-3, 48148(short)magic

			// ------------------------------------------
			// data
			// ------------------------------------------
			// info
			// 4, 2(byte)category
			// 5, 3(byte)priority
			// 6-7, 11(short)sender.length, 原為int會變成 short
			// 8-16, TEST_ROLE(byte[9])sender

			// content
			// 之後為authKey內容,長度32
			// 18-48, aacc8964324738c27c07746e3ea81aff
			// (byte[32])authKey

			// checksum
			// 49-56, 3048607855 (long)checksum
			// ------------------------------------------

			// head長度
			int headLengh = 2// totalLength
			+ 2;// magic

			// data長度
			int dataLength = 0//
					+ 1 // categoryTypeLength
					+ 1// priorityTypeLength
					+ (2)// senderLength
					+ sender.length() // sender
					+ (32)// authkeyLength
			;

			// ------------------------------------------
			// data
			// ------------------------------------------
			// info: categoryType
			byte[] categoryTypeBytes = ByteHelper.toByteArray(categoryType
					.getValue());
			dataOut.write(categoryTypeBytes);

			// info: priorityType
			byte[] priorityTypeBytes = ByteHelper
					.toByteArray(PriorityType.URGENT.getValue());
			dataOut.write(priorityTypeBytes);

			// info: sender.length
			byte[] senderLengthBytes = ByteHelper.toShortByteArray(sender
					.length());
			dataOut.write(senderLengthBytes);
			// info: sender
			byte[] senderBytes = ByteHelper.toByteArray(sender);
			dataOut.write(senderBytes);

			// content: authkey
			dataOut.write(authKey);

			// 1.檢查碼
			if (isChecksum()) {
				long checksum = checksumProcessor.checksum(dataOut
						.toByteArray());
				// System.out.println("checksum: " + checksum);
				byte[] checksumBytes = ByteHelper.toByteArray(checksum);
				dataOut.write(checksumBytes);
				//
				dataLength += (8);// checksum
			}

			// 總長度
			int totalLength = headLengh + dataLength;
			// System.out.println("headLengh: " + headLengh);
			// System.out.println("dataLength: " + dataLength);
			// System.out.println("totalLength: " + totalLength);

			// ------------------------------------------
			// out
			// ------------------------------------------
			byte[] buff = dataOut.toByteArray();

			// 2.加密
			if (isSecurity()) {
				buff = securityProcessor.encrypt(buff);
				// 會改變長度
				dataLength = buff.length;
				totalLength = headLengh + dataLength;
			}

			// 3.壓縮
			if (isCompress()) {
				buff = compressProcessor.compress(buff);
				// 會改變長度
				dataLength = buff.length;
				totalLength = headLengh + dataLength;
			}
			// 總長度
			byte[] totalLengthBytes = ByteHelper.toShortByteArray(totalLength);

			// head
			headOut.write(totalLengthBytes);
			headOut.write(MagicType.HANKSHAKE.getBytes());// length=2
			headOut.writeTo(out);

			// data
			out.write(buff);
			//
			result = out.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IoHelper.close(headOut);
			IoHelper.close(dataOut);
			IoHelper.close(out);
		}

		return result;
	}

	/**
	 * 反握手協定,反組byte[]
	 * 
	 * @param values
	 * @return
	 */
	public Message dehandshake(byte[] values) {
		Message result = null;
		//
		if (values == null) {
			throw new IllegalArgumentException("The Values must not be null");
		}
		ByteArrayOutputStream dataOut = null;
		try {
			dataOut = new ByteArrayOutputStream();
			// ------------------------------------------
			// head
			// ------------------------------------------
			int pos = 0;
			// 資料總長度
			int totalLength = ByteHelper.fromShortInt(ByteHelper.getByteArray(
					values, pos, 2));// 0-1
			pos += 2;
			// System.out.println("totalLength: " + totalLength);

			// 實際收的資料長度
			if (values.length < 38) {
				LOGGER.error("Data[" + values.length + "] < 38 bytes");
				return result;
			}
			// 實際收的資料長度 <資料總長度
			else if (values.length < totalLength) {
				LOGGER.error("Data[" + values.length + "] < [" + totalLength
						+ "] bytes");
				return result;
			}
			//
			int magicTypeValue = ByteHelper.fromShortInt(ByteHelper
					.getByteArray(values, pos, 2));// 2-3
			pos += 2;
			// System.out.println("magicTypeValue: " + magicTypeValue);
			if (MagicType.HANKSHAKE.getValue() != magicTypeValue) {
				LOGGER.error("Invalid handshake");
				return result;
			}

			// ------------------------------------------
			// data
			// ------------------------------------------
			int headLengh = pos;// 4
			byte[] data = ByteHelper.getByteArray(values, pos, totalLength
					- headLengh);

			// 1.解壓
			if (isCompress()) {
				data = compressProcessor.uncompress(data);
			}
			// 2.解密
			if (isSecurity()) {
				data = securityProcessor.decrypt(data);
			}

			pos = 0;
			// info: categoryType
			byte[] categoryTypeBytes = ByteHelper.getByteArray(data, pos, 1);
			CategoryType categoryType = EnumHelper.valueOf(CategoryType.class,
					ByteHelper.toByte(categoryTypeBytes));
			// System.out.println("categoryType: " + categoryType);
			dataOut.write(categoryTypeBytes);
			pos += 1;

			// info: priorityType
			byte[] priorityTypeBytes = ByteHelper.getByteArray(data, pos, 1);
			PriorityType priorityType = EnumHelper.valueOf(PriorityType.class,
					ByteHelper.toByte(priorityTypeBytes));
			// System.out.println("priorityType: " + priorityType);
			dataOut.write(priorityTypeBytes);
			pos += 1;

			// ------------------------------------------
			// CategoryType.HANDSHAKE
			// ------------------------------------------
			// 是否為handshake類型
			boolean handshakeType = CategoryType.HANDSHAKE_CLIENT == categoryType
					|| CategoryType.HANDSHAKE_RELATION == categoryType
					|| CategoryType.HANDSHAKE_SERVER == categoryType;
			if (handshakeType) {
				// info: sender.length
				byte[] senderLengthBytes = ByteHelper
						.getByteArray(data, pos, 2);
				int senderLength = ByteHelper.fromShortInt(senderLengthBytes);
				// System.out.println("senderLength: " + senderLength);
				dataOut.write(senderLengthBytes);
				pos += 2;

				// info: sender
				byte[] senderBytes = ByteHelper.getByteArray(data, pos,
						senderLength);
				StringBuilder sender = new StringBuilder(
						ByteHelper.toString(senderBytes));
				// System.out.println("sender: " + sender);
				dataOut.write(senderBytes);
				pos += senderLength;

				// content: authkey
				byte[] authkey = ByteHelper.getByteArray(data, pos, 32);
				dataOut.write(authkey);
				pos += 32;

				// 3.檢查碼
				if (isChecksum()) {
					// 從遠端收到的checksum
					byte[] checksumBytes = ByteHelper
							.getByteArray(data, pos, 8);
					pos += 8;
					if (checksumBytes == null) {
						LOGGER.error("Checksum is null");
						return result;
					}
					long checksum = ByteHelper.toLong(checksumBytes);
					// System.out.println("checksum: " + checksum);

					// 本地算出來的checksum
					long realChecksum = checksumProcessor.checksum(dataOut
							.toByteArray());
					// System.out.println("realChecksum: " +
					// realChecksum);

					if (realChecksum != checksum) {
						LOGGER.error("Checksum [" + realChecksum
								+ "] not equal expected [" + checksum + "]");
						return result;
					}
				}

				// message
				result = new MessageImpl(categoryType, priorityType);
				result.setSender(sender.toString());
				result.addByteArray(authkey);// 0, String
			} else {
				LOGGER.error("Unsupported categoryType [" + categoryType + "]");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IoHelper.close(dataOut);
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
		if (message == null) {
			throw new IllegalArgumentException("The Message must not be null");
		}
		// 訊息
		Message cloneMessage = (Message) message.clone();
		// System.out.println(cloneMessage);
		String sender = cloneMessage.getSender();
		if (StringHelper.isBlank(sender)) {
			throw new IllegalArgumentException("Sender is blank");
		}
		//
		ByteArrayOutputStream out = null;
		ByteArrayOutputStream headOut = null;
		ByteArrayOutputStream dataOut = null;

		ByteArrayOutputStream contentsLengthOut = null;
		ByteArrayOutputStream contentsOut = null;
		try {

			out = new ByteArrayOutputStream();
			headOut = new ByteArrayOutputStream();
			dataOut = new ByteArrayOutputStream();

			// lengthOut:
			// 1.長度為4,資料為4 ->(short)byte[] 長度變2,資料為4
			// 2.長度為4,資料為2 ->(short)byte[] 長度變2,資料為2
			// 將int轉成short, 且轉為 byte[2]存入
			contentsLengthOut = new ByteArrayOutputStream();
			contentsOut = new ByteArrayOutputStream();

			// content
			List<byte[]> contents = cloneMessage.getContents();
			for (byte[] content : contents) {
				byte[] contentLengthBytes = ByteHelper
						.toShortByteArray(content.length);
				contentsLengthOut.write(contentLengthBytes);
				contentsOut.write(content);
			}
			// ------------------------------------------
			// head
			// ------------------------------------------
			// 0-3, 61(int)
			// 4-5, 48148(short)magic

			// ------------------------------------------
			// data
			// ------------------------------------------
			// MESSAGE_CLIENT
			// ------------------------------------------
			// info
			// 6, 10(byte)category
			// 7, 3(byte)priority
			// 8-11, 210003(int)messageType

			// 12-13, 11(short)sender.length, 原為int會變成 short
			// 14-22, TEST_ROLE(byte[9])sender

			// ------------------------------------------
			// data
			// ------------------------------------------
			// MESSAGE_SERVER
			// ------------------------------------------
			// info
			// 25-28, (int),srcModule.intValue
			// 29-32, (int),destModule.intValue
			// 33-34, 1(short)receivers.size, 原為int會變成 short
			// 35-36, 11(short)receive.length, 原為int會變成 short
			// 37-45, TEST_ROLE(byte[9])receive

			// content
			// 46-47, 1(short)contents.size, 內容個數
			// 48-49, 1(short)content.length, 原為int會變成 short
			// 50-53, 1(int)times, 玩幾次

			// head長度
			int headLengh = 4// totalLength
			+ 2;// magic

			// data長度
			int dataLength = 0//
					// info
					+ 1 // categoryTypeLength
					+ 1// priorityTypeLength
					+ 4// messageTypeLength
					+ (2)// senderLength
					+ sender.length() // sender

					// content
					+ (2)// contentsSize
					+ contentsLengthOut.size()// contentsLength
					+ contentsOut.size()// contents
			;
			// ------------------------------------------
			// MESSAGE_RELATION
			// MESSAGE_SERVER
			// MESSAGE_SYNC
			// ------------------------------------------
			// 是否為server類型
			boolean serverType = CategoryType.MESSAGE_RELATION == cloneMessage
					.getCategoryType()
					|| CategoryType.MESSAGE_SERVER == cloneMessage
							.getCategoryType()
					|| CategoryType.MESSAGE_SYNC == cloneMessage
							.getCategoryType();
			if (serverType) {
				// 接收者
				int receiversLength = 0;
				for (String receiver : cloneMessage.getReceivers()) {
					receiversLength += 2 + receiver.length();
				}

				// data長度
				dataLength = 0//
						// info
						+ 1 // categoryTypeLength
						+ 1// priorityTypeLength
						+ 4// messageTypeLength
						+ (2)// senderLength
						+ sender.length() // sender

						// by server
						+ (4)// srcModuleLength
						+ (4)// destModuleLength
						+ (2)// receiverLength
						+ (receiversLength)// receivers

						// content
						// by server & client
						+ (2)// contentsSize
						+ contentsLengthOut.size()// contentsLength
						+ contentsOut.size()// contents
				;
			}

			// ------------------------------------------
			// data
			// ------------------------------------------
			// info: categoryType
			byte[] categoryTypeBytes = ByteHelper.toByteArray(cloneMessage
					.getCategoryType().getValue());
			dataOut.write(categoryTypeBytes);
			// info: priorityType
			byte[] priorityTypeBytes = ByteHelper.toByteArray(cloneMessage
					.getPriorityType().getValue());
			dataOut.write(priorityTypeBytes);

			// info: messageType
			byte[] messageTypeBytes = ByteHelper.toByteArray(cloneMessage
					.getMessageType().getValue());
			dataOut.write(messageTypeBytes);

			// info: sender.length
			byte[] senderLengthBytes = ByteHelper.toShortByteArray(sender
					.length());
			dataOut.write(senderLengthBytes);

			// info: sender
			byte[] senderBytes = ByteHelper.toByteArray(sender);
			dataOut.write(senderBytes);

			// 是否為server發送
			// System.out.println("serverType: "+serverType);
			if (serverType) {
				// info: srcModule 來源模組
				byte[] srcModuleTypeBytes = ByteHelper.toByteArray(cloneMessage
						.getSrcModule().getValue());
				dataOut.write(srcModuleTypeBytes);

				// info: destModule 目的模組
				byte[] destModuleTypeBytes = ByteHelper
						.toByteArray(cloneMessage.getDestModule().getValue());
				dataOut.write(destModuleTypeBytes);

				// info: receiversSize
				int receiversSize = cloneMessage.getReceivers().size();
				byte[] receiversSizeBytes = ByteHelper
						.toShortByteArray(receiversSize);
				dataOut.write(receiversSizeBytes);
				// info: receivers
				for (String receiver : cloneMessage.getReceivers()) {
					byte[] receiverLengthBytes = ByteHelper
							.toShortByteArray(receiver.length());
					dataOut.write(receiverLengthBytes);

					byte[] receiverBytes = ByteHelper.toByteArray(receiver);
					dataOut.write(receiverBytes);
				}
			}

			// content: contentsSize
			int contentsSize = contents.size();
			byte[] contentsSizeBytes = ByteHelper
					.toShortByteArray(contentsSize);
			dataOut.write(contentsSizeBytes);

			// content: contentsLengthOut
			contentsLengthOut.writeTo(dataOut);

			// content: contentsOut
			contentsOut.writeTo(dataOut);

			// 1.檢查碼
			if (isChecksum()) {
				long checksum = checksumProcessor.checksum(dataOut
						.toByteArray());
				// System.out.println("checksum: " + checksum);
				byte[] checksumBytes = ByteHelper.toByteArray(checksum);
				dataOut.write(checksumBytes);
				//
				dataLength += (8);// checksum
			}
			//
			// 總長度
			int totalLength = headLengh + dataLength;
			// System.out.println("headLengh: " + headLengh);
			// System.out.println("dataLength: " + dataLength);
			// System.out.println("totalLength: " + totalLength);

			// ------------------------------------------
			// out
			// ------------------------------------------
			byte[] buff = dataOut.toByteArray();
			// 2.加密
			if (isSecurity()) {
				buff = securityProcessor.encrypt(buff);
				// 會改變長度
				dataLength = buff.length;
				totalLength = headLengh + dataLength;
			}

			// 3.壓縮
			if (isCompress()) {
				buff = compressProcessor.compress(buff);
				// 會改變長度
				dataLength = buff.length;
				totalLength = headLengh + dataLength;
			}
			// 總長度
			byte[] totalLengthBytes = ByteHelper.toByteArray(totalLength);

			// head
			headOut.write(totalLengthBytes);
			headOut.write(MagicType.MESSAGE.getBytes());// length=2
			headOut.writeTo(out);

			// data
			out.write(buff);
			//
			result = out.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IoHelper.close(headOut);
			IoHelper.close(dataOut);
			IoHelper.close(contentsLengthOut);
			IoHelper.close(contentsOut);
			IoHelper.close(out);
		}
		return result;
	}

	/**
	 * 反訊息協定,反組byte[]
	 * 
	 * @param values
	 * @param moduleEnumType
	 * @param messageEnumType
	 * @return
	 */
	public <T extends Enum<T>, U extends Enum<U>> List<Message> disassemble(
			byte[] values, Class<T> moduleEnumType, Class<U> messageEnumType) {
		List<Message> result = new LinkedList<Message>();
		if (values == null) {
			throw new IllegalArgumentException("The Values must not be null");
		}
		//
		try {
			int pos = 0;
			int dataLength = 0;
			while (pos < values.length
					&& (dataLength = ByteHelper.toInt(ByteHelper.getByteArray(
							values, pos, 4))) < values.length - pos + 1) {
				Message message = disassemble(pos, values, moduleEnumType,
						messageEnumType);
				if (message != null) {
					result.add(message);
				}
				//
				// System.out.println("pos: " + pos + ", " + dataLength);
				pos += dataLength;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 訊息協定,反組byte[]中某一段的訊息
	 * 
	 * @param pos
	 * @param values
	 * @param moduleEnumType
	 * @param messageEnumType
	 * @return
	 */
	protected <T extends Enum<T>, U extends Enum<U>> Message disassemble(
			int pos, byte[] values, Class<T> moduleEnumType,
			Class<U> messageEnumType) {
		Message result = null;
		//
		if (values == null) {
			throw new IllegalArgumentException("The Values must not be null");
		}
		//
		ByteArrayOutputStream dataOut = null;
		try {
			dataOut = new ByteArrayOutputStream();
			// ------------------------------------------
			// head
			// ------------------------------------------
			// int pos = 0;
			// 資料總長度
			int totalLength = ByteHelper.toInt(ByteHelper.getByteArray(values,
					pos, 4));
			pos += 4;
			// System.out.println("totalLength: " + totalLength);

			// 實際收的資料長度
			if (values.length < 12) {
				LOGGER.error("Data[" + values.length + "] < 12 bytes");
				return result;
			}
			// 實際收的資料長度 < 資料總長度
			else if (values.length < totalLength) {
				LOGGER.error("Data[" + values.length + "] < [" + totalLength
						+ "] bytes");
				return result;
			}
			//
			int magicTypeValue = ByteHelper.fromShortInt(ByteHelper
					.getByteArray(values, pos, 2));// 4-5
			pos += 2;
			// System.out.println("magicTypeValue: " + magicTypeValue);
			if (MagicType.MESSAGE.getValue() != magicTypeValue) {
				LOGGER.error("Invalid message");
				return result;
			}

			// ------------------------------------------
			// data
			// ------------------------------------------
			int headLengh = 6;
			byte[] data = ByteHelper.getByteArray(values, pos, totalLength
					- headLengh);

			// 1.解壓
			if (isCompress()) {
				data = compressProcessor.uncompress(data);
			}
			// 2.解密
			if (isSecurity()) {
				data = securityProcessor.decrypt(data);
			}

			pos = 0;
			// info: categoryType
			byte[] categoryTypeBytes = ByteHelper.getByteArray(data, pos, 1);
			CategoryType categoryType = EnumHelper.valueOf(CategoryType.class,
					ByteHelper.toByte(categoryTypeBytes));
			// System.out.println("categoryType: " + categoryType);
			dataOut.write(categoryTypeBytes);
			pos += 1;

			// info: priorityType
			byte[] priorityTypeBytes = ByteHelper.getByteArray(data, pos, 1);
			PriorityType priorityType = EnumHelper.valueOf(PriorityType.class,
					ByteHelper.toByte(priorityTypeBytes));
			// System.out.println("priorityType: " + priorityType);
			dataOut.write(priorityTypeBytes);
			pos += 1;

			// info: messageType
			byte[] messageTypeBytes = ByteHelper.getByteArray(data, pos, 4);
			int messageTypeValue = ByteHelper.toInt(messageTypeBytes);
			MessageType messageType = (MessageType) EnumHelper.valueOf(
					messageEnumType, messageTypeValue);
			// System.out.println("messageType: " + messageType);
			dataOut.write(messageTypeBytes);
			pos += 4;

			// info: sender.length
			byte[] senderLengthBytes = ByteHelper.getByteArray(data, pos, 2);
			int senderLength = ByteHelper.fromShortInt(senderLengthBytes);//
			// System.out.println("senderLength: " + senderLength);
			dataOut.write(senderLengthBytes);
			pos += 2;

			// info: sender
			byte[] senderBytes = ByteHelper.getByteArray(data, pos,
					senderLength);
			StringBuilder sender = new StringBuilder(
					ByteHelper.toString(senderBytes));
			// System.out.println("sender: " + sender);
			dataOut.write(senderBytes);
			pos += senderLength;
			//
			ModuleType srcModuleType = null;
			ModuleType destModuleType = null;
			List<String> receivers = new LinkedList<String>();
			// ------------------------------------------
			// MESSAGE_RELATION
			// MESSAGE_SERVER
			// MESSAGE_SYNC
			// ------------------------------------------
			// 是否為server類型
			boolean serverType = CategoryType.MESSAGE_RELATION == categoryType
					|| CategoryType.MESSAGE_SERVER == categoryType
					|| CategoryType.MESSAGE_SYNC == categoryType;
			// System.out.println("serverType: "+serverType);
			if (serverType) {
				// info: srcModule 來源模組
				byte[] srcModuleTypeBytes = ByteHelper.getByteArray(data, pos,
						4);
				srcModuleType = (ModuleType) EnumHelper.valueOf(moduleEnumType,
						ByteHelper.toInt(srcModuleTypeBytes));
				// System.out.println("srcModuleType: " + srcModuleType);
				dataOut.write(srcModuleTypeBytes);
				pos += 4;

				// info: destModule 目的模組
				byte[] destModuleTypeBytes = ByteHelper.getByteArray(data, pos,
						4);
				destModuleType = (ModuleType) EnumHelper.valueOf(
						moduleEnumType, ByteHelper.toInt(destModuleTypeBytes));
				// System.out.println("destModuleType: " + destModuleType);
				dataOut.write(destModuleTypeBytes);
				pos += 4;

				// info: receiversSize
				byte[] receiversSizeBytes = ByteHelper.getByteArray(data, pos,
						2);
				int receiversSize = ByteHelper.fromShortInt(receiversSizeBytes);
				// System.out.println("receiversSize: " + receiversSize);
				dataOut.write(receiversSizeBytes);
				pos += 2;
				// info: receivers
				for (int i = 0; i < receiversSize; i++) {
					byte[] receiverLengthBytes = ByteHelper.getByteArray(data,
							pos, 2);
					int receiverLength = ByteHelper
							.fromShortInt(receiverLengthBytes);
					dataOut.write(receiverLengthBytes);
					pos += 2;
					// System.out.println("receiverLength: " + receiverLength);
					//
					byte[] receiverBytes = ByteHelper.getByteArray(data, pos,
							receiverLength);
					StringBuilder receiver = new StringBuilder(
							ByteHelper.toString(receiverBytes));
					dataOut.write(receiverBytes);
					pos += receiverLength;
					// System.out.println("receiver: " + receiver);
					receivers.add(receiver.toString());
				}
			}

			// content: contentsSize
			byte[] contentsSizeBytes = ByteHelper.getByteArray(data, pos, 2);
			int contentsSize = ByteHelper.fromShortInt(contentsSizeBytes);
			// System.out.println("contentsSize: " + contentsSize);
			dataOut.write(contentsSizeBytes);
			pos += 2;

			// content: contentsLength
			int[] contentsLength = new int[contentsSize];
			for (int i = 0; i < contentsSize; i++) {
				byte[] contentLengthBytes = ByteHelper.getByteArray(data, pos,
						2);
				contentsLength[i] = ByteHelper.fromShortInt(contentLengthBytes);
				dataOut.write(contentLengthBytes);
				pos += 2;
				// System.out.println("contentLength: " + contentsLength[i]);
			}

			// content: contents
			List<byte[]> contents = new LinkedList<byte[]>();
			for (int i = 0; i < contentsSize; i++) {
				byte[] content = ByteHelper.getByteArray(data, pos,
						contentsLength[i]);
				dataOut.write(content);
				pos += contentsLength[i];
				contents.add(content);
				// SystemHelper.println("content: ", content);
			}

			// 3.檢查碼
			if (isChecksum()) {
				// 從遠端收到的checksum
				byte[] checksumBytes = ByteHelper.getByteArray(data, pos, 8);
				pos += 8;
				if (checksumBytes == null) {
					LOGGER.error("Checksum is null");
					return result;
				}
				long checksum = ByteHelper.toLong(checksumBytes);
				// System.out.println("checksum: " + checksum);

				// 本地算出來的checksum
				long realChecksum = checksumProcessor.checksum(dataOut
						.toByteArray());
				// System.out.println("realChecksum: " + realChecksum);

				if (realChecksum != checksum) {
					LOGGER.error("Checksum [" + realChecksum
							+ "] not equal expected [" + checksum + "]");
					return result;
				}
			}

			boolean clientType = CategoryType.MESSAGE_CLIENT == categoryType;
			boolean accptorType = CategoryType.MESSAGE_ACCEPTOR == categoryType;

			// MESSAGE_CLIENT
			if (clientType) {
				// ------------------------------------------
				result = new MessageImpl(categoryType, priorityType,
						sender.toString(), messageType);

				// destModule,編號來自於messageIntValue,前3碼+"00",如:150001->150000
				int destModuleTypeValue = NumberHelper.toInt(NumberHelper
						.toString(messageTypeValue, "##0").substring(0, 3)
						+ "000");
				// System.out.println("destModuleTypeValue: "
				// + destModuleTypeValue);
				destModuleType = (ModuleType) EnumHelper.valueOf(
						moduleEnumType, destModuleTypeValue);
				// System.out.println("destModuleType: " + destModuleType);
				result.setDestModule(destModuleType);

				// content
				for (int i = 0; i < contents.size(); i++) {
					result.addByteArray(contents.get(i));
				}
			}
			// MESSAGE_RELATION
			// MESSAGE_SERVER
			// MESSAGE_SYNC
			else if (serverType) {
				result = new MessageImpl(categoryType, priorityType,
						srcModuleType, destModuleType, messageType, receivers);
				result.setSender(sender.toString());

				// content
				for (int i = 0; i < contents.size(); i++) {
					result.addByteArray(contents.get(i));
				}
			}
			// MESSAGE_ACCEPTOR
			else if (accptorType) {
				// ------------------------------------------
				result = new MessageImpl(categoryType, priorityType,
						sender.toString(), messageType);
				// ------------------------------------------

				// contents
				for (int i = 0; i < contents.size(); i++) {
					result.addByteArray(contents.get(i));
				}
			} else {
				LOGGER.error("Unsupported categoryType [" + categoryType + "]");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IoHelper.close(dataOut);
		}
		return result;
	}

	protected enum MagicType implements IntEnum, Magicer {

		/**
		 * 握手用
		 * 
		 * int -> short
		 */
		HANKSHAKE(5308, ByteHelper.toShortByteArray(5308)),

		/**
		 * 訊息用
		 * 
		 * int -> short
		 */
		MESSAGE(48148, ByteHelper.toShortByteArray(48148)),

		//
		;

		private final int value;

		private final byte[] bytes;

		private MagicType(int value, byte[] bytes) {
			this.value = value;
			this.bytes = bytes;
		}

		public int getValue() {
			return value;
		}

		public byte[] getBytes() {
			return bytes;
		}
	}
}
