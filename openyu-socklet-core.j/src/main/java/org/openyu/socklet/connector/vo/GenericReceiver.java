package org.openyu.socklet.connector.vo;

import org.openyu.commons.model.BaseModel;
import org.openyu.socklet.message.vo.Message;

/**
 * 泛化接收者,client提供給外部接message用
 */
public interface GenericReceiver extends BaseModel
{

	/**
	 * 接收訊息
	 * @param message
	 */
	void receive(Message message);
}
