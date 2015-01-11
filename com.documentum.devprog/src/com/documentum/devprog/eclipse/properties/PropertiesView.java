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
 * Created on May 4, 2004
 * 
 * Documentum Developer Program 2004
 *
 */
package com.documentum.devprog.eclipse.properties;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;

import com.documentum.devprog.common.UtilityMethods;
import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.CommonConstants;
import com.documentum.devprog.eclipse.common.DocbaseLoginDialog;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.common.SimpleListDialog;
import com.documentum.devprog.eclipse.query.QueryView;
import com.documentum.devprog.eclipse.types.TypeView;

import java.io.File;
import java.io.FileWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * Shows the properties of a docbase object in a table. Similar to a properties
 * dump. It should be able to show any object type.
 * 
 * Fields Shown: AttrName, AttrType, Value(if any).
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 */
public class PropertiesView extends ViewPart {

	/**
	 * Table viewer used to display the object properties.
	 */
	protected TableViewer propTableViewer = null;

	/**
	 * the table used to display the properties.
	 */
	protected TableTree propTable = null;

	/**
	 * Text box used to enter object id.
	 */
	protected Combo objIdBox = null;

	/**
	 * Maximum number of ids to remember
	 */
	protected int MAX_NUM_IDS = 50;

	/**
	 * the object id of the object whose properties need to be dumped.
	 */
	protected String objId = null;

	/**
	 * Current typename
	 */
	protected String currentType = null;

	/**
	 * Column names of the properties dump table.
	 */
	protected String[] colNames = { "Name", "Value", "Type" };

	protected String[] typeColNames = { "Name", "DataType", "S/R" };

	/**
	 * Composite for this view
	 */
	protected Composite propComposite = null;

	/**
	 * The clipboard instance for this view.
	 */
	protected Clipboard clipboard = null;

	/**
	 * Pop up menu (context menu)
	 */
	protected MenuManager contextMenu = null;

	/**
	 * Action to copy selection to clipboard.
	 */
	protected Action copyAction = null;

	/**
	 * Action to add a new attribute to the type definition
	 */
	protected Action addTypeAttribute = null;

	/**
	 * Action to drop an attribute from the type definition.
	 */
	protected Action dropTypeAttribute = null;

	/**
	 * Save the visible properties into a file
	 */
	protected Action saveProperties = null;

	/**
	 * Show only custom properties
	 */
	protected Action showOnlyCustomProperties = null;

	/**
	 * Creates a 'SELECT' query from the sel attrs and populates the query view.
	 */
	protected Action createSelectQuery = null;

	/**
	 * Shows the indexes of repeating attributes.
	 */
	protected Action showRepeatingAttrIndex = null;

	/**
	 * Set of basic attrs to be highlighted.
	 */
	private static HashSet highlightAttrs = new HashSet();

	public static final String ATTR_KEY = "attrInfo";

	protected Action goBack = null;

	protected Action goForward = null;

	protected Action viewFolderContents = null;

	protected Action showTypeHierarchy = null;

	protected Action editPropertyAction = null;

	/**
	 * If true only custom attributes are shown. Otherwise all attributes are
	 * shown.
	 * 
	 */
	private boolean showOnlyCustom = false;

	private List propViewExts = null;

	public final static String VIEW_EXT_ACTIONS = "propViewExtActions";

	static {
		try {
			String bndlName = "com.documentum.devprog.eclipse.properties.highlight_attr";
			ResourceBundle bndl = ResourceBundle.getBundle(bndlName);
			String basicAttrs = bndl.getString("basic_attrs");
			String attrs[] = basicAttrs.split(",");
			for (int i = 0; i < attrs.length; i++) {
				highlightAttrs.add(attrs[i]);
			}

		} catch (MissingResourceException mre) {
			DfLogger.warn(PropertiesView.class.getName(),
					"Error getting resource bundle", null, mre);
		}

	}

	/**
	 * Flags to indicate the state of the view 1 => View is showing properties
	 * of an object 2 => View is currently showing attribute info of a type.
	 */
	protected final int OBJ_PROP_VIEW = 1;

	protected final int TYPE_VIEW = 2;

	protected int CURRENT_VIEW = OBJ_PROP_VIEW;

	private IDfPersistentObject persisObj;

	private String currentDocbase;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createPartControl(Composite parent) {
		clipboard = new Clipboard(parent.getDisplay());
		propComposite = new Composite(parent, SWT.NONE);
		FormLayout propLayout = new FormLayout();
		propComposite.setLayout(propLayout);

		createObjIdInput();
		createPropertiesTable();

		findExtensions();

		createActions();
		createToolbar();
		createPopupMenu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// showPropertiesTable();
	}

	/**
	 * Sets the id of the object whose properties need to be dumped.
	 * 
	 * @param objId
	 */
	public void setObjectId(String objId) {
		this.objId = objId;
		currentType = objId;
	}

	/**
	 * Gets the object id of the object whose properties need to be dumped.
	 * 
	 * @return
	 */
	public String getObjectId() {
		return objId;
	}

