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

import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfLocalModuleRegistry;
import com.documentum.fc.client.IDfModuleDescriptor;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.bof.BOF53Helper;
import com.documentum.devprog.eclipse.common.HTMLDialog;
import com.documentum.devprog.eclipse.common.PluginState;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;

final public class ModuleInfo implements ITypeViewData, Comparator {
	final private static String iconPath = "jar_obj.gif";

	private String docbase = null;

	private IDfModuleDescriptor desc = null;

	final public static int MOD_ROOT = 0;
	final public static int MOD_NODE = 1;

	private int nodeType = 0;

	private Shell shell = null;

	public ModuleInfo(int nodeType, String docbaseName, IDfModuleDescriptor desc) {
		this.nodeType = nodeType;
		this.docbase = docbaseName;
		this.desc = desc;
	}

	public String getDocbase() {
		return docbase;
	}

	public String getDisplayText() {
		if ((getNodeType() == ModuleInfo.MOD_NODE) && (desc != null)) {
			StringBuffer bufDisp = new StringBuffer(16);
			bufDisp.append("[");
			bufDisp.append(desc.getCategory());
			bufDisp.append("] ");
			bufDisp.append(desc.getObjectName());

			return bufDisp.toString();
		} else if (this.getNodeType() == ModuleInfo.MOD_ROOT) {
			return "Modules";
		} else {
			return "err";
		}
	}

	public String getIconPath() {
		return iconPath;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		ArrayList lst = new ArrayList();
		IDfSession sess = null;
		try {
			sess = PluginState.getSession(getDocbase());
			progMon.subTask("Accessing Local Module Registry");
			IDfLocalModuleRegistry reg = sess.getModuleRegistry();
			IDfEnumeration enumMods = reg.getModuleDescriptors();
			progMon.subTask("Enumerating Available Modules");
			while (enumMods.hasMoreElements()) {
				IDfModuleDescriptor desc = (IDfModuleDescriptor) enumMods
						.nextElement();
				ModuleInfo mi = new ModuleInfo(ModuleInfo.MOD_NODE,
						this.getDocbase(), desc);
				lst.add(mi);
			}
			// Collections.sort(lst,this);
		} finally {
			PluginState.releaseSession(sess);
		}

		return (ITypeViewData[]) lst.toArray(ITypeViewData.EMPTY_ARRAY);
	}

	public void onDoubleClick(DoubleClickEvent dce) {
		String data = BOF53Helper.prepareInfo(desc);
		HTMLDialog hd = new HTMLDialog(getShell(), this.getDescriptor()
				.getObjectName(), data);
		hd.open();
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
		return (getNodeType() == ModuleInfo.MOD_ROOT);
	}

	public int getNodeType() {
		return nodeType;
	}

	public IDfModuleDescriptor getDescriptor() {
		return desc;
	}

	public int compare(Object arg0, Object arg1) {

		ModuleInfo mi0 = (ModuleInfo) arg0;
		ModuleInfo mi1 = (ModuleInfo) arg1;

		String str0 = mi0.getDescriptor().getCategory().getName();
		String str1 = mi1.getDescriptor().getCategory().getName();

		Collator coll = Collator.getInstance();
		int comp = coll.compare(str0, str1);
		System.out.println("Coll called: " + str0 + ": " + str1 + ":" + comp);
		return comp;
	}

}
