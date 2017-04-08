package Minigame.Games.Sword;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SuccessPanel extends JPanel
{
	int curLevel;
	
	public SuccessPanel(int curLevel)
	{
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
		
		setVisible(true);
		
		this.curLevel = curLevel;
		
		setBackground(new Color(254, 223, 176));
		
		JLabel label[] = new JLabel[2];
		
		label[0] = new JLabel("목표 달성!");
		label[1] = new JLabel("현재 등급 : " + curLevel);
		
		for(int i=0; i<2; i++)
		{
			label[i].setFont(new Font("맑은 고딕", Font.BOLD, 18));
			label[i].setForeground(new Color(101, 27, 42));
			add(label[i]);
		}
		
	}
	
}
