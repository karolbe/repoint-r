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
package com.documentum.devprog.eclipse.tree;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * A class wishing to add menu items(Actions) to the Repo Navigation tree should
 * subclass this class and implement the run() method. The getTreeData method
 * returns an instance of DocbaseTreeData that contains information about the
 * selected Tree Node.
 * 
 * Call the setText() and setImage() methods of the superclass to set the menu
 * item label and icon.
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class RepoTreeExtension extends Action {

	private DocbaseItem treeData = null;

	private TreeViewer repoTreeViewer = null;

	private TableViewer objectListViewer = null;

	private Shell shell = null;

	void setDocbaseItem(DocbaseItem dtd) {
		treeData = dtd;
	}

	void setRepoTreeViewer(TreeViewer tv) {
		repoTreeViewer = tv;
	}

	void setShell(Shell sh) {
		shell = sh;
	}

	/**
	 * Gets the shell that subclasses can use in their run methods.
	 * 
	 * @return
	 */
	protected Shell getShell() {
		return shell;
	}

	/**
	 * Gets the JFace TreeViewer used to display the repository tree. The root
	 * nodes of the tree viewer are the various repositories.
	 * 
	 * @return org.eclipse.jface.viewers.TreeViewer
	 */
	public TreeViewer getRepoTreeViewer() {
		return repoTreeViewer;
	}

	public TableViewer getObjectListViewer() {
		return objectListViewer;
	}

	/**
	 * Returns back information about the selected tree node.
	 * 
	 * @return
	 */
	public DocbaseItem getDocbaseItem() {
		return treeData;
	}

	/**
	 * Determines whether this action should be displayed in the context menu.
	 * Subclasses should override this method. By default it returns true.
	 * 
	 * @return
	 */
	public boolean showAction() {
		return true;
	}

	void setObjectListViewer(TableViewer objectListViewer) {
		this.objectListViewer = objectListViewer;
	}
}
