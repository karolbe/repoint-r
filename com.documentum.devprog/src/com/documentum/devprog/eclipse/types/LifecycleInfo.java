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
 * Created on Feb 3, 2006
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

final public class LifecycleInfo implements ITypeViewData {
	public final static int ROOT = 0;

	final public static int LIFECYCLE_NODE = 1;

	private String docbaseName = null;
	private int nodeType = 0;

	private IDfTypedObject tObj = null;

	final public static String ATTR_LIST = "r_object_id,r_object_type,object_name";

	private Shell shell = null;

	private final static String iconPath = "type/t_dm_policy_16.gif";

	LifecycleInfo(int nodeType, String docbaseName) {
		this.nodeType = nodeType;
		this.docbaseName = docbaseName;

	}

	public int getNodeType() {
		return nodeType;
	}

	public String getDocbase() {
		return docbaseName;
	}

	public void setData(IDfTypedObject to) {
		tObj = to;
	}

	public IDfTypedObject getData() {
		return tObj;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		ArrayList lstObj = new ArrayList(10);
		if (this.getNodeType() == LifecycleInfo.ROOT) {
			IDfSession sess = PluginState.getSession(this.getDocbase());
			IDfCollection coll = null;

			try {
				StringBuffer bufQuery = new StringBuffer(32);
				bufQuery.append("select ").append(ATTR_LIST);
				bufQuery.append(" from dm_policy ORDER BY object_name");
				String strQuery = bufQuery.toString();

				IDfClientX cx = PluginState.getClientX();
				IDfQuery queryObj = cx.getQuery();
				queryObj.setDQL(strQuery);

				coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
				while (coll.next()) {
					LifecycleInfo li = new LifecycleInfo(
							LifecycleInfo.LIFECYCLE_NODE, this.getDocbase());
					li.setData(coll.getTypedObject());
					lstObj.add(li);
				}

			} finally {
				if (coll != null) {
					coll.close();
				}
				if (sess != null) {
					PluginState.releaseSession(sess);
				}

			}

		}
		return (LifecycleInfo[]) lstObj.toArray(new LifecycleInfo[] {});
	}

	public String getDisplayText() {
		if (this.getNodeType() == LifecycleInfo.ROOT) {
			return "Lifecycles";
		} else if (this.getNodeType() == LifecycleInfo.LIFECYCLE_NODE) {
			try {
				return this.getData().getString("object_name");
			} catch (DfException dfe) {
				DfLogger.error(this, "Error get obj name for lifecycle", null,
						dfe);

			}
		}

		return "";
	}

	public String getIconPath() {
		return iconPath;
	}

	public void onDoubleClick(DoubleClickEvent dce) {
		String data = "";
		if (this.getNodeType() == LifecycleInfo.ROOT) {
			data = "dm_policy";
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
		this.shell = sh;

	}

	public Shell getShell() {
		return shell;
	}

	public IDfTypedObject getProperties() {
		return tObj;
	}

	public boolean hasChildren() {
		return (this.getNodeType() == LifecycleInfo.ROOT);
	}
}
