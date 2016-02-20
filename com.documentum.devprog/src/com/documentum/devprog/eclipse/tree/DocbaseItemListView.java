/*
 * Created on Apr 12, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.common.PreferenceConstants;
import com.documentum.fc.common.DfLogger;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import java.util.*;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DocbaseItemListView extends ViewPart implements
		IPropertyChangeListener {

	private String folderId = null;

	private DocbaseListComposite listViewer;

	private Text txtSearch;

	private Composite searchComposite;

	private Action showHistory;

	private Action goBack = null;

	private Action goForward = null;

	private Action goUp = null;

	private DumpPropertiesAction dumpProperties = null;

	private ArrayList navHistory = new ArrayList(25);

	private int curNavPointer = 0;

	private List repoExtensions = new ArrayList();

	private boolean columnsChanged = false;

	private static Map objIdToNodeMap = new HashMap();

	private QueryJob typeQueryJob = null;

	private Timer queryJobTimer = new Timer();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createPartControl(Composite parent) {
		Composite parentComp = new Composite(parent, SWT.NONE);
		parentComp.setLayout(new FormLayout());

		createSearchBox(parentComp);
		createTable(parentComp);

		findExtensions();
		createActions();
		createPopupMenu(listViewer.getListViewer());
		createToolbar();

		DevprogPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(this);
	}

	private void createSearchBox(Composite parent) {
		searchComposite = new Composite(parent, SWT.NONE);

		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(7, -5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		searchComposite.setLayoutData(fd);

		searchComposite.setLayout(new FormLayout());
		// searchComposite.setBackground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		txtSearch = new Text(searchComposite, SWT.FLAT);
		txtSearch.setBackgroundImage(PluginHelper.getImage("searchBg.gif"));
		txtSearch.setBackground(new Color(Display.getDefault(), 250, 250, 254));
		// txtSearch.setBackground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		Font oldFont = txtSearch.getFont();
		Font newFont = PluginHelper.changeFontStyle(txtSearch.getDisplay(),
				oldFont, SWT.BOLD);
		txtSearch.setFont(newFont);

		// txtSearch.setSize(800,300);
		FormData boxFd = new FormData();
		boxFd.top = new FormAttachment(0, 5);
		boxFd.bottom = new FormAttachment(100, -5);
		boxFd.left = new FormAttachment(70, 5);
		boxFd.right = new FormAttachment(100, -5);
		txtSearch.setLayoutData(boxFd);

		txtSearch.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				// System.out.println("mod text called");
				queryJobTimer.cancel();
				if (txtSearch.getText().trim().length() > 0) {

					// System.out.println("selNode: " +
					// DocbaseItemListView.this.getSelectedItem());
					queryJobTimer = new Timer();
					queryJobTimer.schedule(
							new QueryJobTimerTask(txtSearch.getText(),
									PluginHelper.getRepoTree()
											.getSelectedNode()), 1500);
				}

			}

		});

		txtSearch.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				// e.gc.setXORMode(true);
				// e.gc.drawRoundRectangle(e.x+1, e.y+1, e.width-2,e.height-2 ,5
				// ,5
				// );

			}
		});
	}

	private void createTable(Composite parent) {
		/*
		 * listViewer = new TableViewer(parent);
		 * 
		 * //listViewer.getTable().setLinesVisible(true);
		 * listViewer.getTable().setHeaderVisible(true);
		 * 
		 * //listViewer.setColumnProperties(getVisibleColumns()); Table tbl =
		 * listViewer.getTable();
		 * 
		 * //TODO Have to make this a class ColumnSpecs. This will make it
		 * simpler to add/remove columns TableColumn tc = new
		 * TableColumn(tbl,SWT.LEFT,0); tc.setText("Name"); tc.setWidth(200);
		 * tc.setResizable(true);
		 * 
		 * TableColumn tc2 = new TableColumn(tbl,SWT.LEFT,1);
		 * tc2.setText("Modified"); tc2.setWidth(100); tc2.setResizable(true);
		 * 
		 * TableColumn tc3 = new TableColumn(tbl,SWT.LEFT,2);
		 * tc3.setText("Size"); tc3.setWidth(120); tc3.setResizable(true);
		 * 
		 * TableColumn tc5 = new TableColumn(tbl,SWT.LEFT,3);
		 * tc5.setText("Content Type"); tc5.setWidth(120);
		 * tc5.setResizable(true);
		 * 
		 * TableColumn tc4 = new TableColumn(tbl,SWT.LEFT,4);
		 * tc4.setText("Modifier"); tc4.setWidth(120); tc4.setResizable(true);
		 * 
		 * FormData fd = new FormData(); fd.left = new FormAttachment(0,5);
		 * fd.right = new FormAttachment(100,-5); fd.top = new
		 * FormAttachment(searchComposite,5); fd.bottom = new
		 * FormAttachment(100,-5); listViewer.getTable().setLayoutData(fd);
		 * 
		 * labelProvider = new DocbaseItemListLabelProvider();
		 * listViewer.setLabelProvider(labelProvider);
		 * 
		 * listViewer.setContentProvider(new IStructuredContentProvider(){
		 * 
		 * public Object[] getElements(Object inputElement) { return new
		 * Object[]{}; }
		 * 
		 * public void dispose() {}
		 * 
		 * public void inputChanged(Viewer viewer, Object oldInput, Object
		 * newInput) {}
		 * 
		 * });
		 * 
		 * listViewer.addDoubleClickListener(new
		 * DocbaseItemListEventListener());
		 */

		listViewer = new DocbaseListComposite(parent, SWT.NONE);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		fd.top = new FormAttachment(searchComposite, 5);
		fd.bottom = new FormAttachment(100, -5);
		listViewer.setLayoutData(fd);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	public void setFolderId(String objId) {
		this.folderId = objId;
	}

	public String getFolderId() {
		return folderId;
	}

	public void showFolderContents(String objId, boolean isNew) {
		showFolderContents(objId, null, isNew);
	}

	public void showFolderContents(String objId, String selId, boolean isNew) {
		setFolderId(objId);
		/*
		 * ListViewHelper.resetCollections();
		 * ListViewHelper.showSingleViewFolderContentsAll(objId);
		 * 
		 * IDfCollection folderColl = ListViewHelper.getFolderCollection();
		 * IDfCollection docColl = ListViewHelper.getDocumentCollection();
		 */

		System.out.println("View:ShowFolderContents");

		if (this.areColumnsChanged()) {
			listViewer.resetTable();
			this.setColumnsChanged(false);
		} else {
			listViewer.getListViewer().setInput(new Object());
			System.out.println("just reset input");
		}

		removeAll(); // clear hash map of object ids.

		if (isNew) {
			addToNavHistory(objId);
		}

		ListFiller filler = new ListFiller(objId, listViewer.getListViewer());
		if (selId != null && selId.length() > 0) {
			filler.setSelectionId(selId);
		}
		System.out.println("init filler and about to run");
		new Thread(filler).run();

	}

	public static void putNode(String objId, DocbaseItem item) {
		objIdToNodeMap.put(objId, item);
	}

	public static DocbaseItem getNode(String objId) {
		return (DocbaseItem) objIdToNodeMap.get(objId);
	}

	public static void remove(String objId) {
		objIdToNodeMap.remove(objId);
	}

	public static void removeAll() {
		objIdToNodeMap.clear();
	}

	/**
	 * Create the toolbar for the view.
	 * 
	 */
	protected void createToolbar() {
		IToolBarManager tbMgr = getViewSite().getActionBars()
				.getToolBarManager();

		tbMgr.add(goBack);
		tbMgr.add(goForward);
		// tbMgr.add(goUp);

	}

	protected void createPopupMenu(TableViewer viewer) {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager menuMgr) {
				DocbaseItemListView.this.fillPopupMenu(menuMgr);
			}

		});

		Menu mnu = menuMgr.createContextMenu(viewer.getTable());
		viewer.getTable().setMenu(mnu);

		getSite().registerContextMenu(menuMgr, viewer);

	}

	protected void fillPopupMenu(IMenuManager menuMgr) {
		// System.out.println("fill popupmenu called");
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		menuMgr.add(dumpProperties);
		// menuMgr.add(showHistory);
		// Fill in the extension actions

		menuMgr.add(new Separator(DocbaseTreeView.TREE_EXT_ACTIONS));
		for (int i = 0; i < repoExtensions.size(); i++) {
			RepoTreeExtension act = (RepoTreeExtension) repoExtensions.get(i);
			act.setDocbaseItem(this.getSelectedItem());
			act.setRepoTreeViewer(PluginHelper.getRepoTreeViewer());
			act.setObjectListViewer(PluginHelper.getObjectListViewer());
			if (act.showAction()) {
				menuMgr.add(act);

			}
		}

		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
				+ "-end"));
	}

	private void createActions() {

		dumpProperties = new DumpPropertiesAction(listViewer.getListViewer());
		dumpProperties
				.setImageDescriptor(PluginHelper.getImageDesc("info.gif"));
		dumpProperties.setText("Show Properties");

		goBack = new Action() {
			public void run() {
				goBack();
			}

		};
		goBack.setImageDescriptor(PluginHelper.getImageDesc("backward_nav.gif"));

		goUp = new Action() {

		};
		goUp.setImageDescriptor(PluginHelper.getImageDesc("up_nav.gif"));

		goForward = new Action() {
			public void run() {
				goForward();
			}
		};
		goForward.setImageDescriptor(PluginHelper
				.getImageDesc("forward_nav.gif"));
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
					// Commenting these out as there is no guaratee that the
					// widgets are created when the calls below are made.
					// obj.setObjectListViewer(listViewer.getListViewer());
					// obj.setRepoTreeViewer(PluginHelper.getRepoTreeViewer());
					obj.setShell(getSite().getShell());
					repoExtensions.add(obj);
				} catch (CoreException cex) {
					DfLogger.warn(this, "Error loading extensions", null, cex);
				}
			}
		}
	}

	public boolean areColumnsChanged() {
		return columnsChanged;
	}

	public void setColumnsChanged(boolean columnsChanged) {
		this.columnsChanged = columnsChanged;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Preferences.IPropertyChangeListener#propertyChange
	 * (org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public void propertyChange(
			org.eclipse.core.runtime.Preferences.PropertyChangeEvent event) {
		// System.out.println("propChanged");
		String propName = event.getProperty();
		// System.out.println("prop name: " + propName);
		if (propName.equals(PreferenceConstants.P_VISIBLE_COLUMNS)) {
			setColumnsChanged(true);
		}

	}

	public TableViewer getObjectListViewer() {
		return listViewer.getListViewer();
	}

	DocbaseItem getSelectedItem() {
		IStructuredSelection sel = (IStructuredSelection) listViewer
				.getListViewer().getSelection();
		// System.out.println("sel:isEmpty " + sel.isEmpty());
		if (sel.isEmpty() == false) {
			Object obj = sel.getFirstElement();
			// System.out.println("selObj " + obj);
			if (obj instanceof DocbaseItem) {
				return (DocbaseItem) obj;
			}

		}
		return null;
	}

	public void clearContents() {
		listViewer.getListViewer().setInput(new Object());
		this.setFolderId("00");
	}

	class QueryJobTimerTask extends TimerTask {
		String terms = "";

		DocbaseItem selNode = null;

		QueryJobTimerTask(String terms, DocbaseItem sn) {
			this.terms = terms;
			selNode = sn;
		}

		public void run() {
			System.out.println("About to run QueryJob");

			if (PluginState.getDocbase() != null) {
				if (typeQueryJob != null) {
					typeQueryJob.cancel();
				}
				if (DocbaseItemListView.this.areColumnsChanged()) {
					listViewer.resetTable();
					DocbaseItemListView.this.setColumnsChanged(false);
				}
				typeQueryJob = new QueryJob();
				typeQueryJob.setResultsViewer(listViewer.getListViewer());
				typeQueryJob.setSearchString(terms);
				if (selNode != null) {
					typeQueryJob.setSelectedNode(selNode);
				} else {
					typeQueryJob.setFolderId(DocbaseItemListView.this
							.getFolderId());
				}
				typeQueryJob.schedule();
			}
		}
	};

	private void addToNavHistory(String objectId) {
		this.navHistory.add(objectId);

	}

	private void goForward() {
		if (navHistory.size() == 0) {
			return;
		}

		if (curNavPointer == (navHistory.size() - 1)) {
			curNavPointer = 0;
		} else {
			curNavPointer++;
		}
		String id = (String) navHistory.get(curNavPointer);
		this.showFolderContents(id, false);
	}

	private void goBack() {
		if (navHistory.size() == 0) {
			return;
		}

		if (curNavPointer == 0) {
			curNavPointer = navHistory.size() - 1;
		} else {
			curNavPointer--;
		}

		String id = (String) navHistory.get(curNavPointer);
		this.showFolderContents(id, false);
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	}

	public void saveState(IMemento memento) {

		super.saveState(memento);
	}
}
