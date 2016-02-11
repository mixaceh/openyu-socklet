package org.openyu.socklet.acceptor.service;


import org.openyu.commons.service.BaseService;
import org.openyu.socklet.connector.vo.GenericConnector;

/**
 * 伺服器服務
 */
public interface ServerService extends BaseService {

	/**
	 * localhost:3100
	 * 
	 * @return
	 */
	String getId();

	void setId(String id);

	/**
	 * ip
	 * 
	 * @return
	 */
	String getIp();

	void setIp(String ip);

	/**
	 * port
	 * 
	 * @return
	 */
	int getPort();

	void setPort(int port);

	// ------------------------------------------------

	/**
	 * client最大連線數
	 * 
	 * @return
	 */
	int getMaxClient();

	void setMaxClient(int maxClient);

	/**
	 * client連線數
	 * 
	 * @return
	 */
	int getCounter();

	// ------------------------------------------------

	/**
	 * 關閉genericConnector
	 * 
	 * @param genericConnector
	 */
	boolean close(GenericConnector genericConnector);

}
