/*
 * Created on Sep 10, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.types;

import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfType;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.DocbaseLoginDialog;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class QuickTypeHierarchyView extends TypeView {

	private Object parent = null;
	private TypeInfo typeRoot = null;

	protected Action setType = null;

	public void showHierarchy(String type, String repoName,
			boolean showAncestors) {
		final String typeName = type;
		final String docbase = repoName;
		final boolean showAnc = showAncestors;

		Job jb = new Job("Show Type Tree") {
			public IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Creating Type Tree",
						IProgressMonitor.UNKNOWN);

				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						typeTreeViewer
								.setInput(QuickTypeHierarchyView.this.rootNode);
						typeTreeViewer.add(
								QuickTypeHierarchyView.this.rootNode,
								new TempLoadingNode());
					}

				});

				if (showAnc) {
					final ArrayList ancList = new ArrayList();
					ancList.add(typeName);
					IDfSession sess = null;
					try {
						sess = PluginState.getSession(docbase);
						IDfType type = sess.getType(typeName);
						IDfType superType = type.getSuperType();
						while (superType != null) {
							String sname = superType.getName();
							System.out.println("sname: " + sname);
							ancList.add(sname);

							superType = superType.getSuperType();
						}

						int topIndex = ancList.size() - 1;
						String rootType = (String) ancList.get(topIndex);
						// System.out.println("Got root type: " + rootType);
						parent = QuickTypeHierarchyView.this.rootNode;
						if (topIndex == 0) {
							typeRoot = new TypeInfo(rootType,
									TypeInfo.OBJECT_TYPE, docbase);
							// System.out.println("created TypeInfo since indx = 0");
						} else {
							typeRoot = new QuickTypeInfo(rootType,
									TypeInfo.OBJECT_TYPE, docbase);
							((QuickTypeInfo) typeRoot).setTypeTreePath(ancList);
							if (typeRoot.hasTBO(docbase, rootType)) {
								typeRoot.setTBO(true);
							}
							// System.out.println("Create qti since indx > 0");

						}

						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								typeTreeViewer.setInput(parent);
								typeTreeViewer.add(parent, typeRoot);
								System.out.println("added typeroot" + typeRoot);
								typeTreeViewer.expandToLevel(ancList.size());
								// typeTreeViewer.expandAll();

							}// run()
						});// Display

						monitor.done();
						return Status.OK_STATUS;
					} catch (Exception ex) {
						DfLogger.error(this, "Error Generating Type Tree",
								null, ex);
						return new Status(IStatus.ERROR,
								DevprogPlugin.PLUGIN_ID, IStatus.ERROR,
								"Error Generating Type Tree", ex);
					} finally {
						PluginState.releaseSession(sess);
					}
				}// if(showAncestor)
				else {
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							typeTreeViewer.add(
									QuickTypeHierarchyView.this.rootNode,
									new TypeInfo(typeName,
											TypeInfo.OBJECT_TYPE, docbase));
						}
					});

					return Status.OK_STATUS;
				}
			}

		};// Job

		jb.setUser(true);
		jb.schedule();

		/*
		 * try { PlatformUI.getWorkbench().getProgressService().r } catch
		 * (Exception ex) { DfLogger.error(this, "Error Generating Type Tree",
		 * null, ex); MessageDialog.openError(QuickTypeHierarchy.this.getSite()
		 * .getShell(), "Type Tree Error", "Error Generating Type Hierarchy: " +
		 * ex.getMessage()); }
		 */

	}

	protected void createToolbar() {
		setType = new Action("Set Type") {
			public void run() {
				Shell sh = QuickTypeHierarchyView.this.getSite().getShell();
				InputDialog id = new InputDialog(sh, "Type Name",
						"Show Type Hierarchy For", "", null);
				int status = id.open();
				if (status == InputDialog.OK) {
					String val = id.getValue();
					if (val != null && val.length() > 0) {
						String docbase = PluginState.getDocbase();
						if (docbase == null || docbase.length() == 0) {
							DocbaseLoginDialog dld = new DocbaseLoginDialog(sh);
							int ds = dld.open();
							if (ds == DocbaseLoginDialog.OK) {
								docbase = dld.getDocbaseName();
							}
						}
						QuickTypeHierarchyView.this.showHierarchy(val, docbase,
								true);
					}
				}
			}
		};
		setType.setImageDescriptor(PluginHelper.getImageDesc("hierarchy.gif"));
		setType.setToolTipText("Set New Type");

		IToolBarManager tbMgr = getViewSite().getActionBars()
				.getToolBarManager();
		tbMgr.add(setType);

	}

}
