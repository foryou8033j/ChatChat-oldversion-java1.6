package Client.Fileprotocol.Download;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import Client.Interface.SwingFileDownloadHTTP;

/**
 * 서버로 부터 파일 백그라운드로 다운로드 진행
 * 
 * @author www.codejava.net
 */
public class DownloadTask extends SwingWorker<Void, Void>
{
	private static final int BUFFER_SIZE = 4096;
	private String downloadURL;
	private String saveDirectory;
	private SwingFileDownloadHTTP gui;
	public boolean isDone = false;
	public boolean isError = false;

	public DownloadTask(SwingFileDownloadHTTP gui, String downloadURL, String saveDirectory)
	{
		this.gui = gui;
		this.downloadURL = downloadURL;
		this.saveDirectory = saveDirectory;
	}
	
	public DownloadTask(String downloadURL, String saveDirectory)
	{
		this.downloadURL = downloadURL;
		this.saveDirectory = saveDirectory;
	}

	/**
	 * Executed in background thread
	 */
	@Override
	protected Void doInBackground() throws Exception
	{
		try
		{
			isDone = false;
			HTTPDownloadUtil util = new HTTPDownloadUtil();
			util.downloadFile(downloadURL);
			// set file information on the GUI
			if(gui != null) gui.setFileInfo(util.getFileName(), util.getContentLength());
			String saveFilePath = saveDirectory + File.separator + util.getFileName();
			InputStream inputStream = util.getInputStream();
			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			long totalBytesRead = 0;
			int percentCompleted = 0;
			long fileSize = util.getContentLength();
			while ((bytesRead = inputStream.read(buffer)) != -1)
			{
				outputStream.write(buffer, 0, bytesRead);
				totalBytesRead += bytesRead;
				percentCompleted = (int) (totalBytesRead * 100 / fileSize);
				setProgress(percentCompleted);
			}
			outputStream.close();
			util.disconnect();
		} catch (IOException ex)
		{
			if(gui != null) JOptionPane.showMessageDialog(gui, "Error downloading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			setProgress(0);
			isError = true;
			cancel(true);
		}
		return null;
	}

	/**
	 * Executed in Swing's event dispatching thread
	 */
	@Override
	protected void done()
	{
		isDone=true;
		if (!isCancelled())
		{
			if(gui != null ) JOptionPane.showMessageDialog(gui, "다운로드 완료", "Message", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
