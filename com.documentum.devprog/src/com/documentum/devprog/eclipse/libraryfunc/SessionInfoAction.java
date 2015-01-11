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
 * Created on Aug 23, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.HTMLDialog;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.devprog.eclipse.tree.RepoTreeExtension;

import org.eclipse.jface.dialogs.MessageDialog;

public class SessionInfoAction extends RepoTreeExtension {

	public boolean showAction() {
		DocbaseItem dtd = super.getDocbaseItem();
		if (dtd != null) {
			return dtd.isDocbaseType();
		} else {
			return false;
		}
	}

	public static void printInfo(String title, IDfTypedObject tObj,
			StringBuffer bufData) {
		bufData.append("<html><body><table border='0' style=\" font-family:Arial,Helvetica; color:blue;font-style:bold;font-size:12px;border-style:groove;border-color:#000080;border-width:1;border-spacing:3px;padding:2px;background-color:#d3d3d3\">");

		bufData.append("<tr><td colspan='2'><b>").append(title)
				.append("</b></td></tr>");
		try {
			int start_pos = 0;
			if (tObj instanceof IDfPersistentObject) {
				IDfPersistentObject pObj = (IDfPersistentObject) tObj;
				start_pos = pObj.getType().getInt("start_pos");

			}
			int attrCnt = tObj.getAttrCount();
			for (int i = start_pos; i < attrCnt; i++) {
				bufData.append("<tr>");
				String attrName = tObj.getAttr(i).getName();
				bufData.append(
						"<td style=\"border-style:groove;border-width:1;border-color:#6666FF\">")
						.append(attrName).append("</td>");
				String val = "";
				if (tObj.isAttrRepeating(attrName)) {
					val = tObj.getAllRepeatingStrings(attrName, ",");
				} else {
					val = tObj.getString(attrName);
				}
				bufData.append(
						"<td style=\"border-style:groove;border-width:1;border-color:#6666FF\">")
						.append(val).append("</td>");
				bufData.append("</tr>");
			}
		} catch (DfException dfe) {
			DfLogger.error(SessionInfoAction.class.getName(),
					"Error getting info", null, dfe);
		}
		bufData.append("</table></body></html>");
	}

}
