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
 * Created on Apr 6, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.devprog.eclipse.tree.RepoTreeExtension;

import org.eclipse.jface.dialogs.MessageDialog;

/**
 * 
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class NewFolderAction extends RepoTreeExtension {

	public boolean showAction() {
		DocbaseItem dtd = super.getDocbaseItem();
		if (dtd != null) {
			if (dtd.isCabinetType() || dtd.isFolderType()) {
				return true;
			} else if (dtd.isDocbaseType()) {
				String name = (String) dtd.getData();
				return PluginState.hasIdentity(name);
			}
		}

		return false;
	}

	public void run() {
		try {
			DocbaseItem dtd = super.getDocbaseItem();

			NewFolderDialog nfd = new NewFolderDialog(super.getShell());
			if (dtd.isDocbaseType()) {
				nfd.setParentType("dm_cabinet");
			} else {
				IDfTypedObject to = (IDfTypedObject) dtd.getData();
				IDfId parentId = to.getId("r_object_id");
				nfd.setParentType("dm_folder");
				nfd.setParentId(parentId);
			}
			int status = nfd.open();
			if (status == NewFolderDialog.OK) {
				IDfId pi = nfd.getNewFolderId();
				DocbaseItem nc = DocbaseItem.newTypedObjectInstance(dtd, pi);
				super.getRepoTreeViewer().add(dtd, nc);
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error Creating a new Folder", null, ex);
			MessageDialog.openError(super.getShell(), "Folder Creation Error",
					ex.getMessage());
		}
	}

}
