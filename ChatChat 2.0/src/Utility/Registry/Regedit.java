package Utility.Registry;

import java.util.prefs.Preferences;

/**
 * 레지스트리 정보 읽기 / 수정 / 확인
 * @author Jeoxxiii
 *
 */
public class Regedit
{
	
	//OS 로 부터 client 레지스트리 정보 받기
	static Preferences userRootPrefs = Preferences.userRoot();
	
	/**
	 * 레지스트리에 해당 키가 존재하는지 여부 확인
	 * @param key 키 이름
	 * @return 존재 여부
	 */
	public static boolean isContains(String key)
	{
		return userRootPrefs.get(key, null) != null;
	}
	
	/**
	 * 레지스트리에 키 값 추가
	 * @param key 키 이름
	 * @param value 키 값
	 */
	public static void addRegistry(String key, String value)
	{
		
		try{
			if(isContains(key))
			{
				userRootPrefs.put(key,  value);
			}
			else
				userRootPrefs.put(key, value);
		}catch (Exception e)
		{
			
		}
		
	}
	
	/**
	 * 레지스트리로부터 키 값 확인
	 * @param key 키 이름
	 * @return 키 값
	 */
	public static String getRegistry(String key)
	{
		try
		{
			return userRootPrefs.get(key, ""); 
		}catch (Exception e)
		{
			
		}
		return null;
	}
}
