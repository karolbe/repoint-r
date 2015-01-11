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
 * Created on Aug 25, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.devprog.eclipse.tree.RepoTreeExtension;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class ImportDocumentAction extends RepoTreeExtension {

	public boolean showAction() {
		try {
			DocbaseItem dtd = super.getDocbaseItem();
			if ((dtd != null) && (dtd.getType() == DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) dtd.getData();
				IDfId objId = tObj.getId("r_object_id");
				return (objId.getTypePart() == IDfId.DM_FOLDER || (objId
						.getTypePart() == IDfId.DM_CABINET));
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error showAction", null, ex);

		}
		return false;
	}

	public void run() {
		try {
			DocbaseItem dtd = super.getDocbaseItem();
			IDfTypedObject tObj = (IDfTypedObject) dtd.getData();
			String fldrId = tObj.getString("r_object_id");

			ImportDocumentDialog dlg = new ImportDocumentDialog(getShell(),
					fldrId, "");
			int status = dlg.open();
			if (status == Dialog.OK) {
				String objName = dlg.getObjectName();
				String type = dlg.getObjectType();
				String fmt = dlg.getFormat();
				String fileLoc = dlg.getFile();

				ImportDocumentMonitor docMon = new ImportDocumentMonitor(
						new DfId(fldrId), fileLoc);
				docMon.setFormat(fmt);
				docMon.setObjectType(type);
				docMon.setObjectName(objName);

				IProgressService progServ = PlatformUI.getWorkbench()
						.getProgressService();
				progServ.busyCursorWhile(docMon);
				if (docMon.success() == false) {
					String errs = docMon.getErrors();
					MessageDialog.openError(getShell(), "Import Error", errs);
				} else {
					TreeViewer tv = super.getRepoTreeViewer();
					IDfId ni = docMon.getNewObjId();
					// DocbaseItem doc =
					// DocbaseItem.newTypedObjectInstance(dtd,ni);
					// tv.add(dtd,doc);
				}

			}
		} catch (Exception ex) {
			MessageDialog
					.openError(getShell(), "Import Error", ex.getMessage());
		}

	}

}
