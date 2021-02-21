package io.github.micwan88.helperclass4j.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AesCryptoUtil {
	private static final Logger myLogger = LogManager.getLogger(AesCryptoUtil.class);
	
	public static final String DEFAULT_KEY_TYPE = "AES";
	public static final String DEFAULT_ALGO_TYPE = "AES/GCM/NoPadding";
	public static final String DEFAULT_PBKDF2_TYPE = "PBKDF2WithHmacSHA256";
	public static final int DEFAULT_AESGCM_AUTHTAG_SIZE_IN_BITS = 128;
	public static final int PBKDF2_PASSWORD_KEY_SIZE_IN_BITS = 256;
	public static final int PBKDF2_PASSWORD_SALT_SIZE_IN_BYTES = 16;
	public static final int PBKDF2_PASSWORD_DEFAULT_ITERATION = 1000000;
	
	public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
		return generateAESKey(-1);
	}
	
	public static SecretKey generateAESKey(int keySizeInBit) throws NoSuchAlgorithmException {
		myLogger.traceEntry("generateAESKey - keyBitSize: {}", keySizeInBit);
		
		SecureRandom secureRandom = new SecureRandom();
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance(DEFAULT_KEY_TYPE);
		if (keySizeInBit == -1)
			keyGenerator.init(Cipher.getMaxAllowedKeyLength(DEFAULT_KEY_TYPE), secureRandom);
		else
			keyGenerator.init(keySizeInBit, secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();
		
		myLogger.traceExit("No of bytes return: {}", secretKey.getEncoded().length);
		return secretKey;
	}
	
	public static SecretKey deriveAES256KeyByPassword(String password, byte[] salt, int iteration) throws NoSuchAlgorithmException, InvalidKeySpecException {
		myLogger.traceEntry("deriveAES256KeyByPassword - salt.length: {}, iteration: {}", salt.length, iteration);
		
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 
				iteration, PBKDF2_PASSWORD_KEY_SIZE_IN_BITS);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(DEFAULT_PBKDF2_TYPE);
		
		SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
		
		myLogger.traceExit("No of bytes return: {}", secretKey.getEncoded().length);
		return secretKey;
	}
	
	public static byte[] generateRandomBytes(int byteSize) {
		SecureRandom secureRandom = new SecureRandom();
		
		byte[] randomBytes = new byte[byteSize];
		
		secureRandom.nextBytes(randomBytes);
		
		return randomBytes;
	}
	
	public static String decryptAESGCM(SecretKey secretKey, byte[] iv, byte[] cipherText) throws NoSuchAlgorithmException, 
		NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, 
		InvalidKeyException, InvalidAlgorithmParameterException {
		
		myLogger.debug("decryptAESGCM - iv.lenght:{}", iv.length);
		myLogger.debug("decryptAESGCM - cipherText.lenght:{}", cipherText.length);
		
		final Cipher cipher = Cipher.getInstance(DEFAULT_ALGO_TYPE);
		final GCMParameterSpec gcmParamSpec = new GCMParameterSpec(DEFAULT_AESGCM_AUTHTAG_SIZE_IN_BITS, iv);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParamSpec);
		
		byte[] resultBytes = cipher.doFinal(cipherText);
		String resultStr = new String(resultBytes);
		
		myLogger.traceExit("decryptAESGCM - resultBytes.lenght:{}", resultBytes.length);
		
		return resultStr;
	}
}
