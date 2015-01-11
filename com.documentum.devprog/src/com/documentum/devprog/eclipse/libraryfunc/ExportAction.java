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
 * Created on Jul 29, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfVirtualDocumentNode;

import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.devprog.eclipse.tree.RepoTreeExtension;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.progress.IProgressConstants;

public class ExportAction extends RepoTreeExtension {
	public boolean showAction() {
		try {
			DocbaseItem dtd = getDocbaseItem();
			if (dtd == null) {
				return false;
			}

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

		} catch (Exception dfe) {
			DfLogger.warn(this, "Error ", null, dfe);
			return false;
		}
	}

	public void run() {

		DirectoryDialog dd = new DirectoryDialog(getShell());
		String destfldr = dd.open();
		if (destfldr == null || destfldr.length() == 0) {
			return;
		}

		try {
			DocbaseItem treeNode = getDocbaseItem();
			IDfId objId = null;
			if (treeNode.getType().equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) treeNode.getData();
				objId = tObj.getId("r_object_id");

			} else if (treeNode.getType().equals(DocbaseItem.VDOC_TYPE)) {
				IDfVirtualDocumentNode vn = (IDfVirtualDocumentNode) treeNode
						.getData();
				IDfDocument doc = (IDfDocument) vn.getSelectedObject();
				objId = doc.getObjectId();
			}

			ExportJob job = new ExportJob();
			job.setDestinationFolder(destfldr);
			job.setSourceId(objId);
			job.setUser(true);
			job.setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
			job.schedule();

		} catch (DfException dfe) {
			DfLogger.error(this, dfe.getMessage(), null, dfe);
		}

	}

}
