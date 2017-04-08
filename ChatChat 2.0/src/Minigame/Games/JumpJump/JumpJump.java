package Minigame.Games.JumpJump;

import javax.swing.JFrame;

import Minigame.PanelMinigame;

public class JumpJump extends JFrame
{
	
	public JumpJump(PanelMinigame minigameFrame)
	{
		setSize(500, 220);
		
		Board board = new Board(minigameFrame);
		add(board);
		
		setResizable(false);
		
		setTitle("JumpJump");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
}
