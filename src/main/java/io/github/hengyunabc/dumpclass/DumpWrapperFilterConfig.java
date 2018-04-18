package io.github.hengyunabc.dumpclass;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import sun.jvm.hotspot.oops.InstanceKlass;

/**
 * 
 * @author hengyunabc
 *
 */
public class DumpWrapperFilterConfig {
	static boolean classLoaderPrefix;

	static String pattern;
	static boolean sensitive = false;

	static String outputDirectory = ".";

	static int dumpedCounter = 0;

	static Set<String> dumpedClasses = new LinkedHashSet<String>();
	static Set<String> duplicateClasses = new LinkedHashSet<String>();

	static Set<String> overWriteFiles = new LinkedHashSet<String>();

	public static void beforeDump(InstanceKlass kls) {
		dumpedCounter++;
		String klassName = kls.getName().asString();
		String className = klassName.replace('/', '.');
		if (dumpedClasses.contains(className)) {
			duplicateClasses.add(className);
		}
		dumpedClasses.add(className);
	}

	public static void beforeWrite(File f, InstanceKlass kls) {
		if (f.exists()) {
			overWriteFiles.add(f.getAbsolutePath());
		}
	}

	public static void setClassLoaderPrefix(boolean classLoaderPrefix) {
		DumpWrapperFilterConfig.classLoaderPrefix = classLoaderPrefix;
	}

	public static void setOutputDirectory(String outputDirectory) {
		DumpWrapperFilterConfig.outputDirectory = outputDirectory;

	}

	public static void setPattern(String pattern) {
		DumpWrapperFilterConfig.pattern = pattern;
	}

	public static void setSensitive(boolean sensitive) {
		DumpWrapperFilterConfig.sensitive = sensitive;
	}

	public static int getDumpedCounter() {
		return dumpedCounter;
	}

	public static String getOutputDirectory() {
		return outputDirectory;
	}
}
