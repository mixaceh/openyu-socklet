package org.openyu.socklet.connector.vo.supporter;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.openyu.commons.lang.ArrayHelper;
import org.openyu.commons.model.supporter.BaseModelSupporter;
import org.openyu.commons.nio.NioHelper;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.commons.thread.supporter.BaseRunnableSupporter;
import org.openyu.socklet.connector.vo.ConnectorType;
import org.openyu.socklet.connector.vo.GenericConnector;
import org.openyu.socklet.connector.vo.GenericReceiver;
import org.openyu.socklet.message.service.ProtocolService;
import org.openyu.socklet.message.vo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 泛化客戶端
 * 
 * GenericConnector
 * 
 * +-AcceptorConnector (client->slave1)
 * 
 * -------------------------------------------
 * 
 * +-SocketConnector socket連線客戶端
 * 
 * +-FlashConnector (client->slave1),模擬flash的policy連線用客戶端
 * 
 * +-JavaConnector (client->slave1),java連線用客戶端
 * 
 * +-RelationConnector (slave2->slave1),server間連線用的客戶端
 * 
 */
public abstract class GenericConnectorSupporter extends BaseModelSupporter implements GenericConnector {

	private static final long serialVersionUID = -394102890225520895L;

	private static transient final Logger LOGGER = LoggerFactory.getLogger(GenericConnectorSupporter.class);
	/**
	 * id
	 * 
	 * sun.nio.ch.SelectionKeyImpl@19e09a4
	 */
	protected String id;

	/**
	 * 模組類別
	 */
	@SuppressWarnings("rawtypes")
	protected Class moduleTypeClass;

	/**
	 * 訊息類別
	 */
	@SuppressWarnings("rawtypes")
	protected Class messageTypeClass;

	/**
	 * 重試次數, 0=無限
	 */
	protected int retryNumber = NioHelper.DEFAULT_RETRY_NUMBER;

	/**
	 * 重試暫停毫秒
	 */
	protected long retryPauseMills = NioHelper.DEFAULT_RETRY_PAUSE_MILLS;

	/**
	 * 接收緩衝區大小
	 */
	protected int receiveBufferSize = NioHelper.DEFAULT_RECEIVE_BUFFER_SIZE;

	/**
	 * 發送緩衝區大小
	 */
	protected int sendBufferSize = NioHelper.DEFAULT_SEND_BUFFER_SIZE;
	/**
	 * 協定服務
	 */
	protected transient ProtocolService protocolService;

	/**
	 * 讀取buffer
	 */
	protected transient ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(receiveBufferSize);

	protected transient Lock receiveLock = new ReentrantLock();

	/**
	 * 寫入buffer
	 */
	protected transient ByteBuffer sendBuffer = ByteBuffer.allocateDirect(sendBufferSize);

	protected transient Lock sendLock = new ReentrantLock();

	/**
	 * keep alive buff
	 */
	protected transient ByteBuffer keepAliveBuffer = ByteBuffer.allocateDirect(1);

	protected transient Lock keepAliveLock = new ReentrantLock();

	/**
	 * 是否啟動
	 */
	protected boolean started;

	/**
	 * 讀取者
	 */
	protected transient GenericReceiver receiver;

	protected transient Selector selector;

	protected transient SocketChannel socketChannel;

	protected String acceptor;

	protected String sender;

	protected transient Lock lock = new ReentrantLock();

	protected static final long KEEP_ALIVE_MILLS = 3 * 1000L;

	protected ConnectorType connectorType;

	/**
	 * 已重試次數
	 */
	protected int tries;

	protected KeepAliveRunner keepAliveRunner;

	public GenericConnectorSupporter(String id, Class<?> moduleTypeClass, Class<?> messageTypeClass,
			ProtocolService protocolService) {
		this.id = id;
		this.moduleTypeClass = moduleTypeClass;
		this.messageTypeClass = messageTypeClass;
		this.protocolService = protocolService;
	}

	public GenericConnectorSupporter(String id) {
		this(id, null, null, null);
	}

