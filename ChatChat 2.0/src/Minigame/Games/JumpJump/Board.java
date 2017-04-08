package Minigame.Games.JumpJump;

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
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import Client.Data.MaintenanceData;
import Minigame.PanelMinigame;
import Minigame.Utility.MinigameRankChecker;

public class Board extends JPanel implements ActionListener
{

	PanelMinigame minigameFrame;
	
	private final int B_WIDTH = 500;
	private final int B_HEIGHT = 220;
	
	
	private final int DELAY = 40;
	private int JUMP_HEIGHT = 120;
	private final int JUMP_SPEED = 8;
	
	private int hurdleSpeed = 5;
	private int point = 0;
	
	private final int MAX_HURDLE = 10;
	private final int MAX_WOOD = 10;
	
	private int X;
	private int Y;
	private int sizeX;
	private int sizeY;
	
	private int hurdleX[] = new int[MAX_HURDLE];
	private int hurdleY = 162;
	private int hurdleSizeX;
	private int hurdleSizeY; 
	
	private int woodSpeed = 2;
	private int woodX[] = new int[MAX_WOOD];
	private int woodY = 150;
	private int woodSizeX;
	private int woodSizeY;
	
	
	//현재 움직이고 있는 장애물
	private boolean isMovingHurdle[] = new boolean[MAX_HURDLE];
	
	private boolean isMovingWood[] = new boolean[MAX_WOOD];
	
	//공 상태 관련
	private boolean isJumped = false;
	private boolean isDroped = true;
	private int leastSkillTime = 0;
	private int leastSkillGoalTimes = 0;
	private boolean isCanUseSkill = true;
	private boolean isGod = false;
	
	
	//게임 상태 관련
	private boolean inGame = true;
	private boolean isPause = false;
	private long pauseTime;
	private boolean isPassedFirstHurdle = false;
	private boolean isDrawRectangleBox = false;
	private boolean isDrawActionBox = false;
	
	private Timer timer;
	
	private Image ball;
	private Image eball;
	private Image gball;
	private Image oriball;
	private Image hurdle;
	private Image woods;
	
	long curTime;
	long defTime;
	long stdDefTime;
	
	long curWoodTime;
	long defWoodTime;
	long stdWoodDefTime;
	
	public Board(PanelMinigame minigameFrame)
	{
		this.minigameFrame = minigameFrame;
		
		setBackground(Color.BLACK);
		addKeyListener(new TAdapter());
		setFocusable(true);
		loadImages();
		init();
		
		//minigameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void loadImages()
	{
		ball = new ImageIcon(Board.class.getClassLoader().getResource("ball.png")).getImage();
		oriball = new ImageIcon(Board.class.getClassLoader().getResource("ball.png")).getImage();
		eball = new ImageIcon(Board.class.getClassLoader().getResource("eball.png")).getImage();
		gball = new ImageIcon(Board.class.getClassLoader().getResource("gball.png")).getImage();
		woods = new ImageIcon(Board.class.getClassLoader().getResource("wood.png")).getImage();
		
		hurdle = new ImageIcon(Board.class.getClassLoader().getResource("hurdle.png")).getImage();
		
		//이미지 크기 저장
		sizeX = ball.getWidth(null);
		sizeY = ball.getHeight(null);
		hurdleSizeX = hurdle.getWidth(null);
		hurdleSizeY = hurdle.getHeight(null);
		woodSizeX = woods.getWidth(null);
		woodSizeY = woods.getHeight(null);
		
	}
	
	private void init()
	{
		inGame = true;
		isDrawActionBox = false;
		hurdleSpeed = 5;
		leastSkillTime = 0;
		leastSkillGoalTimes = 5;
		isCanUseSkill = true;
		
		locateBall();
		locateHurdle();
		locateWood();
		
		timer = new Timer(DELAY, this);
		timer.start();
		
		curTime = System.currentTimeMillis();
		defTime = System.currentTimeMillis();
		stdDefTime = setHurdleLocateTime();
		
		curWoodTime = System.currentTimeMillis();
		defTime = System.currentTimeMillis();
		stdDefTime = setHurdleLocateTime();
	}
	
	private void reset()
	{
		for(int i=0; i<MAX_HURDLE; i++)
		{
			hurdleX[i] = 500;
			isMovingHurdle[i] = false;
			woodX[i] = 500;
			isMovingWood[i] = false;
		}
		
		inGame = false;
		isPassedFirstHurdle = false;
		timer.stop();
		point = 0;
		
		loadImages();
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
			
			drawGameScreen(g);
			drawPoint(g);
			
			//정지, 설명 출력
			if(isPause) drawPauseScreen(g);
			if(!isPassedFirstHurdle && !isPause) drawHelpScreen(g);
			
			Toolkit.getDefaultToolkit().sync();
		}else{
			gameOver(g);
		}
	}
	
