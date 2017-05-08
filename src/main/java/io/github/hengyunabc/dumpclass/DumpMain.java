package io.github.hengyunabc.dumpclass;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 
 * <blockquote>
 * 
 * <pre>
 * 
 * String filterClassName = System.getProperty("sun.jvm.hotspot.tools.jcore.filter",
 *         "sun.jvm.hotspot.tools.jcore.PackageNameFilter");
 * String dirName = System.getProperty("sun.jvm.hotspot.tools.jcore.outputDir", ".");
 * 
 * PackageNameFilter:
 * 
 * System.getProperty("sun.jvm.hotspot.tools.jcore.PackageNameFilter.pkgList")
 * 
 * </pre>
 * 
 * </blockquote>
 * 
 * @author hengyunabc
 * 
 */
public class DumpMain {

	static String usage = "Usage:\n" + " java -jar dumpclass.jar <pid> <pattern> [outputDir] <--classLoaderPrefix>\n\n"
			+ "pattern: support ? * wildcard match.\n" + "outputDir: default outputDir is current directory.\n"
			+ "--classLoaderPrefix: every classloader has it's own output directory. When multi classloaders load same name classes, try this.\n\n"
			+ "Example:\n" + " java -jar dumpclass.jar 4345 *StringUtils\n"
			+ " java -jar dumpclass.jar 4345 *StringUtils /tmp\n"
			+ " java -jar dumpclass.jar 4345 *StringUtils /tmp --classLoaderPrefix \n\n"
			+ "Use the specified sa-jdi.jar:\n"
			+ " java -cp \"./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar\" io.github.hengyunabc.dumpclass.DumpMain <pid> <pattern> [outputDir] \n\n";

	public static void main(String[] args)
			throws MalformedURLException, SecurityException, NoSuchMethodException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException, InterruptedException {

		// when sa-jdi not exist in classpath, create a new classloader with sa-jdi in java_home
		checkRelaunch(args);

		ClassLoader classLoader = DumpMain.class.getClassLoader();

		Queue<String> argsQueue = new LinkedList<String>();
		if (args != null) {
			for (String arg : args) {
				if (arg.equals("--classLoaderPrefix") || arg.equals("-classLoaderPrefix")) {
					DumpWrapperFilterConfig.setClassLoaderPrefix(true);
				} else {
					argsQueue.add(arg);
				}
			}
		}

		if (argsQueue.isEmpty()) {
			// print usage
			System.out.println(usage);
			System.exit(-1);
		}

		String pid = argsQueue.poll();

		if (!argsQueue.isEmpty()) {
			DumpWrapperFilterConfig.setPattern(argsQueue.poll());
		}

		if (!argsQueue.isEmpty()) {
			DumpWrapperFilterConfig.setOutputDirectory(argsQueue.poll());
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				// print stat
				System.out.println("dumped classes counter: " + DumpWrapperFilterConfig.getDumpedCounter());
				System.out.println("output directory: "
						+ new File(DumpWrapperFilterConfig.getOutputDirectory()).getAbsolutePath());
			}
		}));

		System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "io.github.hengyunabc.dumpclass.DumpWrapperFilter");

		Method mainMethod = classLoader.loadClass("sun.jvm.hotspot.tools.jcore.ClassDump").getMethod("main",
				String[].class);

		// sun.jvm.hotspot.tools.jcore.ClassDump.main(new String[] { pid });
		mainMethod.invoke(null, new Object[] { new String[] { pid } });
	}

	private static void checkRelaunch(final String[] args)
			throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		try {
			DumpMain.class.getClassLoader().loadClass("sun.jvm.hotspot.tools.jcore.ClassDump");
			return;
		} catch (ClassNotFoundException e) {
			System.out.println("can not find sa-jdi.jar from classpath, try to load it from java.home.");

			String javaHome = System.getProperty("java.home");
			if (javaHome == null) {
				javaHome = System.getenv("JAVA_HOME");
			}

			if (javaHome == null) {
				System.out.println("can not get java.home, can not load sa-jdi.jar.");
				System.exit(-1);
			}

			File file = new File(javaHome + "/lib/sa-jdi.jar");
			if (!file.exists()) {
				// java.home maybe jre
				file = new File(javaHome + "/../lib/sa-jdi.jar");
				if (!file.exists()) {
					System.out.println("can not find lib/sa-jdi.jar from java.home: " + javaHome);
				}
			}

			// build a new classloader, a trick.
			List<URL> urls = new ArrayList<URL>();
			for (URL url : ((URLClassLoader) DumpMain.class.getClassLoader()).getURLs()) {
				//add urls in original classloader to the new one
				urls.add(url);
			}

			//add sa-jdi to the new classloader
			urls.add(file.toURI().toURL());

			URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]),
					ClassLoader.getSystemClassLoader().getParent());

			Class<?> startClass = classLoader.loadClass(DumpMain.class.getName());
			final Method mainMethod = startClass.getMethod("main", String[].class);
			if (!mainMethod.isAccessible()) {
				mainMethod.setAccessible(true);
			}

			//code is in fact running here
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mainMethod.invoke(null, new Object[] { args });
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}

			}).start();

			//sleep util user quit, prevent main method running
			Thread.currentThread().sleep(Long.MAX_VALUE);
		}
	}
}
