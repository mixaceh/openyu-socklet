package org.openyu.socklet.connector.frame.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;

import org.openyu.commons.awt.frame.supporter.BaseFrameSupporter;
import org.openyu.socklet.connector.frame.ClientFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客戶端Frame
 */
public class ClientFrameImpl extends BaseFrameSupporter implements ClientFrame {

	private static final long serialVersionUID = 6472436171710553770L;

	/** The Constant LOGGER. */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(ClientFrameImpl.class);
	/**
	 * 訊息區
	 */
	private JTextArea messageTextArea;

	/**
	 * 命令區
	 */
	private JTextField commandTextField;

	/**
	 * 清除按鈕
	 */
	private JButton clearButton;

	/**
	 * 重連按鈕
	 */
	private JButton reconnectButton;

	/**
	 * 
	 * @param title
	 */
	public ClientFrameImpl(String title) {
		super(title);
	}

	public ClientFrameImpl() {
		this(null);
	}

	public JTextArea getMessageTextArea() {
		return messageTextArea;
	}

	public void setMessageTextArea(JTextArea messageTextArea) {
		this.messageTextArea = messageTextArea;
	}

	public JTextField getCommandTextField() {
		return commandTextField;
	}

	public void setCommandTextField(JTextField commandTextField) {
		this.commandTextField = commandTextField;
	}

	public JButton getClearButton() {
		return clearButton;
	}

	public void setClearButton(JButton clearButton) {
		this.clearButton = clearButton;
	}

	public JButton getReconnectButton() {
		return reconnectButton;
	}

	public void setReconnectButton(JButton reconnectButton) {
		this.reconnectButton = reconnectButton;
	}

	/**
	 * 初始化
	 *
	 * @throws Exception
	 */
	protected void init() {
		super.init();
		//
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(600, 400));

		// messageTextArea
		// messageTextArea = new JTextArea(5, 69); 800,600
		messageTextArea = new JTextArea(5, 51);
		messageTextArea.setLineWrap(true);
		messageTextArea.setWrapStyleWord(true);
		messageTextArea.setEditable(false);
		//
		JScrollPane messageScroll = new JScrollPane();
		//messageScroll.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		messageScroll.getViewport().add(messageTextArea);

		// pageEndLayout
		GridLayout pageEndLayout = new GridLayout(0, 2);
		JPanel pageEndPanel = new JPanel();
		pageEndPanel.setLayout(pageEndLayout);
		//pageEndPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		// commandTextField
		commandTextField = new JTextField(51);// 51
		pageEndPanel.add(commandTextField);

		//button panel
		GridLayout buttonLayout = new GridLayout(0, 2);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(buttonLayout);
		//buttonPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		// clearButton
		clearButton = new JButton("Clear");
		clearButton.setActionCommand("clear");
		buttonPanel.add(clearButton);

		// reconnectButton
		reconnectButton = new JButton("Reconnect");
		reconnectButton.setActionCommand("reconnect");
		buttonPanel.add(reconnectButton);
		//
		pageEndPanel.add(buttonPanel);
		//
		getContentPane().add(messageScroll, BorderLayout.CENTER);
		getContentPane().add(pageEndPanel, BorderLayout.PAGE_END);
	}
}
