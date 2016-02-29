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
 * Created on Feb 27, 2004
 * 
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.*;
import com.documentum.devprog.eclipse.model.DocbaseInfo;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.common.DfLogger;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.*;

/**
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class DocbaseTreeView extends ViewPart {

	TreeViewer treeViewer = null;

	DocbaseItem rootNode = null;

	Action showOnlyDocuments = null;

	Action showOnlyVDocs = null;

	Action refreshNode = null;

	Action showLoginDialog = null;

	Action addDocbase = null;

	Action removeDocbase = null;

	DumpPropertiesAction dumpProperties = null;

	Action encodeURL = null;

	ArrayList repoExtensions = null;

	DmDocumentFilter docFilter = null;

	String docFilterKey = "docFilter";

	VirtualDocumentFilter vDocFilter = null;

	String vdocFilterKey = "vdocFilter";

	MenuManager contextMenu = null;

	Composite parent = null;

	HashMap rootDocbaseNodes = new HashMap();

	Action showDFCInfo = null;

	private static Map objIdToNodeMap = new HashMap();

	private static final String DOCBASE_NAMES_KEY = "DOCBASE_NAMES_KEY";

	private IMemento memento = null;

	// ///////////////
	// Stack view support
	Composite treeStack = null;

	StackLayout treeStackLayout = null;

	// Mapping from docbaseName=>TreeViewer used to display tree.
	HashMap treeViewers = null;

	HashMap showTreeActions = null;

	// //END Stack View support
	// ///////////////////////////

	public static final String TREE_EXT_ACTIONS = "treeExtAct";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createPartControl(Composite parent) {
		try {
			this.parent = parent;

			createSingleTreeView();
			// createStackTreeView();
		} catch (Exception ex) {
			DfLogger.error(this, "Error creating view", null, ex);
		}
	}

	private void createSingleTreeView() {
		treeViewer = new TreeViewer(parent);

		Shell sh = parent.getShell();
		ITreeContentProvider contentProvider = new DocbaseTreeContentProvider(
				null, "");
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new DocbaseTreeLabelProvider());
		treeViewer.setSorter(new DocbaseTreeSorter());
		treeViewer.setUseHashlookup(true);

		rootNode = new DocbaseItem();
		rootNode.setType(DocbaseItem.ROOT);
		treeViewer.setInput(rootNode);

		createFilters();
		createActions();
		findExtensions();
		createMenus();
		createToolbar();
		createPopupMenu(treeViewer);
		TreeEventListener listener = new TreeEventListener(sh);
		treeViewer.addSelectionChangedListener(listener);
		treeViewer.addDoubleClickListener(listener);
		treeViewer.addTreeListener(listener);

		this.restoreDocbaseNames(memento);

		// experimeting Dnd. Does not work as yet.
		createDropTarget();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {

	}

	protected void createMenus() {
		IMenuManager menuMgr = getViewSite().getActionBars().getMenuManager();
		/*
		 * IMenuManager filterMgr = new MenuManager("Tree Filters");
		 * 
		 * menuMgr.add(filterMgr); filterMgr.add(showOnlyDocuments);
		 * filterMgr.add(showOnlyVDocs);
		 */

		menuMgr.add(encodeURL);

	}

	/**
	 * Create the toolbar for the view.
	 * 
	 */
	protected void createToolbar() {
		IToolBarManager tbMgr = getViewSite().getActionBars()
				.getToolBarManager();
		tbMgr.add(addDocbase);
		tbMgr.add(removeDocbase);
		tbMgr.add(showDFCInfo);

	}

	protected void createActions() {
		showOnlyDocuments = new Action("Show only dm_document") {
			public void run() {
				TreeViewer curViewer = getCurrentTreeViewer();
				if (curViewer == null) {
					return;
				}

				if (showOnlyDocuments.isChecked()) {
					treeViewer.addFilter(docFilter);
				} else {
					treeViewer.removeFilter(docFilter);
				}
				addDocbasesAgain();

			}
		};
		showOnlyDocuments.setChecked(false);

		showOnlyVDocs = new Action("Show only virtual documents") {
			public void run() {
				TreeViewer curViewer = getCurrentTreeViewer();
				if (curViewer == null) {
					return;
				}

				if (showOnlyVDocs.isChecked()) {
					treeViewer.addFilter(vDocFilter);
				} else {
					treeViewer.removeFilter(vDocFilter);
				}
				addDocbasesAgain();

			}
		};
		showOnlyVDocs.setChecked(false);

		refreshNode = new Action("Refresh tree node") {
			public void run() {
				TreeViewer curViewer = getCurrentTreeViewer();
				if (curViewer == null) {
					return;
				}
				// System.out.println("refresh node called");

				IStructuredSelection selection = (IStructuredSelection) curViewer
						.getSelection();

				if (selection.isEmpty() == false) {
					// System.out.println("requesting refresh of " +
					// selection.getFirstElement().getClass());
					curViewer.refresh(selection.getFirstElement());
				}
			}
		};
		refreshNode
				.setImageDescriptor(PluginHelper.getImageDesc("refresh.gif"));

		showLoginDialog = new Action("Login / Change Login") {
			public void run() {
				DfLogger.debug(this, "login called", null, null);
				TreeViewer curViewer = getCurrentTreeViewer();
				if (curViewer == null) {
					return;
				}

				IStructuredSelection selection = (IStructuredSelection) curViewer
						.getSelection();

				if (selection.isEmpty() == false) {
					// System.out.println("requesting refresh of " +
					// selection.getFirstElement().getClass());
					DocbaseItem data = (DocbaseItem) selection
							.getFirstElement();
					if (data.getType().equals(DocbaseItem.DOCBASE_TYPE)) {
						DocbaseLoginDialog loginDlg = new DocbaseLoginDialog(
								getSite().getShell());
						loginDlg.setDocbaseName((String) data.getData());
						int code = loginDlg.open();
						if (code == DocbaseLoginDialog.OK) {
							curViewer.collapseToLevel(data, 0);
							curViewer.refresh(data);
							curViewer.expandToLevel(data, 1);
						}
					}
				}

			}
		};

		addDocbase = new Action("Add Repo") {
			public void run() {
				try {
					DocbaseListDialog dld = new DocbaseListDialog(
							DocbaseTreeView.this.getSite().getShell());
					int status = dld.open();
					if (status == DocbaseListDialog.OK) {
						String repoName = dld.getSelectedRepo();
						addDocbase(repoName);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		addDocbase
				.setImageDescriptor(PluginHelper.getImageDesc("add_repo.gif"));
		addDocbase
				.setToolTipText("Select and Add a Repository to the Tree View");

		removeDocbase = new Action("Remove Repo") {
			public void run() {
				TreeViewer tv = getCurrentTreeViewer();
				if (tv == null) {
					return;
				}

				IStructuredSelection sel = (IStructuredSelection) tv
						.getSelection();
				Object selObj = sel.getFirstElement();
				if (selObj != null) {
					DocbaseItem dtd = (DocbaseItem) selObj;
					if (dtd.getType().equals(DocbaseItem.DOCBASE_TYPE)) {
						removeDocbase((String) dtd.getData());
					}
				}
			}
		};
		removeDocbase.setImageDescriptor(PluginHelper
				.getImageDesc("remove_repo.gif"));
		removeDocbase
				.setToolTipText("Remove Selected Repository from the Tree");

		showDFCInfo = new Action("Information") {
			public void run() {
				try {
					StringBuffer bufInfo = new StringBuffer(32);

					String version = DfClient.getDFCVersion();
					bufInfo.append("\nDFC Version: ").append(version);

					String location = DfClient.class.getProtectionDomain()
							.getCodeSource().getLocation().toExternalForm();
					bufInfo.append("\n\nDFC JAR Location: ").append(location);

					bufInfo.append("\n\nDFC Properties");
					PropertyResourceBundle bndl = (PropertyResourceBundle) ResourceBundle
							.getBundle("dfc");
					Enumeration keys = bndl.getKeys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						String val = bndl.getString(key);
						bufInfo.append("\n").append(key).append("=")
								.append(val);
					}

					SimpleTextDialog std = new SimpleTextDialog(
							DocbaseTreeView.this.getSite().getShell(),
							"DFC Information", bufInfo.toString());
					std.open();
				} catch (Exception ex) {
					MessageDialog.openError(DocbaseTreeView.this.getSite()
							.getShell(), "Error", ex.getMessage());
				}
			}
		};
		showDFCInfo.setImageDescriptor(PluginHelper.getImageDesc("info.gif"));
		showDFCInfo.setToolTipText("Information");

		encodeURL = new Action("Encode URL") {
			public void run() {
				InputDialog id = new InputDialog(DocbaseTreeView.this.getSite()
						.getShell(), "URL Encoder",
						"Please Enter URL to be Encoded", null, null);
				int status = id.open();
				if (status == InputDialog.OK) {
					try {
						String url = id.getValue();
						url = URLEncoder.encode(url, "UTF-8");
						InputDialog idRes = new InputDialog(
								DocbaseTreeView.this.getSite().getShell(),
								"URL Encoder", "Encoded URL", url, null);
						idRes.open();
					} catch (Exception ex) {
						MessageDialog.openError(DocbaseTreeView.this.getSite()
								.getShell(), "Error", ex.getMessage());
					}
				}
			}
		};

		dumpProperties = new DumpPropertiesAction(this.getCurrentTreeViewer());
		dumpProperties
				.setImageDescriptor(PluginHelper.getImageDesc("info.gif"));
		dumpProperties.setText("Show Properties");
	}

	/**
	 * Creates filters. This is currently not used as a filter causes the whole
	 * tree to be redrawn.
	 * 
	 */
	protected void createFilters() {
		docFilter = new DmDocumentFilter();
		vDocFilter = new VirtualDocumentFilter();
	}

	/**
	 * Create the context(popup) menu
	 * 
	 * @param viewer
	 */
	protected void createPopupMenu(TreeViewer viewer) {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager menuMgr) {
				DocbaseTreeView.this.fillPopupMenu(menuMgr);
			}

		});

		Menu mnu = menuMgr.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(mnu);

		getSite().registerContextMenu(menuMgr, viewer);
		this.contextMenu = menuMgr;
	}

	protected void fillPopupMenu(IMenuManager menuMgr) {
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		DocbaseItem selNode = getSelectedNode();
		menuMgr.add(addDocbase);
		if (selNode != null) {
			// System.out.println("fillPopupMenu called");
			menuMgr.add(refreshNode);
			menuMgr.add(dumpProperties);
			if (selNode.getType().equals(DocbaseItem.DOCBASE_TYPE)) {
				menuMgr.add(showLoginDialog);
				menuMgr.add(removeDocbase);
			}

		}// if(!null)

		// Fill in the extension actions
		menuMgr.add(new Separator(DocbaseTreeView.TREE_EXT_ACTIONS));
		for (int i = 0; i < repoExtensions.size(); i++) {
			RepoTreeExtension act = (RepoTreeExtension) repoExtensions.get(i);
			act.setDocbaseItem(selNode);
			if (act.showAction()) {
				menuMgr.add(act);
			}
		}

		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
				+ "-end"));
	}

	/**
	 * Add a docbase to the view
	 * 
	 * @param docbase
	 */
	public void addDocbase(String docbase) {
		if (rootDocbaseNodes.containsKey(docbase) == false) {
			DocbaseItem ti = new DocbaseItem();
			ti.setType(DocbaseItem.DOCBASE_TYPE);
			ti.setData(docbase);
			treeViewer.add(rootNode, ti);
			treeViewer.reveal(ti);
			rootDocbaseNodes.put(docbase, ti);

		}
	}

	/**
	 * Remove a docbase from the view. Note that this does not destroy the
	 * docbase session.
	 * 
	 * @param docbase
	 */
	public void removeDocbase(String docbase) {
		if (rootDocbaseNodes.containsKey(docbase)) {
			DocbaseItem ti = (DocbaseItem) rootDocbaseNodes.get(docbase);
			treeViewer.remove(ti);
			rootDocbaseNodes.remove(docbase);
		}
	}

	private void addDocbasesAgain() {
		Iterator iterNodes = rootDocbaseNodes.values().iterator();
		while (iterNodes.hasNext()) {
			treeViewer.add(rootNode, iterNodes.next());
		}
	}

	protected DocbaseItem getSelectedNode() {
		IStructuredSelection sel = (IStructuredSelection) treeViewer
				.getSelection();
		if (sel.isEmpty() == false) {
			DocbaseItem dtd = (DocbaseItem) sel.getFirstElement();
			return dtd;
		}
		return null;
	}

	/**
	 * Finds extensions to the tree view
	 * 
	 */
	protected void findExtensions() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg
				.getExtensionPoint(DevprogPlugin.REPO_TREE_EXT_ID);
		IExtension[] extensions = ep.getExtensions();
		repoExtensions = new ArrayList();
		for (int i = 0; i < extensions.length; i++) {
			IExtension ext = extensions[i];
			IConfigurationElement[] ce = ext.getConfigurationElements();
			for (int j = 0; j < ce.length; j++) {
				try {
					RepoTreeExtension obj = (RepoTreeExtension) ce[j]
							.createExecutableExtension("class");
					String label = ce[j].getAttribute("label");
					obj.setText(label);
					obj.setRepoTreeViewer(treeViewer);
					// obj.setObjectListViewer((TableViewer)PluginHelper.getObjectListViewer());
					obj.setShell(getSite().getShell());
					repoExtensions.add(obj);
				} catch (CoreException cex) {
					DfLogger.warn(this, "Error loading extensions", null, cex);
				}
			}
		}
	}

	private void initializePreferences() {
		Preferences prefs = DevprogPlugin.getDefault().getPluginPreferences();

		String attrList = prefs.getString("ATTR_LIST");
	}

	protected void createDropTarget() {
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
		Transfer types[] = new Transfer[] { TextTransfer.getInstance() };
		DropTarget dt = new DropTarget(treeViewer.getTree(), operations);
		dt.setTransfer(types);
		dt.addDropListener(new DropTargetAdapter() {
			public void dragOver(DropTargetEvent event) {
				System.out.println("Droptargetevent:  "
						+ event.currentDataType.toString());
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL
						| DND.FEEDBACK_SELECT;
			}

			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					System.out.println("Event data null");
					event.detail = DND.DROP_NONE;
					return;
				}
				String val = (String) event.data;
				System.out.println("val: " + val);
			}
		});

	}

	public static void putNode(String objId, DocbaseItem treeNode) {
		Set vals = (Set) objIdToNodeMap.get(objId);
		if (vals == null) {
			vals = new HashSet();
			objIdToNodeMap.put(objId, vals);
		}
		vals.add(treeNode);
	}

	public static void remove(String objId) {
		objIdToNodeMap.remove(objId);
	}

	public static DocbaseItem[] getNodes(String objId) {
		Set vals = (Set) objIdToNodeMap.get(objId);
		if (vals != null) {
			return (DocbaseItem[]) vals.toArray(new DocbaseItem[] {});
		} else {
			return new DocbaseItem[] {};
		}
	}

	// ///////////////////////////////////////////////////////////////
	// /// Methods to implement Stacked Tree view/////////////////////
	// ///////////////////////////////////////////////////////////////
	// /This view is not used anymore. The Add/Remove Repo view is used
	// /instead

	private void createStackTreeView() {
		// treeStack
		treeStack = new Composite(parent, SWT.NONE);
		treeStackLayout = new StackLayout();
		treeStack.setLayout(treeStackLayout);

		createActions();
		createDocbaseTreeViewers();

		createFilters();

		createMenus();

		createShowDocbaseTreeActions();

	}

	private void createDocbaseTreeViewers() {
		try {
			treeViewers = new HashMap();
			java.util.List dbList = PluginHelper.getSortedDocbaseList();

			for (int i = 0; i < dbList.size(); i++) {
				String docbaseName = (String) dbList.get(i);
				setupOneTreeViewer(docbaseName);
			}

			String name = (String) dbList.get(0);
			TreeViewer tv = (TreeViewer) treeViewers.get(name);
			if (tv != null) {
				Tree topTr = tv.getTree();
				treeStackLayout.topControl = topTr;
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error preparing docbase tree views", null, ex);

		}
	}

	private void setupOneTreeViewer(String docbaseName) {
		Shell sh = parent.getShell();
		TreeViewer viewer = new TreeViewer(treeStack);
		ITreeContentProvider contentProvider = new DocbaseTreeContentProvider(
				sh, docbaseName);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new DocbaseTreeLabelProvider());
		viewer.setSorter(new ViewerSorter());

		DocbaseItem docbaseNode = new DocbaseItem();
		docbaseNode.setType(DocbaseItem.ROOT);
		docbaseNode.setData("Root");
		// System.out.println("About to setInput");
		viewer.setInput(docbaseNode);

		TreeEventListener listener = new TreeEventListener(sh);
		viewer.addSelectionChangedListener(listener);
		viewer.addDoubleClickListener(listener);
		viewer.addTreeListener(listener);

		createPopupMenu(viewer);

		Tree tree = viewer.getTree();
		tree.setData(docbaseName);

		treeViewers.put(docbaseName, viewer);
		// System.out.println("Setup viewer for " + docbaseName);
	}

	public TreeViewer getCurrentTreeViewer() {
		/*
		 * Tree visibleTree = (Tree) treeStackLayout.topControl; if (visibleTree
		 * != null) {
		 * 
		 * String name = (String) visibleTree.getData(); TreeViewer tv =
		 * (TreeViewer) treeViewers.get(name); return tv; } return null;
		 */
		return treeViewer;
	}

	private void createShowDocbaseTreeActions() {
		try {
			String mnuName = "Repositories";
			IMenuManager menuMgr = getViewSite().getActionBars()
					.getMenuManager();

			IMenuManager dbLstMnu = menuMgr.findMenuUsingPath(mnuName);
			if (dbLstMnu == null) {
				dbLstMnu = new MenuManager(mnuName);
				menuMgr.add(dbLstMnu);
			}

			dbLstMnu.removeAll();

			java.util.List dbLst = PluginHelper.getSortedDocbaseList();
			for (int i = 0; i < dbLst.size(); i++) {
				String name = (String) dbLst.get(i);
				Action showDb = new Action(name, IAction.AS_RADIO_BUTTON) {

					public void run() {
						String dbName = this.getText();
						TreeViewer dbViewer = (TreeViewer) treeViewers
								.get(dbName);
						if (dbViewer != null) {
							Tree tr = dbViewer.getTree();
							treeStackLayout.topControl = tr;
							treeStack.layout();
							if (PluginState.hasIdentity(dbName)) {
								PluginState.setDocbase(dbName);
							}

						}
					}

				};
				if (i == 0) {
					showDb.setChecked(true);
				}
				dbLstMnu.add(showDb);
				// System.out.println("Added action for: " + showDb.getText());
			}

			menuMgr.add(dbLstMnu);
		} catch (Throwable err) {
			MessageDialog.openError(super.getSite().getShell(), "Error",
					err.getMessage());
		}
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
		restoreSessionManagers(memento);
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		saveDocbaseNames(memento);
		saveSessionManagers(memento);
	}

	private void saveDocbaseNames(IMemento memento) {
		// get docbases and save their names
		if (memento != null) {
			Iterator iterNames = this.rootDocbaseNodes.keySet().iterator();
			StringBuffer bufNames = new StringBuffer(32);
			while (iterNames.hasNext()) {
				String name = (String) iterNames.next();
				bufNames.append(name).append(";");
			}
			String names = bufNames.toString();
			memento.putString(DOCBASE_NAMES_KEY, names);

			String curRepo = PluginState.getDocbase();
			if (curRepo != null && curRepo.length() > 0) {
				memento.putString("CURRENT_DOCBASE", curRepo);
			}
		}
	}

	private void restoreDocbaseNames(IMemento memento) {
		// getdocbasenames
		if (memento != null) {
			String names = memento.getString(DOCBASE_NAMES_KEY);
			if (names != null && names.length() > 0) {
				String[] namesArr = names.split(";");
				for (int i = 0; i < namesArr.length; i++) {
					this.addDocbase(namesArr[i]);
				}
			}

			String curRepo = memento.getString("CURRENT_DOCBASE");
			if (curRepo != null && curRepo.length() > 0) {
				PluginState.setDocbase(curRepo);
			}
		}
	}

	private void saveSessionManagers(IMemento memento) {
		try {
			Map<String, DocbaseInfo> a = PluginState.getSessionManagers();

			for(String docbaseName : a.keySet()) {
				DocbaseInfo docbaseInfo = a.get(docbaseName);
				docbaseInfo.prepareForSave();
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(bout);
				objOut.writeObject(docbaseInfo);

				byte[] bts = bout.toByteArray();
				String hname = InetAddress.getLocalHost().getHostName();
				byte[] encrData = BlowfishJC.encryptData(hname, bts);
				String encStr = BlowfishJC.convertBytesToHex(encrData);
				memento.putString("SESSION_MANAGER_" + docbaseName, encStr);
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error serializing session manager", null, ex);
		}
	}

	private void restoreSessionManagers(IMemento memento) {
		try {
			if (memento != null) {
				String keys[] = memento.getAttributeKeys();
				for(String key : keys) {
					if(key.startsWith("SESSION_MANAGER")) {
						String docbaseName = key.substring("SESSION_MANAGER".length() + 1);
						String encStr = memento.getString(key);
						if (encStr != null && encStr.length() > 0) {
							byte[] encrBytes = BlowfishJC.convertHexToBytes(encStr);
							String hostName = InetAddress.getLocalHost().getHostName();
							byte[] decrBytes = BlowfishJC.decryptData(hostName,
									encrBytes);
							ByteArrayInputStream bin = new ByteArrayInputStream(
									decrBytes);
							ObjectInputStream objIn = new ObjectInputStream(bin);
							DocbaseInfo di = (DocbaseInfo) objIn.readObject();
							di.restore();
							PluginState.addExternalIdentity(di.getUserName(), di.getUserPassword(), null, docbaseName, di.getDocbrokerHost(), true, true);
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}