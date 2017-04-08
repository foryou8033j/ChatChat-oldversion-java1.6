package main;

import Client.Data.MaintenanceData;
import Server.FileServer;
import Server.RunningServer;

public class Server
{

	public static void main(String[] args)
	{
		try
		{
			RunningServer runningServer;
			runningServer = new RunningServer();
			runningServer.start();
			new FileServer(new MaintenanceData().fileServerPORT, runningServer).start();
		} catch (Exception e)
		{
		}
	}

	
}
