package Client.Fileprotocol.Download;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * HTTP 형식으로 서버로 부터 파일 내려받기 진행
 * @author Jeoxxiii
 *
 */
public class DownloadURLTask
{
	
	/**
	 * 버퍼 사이즈
	 */
	final static int size = 1024;

	/**
	 * fileAddress에서 파일을 읽어, 다운로드 디렉토리에 다운로드
	 * 
	 * @param fileAddress
	 * @param localFileName
	 * @param downloadDir
	 */
	public static boolean fileUrlReadAndDownload(String fileAddress, String localFileName, String downloadDir)
	{
		OutputStream outStream = null;
		URLConnection uCon = null;
		InputStream is = null;
		try
		{
			System.out.println("-------Download Start------");
			URL Url;
			byte[] buf;
			int byteRead;
			int byteWritten = 0;
			Url = new URL(fileAddress);
			outStream = new BufferedOutputStream(new FileOutputStream(downloadDir + "\\" + localFileName));
			
			uCon = Url.openConnection();
			is = uCon.getInputStream();
			buf = new byte[size];
			while ((byteRead = is.read(buf)) != -1)
			{
				outStream.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}
			System.out.println("Download Successfully.");
			System.out.println("File name : " + localFileName);
			System.out.println("of bytes  : " + byteWritten);
			System.out.println("-------Download End--------");
		} catch (Exception e)
		{
			return false;
		} finally
		{
			try
			{
				is.close();
				outStream.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 
	 * @param fileAddress
	 * @param downloadDir
	 */
	public static boolean fileUrlDownload(String fileAddress, String downloadDir)
	{
		// 파일 어드레스에서 마지막에 있는 파일이름을 취득
		int slashIndex = fileAddress.lastIndexOf('/');
		int periodIndex = fileAddress.lastIndexOf('.');
		String fileName = fileAddress.substring(slashIndex + 1);
		
		if (periodIndex >= 1 && slashIndex >= 0 && slashIndex < fileAddress.length() - 1)
		{
			if(fileUrlReadAndDownload(fileAddress, fileName, downloadDir));
			else
				return false;
		}
		else
		{
			System.err.println("path or file name Error.");
			return false;
		}
		
		return true;
	}
	
}
