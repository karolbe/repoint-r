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
 * Created on Oct 27, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.query;

import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfAttr;

import com.documentum.fc.client.IDfTypedObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;

/**
 * 
 * Prepares the CSV file
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class CSVFilePreparer implements IRunnableWithProgress {

	// LinkedList objList = null;

	TableViewer resultsViewer = null;

	String filePath = null;

	Exception saveException = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		BufferedWriter bufw = null;
		try {
			if (resultsViewer == null) {
				saveException = new Exception("Results list empty");
				return;
			}

			if ((filePath == null) || (filePath.length() == 0)) {
				saveException = new Exception("CSV File Path is Empty");
				return;
			}

			int count = resultsViewer.getTable().getItemCount();

			monitor.beginTask("Preparing CSV File", count + 1);

			FileWriter fw = new FileWriter(filePath);
			bufw = new BufferedWriter(fw);

			IDfTypedObject tObj_0 = (IDfTypedObject) resultsViewer
					.getElementAt(0);
			int attrCnt = tObj_0.getAttrCount();

			monitor.subTask("Writing attribute names");
			for (int i = 0; i < attrCnt; i++) {
				bufw.write(tObj_0.getAttr(i).getName());
				bufw.write(',');
			}
			bufw.newLine();
			monitor.worked(1);

			if (monitor.isCanceled()) {
				monitor.setCanceled(true);
				return;
			}

			for (int i = 0; i < count; i++) {
				monitor.subTask("Writing result number " + (i + 1));
				IDfTypedObject tObj = (IDfTypedObject) resultsViewer
						.getElementAt(i);
				for (int j = 0; j < attrCnt; j++) {
					String val = null;
					IDfAttr attr = tObj.getAttr(j);
					if (attr.isRepeating()) {
						val = tObj.getAllRepeatingStrings(attr.getName(), ",");

					} else {
						val = tObj.getString(attr.getName());
					}
					bufw.write("\"");
					bufw.write(val);
					bufw.write("\"");
					if (j < (attrCnt - 1)) {
						bufw.write(",");
					}
				}
				bufw.newLine();

				monitor.worked((i + 1));

				if (monitor.isCanceled()) {
					monitor.setCanceled(true);
					return;
				}
			}

			monitor.done();
		} catch (Exception ex) {
			DfLogger.warn(this, "Error saving to csv", null, ex);
			saveException = ex;
		} finally {
			if (bufw != null) {
				try {
					bufw.close();
				} catch (IOException ioe) {
					DfLogger.warn(this, "Error saving csv", null, ioe);
				}
			}
		}

	}

	Exception getException() {
		return saveException;
	}

	public CSVFilePreparer(TableViewer resultsViewer, String file) {
		this.resultsViewer = resultsViewer;
		filePath = file;
	}

}