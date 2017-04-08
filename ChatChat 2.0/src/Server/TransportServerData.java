package Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

import Client.Data.MaintenanceData;
import Client.Transmit.ServerConnection.UpkeepConnection;
import Utility.Security.AES256Cipher;

/**
 * 서버데이터 송수신 쓰레드
 * 
 * @author jeoxxiii
 *
 */
public class TransportServerData extends Thread
{
	public int index = 0;
	public String name = "";
	public String hash = "";
	Socket socket;
	
	BufferedReader bin = null;
	BufferedWriter bout = null;
	
	RunningServer server;
	String userMsg;
	
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public TransportServerData(Socket socket, RunningServer server)
	{
		this.socket = socket;
		this.server = server;
	}

	public void setInfo(String name, String hash)
	{
		this.name = name;
		this.hash = hash;
		setName(name);
	}

	/**
	 * 클라이언트 -> 서버 데이터 수신
	 */
	@Override
	public void run()
	{
		try
		{
			while(!Thread.currentThread().isInterrupted())
			{
				/*
				 * connectionChcker = new ClientConnectionChcker(this, socket,
				 * server); vec_Checker.add(connectionChcker);
				 * connectionChcker.setDaemon(true); connectionChcker.start();
				 */
				// 클라이언트와 서버 연결 상태 확인
				// Client 와 통신이 종료 될 때 까지 Socket 서버 종료를 보류
				// 서버의 스트림으로부터 읽기
				bin = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				// 클라이언트가 보낸 스트림을 한 줄씩 읽어서 메니져 호출
				while ((userMsg = bin.readLine()) != null)
				{
					if (userMsg != null)
					{
						if (MaintenanceData.__showEncodeData) System.out.println("수신된 암호화 데이터 | " + userMsg);
						
						//2.7버전 업데이트 오류로 인한 공지사항 출력, 181을 수신받은 경우 비암호화된 데이터라 판단하고 암호화 모드 해제
						if(userMsg.startsWith("181|"))
						{
							MaintenanceData.useEncodedDataTrnsfer = false;
							
							manager("002|" + "클라이언트 2.7 버전은 오류가 있으니 <a href=\"http://32.1.21.41/chatchat.jar\">여기</a>에서 직접 다시 다운로드 받으세요.");
							manager("002|" + "클라이언트 2.7 버전은 오류가 있으니 <a href=\"http://32.1.21.41/chatchat.jar\">여기</a>에서 직접 다시 다운로드 받으세요.");
							manager("002|" + "클라이언트 2.7 버전은 오류가 있으니 <a href=\"http://32.1.21.41/chatchat.jar\">여기</a>에서 직접 다시 다운로드 받으세요.");
							
							MaintenanceData.useEncodedDataTrnsfer = true;
						}
						
						if(MaintenanceData.useEncodedDataTrnsfer) userMsg = AES256Cipher.decode(userMsg);
							
						if (!MaintenanceData.__showHeartBeatData)
						{
							if (userMsg.contains("900") || userMsg.contains("901"));
							// else System.out.println("서버 | 수신 메세지 ◀ " + userMsg);
						}
						else
							// System.out.println("서버 | 수신 메세지 ◀ " + userMsg);
							manager(userMsg);
					}
				}
			} 
		}catch (Exception e)
		{
			
		}
	}

	/**
	 * 서버 -> 클라이언트 데이터 송신
	 * 
	 * @param msg
	 *            전송 메세지 string
	 * @throws IOException
	 */
	public synchronized void sendMessage(String msg) throws IOException
	{
		try
		{
			// 소켓 배열을 돌면서 연결된 클라이언트 모두에 메세지 전송
			for (Socket socket : server.vec_Socket)
			{
				try
				{
					bout = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
					// 서버에서 종합 된데이터 암호화 후 각 클라이언트에 송신
					String encodeData = null;
					
					if(MaintenanceData.useEncodedDataTrnsfer) encodeData = AES256Cipher.encoding(msg);
					else encodeData = new String(msg);
					
					bout.write(encodeData + "\n");
					if (MaintenanceData.__showEncodeData) System.out.println("송신된 암호화 데이터 | " + encodeData);
					bout.flush();
				} catch (Exception e)
				{
					bout.flush();
				}
			}
		} catch (Exception e)
		{
			System.out.println("서버 | 송신중 오류 | " + e.getMessage());
		}
	}

