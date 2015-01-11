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
 * Created on Oct 6, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.common;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;

import java.util.ArrayList;
import java.util.Collections;

public class Tester {

	private static void testVersionParser() {
		DFCVersionParser parser = new DFCVersionParser(DfClient.getDFCVersion());
		System.out.println(parser.getMajor());
		System.out.println(parser.getMinor());
		System.out.println(parser.getMaintenance());
		System.out.println(parser.getUpdate());
		System.out.println(parser.getServicePack());

		System.out.println(isDFC53());
	}

	public static void main(String[] args) {
		// testVersionParser();
		// testFolderPaths();
		testSubtypeOf();
	}

	private static void testFolderPaths() {
		PluginState.addIdentity("patila", "Murari1379", null, "dm_notes", true);
		IDfSession sess = null;
		try {
			sess = PluginState.getSession("dm_notes");
			IDfSysObject so = (IDfSysObject) sess.getObject(new DfId(
					"0b0000018025eae1"));
			String paths[] = getAllContainingFolderPaths(so);
			for (int i = 0; i < paths.length; i++) {
				System.out.println(paths[i]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Checks if the DFC versign is 5.3 or later.
	 * 
	 * @return
	 */
	public static boolean isDFC53() {
		DFCVersionParser dfcVersion = new DFCVersionParser("5.2.5.012 SP2");
		return ((dfcVersion.getMajor()) == 5 && (dfcVersion.getMinor() == 3));
	}

	public static void testSubtypeOf() {

		PluginState.addIdentity("patila", "Murari1379", null, "dm_notes", true);
		IDfSession sess = null;
		try {
			sess = PluginState.getSession("dm_notes");
			IDfType typeObj = sess.getType("dm_taxonomy");
			System.out.println("st: " + typeObj.isSubTypeOf("dm_sysobject"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static String[] getAllContainingFolderPaths(IDfSysObject sObj)
			throws Exception {
		int fldrCnt = sObj.getFolderIdCount();
		System.out.println("fldrCnt: " + fldrCnt);
		ArrayList pathList = new ArrayList();
		for (int i = 0; i < fldrCnt; i++) {
			IDfId fldrId = sObj.getFolderId(i);
			System.out.println("fldrId: " + fldrId);
			IDfFolder fldr = (IDfFolder) sObj.getSession().getObject(fldrId);
			int pathCnt = fldr.getFolderPathCount();
			System.out.println("pathCnt: " + pathCnt);
			for (int j = 0; j < pathCnt; j++) {
				String path = fldr.getFolderPath(j);
				pathList.add(path);
			}
		}
		Collections.sort(pathList);
		return (String[]) pathList.toArray(new String[] {});
	}

}
