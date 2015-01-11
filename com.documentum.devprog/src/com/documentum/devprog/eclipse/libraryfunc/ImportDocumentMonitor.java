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

import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfSession;

import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.tree.RepoTreeExtension;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.documentum.operations.IDfImportNode;
import com.documentum.operations.IDfImportOperation;

public class ImportDocumentMonitor extends OperationProgressMonitor implements
		IRunnableWithProgress {

	private IDfId targetFolderId = null;

	private String localFile = null;

	private String objectType = "dm_document";

	private String format = null;

	private String objectName = null;

	private IDfId newObjId = null;

	ImportDocumentMonitor(IDfId targetFolderId, String localFile) {
		this.targetFolderId = targetFolderId;
		this.localFile = localFile;
		// this.format = format;

	}

	void setObjectName(String objName) {
		this.objectName = objName;
	}

	void setFormat(String fmt) {
		this.format = fmt;
	}

	void setObjectType(String type) {
		objectType = type;
	}

	public void run(IProgressMonitor mon) {
		IDfSession sess = null;
		try {
			super.setMonitor(mon);

			mon.beginTask("Importing Content ...", 100);
			IDfImportOperation impOper = PluginState.getClientX()
					.getImportOperation();
			sess = PluginState.getSessionById(targetFolderId);
			impOper.setSession(sess);
			super.setOperation(impOper);

			IDfImportNode impNode = (IDfImportNode) impOper.add(localFile);
			impNode.setDestinationFolderId(targetFolderId);
			impNode.setFilePath(localFile);
			impNode.setKeepLocalFile(true);

			impNode.setDocbaseObjectType(objectType);
			if (format != null && format.length() > 0) {
				impNode.setFormat(format);
			}
			if (objectName != null && objectName.length() > 0) {
				impNode.setNewObjectName(objectName);
			}

			if (impOper.execute()) {
				super.setSuccess(true);
				newObjId = impNode.getNewObjectId();
			} else {
				super.setSuccess(false);
			}
			mon.done();

		} catch (Exception ex) {
			DfLogger.error(this, "Error importing document", null, ex);
			super.addErrors(ex.getMessage());
		} finally {
			PluginState.releaseSession(sess);
		}
	}

	public IDfId getNewObjId() {
		return newObjId;
	}

}
