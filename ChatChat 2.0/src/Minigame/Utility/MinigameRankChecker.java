package Minigame.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.w3c.dom.ranges.Range;

import Client.Data.MaintenanceData;
import Client.Fileprotocol.Download.DownloadTask;
import Client.Fileprotocol.Download.DownloadURLTask;
import Client.Interface.SwingFileDownloadHTTP;
import Client.Transmit.TransferFile;

public class MinigameRankChecker
{
	
	//원격지에서 랭킹 파일 다운로드
	//내가 상위 랭크보다 점수가 높을때
	//랭킹에 게시
	
	private String rankName[] = new String[30];
	private int rankPoint[] = new int[30];
	private int maxRank = 0;
	//private String gameName = "";

	
	/**
	 * 원격지로부터 랭크 파일 다운로드
	 * @param gameName 현재 확인 대상 게임 이름
	 */
	private boolean getRankFile(String gameName)
	{
		if(DownloadURLTask.fileUrlDownload("http://" + MaintenanceData.serverIP + "/rank/"+gameName + ".txt", "C:/")) return true;
		else return false;
		
	}
	
	public boolean loadRankFile(String gameName)
	{

			try
			{
				//파일 다운 로드 실패시 false 값 반환
				if(!getRankFile(gameName)) return false;
				
				BufferedReader textLine = new BufferedReader(new FileReader("C:/" + gameName + ".txt"));
				
				int i=0;
				while(textLine != null)
				{
					String tmp = textLine.readLine();
					if((tmp == null) || tmp.equals("")) break;
					
					else
					{
						StringTokenizer tokenData = new StringTokenizer(tmp, "$");
						while(tokenData.hasMoreTokens())
						{
							if(i == 7) break;
							StringTokenizer token = new StringTokenizer(tokenData.nextToken(), "|");
							if(token.hasMoreTokens()) 
								rankName[i] = token.nextToken();
							if(token.hasMoreTokens()) 
								rankPoint[i] = Integer.valueOf(token.nextToken());
							i++;
						}
					}
				}
				//마지막 데이터의 index 값 저장
				maxRank = i-1;
				return true;
			//파일 읽어서 변수에 랭킹 값 저장
				
			} catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		
	}
	
	public boolean compareRank(String gameName, String name, int point) throws IOException
	{
		String subName;
		if(name.length() > 8)
			subName = name.substring(0, 8);
		else
			subName = name.substring(0, name.length());
		
		
		if(loadRankFile(gameName))
		{
			rangeData();
			printData();
			//사용자 데이터가 중간에 들어 갈때
			//모든 데이터를 한칸씩 뒤로 미루고 빈 자리에 저장
			for(int i=0; i<maxRank; i++)
			{
				if(point > rankPoint[i])
				{
					insertData(i, subName, point);
					sendRankFile(gameName);
					printData();
					return true;
				}
			}
			//사용자 데이터가 마지막에 들어갈때
			//위의 반복문 안에서 함수가 종료 되지 않았으면, 어느 자리에도 데이터가 포함되었지 않았다고 판단하고
			//맨 마지막에 데이터 삽입
			insertData(subName, point);
			printData();
			sendRankFile(gameName);
			return true;
		}
		else
		{
			
		}
		return true;
		
	}
	
	private void rangeData()
	{
		
		for(int i=0; i<maxRank; i++)
		{
			for(int j=0; j<maxRank; j++)
			{
				if(rankPoint[i] > rankPoint[j])
					dataExchange(i, j);
			}
		}
	}
	
	private void insertData(int index, String name, int point)
	{
		for(int z=maxRank+1; z>=index; z--)
		{
			rankName[z+1] = rankName[z];
			rankPoint[z+1] = rankPoint[z];
		}
		
		rankName[index] = name;
		rankPoint[index] = point;
		
		maxRank++;
		rangeData();
	}
	
	private void insertData(String name, int point)
	{
		rankName[maxRank] = name;
		rankPoint[maxRank] = point;
		
		maxRank++;
		rangeData();
	}
	
	private void dataExchange(int indexOriginal, int indexBeside)
	{
		int tmpInteger;
		String tmpString;
		
		tmpInteger = rankPoint[indexOriginal];
		rankPoint[indexOriginal] = rankPoint[indexBeside];
		rankPoint[indexBeside] = tmpInteger;
		
		tmpString = rankName[indexOriginal];
		rankName[indexOriginal] = rankName[indexBeside];
		rankName[indexBeside] = tmpString;
		
	}
	
	private void sendRankFile(String gameName) throws IOException
	{
		BufferedWriter fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:/" + gameName + ".txt")));
		
		for(int i=0; i<maxRank; i++)
		{
			if(rankName[i] == null || rankName.equals("")) continue;
			if(rankPoint[i] == 0) continue;
			if(rankPoint[i] < 0) continue;
			
			fout.write(rankName[i] + "|" + rankPoint[i]+"$");
			fout.flush();
		}
		
		fout.close();
		
		//서버에 전송
		try
		{
			//파일 전송시 URI 규격은 \\ 으로 통일해야겠다.
			if(MaintenanceData.isUpdateRank) new TransferFile(MaintenanceData.serverIP, MaintenanceData.fileServerPORT, "C:\\" + gameName + ".txt");
			System.out.println("Fileupload success");
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void printData()
	{
		for(int i=0; i<maxRank; i++)
		{
			System.out.println(i + " " + rankName[i] + "  " + rankPoint[i]);
		}
	}
	
	public int getMaxData()
	{
		return maxRank;
	}
	
	public String getName(int i)
	{
		return rankName[i];
	}
	
	public String getPoint(int i)
	{
		return String.valueOf(rankPoint[i]);
	}
	
}
