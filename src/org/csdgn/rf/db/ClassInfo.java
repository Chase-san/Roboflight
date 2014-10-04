/**
 * Copyright (c) 2013 Robert Maupin
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
package org.csdgn.rf.db;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * A little basic class info package.
 * 
 * @author Robert Maupin
 */
public class ClassInfo {
	public static ClassInfo getClassInfo(InputStream cis) throws IOException {
		DataInputStream dis = new DataInputStream(new BufferedInputStream(cis));
		int magic = dis.readInt();
		if(magic != 0xCAFEBABE) {
			return null;
		}

		ClassInfo info = new ClassInfo();
		info.minorVersion = dis.readUnsignedShort();
		info.majorVersion = dis.readUnsignedShort();

		int constant_pool_count = dis.readUnsignedShort();
		int pool_index = 1; // <--- is not a typo
		int code_index = -1;
		while(pool_index < constant_pool_count) {
			int tag = dis.readUnsignedByte();
			// only save the strings, the other ones do not matter
			if(tag == 1) {
				int length = dis.readUnsignedShort();
				byte[] data = new byte[length];
				dis.read(data);
				String str = new String(data);
				info.constants.put(pool_index, str);
				if("Code".equals(str)) {
					code_index = pool_index;
				}
			} else {
				dis.skip(CONSTANT_POOL_BYTES[tag]);
			}

			++pool_index;
			if(tag == 5 || tag == 6) {
				++pool_index;
			}
		}
		int flags = dis.readUnsignedShort();
		info.isPublic = (flags & 0x1) != 0;
		info.isFinal = (flags & 0x10) != 0;
		info.isInterface = (flags & 0x200) != 0;
		info.isAbstract = (flags & 0x400) != 0;
		info.thisName = info.constants.get(dis.readUnsignedShort() + 1);
		info.superName = info.constants.get(dis.readUnsignedShort() + 1);
		info.interfaceNames = new String[dis.readUnsignedShort()];
		for(int i = 0; i < info.interfaceNames.length; ++i) {
			info.interfaceNames[i] = info.constants.get(dis.readUnsignedShort() + 1);
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
		while(mcount-- > 0) {
			dis.skip(6);
			int count = dis.readUnsignedShort();
			while(count-- > 0) {
				if(dis.readUnsignedShort() == code_index) {
					// CODE
					int attrlen = dis.readInt() & 0x7FFFFFFF;
					dis.skip(4);
					info.codeSize += dis.readInt() & 0x7FFFFFFF;
					dis.skip(attrlen - 8);
				} else {
					dis.skip(dis.readInt() & 0x7FFFFFFF);
				}
			}
		}

		return info;
	}

	public String thisName;
	public String superName;
	public String[] interfaceNames;
	public HashMap<Integer, String> constants;
	public int majorVersion;
	public int minorVersion;
	public int codeSize;
	public boolean isPublic;
	public boolean isAbstract;
	public boolean isFinal;
	public boolean isInterface;

	public File parent;

	private static final int[] CONSTANT_POOL_BYTES = new int[] { 0, 0, 0, 4, 4, 8, 8, 2, 2, 4, 4, 4, 4 };

	private ClassInfo() {
		constants = new HashMap<Integer, String>();
	}

	@Override
	public String toString() {
		return thisName.replace('/', '.');
	}
}
