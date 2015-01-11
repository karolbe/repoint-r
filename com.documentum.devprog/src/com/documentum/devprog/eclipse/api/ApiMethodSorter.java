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
 * Created on Mar 24, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class used to parse the methods and their reference from API Manual.
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class ApiMethodSorter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sortMethods();
	}

	private static void sortMethods() {
		try {
			String basePath = "C:\\devprog\\eclipsePlugin\\repoint31\\api";
			FileReader allMet = new FileReader(new File(basePath
					+ "\\all_api_methods_53sp1.txt"));
			BufferedReader bufAllMet = new BufferedReader(allMet);
			FileWriter setMet = new FileWriter(new File(basePath
					+ "\\set_api_methods.txt"));
			FileWriter getMet = new FileWriter(new File(basePath
					+ "\\get_api_methods.txt"));
			FileWriter execMet = new FileWriter(new File(basePath
					+ "\\exec_api_methods.txt"));

			HashMap hsSet = new HashMap();
			HashMap hsGet = new HashMap();
			HashMap hsExec = new HashMap();

			String line = null;
			String curCommand = null;
			StringBuffer bufCmdInfo = new StringBuffer(32);
			int curCmdType = 0; // 0=exec 1=get 2=set

			while ((line = bufAllMet.readLine()) != null) {
				line = line.trim();
				boolean writeLine = true;
				if (line.indexOf(" page ") != -1) {
					String cmdInfo = bufCmdInfo.toString();
					if (curCmdType == 0) {
						hsExec.put(curCommand, cmdInfo);
					} else if (curCmdType == 1) {
						hsGet.put(curCommand, cmdInfo);
					} else if (curCmdType == 2) {
						hsSet.put(curCommand, cmdInfo);
					}

					// set new command and clear info buffer
					String[] toks = line.split(",");
					curCommand = toks[0].toLowerCase();
					bufCmdInfo = new StringBuffer(32);
					writeLine = false;
				} else if (line.startsWith("dmAPIExec")) {
					curCmdType = 0;
				} else if (line.startsWith("dmAPIGet")) {
					curCmdType = 1;
				} else if (line.startsWith("dmAPISet")) {
					curCmdType = 2;
				}
				if (writeLine) {
					bufCmdInfo.append(" ").append(line);
				}
			}

			writeMethods(setMet, hsSet);
			writeMethods(getMet, hsGet);
			writeMethods(execMet, hsExec);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void writeMethods(FileWriter writer, HashMap set)
			throws Exception {

		String nl = System.getProperty("line.separator");
		Iterator iter = set.keySet().iterator();
		while (iter.hasNext()) {
			String meth = (String) iter.next();
			String methInfo = (String) set.get(meth);

			writer.write("METH=" + meth + nl);
			writer.write("INFO=" + methInfo + nl);
		}

		writer.close();
	}

}
