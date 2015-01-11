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
 * Created on Aug 8, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.devprog.eclipse.tree.RepoTreeExtension;
import com.documentum.devprog.eclipse.common.PluginHelper;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class DeleteDocumentAction extends RepoTreeExtension {

	public void run() {
		try {
			DocbaseItem dtd = super.getDocbaseItem();
			IDfTypedObject tObj = (IDfTypedObject) dtd.getData();
			IDfId objId = tObj.getId("r_object_id");
			String objName = tObj.getString("object_name");

			boolean yes = MessageDialog.openConfirm(getShell(), "Delete",
					"Delete '" + objName + "' ?");
			if (yes) {
				DeleteDocumentMonitor docMon = new DeleteDocumentMonitor(objId);

				IProgressService progServ = PlatformUI.getWorkbench()
						.getProgressService();
				progServ.busyCursorWhile(docMon);
				System.out.println("docMon succ" + docMon.success());
				if (docMon.success() == false) {
					String errs = docMon.getErrors();
					MessageDialog.openError(getShell(), "Delete Error", errs);
				} else {
					TreeViewer tv = super.getRepoTreeViewer();
					tv.remove(dtd);
					if (PluginHelper.getObjectListView().getFolderId()
							.equals(objId.getId())) {
						System.out.println("found folder");
						PluginHelper.getObjectListView().clearContents();
						System.out.println("cleared view");
						/*
						 * if(dtd.getParent() != null) { if(dtd.getType() ==
						 * DocbaseItem.TYPED_OBJ_TYPE) { IDfTypedObject tobj =
						 * (IDfTypedObject) dtd.getData();
						 * PluginHelper.getObjectListView
						 * ().setFolderId(tobj.getString("r_object_id")); } }
						 */
						// TODO show parents contents
					}
					PluginHelper.getObjectListViewer().remove(dtd);
				}
			}

		} catch (Exception ex) {
			DfLogger.error(this, "Error in Delete Action", null, ex);
			MessageDialog
					.openError(getShell(), "Delete Error", ex.getMessage());
		}
	}

	public boolean showAction() {
		DocbaseItem dtd = super.getDocbaseItem();
		if ((dtd != null) && (dtd.getType().equals(DocbaseItem.TYPED_OBJ_TYPE))) {
			IDfTypedObject tObj = (IDfTypedObject) dtd.getData();
			try {
				IDfId objId = tObj.getId("r_object_id");
				if (objId.getTypePart() == IDfId.DM_DOCUMENT
						|| objId.getTypePart() == IDfId.DM_FOLDER) {
					return true;
				}
			} catch (DfException dfe) {
				DfLogger.warn(this, "", null, dfe);
			}
		}
		return false;
	}

}
