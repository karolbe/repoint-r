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
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfSession;

import com.documentum.devprog.eclipse.common.DocLauncher;
import com.documentum.devprog.eclipse.common.PluginState;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

import com.documentum.operations.IDfExportNode;
import com.documentum.operations.IDfExportOperation;
import com.documentum.operations.IDfOperation;
import com.documentum.operations.IDfOperationError;
import com.documentum.operations.IDfOperationMonitor;
import com.documentum.operations.IDfOperationNode;
import com.documentum.operations.IDfOperationStep;

public class ExportJob extends Job implements IDfOperationMonitor {
	private String destFldr = null;

	private IDfId srcId = null;

	private IProgressMonitor monitor = null;

	private int prevWork = 0;

	public ExportJob() {
		super("Export Content");

	}

	public IStatus run(IProgressMonitor mon) {
		monitor = mon;
		IDfSession sess = null;
		try {
			IDfId si = getSourceId();
			if (si == null) {
				return Status.CANCEL_STATUS;
			}

			String destFldr = getDestinationFolder();
			DfLogger.debug(this, "Export oper " + destFldr, null, null);
			if (destFldr == null || destFldr.length() == 0) {

				return Status.CANCEL_STATUS;
			}

			IDfExportOperation expOper = PluginState.getClientX()
					.getExportOperation();
			expOper.setDestinationDirectory(destFldr);

			expOper.setOperationMonitor(this);

			String docbase = DfClient.getLocalClient().getDocbaseNameFromId(si);
			sess = PluginState.getSessionManager().newSession(docbase);
			IDfDocument doc = (IDfDocument) sess.getObject(si);
			monitor.beginTask("Exporting " + doc.getObjectName(), 100);
			super.setName("Exporting " + doc.getObjectName());
			IDfExportNode node = (IDfExportNode) expOper.add(doc);
			if (expOper.execute()) {
				monitor.done();

				DocLauncher dl = new DocLauncher();
				dl.setDocPath(node.getFilePath());
				dl.setDescription("View " + node.getFilePath());
				super.setProperty(IProgressConstants.ACTION_PROPERTY, dl);

				DfLogger.debug(this,
						"Export operation succeeded " + node.getFilePath(),
						null, null);
				return new Status(IStatus.OK, LibraryFunctionsPlugin.PLUGIN_ID,
						IStatus.OK, "Export Successful. Click to View File",
						null);
			} else {
				StringBuffer buf = new StringBuffer(32);
				IDfList errLst = expOper.getErrors();
				for (int i = 0; i < errLst.getCount(); i++) {
					String err = errLst.getString(i);
					buf.append(err);
					buf.append("\r\n");
				}
				DfLogger.debug(this,
						"Export operation failed " + buf.toString(), null, null);
				return new Status(IStatus.ERROR,
						LibraryFunctionsPlugin.PLUGIN_ID, IStatus.ERROR,
						buf.toString(), null);
			}

		} catch (DfException dfe) {
			DfLogger.error(this, "Error exporting content ", null, dfe);
			return new Status(IStatus.ERROR, LibraryFunctionsPlugin.PLUGIN_ID,
					IStatus.ERROR, "Error Exporting Content ", dfe);
		} finally {
			if (sess != null) {
				PluginState.releaseSession(sess);
			}
		}

	}

	void setDestinationFolder(String fldr) {
		destFldr = fldr;
	}

	String getDestinationFolder() {
		return destFldr;
	}

	void setSourceId(IDfId id) {
		srcId = id;
	}

	IDfId getSourceId() {
		return srcId;
	}

	public int getYesNoAnswer(IDfOperationError question) throws DfException {
		DfLogger.info(this, "Export oper err " + question.getMessage(), null,
				null);
		return IDfOperationMonitor.NO;
	}

	public int progressReport(IDfOperation operation, int operationPercentDone,
			IDfOperationStep step, int stepPercentDone, IDfOperationNode node)
			throws DfException {
		monitor.subTask(step.getName());
		monitor.worked(operationPercentDone - prevWork);
		prevWork = operationPercentDone;
		if (monitor.isCanceled()) {
			return IDfOperationMonitor.ABORT;
		}
		return IDfOperationMonitor.CONTINUE;
	}

	public int reportError(IDfOperationError error) throws DfException {
		DfLogger.info(this, "eXport oper err " + error.getMessage(), null, null);
		return IDfOperationMonitor.ABORT;
	}

}
