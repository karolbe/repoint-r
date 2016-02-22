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
 * Created on Jul 16, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.nav;

import com.documentum.devprog.eclipse.common.CommonConstants;
import com.documentum.devprog.eclipse.common.DocbaseLoginDialog;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.types.ITypeViewData;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfBasicAttributes;
import com.documentum.fc.common.IDfId;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class DocbaseTreeData implements ITypeViewData {
	public final static Integer ROOT = new Integer(0);

	/**
	 * Constant that represents a Repository node (i.e. the root node of a
	 * repo).
	 */
	public final static Integer DOCBASE_TYPE = new Integer(1);

	public final static Integer VDOC_TYPE = new Integer(21);

	public final static Integer TYPED_OBJ_TYPE = new Integer(2);

	// public final static String MORE_TYPE = "moreChildren";

	String attrList = "r_object_id,object_name,title,r_link_cnt,r_object_type,a_content_type,r_lock_owner";

	private Integer type = null;

	private Object data = null;

	private DocbaseTreeData parent = null;

	private HashMap additionalData = new HashMap();

	private Shell shell = null;

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
	void setType(Integer type) {
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
	void setData(Object data) {
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
	void setParent(DocbaseTreeData parent) {
		this.parent = parent;
	}

	/**
	 * Gets the parent of this node
	 * 
	 * @return
	 */
	public DocbaseTreeData getParentNode() {
		return parent;
	}

	void addProperty(String key, String value) {
		additionalData.put(key, value);
	}

	String getProperty(String key) {
		return (String) additionalData.get(key);
	}

	public boolean isDocumentType() {
		if (getType().equals(DocbaseTreeData.DOCBASE_TYPE)) {
			return false;
		}

		if (getType().equals(DocbaseTreeData.TYPED_OBJ_TYPE)) {

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
		return getType().equals(DocbaseTreeData.VDOC_TYPE);
	}

	public boolean isDocbaseType() {
		return getType().equals(DocbaseTreeData.DOCBASE_TYPE);
	}

	public String getDocbase() {
		return null;
	}

	public String getDisplayText() {
		DocbaseTreeData treeData = this;
		Integer nodeType = treeData.getType();
		try {
			if (nodeType.equals(DocbaseTreeData.DOCBASE_TYPE)) {
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
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					PluginState.releaseSession(sess);
				}
			} else if (nodeType.equals(DocbaseTreeData.TYPED_OBJ_TYPE)) {
				// System.out.println("getText: objType");

				IDfTypedObject tObj = (IDfTypedObject) treeData.getData();
				String objectName = tObj.getString("object_name");
				String lockOwner = tObj.getString("r_lock_owner");
				if (lockOwner != null && (lockOwner.length() > 0)) {
					objectName += " (Checked out by " + lockOwner + ")";
				}
				// String objectName = treeData.getProperty("object_name");

				return objectName;
			} else if (nodeType.equals(DocbaseTreeData.VDOC_TYPE)) {
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
			} else if (nodeType.equals(DocbaseTreeData.ROOT)) {
				return "Folders/Cabinets";
			} else {
				return "Unable to get name";
			}
		} catch (Exception dfe) {
			DfLogger.warn(this, "Error obtaining labels", null, dfe);
		}

		return "Error getting object_name";
	}

	public String getIconPath() {
		try {

			DocbaseTreeData treeData = this;
			Integer nodeType = treeData.getType();
			ImageDescriptor imgDesc = null;
			String iconPath = null;
			String missingImg = null;
			boolean addLock = false;
			if (nodeType.equals(DocbaseTreeData.DOCBASE_TYPE)) {
				iconPath = "type/t_docbase_16.gif";

			} else if (nodeType.equals(DocbaseTreeData.TYPED_OBJ_TYPE)) {
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
			} else if (nodeType.equals(DocbaseTreeData.VDOC_TYPE)) {
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
			return iconPath;

		} catch (DfException dfe) {
			DfLogger.error(this, "Error getting icon path", null, dfe);
			return null;
		}
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		IDfCollection coll = null;
		IDfSession sess = null;

		Integer nodeType = null;
		DocbaseTreeData parentNode = this;

		try {
			if (nodeType.equals(DocbaseTreeData.DOCBASE_TYPE)) {
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
						return ITypeViewData.EMPTY_ARRAY;
					}
				}

				ArrayList tList = new ArrayList();
				String strQuery = "select "
						+ attrList
						+ " from dm_cabinet where (is_private=0 or owner_name=USER)";
				IDfQuery query = new DfQuery();
				query.setDQL(strQuery);
				sess = PluginState.getSession(docbase);
				coll = query.execute(sess, IDfQuery.DF_READ_QUERY);
				while (coll.next()) {
					IDfTypedObject tObj = null;
					tObj = coll.getTypedObject();
					DocbaseTreeData treeData = new DocbaseTreeData();
					treeData.setData(tObj);
					treeData.setType(DocbaseTreeData.TYPED_OBJ_TYPE);
					treeData.setParent(parentNode);
					treeData.setShell(this.getShell());
					treeData.addProperty("object_name",
							tObj.getString("object_name"));
					treeData.addProperty("a_content_type",
							tObj.getString("a_content_type"));
					treeData.addProperty("r_object_type",
							tObj.getString("r_object_type"));
					tList.add(treeData);
				}
				return (ITypeViewData[]) tList.toArray();

			} else if (nodeType.equals(DocbaseTreeData.TYPED_OBJ_TYPE)) {

				// The process to obtain children can take long
				// if number of children is large. Hence this
				// operation is done async with feedback.
				IProgressService progServ = PlatformUI.getWorkbench()
						.getProgressService();
				return this.addFolderContents(this, progMon);

			} else if (nodeType.equals(DocbaseTreeData.VDOC_TYPE)) {
				LinkedList childList = new LinkedList();
				IDfVirtualDocumentNode vDocNode = (IDfVirtualDocumentNode) parentNode
						.getData();
				int childCnt = vDocNode.getChildCount();

				for (int j = 0; j < childCnt; j++) {
					IDfVirtualDocumentNode vDocChild = vDocNode.getChild(j);
					DocbaseTreeData nodeData = new DocbaseTreeData();
					nodeData.setType(DocbaseTreeData.VDOC_TYPE);
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
				return (ITypeViewData[]) childList.toArray();
			}
			return ITypeViewData.EMPTY_ARRAY;
		} catch (DfException dfe) {
			DfLogger.error(this, "Error in tree content provider", null, dfe);
			MessageDialog.openError(null, "Tree Error", dfe.getMessage());
			return ITypeViewData.EMPTY_ARRAY;

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

	public void onDoubleClick(DoubleClickEvent dce) {
	}

	public void setShell(Shell sh) {
		this.shell = sh;
	}

	public Shell getShell() {
		return shell;
	}

	public IDfTypedObject getProperties() {
		return null;
	}

	public boolean hasChildren() {
		try {
			DocbaseTreeData treeData = this;
			Integer nodeType = treeData.getType();
			if (nodeType.equals(DocbaseTreeData.TYPED_OBJ_TYPE)) {
				IDfTypedObject tObj = (IDfTypedObject) treeData.getData();
				IDfId id = tObj.getId("r_object_id");

				return ((id.getTypePart() == IDfId.DM_FOLDER) || (id
						.getTypePart() == IDfId.DM_CABINET));

			} else if (nodeType.equals(DocbaseTreeData.VDOC_TYPE)) {
				// NOTE Don't use getChildCount(). It completely slows down the
				// drawing of tree. - Aashish
				IDfVirtualDocumentNode vDocNode = (IDfVirtualDocumentNode) treeData
						.getData();
				return vDocNode.isVirtualDocument();
			} else if (nodeType.equals(DocbaseTreeData.DOCBASE_TYPE)) {
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

	protected ITypeViewData[] addFolderContents(DocbaseTreeData parent,
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
				bufQuery.append("select ").append(attrList);
				bufQuery.append(" from dm_sysobject where FOLDER(ID('");
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
						return ITypeViewData.EMPTY_ARRAY;
					}

					IDfTypedObject tObjChild = coll.getTypedObject();
					progMon.subTask(tObjChild.getString("object_name"));

					int linkCnt = tObjChild.getInt("r_link_cnt");
					String childObjType = tObjChild.getString("r_object_type");
					IDfId childId = tObjChild.getId("r_object_id");
					DocbaseTreeData nodeData = new DocbaseTreeData();
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
							nodeData.setType(DocbaseTreeData.VDOC_TYPE);
							nodeData.setData(vDocRoot);
							IDfBasicAttributes basicAttrs = vDocRoot
									.getBasicAttributes();
							nodeData.addProperty("object_name",
									basicAttrs.getString("object_name"));
							nodeData.addProperty("a_content_type",
									basicAttrs.getString("a_content_type"));
							tList.add(nodeData);
						}
					} else {
						nodeData.setType(DocbaseTreeData.TYPED_OBJ_TYPE);
						nodeData.setData(tObjChild);
						nodeData.addProperty("object_name",
								tObjChild.getString("object_name"));
						nodeData.addProperty("a_content_type",
								tObjChild.getString("a_content_type"));
						nodeData.addProperty("r_object_type",
								tObjChild.getString("r_object_type"));
						tList.add(nodeData);
					}

					progMon.worked(1);
				}
				progMon.done();

			}// if (folderType)
			return (ITypeViewData[]) tList.toArray();
		} catch (DfException dfe) {
			DfLogger.error(this, "Error getting folder childen", null, dfe);
			MessageDialog.openError(null, "Folder Contents Error",
					dfe.getMessage());
			return ITypeViewData.EMPTY_ARRAY;
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
}
