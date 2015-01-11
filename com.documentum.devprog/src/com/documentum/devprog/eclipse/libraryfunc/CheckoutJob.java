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

import com.documentum.devprog.eclipse.common.DocLauncher;
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
import org.eclipse.ui.progress.IProgressConstants;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.operations.IDfCheckoutNode;
import com.documentum.operations.IDfCheckoutOperation;
import com.documentum.operations.IDfOperation;
import com.documentum.operations.IDfOperationError;
import com.documentum.operations.IDfOperationMonitor;
import com.documentum.operations.IDfOperationNode;
import com.documentum.operations.IDfOperationStep;

public class CheckoutJob extends Job implements IDfOperationMonitor {

	private IDfId objectId = null;
	private String destFolder = null;

	private IProgressMonitor progMon = null;

	private int prevWork = 0;

	private TableViewer listViewer = null;
	private DocbaseItem docbaseItem = null;

	public CheckoutJob() {
		super("Checkout Content Job");
	}

	protected IStatus run(IProgressMonitor monitor) {
		progMon = monitor;
		IDfSession sess = null;

		try {
			progMon.beginTask("Checkout Job", 100);
			progMon.subTask("Initializing...");
			IDfClientX cx = new DfClientX();

			IDfCheckoutOperation oper = cx.getCheckoutOperation();
			oper.setOperationMonitor(this);

			sess = PluginState.getSessionById(getObjectId());

			progMon.subTask("Obtaining Repository Object");
			IDfSysObject obj = (IDfSysObject) sess.getObject(getObjectId());

			IDfCheckoutNode chkNode = (IDfCheckoutNode) oper.add(obj);

			progMon.subTask("Establishing Destination Folder");
			String df = getDestinationFolder();
			if ((df != null) && (df.length() > 0)) {
				oper.setDestinationDirectory(df);
			}

			progMon.subTask("Now Executing Checkout");
			IStatus retVal = null;
			if (oper.execute()) {
				DocLauncher dl = new DocLauncher();
				dl.setDocPath(chkNode.getFilePath());
				dl.setDescription("View Checked Out File: "
						+ chkNode.getFilePath());
				super.setProperty(IProgressConstants.ACTION_PROPERTY, dl);

				docbaseItem.refreshTypedObject();
				Display disp = Display.getCurrent();
				if (disp == null)
					disp = Display.getDefault();
				disp.syncExec(new Runnable() {

					public void run() {
						listViewer.refresh(docbaseItem);
					}
				});

				retVal = new Status(IStatus.OK,
						LibraryFunctionsPlugin.PLUGIN_ID, IStatus.OK,
						"Checkout Successful", null);
			} else {
				String errs = PluginHelper.prepareOperationError(oper
						.getErrors());
				Exception cex = new Exception(errs);
				retVal = new Status(IStatus.ERROR,
						LibraryFunctionsPlugin.PLUGIN_ID, IStatus.ERROR,
						"Checkout Failed", cex);
			}

			progMon.done();
			return retVal;
		} catch (Exception ex) {
			DfLogger.error(this, "Error executing checkout operation", null, ex);
			return new Status(IStatus.ERROR, LibraryFunctionsPlugin.PLUGIN_ID,
					IStatus.ERROR, "Checkout Failed", ex);
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

	public void setDestinationFolder(String df) {
		destFolder = df;
	}

	public String getDestinationFolder() {
		return destFolder;
	}

	public void setObjectId(IDfId id) {
		objectId = id;
	}

	public IDfId getObjectId() {
		return objectId;
	}

	public void setUIData(TableViewer tv, DocbaseItem dtd) {
		this.listViewer = tv;
		this.docbaseItem = dtd;
	}
}
