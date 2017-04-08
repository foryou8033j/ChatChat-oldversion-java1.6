package Minigame.Games.Sword;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.Timer;

import Minigame.PanelMinigame;

public class Board extends JPanel implements ActionListener
{
	
	int topLevel = 0;
	int curLevel = 1;
	int SuccessTimes = 0;
	int FailTimes = 0;
	int TryedTimes = 0;
	
	boolean isGoal = false;
	
	JPanel panelStatus = new JPanel();
	String goalAry[] = {"13", "14", "15", "16", "17", "18", "19", "20", "21"};
	JComboBox comboGoalList = new JComboBox(goalAry);
	JLabel labelTopLevel = new JLabel("최고 : XX 강");
	JLabel labelLevel = new JLabel("현재 : XX 강");
	JLabel labelSuccess = new JLabel("성공 : XX 회");
	JLabel labelFail = new JLabel("실패 : XX 회");
	JLabel labelTryed = new JLabel("시도 : XX 회");
	JLabel labelHard = new JLabel("강화 확률 : XX %");
	
	JPanel pastList = new JPanel();
	JScrollPane scrollPanelPastList = new JScrollPane(pastList);
	
	JPanel panelActionBox = new JPanel();
	String aryCards[] = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
	JComboBox comboAryPowerCards = new JComboBox(aryCards);
	JButton buttonUseCard = new JButton("강화권 사용하기");
	JCheckBox checkShowSuccess = new JCheckBox("성공한 기록만 보기");
	JToggleButton checkAutoAction = new JToggleButton("자동 강화");
	JButton buttonAction = new JButton("강화 하기");
	JButton buttonReset = new JButton("다시 하기");
	JButton buttonHelp = new JButton("?");
	
	public Timer timer = new Timer(280, this);
	
	Sword sword = null;
	
