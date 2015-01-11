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
 * Created on Feb 1, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * NOT USED.
 * 
 * This was meant to avoid having to put dfc.jar and all in the classpath. The
 * classloader would instead auto pick these up based on configured folders.
 * Maybe sometime in the future.
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DevprogClassloader extends URLClassLoader {

	private static DevprogClassloader inst = null;

	public DevprogClassloader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		inst = this;
	}

	public void addAdditionalURL(URL url) {
		super.addURL(url);
	}

	/**
	 * Adds a file to the classpath
	 * 
	 * @param s
	 * @throws IOException
	 */
	public void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}

	public void addFile(File f) throws IOException {
		addURL(f.toURL());
	}

	public void addJarsInFolderRecursively(File folder) throws IOException {
		if (folder.isDirectory()) {
			File[] fldrCont = folder.listFiles();
			for (int i = 0; i < fldrCont.length; i++) {
				File cur = fldrCont[i];
				if (cur.isFile()) {
					String name = cur.getName();
					if (name.endsWith(".jar")) {
						addFile(cur);
					}
				} else if (cur.isDirectory()) {
					addJarsInFolderRecursively(cur);
				}
			}
		}
	}

	public void addClassFolder(File folder) throws IOException {
		if (folder.isDirectory()) {
			addURL(folder.toURL());
		}

	}

	public static DevprogClassloader getCurrentInstance(ClassLoader parent) {
		URL[] urls = inst.getURLs();
		DevprogClassloader dc = new DevprogClassloader(urls, parent);
		return dc;
	}

}
