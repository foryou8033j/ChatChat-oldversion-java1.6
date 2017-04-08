package Minigame.Games.Snake;

import javax.swing.JFrame;

import Minigame.PanelMinigame;

public class Snake extends JFrame
{
	
	public Snake(PanelMinigame minigameFrame)
	{

		setSize(300, 300);
		
		Board board = new Board(minigameFrame);
		add(board);
		
		setResizable(false);
		
		setTitle("Snake");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
}
