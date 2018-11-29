package io.github.micwan88.helperclass4j.protocol.handler.bytes;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
	private byte[] byteContent = null;
	private String resourceName = null;

	/**
	 * @param byteContent
	 * @param resourceName
	 */
	public Handler(byte[] byteContent, String resourceName) {
		this.byteContent = byteContent;
		this.resourceName = resourceName;
	}
	
	public void setByteContent(byte[] byteContent, String resourceName) {
		this.byteContent = byteContent;
		this.resourceName = resourceName;
	}

	@Override
	protected URLConnection openConnection(URL paramURL) throws IOException {
		if (byteContent == null || resourceName == null)
			throw new UnsupportedOperationException("This handler only support to be created with byte array in constructor");
		
		//Resource not match
		if (!resourceName.equals(paramURL.getFile()))
			throw new UnsupportedOperationException("URL file name does not match with assigned resource name: " + resourceName);
		
		ByteURLConnection byteURLConnection = new ByteURLConnection(paramURL, byteContent);
		
		return byteURLConnection;
	}

}
