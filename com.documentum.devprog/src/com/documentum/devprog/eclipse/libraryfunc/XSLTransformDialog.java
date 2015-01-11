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
 * Created on Oct 19, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginState;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class XSLTransformDialog extends Dialog {

	private String xmlId = null;
	private XSLTransformComposite xslComp = null;
	private String dsSectName = "XSLTransformDialog";

	public XSLTransformDialog(Shell shell, String xmlId) {
		super(shell);
		this.xmlId = xmlId;
	}

	public XSLTransformDialog(Shell shell) {
		super(shell);
	}

	protected Control createDialogArea(Composite parent) {
		xslComp = new XSLTransformComposite();
		if (xmlId != null) {
			xslComp.setSource(xmlId);
		}
		xslComp.setSessionData(PluginState.getSessionManager(),
				PluginState.getDocbase());
		Composite comp = xslComp.createComposite(parent);

		IDialogSettings ds = DevprogPlugin.getDefault().getDialogSettings();
		IDialogSettings xslSettings = ds.getSection(dsSectName);
		if (xslSettings != null) {
			String xslSrc = xslSettings.get("XSLSource");
			if (xslSrc != null) {
				xslComp.setXSLFile(xslSrc);
			}

			String destFile = xslSettings.get("TargetFile");
			if (destFile != null) {
				xslComp.setTargetFile(destFile);
			}

			if (xmlId == null) {
				String xmlSrc = xslSettings.get("XMLSource");
				if (xmlSrc != null) {
					xslComp.setSource(xmlSrc);
				}
			}
		}

		return comp;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// super.createButton(parent,IDialogConstants.OK_ID,"Perform Transform",true);
		super.createButton(parent, IDialogConstants.CANCEL_ID, "Close", false);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("XSL Transform");
	}

	protected void cancelPressed() {
		IDialogSettings ds = DevprogPlugin.getDefault().getDialogSettings();

		IDialogSettings xslSettings = ds.getSection(dsSectName);
		if (xslSettings == null) {
			xslSettings = ds.addNewSection(dsSectName);
		}
		String xmlSource = xslComp.getSource();
		String xslSource = xslComp.getXSLFile();
		xslSettings.put("XMLSource", xmlSource);
		xslSettings.put("XSLSource", xslSource);
		xslSettings.put("TargetFile", xslComp.getTargetFile());

		super.cancelPressed();
	}

}
