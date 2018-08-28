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
�*�
�*******************************************************************************/

/*
 * Created on Feb 3, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.types;

import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.DocbaseLoginDialog;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * Represents a docbase node in the type tree.
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DocbaseNode implements ITypeViewData {
	private String name = null;

	private final static String iconPath = "type/t_docbase_16.gif";

	private Shell shell = null;

	DocbaseNode(String name) {
		this.name = name;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) {
		String docbase = this.getDocbase();
		if (docbase == null || (docbase.length() == 0)) {
			// System.out.println("tv: Docbase is null");
			return ITypeViewData.EMPTY_ARRAY;
		}

		if (PluginState.hasIdentity(docbase) == false) {
			DocbaseLoginDialog loginDlg = new DocbaseLoginDialog(shell);
			loginDlg.setDocbaseName(docbase);
			int ok = loginDlg.open();
			if (ok == DocbaseLoginDialog.CANCEL) {
				return ITypeViewData.EMPTY_ARRAY;
			}
		}

		ArrayList lst = new ArrayList();
		TypeInfo ti = new TypeInfo("Types", TypeInfo.ROOT,
				this.getDisplayText());
		lst.add(ti);
		LifecycleInfo li = new LifecycleInfo(LifecycleInfo.ROOT,
				this.getDisplayText());
		lst.add(li);
		WorkflowInfo wi = new WorkflowInfo(WorkflowInfo.WF_ROOT,
				this.getDocbase());
		lst.add(wi);

		FormatInfo fi = new FormatInfo(FormatInfo.FMT_ROOT, this.getDocbase(),
				null);
		lst.add(fi);

		MethodInfo metInfo = new MethodInfo(MethodInfo.METH_ROOT,
				this.getDocbase(), null);
		lst.add(metInfo);

		RelationTypeInfo ri = new RelationTypeInfo(RelationTypeInfo.REL_ROOT,
				this.getDocbase(), null);
		lst.add(ri);

		JobInfo jobInfo = new JobInfo(JobInfo.JOB_ROOT, this.getDocbase(), null);
		lst.add(jobInfo);

//		DocAppInfo di = new DocAppInfo(DocAppInfo.APP_ROOT, this.getDocbase());
//		lst.add(di);

		XMLAppInfo xi = new XMLAppInfo(XMLAppInfo.XMLAPP_ROOT,
				this.getDocbase(), null);
		lst.add(xi);

		if (PluginHelper.isDFC53()) {
			ModuleInfo mi = new ModuleInfo(ModuleInfo.MOD_ROOT, getDocbase(),
					null);
			lst.add(mi);
		}
		return (ITypeViewData[]) lst.toArray(ITypeViewData.EMPTY_ARRAY);
	}

	public String getDisplayText() {
		return name;
	}

	public String getDocbase() {
		return name;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void onDoubleClick(DoubleClickEvent dce) {
		// do nothing
	}

	/**
	 * Sets the shell that this node can use
	 * 
	 * @param sh
	 */
	public void setShell(Shell sh) {
		this.shell = sh;
	}

	/**
	 * Gets the shell that this node uses.
	 * 
	 * @return
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * NOTE: Returns null.
	 */
	public IDfTypedObject getProperties() {
		return null;
	}

	public boolean hasChildren() {
		return true;
	}

}
