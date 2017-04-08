package Server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

import Client.Data.MaintenanceData;
import Client.Transmit.ServerConnection.UpkeepConnection;

public class RunningServer extends Thread
{
	// 서버 소켓, 단일 클라이언트 소켓 생성
	ServerSocket server = null;
	// 클라이언트와 연결된 소켓들을 배열처럼 저장할 벡터객체 생성
	Vector<Socket> vec_Socket;
	Vector<String> vec_Name, vec_Hash, vec_Index;
	Vector<Thread> vec_Thread;
	
	Socket client = null;
	TransportServerData user = null;
	
	//허트비트 데이터 송신
	ClientConnectionChecker connectionChcker = null;
	
	//파일 출력 버퍼
	BufferedWriter fout;
	
	public boolean isRunning = false;
	
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static ClientConnectionChecker clientConnectionChecker = null;

	public RunningServer()
	{
		
		vec_Socket = new Vector<Socket>();
		vec_Thread = new Vector<Thread>();
		vec_Name = new Vector<String>();
		vec_Hash = new Vector<String>();
		vec_Index = new Vector<String>();
		
		try
		{
			calendar = Calendar.getInstance();
			if(MaintenanceData.isLogging) this.fout = new BufferedWriter(new FileWriter("C:/inetpub/wwwroot/logg/chatlog" + date.format(calendar.getTime()) + ".txt", true));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeLog(String str)
	{
		
		if(MaintenanceData.isLogging)
		{
			try
			{
				fout.write(str);
				fout.newLine();
				fout.flush();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		System.out.println(str);
		
	}

	@Override
	public void run()
	{
		try
		{

			server = new ServerSocket(MaintenanceData.serverPORT);
			calendar = Calendar.getInstance();
			writeLog("서버 | " + dateFormat.format(calendar.getTime()) + " | 서버 정상 개방 | " + MaintenanceData.serverIP+ ":" + server.getLocalPort());
			
			while (true)
			{
				// 서버에 연결된 클라이언트를 client 소켓이 저장
				client = server.accept();
				System.out.println("서버 | 새 사용자 접속");
				
				user = new TransportServerData(client, this);
				
				addUser(user, user.socket);
				
				user.setDaemon(true);
				user.start();
				
				if(MaintenanceData.useHeartbeatCheckService) 
				{
					clientConnectionChecker = new ClientConnectionChecker(this, user);
					clientConnectionChecker.start();
				}
				
				//isRunning = true;
			}
		} catch (IOException ie)
		{
			writeLog("서버 | 오류 메세지 | " + ie.getMessage());
			
		} finally
		{
			try
			{
				writeLog("서버 | 서버 종료중 | 서버, 사용자 소켓 종료");
				
				server.close();
				vec_Socket.removeAllElements();
				vec_Name.removeAllElements();
				vec_Hash.removeAllElements();
				vec_Thread.removeAllElements();
				
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	void addUser(Thread thread, Socket socket)
	{
		vec_Index.add(String.valueOf(vec_Thread.size()));
		vec_Thread.add(thread);
		
		if(!vec_Socket.contains(socket))
			vec_Socket.add(socket);
	}
	
	void addUser(String name, String hash, Thread thread, Socket socket)
	{
		vec_Name.add(name);
		vec_Hash.add(hash);
		addUser(thread, socket);
	}

	synchronized public void removeUser(int index)
	{
		vec_Index.remove(index);
		int i=0;
		for(String indexStr:vec_Index)
		{
			if(indexStr == null) break;
			else vec_Index.setElementAt(String.valueOf(i), i);
			i++;
		}
		vec_Socket.remove(index);
		vec_Thread.remove(index);
	}
	
	synchronized public void removeUser(Thread thread, Socket socket)
	{
		vec_Index.remove(0);
		int i=0;
		for(String indexStr:vec_Index)
		{
			if(indexStr == null) break;
			else vec_Index.setElementAt(String.valueOf(i), i);
			i++;
		}
		vec_Socket.remove(thread);
		vec_Thread.remove(socket);
		
		if(vec_Name.size() == 0 && vec_Hash.size() ==0)
		{
			for(Socket soc:vec_Socket)
			{
				if(soc == null) break;
				try
				{
					soc.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(Thread trans:vec_Thread)
			{
				if(trans == null) break;
				trans.interrupt();
			}
			vec_Index.removeAllElements();
			vec_Thread.removeAllElements();
			vec_Socket.removeAllElements();
		}
	}
	
	
	public void printAllElements(String senderMsg)
	{
		System.out.println("Index : " + vec_Index.toString());
		System.out.println("Name : " + vec_Name.toString());
		System.out.println("Hash : " + vec_Hash.toString());
		System.out.println("Thread : " + vec_Thread.toString());
		System.out.println("Socket : " + vec_Socket.toString());
		System.out.println("sendData : " + senderMsg + "\n");
	}
	
	public synchronized void manager(String str) throws IOException
	{
		
		StringTokenizer token;
		UpkeepConnection upkeepConnection;
		String hashKey = "";
		String roomIndex = "";
		String time = "";
		String name = "";
		String ip = "";
		String message = "";
		token = new StringTokenizer(str, "|");
		int flag = Integer.valueOf(token.nextToken());
		if (token.hasMoreTokens()) hashKey = token.nextToken();
		if (token.hasMoreTokens()) roomIndex = token.nextToken();
		if (token.hasMoreTokens()) time = token.nextToken();
		if (token.hasMoreTokens()) name = token.nextToken();
		if (token.hasMoreTokens()) ip = token.nextToken();
		if (token.hasMoreTokens()) message = token.nextToken();
		switch (flag)
		{
		// 인원 수 검사
			case 000:
				str = "000";
				for (String string : vec_Name)
					str = str.concat("|" + string);
				user.sendMessage(str);
				break;
			// 닉네임 변경
			case 001:
				vec_Name.set(vec_Name.indexOf(hashKey), roomIndex);
				user.sendMessage(str);
				manager("000");
				break;
			// 공지사항
			case 002:
				user.sendMessage(str);
				break;
			// 현재 창 내려 놓음
			case 770:
				user.sendMessage(str);
				break;
			// 현재 창 보고 있음
			case 771:
				user.sendMessage(str);
				break;
			// 개인 전송 메세지
			case 161:
				
				if (message.contains("AdminMessageSender"))
				{
					token = new StringTokenizer(message, "&&");
					int action = Integer.valueOf(token.nextToken());
					switch (action)
					{
						case 333:
							if(vec_Name.contains(roomIndex))
							{
								int index = vec_Name.indexOf(roomIndex);
								vec_Name.remove(roomIndex);
								vec_Hash.remove(index);
								TransportServerData t_user = (TransportServerData) vec_Thread.get(index); 
								t_user.closeUser(index);
							}
							manager("000");
							
							break;
						default:
							user.sendMessage(str);
							break;
					}
				}
				else
					user.sendMessage(str);
				
				break;
			//추방 알림
			case 162:
				user.sendMessage(str);
				break;
			//강제 채팅창 삭제
			case 163:
				user.sendMessage(str);
				break;
			// 파일 전송 성공
			case 171:
				user.sendMessage(str);
				break;
			// 파일 전송 실패
			case 172:
				user.sendMessage(str);
			case 901:
				clientConnectionChecker.receiveData(hashKey);
				break;
			//기본 대화문 통신
			case 999:
				user.sendMessage(str);
				break;
			//게임 데이터 관련
			case 1100:
				user.sendMessage(str);
				break;
			default:
				user.sendMessage(str);
				
		}
		
		printAllElements(str);

	}
}
