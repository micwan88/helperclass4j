package io.github.micwan88.helperclass4j;

public class ByteClassLoader extends ClassLoader {
	private byte[] classDataInBytes = null;
	
	/**
	 * @param parent
	 */
	public ByteClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if (classDataInBytes == null)
			throw new ClassNotFoundException("classDataInBytes is empty");
		
		return defineClass(className, classDataInBytes, 0, classDataInBytes.length);
	}

	public Class<?> loadClass(String className, byte[] classDataInBytes) throws ClassNotFoundException {
		this.classDataInBytes = classDataInBytes;
		return this.loadClass(className);
	}
	
	public void loadClassDataInBytes(byte[] classDataInBytes) {
		this.classDataInBytes = classDataInBytes;
	}
}
