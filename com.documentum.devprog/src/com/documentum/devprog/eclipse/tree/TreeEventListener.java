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
 * Created on Mar 3, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfVirtualDocumentNode;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.properties.PropertiesView;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Aashish Patil (aashish.patil@documentum.com)
 * 
 */
public class TreeEventListener implements ISelectionChangedListener,
		ITreeViewerListener, IDoubleClickListener {

	Shell shell = null;

	public TreeEventListener(Shell sh) {
		shell = sh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
	 * org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		// System.out.println("Sel changed: " + event.getSource());
		// System.out.println("Sel changed: " + event.getSelection());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeViewerListener#treeCollapsed(org.eclipse
	 * .jface.viewers.TreeExpansionEvent)
	 */
	public void treeCollapsed(TreeExpansionEvent event) {
		// System.out.println("tree collapse: " + event.getElement());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeViewerListener#treeExpanded(org.eclipse
	 * .jface.viewers.TreeExpansionEvent)
	 */
	public void treeExpanded(TreeExpansionEvent event) {
		try {
			DocbaseItem treeData = (DocbaseItem) event.getElement();
			TreeViewer treeViewer = (TreeViewer) event.getTreeViewer();
			Integer nodeType = treeData.getType();
			if (nodeType.equals(DocbaseItem.DOCBASE_TYPE)) {
				treeViewer.update(treeData, null);
			}

		} catch (Exception dfe) {
			DfLogger.debug(this, "Error refreshing repo node", null, dfe);
		}
	}

	/**
	 * handle double click event of a tree node. Currently it shows the
	 * properties dump of the selected object.
	 */
	public void doubleClick(DoubleClickEvent dce) {
		try {
			// TreeViewer viewer = (TreeViewer) dce.getSource();
			IStructuredSelection selection = (IStructuredSelection) dce
					.getSelection();
			Object selObj = selection.getFirstElement();
			// showPropertiesView(selObj);
			PluginHelper.showFolderContents(selObj);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Shows the properties view by obtaining the selected objects r_object_id
	 * and handing it to the properties dump view.
	 * 
	 * @param selObj
	 *            This object is either assumed to be IDfTypedObject or <br>
	 *            IDfVirtualDocumentNode.
	 */
	private void showPropertiesView(Object selObj) {
		try {
			DocbaseItem treeData = (DocbaseItem) selObj;
			Integer nodeType = treeData.getType();
			String objId = null;
			if (nodeType.equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) treeData.getData();
				objId = tObj.getString("r_object_id");
			} else if (nodeType.equals(DocbaseItem.VDOC_TYPE)) {
				IDfVirtualDocumentNode vDocNode = (IDfVirtualDocumentNode) treeData
						.getData();
				objId = vDocNode.getBasicAttributes().getString("r_object_id");
			} else {
				return;
			}
			// System.out.println(objId);

			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();
			PropertiesView viewPart = (PropertiesView) actPage
					.showView(DevprogPlugin.PROP_VIEW_ID);
			viewPart.setObjectId(objId);
			viewPart.showPropertiesTable();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}// showPropertiesView(...)
}// TreeEventListener
