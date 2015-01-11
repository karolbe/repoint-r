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
 * Created on Jun 28, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.properties;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.SimpleListDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PopupList;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class PropertiesMouseListener implements MouseListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt
	 * .events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e) {
		// System.out.println("double click called: " +
		// e.getSource().getClass());
		Object src = e.getSource();
		Table propTbl = (Table) src;

		TableItem ti[] = propTbl.getSelection();
		if (ti == null || ti.length == 0) {
			return;
		}
		{

			String attrVal = ti[0].getText(1);
			if (attrVal == null) {
				return;
			}
			String[] value = attrVal.split(",");

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
						} else {
							return;
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
			}// if(isObjectId)
			else if (ti[0].getText(0).equals("r_object_type")) {
				try {
					IWorkbench wkBench = PlatformUI.getWorkbench();
					IWorkbenchWindow actWin = wkBench
							.getActiveWorkbenchWindow();
					IWorkbenchPage actPage = actWin.getActivePage();
					PropertiesView viewPart = (PropertiesView) actPage
							.showView(DevprogPlugin.PROP_VIEW_ID);
					viewPart.showTypeProperties(value[0]);
				} catch (PartInitException pie) {
					DfLogger.warn(this, "Error opening view", null, pie);
				}
			}
			// System.out.println(sel.getFirstElement().getClass().getName());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events
	 * .MouseEvent)
	 */
	public void mouseDown(MouseEvent e) {

		try {
			if (e.button != 1) {
				return;
			}
			// System.out.println("MouseDown event fired: " +
			// e.getSource().getClass());
			Object src = e.getSource();
			Table propTbl = (Table) src;

			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();
			PropertiesView viewPart = (PropertiesView) actPage
					.findView(DevprogPlugin.PROP_VIEW_ID);

			TableItem ti[] = propTbl.getSelection();
			if (ti == null || ti.length == 0) {
				// System.out.println("ti is null;");
				return;
			}

			AttrInfo ai = (AttrInfo) viewPart.getSelection();
			// System.out.println("got attr info: " + ai.getValue() +
			// ai.isRepeating());

			if (ai != null && ai.isRepeating() && ai.isObject()) {
				String[] value = ai.getValues();
				try {

					for (int i = 0; i < value.length; i++) {
						value[i] = "[" + i + "]" + value[i];
					}

					PopupList pl = new PopupList(propTbl.getShell(),
							SWT.V_SCROLL | SWT.H_SCROLL);
					pl.setItems(value);
					Display disp = Display.getCurrent();

					int x = disp.getCursorLocation().x;
					int y = disp.getCursorLocation().y;
					Rectangle rect = new Rectangle(x + 50, y - 100, 300, 100);

					Point tblLoc = propTbl.getLocation();
					tblLoc = disp.map(propTbl, null, ti[0].getBounds(1).x,
							ti[0].getBounds(1).y);
					Rectangle tstRect = new Rectangle(tblLoc.x + 50,
							tblLoc.y - 100, 300, 100);
					String sel = pl.open(tstRect);
					if (sel != null && sel.length() > 0) {
						sel = sel.substring(sel.indexOf(']') + 1);
						if (new DfId(sel).isObjectId()) {
							viewPart.setObjectId(sel);
							viewPart.showPropertiesTable();
						}
					}
				} catch (Exception pie) {
					pie.printStackTrace();
				}
			}// if(isObjectId)

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.
	 * MouseEvent)
	 */
	public void mouseUp(MouseEvent e) {
	}

}
