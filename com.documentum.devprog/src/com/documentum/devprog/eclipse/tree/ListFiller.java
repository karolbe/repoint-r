/*
 * Created on Apr 12, 2007
 *
 * EMC Documentum Developer Program 2006
 */

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
 �*�
 �*******************************************************************************/
package com.documentum.devprog.eclipse.tree;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.common.PreferenceConstants;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class ListFiller implements Runnable {
	private TableViewer viewer = null;

	private IDfCollection folderColl = null;

	private IDfCollection docColl = null;

	private String folderId = null;

	private Table resultsTable;

	/**
	 * Id of object that should be selected in the folder list.
	 */
	private String selectionId = null;

	private int cntrResults = 0;

	public String getSelectionId() {
		return selectionId;
	}

	public void setSelectionId(String selectionId) {
		this.selectionId = selectionId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	ListFiller(IDfCollection folderColl, IDfCollection docColl, TableViewer tv) {
		this.folderColl = folderColl;
		this.docColl = docColl;

		this.viewer = tv;
		this.resultsTable = viewer.getTable();
	}

	ListFiller(String folderId, TableViewer tv) {
		this.folderId = folderId;
		this.viewer = tv;
		this.resultsTable = viewer.getTable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		IDfSession sess = null;
		try {

			StringBuffer bufQuery = new StringBuffer(32);
			bufQuery.append("select ")
					.append(DocbaseItem.getDefaultQueryAttributes())
					.append(" from ");

			boolean showAll = DevprogPlugin.getDefault().getPluginPreferences()
					.getBoolean(PreferenceConstants.P_SHOW_ALL_OBJECTS);
			// Show all objects or just folders and docs
			// If all then you just need one query. If diff then two diff
			// queries are needed.
			if (showAll) {
				bufQuery.append("dm_sysobject");
			} else {
				bufQuery.append(" dm_folder");
			}

			bufQuery.append(" where FOLDER(ID('");
			bufQuery.append(folderId).append("')) ORDER BY 1");

			String strQuery = bufQuery.toString();
			System.out.println("Folder query: " + strQuery);

			sess = PluginState.getSessionById(folderId);
			IDfQuery query = new DfQuery();
			query.setDQL(strQuery);
			folderColl = query.execute(sess, IDfQuery.DF_READ_QUERY);
			System.out.println("Folder Coll: " + folderColl.getAttrCount());

			if (showAll == false) {
				bufQuery = new StringBuffer(32);
				bufQuery.append("select ")
						.append(DocbaseItem.getDefaultQueryAttributes())
						.append(" from dm_document where FOLDER(ID('");

				bufQuery.append(folderId).append("')) ORDER BY 1");
				strQuery = bufQuery.toString();
				// System.out.println("Doc Query: " + strQuery);
				query = new DfQuery();
				query.setDQL(strQuery);
				docColl = query.execute(sess, IDfQuery.DF_READ_QUERY);
			}

			final int bufSize = 7;
			DocbaseItem[] itemArr = new DocbaseItem[bufSize];

			// clear existing contents
			if (folderColl != null) {
				int cntrBuf = 0;
				while (folderColl.next()) {
					IDfTypedObject tObj = folderColl.getTypedObject();
					final DocbaseItem di = new DocbaseItem();
					di.setData(tObj);
					di.setType(DocbaseItem.TYPED_OBJ_TYPE);
					di.setParent(null);

					if (cntrBuf >= bufSize) {
						cntrBuf = 0;
						final DocbaseItem[] ia = itemArr;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								// System.out.println("folderColl: asyncExec called - ");

								for (int i = 0; i < bufSize; i++) {
									// System.out.println("folderColl: Adding "
									// + i);
									viewer.add(ia[i]);
								}
							}
						});
						itemArr = new DocbaseItem[bufSize];
					}
					// System.out.println("Adding to itemArr: " +
					// di.getLabelText());
					itemArr[cntrBuf] = di;
					DocbaseItemListView.putNode(tObj.getString("r_object_id"),
							di);
					cntrBuf++;
					cntrResults++;
				}// while

				if (cntrBuf > 0) {
					final int fCntrBuf = cntrBuf;
					final DocbaseItem[] ia = itemArr;
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {

							for (int i = 0; i < fCntrBuf; i++) {
								viewer.add(ia[i]);

							}
						}
					});
					cntrResults += fCntrBuf;

				}

			}

			if (docColl != null && (showAll == false)) {

				int cntrBuf = 0;
				itemArr = new DocbaseItem[bufSize];
				while (docColl.next()) {
					IDfTypedObject tObj = docColl.getTypedObject();
					final DocbaseItem di = new DocbaseItem();
					di.setData(tObj);
					di.setType(DocbaseItem.TYPED_OBJ_TYPE);
					di.setParent(null);

					if (cntrBuf >= bufSize) {
						cntrBuf = 0;
						final DocbaseItem[] ia = itemArr;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {

								for (int i = 0; i < bufSize; i++) {
									viewer.add(ia[i]);
								}

							}
						});
						itemArr = new DocbaseItem[bufSize];
					}

					itemArr[cntrBuf] = di;
					DocbaseItemListView.putNode(tObj.getString("r_object_id"),
							di);
					cntrBuf++;
					cntrResults++;
				}// while

				if (cntrBuf > 0) {
					final int fCntrBuf = cntrBuf;
					final DocbaseItem[] ia = itemArr;
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {

							for (int i = 0; i < fCntrBuf; i++) {
								viewer.add(ia[i]);
							}
						}
					});
					cntrResults += fCntrBuf;
				}

			}

			final DocbaseItem selObj = DocbaseItemListView
					.getNode(getSelectionId());
			System.out.println("selObj for displ: " + selObj);
			if (selObj != null) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						StructuredSelection sel = new StructuredSelection(
								selObj);
						viewer.setSelection(sel, true);
						viewer.getTable().setFocus();
					}

				});
			}

			System.out.println("Num items: " + cntrResults);
			// doSort();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (docColl != null)
				try {
					docColl.close();
				} catch (DfException dfe1) {
				}

			if (folderColl != null)
				try {
					folderColl.close();
				} catch (DfException dfe2) {
				}

			PluginState.releaseSession(sess);

		}

	}

	private void doSort() {
		final TableColumn[] cols = resultsTable.getColumns();
		for (int icol = 0; icol < cols.length; icol++) {
			final String colName = cols[icol].getText();
			final TableColumn curCol = cols[icol];
			cols[icol].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					try {
						DocbaseItemListSorter sorter = new DocbaseItemListSorter(
								colName);
						System.out.println("cntrResults:" + cntrResults);
						Object[] resObj = new Object[cntrResults];
						for (int i = 0; i < cntrResults; i++) {
							resObj[i] = viewer.getElementAt(i);
						}

						sorter.sort(viewer, resObj);
						viewer.setInput(new Object());
						// resultsTable.removeAll();
						int sortDir = resultsTable.getSortDirection();
						/*
						 * 
						 * if(sortDir == SWT.NONE)
						 * System.out.println("Cur SORT DIR IS NONE"); else
						 * if(sortDir == SWT.UP)
						 * System.out.println("cur sort dir is up"); else
						 * if(sortDir == SWT.DOWN)
						 * System.out.println("cur sort dir is down");
						 */

						if (sortDir == SWT.NONE)
							sortDir = SWT.UP;

						if (resultsTable.getSortColumn() == curCol) {
							System.out.println("Current column selected");
							if (sortDir == SWT.DOWN)
								sortDir = SWT.UP;
							else if (sortDir == SWT.UP) {
								sortDir = SWT.DOWN;
							} else
								sortDir = SWT.UP;
						}

						resultsTable.setSortColumn(curCol);
						resultsTable.setSortDirection(sortDir);
						System.out.println("resobj lenght: " + resObj.length);
						if (sortDir == SWT.UP) {
							for (int i = 0; i < resObj.length; i++) {
								viewer.add(resObj[i]);
							}
						} else if (sortDir == SWT.DOWN) {
							for (int i = resObj.length - 1; i >= 0; i--) {
								viewer.add(resObj[i]);
							}
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			});
		}
	}

}
