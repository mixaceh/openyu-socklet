package org.openyu.socklet.message.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.openyu.commons.enumz.EnumHelper;
import org.openyu.commons.io.IoHelper;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.EncodingHelper;
import org.openyu.commons.lang.NumberHelper;
import org.openyu.commons.mark.Magicer;
import org.openyu.commons.misc.UnsafeHelper;
import org.openyu.commons.security.SecurityType;
import org.openyu.commons.service.supporter.BaseServiceSupporter;
import org.openyu.commons.util.AssertHelper;
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
import org.openyu.socklet.message.vo.HeadType;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.message.vo.MessageType;
import org.openyu.socklet.message.vo.ModuleType;
import org.openyu.socklet.message.vo.Packet;
import org.openyu.socklet.message.vo.PriorityType;
import org.openyu.socklet.message.vo.impl.MessageImpl;
import org.openyu.socklet.message.vo.impl.PacketImpl;
import org.openyu.commons.enumz.IntEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 協定服務
 * 
 * 組成/反組byte[]
 */
public class ProtocolServiceImpl extends BaseServiceSupporter implements ProtocolService {

	private static final long serialVersionUID = 7562819271771546706L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(ProtocolServiceImpl.class);

	/** 檢查碼 */
	private ChecksumProcessor checksumProcessor = new ChecksumProcessorImpl();

	/** 安全性 */
	private SecurityProcessor securityProcessor = new SecurityProcessorImpl();

	/** 壓縮 */
	private CompressProcessor compressProcessor = new CompressProcessorImpl();

