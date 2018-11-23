package io.github.micwan88.helperclass4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.micwan88.helperclass4j.ByteClassLoader;

public class DynamicAppLoader {
	private static final Logger myLogger = LogManager.getLogger(DynamicAppLoader.class);
	
	public static final String MAIN_METHOD_NAME = "main";
	
	private ByteClassLoader byteClassLoader = null;
	private byte[] classDataInBytes = null;
	private String className = null;
	
	/**
	 * @param classDataInBytes
	 * @param className
	 */
	public DynamicAppLoader(byte[] classDataInBytes, String className) {
		this.classDataInBytes = classDataInBytes;
		this.className = className;
		
		byteClassLoader = new ByteClassLoader(this.getClass().getClassLoader());
	}

	public void invokeMain() {
		myLogger.debug("Start DynamicAppLoader ...");
		
		try {
			myLogger.debug("Load {} bytes into class: {}", classDataInBytes.length, className);
			Class<?> dynamicClass = byteClassLoader.loadClass(className, classDataInBytes, true);
			
			Method mainMethod = dynamicClass.getMethod(MAIN_METHOD_NAME, String[].class);
			
			String[] args = null;
			
			myLogger.debug("Invoke static main ...");
			mainMethod.invoke(null, (Object)args);
			myLogger.debug("End main");
		} catch (ClassNotFoundException e) {
			myLogger.error("Cannot found the target class from byte data", e);
		} catch (NoSuchMethodException | SecurityException e) {
			myLogger.error("Cannot get the main method of target class", e);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			myLogger.error("Cannot call the main method of target class", e);
		} catch (Exception e) {
			myLogger.error("Unknown exception", e);
		}
		
		myLogger.debug("End DynamicAppLoader Normally");
	}
	
	public static void invokeMain(byte[] classDataInBytes, String className) {
		DynamicAppLoader dynamicAppLoader = new DynamicAppLoader(classDataInBytes, className);
		dynamicAppLoader.invokeMain();
	}
}
