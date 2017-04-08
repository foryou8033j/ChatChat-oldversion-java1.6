package Client.Data;

import java.awt.Font;
import java.net.Socket;
import java.util.Random;

/**
 * 클라이언트, 서버부, 게임부 데이터 통합 관리 클래스
 * 
 * @author Jeoxxiii
 */
public class MaintenanceData
{
	/******************** 통합 데이터 관리 ***********************/
	public static Socket socket = null; //단일 소켓
	
	final public static boolean isDebug = false;	//디버그 모드
	final public static boolean isLogging = false; //로그 정보를 남기는지
	final public static String curVersion = "3.6";	//버전
	
	public static String serverIP = ""; //Default서버 ip
	final public static int serverPORT = 4030;	 //채팅 서버 port 10:4030 / 28:3000
	final public static int fileServerPORT = 4032; 	//파일 서버 port 10:4032 / 28:1330
	
	public static boolean useEncodedDataTrnsfer = true; //데이터 통신 암호화 유무
	final public static boolean __showEncodeData = false;	//콘솔창에 암호화 데이터 출력
	
	
	/******************** 클라이언트 데이터 관리 *****************/
	public static String clientIP = "";		//클라이언트 ip
	
	final public static String programName = "chat10";	//클라이언트 명
	final public static String serverInitPassword = "5530476";	//서버 진입 패스워드 10:5530476
	final public static String adminPassword = "rnrqkq1!";	//관리자권한 패스워드 10:rnrqkq1!
	
	final public static String uploadFolderAddress = "/upload/";		//서버상 업로드 폴더 위치
	
	final public static String updateCheckerLocalFileAddress = "C:/";	 	//로컬상에 내려받아지는 업데이트 확인 파일 위치
	final public static String updateCheckerLocalFileName = "chatVersion10.txt";	//로컬상에 내려받아지는 업데이트 확인 파일명
	
	final public static String noticeFileName = "notice10.txt";		//서버상에서 보여지는 공지사항 파일명
	final public static String clientFileName = "chat10.jar";	//업데이트시 내려받는 클라이언트 파일명
	
	final public static boolean useCurrentOSTheme = true;	//현재 사용중임 OS의 디자인 적용
	final public static boolean isUserInputServerIP = false;	//사용자 임의 IP입력 유무
	final public static boolean ableToMultipleClientRunning = true;	//클라이언트 다중 실행 유무
	
	final public static Font fontTextArea = new Font("맑은 고딕", Font.ITALIC, 11);	//기존 채팅창 폰트
	final public static int maxSendMessageChar = 255;	//최대 전송 메세지 크기
	final public static int maxNameChar = 15;	//최대 입력 가능한 닉네임
	
	//전역일 정보 입력
	final public static int dYear = 2017;
	final public static int dMonth = 4; //4
	final public static int dDate = 25; //25
	
	
	/******************** 서버 데이터 관리 ***********************/	
	final public static boolean __showHeartBeatData = true; //콘솔창에 하트비트 데이터 출력
	
	final public static String updateCheckerFileAddress = "";		//서버상 업데이트 check 파일 위치 (default = C:/)
	final public static String updateCheckerFileName = "chatVersion10.txt";	//버전 확인 파일명
	
	//하트비트 데이터 전송 간격
	final public static boolean useHeartbeatCheckService = false;	//HeartBeat 기능을 이용하여 서버 상태 확인
	final public static int serverHeartBeatSequence = 2000;		//HeartBeat 데이터 송신 간격
	
	//서버 재연결 시도 간격
	final public static int clinetReconnectToServerSequence = new Random().nextInt(2000); //서버 재 연결 시도 간격
	
	
	/******************** 게임 데이터 관리 ***********************/
	final public static boolean isUpdateRank = true; //랭킹 서버에 연결하여 데이터 송수신 여부
	
	/**
	 * 클라이언트 고유 해쉬값 생성
	 * @return 10000 이하의 integer 해쉬 값
	 */
	public static String getHash()
	{
		return String.valueOf(new Random().nextInt(10000));
	}
	
	/**
	 * 랜덤 정수 생성
	 * @param n1 최솟값
	 * @param n2 최대값
	 * @return 난수 반환
	 */
	public static int getRandomRange(int n1, int n2)
	{
		return (int) (Math.random() * (n2 - n1 + 1) + n1);
	}
	
	/**
	 * 클라이언트 이름
	 * @return 클라이언트 이름 + 버전 명
	 */
	public static String getProgramName()
	{
		if(!isDebug)
		{
			return programName + " " + curVersion;
		}
		else
		{
			return programName + " " + curVersion + " DEBUG";
		}
	}
	
	/**
	 * 각 사무실 위치 정보 
	 * @param hiddenIP
	 * @return 위치 String
	 */
	public static String returnPlaceName(String hiddenIP)
	{
		return "";
	}

}
