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
 * Created on Mar 23, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.api;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfSession;

import com.documentum.devprog.eclipse.common.MRUItem;
import com.documentum.devprog.eclipse.common.MRUQueue;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * API Composite.
 * 
 * Used Eclipse Visual Editor to make this.
 * 
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class ApiComposite extends Composite {

	private StyledText taResults = null;

	private Button butExecute = null;

	private StyledText stxtCmd = null;

	private Text taInfo = null;

	private List lstSuggests = null;

	private Button butScriptFile = null;

	private MRUQueue oldApis = new MRUQueue();

	private static HashMap hsSet = null;

	private static HashMap hsGet = null;

	private static HashMap hsExec = null;

	private static String tokens = "," + System.getProperty("line.separator");

	private Label lblCmd = null;

	private Label lblReference = null;

	private Label lblResuts = null;

	private Button butClearResults = null;

	static {
		hsSet = new HashMap();
		hsGet = new HashMap();
		hsExec = new HashMap();

		URL execFile = PluginHelper.getPluginFile("data/exec_api_methods.txt");
		readFile(hsExec, execFile);

		URL getFile = PluginHelper.getPluginFile("data/get_api_methods.txt");
		readFile(hsGet, getFile);

		URL setFile = PluginHelper.getPluginFile("data/set_api_methods.txt");
		readFile(hsSet, setFile);

	}

	private static void readFile(HashMap hs, URL file) {
		String metPre = "METH=";
		String infoPre = "INFO=";
		try {
			BufferedReader bufw = new BufferedReader(new InputStreamReader(
					file.openStream()));
			String line = null;
			String curCmd = null;
			String curInfo = null;
			while ((line = bufw.readLine()) != null) {
				line = line.trim();
				if (line.startsWith(metPre)) {
					curCmd = line.substring(metPre.length());
				} else if (line.startsWith(infoPre)) {
					curInfo = line.substring(infoPre.length());
					hs.put(curCmd, curInfo);
				}
			}
		} catch (Exception ioe) {
			DfLogger.error(ApiComposite.class.getName(),
					"Error reading method names", null, ioe);
		}
	}

	public ApiComposite(Composite parent, int style) {
		super(parent, style);
		initialize();

	}

	private void initialize() {
		taResults = new StyledText(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		taResults.setBounds(new org.eclipse.swt.graphics.Rectangle(16, 220,
				587, 400));
		butExecute = new Button(this, SWT.NONE);
		butExecute.setBounds(new org.eclipse.swt.graphics.Rectangle(421, 45,
				63, 28));
		butExecute.setText("Execute");
		butExecute
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (PluginState.getDocbase() == null
								|| PluginState.getDocbase().length() == 0) {
							MessageDialog.openWarning(stxtCmd.getShell(),
									"Repository",
									"No Repository Selected. Please Select a Repository");
							return;
						}
						String cmdStr = stxtCmd.getText().trim();
						if (cmdStr.charAt(0) == '@') {
							executeScriptFile(cmdStr.substring(1));
						} else {
							String retVal = executeCommand(cmdStr);
							taResults.append(taResults.getLineDelimiter());
							taResults.append(retVal);
						}
						oldApis.addToMRUQueue(new MRUItem(cmdStr, cmdStr,
								System.currentTimeMillis()));
					}
				});

		stxtCmd = new StyledText(this, SWT.SINGLE);
		stxtCmd.setFont(new Font(Display.getDefault(), "Arial", 10, SWT.NORMAL));
		stxtCmd.setBounds(new org.eclipse.swt.graphics.Rectangle(17, 44, 394,
				34));
		taInfo = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL
				| SWT.READ_ONLY);
		taInfo.setBounds(new org.eclipse.swt.graphics.Rectangle(150, 120, 451,
				48));
		taInfo.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		lstSuggests = new List(this, SWT.V_SCROLL);
		lstSuggests.setBounds(new org.eclipse.swt.graphics.Rectangle(15, 90,
				113, 81));
		lstSuggests.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		butScriptFile = new Button(this, SWT.NONE);
		butScriptFile.setBounds(new org.eclipse.swt.graphics.Rectangle(496, 45,
				103, 29));
		butScriptFile.setText("Choose Script File");
		lblCmd = new Label(this, SWT.NONE);
		lblCmd.setBounds(new org.eclipse.swt.graphics.Rectangle(15, 18, 95, 19));
		lblCmd.setText("Command");
		lblReference = new Label(this, SWT.NONE);
		lblReference.setBounds(new org.eclipse.swt.graphics.Rectangle(150, 90,
				106, 13));
		lblReference.setText("Reference");
		lblResuts = new Label(this, SWT.NONE);
		lblResuts.setBounds(new org.eclipse.swt.graphics.Rectangle(15, 184, 76,
				27));
		lblResuts.setText("Results");
		butClearResults = new Button(this, SWT.NONE);
		butClearResults.setBounds(new org.eclipse.swt.graphics.Rectangle(468,
				182, 133, 29));
		butClearResults.setText("Clear Results Area");
		butClearResults
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						taResults.setText("");
					}
				});
		butScriptFile
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						FileDialog fd = new FileDialog(stxtCmd.getShell());
						fd.setFilterExtensions(new String[] { "*.api", "*.*" });
						String file = fd.open();
						if (file != null) {
							stxtCmd.setText("@" + file);
						}
					}
				});
		lstSuggests
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						String sel = lstSuggests.getSelection()[0];
						stxtCmd.setText(sel + ",c");
						showInfo(sel);
					}
				});
		this.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		stxtCmd.addExtendedModifyListener(new org.eclipse.swt.custom.ExtendedModifyListener() {
			public void modifyText(org.eclipse.swt.custom.ExtendedModifyEvent e) {
				try {
					if (e.length == 1
							&& stxtCmd.getTextRange(e.start, 1).equals(",")) {

						Token tok = getToken(stxtCmd, e.start - 1, tokens);
						// System.out.println(tok.start + ":" + tok.text
						// + ":"
						// + tok.isFirst);
						if (tok.isFirst) {
							String strTok = tok.text;
							showInfo(strTok);
							stxtCmd.append("c");
							stxtCmd.setCaretOffset(stxtCmd.getCharCount());
						}

					} else {
						String cmdStr = stxtCmd.getText().trim();
						String[] toks = cmdStr.split(",");
						if (toks.length == 1) {
							String[] matches = getMatches(toks[0].toLowerCase());
							// System.out.println("Found matches: " +
							// matches.length);
							/*
							 * PopupList lst = new
							 * PopupList(stxtCmd.getShell(),SWT.H_SCROLL |
							 * SWT.V_SCROLL); lst.setItems(matches); Rectangle
							 * rect = new Rectangle(10,10,100,100);
							 * lst.open(rect); stxtCmd.forceFocus();
							 */
							lstSuggests.removeAll();
							for (int i = 0; i < matches.length; i++) {
								lstSuggests.add(matches[i]);
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		stxtCmd.addVerifyKeyListener(new org.eclipse.swt.custom.VerifyKeyListener() {
			public void verifyKey(org.eclipse.swt.events.VerifyEvent e) {

				if (e.keyCode == 13) {
					String ct = stxtCmd.getText().trim();
					String[] apiToks = ct.split(",");
					if (apiToks.length <= 1) {
						if (lstSuggests.getSelection().length > 0) {
							stxtCmd.setText(lstSuggests.getSelection()[0]
									+ ",c");
							stxtCmd.setCaretOffset(stxtCmd.getCharCount());
						}
					} else {
						if (PluginState.getDocbase() == null
								|| PluginState.getDocbase().length() == 0) {
							MessageDialog.openWarning(stxtCmd.getShell(),
									"Repository",
									"No Repository Selected. Please Select a Repository");
							return;
						}
						// System.out.println("enter pressed: exec
						// command");
						String retVal = executeCommand(ct);
						taResults.append(taResults.getLineDelimiter());
						taResults.append(retVal);
						oldApis.addToMRUQueue(new MRUItem(ct, ct, System
								.currentTimeMillis()));
					}
				}
				if (e.keyCode == SWT.ARROW_DOWN) {
					String[] apiToks = stxtCmd.getText().trim().split(",");

					if (apiToks.length == 1 && (apiToks[0].charAt(0) != '@')) {
						if (lstSuggests.getItemCount() > 0) {
							int curIndx = lstSuggests.getSelectionIndex();
							if (curIndx < 0
									|| (curIndx >= lstSuggests.getItemCount() - 1))
								curIndx = -1;

							lstSuggests.setSelection(curIndx + 1);
							showInfo(lstSuggests.getSelection()[0]);
						}
					} else {
						String ct = stxtCmd.getText().trim();
						MRUItem mi = null;
						if (ct == null || ct.length() == 0) {
							mi = oldApis.tail();
						} else {
							mi = oldApis.getPrevious(ct);
							if (mi == null) {
								mi = oldApis.tail();
							}
						}
						if (mi != null) {
							stxtCmd.setText(mi.getData());
						}

					}
				}
				if (e.keyCode == SWT.ARROW_UP) {
					String cmdStr = stxtCmd.getText().trim();
					String[] apiToks = cmdStr.split(",");
					if ((cmdStr.length() > 0) && apiToks.length == 1
							&& (apiToks[0].charAt(0) != '@')) {
						if (lstSuggests.getItemCount() > 0) {
							int curIndx = lstSuggests.getSelectionIndex();
							if (curIndx <= 0
									|| (curIndx >= lstSuggests.getItemCount() - 1))
								curIndx = lstSuggests.getItemCount();

							lstSuggests.setSelection(curIndx - 1);
							showInfo(lstSuggests.getSelection()[0]);
						}
					} else {
						MRUItem mi = null;
						if (cmdStr == null || cmdStr.length() == 0) {
							mi = oldApis.peek();
						} else {
							mi = oldApis.getNext(cmdStr);
							if (mi == null) {
								mi = oldApis.peek();
							}
						}
						if (mi != null) {
							stxtCmd.setText(mi.getData());
						}
					}
				}

			}
		});
		this.setSize(new org.eclipse.swt.graphics.Point(696, 590));
	} /*
	 * private void executeCommand() { String cmdStr = stxtCmd.getText().trim();
	 * // get cmd name int indxParen = cmdStr.indexOf("("); if (indxParen == -1)
	 * { MessageDialog.openError(stxtCmd.getShell(), "Syntax Error", "Unable to
	 * determine the api"); return; } String apiName = cmdStr.substring(0,
	 * indxParen); System.out.println("apiName: " + apiName);
	 * 
	 * if (apiName.equalsIgnoreCase("dmapiexec") ||
	 * apiName.equalsIgnoreCase("dmapiget")) { String cmdArgs =
	 * cmdStr.substring(indxParen + 1).trim(); String[] args = null; int
	 * indxApos = cmdArgs.indexOf("\""); if (indxApos == -1) { indxApos =
	 * cmdArgs.indexOf("'"); if (indxApos != -1) { args = cmdArgs.split("'"); }
	 * } else { args = cmdArgs.split("\""); } if (args == null || args.length ==
	 * 0) { MessageDialog.openError(stxtCmd.getShell(), "Syntax Error", "Unable
	 * to determine the command"); return; }
	 * 
	 * String cmd = null; cmdArgs = args[0]; int indxComma =
	 * cmdArgs.indexOf(","); if (indxComma == -1) { cmd = cmdArgs; } else { cmd
	 * = cmdArgs.substring(0, indxComma); cmdArgs = cmdArgs.substring(indxComma
	 * + 1); }
	 * 
	 * IDfSession sess = null; try { sess = PluginState.getSession(); if
	 * (apiName.equalsIgnoreCase("")) ; } finally { } } }
	 */

	private String executeCommand(String cmdStr) {

		String[] toks = cmdStr.split(",");
		if (toks.length < 2) {
			MessageDialog.openError(stxtCmd.getShell(), "Syntax Error",
					"Unable to parse api command");
			return "Err";
		}

		// StringBuffer returnValue = new StringBuffer(32);
		// String nl = System.getProperty("line.separator");
		// String nl = taResults.getLineDelimiter();
		String returnValue = "";
		IDfSession sess = null;
		try {
			sess = PluginState.getSession();
			String cmd = toks[0];
			// returnValue.append("API Command: ").append(cmd).append(nl);
			if ((hsGet.containsKey(cmd))) {
				String args = "";
				if (toks.length > 2) {
					args = createArgs(toks, 2, toks.length - 1);
				}
				// returnValue.append("Command Type: dmAPIGet").append(nl);
				// returnValue.append("Arguments: ").append(args).append(nl);
				// returnValue.append("Results:").append(nl);
				String res = sess.apiGet(cmd, args);
				// returnValue.append(res);
				returnValue = res;
			} else if (hsExec.containsKey(cmd)) {
				String args = "";

				if (toks.length > 2) {
					args = createArgs(toks, 2, toks.length - 1);
				}
				// returnValue.append("Command Type: dmAPIExec").append(nl);
				// returnValue.append("Arguments: ").append(args).append(nl);
				String res = "" + sess.apiExec(cmd, args);
				// returnValue.append("Results:").append(nl).append(res);
				returnValue = res;
			} else if (hsSet.containsKey(cmd)) {
				if (toks.length > 2) {
					String args = createArgs(toks, 2, toks.length - 2);
					// returnValue.append("Command Type: dmAPISet").append(nl);
					// returnValue.append("Arguments: ").append(args).append(nl);
					String val = toks[toks.length - 1];
					// returnValue.append("Value: ").append(val).append(nl);
					// returnValue.append("Results: ").append(nl);
					// returnValue.append(sess.apiSet(cmd, args, val));
					returnValue = "" + sess.apiSet(cmd, args, val);
				} else {
					// returnValue.append("Unable to parse api command").append(nl);
					returnValue = "Unable to parse api command";
				}
			}
		} catch (DfException dfe) {
			DfLogger.error(this, "Error executing api command", null, dfe);
			// returnValue.append(dfe.getMessage());
			returnValue = dfe.getMessage();
		}
		// returnValue.append(nl);
		return returnValue;
	}

	private void executeScriptFile(String filename) {
		try {
			taResults.setText("");
			BufferedReader bufr = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = bufr.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 && (line.startsWith("#") == false)) {
					String cmd = line;
					String[] toks = line.split(",");
					if (toks.length >= 4) {
						if (toks[0].equalsIgnoreCase("set")
								&& (toks.length == 4)) {
							// means value to be set is on next line
							String nextLine = bufr.readLine();
							if (nextLine != null) {
								nextLine = nextLine.trim();
								cmd = line + "," + nextLine;
							}
						}
					}
					String retVal = executeCommand(cmd);
					taResults.append(retVal);
					taResults.append(taResults.getLineDelimiter());
					taResults.append(taResults.getLineDelimiter());
				}
			}
			bufr.close();
		} catch (Exception ex) {
			DfLogger.error(this, "Error executing api script", null, ex);
			MessageDialog.openError(stxtCmd.getShell(),
					"Script Execution Error", ex.getMessage());
		}

	}

	private String createArgs(String[] toks, int start, int end) {
		StringBuffer bufArgs = new StringBuffer(16);
		for (int i = start; i <= end; i++) {
			bufArgs.append(toks[i]);
			if (i != end) {
				bufArgs.append(",");
			}
		}
		return bufArgs.toString();
	}

	private String[] getMatches(String prefix) {
		ArrayList lst = new ArrayList(30);
		checkInMap(hsSet, prefix, lst);
		checkInMap(hsGet, prefix, lst);
		checkInMap(hsExec, prefix, lst);
		return (String[]) lst.toArray(new String[] {});
	}

	private void showInfo(String cmd) {
		String info = null;
		if (hsExec.containsKey(cmd)) {
			info = (String) hsExec.get(cmd);
		} else if (hsSet.containsKey(cmd)) {
			info = (String) hsSet.get(cmd);
		} else if (hsGet.containsKey(cmd)) {
			info = (String) hsGet.get(cmd);
		}
		if (info != null) {
			taInfo.setText(info);

		}
	}

	private void checkInMap(HashMap mp, String prefx, ArrayList lst) {
		Iterator iter = mp.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if (key.startsWith(prefx)) {
				lst.add(key);
			}
		}
	}

	private Token getToken(StyledText stxt, int pos, String tokens) {
		int cnt = stxt.getCharCount();
		if ((pos < 0) || (pos > (cnt - 1))) {
			return null;
		}

		int lineIndex = stxt.getLineAtOffset(pos);
		int lineFirstIndex = stxt.getOffsetAtLine(lineIndex);

		Token tok = new Token();

		int cntr = pos;
		StringBuffer bufToken = new StringBuffer(12);
		while (cntr >= 0) {
			String curChar = stxt.getText(cntr, cntr);
			if (tokens.indexOf(curChar) != -1) {
				tok.start = cntr + 1;
				break;
			}
			bufToken.append(curChar);
			cntr--;
		}

		bufToken.reverse();

		cntr = pos + 1;
		while (cntr < cnt) {
			String curChar = stxt.getText(cntr, cntr);
			if (tokens.indexOf(curChar) != -1) {
				break;
			}
			bufToken.append(curChar);
			cntr++;
		}

		tok.text = bufToken.toString();

		if (lineFirstIndex == tok.start) {
			tok.isFirst = true;
		}
		return tok;
	}

	class Token {
		boolean isFirst = false;

		int start = 0;

		String text = "";
	}
} // @jve:decl-index=0:visual-constraint="10,10"