	public ProtocolServiceImpl() {
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

	@Override
	protected void doStart() throws Exception {

	}

	@Override
	protected void doShutdown() throws Exception {

	}

	/**
	 * 握手協定,組成byte[]
	 * 
	 * @param categoryType
	 * @param authKey
	 * @param sender
	 * @return
	 */
	public byte[] handshake(CategoryType categoryType, byte[] authKey, String sender) {
		byte[] result = new byte[0];
		//
		AssertHelper.notNull(categoryType, "The CategoryType must not be null");
		AssertHelper.notNull(authKey, "The AuthKey must not be null");
		AssertHelper.isTrue((authKey.length == 32), "The AuthKey length is invalid");
		AssertHelper.notBlank(sender, "The Sender must not be blank");
		AssertHelper.isTrue((sender.length() <= 32), "The Sender length is invalid");

		// ByteArrayOutputStream out = null;
		// ByteArrayOutputStream headOut = null;
		// ByteArrayOutputStream dataOut = null;
		//
		// ByteBuffer out = ByteBuffer.allocate(78);
		// ByteBuffer dataOut = ByteBuffer.allocate(75);
		//
		byte[] dataOut = new byte[75];
		try {

			// out = headOut + dataOut
			// dataOut = infoOut + contentOut + (checksum)
			// ------------------------------------------

			// ------------------------------------------
			// head
			// ------------------------------------------
			// 0, 61(byte)
			// 1-2, 5308(short)magic

			// ------------------------------------------
			// data
			// ------------------------------------------
			// info
			// 3, 2(byte)category
			// 4, 3(byte)priority
			// 5, 11(byte)sender.length, 原為int會變成byte
			// 6-37, TEST_ROLE(byte[9])sender, max=32

			// content
			// 之後為authKey內容,長度32
			// 38-69, aacc8964324738c27c07746e3ea81aff
			// (byte[32])authKey

			// checksum
			// 70-77, 3048607855 (long)checksum
			// ------------------------------------------

			// head長度
			int headLengh = (1)// (byte)totalLength
					+ (2);// (short)magic

			// data長度
			int dataLength = 0//
					+ 1 // categoryTypeLength
					+ 1// priorityTypeLength
					+ (1)// (byte)senderLength
					+ sender.length() // sender, max=32
					+ (32)// authkeyLength

			// 8 checksum
			;

			// ------------------------------------------
			// data
			// ------------------------------------------
			int pos = 0;

			// info: categoryType
			// byte[] categoryTypeBytes = ByteHelper.toByteArray(categoryType
			// .getValue());
			// dataOut.write(categoryTypeBytes);
			// dataOut.put(categoryType.getValue());
			UnsafeHelper.putByte(dataOut, pos, categoryType.getValue());
			pos += 1;

			// info: priorityType
			// byte[] priorityTypeBytes =
			// ByteHelper.toByteArray(PriorityType.URGENT
			// .getValue());
			// dataOut.write(priorityTypeBytes);
			// dataOut.put(PriorityType.URGENT.getValue());
			UnsafeHelper.putByte(dataOut, pos, PriorityType.URGENT.getValue());
			pos += 1;

			// info: sender.length(byte)
			byte[] senderLengthBytes = ByteHelper.toByteByteArray(sender.length());
			// dataOut.write(senderLengthBytes);
			// dataOut.put(senderLengthBytes);
			UnsafeHelper.putByteArray(dataOut, pos, senderLengthBytes);
			pos += 1;

			// info: sender
			byte[] senderBytes = ByteHelper.toByteArray(sender);
			// dataOut.write(senderBytes);
			// dataOut.put(senderBytes);
			UnsafeHelper.putByteArray(dataOut, pos, senderBytes);
			pos += senderBytes.length;

			// content: authkey
			// dataOut.write(authKey);
			// dataOut.put(authKey);
			UnsafeHelper.putByteArray(dataOut, pos, authKey);
			pos += authKey.length;

			// 1.檢查碼
			if (isChecksum()) {
				// dataOut.flip();
				// byte[] buff = new byte[dataOut.remaining()];
				// dataOut.get(buff);
				// dataOut.limit(dataOut.capacity());

				byte[] buff = UnsafeHelper.getByteArray(dataOut, 0, pos);
				//
				long checksum = checksumProcessor.checksum(buff);
				// System.out.println("checksum: " + checksum);
				byte[] checksumBytes = ByteHelper.toByteArray(checksum);
				// dataOut.write(checksumBytes);
				// dataOut.putLong(checksum);
				UnsafeHelper.putByteArray(dataOut, pos, checksumBytes);
				pos += 8;
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

			// byte[] buff = dataOut.toByteArray();

			// dataOut.flip();
			// byte[] buff = new byte[dataOut.remaining()];
			// dataOut.get(buff);
			// dataOut.limit(dataOut.capacity());

			byte[] buff = UnsafeHelper.getByteArray(dataOut, 0, pos);

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
			// 總長度(byte)
			byte[] totalLengthBytes = ByteHelper.toByteByteArray(totalLength);

			// head
			// headOut.write(totalLengthBytes);
			// headOut.write(MagicType.HANKSHAKE.getBytes());// length=2
			// headOut.writeTo(out);

			// out.put(totalLengthBytes);
			// out.put(MagicType.HANKSHAKE.getBytes());

			// 當加密或壓縮時 ,會改變總長度, 此時再宣告out的大小
			byte[] out = new byte[totalLength];
			pos = 0;
			UnsafeHelper.putByteArray(out, pos, totalLengthBytes);
			pos += 1;

			UnsafeHelper.putByteArray(out, pos, MagicType.HANKSHAKE.toByteArray());
			pos += 2;

			// data
			// out.write(buff);

			// out.put(buff);

			//
			// result = out.toByteArray();
			// out.flip();

			// result = new byte[out.remaining()];
			// out.get(result);

			UnsafeHelper.putByteArray(out, pos, buff);
			pos += buff.length;
			//
			result = UnsafeHelper.getByteArray(out, 0, pos);
			result = encode(HeadType.HANDSHAKE, result);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// IoHelper.close(headOut);
			// IoHelper.close(dataOut);
			// IoHelper.close(out);
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
		AssertHelper.notNull(values, "The Values must not be null");
		//
		Packet<byte[]> packet = decode(values);
		AssertHelper.isTrue((HeadType.HANDSHAKE == packet.getHeadType()), "The HeadType must be HANDSHAKE");
		byte[] buff = packet.getBody();
		//
		ByteArrayOutputStream dataIn = null;
		try {
			dataIn = new ByteArrayOutputStream();
			// ------------------------------------------
			// head
			// ------------------------------------------
			int pos = 0;
			// 資料總長度
			int totalLength = ByteHelper.fromByteInt(ByteHelper.getByteArray(buff, pos, 1));// 0
			pos += 1;
			// System.out.println("totalLength: " + totalLength);

			// 實際收的資料長度
			AssertHelper.isTrue(buff.length >= 38, "Data length [" + buff.length + "] is invalid");

			// 實際收的資料長度 <資料總長度
			AssertHelper.isTrue(buff.length >= totalLength,
					"Data length [" + buff.length + "] < expected length [" + totalLength + "] bytes");

			//
			byte[] magicTypeBytes = ByteHelper.getByteArray(buff, pos, 2);
			int magicTypeValue = ByteHelper.fromShortInt(magicTypeBytes);// 2-3
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
			byte[] data = ByteHelper.getByteArray(buff, pos, totalLength - headLengh);

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
			CategoryType categoryType = EnumHelper.valueOf(CategoryType.class, ByteHelper.toByte(categoryTypeBytes));
			// System.out.println("categoryType: " + categoryType);
			dataIn.write(categoryTypeBytes);
			pos += 1;

			// info: priorityType
			byte[] priorityTypeBytes = ByteHelper.getByteArray(data, pos, 1);
			PriorityType priorityType = EnumHelper.valueOf(PriorityType.class, ByteHelper.toByte(priorityTypeBytes));
			// System.out.println("priorityType: " + priorityType);
			dataIn.write(priorityTypeBytes);
			pos += 1;

			// ------------------------------------------
			// CategoryType.HANDSHAKE
			// ------------------------------------------
			// 是否為handshake類型
			boolean handshakeType = CategoryType.HANDSHAKE_CLIENT == categoryType
					|| CategoryType.HANDSHAKE_RELATION == categoryType || CategoryType.HANDSHAKE_SERVER == categoryType;
			if (handshakeType) {
				// info: sender.length
				byte[] senderLengthBytes = ByteHelper.getByteArray(data, pos, 1);
				int senderLength = ByteHelper.fromByteInt(senderLengthBytes);
				// System.out.println("senderLength: " + senderLength);
				dataIn.write(senderLengthBytes);
				pos += 1;

				// info: sender
				byte[] senderBytes = ByteHelper.getByteArray(data, pos, senderLength);
				StringBuilder sender = new StringBuilder(ByteHelper.toString(senderBytes));
				// System.out.println("sender: " + sender);
				dataIn.write(senderBytes);
				pos += senderLength;

				// content: authkey
				byte[] authkey = ByteHelper.getByteArray(data, pos, 32);
				dataIn.write(authkey);
				pos += 32;

				// 3.檢查碼
				if (isChecksum()) {
					// 從遠端收到的checksum
					byte[] checksumBytes = ByteHelper.getByteArray(data, pos, 8);
					pos += 8;
					AssertHelper.notNull(checksumBytes, "The Checksum must not be null");
					//
					long checksum = ByteHelper.toLong(checksumBytes);
					// System.out.println("checksum: " + checksum);

					// 本地算出來的checksum
					long realChecksum = checksumProcessor.checksum(dataIn.toByteArray());
					// System.out.println("realChecksum: " +
					// realChecksum);

					AssertHelper.isTrue(realChecksum == checksum,
							"Checksum [" + realChecksum + "] not equal expected [" + checksum + "]");
				}

				// message
				result = new MessageImpl(categoryType, priorityType);
				result.setSender(sender.toString());
				result.addByteArray(authkey);// 0, String
			} else {
				AssertHelper.unsupported("Unsupported categoryType [" + categoryType + "]");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IoHelper.close(dataIn);
		}
		return result;
	}

	/**
	 * 訊息協定,組成byte[]
	 * 
	 * @param message
	 * @return
	 * 
	 * 		CategoryType.MESSAGE_CLIENT
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
		AssertHelper.notNull(message, "The Message must not be null");
		// 訊息
		Message buff = (Message) message.clone();
		// System.out.println(cloneMessage);
		String sender = buff.getSender();
		AssertHelper.notBlank(sender, "The Sender must not be blank");
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
			List<byte[]> contents = buff.getContents();
			for (byte[] content : contents) {
				byte[] contentLengthBytes = ByteHelper.toShortByteArray(content.length);
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
			boolean serverType = CategoryType.MESSAGE_RELATION == buff.getCategoryType()
					|| CategoryType.MESSAGE_SERVER == buff.getCategoryType()
					|| CategoryType.MESSAGE_SYNC == buff.getCategoryType();
			if (serverType) {
				// 接收者
				int receiversLength = 0;
				for (String receiver : buff.getReceivers()) {
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
			byte[] categoryTypeBytes = ByteHelper.toByteArray(buff.getCategoryType().getValue());
			dataOut.write(categoryTypeBytes);
			// info: priorityType
			byte[] priorityTypeBytes = ByteHelper.toByteArray(buff.getPriorityType().getValue());
			dataOut.write(priorityTypeBytes);

			// info: messageType
			byte[] messageTypeBytes = ByteHelper.toByteArray(buff.getMessageType().getValue());
			dataOut.write(messageTypeBytes);

			// info: sender.length
			byte[] senderLengthBytes = ByteHelper.toShortByteArray(sender.length());
			dataOut.write(senderLengthBytes);

			// info: sender
			byte[] senderBytes = ByteHelper.toByteArray(sender);
			dataOut.write(senderBytes);

			// 是否為server發送
			// System.out.println("serverType: "+serverType);
			if (serverType) {
				// info: srcModule 來源模組
				byte[] srcModuleTypeBytes = ByteHelper.toByteArray(buff.getSrcModule().getValue());
				dataOut.write(srcModuleTypeBytes);

				// info: destModule 目的模組
				byte[] destModuleTypeBytes = ByteHelper.toByteArray(buff.getDestModule().getValue());
				dataOut.write(destModuleTypeBytes);

				// info: receiversSize
				int receiversSize = buff.getReceivers().size();
				byte[] receiversSizeBytes = ByteHelper.toShortByteArray(receiversSize);
				dataOut.write(receiversSizeBytes);
				// info: receivers
				for (String receiver : buff.getReceivers()) {
					byte[] receiverLengthBytes = ByteHelper.toShortByteArray(receiver.length());
					dataOut.write(receiverLengthBytes);

					byte[] receiverBytes = ByteHelper.toByteArray(receiver);
					dataOut.write(receiverBytes);
				}
			}

			// content: contentsSize
			int contentsSize = contents.size();
			byte[] contentsSizeBytes = ByteHelper.toShortByteArray(contentsSize);
			dataOut.write(contentsSizeBytes);

			// content: contentsLengthOut
			contentsLengthOut.writeTo(dataOut);

			// content: contentsOut
			contentsOut.writeTo(dataOut);

			// 1.檢查碼
			if (isChecksum()) {
				long checksum = checksumProcessor.checksum(dataOut.toByteArray());
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
			byte[] buf = dataOut.toByteArray();
			// 2.加密
			if (isSecurity()) {
				buf = securityProcessor.encrypt(buf);
				// 會改變長度
				dataLength = buf.length;
				totalLength = headLengh + dataLength;
			}

			// 3.壓縮
			if (isCompress()) {
				buf = compressProcessor.compress(buf);
				// 會改變長度
				dataLength = buf.length;
				totalLength = headLengh + dataLength;
			}
			// 總長度
			byte[] totalLengthBytes = ByteHelper.toByteArray(totalLength);

			// head
			headOut.write(totalLengthBytes);
			headOut.write(MagicType.MESSAGE.toByteArray());// length=2
			headOut.writeTo(out);

			// data
			out.write(buf);
			//
			result = out.toByteArray();
			result = encode(HeadType.MESSAGE, result);
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
	public <T extends Enum<T>, U extends Enum<U>> List<Message> disassemble(byte[] values, Class<T> moduleEnumType,
			Class<U> messageEnumType) {
		List<Message> result = new LinkedList<Message>();
		AssertHelper.notNull(values, "The Values must not be null");
		//
		Packet<byte[]> packet = decode(values);
		AssertHelper.isTrue((HeadType.MESSAGE == packet.getHeadType()), "The HeadType must be MESSAGE");
		byte[] buff = packet.getBody();
		try {
			int pos = 0;
			int dataLength = 0;
			while (pos < buff.length
					&& (dataLength = ByteHelper.toInt(ByteHelper.getByteArray(buff, pos, 4))) < buff.length - pos + 1) {
				Message message = disassemble(pos, buff, moduleEnumType, messageEnumType);
				if (message == null) {
					break;
				}
				result.add(message);
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
	protected <T extends Enum<T>, U extends Enum<U>> Message disassemble(int pos, byte[] values,
			Class<T> moduleEnumType, Class<U> messageEnumType) {
		Message result = null;
		//
		AssertHelper.notNull(values, "The Values must not be null");
		//
		ByteArrayOutputStream dataOut = null;
		try {
			dataOut = new ByteArrayOutputStream();
			// ------------------------------------------
			// head
			// ------------------------------------------
			// int pos = 0;
			// 資料總長度
			int totalLength = ByteHelper.toInt(ByteHelper.getByteArray(values, pos, 4));
			pos += 4;
			// System.out.println("totalLength: " + totalLength);

			// 實際收的資料長度
			if (values.length < 12) {
				LOGGER.error("Data[" + values.length + "] < 12 bytes");
				return result;
			}
			// 實際收的資料長度 < 資料總長度
			else if (values.length < totalLength) {
				LOGGER.error("Data[" + values.length + "] < [" + totalLength + "] bytes");
				return result;
			}
			//
			int magicTypeValue = ByteHelper.fromShortInt(ByteHelper.getByteArray(values, pos, 2));// 4-5
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
			byte[] data = ByteHelper.getByteArray(values, pos, totalLength - headLengh);

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
			CategoryType categoryType = EnumHelper.valueOf(CategoryType.class, ByteHelper.toByte(categoryTypeBytes));
			// System.out.println("categoryType: " + categoryType);
			dataOut.write(categoryTypeBytes);
			pos += 1;

			// info: priorityType
			byte[] priorityTypeBytes = ByteHelper.getByteArray(data, pos, 1);
			PriorityType priorityType = EnumHelper.valueOf(PriorityType.class, ByteHelper.toByte(priorityTypeBytes));
			// System.out.println("priorityType: " + priorityType);
			dataOut.write(priorityTypeBytes);
			pos += 1;

			// info: messageType
			byte[] messageTypeBytes = ByteHelper.getByteArray(data, pos, 4);
			int messageTypeValue = ByteHelper.toInt(messageTypeBytes);
			MessageType messageType = (MessageType) EnumHelper.valueOf(messageEnumType, messageTypeValue);
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
			byte[] senderBytes = ByteHelper.getByteArray(data, pos, senderLength);
			StringBuilder sender = new StringBuilder(ByteHelper.toString(senderBytes));
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
					|| CategoryType.MESSAGE_SERVER == categoryType || CategoryType.MESSAGE_SYNC == categoryType;
			// System.out.println("serverType: "+serverType);
			if (serverType) {
				// info: srcModule 來源模組
				byte[] srcModuleTypeBytes = ByteHelper.getByteArray(data, pos, 4);
				srcModuleType = (ModuleType) EnumHelper.valueOf(moduleEnumType, ByteHelper.toInt(srcModuleTypeBytes));
				// System.out.println("srcModuleType: " + srcModuleType);
				dataOut.write(srcModuleTypeBytes);
				pos += 4;

				// info: destModule 目的模組
				byte[] destModuleTypeBytes = ByteHelper.getByteArray(data, pos, 4);
				destModuleType = (ModuleType) EnumHelper.valueOf(moduleEnumType, ByteHelper.toInt(destModuleTypeBytes));
				// System.out.println("destModuleType: " + destModuleType);
				dataOut.write(destModuleTypeBytes);
				pos += 4;

				// info: receiversSize
				byte[] receiversSizeBytes = ByteHelper.getByteArray(data, pos, 2);
				int receiversSize = ByteHelper.fromShortInt(receiversSizeBytes);
				// System.out.println("receiversSize: " + receiversSize);
				dataOut.write(receiversSizeBytes);
				pos += 2;
				// info: receivers
				for (int i = 0; i < receiversSize; i++) {
					byte[] receiverLengthBytes = ByteHelper.getByteArray(data, pos, 2);
					int receiverLength = ByteHelper.fromShortInt(receiverLengthBytes);
					dataOut.write(receiverLengthBytes);
					pos += 2;
					// System.out.println("receiverLength: " + receiverLength);
					//
					byte[] receiverBytes = ByteHelper.getByteArray(data, pos, receiverLength);
					StringBuilder receiver = new StringBuilder(ByteHelper.toString(receiverBytes));
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
				byte[] contentLengthBytes = ByteHelper.getByteArray(data, pos, 2);
				contentsLength[i] = ByteHelper.fromShortInt(contentLengthBytes);
				dataOut.write(contentLengthBytes);
				pos += 2;
				// System.out.println("contentLength: " + contentsLength[i]);
			}

			// content: contents
			List<byte[]> contents = new LinkedList<byte[]>();
			for (int i = 0; i < contentsSize; i++) {
				byte[] content = ByteHelper.getByteArray(data, pos, contentsLength[i]);
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
				long realChecksum = checksumProcessor.checksum(dataOut.toByteArray());
				// System.out.println("realChecksum: " + realChecksum);

				if (realChecksum != checksum) {
					LOGGER.error("Checksum [" + realChecksum + "] not equal expected [" + checksum + "]");
					return result;
				}
			}

			boolean clientType = CategoryType.MESSAGE_CLIENT == categoryType;
			boolean accptorType = CategoryType.MESSAGE_ACCEPTOR == categoryType;

			// MESSAGE_CLIENT
			if (clientType) {
				// ------------------------------------------
				result = new MessageImpl(categoryType, priorityType, sender.toString(), messageType);

				// destModule,編號來自於messageIntValue,前3碼+"00",如:150001->150000
				String pattern = "##0";
				StringBuilder sb = new StringBuilder(NumberHelper.toString(messageTypeValue, pattern));
				String destModule = sb.substring(0, 3) + "000";
				int destModuleTypeValue = NumberHelper.toInt(destModule, 0, pattern);

				// System.out.println("destModuleTypeValue: "
				// + destModuleTypeValue);
				destModuleType = (ModuleType) EnumHelper.valueOf(moduleEnumType, destModuleTypeValue);
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
				result = new MessageImpl(categoryType, priorityType, srcModuleType, destModuleType, messageType,
						receivers);
				result.setSender(sender.toString());

				// content
				for (int i = 0; i < contents.size(); i++) {
					result.addByteArray(contents.get(i));
				}
			}
			// MESSAGE_ACCEPTOR
			else if (accptorType) {
				// ------------------------------------------
				result = new MessageImpl(categoryType, priorityType, sender.toString(), messageType);
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

	// ------------------------------------------
	// 2014/12/15
	// ------------------------------------------
	public byte[] encode(HeadType headType) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = headType.toByteArray();
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		//
		return result;
	}

	public byte[] encode(HeadType headType, boolean body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = ByteHelper.toByteArray(body);
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		//
		return result;
	}

	public byte[] encode(HeadType headType, char body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = ByteHelper.toByteArray(body);
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		return result;
	}

	public byte[] encode(HeadType headType, String body) {
		return encode(headType, body, EncodingHelper.UTF_8);
	}

	public byte[] encode(HeadType headType, String body, String charsetName) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		AssertHelper.notNull(body, "The Body must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = ByteHelper.toByteArray(body, charsetName);
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		//
		return result;
	}

	public byte[] encode(HeadType headType, byte body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = ByteHelper.toByteArray(body);
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		//
		return result;
	}

	public byte[] encode(HeadType headType, short body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = ByteHelper.toByteArray(body);
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		//
		return result;
	}

	public byte[] encode(HeadType headType, int body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = ByteHelper.toByteArray(body);
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		//
		return result;
	}

	public byte[] encode(HeadType headType, long body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = ByteHelper.toByteArray(body);
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		//
		return result;
	}

	public byte[] encode(HeadType headType, float body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = ByteHelper.toByteArray(body);
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		//
		return result;
	}

	public byte[] encode(HeadType headType, double body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		//
		byte[] headBytes = headType.toByteArray();
		byte[] bodyBytes = ByteHelper.toByteArray(body);
		int length = (4 + 4) + bodyBytes.length;
		byte[] lengthBytes = ByteHelper.toByteArray(length);
		//
		// result = ArrayHelper.add(headBytes, lengthBytes);
		// result = ArrayHelper.add(result, bodyBytes);
		//
		result = headBytes;
		result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
		result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
		//
		return result;
	}

	public byte[] encode(HeadType headType, byte[] body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		AssertHelper.notNull(body, "The Body must not be null");
		//
		try {
			byte[] headBytes = headType.toByteArray();
			byte[] bodyBytes = body;
			int length = (4 + 4) + bodyBytes.length;
			byte[] lengthBytes = ByteHelper.toByteArray(length);
			//
			// result = ArrayHelper.add(headBytes, lengthBytes);
			// result = ArrayHelper.add(result, bodyBytes);
			//
			result = headBytes;
			result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
			result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
			//
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//
		return result;
	}

	public byte[] encode(HeadType headType, Serializable body) {
		byte[] result = new byte[0];
		AssertHelper.notNull(headType, "The HeadType must not be null");
		AssertHelper.notNull(body, "The Body must not be null");
		//
		try {
			byte[] headBytes = headType.toByteArray();
			byte[] bodyBytes = ByteHelper.toByteArray(body);
			int length = (4 + 4) + bodyBytes.length;
			byte[] lengthBytes = ByteHelper.toByteArray(length);
			//
			// result = ArrayHelper.add(headBytes, lengthBytes);
			// result = ArrayHelper.add(result, bodyBytes);
			//
			result = headBytes;
			result = UnsafeHelper.putByteArray(result, result.length, lengthBytes);
			result = UnsafeHelper.putByteArray(result, result.length, bodyBytes);
			//
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//
		return result;
	}

	public Packet<byte[]> decode(byte[] values) {
		Packet<byte[]> result = null;
		AssertHelper.notNull(values, "The Values must not be null");
		//
		try {
			int pos = 0;
			byte[] headBytes = ByteHelper.getByteArray(values, pos, 4);
			pos += 4;
			//
			byte[] lengthBytes = ByteHelper.getByteArray(values, pos, 4);
			pos += 4;
			//
			byte[] bodyBytes = ByteHelper.getByteArray(values, pos, values.length - pos);
			// SystemUtil.println(headBytes);
			//
			int headTypeValue = ByteHelper.toInt(headBytes);
			HeadType headType = EnumHelper.valueOf(HeadType.class, headTypeValue);
			int length = ByteHelper.toInt(lengthBytes);
			// System.out.println(headType);

			switch (headType) {
			case HANDSHAKE: {
				result = new PacketImpl<byte[]>(values, headType, length, bodyBytes);
				break;
			}
			case MESSAGE: {
				result = new PacketImpl<byte[]>(values, headType, length, bodyBytes);
				break;
			}
			case KEEP_ALIVE: {
				result = new PacketImpl<byte[]>(values, headType, length, bodyBytes);
				break;
			}
			case FILE: {
				result = new PacketImpl<byte[]>(values, headType, length, bodyBytes);
				break;
			}
			default: {
				LOGGER.error("Can't decode values");
				break;
			}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	protected enum MagicType implements IntEnum,Magicer {

		/**
		 * 握手用
		 * 
		 * int -> short
		 */
		// 14BC
		HANKSHAKE(5308) {
			public byte[] toByteArray() {
				if (byteArray == null) {
					byteArray = ByteHelper.toShortByteArray(getValue());
				}
				return byteArray;
			}
		},

		/**
		 * 訊息用
		 * 
		 * int -> short
		 */
		// BC14
		MESSAGE(48148) {
			public byte[] toByteArray() {
				if (byteArray == null) {
					byteArray = ByteHelper.toShortByteArray(getValue());
				}
				return byteArray;
			}
		},

		//
		;

		private final int value;

		protected byte[] byteArray;

		private MagicType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public abstract byte[] toByteArray();

	}

}
