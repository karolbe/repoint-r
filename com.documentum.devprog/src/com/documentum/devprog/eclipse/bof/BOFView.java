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
 * Created on Sep 24, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.bof;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

public class BOFView extends ViewPart {

	public final static String BOF_VIEW_ID = DevprogPlugin.VIEW_EXT_ID
			+ ".bofView";

	private TreeViewer bofViewer = null;

	private MenuManager contextMenu = null;

	private Action refreshNode = null;

	public void createPartControl(Composite parent) {
		bofViewer = new TreeViewer(parent);

		if (PluginHelper.isDFC53() == false) {
			bofViewer.setContentProvider(new BOF52ContentProvider(this
					.getSite().getShell()));
			bofViewer.setLabelProvider(new BOF52LabelProvider());
		} else {
			try {
				/*
				 * super.setContentDescription("Global Module Registry: " +
				 * DfClient.getLocalClient().getModuleRegistry()
				 * .getRegistryHostName());
				 */
				bofViewer.setContentProvider(new BOF53ContentProvider(this
						.getSite().getShell()));
				bofViewer.setLabelProvider(new BOF53LabelProvider());
			} catch (Exception ex) {
				MessageDialog
						.openError(
								super.getSite().getShell(),
								"Error obtaining module registry information. View may not work correctly",
								ex.getMessage());
			}
		}

		createActions();
		createPopupMenu();
		bofViewer.addDoubleClickListener(new BOFTreeListener(getSite()
				.getShell()));
		bofViewer.setInput("");
	}

	protected void createPopupMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager menuMgr) {
				BOFView.this.fillPopupMenu(menuMgr);
			}

		});

		Menu mnu = menuMgr.createContextMenu(bofViewer.getTree());
		bofViewer.getTree().setMenu(mnu);

		getSite().registerContextMenu(menuMgr, bofViewer);
		this.contextMenu = menuMgr;
	}

	protected void fillPopupMenu(IMenuManager menuMgr) {
		menuMgr.add(refreshNode);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
				+ "-end"));
	}

	protected void createActions() {
		refreshNode = new Action("Refresh") {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) bofViewer
						.getSelection();
				if (sel != null && sel.isEmpty() == false) {
					bofViewer.refresh(sel.getFirstElement());
				}
			}
		};
	}

	public void setFocus() {
	}

}
