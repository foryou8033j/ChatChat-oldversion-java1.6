package Minigame.Games.Sword;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class HelpFrame extends JFrame
{
	
	JTextArea textPane;
	
	public HelpFrame(Sword sword)
	{
		
		setBounds(sword.getX() + sword.getWidth(), sword.getY(), 300, 500);
		
		textPane = new JTextArea();
		
		add(textPane);
		
		printPercent();

	}
	
	private void printPercent()
	{
		ActionData actionData = new ActionData(1);
		
		textPane.append("강화 확률\n\n");
		
		for(int i=1; i< actionData.MAX_LEVEL; i++)
		{
			String tmp = "Level " + (i+1) + " : " + actionData.getHard(i) * 100 + " %\n" ;
			textPane.append(tmp);
		}
		
		String tmp = "Level " + (actionData.MAX_LEVEL+1) + " : " + actionData.getHard(actionData.MAX_LEVEL) * 100 + " % [최고 강화]\n" ;
		textPane.append(tmp);
		
		
	}
	
	
}
