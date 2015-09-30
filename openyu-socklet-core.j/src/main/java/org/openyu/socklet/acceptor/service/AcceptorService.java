package org.openyu.socklet.acceptor.service;

import java.util.List;
import java.util.Map;

import org.openyu.commons.service.BaseService;
import org.openyu.socklet.connector.vo.AcceptorConnector;

/**
 * 接收器服務
 */
public interface AcceptorService extends BaseService {

	/**
	 * master,slave1...n
	 * 
	 * @return
	 */
	String getId();

	void setId(String id);

	/**
	 * 內部server,供內部server之間的連線
	 * 
	 * localhost:3000
	 * 
	 * @return
	 */
	List<String> getRelationServers();

	void setRelationServers(List<String> relationServers);

	/**
	 * 外部server,供client之間的連線
	 * 
	 * localhost:3100
	 * 
	 * @return
	 */
	List<String> getClientServers();

	void setClientServers(List<String> clientServers);

	/**
	 * client最大連線數
	 * 
	 * ServerService.maxClient=AcceptorServer.maxClient/relationServers.size
	 * 
	 * ServerService.maxClient=AcceptorServer.maxClient/clientServers.size
	 * 
	 * @return
	 */
	int getMaxClient();

	void setMaxClient(int maxClient);

	// ------------------------------------------------

	/**
	 * cluster
	 * 
	 * @return
	 */
	String getCluster();

	void setCluster(String cluster);

	/**
	 * 連線到其他server, 以acceptor id判斷
	 * 
	 * @return
	 */
	List<String> getRelations();

	void setRelations(List<String> relations);

	// ------------------------------------------------

	/**
	 * 初始化參數,contextService
	 * 
	 * @return
	 */
	Map<String, String> getInitParameters();

	void setInitParameters(Map<String, String> initParameters);

	/**
	 * 取得所有serverService.counter加總
	 * 
	 * @return
	 */
	int getCounter();

	// ------------------------------------------------

	/**
	 * 啟動
	 */
	void start();

	/**
	 * 關閉
	 */
	void shutdown();

//	/**
//	 * 是否已啟動
//	 * 
//	 * @return
//	 */
//	boolean isStarted();
//
//	void setStarted(boolean started);

	/**
	 * 取得sender的connector
	 * 
	 * @param sender
	 * @return
	 */
	AcceptorConnector getAcceptorConnector(String sender);

	/**
	 * 實例id
	 * 
	 * id=slave1 -> instanceId=01
	 * 
	 * id=slave1_02 -> instanceId=02
	 * 
	 * @return
	 */
	String getInstanceId();

	void setInstanceId(String instanceId);

	/**
	 * 輸出id
	 * 
	 * @return
	 */
	String getOutputId();

	void setOutputId(String outputId);

	/**
	 * 重試次數, 0=無限
	 *
	 * @return
	 */
	int getRetryNumber();

	void setRetryNumber(int retryNumber);

	/**
	 * 重試暫停毫秒
	 *
	 * @return
	 */
	long getRetryPauseMills();

	void setRetryPauseMills(long retryPauseMills);

	/**
	 * relation重試次數, 0=無限
	 *
	 * @return
	 */
	int getRelationRetryNumber();

	void setRelationRetryNumber(int relationRetryNumber);

	/**
	 * relation重試暫停毫秒
	 *
	 * @return
	 */
	long getRelationRetryPauseMills();

	void setRelationRetryPauseMills(long relationRetryPauseMills);

}
