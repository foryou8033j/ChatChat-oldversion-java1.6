package Client.Transmit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import Client.Data.MaintenanceData;
import Client.Interface.ClientFrame;
import Utility.Notification.DesktopNotify;
import Utility.Security.AES256Cipher;

public class ReceiveData extends Thread implements ActionListener
{
	ClientFrame clientFrame;
	public BufferedReader bin;
	String readLine;
	StringTokenizer token;
	boolean isShowingMessage = false;
	boolean isShowingServerErrorRestoreMessage = false;
	
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public ReceiveData(ClientFrame clientFrame)
	{
		this.clientFrame = clientFrame;
	}

	/**
	 * 데이터 수신 로직 종료
	 */
	public void closeReceive()
	{
		try
		{
			MaintenanceData.socket.close();
			//bin.close();
		} catch (Exception e)
		{
			System.out.println("클라이언트 | 폐쇄 오류 | " + e.getMessage());
		}
	}

	@Override
	public void run()
	{
		
		while (true)
		{
			
			try
			{
				// 서버의 스트림으로부터 읽기
				bin = new BufferedReader(new InputStreamReader(MaintenanceData.socket.getInputStream(), "UTF-8"));
				
				// 서버에서 날라온거것을 한 라인씩 읽어서 호출하기
				while ((readLine = bin.readLine()) != null)
				{
					
					//암호화되어 수신 된 데이터, 복호화하여 처리
					if(MaintenanceData.__showEncodeData) System.out.println("수신된 암호화 데이터 | " + readLine);
					
					String decodeData = AES256Cipher.decode(readLine);
					if(!MaintenanceData.__showHeartBeatData)
					{
						if( decodeData.contains("900") || decodeData.contains("901"));
						else System.out.println("클라이언트 ◀ 서버 | " + decodeData);
					}
					else
						System.out.println("클라이언트 ◀ 서버 | " + decodeData);
						 
					manager(AES256Cipher.decode(readLine));
					
					
				}
			} catch (Exception e)
			{
			} finally
			{
				
				closeReceive();
			}
			
		}
	}

