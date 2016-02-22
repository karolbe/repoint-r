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
 * Created on Jul 20, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.query;

import com.documentum.devprog.common.UtilityMethods;
import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.*;
import com.documentum.devprog.eclipse.libraryfunc.ACLDialog;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.xml.xdql.DfXmlQuery;
import com.documentum.xml.xdql.IDfXmlQuery;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.ui.progress.IProgressService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class QueryView extends ViewPart {
	Clipboard clipboard = null;

	SashForm sashParent = null;

	Composite queryComposite = null;

	StyledText queryText = null;

	// radio buttons for query options
	Group optParent = null;

	Button readQuery = null;

	Button execQuery = null;

	Button applyQuery = null;

	Button execReadQuery = null;

	// Buttons that allow you to run a query.
	Composite butParent = null;

	Button runDQL = null;

	Button runXDQL = null;

	// XDQL Options
	Group optXDQL = null;

	Button butXOptions = null;

	Menu optionsMenu = null;

	Button followAssembly = null;

	Button virtualDocumentNested = null;

	Button useLowerCase = null;

	Button repeatingAsNested = null;

	Button repeatingIncludeIndex = null;

	Button metaDataAsAttributes = null;

	Button contentAsLink = null;

	Button includeContent = null;

	//
	TabFolder optionTabs = null;

	TabItem dqlTab = null;

	TabItem xdqlTab = null;

	// Results
	Composite resultsParent = null;

	StackLayout resultsLayout = null;

	Table resultsTable = null;

	TableViewer resultsViewer = null;

	TableCursor resultsCursor = null;

	Browser xdqlResults = null;

	Text resultsSummary = null;

	Action dumpProperties = null;

	Action copyAction = null;

	Action executeDQLAsync = null;

	Action execyteXDQLAsync = null;

	Action showPaths = null;

	EnableKbHistoryNav enableKeyboardScroll = null;

	// Old queries
	MRUQueue oldQueryList = null;

	int maxOldQueries = 25;

	int curIndex = 0;

	PopupList oldQueryListBox = null;

	Action showOldQueries = null;

	Action prepareCSVFile = null;

	Action selectRepo = null;

	Action addToFavorites = null;

	Action showACL = null;

	private String queryDocbase = null;

	private SortedMap favoriteQueries = new TreeMap();

	final private static String KEY_FAV_QUERIES = Messages
			.getString("QueryView.1"); //$NON-NLS-1$

	private HashSet queryKeywords = new HashSet();

	private Color keywordsColor = null;

	private Color typeColor = null;

	private Map typeLists = new HashMap();

	private Menu mnuOptions;

	private Composite mnuOptionsComp;

	private Object dqlExecutorJobFamily = new Object();

	private DQLQueryExecutor dqlQueryExec;

	private Composite optionsComposite;

	private ExpandItem eiQueryCfg;

	private ExpandItem eiResults;

	private ExpandBar expandBar;

	private Composite resultsContainer;

	private String enable_kb_hist_nav = "ENABLE_KB_HIST_NAV";

	private Image oldQueryTextImage;

	private Color qtBgColor1 = null;

	private Color qtBgColor2 = null;

	protected String getQueryDocbase() {
		return queryDocbase;
	}

	protected void setQueryDocbase(String qd) {
		queryDocbase = qd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createPartControl(Composite parent) {
		// parent.setLayout(new FormLayout());

		createUI(parent);
		createActions();
		createToolbar();
		createPopupMenu();
		createMenus();

	}

	protected void createUI(Composite parent) {
		clipboard = new Clipboard(parent.getDisplay());

		expandBar = new ExpandBar(parent, SWT.V_SCROLL);
		expandBar.setSpacing(5);
		expandBar.addExpandListener(new ExpandListener() {

			public void itemCollapsed(ExpandEvent e) {
				Display.getDefault().asyncExec(new Runnable() {

					public void run() {
						computeResultsHeight();
					}
				});
			}

			public void itemExpanded(ExpandEvent e) {
				Display.getDefault().asyncExec(new Runnable() {

					public void run() {
						computeResultsHeight();
					}
				});
			}

		});

		queryComposite = new Composite(expandBar, SWT.NONE);
		queryComposite.setLayout(new FormLayout());
		createQueryBox(queryComposite);

		// createOptionsMenu(queryComposite);
		createOptionTabFolder(queryComposite);
		createDQLOptions(optionTabs);
		createXDQLOptions(optionTabs);

		// createOptionsComposite(queryComposite);
		// createDQLOptionsExpandBar(optionsComposite);
		// createXDQLOptionsExpandBar(optionsComposite);

		createActionButtons(queryComposite);

		eiQueryCfg = new ExpandItem(expandBar, SWT.NONE, 0);
		eiQueryCfg.setText("Query Configuration");
		eiQueryCfg.setControl(queryComposite);
		eiQueryCfg.setHeight(queryComposite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y);
		eiQueryCfg.setExpanded(true);

		createResultsUI(expandBar);

		eiResults = new ExpandItem(expandBar, SWT.NONE, 1);
		eiResults.setText("Results");
		eiResults.setControl(resultsContainer);

		eiResults.setExpanded(false);

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				computeResultsHeight();
			}
		});

		// sashParent.setWeights(new int[] { 30, 70 });
	}

	void computeResultsHeight() {
		// int total = (int)(Display.getDefault().getBounds().height * 0.6);
		int total = expandBar.getClientArea().height;
		System.out.println("Total: " + total);

		int curHeight = 0;
		curHeight += eiQueryCfg.getHeaderHeight();
		curHeight += eiResults.getHeaderHeight();

		curHeight += 3 * expandBar.getSpacing();

		if (eiQueryCfg.getExpanded()) {
			curHeight += eiQueryCfg.getHeight();
		}

		int resultsHeight = total - curHeight;
		System.out.println("resultsHt: " + resultsHeight);
		eiResults.setHeight(resultsHeight);
	}

	protected void createMenus() {
		IMenuManager menuMgr = getViewSite().getActionBars().getMenuManager();
		IMenuManager favMnu = new MenuManager(
				Messages.getString("QueryView.MNU_FAVORITES")); //$NON-NLS-1$
		menuMgr.add(favMnu);
		favMnu.add(addToFavorites);
		favMnu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		favMnu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
				+ Messages.getString("QueryView.3"))); //$NON-NLS-1$

		favMnu.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				mgr.removeAll();
				fillFavMenu(mgr);
			}
		});
	}

	private void fillFavMenu(IMenuManager mgr) {
		mgr.add(addToFavorites);
		Iterator iter = favoriteQueries.keySet().iterator();
		while (iter.hasNext()) {
			String desc = (String) iter.next();
			Action act = new Action(desc) {
				public void run() {
					String query = (String) favoriteQueries.get(this.getText());
					queryText.setText(query);

					eiQueryCfg.setExpanded(false);
					eiResults.setExpanded(true);
					computeResultsHeight();
				}
			};
			mgr.add(act);
		}
	}

	protected void createPopupMenu() {
		MenuManager menuMgr = new MenuManager(Messages.getString("QueryView.4")); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuMgr) {
				fillPopupMenu(menuMgr);
			}

		});

		Menu mnu = menuMgr.createContextMenu(resultsCursor);
		resultsCursor.setMenu(mnu);
		resultsViewer.getTable().setMenu(mnu);

		getSite().registerContextMenu(menuMgr, resultsViewer);

	}

	protected void fillPopupMenu(IMenuManager menuMgr) {
		// System.out.println("fillPopupMenu called");
		menuMgr.add(copyAction);
		menuMgr.add(dumpProperties);
		menuMgr.add(showPaths);
		menuMgr.add(showACL);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
				+ Messages.getString("QueryView.5"))); //$NON-NLS-1$
	}

	protected void createToolbar() {
		IToolBarManager tbMgr = getViewSite().getActionBars()
				.getToolBarManager();
		tbMgr.add(selectRepo);
		tbMgr.add(showOldQueries);
		tbMgr.add(prepareCSVFile);
		tbMgr.add(addToFavorites);
		tbMgr.add(executeDQLAsync);
		tbMgr.add(this.enableKeyboardScroll);
	}

	protected void createActions() {
		dumpProperties = new Action(
				Messages.getString("QueryView.ACT_DUMP_PROPS")) //$NON-NLS-1$
		{
			public void run() {
				dumpProperties(getSelectedData());
			}
		};

		showACL = new Action(
				Messages.getString("QueryView.ACT_SHOW_ACL")) //$NON-NLS-1$
		{
			public void run() {
				showACL(getSelectedData());
			}
		};

		showOldQueries = new Action(
				Messages.getString("QueryView.ACT_OLD_QUERIES")) //$NON-NLS-1$
		{
			public void run() {
				String[] oldQLst = getOldQueryList();
				if (oldQLst == null || oldQLst.length == 0) {
					return;
				}
				Display curDisp = Display.getCurrent();
				Point curLocation = curDisp.getCursorLocation();
				// System.out.println("x: " + curLocation.x + ": y=" +
				// curLocation.y);
				Rectangle rect = new Rectangle(curLocation.x, curLocation.y,
						500, 10);
				oldQueryListBox = new PopupList(QueryView.this.getSite()
						.getShell(), SWT.H_SCROLL | SWT.V_SCROLL);

				oldQueryListBox.setItems(getOldQueryList());
				String selected = oldQueryListBox.open(rect);
				if (selected != null && (selected.length() > 0)) {
					QueryView.this.queryText.setText(selected);
				}
			}
		};
		showOldQueries.setImageDescriptor(PluginHelper.getImageDesc(Messages
				.getString("QueryView.8"))); //$NON-NLS-1$
		showOldQueries.setToolTipText(Messages
				.getString("QueryView.TIP_OLD_QUERIES")); //$NON-NLS-1$

		prepareCSVFile = new Action(
				Messages.getString("QueryView.ACT_SAVE_RESULTS")) //$NON-NLS-1$
		{
			public void run() {
				/*
				 * This is not relevant anymore as the new results are streamed
				 * to the viewer and do not use the old LinkedList object
				 * LinkedList lstTObjs = (LinkedList) resultsViewer.getInput();
				 * if (lstTObjs == null) { return; }
				 */

				FileDialog fdlg = new FileDialog(QueryView.this.getSite()
						.getShell(), SWT.SAVE | SWT.SINGLE);
				fdlg.setFilterExtensions(new String[] { Messages
						.getString("QueryView.11") }); //$NON-NLS-1$
				String filename = fdlg.open();

				if (filename != null) {
					try {

						if (filename.endsWith(Messages
								.getString("QueryView.12")) == false) //$NON-NLS-1$
						{
							filename += Messages.getString("QueryView.13"); //$NON-NLS-1$
						}

						File fl = new File(filename);
						if (fl.exists()) {
							boolean ok = MessageDialog
									.openConfirm(
											QueryView.this.getSite().getShell(),
											Messages.getString("QueryView.DLG_FILE_OVERWRITE"), Messages.getString("QueryView.15") + filename + Messages.getString("QueryView.16")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							if (ok == false) {
								return;
							}
						}

						CSVFilePreparer filePrep = new CSVFilePreparer(
								resultsViewer, filename);
						IProgressService progServ = PlatformUI.getWorkbench()
								.getProgressService();
						// progServ.busyCursorWhile(filePrep);
						progServ.runInUI(QueryView.this.getSite()
								.getWorkbenchWindow(), filePrep, null);
						Exception ex = filePrep.getException();
						if (ex != null) {
							MessageDialog
									.openError(
											QueryView.this.getSite().getShell(),
											Messages.getString("QueryView.DLG_TITLE_SAVE_RESULTS"), ex //$NON-NLS-1$
													.getMessage());
						} else {
							MessageDialog
									.openInformation(
											QueryView.this.getSite().getShell(),
											"CSV File Preparation", Messages.getString("QueryView.DLG_MSG_SAVE_RESULTS_SUCCESS")); //$NON-NLS-1$ //$NON-NLS-2$                            
						}

					} catch (Exception ex) {
						MessageDialog.openError(QueryView.this.getSite()
								.getShell(), "CSV File Preparation", ex //$NON-NLS-1$
								.getMessage());
					}
				}

			}
		};
		prepareCSVFile.setImageDescriptor(PluginHelper
				.getImageDesc(CommonConstants.SAVE_ICON));
		prepareCSVFile.setToolTipText(Messages
				.getString("QueryView.TTIP_SAVE_RESULTS")); //$NON-NLS-1$

		selectRepo = new Action(Messages.getString("QueryView.ACT_SELECT_REPO")) //$NON-NLS-1$
		{
			public void run() {
				DocbaseListDialog dld = new DocbaseListDialog(getSite()
						.getShell());
				int status = dld.open();
				if (status == DocbaseListDialog.OK) {
					String selRepo = dld.getSelectedRepo();
					if (PluginState.hasIdentity(selRepo) == false) {
						DocbaseLoginDialog loginDlg = new DocbaseLoginDialog(
								getSite().getShell());
						loginDlg.setDocbaseName(selRepo);
						int st = loginDlg.open();
						if (st == DocbaseLoginDialog.CANCEL) {
							// OK means that user got successfully authenticated
							return;
						}
					}
					PluginState.setDocbase(selRepo);
					QueryView.this.setContentDescription("Current Repository: "
							+ selRepo);
					this.setToolTipText(Messages
							.getString("QueryView.TTIP_SELECT_REPO") + selRepo); //$NON-NLS-1$

				}

			}
		};
		selectRepo.setImageDescriptor(PluginHelper.getImageDesc(Messages
				.getString("QueryView.24"))); //$NON-NLS-1$
		String initTt = Messages.getString("QueryView.25"); //$NON-NLS-1$
		String curDb = PluginState.getDocbase();
		if (curDb != null && curDb.length() > 0) {
			initTt += Messages.getString("QueryView.26") + curDb; //$NON-NLS-1$
		}
		selectRepo.setToolTipText(initTt);

		copyAction = new Action(Messages.getString("QueryView.ACT_COPY")) //$NON-NLS-1$
		{
			public void run() {
				String selText = getSelectedData();
				TextTransfer tt = TextTransfer.getInstance();
				clipboard.setContents(new String[] { selText },
						new TextTransfer[] { tt });
			}
		};

		addToFavorites = new Action(
				Messages.getString("QueryView.ACT_ADD_TO_FAV")) //$NON-NLS-1$
		{
			public void run() {
				String query = queryText.getText();
				if (query != null && query.length() > 0) {
					InputDialog id = new InputDialog(
							QueryView.this.getSite().getShell(),
							Messages.getString("QueryView.DLG_TITLE_FAV_NAME"), Messages.getString("QueryView.DLG_MSG_FAV_NAME"), "", new FavNameValidator(favoriteQueries)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					int status = id.open();
					if (status == InputDialog.OK) {
						String desc = id.getValue();
						favoriteQueries.put(desc, query);
					}
				}

			}
		};
		addToFavorites.setImageDescriptor(PluginHelper.getImageDesc(Messages
				.getString("QueryView.IMG_FAV_ACTION"))); //$NON-NLS-1$
		addToFavorites.setToolTipText(Messages
				.getString("QueryView.TTIP_ADD_TO_FAV")); //$NON-NLS-1$

		executeDQLAsync = new Action("Execute Async") {
			public void run() {
				executeDQL(true);
			}
		};
		executeDQLAsync.setImageDescriptor(PluginHelper
				.getImageDesc("async_dql.gif"));
		executeDQLAsync.setToolTipText("Execute DQL Query in Background");

		showPaths = new Action("Show Paths") {
			public void run() {
				String sel = getSelectedData();
				IDfId objId = new DfId(sel);
				if (objId.isObjectId()) {
					IDfSession sess = null;
					try {
						sess = PluginState.getSessionById(objId);
						IDfSysObject sobj = (IDfSysObject) sess
								.getObject(objId);
						String[] paths = UtilityMethods.getPaths(sobj, true);
						SimpleListDialog sld = new SimpleListDialog(
								QueryView.this.getSite().getShell(), paths,
								"Paths: " + sobj.getObjectName());
						sld.open();
					} catch (Exception ex) {
						DfLogger.error(this, "Error getting paths", null, ex);
						MessageDialog.openError(QueryView.this.getSite()
								.getShell(), "Paths Error", ex.getMessage());
					} finally {

					}

				}
			}
		};

		this.enableKeyboardScroll = new EnableKbHistoryNav(
				"Enable Keyboard History Scroll");
		this.enableKeyboardScroll.setImageDescriptor(PluginHelper
				.getImageDesc("history_nav.gif"));
		Preferences prefs = DevprogPlugin.getDefault().getPluginPreferences();
		String strBool = prefs.getString(this.enable_kb_hist_nav);
		boolean bl = prefs.getBoolean(this.enable_kb_hist_nav);
		this.enableKeyboardScroll.setChecked(bl);

	}

	/**
	 * Gets the selected data from the TableCursor.
	 * 
	 * @return
	 */
	protected String getSelectedData() {
		try {
			if (resultsCursor != null) {
				TableItem row = resultsCursor.getRow();
				int col = resultsCursor.getColumn();
				if (row != null) {
					String selText = row.getText(col);
					return selText;
				}
			}
		} catch (Exception ex) {
			DfLogger.info(this, Messages.getString("QueryView.34"), null, ex); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {

	}

	/**
	 * Creates the text box where the query is entered.
	 * 
	 * @param parent
	 */
	protected void createQueryBox(Composite parent) {
		queryText = new StyledText(parent, SWT.MULTI | SWT.V_SCROLL);
		queryText.setWordWrap(true);

		if (qtBgColor1 == null) {
			qtBgColor1 = new Color(Display.getDefault(), 250, 250, 255);
		}
		if (qtBgColor2 == null) {
			qtBgColor2 = new Color(Display.getDefault(), 235, 235, 240);
		}
		// queryText.setBackground();

		queryText.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				Display display = queryText.getDisplay();
				Rectangle rect = queryText.getClientArea();
				Image newImage = new Image(display, 1, Math.max(1, rect.height));
				GC gc = new GC(newImage);
				/*
				 * gc.setForeground (qtBgColor2); gc.setBackground (qtBgColor1);
				 * gc.fillGradientRectangle (rect.x, rect.y, 1, rect.height/2,
				 * true);
				 */

				gc.setForeground(qtBgColor1);
				gc.setBackground(qtBgColor2);
				// gc.fillGradientRectangle(rect.x,rect.height/2,1,
				// rect.height/2,true);
				gc.fillGradientRectangle(rect.x, rect.y, 1, rect.height, true);

				gc.dispose();
				// newImage.getImageData().alpha = 128;
				queryText.setBackgroundImage(newImage);
				if (oldQueryTextImage != null)
					oldQueryTextImage.dispose();
				oldQueryTextImage = newImage;
			}
		});

		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(70, -5);
		fd.right = new FormAttachment(100, -5);
		fd.left = new FormAttachment(0, 5);
		queryText.setLayoutData(fd);

		// set default font
		Font oldFont = queryText.getFont();
		FontData fdata[] = oldFont.getFontData();
		for (int i = 0; i < fdata.length; i++) {
			fdata[i].setHeight(10);
			fdata[i].setName("Helvetica"); //$NON-NLS-1$

		}
		Font newfont = new Font(getSite().getShell().getDisplay(), fdata);
		queryText.setFont(newfont);

		queryText.addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent ve) {
				if (QueryView.this.enableKeyboardScroll.isChecked()) {
					if (ve.keyCode == SWT.ARROW_UP) {
						String curQuery = queryText.getText().trim();
						MRUItem mi = null;
						if ((curQuery == null) || (curQuery.length() == 0)) {
							mi = oldQueryList.peek();
						} else {
							mi = oldQueryList.getNext(curQuery);
						}

						if (mi != null) {
							queryText.setText(mi.getData());
						} else {
							queryText.setText(""); //$NON-NLS-1$
						}

						ve.doit = false;
					}

					if (ve.keyCode == SWT.ARROW_DOWN) {

						String curQuery = queryText.getText().trim();
						MRUItem mi = null;
						if ((curQuery == null) || (curQuery.length() == 0)) {
							mi = oldQueryList.tail();
						} else {
							mi = oldQueryList.getPrevious(curQuery);
						}

						if (mi != null) {
							queryText.setText(mi.getData());
						} else {
							queryText.setText(""); //$NON-NLS-1$
						}
						ve.doit = false;
					}

				}

				if (ve.keyCode == 13) {
					ve.doit = false;
					executeDQL(false);
				}

				if (ve.character == 0x01) {
					queryText.selectAll();
				}
			}
		});
		queryText.addExtendedModifyListener(new ExtendedModifyListener() {

			public void modifyText(ExtendedModifyEvent eme) {
				try {
					/*
					 * This commented region looks for single words and colors
					 * them. if (queryText.getCharCount() > 0) {
					 * System.out.println("modifyText fired: " + eme.start + ":"
					 * + eme.replacedText + ":" + eme.length); QueryWord qw =
					 * getWord(eme.start); System.out.println("query word =" +
					 * qw.word);
					 * 
					 * //current style range StyleRange curRange = queryText
					 * .getStyleRangeAtOffset(qw.start); // define keywords
					 * style range StyleRange sr =
					 * getKeywordsStyleRange(qw.start,qw.word.length());
					 * 
					 * if (isKeyword(qw.word)) {
					 * queryText.replaceStyleRanges(sr.start, sr.length, new
					 * StyleRange[] { sr }); } else if (curRange != null &&
					 * (curRange.similarTo(sr))) { StyleRange defRange =
					 * getDefaultStyleRange(qw.start,qw.word.length());
					 * queryText.replaceStyleRanges(defRange.start,
					 * defRange.length, new StyleRange[] { defRange }); } }
					 */

					if (queryText.getCharCount() > 0) {
						// TODO appending the space is a hack so that the regex
						// works correctly
						// Look for a better soln in future - Aashish .
						String text = queryText.getText() + " ";
						SimpleDQLTokenizer toks = new SimpleDQLTokenizer(text,
								true);
						while (toks.hasNext()) {
							QueryWord qw = (QueryWord) toks.next();
							if (isKeyword(qw.word)) {
								StyleRange sr = getKeywordsStyleRange(qw.start,
										qw.word.length());
								queryText.replaceStyleRanges(qw.start,
										qw.word.length(),
										new StyleRange[] { sr });
							} else if (isTypeName(qw.word)) {
								StyleRange sr = getTypeStyleRange(qw.start,
										qw.word.length());
								queryText.replaceStyleRanges(qw.start,
										qw.word.length(),
										new StyleRange[] { sr });
							} else {
								StyleRange sr = getDefaultStyleRange(qw.start,
										qw.word.length());
								queryText.replaceStyleRanges(qw.start,
										qw.word.length(),
										new StyleRange[] { sr });
							}
						}

					}

				} catch (Exception ex) {
					DfLogger.error(this, "Error modifying colors", null, ex);
				}
			}

		});

		queryText.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				String repo = PluginState.getDocbase();
				String desc = "Current Repository: ";
				if (repo != null) {
					desc += repo;
				} else {
					desc += "No Repository Selected";
				}
				QueryView.this.setContentDescription(desc);

			}

			public void focusLost(FocusEvent e) {
			}

		});

		initQueryKeywords();

	}

	protected void createOptionsMenu(Composite parent) {
		mnuOptionsComp = new Composite(parent, SWT.NONE);
		mnuOptionsComp.setLayout(new FillLayout());
		final Link butDQLOpts = new Link(mnuOptionsComp, SWT.NONE);
		butDQLOpts.setText("<A>dql opts</A>");

		mnuOptions = new Menu(mnuOptionsComp.getShell(), SWT.POP_UP);

		MenuItem mniTest1 = new MenuItem(mnuOptions, SWT.CHECK);
		mniTest1.setText("test1");
		MenuItem mniTest2 = new MenuItem(mnuOptions, SWT.CHECK);
		mniTest2.setText("test2");

		FormData fd = new FormData();
		fd.top = new FormAttachment(queryText, 5);
		fd.right = new FormAttachment(100, -5);
		fd.left = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(80, -5);
		mnuOptionsComp.setLayoutData(fd);

		butDQLOpts.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = butDQLOpts.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = butDQLOpts.toDisplay(pt);
				mnuOptions.setLocation(pt);
				mnuOptions.setVisible(true);

			}

		});

	}

	protected void createOptionsComposite(Composite parent) {
		optionsComposite = new Composite(parent, SWT.NONE);
		optionsComposite.setLayout(new FormLayout());

		FormData fd = new FormData();
		fd.top = new FormAttachment(queryText, 5);
		fd.right = new FormAttachment(100, -5);
		fd.left = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(80, -5);
		optionsComposite.setLayoutData(fd);

	}

	protected void createDQLOptionsExpandBar(Composite parent) {
		ExpandBar dqlExpandBar = new ExpandBar(parent, SWT.V_SCROLL);

		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(100, -5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(50, -5);
		dqlExpandBar.setLayoutData(fd);

		optParent = new Group(dqlExpandBar, SWT.SHADOW_ETCHED_IN);
		optParent.setText("DQL Query Options"); //$NON-NLS-1$
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		optParent.setLayout(layout);
		optParent.setToolTipText(Messages
				.getString("QueryView.TTIP_DQL_QUERY_OPT")); //$NON-NLS-1$        

		readQuery = new Button(optParent, SWT.RADIO);
		readQuery.setText(Messages.getString("QueryView.DQL_READ_QUERY")); //$NON-NLS-1$
		readQuery.setSelection(true);

		execQuery = new Button(optParent, SWT.RADIO);
		execQuery.setText(Messages.getString("QueryView.DQL_EXEC_QUERY")); //$NON-NLS-1$

		execReadQuery = new Button(optParent, SWT.RADIO);
		execReadQuery.setText(Messages
				.getString("QueryView.DQL_EXEC_READ_QUERY")); //$NON-NLS-1$

		applyQuery = new Button(optParent, SWT.RADIO);
		applyQuery.setText(Messages.getString("QueryView.DQL_APPLY_QUERY")); //$NON-NLS-1$

		ExpandItem expItem = new ExpandItem(dqlExpandBar, SWT.NONE, 0);
		expItem.setText("DQL Options");
		expItem.setHeight(optParent.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		expItem.setControl(optParent);
	}

	protected void createXDQLOptionsExpandBar(Composite parent) {

		ExpandBar xdqlExpandBar = new ExpandBar(parent, SWT.V_SCROLL);
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(100, -5);
		fd.left = new FormAttachment(50, 5);
		fd.right = new FormAttachment(100, -5);
		xdqlExpandBar.setLayoutData(fd);

		optXDQL = new Group(xdqlExpandBar, SWT.SHADOW_ETCHED_IN);
		optXDQL.setText("XDQL Options"); //$NON-NLS-1$

		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		optXDQL.setLayout(gl);

		followAssembly = new Button(optXDQL, SWT.CHECK);
		followAssembly.setText(Messages
				.getString("QueryView.XDQL_FOLLOW_ASSEM")); //$NON-NLS-1$

		virtualDocumentNested = new Button(optXDQL, SWT.CHECK);
		virtualDocumentNested.setText(Messages
				.getString("QueryView.XDQL_VDOC_NESTED")); //$NON-NLS-1$

		useLowerCase = new Button(optXDQL, SWT.CHECK);
		useLowerCase.setText(Messages.getString("QueryView.XDQL_USE_LOWER")); //$NON-NLS-1$

		repeatingAsNested = new Button(optXDQL, SWT.CHECK);
		repeatingAsNested.setText(Messages
				.getString("QueryView.XDQL_REPEATING_NESTED")); //$NON-NLS-1$

		repeatingIncludeIndex = new Button(optXDQL, SWT.CHECK);
		repeatingIncludeIndex.setText(Messages
				.getString("QueryView.XDQL_REPEATING_INDEX")); //$NON-NLS-1$

		metaDataAsAttributes = new Button(optXDQL, SWT.CHECK);
		metaDataAsAttributes.setText(Messages
				.getString("QueryView.XDQL_METADATA_ATTRS")); //$NON-NLS-1$

		contentAsLink = new Button(optXDQL, SWT.CHECK);
		contentAsLink
				.setText(Messages.getString("QueryView.XDQL_CONTENT_LINK")); //$NON-NLS-1$

		includeContent = new Button(optXDQL, SWT.CHECK);
		includeContent.setText(Messages
				.getString("QueryView.XDQL_INCLUDE_CONTENT")); //$NON-NLS-1$

		ExpandItem expItem = new ExpandItem(xdqlExpandBar, SWT.NONE, 0);
		expItem.setText("XDQL Options");
		expItem.setHeight(optXDQL.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		expItem.setControl(optXDQL);
	}

	protected void createOptionTabFolder(Composite parent) {
		optionTabs = new TabFolder(parent, SWT.TOP | SWT.FLAT);
		FormData fd = new FormData();
		fd.top = new FormAttachment(queryText, 5);
		fd.right = new FormAttachment(100, -5);
		fd.left = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(90, -5);
		optionTabs.setLayoutData(fd);
	}

	/**
	 * Creates the check boxes that set XDQL options.
	 * 
	 * @param parent
	 */
	protected void createDQLOptions(Composite parent) {
		optParent = new Group(parent, SWT.SHADOW_ETCHED_IN);
		// optParent.setText("DQL Query Options"); //$NON-NLS-1$
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		optParent.setLayout(layout);
		optParent.setToolTipText(Messages
				.getString("QueryView.TTIP_DQL_QUERY_OPT")); //$NON-NLS-1$

		/*
		 * FormData fd = new FormData(); fd.top = new FormAttachment(queryText,
		 * 5); fd.right = new FormAttachment(100, -5); fd.left = new
		 * FormAttachment(0, 5); fd.bottom = new FormAttachment(50, -5);
		 * optParent.setLayoutData(fd);
		 */

		readQuery = new Button(optParent, SWT.RADIO);
		readQuery.setText(Messages.getString("QueryView.DQL_READ_QUERY")); //$NON-NLS-1$
		readQuery.setSelection(true);

		execQuery = new Button(optParent, SWT.RADIO);
		execQuery.setText(Messages.getString("QueryView.DQL_EXEC_QUERY")); //$NON-NLS-1$

		execReadQuery = new Button(optParent, SWT.RADIO);
		execReadQuery.setText(Messages
				.getString("QueryView.DQL_EXEC_READ_QUERY")); //$NON-NLS-1$

		applyQuery = new Button(optParent, SWT.RADIO);
		applyQuery.setText(Messages.getString("QueryView.DQL_APPLY_QUERY")); //$NON-NLS-1$

		TabItem ti = new TabItem(optionTabs, SWT.NONE);
		ti.setControl(optParent);
		ti.setText(Messages.getString("QueryView.TAB_DQL_QUERY_OPT")); //$NON-NLS-1$
	}

	protected void createXDQLOptions(Composite parent) {
		ScrolledComposite scrComp = new ScrolledComposite(parent, SWT.V_SCROLL);
		scrComp.setExpandHorizontal(true);
		scrComp.setExpandVertical(true);

		optXDQL = new Group(scrComp, SWT.SHADOW_ETCHED_IN);
		// optXDQL.setText("XDQL Options"); //$NON-NLS-1$

		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		optXDQL.setLayout(gl);

		/*
		 * FormData fd = new FormData(); fd.top = new FormAttachment(optParent,
		 * 5); fd.bottom = new FormAttachment(80, -5); fd.left = new
		 * FormAttachment(0, 5); fd.right = new FormAttachment(100, -5);
		 * optXDQL.setLayoutData(fd);
		 */

		followAssembly = new Button(optXDQL, SWT.CHECK);
		followAssembly.setText(Messages
				.getString("QueryView.XDQL_FOLLOW_ASSEM")); //$NON-NLS-1$

		virtualDocumentNested = new Button(optXDQL, SWT.CHECK);
		virtualDocumentNested.setText(Messages
				.getString("QueryView.XDQL_VDOC_NESTED")); //$NON-NLS-1$

		useLowerCase = new Button(optXDQL, SWT.CHECK);
		useLowerCase.setText(Messages.getString("QueryView.XDQL_USE_LOWER")); //$NON-NLS-1$

		repeatingAsNested = new Button(optXDQL, SWT.CHECK);
		repeatingAsNested.setText(Messages
				.getString("QueryView.XDQL_REPEATING_NESTED")); //$NON-NLS-1$

		repeatingIncludeIndex = new Button(optXDQL, SWT.CHECK);
		repeatingIncludeIndex.setText(Messages
				.getString("QueryView.XDQL_REPEATING_INDEX")); //$NON-NLS-1$

		metaDataAsAttributes = new Button(optXDQL, SWT.CHECK);
		metaDataAsAttributes.setText(Messages
				.getString("QueryView.XDQL_METADATA_ATTRS")); //$NON-NLS-1$

		contentAsLink = new Button(optXDQL, SWT.CHECK);
		contentAsLink
				.setText(Messages.getString("QueryView.XDQL_CONTENT_LINK")); //$NON-NLS-1$

		includeContent = new Button(optXDQL, SWT.CHECK);
		includeContent.setText(Messages
				.getString("QueryView.XDQL_INCLUDE_CONTENT")); //$NON-NLS-1$

		scrComp.setMinWidth(optXDQL.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
		scrComp.setMinHeight(optXDQL.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		scrComp.setContent(optXDQL);

		TabItem ti = new TabItem(optionTabs, SWT.NONE);
		ti.setControl(scrComp);
		ti.setText(Messages.getString("QueryView.XDQL_TAB")); //$NON-NLS-1$

	}

	/**
	 * Creates the buttons that the user presses to execute the query.
	 * 
	 * @param parent
	 */
	protected void createActionButtons(Composite parent) {
		butParent = new Composite(parent, SWT.NONE);
		FormLayout fl = new FormLayout();
		butParent.setLayout(fl);

		FormData fd = new FormData();
		fd.top = new FormAttachment(optionTabs, 5);
		// fd.top = new FormAttachment(optionsComposite, 5);
		fd.bottom = new FormAttachment(100, -5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		butParent.setLayoutData(fd);

		runDQL = new Button(butParent, SWT.PUSH | SWT.FLAT);
		runDQL.setText(Messages.getString("QueryView.BUTTON_DQL")); //$NON-NLS-1$
		FormData fd1 = new FormData();
		fd1.top = new FormAttachment(0, 5);
		fd1.bottom = new FormAttachment(100, -5);
		fd1.left = new FormAttachment(0, 5);
		fd1.right = new FormAttachment(25, -5);
		runDQL.setLayoutData(fd1);

		runDQL.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				executeDQL(false);
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});

		Button runDQLAsync = new Button(butParent, SWT.PUSH | SWT.FLAT);
		runDQLAsync.setText("DQL Async");
		runDQLAsync.setLayoutData(PluginHelper.getFormData(0, 100, 25, 50));
		runDQLAsync.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				executeDQL(true);
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});

		runXDQL = new Button(butParent, SWT.PUSH | SWT.FLAT);
		runXDQL.setText(Messages.getString("QueryView.BUTTON_XDQL")); //$NON-NLS-1$
		runXDQL.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				executeXDQL(false);
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});
		FormData fd2 = new FormData();
		fd2.top = new FormAttachment(0, 5);
		fd2.bottom = new FormAttachment(100, -5);
		fd2.left = new FormAttachment(50, 5);
		fd2.right = new FormAttachment(75, -5);
		runXDQL.setLayoutData(fd2);

		Button runXDQLAsync = new Button(butParent, SWT.PUSH | SWT.FLAT);
		runXDQLAsync.setText("XDQL Async");
		runXDQLAsync.setLayoutData(PluginHelper.getFormData(0, 100, 75, 100));
		runXDQLAsync.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				executeXDQL(true);
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}

		});
		/*
		 * resultsSummary = new Text(butParent, SWT.READ_ONLY | SWT.MULTI |
		 * SWT.V_SCROLL | SWT.WRAP);
		 * resultsSummary.setBackground(super.getSite().getShell().getDisplay()
		 * .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		 * resultsSummary.setText(Messages
		 * .getString("QueryView.RESULTS_SUMMARY")); //$NON-NLS-1$ FormData fd3
		 * = new FormData(); fd3.top = new FormAttachment(0, 5); fd3.bottom =
		 * new FormAttachment(100, -5); fd3.left = new
		 * FormAttachment(runXDQLAsync, 5); fd3.right = new FormAttachment(96,
		 * -5); resultsSummary.setLayoutData(fd3);
		 * 
		 * Button stopQuery = new Button(butParent,SWT.PUSH | SWT.FLAT);
		 * stopQuery.setImage(PluginHelper.getImage("stop.gif"));
		 * stopQuery.addSelectionListener(new SelectionListener(){
		 * 
		 * public void widgetDefaultSelected(SelectionEvent e) {
		 * widgetSelected(e); }
		 * 
		 * public void widgetSelected(SelectionEvent e) { if(dqlQueryExec !=
		 * null) { dqlQueryExec.cancel(); } }
		 * 
		 * }); FormData fdStop = new FormData(); fdStop.top = new
		 * FormAttachment(0,3); fdStop.bottom = new FormAttachment(100,-3);
		 * fdStop.left = new FormAttachment(resultsSummary,5); fdStop.right =
		 * new FormAttachment(100,-5); stopQuery.setLayoutData(fdStop);
		 */

	}

	protected void createResultsUI(Composite parent) {
		resultsContainer = new Composite(parent, SWT.NONE);
		resultsContainer.setLayout(new FormLayout());

		// resultsParent.setLayout(new FillLayout());
		FormData fd = new FormData();
		fd.top = new FormAttachment(queryComposite, 5);
		fd.bottom = new FormAttachment(100, -5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		resultsContainer.setLayoutData(fd);

		createResultsSummary(resultsContainer);

		resultsParent = new Composite(resultsContainer, SWT.NONE);
		FormData fdRes = new FormData();
		fdRes.top = new FormAttachment(15, 5);
		fdRes.bottom = new FormAttachment(100, -5);
		fdRes.left = new FormAttachment(0, 5);
		fdRes.right = new FormAttachment(100, -5);
		resultsParent.setLayoutData(fdRes);

		resultsLayout = new StackLayout();
		resultsParent.setLayout(resultsLayout);

		createDQLResultsTable(resultsParent);
		createXDQLResultsView(resultsParent);
		resultsLayout.topControl = xdqlResults;
	}

	protected void createResultsSummary(Composite parent) {
		Composite sumParent = new Composite(parent, SWT.NONE);
		FormData fdSum = new FormData();
		fdSum.left = new FormAttachment(0, 5);
		fdSum.right = new FormAttachment(100, -5);
		fdSum.top = new FormAttachment(0, 5);
		fdSum.bottom = new FormAttachment(15, -5);
		sumParent.setLayoutData(fdSum);

		sumParent.setLayout(new FormLayout());

		resultsSummary = new Text(sumParent, SWT.READ_ONLY | SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP);
/*
		resultsSummary.setBackground(super.getSite().getShell().getDisplay()
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
*/
		resultsSummary.setText(Messages.getString("QueryView.RESULTS_SUMMARY")); //$NON-NLS-1$
		FormData fd3 = new FormData();
		fd3.top = new FormAttachment(0, 5);
		fd3.bottom = new FormAttachment(50, -5);
		fd3.left = new FormAttachment(0, 5);
		fd3.right = new FormAttachment(90, -5);
		resultsSummary.setLayoutData(fd3);

		Button stopQuery = new Button(sumParent, SWT.PUSH | SWT.FLAT);
		stopQuery.setImage(PluginHelper.getImage("stop.gif"));
		stopQuery.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				if (dqlQueryExec != null) {
					dqlQueryExec.cancel();
				}

			}

		});
		FormData fdStop = new FormData();
		fdStop.top = new FormAttachment(0, 3);
		fdStop.bottom = new FormAttachment(100, -3);
		fdStop.left = new FormAttachment(resultsSummary, 5);
		fdStop.right = new FormAttachment(100, -5);
		stopQuery.setLayoutData(fdStop);
	}

	protected void createDQLResultsTable(Composite parent) {
		resultsTable = new Table(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		resultsTable.setHeaderVisible(true);
		resultsTable.setLinesVisible(true);

		resultsCursor = new TableCursor(resultsTable, SWT.NONE);
		resultsCursor.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent me) {
			}

			public void mouseUp(MouseEvent me) {
			}

			public void mouseDoubleClick(MouseEvent me) {
				String selData = getSelectedData();
				dumpProperties(selData);
			}
		});

		resultsViewer = new TableViewer(resultsTable);
		resultsViewer.setContentProvider(new DQLResultsContentProvider());
		resultsViewer.setLabelProvider(new DQLQueryLabelProvider());
		// resultsViewer.setCellModifier(new DQLCellModifier());
		resultsViewer.setUseHashlookup(true);

	}

	protected void createXDQLResultsView(Composite parent) {
		try {
			xdqlResults = new Browser(parent, SWT.NONE);
		} catch (Error er) {
			// System.out.println("Unable to instantiate browser");
		}

	}

	protected void executeDQL(boolean async) {
		String docbaseName = PluginState.getDocbase();
		if ((docbaseName == null) || (docbaseName.length() == 0)) {
			MessageDialog.openWarning(getSite().getShell(),
					Messages.getString("QueryView.DLG_NO_REPO"), //$NON-NLS-1$
					Messages.getString("QueryView.DLG_MSG_NO_REPO")); //$NON-NLS-1$
			return;
		}

		try {
			// setup query object
			String queryStr = queryText.getText().trim();
			if (queryStr == null || (queryStr.length() == 0)) {
				return;
			}

			int queryType = 0;
			if (readQuery.getSelection()) {
				queryType = IDfQuery.DF_READ_QUERY;
			} else if (execQuery.getSelection()) {
				queryType = IDfQuery.DF_EXEC_QUERY;
			} else if (applyQuery.getSelection()) {
				queryType = IDfQuery.DF_APPLY;
			} else if (execReadQuery.getSelection()) {
				queryType = IDfQuery.DF_EXECREAD_QUERY;
			}

			if (async) {
				FileDialog fd = new FileDialog(this.getSite().getShell(),
						SWT.SAVE);
				fd.setFilterExtensions(new String[] { ".xls" });
				fd.setText("Choose File to Save Query Results");
				String filename = fd.open();
				if (filename != null) {
					DQLQueryJob job = new DQLQueryJob(this, queryStr, queryType);
					job.setResultsFile(filename);
					job.setProperty(IProgressConstants.KEEP_PROPERTY,
							Boolean.TRUE);
					job.setUser(true);
					job.setPriority(Job.LONG);
					job.schedule();
				}

			} else {

				if (dqlQueryExec != null) {
					dqlQueryExec.cancel();
					dqlQueryExec = null;
				}

				this.eiQueryCfg.setExpanded(false);
				this.eiResults.setExpanded(true);
				this.computeResultsHeight();

				dqlQueryExec = new DQLQueryExecutor(queryStr, queryType);
				dqlQueryExec.setUIControls(resultsTable, resultsViewer,
						resultsLayout, resultsSummary);
				dqlQueryExec.setProperty(IProgressConstants.KEEP_PROPERTY,
						Boolean.TRUE);
				// dqlQueryExec.setUser(true);
				dqlQueryExec.schedule();
				this.addQueryToHistory(queryStr);

			}
		} catch (Exception ex) {
			DfLogger.warn(this, "Error preparing dql results", null, ex); //$NON-NLS-1$
			MessageDialog.openError(this.getSite().getShell(),
					Messages.getString("QueryView.DLG_ERR_RESULTS_PREP"), //$NON-NLS-1$
					"Error: " + ex.getMessage()); //$NON-NLS-1$
		}

	}

	public void setQueryString(String queryStr) {
		queryText.setText(queryStr);
	}

	public void setupDQLResults(String queryStr, String[] columnNames,
			LinkedList resultsList, long execTime) {
		int attrCnt = columnNames.length;

		// should dispose old columns as query will fill new
		resultsTable.removeAll();
		TableColumn cols[] = resultsTable.getColumns();
		for (int i = 0; i < cols.length; i++) {
			cols[i].dispose();
		}

		CellEditor[] editors = new CellEditor[attrCnt];
		for (int i = 0; i < attrCnt; i++) {
			TableColumn col = new TableColumn(resultsTable, SWT.LEFT, i);
			final String colName = columnNames[i];
			col.setText(colName);
			col.setResizable(true);
			col.setWidth(150);
			col.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					resultsViewer.setSorter(new DQLResultsSorter(colName));
				}
			});
			// System.out.println("Created column: " + colNames[i]);

			editors[i] = new TextCellEditor(resultsTable, SWT.READ_ONLY);
			Text txtCtrl = (Text) editors[i].getControl();
			txtCtrl.setTextLimit(255);

		}

		// resultsLayout.topControl = resTbl;
		// resultsViewer.setColumnProperties(colNames);

		// System.out.println("just gave label prv");
		resultsViewer.setColumnProperties(columnNames);
		/*
		 * if (colNames.length > 0) { resultsViewer.setSorter(new
		 * DQLResultsSorter(colNames[0])); }
		 */
		// resultsViewer.setCellEditors(editors);
		StringBuffer bufSummary = new StringBuffer(32);
		bufSummary.append("Number of Results: " + resultsList.size()) //$NON-NLS-1$
				.append(".  "); //$NON-NLS-1$
		bufSummary.append("Approximate Execution Time: ").append( //$NON-NLS-1$
				String.valueOf(execTime));
		bufSummary.append(" milliseconds  "); //$NON-NLS-1$
		resultsSummary.setText(bufSummary.toString());

		resultsViewer.setInput(resultsList);
		resultsLayout.topControl = resultsTable;
		resultsParent.layout();
		addQueryToHistory(queryStr);

	}

	/**
	 * Gets the column names to be used in creating the results table
	 * 
	 * @param coll
	 * @return
	 */
	protected String[] getColumnNames(IDfCollection coll) {
		try {
			if (coll == null) {
				return new String[] {};
			}
			int attrCnt = coll.getAttrCount();
			String[] colNames = new String[attrCnt];
			for (int i = 0; i < attrCnt; i++) {
				colNames[i] = coll.getAttr(i).getName();
				// System.out.println("Col name: " + colNames[i]);
			}
			return colNames;
		} catch (Exception ex) {
			ex.printStackTrace();
			return (new String[] {});
		}
	}

	protected void executeXDQL(boolean async) {
		String docbaseName = PluginState.getDocbase();
		if ((docbaseName == null) || (docbaseName.length() == 0)) {
			MessageDialog.openWarning(getSite().getShell(),
					"Repository Authentication", //$NON-NLS-1$
					"Cannot find a repository against which to run the query"); //$NON-NLS-1$
			return;
		}

		try {
			// setup query object
			String queryStr = queryText.getText().trim();
			if (queryStr == null || (queryStr.length() == 0)) {
				return;
			}
			IDfXmlQuery queryObj = new DfXmlQuery();
			queryObj.setDql(queryStr);
			int queryType = 0;

			if (execQuery.getSelection()) {
				queryType = IDfQuery.DF_EXEC_QUERY;
			} else {
				queryType = IDfQuery.DF_READ_QUERY;
			}
			/*
			 * else if (applyQuery.getSelection()) { queryType =
			 * IDfQuery.DF_APPLY; } else if (execReadQuery.getSelection()) {
			 * queryType = IDfQuery.DF_EXECREAD_QUERY; }
			 */

			// queryObj.setContentFormat("xml");
			queryObj.setContentEncoding("dom"); //$NON-NLS-1$

			populateXDQLOptions(queryObj);

			if (async) {
				FileDialog fd = new FileDialog(this.getSite().getShell(),
						SWT.SAVE);
				fd.setFilterExtensions(new String[] { ".xml" });
				fd.setText("Choose File to Save XDQL Query Results");
				String filename = fd.open();
				if (filename != null) {
					XDQLQueryJob job = new XDQLQueryJob(queryObj, queryType,
							filename);
					job.setProperty(IProgressConstants.KEEP_PROPERTY,
							Boolean.TRUE);
					job.setUser(true);
					job.setPriority(Job.LONG);
					job.schedule();
				}
			} else {

				XDQLQueryExecutor xdqlExec = new XDQLQueryExecutor(queryObj,
						queryType);
				IProgressService progServ = PlatformUI.getWorkbench()
						.getProgressService();
				long beginTime = System.currentTimeMillis();
				progServ.busyCursorWhile(xdqlExec);
				long endTime = System.currentTimeMillis();
				String domStr = xdqlExec.getResults();

				double timeInSec = (double) ((endTime - beginTime) / 1000);
				StringBuffer bufSummary = new StringBuffer(32);
				bufSummary.append("Approximate Execution Time:").append( //$NON-NLS-1$
						String.valueOf((endTime - beginTime)))
						.append(" milliseconds"); //$NON-NLS-1$            
				resultsSummary.setText(bufSummary.toString());

				resultsLayout.topControl = xdqlResults;
				String randomName = "" + System.currentTimeMillis() + "" //$NON-NLS-1$ //$NON-NLS-2$
						+ (Math.random() * 1000);
				File tempFile = File.createTempFile(randomName, ".xml"); //$NON-NLS-1$
				FileOutputStream tempOut = new FileOutputStream(tempFile);
				BufferedOutputStream bufOut = new BufferedOutputStream(tempOut);
				bufOut.write(domStr.getBytes()); //$NON-NLS-1$
				bufOut.close();
				xdqlResults.setUrl(tempFile.getAbsolutePath());

				this.eiQueryCfg.setExpanded(false);
				this.eiResults.setExpanded(true);
				this.computeResultsHeight();
				resultsLayout.topControl = xdqlResults;
				resultsParent.layout();

			}
			addQueryToHistory(queryStr);
		} catch (Exception ex) {
			DfLogger.error(this, "XDQL Query execution error", null, ex); //$NON-NLS-1$
		} finally {

		}

	}

	protected void populateXDQLOptions(IDfXmlQuery query) {
		query.setVirtualDocumentNested(virtualDocumentNested.getSelection());
		query.setContentAsLink(contentAsLink.getSelection());
		query.setFollowAssembly(followAssembly.getSelection());
		query.setRepeatingIncludeIndex(repeatingIncludeIndex.getSelection());
		query.setRepeatingAsNested(repeatingAsNested.getSelection());
		query.setMetaDataAsAttributes(metaDataAsAttributes.getSelection());
		query.includeContent(includeContent.getSelection());
		if (useLowerCase.getSelection()) {
			query.useLowerCaseTagNames();
		}
	}

	/**
	 * Adds a query to the query history
	 * 
	 * @param query
	 */
	protected void addQueryToHistory(String query) {
		if (oldQueryList == null) {
			oldQueryList = new MRUQueue(maxOldQueries);
		}

		MRUItem mi = new MRUItem(query, query, System.currentTimeMillis());
		oldQueryList.addToMRUQueue(mi);
	}

	protected String[] getOldQueryList() {
		if (oldQueryList == null) {
			return new String[] {};
		}
		LinkedList lst = oldQueryList.getMRUList();
		String toRet[] = new String[lst.size()];
		Iterator iter = lst.iterator();
		int cntr = 0;
		while (iter.hasNext()) {
			MRUItem mi = (MRUItem) iter.next();
			toRet[cntr] = mi.getData();
			cntr++;
		}
		return toRet;

	}

	protected void dumpProperties(String data) {
		// System.out.println("data: " + data);
		String[] arrs = data.split(","); //$NON-NLS-1$
		// System.out.println("Arrs len: " + arrs.length);
		int arrLen = arrs.length;
		if ((arrLen > 0) && (new DfId(arrs[0])).isObjectId()) {
			if (arrLen > 1) {
				SimpleListDialog sld = new SimpleListDialog(getSite()
						.getShell(), arrs,
						Messages.getString("QueryView.DLG_OBJ_ID_SEL")); //$NON-NLS-1$
				int status = sld.open();
				if (status == SimpleListDialog.OK) {
					String[] sel = sld.getSelection();
					data = sel[0];
				}
			} else {
				data = arrs[0];
			}
			PluginHelper.showPropertiesView(data);
		}

	}

	protected void showACL(String data) {
		String[] arrs = data.split(",");
		int arrLen = arrs.length;
		if ((arrLen > 0) && (new DfId(arrs[0])).isObjectId()) {
			IDfSession sess = PluginState.getSessionById(arrs[0]);
			IDfSysObject sobj;
			try {
				sobj = (IDfSysObject) sess.getObject(new DfId(arrs[0]));
				ACLDialog sld = new ACLDialog(getSite().getShell(), sobj.getACL());
				sld.open();
			} catch (DfException e) {
				e.printStackTrace();
			}
		}
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {

		super.init(site, memento);
		Preferences prefs = DevprogPlugin.getDefault().getPluginPreferences();
		String favQueries = prefs.getString(KEY_FAV_QUERIES);
		if (favQueries != null) {
			restoreFavoriteQueries(favQueries);
		}
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		Preferences prefs = DevprogPlugin.getDefault().getPluginPreferences();
		prefs.setValue(KEY_FAV_QUERIES, serializeFavoriteQueries());

		prefs.setValue(enable_kb_hist_nav,
				this.enableKeyboardScroll.isChecked());

	}

	// //////////////////////////////////////
	// // Favorite Queries Implementation////
	// //////////////////////////////////////

	private String serializeFavoriteQueries() {

		StringBuffer valBuffer = new StringBuffer(64);
		Iterator keyIter = favoriteQueries.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = (String) keyIter.next();
			String val = (String) favoriteQueries.get(key);
			valBuffer.append(key).append(":").append(val).append(";"); //$NON-NLS-1$ //$NON-NLS-2$

		}
		return valBuffer.toString();
	}

	private void restoreFavoriteQueries(String queries) {
		try {
			if (favoriteQueries == null) {
				favoriteQueries = new TreeMap();
			}

			if (queries == null) {
				return;
			}

			String[] pairs = queries.split(";"); //$NON-NLS-1$
			for (int i = 0; i < pairs.length; i++) {
				String[] pair = pairs[i].split(":"); //$NON-NLS-1$
				if (pair.length == 2) {
					favoriteQueries.put(pair[0], pair[1]);
				}
			}
		} catch (Exception ioe) {
			DfLogger.error(this, "Error restoring favorite queries", null, ioe); //$NON-NLS-1$
		}
	}

	class FavNameValidator implements IInputValidator {
		SortedMap favs = null;

		FavNameValidator(SortedMap favs) {
			this.favs = favs;
		}

		public String isValid(String newText) {
			if (favs.containsKey(newText.trim())) {
				return Messages
						.getString("QueryView.ERR_DUPLICATE_FAVQUERY_NAME"); //$NON-NLS-1$
			}
			return null;
		}
	}

	// /////////////////////////////////////////////////
	// ///Query Editor Support//////////////////////////
	// /////////////////////////////////////////////////

	/*
	 * Input - some position in the buffer Possible values: 0, max index,
	 * somewhere in middle.
	 */
	private QueryWord getWord(int pos) {
		StringBuffer bufWord = new StringBuffer(12);

		int curPos = pos;
		int charCnt = queryText.getCharCount();
		if (curPos >= charCnt) {
			curPos = charCnt - 1;
		}
		if (curPos < 0) {
			curPos = 0;
		}

		// first go backwards
		while (curPos >= 0) {
			String curChar = queryText.getTextRange(curPos, 1);
			if (isQueryToken(curChar)) //$NON-NLS-1$
			{
				break;
			} else {
				bufWord.append(curChar);
				curPos--;
			}
		}
		bufWord.reverse();
		int start = 0;
		if (curPos > 0) {
			start = ((curPos + 1) < charCnt ? curPos + 1 : (charCnt - 1));
		}

		curPos = pos + 1;
		while (curPos < queryText.getCharCount()) {
			String curChar = queryText.getTextRange(curPos, 1);
			if (curChar.equals(" ")) //$NON-NLS-1$
			{
				break;
			}
			bufWord.append(curChar);
			curPos++;
		}

		QueryWord qw = new QueryWord();
		qw.start = start;
		qw.word = bufWord.toString();
		return qw;

	}

	private void initQueryKeywords() {
		String[] keywords = Messages.getString("QueryView.QUERY_KEYWORDS")
				.split(",");
		for (int i = 0; i < keywords.length; i++) {
			queryKeywords.add(keywords[i].toLowerCase());
		}

		// Navy blue
		keywordsColor = new Color(super.getSite().getShell().getDisplay(), 0,
				0, 128);
	}

	private boolean isQueryToken(String tokCh) {
		if (tokCh.equals(" ") || tokCh.equals("(") || tokCh.equals(")")) {
			return true;
		}
		return false;
	}

	private boolean isKeyword(String word) {
		String low = word.toLowerCase();
		return queryKeywords.contains(low);
	}

	private StyleRange getKeywordsStyleRange(int offset, int length) {
		StyleRange sr = new StyleRange();
		sr.start = offset;
		sr.length = length;
		sr.fontStyle = SWT.BOLD;
		sr.foreground = keywordsColor;
		return sr;
	}

	private void initTypeList() {
		if (typeColor == null) {
			typeColor = new Color(super.getSite().getShell().getDisplay(), 0,
					138, 189);
		}

		String repo = PluginState.getDocbase();
		if (repo != null) {
			Set list = (Set) typeLists.get(repo);
			if (list == null) {
				list = PluginHelper.getTypeList(repo);
				typeLists.put(repo, list);
			}
		}
	}

	/**
	 * Checks if the specified string represents a type name
	 * 
	 * @param name
	 * @return
	 */
	private boolean isTypeName(String name) {
		initTypeList();
		String repo = PluginState.getDocbase();
		if (repo != null) {
			Set list = (Set) typeLists.get(repo);
			return list.contains(name);

		} else {
			return false;
		}

	}

	/**
	 * Gets the style range for type names
	 * 
	 * @param offset
	 * @param length
	 * @return
	 */
	private StyleRange getTypeStyleRange(int offset, int length) {
		StyleRange sr = new StyleRange();
		sr.start = offset;
		sr.length = length;
		sr.fontStyle = SWT.BOLD;
		sr.foreground = typeColor;
		return sr;
	}

	private StyleRange getDefaultStyleRange(int offset, int length) {
		StyleRange sr = new StyleRange();
		sr.start = offset;
		sr.length = length;
		sr.foreground = null;
		sr.background = null;
		return sr;
	}

	class EnableKbHistoryNav extends Action {

		EnableKbHistoryNav(String name) {
			super(name, IAction.AS_CHECK_BOX);
		}

	}

}