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
 * Created on Jul 16, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.common;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfDocbaseMap;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Generic docbase login dialog.
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class DocbaseLoginDialog extends org.eclipse.jface.dialogs.Dialog {
	Text txtName = null;

	Text txtPasswd = null;

	Text txtDomain = null;

	Combo docbaseList = null;

	String docbaseName = null;

	Label lblStatus = null;

	private int width = 400;
	private int height = 300;

	public DocbaseLoginDialog(Shell parent) {
		super(parent);

	}

	/**
	 * Sets the name of the docbase for which login is required.
	 * 
	 * @param docbaseInfo
	 */
	public void setDocbaseName(String docbaseName) {
		if ((docbaseName != null) && (docbaseName.length() > 0)) {
			this.docbaseName = docbaseName;
		}
	}

	/**
	 * Gets the docbase name for which login is being done or was done.
	 * 
	 * @return
	 */
	public String getDocbaseName() {
		return docbaseName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets
	 * .Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = this.width;
		gd.heightHint = this.height;
		composite.setLayoutData(gd);

		composite.setLayout(new FormLayout());

		Label lblName = new Label(composite, SWT.SINGLE);
		lblName.setText(Messages.getString("DocbaseLoginDialog.0")); //$NON-NLS-1$
		lblName.setLayoutData(PluginHelper.getFormData(0, 10, 0, 30));

		txtName = new Text(composite, SWT.SINGLE);
		txtName.setLayoutData(PluginHelper.getFormData(0, 10, 30, 100));

		Label lblPassword = new Label(composite, SWT.SINGLE);
		lblPassword.setText(Messages.getString("DocbaseLoginDialog.1")); //$NON-NLS-1$        
		lblPassword.setLayoutData(PluginHelper.getFormData(10, 20, 0, 30));

		txtPasswd = new Text(composite, SWT.SINGLE | SWT.PASSWORD);
		txtPasswd.setLayoutData(PluginHelper.getFormData(10, 20, 30, 100));

		Label lblDomain = new Label(composite, SWT.SINGLE);
		lblDomain.setText(Messages.getString("DocbaseLoginDialog.2")); //$NON-NLS-1$       
		lblDomain.setLayoutData(PluginHelper.getFormData(20, 30, 0, 30));

		txtDomain = new Text(composite, SWT.SINGLE);
		txtDomain.setLayoutData(PluginHelper.getFormData(20, 30, 30, 100));

		docbaseList = getDocbasePickList(composite);
		docbaseList.setLayoutData(PluginHelper.getFormData(30, 45, 0, 50));

		return composite;
	}// createDialogArea

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		String username = txtName.getText();
		String password = txtPasswd.getText();
		String domain = txtDomain.getText();

		int selIndx = docbaseList.getSelectionIndex();
		String docbase = docbaseList.getItem(selIndx);
		boolean authenticated = PluginState.addIdentity(username, password,
				domain, docbase, true);
		if (authenticated == true) {
			super.okPressed();
		} else {
			MessageDialog
					.openError(
							getShell(),
							Messages.getString("DocbaseLoginDialog.3"), Messages.getString("DocbaseLoginDialog.4") + docbase + Messages.getString("DocbaseLoginDialog.5")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/**
	 * Creates and returns a drop down list control that will be populated with
	 * the list of docbases visible.
	 * 
	 * @param parent
	 *            The parent composite within which the dropdownlist is created
	 * @return Combo (style=DROP_DOWN) containing list of docbases. If an error
	 *         occurs the Combo box is empty.
	 */
	protected Combo getDocbasePickList(Composite parent) {
		Combo docbaseList = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

		// if docbase name already specified just show that.
		String docName = getDocbaseName();
		if ((docName != null) && (docName.length() > 0)) {
			docbaseList.add(docName);
			docbaseList.select(0);
			return docbaseList;
		}

		try {
			IDfClient localClient = DfClient.getLocalClient();
			IDfDocbaseMap docMap = localClient.getDocbaseMap();
			int numDocbases = docMap.getDocbaseCount();
			ArrayList lstDocbases = new ArrayList(numDocbases);
			for (int i = 0; i < numDocbases; i++) {
				String docbaseName = docMap.getDocbaseName(i);
				// docbaseList.add(docbaseName);
				lstDocbases.add(docbaseName);
			}

			Collections.sort(lstDocbases);

			for (int i = 0; i < lstDocbases.size(); i++) {
				String name = (String) lstDocbases.get(i);
				docbaseList.add(name);
			}
			docbaseList.select(0);

		} catch (Exception dfe) {
			DfLogger.error(this, "Error getting repoList", null, dfe);
			docbaseList.add("Error getting repository list: "
					+ dfe.getMessage());
			MessageDialog.openError(super.getShell(), "Repo List Error",
					"Error getting Repo List: " + dfe.getMessage());
		}
		return docbaseList;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("DocbaseLoginDialog.6")); //$NON-NLS-1$
	}
}