package Client.Transmit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLEncoder;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class TransferFile extends SwingWorker<Void, Void>
{
	private Socket s;
	JProgressBar progressBar;
	String file;

	public TransferFile(String host, int port, String file, JProgressBar bar)
	{
		try
		{
			this.progressBar = bar;
			s = new Socket(host, port);
			this.file = file;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public TransferFile(String host, int port, String file)
	{
		try
		{
			s = new Socket(host, port);
			sendFile(file);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendFile(String file) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		dos.writeUTF(file);
		
		int bytesRead = -1;
		long totalBytesRead = 0;
		int percentCompleted = 0;
		long fileSize = new File(file).length();
		byte[] buffer = new byte[2048];
		
		while ((bytesRead = fis.read(buffer)) != -1)
		{
			dos.write(buffer);
			totalBytesRead += bytesRead;
			System.out.println("클라이언트 -> 서버 파일 전송 " + totalBytesRead);
			percentCompleted = (int) (totalBytesRead * 100 / fileSize);
			setProgress(percentCompleted);
		}
		
		fis.close();
		dos.close();
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		sendFile(file);
		return null;
	}
	
	@Override
	protected void done()
	{
		System.out.println("done");
		if (!isCancelled())
		{
			JOptionPane.showMessageDialog(null, "File has been uploaded successfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
