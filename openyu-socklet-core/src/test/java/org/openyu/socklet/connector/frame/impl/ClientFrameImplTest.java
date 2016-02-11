package org.openyu.socklet.connector.frame.impl;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.junit.Test;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.lang.StringHelper;
import org.openyu.commons.thread.ThreadHelper;
import org.openyu.socklet.connector.frame.ClientFrame;

public class ClientFrameImplTest extends BaseTestSupporter {

	@Test
	// no edt
	// 1 times: 877 mills.
	//
	// edt
	// 1 times: 172 mills.
	public void setVisible() {
		final int count = 1;
		final long beg = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					ClientFrame frame = new ClientFrameImpl("ClientFrame");
					// commandTextField
					frame.getCommandTextField().addKeyListener(
							new CommandTextFieldKeyAdapter(frame));
					// clearButton
					frame.getClearButton().addActionListener(
							new ClearButtonActionAdapter(frame));
					// reconnectButton
					frame.getReconnectButton().addActionListener(
							new ReconnectButtonActionAdapter(frame));
					//
					frame.setVisible(true);
					//
					long end = System.currentTimeMillis();
					System.out.println(count + " times: " + (end - beg)
							+ " mills. ");
				}
			});
		}
		//
		ThreadHelper.sleep(30 * 1000);
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
				StringBuilder text = new StringBuilder();
				text.append(commandTextField.getText());
				//
				messageTextArea.append(text + StringHelper.LF);
				messageTextArea.setCaretPosition(messageTextArea.getText()
						.length());
			}
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				commandTextField.setText("");
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
	protected static class ReconnectButtonActionAdapter implements
			ActionListener {

		private ClientFrame clientFrame;

		/**
		 * 訊息區
		 */
		private JTextArea messageTextArea;

		public ReconnectButtonActionAdapter(ClientFrame clientFrame) {
			this.clientFrame = clientFrame;
			//
			this.messageTextArea = clientFrame.getMessageTextArea();
		}

		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if ("reconnect".equals(action)) {
				messageTextArea.append("Reconnect..." + StringHelper.LF);
				messageTextArea.setCaretPosition(messageTextArea.getText()
						.length());
			}
		}
	}
}
