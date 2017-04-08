package Minigame.Games.Tetris;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import Client.Data.MaintenanceData;
import Minigame.PanelMinigame;
import Minigame.Games.Tetris.Shape.Tetrominoes;
import Minigame.Utility.MinigameRankChecker;

public class Board extends JPanel implements ActionListener, ComponentListener
{
	
	final int BoardWidth = 12;
	final int BoardHeight = 24;
	
	int frameSizeX;
	int frameSizeY;
	
	Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	public boolean isGhost = false;
	boolean isOver = false;
	int numLinesRemoved = 0;
	int numTetrominoDropCount = 0;
	int numCountGhostUse = 0;
	int curX = 0;
	int curY = 0;
	int ghostCurX = 0;
	int ghostCurY = 0;
	
	Shape curPiece;
	Shape ghostPiece;
	
	Tetrominoes[] board;
	
	PanelMinigame minigameFrame;
	
	Tetris tetrisFrame;
	HelpBoard helpBoard;
	
	long curTime;
	long defTime;
	long pauseTime;
	
	public Board(Tetris parent, PanelMinigame minigameFrame, HelpBoard helpBoard)
	{
		this.minigameFrame = minigameFrame;
		this.tetrisFrame = parent;
		this.helpBoard = helpBoard;
		
		parent.addComponentListener(this);
		
		setFocusable(true);
		
		curPiece = new Shape();	//현재 떨어지고 잇는 테트로미노
		ghostPiece = new Shape();	//고스트 테트로미노
		
		timer = new Timer(400, this);
		timer.start();
		
		frameSizeX = 200;
		frameSizeY = 400;
		
		board = new Tetrominoes[BoardWidth * BoardHeight];
		addKeyListener(new TAdapter());
		clearBoard();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		
		curTime = System.currentTimeMillis();
		helpBoard.leftTime = (defTime - curTime);
		if((float) (defTime - curTime) / 1000 < 0)
		{
			curPiece.setShape(Tetrominoes.NoShape);
			timer.stop();
			isStarted = false;
			isOver = true;
			repaint();
		}
		
		if((float) (defTime - curTime) / 1000 < 300)
			timer.setDelay(300);
		if((float) (defTime - curTime) / 1000 < 180)
			timer.setDelay(200);
		
		helpBoard.repaint();
		
		if(isFallingFinished)
		{
			isFallingFinished = false;
			newPiece();
		}else {
			oneLineDown();
		}
	}
	
