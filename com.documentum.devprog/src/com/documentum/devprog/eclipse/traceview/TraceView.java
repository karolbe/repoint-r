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
 * Created on Sep 6, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.traceview;

import com.documentum.fc.common.DfLogger;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PreferenceConstants;
import com.documentum.devprog.eclipse.common.SimpleTextDialog;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class TraceView extends ViewPart implements SelectionListener {
	private Tree viewer = null;

	private Action startTrace = null;

	private Action stopTrace = null;

	private Action clearBuffer = null;

	private Action copySelection = null;

	private Action copyAll = null;

	private Action findEntry = null;

	private Action setTriggerWords = null;

	private Action traceConfig = null;

	private boolean doTrace = false;

	private String[] triggerWords = new String[] {};

	private Set searchResults = new HashSet();

	/**
	 * The clipboard instance for this view.
	 */
	private Clipboard clipboard = null;

	private ListenerThread listenerThr = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */

	public void createPartControl(Composite parent) {
		createViewer(parent);
		createClipboard(parent);
		createActions();
		createToolbar();
		createPopupMenu();
		// createLogListener();

	}

	private void createViewer(Composite parent) {

		viewer = new Tree(parent, SWT.VIRTUAL | SWT.SINGLE | SWT.BORDER);
		viewer.addSelectionListener(this);
		// viewer.setBackground(new Color(Display.getDefault(),232,242,254));

		/*
		 * TreeColumn srcCol = new TreeColumn(viewer, SWT.CENTER);
		 * srcCol.setText("Source"); srcCol.setWidth(200);
		 * 
		 * TreeColumn methName = new TreeColumn(viewer, SWT.NONE);
		 * methName.setText("Method"); methName.setWidth(200);
		 * 
		 * TreeColumn methArgs = new TreeColumn(viewer, SWT.NONE);
		 * methArgs.setText("Arguments"); methArgs.setWidth(200);
		 * 
		 * TreeColumn returnValue = new TreeColumn(viewer, SWT.NONE);
		 * returnValue.setText("Return Value"); returnValue.setWidth(200);
		 */
	}

	private void createActions() {
		startTrace = new Action() {
			public void run() {
				if (listenerThr == null) {
					createLogListener();
				}
				doTrace = true;
				startTrace.setEnabled(false);
				stopTrace.setEnabled(true);
			}
		};
		startTrace.setImageDescriptor(PluginHelper
				.getImageDesc("resume_co.gif"));
		startTrace.setToolTipText("Start Trace Display");

		stopTrace = new Action() {
			public void run() {
				doTrace = false;
				startTrace.setEnabled(true);
				stopTrace.setEnabled(false);
			}
		};
		stopTrace.setImageDescriptor(PluginHelper
				.getImageDesc("suspend_co.gif"));
		stopTrace.setToolTipText("Stop Trace Display");
		stopTrace.setEnabled(false);

		clearBuffer = new Action() {
			public void run() {
				viewer.removeAll();
			}
		};
		clearBuffer.setImageDescriptor(PluginHelper
				.getImageDesc("clear_co.gif"));
		clearBuffer.setToolTipText("Clear Buffer Contents");

		copySelection = new Action() {
			public void run() {
				TreeItem[] ti = viewer.getSelection();
				String nl = System.getProperty("line.separator");
				StringBuffer bufSel = new StringBuffer(32);
				for (int i = 0; i < ti.length; i++) {
					bufSel.append(ti[i].getText());
					bufSel.append(nl);
				}
				String sel = bufSel.toString();
				TextTransfer tt = TextTransfer.getInstance();
				clipboard.setContents(new String[] { sel },
						new TextTransfer[] { tt });
			}

		};
		copySelection.setImageDescriptor(PluginHelper.getImageDesc("copy.gif"));
		copySelection.setToolTipText("Copy Selection");
		copySelection.setText("Copy Selection");
		copySelection.setAccelerator(SWT.CTRL | 'C');

		copyAll = new Action() {
			public void run() {
				TreeItem[] tis = viewer.getItems();
				StringBuffer bufSel = new StringBuffer(tis.length * 24);
				String nl = System.getProperty("line.separator");
				for (int i = 0; i < tis.length; i++) {
					bufSel.append(tis[i].getText());
					bufSel.append(nl);
				}

				String sel = bufSel.toString();
				TextTransfer tt = TextTransfer.getInstance();
				clipboard.setContents(new String[] { sel },
						new TextTransfer[] { tt });
			}

		};
		copyAll.setImageDescriptor(PluginHelper.getImageDesc("copy_all.gif"));
		copyAll.setToolTipText("Copy All");
		copyAll.setText("Copy All");

		setTriggerWords = new Action() {
			public void run() {
				System.out.println("set trigger words called");
				StringBuffer bufWords = new StringBuffer(32);
				String nl = System.getProperty("line.separator");

				bufWords.append("Enter Each Word on a New Line. Search is Case-Sensitive. (DO NOT REMOVE THIS LINE.)");
				bufWords.append(nl);
				for (int i = 0; i < triggerWords.length; i++) {
					bufWords.append(triggerWords[i]).append(nl);

				}
				String data = bufWords.toString();
				System.out.println("data: " + data);
				SimpleTextDialog stv = new SimpleTextDialog(getSite()
						.getShell(), "Trigger Words", data);
				System.out.println("opening dialog");
				int status = stv.open();
				if (status == SimpleTextDialog.OK) {
					String newWords = stv.getTextData();
					String[] words = newWords.split(nl);
					List twords = new ArrayList();

					for (int i = 1; i < words.length; i++) {
						twords.add(words[i]);
						System.out.println(words[i]);
					}

					triggerWords = (String[]) twords.toArray(new String[] {});
				}
			}
		};
		setTriggerWords.setImageDescriptor(PluginHelper
				.getImageDesc("trigger_words.gif"));
		setTriggerWords
				.setToolTipText("Set trigger words. Entries containing trigger words get highlighted upon arrival");

		findEntry = new Action() {
			public void run() {
				InputDialog srchTerm = new InputDialog(getSite().getShell(),
						"Find",
						"Enter the text to find. Search is case-sensitive.",
						"", null);
				int status = srchTerm.open();
				if (status == InputDialog.OK) {
					String text = srchTerm.getValue();

					if (text.length() > 0) {
						Iterator iterOldResults = searchResults.iterator();
						while (iterOldResults.hasNext()) {
							TreeItem item = (TreeItem) iterOldResults.next();
							item.setBackground(Display.getDefault()
									.getSystemColor(SWT.COLOR_WHITE));
						}

						TreeItem[] ti = viewer.getItems();
						searchResults.clear();
						for (int i = 0; i < ti.length; i++) {
							if (ti[i].getText().indexOf(text) != -1) {
								searchResults.add(ti[i]);
								ti[i].setBackground(Display.getDefault()
										.getSystemColor(SWT.COLOR_YELLOW));
							}
						}
					}
				}
			}

		};
		findEntry.setImageDescriptor(PluginHelper.getImageDesc("search.gif"));
		findEntry.setToolTipText("Find");

		traceConfig = new Action() {
			public void run() {
				TraceConfigurationDialog tcd = new TraceConfigurationDialog(
						getSite().getShell());
				tcd.open();
			}
		};
		traceConfig.setImageDescriptor(PluginHelper
				.getImageDesc("trace_config.gif"));
		traceConfig.setToolTipText("Configure Tracing");
	}

	private void createClipboard(Composite parent) {
		clipboard = new Clipboard(parent.getDisplay());
	}

	private void createToolbar() {
		IToolBarManager tbMgr = getViewSite().getActionBars()
				.getToolBarManager();
		tbMgr.add(startTrace);
		tbMgr.add(stopTrace);
		tbMgr.add(clearBuffer);
		tbMgr.add(copySelection);
		tbMgr.add(copyAll);
		tbMgr.add(setTriggerWords);
		tbMgr.add(findEntry);
		tbMgr.add(traceConfig);
	}

	private void createLogListener() {
		listenerThr = new ListenerThread();
		new Thread(listenerThr).start();
	}

	/**
	 * Create the context(popup) menu
	 * 
	 * @param viewer
	 */
	protected void createPopupMenu() {
		MenuManager menuMgr = new MenuManager("#TracePopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuMgr) {
				fillPopupMenu(menuMgr);
			}
		});

		Menu mnu = menuMgr.createContextMenu(viewer);
		viewer.setMenu(mnu);

		// getSite().registerContextMenu(menuMgr, viewer);
	}

	protected void fillPopupMenu(IMenuManager menuMgr) {
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuMgr.add(copySelection);
		menuMgr.add(copyAll);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
				+ "-end"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */

	public void setFocus() {

	}

	public class ListenerThread implements Runnable {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			Preferences prefs = DevprogPlugin.getDefault()
					.getPluginPreferences();
			int port = prefs.getInt(PreferenceConstants.P_PORT);
			try {

				if (port < 0) {
					port = 4445;
				}

				ServerSocket servSock = new ServerSocket(port);

				Display disp = Display.getDefault();
				final int tport = port;
				disp.asyncExec(new Runnable() {
					public void run() {
						TreeItem ti = new TreeItem(viewer, SWT.NONE);
						ti.setText("Trace listener listening on port " + tport
								+ " for trace messages");
						ti.setBackground(Display.getDefault().getSystemColor(
								SWT.COLOR_INFO_BACKGROUND));
					}
				});

				while (true) {
					Socket sock = servSock.accept();
					TraceMonitor traceMon = new TraceMonitor(sock);
					/*
					 * Display display = Display.getCurrent(); if(display ==
					 * null) { display = Display.getDefault(); }
					 * display.asyncExec(traceMon);
					 */
					new Thread(traceMon).start();
				}
			} catch (Exception ex) {
				DfLogger.error(this, "Error Creating a Socket", null, ex);
				Display display = Display.getCurrent();
				if (display == null) {
					display = Display.getDefault();
				}
				final int tport = port;
				final String emsg = ex.getMessage();
				display.asyncExec(new Runnable() {

					public void run() {
						TreeItem ti = new TreeItem(viewer, SWT.NONE);
						ti.setText("Error Creating a socket on port: " + tport
								+ ". Tracing will not function correctly. "
								+ emsg);
						ti.setBackground(Display.getDefault().getSystemColor(
								SWT.COLOR_RED));
					}

				});
				listenerThr = null;
			}

		}

	}

	public class TraceMonitor implements Runnable {

		private Socket sock = null;

		TraceMonitor(Socket sock) {
			this.sock = sock;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				ObjectInputStream ois = new ObjectInputStream(
						sock.getInputStream());

				while (true) {

					try {
						final LoggingEvent evt = (LoggingEvent) ois
								.readObject();
						if (doTrace) {
							// print only if tracing is on.
							// It is necessary to still empty
							// the pipe otherwise the logging (i.e. the sender)
							// app seems to be hanging.
							final String msg = evt.getMessage().toString();

							// System.out.println(evt.getMessage());

							try {

								Thread.sleep(DevprogPlugin
										.getDefault()
										.getPreferenceStore()
										.getInt(PreferenceConstants.P_TRACE_DELAY));
								// put sleep in this thread and not the UI
								// thread for obvious reasons
							} catch (InterruptedException ie) {
							}

							Display display = Display.getCurrent();
							if (display == null) {
								display = Display.getDefault();
							}
							display.asyncExec(new Runnable() {
								public void run() {
									TreeItem ti = new TreeItem(viewer, SWT.NONE);
									for (int i = 0; i < triggerWords.length; i++) {
										if (msg.indexOf(triggerWords[i]) != -1) {
											ti.setBackground(new Color(Display
													.getDefault(), 180, 200,
													254));
											break;
										}
									}

									if ((msg.indexOf("DfException") != -1)
											|| (msg.indexOf("Exception") != -1)
											|| (msg.indexOf("Error") != -1)) {
										ti.setBackground(new Color(Display
												.getDefault(), 232, 72, 80));
										// ti.setImage(PluginHelper.getImage("error_obj.gif"));
									}

									ti.setText(msg);
									viewer.showItem(ti);
								}
							});
						}// if(doTrace)

					} catch (ClassNotFoundException cnfe) {
						cnfe.printStackTrace();
					}

				}
			} catch (IOException ioe) {
				DfLogger.warn(this, "IO Error in reading Socket Stream", null,
						ioe);
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse
	 * .swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt
	 * .events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == viewer) {
			TreeItem selItem = viewer.getSelection()[0];
			String evtMsg = selItem.getText();
			DFCTraceEntry de = new DFCTraceEntry(evtMsg);
			PluginHelper.showDFCTraceEntry(de);
		}

		// System.out.println(e.getSource().getClass());

	}

}
