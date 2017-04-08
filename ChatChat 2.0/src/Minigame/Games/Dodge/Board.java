package Minigame.Games.Dodge;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import Minigame.PanelMinigame;

public class Board extends JPanel
{
	
	
	public Board(PanelMinigame minigameFrame)
	{
		
		
		
		addKeyListener(new TAdapter());
		setFocusable(true);
	}
	
	@Override
	protected void paintComponent(Graphics arg0)
	{
		super.paintComponent(arg0);
		
		
	}
	
	
	//키 입력 
	private class TAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent arg0)
		{
			// TODO Auto-generated method stub
			super.keyPressed(arg0);
		}
	}
	
}