	//게임 화면 출력
	private void drawGameScreen(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.drawLine(0, 170, 500, 170);
		
		for(int i=0; i<MAX_WOOD; i++)
		{
			if(isMovingWood[i])
			{
				if(isDrawRectangleBox) g.drawString(String.valueOf(i), woodX[i]+4,woodY-5);
				g.drawImage(woods, woodX[i], woodY, this);
				if(isDrawRectangleBox) drawRectangleBox(g, woodX[i], woodY, woodSizeX, woodSizeY);
			}
		}
		
		for(int i=0; i<MAX_HURDLE; i++)
		{
			if(isMovingHurdle[i])
			{
				if(isDrawRectangleBox) g.drawString(String.valueOf(i), hurdleX[i]+4,hurdleY+20);
				g.drawImage(hurdle, hurdleX[i], hurdleY, this);
				
				if(isDrawRectangleBox) drawRectangleBox(g, hurdleX[i], hurdleY, hurdleSizeX, hurdleSizeY);
				
				if(isDrawActionBox) 
				{
					g.setColor(Color.YELLOW);
					g.drawLine(hurdleX[i]+7, hurdleY, hurdleX[i], hurdleY+hurdleSizeY);
					g.drawLine(hurdleX[i]+8, hurdleY, hurdleX[i]+hurdleSizeX, hurdleY+hurdleSizeY);
					g.setColor(Color.WHITE);
				}
			}
		}
		
		//볼 이미지 출력
		g.drawImage(ball, X, Y, this);
		if(!isCanUseSkill) g.drawString(String.valueOf(leastSkillGoalTimes - leastSkillTime + 1), X + (sizeX/2)-3, Y-5);
		
		if(isDrawRectangleBox) drawRectangleBox(g, X, Y, sizeX, sizeY);
		if(isDrawActionBox)	
		{
			g.setColor(Color.YELLOW);
			g.drawLine(X+(sizeX/2)-3, Y+sizeY, X-3, Y+(sizeY/2));
			g.drawLine(X+(sizeX/2)+3, Y+sizeY, X+sizeX+3, Y+(sizeY/2));
		}
	}
	
	/**
	 * 네모 기준 박스 출력
	 */
	private void drawRectangleBox(Graphics g, int posX, int posY, int sizeX, int sizeY)
	{
		g.setColor(Color.GREEN);
		g.drawLine(posX, posY, posX+sizeX, posY);
		g.drawLine(posX, posY+sizeY, posX+sizeX, posY+sizeY);
		g.drawLine(posX, posY, posX, posY+sizeY);
		g.drawLine(posX+sizeX, posY, posX+sizeX, posY+sizeY);
		g.setColor(Color.WHITE);
	}
	
	//현재 게임 화면 출력과 함께 정지 화면 출력
	private void gameOver(Graphics g)
	{
		isDroped = true;
		isDrawActionBox = true;
		isCanUseSkill = true;
		
		MinigameRankChecker michk = new MinigameRankChecker();
		if(MaintenanceData.isUpdateRank)
		{
			try
			{
				if(minigameFrame.clientFrame != null)
					michk.compareRank("jumpjump", minigameFrame.clientFrame.id, point);
				else
				{
					michk.compareRank("jumpjump", minigameFrame.id, point);
				}
				 
			} catch (IOException e){}
		}
		
		drawGameScreen(g);
		
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);
	
		g.setColor(Color.WHITE);
		g.setFont(small);
		String msg = "Game Over";
		g.drawString(msg,  (B_WIDTH / 4) - metr.stringWidth(msg)/2, B_HEIGHT / 2 - metr.getHeight()-20);
		
		msg = "Press ENTER key to Restart";
		g.drawString(msg,   (B_WIDTH / 4) - metr.stringWidth(msg)/2, B_HEIGHT / 2-20);
		
