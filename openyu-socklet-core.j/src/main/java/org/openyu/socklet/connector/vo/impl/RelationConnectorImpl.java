package org.openyu.socklet.connector.vo.impl;

import java.nio.channels.SelectionKey;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.openyu.socklet.connector.vo.ConnectorType;
import org.openyu.socklet.connector.vo.RelationConnector;
import org.openyu.socklet.connector.vo.supporter.SocketConnectorSupporter;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.CategoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationConnectorImpl extends SocketConnectorSupporter implements
		RelationConnector {

	private static final long serialVersionUID = 4459724922826063827L;

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(RelationConnectorImpl.class);

	private boolean sendAlready;

	@SuppressWarnings("rawtypes")
	public RelationConnectorImpl(String id, Class moduleTypeClass,
			Class messageTypeClass, ProtocolService protocolService, String ip,
			int port) {
		super(id, moduleTypeClass, messageTypeClass, protocolService);
		this.ip = ip;
		this.port = port;
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
		if (bytes == null) {
			return;
		}
		// ---------------------------------------------
		// read authKey, authKey=32
		// ---------------------------------------------
		if (!readAuthKey && authKey == null && bytes.length == 32) {
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
		else if (!readHandshake && sendHandshake && !handshake
				&& bytes.length >= 36) {
			receiveHandshake(bytes);
		}
		// ---------------------------------------------
		// read message
		// ---------------------------------------------
		else if (authKey != null && handshake) {
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

	protected void sendAlready() {
		byte[] bytes = new byte[] { ConnectorType.RELATION_SOCKLET.getValue() };
		int write = send(bytes);
		if (write > 0) {
			sendAlready = true;
		}
		//
		// log("sendAlready: " + sendAlready);
	}

	protected void sendHandshake() {
		// SystemHelper.println("authKey: ", authKey);
		// System.out.println("id: " + id);
		byte[] bytes = protocolService.handshake(
				CategoryType.HANDSHAKE_RELATION, authKey, id);
		int write = send(bytes);
		if (write > 0) {
			sendHandshake = true;
		}
		//
		// System.out.println("sendHandshake: " + sendHandshake);
		// SystemHelper.println(bytes);
	}

	public void shutdown() {
		try {
			if (started) {
				super.shutdown();
				sendAlready = false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			started = false;
		}
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.appendSuper(super.toString());
		builder.append("sendAlready", sendAlready);
		return builder.toString();
	}
}
