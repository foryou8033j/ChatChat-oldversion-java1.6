package Client.Transmit;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import Utility.Security.AES256Cipher;
import Client.Data.MaintenanceData;
import Client.Interface.ClientFrame;

public class TransmitData
{
	ClientFrame clientFrame;
	public BufferedWriter bout;
	
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public TransmitData(ClientFrame clientFrame)
	{
		this.clientFrame = clientFrame;
	}

	public void sendMessage()
	{
		try
		{
			calendar = Calendar.getInstance();
			sendMessage("999|" + clientFrame.hashKey + "|0|" + dateFormat.format(calendar.getTime()) + "|" + clientFrame.id + "|" + MaintenanceData.clientIP.replace("32.1.", "*.*.") + "|" + clientFrame.txtInput.getText());
		} catch (Exception ie)
		{
			System.out.println("클라이언트 | 송신 오류 1 | " + ie.getMessage());
			clientFrame.append("------------------서버로 메세지 전송 중 오류 발생 (ErrorCode 101)---------------------\n\n");
		}
	}

	public void sendMessage(String str)
	{
		try
		{
			
			if(!MaintenanceData.__showHeartBeatData)
			{
				if( str.contains("900") || str.contains("901"));
				else System.out.println("클라이언트 ▶ 서버 | " + str);
			}
			else
				System.out.println("클라이언트 ▶ 서버 | " + str);
			
			bout = new BufferedWriter(new OutputStreamWriter(MaintenanceData.socket.getOutputStream(), "UTF-8"));
			//암호화된 데이터 송신
			String encodedData = AES256Cipher.encoding(str);
			if(MaintenanceData.__showEncodeData) System.out.println("송신된 암호화 데이터 | " + encodedData);
			
			if(!MaintenanceData.socket.isClosed())
				bout.write(encodedData + "\n");
			bout.flush();
		} catch (Exception e)
		{
			try
			{
				bout.flush();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("클라이언트 | 송신 오류 2 | " + e.getMessage());
		}
	}
}
