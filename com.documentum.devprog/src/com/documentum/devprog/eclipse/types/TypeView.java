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
 * Created on Jul 19, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.types;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.DocbaseListDialog;
import com.documentum.devprog.eclipse.common.DocbaseLoginDialog;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.properties.PropertiesView;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class TypeView extends ViewPart {
	protected TreeViewer typeTreeViewer = null;

	protected String displayDocbase = null;

	Action refreshNode = null;

	Action addType = null;

	Action dropType = null;

	Action showLoginDialog = null;

	Action addDocbase = null;

	Action removeDocbase = null;

	Action showObjectsWithPolicy = null;

	Action showWorkflowsFromProcess = null;

	Action showRelationsWithType = null;

	Action showMethodForJob = null;

	MenuManager contextMenu = null;

	HashMap rootDocbaseNodes = new HashMap();

	protected AbsoluteRoot rootNode = new AbsoluteRoot();

	TypeTreeLabelProvider labelProv = null;

	TypeTreeContentProvider contProv = null;

	private ArrayList typeTreeExts = null;

	public static final String TREE_EXT_ACTIONS = "typeTreeExtActions";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createPartControl(Composite parent) {
		typeTreeViewer = new TreeViewer(parent);

		Shell sh = parent.getShell();
		contProv = new TypeTreeContentProvider(sh);
		typeTreeViewer.setContentProvider(contProv);
		labelProv = new TypeTreeLabelProvider();
		typeTreeViewer.setLabelProvider(labelProv);
		typeTreeViewer.setSorter(new ViewerSorter());
		typeTreeViewer.addDoubleClickListener(new TypeTreeListener());

		typeTreeViewer.setInput(rootNode);

		// createDocbaseMenu();

		findExtensions();
		createActions();
		createPopupMenu();
		createToolbar();

		// showTypeTree();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {

	}

	protected void createToolbar() {
		IToolBarManager tbMgr = getViewSite().getActionBars()
				.getToolBarManager();
		tbMgr.add(addDocbase);
		tbMgr.add(removeDocbase);
	}

	protected void createActions() {
		refreshNode = new Action("Refresh Node") {
			public void run() {
				Object sel = getSelection();
				if (sel != null) {
					if (sel instanceof TypeInfo) {
						TypeInfo ti = (TypeInfo) sel;
						if (labelProv != null && ti != null) {
							if (PluginHelper.isDFC53()) {
								TBOList53.refreshTBOList(ti.getDocbaseName());
							}
						}
						typeTreeViewer.refresh(ti);
					} else {
						typeTreeViewer.refresh(sel);
					}

				}
			}
		};

		addType = new Action("Add Type ...") {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) typeTreeViewer
						.getSelection();
				if (sel.isEmpty() == false) {
					String parentType = (String) sel.getFirstElement();

					InputDialog newTypeDlg = new InputDialog(TypeView.this
							.getSite().getShell(), "Create New Type",
							"Enter a name for the new type whose super type is "
									+ parentType, "", new TypeNameValidator());
					int status = newTypeDlg.open();
					if (status == InputDialog.OK) {
						String newTypeName = newTypeDlg.getValue();
						TypeView.this.createType(newTypeName, parentType);
					}
				}
			}

		};

		dropType = new Action("Drop Type ...") {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) typeTreeViewer
						.getSelection();
				if (sel.isEmpty() == false) {
					String typeName = (String) sel.getFirstElement();

					boolean status = MessageDialog.openConfirm(TypeView.this
							.getSite().getShell(), "Confirm Drop Type",
							"Are you sure you want to drop(remove) type "
									+ typeName
									+ ". This action cannot be undone");
					if (status == true) {
						dropType(typeName);
					}
				}
			}

		};

		showLoginDialog = new Action("Login") {
			public void run() {

				IStructuredSelection sel = (IStructuredSelection) typeTreeViewer
						.getSelection();
				if (sel.isEmpty()) {
					return;
				}

				Object selElem = getSelection();
				if (selElem instanceof DocbaseNode)

				{
					DocbaseNode dn = (DocbaseNode) selElem;
					DocbaseLoginDialog loginDlg = new DocbaseLoginDialog(
							getSite().getShell());
					loginDlg.setDocbaseName(dn.getDocbase());
					int code = loginDlg.open();
					if (code == DocbaseLoginDialog.OK) {
						typeTreeViewer.expandToLevel(dn, 1);
					}
				}

			}
		};

		addDocbase = new Action("Add Repo") {
			public void run() {
				DocbaseListDialog dld = new DocbaseListDialog(TypeView.this
						.getSite().getShell());
				int status = dld.open();
				if (status == DocbaseListDialog.OK) {
					String selRepo = dld.getSelectedRepo();
					addDocbase(selRepo);
				}
			}
		};
		addDocbase
				.setImageDescriptor(PluginHelper.getImageDesc("add_repo.gif"));
		addDocbase.setToolTipText("Add Repository to the Type Tree");

		removeDocbase = new Action("Remove Repository") {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) typeTreeViewer
						.getSelection();
				if (sel.isEmpty() == false) {
					DocbaseNode ti = (DocbaseNode) sel.getFirstElement();
					String docbaseName = ti.getDocbase();
					removeDocbase(docbaseName);
				}
			}
		};
		removeDocbase.setImageDescriptor(PluginHelper
				.getImageDesc("remove_repo.gif"));
		removeDocbase
				.setToolTipText("Remove Selected Repository from the Type Tree");

		showObjectsWithPolicy = new Action("Show Objects With This Policy") {
			public void run() {
				try {
					StringBuffer bufQuery = new StringBuffer(16);
					bufQuery.append("select * from dm_sysobject where r_policy_id='");

					LifecycleInfo li = (LifecycleInfo) getSelection();
					bufQuery.append(li.getData().getString("r_object_id"));

					bufQuery.append("'");
					String strQuery = bufQuery.toString();

					PluginHelper.showQueryView(strQuery);
				} catch (Exception ex) {
					DfLogger.error(this, "Error showing policy obj", null, ex);
					MessageDialog.openError(TypeView.this.getSite().getShell(),
							"Error Creating Query", ex.getMessage());
				}
			}
		};

		showRelationsWithType = new Action(
				"Show Relations with this Relation Type") {
			public void run() {
				try {
					StringBuffer bufQuery = new StringBuffer(32);
					bufQuery.append("select * from dm_relation where relation_name='");

					RelationTypeInfo ri = (RelationTypeInfo) getSelection();
					String relName = ri.getProperties().getString(
							"relation_name");
					bufQuery.append(relName);

					bufQuery.append("'");
					String strQuery = bufQuery.toString();

					PluginHelper.showQueryView(strQuery);

				} catch (Exception ex) {
					DfLogger.error(this, "Error getting relation name", null,
							ex);
					MessageDialog.openError(TypeView.this.getSite().getShell(),
							"Error Relation Type", ex.getMessage());

				}
			}

		};

		showWorkflowsFromProcess = new Action(
				"Query Workflows Instantiated using this Process") {
			public void run() {
				try {
					StringBuffer bufQuery = new StringBuffer(24);
					bufQuery.append("select * from dm_workflow where process_id='");

					WorkflowInfo wi = (WorkflowInfo) getSelection();
					String id = wi.getProperties().getString("r_object_id");
					bufQuery.append(id);
					bufQuery.append("'");
					String strQuery = bufQuery.toString();

					PluginHelper.showQueryView(strQuery);
				} catch (Exception ex) {
					DfLogger.error(this, "Error getting process id", null, ex);
					MessageDialog.openError(TypeView.this.getSite().getShell(),
							"Workflow Error ", ex.getMessage());

				}

			}
		};

		showMethodForJob = new Action("Show Method For Job") {
			public void run() {
				try {
					JobInfo ji = (JobInfo) getSelection();
					IDfId mi = ji.getMethodId();
					PluginHelper.showPropertiesView(mi.getId());

				} catch (Exception ex) {
					DfLogger.error(this, "Error showing method props", null, ex);
					MessageDialog.openError(TypeView.this.getSite().getShell(),
							"Method Properties", ex.getMessage());
				}
			}
		};

	}

	protected void showTypeTree() {
		// typeTreeViewer.getTree().removeAll();
		typeTreeViewer.setInput("");
	}

	protected void createFirstLevelNodes() {

	}

	protected void createPopupMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager menuMgr) {
				TypeView.this.fillPopupMenu(menuMgr);
			}

		});

		Menu mnu = menuMgr.createContextMenu(typeTreeViewer.getTree());
		typeTreeViewer.getTree().setMenu(mnu);

		getSite().registerContextMenu(menuMgr, typeTreeViewer);
		this.contextMenu = menuMgr;
	}

	protected void fillPopupMenu(IMenuManager menuMgr) {
		// System.out.println("fillPopupMenu called");

		// menuMgr.add(addType);
		// menuMgr.add(dropType);

		Object selObj = getSelection();

		if (selObj != null) {
			menuMgr.add(refreshNode);
		}

		if (selObj instanceof DocbaseNode) {
			menuMgr.add(removeDocbase);
			menuMgr.add(showLoginDialog);
		}

		if (selObj instanceof LifecycleInfo) {
			LifecycleInfo li = (LifecycleInfo) selObj;
			if (li.getNodeType() == LifecycleInfo.LIFECYCLE_NODE) {
				menuMgr.add(showObjectsWithPolicy);
			}

		}

		if (selObj instanceof WorkflowInfo) {
			WorkflowInfo wi = (WorkflowInfo) selObj;
			if (wi.getNodeType() == WorkflowInfo.WF_NODE) {
				menuMgr.add(showWorkflowsFromProcess);
			}
		}

		if (selObj instanceof RelationTypeInfo) {
			RelationTypeInfo ri = (RelationTypeInfo) selObj;
			if (ri.getNodeType() == RelationTypeInfo.REL_NODE) {
				menuMgr.add(showRelationsWithType);
			}
		}

		if (selObj instanceof TypeInfo) {
			TypeInfo ti = (TypeInfo) selObj;

			menuMgr.add(new Separator(TypeView.TREE_EXT_ACTIONS));
			for (int i = 0; i < typeTreeExts.size(); i++) {
				TypeTreeExtension act = (TypeTreeExtension) typeTreeExts.get(i);
				act.setTypeInfo(ti);
				if (act.showAction()) {
					menuMgr.add(act);
				}
			}
		}

		if (selObj instanceof JobInfo) {
			menuMgr.add(showMethodForJob);
		}

		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
				+ "-end"));
	}

	protected void createType(String newType, String superType) {
		if ((displayDocbase == null) || (displayDocbase.length() == 0)) {
			return;
		}

		StringBuffer bufQuery = new StringBuffer(32);
		bufQuery.append("CREATE TYPE ").append(newType)
				.append(" WITH SUPERTYPE ");
		if (superType.equals(TypeTreeContentProvider.PERSISTENT_OBJ)) {
			bufQuery.append("NULL");
		} else {
			bufQuery.append(superType);
		}
		String query = bufQuery.toString();

		IDfQuery queryObj = new DfQuery();
		queryObj.setDQL(query);
		IDfSession sess = null;
		IDfCollection coll = null;
		try {
			sess = PluginState.getSession(displayDocbase);
			coll = queryObj.execute(sess, IDfQuery.EXEC_QUERY);

			System.out.println("Type created successfully");
			typeTreeViewer.add(superType, newType);
			ISelection newSel = new StructuredSelection(newType);
			typeTreeViewer.setSelection(newSel);
			TypeView.showTypePropertiesView(newType);
			MessageDialog
					.openInformation(this.getSite().getShell(),
							"Create type successful",
							"Type created successfully. Edit properties table to add/remove properties");

		} catch (DfException dfe) {
			DfLogger.warn(this, "Error creating type:", null, dfe);
			MessageDialog.openError(this.getSite().getShell(),
					"Error creating type", dfe.getMessage());
		} finally {
			PluginState.releaseSession(sess);

			try {
				if (coll != null)
					coll.close();
			} catch (DfException dfe2) {
			}
		}
	}

	protected void dropType(String typeName) {
		if ((displayDocbase == null) || (displayDocbase.length() == 0)) {
			return;
		}

		StringBuffer bufQuery = new StringBuffer(20);
		bufQuery.append("DROP TYPE ").append(typeName);
		String query = bufQuery.toString();

		IDfSession sess = null;
		IDfCollection coll = null;
		try {
			sess = PluginState.getSession(displayDocbase);
			IDfQuery queryObj = new DfQuery();
			queryObj.setDQL(query);
			coll = queryObj.execute(sess, IDfQuery.EXEC_QUERY);

			typeTreeViewer.remove(typeName);

			MessageDialog.openInformation(this.getSite().getShell(),
					"Drop Type", "Type dropped successfully");

		} catch (DfException dfe) {
			DfLogger.warn(this, "Error dropping type", null, dfe);
			MessageDialog.openError(this.getSite().getShell(),
					"Drop Type Error", dfe.getMessage());
		} finally {
			PluginState.releaseSession(sess);

			try {
				if (coll != null)
					coll.close();
			} catch (DfException dfe2) {
			}
		}
	}

	public static void showTypePropertiesView(Object selObj) {
		try {
			String typeName = (String) selObj;
			// System.out.println(objId);

			IWorkbench wkBench = PlatformUI.getWorkbench();
			IWorkbenchWindow actWin = wkBench.getActiveWorkbenchWindow();
			IWorkbenchPage actPage = actWin.getActivePage();
			PropertiesView viewPart = (PropertiesView) actPage
					.showView(DevprogPlugin.PROP_VIEW_ID);
			// viewPart.setObjectId(objId);
			viewPart.showTypeProperties(typeName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addDocbase(String docbase) {
		if (rootDocbaseNodes.containsKey(docbase) == false) {
			DocbaseNode dn = new DocbaseNode(docbase);
			typeTreeViewer.add(rootNode, dn);
			typeTreeViewer.reveal(dn);
			rootDocbaseNodes.put(docbase, dn);
		}
	}

	public void removeDocbase(String docbase) {
		if (rootDocbaseNodes.containsKey(docbase)) {
			DocbaseNode ti = (DocbaseNode) rootDocbaseNodes.get(docbase);
			typeTreeViewer.remove(ti);
			rootDocbaseNodes.remove(docbase);
		}
	}

	private Object getSelection() {
		IStructuredSelection sel = (IStructuredSelection) typeTreeViewer
				.getSelection();
		if (sel.isEmpty() == false) {
			return sel.getFirstElement();
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
				.getExtensionPoint(DevprogPlugin.TYPE_VIEW_EXT_ID);
		IExtension[] extensions = ep.getExtensions();
		typeTreeExts = new ArrayList();
		for (int i = 0; i < extensions.length; i++) {
			IExtension ext = extensions[i];
			IConfigurationElement[] ce = ext.getConfigurationElements();
			for (int j = 0; j < ce.length; j++) {
				try {
					TypeTreeExtension obj = (TypeTreeExtension) ce[j]
							.createExecutableExtension("class");
					System.out.println("Got extension: " + obj);
					String label = ce[j].getAttribute("label");
					obj.setText(label);
					obj.setTypeTreeViewer(typeTreeViewer);
					obj.setShell(getSite().getShell());
					typeTreeExts.add(obj);
				} catch (CoreException cex) {
					DfLogger.warn(this, "Error loading extensions", null, cex);
				}
			}
		}
	}

	class TypeNameValidator implements IInputValidator {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
		 */
		public String isValid(String newText) {
			if (newText.startsWith("dm_") || (newText.startsWith("dmi_"))) {
				String errMsg = "Custom type names should not start with dm_ or dmi_.";
				return errMsg;
			}
			return null;
		}
	}
}