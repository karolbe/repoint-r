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
 * Created on Feb 7, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.types;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;

import com.documentum.com.IDfClientX;

public class FormatInfo implements ITypeViewData {

	final public static int FMT_ROOT = 0;
	final public static int FMT_NODE = 1;

	int nodeType = FMT_ROOT;

	private String docbase = null;
	private IDfTypedObject tObj = null;

	private Shell shell = null;

	final private static String iconPath = "type/t_dm_format_16.gif";
	final private static String query = "select r_object_id,name from dm_format";

	final public static String ATTR_LIST = "r_object_id,name";

	public FormatInfo(int nodeType, String docbase, IDfTypedObject tObj) {
		this.nodeType = nodeType;
		this.docbase = docbase;
		this.tObj = tObj;
	}

	public String getDocbase() {
		return docbase;
	}

	public String getDisplayText() {
		if (this.getNodeType() == FormatInfo.FMT_ROOT) {
			return "Formats";
		} else if (this.getNodeType() == FormatInfo.FMT_NODE) {
			try {
				return this.getProperties().getString("name");
			} catch (DfException dfe) {
				DfLogger.error(this, "Error getting fmt name", null, dfe);
			}

		}
		return "";
	}

	public String getIconPath() {
		if (this.getNodeType() == FormatInfo.FMT_ROOT) {
			return iconPath;
		} else {
			try {
				StringBuffer bufPath = new StringBuffer(12);
				bufPath.append("format/f_").append(
						getProperties().getString("name"));
				bufPath.append("_16.gif");
				return bufPath.toString();
			} catch (DfException dfe) {
				DfLogger.error(this, "Error getting format props", null, dfe);
			}
		}
		return iconPath;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		ArrayList lst = new ArrayList();

		IDfClientX cx = PluginState.getClientX();
		IDfQuery queryObj = cx.getQuery();
		queryObj.setDQL(query);

		IDfSession sess = null;
		IDfCollection coll = null;
		try {
			sess = PluginState.getSession(getDocbase());
			progMon.subTask("Querying for Formats");
			coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
			progMon.subTask("Iterating Formats");
			while (coll.next()) {
				IDfTypedObject to = coll.getTypedObject();
				FormatInfo fi = new FormatInfo(FormatInfo.FMT_NODE,
						getDocbase(), to);
				lst.add(fi);
			}
		} finally {
			if (coll != null)
				coll.close();
			PluginState.releaseSession(sess);
		}
		return (ITypeViewData[]) lst.toArray(ITypeViewData.EMPTY_ARRAY);

	}

	public void onDoubleClick(DoubleClickEvent dce) {
		try {
			PluginHelper.showPropertiesView(this.getProperties().getString(
					"r_object_id"));
		} catch (Exception ex) {
			MessageDialog.openError(getShell(), "Format Display Error",
					ex.getMessage());
		}

	}

	public void setShell(Shell sh) {
		this.shell = sh;

	}

	public Shell getShell() {
		return shell;
	}

	public IDfTypedObject getProperties() {
		return tObj;
	}

	public boolean hasChildren() {
		return (this.getNodeType() == FormatInfo.FMT_ROOT);
	}

	public int getNodeType() {
		return nodeType;
	}

}
