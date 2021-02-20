package io.github.micwan88.helperclass4j.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AesCryptoUtil {
	private static final Logger myLogger = LogManager.getLogger(AesCryptoUtil.class);
	
	public static String DEFAULT_KEY_TYPE = "AES";
	public static String DEFAULT_PBKDF2_TYPE = "PBKDF2WithHmacSHA256";
	public static int PBKDF2_PASSWORD_KEY_SIZE_IN_BYTES = 256;
	public static int PBKDF2_PASSWORD_SALT_SIZE_IN_BYTES = 16;
	public static int PBKDF2_PASSWORD_DEFAULT_ITERATION = 1000000;
	
	public static byte[] generateAESKey() throws NoSuchAlgorithmException {
		return generateAESKey(-1);
	}
	
	public static byte[] generateAESKey(int keySizeInBit) throws NoSuchAlgorithmException {
		myLogger.traceEntry("keyBitSize: {}", keySizeInBit);
		
		SecureRandom secureRandom = new SecureRandom();
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance(DEFAULT_KEY_TYPE);
		if (keySizeInBit == -1)
			keyGenerator.init(Cipher.getMaxAllowedKeyLength(DEFAULT_KEY_TYPE), secureRandom);
		else
			keyGenerator.init(keySizeInBit, secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();
		
		byte[] secretKeyInBytes = secretKey.getEncoded();
		myLogger.traceExit("No of bytes return: {}", secretKeyInBytes.length);
		
		return secretKeyInBytes;
	}
	
	public static byte[] deriveAES256KeyByPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] passwordSalt = generateRandomBytes(PBKDF2_PASSWORD_SALT_SIZE_IN_BYTES);
		
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), passwordSalt, 
				PBKDF2_PASSWORD_DEFAULT_ITERATION, PBKDF2_PASSWORD_KEY_SIZE_IN_BYTES);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(DEFAULT_PBKDF2_TYPE);
		
		byte[] secretKeyInBytes = secretKeyFactory.generateSecret(keySpec).getEncoded();
		myLogger.traceExit("No of bytes return: {}", secretKeyInBytes.length);
		
		return secretKeyInBytes;
	}
	
	public static byte[] generateRandomBytes(int byteSize) {
		SecureRandom secureRandom = new SecureRandom();
		
		byte[] randomBytes = new byte[byteSize];
		
		secureRandom.nextBytes(randomBytes);
		
		return randomBytes;
	}
}
