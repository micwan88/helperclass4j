package io.github.micwan88.helperclass4j;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Test;

public class TestByteClassLoader {
	private byte[] jarInBytes = null;
	
	@Before
	public void loadJarToBytes() throws DecoderException {
		//Load a jar with a class and resource
		jarInBytes = Hex.decodeHex("");
	}
	
	@Test
	public void startTest() throws ClassNotFoundException {
		ByteClassLoader byteClassLoader = new ByteClassLoader(this.getClass().getClassLoader());
		byteClassLoader.initClassDataInBytes(jarInBytes, true);
		
		Class<?> helloWorldClass = byteClassLoader.loadClass("test.HelloWorld");
		byteClassLoader.getResourceAsStream("msg.properties");
	}
	
	public static void main(String[] args) {
	}
}
