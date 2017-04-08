package Minigame.Games.Snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import Client.Data.MaintenanceData;
import Minigame.PanelMinigame;
import Minigame.Utility.MinigameRankChecker;

public class Board extends JPanel implements ActionListener
{

	PanelMinigame minigameFrame;
	
	private final int B_WIDTH = 300;
	private final int B_HEIGHT = 300;
	private final int DOT_SIZE = 10;
	private final int ALL_DOTS = 900;
	private final int RAND_POS = 27;
	private final int DELAY = 140;
	
	private final int x[] = new int[ALL_DOTS];
	private final int y[] = new int[ALL_DOTS];
	
	private int dots;
	private int apple_x;
	private int apple_y;
	
	private boolean leftDirection = false;
	private boolean rightDirection = true;
	private boolean upDirection = false;
	private boolean downDirection = false;
	private boolean isDraw = false;
	
	private boolean inGame = true;
	private boolean isPause = false;
	
	private Timer timer;
	private Image ball;
	private Image apple;
	private Image leftHead;
	private Image rightHead;
	private Image upHead;
	private Image downHead;
	
	public Board(PanelMinigame minigameFrame)
	{
		this.minigameFrame = minigameFrame;
		//minigameFrame.setSize(B_WIDTH, B_HEIGHT);
		//minigameFrame.setResizable(false);
		
		setBackground(Color.BLACK);
		addKeyListener(new TAdapter());
		setFocusable(true);
		loadImages();
		init();
	}
	
	private void loadImages()
	{
		ball = new ImageIcon(Board.class.getClassLoader().getResource("dot.png")).getImage();
		apple = new ImageIcon(Board.class.getClassLoader().getResource("apple.png")).getImage();
		leftHead = new ImageIcon(Board.class.getClassLoader().getResource("lefthead.png")).getImage();
		rightHead = new ImageIcon(Board.class.getClassLoader().getResource("righthead.png")).getImage();
		upHead = new ImageIcon(Board.class.getClassLoader().getResource("uphead.png")).getImage();
		downHead = new ImageIcon(Board.class.getClassLoader().getResource("downhead.png")).getImage();
	}
	
	private void setHard()
	{
		if(dots < 10) timer.setDelay(120);
		else if(dots < 15) timer.setDelay(110);
		else if(dots < 20) timer.setDelay(100);
		else if(dots < 30) timer.setDelay(80);
		else if(dots < 40) timer.setDelay(75);
		else if(dots < 50) timer.setDelay(70);
		else if(dots < 60) timer.setDelay(65);
		else if(dots < 70) timer.setDelay(60);
		else if(dots < 80) timer.setDelay(55);
		else if(dots < 90) timer.setDelay(50);
		else if(dots < 100) timer.setDelay(45);
		else if(dots < 110) timer.setDelay(40);
		else if(dots < 120) timer.setDelay(35);
		else if(dots < 140) timer.setDelay(30);
		else if(dots < 160) timer.setDelay(25);
	}
	
	private void init()
	{
		inGame = true;
		dots = 3;
		
		for(int z = 0; z < dots; z++)
		{
			x[z] = 50 - z * 10;
			y[z] = 50;
		}
		
		locateApple();
		
		timer = new Timer(DELAY, this);
		timer.start();
		setHard();
	}
	
	private void reset()
	{
		inGame = false;
		isPause = false;
		timer.stop();
		
		loadImages();
		
		leftDirection = false;
		rightDirection = true;
		upDirection = false;
		downDirection = false;
		init();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		doDrawing(g);
	}
	
	private void doDrawing(Graphics g)
	{
		if(inGame)
		{
			
			drawPoint(g);
			
			
			
			for(int z=0; z < dots; z++)
			{
				if(z==0)
				{
					if(leftDirection) g.drawImage(leftHead, x[z], y[z], this);
					if(rightDirection) g.drawImage(rightHead, x[z], y[z], this);
					if(upDirection) g.drawImage(upHead, x[z], y[z], this);
					if(downDirection) g.drawImage(downHead, x[z], y[z], this);
					isDraw = true;
				}else {
					g.drawImage(ball, x[z], y[z], this);
				}
			}
			
			g.drawImage(apple, apple_x, apple_y, this);
			
			if(isPause) drawPauseScreen(g);
			if(dots < 5 && !isPause) drawHelpScreen(g);
			Toolkit.getDefaultToolkit().sync();			

		}else{
			gameOver(g);
		}
	}
	
