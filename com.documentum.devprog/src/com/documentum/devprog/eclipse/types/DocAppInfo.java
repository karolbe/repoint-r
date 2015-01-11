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
 * Created on Feb 7, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.types;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfLocalModuleRegistry;
import com.documentum.fc.client.IDfModuleDescriptor;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;

import com.documentum.ApplicationManager.DfApplicationX;
import com.documentum.ApplicationManager.IDfAppRef;
import com.documentum.ApplicationManager.IDfAppRefEnumeration;
import com.documentum.ApplicationManager.IDfApplication;
import com.documentum.com.IDfClientX;

public class DocAppInfo implements ITypeViewData {

	public final static int APP_ROOT = 0;

	public final static int APP_NODE = 1;

	public final static int APP_REF_CAT = 2;

	private int nodeType = 0;

	private String docbase = null;

	private IDfTypedObject appObj;

	private Shell shell = null;

	private final static String iconPath = "type/t_dm_application_16.gif";

	private Integer nodeCategory = null;

	private static HashMap catMap = new HashMap();

	private static String appQuery = "select r_object_id,object_name from dm_application";

	static {
		catMap.put(new Integer(IDfApplication.TYPE_FORMAT), "Formats");
		catMap.put(new Integer(IDfApplication.TYPE_JOB), "Jobs");
		catMap.put(new Integer(IDfApplication.TYPE_METHOD), "Methods");
		catMap.put(new Integer(IDfApplication.TYPE_POLICY), "Lifecycles");
		catMap.put(new Integer(IDfApplication.TYPE_PROCESS), "Processes");
		catMap.put(new Integer(IDfApplication.TYPE_MODULE), "Modules");
		catMap.put(new Integer(IDfApplication.TYPE_XML_APP), "XML Applications");
		catMap.put(new Integer(IDfApplication.TYPE_TYPE), "Object Types");
		catMap.put(new Integer(IDfApplication.TYPE_ALL), "All");
	}

	public DocAppInfo(int nodeType, String docbase) {
		this.nodeType = nodeType;
		this.docbase = docbase;
	}

	public int getNodeType() {
		return nodeType;
	}

	public String getDocbase() {
		return docbase;
	}

