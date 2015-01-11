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
 * Created on Feb 5, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.types;

import com.documentum.fc.common.DfException;

import com.documentum.fc.client.IDfTypedObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * Represents a node in the tree.
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public interface ITypeViewData {
	/**
	 * Array of empty objects. Useful if a node wants to return no children.
	 */
	public ITypeViewData[] EMPTY_ARRAY = {};

	/**
	 * Gets the name of the docbase to which this node belongs.
	 * 
	 * @return
	 */
	public String getDocbase();

	/**
	 * The text to be displayed in the tree node
	 * 
	 * @return
	 */
	public String getDisplayText();

	/**
	 * Path relative to the 'icons' folder. Since path is relative it should not
	 * start with a '/'.
	 * 
	 * @return
	 */
	public String getIconPath();

	/**
	 * Gets the children for this node.
	 * 
	 * @param progMon
	 *            TODO
	 * @param parent
	 *            TODO
	 * @return
	 */
	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException;

	/**
	 * Event handler called when this node in the tree is double-clicked.
	 * 
	 * @param dce
	 */
	public void onDoubleClick(DoubleClickEvent dce);

	/**
	 * Sets the shell that this node can use
	 * 
	 * @param sh
	 */
	public void setShell(Shell sh);

	/**
	 * Gets the shell that this node uses.
	 * 
	 * @return
	 */
	public Shell getShell();

	/**
	 * Gets the IDfTypedObject associated with this tree node. This mehtod could
	 * return null, if the implementor does not store data as a IDfTypedObject.
	 * 
	 * TODO Probably remove this method as not all implementors return data as
	 * IDfTypedObejct. Let them decide how to store data instead.
	 * 
	 * @return
	 */
	public IDfTypedObject getProperties();

	/**
	 * Determine whether this node has children or not.
	 * 
	 * @return
	 */
	public boolean hasChildren();

}
