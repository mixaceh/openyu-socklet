package org.openyu.socklet.connector.vo.impl;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.openyu.socklet.connector.vo.AcceptorConnector;
import org.openyu.socklet.connector.vo.supporter.GenericConnectorSupporter;
import org.openyu.socklet.message.service.ProtocolService;

public class AcceptorConnectorImpl extends GenericConnectorSupporter implements AcceptorConnector
{

	private static final long serialVersionUID = 4868329624131205448L;

	// localhost:3100
	private String server;

	// flashPolicy
	private boolean sendFlashPolicy;

	private boolean readFlashPolicy;

	// already
	private boolean readAlready;

	// authKey
	private boolean sendAuthKey;

	private byte[] authKey;

	// handshake
	private boolean sendHandshake;

	private boolean handshake;

	private boolean readHandshake;

	private String serverIp;

	private int serverPort;

	private String clientIp;

	private int clientPort;

	public AcceptorConnectorImpl(String id, Class<?> moduleTypeClass, Class<?> messageTypeClass,
									ProtocolService protocolService)
	{
		super(id, moduleTypeClass, messageTypeClass, protocolService);
	}

	public AcceptorConnectorImpl(String id)
	{
		super(id);
	}

	public void start()
	{
		if (!started)
		{
			started = true;
		}
	}

	public String getAcceptor()
	{
		return acceptor;
	}

	public void setAcceptor(String acceptor)
	{
		this.acceptor = acceptor;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public boolean isSendFlashPolicy()
	{
		return sendFlashPolicy;
	}

	public void setSendFlashPolicy(boolean sendFlashPolicy)
	{
		this.sendFlashPolicy = sendFlashPolicy;
	}

	public boolean isReadFlashPolicy()
	{
		return readFlashPolicy;
	}

	public void setReadFlashPolicy(boolean readFlashPolicy)
	{
		this.readFlashPolicy = readFlashPolicy;
	}

	public boolean isReadAlready()
	{
		return readAlready;
	}

	public void setReadAlready(boolean readAlready)
	{
		this.readAlready = readAlready;
	}

	public boolean isSendAuthKey()
	{
		return sendAuthKey;
	}

	public void setSendAuthKey(boolean sendAuthKey)
	{
		this.sendAuthKey = sendAuthKey;
	}

	public byte[] getAuthKey()
	{
		return authKey;
	}

	public void setAuthKey(byte[] authKey)
	{
		this.authKey = authKey;
	}

	public boolean isSendHandshake()
	{
		return sendHandshake;
	}

	public void setSendHandshake(boolean sendHandshake)
	{
		this.sendHandshake = sendHandshake;
	}

	public boolean isHandshake()
	{
		return handshake;
	}

	public void setHandshake(boolean handshake)
	{
		this.handshake = handshake;
	}

	public boolean isReadHandshake()
	{
		return readHandshake;
	}

	public void setReadHandshake(boolean readHandshake)
	{
		this.readHandshake = readHandshake;
	}

	public boolean isValid()
	{
		return authKey != null && handshake;
	}

	public String getServerIp()
	{
		return serverIp;
	}

	public void setServerIp(String serverIp)
	{
		this.serverIp = serverIp;
	}

	public int getServerPort()
	{
		return serverPort;
	}

	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}

	public String getClientIp()
	{
		return clientIp;
	}

	public void setClientIp(String clientIp)
	{
		this.clientIp = clientIp;
	}

	public int getClientPort()
	{
		return clientPort;
	}

	public void setClientPort(int clientPort)
	{
		this.clientPort = clientPort;
	}

	public String toString()
	{
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("server", server);
		builder.append("serverIp", serverIp);
		builder.append("serverPort", serverPort);
		builder.append("clientIp", clientIp);
		builder.append("clientPort", clientPort);
		return builder.toString();
	}
}
