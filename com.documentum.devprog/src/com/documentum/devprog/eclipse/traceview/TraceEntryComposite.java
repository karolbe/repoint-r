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
 * Created on Sep 6, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.traceview;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class TraceEntryComposite extends Composite {

	private Label lblSource = null;

	private Label lblMethod = null;

	private Label lblArguments = null;

	private Label lblReturn = null;

	private DFCTraceEntry traceEntry = null;

	private Text txtClassValue = null;

	private Text txtMethod = null;

	private Text txtArguments = null;

	private Text txtReturnValue = null;

	public TraceEntryComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		lblSource = new Label(this, SWT.NONE);
		lblSource.setBounds(new Rectangle(15, 30, 76, 17));
		lblSource.setText("Source Class");
		lblMethod = new Label(this, SWT.NONE);
		lblMethod.setBounds(new Rectangle(15, 107, 106, 20));
		lblMethod.setText("Method Name");
		lblArguments = new Label(this, SWT.NONE);
		lblArguments.setBounds(new Rectangle(14, 180, 181, 21));
		lblArguments.setText("Method Arguments");
		lblReturn = new Label(this, SWT.NONE);
		lblReturn.setBounds(new Rectangle(14, 256, 168, 18));
		lblReturn.setText("Return Value");
		txtClassValue = new Text(this, SWT.READ_ONLY);
		txtClassValue.setBounds(new Rectangle(15, 62, 286, 23));
		txtClassValue.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		txtMethod = new Text(this, SWT.READ_ONLY);
		txtMethod.setBounds(new Rectangle(14, 135, 286, 27));
		txtMethod.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		txtArguments = new Text(this, SWT.READ_ONLY);
		txtArguments.setBounds(new Rectangle(15, 210, 288, 26));
		txtArguments.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		txtReturnValue = new Text(this, SWT.READ_ONLY);
		txtReturnValue.setBounds(new Rectangle(14, 286, 288, 27));
		txtReturnValue.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		this.setSize(new Point(345, 413));
		setLayout(null);
	}

	public void setTraceEntry(DFCTraceEntry traceEntry) {
		this.traceEntry = traceEntry;
		if (txtClassValue != null) {
			txtClassValue.setText("");
			String srcCls = traceEntry.getSourceClass();
			if (srcCls != null)
				txtClassValue.setText(srcCls);
		}
		if (txtMethod != null) {
			txtMethod.setText("");
			String srcMeth = traceEntry.getSourceMethod();
			if (srcMeth != null)
				txtMethod.setText(srcMeth);
		}
		if (txtArguments != null) {
			txtArguments.setText("");
			String args = traceEntry.getArguments();
			if (args != null)
				txtArguments.setText(args);
		}
		if (txtReturnValue != null) {
			txtReturnValue.setText("");
			String retVal = traceEntry.getReturnValue();
			if (retVal != null)
				txtReturnValue.setText(retVal);
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
