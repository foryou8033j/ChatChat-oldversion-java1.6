package main;

import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import Client.Data.MaintenanceData;
import Client.Interface.ClientFrame;
import Client.Interface.SwingFileDownloadHTTP;
import Client.Transmit.ReceiveData;
import Client.Transmit.ServerConnection.UpdateChecker;
import Client.Transmit.ServerConnection.UpkeepConnection;
import Utility.Registry.Regedit;

public class Client
{
	static ClientFrame clientFrame;
	
	public static void main(String[] argc)
	{
		try
		{
			
			if(MaintenanceData.useCurrentOSTheme) UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			if(MaintenanceData.isUserInputServerIP) MaintenanceData.serverIP = JOptionPane.showInputDialog(null, "IP 입력", "Input Server IP", JOptionPane.INFORMATION_MESSAGE);
			
			final SwingFileDownloadHTTP filedownloader = new SwingFileDownloadHTTP(MaintenanceData.serverIP, true);
			
			if (UpdateChecker.isUpdated(MaintenanceData.serverIP, MaintenanceData.updateCheckerLocalFileAddress, MaintenanceData.curVersion))
			{
				//새로운 클라이언트를 받으면서 기존 패치노트 열람 정보 초기화
				Regedit.addRegistry("isOpenPatchNote", "false");
				
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						filedownloader.setVisible(true);
					}
				});
			}
			else
			{
				try
				{
					//레지스트리에 열람 정보가 있는지 확인 후 없으면 초기화
					if(!Regedit.isContains("isOpenPatchNote"))
						Regedit.addRegistry("isOpenPatchNote", "false");
					
					MaintenanceData.socket = new Socket(MaintenanceData.serverIP, MaintenanceData.serverPORT);
					System.out.println("클라이언트 | 채팅서버 연결성공!");
					
					clientFrame = new ClientFrame();
					SwingUtilities.invokeLater(clientFrame);
					
					new ReceiveData(clientFrame).start();
					new UpkeepConnection(clientFrame).start();
					
				} catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, "채팅 서버가 활성화 되어있지 않습니다.", "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					System.exit(0);
				}
			}
		} catch (Exception ie)
		{
			JOptionPane.showMessageDialog(null, "업데이트 서버가 활성화 되어있지 않습니다.", "Error", JOptionPane.ERROR_MESSAGE);
			ie.printStackTrace();
			System.exit(0);
		}
	}
}
