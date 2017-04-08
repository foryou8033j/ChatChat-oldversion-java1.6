package Client.Interface;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;

import org.hamcrest.core.IsCollectionContaining;

import Client.Data.MaintenanceData;
import Client.Transmit.TransmitData;
import Minigame.PanelMinigame;
import Utility.Registry.Regedit;

public class ClientFrame extends JFrame implements ActionListener, KeyListener, ComponentListener, Runnable
{
	
	//클래스
	public ScreenLock screenLock = new ScreenLock(this);
	public TransmitData transmitThread = null;
	public EnteredFrame enteredFrame = null;
	public PanelMinigame panelMinigame = null;
	
	//디자인
	public JPanel panelMember = new JPanel();	//사용자 목록 패널
	public JEditorPane txtArea = new JEditorPane(); 	//대화창
	public JScrollPane scrollPane = new JScrollPane(txtArea);
	public JPanel panelSideMenubar = new JPanel(); //우측 메뉴창
	public JPanel panelLowerMenubar = new JPanel(); //하단 메뉴바
	
	public JLabel labelStatusServer = new JLabel("연결됨");
	public JTextField txtInput = new JTextField(15); // 입력 창
	JButton btnTransfer = new JButton("전송");
	JButton btnClean = new JButton("삭제");
	JButton btnExit = new JButton("종료");
	JToggleButton btnMenu = new JToggleButton("<<");
	
	JCheckBox chkAlwaysTextShowBottom = new JCheckBox("항상 채팅 밑으로 보기");
	JCheckBox chkAlwayOnTop = new JCheckBox("항상 위에");
	public JCheckBox chkNotification = new JCheckBox("알림");
	JToggleButton btnAdmin = new JToggleButton("관리자권한");
	JButton btnFileSend = new JButton("파일전송");
	JToggleButton btnMiniGame = new JToggleButton("미니게임천국");
	JButton btnLockScreen = new JButton("화면 잠그기");

	//변수
	public String id;
	public String hashKey;
	
	public boolean isMinimized = false;		//현재 창 최소화 여부
	public boolean isFirst = true;		//최초 실행 여부, true : EnteredFrame 표시
	public boolean isRunning = false;	//현재 클라이언트 실행 중 여부, UpkeepConnection 에서 관리
	public boolean isAdmin = false;		//관리자 여부
	boolean isTextAlwaysBottomPosition = true;	//대화내용을 항상 밑으로 표시
	public boolean currentClientLock = false; 	//현재 클라이언트 잠금 여부
	
	public String lockPassword = "";	//잠금 패스워드 관리
	
