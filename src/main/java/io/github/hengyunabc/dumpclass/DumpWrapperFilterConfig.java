package io.github.hengyunabc.dumpclass;

/**
 * 
 * @author hengyunabc
 *
 */
public class DumpWrapperFilterConfig {
	static boolean classLoaderPrefix;

	static String pattern;

	static String outputDirectory = ".";

	static int dumpedCounter = 0;

	public static void setClassLoaderPrefix(boolean classLoaderPrefix) {
		DumpWrapperFilterConfig.classLoaderPrefix = classLoaderPrefix;
	}

	public static void setOutputDirectory(String outputDirectory) {
		DumpWrapperFilterConfig.outputDirectory = outputDirectory;

	}

	public static void setPattern(String pattern) {
		DumpWrapperFilterConfig.pattern = pattern;
	}

	public static int getDumpedCounter() {
		return dumpedCounter;
	}

	public static String getOutputDirectory() {
		return outputDirectory;
	}
}