	/**
	 * Creates the panel containing text box and button. These are used to enter
	 * the object id and act on the input object id.
	 * 
	 */
	protected void createObjIdInput() {
		Composite inputComposite = new Composite(propComposite, SWT.NONE);
		// inputComposite.setLayout(new FormLayout());

		inputComposite.setLayout(new GridLayout(5, false));

		// objIdBox = new Text(inputComposite, SWT.BORDER | SWT.SINGLE);
		objIdBox = new Combo(inputComposite, SWT.DROP_DOWN | SWT.FLAT);
		objIdBox.setSize(100, 30);
		objIdBox.setToolTipText("Id or Type of object whose properties need to be viewed");
		objIdBox.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {

				if (e.keyCode == 13) {
					showDataInBox();
				}
			}
		});

		FormData txtBoxData = new FormData();
		txtBoxData.right = new FormAttachment(82, -5);
		txtBoxData.left = new FormAttachment(0, 5);
		// objIdBox.setLayoutData(txtBoxData);
		GridData gd = PluginHelper.getGridData(2);
		gd.widthHint = 200;
		gd.heightHint = 10;
		objIdBox.setLayoutData(gd);

		Button butGo = new Button(inputComposite, SWT.PUSH);
		butGo.setText("Dump");
		FormData butData = new FormData();
		butData.left = new FormAttachment(82, 5);
		butData.right = new FormAttachment(100, -5);
		// butGo.setLayoutData(butData);
		butGo.setLayoutData(PluginHelper.getGridData(1));
		butGo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent se) {
				showDataInBox();
			}

			public void widgetSelected(SelectionEvent se) {
				widgetDefaultSelected(se);
			}
		});

		/*
		 * Button butPrev = new Button(inputComposite, SWT.PUSH | SWT.FLAT |
		 * SWT.ARROW_LEFT);
		 * butPrev.setImage(PluginHelper.getImage("backward_nav.gif"));
		 * butPrev.addSelectionListener(new SelectionListener() { public void
		 * widgetSelected(SelectionEvent se) { int curIndex =
		 * objIdBox.getSelectionIndex(); // walk down the box to older data. If
		 * already at bottom go to // top. if (curIndex ==
		 * (objIdBox.getItemCount() - 1)) { objIdBox.select(0); } else {
		 * objIdBox.select(curIndex + 1); }
		 * 
		 * showDataInBox(); }
		 * 
		 * public void widgetDefaultSelected(SelectionEvent se) {
		 * widgetSelected(se); }
		 * 
		 * }); butPrev.setLayoutData(PluginHelper.getGridData(1));
		 * 
		 * Button butNext = new Button(inputComposite, SWT.PUSH | SWT.FLAT |
		 * SWT.ARROW_RIGHT);
		 * butNext.setImage(PluginHelper.getImage("forward_nav.gif"));
		 * butNext.addSelectionListener(new SelectionListener() { public void
		 * widgetSelected(SelectionEvent se) { int curIndex =
		 * objIdBox.getSelectionIndex(); if (curIndex == 0) { // if at top next
		 * means to go down (cycle) objIdBox.select(objIdBox.getItemCount() -
		 * 1); } else { objIdBox.select(curIndex - 1); } showDataInBox(); }
		 * 
		 * public void widgetDefaultSelected(SelectionEvent se) {
		 * widgetSelected(se); }
		 * 
		 * }); butNext.setLayoutData(PluginHelper.getGridData(1));
		 */
	}

	protected void showDataInBox() {
		String data = getBoxValue();
		if (data != null) {
			data = data.trim();
		}
		if ((new DfId(data)).isObjectId()) {
			setObjectId(data);
			showPropertiesTable();
		} else {
			showTypeProperties(data);
		}
	}

	/**
	 * Creates the table and table viewer UI widgets. These do not contain any
	 * data but are ready for displaying data.
	 * 
	 */
	protected void createPropertiesTable() {
		if (propComposite == null) {
			return;
		}

		// /Table tree is used to maintain backwards compatibility with
		// Eclipse 3.0
		propTable = new TableTree(propComposite, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		Table table = propTable.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn nameCol = new TableColumn(table, SWT.LEFT, 0);
		nameCol.setText(colNames[0]);
		nameCol.setWidth(120);
		nameCol.setResizable(true);

		TableColumn typeCol = new TableColumn(table, SWT.CENTER, 1);
		typeCol.setText(colNames[1]);
		typeCol.setResizable(true);
		typeCol.setWidth(150);

		TableColumn valueCol = new TableColumn(table, SWT.RIGHT, 2);
		valueCol.setText(colNames[2]);
		valueCol.setResizable(true);
		valueCol.setWidth(75);

		table.addMouseListener(new PropertiesMouseListener());

		FormData frmData = new FormData();
		frmData.top = new FormAttachment(11, 5);
		frmData.left = new FormAttachment(0, 5);
		frmData.right = new FormAttachment(100, -5);
		frmData.bottom = new FormAttachment(100, -5);
		propTable.setLayoutData(frmData);

	}

	/**
	 * Displays the properties for the object id specified using
	 * 
	 * @param parent
	 */

	protected void addToBox(String objId) {
		if (objIdBox.indexOf(objId) == -1) {
			objIdBox.add(objId, 0);
			if (objIdBox.getItemCount() > MAX_NUM_IDS) {
				objIdBox.remove(objIdBox.getItemCount() - 1);
			}
		}

		int selInd = objIdBox.indexOf(objId);
		if (selInd != -1) {
			objIdBox.select(selInd);
		}

	}

	public void showPropertiesTable() {

		if (propTable == null) {
			return;
		}

		IStatusLineManager smgr = super.getViewSite().getActionBars()
				.getStatusLineManager();
		smgr.setMessage("Getting Properties");
		smgr.getProgressMonitor().beginTask("Getting Properties",
				IProgressMonitor.UNKNOWN);

		IDfSession sess = null;

		try {
			String objId = getObjectId();
			IDfId id = new DfId(objId);
			if (id.isNull() || id.isObjectId() == false) {
				return;
			} else {
				String docbaseName = DfClient.getLocalClient()
						.getDocbaseNameFromId(id);
				if (PluginState.hasIdentity(docbaseName) == false) {
					DocbaseLoginDialog loginDlg = new DocbaseLoginDialog(this
							.getSite().getShell());
					loginDlg.setDocbaseName(docbaseName);
					loginDlg.open();
				}
			}

			CURRENT_VIEW = this.OBJ_PROP_VIEW;
			sess = PluginState.getSessionById(objId);
			currentDocbase = sess.getDocbaseName();

			propTable.removeAll();

			TableColumn[] tcols = propTable.getTable().getColumns();
			tcols[1].setText("Value");
			tcols[2].setText("Data Type");

			persisObj = sess.getObject(new DfId(objId));
			int attrCnt = persisObj.getAttrCount();
			currentType = persisObj.getType().getName();

			// enable/disable View Folder Contents button
			/*
			 * disabling this. Too much checking to disable a simple button will
			 * unnecessarily cause performance problems. if
			 * (UtilityMethods.hasSupertype(sess, currentType, "dm_sysobject"))
			 * { viewFolderContents.setEnabled(true); } else {
			 * viewFolderContents.setEnabled(false); }
			 */

			String prefx = "";
			if (persisObj.hasAttr("object_name")) {
				prefx = persisObj.getString("object_name");
				String boxLbl = prefx + " --> " + objId;
				addToBox(boxLbl);
			} else if (persisObj.hasAttr("name")) {
				prefx = persisObj.getString("name");
				String boxLbl = prefx + " --> " + objId;
				addToBox(boxLbl);
			} else {
				addToBox(objId);
			}

			// start_pos is the start index of custom attrs (non-inherited
			// attrs)
			int startPos = persisObj.getType().getInt("start_pos");
			ArrayList attrLst = new ArrayList();
			ArrayList sysAttrLst = new ArrayList();
			ArrayList intAttrLst = new ArrayList();
			ArrayList custAttrLst = new ArrayList();
			ArrayList appAttrLst = new ArrayList();

			int startIndex = 0;
			if (this.showOnlyCustom()) {
				startIndex = startPos;
			}

			for (int i = startIndex; i < attrCnt; i++) {
				IDfAttr attr = persisObj.getAttr(i);
				AttrInfo ai = new AttrInfo();
				ai.setObject(true);
				ai.setName(attr.getName());

				if (attr.isRepeating()) {
					int valCnt = persisObj.getValueCount(attr.getName());
					for (int vc = 0; vc < valCnt; vc++) {
						String val = persisObj.getRepeatingString(
								attr.getName(), vc);
						ai.addValue(val);
					}
					ai.setRepeating(true);

				} else {
					ai.setValue(persisObj.getString(attr.getName()));
					ai.setRepeating(false);

				}

				String dataType = getAttrTypeName(attr.getDataType());
				if (attr.getLength() > 0) {
					dataType += "(" + attr.getLength() + ")";
				}
				ai.setDataType(dataType);

				ai.setCustom(((startPos != 0) && (i >= startPos)));
				if (ai.isCustom()) {
					custAttrLst.add(ai);
				} else if (ai.getName().startsWith("r_")) {
					sysAttrLst.add(ai);
				} else if (ai.getName().startsWith("i_")) {
					intAttrLst.add(ai);
				} else if (ai.getName().startsWith("a_")) {
					appAttrLst.add(ai);
				} else {
					attrLst.add(ai);
				}
			}

			// sort separately
			AttrComparator attrComp = new AttrComparator();
			Collections.sort(attrLst, attrComp);
			TableTreeItem ttiStd = new TableTreeItem(propTable, SWT.NONE);
			ttiStd.setText("Standard");

			this.addTreeItems(ttiStd, attrLst);

			Collections.sort(custAttrLst, attrComp);
			TableTreeItem ttiCust = new TableTreeItem(propTable, SWT.NONE);
			ttiCust.setText("Custom");
			addTreeItems(ttiCust, custAttrLst);

			Collections.sort(sysAttrLst, attrComp);
			TableTreeItem ttiSys = new TableTreeItem(propTable, SWT.NONE);
			ttiSys.setText("System");
			addTreeItems(ttiSys, sysAttrLst);

			Collections.sort(appAttrLst, attrComp);
			TableTreeItem ttiApp = new TableTreeItem(propTable, SWT.NONE);
			ttiApp.setText("Application");
			addTreeItems(ttiApp, appAttrLst);

			Collections.sort(intAttrLst, attrComp);
			TableTreeItem ttiInt = new TableTreeItem(propTable, SWT.NONE);
			ttiInt.setText("Internal");
			addTreeItems(ttiInt, intAttrLst);
			// propTable.pack();

			super.getViewSite().getActionBars().getStatusLineManager()
					.getProgressMonitor().done();
			super.getViewSite().getActionBars().getStatusLineManager()
					.setMessage("Done Getting Properties");
		} catch (Exception ex) {
			DfLogger.error(this, "Error showing properties for " + objId, null,
					ex);
			MessageDialog.openError(getSite().getShell(),
					"Properties View Error", ex.getMessage());
		} finally {
			if (sess != null) {
				PluginState.releaseSession(sess);
			}
		}

	}

	public void showTypeProperties(String typeName) {
		if ((typeName == null) || (typeName.length() == 0)) {
			DfLogger.warn(this, "No type name specified to view properties",
					null, null);
			return;
		}

		if (propTable == null) {
			return;
		}

		IDfSession sess = null;

		try {

			sess = PluginState.getSession();
			currentDocbase = PluginState.getDocbase();
			if (sess == null) {
				return;
			}

			CURRENT_VIEW = this.TYPE_VIEW;
			IDfType typeObj = sess.getType(typeName.toLowerCase());
			if (typeObj == null) {
				DfLogger.warn(this, "Invalid typename specified", null, null);
				MessageDialog.openError(getSite().getShell(),
						"Invalid Typename", "Invalid Typename specified");
				return;
			}

			currentType = typeName;

			propTable.removeAll();
			for (int i = 0; i < typeColNames.length; i++) {
				propTable.getTable().getColumn(i).setText(typeColNames[i]);
			}

			addToBox(typeName);

			int attrCnt = typeObj.getInt("attr_count");
			int startPos = typeObj.getInt("start_pos");
			ArrayList attrLst = new ArrayList();
			ArrayList custAttrLst = new ArrayList();
			ArrayList sysAttrLst = new ArrayList();
			ArrayList intAttrLst = new ArrayList();
			ArrayList appAttrLst = new ArrayList();

			int startIndex = 0;
			if (this.showOnlyCustom()) {
				startIndex = startPos;
			}

			for (int i = startIndex; i < attrCnt; i++) {
				AttrInfo attrInfo = new AttrInfo();

				attrInfo.setObject(false);
				attrInfo.setName(typeObj.getRepeatingString("attr_name", i));
				attrInfo.setRepeating(typeObj.getRepeatingBoolean(
						"attr_repeating", i));
				String dataType = getAttrTypeName(typeObj.getRepeatingInt(
						"attr_type", i));
				int attrLength = typeObj.getRepeatingInt("attr_length", i);
				if (attrLength > 0) {
					dataType += "(" + attrLength + ")";
				}
				attrInfo.setDataType(dataType);
				attrInfo.setCustom(((startPos != 0) && (i >= startPos)));

				if (attrInfo.isCustom()) {
					custAttrLst.add(attrInfo);
				} else if (attrInfo.getName().startsWith("r_")) {
					sysAttrLst.add(attrInfo);
				} else if (attrInfo.getName().startsWith("i_")) {
					intAttrLst.add(attrInfo);
				} else if (attrInfo.getName().startsWith("a_")) {
					appAttrLst.add(attrInfo);
				} else {
					attrLst.add(attrInfo);
				}
			}

			AttrComparator attrComp = new AttrComparator();
			Collections.sort(attrLst, attrComp);
			TableTreeItem ttiStd = new TableTreeItem(propTable, SWT.NONE);
			ttiStd.setText("Standard");
			this.addTreeItems(ttiStd, attrLst);

			Collections.sort(custAttrLst, attrComp);
			TableTreeItem ttiCust = new TableTreeItem(propTable, SWT.NONE);
			ttiCust.setText("Custom");
			addTreeItems(ttiCust, custAttrLst);

			Collections.sort(sysAttrLst, attrComp);
			TableTreeItem ttiSys = new TableTreeItem(propTable, SWT.NONE);
			ttiSys.setText("System");
			addTreeItems(ttiSys, sysAttrLst);

			Collections.sort(appAttrLst, attrComp);
			TableTreeItem ttiApp = new TableTreeItem(propTable, SWT.NONE);
			ttiApp.setText("Application");
			addTreeItems(ttiApp, appAttrLst);

			Collections.sort(intAttrLst, attrComp);
			TableTreeItem ttiInt = new TableTreeItem(propTable, SWT.NONE);
			ttiInt.setText("Internal");
			addTreeItems(ttiInt, intAttrLst);

			// propTable.pack();

		} catch (Exception ex) {
			DfLogger.error(this, "Error displaying type info", null, ex);
			MessageDialog.openError(getSite().getShell(),
					"Type Properties Error", ex.getMessage());
		} finally {
			if (sess != null) {
				PluginState.releaseSession(sess);
			}

		}
	}

	/**
	 * Displays just the attributes of a docbase type.
	 * 
	 * @param typeName
	 */
	/*
	 * public void showTypeProperties(String typeName) { if((typeName == null)
	 * || (typeName.length() == 0)) { DfLogger.warn(this,"No type name specified
	 * to view properties",null,null); return; }
	 * 
	 * if (propTableViewer == null) { return; }
	 * 
	 * propTableViewer.getTable().removeAll();
	 * 
	 * IDfSession sess = null; IDfSessionManager sessMgr = null; try { sess =
	 * PluginState.getSession(); if(sess == null) { return; }
	 * 
	 * IDfType typeObj = sess.getType(typeName); if(typeObj == null) {
	 * DfLogger.warn(this,"Invalid typename specified",null,null); return; }
	 * 
	 * 
	 * int attrCnt = typeObj.getInt("attr_count"); for (int i = 0; i < attrCnt;
	 * i++) {
	 * 
	 * AttrInfo attrInfo = new AttrInfo(); String row[] = new String[3];
	 * //row[0] = attr.getName(); attrInfo.name =
	 * typeObj.getRepeatingString("attr_name",i); boolean isRepeating =
	 * typeObj.getRepeatingBoolean("attr_repeating",i); if (isRepeating) {
	 * //row[1] = pObj.getAllRepeatingStrings(attr.getName(), ",");
	 * attrInfo.type = "R"; } else { //row[1] = pObj.getString(attr.getName());
	 * //attrInfo.value = pObj.getString(attr.getName()); attrInfo.type = "S"; }
	 * 
	 * String dataType =
	 * getAttrTypeName(typeObj.getRepeatingInt("attr_type",i)); int attrLength =
	 * typeObj.getRepeatingInt("attr_length",i); if (attrLength > 0) { dataType
	 * += "(" + attrLength + ")"; } //row[2] = dataType; attrInfo.value =
	 * dataType;
	 * 
	 * //ti.setText(row); propTableViewer.add(attrInfo); } //propTable.layout();
	 * } catch (Exception ex) { ex.printStackTrace(); } finally { if (sess !=
	 * null) { PluginState.releaseSession(sess); } } }
	 */

	/**
	 * the IDfAttr.getType() method returns an integer code. This method maps
	 * the code to string values using the constants defined in IDfAttr class.
	 * 
	 * @param num
	 * @return
	 */
	protected String getAttrTypeName(int num) {
		if (num == IDfAttr.DM_BOOLEAN) {
			return "boolean";
		} else if (num == IDfAttr.DM_DOUBLE) {
			return "double";
		} else if (num == IDfAttr.DM_ID) {
			return "ID";
		} else if (num == IDfAttr.DM_INTEGER) {
			return "int";
		} else if (num == IDfAttr.DM_STRING) {
			return "string";
		} else if (num == IDfAttr.DM_TIME) {
			return "time";
		} else if (num == IDfAttr.DM_UNDEFINED) {
			return "undefined";
		} else {
			return "Cannot resolve type";
		}
	}

	protected void createActions() {
		createCopyAction();

		saveProperties = new Action("Save") {
			public void run() {
				saveData();
			}
		};
		saveProperties.setImageDescriptor(PluginHelper
				.getImageDesc(CommonConstants.SAVE_ICON));
		saveProperties
				.setToolTipText("Save Properties to a Tab-Separated File that can be Opened in MS Excel");

		showOnlyCustomProperties = new Action("Show Only Custom Properties") {
			public void run() {
				if (this.isChecked()) {
					setShowOnlyCustom(true);
					// this.setChecked(false);
				} else {
					setShowOnlyCustom(false);
					// this.setChecked(true);
				}

				if (CURRENT_VIEW == PropertiesView.this.OBJ_PROP_VIEW) {
					showPropertiesTable();
				} else if (CURRENT_VIEW == PropertiesView.this.TYPE_VIEW) {
					showTypeProperties(currentType);
				}

			}
		};
		showOnlyCustomProperties.setChecked(false);

		createSelectQuery = new Action("Create Select Query") {
			public void run() {

				AttrInfo[] attrs = getMultiSelection();
				if (attrs.length > 0) {
					StringBuffer bufQuery = new StringBuffer(32);
					bufQuery.append("SELECT ");
					bufQuery.append("r_object_id, ");
					for (int i = 0; i < attrs.length; i++) {
						bufQuery.append(attrs[i].getName());
						if (i < (attrs.length - 1)) {
							bufQuery.append(", ");
						}
					}

					bufQuery.append(" FROM ").append(currentType);

					String strQuery = bufQuery.toString();
					PluginHelper.showQueryView(strQuery);

				}

			}
		};

		goForward = new Action("Go Forward") {
			public void run() {
				goForward();
			}
		};
		goForward.setImageDescriptor(PluginHelper
				.getImageDesc("forward_nav.gif"));

		goBack = new Action("Go Back") {
			public void run() {
				goBack();
			}
		};
		goBack.setImageDescriptor(PluginHelper.getImageDesc("backward_nav.gif"));

		viewFolderContents = new Action("View Folder Contents") {
			public void run() {
				String objId = PropertiesView.this.getBoxValue();
				IDfId id = new DfId(objId);
				if (id.isObjectId()) {
					IDfSession sess = null;
					try {
						sess = PluginState.getSessionById(id);

						IDfPersistentObject pObj = sess.getObject(id);
						IDfType type = pObj.getType();
						if (type.isSubTypeOf("dm_sysobject")) {
							IDfSysObject sObj = (IDfSysObject) pObj;
							String[] paths = PluginHelper
									.getAllContainingFolderPaths(sObj);
							String selPath = "";
							if (paths.length > 1) {
								SimpleListDialog sld = new SimpleListDialog(
										PropertiesView.this.getSite()
												.getShell(), paths,
										"Please select a folder");
								int status = sld.open();
								if (status == SimpleListDialog.OK) {
									selPath = sld.getSelection()[0];
								}
							} else if (paths.length == 1) {
								selPath = paths[0];
							}
							DfLogger.debug(this, "got Selected Path: "
									+ selPath, null, null);
							System.out.println("Selected PAth: " + selPath);

							String fldrId = UtilityMethods.getIdByPath(sess,
									selPath).getId();
							DfLogger.debug(this, "Got Containing Folder Id: "
									+ fldrId, null, null);
							System.out.println("Containing Fldr ID: " + fldrId);
							PluginHelper.showFolderContents(fldrId, id.getId());
						} else {
							MessageDialog
									.openInformation(PropertiesView.this
											.getSite().getShell(), "Object Id",
											"Not a SysObject or this object cannot be contained in a folder/cabinet");
						}

					} catch (Exception ex) {
						MessageDialog.openError(PropertiesView.this.getSite()
								.getShell(), "Error", "Error Opening Folder: "
								+ ex.getMessage());
					} finally {
						PluginState.releaseSession(sess);
					}
				} else {
					MessageDialog.openInformation(PropertiesView.this.getSite()
							.getShell(), "Invalid Id", "Not a Valid Object Id");
				}
			}
		};
		viewFolderContents.setImageDescriptor(PluginHelper
				.getImageDesc("open_folder.gif"));
		viewFolderContents
				.setToolTipText("View Contents of Folder Containing this Object");

		showTypeHierarchy = new Action("Show Type Tree") {
			public void run() {
				if (currentType != null && currentType.length() > 0) {
					PluginHelper.showQuickTypeHierarchy(currentType,
							currentDocbase, true);
				}

			}
		};
		showTypeHierarchy
				.setToolTipText("Show the Type Tree for Current Object (Type)");
		showTypeHierarchy.setImageDescriptor(PluginHelper
				.getImageDesc("hierarchy.gif"));

		editPropertyAction = new Action("Edit Property") {
			public void run() {
				AttrInfo ai = PropertiesView.this.getSelection();
				// DfLogger.debug(this, ", colNames, arg3)
				System.out.println("Got Selected Attribute: " + ai.getName());
				if (ai.isRepeating()) {
					try {
						System.out.println("Is repeeating ");
						RepeatingAttributesEditDialog dlg = new RepeatingAttributesEditDialog(
								PropertiesView.this.getSite().getShell(), ai);

						int status = dlg.open();
						if (status == dlg.OK) {
							if (persistAttribute(ai))
								updateCurrentSelection(ai);

						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} else {
					InputDialog idlg = new InputDialog(PropertiesView.this
							.getSite().getShell(), "Edit Attribute",
							"Enter Attribute Value for " + ai.getName(),
							ai.getValue(), new AttrValueInputValidator(ai));
					int status = idlg.open();
					if (status == InputDialog.OK) {
						String val = idlg.getValue();
						ai.setValue(val);
						ai.setModified(true);
						if (persistAttribute(ai))
							updateCurrentSelection(ai);

					}
				}
			}
		};
	}

	protected void createToolbar() {
		IToolBarManager tbMgr = getViewSite().getActionBars()
				.getToolBarManager();

		tbMgr.add(saveProperties);
		tbMgr.add(viewFolderContents);
		tbMgr.add(showTypeHierarchy);
		tbMgr.add(goBack);
		tbMgr.add(goForward);

	}

	protected void createPopupMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuMgr) {
				PropertiesView.this.fillPopupMenu(menuMgr);
			}
		});

		Menu mnu = menuMgr.createContextMenu(propTable);
		propTable.setMenu(mnu);

		getSite().registerContextMenu(menuMgr, propTableViewer);
		this.contextMenu = menuMgr;
	}

	protected void fillPopupMenu(IMenuManager menuMgr) {

		AttrInfo ai = getSelection();
		// System.out.println("fillPopupMenu called");
		menuMgr.add(copyAction);
		menuMgr.add(showOnlyCustomProperties);
		menuMgr.add(createSelectQuery);

		if (CURRENT_VIEW == this.OBJ_PROP_VIEW) {
			menuMgr.add(editPropertyAction);
		}

		menuMgr.add(new Separator(TypeView.TREE_EXT_ACTIONS));
		for (int i = 0; i < propViewExts.size(); i++) {
			PropertiesViewExtension act = (PropertiesViewExtension) propViewExts
					.get(i);

			act.setAttrInfo(ai);
			act.setTypeName(currentType);
			act.setShell(this.getViewSite().getShell());
			String id = getBoxValue();
			if ((new DfId(id)).isObjectId()) {
				act.setObjectId(id);
			}

			if (act.showAction()) {
				menuMgr.add(act);
			}
		}

		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
				+ "-end"));
	}

	protected void createCopyAction() {
		copyAction = new Action("Copy") {
			public void run() {

				TableTreeItem[] selItems = propTable.getSelection();
				if (selItems.length == 0) {
					return;
				} else {
					String val = selItems[0].getText(1);
					TextTransfer trans = TextTransfer.getInstance();
					clipboard.setContents(new String[] { val },
							new Transfer[] { trans });
				}
			}

		};
	}

	private void goBack() {
		int curIndex = objIdBox.getSelectionIndex();
		// walk down the box to older data. If already at bottom go to
		// top.
		if (curIndex == (objIdBox.getItemCount() - 1)) {
			objIdBox.select(0);
		} else {
			objIdBox.select(curIndex + 1);
		}

		showDataInBox();
	}

	private void goForward() {
		int curIndex = objIdBox.getSelectionIndex();
		if (curIndex == 0) {
			// if at top next means to go down (cycle)
			objIdBox.select(objIdBox.getItemCount() - 1);
		} else {
			objIdBox.select(curIndex - 1);
		}
		showDataInBox();
	}

	private String getSelectedAttr() {
		TableTreeItem ti[] = propTable.getSelection();
		if (ti.length > 0) {
			String val = ti[0].getText(0);
			return val;
		}
		return "";
	}

	public AttrInfo getSelection() {
		TableTreeItem ti[] = propTable.getSelection();
		if (ti.length > 0) {
			return (AttrInfo) ti[0].getData(ATTR_KEY);
		}
		return null;
	}

	private void updateCurrentSelection(AttrInfo ai) {
		TableTreeItem[] ti = propTable.getSelection();
		if (ti.length > 0) {
			TableTreeItem selItem = ti[0];
			selItem.setText(0, ai.getName());
			if (ai.isObject()) {
				if (ai.isRepeating()) {
					String[] vals = ai.getValues();
					StringBuffer bufVals = new StringBuffer(vals.length * 12);
					for (int ivals = 0; ivals < vals.length; ivals++)
						bufVals.append("{").append(vals[ivals]).append("}")
								.append(" , ");

					selItem.setText(1, bufVals.toString());
				} else
					selItem.setText(1, ai.getValue());
				selItem.setText(2, ai.getDataType());
			}

		}
	}

	public AttrInfo[] getMultiSelection() {
		TableTreeItem ti[] = propTable.getSelection();
		if (ti.length > 0) {
			ArrayList lst = new ArrayList(ti.length);
			for (int i = 0; i < ti.length; i++) {
				lst.add((AttrInfo) ti[i].getData(ATTR_KEY));
			}
			return (AttrInfo[]) lst.toArray(new AttrInfo[] {});
		}
		return new AttrInfo[] {};
	}

	private boolean isBasicAttr(String name) {
		return highlightAttrs.contains(name);
	}

	private Color getColor(int color) {
		return getSite().getShell().getDisplay().getSystemColor(color);
	}

	private void saveData() {

		if (propTable == null) {
			return;
		}

		FileDialog fdlg = new FileDialog(getSite().getShell(), SWT.SAVE
				| SWT.SINGLE);
		fdlg.setFilterExtensions(new String[] { ".xls", ".txt", "*" });
		String filename = fdlg.open();

		if (filename != null) {
			try {

				File fl = new File(filename);
				if (fl.exists()) {
					boolean ok = MessageDialog
							.openConfirm(
									getSite().getShell(),
									"Confirm Overwrite",
									"The file "
											+ filename
											+ " already exists. Do you wish to overwrite this file");
					if (ok == false) {
						return;
					}
				}

				FileWriter fw = new FileWriter(fl);

				TableTreeItem[] ti = propTable.getItems();
				String lineSep = System.getProperty("line.separator");
				for (int i = 0; i < ti.length; i++) {

					TableTreeItem t = ti[i];
					String line = t.getText(0) + lineSep;
					fw.write(line);

					TableTreeItem[] childs = t.getItems();

					for (int j = 0; j < childs.length; j++) {
						TableTreeItem child = childs[j];
						StringBuffer lineBuf = new StringBuffer(32);
						lineBuf.append("\"");
						lineBuf.append(child.getText(0));
						lineBuf.append("\"");
						lineBuf.append("\t");
						lineBuf.append("\"");
						lineBuf.append(child.getText(1));
						lineBuf.append("\"");
						lineBuf.append("\t");
						lineBuf.append("\"");
						lineBuf.append(child.getText(2));
						lineBuf.append("\"");
						lineBuf.append(lineSep);
						fw.write(lineBuf.toString());
					}
				}

				fw.close();
				MessageDialog
						.openInformation(getSite().getShell(), "Save",
								"Properties data was successfully saved to "
										+ filename);

			} catch (Exception ex) {
				MessageDialog.openError(getSite().getShell(), "Save Error",
						ex.getMessage());
			}
		}

	}

	/**
	 * Returns the object id or the type name that is currently in the text box
	 * 
	 * @return
	 */
	private String getBoxValue() {
		String val = objIdBox.getText();
		int indx = val.indexOf("-->");
		if (indx != -1) {
			// its an object id and not type.
			// get id now.
			val = val.substring(indx + 3).trim();
		}
		return val;
	}

	private void setShowOnlyCustom(boolean flag) {
		showOnlyCustom = flag;
	}

	private boolean showOnlyCustom() {
		return showOnlyCustom;
	}

	/**
	 * Finds extensions to the tree view
	 * 
	 */
	protected void findExtensions() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg
				.getExtensionPoint(DevprogPlugin.PROP_VIEW_EXT_ID);
		IExtension[] extensions = ep.getExtensions();
		propViewExts = new ArrayList();
		for (int i = 0; i < extensions.length; i++) {
			IExtension ext = extensions[i];
			IConfigurationElement[] ce = ext.getConfigurationElements();
			for (int j = 0; j < ce.length; j++) {
				try {
					PropertiesViewExtension obj = (PropertiesViewExtension) ce[j]
							.createExecutableExtension("class");
					String label = ce[j].getAttribute("label");
					obj.setText(label);
					obj.setShell(getSite().getShell());
					propViewExts.add(obj);
				} catch (CoreException cex) {
					DfLogger.warn(this, "Error loading extensions", null, cex);
				}
			}
		}
	}

	private void addTreeItems(TableTreeItem parent, ArrayList attrLst) {
		for (int i = 0; i < attrLst.size(); i++) {
			AttrInfo ai = (AttrInfo) attrLst.get(i);

			TableTreeItem ti = new TableTreeItem(parent, SWT.NONE);
			ti.setText(0, ai.getName());
			if (ai.isObject()) {
				if (ai.isRepeating()) {
					String repStr = this.createRepeatingAttrDisplayString(ai);
					ti.setText(1, repStr);
				} else
					ti.setText(1, ai.getValue());
				ti.setText(2, ai.getDataType());
			} else {
				ti.setText(1, ai.getDataType());
				if (ai.isRepeating()) {
					ti.setText(2, "R");
				} else {
					ti.setText(2, "S");
				}
			}

			ti.setData(ATTR_KEY, ai);

			if (isBasicAttr(ai.getName())) {
				ti.setForeground(getColor(SWT.COLOR_BLUE));
			}
			if (ai.isCustom()) {
				ti.setForeground(getColor(SWT.COLOR_RED));
			}
		}
		parent.setExpanded(true);
		parent.setFont(PluginHelper.changeFontStyle(parent.getDisplay(),
				parent.getFont(), SWT.BOLD));
	}

	class AttrComparator implements Comparator {
		private Collator coll = Collator.getInstance();

		public int compare(Object o1, Object o2) {
			AttrInfo a1 = (AttrInfo) o1;
			AttrInfo a2 = (AttrInfo) o2;

			return coll.compare(a1.getName(), a2.getName());
		}
	}

	private boolean persistAttribute(AttrInfo ai) {
		IDfSession sess = null;
		try {
			sess = PluginState.getSessionById(this.objId);
			persisObj = (IDfPersistentObject) sess.getObject(new DfId(objId));
			if (ai.isRepeating()) {
				String values[] = ai.getValues();
				persisObj.removeAll(ai.getName());
				for (int i = 0; i < values.length; i++) {
					persisObj.setRepeatingString(ai.getName(), i, values[i]);
				}
			} else {
				persisObj.setString(ai.getName(), ai.getValue());
			}
			persisObj.save();
			return true;
		} catch (Exception ex) {
			DfLogger.error(this, "Error Saving Property " + ai.getName()
					+ " on object " + this.objId, null, ex);
			MessageDialog.openError(getSite().getShell(),
					"Error Saving Attribute", ex.getMessage());
			return false;
		} finally {
			PluginState.releaseSession(sess);
		}
	}

	private String createRepeatingAttrDisplayString(AttrInfo ai) {
		String[] vals = ai.getValues();
		StringBuffer bufVals = new StringBuffer(vals.length * 12);
		for (int ivals = 0; ivals < vals.length; ivals++) {
			bufVals.append("{").append(vals[ivals]).append("}");
			if (ivals < (vals.length - 1))
				bufVals.append(" , ");
		}
		return bufVals.toString();
	}

}