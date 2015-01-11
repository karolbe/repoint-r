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
 * Created on May 8, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.query;

import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfSession;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.DocLauncher;
import com.documentum.devprog.eclipse.common.PluginState;

import java.io.FileWriter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.IProgressConstants;

import com.documentum.xml.xdql.IDfXmlQuery;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class XDQLQueryJob extends Job {

	private IDfXmlQuery xmlQuery = null;

	private int queryType = 0;

	private String file = null;

	public XDQLQueryJob(IDfXmlQuery query, int queryType, String file) {
		super("Asynchronous XDQL Query Execution");
		this.queryType = queryType;
		this.xmlQuery = query;
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		IDfSession sess = null;

		try {

			if (file != null) {
				monitor.beginTask(
						"Executing XDQL. This process is not cancellable",
						IProgressMonitor.UNKNOWN);
				sess = PluginState.getSessionManager().newSession(
						PluginState.getDocbase());
				xmlQuery.execute(queryType, sess);
				FileWriter fw = new FileWriter(file);
				fw.write(xmlQuery.getXMLString());
				fw.close();

				monitor.done();

				DocLauncher dl = new DocLauncher();
				dl.setDocPath(file);
				dl.setDescription("View Results: " + file);
				super.setProperty(IProgressConstants.ACTION_PROPERTY, dl);

				return new Status(IStatus.OK, DevprogPlugin.PLUGIN_ID,
						IStatus.OK, "Query Complete: " + file, null);

			} else {
				return new Status(IStatus.CANCEL, DevprogPlugin.PLUGIN_ID,
						IStatus.OK, "Job Cancelled ", null);
			}

		} catch (Exception dfe) {
			DfLogger.warn(this, "Error executing XDQL", null, dfe);
			return new Status(IStatus.ERROR, DevprogPlugin.PLUGIN_ID,
					IStatus.OK, dfe.getMessage(), dfe);
		} finally {
			if (sess != null) {
				PluginState.releaseSession(sess);
			}
		}
	}

}
