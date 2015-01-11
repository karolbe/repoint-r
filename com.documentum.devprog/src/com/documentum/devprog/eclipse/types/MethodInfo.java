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

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;

public class MethodInfo implements ITypeViewData {

	public static final int METH_ROOT = 0;
	public static final int METH_NODE = 1;

	private String docbase = null;
	private IDfTypedObject tObj = null;
	private int nodeType = 0;
	private Shell shell = null;
	private static final String query = "select r_object_id,object_name from dm_method";
	private static final String iconPath = "type/t_dm_method_16.gif";
	final public static String ATTR_LIST = "r_object_id,object_name";

	public MethodInfo(int nodeType, String docbase, IDfTypedObject tObj) {
		this.nodeType = nodeType;
		this.docbase = docbase;
		this.tObj = tObj;
	}

	public String getDocbase() {
		return docbase;
	}

	public String getDisplayText() {
		if (this.getNodeType() == MethodInfo.METH_ROOT) {
			return "Methods";
		} else {
			try {
				return this.getProperties().getString("object_name");
			} catch (DfException dfe) {
				DfLogger.error(this, "Error getting method object name", null,
						dfe);
			}

		}
		return "";
	}

	public String getIconPath() {
		return iconPath;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		IDfClientX cx = new DfClientX();
		IDfQuery queryObj = cx.getQuery();
		queryObj.setDQL(query);
		ArrayList lst = new ArrayList();

		IDfSession sess = null;
		IDfCollection coll = null;
		try {
			sess = PluginState.getSession(getDocbase());
			coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				IDfTypedObject to = coll.getTypedObject();
				MethodInfo mi = new MethodInfo(MethodInfo.METH_NODE,
						getDocbase(), to);
				lst.add(mi);
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

		} catch (DfException dfe) {
			DfLogger.error(this, "Error displaying method props", null, dfe);
			MessageDialog.openError(this.getShell(), "Error", dfe.getMessage());
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
		return (this.getNodeType() == MethodInfo.METH_ROOT);
	}

	public int getNodeType() {
		return nodeType;
	}

}
