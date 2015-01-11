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
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;

import com.documentum.ApplicationManager.DfApplicationX;
import com.documentum.ApplicationManager.IDfAppRef;
import com.documentum.ApplicationManager.IDfApplication;
import com.documentum.ApplicationManager.IDfApplicationX;

public class DocAppRefInfo implements ITypeViewData {

	private IDfTypedObject appObj = null;

	private IDfId appRefId = null;

	private String refId = null;

	private String refType = null;

	private String refName = null;

	private String docbase = null;

	private Shell shell = null;

	private Integer nodeCategory = null;

	public DocAppRefInfo(Integer nodeCategory, String docbase,
			IDfTypedObject appObj, IDfId appRefId) throws DfException {
		this.nodeCategory = nodeCategory;
		this.docbase = docbase;
		this.appObj = appObj;
		this.setAppRefId(appRefId);
	}

	public String getDocbase() {

		return docbase;
	}

	public String getDisplayText() {
		return this.refName;
	}

	public String getIconPath() {
		StringBuffer bufPath = new StringBuffer(12);
		bufPath.append("type/t_");
		bufPath.append(this.refType);
		bufPath.append("_16.gif");
		return bufPath.toString();
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		return ITypeViewData.EMPTY_ARRAY;
	}

	public void onDoubleClick(DoubleClickEvent dce) {
		PluginHelper.showPropertiesView(this.refId);

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
		return false;
	}

	public IDfId getAppRefId() {
		return appRefId;
	}

	public void setAppRefId(IDfId appRefId) throws DfException {
		IDfSession sess = null;
		try {
			sess = PluginState.getSession(getDocbase());
			this.appRefId = appRefId;
			IDfApplicationX appX = new DfApplicationX();
			IDfApplication app = appX.findFromApplicationName(sess, this
					.getAppObj().getString("object_name"));
			IDfAppRef ar = (IDfAppRef) sess.getObject(appRefId);
			// IDfAppRef ar = app.getObjectReferenceById(appRefId);
			this.refId = ar.getString("application_obj_id");

			IDfPersistentObject po = sess.getObject(new DfId(this.refId));
			this.refType = po.getType().getName();
			if (po.hasAttr("object_name")) {
				this.refName = po.getString("object_name");
			} else if (po.hasAttr("name")) {
				this.refName = po.getString("name");
			} else {
				this.refName = this.refType + " - " + this.refId;
			}
		} finally {
			PluginState.releaseSession(sess);
		}
	}

	public IDfTypedObject getAppObj() {
		return appObj;
	}

	public void setAppObj(IDfTypedObject appObj) {
		this.appObj = appObj;
	}

	public Integer getNodeCategory() {
		return nodeCategory;
	}

	public void setNodeCategory(Integer nodeCategory) {
		this.nodeCategory = nodeCategory;
	}

}
