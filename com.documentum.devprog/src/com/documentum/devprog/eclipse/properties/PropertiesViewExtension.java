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
 * Created on Jul 22, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.properties;

import com.documentum.devprog.eclipse.model.AttrInfo;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

/**
 * Plugins wishing to contribute actions to the properties view menu, must
 * subclass this class.
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class PropertiesViewExtension extends Action {
	/**
	 * Shell for use by extensions
	 */
	private Shell shell = null;

	/**
	 * Information about the selected attribute such as its name, type, value
	 * (if an object) and
	 */
	private AttrInfo attrInfo = null;

	/**
	 * Name of the object type
	 */
	private String typeName = null;

	/**
	 * If properties view is showing properties of an object ( not a type
	 * description), this is the object id.
	 */
	private String objectId = null;

	void setShell(Shell sh) {
		shell = sh;
	}

	/**
	 * Gets the shell.
	 * 
	 * @return
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * Gets the attribute information of the selected attribute
	 * 
	 * @return
	 */
	public AttrInfo getAttrInfo() {
		return attrInfo;
	}

	/**
	 * 
	 * @param ai
	 */
	void setAttrInfo(AttrInfo ai) {
		attrInfo = ai;
	}

	void setTypeName(String tn) {
		typeName = tn;
	}

	/**
	 * Gets the object id if an object's properties are getting dumped.
	 * 
	 * @return
	 */
	public String getObjectId() {
		return objectId;
	}

	void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * Gets the name of the repo type
	 * 
	 * @return
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Method decides if the action(menu item) must be shown in the menu.
	 * Subclasses must override this to determine if an action must be shown
	 * 
	 * @return true=>Show false=>Don't show
	 * 
	 */
	public boolean showAction() {
		return true;
	}

}
