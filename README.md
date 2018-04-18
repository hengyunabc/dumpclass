# Dumpclass
Dump classes from running JVM process by sa-jdi.jar.

* Support wildcard match

* Support multi classloader


### Download dumpclass.jar

You can download dumpclass.jar from maven center.

http://search.maven.org/#search%7Cga%7C1%7Cdumpclass

```bash
wget http://search.maven.org/remotecontent?filepath=io/github/hengyunabc/dumpclass/0.0.1/dumpclass-0.0.1.jar -O dumpclass.jar
```

### Usage

```
Usage:
 pattern                  : class name wildcard match pattern
 --noStat                 : do not print dump stat (default: false)
 --sensitive              : class name wildcard match sensitive. In mac os, try
                            this option. (default: false)
 -c (--classLoaderPrefix) : classLoaderPrefix (default: false)
 -o (--outputDir) VAL     : outputDir
 -p (--pid) N             : pid

Example:
  java -jar dumpclass.jar -p 4345 *StringUtils
  java -jar dumpclass.jar -p 4345 -o /tmp/dump *StringUtils
  java -jar dumpclass.jar -p 4345 -o /tmp/dump --classLoaderPrefix  *StringUtils
Use the specified sa-jdi.jar:
  java -cp './dumpclass.jar:$JAVA_HOME/lib/sa-jdi.jar' io.github.hengyunabc.dumpclass.DumpMain 4345 *StringUtils
```

### Trouble

* Try to use sudo

```
Error attaching to process: sun.jvm.hotspot.debugger.DebuggerException: Can't attach to the process. Could be caused by an incorrect pid or lack of privileges.
sun.jvm.hotspot.debugger.DebuggerException: sun.jvm.hotspot.debugger.DebuggerException: Can't attach to the process. Could be caused by an incorrect pid or lack of privileges.
	at sun.jvm.hotspot.debugger.bsd.BsdDebuggerLocal$BsdDebuggerLocalWorkerThread.execute(BsdDebuggerLocal.java:169)
	at sun.jvm.hotspot.debugger.bsd.BsdDebuggerLocal.attach(BsdDebuggerLocal.java:287)
	at sun.jvm.hotspot.HotSpotAgent.attachDebugger(HotSpotAgent.java:671)
	at sun.jvm.hotspot.HotSpotAgent.setupDebuggerDarwin(HotSpotAgent.java:659)
	at sun.jvm.hotspot.HotSpotAgent.setupDebugger(HotSpotAgent.java:341)
	at sun.jvm.hotspot.HotSpotAgent.go(HotSpotAgent.java:304)
	at sun.jvm.hotspot.HotSpotAgent.attach(HotSpotAgent.java:140)
	at sun.jvm.hotspot.tools.Tool.start(Tool.java:185)
	at sun.jvm.hotspot.tools.Tool.execute(Tool.java:118)
	at sun.jvm.hotspot.tools.jcore.ClassDump.main(ClassDump.java:180)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at io.github.hengyunabc.dumpclass.DumpMain.run(DumpMain.java:132)
	at io.github.hengyunabc.dumpclass.DumpMain.doMain(DumpMain.java:68)
	at io.github.hengyunabc.dumpclass.DumpMain.main(DumpMain.java:61)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at io.github.hengyunabc.dumpclass.DumpMain$2.run(DumpMain.java:184)
	at java.lang.Thread.run(Thread.java:745)
Caused by: sun.jvm.hotspot.debugger.DebuggerException: Can't attach to the process. Could be caused by an incorrect pid or lack of privileges.
	at sun.jvm.hotspot.debugger.bsd.BsdDebuggerLocal.attach0(Native Method)
	at sun.jvm.hotspot.debugger.bsd.BsdDebuggerLocal.access$100(BsdDebuggerLocal.java:65)
	at sun.jvm.hotspot.debugger.bsd.BsdDebuggerLocal$1AttachTask.doit(BsdDebuggerLocal.java:278)
	at sun.jvm.hotspot.debugger.bsd.BsdDebuggerLocal$BsdDebuggerLocalWorkerThread.run(BsdDebuggerLocal.java:144)
```

* Make sure use the same jdk version.

```
Attaching to process ID 53577, please wait...
Exception in thread "main" java.lang.reflect.InvocationTargetException
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at io.github.hengyunabc.dumpclass.DumpMain.main(DumpMain.java:101)
Caused by: java.lang.InternalError: void* type hasn't been seen when parsing int*
	at sun.jvm.hotspot.HotSpotTypeDataBase.recursiveCreateBasicPointerType(HotSpotTypeDataBase.java:721)
	at sun.jvm.hotspot.HotSpotTypeDataBase.lookupType(HotSpotTypeDataBase.java:134)
	at sun.jvm.hotspot.HotSpotTypeDataBase.lookupOrCreateClass(HotSpotTypeDataBase.java:631)
	at sun.jvm.hotspot.HotSpotTypeDataBase.createType(HotSpotTypeDataBase.java:751)
	at sun.jvm.hotspot.HotSpotTypeDataBase.readVMTypes(HotSpotTypeDataBase.java:195)
	at sun.jvm.hotspot.HotSpotTypeDataBase.<init>(HotSpotTypeDataBase.java:89)
	at sun.jvm.hotspot.HotSpotAgent.setupVM(HotSpotAgent.java:403)
	at sun.jvm.hotspot.HotSpotAgent.go(HotSpotAgent.java:305)
	at sun.jvm.hotspot.HotSpotAgent.attach(HotSpotAgent.java:140)
	at sun.jvm.hotspot.tools.Tool.start(Tool.java:185)
	at sun.jvm.hotspot.tools.Tool.execute(Tool.java:118)
	at sun.jvm.hotspot.tools.jcore.ClassDump.main(ClassDump.java:180)
	... 5 more
```

* One class loaded by multi ClassLoader

Try to use HSDB. After attach java process, "Tools", "Class Browser".

```bash
sudo java -classpath "$JAVA_HOME/lib/sa-jdi.jar" sun.jvm.hotspot.HSDB
```

### complie dumpclass.jar

```bash
mvn clean package
ls -alh target
```

## License

Apache License V2
