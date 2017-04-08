package main;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Client.Data.MaintenanceData;
import Minigame.PanelMinigame;

public class MinigameTest
{
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		
		if(MaintenanceData.useCurrentOSTheme) UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new JFrame("미니게임천국");

		
		frame.setBounds(200, 200, 260, 250);
		frame.setResizable(false);
		if(MaintenanceData.isDebug) frame.add(new PanelMinigame("test"));
		else frame.add(new PanelMinigame(JOptionPane.showInputDialog(null, "이름 입력", "이름 입력")));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
}
