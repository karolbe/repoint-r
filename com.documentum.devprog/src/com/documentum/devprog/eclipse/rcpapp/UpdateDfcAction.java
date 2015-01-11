/*
 * Created on Sep 27, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.rcpapp;

import com.documentum.devprog.eclipse.common.DFCConfigurationHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class UpdateDfcAction extends Action implements IWorkbenchAction {

	/**
	 * @param text
	 */
	public UpdateDfcAction(String text) {
		super(text);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {
	}

	public void run() {
		int status = DFCConfigurationHelper.configureDfcJars(true);
		if (status == -1) {
			MessageDialog.openInformation(
					Display.getDefault().getActiveShell(), "Restart",
					"Please restart Repoint for the changes to take effect.");
		}
	}

}
