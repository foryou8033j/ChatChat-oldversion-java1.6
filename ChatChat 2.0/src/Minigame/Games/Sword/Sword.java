package Minigame.Games.Sword;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import Minigame.PanelMinigame;

public class Sword extends JFrame
{
	
	PanelMinigame panelMinigame = null;
	
	public Sword(PanelMinigame panelMinigame)
	{
		this.panelMinigame = panelMinigame;
		
		setSize(650, 320);
		
		final Board board = new Board(panelMinigame, this);
		add(board);
		
		setResizable(true);
		
		setTitle("강화 시뮬레이션");
		
		/**
		 *창을 종료 했다고 판단
		 */
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				System.out.println("종료 : " + board.getLevel());
				board.timer.stop();
			}
		});
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
}
