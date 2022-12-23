package io.github.micwan88.helperclass4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class TestByteClassLoader {
	final String TEST_JAR_HEX_CODE = "504b0304140008080800ea9b7d4d000000000000000000000000140004004d4554412d494e462f4d414e49464553542e4d46feca0000f34dcccb4c4b2d2ed10d4b2d2acecccfb35230d433e0e5e2e50200504b0708b27f02ee1b00000019000000504b0304140008080800e19b7d4d00000000000000000000000015000000746573742f48656c6c6f576f726c642e636c6173736d50bb4ec340109c230fc78e212f12de90502514b8a0a008a24142141620058582ea929cc245671f722e487c16142051f0017c1462ed44b240b962e7766f6776f6be7f3ebf009ce0d0c20a43c988a9f1ae8452fa5e476a6421cb509ef067ee291e8ebd9bc1440c0d43fe4c86d29c3364da9d3e43f6428f84830c6c1739e449c797a1b89e050311ddf181120c555f0fb9eaf348c6f9a298358f72ca50f1ff8dedd253c065c8d0683ff8e9f89e896438ee76fa362aa859a8fe31d77b991a11b858479d8ce919f9accfc9527bb7c434c4173ce816b0c1504cc63593790eb6b063619ba1b6a4dfc52ef618aca7b8a4c854bdbdcc1379e6d198d6a92db1cce0f4f42c1a8a4b192f5e4a773d8e7bd10259467c56e8467f48d1a2cc236484b9a30f145e936787623e296650a4e8ce1b0857096daca1b4209f2662547b43b9da78c7662ae010c6a40249a52236f67140c8d04c3a5bbf504b0708bf3374e4520100001f020000504b03041400080808003c9a7d4d00000000000000000000000013000000746573742f6d73672e70726f70657274696573cb2d4eb7f548cdc9c95708cf2fca490100504b0708c0497b27110000000f000000504b01021400140008080800ea9b7d4db27f02ee1b000000190000001400040000000000000000000000000000004d4554412d494e462f4d414e49464553542e4d46feca0000504b01021400140008080800e19b7d4dbf3374e4520100001f020000150000000000000000000000000061000000746573742f48656c6c6f576f726c642e636c617373504b010214001400080808003c9a7d4dc0497b27110000000f0000001300000000000000000000000000f6010000746573742f6d73672e70726f70657274696573504b05060000000003000300ca000000480200000000";
	final String TEST_CLASS_HEX_CODE = "cafebabe00000033002207000201000f746573742f48656c6c6f576f726c640700040100106a6176612f6c616e672f4f626a6563740100063c696e69743e010003282956010004436f64650a000300090c0005000601000f4c696e654e756d6265725461626c650100124c6f63616c5661726961626c655461626c65010004746869730100114c746573742f48656c6c6f576f726c643b0100046d61696e010016285b4c6a6176612f6c616e672f537472696e673b295609001100130700120100106a6176612f6c616e672f53797374656d0c001400150100036f75740100154c6a6176612f696f2f5072696e7453747265616d3b08001701000b48656c6c6f20576f726c640a0019001b07001a0100136a6176612f696f2f5072696e7453747265616d0c001c001d0100077072696e746c6e010015284c6a6176612f6c616e672f537472696e673b2956010004617267730100135b4c6a6176612f6c616e672f537472696e673b01000a536f7572636546696c6501000f48656c6c6f576f726c642e6a617661002100010003000000000002000100050006000100070000002f00010001000000052ab70008b100000002000a00000006000100000003000b0000000c000100000005000c000d00000009000e000f00010007000000370002000100000009b200101216b60018b100000002000a0000000a00020000000500080006000b0000000c000100000009001e001f000000010020000000020021";
	
	private byte[] jarInBytes = null;
	private byte[] classInBytes = null;
	
	@BeforeAll
	void loadTestBytes() throws DecoderException {
		//Load a jar with a class and resource
		jarInBytes = Hex.decodeHex(TEST_JAR_HEX_CODE);
		//Load a class
		classInBytes = Hex.decodeHex(TEST_CLASS_HEX_CODE);
	}
	
	@Test
	void TestLoadClassFileDirectly() {
		ByteClassLoader byteClassLoader = new ByteClassLoader(this.getClass().getClassLoader());
		
		boolean noError = false;
		Class<?> helloWorldClass = null;
		try {
			helloWorldClass = byteClassLoader.loadClass("test.HelloWorld", classInBytes, false);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		Assertions.assertNotNull(helloWorldClass);
		
		Method mainMethod = null;
		try {
			mainMethod = helloWorldClass.getMethod("main", String[].class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		Assertions.assertNotNull(mainMethod);
		
		noError = false;
		String[] args = null;
		try {
			mainMethod.invoke(null, (Object)args);
			noError = true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Assertions.assertTrue(noError);
	}
	
	@Test
	void TestLoadClass() {
		ByteClassLoader byteClassLoader = new ByteClassLoader(this.getClass().getClassLoader());
		
		byteClassLoader.loadSingleFileDataInBytes(classInBytes, "test/HelloWorld.class");
		
		Class<?> helloWorldClass = null;
		try {
			helloWorldClass = byteClassLoader.loadClass("test.HelloWorld");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Assertions.assertNotNull(helloWorldClass);
		
		Method mainMethod = null;
		try {
			mainMethod = helloWorldClass.getMethod("main", String[].class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		Assertions.assertNotNull(mainMethod);
		
		boolean noError = false;
		String[] args = null;
		try {
			mainMethod.invoke(null, (Object)args);
			noError = true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Assertions.assertTrue(noError);
	}
	
	@Test
	void TestLoadClassJarDirectly() {
		ByteClassLoader byteClassLoader = new ByteClassLoader(this.getClass().getClassLoader());
		
		boolean noError = false;
		Class<?> helloWorldClass = null;
		try {
			helloWorldClass = byteClassLoader.loadClass("test.HelloWorld", jarInBytes, true);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		Assertions.assertNotNull(helloWorldClass);
		
		Method mainMethod = null;
		try {
			mainMethod = helloWorldClass.getMethod("main", String[].class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		Assertions.assertNotNull(mainMethod);
		
		noError = false;
		String[] args = null;
		try {
			mainMethod.invoke(null, (Object)args);
			noError = true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Assertions.assertTrue(noError);
	}
	
	@Test
	void TestLoadJar() {
		ByteClassLoader byteClassLoader = new ByteClassLoader(this.getClass().getClassLoader());
		
		boolean noError = false;
		try {
			byteClassLoader.loadJarDataInBytes(jarInBytes);
			noError = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assertions.assertTrue(noError);
		
		Class<?> helloWorldClass = null;
		try {
			helloWorldClass = byteClassLoader.loadClass("test.HelloWorld");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Assertions.assertNotNull(helloWorldClass);
		
		Method mainMethod = null;
		try {
			mainMethod = helloWorldClass.getMethod("main", String[].class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		Assertions.assertNotNull(mainMethod);
		
		noError = false;
		String[] args = null;
		try {
			mainMethod.invoke(null, (Object)args);
			noError = true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Assertions.assertTrue(noError);
		
		InputStream inStream = byteClassLoader.getResourceAsStream("test/msg.properties");
		Properties prop = new Properties();
		try {
			prop.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assertions.assertEquals(prop.getProperty("msg"), "Hello World");
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Usage: class testJarFile");
			return;
		}
		
		Path jarFilePath = Paths.get(args[0]);
		if (!Files.isRegularFile(jarFilePath)) {
			System.out.println(jarFilePath.toAbsolutePath() + " does not exist!");
			return;
		}
		System.out.println(Hex.encodeHexString(Files.readAllBytes(jarFilePath), true));
	}
}
