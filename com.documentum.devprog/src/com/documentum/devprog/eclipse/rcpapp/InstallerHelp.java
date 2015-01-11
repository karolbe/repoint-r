/*******************************************************************************
 * Copyright (c) 2005-2006, EMC Corporation 
 * All rights reserved.

 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided that 
 * the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * - Neither the name of the EMC Corporation nor the names of its 
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *******************************************************************************/

/*
 * Created on Sep 26, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.rcpapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class InstallerHelp {

	public static File copyFile(File srcFile, File targetFolder)
			throws IOException {
		BufferedInputStream bufIn = new BufferedInputStream(
				new FileInputStream(srcFile));

		String targetFilePath = targetFolder.getAbsolutePath() + File.separator
				+ srcFile.getName();

		BufferedOutputStream bufOut = new BufferedOutputStream(
				new FileOutputStream(targetFilePath));

		byte[] buf = new byte[1024];
		int cnt = 0;
		while ((cnt = bufIn.read(buf)) != -1) {
			bufOut.write(buf, 0, cnt);
		}

		bufOut.close();
		bufIn.close();

		return new File(targetFilePath);
	}

	public static void copyFolderContents(File srcFldr, File targetFldr,
			FilenameFilter filter) throws IOException {
		File[] fllist = srcFldr.listFiles();
		for (int i = 0; i < fllist.length; i++) {
			if (fllist[i].isFile()
					&& fllist[i].getName().toLowerCase().endsWith(".jar")) {
				copyFile(fllist[i], targetFldr);
			}
			if (fllist[i].isDirectory()) {
				copyFolderContents(fllist[i], targetFldr, filter);
			}
		}
	}

	public static class JarFilter implements FilenameFilter {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".jar");
		}
	}

	public static File createPropertiesJar(File configFldr, File targetFile)
			throws IOException {
		BufferedOutputStream bufOut = new BufferedOutputStream(
				new FileOutputStream(targetFile));
		JarOutputStream jout = new JarOutputStream(bufOut);

		File[] lst = configFldr.listFiles();
		for (int i = 0; i < lst.length; i++) {
			if (lst[i].isDirectory() == false) {
				String name = lst[i].getName();
				if (name.equalsIgnoreCase("dfc.properties")
						|| name.equalsIgnoreCase("log4j.properties")
						|| name.equalsIgnoreCase("dbor.properties")) {
					addFileToJar(lst[i], jout, "");
				}
			}
		}

		jout.close();
		return targetFile;

	}

	private static void addFileToJar(File file, ZipOutputStream zipStream,
			String path) throws IOException {
		FileInputStream fin = new FileInputStream(file);

		int bufSize = 1024;
		byte ipBuf[] = new byte[bufSize];
		int lenRead = 0;

		String filename = path + file.getName();
		zipStream.putNextEntry(new ZipEntry(filename));

		while ((lenRead = fin.read(ipBuf)) > 0) {
			zipStream.write(ipBuf, 0, lenRead);
		}

		zipStream.closeEntry();
		fin.close();
	}

	/**
	 * 
	 * @param home
	 *            home can be repoint home or eclipse home
	 * @return
	 */
	public static File getDfcPluginLibFolder(File home) {
		String absPath = home.getAbsolutePath();
		if (absPath.endsWith(File.separator) == false) {
			absPath += File.separator;
		}

		File dfcFolder = getDfcPluginFolder(new File(absPath + "plugins"));
		if (dfcFolder != null) {
			return new File(dfcFolder.getAbsolutePath() + File.separator
					+ "lib");
		}
		return null;
	}

	public static File getPluginXmlFile(File pluginFolder) {
		String absPath = pluginFolder.getAbsolutePath();
		if (absPath.endsWith(File.separator) == false) {
			absPath += File.separator;
		}
		return new File(absPath + "plugin.xml");
	}

	public static File getPluginRootFolder(File home) {
		String absPath = home.getAbsolutePath();
		if (absPath.endsWith(File.separator) == false) {
			absPath += File.separator;
		}

		File pluginsFolder = new File(absPath + "plugins");
		return pluginsFolder;
	}

	public static File getDfcPluginFolder(File pluginsFolder) {
		File[] lst = pluginsFolder.listFiles();
		for (int i = 0; i < lst.length; i++) {
			if (lst[i].isDirectory()
					&& lst[i].getName().startsWith("com.documentum.dfc"))
				return lst[i];
		}
		return null;
	}

	public static File getDfcPluginConfFolder(File home) {
		String absPath = home.getAbsolutePath();
		if (absPath.endsWith(File.separator) == false) {
			absPath += File.separator;
		}

		File dfcFolder = getDfcPluginFolder(new File(absPath + "plugins"));
		if (dfcFolder != null) {
			return new File(dfcFolder.getAbsolutePath() + File.separator
					+ "config");
		}
		return null;
	}
}
