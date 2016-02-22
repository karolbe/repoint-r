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
 * Created on Jun 27, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.common;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Dialog that displays docbases visible to the current docbroker.
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DocbaseListDialog extends Dialog {
	ListViewer repoListViewer = null;

	String selectedRepo = null;
	private int width = 400;
	private int height = 300;

	public DocbaseListDialog(Shell sh) {
		super(sh);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = this.width;
		gd.heightHint = this.height;
		composite.setLayoutData(gd);

		composite.setLayout(new FormLayout());

		repoListViewer = new ListViewer(composite, SWT.V_SCROLL | SWT.SINGLE);
		repoListViewer.setContentProvider(new DocbaseListContentProvider());
		repoListViewer.setLabelProvider(new DocbaseListLabelProvider());
		repoListViewer.setSorter(new ViewerSorter());
		repoListViewer.getList().setLayoutData(PluginHelper.getFormData(0, 40, 0, 100));

		Button addExternalDocbase = new Button(composite, SWT.NONE);
		addExternalDocbase.setText("Add External Docbase");
		addExternalDocbase.setLayoutData(PluginHelper.getFormData(80, 95, 50, 100));
		addExternalDocbase.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				showExternalDocbaseDialog();
			}

			public void widgetDefaultSelected(SelectionEvent selectionEvent) {

			}
		});

		Font oldFont = repoListViewer.getList().getFont();
		FontData fdata[] = oldFont.getFontData();
		for (int i = 0; i < fdata.length; i++) {
			fdata[i].setHeight(10);
			fdata[i].setName("Arial");
		}
		Font newFont = new Font(super.getShell().getDisplay(), fdata);
		repoListViewer.getList().setFont(newFont);

		repoListViewer.setInput(""); //$NON-NLS-1$
		repoListViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent sce) {
						IStructuredSelection sel = (IStructuredSelection) sce
								.getSelection();
						String repoName = (String) sel.getFirstElement();
						if (repoName != null) {
							selectedRepo = repoName;
						}
					}
				});
		repoListViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent dce) {
				IStructuredSelection sel = (IStructuredSelection) dce
						.getSelection();
				String repoName = (String) sel.getFirstElement();
				if (repoName != null) {
					selectedRepo = repoName;
				}
				okPressed();
			}

		});
		return parent;
	}

	private void showExternalDocbaseDialog() {
		ExternalDocbaseDialog docbrokerDlg = new ExternalDocbaseDialog(
				getShell());
		docbrokerDlg.open();
		repoListViewer.add(docbrokerDlg.getDocbase());
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(Messages.getString("DocbaseListDialog.1")); //$NON-NLS-1$
	}

	protected void okPressed() {
		if (selectedRepo != null && selectedRepo.length() != 0) {
			super.okPressed();
		}
	}

	public String getSelectedRepo() {
		return selectedRepo;
	}

	class DocbaseListContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {

			try {
				return PluginHelper.getSortedDocbaseList().toArray();
			} catch (Throwable th) {
				MessageDialog.openError(null, "Repo List Error",
						th.getMessage());
			}
			return new Object[] {};

		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	class DocbaseListLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			if (element instanceof String) {
				return (String) element;
			}
			return ""; //$NON-NLS-1$
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}
}