		msg = "점수 : " + String.valueOf(point);
		g.drawString(msg,   (B_WIDTH / 2) - metr.stringWidth(msg), B_HEIGHT - (metr.getHeight()*2 - 5));
		
		if(MaintenanceData.isUpdateRank)
		{
			int pos=15;
			for(int i=0; i<michk.getMaxData(); i++)
			{
				if(i==6) break;
				g.drawString(i+1 + ". " + michk.getName(i) + "    " + michk.getPoint(i) ,  (B_WIDTH + metr.stringWidth(msg)) / 2 + 10, B_HEIGHT / 2 - metr.getHeight()-70+pos);
				pos+=20;
			}
		}
		
		quit();
	}
	
	private void drawHelpScreen(Graphics g)
	{
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);
	
		g.setColor(Color.WHITE);
		g.setFont(small);
		String msg = "[SPACE BAR] 점프";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 - metr.getHeight()-20);
		
		msg = "[ENTER] 다시 시작";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2-20);
		
		msg = "[ESC], [P] 일시 정지";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 + metr.getHeight()-20 );
		
		msg = "하늘색 공 상태 일 때 한번 더 점프 가능!";
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 + metr.getHeight()*2-20 );
		
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
		String msg = "점수 : " + String.valueOf(point);
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);
		
		g.setColor(Color.WHITE);
		g.setFont(small);
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) / 2, metr.getHeight());
		if( hurdleSpeed == 12) g.drawString("속도 : " + String.valueOf(hurdleSpeed) + " MAX",  10, metr.getHeight());
		else g.drawString("속도 : " + String.valueOf(hurdleSpeed),  10, metr.getHeight());
		
		if(isPause) msg = String.format("%.2f",(float) (stdDefTime - (pauseTime - defTime))/1000)  + " 초 후 새 장애물";
		else msg = String.format("%.2f",(float) (stdDefTime - (System.currentTimeMillis() - defTime))/1000)  + " 초 후 새 장애물";
		
		g.drawString(msg,  (B_WIDTH - metr.stringWidth(msg)) - 10, metr.getHeight());
		
		g.setColor(Color.RED);
		if(isGod) g.drawString("GOD",  (B_WIDTH - metr.stringWidth("GOD")) - 10, metr.getHeight()+20);
		
	}
	
	private void setHurdleSpeed()
	{
		if(point < 400)
			hurdleSpeed = 5;
		if(point > 700)
			hurdleSpeed = 6;
		if(point > 1000)
			hurdleSpeed = 7;
		if(point > 1300)
			hurdleSpeed = 8;
		if(point > 1600)
			hurdleSpeed = 9;
		if(point > 1900)
			hurdleSpeed = 10;
		if(point > 2200)
			hurdleSpeed = 11;
		if(point > 3000)
			hurdleSpeed = 12;
	}
	
	private int setStartTime()
	{
		if(point < 400)
			return 350;
		if(point > 700)
			return 150;
		if(point > 1000)
			return 125;
		if(point > 1300)
			return 75;
		if(point > 1600)
			return 25;
		if(point > 1900)
			return 15;
		if(point > 2200)
			return 10;
		if(point > 3000)
			return 5;
		
		return 600;
	}
	
	private int setEndTime()
	{
		if(point < 400)
			return 1700;
		if(point > 700)
			return 1500;
		if(point > 1000)
			return 1000;
		if(point > 1300)
			return 400;
		if(point > 1600)
			return 250;
		if(point > 1900)
			return 150;
		if(point > 2200)
			return 50;
		if(point > 3000)
			return 25;
		
		return 1600;
	}
	
	private long setHurdleLocateTime()
	{
		Random random = new Random();
		
		long start = setStartTime();
		long end = setEndTime();
		
		return start + (long)(random.nextDouble() * (end - start));
	}
	
	private void moveBall()
	{
		//캐릭터(공) 이동
				if(isJumped)
				{
					
					if(Y > JUMP_HEIGHT)
					{
						Y-=JUMP_SPEED;
					}
					else
						isJumped = false;
				}
				else if(Y < 150)
					Y+=JUMP_SPEED;
				
				//바닥에 닿았는지 여부
				if(Y == 150)
					isDroped = true;
	}
	
	private void moveHurdle()
	{
		
		//장애물 이동
		for(int i=0; i<MAX_HURDLE; i++)
		{
			
			if(isMovingHurdle[i])
			{
				//허들 위치 변경
				hurdleX[i] -= hurdleSpeed;
				
				//특정 시간이 지난 경우, 새로운 허들 생성
				if(getDefTime()) locateHurdle();
				
				//허들이 벽을 지나친 경우, 기존 허들 삭제
				if (hurdleX[i]+hurdleSizeX <= 0)
				{
					isPassedFirstHurdle = true;
					isMovingHurdle[i] = false;
					
					if(!isCanUseSkill) leastSkillTime++;
					if(leastSkillTime > leastSkillGoalTimes)
					{
						ball = oriball;
						isCanUseSkill = true;
						leastSkillTime = 0;
					}
					
				}
				
				if (hurdleX[i] <= X)
				{
					point++;
				}
				
			}

		}
	}
	
	private void moveWood()
	{
		
		//장애물 이동
		for(int i=0; i<MAX_WOOD; i++)
		{
			
			if(isMovingWood[i])
			{
				//허들 위치 변경
				woodX[i] -= woodSpeed;
				
				//특정 지점 지난 경우, 새로운 허들 생성
				if(getWoodDefTime()) locateWood();
				
				//허들이 벽을 지나친 경우, 기존 허들 삭제
				if (woodX[i]+woodSizeX <= 0)
					isMovingWood[i] = false;
				
			}

		}
	}

	//일정한 시간이 지났는지 검사하여, 지났으면 true 반환
	private boolean getWoodDefTime()
	{
		//현재 시간 연산
		if(isPause) return false;
		else curWoodTime = System.currentTimeMillis();
		
		Random random = new Random();
		
		long start = (long) 2000;
		long end = (long) 5000;
		
		if(  (curWoodTime - defWoodTime) > stdWoodDefTime)
		{
			defWoodTime = System.currentTimeMillis();
			stdWoodDefTime = start + (long)(random.nextDouble() * (end - start));
			return true;
		}
		else
			return false;
	}
	
	//일정한 시간이 지났는지 검사하여, 지났으면 true 반환
	private boolean getDefTime()
		{
			//현재 시간 연산
			if(isPause) return false;
			else curTime = System.currentTimeMillis();
			
			if(  (curTime - defTime) > stdDefTime)
			{
				defTime = System.currentTimeMillis();
				stdDefTime = setHurdleLocateTime();
				return true;
			}
			else
				return false;
		}

	private void checkCollision()
	{
		
		for(int i=0; i<MAX_HURDLE; i++)
		{
			//움직이는 허들만 포함
			if(isMovingHurdle[i])
			{
				//공이 허들에 닿는 지점과 허들에서 닿을 수 있는 지점 지정
				
				// X+(sizeX/2)  --> X
				// Y+sizeY --> Y+(sizeY/2)
				//hurdleX[i]+8 --> hurdleX[i]+hurdleSizeX
				//hurdleY --> hurdleY+hurdleSizeY
				
				//공의 좌측면
				for(int ballCrushX = X; ballCrushX<=X+(sizeX/2); ballCrushX++)
				{
					for(int ballCrushY = Y+sizeY; ballCrushY >= Y+(sizeY/2); ballCrushY--)
					{
						if((ballCrushX-X) == (ballCrushY-Y)/2)
						{
							//g.setColor(Color.GREEN);
							//g.drawLine( X, Y+(sizeY/2), ballCrushX, ballCrushY);
						}
						
						//허들의 우측면
						for(int hurdleCrushX = hurdleX[i] + (hurdleSizeX/2); hurdleCrushX >= hurdleX[i]; hurdleCrushX--)
						{
							for(int hurdleCrushY = hurdleY; hurdleCrushY <= hurdleY+hurdleSizeY; hurdleCrushY++)
							{
								//System.out.println(hurdleCrushX-hurdleX[i] + " " + (hurdleCrushY-hurdleY));
								if(hurdleCrushX-hurdleX[i] == (hurdleCrushY-hurdleY))
								{
									//g.setColor(Color.GREEN);
									//g.drawLine( hurdleX[i]  + (hurdleSizeX/2), hurdleY, hurdleCrushX  + (hurdleSizeX/2), hurdleCrushY);
									if(ballCrushX == hurdleCrushX  + (hurdleSizeX/2) && ballCrushY == hurdleCrushY)
										inGame = false;
								}
							}
						}
						
					}
				}

				// X+(sizeX/2) --> X+sizeX
				// Y+sizeY --> Y+(sizeY/2)
				// hurdleX[i]+7  --> hurdleX[i]
				// hurdleY --> hurdle+hurdleSizeY
				
				//공의 우측면
				for(int ballCrushX = X+(sizeX/2)+3; ballCrushX <= X+sizeX; ballCrushX++)
				{
					for(int ballCrushY = Y+sizeY; ballCrushY >= Y+(sizeY/2); ballCrushY--)
					{
						if((ballCrushX-X) == (ballCrushY-Y)/2+3)
						{
							//g.setColor(Color.YELLOW);
							//g.drawLine( X+sizeX,  Y+(sizeY/2), ballCrushX, ballCrushY);
						}
						
						//허들의 좌측면
						for(int hurdleCrushX = hurdleX[i]+(hurdleSizeX/2); hurdleCrushX <= hurdleX[i] + hurdleSizeX; hurdleCrushX++)
						{
							for(int hurdleCrushY = hurdleY; hurdleCrushY <= hurdleY + hurdleSizeY; hurdleCrushY++)
							{
								if(hurdleCrushX-hurdleX[i] == (hurdleCrushY-hurdleY))
								{
									//g.setColor(Color.YELLOW);
									//g.drawLine( hurdleX[i] + (hurdleSizeX/2), hurdleY, hurdleCrushX - (hurdleSizeX/2), hurdleCrushY);
									//System.out.println(hurdleCrushY);
									if(ballCrushX == hurdleCrushX && ballCrushY+3 == hurdleCrushY)
										inGame = false;
										
								}
							}
						}
						
						
						
						
					}
				}
			}
		}
		
		if(!inGame)
			timer.stop();
	}
	
	private void locateHurdle()
	{
		for(int i=0; i<MAX_HURDLE; i++)
		{
			if(!isMovingHurdle[i])
			{
				hurdleX[i] = 500;
				isMovingHurdle[i] = true;
				return;
			}
		}
	}
	
	private void locateWood()
	{
		for(int i=0; i<MAX_WOOD; i++)
		{
			if(!isMovingWood[i])
			{
				woodX[i] = 500;
				isMovingWood[i] = true;
				return;
			}
		}
	}
	
	private void locateBall()
	{
		X = 50;
		Y = 150;
		isJumped = false;
	}
	
	private void quit()
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		if(minigameFrame.clientFrame != null) minigameFrame.clientFrame.transmitThread.sendMessage("1200" + "|" + "JumpJump" + "|" + String.valueOf(point) + "|" + dateFormat.format(calendar.getTime()) + "|" + minigameFrame.clientFrame.id);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		
		if(inGame)
		{
			if(!isPause) 
			{
				setHurdleSpeed();
				moveBall();
				moveHurdle();
				moveWood();
			}
			//충돌 검사
			if(!isGod) checkCollision();
		}
		repaint();
	}

	private class TAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			
			if(key == KeyEvent.VK_SPACE && !isPause)
			{
				if(isDroped)
				{
					JUMP_HEIGHT = 120;
					isDroped = false;
					if(!isJumped) isJumped = true;
				}
				else if(isCanUseSkill && !isDroped)
				{
					JUMP_HEIGHT = 100;
					leastSkillGoalTimes = new Random().nextInt(5) + 3;
					isJumped = true;
					isCanUseSkill = false;
					ball = eball;
				}
				
			}
			
			if (key == KeyEvent.VK_ENTER)
			{
				reset();
			}
			
			if (inGame && (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE))
			{
				if(isPause) isPause = false;
				else 
				{
					pauseTime = System.currentTimeMillis();
					isPause = true;
				}
			}
			
			if(key == KeyEvent.VK_D)
			{
				if(isDrawRectangleBox) isDrawRectangleBox=false;
				else isDrawRectangleBox = true;
			}
			if(key == KeyEvent.VK_F)
			{
				if(isDrawActionBox) isDrawActionBox = false;
				else isDrawActionBox = true;
			}
			
			if(MaintenanceData.isDebug)
			{
				if(key == KeyEvent.VK_UP)
				{
					point += 100;
				}
				if(key == KeyEvent.VK_DOWN)
				{
					point -= 100;
				}
				if(key == KeyEvent.VK_G)
				{
					if(isGod)
						isGod = false;
					else
						isGod = true;
				}
			}
			
		}
	}
	

	
}
