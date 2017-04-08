package Minigame;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import Client.Interface.ClientFrame;
import Minigame.Games.Dodge.Dodge;
import Minigame.Games.JumpJump.JumpJump;
import Minigame.Games.Snake.Snake;
import Minigame.Games.Sword.Sword;
import Minigame.Games.Tetris.Tetris;

public class PanelMinigame extends JPanel implements Runnable, ActionListener
{
	
	public JButton initGameSnake = new JButton("뱀 게임");
	public JButton initGameJumpJump = new JButton("점프 점프");
	public JButton initGameDodge = new JButton("닷지");
	public JButton initGameTetris = new JButton("테트리스");
	public JButton initGameSpace = new JButton("강화 하기");

	public ClientFrame clientFrame;
	
	public String id = "TempUser";
	
	//채팅창에서 실행 될 경우
	public PanelMinigame(ClientFrame clientFrame)
	{
		this.clientFrame = clientFrame; 
		setComponents();
	}
	
	//단독으로 실행 될 경우
	public PanelMinigame(String id)
	{

		this.id = id;
		setComponents();
		
	}

	private void setComponents()
	{
		
		setLayout(new GridLayout(5, 1, 20, 3));
		add(initGameSnake);
		add(initGameJumpJump);
		add(initGameDodge);
		add(initGameTetris);
		add(initGameSpace);
		
		initGameSnake.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		initGameJumpJump.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		initGameDodge.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		initGameTetris.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		initGameSpace.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		
		initGameDodge.setEnabled(false);
		
		initGameSnake.addActionListener(this);
		initGameJumpJump.addActionListener(this);
		initGameDodge.addActionListener(this);
		initGameTetris.addActionListener(this);
		initGameSpace.addActionListener(this);
		
	}
	
	@Override
	public void run()
	{
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if(arg0.getSource() == initGameSnake)
		{
			Snake game = new Snake(this);
			game.setLocationRelativeTo(this);
			game.setVisible(true);
		}
		else if(arg0.getSource() == initGameJumpJump)
		{
			JumpJump game = new JumpJump(this);
			game.setLocationRelativeTo(this);
			game.setVisible(true);
		}
		
		else if(arg0.getSource() == initGameDodge)
		{
			Dodge game = new Dodge(this);
			game.setLocationRelativeTo(this);
			game.setVisible(true);
		}
		
		else if(arg0.getSource() == initGameTetris)
		{
			Tetris game = new Tetris(this);
			game.setLocationRelativeTo(this);
			game.setVisible(true);
		}
		
		else if(arg0.getSource() == initGameSpace)
		{
			Sword game = new Sword(this);
			game.setLocationRelativeTo(this);
			game.setVisible(true);
		}
	}
	
}


