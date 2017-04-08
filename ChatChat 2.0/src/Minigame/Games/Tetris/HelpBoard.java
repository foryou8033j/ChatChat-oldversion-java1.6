package Minigame.Games.Tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import Minigame.Games.Tetris.Shape.Tetrominoes;

public class HelpBoard extends JPanel
{
	
	final int BoardWidth = 5;
	final int BoardHeight = 30;
	
	public Shape nextPiece;
	public Shape tempPiece;
	
	int nextPieceX = 0;
	int nextPieceY = 0;
	
	int tempPieceX = 0;
	int tempPieceY = 0;
	
	int squareWidth() {return (int) getSize().getWidth() / BoardWidth;}
	int squareHeight() {return (int) getSize().getHeight() / BoardHeight;}
	
	public int point = 0;
	public int ghostUsed = 0;
	public boolean isGhost = false;
	
	public long leftTime;
	
	public HelpBoard()
	{
		leftTime = 0;
		nextPiece = new Shape();
		tempPiece = new Shape();
		
		nextPieceX = BoardWidth / 2;
		nextPieceY = BoardHeight - 1 + nextPiece.minY() - 3;
		
		tempPieceX = BoardWidth / 2;
		tempPieceY = BoardHeight - 1 + nextPiece.minY() - 10;
		
		setBorder(new BevelBorder(BevelBorder.RAISED));
		
		newPiece();
	}
	
	public void cleanPieces()
	{
		point = 0;
		ghostUsed = 0;
		
		nextPiece.setShape(Tetrominoes.NoShape);
		tempPiece.setShape(Tetrominoes.NoShape);
		newPiece();
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		Font small = new Font("Helvetica", Font.BOLD, 10);
		FontMetrics metr = getFontMetrics(small);
		g.setFont(small);
		
		g.setColor(Color.BLACK);
		String msg = "Next";
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 - metr.getHeight() * 11);
		
		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();
		
		nextPieceX = 2;
		nextPieceY = (getSize().height / 12) - 5;
		
		if(nextPiece.getShape() != Tetrominoes.NoShape)
		{
			for(int i=0; i<4; ++i)
			{
				int x = nextPieceX + nextPiece.x(i);
				int y = nextPieceY - nextPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(),
						boardTop + (BoardHeight - y - 1) * squareHeight(),
						nextPiece.getShape());
			}
		}
		
		g.setColor(Color.BLACK);
		msg = "Temp";
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 - metr.getHeight() * 5);
		
		if(tempPiece.getShape() != Tetrominoes.NoShape)
		{
			for(int i=0; i<4; ++i)
			{
				int x = tempPieceX + tempPiece.x(i);
				int y = tempPieceY - tempPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(),
						boardTop + (BoardHeight - y - 1) * squareHeight(),
						tempPiece.getShape());
			}
		}
		else
		{
			g.setColor(Color.RED);
			msg = "X";
			g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 - metr.getHeight() * 3);
		}
		
		small = new Font("Helvetica", Font.BOLD, 10);
		metr = getFontMetrics(small);
	
		g.setFont(small);
		g.setColor(Color.BLACK);
		g.drawLine(0, getSize().height / 2, 50, getSize().height / 2);
			
		
		msg = "Points";
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 + metr.getHeight() * 2);
		
		msg = String.valueOf(point);
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 + metr.getHeight() * 3);
		
		msg = "Ghost";
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 + metr.getHeight() * 5);
		msg = "penalty";
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 + metr.getHeight() * 6);
		
		msg = String.valueOf(ghostUsed);
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 + metr.getHeight() * 7);
		
		msg = "Ghost";
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 + metr.getHeight() * 9);
		msg = "Mode";
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 + metr.getHeight() * 10);
		
		if(isGhost)
		{
			g.setColor(Color.RED);
			msg = "ON";
		}
		else
		{
			msg = "OFF";
		}
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 + metr.getHeight() * 11);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
		//30초 남았을 때 강조
		//msg = String.format("%.2f", leftTime) + "s..";
		msg = dateFormat.format(leftTime);
		if((float) leftTime/1000 < 30)
			g.setColor(Color.RED);
		else
			g.setColor(Color.BLACK);
		g.drawString(msg,  (getSize().width - metr.stringWidth(msg)) / 2, getSize().height / 2 + metr.getHeight() * 12);
			
		
	}
	
	private void newPiece()
	{
		nextPiece.setRandomShape();
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
	
}
