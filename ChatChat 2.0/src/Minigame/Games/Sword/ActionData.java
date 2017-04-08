package Minigame.Games.Sword;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import Client.Data.MaintenanceData;

public class ActionData extends JPanel
{
	
	public final int MIN_LEVEL = 1;
	public final int MAX_LEVEL = 21;
	
	int curLevel = 0;
	int pastLevel = 0;
	boolean isSuccess = false;
	int Percent = 0;
	
	public ActionData(int curLevel)
	{
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
		
		setVisible(true);
		
		this.curLevel = curLevel;
		pastLevel = curLevel;
		
		doAction();		
		
		setComponentsFromAction();
		
	}
	
	public ActionData(int curLevel, int goalLevel, boolean sel)
	{
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
		
		setVisible(true);
		
		this.curLevel = curLevel;
		pastLevel = curLevel;
		
		if(sel)
		{
			setBackground(new Color(181, 187, 143));
			
			JLabel label[] = new JLabel[3];
			
			label[0] = new JLabel("강화권 사용 성공!");
			label[1] = new JLabel("현재 등급 : " + goalLevel);
			label[2] = new JLabel(pastLevel + " → " + goalLevel);
			
			for(int i=0; i<3; i++)
			{
				label[i].setFont(new Font("맑은 고딕", Font.BOLD, 18));
				label[i].setForeground(Color.WHITE);
				add(label[i]);
			}
			
			isSuccess = true;
		}
		else
		{
			setBackground(new Color(208, 67, 73));
			
			JLabel label[] = new JLabel[2];
			
			label[0] = new JLabel("강화권 사용에 실패했습니다!");
			label[1] = new JLabel("현재 등급 : " + curLevel);
			
			for(int i=0; i<2; i++)
			{
				label[i].setFont(new Font("맑은 고딕", Font.BOLD, 18));
				label[i].setForeground(Color.WHITE);
				add(label[i]);
			}
			
			isSuccess = false;
		}
		
	}
	
	private void doAction()
	{
		if(getRandom())
		{
			curLevel++;
			isSuccess = true;
		}
		else
		{
			failResponse();
			isSuccess = false;
		}
	}
	
	private void setComponentsFromAction()
	{
		if(isSuccess)
			setBackground(new Color(181, 187, 143));
		else
			setBackground(new Color(208, 67, 73));
		
		JLabel label[] = new JLabel[4];
		
		String suc = null;
		if(isSuccess)
			suc = "성공!";
		else
			suc = "실패!";
		
		label[0] = new JLabel(suc);
		label[1] = new JLabel("현재 등급 : " + curLevel);
		label[2] = new JLabel(pastLevel + " → " + curLevel);
		label[3] = new JLabel(getHard(pastLevel)*100 + " %");
		
		for(int i=0; i<4; i++)
		{
			label[i].setFont(new Font("맑은 고딕", Font.BOLD, 18));
			label[i].setForeground(Color.WHITE);
			add(label[i]);
		}
	}
	
	public boolean getSuccess()
	{
		return isSuccess;
	}
	
	public int getLevel()
	{
		return curLevel;
	}
	
	private boolean getRandom()
	{
		if(Math.random() < getHard(pastLevel))
			return true;
		else
			return false;
	}
	
	public double getHard(int pastLevel)
	{
		if(pastLevel == 1)
			return 1;
		else if (pastLevel == 2)
			return 1;
		else if (pastLevel == 3)
			return 1;
		else if (pastLevel == 4)
			return 1;
		else if (pastLevel == 5)
			return 0.9;
		else if (pastLevel == 6)
			return 0.8;
		else if (pastLevel == 7)
			return 0.75;
		else if (pastLevel == 8)
			return 0.7;
		else if (pastLevel == 9)
			return 0.6;
		else if (pastLevel == 10)
			return 0.5;
		else if (pastLevel == 11)
			return 0.4;
		else if (pastLevel == 12)
			return 0.25;
		else if (pastLevel == 13)
			return 0.2;
		else if (pastLevel == 14)
			return 0.1;
		else if (pastLevel == 15)
			return 0.08;
		else if (pastLevel == 16)
			return 0.06;
		else if (pastLevel == 17)
			return 0.04;
		else if (pastLevel == 18)
			return 0.02;
		else if (pastLevel == 19)
			return 0.01;
		else if (pastLevel == 20)
			return 0.005;

		else return 0.001;
	}
	
	private void failResponse()
	{
		if(curLevel == 5)
			curLevel -= 1;
		else if(curLevel == 6)
			curLevel -= 1;
		else if(curLevel == 7)
			curLevel -= 1;
		else if(curLevel == 8)
			curLevel -= 3;
		else if(curLevel == 9)
			curLevel -= 3;
		else if(curLevel == 10)
			curLevel -= 3;
		else if(curLevel == 11)
			curLevel -= 10;
		else if(curLevel == 12)
			curLevel -= 11;
		else if(curLevel == 13)
			curLevel -= 12;
		else if(curLevel == 14)
			curLevel -= 13;
		else if(curLevel == 15)
			curLevel -= 14;
		else if(curLevel == 16)
			curLevel -= 15;
		else if(curLevel == 17)
			curLevel -= 16;
		else if(curLevel == 18)
			curLevel -= 17;
		else if(curLevel == 19)
			curLevel -= 18;
		else if(curLevel == 20)
			curLevel -= 19;
	}
	
}
