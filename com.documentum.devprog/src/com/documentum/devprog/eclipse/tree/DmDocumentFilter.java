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
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfTypedObject;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters only dm_documents, folders (always there) and virtual documents
 * (since they are technically documents).
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 * 
 * 
 */
public class DmDocumentFilter extends ViewerFilter {

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
			System.out.println("type " + type);
			if (type.equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) treeData.getData();
				IDfId objId = tObj.getId("r_object_id");
				System.out.println(tObj.getString("object_name") + ": "
						+ tObj.getString("r_object_type"));
				if ((objId.getTypePart() == IDfId.DM_DOCUMENT)
						|| (objId.getTypePart() == IDfId.DM_FOLDER)
						|| (objId.getTypePart() == IDfId.DM_CABINET)) {
					System.out.println("dm_doc: true");
					return true;
				} else {
					System.out.println("dm_doc: f");
					return false;
				}
			}
			return true;

		} catch (DfException dfe) {
			DfLogger.warn(this, "Error filtering", null, dfe);
			return false;
		}
	}

}
