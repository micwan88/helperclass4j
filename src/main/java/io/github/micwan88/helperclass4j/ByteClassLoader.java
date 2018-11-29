package io.github.micwan88.helperclass4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.micwan88.helperclass4j.protocol.handler.bytes.Handler;

public class ByteClassLoader extends ClassLoader {
	private static final Logger myLogger = LogManager.getLogger(ByteClassLoader.class);
	
	private byte[] classDataInBytes = null;
	private boolean isJar = false;
	
	/**
	 * @param parent
	 */
	public ByteClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if (classDataInBytes == null)
			throw new ClassNotFoundException("classDataInBytes is empty");
		
		if (isJar) {
			String filePath = className.replaceAll("\\.", "/").concat(".class");
			byte[] extractedBytes = getByteArrayFromZip(filePath);
			if (extractedBytes == null)
				throw new ClassNotFoundException("Cannot find " + filePath + " in bytes");
			return defineClass(className, extractedBytes, 0, extractedBytes.length);
		}
		
		return defineClass(className, classDataInBytes, 0, classDataInBytes.length);
	}

	public Class<?> loadClass(String className, byte[] classDataInBytes, boolean isJar) throws ClassNotFoundException {
		this.classDataInBytes = classDataInBytes;
		this.isJar = isJar;
		
		return this.loadClass(className);
	}
	
	@Override
	protected URL findResource(String paramString) {
		if (classDataInBytes != null && isJar) {
			byte[] extractedBytes = getByteArrayFromZip(paramString);
			if (extractedBytes != null) {
				try {
					return new URL(null, "bytes:///" + paramString, new Handler(extractedBytes, paramString));
				} catch (MalformedURLException e) {
					//Do nothing
				}
			}
		}
		return null;
	}

	public void initClassDataInBytes(byte[] classDataInBytes, boolean isJar) {
		this.classDataInBytes = classDataInBytes;
		this.isJar = isJar;
	}
	
	private byte[] getByteArrayFromZip(String resourcesName) {
		byte[] tmpBuffer = new byte[1024];
		ZipInputStream zipInStream = null;
		ByteArrayOutputStream baos = null;
		try {
			zipInStream = new ZipInputStream(new ByteArrayInputStream(classDataInBytes));
			ZipEntry zipEntry = null;
			
			while ((zipEntry = zipInStream.getNextEntry()) != null) {
				if (zipEntry.getName().equalsIgnoreCase(resourcesName)) {
					break;
				}
				zipEntry = null;
				zipInStream.closeEntry();
			}
			
			if (zipEntry == null)
				return null;
			
			baos = new ByteArrayOutputStream();
			int count;
			while ((count = zipInStream.read(tmpBuffer)) > 0) {
				baos.write(tmpBuffer, 0, count);
			}
			zipInStream.closeEntry();
			return baos.toByteArray();
		} catch (IOException e) {
			myLogger.error("Cannot read zip content", e);
		} catch (Exception e) {
			myLogger.error("Unknown error during read zip content", e);
		} finally {
			try {
				if (zipInStream != null)
					zipInStream.close();
			} catch (IOException e) {
				//Do nothing
			}
		}
		
		return null;
	}
}
