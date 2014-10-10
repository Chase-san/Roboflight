package org.csdgn.plugin;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginClassLoader extends ClassLoader {
	private static final int IO_BUFFER_SIZE = 16384;

	private static byte[] getAndClose(InputStream stream) throws IOException {
		if(stream != null) {
			try {
				final BufferedInputStream input = new BufferedInputStream(stream);
				final ByteArrayOutputStream buffer = new ByteArrayOutputStream(); /* magic */
				final byte[] reader = new byte[IO_BUFFER_SIZE];
				int r = 0;
				while((r = input.read(reader, 0, IO_BUFFER_SIZE)) != -1) {
					buffer.write(reader, 0, r);
				}
				buffer.flush();
				return buffer.toByteArray();
			} finally {
				stream.close();
			}
		}
		return null;
	}

	private static byte[] getFileContents(File file) {
		try {
			return getAndClose(new FileInputStream(file));
		} catch(IOException e) {
		}
		return null;
	}

	public PluginClassLoader(ClassLoader parent) {
		super(parent);
	}

	public void defineClass(ClassInfo info) throws IOException {
		ClassOrigin co = info.getOrigin();
		if(co.inJar) {
			defineJar(info);
			return;
		}
		byte[] data = getFileContents(co.file);
		super.defineClass(info.thisName.replace('/', '.'), data, 0, data.length);
	}

	public void defineJar(ClassInfo info) throws IOException {
		File jar = info.getOrigin().file;
		ZipFile zip = new ZipFile(jar, ZipFile.OPEN_READ);

		try {
			for(ClassInfo dep : PluginService.getDependancies(info)) {
				ZipEntry e = zip.getEntry(dep.getOrigin().path);
				byte[] data = getAndClose(zip.getInputStream(e));
				super.defineClass(dep.thisName.replace('/', '.'), data, 0, data.length);
			}
		} finally {
			zip.close();
		}
	}

}
