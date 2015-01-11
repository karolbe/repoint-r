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

import java.util.Arrays;

/**
 * 
 * 
 * Simple version parser to determine whether we are using 5.3 or 5.2
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DFCVersionParser {

	private String versionStr = "";

	private int major = 0;

	private int minor = 0;

	private int maintenance = 0;

	private int update = 0;

	private String servicePack = "";

	public DFCVersionParser(String ver) {
		versionStr = ver;
		parseVersionString();
	}

	private void parseVersionString() {
		versionStr = versionStr.trim();
		/*
		 * versionStr.toLowerCase(); System.out.println("versionStr: " +
		 * versionStr); int spIndx = versionStr.indexOf('s'); if(spIndx == -1) {
		 * spIndx = versionStr.indexOf('S'); } System.out.println("spIndx: " +
		 * spIndx); if(spIndx != -1) { servicePack =
		 * versionStr.substring(spIndx); } else { spIndx = versionStr.length();
		 * }
		 */
		// String vernums = versionStr.substring(0,spIndx);
		// String[] fl = vernums.split("([.])");

		// split on . OR space OR S
		// To split on a space add the foll. \\s+
		String[] fl = versionStr.split("([.S])");
		System.out.println();
		if (fl.length > 0) {
			major = Integer.parseInt(fl[0]);
		}
		if (fl.length > 1) {
			minor = Integer.parseInt(fl[1].trim());
		}
		if (fl.length > 2) {
			maintenance = Integer.parseInt(fl[2].trim());
		}
		if (fl.length > 3) {
			update = Integer.parseInt(fl[3].trim());
		}
		if (fl.length == 5) {
			servicePack = "S" + fl[4].toUpperCase(); // have to add 'S' since
														// the split function
														// removes the delim
		}

	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getMaintenance() {
		return maintenance;
	}

	public int getUpdate() {
		return update;
	}

	public String getServicePack() {
		return servicePack;
	}

	/*
	 * private void parseVersionStringR() { //String pattern =
	 * "(\\d)[.](\\d)[.](\\d)[.](\\d{4})(\\w)"; String pattern = "\\d."; Pattern
	 * pat = Pattern.compile(pattern); Matcher mch = pat.matcher(versionStr);
	 * if(mch.matches()) { System.out.println("matches"); while(mch.find()) {
	 * System.out.println(mch.group()); } }
	 * 
	 * }
	 */

}
