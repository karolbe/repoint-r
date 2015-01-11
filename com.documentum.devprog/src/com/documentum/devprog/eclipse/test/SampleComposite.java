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
 * Created on Dec 12, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

public class SampleComposite extends Composite {

	// Test comment to test checkout in SVN
	private Text textFilename = null;

	private Button buttonChooseFile = null;

	private Group grpFileName = null;

	private ScrolledComposite scrolledComposite = null;

	private ToolBar toolBar = null;

	public SampleComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		textFilename = new Text(this, SWT.BORDER);
		buttonChooseFile = new Button(this, SWT.NONE);
		buttonChooseFile.setText("Browse");
		createGrpFileName();
		this.setLayout(gridLayout);
		buttonChooseFile
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						FileDialog fd = new FileDialog(buttonChooseFile
								.getShell(), SWT.OPEN);
						String flName = fd.open();
						textFilename.setText(flName);
					}
				});

		setSize(new org.eclipse.swt.graphics.Point(446, 367));
	}

	/**
	 * This method initializes grpFileName
	 * 
	 */
	private void createGrpFileName() {
		grpFileName = new Group(this, SWT.NONE);
		createScrolledComposite();
	}

	/**
	 * This method initializes scrolledComposite
	 * 
	 */
	private void createScrolledComposite() {
		scrolledComposite = new ScrolledComposite(grpFileName, SWT.NONE);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(false);
		createToolBar();
		scrolledComposite.setContent(toolBar);
		scrolledComposite.setBounds(new org.eclipse.swt.graphics.Rectangle(15,
				18, 87, 64));
	}

	/**
	 * This method initializes toolBar
	 * 
	 */
	private void createToolBar() {
		toolBar = new ToolBar(scrolledComposite, SWT.NONE);
	}
} // @jve:decl-index=0:visual-constraint="13,5"
