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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestByteClassLoader {
	final String TEST_JAR_HEX_CODE = "504b0304140008080800ea9b7d4d000000000000000000000000140004004d4554412d494e462f4d414e49464553542e4d46feca0000f34dcccb4c4b2d2ed10d4b2d2acecccfb35230d433e0e5e2e50200504b0708b27f02ee1b00000019000000504b0304140008080800e19b7d4d00000000000000000000000015000000746573742f48656c6c6f576f726c642e636c6173736d50bb4ec340109c230fc78e212f12de90502514b8a0a008a24142141620058582ea929cc245671f722e487c16142051f0017c1462ed44b240b962e7766f6776f6be7f3ebf009ce0d0c20a43c988a9f1ae8452fa5e476a6421cb509ef067ee291e8ebd9bc1440c0d43fe4c86d29c3364da9d3e43f6428f84830c6c1739e449c797a1b89e050311ddf181120c555f0fb9eaf348c6f9a298358f72ca50f1ff8dedd253c065c8d0683ff8e9f89e896438ee76fa362aa859a8fe31d77b991a11b858479d8ce919f9accfc9527bb7c434c4173ce816b0c1504cc63593790eb6b063619ba1b6a4dfc52ef618aca7b8a4c854bdbdcc1379e6d198d6a92db1cce0f4f42c1a8a4b192f5e4a773d8e7bd10259467c56e8467f48d1a2cc236484b9a30f145e936787623e296650a4e8ce1b0857096daca1b4209f2662547b43b9da78c7662ae010c6a40249a52236f67140c8d04c3a5bbf504b0708bf3374e4520100001f020000504b03041400080808003c9a7d4d00000000000000000000000013000000746573742f6d73672e70726f70657274696573cb2d4eb7f548cdc9c95708cf2fca490100504b0708c0497b27110000000f000000504b01021400140008080800ea9b7d4db27f02ee1b000000190000001400040000000000000000000000000000004d4554412d494e462f4d414e49464553542e4d46feca0000504b01021400140008080800e19b7d4dbf3374e4520100001f020000150000000000000000000000000061000000746573742f48656c6c6f576f726c642e636c617373504b010214001400080808003c9a7d4dc0497b27110000000f0000001300000000000000000000000000f6010000746573742f6d73672e70726f70657274696573504b05060000000003000300ca000000480200000000";
	
	private byte[] jarInBytes = null;
	
	@Before
	public void loadJarToBytes() throws DecoderException {
		//Load a jar with a class and resource
		jarInBytes = Hex.decodeHex(TEST_JAR_HEX_CODE);
	}
	
	@Test
	public void startTest() {
		ByteClassLoader byteClassLoader = new ByteClassLoader(this.getClass().getClassLoader());
		byteClassLoader.initClassDataInBytes(jarInBytes, true);
		
		Class<?> helloWorldClass = null;
		try {
			helloWorldClass = byteClassLoader.loadClass("test.HelloWorld");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(helloWorldClass);
		
		Method mainMethod = null;
		try {
			mainMethod = helloWorldClass.getMethod("main", String[].class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(mainMethod);
		
		boolean noError = false;
		String[] args = null;
		try {
			mainMethod.invoke(null, (Object)args);
			noError = true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(noError);
		
		InputStream inStream = byteClassLoader.getResourceAsStream("test/msg.properties");
		Properties prop = new Properties();
		try {
			prop.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(prop.getProperty("msg"), "Hello World");
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
