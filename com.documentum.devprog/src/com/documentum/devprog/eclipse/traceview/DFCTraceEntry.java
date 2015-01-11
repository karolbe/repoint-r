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
 * Created on Sep 7, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.traceview;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DFCTraceEntry {
	private String sourceClass = null;

	private String sourceMethod = null;

	private String arguments = null;

	private String returnValue = null;

	private String traceEntry = null;

	public DFCTraceEntry(String te) {
		traceEntry = te;
		parseTraceEntry();
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

	public String getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(String sourceClass) {
		this.sourceClass = sourceClass;
	}

	public String getSourceMethod() {
		return sourceMethod;
	}

	public void setSourceMethod(String sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	/*
	 * private void parseTraceEntry() { if (traceEntry != null &&
	 * traceEntry.length() > 0) {
	 * 
	 * String[] msgParts = traceEntry.split(" ");
	 * 
	 * String fullClName = removeStartingDotsFromClassName(msgParts[0]); int
	 * dotIndx = fullClName.lastIndexOf('.');
	 * 
	 * String clName = fullClName.substring(0, dotIndx); setSourceClass(clName);
	 * 
	 * String methName = fullClName.substring(dotIndx + 1);
	 * setSourceMethod(methName);
	 * 
	 * for (int i = 1; i < msgParts.length; i++) { if
	 * (msgParts[i].startsWith("('")) { setArguments(msgParts[i]); } else if
	 * (msgParts[i].startsWith("'")) { setReturnValue(msgParts[i]); } }
	 * 
	 * }
	 * 
	 * }
	 */

	private void parseTraceEntry() {
		if (traceEntry != null && traceEntry.length() > 0) {
			traceEntry = removeStartingDotsFromClassName(traceEntry);

			int indxOpenParan = traceEntry.indexOf('(');
			int indxClosedParan = traceEntry.indexOf(')');

			String arguments = traceEntry.substring(indxOpenParan,
					indxClosedParan + 1);
			this.setArguments(arguments);

			String clsName = traceEntry.substring(0, indxOpenParan);
			int indxLastDot = clsName.lastIndexOf('.');

			String methName = clsName.substring(indxLastDot + 1);
			this.setSourceMethod(methName);

			clsName = clsName.substring(0, indxLastDot);
			setSourceClass(clsName);

			if ((indxClosedParan + 1) <= traceEntry.length() - 1) {
				String returnValue = traceEntry.substring(indxClosedParan + 1);
				this.setReturnValue(returnValue);
			}
		}

	}

	public static String removeStartingDotsFromClassName(String name) {
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) != '.') {
				return name.substring(i);
			}
		}
		return name;
	}

	private String getMethodName(String classname) {
		int dotIndx = classname.lastIndexOf('.');
		if (dotIndx != -1) {
			return classname.substring(dotIndx + 1);
		}
		return classname;

	}

}
