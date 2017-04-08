package Client.Transmit.ServerConnection;

import java.awt.Color;
import java.awt.Font;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import main.Client;
import Client.Data.MaintenanceData;
import Client.Interface.ClientFrame;
import Client.Transmit.ReceiveData;

public class UpkeepConnection extends Thread
{
	ClientFrame clientFrame;

	public UpkeepConnection(ClientFrame clientFrame)
	{
		this.clientFrame = clientFrame;
	}

	boolean isRed = false;
	boolean isServerDown = false;
	boolean isSendMessage = false;

	@Override
	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
		{
			if (MaintenanceData.socket.isClosed() && clientFrame.isRunning)
			{
				labelServerStatusChanged(false);
				try
				{
					System.out.println("클라이언트 | 서버로 재 연결을 시도 하는 중 " + MaintenanceData.serverIP + " | " + MaintenanceData.socket.isClosed() + " | " + MaintenanceData.socket.isConnected() + " | " + MaintenanceData.socket.isInputShutdown() + " | " + MaintenanceData.socket.isOutputShutdown());
					
					//MaintenanceData.socket = new Socket();
					MaintenanceData.socket = new Socket(MaintenanceData.serverIP, MaintenanceData.serverPORT);
					//MaintenanceData.socket.connect(new InetSocketAddress(MaintenanceData.serverIP, MaintenanceData.serverPORT));
					//MaintenanceData.socket.setReuseAddress(true);
					
					//추후 서버 안정성을 위해 딜레이 추가
					//try{Thread.sleep(MaintenanceData.clinetReconnectToServerSequence);}catch(Exception e) {}

					isServerDown = true;
				} catch (Exception e)
				{
				}
			}
			else
			{
				if (isRed)
				{
					labelServerStatusChanged(true);
					System.out.println("클라이언트 | 서버 재 연결 성공 " + MaintenanceData.serverIP + " | " + MaintenanceData.socket.isClosed() + " | " + MaintenanceData.socket.isConnected() + " | " + MaintenanceData.socket.isInputShutdown() + " | " + MaintenanceData.socket.isOutputShutdown());
					Calendar calendar = Calendar.getInstance();
					SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
					String str = "182|" + clientFrame.hashKey + "|0|" + dateFormat.format(calendar.getTime()) + "|" + clientFrame.id + "|" + MaintenanceData.socket.getLocalAddress().getHostAddress().replace("32.1.", "*.*.") + "|join";
					clientFrame.transmitThread.sendMessage(str);
				}
				isServerDown = false;
			}
			//try{Thread.sleep(1000);}catch(Exception e) {}
		}
		
		//System.out.println("이 메세지가 보이면 클라이언트는 비정상적으로 종료된 것임.");
		//System.exit(0);
		
	}

	public void labelServerStatusChanged(boolean set)
	{
		
		if (set)
		{
			clientFrame.labelStatusServer.setForeground(Color.BLACK);
			clientFrame.labelStatusServer.setText("연결됨");
			clientFrame.labelStatusServer.setFont(new Font("", Font.PLAIN, 12));
			clientFrame.labelStatusServer.setToolTipText("서버 연결 상태 양호");
			isRed = false;
		}
		else
		{
			clientFrame.labelStatusServer.setForeground(Color.RED);
			clientFrame.labelStatusServer.setText("연결끊김");
			clientFrame.labelStatusServer.setFont(new Font("", Font.BOLD, 12));
			clientFrame.labelStatusServer.setToolTipText("<HTML><STRONG>서버 연결 상태 불안정</STRONG><BR>재 접속 시도 중 입니다.");
			isRed = true;
		}
		
	}

	
}
