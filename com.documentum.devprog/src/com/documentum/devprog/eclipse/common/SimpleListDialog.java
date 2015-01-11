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
 * Created on Jun 27, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.common;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Shows a list of data in a list within a dialog.
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class SimpleListDialog extends Dialog {

	private int style = SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL;

	private List lst = null;

	private String[] data = {};

	private String[] selectedData = {};

	private String title = "";

	private int width = 500;
	private int height = 350;

	public SimpleListDialog(Shell sh, String[] data, String title, int style) {
		super(sh);
		this.style = style;
		this.data = data;
		this.title = title;
	}

	public SimpleListDialog(Shell sh, String[] data, String title) {
		super(sh);
		this.data = data;
		this.title = title;
	}

	protected Control createDialogArea(Composite parent) {
		Composite rootContr = new Composite(parent, SWT.NONE);
		GridData gd = new GridData();

		gd.widthHint = width;
		gd.heightHint = height;
		rootContr.setLayoutData(gd);

		rootContr.setLayout(new FillLayout());
		lst = new List(rootContr, style);

		for (int i = 0; i < data.length; i++) {
			lst.add(data[i]);
		}

		lst.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				selectedData = lst.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}

		});

		lst.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				selectedData = lst.getSelection();
				okPressed();
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
			}
		});

		return super.createDialogArea(parent);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (title != null) {
			newShell.setText(title);
		}

	}

	protected void okPressed() {
		if (selectedData != null && selectedData.length > 0) {
			super.okPressed();
		}
	}

	public String[] getSelection() {
		if (selectedData == null) {
			selectedData = new String[] {};
		}
		return selectedData;
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

}
