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
 * Created on Feb 6, 2006
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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;

import com.documentum.com.IDfClientX;

public class RelationTypeInfo implements ITypeViewData {

	private String docbase = null;
	private IDfTypedObject tObj = null;

	public final static String ATTR_LIST = "r_object_id,relation_name";

	private final static String iconPath = "type/dm_relation_16.gif";

	public static final int REL_ROOT = 0;

	public static final int REL_NODE = 1;

	private int nodeType = 0;

	private Shell shell = null;

	public RelationTypeInfo(int nodeType, String docbase, IDfTypedObject tObj) {
		this.nodeType = nodeType;
		this.docbase = docbase;
		this.tObj = tObj;
	}

	public String getDocbase() {
		return docbase;
	}

	public String getDisplayText() {
		if (this.getNodeType() == RelationTypeInfo.REL_ROOT) {
			return "Relation Types";
		} else if (tObj != null) {
			try {
				return tObj.getString("relation_name");
			} catch (DfException dfe) {
				DfLogger.error(this, "Error getting relation type name", null,
						dfe);
			}
		}
		return "err";

	}

	public String getIconPath() {
		return iconPath;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		IDfSession sess = null;
		IDfCollection coll = null;
		ArrayList lst = new ArrayList();
		try {
			StringBuffer bufQuery = new StringBuffer(32);
			bufQuery.append("select ").append(ATTR_LIST);
			bufQuery.append(" from dm_relation_type ORDER BY relation_name");
			String strQuery = bufQuery.toString();

			IDfClientX cx = PluginState.getClientX();
			IDfQuery queryObj = cx.getQuery();
			queryObj.setDQL(strQuery);

			sess = PluginState.getSession(getDocbase());
			coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				IDfTypedObject to = coll.getTypedObject();
				RelationTypeInfo ri = new RelationTypeInfo(
						RelationTypeInfo.REL_NODE, this.getDocbase(), to);
				lst.add(ri);
			}

		} finally {
			if (coll != null) {
				coll.close();
			}
			PluginState.releaseSession(sess);
		}
		return (ITypeViewData[]) lst.toArray(ITypeViewData.EMPTY_ARRAY);
	}

	public void onDoubleClick(DoubleClickEvent dce) {
		if (tObj != null) {
			try {
				String objId = getProperties().getString("r_object_id");
				PluginHelper.showPropertiesView(objId);
			} catch (DfException dfe) {
				DfLogger.error(this, "Error getting obj id of rel type", null,
						dfe);
			}
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
		return (this.getNodeType() == RelationTypeInfo.REL_ROOT);
	}

	public int getNodeType() {
		return nodeType;
	}

}
