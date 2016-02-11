package org.openyu.socklet.connector.vo.impl;

import java.nio.channels.SelectionKey;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openyu.socklet.connector.vo.ConnectorType;
import org.openyu.socklet.connector.vo.JavaConnector;
import org.openyu.socklet.connector.vo.supporter.SocketConnectorSupporter;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.CategoryType;

/**
 * 1.收到訊息,並無放到queue,而是藉由receiver轉出去,在 setReceiver設定
 * 
 * 2.發送訊息,並無放到queue,直接由socket發送出去
 * 
 * 3.通訊協定
 * 
 * sendAlready -> receiveAuthKey -> sendHandshake -> receiveHandshake
 * 
 */
public class JavaConnectorImpl extends SocketConnectorSupporter implements
		JavaConnector {

	private static final long serialVersionUID = -2329630939049962051L;

	private static transient Logger LOGGER = LoggerFactory
			.getLogger(JavaConnectorImpl.class);

	private boolean sendAlready;

	@SuppressWarnings("rawtypes")
	public JavaConnectorImpl(String id, Class moduleTypeClass,
			Class messageTypeClass, ProtocolService protocolService, String ip,
			int port) {
		super(id, moduleTypeClass, messageTypeClass, protocolService);
		this.ip = ip;
		this.port = port;
	}

	@SuppressWarnings("rawtypes")
	public JavaConnectorImpl(Class moduleTypeClass, Class messageTypeClass,
			ProtocolService protocolService) {
		this(null, moduleTypeClass, messageTypeClass, protocolService, null, 0);
	}

	public boolean isSendAlready() {
		return sendAlready;
	}

	protected void connectable(SelectionKey selectionKey) throws Exception {
		// ---------------------------------------------
		// write already
		// ---------------------------------------------
		if (!sendAlready) {
			sendAlready();
		}
	}

	protected void readable(SelectionKey selectionKey) throws Exception {
		byte[] bytes = receive();
		// ---------------------------------------------
		// read authKey, authKey=32
		// ---------------------------------------------
		if (!readAuthKey && authKey == null && bytes != null
				&& bytes.length == 32) {
			receiveAuthKey(bytes);
			// ---------------------------------------------
			// write handshake
			// ---------------------------------------------
			// authKey = "8d0b45f1148e7a93cd19ae14f8f600df".getBytes();
			if (authKey != null && !sendHandshake) {
				sendHandshake();
			}
		}
		// ---------------------------------------------
		// read handshake, 36
		// ---------------------------------------------
		else if (!readHandshake && sendHandshake && !handshake && bytes != null
				&& bytes.length >= 36) {
			receiveHandshake(bytes);
		}
		// ---------------------------------------------
		// read message
		// ---------------------------------------------
		else if (authKey != null && handshake && bytes != null) {
			receiveMessage(bytes);
		}
	}

	protected void writable(SelectionKey selectionKey) throws Exception {
		// // ---------------------------------------------
		// // write already
		// // ---------------------------------------------
		// if (!sendAlready)
		// {
		// sendAlready();
		// }

		// ---------------------------------------------
		// write handshake
		// ---------------------------------------------
		// authKey = "8d0b45f1148e7a93cd19ae14f8f600df".getBytes();
		// if (authKey != null && !sendHandshake)
		// {
		// sendHandshake();
		// }
	}

	/**
	 * byte[0]=1, flash client
	 * 
	 * byte[0]=2, java client
	 * 
	 * byte[0]=3, java relation
	 */
	protected void sendAlready() {
		byte[] bytes = new byte[] { ConnectorType.JAVA_SOCKLET.getValue() };
		int write = send(bytes);
		if (write > 0) {
			sendAlready = true;
		}
		//
		// System.out.println("sendAlready: " + sendAlready);
	}

	protected void sendHandshake() {
		byte[] bytes = protocolService.handshake(CategoryType.HANDSHAKE_CLIENT,
				authKey, id);
		int write = send(bytes);
		if (write > 0) {
			sendHandshake = true;
		}
		//
		// System.out.println("sendHandshake: " + sendHandshake);
	}

	public void shutdown() {
		try {
			if (started) {
				super.shutdown();
				sendAlready = false;
			}
		} catch (Exception ex) {
			started = false;
			LOGGER.warn("[" + id + "]", ex);
		}
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.appendSuper(super.toString());
		builder.append("sendAlready", sendAlready);
		return builder.toString();
	}
}
