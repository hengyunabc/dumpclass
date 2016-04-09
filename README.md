# Dumpclass
Dump classes from running JVM process by sa-jdi.jar.

* Support wildcard match

* Support multi classloader


### Download dumpclass.jar

You can download dumpclass.jar from maven center.

http://search.maven.org/#search%7Cga%7C1%7Cdumpclass

### Usage

```
Usage:
 java -jar dumpclass.jar <pid> <pattern> [outputDir] <--classLoaderPrefix>

pattern: support ? * wildcard match.
outputDir: default outputDir is current directory.
--classLoaderPrefix: every classloader has it's own output directory. When multi classloaders load same name classes, try this.

Example:
 java -jar dumpclass.jar 4345 *StringUtils
 java -jar dumpclass.jar 4345 *StringUtils /tmp
 java -jar dumpclass.jar 4345 *StringUtils /tmp --classLoaderPrefix

Use the specified sa-jdi.jar:
 java -cp "./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar" io.github.hengyunabc.dumpclass.DumpMain <pid> <pattern> [outputDir]
```

### complie dumpclass.jar

```bash
mvn clean package
ls -alh target
```

##License

Apache License V2
