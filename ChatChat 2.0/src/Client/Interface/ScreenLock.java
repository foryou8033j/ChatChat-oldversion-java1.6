package Client.Interface;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Client.Data.MaintenanceData;

public class ScreenLock extends JPanel implements ActionListener
{
	
	ClientFrame clientFrame;
	JLabel labelTimer = new JLabel("## : ## : ## : ####", SwingConstants.CENTER);
	JButton btnBack = new JButton("돌아가기");
	
	//Thread timer = new Thread();
	Timer timer = new Timer(0, this);
	
	private int clientWidth = 0;
	private int clientHeight = 0;
	
	public boolean isLocked = false;
	
	public ScreenLock(ClientFrame clientFrame)
	{
		this.clientFrame = clientFrame;
		setLayout(new GridLayout(2, 0, 30, 0));
		add(labelTimer);
		add(btnBack);
		btnBack.addActionListener(this);
		
		labelTimer.setFont(new Font("맑은 고딕", Font.BOLD, 18));

		timer.start();
		
	}
	
	public void showLockScreen()
	{
		clientFrame.setTitle("내 군생활 얼마나 남았니 v" + MaintenanceData.curVersion);
		setVisible(true);
		clientFrame.currentClientLock = true;
		clientFrame.remove(clientFrame.scrollPane);
		clientFrame.add("Center",this);
		clientFrame.panelMember.setVisible(false);
		clientFrame.scrollPane.setVisible(false);
		clientFrame.txtInput.setVisible(false);
		clientFrame.panelSideMenubar.setVisible(false);
		clientFrame.panelMinigame.setVisible(false);
		clientFrame.panelLowerMenubar.setVisible(false);
		
		clientWidth = clientFrame.getWidth();
		clientHeight = clientFrame.getHeight();
		
		clientFrame.setSize(320, 160);
		
		isLocked = true;
	}
	
	public void hideLockScreen()
	{
		setVisible(false);
		clientFrame.remove(this);
		clientFrame.currentClientLock = false;
		clientFrame.add("Center", clientFrame.scrollPane);
		clientFrame.setTitle(MaintenanceData.getProgramName());
		clientFrame.panelMember.setVisible(true);
		clientFrame.scrollPane.setVisible(true);
		clientFrame.txtInput.setVisible(true);
		clientFrame.panelSideMenubar.setVisible(true);
		clientFrame.panelLowerMenubar.setVisible(true);
		clientFrame.setSize(clientWidth, clientHeight);
		
		if(clientFrame.btnMiniGame.isSelected())
			clientFrame.panelMinigame.setVisible(true);
		else
			clientFrame.panelMinigame.setVisible(false);
		
		isLocked = false;
		
	}
	
	private void getDefTime()
	{
		Calendar targetCalendar = Calendar.getInstance();
		
		Date date = new Date(MaintenanceData.dYear, MaintenanceData.dMonth-2, MaintenanceData.dDate, 10, 0, 0);
		
		long count = date.getTime() - System.currentTimeMillis();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM개월 dd일 HH시간 mm분 ss초 SSS");
		
		labelTimer.setText(dateFormat.format(count));
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		
		getDefTime();
		
		if(arg0.getSource() == btnBack)
		{
			if(clientFrame.lockPassword.equals(""))
			{
				hideLockScreen();
				return;
			}
			
			String inputPassword = JOptionPane.showInputDialog(this, "패스워드 입력", "패스워드를 입력하세요", JOptionPane.INFORMATION_MESSAGE);
			if(inputPassword != null && inputPassword.equals(clientFrame.lockPassword))
			{
				hideLockScreen();
			}
			else if(inputPassword == null || inputPassword.equals(""))
			{
				JOptionPane.showMessageDialog(this, "패스워드를 입력하세요.", "패스워드 불일치", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(this, "패스워드가 일치하지 않습니다.", "패스워드 불일치", JOptionPane.ERROR_MESSAGE);
			}
				
		}
		
	}
	
}

