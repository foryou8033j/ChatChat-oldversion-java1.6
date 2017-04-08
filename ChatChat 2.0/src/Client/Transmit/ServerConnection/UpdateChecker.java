package Client.Transmit.ServerConnection;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import Client.Data.MaintenanceData;
import Client.Fileprotocol.Download.DownloadURLTask;

public class UpdateChecker
{
	
	public static boolean isUpdated(String url, String address, String curVersion) throws IOException
	{
		try
		{
			
			DownloadURLTask.fileUrlDownload("http://" + url + "/"+MaintenanceData.updateCheckerFileName, address);
			String newVersion = "";
			BufferedReader textVersion = new BufferedReader(new FileReader(address + "/" + MaintenanceData.updateCheckerLocalFileName));
			newVersion = textVersion.readLine();
			System.out.println("NewVersion : " + newVersion);
			System.out.println("CurVersion : " + curVersion);
			double dCurVersion = Double.parseDouble(curVersion);
			double dNewVersion = Double.parseDouble(newVersion);
			textVersion.close();
			if (dNewVersion > dCurVersion)
			{
				textVersion.close();
				if (0 == JOptionPane.showConfirmDialog(null, "<HTML>최신 " + newVersion + " 버전이 업데이트 되었습니다.<BR>내려받으시겠습니까?</HTML>", "업데이트 확인", JOptionPane.YES_NO_OPTION))
					return true;
				else
				{
					JOptionPane.showMessageDialog(null, "클라이언트를 종료합니다.", "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
					return false;
				}
			}
			else
				return false;
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
