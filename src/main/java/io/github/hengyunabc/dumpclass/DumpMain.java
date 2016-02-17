package io.github.hengyunabc.dumpclass;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

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

    static String usage = "Usage:\n" + " java -jar dumpclass.jar <pid> <pattern> [outputDir] \n\n"
            + "pattern: support ? * wildcard match.\n" + "outputDir: default outputDir is current directory.\n\n"
            + "Example:\n" + " java -jar dumpclass.jar 4345 *StringUtils\n"
            + " java -jar dumpclass.jar 4345 *StringUtils /tmp\n\n" + "Use the specified sa-jdi.jar:\n"
            + " java -cp \"./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar\" io.github.hengyunabc.dumpclass.DumpMain <pid> <pattern> [outputDir] \n\n";

    public static void main(String[] args) throws MalformedURLException, SecurityException, NoSuchMethodException,
            ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (args.length >= 3) {
            System.setProperty(WildcardFilter.PROPERTY_KEY, args[1]);
            System.setProperty("sun.jvm.hotspot.tools.jcore.outputDir", args[2]);
        } else if (args.length == 2) {
            System.setProperty(WildcardFilter.PROPERTY_KEY, args[1]);
        } else if (args.length == 1) {
            // skip
        } else {
            // print usage
            System.out.println(usage);
            System.exit(-1);
        }

        ClassLoader runClassLoader = null;
        try {
            DumpMain.class.getClassLoader().loadClass("sun.jvm.hotspot.tools.jcore.ClassDump");
            runClassLoader = DumpMain.class.getClassLoader();
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
            runClassLoader = classLoader;
        }

        System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "io.github.hengyunabc.dumpclass.WildcardFilter");

        Method mainMethod = runClassLoader.loadClass("sun.jvm.hotspot.tools.jcore.ClassDump").getMethod("main",
                String[].class);

        // sun.jvm.hotspot.tools.jcore.ClassDump.main(new String[] { args[0] });
        mainMethod.invoke(null, new Object[] { new String[] { args[0] } });

    }
}
