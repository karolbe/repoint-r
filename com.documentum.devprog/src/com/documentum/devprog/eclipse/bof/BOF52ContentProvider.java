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
import com.documentum.fc.client.IDfDbor;
import com.documentum.fc.client.IDfDborEntry;
import com.documentum.fc.client.IDfEnumeration;

import com.documentum.devprog.eclipse.common.PluginHelper;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

public class BOF52ContentProvider implements ITreeContentProvider {

	private Shell shell = null;

	BOF52ContentProvider(Shell sh) {
		shell = sh;
	}

	public Object[] getChildren(Object parentElement) {
		ArrayList lst = new ArrayList();
		try {
			IDfDbor dbor = DfClient.getLocalClient().getDbor();
			IDfEnumeration enumDe = dbor.getAll();
			while (enumDe.hasMoreElements()) {
				IDfDborEntry de = (IDfDborEntry) enumDe.nextElement();
				if (de.isServiceBased()) {
					lst.add(de);
				}
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error getting 5.2 servies", null, ex);
			MessageDialog.openError(shell, "BOF View",
					PluginHelper.serializeStackTrace(ex));
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
