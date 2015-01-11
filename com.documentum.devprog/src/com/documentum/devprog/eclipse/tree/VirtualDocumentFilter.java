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
 * Created on May 5, 2004
 * 
 * Documentum Developer Program 2004
 *
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfTypedObject;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * 
 * 
 * Allows display of only virtual documents and folders(always there).
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 */
public class VirtualDocumentFilter extends ViewerFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		try {
			DocbaseItem treeData = (DocbaseItem) element;
			Integer type = treeData.getType();
			if (type.equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) treeData.getData();
				IDfId objId = tObj.getId("r_object_id");
				if (objId.getTypePart() == IDfId.DM_FOLDER
						|| (objId.getTypePart() == IDfId.DM_CABINET)) {
					return true;
				} else {
					return false;
				}
			} else if (type.equals(DocbaseItem.VDOC_TYPE)) {
				return true;
			} else {
				return true;
			}

		} catch (DfException dfe) {
			DfLogger.warn(this, "Error filtering vdoc", null, dfe);
			return false;
		}
	}

}