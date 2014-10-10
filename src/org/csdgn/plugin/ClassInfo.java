/**
 * Copyright (c) 2013-2014 Robert Maupin
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */
package org.csdgn.plugin;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A little basic class info package.
 * 
 * @author Robert Maupin
 */
public class ClassInfo {
	public final String thisName;
	public final String superName;
	private final String[] interfaceNames;
	private final String[] referenceNames;
	public final int majorVersion;
	public final int minorVersion;
	public final int codeSize;
	public final boolean isPublic;
	public final boolean isAbstract;
	public final boolean isFinal;
	public final boolean isInterface;

	private ClassOrigin origin;

	private static final int[] CONSTANT_POOL_BYTES = new int[] { 0, 0, 0, 4, 4, 8, 8, 2, 2, 4, 4, 4, 4 };

	protected ClassInfo(DataInputStream dis) throws IOException {
		int magic = dis.readInt();
		if(magic != 0xCAFEBABE) {
			throw new StreamCorruptedException();
		}

		minorVersion = dis.readUnsignedShort();
		majorVersion = dis.readUnsignedShort();

		int constant_pool_count = dis.readUnsignedShort();
		int pool_index = 1; // <--- is not a typo
		int code_index = -1;

		HashMap<Integer, String> constants = new HashMap<Integer, String>();
		ArrayList<Integer> classReferences = new ArrayList<Integer>();

		while(pool_index < constant_pool_count) {
			int tag = dis.readUnsignedByte();
			// only save the strings, the other ones do not matter
			if(tag == 1) {
				/* String */
				int length = dis.readUnsignedShort();
				byte[] data = new byte[length];
				dis.read(data);
				String str = new String(data);
				constants.put(pool_index, str);
				if("Code".equals(str)) {
					code_index = pool_index;
				}
			} else if(tag == 7) {
				/* Class Reference */
				classReferences.add(dis.readUnsignedShort());
			} else {
				dis.skip(CONSTANT_POOL_BYTES[tag]);
			}

			++pool_index;
			if(tag == 5 || tag == 6) {
				++pool_index; /*
							 * long and double take two slots in the constants
							 * pool
							 */
			}
		}
		/* convert class references */
		referenceNames = new String[classReferences.size()];

		for(int i = 0; i < classReferences.size(); ++i) {
			referenceNames[i] = constants.get(classReferences.get(i));
		}

		int flags = dis.readUnsignedShort();
		isPublic = (flags & 0x1) != 0;
		isFinal = (flags & 0x10) != 0;
		isInterface = (flags & 0x200) != 0;
		isAbstract = (flags & 0x400) != 0;
		thisName = constants.get(dis.readUnsignedShort() + 1);
		superName = constants.get(dis.readUnsignedShort() + 1);
		interfaceNames = new String[dis.readUnsignedShort()];
		for(int i = 0; i < interfaceNames.length; ++i) {

			interfaceNames[i] = constants.get(dis.readUnsignedShort() + 1);
		}
		// fields
		int fcount = dis.readUnsignedShort();
		while(fcount-- > 0) {
			dis.skip(6);
			int count = dis.readUnsignedShort();
			while(count-- > 0) {
				dis.skip(2);
				dis.skip(dis.readInt() & 0x7FFFFFFF);
			}
		}

		// methods !!! (will always be at least 1)
		int mcount = dis.readUnsignedShort();
		int tCodeSize = 0;
		while(mcount-- > 0) {
			dis.skip(6);
			int count = dis.readUnsignedShort();
			while(count-- > 0) {
				if(dis.readUnsignedShort() == code_index) {
					// CODE
					int attrlen = dis.readInt() & 0x7FFFFFFF;
					dis.skip(4);
					tCodeSize += dis.readInt() & 0x7FFFFFFF;
					dis.skip(attrlen - 8);
				} else {
					dis.skip(dis.readInt() & 0x7FFFFFFF);
				}
			}
		}
		codeSize = tCodeSize;
	}

	protected ClassInfo(InputStream in) throws IOException {
		this(new DataInputStream(new BufferedInputStream(in)));
	}

	public String[] getClassReferenceNames() {
		return referenceNames.clone();
	}

	public String[] getInterfaceNames() {
		return interfaceNames.clone();
	}

	public ClassOrigin getOrigin() {
		return origin;
	}

	@Override
	public int hashCode() {
		return thisName.hashCode();
	}

	protected void setOrigin(ClassOrigin origin) {
		this.origin = origin;
	}

	@Override
	public String toString() {
		return thisName.replace('/', '.');
	}
}
