package Minigame.Games.Tetris;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

import Minigame.PanelMinigame;

public class Tetris extends JFrame
{
	
	HelpBoard helpBoard;
	
	public Tetris(PanelMinigame minigameFrame)
	{
		
		helpBoard = new HelpBoard();
		helpBoard.setPreferredSize(new Dimension(50, 400));
		add(helpBoard, BorderLayout.EAST);
		
		Board board = new Board(this, minigameFrame, helpBoard);
		add(board, BorderLayout.CENTER);
		board.start();

		setResizable(false);
		
		setSize(240, 400);
		setTitle("Tetris");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
}
