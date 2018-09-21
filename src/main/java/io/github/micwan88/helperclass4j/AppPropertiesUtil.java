package io.github.micwan88.helperclass4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppPropertiesUtil {
	public static final String APP_PROPERTY_FILE = "app.properties";
	
	private static final Logger myLogger = LogManager.getLogger(AppPropertiesUtil.class);
	private static HashMap<String, Properties> appPropertiesMap = new HashMap<>();
	
	public static Properties loadProperties(Path propertyFilePath) {
		BufferedReader br = null;
		
		myLogger.debug("try loading property file {}", propertyFilePath.toAbsolutePath());
		
		try {
			br = Files.newBufferedReader(propertyFilePath, Charset.forName("UTF-8"));
			Properties tmpProperties = new Properties();
			tmpProperties.load(br);
			
			myLogger.debug("Load property file done");
			
			return tmpProperties;
		} catch (UnsupportedEncodingException e) {
			myLogger.error("Cannot read property file", e);
		} catch (IOException e) {
			myLogger.error("Cannot read property file", e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				//Do Nothing
			}
		}
		return null;
	}
	
	public static Properties loadProperties(String resourcesName) {
		BufferedReader br = null;
		
		myLogger.debug("try loading property file {} in classpath", resourcesName);
		
		InputStream inStream = AppPropertiesUtil.class.getClass().getResourceAsStream("/" + resourcesName);
		if (inStream == null) {
			myLogger.error("{} not found in classpath !", resourcesName);
			return null;
		}
		
		try {
			br = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
			Properties tmpProperties = new Properties();
			tmpProperties.load(br);
			
			myLogger.debug("Load property file done");
			
			return tmpProperties;
		} catch (UnsupportedEncodingException e) {
			myLogger.error("Cannot read property file", e);
		} catch (IOException e) {
			myLogger.error("Cannot read property file", e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				//Do Nothing
			}
		}
		return null;
	}
	
	public Properties getAppProperty() {
		return getAppProperty(APP_PROPERTY_FILE);
	}
	
	public Properties getAppProperty(String resourcesName) {
		return getAppProperty(resourcesName, false);
	}
	
	public Properties getAppProperty(String resourcesName, boolean reload) {
		if (!reload && appPropertiesMap.containsKey(resourcesName))
			return appPropertiesMap.get(resourcesName);
		
		myLogger.debug("Properties {} is not yet load or force reload, try loading it ...", resourcesName);
		
		Properties tmpProperties = loadProperties(resourcesName);
		if (tmpProperties != null)
			appPropertiesMap.put(resourcesName, tmpProperties);
		return tmpProperties;
	}
	
	public String getAppPropertyValue(String key, String defaultValue) {
		return getAppPropertyValue(APP_PROPERTY_FILE, key, defaultValue);
	}
	
	public String getAppPropertyValue(String resourcesName, String key, String defaultValue) {
		Properties tmpProperties = getAppProperty(resourcesName);
		if (tmpProperties == null)
			return null;
		
		return tmpProperties.getProperty(key, defaultValue);
	}
	
	public long getAppPropertyValueLong(String key, long defaultValue) {
		return getAppPropertyValueLong(APP_PROPERTY_FILE, key, defaultValue);
	}
	
	public long getAppPropertyValueLong(String resourcesName, String key, long defaultValue) {
		Properties tmpProperties = getAppProperty(resourcesName);
		if (tmpProperties == null)
			return 0L;
		
		return (tmpProperties.containsKey(key)?Long.parseLong(tmpProperties.getProperty(key)):defaultValue);
	}
}
