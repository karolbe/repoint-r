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
 * Created on Jul 22, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.types;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Plugins wanting to contribute actions to the Type Tree should subclass this
 * class and implement the run() method of the Action class.
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class TypeTreeExtension extends Action {
	private TreeViewer typeTreeViewer = null;

	private TypeInfo typeInfo = null;

	private Shell shell = null;

	void setTypeTreeViewer(TreeViewer tv) {
		typeTreeViewer = tv;
	}

	public TreeViewer getTypeTreeViewer() {
		return typeTreeViewer;
	}

	void setTypeInfo(TypeInfo ti) {
		typeInfo = ti;
	}

	/**
	 * Gets the TypeInfo object that contains information about the selected
	 * type node.
	 * 
	 * @return
	 */
	public TypeInfo getTypeInfo() {
		return typeInfo;
	}

	void setShell(Shell sh) {
		shell = sh;
	}

	public Shell getShell() {
		return shell;
	}

	/**
	 * Subclasses can implement this to determine whether to show the action in
	 * the menu or not. Default is that the action is always shown.
	 * 
	 * @return
	 */
	public boolean showAction() {
		return true;
	}

}
