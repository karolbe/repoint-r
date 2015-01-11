/*
 * Created on Aug 16, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.devprog.eclipse.tree.RepoTreeExtension;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class CancelCheckoutAction extends RepoTreeExtension {

	public boolean showAction() {
		DocbaseItem dtd = super.getDocbaseItem();

		try {
			if (dtd != null && dtd.isDocumentType()) {
				IDfTypedObject to = (IDfTypedObject) dtd.getData();
				String lockOwner = to.getString("r_lock_owner");
				return (lockOwner != null && lockOwner.length() > 0);
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error getting lock owner", null, ex);
		}
		return false;
	}

	public void run() {
		try {
			DocbaseItem dtd = super.getDocbaseItem();
			if (dtd != null) {
				boolean cancel = MessageDialog
						.openConfirm(
								getShell(),
								"Cancel Checkout",
								"Are you sure you want to cancel checkout because all changes made to file will be lost once checkout is cancelled?");
				if (cancel) {
					IDfTypedObject to = (IDfTypedObject) dtd.getData();
					IDfId oi = to.getId("r_object_id");
					CancelCheckoutJob ccjob = new CancelCheckoutJob();
					ccjob.setObjectId(oi);
					ccjob.setObjectListViewer(super.getObjectListViewer());
					ccjob.setDocbaseItem(dtd);
					ccjob.setProperty(IProgressConstants.KEEP_PROPERTY,
							Boolean.TRUE);
					ccjob.setUser(true);
					ccjob.schedule();
				}
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error running checin operation", null, ex);
			MessageDialog.openError(super.getShell(), "Checkin Error",
					ex.getMessage());
		}
	}

}
