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
 * Created on Sep 24, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.bof;

import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfGlobalModuleRegistry;
import com.documentum.fc.client.IDfModuleDescriptor;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.SimpleTextDialog;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

public class BOF53ContentProvider implements ITreeContentProvider {

	private Shell shell = null;

	BOF53ContentProvider(Shell sh) {
		shell = sh;
	}

	public Object[] getChildren(Object parentElement) {
		ArrayList lst = new ArrayList();
		try {
			if (BOF53Helper.isGlobalRegistryAccessible()) {
				IDfClient lc = DfClient.getLocalClient();
				IDfGlobalModuleRegistry reg = lc.getModuleRegistry();
				IDfEnumeration enumServ = reg.getServiceDescriptors();

				while (enumServ.hasMoreElements()) {
					IDfModuleDescriptor desc = (IDfModuleDescriptor) enumServ
							.nextElement();
					lst.add(desc);
				}
			} else {
				String infoMsg = "DFC 5.3 Global Module Registry is not accessible. Repoint will fallback to using the DBOR to obtain services. You will see only the services listed in the DBOR";
				MessageDialog.openWarning(shell, "BOF View", infoMsg);
				BOF52ContentProvider prov = new BOF52ContentProvider(shell);
				return prov.getChildren(parentElement);
			}

		} catch (Exception ex) {
			DfLogger.error(this, "Error obtaining service descriptors", null,
					ex);
			String message = "If you are using DFC 5.3, your global module registry may not be accessible or incorrectly configured.\nDFC Stack Trace follows";
			SimpleTextDialog std = new SimpleTextDialog(shell,
					"BOF View Error", message + "\n"
							+ PluginHelper.serializeStackTrace(ex));
			std.open();
		}
		return lst.toArray();

	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof String) {
			return true;
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return new String[] { "Services" };
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
