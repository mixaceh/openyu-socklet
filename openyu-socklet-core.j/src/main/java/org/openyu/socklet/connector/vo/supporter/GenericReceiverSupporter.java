package org.openyu.socklet.connector.vo.supporter;

import org.openyu.commons.model.supporter.BaseModelSupporter;
import org.openyu.socklet.connector.vo.GenericReceiver;

/**
 * 泛化接收者,client提供給外部接message用
 */
public abstract class GenericReceiverSupporter extends BaseModelSupporter implements
		GenericReceiver
{

	private static final long serialVersionUID = -5937113838020215753L;

	public GenericReceiverSupporter()
	{}
}
