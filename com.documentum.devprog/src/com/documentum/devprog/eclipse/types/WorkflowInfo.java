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
 * Created on Feb 5, 2006
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

public final class WorkflowInfo implements ITypeViewData {

	final public static int WF_ROOT = 0;

	final public static int WF_NODE = 1;

	final private static String iconPath = "type/t_dm_process_16.gif";

	private int nodeType = 0;

	private String docbaseName = null;

	private IDfTypedObject tObj = null;

	final public static String ATTR_LIST = "r_object_id,object_name,r_object_type";

	public WorkflowInfo(int type, String docbaseName) {
		this.nodeType = type;
		this.docbaseName = docbaseName;

	}

	public IDfTypedObject getData() {
		return tObj;
	}

	public void setData(IDfTypedObject tObj) {
		this.tObj = tObj;
	}

	public String getDocbase() {
		return docbaseName;
	}

	public void setDocbase(String db) {
		docbaseName = db;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		IDfSession sess = null;
		IDfCollection coll = null;
		ArrayList lstCh = new ArrayList(10);
		try {
			IDfClientX cx = PluginState.getClientX();

			StringBuffer bufQuery = new StringBuffer(24);
			bufQuery.append("select ").append(ATTR_LIST);
			bufQuery.append(" from dm_process ORDER BY object_name");
			String strQuery = bufQuery.toString();

			IDfQuery query = cx.getQuery();
			query.setDQL(strQuery);

			sess = PluginState.getSession(getDocbase());
			coll = query.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				IDfTypedObject tObj = coll.getTypedObject();
				WorkflowInfo wi = new WorkflowInfo(WorkflowInfo.WF_NODE,
						this.getDocbase());
				wi.setData(tObj);
				lstCh.add(wi);
			}
		} finally {
			if (coll != null) {
				coll.close();
			}
			PluginState.releaseSession(sess);
		}
		return (ITypeViewData[]) lstCh.toArray(ITypeViewData.EMPTY_ARRAY);

	}

	public int getNodeType() {
		return nodeType;
	}

	public String getDisplayText() {
		if (this.getNodeType() == WorkflowInfo.WF_ROOT) {
			return "Processes";
		} else {
			try {
				return this.getData().getString("object_name");
			} catch (DfException dfe) {
				DfLogger.error(this, "Error getting obj name", null, dfe);
				return "err";
			}
		}
	}

	public String getIconPath() {
		return iconPath;
	}

	public void onDoubleClick(DoubleClickEvent dce) {
		String data = "";
		if (this.getNodeType() == LifecycleInfo.ROOT) {
			data = "dm_process";
		} else if (this.getNodeType() == LifecycleInfo.LIFECYCLE_NODE) {
			try {
				data = getData().getString("r_object_id");

			} catch (DfException dfe) {
				DfLogger.error(this, "Error gettting data", null, dfe);
				MessageDialog.openError(getShell(), "Error Showing Properties",
						dfe.getMessage());
			}
		}
		PluginHelper.showPropertiesView(data);
	}

	public void setShell(Shell sh) {
	}

	public Shell getShell() {
		return null;
	}

	public IDfTypedObject getProperties() {
		return tObj;
	}

	public boolean hasChildren() {
		return (this.getNodeType() == WorkflowInfo.WF_ROOT);
	}

}