	public GenericConnectorSupporter() {
		this(null, null, null, null);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	/**
	 * 取得讀取者,message
	 * 
	 * @return
	 */
	public GenericReceiver getReceiver() {
		return receiver;
	}

	/**
	 * 設定讀取者
	 * 
	 * @param receiver
	 */
	public void setReceiver(GenericReceiver receiver) {
		this.receiver = receiver;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public String getAcceptor() {
		return acceptor;
	}

	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public ConnectorType getConnectorType() {
		return connectorType;
	}

	public void setConnectorType(ConnectorType connectorType) {
		this.connectorType = connectorType;
	}

	public boolean isValid() {
		return started;
	}

	@SuppressWarnings("rawtypes")
	public Class getModuleTypeClass() {
		return moduleTypeClass;
	}

	@SuppressWarnings("rawtypes")
	public void setModuleTypeClass(Class moduleTypeClass) {
		this.moduleTypeClass = moduleTypeClass;
	}

	@SuppressWarnings("rawtypes")
	public Class getMessageTypeClass() {
		return messageTypeClass;
	}

	@SuppressWarnings("rawtypes")
	public void setMessageTypeClass(Class messageTypeClass) {
		this.messageTypeClass = messageTypeClass;
	}

	public int getRetryNumber() {
		return retryNumber;
	}

	public void setRetryNumber(int retryNumber) {
		this.retryNumber = retryNumber;
	}

	public long getRetryPauseMills() {
		return retryPauseMills;
	}

	public void setRetryPauseMills(long retryPauseMills) {
		this.retryPauseMills = retryPauseMills;
	}

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
		this.receiveBuffer = ByteBuffer.allocateDirect(receiveBufferSize);
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
		this.sendBuffer = ByteBuffer.allocateDirect(sendBufferSize);
	}

	public int getTries() {
		return tries;
	}

	public void setTries(int tries) {
		this.tries = tries;
	}

	public int addTries() {
		return tries++;
	}

	public void shutdown() {
		if (!started) {
			return;
		}
		//
		try {
			lock.lockInterruptibly();
			try {
				tries = 0;
				receiveBuffer = ByteBuffer.allocateDirect(receiveBufferSize);
				sendBuffer = ByteBuffer.allocateDirect(sendBufferSize);
				keepAliveBuffer = ByteBuffer.allocateDirect(1);
				//
				NioHelper.close(selector);
				NioHelper.close(socketChannel);
				//
				keepAliveRunner.shutdown();
				//
				started = false;
			} catch (Exception ex) {
				// ex.printStackTrace();
				started = false;
			} finally {
				lock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//
		if (!started) {
			LOGGER.info("[" + this.sender + "] Disconnected to [" + acceptor + "]");
		}
	}

	/**
	 * 接收,byte[]
	 * 
	 * 因操作receiveBuffer,可能會有多緒,故加鎖
	 * 
	 * #fix 改為local receiveBuffer, 不用加鎖了, 2014/11/02
	 */
	public byte[] receive() {
		byte[] result = null;
		if (!started) {
			return result;
		}
		//
		try {
			// #issue: java.lang.OutOfMemoryError: Direct buffer memory
			// http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6857566
			// ByteBuffer receiveBuffer = ByteBuffer
			// .allocateDirect(NioHelper.DEFAULT_RECEIVE_BUFFER_SIZE);

			receiveLock.lockInterruptibly();
			try {
				// #fix: 改為共用receiveBuffer
				result = NioHelper.read(socketChannel, receiveBuffer);
				// ThreadHelper.sleep(50);
			} catch (Exception ex) {
				// ex.printStackTrace();
			} finally {
				receiveLock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 發送,byte[]
	 * 
	 * 因操作sendBuffer,可能會有多緒,故加鎖
	 */
	public int send(byte[]... values) {
		int result = 0;
		if (!started) {
			return result;
		}
		//
		try {
			// #issue: java.lang.OutOfMemoryError: Direct buffer memory
			// http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6857566
			// ByteBuffer sendBuffer = ByteBuffer
			// .allocateDirect(NioHelper.DEFAULT_SEND_BUFFER_SIZE);

			sendLock.lockInterruptibly();
			try {
				if (connectorType == ConnectorType.FLASH_SOCKLET) {
					// 加入flash結尾符 "\0"
					byte[][] buff = ArrayHelper.add(FLASH_EOF_BYTES, values);
					// #fix: 改為共用sendBuffer
					result = NioHelper.write(socketChannel, sendBuffer, buff);
				} else {
					result = NioHelper.write(socketChannel, sendBuffer, values);
				}
				// ThreadHelper.sleep(50);
			} catch (Exception ex) {
				// ex.printStackTrace();
			} finally {
				sendLock.unlock();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 發送,message
	 * 
	 * @param message
	 * @return
	 */
	public int send(Message message) {
		int result = 0;
		if (!started) {
			return result;
		}
		//
		byte[] buff = protocolService.assemble(message);
		if (buff != null) {
			result = send(buff);
		}
		return result;
	}

	protected class KeepAliveRunner extends BaseRunnableSupporter {

		public KeepAliveRunner() {
		}

		@Override
		protected void doRun() throws Exception {
			while (true) {
				try {
					if (isShutdown()) {
						break;
					}
					keepAlive();
					ThreadHelper.sleep(KEEP_ALIVE_MILLS);
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 監測是否連線
	 */
	protected void keepAlive() {
		try {
			// check by read
			// #issue: java.lang.OutOfMemoryError: Direct buffer memory
			// http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6857566
			// ByteBuffer keepAliveBuffer = ByteBuffer.allocateDirect(1);

			keepAliveLock.lockInterruptibly();
			try {
				// #fix: 改為共用keepAliveBuffer
				socketChannel.read(keepAliveBuffer);
				keepAliveBuffer.flip();
				keepAliveBuffer.clear();
			} catch (Exception ex) {
				// ex.printStackTrace();
				shutdown();
			} finally {
				keepAliveLock.unlock();
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
	}

	public boolean equals(Object object) {
		if (!(object instanceof GenericConnectorSupporter)) {
			return false;
		}
		if (this == object) {
			return true;
		}
		GenericConnectorSupporter other = (GenericConnectorSupporter) object;
		return new EqualsBuilder().append(id, other.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", id);
		builder.append("acceptor", acceptor);
		builder.append("sender", sender);
		builder.append("started", started);
		builder.append("retryNumber", retryNumber);
		builder.append("retryPauseMills", retryPauseMills);
		builder.append("receiveBufferSize", receiveBufferSize);
		builder.append("sendBufferSize", sendBufferSize);
		builder.append("tries", tries);
		return builder.toString();
	}

}
