package io.github.hengyunabc.dumpclass;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

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

	@Option(name = "-p", aliases = "--pid", required = true, usage = "pid")
	public int pid;

	@Option(name = "-o", aliases = "--outputDir", usage = "outputDir")
	public String outputDir;

	@Option(name = "-c", aliases = "--classLoaderPrefix", usage = "classLoaderPrefix")
	public boolean classLoaderPrefix = false;

	@Option(name = "--noStat", usage = "do not print dump stat")
	public boolean noStat = false;

	@Option(name = "--sensitive", usage = "class name wildcard match sensitive. In mac os, try this option.")
	public boolean sensitive = false;

	@Argument(required = true, usage = "class name wildcard match pattern", metaVar = "pattern")
	private String pattern;

	public static void main(String[] args) throws Exception {
		// check if need to reluanch
		checkRelaunch(args);
		System.exit(new DumpMain().doMain(args));
	}

	public int doMain(String[] args) throws Exception {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
			run();
			return 0;
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("Usage: ");
			parser.printUsage(System.err);
			System.out.println();
			System.err.println("Example: ");
			System.err.println("  java -jar dumpclass.jar -p 4345 *StringUtils");
			System.err.println("  java -jar dumpclass.jar -p 4345 -o /tmp/dump *StringUtils");
			System.err.println("  java -jar dumpclass.jar -p 4345 -o /tmp/dump --classLoaderPrefix  *StringUtils");
			System.err.println("Use the specified sa-jdi.jar:");
			System.err.println(
					"  java -cp \'./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar\' io.github.hengyunabc.dumpclass.DumpMain 4345 *StringUtils");
			return 1;
		}
	}

	private void run() throws Exception {
		ClassLoader classLoader = DumpMain.class.getClassLoader();

		DumpWrapperFilterConfig.setPattern(this.pattern);
		if (this.outputDir != null) {
			DumpWrapperFilterConfig.setOutputDirectory(this.outputDir);
		}
		DumpWrapperFilterConfig.setSensitive(sensitive);

		if (!noStat) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					// print stat
					System.out.println("Dumped classes counter: " + DumpWrapperFilterConfig.getDumpedCounter());
					System.out.println("Output directory: "
							+ new File(DumpWrapperFilterConfig.getOutputDirectory()).getAbsolutePath());
					if (!DumpWrapperFilterConfig.duplicateClasses.isEmpty()) {
						System.out.println("DuplicateClasses size: " + DumpWrapperFilterConfig.duplicateClasses.size());
						if (!classLoaderPrefix) {
							System.out.println(
									"Dumped Classes contain duplicate classes, please add --classLoaderPrefix command option, otherwise the class with the same name will be overwritten.");
							System.out.println("Dumped Classes: ");
							for (String clazz : DumpWrapperFilterConfig.duplicateClasses) {
								System.out.println(clazz);
							}
						}
					}

					if (!DumpWrapperFilterConfig.overWriteFiles.isEmpty()) {
						System.out.println("Over write files size: " + DumpWrapperFilterConfig.overWriteFiles.size());
						System.out.println("Over write files: ");
						for (String file : DumpWrapperFilterConfig.overWriteFiles) {
							System.out.println(file);
						}
					}
				}
			}));
		}

		System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "io.github.hengyunabc.dumpclass.DumpWrapperFilter");

		Method mainMethod = classLoader.loadClass("sun.jvm.hotspot.tools.jcore.ClassDump").getMethod("main",
				String[].class);

		// sun.jvm.hotspot.tools.jcore.ClassDump.main(new String[] { pid });
		mainMethod.invoke(null, new Object[] { new String[] { "" + pid } });
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
				urls.add(url);
			}

			urls.add(file.toURI().toURL());

			URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]),
					ClassLoader.getSystemClassLoader().getParent());

			Class<?> startClass = classLoader.loadClass(DumpMain.class.getName());
			final Method mainMethod = startClass.getMethod("main", String[].class);
			if (!mainMethod.isAccessible()) {
				mainMethod.setAccessible(true);
			}

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

			Thread.sleep(Long.MAX_VALUE);
		}
	}
}