	int squareWidth() {return (int) getSize().getWidth() / BoardWidth;}
	int squareHeight() {return (int) getSize().getHeight() / BoardHeight;}
	Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }
	
	public void start()
	{
		if(isPaused)
			return;
		
		curTime = System.currentTimeMillis();
		defTime = System.currentTimeMillis() + 600000;
		
		isStarted = true;
		isOver = false;
		isFallingFinished = false;
		isGhost = false;
		numCountGhostUse = 0;
		numLinesRemoved = 0;
		helpBoard.point = numLinesRemoved;
		clearBoard();
		helpBoard.cleanPieces();
		
		newPiece();
		timer.setDelay(400);
		timer.start();
	}
	
	public void pause()
	{
		if(!isStarted)
			return;
		
		isPaused = !isPaused;
		if(isPaused) 
		{
			pauseTime = System.currentTimeMillis();
			timer.stop();
		}else {
			timer.start();
			defTime += (System.currentTimeMillis() - pauseTime);
		}
		repaint();
	}
	
	private void exchangePiece()
	{
		
		if(helpBoard.tempPiece.getShape() == Tetrominoes.NoShape)
		{
			helpBoard.tempPiece.setShape(curPiece.getShape());
			newPiece();
		}
		else
		{
			if(tryExchange(helpBoard.tempPiece))
			{
				Shape tempShape = new Shape();
				tempShape.setShape(helpBoard.tempPiece.getShape());
				helpBoard.tempPiece.setShape(curPiece.getShape());
				curPiece.setShape(tempShape.getShape());
			}
				
		}
		
		helpBoard.repaint();
		
	}
	
	private boolean tryExchange(Shape newPiece)
	{
		for(int i = 0; i<4; ++i)
		{
			int x = curX + newPiece.x(i);
			int y = curY + newPiece.y(i);
			if(x < 0)
			{
				curX += newPiece.x(i)*-1;
				curX += 1;
			}
			if(x >= BoardWidth)
			{
				curX -= newPiece.x(i);
				curX -= 1;
			}
			if(y < 0 || y >= BoardHeight)
				return false;
			if(shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}
		
		return true;
	}
	
	public void reset()
	{
		start();
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();
		
		for(int i=0; i<BoardHeight; ++i)
		{
			for(int j=0; j<BoardWidth; ++j)
			{
				Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
				if(shape != Tetrominoes.NoShape)
					drawSquare(g, 0 + j * squareWidth(),
							boardTop + i * squareHeight(), shape);
			}
		}
		
		if(isGhost)
		{
			helpBoard.isGhost = true;
			drawGhostPiece(g);
		}
		else
			helpBoard.isGhost = false;
		
		if(curPiece.getShape() != Tetrominoes.NoShape)
		{
			for(int i=0; i<4; ++i)
			{
				int x = curX + curPiece.x(i);
				int y = curY - curPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(),
						boardTop + (BoardHeight - y - 1) * squareHeight(),
						curPiece.getShape());
			}
		}
		
		if(numTetrominoDropCount < 3 && !isPaused && !isOver) drawHelpScreen(g);
		
		if(isOver)
			drawGameOverScreen(g);
		
		if(isPaused)
			drawPauseScreen(g);
		
		g.setColor(Color.RED);
		g.drawLine(0, BoardHeight, frameSizeX, BoardHeight);
		
		helpBoard.repaint();
		
	}
	
	private void drawGameOverScreen(Graphics g)
	{
		
		
		MinigameRankChecker michk = new MinigameRankChecker();
		if(MaintenanceData.isUpdateRank)
		{
			try
			{
				
				if(minigameFrame.clientFrame != null)
					michk.compareRank("tetris", minigameFrame.clientFrame.id, getPoint());
				else
				{
					michk.compareRank("tetris", minigameFrame.id, getPoint());
				}
				 
			} catch (IOException e){}
		}
		
		Font small = new Font("Helvetica", Font.BOLD, 20);
		FontMetrics metr = getFontMetrics(small);
		
		g.setColor(Color.RED);
		g.setFont(small);
		String msg = "";
		if((float) (defTime - curTime) / 1000 < 0) msg = "Time Over";
		else msg = "Game Over";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 - metr.getHeight()-90);
		g.setColor(Color.BLACK);
		small = new Font("Helvetica", Font.BOLD, 14);
		metr = getFontMetrics(small);
		g.setFont(small);
		msg = "Press ENTER to Restart";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 -80);
		
		
		if(MaintenanceData.isUpdateRank)
		{
			int pos=15;
			for(int i=0; i<michk.getMaxData(); i++)
			{
				if(i==6) break;
				g.drawString(i+1 + ". " + michk.getName(i) + "    " + michk.getPoint(i) ,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 - metr.getHeight()-40+pos);
				pos+=20;
			}
		}
		
		small = new Font("Helvetica", Font.BOLD, 10);
		g.setFont(small);
		metr = getFontMetrics(small);
		
		msg = "Points +";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight() * 6);
		
		msg = String.valueOf(helpBoard.point);
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight() * 7);
		
		msg = "Penalty -";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight() * 8);
		
		msg = String.valueOf(helpBoard.ghostUsed);
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight() * 9);
		
		small = new Font("Helvetica", Font.BOLD, 11);
		g.setFont(small);
		metr = getFontMetrics(small);
		
		msg = "Point Result";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight() * 9);
		
		msg = String.valueOf(helpBoard.point - helpBoard.ghostUsed);
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight() * 10);
		
		quit();
		
		
	}
	
	private void quit()
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		if(minigameFrame.clientFrame != null) minigameFrame.clientFrame.transmitThread.sendMessage("1200" + "|" + "Tetris" + "|" + String.valueOf(numLinesRemoved) + "|" + dateFormat.format(calendar.getTime()) + "|" + minigameFrame.clientFrame.id);
	}
	
	private void drawHelpScreen(Graphics g)
	{
		Font small = new Font("Helvetica", Font.BOLD, 11);
		FontMetrics metr = getFontMetrics(small);
	
		g.setColor(Color.BLACK);
		g.setFont(small);
		
		String msg = "[방향키 ← →] 블럭 이동";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 - metr.getHeight()*4);
		
		msg = "[방향키 ↑] 블럭 회전";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 - metr.getHeight()*3);
		
		msg = "[SPACE BAR] 블럭 한번에 내리기";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 - metr.getHeight()*2);
		
		msg = "[↓] 블럭 빨리 내리기";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 - metr.getHeight());
		
		msg = "[ENTER] 다시 시작";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2);
		
		msg = "[ESC][P] 일시 정지";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight()*1);
		
		msg = "[SHIFT] 블럭 저장 하기";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight()*2);
		
		msg = "[G] 고스트 블럭 사용 (패널티)";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight()*3);
	}
	
	private void drawPauseScreen(Graphics g)
	{
		Font small = new Font("Helvetica", Font.BOLD, 20);
		FontMetrics metr = getFontMetrics(small);
		
		g.setColor(Color.BLACK);
		g.setFont(small);
		
		String msg = "Pause";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 - metr.getHeight());
		
		small = new Font("Helvetica", Font.BOLD, 10);
		g.setFont(small);
		metr = getFontMetrics(small);
		
		msg = "Points";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight());
		
		msg = String.valueOf(helpBoard.point);
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight() * 2);
		
		msg = "Panalty";
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight() * 4);
		
		msg = String.valueOf(helpBoard.ghostUsed);
		g.drawString(msg,  (frameSizeX - metr.stringWidth(msg) - 50) / 2, frameSizeY / 2 + metr.getHeight() * 5);

	}
	
	private void dropDown()
	{
		int newY = curY;
		while(newY > 0)
		{
			if(!tryMove(curPiece, curX, newY - 1))
				break;
			--newY;
		}
		pieceDropped();
	}

	private void oneLineDown()
	{
		if(!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}
	
	private void clearBoard()
	{
		for(int i=0; i<BoardHeight * BoardWidth; ++i)
			board[i] = Tetrominoes.NoShape;
	}
	
	private void pieceDropped()
	{
		for(int i=0; i<4; ++i)
		{
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BoardWidth) + x] = curPiece.getShape();
		}
		removeFullLines();
		
		if(!isFallingFinished)
			newPiece();
	}
	
	private void newPiece()
	{
		numTetrominoDropCount++;
		
		curPiece.setShape(helpBoard.nextPiece.getShape());
		helpBoard.nextPiece.setRandomShape();
		helpBoard.repaint();
		
		curX = BoardWidth / 2 + 1;
		curY = BoardHeight - 1 + curPiece.minY();
		
		if(!tryMove(curPiece, curX, curY))
		{
			curPiece.setShape(Tetrominoes.NoShape);
			timer.stop();
			isStarted = false;
			isOver = true;
			repaint();
		}
	}
	
	private boolean tryMove(Shape newPiece, int newX, int newY)
	{
		for(int i = 0; i<4; ++i)
		{
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			if(x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			if(shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}
		
		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}
	
	private void drawGhostPiece(Graphics g)
	{
		
		ghostPiece = curPiece;
		
		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();
		
		int newY = curY;
		while(newY > 0)
		{
			if(!tryGhostPieceMove(ghostPiece, curX, newY - 1))
				break;
			--newY;
		}
		
		if(ghostPiece.getShape() != Tetrominoes.NoShape)
		{
			for(int i=0; i<4; ++i)
			{
				int x = ghostCurX + ghostPiece.x(i);
				int y = ghostCurY - ghostPiece.y(i);
				drawGhostSquare(g, 0 + x * squareWidth(),
						boardTop + (BoardHeight - y - 1) * squareHeight(),
						ghostPiece.getShape());
			}
		}

		
	}
	
	private boolean tryGhostPieceMove(Shape newGhostPiece, int newX, int newY)
	{
		for(int i = 0; i<4; ++i)
		{
			int x = newX + ghostPiece.x(i);
			int y = newY - ghostPiece.y(i);
			if(x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false;
			if(shapeAt(x, y) != Tetrominoes.NoShape)
				return false;
		}
		
		ghostPiece = newGhostPiece;
		ghostCurX = newX;
		ghostCurY = newY;
		repaint();
		return true;
	}
	
	private void drawGhostSquare(Graphics g, int x, int y, Tetrominoes shape)
	{
		
		Color color = Color.BLACK;
		
		g.setColor(color);
		g.fillRect(x+1, y+1, squareWidth()-2, squareHeight()-2);
		
		g.setColor(color.brighter());
		g.drawLine(x, y+squareHeight() - 1,  x,  y);
		g.drawLine(x, y, x + squareWidth() - 1, y);
		
		g.setColor(color.darker());
		g.drawLine(x+1, y+squareHeight()-1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() -1, x+squareWidth() - 1, y+1);
	}
	
	private void removeFullLines()
	{
		int numFullLines = 0;
		
		for (int i=BoardHeight -1; i >= 0; --i)
		{
			boolean lineisFull = true;
			
			for(int j=0; j<BoardWidth; ++j)
			{
				if(shapeAt(j, i) == Tetrominoes.NoShape)
				{
					lineisFull = false;
					break;
				}
			}
			
			if(lineisFull)
			{
				numFullLines++;
				for(int k = i; k<BoardHeight - 1; ++k)
				{
					for(int j = 0; j<BoardWidth; ++j)
						board[(k*BoardWidth) + j] = shapeAt(j, k+1);
				}
			}
		}
		
		if(numFullLines > 0)
		{
			numLinesRemoved += numFullLines;
			helpBoard.point = getPointAdd(numFullLines);
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NoShape);
			repaint();
		}
	}
	
	private int getPointAdd(int numFullLines)
	{
		int sum = helpBoard.point;
		
		for(int i = 0 ; i<numFullLines; i++)
		{	
			if(isGhost) helpBoard.ghostUsed += numFullLines * 50;
			sum += numFullLines * 100;
		}
		
		return sum;
	}
	
	private int getPoint()
	{
		return helpBoard.point - helpBoard.ghostUsed;
	}
	
	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape)
	{
		Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102),
				new Color(102, 204, 102), new Color(102, 102, 204),
				new Color(204, 204, 102), new Color(204, 102, 204),
				new Color(102, 204, 204), new Color(218, 170, 0)
		};
		
		Color color = colors[shape.ordinal()];
		
		g.setColor(color);
		g.fillRect(x+1, y+1, squareWidth()-2, squareHeight()-2);
		
		g.setColor(color.brighter());
		g.drawLine(x, y+squareHeight() - 1,  x,  y);
		g.drawLine(x, y, x + squareWidth() - 1, y);
		
		g.setColor(color.darker());
		g.drawLine(x+1, y+squareHeight()-1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() -1, x+squareWidth() - 1, y+1);
	}

	class TAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			
			if(!isStarted && (e.getKeyCode() == KeyEvent.VK_ENTER))
				reset();
			
			if(!isStarted || curPiece.getShape() == Tetrominoes.NoShape)
			{
				return;
			}

			int keycode = e.getKeyCode();
			
			if(keycode == 'p' || keycode == 'P' || keycode == KeyEvent.VK_ESCAPE)
			{
				pause();
				return;
			}
			
			if(isPaused)
				return;
			
			switch(keycode)
			{
				case KeyEvent.VK_LEFT:
					tryMove(curPiece, curX - 1, curY);
					break;
				case KeyEvent.VK_RIGHT:
					tryMove(curPiece, curX + 1, curY);
					break;
				case KeyEvent.VK_DOWN:
					oneLineDown();
					break;
				case KeyEvent.VK_UP:
					//tryMove(curPiece.rotateLeft(), curX, curY);
					tryMove(curPiece.rotateRight(), curX, curY);
					if(isGhost) tryGhostPieceMove(ghostPiece.rotateRight(), ghostCurX, ghostCurY);
					break;
				case KeyEvent.VK_SPACE:
					dropDown();
				case 'd':
				case 'D':
					break;
				case KeyEvent.VK_ENTER:
					reset();
					break;
				case KeyEvent.VK_SHIFT:
					exchangePiece();
					break;
				case 'g':
				case 'G':
					if(isGhost) isGhost = false;
					else 
					{
						helpBoard.ghostUsed += 300;
						isGhost = true;
					}
					break;
				
			}
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0)
	{
		frameSizeX = tetrisFrame.getSize().width;
		frameSizeY = tetrisFrame.getSize().height;
		
		System.out.println("frame Size changed : " + frameSizeX + " * " + frameSizeY);
	}

	@Override
	public void componentShown(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
}
