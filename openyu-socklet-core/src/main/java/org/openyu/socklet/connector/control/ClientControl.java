package org.openyu.socklet.connector.control;

import org.openyu.commons.awt.control.BaseControl;
import org.openyu.socklet.connector.frame.ClientFrame;
import org.openyu.socklet.connector.service.ClientService;

/**
 * 客戶端控制器
 */
public interface ClientControl extends BaseControl {

	/**
	 * 客戶端服務
	 * 
	 * @return
	 */
	ClientService getClientService();

	void setClientService(ClientService clientService);

	/**
	 * 客戶端Frame
	 * 
	 * @return
	 */
	ClientFrame getClientFrame();

	void setClientFrame(ClientFrame clientFrame);
}
