package com.tangramfactory.smartweight.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.os.Environment;


import com.tangramfactory.smartweight.SmartWeightApplication;

public class UUIDCryptoUtils {
	private static String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"."+ SmartWeightApplication.BASE_TAG
			+ File.separator + ".data" + File.separator;
	private static String FILE_NAME = ".123iuhr98fy2q3491h";
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	private static final String KEY = "H)OR%HG!DC$KL@SR";

	public static boolean hasUUIDFile() {
		File targetFile = null;
		try {
			targetFile = new File(ROOT_PATH + FILE_NAME);
		} catch (Exception e) {
			return false;
		}
		return targetFile.exists();
	}


	public static void makeDeviceUUID() {
		try {
			File targetFile = new File(ROOT_PATH);
			if (!targetFile.exists())
				targetFile.mkdirs();

			targetFile = new File(ROOT_PATH + FILE_NAME);

			Key secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			String mUUID = UUID.randomUUID().toString();

			byte[] outputBytes = cipher.doFinal(mUUID.getBytes("UTF-8"));
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			outputStream.write(outputBytes);
			outputStream.close();
			DebugLogger.d("CryptoUtils", "mUUID : " + mUUID);
		} catch (Exception ex) {
			DebugLogger.e("CryptoUtils", "Error encryptUUID", ex);
		}
	}
	
	public static void deleteDeviceUUID(){
		File targetFile = null;
		targetFile = new File(ROOT_PATH + FILE_NAME);
		if(targetFile.exists())
			targetFile.delete();
	}
	
	
	public static void updateDeviceUUID(String uuid) {
		try {
			File targetFile = new File(ROOT_PATH);
			if (!targetFile.exists())
				targetFile.mkdirs();

			targetFile = new File(ROOT_PATH + FILE_NAME);

			Key secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			byte[] outputBytes = cipher.doFinal(uuid.getBytes("UTF-8"));
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			outputStream.write(outputBytes);
			outputStream.close();
			DebugLogger.d("CryptoUtils", "mUUID : " + uuid);
		} catch (Exception ex) {
			DebugLogger.e("CryptoUtils", "Error encryptUUID", ex);
		}
	}

	public static String getDeviceUUID() {
		try {
			File targetFile = new File(ROOT_PATH + FILE_NAME);

			Key secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			FileInputStream inputStream = new FileInputStream(targetFile);
			byte[] inputBytes = new byte[(int) targetFile.length()];
			inputStream.read(inputBytes);

			byte[] outputBytes = cipher.doFinal(inputBytes);

			inputStream.close();
			String mUUID = new String(outputBytes);
			DebugLogger.d("CryptoUtils", "mUUID : " + mUUID);
			return mUUID;
		} catch (Exception ex) {
			DebugLogger.e("CryptoUtils", "Error decryptUUID", ex);
			return null;
		}

	}
}
