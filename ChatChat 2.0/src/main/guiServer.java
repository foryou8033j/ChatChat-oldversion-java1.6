package main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Client.Data.MaintenanceData;
import Server.FileServer;
import Server.RunningServer;

public class guiServer implements ActionListener
{
	private SystemTray systemTray;
	private PopupMenu mPopup;
	private MenuItem mchatServer, mFileServer, mExit;
	JFrame frame = new JFrame("Server");
	JButton btnStartChatServer = new JButton("채팅 서버 동작");
	ServerSocket chatServer = null;
	boolean isRunningChat = false;
	RunningServer runningServer;

	public guiServer()
	{
		frame.setLayout(new FlowLayout());
		frame.add(btnStartChatServer);
		frame.setVisible(true);
		frame.setBounds(200, 200, 180, 70);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		btnStartChatServer.addActionListener(this);
		frame.setVisible(true);
		try
		{
			initSystemTrayIcon();
		} catch (AWTException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		new guiServer();
	}

	// TODO 시스템 트레이 동작 관련 문제 수정 필요
	public void initSystemTrayIcon() throws AWTException
	{
		if (SystemTray.isSupported())
		{
			mPopup = new PopupMenu();
			mchatServer = new MenuItem("채팅 서버 동작");
			mFileServer = new MenuItem("파일 서버 동장");
			mExit = new MenuItem("종    료");
			mchatServer.addActionListener(this);
			mFileServer.addActionListener(this);
			mExit.addActionListener(this);
			systemTray = SystemTray.getSystemTray();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getSource() == btnStartChatServer)
		{
			if (isRunningChat)
			{
				btnStartChatServer.setText("채팅 서버 동작");
				isRunningChat = false;
				btnStartChatServer.setForeground(Color.BLACK);
				try
				{
					runningServer.interrupt();
					runningServer = null;
				} catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, "서버를 닫는 중 오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
			else
			{
				btnStartChatServer.setText("채팅 서버 동작 중");
				btnStartChatServer.setForeground(Color.RED);
				runningServer = new RunningServer();
				runningServer.start();
				new FileServer(new MaintenanceData().fileServerPORT, runningServer).start();
				isRunningChat = true;
			}
		}
	}
}
