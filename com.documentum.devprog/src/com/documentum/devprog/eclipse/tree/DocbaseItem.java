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
 * Created on Jul 16, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfBasicAttributes;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfVirtualDocumentNode;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.CommonConstants;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.common.PreferenceConstants;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import com.documentum.com.DfClientX;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class DocbaseItem {

	// DEPRECATED - This is now stored in the Plugin Preferences
	// private static String ATTR_LIST =
	// "r_object_id,object_name,title,r_link_cnt,r_object_type,a_content_type,r_lock_owner";

	public final static Integer ROOT = new Integer(0);

	/**
	 * Constant that represents a Repository node (i.e. the root node of a
	 * repo).
	 */
	public final static Integer DOCBASE_TYPE = new Integer(1);

	public final static Integer VDOC_TYPE = new Integer(21);

	public final static Integer TYPED_OBJ_TYPE = new Integer(2);

	// public final static String MORE_TYPE = "moreChildren";

	private Integer type = null;

	private Object data = null;

	private DocbaseItem parent = null;

	private HashMap additionalData = new HashMap();

	private static String folderIcon = "type/t_dm_folder_16.gif";

	private static String docIcon = "type/t_dm_document_16.gif";

	private static String unknownType = "type/t_unknown_16.gif";

	private static String cabIcon = "type/t_dm_cabinet_16.gif";

	private static String lockIcon = "lock.gif";

	/**
	 * Sets the type of the tree node. Example:
	 * DOCBASE_TYPE,VDOC_TYPE,TYPED_OBJ_TYPE
	 * 
	 * @param type
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * Gets the type of this node.
	 * 
	 * @return DOCBASE_TYPE, VDOC_TYPE, TYPED_OBJ_TYPE
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * Sets the data belonging to this node.
	 * 
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * gets the Data contained by this node.
	 * 
	 * @return VDOC_TYPE=>IDfVirtualDocumentNode, TYPED_OBJ_TYPE=>IDfTypedObject
	 *         DOCBASE_TYPE=>Name of the repo.
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Sets the parent of this node.
	 * 
	 * @param parent
	 */
	public void setParent(DocbaseItem parent) {
		this.parent = parent;
	}

	/**
	 * Gets the parent of this node
	 * 
	 * @return
	 */
	public DocbaseItem getParent() {
		return parent;
	}

	void addProperty(String key, String value) {
		additionalData.put(key, value);
	}

	String getProperty(String key) {
		return (String) additionalData.get(key);
	}

	public boolean isDocumentType() {
		if (getType().equals(DocbaseItem.DOCBASE_TYPE)) {
			return false;
		}

		if (getType().equals(DocbaseItem.TYPED_OBJ_TYPE)) {

			try {
				IDfTypedObject tObj = (IDfTypedObject) getData();
				IDfId objId = tObj.getId("r_object_id");

				if (objId.getTypePart() == IDfId.DM_DOCUMENT) {
					return true;
				}
			} catch (DfException dfe) {
				return false;
			}

		}
		return false;
	}

	public boolean isVdoc() {
		return getType().equals(DocbaseItem.VDOC_TYPE);
	}

	public boolean isDocbaseType() {
		return getType().equals(DocbaseItem.DOCBASE_TYPE);
	}

	public boolean isFolderType() {
		try {
			if (getType().equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject to = (IDfTypedObject) getData();
				IDfId oi = to.getId("r_object_id");
				return (oi.getTypePart() == IDfId.DM_FOLDER);
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error getting type", null, ex);
		}
		return false;
	}

	public boolean isCabinetType() {
		try {
			if (getType().equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject to = (IDfTypedObject) getData();
				IDfId oi = to.getId("r_object_id");
				return (oi.getTypePart() == IDfId.DM_CABINET);
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error getting type", null, ex);
		}
		return false;
	}

	/**
	 * Creates a new DocbaseTreeData object of TYPED_OBJ type. Use this to
	 * create objects any sysobjects (e.g. documents, folders, methods,...).
	 * 
	 * @param oi
	 * @return
	 */
	public static DocbaseItem newTypedObjectInstance(DocbaseItem parent,
			IDfId oi) throws Exception {
		IDfTypedObject to = getTypedObject(oi.getId());

		DocbaseItem d = new DocbaseItem();
		d.setData(to);
		d.setType(DocbaseItem.TYPED_OBJ_TYPE);
		d.setParent(parent);
		return d;
	}

	/**
	 * Replaces the current IDfTypedObject with a new one specified by the obj
	 * id The method will populate the typed object with the necessary
	 * attributes
	 * 
	 * @param objId
	 * @throws DfException
	 */
	public void replaceTypedObject(String objId) throws DfException {
		IDfTypedObject to = getTypedObject(objId);
		this.setData(to);

	}

	/**
	 * Refreshes the attributes of the current typed object.
	 * 
	 * @throws DfException
	 */
	public void refreshTypedObject() throws DfException {
		if (this.getType() == DocbaseItem.TYPED_OBJ_TYPE) {
			String objId = ((IDfTypedObject) getData())
					.getString("r_object_id");
			IDfTypedObject to = getTypedObject(objId);
			this.setData(to);
		}
	}

	private static IDfTypedObject getTypedObject(String objId)
			throws DfException {
		IDfSession sess = null;
		IDfCollection coll = null;
		try {
			StringBuffer bufQuery = new StringBuffer(32);
			bufQuery.append("select ").append(getDefaultQueryAttributes());
			bufQuery.append(" from dm_sysobject where r_object_id='");
			bufQuery.append(objId).append("'");
			String q = bufQuery.toString();

			IDfQuery qo = new DfClientX().getQuery();
			qo.setDQL(q);
			sess = PluginState.getSessionById(objId);
			coll = qo.execute(sess, IDfQuery.DF_READ_QUERY);
			if (coll.next()) {
				IDfTypedObject to = coll.getTypedObject();
				return to;
			}

		} finally {
			if (coll != null) {
				coll.close();
			}
			PluginState.releaseSession(sess);
		}
		return null;
	}

	public String getLabelText() {
		DocbaseItem treeData = (DocbaseItem) this;
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

	public Image getIcon() {

		try {

			DocbaseItem treeData = (DocbaseItem) this;
			Integer nodeType = treeData.getType();
			ImageDescriptor imgDesc = null;
			String iconPath = null;
			String missingImg = null;
			boolean addLock = false;

			if (nodeType.equals(DocbaseItem.DOCBASE_TYPE)) {
				iconPath = "type/t_docbase_16.gif";

			} else if (nodeType.equals(DocbaseItem.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) treeData.getData();
				String type = tObj.getString("r_object_type");
				IDfId id = tObj.getId("r_object_id");

				if (tObj.getString("r_lock_owner").length() > 0) {

					addLock = true;
				}

				if (id.getTypePart() == IDfId.DM_DOCUMENT) {
					String contentType = tObj.getString("a_content_type");

					// Commented this out to get icons from the OS itself.
					// iconPath = "format/f_" + contentType + "_16.gif";
					// missingImg = docIcon;

					IDfSession sess = null;
					try {
						/*
						 * //Disabling this for performance reasons sess =
						 * PluginState.getSessionById(id); IDfFormat fmt =
						 * sess.getFormat(contentType); String ext =
						 * fmt.getDOSExtension(); //
						 * System.out.println("extension: " + ext); Program p =
						 * Program.findProgram(ext); ImageData idata = null; if
						 * (p != null) { idata = p.getImageData(); } if (idata
						 * != null) { Image img = new
						 * Image(Display.getDefault(), idata); if(addLock) {
						 * return overlayLock(img); }
						 * 
						 * return img; } else
						 */
						{
							iconPath = "format/f_" + contentType + "_16.gif";
							missingImg = docIcon;
						}
					} finally {
						PluginState.releaseSession(sess);
					}

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
			// System.out.println("IconPath=" + iconPath + "," + missingImg);
			Image img = PluginHelper.getImage(iconPath, missingImg);
			if (addLock) {
				return overlayLock(img);
			}
			return img;

		} catch (DfException dfe) {
			DfLogger.error(this, "Error getting image", null, dfe);
			return null;
		}
	}

	/**
	 * @return the aTTR_LIST
	 */
	public static String getDefaultQueryAttributes() {
		return DevprogPlugin.getDefault().getPluginPreferences()
				.getString(PreferenceConstants.P_ATTR_LIST);
	}

	private Image overlayLock(Image img) {
		Image lckImg = PluginHelper.getImage("lock.gif");
		IconEmbelisher ie = new IconEmbelisher(img, lckImg,
				IconEmbelisher.BOTTOM_RIGHT);
		return ie.getImage();
	}

}