	/**
	 * Notification 액션 수행
	 */
	public void showNotification(String title, String message, int messageType, long time)
	{
		DesktopNotify.showDesktopMessage(title, message, messageType, time, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					clientFrame.toFront();
					clientFrame.setState(Frame.NORMAL);
				}
			});
	}

	/**
	 * 서버로 부터 받은 데이터 재가공 후 클라이언트에 적용, 또는 재전송
	 * @param str
	 */
	private void manager(String str)
	{
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
		
		String place = MaintenanceData.returnPlaceName(ip);
		
		switch (flag)
		{
			// 인원 수 검사
			case 000:
				__checkPeopleCount(str);
				break;
				
			//닉네임 변경 사항 게시
			case 001:
				clientFrame.append("■ 닉네임 변경【 " + hashKey + " -> " + roomIndex + " 】 " + time + " <BR><BR>");
				break;
				
			// 서버 공지사항
			case 002:
				
				clientFrame.append("■ 공지 사항【 " + hashKey + " 】 ");
				break;
			// 개인 수신 메세지 출력
			case 161:
				__printPrivateMessage(time, name, roomIndex, message);
				break;
				
			//관리자 추방 명령 수행
			case 162:
				clientFrame.append("■ "+ hashKey + " 가 관리자로부터 추방되었습니다. 사유 : " + roomIndex + "<BR><BR>");
				break;
			//관리자 채팅 삭제 명령 수행
			case 163:
				clientFrame.txtArea.setText("");
				clientFrame.append("■ 관리자 명령으로 채팅창을 모두 지웠습니다. <BR><BR>");
				break;
				
			// 파일 전송후 사용자 채팅창에 메세지 등록
			case 171:
				__fileSendToServerFromClient(hashKey);
				break;
				
			// 유저 입장
			case 181:
				clientFrame.append("■ 유저 입장【 " + name + " (" + ip + ") " + place + " 】 " + time + " <BR><BR>");
				break;
			//서버 복구 메세지 출력
			case 182:
				__showServerRestoreMessage(time);
				break;
			// 유저 퇴장
			case 191:
				clientFrame.append("■ 유저 퇴장【 " + name + " (" + ip + ") " + place + " 】 " + time + " <BR><BR>");
				break;
			// 중복 이름 발견
			case 400:
				__findOverlabName(name, hashKey);
				break;
				
			// 현재 창 띄워짐
			case 770:
				__userWindowViewing(name);
				break;
			// 현재 창 내려감
			case 771:
				__userWindowHidden(name);
				break;
				
			//heartbeat 패킷 수신에 대한 response 전송
			case 900:
				if(hashKey.equals(clientFrame.id))
				{
					clientFrame.transmitThread.sendMessage("901|" + clientFrame.id);
				}
				//clientFrame.transmitThread.sendMessage("901" + "|" + roomIndex + "|" + "0" + "|" + dateFormat.format(calendar.getTime()) +"|" + "Connection OK...");
				break;
			//게임 관련 내용 수신
			case 1200:
				__printGamePoint(time, hashKey, name, roomIndex);
				break;
				
			//디폴트 메세지 수신
			default:
				__defaultReceiveMessage(time, name, message);
				break;
		}
	}

	/**
	 * 기본 사용자 메세지 출력
	 * @param time
	 * @param sender
	 * @param message
	 */
	private void __defaultReceiveMessage(String time, String sender, String message)
	{
		isShowingServerErrorRestoreMessage = false;
		if (sender.equals(clientFrame.id))
		{
			clientFrame.append("<STRONG>" + time + "【 " + sender + " 】 ▶ " + message + "<BR><BR>");
			if (isShowingMessage) isShowingMessage = false;
			clientFrame.txtArea.setCaretPosition(clientFrame.txtArea.getDocument().getLength());
		}
		else
		{
			clientFrame.append(time + "【 " + sender + " 】 ▷ " + message + "<BR><BR>");
			
			//알람 체크 안해도 나 부르는건 뜨게 하자
			if(message.contains("@"+clientFrame.id) ) showNotification("새 메세지", sender + "님이 찾습니다", DesktopNotify.TIP, 10000L);
			else if (clientFrame.chkNotification.isSelected() && clientFrame.currentClientLock) showNotification("새 메세지", "새로운 메세지가 있습니다.", DesktopNotify.INFORMATION, 2000L);
			else if (clientFrame.chkNotification.isSelected() && clientFrame.isMinimized && !clientFrame.currentClientLock) showNotification("새 메세지", "["+sender+"] " + message, DesktopNotify.INFORMATION, 2000L);
		}
		
	}
	
	/**
	 * 서버의 데이터를 받아 인원수 현황을 클라이언트에 적용
	 */
	private void __checkPeopleCount(String str)
	{
		clientFrame.panelMember.removeAll();
		clientFrame.panelMember.revalidate();
		clientFrame.panelMember.repaint();
		token = new StringTokenizer(str, "|");
		Integer.parseInt(token.nextToken());
		while (token.hasMoreTokens())
		{
			String tmp_string = token.nextToken();
			JButton tmpButton = new JButton(tmp_string);
			if (clientFrame.id.equals(tmp_string))
			{
				tmpButton.setToolTipText("자신의 닉네임을 클릭하여 변경 할 수 있습니다.");
				tmpButton.setFont(new Font("", Font.BOLD, 12));
				tmpButton.setForeground(Color.BLUE);
			}
			else
			{
				tmpButton.setToolTipText("<HTML>상대방이 창을 비활성화 했을 때는 붉은색으로 표시됩니다.<BR>클릭하여 개인 메세지를 전송 할 수 있습니다.");
			}
			tmpButton.addActionListener(this);
			clientFrame.panelMember.add(tmpButton);
		}
		clientFrame.panelMember.revalidate();
	}
	
	/**
	 * 서버로 부터 받은 개인 메세지를 재가공 하여 클라이언트에 적용
	 * @param str
	 */
	private void __printPrivateMessage(String time, String sender, String receiver, String msg)
	{
		if (receiver.equals(clientFrame.id))
		{
			if (msg.contains("AdminMessageSender"))
			{
				__admin_activeAdminMessage(msg);
			}
			else
			{
				clientFrame.append("<STRONG><font color=blue>" + time + "【 " + sender + "의 개인 메세지 】<BR>▶" + msg + "</font color><BR><BR>");
				if (clientFrame.chkNotification.isSelected() && clientFrame.currentClientLock) showNotification("새 개인 메세지", sender + "님이 개인 메세지를 보냈습니다.", DesktopNotify.INFORMATION, 3000L);
				else if (clientFrame.chkNotification.isSelected() && !clientFrame.currentClientLock) showNotification(sender + "님의 새 개인 메세지", msg , DesktopNotify.INFORMATION, 5000L);
			}
		}
	}
	
	/**
	 * 서버로 부터 받은 관리자 명령을 수행
	 * @param msg
	 */
	private void __admin_activeAdminMessage(String msg)
	{
		String subMessage = "";
		token = new StringTokenizer(msg, "&&");
		int action = Integer.valueOf(token.nextToken());
		token.nextToken();
		if (token.hasMoreTokens()) subMessage = token.nextToken();
		switch (action)
		{
			case 444:
				JOptionPane.showMessageDialog(clientFrame, "<HTML>관리자로부터 추방되었습니다.<BR>사유 : " + subMessage , "관리자 명령 수행", JOptionPane.INFORMATION_MESSAGE);
				clientFrame.closeAction();
				break;
		}
	}
	
	/**
	 * 서버에 등록된 파일을 채팅창에 게시
	 */
	private void __fileSendToServerFromClient(String recieveFileName)
	{
		String filename = recieveFileName;
		int slashIndex = filename.lastIndexOf('\\');
		int periodIndex = filename.lastIndexOf('.');
		String strExtension[] = { "jpg", "JPG", "png", "PNG", "bmp", "BMP", "gif", "GIF", "jpeg", "JPEG" };
		// 파일 어드레스에서 마지막에 있는 파일이름을 취득
		String fileName = filename.substring(slashIndex + 1);
		String extension = filename.substring(periodIndex + 1);
		for (String ext:strExtension)
		{
			if (ext == null)
			{
				clientFrame.append("■ 서버에 파일이 게시 됨 【 <a href=\"http://" + MaintenanceData.serverIP + MaintenanceData.uploadFolderAddress + fileName + "\">다운로드 (" + fileName + ")</a> 】  <BR><BR>");
				return;
			}
			if (ext.equals(extension))
			{
				//style=\"width: 100px; heigh: 100px\"
				//width=150 height=160
				//TODO 이미지를 pane 에 출력시 크기가 제대로 표시되지 않음.
				//String imgstr = "http://" + MaintenanceData.serverIP + "/upload/" + fileName;
				clientFrame.append("■ 서버에 파일이 게시 됨 【 <a href=\"http://" + MaintenanceData.serverIP + MaintenanceData.uploadFolderAddress + fileName + "\">다운로드(" + fileName + ")</a> 】  <BR> ");
				clientFrame.appendImg("<a href=\"http://" + MaintenanceData.serverIP + MaintenanceData.uploadFolderAddress + fileName + "\"><img src=\""
							+ "http://" + MaintenanceData.serverIP + MaintenanceData.uploadFolderAddress + fileName
							+ "\" width=170 height=170></img></a><BR><BR>");
				
				break;
			}
			else
				continue;
		}
		clientFrame.append("■ 서버에 파일이 게시 됨 【 <a href=\"http://" + MaintenanceData.serverIP + MaintenanceData.uploadFolderAddress + fileName + "\">다운로드 (" + fileName + ")</a> 】  <BR><BR>");
		
	}
	
	/**
	 * 사용자가 현재 창을 띄움<BR>
	 * 닉네임 검게 표시, 자신의 닉네임 푸르게 표시
	 * @param name
	 */
	private void __userWindowViewing(String name)
	{
		for (int i = 0; i < clientFrame.panelMember.getComponentCount(); i++)
		{
			if (((JButton) clientFrame.panelMember.getComponents()[i]).getText().equals(name))
				{
					((JButton) clientFrame.panelMember.getComponents()[i]).setForeground(Color.BLACK);
					isShowingServerErrorRestoreMessage = false;
				}
			if (((JButton) clientFrame.panelMember.getComponents()[i]).getText().equals(clientFrame.id)) ((JButton) clientFrame.panelMember.getComponents()[i]).setForeground(Color.BLUE);
		}
	}
	
	/**
	 *사용자차 창을 내림
	 */
	private void __userWindowHidden(String name)
	{
		for (int i = 0; i < clientFrame.panelMember.getComponentCount(); i++)
			if (((JButton) clientFrame.panelMember.getComponents()[i]).getText().equals(name)) ((JButton) clientFrame.panelMember.getComponents()[i]).setForeground(Color.RED);
	}
	
	/**
	 * 서버 복구 메세지 및 알람 출력
	 * @param time
	 */
	private void __showServerRestoreMessage(String time)
	{
		if(!isShowingServerErrorRestoreMessage) 
		{
			clientFrame.append("■ 서버 장애 복구 " + time + " <BR><BR>");
			DesktopNotify.showDesktopMessage("서버 장애 복구", "서버 장애가 복구 되었습니다", DesktopNotify.SUCCESS, 4500, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					clientFrame.toFront();
					clientFrame.setState(Frame.NORMAL);
				}
			});
			isShowingServerErrorRestoreMessage = true;
		}
	}
	
	/**
	 * 중복 이름 발견
	 */
	private void __findOverlabName(String name, String hashKey)
	{
		if (name.equals(clientFrame.enteredFrame.getID()) && hashKey.equals(clientFrame.hashKey))
		{
			JOptionPane.showMessageDialog(null, "중복 된 이름이 있습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			clientFrame.enteredFrame.setVisible(true);
			clientFrame.setVisible(false);
			clientFrame.isFirst = true;
		}
	}
	
	/**
	 * 게임 획득에 따른 점수를 채팅창에 출력한다.
	 * @param time 시간
	 * @param gameName 게임 이름
	 * @param userName 사용자 이름
	 * @param point 점수
	 */
	private void __printGamePoint(String time, String gameName, String userName, String point)
	{
		clientFrame.append("★ " + userName +" 님 이 " + gameName + " 에서 " + point + " 점을 획득!<BR><BR>");
	}
	
/**
	 * 클라이언트에서 동기적으로 생성되는 컴포넌트들의 액션을 수행
	 */
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		String str = ((JButton) arg0.getSource()).getText();
		boolean isOverLab = false;
		if (str.equals(clientFrame.id))
		{
			String tmp = clientFrame.id;
			String nick = JOptionPane.showInputDialog(clientFrame, "닉네임 입력", "닉네임 변경", JOptionPane.OK_CANCEL_OPTION);
			if (nick != null)
			{
				for (int i = 0; i < clientFrame.panelMember.getComponentCount(); i++)
					if (((JButton) clientFrame.panelMember.getComponents()[i]).getText().equals(nick)) isOverLab = true;
				if (isOverLab)
					JOptionPane.showMessageDialog(clientFrame, "중복 된 닉네임이 존재 합니다", "오류", JOptionPane.ERROR_MESSAGE);
				else if (nick.length() >= MaintenanceData.maxNameChar)
					JOptionPane.showMessageDialog(clientFrame, "닉네임은 최대 "+MaintenanceData.maxNameChar+"자 까지 입력 가능합니다.", "오류", JOptionPane.ERROR_MESSAGE);
				else if (nick.equals("") || nick == null)
					JOptionPane.showMessageDialog(clientFrame, "닉네임을 입력 하세요.", "오류", JOptionPane.ERROR_MESSAGE);
				else if (nick.equals("Null") || nick.equals("null"))	//null씨발
					JOptionPane.showMessageDialog(clientFrame, "허용되지 않은 닉네임입니다.", "오류", JOptionPane.ERROR_MESSAGE);
				else
				{
					clientFrame.transmitThread.sendMessage("001|" + tmp + "|" + nick);
					clientFrame.id = nick;
				}
			}
		}
		else
		{
			String sendMessage;
			calendar = Calendar.getInstance();
			if(clientFrame.isAdmin)
			{
				
				if(clientFrame.showUserChoiceMessage("관리자 권한", "해당 사용자를 추방하시겠습니까?"))
				{
					String message = JOptionPane.showInputDialog(clientFrame, "사유 입력", "사유 입력", JOptionPane.INFORMATION_MESSAGE);
					if(message == null || message.equals("")) message = "없음";
					
					clientFrame.transmitThread.sendMessage("161" + "|" + clientFrame.hashKey + "|" + str + "|" + dateFormat.format(calendar.getTime()) + "|" + clientFrame.id + "|" + MaintenanceData.clientIP + "|444&&AdminMessageSender&&" + message); //사용자가 접속해 있다고 가정 정상적인 종료 로직을 전송
					clientFrame.transmitThread.sendMessage("161" + "|" + clientFrame.hashKey + "|" + str + "|" + dateFormat.format(calendar.getTime()) + "|" + clientFrame.id + "|" + MaintenanceData.clientIP + "|333&&AdminMessageSender&&" + message); //사용자가 비정상적으로 접속해 있다고 가정, 서버에서 강제로 제거
					clientFrame.transmitThread.sendMessage("162" + "|" + str + "|" + message);
				}
			}
			else
			{
				sendMessage = JOptionPane.showInputDialog(clientFrame, "전송 할 메세지 입력", str + "에게 메세지 전송", JOptionPane.OK_CANCEL_OPTION);
				if (sendMessage == null || sendMessage.equals(""))
					return;
				else if(sendMessage.contains("AdminMessageSender"))
					return;
				else
				{
					clientFrame.transmitThread.sendMessage("161" + "|" + clientFrame.hashKey + "|" + str + "|" + dateFormat.format(calendar.getTime()) + "|" + clientFrame.id + "|" + MaintenanceData.clientIP + "|" + sendMessage);
					JOptionPane.showMessageDialog(clientFrame, "메세지가 전송 되었습니다.", "전송 완료", JOptionPane.INFORMATION_MESSAGE);
					clientFrame.append("<STRONG><font color=blue>" + dateFormat.format(calendar.getTime()) + "【 " + str + "에게 전송 된 개인 메세지 】<BR>▶" + sendMessage + "</font color><BR><BR>");
				}
			}
			
		}
	}
}
