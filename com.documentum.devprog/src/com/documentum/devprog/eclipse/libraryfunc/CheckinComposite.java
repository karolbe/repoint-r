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

/*
 * Created on Mar 28, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfVersionPolicy;
import com.documentum.fc.common.IDfId;
import com.documentum.operations.IDfCheckinOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.progress.IProgressConstants;

public class CheckinComposite extends Composite {

	private Label lblObjectName = null;

	private Label label = null;

	private Label lblVersion = null;

	private Label label1 = null;

	private Button radSameVer = null;

	private Label label2 = null;

	private Button radNextMinor = null;

	private Label label3 = null;

	private Label label4 = null;

	private Label label6 = null;

	private Label label7 = null;

	private Button radNextMajor = null;

	private Label label9 = null;

	private Label lblNextMinor = null;

	private Label lblNextMajor = null;

	private Label lblVerLabels = null;

	private Label label8 = null;

	private Label label10 = null;

	private Text txtVersionLabels = null;

	private Label lblCheckinFile = null;

	private Label label12 = null;

	private Text txtFile = null;

	private Button butCheckinFile = null;

	private IDfId objectId = null;

	private TableViewer repoTree = null;

	private DocbaseItem parent = null;

	private Label label5 = null;

	private Button radBranchVersion = null;

	private Label label13 = null;

	private Label lblBranchVersion = null;

	private Label lblLogEntry = null;

	private Label label14 = null;

	private Label label15 = null;

	private Text txtLogEntry = null;

	public CheckinComposite(Composite parent, int style) {
		super(parent, style);
		initialize();

	}

	private void initialize() {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		lblObjectName = new Label(this, SWT.NONE);
		lblObjectName.setText("Label");
		lblObjectName.setLayoutData(gridData);
		label1 = new Label(this, SWT.NONE);
		label2 = new Label(this, SWT.NONE);
		label = new Label(this, SWT.NONE);
		lblVersion = new Label(this, SWT.NONE);
		lblVersion.setText("Same Version");
		radSameVer = new Button(this, SWT.RADIO);
		radSameVer.setText("");
		label3 = new Label(this, SWT.NONE);
		label4 = new Label(this, SWT.NONE);
		lblNextMinor = new Label(this, SWT.NONE);
		lblNextMinor.setText("Next Minor");
		radNextMinor = new Button(this, SWT.RADIO);
		radNextMinor.setText("");
		label6 = new Label(this, SWT.NONE);
		label7 = new Label(this, SWT.NONE);
		lblNextMajor = new Label(this, SWT.NONE);
		lblNextMajor.setText("Next Major");
		radNextMajor = new Button(this, SWT.RADIO);
		radNextMajor.setText("");
		label9 = new Label(this, SWT.NONE);
		label5 = new Label(this, SWT.NONE);
		lblBranchVersion = new Label(this, SWT.NONE);
		lblBranchVersion.setText("Branch Version");
		radBranchVersion = new Button(this, SWT.RADIO);
		label13 = new Label(this, SWT.NONE);
		lblVerLabels = new Label(this, SWT.NONE);
		lblVerLabels.setText("Version Labels");
		txtVersionLabels = new Text(this, SWT.BORDER);
		label8 = new Label(this, SWT.NONE);
		label10 = new Label(this, SWT.NONE);
		lblLogEntry = new Label(this, SWT.NONE);
		lblLogEntry.setText("Log Entry(Comments)");
		txtLogEntry = new Text(this, SWT.BORDER);
		label14 = new Label(this, SWT.NONE);
		label15 = new Label(this, SWT.NONE);
		lblCheckinFile = new Label(this, SWT.NONE);
		lblCheckinFile.setText("Checkin From File");
		txtFile = new Text(this, SWT.BORDER);
		butCheckinFile = new Button(this, SWT.NONE);
		butCheckinFile.setText("Browse");
		butCheckinFile
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                        FileDialog fd = new FileDialog(CheckinComposite.this
                                .getShell());
                        String file = fd.open();
                        if (file != null) {
                            try {
                                txtFile.setText(file);
                            } catch (Exception dfei) {
                            }
                        }
                    }
                });
		label12 = new Label(this, SWT.NONE);
		this.setLayout(gridLayout);
		setSize(new org.eclipse.swt.graphics.Point(395, 300));
	}

	public void executeOperation() {
		CheckinJob cj = new CheckinJob();

		cj.setObjectId(this.getObjectId());

		if (radSameVer.isEnabled() && radSameVer.getSelection()) {
			cj.setVersionPolicy(IDfCheckinOperation.SAME_VERSION);
		} else if (radNextMinor.isEnabled() && radNextMinor.getSelection()) {
			cj.setVersionPolicy(IDfCheckinOperation.NEXT_MINOR);
		} else if (radNextMajor.isEnabled() && radNextMajor.getSelection()) {
			cj.setVersionPolicy(IDfCheckinOperation.NEXT_MAJOR);
		} else if (radBranchVersion.isEnabled()
				&& radBranchVersion.getSelection()) {
			cj.setVersionPolicy(IDfCheckinOperation.BRANCH_VERSION);
		}

		if (txtVersionLabels.getText() != null
				&& txtVersionLabels.getText().length() > 0) {
			cj.setVersionLabels(txtVersionLabels.getText());
		}

		String cf = txtFile.getText();
		if (cf != null && cf.length() > 0) {
			cj.setCheckinFile(cf);
		}

		String le = txtLogEntry.getText();
		if (le != null && le.length() > 0) {
			cj.setLogEntry(le);
		}
		cj.setParent(this.getObjectParent());
		cj.setRepoTree(this.getRepoTree());

		cj.setUser(true);
		cj.setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
		cj.schedule();

	}

	private void initializeObject() {
		IDfSession sess = null;
		try {
			sess = PluginState.getSessionById(getObjectId());
			IDfSysObject sObj = (IDfSysObject) sess.getObject(getObjectId());

			lblObjectName.setText(sObj.getObjectName());

			IDfVersionPolicy verPol = sObj.getVersionPolicy();

			int defVer = verPol.getDefaultCheckinVersion();
			if (verPol.canVersion(IDfVersionPolicy.DF_SAME_VERSION)) {
				radSameVer.setEnabled(true);
				radSameVer.setText(verPol.getSameLabel());
				if (defVer == IDfCheckinOperation.SAME_VERSION) {
					radSameVer.setSelection(true);
				}
			} else {
				radSameVer.setEnabled(false);
			}

			if (verPol.canVersion(IDfVersionPolicy.DF_NEXT_MINOR)) {
				radNextMinor.setEnabled(true);
				radNextMinor.setText(verPol.getNextMinorLabel());
				if (defVer == IDfCheckinOperation.NEXT_MINOR) {
					radNextMinor.setSelection(true);
				}
			} else {
				radNextMinor.setEnabled(false);
			}

			if (verPol.canVersion(IDfVersionPolicy.DF_NEXT_MAJOR)) {
				radNextMajor.setEnabled(true);
				radNextMajor.setText(verPol.getNextMajorLabel());
				if (defVer == IDfCheckinOperation.NEXT_MAJOR) {
					radNextMajor.setSelection(true);
				}
			} else {
				radNextMajor.setEnabled(false);
			}

			if (verPol.canVersion(IDfVersionPolicy.DF_BRANCH_VERSION)) {
				radBranchVersion.setEnabled(true);
				radBranchVersion.setText(verPol.getBranchLabel());
				if (defVer == IDfCheckinOperation.BRANCH_VERSION) {
					radBranchVersion.setSelection(true);
				}
			} else {
				radBranchVersion.setEnabled(false);
			}

			if (verPol.canCheckinFromFile()) {
				this.butCheckinFile.setEnabled(true);
				this.txtFile.setEnabled(true);
			} else {
				this.butCheckinFile.setEnabled(false);
				this.txtFile.setEnabled(false);
			}

		} catch (Exception ex) {
			MessageDialog.openError(super.getShell(), "Object Initialization",
					"Error initializing repository object: " + ex.getMessage());
		}

	}

	public IDfId getObjectId() {
		return objectId;
	}

	public void setObjectId(IDfId objectId) {
		this.objectId = objectId;
		initializeObject();
	}

	public TableViewer getRepoTree() {
		return repoTree;
	}

	public void setRepoTree(TableViewer repoTree) {
		this.repoTree = repoTree;
	}

	public DocbaseItem getObjectParent() {
		return parent;
	}

	public void setObjectParent(DocbaseItem parent) {
		this.parent = parent;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
