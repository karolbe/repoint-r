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
 * Created on Feb 8, 2006
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

public class XMLAppInfo implements ITypeViewData {

	final public static int XMLAPP_ROOT = 0;

	final public static int XMLAPP_NODE = 1;

	private String docbase = null;

	private IDfTypedObject xmlAppObj = null;

	private final static String iconPath = "type/t_dm_xml_application_16.gif";

	public final static String ATTR_LIST = "r_object_id,object_name";

	private Shell shell = null;

	private int nodeType = 0;

	private final static String query = "select r_object_id,object_name from dm_xml_application";

	public XMLAppInfo(int nodeType, String docbase, IDfTypedObject xmlApp) {
		this.nodeType = nodeType;
		this.docbase = docbase;
		this.xmlAppObj = xmlApp;
	}

	public int getNodeType() {
		return nodeType;
	}

	public IDfTypedObject getXMLApplication() {
		return xmlAppObj;
	}

	public String getDocbase() {
		return docbase;
	}

	public String getDisplayText() {
		try {
			if (this.getNodeType() == XMLAppInfo.XMLAPP_ROOT) {
				return "XML Applications";
			} else {
				return this.getXMLApplication().getString("object_name");
			}
		} catch (DfException dfe) {
			DfLogger.error(this, "Error getting xml app name", null, dfe);
		}
		return "";
	}

	public String getIconPath() {
		return iconPath;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		ArrayList lst = new ArrayList();

		IDfClientX cx = PluginState.getClientX();
		IDfQuery q = cx.getQuery();
		q.setDQL(query);

		IDfSession sess = null;
		IDfCollection coll = null;
		try {
			sess = PluginState.getSession(getDocbase());
			coll = q.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				IDfTypedObject tObj = coll.getTypedObject();
				XMLAppInfo xi = new XMLAppInfo(XMLAppInfo.XMLAPP_NODE,
						getDocbase(), tObj);
				lst.add(xi);
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
			PluginHelper.showPropertiesView(this.getXMLApplication().getString(
					"r_object_id"));

		} catch (Exception ex) {
			DfLogger.error(this, "Error showing xml app props", null, ex);
		}
	}

	public void setShell(Shell sh) {
		this.shell = sh;
	}

	public Shell getShell() {
		return shell;
	}

	public IDfTypedObject getProperties() {
		return null;
	}

	public boolean hasChildren() {
		return (this.getNodeType() == XMLAppInfo.XMLAPP_ROOT);
	}

}
