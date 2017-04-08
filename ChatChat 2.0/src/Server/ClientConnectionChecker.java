package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.SwingUtilities;

import Client.Data.MaintenanceData;

/**
 * 서버와 커넥션이 끊어진 클라이언트에 대해 연결을 중단한다.
 * 현재 검증중인 클래스.
 * @author navy
 *
 */
public class ClientConnectionChecker extends Thread
{
	RunningServer server;
	TransportServerData user;
	
	Vector<String> tempMember  =  new Vector<String>();
	
	boolean sequenceReceiveData = false;

	public ClientConnectionChecker(RunningServer server, TransportServerData user)
	{
		this.server = server;
		this.user = user;
	}
	
	@Override
	public void run()
	{
		
		while(!Thread.currentThread().isInterrupted())
		{
			if(true)
			{
				sequenceReceiveData = true;
				tempMember.removeAllElements();
				
				for(String str:server.vec_Name)
				{
					if (str == null) break;
					else
					{
						try
						{
							tempMember.add(str);
							System.out.println("== " + tempMember.toString());
							user.sendMessage("900|" + str);
						} catch (IOException e)
						{
							
						}
					}
				}
				
				
				try{Thread.sleep(MaintenanceData.serverHeartBeatSequence);}catch(Exception e) {}
				
				sequenceReceiveData = false;
				
			
				for(String mem:tempMember)
				{
					if(mem == null) break;
					else
					{
						if(server.vec_Name.contains(mem)) server.vec_Name.remove(mem);
					}
				}
				
				try
				{
					user.sendMessage("000");
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			}
		}
		
	}
	
	public void receiveData(String str)
	{
		if(sequenceReceiveData)
		{
			if(tempMember.contains(str)) tempMember.remove(str);
		}
	}
	
	
		
	
}
