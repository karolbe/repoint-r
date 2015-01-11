/*
 * Created on Aug 16, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;

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

import com.documentum.operations.IDfCancelCheckoutOperation;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class CancelCheckoutJob extends Job {
	private IDfId objectId = null;

	private TableViewer listViewer = null;

	private DocbaseItem item = null;

	public CancelCheckoutJob() {
		super("Cancel Checkout");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		IDfSession session = null;
		try {
			session = PluginState.getSessionById(getObjectId());

			IDfCancelCheckoutOperation cancelCheckout = PluginState
					.getClientX().getCancelCheckoutOperation();
			IDfSysObject sObj = (IDfSysObject) session.getObject(getObjectId());
			cancelCheckout.add(sObj);

			if (cancelCheckout.execute()) {
				item.refreshTypedObject();
				Display disp = Display.getCurrent();
				if (disp == null)
					disp = Display.getDefault();
				disp.syncExec(new Runnable() {

					public void run() {
						listViewer.refresh(item);
					}
				});

				return new Status(IStatus.OK, LibraryFunctionsPlugin.PLUGIN_ID,
						IStatus.OK, "Cancel Checkout Successful", null);
			} else {
				String errs = PluginHelper.prepareOperationError(cancelCheckout
						.getErrors());
				Exception cex = new Exception(errs);
				return new Status(IStatus.ERROR,
						LibraryFunctionsPlugin.PLUGIN_ID, IStatus.ERROR,
						"Cancel Checkout Failed", cex);
			}
		} catch (DfException dfe) {
			DfLogger.error(this, "Error executing cancel checkout operation",
					null, dfe);
			return new Status(IStatus.ERROR, LibraryFunctionsPlugin.PLUGIN_ID,
					IStatus.ERROR, "Cancel Checkout Failed", dfe);
		} finally {
			PluginState.releaseSession(session);
		}

	}

	public IDfId getObjectId() {
		return objectId;
	}

	public void setObjectId(IDfId objectId) {
		this.objectId = objectId;
	}

	public DocbaseItem getDocbaseItem() {
		return item;
	}

	public void setDocbaseItem(DocbaseItem parent) {
		this.item = parent;
	}

	public TableViewer getObjectListViewer() {
		return listViewer;
	}

	public void setObjectListViewer(TableViewer repoTree) {
		this.listViewer = repoTree;
	}

}
