package org.csdgn.plugin;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.csdgn.utils.Files;

public class PluginClassLoader extends ClassLoader {
	private static class AccessList {
		private static boolean matchWildcard(String pattern, String test) {
			pattern = pattern.replace(".", "\\.");
			pattern = pattern.replace("**", ".+");
			pattern = pattern.replace("*", "[^.]*");
			return test.matches(pattern);
		}

		private HashSet<String> accessList = new HashSet<String>();

		public void addRule(String rule) {
			accessList.add(rule);
		}

		public boolean matches(String test) {
			for(String arule : accessList) {
				if(matchWildcard(arule, test)) {
					return true;
				}
			}
			return false;
		}

		public void reset() {
			accessList.clear();
		}
	}

	private AccessList white = new AccessList();
	private AccessList black = new AccessList();
	private boolean useWhiteList = false;
	private boolean useBlackList = false;

	public PluginClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * Adds a given pattern to the black access list to allow loading. Anything
	 * defined on the black list will be blocked from loading via
	 * {@link SecurityException} . The pattern is a simple star, double star
	 * setup. For example: <code>foo.**</code> matches <code>foo.bar</code>,
	 * <code>foo.qux.bar</code>, and so on. Where as <code>foo.*</code> would
	 * match <code>foo.bar</code> but not <code>foo.qux.bar</code>.
	 */
	public void blacklist(String pkgPattern) {
		black.addRule(pkgPattern);
		useBlackList = true;
	}

	public void defineClass(ClassInfo info) throws IOException {
		ClassOrigin co = info.getOrigin();
		if(co.inJar) {
			defineJar(info);
			return;
		}

		byte[] data = Files.getFileContents(co.file);
		String name = info.thisName.replace('/', '.');
		super.defineClass(name, data, 0, data.length);
		white.addRule(name);
	}

	public void defineJar(ClassInfo info) throws IOException {
		File jar = info.getOrigin().file;
		ZipFile zip = new ZipFile(jar, ZipFile.OPEN_READ);

		try {
			for(ClassInfo dep : PluginService.getDependancies(info)) {
				ZipEntry e = zip.getEntry(dep.getOrigin().path);
				byte[] data = Files.getAndClose(zip.getInputStream(e));
				String name = dep.thisName.replace('/', '.');
				super.defineClass(name, data, 0, data.length);
				white.addRule(name);
			}
		} finally {
			zip.close();
		}
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		/* Loading doesn't happen very often, so list checking here is perfectly okay. :) */
		
		/*
		 * Do not even try negotiating list conflicts. Full on being somebody
		 * else's problem at this point.
		 */
		if(useWhiteList && !white.matches(name)) {
			throw new SecurityException("Loading of class '" + name + "' not allowed by classloader whitelist.");
		}
		
		if(useBlackList && black.matches(name)) {
			throw new SecurityException("Loading of class '" + name + "' not allowed by classloader blacklist.");
		}
		
		return super.loadClass(name);
	}

	/**
	 * Adds a given pattern to the black access list to allow loading. Anything
	 * defined on the black list will be blocked from loading via
	 * {@link SecurityException} . The pattern is a simple star, double star
	 * setup. For example: <code>foo.**</code> matches <code>foo.bar</code>,
	 * <code>foo.qux.bar</code>, and so on. Where as <code>foo.*</code> would
	 * match <code>foo.bar</code> but not <code>foo.qux.bar</code>.
	 */
	public void whitelist(String pkgPattern) {
		white.addRule(pkgPattern);
		useWhiteList = true;
	}

	public void resetAccessLists() {
		white.reset();
		black.reset();
		useWhiteList = false;
		useBlackList = false;
	}

}
