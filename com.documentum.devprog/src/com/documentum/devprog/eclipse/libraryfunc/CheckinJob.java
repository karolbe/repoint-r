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
 * Created on Mar 28, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.tree.DocbaseItem;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.operations.IDfCheckinNode;
import com.documentum.operations.IDfCheckinOperation;
import com.documentum.operations.IDfOperation;
import com.documentum.operations.IDfOperationError;
import com.documentum.operations.IDfOperationMonitor;
import com.documentum.operations.IDfOperationNode;
import com.documentum.operations.IDfOperationStep;

public class CheckinJob extends Job implements IDfOperationMonitor {
	private IDfId objectId = null;

	private TableViewer repoTree = null;
	private DocbaseItem docbaseItem = null;

	private IProgressMonitor progMon = null;
	private int prevWork = 0;

	private String versionLabels = null;
	private int versionPolicy = IDfCheckinOperation.NEXT_MINOR;
	private String checkinFile = null;
	private String logEntry = null;

	public CheckinJob() {
		super("Checkin Job");
	}

	protected IStatus run(IProgressMonitor monitor) {
		progMon = monitor;
		IDfSession sess = null;
		try {
			progMon.beginTask("Checkin Job", 100);
			IDfClientX cx = new DfClientX();
			IDfCheckinOperation oper = cx.getCheckinOperation();
			oper.setOperationMonitor(this);

			progMon.subTask("Obtaining Repository Object");
			sess = PluginState.getSessionById(getObjectId());
			IDfSysObject sObj = (IDfSysObject) sess.getObject(getObjectId());

			IDfCheckinNode chkNode = (IDfCheckinNode) oper.add(sObj);
			chkNode.setCheckinVersion(this.getVersionPolicy());

			String filePath = this.getCheckinFile();
			if (filePath != null && filePath.trim().length() > 0) {
				chkNode.setFilePath(this.getCheckinFile());
			}

			String verLbl = this.getVersionLabels();
			if (verLbl != null && verLbl.trim().length() > 0) {
				chkNode.setVersionLabels(verLbl);
			}

			IStatus retVal = null;
			progMon.subTask("Executing Operation");
			if (oper.execute()) {
				String le = this.getLogEntry();

				if (le != null && le.trim().length() > 0) {
					IDfSysObject sobj = chkNode.getNewObject();
					System.out.println(sobj.getAllRepeatingStrings(
							"r_version_label", ","));
					sobj.setLogEntry(le);
					sobj.save();

				}

				docbaseItem
						.replaceTypedObject(chkNode.getNewObjectId().getId());

				Display disp = Display.getCurrent();
				if (disp == null)
					disp = Display.getDefault();
				disp.syncExec(new Runnable() {

					public void run() {
						repoTree.refresh(docbaseItem);
					}
				});

				retVal = new Status(IStatus.OK,
						LibraryFunctionsPlugin.PLUGIN_ID, IStatus.OK,
						"Checkin Successful", null);
			} else {
				String errs = PluginHelper.prepareOperationError(oper
						.getErrors());
				Exception cex = new Exception(errs);
				retVal = new Status(IStatus.ERROR,
						LibraryFunctionsPlugin.PLUGIN_ID, IStatus.ERROR,
						"Checkin Failed", cex);

			}
			progMon.done();
			return retVal;
		} catch (Exception ex) {

			DfLogger.error(this, "Error checkin", null, ex);
			return new Status(IStatus.ERROR, LibraryFunctionsPlugin.PLUGIN_ID,
					IStatus.ERROR, "Checkin Failed", ex);
		}

	}

	public int reportError(IDfOperationError err) throws DfException {
		if (progMon.isCanceled()) {
			return IDfOperationMonitor.ABORT;
		}
		return IDfOperationMonitor.CONTINUE;
	}

	public int getYesNoAnswer(IDfOperationError err) throws DfException {
		if (progMon.isCanceled()) {
			return IDfOperationMonitor.NO;
		}
		return IDfOperationMonitor.YES;
	}

	public int progressReport(IDfOperation operation, int operationPercentDone,
			IDfOperationStep step, int stepPercentDone, IDfOperationNode node)
			throws DfException {
		progMon.subTask(step.getDescription());
		progMon.worked(operationPercentDone - prevWork);
		prevWork = operationPercentDone;
		if (progMon.isCanceled()) {
			return IDfOperationMonitor.ABORT;
		} else {
			return IDfOperationMonitor.CONTINUE;
		}
	}

	public void setObjectId(IDfId id) {
		this.objectId = id;
	}

	public IDfId getObjectId() {
		return objectId;
	}

	public String getVersionLabels() {
		return versionLabels;
	}

	public void setVersionLabels(String versionLabels) {
		this.versionLabels = versionLabels;
	}

	public int getVersionPolicy() {
		return versionPolicy;
	}

	public void setVersionPolicy(int versionPolicy) {
		this.versionPolicy = versionPolicy;
	}

	public String getCheckinFile() {
		return checkinFile;
	}

	public void setCheckinFile(String checkinFile) {
		this.checkinFile = checkinFile;
	}

	public String getLogEntry() {
		return logEntry;
	}

	public void setLogEntry(String logEntry) {
		this.logEntry = logEntry;
	}

	public TableViewer getRepoTree() {
		return repoTree;
	}

	public void setRepoTree(TableViewer repoTree) {
		this.repoTree = repoTree;
	}

	public DocbaseItem getParent() {
		return docbaseItem;
	}

	public void setParent(DocbaseItem parent) {
		this.docbaseItem = parent;
	}

}
