package Client.Interface;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.synth.Region;

import Utility.Registry.Regedit;
import Client.Data.MaintenanceData;
import Client.Transmit.TransmitData;

public class EnteredFrame extends JFrame implements ActionListener, KeyListener
{
	boolean isDebug;
	
	static JTextField tfName = new JTextField(8);
	static JPasswordField tfPwd = new JPasswordField(8);
	
	JButton btnConnect = new JButton("접속");
	JButton btnPatch = new JButton("패치노트");
	
	ClientFrame clientFrame;

	public EnteredFrame(ClientFrame clientFrame)
	{
		super("서버 접속");
		
		if(!MaintenanceData.ableToMultipleClientRunning)
		{
			
			try{
				//데이터 소켓을 임시로 생성하여, 동일한 클라이언트 실행 시 예외 유도
				new DatagramSocket(1103);
			} catch(SocketException e)
			{
				JOptionPane.showMessageDialog(this, "<HTML>프로그램이 이미 실행 중 입니다.<BR>클라이언트를 종료합니다.", "오류", JOptionPane.ERROR_MESSAGE);
				clientFrame.closeAction();
			}
		}
		
		this.clientFrame = clientFrame;
		this.isDebug = MaintenanceData.isDebug;
		
		setLayout(new FlowLayout());
		add(new JLabel("닉네임     ", SwingConstants.CENTER));
		add(tfName);
		add(new JLabel("패스워드 ", SwingConstants.CENTER));
		add(tfPwd);
		add(btnPatch);
		add(btnConnect);
		btnConnect.setToolTipText("채팅 접속");
		
		if (isDebug)
		{
			tfName.setText("Test" + String.valueOf(new Random().nextInt(1000)));
			tfPwd.setText(MaintenanceData.serverInitPassword);
		}
		
		btnPatch.setBackground(new Color(153, 209, 182));
		btnPatch.setToolTipText("도움말과 패치노트 확인");
		
		tfName.setHorizontalAlignment(SwingConstants.CENTER);
		tfPwd.setHorizontalAlignment(SwingConstants.CENTER);
		
		tfName.setToolTipText("채팅에서 사용 될 닉네임 입력");
		tfPwd.setToolTipText("관리자에게 부여받은 비밀번호 입력");
		
		tfPwd.setEchoChar('*');
		
		btnPatch.addActionListener(this);
		btnConnect.addActionListener(this);
		tfName.addKeyListener(this);
		tfPwd.addKeyListener(this);
		super.addKeyListener(this);
		super.getContentPane().addKeyListener(this);
		
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(300, 300, 200, 120);
		
		setAlwaysOnTop(true);
		setResizable(false);
		setVisible(false);
	}
	
	void showPatchNote()
	{
		Regedit.addRegistry("isOpenPatchNote", "true");
		
		String patchNote = "<HTML><PRE>" + "<CENTER>161111<BR>"
				+ "v" + MaintenanceData.curVersion + "</CENTER>" + "<BR>"
				+ "<CENTER><STRONG>[공지 사항]</STRONG></CENTER>"
				+ "1. <BR>"
				+ "<BR><STRONG><CENTER>[패치 노트]</STRONG></CENTER>"
				+ "1. 서버 변경<BR>"
				+ "</PRE></HTML>";
		JOptionPane.showMessageDialog(null, patchNote, "패치 노트 v" + MaintenanceData.curVersion, JOptionPane.DEFAULT_OPTION);
	}

	protected void exceptEnteredRoom()
	{
		if (tfName.getText().length() >= MaintenanceData.maxNameChar)
		{
			JOptionPane.showMessageDialog(this, "닉네임은 최대 "+ MaintenanceData.maxNameChar +"자 까지 입력 가능합니다.", "오류", JOptionPane.ERROR_MESSAGE);
		}
		else if (tfName.getText().equals("") || tfName.getText() == null)
		{
			JOptionPane.showMessageDialog(this, "닉네임을 입력 하세요.", "오류", JOptionPane.ERROR_MESSAGE);
		}
		else if (tfName.getText().equals("Null") || tfName.getText().equals("null"))
		{
			JOptionPane.showMessageDialog(this, "허용되지 않은 닉네임입니다.", "오류", JOptionPane.ERROR_MESSAGE);
		}
		else if (!tfPwd.getText().equals(MaintenanceData.serverInitPassword))
		{
			JOptionPane.showMessageDialog(this, "패스워드가 일치하지 않습니다", "오류", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			//업데이트 후 or 최초 실행시 패치노트 확인 안했을때 보여주기
			if(Regedit.getRegistry("isOpenPatchNote").equals("false"))
				showPatchNote();
			
			clientFrame.isFirst = false;
			setVisible(false);
			clientFrame.setVisible(true);
			
			clientFrame.id = tfName.getText();
			
			//사용자 추가 플래그 서버에 전송
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			clientFrame.transmitThread.sendMessage("181|" + clientFrame.hashKey + "|"+MaintenanceData.curVersion+"|" + dateFormat.format(calendar.getTime()) + "|" + clientFrame.id + "|" + MaintenanceData.socket.getLocalAddress().getHostAddress().replace("32.1.", "*.*.") + "|join");
		}
	}

	public String getID()
	{
		return tfName.getText();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if(arg0.getSource() == btnConnect)
			exceptEnteredRoom();
		else if(arg0.getSource() == btnPatch)
			showPatchNote();
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) exceptEnteredRoom();
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) exceptEnteredRoom();
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) exceptEnteredRoom();
	}
}
