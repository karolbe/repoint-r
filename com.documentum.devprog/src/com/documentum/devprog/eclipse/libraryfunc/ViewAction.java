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
 * Created on Jun 28, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.devprog.eclipse.tree.RepoTreeExtension;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class ViewAction extends RepoTreeExtension {

	public boolean showAction() {
		try {
			DocbaseItem dtd = getDocbaseItem();
			if (dtd != null) {
				if (dtd.getType().equals(DocbaseItem.TYPED_OBJ_TYPE)) {
					IDfTypedObject tObj = (IDfTypedObject) dtd.getData();
					IDfId objId = tObj.getId("r_object_id");
					if (objId.getTypePart() == IDfId.DM_DOCUMENT) {
						return true;
					} else {
						return false;
					}
				} else if (dtd.getType().equals(DocbaseItem.DOCBASE_TYPE)) {
					return false;
				}
				return true;
			} else
				return false;

		} catch (Exception dfe) {
			DfLogger.warn(this, "Error ", null, dfe);
			return false;
		}

	}

	public void run() {
		try {

			ViewMonitor vm = new ViewMonitor(getDocbaseItem());

			IProgressService progServ = PlatformUI.getWorkbench()
					.getProgressService();
			progServ.busyCursorWhile(vm);

			if (vm.success()) {
				String expFl = vm.getExportedFile();

				boolean suc = Program.launch(expFl);
				if (suc == false) {
					MessageDialog.openError(null, "Launch Error",
							" Failed to launch program");
				}
			} else {
				MessageDialog.openError(null, "View Failed",
						vm.getErrorMessage());
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error viewing file", null, ex);
		}

	}

}
