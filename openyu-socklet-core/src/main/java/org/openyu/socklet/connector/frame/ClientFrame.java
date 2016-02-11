package org.openyu.socklet.connector.frame;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.openyu.commons.awt.frame.BaseFrame;

/**
 * 客戶端Frame
 */
public interface ClientFrame extends BaseFrame {
	/**
	 * 訊息區
	 * 
	 * @return
	 */
	JTextArea getMessageTextArea();

	void setMessageTextArea(JTextArea messageTextArea);

	/**
	 * 命令區
	 * 
	 * @return
	 */
	JTextField getCommandTextField();

	void setCommandTextField(JTextField commandTextField);

	/**
	 * 清除按鈕
	 * 
	 * @return
	 */
	JButton getClearButton();

	void setClearButton(JButton clearButton);

	/**
	 * 重連按鈕
	 * 
	 * @return
	 */
	JButton getReconnectButton();

	void setReconnectButton(JButton reconnectButton);
}
