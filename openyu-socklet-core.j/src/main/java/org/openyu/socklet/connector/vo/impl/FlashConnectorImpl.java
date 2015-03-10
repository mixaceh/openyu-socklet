package org.openyu.socklet.connector.vo.impl;

import java.nio.channels.SelectionKey;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.lang.ObjectHelper;
import org.openyu.socklet.connector.vo.FlashConnector;
import org.openyu.socklet.connector.vo.supporter.SocketConnectorSupporter;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.CategoryType;
import org.openyu.socklet.message.vo.Message;

public class FlashConnectorImpl extends SocketConnectorSupporter implements FlashConnector
{
	
	private static final long serialVersionUID = -2888075402215391319L;

	private static transient Logger log = Logger.getLogger(FlashConnectorImpl.class);

//	// flash -> server, length=22
//	private static final String FLASH_INPUT_POLICY = "<policy-file-request/>";
//
//	private static final byte[] FLASH_INPUT_POLICY_BYTES = ByteHelper.toBytes(FLASH_INPUT_POLICY);
//
//	// server -> flash, length=87
//	private static final String FLASH_OUTPUT_POLICY = "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>";
//
//	private static final byte[] FLASH_OUTPUT_POLICY_BYTES = ByteHelper.toBytes(FLASH_OUTPUT_POLICY);
//
//	// length=1,flash socket,xmlSocket用
////	private static final char FLASH_EOF = (char) 0x00;
////
////	private static final byte[] FLASH_EOF_BYTES = ByteHelper.toBytes(FLASH_EOF);
//	
//	private final byte FLASH_EOF = 0x00;//1個byte
//
//	private final byte[] FLASH_EOF_BYTES = new byte[] { FLASH_EOF };
	

	// flash
	private boolean sendFlashPolicy;

	private boolean flashPolicy;

	private boolean readFlashPolicy;

	public FlashConnectorImpl(String id, Class moduleTypeClass, Class messageTypeClass,
							ProtocolService protocolService, String ip, int port)
	{
		super(id, moduleTypeClass, messageTypeClass, protocolService);
		this.ip = ip;
		this.port = port;
	}

	public boolean isSendFlashPolicy()
	{
		return sendFlashPolicy;
	}

	public boolean isFlashPolicy()
	{
		return flashPolicy;
	}

	public boolean isReadFlashPolicy()
	{
		return readFlashPolicy;
	}

	protected void connectable(SelectionKey selectionKey) throws Exception
	{
		// ---------------------------------------------
		// write flashPolicy
		// ---------------------------------------------
		if (!sendFlashPolicy)
		{
			sendFlashPolicy();
		}
	}

	protected void readable(SelectionKey selectionKey) throws Exception
	{
		byte[] bytes = receive();
		if (bytes == null)
		{
			return;
		}
		// ---------------------------------------------
		// read flashPolicy, FLASH_OUTPUT_POLICY+FLASH_EOF=87+1=88
		// ---------------------------------------------
		if (!readFlashPolicy && sendFlashPolicy && !flashPolicy && bytes.length == 88)
		{
			receiveFlashPolicy(bytes);
		}
		// ---------------------------------------------
		// read authKey, authKey=32
		// ---------------------------------------------
		else if (!readAuthKey && authKey == null && flashPolicy && bytes.length == 32)
		{
			receiveAuthKey(bytes);
			// ---------------------------------------------
			// write handshake
			// ---------------------------------------------
			// authKey[] = "8d0b45f1148e7a93cd19ae14f8f600df".getBytes();
			if (flashPolicy && authKey != null && !sendHandshake)
			{
				sendHandshake();
			}
		}
		// ---------------------------------------------
		// read handshake, 36
		// ---------------------------------------------
		else if (!readHandshake && sendHandshake && !handshake && bytes.length != 88
				&& bytes.length != 34 && bytes.length >= 36)
		{
			receiveHandshake(bytes);
		}
		// ---------------------------------------------
		// read message
		// ---------------------------------------------
		else if (flashPolicy && authKey != null && handshake)
		{
			receiveMessage(bytes);
		}
	}

	protected void writable(SelectionKey selectionKey) throws Exception
	{
		//		// ---------------------------------------------
		//		// write flashPolicy
		//		// ---------------------------------------------
		//		if (!sendFlashPolicy)
		//		{
		//			sendFlashPolicy();
		//		}
		//		// ---------------------------------------------
		//		// write handshake
		//		// ---------------------------------------------
		//		// authKey = "8d0b45f1148e7a93cd19ae14f8f600df".getBytes();
		//		// flashPolicy = true;get
		//		if (flashPolicy && authKey != null && !sendHandshake)
		//		{
		//			sendHandshake();
		//		}
	}

	protected void receiveFlashPolicy(byte[] bytes)
	{
		readFlashPolicy = true;
		bytes = ByteHelper.getByteArray(bytes, 0, bytes.length - FLASH_EOF_BYTES.length);
		String flashOutputPolicy = ByteHelper.toString(bytes);
		//
		if (FLASH_OUTPUT_POLICY.equalsIgnoreCase(flashOutputPolicy))
		{
			flashPolicy = true;
		}
		//
		//log("readFlashPolicy: " + flashPolicy);
	}

