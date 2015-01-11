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

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfVirtualDocumentNode;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.tree.DocbaseItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.operations.IDfExportNode;
import com.documentum.operations.IDfExportOperation;
import com.documentum.operations.IDfOperation;
import com.documentum.operations.IDfOperationError;
import com.documentum.operations.IDfOperationMonitor;
import com.documentum.operations.IDfOperationNode;
import com.documentum.operations.IDfOperationStep;
import com.documentum.registry.IDfCheckedOutObject;
import com.documentum.registry.IDfClientRegistry;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class ViewMonitor implements IDfOperationMonitor, IRunnableWithProgress {

	private IProgressMonitor progMon = null;

	private DocbaseItem treeNode = null;

	private String errorMessage = null;

	private String exportedFile = null;

	private boolean success = false;

	private int prevWork = 0;

	ViewMonitor(DocbaseItem node) {

		treeNode = node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.documentum.operations.IDfOperationMonitor#reportError(com.documentum
	 * .operations.IDfOperationError)
	 */
	public int reportError(IDfOperationError arg0) throws DfException {
		System.out.println("Error: " + arg0.getMessage());
		errorMessage += "," + arg0.getMessage();
		success = false;
		progMon.setCanceled(true);
		progMon.done();
		return IDfOperationMonitor.ABORT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.documentum.operations.IDfOperationMonitor#getYesNoAnswer(com.documentum
	 * .operations.IDfOperationError)
	 */
	public int getYesNoAnswer(IDfOperationError arg0) throws DfException {
		System.out.println("yes/no: " + arg0.getMessage());
		errorMessage += "," + arg0.getMessage();
		success = false;
		progMon.setCanceled(true);
		progMon.done();
		return IDfOperationMonitor.NO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.documentum.operations.IDfOperationMonitor#progressReport(com.documentum
	 * .operations.IDfOperation, int,
	 * com.documentum.operations.IDfOperationStep, int,
	 * com.documentum.operations.IDfOperationNode)
	 */
	public int progressReport(IDfOperation arg0, int arg1,
			IDfOperationStep arg2, int arg3, IDfOperationNode arg4)
			throws DfException {
		if (progMon != null) {
			progMon.subTask(arg2.getDescription());
			progMon.worked(arg1 - prevWork);
			prevWork = arg1;
			if (progMon.isCanceled()) {
				success = false;
				progMon.setCanceled(true);
				progMon.done();
				return IDfOperationMonitor.ABORT;
			}
		}
		return IDfOperationMonitor.CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		progMon = monitor;
		executeOperation();
	}

	private void executeOperation() {
		IDfSession sess = null;
		try {
			sess = PluginState.getSession();
			IDfDocument doc = null;

			if (treeNode.getType().equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) treeNode.getData();
				String objId = tObj.getString("r_object_id");
				doc = (IDfDocument) sess.getObject(new DfId(objId));
			} else if (treeNode.getType().equals(DocbaseItem.VDOC_TYPE)) {
				IDfVirtualDocumentNode vn = (IDfVirtualDocumentNode) treeNode
						.getData();
				IDfId objId = vn.getBasicAttributes().getId("r_object_id");
				doc = (IDfDocument) sess.getObject(objId);
			}

			// String curUser = sess.getLoginUserName();
			if (doc.isCheckedOut()) {
				IDfClientX cx = new DfClientX();
				IDfClientRegistry reg = cx.getClientRegistry();
				IDfCheckedOutObject co = reg.getCheckedOutObjectById(doc
						.getObjectId());
				String path = co.getFilePath();
				exportedFile = path;
				success = true;
				return;
			}

			IDfExportOperation expOper = PluginState.getClientX()
					.getExportOperation();

			// System.out.println("numStems: " + numSteps);
			progMon.beginTask("Exporting  " + doc.getObjectName(), 100);

			// testing
			String exportDir = null;
			try {
				PropertyResourceBundle bndl = (PropertyResourceBundle) PropertyResourceBundle
						.getBundle("dfc");
				exportDir = bndl.getString("dfc.export.dir");
				// System.out.println("DFC Export Dir: " + exportDir);
			} catch (Exception ex) {
				DfLogger.error(this,
						"Unable to get DFC Export Dir. Using user temp folder",
						null, ex);
				exportDir = null;
			}
			if (exportDir == null) {
				// String tempDir = "\"" + System.getProperty("java.io.tmpdir")
				// + "\"";
				String tempDir = System.getProperty("java.io.tmpdir");
				// System.out.println("temp dir: " + tempDir);
				expOper.setDestinationDirectory(tempDir);
			}

			expOper.setOperationMonitor(this);
			IDfExportNode node = (IDfExportNode) expOper.add(doc);

			if (expOper.execute()) {
				progMon.done();
				exportedFile = node.getFilePath();
				success = true;
			} else {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PluginHelper.printOperationErrors(bout, expOper.getErrors());
				errorMessage = bout.toString();
				success = false;
			}
		} catch (Exception ex) {
			DfLogger.warn(this, "Error in view monitor", null, ex);
			errorMessage = ex.getMessage();
			success = false;
		} finally {
			PluginState.releaseSession(sess);
		}
	}

	String getErrorMessage() {
		return errorMessage;
	}

	String getExportedFile() {
		return exportedFile;
	}

	boolean success() {
		return success;
	}

}
