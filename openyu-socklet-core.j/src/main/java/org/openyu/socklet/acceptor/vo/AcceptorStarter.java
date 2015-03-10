package org.openyu.socklet.acceptor.vo;

import org.openyu.commons.model.BaseModel;
import org.openyu.socklet.acceptor.service.AcceptorService;

/**
 * 接收啟動器,與連結設定
 */
public interface AcceptorStarter extends BaseModel
{

	/**
	 * 本地想要啟動的acceptor id
	 * 
	 * @return
	 */
	String getId();

	void setId(String id);

	/**
	 * 接受器服務
	 * 
	 * @return
	 */
	AcceptorService getAcceptorService();

	void setAcceptorService(AcceptorService acceptorService);

}