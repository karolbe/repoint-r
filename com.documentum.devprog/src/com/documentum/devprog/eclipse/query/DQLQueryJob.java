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
 * Created on Sep 23, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.query;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfAttr;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.DocLauncher;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.progress.IProgressConstants;

public class DQLQueryJob extends Job {

	private int queryType = 0;

	private String queryStr = null;

	private Exception queryException = null;

	private LinkedList resultsList = null;

	private String[] columnNames = null;

	private QueryView view = null;

	private String resultsFile = null;

	public DQLQueryJob(QueryView view, String queryStr, int queryType) {
		super("DQL Query Job");
		this.view = view;
		this.queryStr = queryStr;
		this.queryType = queryType;
	}

	protected IStatus run(IProgressMonitor monitor) {
		IDfSession sess = null;
		IDfCollection results = null;
		BufferedWriter bufw = null;

		try {
			bufw = new BufferedWriter(new FileWriter(this.getResultsFile()));
			monitor.beginTask("Executing query", IProgressMonitor.UNKNOWN);
			long beginTime = System.currentTimeMillis();
			sess = PluginState.getSessionManager().newSession(
					PluginState.getDocbase());
			IDfQuery queryObj = new DfQuery();
			queryObj.setDQL(queryStr);
			try {
				results = queryObj.execute(sess, queryType);

				monitor.subTask("Writing Column Names");
				this.writeTypedObject(results, bufw, false);

				monitor.subTask("Iterating collection ...");
				resultsList = new LinkedList();
				while (results.next()) {
					if (monitor.isCanceled()) {
						monitor.setCanceled(true);
						break;
					}
					IDfTypedObject tObj = results.getTypedObject();
					this.writeTypedObject(tObj, bufw, true);
					monitor.worked(1);
				}
				long endTime = System.currentTimeMillis();
				monitor.done();

				bufw.close();
				DocLauncher dl = new DocLauncher();
				dl.setDocPath(this.getResultsFile());
				dl.setDescription("View Results: " + this.getResultsFile());
				super.setProperty(IProgressConstants.ACTION_PROPERTY, dl);

				return new Status(IStatus.OK, DevprogPlugin.PLUGIN_ID,
						IStatus.OK, "Query Complete: " + queryStr, null);
			} catch (DfException dfe) {
				DfLogger.warn(this, "Error executing dql query ", null, dfe);
				queryException = dfe;
				monitor.done();
				return new Status(IStatus.ERROR, DevprogPlugin.PLUGIN_ID,
						IStatus.OK, dfe.getMessage(), dfe);
			}

		} catch (Exception ex) {
			DfLogger.warn(this, "Error executing dql query ", null, ex);
			queryException = ex;
			return new Status(IStatus.ERROR, DevprogPlugin.PLUGIN_ID,
					IStatus.OK, ex.getMessage(), ex);
		} finally {
			try {
				if (results != null) {
					results.close();
				}
			} catch (DfException dfe2) {
			}

			PluginState.releaseSession(sess);
		}
	}

	private void findColumnNames(IDfCollection coll) {
		try {

			int attrCnt = coll.getAttrCount();
			columnNames = new String[attrCnt];
			for (int i = 0; i < attrCnt; i++) {
				columnNames[i] = coll.getAttr(i).getName();
			}

		} catch (Exception ex) {
			DfLogger.error(this, "Error getting column names", null, ex);

		}
	}

	private void writeTypedObject(IDfTypedObject to, BufferedWriter bufw,
			boolean writeValue) throws Exception {
		int attrCnt = to.getAttrCount();
		StringBuffer buf = new StringBuffer(32);
		String lineSep = System.getProperty("line.separator");
		for (int i = 0; i < attrCnt; i++) {
			String val = "";
			IDfAttr attr = to.getAttr(i);
			if (writeValue) {
				if (attr.isRepeating()) {
					val = to.getAllRepeatingStrings(attr.getName(), ",");

				} else {
					val = to.getString(attr.getName());
				}
			} else {
				val = attr.getName();
			}

			buf.append("\"");
			buf.append(val);
			buf.append("\"");
			if (i != attrCnt - 1) {
				buf.append("\t");
			}
		}
		buf.append(lineSep);
		bufw.write(buf.toString());
	}

	class DisplayResultsAction extends Action {
		QueryView view;

		String[] colNames = null;

		LinkedList results = null;

		long execTime = 0;

		String queryStr = "";

		DisplayResultsAction(QueryView view, String queryStr,
				String[] colNames, LinkedList results, long execTime) {
			this.view = view;
			this.queryStr = queryStr;
			this.colNames = colNames;
			this.results = results;
			this.execTime = execTime;
		}

		public void run() {
			view.setupDQLResults(queryStr, colNames, results, execTime);
			PluginHelper.showQueryView(null);
		}

	}

	public String getResultsFile() {
		return resultsFile;
	}

	public void setResultsFile(String resultsFile) {
		this.resultsFile = resultsFile;
	}

}
