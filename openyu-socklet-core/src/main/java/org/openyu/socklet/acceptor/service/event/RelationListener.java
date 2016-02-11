package org.openyu.socklet.acceptor.service.event;

import java.util.Set;
import org.openyu.commons.lang.event.BaseListener;

/**
 * 關連監聽器
 */
public interface RelationListener extends BaseListener
{

	/**
	 * 關聯主動/被動連線時用
	 * 
	 * @param relationEvent
	 */
	void relationConnected(RelationEvent relationEvent);

	/**
	 * 關聯主動/被動斷線時用
	 * 
	 * @param relationEvent
	 */
	void relationDisconnected(RelationEvent relationEvent);
	

	/**
	 * 關聯只有主動連線失敗時用
	 * 
	 * @param relationEvent
	 */
	void relationRefused(RelationEvent relationEvent);

	/**
	 * 想要註冊的acceptor id, ex:master,slave1
	 * 
	 * @return
	 */
	Set<String> getAcceptors();

	void setAcceptors(Set<String> acceptors);
}
