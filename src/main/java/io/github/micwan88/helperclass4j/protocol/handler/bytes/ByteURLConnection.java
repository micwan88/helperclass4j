package io.github.micwan88.helperclass4j.protocol.handler.bytes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ByteURLConnection extends URLConnection {
	private byte[] byteContent = null;
	private ByteArrayInputStream byteInStream = null;
	
	protected ByteURLConnection(URL paramURL) {
		super(paramURL);
	}
	
	public ByteURLConnection(URL paramURL, byte[] byteContent) {
		super(paramURL);
		this.byteContent = byteContent;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		if (byteInStream == null)
			connect();
		
		return byteInStream;
	}

	@Override
	public void connect() throws IOException {
		if (byteContent == null)
			throw new IOException("This handler only support to be created with byte array in constructor");
		
		byteInStream = new ByteArrayInputStream(byteContent);
	}
}
