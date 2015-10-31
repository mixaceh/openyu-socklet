package org.openyu.socklet.connector.service.impl;

import java.awt.EventQueue;

import javax.swing.JTextArea;

import org.openyu.commons.lang.StringHelper;
import org.openyu.socklet.connector.service.supporter.ClientServiceSupporter;
import org.openyu.socklet.message.vo.Message;
import org.openyu.socklet.connector.control.ClientControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 客戶端服務
 */
public class ClientServiceImpl extends ClientServiceSupporter {

	private static final long serialVersionUID = 7304778591781788192L;

	private static final transient Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);
	/**
	 * 客戶端控制器
	 */
	@Autowired
	@Qualifier("clientControl")
	private ClientControl clientControl;

	public ClientServiceImpl() {

	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		//
		clientControl.setId(id);
		clientControl.setClientService(this);
	}

	public void service(final Message message) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JTextArea messageTextArea = clientControl.getClientFrame().getMessageTextArea();
				messageTextArea.append("server> " + message + StringHelper.LF);
				messageTextArea.setCaretPosition(messageTextArea.getText().length());
			}
		});
		//
		LOGGER.info(String.valueOf(message));
	}
}
