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
 * Created on Mar 3, 2004
 * 
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.devprog.eclipse.common.CommonConstants;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfVirtualDocumentNode;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfBasicAttributes;
import com.documentum.fc.common.IDfId;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Aashish Patil (aashish.patil@documentum.com)
 * 
 * 
 */
public class DocbaseTreeLabelProvider extends LabelProvider {

	private static Map imageCache = new HashMap();

	private static String folderIcon = "type/t_dm_folder_16.gif";
	private static String docIcon = "type/t_dm_document_16.gif";
	private static String unknownType = "type/t_unknown_16.gif";
	private static String cabIcon = "type/t_dm_cabinet_16.gif";
	private static String lockIcon = "lock.gif";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		try {

			DocbaseItem treeData = (DocbaseItem) element;
			Integer nodeType = treeData.getType();
			ImageDescriptor imgDesc = null;
			String iconPath = null;
			String missingImg = null;
			boolean addLock = false;
			if (nodeType.equals(DocbaseItem.DOCBASE_TYPE)) {
				iconPath = "type/t_docbase_16.gif";

			} else if (nodeType.equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) treeData.getData();
				String type = treeData.getProperty("r_object_type");
				IDfId id = tObj.getId("r_object_id");
				if (id.getTypePart() == IDfId.DM_DOCUMENT) {
					String contentType = treeData.getProperty("a_content_type");
					iconPath = "format/f_" + contentType + "_16.gif";
					missingImg = docIcon;
				} else if (id.getTypePart() == IDfId.DM_FOLDER
						|| id.getTypePart() == IDfId.DM_CABINET) {
					iconPath = "type/t_" + type + "_16.gif";

					if (id.getTypePart() == IDfId.DM_CABINET) {
						missingImg = cabIcon;
					} else {
						missingImg = folderIcon;
					}
				} else {
					iconPath = "type/t_" + type + "_16.gif";
					missingImg = unknownType;
				}
			} else if (nodeType.equals(DocbaseItem.VDOC_TYPE)) {
				IDfVirtualDocumentNode vDocNode = (IDfVirtualDocumentNode) treeData
						.getData();
				if (vDocNode.isVirtualDocument()) {
					iconPath = "type/t_vdoc_16.gif";
				} else {
					IDfId id = vDocNode.getBasicAttributes().getId(
							"r_object_id");
					IDfSession sess = null;
					try {
						sess = PluginState.getSessionById(id.toString());
						IDfSysObject sObj = (IDfSysObject) sess
								.getObjectWithType(id, null,
										CommonConstants.DF_SYSOBJ_CLASS);
						String contentType = sObj.getContentType();
						iconPath = "format/f_" + contentType + "_16.gif";
					} finally {
						PluginState.releaseSession(sess);
					}
				}
				missingImg = docIcon;
			}

			Image img = PluginHelper.getImage(iconPath, missingImg);
			return img;

		} catch (DfException dfe) {
			DfLogger.error(this, "Error getting image", null, dfe);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public synchronized String getText(Object element) {
		DocbaseItem treeData = (DocbaseItem) element;
		Integer nodeType = treeData.getType();
		try {
			if (nodeType.equals(DocbaseItem.DOCBASE_TYPE)) {
				StringBuffer dispData = new StringBuffer(12);
				IDfSession sess = null;
				try {
					String repoName = (String) treeData.getData();
					dispData.append(repoName);
					if (PluginState.hasIdentity(repoName)) {
						sess = PluginState.getSession(repoName);
						if (sess != null) {
							dispData.append(" [")
									.append(sess.getLoginUserName())
									.append("]");
						}
					}
					return dispData.toString();

				} finally {
					PluginState.releaseSession(sess);
				}
			} else if (nodeType.equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				// System.out.println("getText: objType");

				IDfTypedObject tObj = (IDfTypedObject) treeData.getData();
				String objectName = tObj.getString("object_name");
				String lockOwner = tObj.getString("r_lock_owner");
				if (lockOwner != null && (lockOwner.length() > 0)) {
					objectName += " (Checked out by " + lockOwner + ")";
				}
				// String objectName = treeData.getProperty("object_name");

				return objectName;
			} else if (nodeType.equals(DocbaseItem.VDOC_TYPE)) {
				// System.out.println("getText vDoc");
				IDfVirtualDocumentNode vDocNode = (IDfVirtualDocumentNode) treeData
						.getData();
				// IDfSysObject selObj = vDocNode.getSelectedObject();
				IDfBasicAttributes basAttrs = vDocNode.getBasicAttributes();
				String objName = basAttrs.getString("object_name");
				if (basAttrs.getIsLocked()) {
					objName += " ( Checked out )";
				}
				return objName;
			} else {
				return "Unable to get name";
			}
		} catch (Exception dfe) {
			DfLogger.warn(this, "Error obtaining labels", null, dfe);
		}

		return "Error getting object_name";
	}

	/*
	 * 
	 * Dispose off the images. Important when using SWT.
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		Iterator iterVals = imageCache.values().iterator();
		while (iterVals.hasNext()) {
			Image img = (Image) iterVals.next();
			img.dispose();
		}

		imageCache.clear();
	}

	private Image overlayLockIcon(Image img) {

		Image lockImg = (Image) imageCache.get(lockIcon);
		if (lockImg == null) {
			lockImg = PluginHelper.getImage(lockIcon, unknownType);
			imageCache.put(lockIcon, img);
		}
		System.out.println("Got lockImg");
		IconEmbelisher iemb = new IconEmbelisher(img, lockImg,
				IconEmbelisher.TOP_RIGHT);
		Image tmpimg = iemb.getImage();
		return tmpimg;
	}

}