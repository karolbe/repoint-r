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
 * Created on Jul 17, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.tree;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * 
 * 
 * Sorts the contents of a docbase in categories - Folders, Documents and
 * Sysobjects.
 * 
 * Each category is further alphabetically sorted.
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DocbaseTreeSorter extends ViewerSorter {

	public final static int CATEGORY_DOC = 0;

	public final static int CATEGORY_FOLDER = 1;

	public final static int CATEGORY_VDOC = 2;

	public final static int CATEGORY_DOCBASE = 3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		// use default compare - compare using the object label.
		return super.compare(viewer, e1, e2);
	}

	public int category(Object element) {

		DocbaseItem dtd = (DocbaseItem) element;
		/*
		 * if(dtd.getType().equals(DocbaseTreeData.TYPED_OBJ_TYPE)) {
		 * IDfTypedObject to = (IDfTypedObject) dtd.getData(); try { return
		 * to.getId("r_object_id").getTypePart(); } catch(Exception ex){ return
		 * 0; } }
		 */
		if (dtd.isFolderType()) {
			return 0;
		} else if (dtd.isDocumentType()) {
			return 1;
		} else if (dtd.isVdoc()) {
			return 2;
		} else {
			return 3;
		}
	}
}