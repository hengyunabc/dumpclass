# Dumpclass
Dump classes from running JVM process by sa-jdi.jar.

Support wildcard match.


### Download dumpclass.jar


```
Usage:
 java -cp "./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar" io.github.hengyunabc.dumpclass.DumpMain <pid> <pattern> [outputDir]

pattern: support ? * wildcard match.
outputDir: default outputDir is current directory.

Example:
 java -cp "./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar" io.github.hengyunabc.dumpclass.DumpMain 4345 *StringUtils
 java -cp "./dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar" io.github.hengyunabc.dumpclass.DumpMain 4345 *StringUtils /tmp
```
 
### complie dumpclass.jar
 
```bash
mvn clean package
ls -alh target
```
