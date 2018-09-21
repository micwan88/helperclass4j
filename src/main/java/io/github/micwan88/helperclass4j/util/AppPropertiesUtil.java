package io.github.micwan88.helperclass4j.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppPropertiesUtil {
	public static final String APP_PROPERTY_FILE = "app.properties";
	
	private static final Logger myLogger = LogManager.getLogger(AppPropertiesUtil.class);
	private static Properties appProperties = null;
	
	public Properties loadOtherProperties(Path propertyFilePath) {
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
	
	private Properties loadAppProperties() {
		if (appProperties != null)
			return appProperties;
		
		BufferedReader br = null;
		if (appProperties == null) {
			myLogger.debug("appProperties is null, try loading {}", APP_PROPERTY_FILE);
			
			InputStream inStream = this.getClass().getResourceAsStream("/" + APP_PROPERTY_FILE);
			if (inStream == null) {
				myLogger.error("{} not found !", APP_PROPERTY_FILE);
				return null;
			}
			
			try {
				br = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
				Properties tmpProperties = new Properties();
				tmpProperties.load(br);
				
				myLogger.debug("Load appProperties done");
				
				appProperties = tmpProperties;
				return appProperties;
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
		}
		return null;
	}
	
	public Properties getAppProperty() {
		return loadAppProperties();
	}
	
	public String getAppPropertyValue(String key, String defaultValue) {
		if (appProperties == null && loadAppProperties() == null)
			return null;
		
		return appProperties.getProperty(key, defaultValue);
	}
	
	public long getAppPropertyValueLong(String key, long defaultValue) {
		if (appProperties == null && loadAppProperties() == null)
			return 0L;
		
		return (appProperties.containsKey(key)?Long.parseLong(appProperties.getProperty(key)):defaultValue);
	}
}
