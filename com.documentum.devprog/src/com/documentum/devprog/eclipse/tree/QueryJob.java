/*
 * Created on Jul 20, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.DfException;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.util.LinkedList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class QueryJob extends Job {

	private String searchString = null;

	private TableViewer resultsViewer = null;

	private LinkedList resBuffer = new LinkedList();

	private Object syncObj = new Object();

	private DocbaseItem selectedNode = null;

	private String docbase = null;

	/*
	 * The id of the folder to query. If this is set, then it takes precedence
	 * over the selected folder in the tree
	 */
	private String folderId = null;

	QueryJob() {
		super("OnceYouTypeQueryJob");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {

		IDfSession sess = null;
		IDfCollection coll = null;
		try {

			IStatus canStat = new Status(IStatus.CANCEL,
					DevprogPlugin.PLUGIN_ID, IStatus.OK, "Cancelled", null);
			if (monitor.isCanceled()) {
				done(canStat);
				return canStat;
			}

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {

					IViewPart vp = (IViewPart) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.getActivePart();
					IStatusLineManager smgr = vp.getViewSite().getActionBars()
							.getStatusLineManager();
					smgr.getProgressMonitor().beginTask(
							"Performing a DESCEND Query on the Folder ...",
							IProgressMonitor.UNKNOWN);
					smgr.setMessage(PluginHelper.getImage("search.gif"),
							"Querying");
				}

			});

			DocbaseItem sdi = selectedNode;
			System.out.println("sdi: " + sdi);
			String fldrId = null;
			if ((folderId != null) && folderId.length() > 0) {
				fldrId = folderId;
				sess = PluginState.getSessionById(fldrId);
			} else if (sdi != null) {
				if (sdi.getType() == DocbaseItem.DOCBASE_TYPE) {
					sess = PluginState.getSession((String) sdi.getData());
				} else if (sdi.getType() == DocbaseItem.TYPED_OBJ_TYPE) {
					IDfTypedObject to = (IDfTypedObject) sdi.getData();
					try {
						fldrId = to.getString("r_object_id");
						sess = PluginState.getSessionById(fldrId);
					} catch (DfException dfe2) {
					}
				}
			} else {
				sess = PluginState.getSession();
			}

			String strQuery = ListViewHelper.formQueryString(searchString,
					fldrId);

			IDfQuery query = PluginState.getClientX().getQuery();
			query.setDQL(strQuery);

			coll = query.execute(sess, IDfQuery.DF_READ_QUERY);
			if (monitor.isCanceled()) {
				done(canStat);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {

						IViewPart vp = (IViewPart) PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage()
								.getActivePart();
						IStatusLineManager smgr = vp.getViewSite()
								.getActionBars().getStatusLineManager();
						smgr.getProgressMonitor().done();
						smgr.setMessage("Querying Stopped");
					}

				});

				return canStat;
			}

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					resultsViewer.setInput(new Object());
				}

			});

			int cntrBuf = 0;
			final int bufSize = 5;
			while (coll.next()) {
				if (monitor.isCanceled()) {
					done(canStat);
					return canStat;
				}

				IDfTypedObject tObj = coll.getTypedObject();
				final DocbaseItem di = new DocbaseItem();
				di.setData(tObj);
				di.setType(DocbaseItem.TYPED_OBJ_TYPE);

				if (cntrBuf > bufSize) {
					cntrBuf = 0;
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							for (int i = 0; i < bufSize; i++) {
								DocbaseItem tdi = getNextObject();
								if (tdi != null) {
									resultsViewer.add(tdi);
									System.out.println("Added: "
											+ tdi.getLabelText());
								} else
									break;
							}
						}
					});
				}// if
				cntrBuf++;
				addObject(di);
				// System.out.println("Added obj: " + di.getLabelText());
			}// while

			System.out.println("cntrBuf: " + cntrBuf);
			if (cntrBuf > 0) {
				final int cb = cntrBuf;
				Runnable finThr = new Runnable() {
					public void run() {
						DocbaseItem di = null;
						while ((di = getNextObject()) != null) {
							// System.out.println("di: " + di);
							resultsViewer.add(di);
							System.out.println("Just added: "
									+ di.getLabelText());
						}
					}
				};
				Display.getDefault().asyncExec(finThr);

			}//
			IStatus finStat = new Status(IStatus.OK, DevprogPlugin.PLUGIN_ID,
					IStatus.OK, "Finished", null);
			monitor.done();

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {

					IViewPart vp = (IViewPart) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.getActivePart();
					IStatusLineManager smgr = vp.getViewSite().getActionBars()
							.getStatusLineManager();
					smgr.getProgressMonitor().done();
					smgr.setMessage("Finished Querying");
				}

			});

			done(finStat);
			return finStat;
		} catch (Exception ex) {
			ex.printStackTrace();
			IStatus errStat = new Status(IStatus.ERROR,
					DevprogPlugin.PLUGIN_ID, IStatus.OK, "Error", ex);
			done(errStat);
			return errStat;
		} finally {
			if (coll != null) {
				try {
					coll.close();
				} catch (Exception ex) {
				}
				PluginState.releaseSession(sess);
			}
		}
	}

	void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	void setResultsViewer(TableViewer resultsViewer) {
		this.resultsViewer = resultsViewer;
	}

	DocbaseItem getNextObject() {
		synchronized (syncObj) {
			return (DocbaseItem) resBuffer.removeFirst();
		}
	}

	void addObject(DocbaseItem di) {
		synchronized (syncObj) {
			resBuffer.add(di);
		}
	}

	void setDocbase(String docbase) {
		this.docbase = docbase;
	}

	void setSelectedNode(DocbaseItem selectedNode) {
		this.selectedNode = selectedNode;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

}
