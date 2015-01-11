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
 * Created on Feb 27, 2004
 *  
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfBasicAttributes;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfVirtualDocumentNode;

import com.documentum.devprog.eclipse.common.CommonConstants;
import com.documentum.devprog.eclipse.common.DocbaseLoginDialog;
import com.documentum.devprog.eclipse.common.PluginState;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * 
 * Content provider for the Docbase browser. If input is TypedObject - It checks
 * if folder and returns all objects linked to this folder. - If the object is a
 * virtual document, it returns the root as IDfVirtualDocumentNode.
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 * 
 * 
 */
public class DocbaseTreeContentProvider implements ITreeContentProvider {

	String attrList = "r_object_id,object_name,title,r_link_cnt,r_object_type,a_content_type,r_lock_owner";

	Shell shell = null;

	// The docbase for which this is a provider. Used only if Stacked Tree View
	// is used.
	String docbaseName = null;

	DocbaseTreeContentProvider(Shell sh, String docbaseName) {
		shell = sh;
		this.docbaseName = docbaseName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	public Object[] getChildren(Object parentElement) {
		IDfCollection coll = null;
		IDfSession sess = null;

		Integer nodeType = null;
		DocbaseItem parentNode = null;
		if (parentElement instanceof DocbaseItem) {
			parentNode = (DocbaseItem) parentElement;
			nodeType = parentNode.getType();
		} else {
			return CommonConstants.EMPTY_OBJ_ARRAY;
		}

		try {
			if (nodeType.equals(DocbaseItem.DOCBASE_TYPE)) {
				// must be a docbase name
				String docbase = (String) parentNode.getData();

				if (PluginState.hasIdentity(docbase) == false) {
					// DfLogger.warn(this,"Session manager has no identity
					// established",null,null);
					DocbaseLoginDialog loginDialog = new DocbaseLoginDialog(
							shell);
					loginDialog.setDocbaseName(docbase);
					int code = loginDialog.open();
					if (code == DocbaseLoginDialog.CANCEL) {
						return CommonConstants.EMPTY_OBJ_ARRAY;
					}
				}

				ArrayList tList = new ArrayList();
				String strQuery = "select "
						+ attrList
						+ " from dm_cabinet where (is_private=0 or owner_name=USER)";
				IDfQuery query = new DfQuery();
				query.setDQL(strQuery);
				sess = PluginState.getSession(docbase);
				System.out.println("Session is " + sess);
				coll = query.execute(sess, IDfQuery.DF_READ_QUERY);
				while (coll.next()) {
					IDfTypedObject tObj = null;
					tObj = coll.getTypedObject();
					DocbaseItem treeData = new DocbaseItem();
					treeData.setData(tObj);
					treeData.setType(DocbaseItem.TYPED_OBJ_TYPE);
					treeData.setParent(parentNode);
					treeData.addProperty("object_name",
							tObj.getString("object_name"));
					treeData.addProperty("a_content_type",
							tObj.getString("a_content_type"));
					treeData.addProperty("r_object_type",
							tObj.getString("r_object_type"));
					tList.add(treeData);
					DocbaseTreeView.putNode(tObj.getString("r_object_id"),
							treeData);
				}
				return tList.toArray();

			} else if (nodeType.equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				Object[] toRet = null;
				try {
					// The process to obtain children can take long
					// if number of children is large. Hence this
					// operation is done async with feedback.
					IProgressService progServ = PlatformUI.getWorkbench()
							.getProgressService();
					TreeNodeFill runnable = new TreeNodeFill(parentNode);
					progServ.busyCursorWhile(runnable);

					return runnable.getResults();

				}

				catch (InvocationTargetException ite) {

				} catch (InterruptedException inte) {

				}

			} else if (nodeType.equals(DocbaseItem.VDOC_TYPE)) {
				LinkedList childList = new LinkedList();
				IDfVirtualDocumentNode vDocNode = (IDfVirtualDocumentNode) parentNode
						.getData();
				int childCnt = vDocNode.getChildCount();

				for (int j = 0; j < childCnt; j++) {
					IDfVirtualDocumentNode vDocChild = vDocNode.getChild(j);
					DocbaseItem nodeData = new DocbaseItem();
					nodeData.setType(DocbaseItem.VDOC_TYPE);
					nodeData.setData(vDocChild);
					nodeData.setParent(parentNode);
					IDfBasicAttributes basAttrs = vDocChild
							.getBasicAttributes();
					nodeData.addProperty("object_name",
							basAttrs.getString("object_name"));
					nodeData.addProperty("r_content_type",
							basAttrs.getString("a_content_type"));

					childList.add(nodeData);
				}
				return childList.toArray();
			}
			return CommonConstants.EMPTY_OBJ_ARRAY;
		} catch (DfException dfe) {
			DfLogger.error(this, "Error in tree content provider", null, dfe);
			MessageDialog.openError(null, "Tree Error", dfe.getMessage());
			return CommonConstants.EMPTY_OBJ_ARRAY;

		} finally {
			try {
				if (coll != null) {
					coll.close();
				}
			} catch (DfException dfe2) {
			}

			PluginState.releaseSession(sess);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	public Object getParent(Object element) {
		DocbaseItem treeData = (DocbaseItem) element;
		return treeData.getParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	public boolean hasChildren(Object element) {
		try {
			DocbaseItem treeData = (DocbaseItem) element;
			Integer nodeType = treeData.getType();
			if (nodeType.equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) treeData.getData();
				IDfId id = tObj.getId("r_object_id");

				return ((id.getTypePart() == IDfId.DM_FOLDER) || (id
						.getTypePart() == IDfId.DM_CABINET));

			} else if (nodeType.equals(DocbaseItem.VDOC_TYPE)) {
				// NOTE Don't use getChildCount(). It completely slows down the
				// drawing of tree. - Aashish
				IDfVirtualDocumentNode vDocNode = (IDfVirtualDocumentNode) treeData
						.getData();
				return vDocNode.isVirtualDocument();
			} else if (nodeType.equals(DocbaseItem.DOCBASE_TYPE)) {
				return true;
			}
			return false;

		} catch (DfException dfe) {
			DfLogger.error(this,
					"Error in hasChildren() of tree content provider", null,
					dfe);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		DocbaseItem elem = (DocbaseItem) inputElement;
		if (elem.getType().equals(DocbaseItem.ROOT)) {
			// return getProvidedDocbase();
			return new Object[] {};
		} else {
			return getChildren(inputElement);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	protected Object[] getDocbaseList(DocbaseItem parent) {
		try {
			DfLogger.debug(this, "Generating list of docbases", null, null);
			IDfClient localClient = DfClient.getLocalClient();
			IDfDocbaseMap docMap = localClient.getDocbaseMap();
			int docCnt = docMap.getDocbaseCount();
			DocbaseItem docNodes[] = new DocbaseItem[docCnt];
			for (int i = 0; i < docCnt; i++) {
				docNodes[i] = new DocbaseItem();
				docNodes[i].setData(docMap.getDocbaseName(i));
				docNodes[i].setType(DocbaseItem.DOCBASE_TYPE);
				docNodes[i].setParent(parent);

			}
			return docNodes;

		} catch (DfException dfe) {
			DfLogger.warn(this, "Error obtaining docbase list:", null, dfe);
			return CommonConstants.EMPTY_OBJ_ARRAY;
		}
	}

	/**
	 * Gets the docbase for which this class acts as a provider
	 * 
	 * @return
	 */
	private Object[] getProvidedDocbase() {
		DfLogger.debug(this, "getProvidedDocbase called", null, null);
		DocbaseItem treeData = new DocbaseItem();
		treeData.setType(DocbaseItem.DOCBASE_TYPE);
		treeData.setData(docbaseName);
		Object[] objArr = { treeData };
		return objArr;
	}

}