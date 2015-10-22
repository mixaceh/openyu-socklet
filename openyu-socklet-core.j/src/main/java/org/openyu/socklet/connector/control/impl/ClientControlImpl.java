package org.openyu.socklet.connector.control.impl;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Component;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.openyu.commons.awt.control.supporter.BaseControlSupporter;
import org.openyu.commons.lang.StringHelper;
import org.openyu.socklet.connector.control.ClientControl;
import org.openyu.socklet.connector.frame.ClientFrame;
import org.openyu.socklet.connector.frame.impl.ClientFrameImpl;
import org.openyu.socklet.connector.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客戶端控制器
 */
public class ClientControlImpl extends BaseControlSupporter implements ClientControl {

	private static final long serialVersionUID = -5562471882897009907L;

	private static final transient Logger LOGGER = LoggerFactory.getLogger(ClientControlImpl.class);

	/**
	 * 客戶端服務
	 */
	private ClientService clientService;

	/**
	 * 客戶端Frame
	 */
	private ClientFrame clientFrame;

	public ClientControlImpl() {
	}

	public ClientService getClientService() {
		return clientService;
	}

	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	public ClientFrame getClientFrame() {
		return clientFrame;
	}

	public void setClientFrame(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
	}

	/**
	 * 顯示
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				clientFrame = new ClientFrameImpl(id);
				// commandTextField
				clientFrame.getCommandTextField().addKeyListener(new CommandTextFieldKeyAdapter(clientFrame));
				// clearButton
				clientFrame.getClearButton().addActionListener(new ClearButtonActionAdapter(clientFrame));
				// reconnectButton
				clientFrame.getReconnectButton()
						.addActionListener(new ReconnectButtonActionAdapter(clientFrame, clientService));
				//
				ClientControlImpl.this.setComponent((Component) clientFrame);
				//
				clientFrame.setVisible(true);// 顯示frame
			}
		});
		//
		clientService.start();
		boolean started = clientService.isStarted();
		if (started) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					clientFrame.getMessageTextArea().append("[" + clientService.getId() + "] Connected to " + "["
							+ clientService.getIp() + ":" + clientService.getPort() + "]" + StringHelper.LF);
					clientFrame.getMessageTextArea()
							.setCaretPosition(clientFrame.getMessageTextArea().getText().length());
				}
			});
		} else {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					clientFrame.getMessageTextArea().append("Can't connect to [" + clientService.getIp() + ":"
							+ clientService.getPort() + "]" + StringHelper.LF);
					clientFrame.getMessageTextArea()
							.setCaretPosition(clientFrame.getMessageTextArea().getText().length());
				}
			});
		}

	}

	/**
	 * 命令區,輸入處理
	 */
	protected static class CommandTextFieldKeyAdapter extends KeyAdapter {

		private ClientFrame clientFrame;
		/**
		 * 命令區
		 */
		private JTextField commandTextField;

		/**
		 * 訊息區
		 */
		private JTextArea messageTextArea;

		public CommandTextFieldKeyAdapter(ClientFrame clientFrame) {
			this.clientFrame = clientFrame;
			//
			this.commandTextField = clientFrame.getCommandTextField();
			this.messageTextArea = clientFrame.getMessageTextArea();
		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				//
				StringBuilder buff = new StringBuilder();
				buff.append(commandTextField.getText());
				messageTextArea.append("client > " + buff.toString() + StringHelper.LF);
				messageTextArea.setCaretPosition(clientFrame.getMessageTextArea().getText().length());
			}
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				clientFrame.getCommandTextField().setText("");
			}
		}
	}

	/**
	 * 清除按鈕, 按下處理
	 */
	protected static class ClearButtonActionAdapter implements ActionListener {

		private ClientFrame clientFrame;

		/**
		 * 訊息區
		 */
		private JTextArea messageTextArea;

		public ClearButtonActionAdapter(ClientFrame clientFrame) {
			this.clientFrame = clientFrame;
			//
			this.messageTextArea = clientFrame.getMessageTextArea();
		}

		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if ("clear".equals(action)) {
				messageTextArea.setText("");
			}

		}
	}

	/**
	 * 重連按鈕, 按下處理
	 */
	protected static class ReconnectButtonActionAdapter implements ActionListener {

		private ClientFrame clientFrame;

		private ClientService clientService;

		/**
		 * 訊息區
		 */
		private JTextArea messageTextArea;

		public ReconnectButtonActionAdapter(ClientFrame clientFrame, ClientService clientService) {
			this.clientFrame = clientFrame;
			this.clientService = clientService;
			//
			this.messageTextArea = clientFrame.getMessageTextArea();
		}

		public void actionPerformed(ActionEvent e) {
			// String action = e.getActionCommand();
			// if ("reconnect".equals(action)) {
			// messageTextArea
			// .append("Reconnect to [" + clientService.getIp() + ":"
			// + clientService.getPort() + "]"
			// + StringHelper.LF);
			// messageTextArea.setCaretPosition(clientFrame
			// .getMessageTextArea().getText().length());
			// //
			// clientService.shutdown();
			// // #issue 若原是連線,又斷線重連,會發生無法連線
			// clientService.start();
			// boolean started = clientService.isStarted();
			// //
			// if (started) {
			// clientFrame.getMessageTextArea().append(
			// "[" + clientService.getId() + "] Connected to "
			// + "[" + clientService.getIp() + ":"
			// + clientService.getPort() + "]"
			// + StringHelper.LF);
			// clientFrame.getMessageTextArea()
			// .setCaretPosition(
			// clientFrame.getMessageTextArea().getText()
			// .length());
			// } else {
			// clientFrame.getMessageTextArea().append(
			// "Can't connect to [" + clientService.getIp() + ":"
			// + clientService.getPort() + "]"
			// + StringHelper.LF);
			// clientFrame.getMessageTextArea()
			// .setCaretPosition(
			// clientFrame.getMessageTextArea().getText()
			// .length());
			// }
			// }
		}
	}
}