	public Board(PanelMinigame minigameFrame, Sword sword)
	{
		
		setLayout(new BorderLayout());
		
		this.sword = sword;
		
		panelStatus.add(new JLabel("목표 강화 :"));
		comboGoalList.setSelectedIndex(3);
		panelStatus.add(comboGoalList);
		panelStatus.add(labelTopLevel);
		panelStatus.add(labelLevel);
		panelStatus.add(labelSuccess);
		panelStatus.add(labelFail);
		panelStatus.add(labelTryed);
		panelStatus.add(labelHard);
		panelStatus.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 6));
		
		add("North", panelStatus);
		add("Center", scrollPanelPastList);
		pastList.setLayout(new BoxLayout(pastList, BoxLayout.PAGE_AXIS));
		
		
		checkAutoAction.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JToggleButton toggleBox = (JToggleButton) arg0.getSource();
				
				if( toggleBox.isSelected() )
				{
					toggleBox.setForeground(Color.RED);
					toggleBox.setFont(new Font("맑은 고딕", Font.BOLD, 12));
					buttonAction.setEnabled(false);
					buttonReset.setEnabled(false);
					
				}
				else
				{
					toggleBox.setForeground(Color.BLACK);
					toggleBox.setFont(new Font("맑은 고딕", Font.BOLD, 12));
					buttonAction.setEnabled(true);
					buttonReset.setEnabled(true);
				}
			}
		});
		
		checkShowSuccess.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JToggleButton toggleBox = (JToggleButton) arg0.getSource();
				
				if( toggleBox.isSelected() )
				{
					toggleBox.setForeground(Color.RED);
					toggleBox.setFont(new Font("맑은 고딕", Font.BOLD, 12));
				}
				else
				{
					toggleBox.setForeground(Color.BLACK);
					toggleBox.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
				}
			}
		});
		
		comboAryPowerCards.setSelectedIndex(6);
		buttonUseCard.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		checkShowSuccess.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		checkAutoAction.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		buttonAction.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		buttonReset.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		buttonHelp.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		panelActionBox.add(comboAryPowerCards);
		panelActionBox.add(buttonUseCard);
		panelActionBox.add(checkShowSuccess);
		panelActionBox.add(checkAutoAction);
		panelActionBox.add(buttonAction);
		panelActionBox.add(buttonReset);
		panelActionBox.add(buttonHelp);
		
		add("South", panelActionBox);
		
		buttonUseCard.addActionListener(this);
		checkAutoAction.addActionListener(this);
		buttonAction.addActionListener(this);
		buttonReset.addActionListener(this);
		buttonHelp.addActionListener(this);
		
		scrollPanelPastList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanelPastList.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
		{
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0)
			{
				if(checkAutoAction.isSelected()) arg0.getAdjustable().setValue(arg0.getAdjustable().getMaximum());
			}
		});

		init();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if(curLevel == Integer.valueOf((String) comboGoalList.getSelectedItem()) && !isGoal)
			completeGoal();
		else if(curLevel == 21 && !isGoal)
			completeGoal();
			
		if(TryedTimes > 7000)
			pastList.removeAll();
		
		if(curLevel < Integer.valueOf((String) comboAryPowerCards.getSelectedItem()))
			buttonUseCard.setEnabled(true);
		else
			buttonUseCard.setEnabled(false);
		
		if(checkAutoAction.isSelected())
			initAction();

		if(arg0.getSource() == buttonAction)
			initAction();
		else if (arg0.getSource() == buttonReset)
			reset();
		else if (arg0.getSource() == buttonHelp)
			new HelpFrame(sword).setVisible(true);
		
		else if(arg0.getSource() == buttonUseCard)
		{
			pastList.add(Box.createRigidArea(new Dimension(0, 10)));
			if(curLevel < Integer.valueOf((String) comboAryPowerCards.getSelectedItem()))
			{
				pastList.add(new ActionData(curLevel, Integer.valueOf((String) comboAryPowerCards.getSelectedItem()),  true));
				curLevel = Integer.valueOf((String) comboAryPowerCards.getSelectedItem());
				SuccessTimes++;
				TryedTimes++;
				if(curLevel > topLevel)
					topLevel = curLevel;
			}
			else
			{
				pastList.add(new ActionData(curLevel, Integer.valueOf((String) comboAryPowerCards.getSelectedItem()),  false));
			}
			
			toBottom();
		}

		printData();
		
		
		pastList.revalidate();
	
		
	}
	
	private void init()
	{
		topLevel = 0;
		resetData();
		
		timer.start();
	}
	
	/**
	 * 최고 기록 제외하여 초기화
	 */
	private void reset()
	{
		resetData();
		
		checkAutoAction.setEnabled(true);
		buttonAction.setEnabled(true);
		timer.start();
	}
	
	/**
	 * 데이터 초기화
	 */
	private void resetData()
	{
		curLevel = 1;
		SuccessTimes = 0;
		FailTimes = 0;
		TryedTimes = 0;
		isGoal = false;
		
		pastList.removeAll();
	}
	
	private void printData()
	{
		labelTopLevel.setText("최고 : " + topLevel +" 강");
		labelLevel.setText("현재 : "  + curLevel + " 강");
		labelSuccess.setText("성공 : " + SuccessTimes + " 회");
		labelFail.setText("실패 : " + FailTimes + " 회");
		labelTryed.setText("시도 : " + TryedTimes + " 회");
		labelHard.setText("확률 : " + new ActionData(curLevel).getHard(curLevel)*100 + " %");
	}
	
	private void completeGoal()
	{
		pastList.add(Box.createRigidArea(new Dimension(0, 10)));
		pastList.add(new SuccessPanel(curLevel));
		checkAutoAction.setSelected(false);
		checkAutoAction.setForeground(Color.BLACK);
		checkAutoAction.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		checkAutoAction.setEnabled(false);
		buttonAction.setEnabled(false);
		buttonReset.setEnabled(true);
		isGoal = true;
		toBottom();
	}
	
	public int getLevel()
	{
		return curLevel;
	}
	
	/**
	 * 검 강화 시작
	 */
	private void initAction()
	{

		
		ActionData actionData = new ActionData(curLevel);
		
		if(checkShowSuccess.isSelected())
		{
			if(actionData.getSuccess())
			{
				pastList.add(Box.createRigidArea(new Dimension(0, 10)));
				pastList.add(actionData);
			}
		}
		else
		{
			pastList.add(Box.createRigidArea(new Dimension(0, 10)));
			pastList.add(actionData);
		}
		
		
		
		if(actionData.getSuccess())
		{
			curLevel++;
			SuccessTimes++;
		}
		else
		{
			curLevel = actionData.getLevel();
			FailTimes++;
		}
		
		if(curLevel > topLevel)
			topLevel = curLevel;
		
		TryedTimes++;
		
		
		toBottom();
	}
	
	private void toBottom()
	{
		pastList.revalidate();
		int height = (int) pastList.getPreferredSize().getHeight();
		Rectangle rect = new Rectangle(0, height, 10, 10);
		pastList.scrollRectToVisible(rect);
	}
	
	
}
