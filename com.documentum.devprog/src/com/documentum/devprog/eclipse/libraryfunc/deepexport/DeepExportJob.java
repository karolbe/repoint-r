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
 * Created on Sep 21, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc.deepexport;

import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.devprog.eclipse.common.DocLauncher;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.libraryfunc.LibraryFunctionsPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

public class DeepExportJob extends Job {

	private String destFolder = null;

	private IDfId srcFolder = null;

	private String folderName = null;

	public DeepExportJob(String folderName, IDfId folderId, String localfolder) {
		super("Deep Export Job");
		this.folderName = folderName;
		srcFolder = folderId;
		destFolder = localfolder;

	}

	public DeepExportJob() {
		super("Deep Export Job");
	}

	public void setFolderName(String fn) {
		this.folderName = fn;
	}

	public void setFolderId(IDfId fi) {
		srcFolder = fi;
	}

	public void setDestFolder(String df) {
		destFolder = df;
	}

	protected IStatus run(IProgressMonitor monitor) {

		try {
			monitor.beginTask("Deep Exporting to " + destFolder,
					IProgressMonitor.UNKNOWN);
			super.setName("Deep Exporting to: " + destFolder);

			monitor.subTask("Setting up Deep Export");
			DpDeepExportService expServ = new DpDeepExportService();
			expServ.setSM(PluginState.getSessionManager());
			monitor.subTask("Now Deep Exporting");
			String path = expServ.deepExportFolder(srcFolder, destFolder);

			monitor.done();

			DocLauncher dl = new DocLauncher();
			dl.setDocPath(path);
			dl.setDescription("View Folder");
			super.setProperty(IProgressConstants.ACTION_PROPERTY, dl);

			return new Status(IStatus.OK, LibraryFunctionsPlugin.PLUGIN_ID,
					IStatus.OK, "Complete. " + path, null);

		} catch (Exception ex) {
			ex.printStackTrace();
			DfLogger.error(this, "Deep Export " + srcFolder + " to "
					+ destFolder + " failed.", null, ex);
			monitor.done();
			return new Status(IStatus.ERROR, LibraryFunctionsPlugin.PLUGIN_ID,
					IStatus.OK, ex.getMessage(), ex);
		}

	}

}
