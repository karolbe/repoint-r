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
 * Created on Aug 25, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfList;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfSession;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.operations.IDfFormatRecognizer;

public class ImportDocumentDialog extends Dialog {

	private String targetFolderId = null;

	private String fileLocation = null;

	private String objectName = null;

	private String format = null;

	private String objectType = null;

	private Combo typeList;

	private Combo fmtList;

	private Text objNameText;

	private Text flLoc;

	public ImportDocumentDialog(Shell sh, String targetFldr, String fileLocation) {
		super(sh);
		targetFolderId = targetFldr;
		this.fileLocation = fileLocation;
	}

	protected Control createDialogArea(Composite parent) {
		Composite impComp = new Composite(parent, SWT.NONE);
		impComp.setLayout(new GridLayout(4, true));
		final IDfSession sess = PluginState.getSessionById(targetFolderId);
		try {

			flLoc = new Text(impComp, SWT.SINGLE | SWT.BORDER);
			flLoc.setLayoutData(PluginHelper.getGridData(3));
			flLoc.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent me) {
					fileLocation = flLoc.getText();
				}
			});
			if (fileLocation != null && fileLocation.length() > 0) {
				flLoc.setText(fileLocation);
			}

			Button butSelFile = new Button(impComp, SWT.PUSH);
			butSelFile.setText("Choose File");
			butSelFile.setLayoutData(PluginHelper.getGridData(1));
			butSelFile.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent se) {
					FileDialog fd = new FileDialog(ImportDocumentDialog.this
							.getShell());
					String file = fd.open();
					if (file != null) {
						try {
							flLoc.setText(file);
							File fl = new File(file);
							objNameText.setText(fl.getName());

							/*
							 * IDfClientX cx = new DfClientX();
							 * IDfFormatRecognizer fr = cx.getFormatRecognizer(
							 * sess, fl.getAbsolutePath(), ""); IDfList fmts =
							 * fr.getSuggestedFileFormats(); if (fmts.getCount()
							 * > 0) { for(int i=0;i<fmts.getCount();i++) {
							 * System.out.println(fmts.getString(i)); } String
							 * defFmt = fmts.getString(0);
							 * 
							 * int indx = fmtList.indexOf(defFmt); if (indx !=
							 * -1) { fmtList.select(indx); } }
							 */

						} catch (Exception dfei) {
						}
					}
				}

				public void widgetDefaultSelected(SelectionEvent se) {
					widgetSelected(se);
				}

			});

			Label lblObjName = new Label(impComp, SWT.SINGLE);
			lblObjName.setText("Object Name");
			lblObjName.setLayoutData(PluginHelper.getGridData(1));

			objNameText = new Text(impComp, SWT.SINGLE | SWT.BORDER);
			objNameText.setLayoutData(PluginHelper.getGridData(3));
			objNameText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent me) {
					objectName = objNameText.getText();
				}

			});

			Label lblType = new Label(impComp, SWT.SINGLE);
			lblType.setText("Object Type");
			lblType.setLayoutData(PluginHelper.getGridData(1));

			typeList = new Combo(impComp, SWT.DROP_DOWN | SWT.SINGLE);
			typeList.setLayoutData(PluginHelper.getGridData(3));
			String[] types = PluginHelper.getAllSubtypes(sess, "dm_document");
			int dmdocIndx = 0;
			for (int i = 0; i < types.length; i++) {
				typeList.add(types[i]);
				if (types[i].equals("dm_document"))
					dmdocIndx = i;
			}
			typeList.select(dmdocIndx);
			typeList.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent se) {
					int indx = typeList.getSelectionIndex();
					objectType = typeList.getItem(indx);

				}

				public void widgetDefaultSelected(SelectionEvent se) {
					widgetSelected(se);
				}

			});

			/*
			 * Label lblFormat = new Label(impComp, SWT.SINGLE);
			 * lblFormat.setText("Format");
			 * lblFormat.setLayoutData(PluginHelper.getGridData(2));
			 * 
			 * fmtList = new Combo(impComp, SWT.DROP_DOWN);
			 * fmtList.setLayoutData(PluginHelper.getGridData(3)); String fmts[]
			 * = PluginHelper.getAllFormats(sess); for (int i = 0; i <
			 * fmts.length; i++) { fmtList.add(fmts[i]); }
			 * fmtList.addSelectionListener(new SelectionListener() { public
			 * void widgetSelected(SelectionEvent se) { int indx =
			 * fmtList.getSelectionIndex(); format = fmtList.getItem(indx);
			 * 
			 * }
			 * 
			 * public void widgetDefaultSelected(SelectionEvent se) {
			 * widgetSelected(se); } });
			 */
		} catch (DfException dfe) {
			DfLogger.error(this, "Error in import dialog", null, dfe);
		} finally {
			PluginState.releaseSession(sess);
		}
		return impComp;

	}

	public String getFormat() {
		return format;
	}

	public String getObjectName() {
		return objectName;
	}

	public String getObjectType() {
		return objectType;
	}

	public String getFile() {
		return fileLocation;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Import Document");
	}

}
