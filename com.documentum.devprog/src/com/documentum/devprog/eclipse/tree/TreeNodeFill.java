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

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfBasicAttributes;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfVirtualDocument;
import com.documentum.fc.client.IDfVirtualDocumentNode;

import com.documentum.devprog.eclipse.common.CommonConstants;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
class TreeNodeFill implements IRunnableWithProgress {

	private DocbaseItem parentNode = null;

	// Not used anymore. Attr list obtained from PLugin Preferences
	// String attrList =
	// "r_object_id,object_name,title,r_link_cnt,r_object_type,a_content_type,r_lock_owner";

	Object result[] = null;

	TreeNodeFill(DocbaseItem data) {
		parentNode = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		result = addFolderContents(parentNode, monitor);

	}

	protected Object[] addFolderContents(DocbaseItem parent,
			IProgressMonitor progMon) {
		IDfCollection coll = null;
		LinkedList tList = new LinkedList();
		IDfSession sess = null;
		try {

			IDfTypedObject tObj = (IDfTypedObject) parent.getData();
			String objType = tObj.getString("r_object_type");
			IDfId fldrId = tObj.getId("r_object_id");
			sess = PluginState.getSessionById(fldrId);
			int typePart = fldrId.getTypePart();

			if ((typePart == IDfId.DM_FOLDER) || (typePart == IDfId.DM_CABINET)) {
				int fldrLinkCnt = tObj.getInt("r_link_cnt");
				// System.out.println("num children: " + fldrLinkCnt);
				progMon.beginTask("Obtaining folder contents ", fldrLinkCnt);

				StringBuffer bufQuery = new StringBuffer(32);
				bufQuery.append("select ").append(
						DocbaseItem.getDefaultQueryAttributes());
				// bufQuery.append(" from dm_sysobject where FOLDER(ID('");
				bufQuery.append(" from dm_folder where FOLDER(ID('");
				bufQuery.append(fldrId).append("'))");
				String query = bufQuery.toString();
				IDfQuery q = new DfQuery();
				q.setDQL(query);
				// IDfFolder fldr = (IDfFolder) sess.getObject(fldrId);
				// coll = fldr.getContents(attrList);
				coll = q.execute(sess, IDfQuery.DF_READ_QUERY);

				int workComplete = 0;
				while (coll.next()) {
					if (progMon.isCanceled()) {
						return CommonConstants.EMPTY_OBJ_ARRAY;
					}

					IDfTypedObject tObjChild = coll.getTypedObject();
					progMon.subTask(tObjChild.getString("object_name"));

					int linkCnt = tObjChild.getInt("r_link_cnt");
					String childObjType = tObjChild.getString("r_object_type");
					IDfId childId = tObjChild.getId("r_object_id");
					DocbaseItem nodeData = new DocbaseItem();
					nodeData.setParent(parent);
					if ((childId.getTypePart() == IDfId.DM_DOCUMENT)
							&& (linkCnt > 0)) {
						IDfSysObject sObjChild = (IDfSysObject) sess
								.getObject(childId);
						boolean isVDoc = sObjChild.isVirtualDocument();
						boolean isAssem = false;
						if (!isVDoc) {
							isAssem = PluginHelper.isAssembly(sObjChild);
						}
						if (isVDoc || isAssem) {
							IDfVirtualDocument vDoc = sObjChild
									.asVirtualDocument("", isAssem);
							IDfVirtualDocumentNode vDocRoot = vDoc
									.getRootNode();
							nodeData.setType(DocbaseItem.VDOC_TYPE);
							nodeData.setData(vDocRoot);
							IDfBasicAttributes basicAttrs = vDocRoot
									.getBasicAttributes();
							nodeData.addProperty("object_name",
									basicAttrs.getString("object_name"));
							nodeData.addProperty("a_content_type",
									basicAttrs.getString("a_content_type"));
							tList.add(nodeData);
						}
					} else if (childId.getTypePart() == IDfId.DM_FOLDER) {
						nodeData.setType(DocbaseItem.TYPED_OBJ_TYPE);
						nodeData.setData(tObjChild);
						tList.add(nodeData);
						DocbaseTreeView.putNode(childId.getId(), nodeData);
					}
					// Disabled showing of other types. (7/10/07) - Aashish
					/*
					 * else { nodeData.setType(DocbaseDataItem.TYPED_OBJ_TYPE);
					 * nodeData.setData(tObjChild);
					 * nodeData.addProperty("object_name"
					 * ,tObjChild.getString("object_name"));
					 * nodeData.addProperty
					 * ("a_content_type",tObjChild.getString("a_content_type"));
					 * nodeData.addProperty("r_object_type",tObjChild.getString(
					 * "r_object_type")); tList.add(nodeData); }
					 */

					progMon.worked(1);
				}
				progMon.done();

			}// if (folderType)
			return tList.toArray();
		} catch (DfException dfe) {
			DfLogger.error(this, "Error getting folder childen", null, dfe);
			MessageDialog.openError(null, "Folder Contents Error",
					dfe.getMessage());
			return CommonConstants.EMPTY_OBJ_ARRAY;
		} finally {
			PluginState.releaseSession(sess);

			if (coll != null) {
				try {
					coll.close();
				} catch (DfException dfe2) {
				}
			}
		}
	}

	Object[] getResults() {
		return result;
	}

}
