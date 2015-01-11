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
 * Created on Sep 13, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;

import com.documentum.devprog.common.UtilityMethods;
import com.documentum.devprog.eclipse.common.CommonConstants;
import com.documentum.devprog.eclipse.common.PluginState;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import com.documentum.operations.IDfCancelCheckoutOperation;
import com.documentum.operations.IDfDeleteOperation;
import com.documentum.operations.IDfExportNode;
import com.documentum.operations.IDfExportOperation;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class TreeHelper {

	/**
	 * Gets the size of the folder specified by the id.
	 * 
	 * @param sess
	 * @param objectId
	 * @return
	 * @throws DfException
	 */
	public static long getFolderSize(IDfSession sess, String objectId)
			throws DfException {
		IDfCollection coll = null;
		try {
			StringBuffer bufQuery = new StringBuffer(32);
			bufQuery.append(
					"select sum(content_size) as content_size from dm_sysobject,dmr_content where dm_sysobject.i_contents_id = dmr_content.r_object_id and FOLDER(ID('")
					.append(objectId).append("'),DESCEND)");
			String strQuery = bufQuery.toString();
			IDfQuery query = PluginState.getClientX().getQuery();
			query.setDQL(strQuery);
			coll = query.execute(sess, IDfQuery.DF_READ_QUERY);
			if (coll.next()) {
				long size = coll.getLong("content_size");
				return size;
			}
			return -1;
		} finally {
			if (coll != null) {
				coll.close();
			}
		}
	}

	public static String deleteFile(IDfSession sess, String objId)
			throws DfException {
		IDfDocument doc = (IDfDocument) sess.getObjectWithType(new DfId(objId),
				null, CommonConstants.DF_DOCUMENT_CLASS);

		IDfDeleteOperation delOper = PluginState.getClientX()
				.getDeleteOperation();
		delOper.add(doc);

		if (delOper.execute()) {
			return null;
		} else {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			UtilityMethods.printOperationErrors(bout, delOper.getErrors());
			return bout.toString();
		}

	}

	public static String cancelCheckout(IDfSession sess, String objId)
			throws DfException {
		IDfDocument doc = (IDfDocument) sess.getObjectWithType(new DfId(objId),
				null, CommonConstants.DF_DOCUMENT_CLASS);

		IDfCancelCheckoutOperation canOper = PluginState.getClientX()
				.getCancelCheckoutOperation();
		canOper.add(doc);
		if (canOper.execute()) {
			return null;
		} else {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			UtilityMethods.printOperationErrors(bout, canOper.getErrors());
			return bout.toString();
		}

	}

	public static void viewDocument(IDfSession sess, String objId, Shell sh)
			throws DfException {

		final IDfExportOperation expOper = PluginState.getClientX()
				.getExportOperation();
		expOper.setDestinationDirectory("c:\\devprog");
		IDfExportNode expNode = (IDfExportNode) expOper.add(new DfId(objId));

		expOper.setSession(sess);
		boolean operResult;
		IRunnableWithProgress rwp = new IRunnableWithProgress() {

			public void run(IProgressMonitor progMon) {
				try {
					progMon.beginTask("Exporting document",
							IProgressMonitor.UNKNOWN);
					expOper.execute();
				} catch (DfException dfe) {
					MessageDialog.openError(null, "View Error",
							dfe.getMessage());
					dfe.printStackTrace();
				}
			}

		};

		IProgressService progServ = PlatformUI.getWorkbench()
				.getProgressService();
		try {
			progServ.busyCursorWhile(rwp);
		} catch (InterruptedException ie) {

		} catch (InvocationTargetException ite) {

		}

		if (expOper.succeeded(expNode)) {
			String filePath = expNode.getFilePath();
			Program.launch(filePath);
		} else {
			MessageDialog.openError(sh, "View Error", "Error exporting file");
		}

	}

}