	synchronized void closeUser(int index) throws IOException
	{
		try
		{
			System.out.println("서버 | 사용자 정리");
			// Client 와 Socket 이 통신 중 일경우 종료될 때 까지 대기.
			bin.close();
			bout.close();
			socket.close();
			server.removeUser(index);
			server.printAllElements("서버 | 사용자 정리 끝");
		} catch (Exception e)
		{
			e.printStackTrace();
			server.printAllElements("서버 | 정리중 오류 | " + e.getMessage());
		}
	}

	/**
	 * 각 클라이언트 소켓, 쓰레드 진행되어야 하는 명령 수행
	 * 
	 * @param str
	 * @throws IOException
	 */
	public void manager(String str) throws IOException
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
		// 사용자 입장
			case 181:
				
				boolean isOverlabName = false;
				boolean isOverlabHashKey = false;
				for (int i = 0; i < server.vec_Name.size(); i++)
					if (server.vec_Name.get(i).equals(name)) isOverlabName = true;
				for (int i = 0; i < server.vec_Hash.size(); i++)
					if (server.vec_Hash.get(i).equals(hashKey)) isOverlabHashKey = true;
				if (isOverlabName)
				{
					String temp_string = "";
					for (String string : server.vec_Name)
						temp_string = temp_string.concat(" | " + string);
					String tstr = "서버 | 명령줄 실행 | 중복 된 사용자 | " + name + "(" + hashKey + ") | 사용자 추방" + temp_string;
					server.writeLog(str);
					str = "400|" + hashKey + "|" + roomIndex + "|" + time + "|" + name + "|" + ip + temp_string;
					if (isOverlabName && isOverlabHashKey) manager("000");
				}
				else
				{
					if (name == null || name.equals("Null") || name.equals("null"))
						manager("000");
					else
					{
						server.vec_Name.add(name);
						server.vec_Hash.add(hashKey);
						setInfo(name, hashKey);
						BufferedReader textVersion;
						try
						{
							textVersion = new BufferedReader(new InputStreamReader(new FileInputStream("C:/inetpub/wwwroot/" + MaintenanceData.noticeFileName), "UTF-8"));
							// textVersion = new BufferedReader(new
							// FileReader("C:/inetpub/wwwroot/notice.txt"));
							manager("002|" + textVersion.readLine());
						} catch (FileNotFoundException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						manager("000");
					}
				}
				sendMessage(str);
				break;
			// 서버가 복귀된 유저가 접속
			case 182:
				// user.sendMessage(str);
				// user.setInfo(name, hashKey);
				
				if(!server.vec_Name.contains(name)) server.vec_Name.add(name);
				if(!server.vec_Hash.contains(hashKey)) server.vec_Hash.add(hashKey);
				setInfo(name, hashKey);
				// addUser(name, hashKey, user, user.socket);
				/*
				 * user.sendMessage(str); for (int i = 0; i < vec_Name.size();
				 * i++) if (vec_Name.get(i).equals(name)) str = "000"; for (int
				 * i = 0; i < vec_Hash.size(); i++) if
				 * (vec_Hash.get(i).equals(hashKey)) str = "000"; if
				 * (!str.equals("000")) { vec_Name.add(name);
				 * vec_Hash.add(hashKey); }
				 */
				sendMessage(str);
				manager("000");
				break;
			// 사용자 퇴장
			case 191:
				
				server.vec_Name.remove(name);
				server.vec_Hash.remove(hashKey);
				server.removeUser(this, socket);
				Thread.currentThread().interrupt();
				sendMessage(str);
				/*sendMessage(str);
				TransportServerData t_user = (TransportServerData) server.vec_Thread.get(server.vec_Name.indexOf(name));
				t_user.closeUser(server.vec_Name.indexOf(name));
				server.vec_Name.remove(name);
				server.vec_Hash.remove(hashKey);*/
				
				
				manager("000");
				break;
			// 쓰레드 단 명령문은 전체 명령문 행으로 이동
			default:
				server.manager(str);
				break;
		}
	}
}
