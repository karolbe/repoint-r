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
 * Created on Aug 6, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.query;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginState;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Execute query with progress dialog.
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class DQLQueryExecutor extends Job {
	private int queryType = 0;

	private String queryStr = null;

	private Exception queryException = null;

	private String[] columnNames = null;

	private Table resultsTable = null;

	private TableViewer resultsViewer = null;

	private StackLayout resultsLayout = null;

	private Text resultsSummary = null;

	private long beginTime = 0;

	private long endTime = 0;

	private LinkedList itemQ = null;

	private Object qSyncObject = new Object();

	private int cntrResults;

	DQLQueryExecutor(String queryStr, int type) {
		super("DQL Query Executor");

		this.queryStr = queryStr;
		this.queryType = type;

		itemQ = new LinkedList();

	}

	void setUIControls(Table resultsTable, TableViewer resultsViewer,
			StackLayout resultsLayout, Text resultsSummary) {
		this.resultsTable = resultsTable;
		this.resultsViewer = resultsViewer;
		this.resultsLayout = resultsLayout;
		this.resultsSummary = resultsSummary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		IDfSession sess = null;
		IDfCollection results = null;
		try {

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					resultsSummary.setText("Executing Query...");
				}
			});

			monitor.beginTask("Executing query", IProgressMonitor.UNKNOWN);
			sess = PluginState.getSession();
			IDfQuery queryObj = new DfQuery();
			queryObj.setDQL(queryStr);
			try {
				beginTime = System.currentTimeMillis();
				results = queryObj.execute(sess, queryType);

				monitor.subTask("Finding Column Names");
				findColumnNames(results);

				monitor.subTask("Iterating collection ...");
				setupDQLResults(monitor, results);
				monitor.done();

				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						doSort();
					}
				});

				IStatus retStat = new Status(IStatus.OK,
						DevprogPlugin.PLUGIN_ID, IStatus.OK,
						"Finished Query Execution", null);
				super.done(retStat);
				return retStat;
			} catch (DfException dfe) {
				DfLogger.warn(this, "Error executing dql query ", null, dfe);
				final String errMsg = dfe.getMessage();
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						resultsSummary.setText("Query Error:" + errMsg);
					}
				});
				IStatus errStatus = new Status(IStatus.ERROR,
						DevprogPlugin.PLUGIN_ID, IStatus.OK,
						"Error Executing Query", dfe);
				return errStatus;
			}

		} catch (Exception ex) {
			DfLogger.warn(this, "Error executing dql query ", null, ex);
			return new Status(IStatus.ERROR, DevprogPlugin.PLUGIN_ID,
					IStatus.OK, "Error Executing Query", ex);

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

	public void setupDQLResults(IProgressMonitor monitor,
			IDfCollection resultsColl) throws DfException {
		final int attrCnt = columnNames.length;

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				// should dispose old columns as query will fill new
				resultsTable.removeAll();
				TableColumn cols[] = resultsTable.getColumns();
				for (int i = 0; i < cols.length; i++) {
					cols[i].dispose();
				}

				CellEditor[] editors = new CellEditor[attrCnt];
				for (int i = 0; i < attrCnt; i++) {
					TableColumn col = new TableColumn(resultsTable, SWT.LEFT, i);
					final String colName = columnNames[i];
					col.setText(colName);
					col.setResizable(true);
					col.setWidth(150);
					// System.out.println("Created column: " + colNames[i]);

					editors[i] = new TextCellEditor(resultsTable, SWT.READ_ONLY);
					Text txtCtrl = (Text) editors[i].getControl();
					txtCtrl.setTextLimit(255);

				}
				resultsViewer.setColumnProperties(columnNames);
				resultsLayout.topControl = resultsTable;
				resultsTable.getParent().layout();
			}

		});

		final String msgStreaming = "Finished Executing. Now Streaming Results ...";
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				resultsSummary.setText(msgStreaming);
			}
		});

		cntrResults = 0;
		final int bufSize = 9;
		int cntrBuffer = 0;
		while (resultsColl.next()) {

			if (monitor.isCanceled()) {
				return;
			}

			IDfTypedObject tObj = resultsColl.getTypedObject();
			if (cntrBuffer >= bufSize) {
				cntrBuffer = 0;
				final int curCnt = cntrResults;

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						for (int ibuf = 0; ibuf < bufSize; ibuf++) {
							IDfTypedObject tmpTObj = getNextObject();
							if (tmpTObj != null) {
								resultsViewer.add(tmpTObj);
							} else
								break;
						}
						resultsSummary.setText(msgStreaming + curCnt);
					}
				});
			}

			addObject(tObj);
			cntrBuffer++;
			cntrResults++;

		}// while

		if (cntrBuffer > 0) {
			final int fbufCntr = cntrBuffer;

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					for (int ibuf = 0; ibuf < fbufCntr; ibuf++) {
						IDfTypedObject tmpTObj = getNextObject();
						if (tmpTObj != null) {
							resultsViewer.add(tmpTObj);
						} else
							break;
					}
				}
			});
		}

		endTime = System.currentTimeMillis();
		long execTime = endTime - beginTime;
		final StringBuffer bufSummary = new StringBuffer(32);
		bufSummary.append("Count: " + cntrResults) //$NON-NLS-1$
				.append(".  "); //$NON-NLS-1$
		bufSummary.append("Approximate Time: ").append( //$NON-NLS-1$
				String.valueOf(execTime));
		bufSummary.append(" milliseconds  "); //$NON-NLS-1$

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				resultsSummary.setText(bufSummary.toString());
			}
		});

	}

	IDfTypedObject getNextObject() {
		synchronized (qSyncObject) {
			IDfTypedObject tObj = (IDfTypedObject) itemQ.removeFirst();
			return tObj;
		}
	}

	void addObject(IDfTypedObject tobj) {
		synchronized (qSyncObject) {
			itemQ.add(tobj);
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

	/**
	 * Integration of the ViewerSorter with the async addition of results.
	 * 
	 */
	private void doSort() {
		final TableColumn[] cols = resultsTable.getColumns();
		for (int icol = 0; icol < cols.length; icol++) {
			final String colName = cols[icol].getText();
			final TableColumn curCol = cols[icol];
			cols[icol].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					DQLResultsSorter sorter = new DQLResultsSorter(colName);
					Object[] resObj = new Object[cntrResults];
					for (int i = 0; i < cntrResults; i++) {
						resObj[i] = resultsViewer.getElementAt(i);
					}

					sorter.sort(resultsViewer, resObj);
					resultsTable.removeAll();
					int sortDir = resultsTable.getSortDirection();
					if (sortDir == SWT.NONE)
						sortDir = SWT.UP;

					if (resultsTable.getSortColumn() == curCol) {
						if (sortDir == SWT.DOWN)
							sortDir = SWT.UP;
						else if (sortDir == SWT.UP) {
							sortDir = SWT.DOWN;
						} else
							sortDir = SWT.UP;
					}

					resultsTable.setSortColumn(curCol);
					resultsTable.setSortDirection(sortDir);

					System.out.println("resObj Length: " + resObj.length);
					if (sortDir == SWT.UP) {
						for (int i = 0; i < resObj.length; i++) {
							resultsViewer.add(resObj[i]);

						}
					} else if (sortDir == SWT.DOWN) {
						for (int i = resObj.length - 1; i >= 0; i--) {
							resultsViewer.add(resObj[i]);
						}
					}

				}
			});
		}
	}

	String[] getColumnNames() {
		if (columnNames == null) {
			columnNames = new String[] {};
		}
		return columnNames;
	}

	Exception getException() {
		return queryException;
	}

}