	protected void receiveAuthKey(byte[] bytes)
	{
		readAuthKey = true;
		bytes = ByteHelper.getByteArray(bytes, 0, bytes.length - FLASH_EOF_BYTES.length);
		if (bytes != null)
		{
			authKey = bytes;
		}
		//
		//log("readAuthKey: " + ByteHelper.toString(authKey));
	}

	protected void receiveHandshake(byte[] bytes)
	{
		readHandshake = true;
		bytes = ByteHelper.getByteArray(bytes, 0, bytes.length - FLASH_EOF_BYTES.length);
		Message message = protocolService.dehandshake(bytes);
		if (message != null)
		{
			//檢查認證碼是否與client發出去的相同
			byte[] serverAuthkey = message.getByteArray(0);
			if (serverAuthkey == null || serverAuthkey.length != 32
					|| !ObjectHelper.equals(serverAuthkey, authKey))
			{
				log.warn("authkey is invalid");
				return;
			}
			//
			if (CategoryType.HANDSHAKE_SERVER.equals(message.getCategoryType()))
			{
				handshake = true;
				acceptor = message.getSender();
				log.info("[" + id + "] " + "connected to [" + acceptor + "]");
			}
		}
		//
		//log("handshake: " + handshake);
	}

	protected void receiveMessage(byte[] bytes)
	{
		//接收者
		if (receiver == null)
		{
			log.warn("[" + id + "] " + "no receiver to receive message");
			return;
		}
		//
		bytes = ByteHelper.getByteArray(bytes, 0, bytes.length - FLASH_EOF_BYTES.length);
		@SuppressWarnings("unchecked")
		List<Message> messages = protocolService.disassemble(bytes, moduleTypeClass,
			messageTypeClass);
		//
		for (Message message : messages)
		{
			if (message == null)
			{
				continue;
			}
			receiver.receive(message);
		}
	}

	protected void sendFlashPolicy()
	{
		// System.out.println(writeBuffer);//[pos=0 lim=131072 cap=131072]
		//			writeBuffer.put(FLASH_INPUT_POLICY_BYTES);
		//			writeBuffer.put(FLASH_EOF_BYTES);
		//			// System.out.println(writeBuffer);//[pos=24 lim=131072 cap=131072]
		//			writeBuffer.flip();
		//			// System.out.println(writeBuffer);//[pos=0 lim=24 cap=131072]
		//			int write = clientChannel.write(writeBuffer);
		//			// System.out.println(writeBuffer);//[pos=24 lim=24 cap=131072]
		//			writeBuffer.clear();
		// System.out.println(writeBuffer);//[pos=0 lim=131072 cap=131072]

		int write = send(FLASH_INPUT_POLICY_BYTES, FLASH_EOF_BYTES);
		if (write > 0)
		{
			sendFlashPolicy = true;
		}
		//
		//log("sendFlashPolicy: " + sendFlashPolicy);
	}

	protected void sendHandshake()
	{
		byte[] bytes = protocolService.handshake(CategoryType.HANDSHAKE_CLIENT, authKey, id);
		//
		int write = send(bytes, FLASH_EOF_BYTES);
		if (write > 0)
		{
			sendHandshake = true;
		}
		//
		//log("sendHandshake: " + sendHandshake);
	}

//	public int send(Message message)
//	{
//		int result = 0;
//		if (flashPolicy && authKey != null && handshake)
//		{
//			byte[] bytes = protocolService.assemble(message);
//			if (bytes != null)
//			{
//				result = send(bytes, FLASH_EOF_BYTES);
//			}
//		}
//		return result;
//	}

	public void shutdown()
	{
		try
		{
			if (started)
			{
				super.shutdown();
				sendFlashPolicy = false;
				flashPolicy = false;
				readFlashPolicy = false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			started = false;
		}
	}
	
	public String toString()
	{
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.appendSuper(super.toString());
		builder.append("sendFlashPolicy", sendFlashPolicy);
		builder.append("flashPolicy", flashPolicy);
		builder.append("readFlashPolicy", readFlashPolicy);
		return builder.toString();
	}
}

// test writeBuffer
// String msg = "abcdeabcdeabcdeabcdeabcdeabcde";
// writeBuffer.put(msg.getBytes());
// System.out.println(writeBuffer);//[pos=30 lim=131072 cap=131072]
// writeBuffer.flip();
// System.out.println(writeBuffer);//[pos=0 lim=30 cap=131072]
// write = channel.write(writeBuffer);
// System.out.println(writeBuffer);//[pos=30 lim=30 cap=131072]
// writeBuffer.clear();
// System.out.println(writeBuffer);//[pos=0 lim=131072 cap=131072]
// if (write > 0)
// {
// System.out.println("[client] send: " + write + ", " + msg);
// }
