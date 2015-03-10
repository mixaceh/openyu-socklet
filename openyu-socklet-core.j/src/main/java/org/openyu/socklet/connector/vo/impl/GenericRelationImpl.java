package org.openyu.socklet.connector.vo.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openyu.commons.bean.supporter.BaseBeanSupporter;
import org.openyu.socklet.connector.vo.GenericConnector;
import org.openyu.socklet.connector.vo.GenericReceiver;
import org.openyu.socklet.connector.vo.GenericRelation;
import org.openyu.socklet.message.vo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericRelationImpl extends BaseBeanSupporter implements
		GenericRelation {

	private static final long serialVersionUID = 4579039593477933835L;

	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(GenericRelationImpl.class);

	private String id;

	private Map<String, GenericConnector> clients = new ConcurrentHashMap<String, GenericConnector>();

	/**
	 * 讀取者
	 */
	private GenericReceiver receiver;

	/**
	 * 隨機取出Client的亂數
	 */
	private AtomicInteger random = new AtomicInteger(0);

	/**
	 * 是否已連線
	 */
	private boolean connected;

	public GenericRelationImpl(String id) {
		this.id = id;
	}

	/**
	 * id
	 * 
	 * slave1 連 master
	 * 
	 * id=slave1
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 泛化客戶端
	 * 
	 * sender=slave2:0:localhost:3000, GenericClient
	 * 
	 * @return
	 */
	public Map<String, GenericConnector> getClients() {
		return clients;
	}

	public void setClients(Map<String, GenericConnector> clients) {
		this.clients = clients;
	}

	/**
	 * 取得訊息讀取者
	 * 
	 * @return
	 */
	public GenericReceiver getReceiver() {
		return receiver;
	}

	public void setReceiver(GenericReceiver receiver) {
		this.receiver = receiver;
	}

	/**
	 * 發送訊息
	 * 
	 * @param message
	 * @return
	 */
	public int send(Message message) {
		int result = 0;
		GenericConnector genericClient = getNextClient();
		if (genericClient != null) {
			result = genericClient.send(message);
		} else {
			LOGGER.warn("no connection to send");
		}
		return result;
	}

	/**
	 * 取得下一個隨機連線
	 * 
	 * @return
	 */
	public GenericConnector getNextClient() {
		GenericConnector result = null;
		int size = clients.size();
		int index = random.getAndIncrement() % clients.size();
		int i = 0;
		for (GenericConnector genericClient : clients.values()) {
			if (i == index) {
				result = genericClient;
				break;
			}
			i++;
		}
		// 未合法連線時,繼續找合法連線的
		if (!result.isValid() && index < size - 1) {
			result = getNextClient();
		}
		return result;
	}

	/**
	 * 是否已連線
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean equals(Object object) {
		if (!(object instanceof GenericRelationImpl)) {
			return false;
		}
		if (this == object) {
			return true;
		}
		GenericRelationImpl other = (GenericRelationImpl) object;
		return new EqualsBuilder().append(id, other.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

}
