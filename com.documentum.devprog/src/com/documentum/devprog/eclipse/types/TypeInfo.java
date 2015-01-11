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
 * Created on Mar 12, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.types;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfAttr;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDbor;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.util.LinkedList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class TypeInfo implements ITypeViewData {
	private String name = "";

	private String docbaseName;

	private int type;

	private boolean tbo = false;

	private Shell shell = null;

	/**
	 * Means its the root node of the tree.
	 */
	public static int ROOT = 0;

	/**
	 * Means that this is a type.
	 */
	public static int OBJECT_TYPE = 2;

	TypeInfo(String name, int type, String docbaseName) {
		this.type = type;
		this.name = name;
		this.docbaseName = docbaseName;
	}

	void setTBO(boolean flag) {
		tbo = flag;
	}

	public boolean isTBO() {
		return tbo;
	}

	public String getDocbaseName() {
		return docbaseName;
	}

	public int getNodeType() {
		return type;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) {
		IDfSession sess = null;
		IDfCollection coll = null;

		String name = null;
		// System.out.println("getchildren for " + name);

		if (this.getNodeType() == TypeInfo.ROOT) {
			// implies to get all direct children of the
			// persistent object type.
			name = " ";
		} else {
			name = this.getTypeName();
		}

		String query = "select name from dm_type where super_name='" + name
				+ "' ORDER BY name";
		try {
			String docbase = this.getDocbaseName();

			sess = PluginState.getSession(docbase);
			LinkedList lstNames = new LinkedList();

			IDfQuery dqlQuery = new DfQuery();
			dqlQuery.setDQL(query);
			progMon.subTask("Executing Query to get Types");
			coll = dqlQuery.execute(sess, IDfQuery.DF_READ_QUERY);
			progMon.subTask("Getting List of TBOs");
			while (coll.next()) {
				String typeName = coll.getString("name");
				// System.out.println("Got type name: " + typeName);
				TypeInfo ti = new TypeInfo(typeName, TypeInfo.OBJECT_TYPE,
						docbase);
				if (hasTBO(docbase, typeName)) {
					ti.setTBO(true);
				}
				lstNames.add(ti);
			}

			return (ITypeViewData[]) lstNames
					.toArray(ITypeViewData.EMPTY_ARRAY);
		} catch (DfException dfe) {
			DfLogger.warn(this, "", null, dfe);
			MessageDialog.openError(null, "Type List Error", dfe.getMessage());
		} finally {
			if (coll != null) {
				try {
					coll.close();
				} catch (DfException dfe2) {
				}
			}

			if (sess != null) {
				PluginState.releaseSession(sess);
			}

		}
		return ITypeViewData.EMPTY_ARRAY;
	}

	public String getDisplayText() {
		StringBuffer dispStr = new StringBuffer(12);
		dispStr.append(name);
		if (getNodeType() == TypeInfo.OBJECT_TYPE) {
			if (isTBO()) {
				dispStr.append(" [TBO]");
			}
		}

		return dispStr.toString();
	}

	public String getDocbase() {
		return docbaseName;
	}

	public String getIconPath() {
		StringBuffer bufIcon = new StringBuffer(12);
		bufIcon.append("type/t_").append(this.getDisplayText());
		bufIcon.append("_16.gif");
		String strIcon = bufIcon.toString();
		return strIcon;
	}

	public Shell getShell() {
		return shell;
	}

	public void onDoubleClick(DoubleClickEvent dce) {
		// System.out.println("Type name: " + name);
		PluginHelper.showTypeProperties(name);

	}

	public void setShell(Shell sh) {
		this.shell = sh;
	}

	protected boolean hasTBO(String repoName, String name) {
		try {

			if (PluginHelper.isDFC53() == false) {
				// pre 5.3 DFC
				IDfDbor dbor = DfClient.getLocalClient().getDbor();
				try {
					String clsName = dbor.lookupObject(name);
					if (clsName == null || clsName.length() == 0) {
						return false;
					}
				} catch (DfException dfeint) {
					return false;
				}
			} else {
				return TBOList53.hasTBO(repoName, name);
			}
		} catch (DfException dfe) {
			DfLogger.error(this, "Error getting tbo info", null, dfe);

		}
		return false;
	}

	public IDfTypedObject getProperties() {
		return null;
	}

	public boolean hasChildren() {
		return true;
	}

	public String getTypeName() {
		return name;
	}

	/**
	 * This method returns a string which contains the DQl for a type.
	 * 
	 * @return an HTML String which containts the DDL.
	 */
	public String getDQLForType() {
		StringBuffer sQLString = new StringBuffer(32);
		int iAttrCount = 0;
		IDfType oType = this.getType();
		IDfAttr oTypeAttr = null;
		String sSuperName = "";
		String sAttrNAme;
		try {
			sQLString
					.append("<html><body style=\"font-family:Arial,Helvetica;color:blue;font-size:14px\">");
			sQLString = sQLString.append("<b>Create Type</b> \"")
					.append(oType.getName()).append("\"").append(" (<br></br>");
			sSuperName = oType.getSuperName();
			iAttrCount = oType.getTypeAttrCount();

			for (int iIndex = 0; iIndex < iAttrCount; ++iIndex) {
				oTypeAttr = oType.getTypeAttr(iIndex);
				sAttrNAme = oTypeAttr.getName();
				sQLString.append(sAttrNAme).append(" ");
				if (oTypeAttr.getDataType() == oTypeAttr.DM_BOOLEAN) {
					sQLString.append("boolean");
				}
				if (oTypeAttr.getDataType() == oTypeAttr.DM_UNDEFINED) {
					sQLString.append("Undefined");
				}
				if (oTypeAttr.getDataType() == oTypeAttr.DM_DOUBLE) {
					sQLString.append("double");
				}
				if (oTypeAttr.getDataType() == oTypeAttr.DM_STRING) {

					sQLString.append("String(").append(oTypeAttr.getLength())
							.append(")");
				}
				if (oTypeAttr.getDataType() == oTypeAttr.DM_ID) {
					sQLString.append("ID");
				}
				if (oTypeAttr.getDataType() == oTypeAttr.DM_INTEGER) {
					sQLString.append("Integer");
				}
				if (oTypeAttr.getDataType() == oTypeAttr.DM_TIME) {
					sQLString.append("Date");
				}
				if (oTypeAttr.isRepeating()) {
					sQLString.append(" Repeating");
				}
				if (iIndex != iAttrCount - 1) {
					sQLString.append(",<br></br>");
				}
			}
			System.out.println(sQLString);
			sSuperName = sSuperName.length() > 0 ? sSuperName : "Null ";
			sQLString.append(")");
			sQLString.append(" With Super Type ").append(sSuperName)
					.append(" Publish");
			sQLString.append("</body></html>");
		} catch (DfException oDf) {
			DfLogger.warn(this, "", null, oDf);
			MessageDialog.openError(null, "getDQLForType error",
					oDf.getMessage());
		}
		return sQLString.toString();
	}

	public IDfType getType() {
		IDfSession sess = null;
		IDfType oTypeForTypeName = null;
		String docbase = this.getDocbaseName();
		sess = PluginState.getSession(docbase);
		try {
			if (sess != null) {
				oTypeForTypeName = sess.getType(this.getTypeName());
			}

		} catch (DfException oDf) {
			DfLogger.warn(this, "", null, oDf);
			MessageDialog.openError(null, "Type List Error", oDf.getMessage());
		} finally {
			if (sess != null) {
				PluginState.releaseSession(sess);
			}
		}
		return oTypeForTypeName;
	}
}
