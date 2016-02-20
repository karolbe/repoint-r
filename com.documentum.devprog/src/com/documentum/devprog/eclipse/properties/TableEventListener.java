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
 * Created on Jul 19, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.properties;

import com.documentum.devprog.eclipse.model.AttrInfo;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.SimpleListDialog;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class TableEventListener implements IDoubleClickListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse
	 * .jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) {
		/*
		 * System.out.println("Table dbl click: " + event.getSource().getClass()
		 * + " :");
		 */
		IStructuredSelection sel = (IStructuredSelection) event.getSelection();
		if (sel.isEmpty()) {
			return;
		} else {
			AttrInfo ai = (AttrInfo) sel.getFirstElement();
			if (ai.getValue() == null) {
				return;
			}
			DfLogger.debug(this, "Value after dblclick: " + ai.getValue(),
					null, null);
			String[] value = ai.getValue().split(",");

			IDfId id = new DfId(value[0]);
			if (id.isObjectId()) {
				try {
					String objId = value[0];
					if (value.length > 1) {
						SimpleListDialog sld = new SimpleListDialog(null,
								value, "Object Id Selector");
						int status = sld.open();
						if (status == SimpleListDialog.OK) {
							String[] selId = sld.getSelection();
							objId = selId[0];
						}
					}

					DfLogger.debug(this, "Obj Id after dblclick: " + objId,
							null, null);
					IWorkbench wkBench = PlatformUI.getWorkbench();
					IWorkbenchWindow actWin = wkBench
							.getActiveWorkbenchWindow();
					IWorkbenchPage actPage = actWin.getActivePage();
					PropertiesView viewPart = (PropertiesView) actPage
							.showView(DevprogPlugin.PROP_VIEW_ID);
					viewPart.setObjectId(objId);
					viewPart.showPropertiesTable();
				} catch (PartInitException pie) {
					pie.printStackTrace();
				}
			}
			// System.out.println(sel.getFirstElement().getClass().getName());
		}
	}

}