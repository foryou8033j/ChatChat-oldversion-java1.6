package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer extends Thread
{
	private ServerSocket ss;
	RunningServer runningServer;

	boolean isGameData = false;
	
	public FileServer(int port, RunningServer runningServer)
	{
		try
		{
			ss = new ServerSocket(port);
			this.runningServer = runningServer;
		} catch (IOException e)
		{
			//e.printStackTrace();
		}
	}

	public void run()
	{
		while (true)
		{
			try
			{
				Socket clientSock = ss.accept();
				saveFile(clientSock);
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
			}
		}
	}

	private void saveFile(Socket clientSock) throws IOException
	{
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		String filename = dis.readUTF();
		int slashIndex = filename.lastIndexOf('\\');
		int periodIndex = filename.lastIndexOf('.');
		// 파일 어드레스에서 마지막에 있는 파일이름을 취득
		String fileName = filename.substring(slashIndex + 1);
		
		FileOutputStream fos;
		System.out.println(fileName);
		//게임데이터를 따로 처리 -> 추후 레지스트리 데이터 방식으로 변경 할 예정.
		if(fileName.equals("snake.txt")) isGameData = true;
		else if (fileName.equals("jumpjump.txt")) isGameData = true;
		else if (fileName.equals("tetris.txt")) isGameData = true;
		else if (fileName.equals("dodge.txt")) isGameData = true;
		else if (fileName.equals("sword.txt")) isGameData = true;
		else isGameData = false;
		
		System.out.println(fileName);
		
		if(isGameData) fos = new FileOutputStream("C:/inetpub/wwwroot/rank/" + fileName, false);
		else fos = new FileOutputStream("C:/inetpub/wwwroot/upload/" + fileName);
		
		byte[] buffer = new byte[2048];
			
		int filesize = Integer.MAX_VALUE; // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0)
		{
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}
			
		String tstr = "파일 | 파일 수신 완료 | " + filename;
		System.out.println(tstr);
		//파일 수신 정보 클라리언트에 전송
		if(!isGameData) runningServer.manager("171|" + filename);
		fos.close();
		dis.close();
		return;
		
	}
}
