package io.github.hengyunabc.dumpclass;

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

    static String usage = "Usage:\n"
            + " java -cp \"./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar\" io.github.hengyunabc.dumpclass.DumpMain <pid> <pattern> [outputDir] \n\n"
            + "pattern: support ? * wildcard match.\n" + "outputDir: default outputDir is current directory.\n\n"
            + "Example:\n"
            + " java -cp \"./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar\" io.github.hengyunabc.dumpclass.DumpMain 4345 *StringUtils\n"
            + " java -cp \"./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar\" io.github.hengyunabc.dumpclass.DumpMain 4345 *StringUtils /tmp";

    public static void main(String[] args) {
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

        System.setProperty("sun.jvm.hotspot.tools.jcore.filter", WildcardFilter.class.getName());
        sun.jvm.hotspot.tools.jcore.ClassDump.main(new String[] { args[0] });
    }
}
