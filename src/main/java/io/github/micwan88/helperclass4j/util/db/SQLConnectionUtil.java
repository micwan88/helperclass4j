package io.github.micwan88.helperclass4j.util.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.micwan88.helperclass4j.util.AppPropertiesUtil;

public class SQLConnectionUtil {
	public static final String APP_PROPERTY_KEY_JDBC_DRIVER_CLASS = "jdbc.driverClassName";
	public static final String APP_PROPERTY_KEY_JDBC_URL = "jdbc.url";
	public static final String APP_PROPERTY_KEY_JDBC_USERNAME = "jdbc.username";
	public static final String APP_PROPERTY_KEY_JDBC_PASSWORD = "jdbc.password";
	public static final String APP_PROPERTY_KEY_JDBC_MAX_TOTAL = "jdbc.maxTotal";
	public static final String APP_PROPERTY_KEY_JDBC_MAX_IDLE = "jdbc.maxIdle";
	public static final String APP_PROPERTY_KEY_JDBC_MAX_WAIT_MILLIS = "jdbc.maxWaitMillis";
	
	private static final Logger myLogger = LogManager.getLogger(SQLConnectionUtil.class);
	
	private static DataSource poolDataSource = null;
	
	public static Properties checkJDBCProperties() throws IOException {
		AppPropertiesUtil appPropertiesUtil = new AppPropertiesUtil();
		Properties appProperties = appPropertiesUtil.getAppProperty();
		
		if (appProperties == null)
			throw new IOException("Cannot load app properties");
		
		String jdbcDriverClassName = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_DRIVER_CLASS);
		String jdbcURL = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_URL);
		String jdbcUserName = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_USERNAME);
		String jdbcPassword = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_PASSWORD);
		
		if (jdbcDriverClassName == null)
			throw new IOException("Cannot get app properties: " + APP_PROPERTY_KEY_JDBC_DRIVER_CLASS);
		
		if (jdbcURL == null)
			throw new IOException("Cannot get app properties: " + APP_PROPERTY_KEY_JDBC_URL);
		
		if (jdbcUserName == null)
			throw new IOException("Cannot get app properties: " + APP_PROPERTY_KEY_JDBC_USERNAME);
		
		if (jdbcPassword == null)
			throw new IOException("Cannot get app properties: " + APP_PROPERTY_KEY_JDBC_PASSWORD);
		
		String jdbcPoolMaxTotal = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_MAX_TOTAL);
		String jdbcPoolMaxIdle = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_MAX_IDLE);
		String jdbcPoolMaxWaitMillis = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_MAX_WAIT_MILLIS);
		
		myLogger.debug("jdbcDriverClassName: {}", jdbcDriverClassName);
		myLogger.debug("jdbcURL: {}", jdbcURL);
		myLogger.debug("jdbcUserName: {}", jdbcUserName);
		myLogger.debug("jdbcPassword: {}", jdbcPassword);
		myLogger.debug("jdbcPoolMaxTotal: {}", jdbcPoolMaxTotal);
		myLogger.debug("jdbcPoolMaxIdle: {}", jdbcPoolMaxIdle);
		myLogger.debug("jdbcPoolMaxWaitMillis: {}", jdbcPoolMaxWaitMillis);
		
		return appProperties;
	}
	
	public static Connection getJndiDBConnection(String jndiName) throws SQLException, NamingException {
		Context jndiContext = new InitialContext();
		DataSource ds = (DataSource)jndiContext.lookup(jndiName);
		return ds.getConnection();
	}
	
	public static Connection getLocalPoolDBConnection() throws IOException, SQLException {
		if (poolDataSource == null || ((BasicDataSource)poolDataSource).isClosed())
			poolDataSource = initLocalPoolDataSource(checkJDBCProperties());
		
		return poolDataSource.getConnection();
	}
	
	public static Connection getLocalPoolDBConnection(Properties appProperties) throws IOException, SQLException {
		if (poolDataSource == null || ((BasicDataSource)poolDataSource).isClosed())
			poolDataSource = initLocalPoolDataSource(appProperties);
		
		return poolDataSource.getConnection();
	}
	
	public static Connection getNonPoolDBConnection() throws ClassNotFoundException, SQLException, IOException {
		return getNonPoolDBConnection(checkJDBCProperties());
	}
	
	public static Connection getNonPoolDBConnection(Properties appProperties) throws ClassNotFoundException, SQLException, IOException {
		myLogger.debug("Start getNonPoolDBConnection");
		
		String jdbcDriverClassName = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_DRIVER_CLASS);
		String jdbcURL = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_URL);
		String jdbcUserName = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_USERNAME);
		String jdbcPassword = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_PASSWORD);
		
		myLogger.debug("Loading driver class: {}", jdbcDriverClassName);
		Class.forName(jdbcDriverClassName);
		
		myLogger.debug("Get non-pool DB connection ...");
		return DriverManager.getConnection(jdbcURL, jdbcUserName, jdbcPassword);
	}
	
	public static void closeLocalPoolDataSource() {
		if (poolDataSource == null)
			return;
		myLogger.debug("Start closeLocalPoolDataSource");
		try {
			((BasicDataSource)poolDataSource).close();
		} catch (SQLException e) {
			//Do Nothing
		}
		poolDataSource = null;
		myLogger.debug("End closeLocalPoolDataSource");
	}
	
	private static DataSource initLocalPoolDataSource(Properties appProperties) throws IOException {
		myLogger.debug("Start initPoolDataSource");
		
		String jdbcDriverClassName = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_DRIVER_CLASS);
		String jdbcURL = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_URL);
		String jdbcUserName = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_USERNAME);
		String jdbcPassword = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_PASSWORD);
		
		String jdbcPoolMaxTotal = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_MAX_TOTAL);
		String jdbcPoolMaxIdle = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_MAX_IDLE);
		String jdbcPoolMaxWaitMillis = appProperties.getProperty(APP_PROPERTY_KEY_JDBC_MAX_WAIT_MILLIS);
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(jdbcDriverClassName);
		ds.setUrl(jdbcURL);
		ds.setUsername(jdbcUserName);
		ds.setPassword(jdbcPassword);
		
		if (jdbcPoolMaxTotal != null && !jdbcPoolMaxTotal.trim().equals(""))
			ds.setMaxTotal(Integer.parseInt(jdbcPoolMaxTotal));
		
		if (jdbcPoolMaxIdle != null && !jdbcPoolMaxIdle.trim().equals(""))
			ds.setMaxIdle(Integer.parseInt(jdbcPoolMaxIdle));
		
		if (jdbcPoolMaxWaitMillis != null && !jdbcPoolMaxWaitMillis.trim().equals(""))
			ds.setMaxWaitMillis(Long.parseLong(jdbcPoolMaxWaitMillis));
		
		myLogger.debug("End initPoolDataSource");
		return ds;
	}
	
	public static void close(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				//Do Nothing
			}
		}
	}
	
	public static void close(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				//Do Nothing
			}
		}
	}
	
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				//Do Nothing
			}
		}
	}
	
	public static void close(Connection dbConn) {
		if (dbConn != null) {
			try {
				dbConn.close();
			} catch (SQLException e) {
				//Do nothing
			}
		}
	}
	
	public static void resetAutoCommit(Connection dbConn) {
		if (dbConn != null) {
			try {
				dbConn.setAutoCommit(true);
			} catch (SQLException e) {
				//Do nothing
			}
		}
	}
	
	public static void rollbackTrans(Connection dbConn) {
		if (dbConn != null) {
			try {
				dbConn.rollback();
			} catch (SQLException e) {
				//Do nothing
			}
		}
	}
	
	public static void rollbackTransAndResetAutoCommit(Connection dbConn) {
		if (dbConn != null) {
			try {
				dbConn.rollback();
			} catch (SQLException e) {
				//Do nothing
			}
			try {
				dbConn.setAutoCommit(true);
			} catch (SQLException e) {
				//Do nothing
			}
		}
	}
}