	private void gameOver(Graphics g)
	{
		MinigameRankChecker michk = new MinigameRankChecker();
		if(MaintenanceData.isUpdateRank)
		{
			try
			{
				
				if(minigameFrame.clientFrame != null)
					michk.compareRank("snake", minigameFrame.clientFrame.id, dots-3);
				else
				{
					michk.compareRank("snake", minigameFrame.id, dots-3);
				}
				 
			} catch (IOException e){}
		}
		
		Font small = new Font("Helvetica", Font.BOLD, 20);
		FontMetrics metr = getFontMetrics(small);
		
		g.setColor(Color.RED);
		g.setFont(small);
		String msg = "Game Over";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 - metr.getHeight()-90);
		g.setColor(Color.WHITE);
		small = new Font("Helvetica", Font.BOLD, 14);
		metr = getFontMetrics(small);
		g.setFont(small);
		msg = "Press SPACE BAR to Restart";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 -80);
		
		
		if(MaintenanceData.isUpdateRank)
		{
			int pos=15;
			for(int i=0; i<michk.getMaxData(); i++)
			{
				if(i==6) break;
				g.drawString(i+1 + ". " + michk.getName(i) + "    " + michk.getPoint(i) ,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 - metr.getHeight()-40+pos);
				pos+=20;
			}
		}
		
		msg = "점수 : " + String.valueOf(dots - 3);
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT - (metr.getHeight()*2));
		
		quit();
	}
	
	private void drawHelpScreen(Graphics g)
	{
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);
	
		g.setColor(Color.WHITE);
		g.setFont(small);
		String msg = "[방향키] 방향 전환";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 - metr.getHeight());
		
		msg = "[SPACE BAR] 재시작";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
		
		msg = "[P], [ESC] 일시 정지";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 + metr.getHeight());
		
	}
	
	private void drawPauseScreen(Graphics g)
	{
		Font small = new Font("Helvetica", Font.BOLD, 20);
		FontMetrics metr = getFontMetrics(small);
		
		g.setColor(Color.WHITE);
		g.setFont(small);
		String msg = "Pause";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 - metr.getHeight());
	}
	
	private void drawPoint(Graphics g)
	{
		String msg = "점수 : " + String.valueOf(dots - 3);
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);
		
		g.setColor(Color.WHITE);
		g.setFont(small);
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, metr.getHeight());
		g.drawString("속도 : " + String.valueOf(300 - timer.getDelay()),  10, metr.getHeight());
	}
	
	private void checkApple()
	{
		if((x[0] == apple_x) && (y[0] == apple_y))
		{
			dots++;
			//증가된 점수에 따른 난이도 변경
			setHard();
			locateApple();
		}
	}
	
	private void move()
	{
		for(int z = dots; z>0; z--)
		{
			x[z] = x[(z-1)];
			y[z] = y[(z-1)];
		}
		
		if(leftDirection)
			x[0] -= DOT_SIZE;
		if(rightDirection)
			x[0] += DOT_SIZE;
		if(upDirection)
			y[0] -= DOT_SIZE;
		if(downDirection)
			y[0] += DOT_SIZE;
	}
	
	private void checkCollision()
	{
		for(int z = dots; z>0; z--)
		{
			if(z > 3 && (x[0] == x[z]) && (y[0] == y[z]))
					inGame = false;
		}
		
		if(y[0] >= B_HEIGHT-30)
			inGame = false;
		if(y[0] < 0)
			inGame = false;
		if(x[0] >= B_WIDTH-10)
			inGame = false;
		if(x[0] < 0)
			inGame = false;
		
		if(!inGame)
			timer.stop();
	}
	
	private void locateApple()
	{
		int r = (int) (Math.random() * RAND_POS);
		apple_x = ((r * DOT_SIZE));
		
		r = (int) (Math.random() * RAND_POS);
		apple_y = ((r * DOT_SIZE));
	}
	
	private void quit()
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		if(minigameFrame.clientFrame != null) minigameFrame.clientFrame.transmitThread.sendMessage("1200" + "|" + "Snake" + "|" + String.valueOf(dots-3) + "|" + dateFormat.format(calendar.getTime()) + "|" + minigameFrame.clientFrame.id);
	}


	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		
		if(inGame)
		{
			checkApple();
			if(!isPause) move();
			checkCollision();
		}
		
		repaint();
	}

	private class TAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			
			if((key == KeyEvent.VK_LEFT) && (!rightDirection) && isDraw && !isPause)
			{
				leftDirection = true;
				upDirection = false;
				downDirection = false;
			}
			
			if((key == KeyEvent.VK_RIGHT) && (!leftDirection) && isDraw && !isPause)
			{
				rightDirection = true;
				upDirection = false;
				downDirection = false;
			}
			
			if((key == KeyEvent.VK_UP) && (!downDirection) && isDraw && !isPause)
			{
				upDirection = true;
				leftDirection = false;
				rightDirection = false;
			}
			
			if((key == KeyEvent.VK_DOWN) && (!upDirection) && isDraw && !isPause)
			{
				downDirection = true;
				leftDirection = false;
				rightDirection = false;
			}
			
			if(key == KeyEvent.VK_SPACE)
			{
				reset();
			}
			
			if((key == KeyEvent.VK_P) || (key == KeyEvent.VK_ESCAPE))
			{
				if(isPause) isPause = false;
				else isPause = true;
			}
			
			isDraw=false;
		}
	}
	

	
}
