package Utility.Security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.junit.Test;

public class AES256Cipher
{
	private static String algorithm = "AES/ECB/PKCS5Padding";
	private static String stringKey = "";
	private static Key key = null;
	private static Cipher cipher = null;
	
	public AES256Cipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException
	{
		
	}
	
	/**
	 * 고정키 정보 반환
	 * @return
	 */
	@Test
	public static String key()
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat date = new SimpleDateFormat("MMdd");
		
		//키 값을 매일 변경
		return "2a5s_" + date.format(calendar.getTime()) + "b8q6s8q";
	}
	
	/**
	 * 키 값 반환<BR>
	 * 24바이트인 경우 TripleDES 그 외 DES
	 * @return 키 값
	 */
	private static Key getKey() throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException
	{
		return (key().length() == 24) ? getKey2( key() ) : getKey1( key() );
	}
	
	/**
	 * 지정된 비밀키를 가지고 오는 메서드 (DES)<BR>
	 * require Key Size : 16bytes
	 * @param keyValue
	 * @return 비밀키 클래스
	 */
	private static Key getKey1(String keyValue) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException
	{
		DESKeySpec desKeySpec = new DESKeySpec(keyValue.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		Key key = keyFactory.generateSecret(desKeySpec);
		return key;
	}
	
	/**
	 * 지정된 비밀키를 가지고 오는 메서드 (TripleDES)<BR>
	 * require Key Size : 24byte
	 * @param keyValue
	 * @return 비밀 키 클래스
	 */
	private static Key getKey2(String keyValue) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException
	{
		DESKeySpec desKeySpec = new DESKeySpec(keyValue.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		Key key = keyFactory.generateSecret(desKeySpec);
		return key;
	}
	
	/**
	 * @param input 비밀키 암호화 대상 문자열
	 * @return 암호화된 문자열
	 */
	public static String encoding(String input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException
	{
		if(input == null || input.length() == 0)
			return "";
		
		String instance = (key().length() == 24) ? "DESede/ECB/PKCS5Padding" : "DES/ECB/PKCS5Padding";
		Cipher cipher = Cipher.getInstance(instance);
		cipher.init(Cipher.ENCRYPT_MODE, getKey());
		String amalgam = input;
		
		byte[] inputByte = amalgam.getBytes("UTF8");
		byte[] outputByte = cipher.doFinal(inputByte);

		//Default API가 아니라 불안정, 추후 jre 업그레이드 필요 (1.6jre -> 1.8jre)
		return Base64.getEncoder().encodeToString(outputByte);
		
	}
	
	public static String decode(String input) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException
	{
		if(input == null || input.length() == 0)
			return "";
		
		String instance = (key().length() == 24) ? "DESede/ECB/PKCS5Padding" : "DES/ECB/PKCS5Padding";
		Cipher cipher = Cipher.getInstance(instance);
		cipher.init(Cipher.DECRYPT_MODE, getKey());
		String amalgam = input;
		
		//Default API가 아니라 불안정, 추후 jre 업그레이드 필요 (1.6jre -> 1.8jre)
		byte[] inputByte = Base64.getDecoder().decode(input);
		
		
		byte[] outputByte = cipher.doFinal(inputByte);
		
		return new String(outputByte, "UTF8");
	}
	
	
}
