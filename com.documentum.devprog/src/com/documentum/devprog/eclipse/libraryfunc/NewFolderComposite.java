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
 * Created on Apr 6, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

/**
 * 
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class NewFolderComposite extends Composite {

	private Label lblObjName = null;
	private Text txtObjName = null;
	private Label lblType = null;
	private Combo cmbTypes = null;

	// //Non-VE variables
	private String objectName = null;
	private String objectType = null;
	private IDfId parentId = null;
	private IDfId newFolderId = null;
	private String parentType = "dm_folder";

	public NewFolderComposite(Composite parent, int style, String parentType) {
		super(parent, style);
		this.parentType = parentType;
		initialize();
	}

	private void initialize() {
		lblObjName = new Label(this, SWT.NONE);
		lblObjName.setBounds(new org.eclipse.swt.graphics.Rectangle(16, 17,
				105, 26));
		lblObjName.setText("Name");
		txtObjName = new Text(this, SWT.BORDER);
		txtObjName.setBounds(new org.eclipse.swt.graphics.Rectangle(134, 19,
				304, 24));
		txtObjName
				.setToolTipText("Enter the value for object_name property of the folder/cabinet");
		txtObjName
				.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
						objectName = txtObjName.getText();
					}
				});
		lblType = new Label(this, SWT.NONE);
		lblType.setBounds(new org.eclipse.swt.graphics.Rectangle(13, 60, 109,
				22));
		lblType.setText("Object Type");
		this.setLayout(null);
		createCmbTypes();
		setSize(new org.eclipse.swt.graphics.Point(445, 146));
	}

	/**
	 * This method initializes cmbTypes
	 * 
	 */
	private void createCmbTypes() {
		cmbTypes = new Combo(this, SWT.NONE);
		cmbTypes.setBounds(new org.eclipse.swt.graphics.Rectangle(134, 62, 302,
				21));
		cmbTypes.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				objectType = cmbTypes.getItem(cmbTypes.getSelectionIndex());
			}

			public void widgetDefaultSelected(
					org.eclipse.swt.events.SelectionEvent e) {
			}
		});

		populateTypes();

	}

	private void populateTypes() {
		String[] ts = this.getTypes();
		for (int i = 0; i < ts.length; i++) {
			cmbTypes.add(ts[i]);
		}
		int indx = cmbTypes.indexOf(this.getParentType());
		if (indx >= 0) {
			cmbTypes.select(indx);
		}
		objectType = this.getParentType();

	}

	private String[] getTypes() {
		IDfSession sess = null;
		try {
			sess = PluginState.getSession();

			if (this.getParentType().equals("dm_folder")) {
				return PluginHelper.getAllFolderTypes(sess, "dm_folder", true);
			} else {
				return PluginHelper.getAllSubtypes(sess, "dm_cabinet");

			}

		} catch (Exception ex) {
			DfLogger.error(this, "Error getting subtypes", null, ex);
			return new String[] { "Error getting types: " + ex.getMessage() };
		} finally {
			PluginState.releaseSession(sess);
		}
	}

	public String getObjectName() {
		return objectName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setParentId(IDfId parentId) {
		this.parentId = parentId;
	}

	public IDfId getParentId() {
		return parentId;
	}

	public void createFolder() {
		IDfSession sess = null;
		try {
			sess = PluginState.getSession();
			IDfFolder nf = (IDfFolder) sess.newObject(getObjectType());
			nf.setObjectName(getObjectName());
			if (this.getParentType().equals("dm_folder")) {
				nf.link(parentId.getId());
			}

			nf.save();
			newFolderId = nf.getObjectId();
		} catch (Exception ex) {
			DfLogger.error(this, "Error creating folder", null, ex);
			MessageDialog.openError(super.getShell(), "Folder Create Error",
					ex.getMessage());
		} finally {
			PluginState.releaseSession(sess);
		}
	}

	public IDfId getNewFolderId() {
		return newFolderId;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
