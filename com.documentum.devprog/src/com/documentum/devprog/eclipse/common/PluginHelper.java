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
 * Created on Mar 11, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.common;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.properties.PropertiesView;
import com.documentum.devprog.eclipse.query.QueryView;
import com.documentum.devprog.eclipse.traceview.DFCTraceEntry;
import com.documentum.devprog.eclipse.traceview.TraceEntryView;
import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.devprog.eclipse.tree.DocbaseItemListView;
import com.documentum.devprog.eclipse.tree.DocbaseTreeView;
import com.documentum.devprog.eclipse.types.QuickTypeHierarchyView;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Set;

import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

import com.documentum.com.IDfClientX;
import com.documentum.operations.IDfOperationError;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class PluginHelper {
	private static String logId = "com.documentum.devprog.PluginHelper";

	private static ImageRegistry imageReg = new ImageRegistry();

	private static ImageRegistry imageDescReg = new ImageRegistry();

	private static DFCVersionParser dfcVersion = new DFCVersionParser(
			DfClient.getDFCVersion());

	public static List getSortedDocbaseList() throws DfException {
		ArrayList dbLst = new ArrayList();

		IDfClient localClient = DfClient.getLocalClient();
		IDfDocbaseMap dmap = localClient.getDocbaseMap();
		for (int i = 0; i < dmap.getDocbaseCount(); i++) {
			String name = dmap.getDocbaseName(i);
			dbLst.add(name);
		}
		Collections.sort(dbLst);

		return dbLst;
	}

	public static void showPropertiesView(String objectId) {
		try {
			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();
			PropertiesView viewPart = (PropertiesView) actPage
					.showView(DevprogPlugin.PROP_VIEW_ID);
			viewPart.setObjectId(objectId);
			viewPart.showPropertiesTable();
		} catch (Exception ex) {
			DfLogger.warn(logId, "Error showing properties view", null, ex);
		}
	}

	public static void showTypeProperties(String typeName) {
		try {
			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();
			PropertiesView viewPart = (PropertiesView) actPage
					.showView(DevprogPlugin.PROP_VIEW_ID);
			viewPart.showTypeProperties(typeName);
		} catch (Exception ex) {
			DfLogger.warn(logId, "Error showing properties view", null, ex);
		}
	}

	public static void showQueryView(String queryStr) {
		try {
			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();
			QueryView viewPart = (QueryView) actPage
					.showView(DevprogPlugin.QUERY_VIEW_ID);
			if (queryStr != null && queryStr.length() > 0) {
				viewPart.setQueryString(queryStr);
			}
		} catch (Exception ex) {
			DfLogger.warn(logId, "Error showing properties view", null, ex);
		}
	}

	public static void showQuickTypeHierarchy(String typeName, String docbase,
			boolean showAncestors) {
		try {
			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();
			QuickTypeHierarchyView viewPart = (QuickTypeHierarchyView) actPage
					.showView(DevprogPlugin.QUICK_TYPE_HIERARCHY_VIEW_ID);
			viewPart.showHierarchy(typeName, docbase, showAncestors);
		} catch (Exception ex) {
			DfLogger.warn(logId, "Error showing quick type hierarchy view",
					null, ex);
		}
	}

	public static void showDFCTraceEntry(DFCTraceEntry te) {
		try {
			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();
			TraceEntryView viewPart = (TraceEntryView) actPage
					.showView(TraceEntryView.TRACE_ENTRY_VIEW_ID);

			if (te != null) {
				viewPart.setDFCTraceEntry(te);
			}

		} catch (Exception ex) {
			DfLogger.warn(logId, "Error showing trace entry view", null, ex);
		}
	}

	public static boolean isAssembly(IDfTypedObject sObj) throws DfException {
		String strAssembledFromId = sObj.getString("r_assembled_from_id");
		IDfId id = new DfId(strAssembledFromId);
		return (!id.isNull() && id.isObjectId());
	}

	/**
	 * Gets the URL to a file within the plugin folder.
	 * 
	 * @param relPath
	 *            path should not start with a '/'
	 * @return
	 */
	public static URL getPluginFile(String relPath) {
		Bundle bndl = Platform.getBundle(DevprogPlugin.PLUGIN_ID);
		return bndl.getEntry(relPath);
	}

	/**
	 * Gets the URL to a file within the plugin folder, within a specified
	 * plugin
	 * 
	 * @param pluginId
	 *            The plugin relative to which the path is specified.
	 * @param relPath
	 *            path should not start with a '/'
	 * @return
	 */
	public static URL getPluginFile(String pluginId, String relPath) {
		Bundle bndl = Platform.getBundle(pluginId);
		return bndl.getEntry(relPath);
	}

	/**
	 * Serializes the specified element to a String object.
	 * 
	 * @param node
	 * @return
	 * @throws ConfigException
	 */
	public static String serializeDOMToString(Element node) {
		try {
			CharArrayWriter arrWriter = new CharArrayWriter();
			OutputFormat xmlOutputFormat = new OutputFormat(Method.XML, null,
					true);
			// xmlOutputFormat.setOmitXMLDeclaration(true);
			xmlOutputFormat.setIndenting(false);
			xmlOutputFormat.setLineWidth(0);
			XMLSerializer writer = new XMLSerializer(arrWriter, xmlOutputFormat);
			DOMSerializer domSerializer = writer.asDOMSerializer();
			domSerializer.serialize(node);

			return arrWriter.toString();
		} catch (IOException ioe) {
			return "";
		}

	}

	public static GridData getGridData(int horizSpan) {
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = horizSpan;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessVerticalSpace = true;
		return gd;
	}

	/**
	 * It returns all the docbase types have a specified docbase type as their
	 * supertype. To put it another way, it returns all the subtypes of a
	 * particular type.
	 * 
	 * @param sess
	 *            The docbase session to use
	 * @param superType
	 *            The supertype whose subtypes are needed.
	 * @return A java.util.LinkedList object that contains all subtype names as
	 *         java.lang.String objects.
	 */
	public static String[] getAllSubtypes(IDfSession sess, String superType)
			throws DfException {
		IDfCollection coll = null;
		try {

			StringBuffer bufQuery = new StringBuffer(32);
			bufQuery.append(
					"select r_type_name from dmi_type_info where ANY r_supertype IN ('")
					.append(superType).append("')");

			IDfQuery query = new DfQuery();
			query.setDQL(bufQuery.toString());

			LinkedList subTypeList = new LinkedList();

			coll = query.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				String strType = coll.getString("r_type_name");
				subTypeList.add(strType);
			}

			String[] allSubTypes = new String[subTypeList.size()];
			Collections.sort(subTypeList);
			subTypeList.toArray(allSubTypes);
			return allSubTypes;
		} finally {
			if (coll != null) {
				coll.close();
			}
		}
	}

	/**
	 * Gets all the subtypes of dm_folder or the superType mentioned. The third
	 * flag tells whether to include dm_cabinet and its subtypes or not.
	 * 
	 * @param sess
	 * @param superType
	 *            dm_folder or a subtype of dm_folder. If null dm_folder is
	 *            assumed.
	 * @param filterCabinets
	 *            true=>don't include cabinets or its subtypes. false=>include
	 *            cabinets and its subtypes.
	 * @return
	 * @throws DfException
	 */
	public static String[] getAllFolderTypes(IDfSession sess, String superType,
			boolean filterCabinets) throws DfException {
		if (superType == null || superType.length() == 0) {
			superType = "dm_folder";
		}

		HashSet cabs = null;
		if (filterCabinets) {
			cabs = new HashSet(Arrays.asList(PluginHelper.getAllSubtypes(sess,
					"dm_cabinet")));

		}

		IDfCollection coll = null;
		try {

			StringBuffer bufQuery = new StringBuffer(32);
			bufQuery.append(
					"select r_type_name from dmi_type_info where ANY r_supertype IN ('")
					.append(superType).append("')");

			IDfQuery query = new DfQuery();
			query.setDQL(bufQuery.toString());

			LinkedList subTypeList = new LinkedList();

			coll = query.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				String strType = coll.getString("r_type_name");
				if ((filterCabinets)) {
					if (cabs.contains(strType) == false)
						subTypeList.add(strType);

				} else {
					subTypeList.add(strType);
				}
			}

			String[] allSubTypes = new String[subTypeList.size()];
			Collections.sort(subTypeList);
			subTypeList.toArray(allSubTypes);
			return allSubTypes;
		} finally {
			if (coll != null) {
				coll.close();
			}
		}
	}

	/**
	 * Gets an array of all the format names defined in a repo. The array
	 * contains the value of the 'name' attribute of dm_format
	 * 
	 * @param sess
	 *            repoSession
	 * @return list of formats.
	 * @throws DfException
	 */
	public static String[] getAllFormats(IDfSession sess) throws DfException {
		String query = "select name from dm_format";
		IDfQuery queryObj = new DfQuery();
		queryObj.setDQL(query);
		IDfCollection coll = null;
		ArrayList fmtLst = new ArrayList(20);
		try {
			coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				fmtLst.add(coll.getString("name"));
			}

			return (String[]) fmtLst.toArray(new String[] {});
		} finally {
			if (coll != null) {
				coll.close();
			}
		}

	}

	/**
	 * Gets an image from the image registry
	 * 
	 * @param path
	 * @return
	 */
	public static Image getImage(String path) {
		Image img = imageReg.get(path);
		if (img == null) {

			StringBuffer bufPath = new StringBuffer(24);
			bufPath.append(CommonConstants.ICONS_FOLDER).append(path);
			IPath ipath = new Path(bufPath.toString());

			Bundle bndl = Platform.getBundle(DevprogPlugin.PLUGIN_ID);
			URL url = Platform.find(bndl, ipath);
			ImageDescriptor desc = ImageDescriptor.createFromURL(url);
			img = desc.createImage(true);
			imageReg.put(path, img);
		}
		return img;

	}

	/**
	 * Gets an image from the image registry
	 * 
	 * @param path
	 * @return
	 */
	public static Image getImage(String path, String unknownImg) {
		Image img = imageReg.get(path);
		if (img == null) {

			StringBuffer bufPath = new StringBuffer(24);
			bufPath.append(CommonConstants.ICONS_FOLDER).append(path);
			IPath ipath = new Path(bufPath.toString());

			StringBuffer bufUnknown = new StringBuffer(24);
			bufUnknown.append(CommonConstants.ICONS_FOLDER).append(unknownImg);
			IPath uPath = new Path(bufUnknown.toString());

			Bundle bndl = Platform.getBundle(DevprogPlugin.PLUGIN_ID);
			URL url = Platform.find(bndl, ipath);
			String finalPath = path;
			if (url == null) {
				img = imageReg.get(unknownImg);
				if (img == null) {
					url = Platform.find(bndl, uPath);
					finalPath = unknownImg;
				} else {
					return img;
				}
			}

			ImageDescriptor desc = ImageDescriptor.createFromURL(url);
			img = desc.createImage(true);
			imageReg.put(finalPath, img);
		}
		return img;

	}

	/**
	 * Gets the image descriptor for the specified path. Path is relative to the
	 * icons folder. It should not start with a front slash.
	 * 
	 * @param path
	 * @return
	 */
	public static ImageDescriptor getImageDesc(String path) {
		ImageDescriptor desc = imageDescReg.getDescriptor(path);
		if (desc == null) {

			StringBuffer bufPath = new StringBuffer(24);
			bufPath.append(CommonConstants.ICONS_FOLDER).append(path);
			IPath ipath = new Path(bufPath.toString());

			Bundle bndl = Platform.getBundle(DevprogPlugin.PLUGIN_ID);
			URL url = Platform.find(bndl, ipath);
			desc = ImageDescriptor.createFromURL(url);
			imageDescReg.put(path, desc);
		}
		return desc;
	}

	/**
	 * Gets the list of type names for a repo.
	 * 
	 * @param repoName
	 * @return
	 */
	public static Set getTypeList(String repoName) {
		Set types = new HashSet();
		IDfSession sess = null;
		IDfCollection coll = null;
		try {
			String query = "select name from dm_type";
			IDfQuery queryObj = new DfQuery();
			queryObj.setDQL(query);
			sess = PluginState.getSession(repoName);
			coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				String name = coll.getString("name");
				types.add(name);
			}
		} catch (Exception ex) {
			DfLogger.error(logId, "Error getting types", null, ex);
		} finally {
			if (coll != null) {
				try {
					coll.close();
				} catch (DfException dfe2) {
				}
			}
			PluginState.releaseSession(sess);
		}
		return types;

	}

	/**
	 * Gets ALL the attributes in a repo.
	 * 
	 * @param repo
	 * @return
	 */
	public static Set getAttributeList(String repo) {
		Set set = new HashSet();
		IDfSession sess = null;
		IDfCollection coll = null;
		String query = "select r_object_id,attr_name from dm_type";

		try {
			IDfQuery queryObj = PluginState.getClientX().getQuery();
			queryObj.setDQL(query);

			sess = PluginState.getSession(repo);
			coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				int cnt = coll.getValueCount("attr_name");
				for (int i = 0; i < cnt; i++) {
					String val = coll.getRepeatingString("attr_name", i);
					set.add(val);
				}
			}
		} catch (Exception ex) {
			DfLogger.error(logId, "ERror getting attrs", null, null);
		}
		return set;
	}

	/**
	 * Gets the path of allfolders that contain the given object
	 * 
	 * @param sObj
	 * @return
	 * @throws Exception
	 */
	public static String[] getAllContainingFolderPaths(IDfSysObject sObj)
			throws Exception {
		int fldrCnt = sObj.getFolderIdCount();
		ArrayList pathList = new ArrayList();
		for (int i = 0; i < fldrCnt; i++) {
			IDfId fldrId = sObj.getFolderId(i);
			IDfFolder fldr = (IDfFolder) sObj.getSession().getObject(fldrId);
			int pathCnt = fldr.getFolderPathCount();
			for (int j = 0; j < pathCnt; j++) {
				String path = fldr.getFolderPath(j);
				pathList.add(path);
			}
		}
		Collections.sort(pathList);
		return (String[]) pathList.toArray(new String[] {});
	}

	/**
	 * Gets a string representation of a stack trace.
	 * 
	 * @param th
	 * @return
	 */
	public static String serializeStackTrace(Throwable th) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(bout);
		th.printStackTrace(pw);
		pw.close();
		return bout.toString();

	}

	/**
	 * Checks if the DFC version is 5.3 or later.
	 * 
	 * @return
	 */
	public static boolean isDFC53() {
		return ((dfcVersion.getMajor()) == 5 && (dfcVersion.getMinor() == 3));
	}

	/**
	 * Gets a new FormData instance for the specified dimensions
	 * 
	 * @param top
	 * @param bottom
	 * @param left
	 * @param right
	 * @return
	 */
	public static FormData getFormData(int top, int bottom, int left, int right) {
		FormData fd = new FormData();
		fd.top = new FormAttachment(top, 5);
		fd.bottom = new FormAttachment(bottom, -5);
		fd.left = new FormAttachment(left, 5);
		fd.right = new FormAttachment(right, -5);
		return fd;
	}

	/**
	 * Prints the errors generated by IDfOperation. Errors are generated if
	 * IDfOperation.execute() returns false;
	 * 
	 * @param out
	 *            The output stream to which the error messages are printed.
	 * @param errorList
	 *            The error list geenrated by the operation.
	 * 
	 */
	public static void printOperationErrors(OutputStream out, IDfList errorList) {

		try {
			String beginMessage = "\n--Printing Operation Error List--\n";
			out.write(beginMessage.getBytes());
			for (int errorIndex = 0; errorIndex < errorList.getCount(); errorIndex++) {
				IDfOperationError error = (IDfOperationError) errorList
						.get(errorIndex);

				String strErrMsg = error.getMessage();
				out.write(strErrMsg.getBytes());
			}
			String endMessage = "\n--End Printing Operation Error List--\n";
			out.write(endMessage.getBytes());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Converts the error list to a string so that it can be easily printed out.
	 * 
	 * @param errList
	 * @return
	 */
	public static String prepareOperationError(IDfList errList) {
		StringBuffer bufErr = new StringBuffer(32);
		String nl = System.getProperty("line.separator");
		try {
			for (int i = 0; i < errList.getCount(); i++) {
				IDfOperationError err = (IDfOperationError) errList.get(i);
				bufErr.append(err.getErrorCode() + "," + err.getMessage() + ","
						+ err.getException());
				bufErr.append(nl);
			}
		} catch (Exception ex) {
			bufErr.append(ex.getMessage());
		}
		return bufErr.toString();

	}

	/**
	 * Converts a type part to the corresponding type name.
	 * 
	 * NOTE: This method does not cover all types.
	 * 
	 * @param objectId
	 * @return
	 * @throws DfException
	 */
	public static String getTypeNameFromId(IDfId objectId) throws DfException {
		switch (objectId.getTypePart()) {
		case IDfId.DM_ACL:
			return "dm_acl";
		case IDfId.DM_CABINET:
			return "dm_cabinet";
		case IDfId.DM_DOCUMENT:
			return "dm_document";
		case IDfId.DM_FOLDER:
			return "dm_folder";
		case IDfId.DM_FORMAT:
			return "dm_format";
		case IDfId.DM_METHOD:
			return "dm_method";
		case IDfId.DM_POLICY:
			return "dm_policy";
		case IDfId.DM_PROCESS:
			return "dm_process";
		case IDfId.DM_RELATION:
			return "dm_relation";
		case IDfId.DM_RELATIONTYPE:
			return "dm_relation_type";
		case IDfId.DM_USER:
			return "dm_user";
		case IDfId.DM_WORKFLOW:
			return "dm_workflow";
		default:
			return "";
		}
	}

	/**
	 * Converts a persistent object to a typed object. This is needed because a
	 * persistent object is bound to a session while a typed object returned
	 * from a query is a cached object and hence it is safe to use it.
	 * 
	 * @param sess
	 * @param attrList
	 * @param pObj
	 * @return
	 * @throws DfException
	 */
	public static IDfTypedObject convertToTypedObject(IDfSession sess,
			String attrList, IDfPersistentObject pObj) throws DfException {
		IDfClientX cx = PluginState.getClientX();
		IDfQuery queryObj = cx.getQuery();

		StringBuffer bufQuery = new StringBuffer(24);
		bufQuery.append("select ").append(attrList);
		bufQuery.append(" from ").append(pObj.getType().getName());
		bufQuery.append(" where r_object_id='").append(
				pObj.getString("r_object_id"));
		bufQuery.append("'");
		String strQuery = bufQuery.toString();
		queryObj.setDQL(strQuery);

		IDfCollection coll = null;
		try {
			coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
			if (coll.next()) {
				IDfTypedObject to = coll.getTypedObject();
				return to;
			}
		} finally {
			if (coll != null)
				coll.close();
		}
		return null;
	}

	/**
	 * Checks if the specified property has been configured in the
	 * dfc.properties file.
	 * 
	 * @param property
	 * @return
	 */
	public static boolean hasDFCProperty(String property) {
		try {
			PropertyResourceBundle bndl = (PropertyResourceBundle) PropertyResourceBundle
					.getBundle("dfc");
			String val = bndl.getString(property);
			return (val != null);
		} catch (Exception ex) {
			return false;
		}
	}

	public static Font changeFontStyle(Display disp, Font oldFont, int style) {
		FontData[] fd = oldFont.getFontData();
		for (int i = 0; i < fd.length; i++) {
			fd[i].setStyle(style);
		}
		Font newFnt = new Font(disp, fd);
		return newFnt;
	}

	public static void showFolderContents(Object selObj) {
		System.out.println("showFoldercontents: " + selObj);
		try {

			System.out.println("selObj class: " + selObj.getClass());
			DocbaseItem di = (DocbaseItem) selObj;
			System.out.println("isDI: " + di.isCabinetType() + ","
					+ di.isFolderType());
			if (di.isCabinetType() || di.isFolderType()) {
				IWorkbench wkBench = PlatformUI.getWorkbench();
				IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
				IWorkbenchPage actPage = actWin.getActivePage();

				IDfTypedObject tObj = (IDfTypedObject) di.getData();
				String objId = tObj.getString("r_object_id");
				System.out.println("objId: " + objId);
				DocbaseItemListView listView = (DocbaseItemListView) actPage
						.showView(DevprogPlugin.DOCBASE_ITEM_LIST_VIEW_ID);
				System.out.println("listView: " + listView);
				listView.showFolderContents(objId, true);
			}

		} catch (Exception ex) {
			DfLogger.error(PluginHelper.logId, "Error showing folder contents",
					null, ex);
		}

	}

	public static void showFolderContents(String folderId) {
		try {
			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();

			DocbaseItemListView listView = (DocbaseItemListView) actPage
					.showView(DevprogPlugin.DOCBASE_ITEM_LIST_VIEW_ID);
			System.out.println("listView: " + listView);
			listView.showFolderContents(folderId, true);

		} catch (Exception ex) {
			DfLogger.error(PluginHelper.logId, "Error showing folder contents",
					null, ex);
		}
	}

	public static void showFolderContents(String folderId, String selectionId) {
		try {
			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();

			DocbaseItemListView listView = (DocbaseItemListView) actPage
					.showView(DevprogPlugin.DOCBASE_ITEM_LIST_VIEW_ID);
			System.out.println("listView: " + listView);
			listView.showFolderContents(folderId, selectionId, true);

		} catch (Exception ex) {
			DfLogger.error(PluginHelper.logId, "Error showing folder contents",
					null, ex);
		}
	}

	/**
	 * Gets the TreeViewer being used by the RepoTree
	 * 
	 * @return
	 */
	public static TreeViewer getRepoTreeViewer() {
		IWorkbench wkBench = PlatformUI.getWorkbench();
		IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
		IWorkbenchPage actPage = actWin.getActivePage();
		DocbaseTreeView dtv = (DocbaseTreeView) actPage
				.findView(DevprogPlugin.TREE_VIEW_ID);
		return dtv.getCurrentTreeViewer();
	}

	/**
	 * Gets the TreeViewer being used by the RepoTree
	 * 
	 * @return
	 */
	public static DocbaseTreeView getRepoTree() {
		IWorkbench wkBench = PlatformUI.getWorkbench();
		IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
		IWorkbenchPage actPage = actWin.getActivePage();
		DocbaseTreeView dtv = (DocbaseTreeView) actPage
				.findView(DevprogPlugin.TREE_VIEW_ID);
		return dtv;
	}

	/**
	 * Gets the structured viewer used for displaying object list
	 * 
	 * @return
	 */
	public static TableViewer getObjectListViewer() {
		IWorkbench wkBench = PlatformUI.getWorkbench();
		IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
		IWorkbenchPage actPage = actWin.getActivePage();
		DocbaseItemListView di = (DocbaseItemListView) actPage
				.findView(DevprogPlugin.DOCBASE_ITEM_LIST_VIEW_ID);
		return di.getObjectListViewer();
	}

	/**
	 * 
	 * @return
	 */
	public static DocbaseItemListView getObjectListView() {
		IWorkbench wkBench = PlatformUI.getWorkbench();
		IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
		IWorkbenchPage actPage = actWin.getActivePage();
		DocbaseItemListView di = (DocbaseItemListView) actPage
				.findView(DevprogPlugin.DOCBASE_ITEM_LIST_VIEW_ID);
		return di;
	}
}
