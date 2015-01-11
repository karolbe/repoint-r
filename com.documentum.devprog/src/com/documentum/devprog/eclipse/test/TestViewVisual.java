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
 * Created on Dec 8, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class TestViewVisual extends ViewPart {

	public static final String ID = "com.documentum.devprog.eclipse.test.TestViewVisual"; // TODO
																							// Needs
																							// to
																							// be
																							// whatever
																							// is
																							// mentioned
																							// in
																							// plugin.xml
	private Composite top = null;
	private Composite top1 = null;
	private Browser browser = null;
	private Label label5 = null;
	private Label label6 = null;
	private Label label7 = null;
	private Label label = null;
	private Label label1 = null;
	private Label label2 = null;
	private Label label3 = null;
	private Label label4 = null;
	private Label label8 = null;
	private Button button = null;

	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 4;
		top1 = new Composite(parent, SWT.NONE);
		top1.setLayout(gridLayout);
		label3 = new Label(top1, SWT.NONE);
		label4 = new Label(top1, SWT.NONE);
		label8 = new Label(top1, SWT.NONE);
		button = new Button(top1, SWT.NONE);
		createBrowser();
		label = new Label(top1, SWT.NONE);
		label1 = new Label(top1, SWT.NONE);
		label2 = new Label(top1, SWT.NONE);
		label5 = new Label(top1, SWT.NONE);
		label6 = new Label(top1, SWT.NONE);
		label7 = new Label(top1, SWT.NONE);
	}

	public void setFocus() {
	}

	/**
	 * This method initializes browser
	 * 
	 */
	private void createBrowser() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 4;
		browser = new Browser(top1, SWT.NONE);
		browser.setLayoutData(gridData);
	}

}
