package io.github.hengyunabc.dumpclass;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.tools.jcore.ClassFilter;
import sun.jvm.hotspot.tools.jcore.ClassWriter;

/**
 * this filter will dump classes file to outputDirectory.
 * 
 * @author hengyunabc
 *
 */
public class DumpWrapperFilter implements ClassFilter {

	ClassFilter classFilter;

	public DumpWrapperFilter() {
		classFilter = new WildcardFilter(DumpWrapperFilterConfig.pattern);
	}

	@Override
	public boolean canInclude(InstanceKlass kls) {
		if (classFilter != null && classFilter.canInclude(kls)) {
			dumpKlass(kls);
		}

		return false;
	}

	private String getClassLoaderDirName(Oop classLoader) {
		if (classLoader == null) {
			return "BootstrapClassLoader";
		}
		return classLoader.getClass().getName() + "@" + Integer.toHexString(classLoader.hashCode());
	}

	private void dumpKlass(InstanceKlass kls) {

		DumpWrapperFilterConfig.dumpedCounter++;

		String klassName = kls.getName().asString();
		klassName = klassName.replace('/', File.separatorChar);

		File outDest = new File(DumpWrapperFilterConfig.outputDirectory);

		if (DumpWrapperFilterConfig.classLoaderPrefix) {
			outDest = new File(DumpWrapperFilterConfig.outputDirectory, getClassLoaderDirName(kls.getClassLoader()));
			// write ClassLoader into to classLoader.txt file
			if (kls.getClassLoader() != null) {
				outDest.mkdirs();
				File classLoaderInfo = new File(outDest, "classLoader.txt");
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(classLoaderInfo);
					out.write(kls.getClassLoader().toString().getBytes("UTF-8"));
					out.flush();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}

		}

		try {
			OutputStream os = null;
			int index = klassName.lastIndexOf(File.separatorChar);
			File dir = null;
			if (index != -1) {
				String dirName = klassName.substring(0, index);
				dir = new File(outDest, dirName);
			} else {
				dir = outDest;
			}
			dir.mkdirs();
			File f = new File(dir, klassName.substring(index + 1) + ".class");
			f.createNewFile();
			os = new BufferedOutputStream(new FileOutputStream(f));
			try {
				ClassWriter cw = new ClassWriter(kls, os);
				cw.write();
			} finally {
				if (os != null) {
					os.close();
				}
			}
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}
}
