package io.github.micwan88.helperclass4j.httpclient;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomRetryHandler implements HttpRequestRetryHandler {
	
	private static final Logger myLogger = LogManager.getLogger(CustomRetryHandler.class);
	
	private int maxReTryCount = 5;
    private long errorSleepTime = 1000L;

    public CustomRetryHandler(int maxReTryCount, long errorSleepTime) {
        this.maxReTryCount = maxReTryCount;
        this.errorSleepTime = errorSleepTime;
    }

    public CustomRetryHandler(int maxReTryCount) {
        this.maxReTryCount = maxReTryCount;
    }
	
	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext httpContext) {
		myLogger.debug("Retry count: {}", executionCount);
		
        if (executionCount > maxReTryCount) {
            myLogger.debug("Reach max retry count: {}. So not retry.", maxReTryCount);
            return false;
        }
        if (exception instanceof UnknownHostException) {
            myLogger.debug("UnknownHostException. So not retry.");
            // Unknown host
            return false;
        }
        if (exception instanceof SSLException) {
            myLogger.debug("SSLException. So not retry.");
            // SSL handshake exception
            return false;
        }
        if (exception instanceof NoHttpResponseException) {
            myLogger.debug("NoHttpResponseException. Retrying...");
            try {
                Thread.sleep(errorSleepTime);
            } catch (InterruptedException e) {
                //Do nothing
            }

            // Connection fail
            return true;
        }
        if (exception instanceof ConnectTimeoutException) {
            myLogger.debug("ConnectTimeoutException. Retrying...");
            try {
                Thread.sleep(errorSleepTime);
            } catch (InterruptedException e) {
                //Do nothing
            }

            // Connection refused
            return true;
        }
        if (exception instanceof SocketTimeoutException) {
            myLogger.debug("SocketTimeoutException. Retrying...");
            try {
                Thread.sleep(errorSleepTime);
            } catch (InterruptedException e) {
                //Do nothing
            }

            // Connection Timeout
            return true;
        }
        if (exception instanceof InterruptedIOException) {
            myLogger.debug("InterruptedIOException. Retrying...");
            try {
                Thread.sleep(errorSleepTime);
            } catch (InterruptedException e) {
                //Do nothing
            }

            // Timeout
            return true;
        }
        myLogger.debug("Unknown exception. So not retry. {}", exception.getMessage());
        return false;
	}

}