	//시간
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public ClientFrame()
	{
		super(MaintenanceData.getProgramName());
		
		//소켓 연결 정보 저장
		MaintenanceData.clientIP = MaintenanceData.socket.getLocalAddress().getHostAddress();
		
		hashKey = MaintenanceData.getHash();
		
		enteredFrame = new EnteredFrame(this);
		
		isRunning = true;
		
		transmitThread = new TransmitData(this);
		screenLock = new ScreenLock(this);
		
		panelMinigame = new PanelMinigame(this);

		add("Center",screenLock);
		screenLock.setVisible(false);
		
		//예기치 못한 상황에서 종료되는 경우를 위한 ShutdownHook thread
		//프로그램 실행중에는 항상 동작하여야 하는 UpKeepConnection 클래스에서 동작하도록 한다.
		Thread shutdownThread = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(shutdownThread);
		
		if (MaintenanceData.isDebug)
		{
			setAlwaysOnTop(true);
			chkAlwayOnTop.setSelected(true);
			chkAlwayOnTop.setForeground(Color.RED);
			btnAdmin.setSelected(true);
			isAdmin = true;
			append("● 관리자 입니다.<BR><BR>");
			btnAdmin.setForeground(Color.RED);
		}
		txtArea.setOpaque(true);
		txtArea.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		txtArea.setContentType("text/html;charset=UTF-8");
		Font font = MaintenanceData.fontTextArea;
		String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; ";
		((HTMLDocument) txtArea.getDocument()).getStyleSheet().addRule(bodyRule);
		txtArea.setEditable(false);
		
		panelMember.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 6));
		panelMember.setEnabled(false);
		
		add("Center", scrollPane);
		add("North", panelMember);
		add("West", panelMinigame);
		add("East", panelSideMenubar);
		add("South", panelLowerMenubar);
		
		panelMinigame.setVisible(false);
		
		panelSideMenubar.setVisible(false);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollPaneMember.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		labelStatusServer.setFont(new Font("", Font.PLAIN, 12));
		panelLowerMenubar.add(labelStatusServer);
		panelLowerMenubar.add(txtInput);
		panelLowerMenubar.add(btnTransfer);
		panelLowerMenubar.add(btnClean);
		
		panelLowerMenubar.add(btnExit);
		panelLowerMenubar.add(btnMenu);
		btnMenu.addActionListener(this);
		Border loweredetched = BorderFactory.createRaisedBevelBorder();
		TitledBorder titleborder = BorderFactory.createTitledBorder(loweredetched, "MENU");
		panelSideMenubar.setBorder(titleborder);
		panelSideMenubar.setLayout(new GridLayout(7, 1, 0, 6));
		//panelSideMenubar.setLayout(new BoxLayout(panelSideMenubar, BoxLayout.Y_AXIS));
		panelSideMenubar.add(chkAlwaysTextShowBottom);
		panelSideMenubar.add(chkAlwayOnTop);
		panelSideMenubar.add(chkNotification);
		panelSideMenubar.add(btnAdmin);
		panelSideMenubar.add(btnFileSend);
		panelSideMenubar.add(btnMiniGame);
		panelSideMenubar.add(btnLockScreen);
		
		btnAdmin.addActionListener(this);
		btnLockScreen.addActionListener(this);
		btnMiniGame.addActionListener(this);
		txtInput.addKeyListener(this);
		btnFileSend.addActionListener(this);
		btnClean.addActionListener(this);
		btnTransfer.addActionListener(this);
		btnExit.addActionListener(this);
		
		
		btnClean.setToolTipText("대화창에 출력 된 모든 대화를 삭제합니다.");
		btnFileSend.setToolTipText("파일 전송을 시작합니다.");
		btnAdmin.setToolTipText("관리자 권한을 획득합니다.");
		btnMiniGame.setToolTipText("미니게임 ㄱㄱ");
		btnLockScreen.setToolTipText("허용되지 않은 사용자가 화면을 볼 수 없도록 잠급니다.");
		chkAlwaysTextShowBottom.setToolTipText("새로운 채팅이 오면 항상 대화창을 밑으로 내립니다.");
		chkAlwayOnTop.setToolTipText("대화창이 항상 위에 위치합니다.");
		chkNotification.setToolTipText("창을 최소화 했을 때 대화가 오면 알람이 표시됩니다.");
		btnExit.setToolTipText("채팅 클라이언트를 종료합니다.");
		btnMenu.setToolTipText("메뉴창을 열고 닫습니다.");
		labelStatusServer.setToolTipText("서버 연결 상태 양호");
		
	
		//하이퍼링크에서 동작할 행동 지정
		hyperlinkClickAction();
		
		//사용자가 기존에 설정한 정보 레지스트리로 부터 받아오기
		firstOptionLoadFromRegistry();
		
		//대화창 항상 밑에 두기 체크 박스 액션
		chkAlwaysTextShowBottom.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (((JCheckBox) arg0.getSource()).isSelected())
				{
					((JCheckBox) arg0.getSource()).setForeground(Color.RED);
					isTextAlwaysBottomPosition = true;
					Regedit.addRegistry("alwaysTextShowBottom", "true");
				}
				else if (!((JCheckBox) arg0.getSource()).isSelected())
				{
					((JCheckBox) arg0.getSource()).setForeground(Color.BLACK);
					isTextAlwaysBottomPosition = false;
					Regedit.addRegistry("alwaysTextShowBottom", "false");
				}
			}
		});
		
		//항상위에 체크 박스 액션
		chkAlwayOnTop.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (((JCheckBox) arg0.getSource()).isSelected())
				{
					((JCheckBox) arg0.getSource()).setForeground(Color.RED);
					setAlwaysOnTop(true);
					Regedit.addRegistry("alwaysFrameOnTop", "true");
				}
				else if (!((JCheckBox) arg0.getSource()).isSelected())
				{
					((JCheckBox) arg0.getSource()).setForeground(Color.BLACK);
					setAlwaysOnTop(false);
					Regedit.addRegistry("alwaysFrameOnTop", "false");
				}
			}
		});
		
		//알림 체크 박스 액션
		chkNotification.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (((JCheckBox) arg0.getSource()).isSelected())
				{
					((JCheckBox) arg0.getSource()).setForeground(Color.RED);
					Regedit.addRegistry("showNotification", "true");
				}
				else if (!((JCheckBox) arg0.getSource()).isSelected())
				{
					try
					{
						((JCheckBox) arg0.getSource()).setForeground(Color.BLACK);
						Regedit.addRegistry("showNotification", "false");
					} catch (Exception e)
					{
					}
				}
			}
		});
		
		//창의 최소화를 감지
		addWindowStateListener(new WindowStateListener()
		{
			@Override
			public void windowStateChanged(WindowEvent arg0)
			{
				if ((arg0.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED)
				{
					isMinimized = true;
					stopViewing();
				}
				else
				{
					isMinimized = false;
					currentViewing();
				}
			}
		});
		
		//우측 상단 X 버튼을 눌렀을 때 종료 로직을 따르도록 수행
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				if(showUserChoiceMessage("종료하시겠습니까?",  "종료하시겠습니까?")) closeAction();
			}
		});
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//pack();
		

		addComponentListener(this);
		setVisible(false);
	}
	
	@Override
	public void run()
	{
		//최초 실행시 채팅창 대신 사용자 정보 입력창 표시
		enteredFrame.setVisible(true);
	}
	
	/**
	 * 레지스트리로부터 키값을 불러와 적용
	 */
	private void firstOptionLoadFromRegistry()
	{
		if(Regedit.getRegistry("alwaysTextShowBottom").equals("true"))
		{
			chkAlwaysTextShowBottom.setSelected(true);
			chkAlwaysTextShowBottom.setForeground(Color.RED);
			isTextAlwaysBottomPosition = true;
		}
		else if(!Regedit.isContains("alwaysTextShowBottom"))
		{
			chkAlwaysTextShowBottom.setSelected(true);
			chkAlwaysTextShowBottom.setForeground(Color.RED);
			isTextAlwaysBottomPosition = true;
			Regedit.addRegistry("alwaysTextShowBottom", "true");
		}
		else
		{
			chkAlwaysTextShowBottom.setSelected(false);
			chkAlwaysTextShowBottom.setForeground(Color.BLACK);
			isTextAlwaysBottomPosition = false;
		}
		
		if(Regedit.getRegistry("alwaysFrameOnTop").equals("true"))
		{
			chkAlwayOnTop.setSelected(true);
			chkAlwayOnTop.setForeground(Color.RED);
			setAlwaysOnTop(true);
		}
		else
		{
			chkAlwayOnTop.setSelected(false);
			chkAlwayOnTop.setForeground(Color.BLACK);
			setAlwaysOnTop(false);
		}
		
		if(Regedit.getRegistry("showNotification").equals("true"))
		{	
			chkNotification.setSelected(true);
			chkNotification.setForeground(Color.RED);
		}
		else
		{
			chkNotification.setSelected(false);
			chkNotification.setForeground(Color.BLACK);
		}
			
		//레지스트리에 있는 기본 값 받아서 적용
		if(Regedit.isContains("posX") && Regedit.isContains("posY") && Regedit.isContains("Width") && Regedit.isContains("Height"))
			setBounds(Integer.valueOf(Regedit.getRegistry("posX")), Integer.valueOf(Regedit.getRegistry("posY")), Integer.valueOf(Regedit.getRegistry("Width")), Integer.valueOf(Regedit.getRegistry("Height")));
				
		//하나라도 없으면 재설정
		if(!Regedit.isContains("posX") || !Regedit.isContains("posY") || !Regedit.isContains("Width") || !Regedit.isContains("Height"))
		{
			setBounds(200, 400, 517, 343);
			Regedit.addRegistry("posX", String.valueOf(getX()));
			Regedit.addRegistry("posY", String.valueOf(getY()));
			Regedit.addRegistry("Width", String.valueOf(getWidth()));
			Regedit.addRegistry("Height", String.valueOf(getHeight()));
		}
		
	}

	/**
	 * 전송 될 메세지를 텍스트 입력창에서 알맞게 변환
	 */
	public void messageFormat()
	{
		// 전송버튼 눌렀을 경우
		// 메세지 입력없이 전송버튼만 눌렀을 경우
		if (txtInput.getText().equals(""))
		{
			return;
		}
		else if (txtInput.getText().length() > MaintenanceData.maxSendMessageChar)
		{
			append("■ 메세지는 최대 "+ MaintenanceData.maxSendMessageChar +"자 까지 전송 가능합니다. <BR><BR>");
			txtArea.setCaretPosition(txtArea.getDocument().getLength());
			return;
		}
		else if(txtInput.getText().contentEquals("/getFrameSize"))
		{
			append("● FrameSize : "+this.getSize().width+" * " + this.getSize().height + " <BR><BR>");
			txtInput.setText("");
			return;
		}
		else if(txtInput.getText().contentEquals("/getFramePosition"))
		{
			append("● FramePosition : "+this.getX()+" * " + this.getY() + " <BR><BR>");
			txtInput.setText("");
			return;
		}
		else
		{
			txtArea.setCaretPosition(txtArea.getDocument().getLength());
			transmitThread.sendMessage();
			txtInput.setText("");
		}
		
	}
	
	//수정 예정
	private void hyperlinkClickAction()
	{
		txtArea.addHyperlinkListener(new HyperlinkListener()
		{
			@Override
			public void hyperlinkUpdate(final HyperlinkEvent arg0)
			{
				if (arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					if (Desktop.isDesktopSupported())
					{
						
						Object btn[] = {"내려받기", "브라우저로 확인하기"};
						int sel = JOptionPane.showOptionDialog(null, "<HTML>파일로 내려 받으시겠습니까?<BR>또는 브라우저를 통해 확인 하시겠습니까?</HTML>", "첨부파일 확인", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, btn, btn[0]);
						
						
						if(sel == 0)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								@Override
								public void run()
								{
									SwingFileDownloadHTTP filedownloader = new SwingFileDownloadHTTP(MaintenanceData.serverIP, false);
									filedownloader.setURL(arg0.getURL().toString());
									filedownloader.setVisible(true);
								}
							});
						}
						else if(sel == 1)
						{
							
							try
							{
								Desktop.getDesktop().browse(arg0.getURL().toURI());
							} catch (IOException e)
							{
								e.printStackTrace();
							} catch (URISyntaxException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
	}

	/**
	 * editorPane에 append 수행
	 * 
	 * @param s
	 *            input String
	 */
	public void append(String str)
	{
		String s = null;
		try
		{
			
			try
			{
				s = new String(str.getBytes("UTF-8"), "UTF-8");
			} catch (UnsupportedEncodingException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Document doc = txtArea.getDocument();
			EditorKit kit = txtArea.getEditorKit();
			StringReader r = new StringReader(s);
			try
			{
				kit.read(r, doc, doc.getLength());
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(isTextAlwaysBottomPosition) txtArea.setCaretPosition(txtArea.getDocument().getLength());
			// Document doc = txtArea.getDocument();
			// doc.insertString(doc.getLength(), s, null);
		} catch (BadLocationException exc)
		{
			exc.printStackTrace();
		}
	}
	
	/**
	 * 이미지 확장용
	 * @param str
	 */
	public void appendImg(String str)
	{
		//hyperlinkActionSelector(1);
		append(str);
		//hyperlinkActionSelector(0);
	}
	
	/**
	 * 사용자 물음 창 확인
	 * @param title 제목
	 * @param message 문자열
	 * @return
	 */
	public boolean showUserChoiceMessage(String title, String message)
	{
		int sel = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
		if(sel == 0)
			return true;
		else
			return false;
	}

	/**
	 * 종료 수행 로직
	 */
	public void closeAction()
	{
		try
		{
			if(isRunning)
			{
				isRunning = false;
				
				calendar = Calendar.getInstance();
				
				if(!MaintenanceData.socket.isClosed())
				{
					transmitThread.sendMessage("191|" + hashKey + "|0|" + dateFormat.format(calendar.getTime()) + "|" + id + "|" + MaintenanceData.socket.getLocalAddress().getHostAddress().replace("32.1.", "*.*.") + "|quit");
					MaintenanceData.socket.close();
					//transmitThread.socket.close();
					transmitThread.bout.close();
				}
			}
			
		} catch (IOException ie)
		{
			ie.printStackTrace();
			
		} finally
		{
			System.exit(0);
		}
	}

	/**
	 * 프레임 창 올림
	 */
	public void currentViewing()
	{
		calendar = Calendar.getInstance();
		String str = "770|" + hashKey + "|0|" + dateFormat.format(calendar.getTime()) + "|" + id + "|" + MaintenanceData.socket.getLocalAddress().getHostAddress().replace("32.1.", "*.*.") + "|currentViewing";
		transmitThread.sendMessage(str);
	}

	/**
	 * 프레임 창 내림
	 */
	public void stopViewing()
	{
		calendar = Calendar.getInstance();
		String str = "771|" + hashKey + "|0|" + dateFormat.format(calendar.getTime()) + "|" + id + "|" + MaintenanceData.socket.getLocalAddress().getHostAddress().replace("32.1.", "*.*.") + "|windows down";
		transmitThread.sendMessage(str);
	}
	
	/**
	 * 메뉴 버튼 동작 메소드
	 * @param isSelected
	 */
	private void actionMenuButton(boolean isSelected)
	{
		if(isSelected)
		{
			btnMenu.setForeground(Color.RED);
			btnMenu.setText(">>");
			panelSideMenubar.setVisible(true);
		}
		else
		{
			if(btnMiniGame.isSelected())
			{
				panelMinigame.setVisible(false);
				btnMiniGame.setForeground(Color.BLACK);
				btnMiniGame.setSelected(false);
			}
			
			btnMenu.setForeground(Color.BLACK);
			btnMenu.setText("<<");
			panelSideMenubar.setVisible(false);
		}
	}

	private void actionAdminButton()
	{
		if(!isAdmin)
		{
			String password = JOptionPane.showInputDialog(this, "관리자 패스워드 입력", "관리자 패스워드 입력", JOptionPane.OK_CANCEL_OPTION);
			if(password != null && password.equals(MaintenanceData.adminPassword))
			{
				btnAdmin.setSelected(true);
				isAdmin = true;
				append("● 관리자 입니다.<BR><BR>");
				btnAdmin.setForeground(Color.RED);
			}
			else if(password == null)
			{
				btnAdmin.setSelected(false);
				return;
			}
			else
			{
				btnAdmin.setSelected(false);
				JOptionPane.showMessageDialog(this, "패스워드가 일치하지 않습니다.", "관리자 권한 획득 실패", JOptionPane.ERROR_MESSAGE);
			}
			
			//else
		}
		else if(isAdmin)
		{
			btnAdmin.setSelected(false);
			btnAdmin.setForeground(Color.BLACK);
			isAdmin = false;
			append("● 관리자 권한이 해제되었습니다.<BR><BR>");
		}
		else
		{
			
		}
	}
	
	private void actionLockButton()
	{
		if(lockPassword.equals(""))
		{
			if(showUserChoiceMessage("잠금화면 패스워드", "<HTML>잠금 해제 패스워드가 없습니다.<BR>입력하시겠습니까?</HTML>"))
			{
				lockPassword = JOptionPane.showInputDialog(this, "패스워드 설정", "잠금 해제 패스워드 입력", JOptionPane.INFORMATION_MESSAGE);
			}
			
		}
		else
		{
			if(showUserChoiceMessage("잠금화면 패스워드", "<HTML>잠금 해제 패스워드가 있습니다.<BR>변경하시겠습니까?</HTML>"))
			{
				lockPassword = JOptionPane.showInputDialog(this, "패스워드 설정", "변경 할 패스워드 입력", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		if(lockPassword == null)
			lockPassword = "";
		
		screenLock.showLockScreen();
		
	}
	
	private void actionMiniGameButton(boolean isSelected)
	{
		if(isSelected)
		{
			btnMiniGame.setForeground(Color.RED);
			panelMinigame.setVisible(true);
		}
		else
		{
			btnMiniGame.setForeground(Color.BLACK);
			panelMinigame.setVisible(false);
		}
		
	}
	
	/**
	 * 마우스 클릭 액션 수행
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == btnTransfer) // 전송 버튼
		{
			messageFormat();
		}
		else if (e.getSource() == btnClean) // 삭제 버튼
		{
			if(isAdmin)
			{
				if(showUserChoiceMessage("관리자 권한", "전체 사용자의 채팅창을 지우시겠습니까?"))
				{
					//사용자 채팅창 클린 플래그 전송
					transmitThread.sendMessage("163");
					txtArea.setText("");
				}
			}
			else
				txtArea.setText("");
				
		}
		else if (e.getSource() == btnExit) // 닫기버튼 눌렸을 경우
		{
			if(showUserChoiceMessage("종료하시겠습니까?",  "종료하시겠습니까?")) 
				closeAction();
		}
		//메뉴버튼
		else if (e.getSource() == btnMenu)
		{
			actionMenuButton(btnMenu.isSelected());
		}
		else if (e.getSource() == btnFileSend) // 파일 전송 버튼
		{
			try
			{
				new SwingFileUploadHTTP(this);
			} catch (Exception ie){}
		}
		
		else if (e.getSource() == btnAdmin) //어드민 버튼
		{
			actionAdminButton();
		}
		
		else if(e.getSource() == btnMiniGame) //미니게임 버튼
		{
			actionMiniGameButton(btnMiniGame.isSelected());
		}
		
		else if(e.getSource() == btnLockScreen) //화면 잠금 버튼
		{
			actionLockButton();
		}
		
		validate();
			
	}

	/**
	 * 키 입력 액션 수행
	 */
	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) messageFormat();
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) messageFormat();
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) messageFormat();
	}

	/**
	 * 프레임 동작 수행
	 */
	@Override
	public void componentHidden(ComponentEvent arg0)
	{
	}

	@Override
	public void componentMoved(ComponentEvent arg0)
	{
		Regedit.addRegistry("posX", String.valueOf(getX()));
		Regedit.addRegistry("posY", String.valueOf(getY()));
	}

	@Override
	public void componentResized(ComponentEvent arg0)
	{
		if(!screenLock.isLocked)
		{
			Regedit.addRegistry("Width", String.valueOf(getWidth()));
			Regedit.addRegistry("Height", String.valueOf(getHeight()));
		}
	}

	@Override
	public void componentShown(ComponentEvent arg0){}
	
	/**
	 * 예기치 않은 예외상황에서 클라이언트를 정상적으로 종료하기 위한 쓰레드
	 * @author Jeoxxiii
	 */
	private class ShutdownHook extends Thread
	{
		//private Thread target;
		
		@Override
		public void run()
		{
			System.out.println("ShutdownHook is Activated!");
			try
			{
				//ㅆ발 왜 안돼, 임시 비활성화
				//closeAction();
				//ShutdownHook 이 실행되고 나서 프로세서 비정상 종료되어
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
	}

}
