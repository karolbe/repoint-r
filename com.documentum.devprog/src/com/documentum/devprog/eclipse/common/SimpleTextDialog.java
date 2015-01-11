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
 * Created on Sep 22, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.common;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Displays text data in a text box within a dialog.
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class SimpleTextDialog extends Dialog {

	private String data = null;
	private String title = null;

	private int width = 500;
	private int height = 400;
	private Text txtBox;

	public SimpleTextDialog(Shell sh, String title, String data) {
		super(sh);
		this.data = data;
		this.title = title;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridData gd = PluginHelper.getGridData(1);
		gd.widthHint = width;
		gd.heightHint = height;
		comp.setLayoutData(gd);

		comp.setLayout(new GridLayout(1, true));
		txtBox = new Text(comp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		txtBox.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				data = txtBox.getText();

			}
		});
		txtBox.setLayoutData(PluginHelper.getGridData(1));
		if (data != null) {
			txtBox.setText(data);
		}
		return comp;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (title != null) {
			newShell.setText(title);
		}
	}

	public String getTextData() {
		return data;
	}

}
