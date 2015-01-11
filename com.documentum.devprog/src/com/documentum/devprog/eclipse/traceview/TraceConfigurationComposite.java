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
 * Created on Sep 18, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.traceview;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.common.PreferenceConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Font;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class TraceConfigurationComposite extends Composite {

	private Label lblPropFile = null;

	private Text txtDFCPropFile = null;

	private Button butChoosePropFile = null;

	private Button chkEnableTracing = null;

	private Group grpTraceProperties = null;

	private Button chkRecordParams = null;

	private Button chkRecordReturnValue = null;

	private Button chkCompactMode = null;

	private Label lblStackDepth = null;

	private Text txtStackDepth = null;

	private Label lblPort = null;

	private Text txtPort = null;

	private Label lblLog4jFile = null;

	private Text txtLog4jPropFile = null;

	private Button butChooseLog4jPropFile = null;

	private Properties dfcProperties = new Properties(); // @jve:decl-index=0:

	private Properties log4jProperties = new Properties();

	private static final String PROP_ENABLE_TRACE = "dfc.tracing.enabled";

	private static final String PROP_RECORD_PARAMS = "dfc.tracing.recordParameters";

	private static final String PROP_RECORD_RET_VAL = "dfc.tracing.recordReturnValue";

	private static final String PROP_COMPACT_MODE = "dfc.tracing.compactMode";

	private static final String PROP_COMBINE_DMCL = "dfc.tracing.combineDMCL";

	private static final String PROP_STACK_DEPTH = "dfc.tracing.stackDepth";

	private static final String PROP_LOG4J_APPENDER = "log4j.appender.FILE_TRACE";

	private static final String PROP_LOG4J_RHOST = PROP_LOG4J_APPENDER
			+ ".RemoteHost";

	private static final String PROP_LOG4J_PORT = PROP_LOG4J_APPENDER + ".Port"; // @jve:decl-index=0:

	private static final String PROP_LOG4J_LOCINFO = PROP_LOG4J_APPENDER
			+ ".LocationInfo";

	private Button chkCombineDMCL = null;

	private Label lblStatus = null;

	private Label label = null;

	public TraceConfigurationComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		initProperties();
	}

	private void initialize() {
		lblPropFile = new Label(this, SWT.NONE);
		lblPropFile.setBounds(new Rectangle(15, 30, 106, 18));
		lblPropFile.setText("DFC Properties File");
		txtDFCPropFile = new Text(this, SWT.BORDER);
		txtDFCPropFile.setBounds(new Rectangle(134, 30, 349, 21));
		txtDFCPropFile.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		txtDFCPropFile.setEditable(false);
		butChoosePropFile = new Button(this, SWT.NONE);
		butChoosePropFile.setBounds(new Rectangle(495, 30, 63, 22));
		butChoosePropFile.setText("Choose");
		butChoosePropFile
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						FileDialog fd = new FileDialog(getShell(), SWT.OPEN
								| SWT.SINGLE);
						fd.setFilterExtensions(new String[] { "*.properties" });
						String flname = fd.open();
						if (flname != null) {
							txtDFCPropFile.setText(flname);
							loadDFCPropertiesFile();
						}
					}
				});
		chkEnableTracing = new Button(this, SWT.CHECK);
		chkEnableTracing.setBounds(new Rectangle(15, 105, 286, 17));
		chkEnableTracing.setText("Enable Tracing for Repoint Trace View");
		chkEnableTracing
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (chkEnableTracing.getSelection()) {
							dfcProperties
									.setProperty(PROP_ENABLE_TRACE, "true");
							enableTraceControls(true);
							enableSocketAppender();
						} else {
							dfcProperties.setProperty(PROP_ENABLE_TRACE,
									"false");
							enableTraceControls(false);
							disableSocketAppender();
						}
					}
				});
		createGrpTraceProperties();
		this.setSize(new Point(569, 463));
		setLayout(null);
		lblLog4jFile = new Label(this, SWT.NONE);
		lblLog4jFile.setBounds(new Rectangle(15, 61, 106, 18));
		lblLog4jFile.setText("Log4j Properties File");
		txtLog4jPropFile = new Text(this, SWT.BORDER);
		txtLog4jPropFile.setBounds(new Rectangle(134, 60, 349, 21));
		txtLog4jPropFile.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		txtLog4jPropFile.setEditable(false);
		butChooseLog4jPropFile = new Button(this, SWT.NONE);
		butChooseLog4jPropFile.setBounds(new Rectangle(494, 59, 63, 21));
		butChooseLog4jPropFile.setText("Choose");
		lblStatus = new Label(this, SWT.NONE);
		lblStatus.setBounds(new Rectangle(14, 431, 542, 20));
		lblStatus.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		lblStatus.setText("");
		label = new Label(this, SWT.NONE);
		label.setBounds(new Rectangle(13, 126, 540, 18));
		label.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		label.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.ITALIC));
		label.setText("NOTE: For changes to take effect, you must restart the application (e.g. Tomcat) being traced.");
		butChooseLog4jPropFile
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
						fd.setFilterExtensions(new String[] { "*.properties" });
						String flname = fd.open();
						if (flname != null) {
							txtLog4jPropFile.setText(flname);
							loadLog4jPropertiesFile();
						}
					}
				});
	}

	/**
	 * This method initializes grpTraceProperties
	 * 
	 */
	private void createGrpTraceProperties() {
		grpTraceProperties = new Group(this, SWT.NONE);
		grpTraceProperties.setLayout(null);
		grpTraceProperties.setText("Trace Properties");
		grpTraceProperties.setBounds(new Rectangle(31, 150, 524, 271));
		chkRecordParams = new Button(grpTraceProperties, SWT.CHECK);
		chkRecordParams.setBounds(new Rectangle(33, 19, 247, 21));
		chkRecordParams
				.setToolTipText("Specifies whether to record the parameter information of methods, if any, while tracing.");
		chkRecordParams.setText("Record Parameters");
		chkRecordParams
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (chkRecordParams.getSelection()) {
							dfcProperties.setProperty(PROP_RECORD_PARAMS, "on");
						} else {
							dfcProperties
									.setProperty(PROP_RECORD_PARAMS, "off");
						}
					}
				});
		chkRecordReturnValue = new Button(grpTraceProperties, SWT.CHECK);
		chkRecordReturnValue.setBounds(new Rectangle(33, 59, 256, 16));
		chkRecordReturnValue
				.setToolTipText("Specifies whether to record the return value of methods, if any, while tracing.");
		chkRecordReturnValue.setText("Record Return Value");
		chkRecordReturnValue
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (chkRecordReturnValue.getSelection()) {
							dfcProperties
									.setProperty(PROP_RECORD_RET_VAL, "on");
						} else {
							dfcProperties.setProperty(PROP_RECORD_RET_VAL,
									"off");
						}

					}
				});
		chkCompactMode = new Button(grpTraceProperties, SWT.CHECK);
		chkCompactMode.setBounds(new Rectangle(33, 103, 255, 16));
		chkCompactMode
				.setToolTipText("Specifies whether to output multiple trace outputs on to a single line or not.");
		chkCompactMode.setText("Compact Mode");
		chkCompactMode
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (chkCompactMode.getSelection()) {
							dfcProperties.setProperty(PROP_COMPACT_MODE, "on");
						} else {
							dfcProperties.setProperty(PROP_COMPACT_MODE, "off");
						}
					}
				});
		lblStackDepth = new Label(grpTraceProperties, SWT.NONE);
		lblStackDepth.setBounds(new Rectangle(31, 205, 107, 22));
		lblStackDepth
				.setToolTipText("Specifies how many levels deep the method calls need to be traced. Valid values: 0 and above.");
		lblStackDepth.setText("Stack Depth");
		txtStackDepth = new Text(grpTraceProperties, SWT.BORDER);
		txtStackDepth.setBounds(new Rectangle(155, 203, 357, 22));
		txtStackDepth
				.setToolTipText("Specifies how many levels deep the method calls need to be traced.Valid values: 0 and above.");
		txtStackDepth
				.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {

						String num = txtStackDepth.getText();
						if (num.length() > 0) {
							try {
								int depth = Integer.parseInt(num);
								if (depth < 0) {
									setStatus(
											"Depth should be greater than or equal to zero",
											true);

								} else {
									dfcProperties.setProperty(PROP_STACK_DEPTH,
											num);
								}
							} catch (NumberFormatException nfe) {
								setStatus("Invalid Stack Depth Specified", true);
							}
						}
					}
				});

		lblPort = new Label(grpTraceProperties, SWT.NONE);
		lblPort.setBounds(new Rectangle(30, 236, 109, 17));
		lblPort.setText("Port Number");
		txtPort = new Text(grpTraceProperties, SWT.BORDER);
		txtPort.setBounds(new Rectangle(155, 236, 358, 19));
		txtPort.setToolTipText("The port number to which the process being traced sends logs to Repoint. This should be the same as the Port in the menu Repoint->Preferences->Trace View");
		txtPort.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				if (txtPort.getText().length() > 0) {
					try {
						int portn = Integer.parseInt(txtPort.getText());
						DevprogPlugin.getDefault().getPreferenceStore()
								.setValue(PreferenceConstants.P_PORT, portn);
						log4jProperties.setProperty(PROP_LOG4J_PORT, txtPort
								.getText().trim());
					} catch (NumberFormatException nfe) {

						setStatus("Invalid Port Number", true);
					}
				}

			}
		});
		chkCombineDMCL = new Button(grpTraceProperties, SWT.CHECK);
		chkCombineDMCL.setBounds(new Rectangle(33, 148, 256, 17));
		chkCombineDMCL
				.setToolTipText("Specifies whether to combine the trace of dmcl along with other traces.");
		chkCombineDMCL.setText("Combine DMCL Traces");
		chkCombineDMCL
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (chkCombineDMCL.getSelection()) {
							dfcProperties
									.setProperty(PROP_COMBINE_DMCL, "true");
						} else {
							dfcProperties.setProperty(PROP_COMBINE_DMCL,
									"false");
						}
					}
				});
	}

	private void initProperties() {
		String dfcPropFile = PluginState.getDFCPropertiesFile();
		if (dfcPropFile != null) {
			txtDFCPropFile.setText(dfcPropFile);
			loadDFCPropertiesFile();

		}

		String log4jPropFile = PluginState.getLog4jPropertiesFile();
		if (log4jPropFile != null) {
			txtLog4jPropFile.setText(log4jPropFile);
			loadLog4jPropertiesFile();
		}
		updateControlsFromFiles();
	}

	private void updateControlsFromFiles() {
		String enableTr = dfcProperties.getProperty(PROP_ENABLE_TRACE);
		if (enableTr != null && enableTr.equalsIgnoreCase("true")) {
			chkEnableTracing.setSelection(true);
			enableSocketAppender();
			enableTraceControls(true);
		} else {
			chkEnableTracing.setSelection(false);
			enableTraceControls(false);
		}

		String compMode = dfcProperties.getProperty(PROP_COMPACT_MODE);
		if (compMode != null && compMode.equalsIgnoreCase("on")) {
			chkCompactMode.setSelection(true);
		} else {
			chkCompactMode.setSelection(false);
		}

		String recParams = dfcProperties.getProperty(PROP_RECORD_PARAMS);
		if (recParams != null && recParams.equalsIgnoreCase("on")) {
			chkRecordParams.setSelection(true);
		} else {
			chkRecordParams.setSelection(false);
		}

		String recRetVal = dfcProperties.getProperty(this.PROP_RECORD_RET_VAL);
		if (recRetVal != null && recRetVal.equalsIgnoreCase("on")) {
			chkRecordReturnValue.setSelection(true);

		} else {
			chkRecordReturnValue.setSelection(false);
		}

		String combineDMCL = dfcProperties.getProperty(this.PROP_COMBINE_DMCL);
		if (combineDMCL != null && combineDMCL.equalsIgnoreCase("true")) {
			chkCombineDMCL.setSelection(true);
		} else {
			chkCombineDMCL.setSelection(false);
		}

		String stkDepth = dfcProperties.getProperty(this.PROP_STACK_DEPTH);
		if (stkDepth != null) {
			txtStackDepth.setText(stkDepth);
		}

		String portNum = log4jProperties.getProperty(this.PROP_LOG4J_PORT);
		if (portNum != null) {
			txtPort.setText(portNum);
		} else {
			Preferences prefs = DevprogPlugin.getDefault()
					.getPluginPreferences();
			txtPort.setText(prefs.getInt(PreferenceConstants.P_PORT) + "");
		}
	}

	private void loadDFCPropertiesFile() {
		try {
			FileInputStream fin = new FileInputStream(txtDFCPropFile.getText());
			dfcProperties.clear();
			dfcProperties.load(fin);
			updateControlsFromFiles();
		} catch (IOException ioe) {
			setStatus("Error opening DFC Properties File", true);

		}
	}

	private void loadLog4jPropertiesFile() {
		try {
			FileInputStream fin = new FileInputStream(
					txtLog4jPropFile.getText());
			log4jProperties.clear();
			log4jProperties.load(fin);
			updateControlsFromFiles();
		} catch (IOException ioe) {
			setStatus(
					"Error loading Log4j Properties File: " + ioe.getMessage(),
					true);
		}
	}

	private void enableTraceControls(boolean enable) {
		chkCombineDMCL.setEnabled(enable);
		chkCompactMode.setEnabled(enable);
		chkRecordParams.setEnabled(enable);
		chkRecordReturnValue.setEnabled(enable);
		txtPort.setEnabled(enable);
		txtStackDepth.setEnabled(enable);
	}

	private void enableSocketAppender() {
		log4jProperties.setProperty(PROP_LOG4J_APPENDER,
				"org.apache.log4j.net.SocketAppender");
		log4jProperties.setProperty(PROP_LOG4J_RHOST, "localhost");

		if (log4jProperties.containsKey(PROP_LOG4J_PORT) == false) {
			Preferences prefs = DevprogPlugin.getDefault()
					.getPluginPreferences();
			String p = "" + prefs.getInt(PreferenceConstants.P_PORT);
			log4jProperties.setProperty(PROP_LOG4J_PORT, p);
			txtPort.setText(p);
		}
	}

	private void disableSocketAppender() {
		log4jProperties.setProperty(PROP_LOG4J_APPENDER,
				"org.apache.log4j.RollingFileAppender");
	}

	public void saveChanges() {
		try {
			saveProperties(dfcProperties, txtDFCPropFile.getText());
			saveProperties(log4jProperties, txtLog4jPropFile.getText());

		} catch (IOException ioe) {
			MessageDialog.openError(getShell(), "Save Error",
					"Error saving property files");
		}
	}

	public void saveProperties(Properties props, String file)
			throws IOException {
		BufferedReader bufR = new BufferedReader(new FileReader(file));
		String line = "";
		String nl = System.getProperty("line.separator");
		StringBuffer bufOp = new StringBuffer(256);

		Set printedKeys = new HashSet();

		while ((line = bufR.readLine()) != null) {
			if (line.startsWith("#") == false) {
				int indx = line.indexOf('=');

				if (indx > 0) {
					String k = line.substring(0, indx);
					String v = line.substring(indx + 1);
					String val = props.getProperty(k);
					if (val == null)
						val = v;

					bufOp.append(k).append("=").append(val);
					printedKeys.add(k);
					System.out.println("Printed key: " + k);
				} else {
					// maybe a blank line
					bufOp.append(line);
				}

			} else {
				bufOp.append(line);
			}
			bufOp.append(nl);
		}
		bufR.close();

		// print keys present in properties but not in file
		Iterator iterAllKeys = props.keySet().iterator();
		while (iterAllKeys.hasNext()) {
			String key = (String) iterAllKeys.next();
			if (printedKeys.contains(key) == false) {
				String val = (String) props.get(key);
				bufOp.append(key).append("=").append(val);
				bufOp.append(nl);
			}
		}

		BufferedWriter bufW = new BufferedWriter(new FileWriter(file));
		bufW.write(bufOp.toString());
		bufW.close();
	}

	private void setStatus(String msg, boolean error) {
		if (error) {
			lblStatus.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_RED));
		} else {
			lblStatus.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_BLUE));
		}
		lblStatus.setText(msg);
	}

} // @jve:decl-index=0:visual-constraint="10,10"