	public String getDisplayText() {
		IDfSession sess = null;
		try {
			if (this.getNodeType() == DocAppInfo.APP_ROOT) {
				return "Applications";
			} else if (this.getNodeType() == DocAppInfo.APP_NODE) {
				return getApplication().getString("object_name");
			} else if (this.getNodeType() == DocAppInfo.APP_REF_CAT) {
				return DocAppInfo.getCategoryLabel(this.getNodeCategory());
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error getting app info", null, ex);
		}
		return "";
	}

	public String getIconPath() {
		if (this.getNodeType() == DocAppInfo.APP_REF_CAT) {
			return "type/t_dm_category_16.gif";
		}

		return iconPath;
	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {

		IDfSession sess = null;
		try {

			sess = PluginState.getSession(getDocbase());

			if (this.getNodeType() == DocAppInfo.APP_ROOT) {
				return getApplications(sess);
			} else if (this.getNodeType() == DocAppInfo.APP_NODE) {
				return this.getAppCategories(sess);
			} else if (this.getNodeType() == DocAppInfo.APP_REF_CAT) {
				return this.getCategoryChildren(sess, progMon);
			}
		} finally {
			PluginState.releaseSession(sess);
		}
		return ITypeViewData.EMPTY_ARRAY;

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
		return true;
	}

	public IDfTypedObject getApplication() {
		return appObj;
	}

	public void setApplication(IDfTypedObject appObj) {
		this.appObj = appObj;
	}

	/**
	 * IDfApplication.TYPE_XXX value
	 * 
	 * @param cat
	 */
	public void setNodeCategory(Integer cat) {
		this.nodeCategory = cat;

	}

	public Integer getNodeCategory() {
		return this.nodeCategory;
	}

	public static String getCategoryLabel(Integer catType) {
		return (String) catMap.get(catType);
	}

	private ITypeViewData[] getApplications(IDfSession sess) throws DfException {
		ArrayList lst = new ArrayList();
		IDfClientX cx = PluginState.getClientX();
		IDfQuery queryObj = cx.getQuery();
		queryObj.setDQL(appQuery);

		IDfCollection coll = null;
		try {
			coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				IDfTypedObject to = coll.getTypedObject();
				DocAppInfo di = new DocAppInfo(DocAppInfo.APP_NODE,
						getDocbase());
				di.setApplication(to);
				lst.add(di);
			}
		} finally {
			if (coll != null)
				coll.close();
		}

		return (ITypeViewData[]) lst.toArray(ITypeViewData.EMPTY_ARRAY);
	}

	private ITypeViewData[] getAppCategories(IDfSession sess)
			throws DfException {
		ArrayList lst = new ArrayList();
		Iterator iterKeys = catMap.keySet().iterator();
		while (iterKeys.hasNext()) {
			Integer key = (Integer) iterKeys.next();
			DocAppInfo di = new DocAppInfo(DocAppInfo.APP_REF_CAT,
					this.getDocbase());
			di.setApplication(this.getApplication());
			di.setNodeCategory(key);
			lst.add(di);
		}
		return (ITypeViewData[]) lst.toArray(ITypeViewData.EMPTY_ARRAY);
	}

	/*
	 * private ITypeViewData[] getCategoryChildren(IDfSession sess,
	 * IProgressMonitor progMon) throws DfException { ArrayList lst = new
	 * ArrayList(); progMon.subTask("Querying ...");
	 * app.setSessionManager(PluginState.getSessionManager());
	 * IDfAppRefEnumeration enumAppRef =
	 * app.elements(this.getNodeCategory().intValue());
	 * progMon.subTask("Enumerating...");
	 * 
	 * 
	 * while (enumAppRef.hasMoreElements()) { DocAppInfo di = new
	 * DocAppInfo(DocAppInfo.APP_REF, this.getDocbase());
	 * di.setApplication(this.getApplication()); IDfAppRef ref =
	 * enumAppRef.nextElement(); System.out.println("App Ref: " +
	 * ref.getApplicationObjectId()); di.setAppRef(ref); lst.add(di); }
	 * 
	 * return (ITypeViewData[]) lst.toArray(ITypeViewData.EMPTY_ARRAY); }
	 */

	private ITypeViewData[] getCategoryChildren(IDfSession sess,
			IProgressMonitor progMon) throws DfException {
		ArrayList lst = new ArrayList();
		progMon.subTask("Querying ...");

		IDfApplication app = (new DfApplicationX()).findFromApplicationName(
				sess, this.getApplication().getString("object_name"));
		IDfAppRefEnumeration enumAppRef = app.elements(this.getNodeCategory()
				.intValue());
		progMon.subTask("Enumerating...");

		while (enumAppRef.hasMoreElements()) {
			IDfAppRef appRef = enumAppRef.nextElement();
			IDfId appId = appRef.getApplicationObjectId();
			IDfPersistentObject pObj = sess.getObject(appId);

			switch (this.getNodeCategory().intValue()) {
			case IDfApplication.TYPE_POLICY: {
				IDfTypedObject to = PluginHelper.convertToTypedObject(sess,
						LifecycleInfo.ATTR_LIST, pObj);
				LifecycleInfo li = new LifecycleInfo(
						LifecycleInfo.LIFECYCLE_NODE, getDocbase());
				li.setData(to);
				lst.add(li);
				break;
			}
			case IDfApplication.TYPE_PROCESS: {
				IDfTypedObject to = PluginHelper.convertToTypedObject(sess,
						WorkflowInfo.ATTR_LIST, pObj);
				WorkflowInfo wi = new WorkflowInfo(WorkflowInfo.WF_NODE,
						getDocbase());
				wi.setData(to);
				lst.add(wi);
				break;
			}
			case IDfApplication.TYPE_FORMAT: {
				IDfTypedObject to = PluginHelper.convertToTypedObject(sess,
						FormatInfo.ATTR_LIST, pObj);
				FormatInfo fi = new FormatInfo(FormatInfo.FMT_NODE,
						getDocbase(), to);
				lst.add(fi);
				break;
			}
			case IDfApplication.TYPE_METHOD: {
				IDfTypedObject to = PluginHelper.convertToTypedObject(sess,
						MethodInfo.ATTR_LIST, pObj);
				MethodInfo mi = new MethodInfo(MethodInfo.METH_NODE,
						getDocbase(), to);
				lst.add(mi);
				break;
			}
			case IDfApplication.TYPE_JOB: {
				IDfTypedObject to = PluginHelper.convertToTypedObject(sess,
						JobInfo.ATTR_LIST, pObj);
				JobInfo ji = new JobInfo(JobInfo.JOB_NODE, getDocbase(), to);
				lst.add(ji);
				break;
			}
			case IDfApplication.TYPE_TYPE: {
				TypeInfo ti = new TypeInfo(pObj.getString("name"),
						TypeInfo.OBJECT_TYPE, getDocbase());
				lst.add(ti);
				break;
			}
			case IDfApplication.TYPE_RELATION_TYPE: {
				IDfTypedObject to = PluginHelper.convertToTypedObject(sess,
						RelationTypeInfo.ATTR_LIST, pObj);
				RelationTypeInfo ri = new RelationTypeInfo(
						RelationTypeInfo.REL_NODE, getDocbase(), to);
				lst.add(ri);
				break;
			}
			case IDfApplication.TYPE_MODULE: {
				String objName = pObj.getString("object_name");
				IDfLocalModuleRegistry reg = sess.getModuleRegistry();
				IDfModuleDescriptor desc = reg.getModuleDescriptor(objName);
				ModuleInfo mi = new ModuleInfo(ModuleInfo.MOD_NODE,
						getDocbase(), desc);
				lst.add(mi);
				break;
			}
			case IDfApplication.TYPE_XML_APP: {
				IDfTypedObject to = PluginHelper.convertToTypedObject(sess,
						XMLAppInfo.ATTR_LIST, pObj);
				XMLAppInfo xi = new XMLAppInfo(XMLAppInfo.XMLAPP_NODE,
						getDocbase(), to);
				lst.add(xi);
				break;
			}
			default: {
				DocAppRefInfo dri = new DocAppRefInfo(this.getNodeCategory(),
						getDocbase(), this.getApplication(),
						appRef.getObjectId());
				lst.add(dri);
				break;
			}
			}
		}

		return (ITypeViewData[]) lst.toArray(ITypeViewData.EMPTY_ARRAY);
	}

}
