package io.github.micwan88.helperclass4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.github.micwan88.helperclass4j.protocol.handler.bytes.Handler;

public class ByteClassLoader extends ClassLoader {
	private HashMap<String, byte[]> byteDataMap = new HashMap<>();
	
	public ByteClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	public void loadSingleFileDataInBytes(byte[] byteData, String resourcesName) {
		byteDataMap.put(resourcesName, byteData);
	}
	
	public void loadJarDataInBytes(byte[] byteData) throws IOException {
		HashMap<String, byte[]> tmpByteDataMap = loadByteDataMapFromZip(byteData);
		byteDataMap.putAll(tmpByteDataMap);
	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if (byteDataMap.isEmpty())
			throw new ClassNotFoundException("byte data is empty");
		
		String filePath = className.replaceAll("\\.", "/").concat(".class");
		byte[] extractedBytes = byteDataMap.get(filePath);
		if (extractedBytes == null)
			throw new ClassNotFoundException("Cannot find " + filePath + " in bytes");
		
		return defineClass(className, extractedBytes, 0, extractedBytes.length);
	}

	public Class<?> loadClass(String className, byte[] classDataInBytes, boolean isJar) throws ClassNotFoundException, 
		IOException {
		if (isJar)
			loadJarDataInBytes(classDataInBytes);
		else
			loadSingleFileDataInBytes(classDataInBytes, className.replaceAll("\\.", "/").concat(".class"));
		
		return this.loadClass(className);
	}
	
	@Override
	protected URL findResource(String paramString) {
		byte[] extractedBytes = byteDataMap.get(paramString);
		if (extractedBytes != null) {
			try {
				return new URL(null, "bytes:///" + paramString, new Handler(extractedBytes, paramString));
			} catch (MalformedURLException e) {
				//Do nothing
			}
		}
		return null;
	}
	
	private HashMap<String, byte[]> loadByteDataMapFromZip(byte[] jarDataInBytes) throws IOException {
		HashMap<String, byte[]> tmpByteDataMap = new HashMap<>();
		
		byte[] tmpBuffer = new byte[1024];
		ZipInputStream zipInStream = null;
		ByteArrayOutputStream baos = null;
		try {
			zipInStream = new ZipInputStream(new ByteArrayInputStream(jarDataInBytes));
			ZipEntry zipEntry = null;
			
			while ((zipEntry = zipInStream.getNextEntry()) != null) {
				baos = new ByteArrayOutputStream();
				int count;
				while ((count = zipInStream.read(tmpBuffer)) > 0) {
					baos.write(tmpBuffer, 0, count);
				}
				zipInStream.closeEntry();
				
				tmpByteDataMap.put(zipEntry.getName(), baos.toByteArray());
				baos.close();
			}
		} finally {
			try {
				if (zipInStream != null)
					zipInStream.close();
			} catch (IOException e) {
				//Do nothing
			}
		}
		return tmpByteDataMap;
	}
}
