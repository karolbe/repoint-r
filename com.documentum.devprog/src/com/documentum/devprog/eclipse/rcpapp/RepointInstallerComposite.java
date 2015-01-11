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
 * Created on Sep 26, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.rcpapp;

import com.documentum.devprog.eclipse.common.DFCConfigurationHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class RepointInstallerComposite extends Composite {

	private Label label1 = null;

	private Text txtDfcFolder = null;

	private Button butDfcFolder = null;

	private Label labelDfcFolderInfo = null;

	private Label label2 = null;

	private Text txtConfigFolder = null;

	private Button butConfigFolder = null;

	private Label label3 = null;

	private ProgressBar progressBar = null;

	private Label label = null;

	private Text textArea = null;

	public RepointInstallerComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		postInit();
	}

	private void initialize() {
		label1 = new Label(this, SWT.NONE);
		label1.setBounds(new Rectangle(16, 59, 89, 18));
		label1.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.BOLD));
		label1.setText("DFC Jar Folder");
		txtDfcFolder = new Text(this, SWT.BORDER);
		txtDfcFolder.setBounds(new Rectangle(120, 61, 303, 18));
		butDfcFolder = new Button(this, SWT.NONE);
		butDfcFolder.setBounds(new Rectangle(434, 60, 76, 19));
		butDfcFolder.setText("Choose");
		butDfcFolder
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						DirectoryDialog dd = new DirectoryDialog(getShell());
						String fldr = dd.open();
						if (fldr != null) {
							txtDfcFolder.setText(fldr);
						}
					}
				});
		labelDfcFolderInfo = new Label(this, SWT.NONE);
		labelDfcFolderInfo.setBounds(new Rectangle(15, 84, 496, 21));
		labelDfcFolderInfo.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		labelDfcFolderInfo.setFont(new Font(Display.getDefault(), "Tahoma", 8,
				SWT.ITALIC));
		labelDfcFolderInfo
				.setText("The folder that contains the dfc.jar. All jars in this folder will be copied to the DFC plugin within repoint");
		label2 = new Label(this, SWT.NONE);
		label2.setBounds(new Rectangle(14, 119, 91, 14));
		label2.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.BOLD));
		label2.setText("Config Folder");
		txtConfigFolder = new Text(this, SWT.BORDER);
		txtConfigFolder.setBounds(new Rectangle(120, 120, 304, 19));
		butConfigFolder = new Button(this, SWT.NONE);
		butConfigFolder.setBounds(new Rectangle(435, 118, 76, 20));
		butConfigFolder.setText("Choose");
		butConfigFolder
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						DirectoryDialog dd = new DirectoryDialog(getShell());
						String fldr = dd.open();
						if (fldr != null) {
							txtConfigFolder.setText(fldr);
						}
					}
				});
		label3 = new Label(this, SWT.NONE);
		label3.setBounds(new Rectangle(16, 144, 496, 18));
		label3.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		label3.setFont(new Font(Display.getDefault(), "Tahoma", 8, SWT.ITALIC));
		label3.setText("The folder that contains the dfc.properties file (e.g. C:\\Documentum\\config). ");
		progressBar = new ProgressBar(this, SWT.SMOOTH);
		progressBar.setBounds(new Rectangle(16, 178, 496, 19));
		progressBar.setMaximum(2);
		label = new Label(this, SWT.NONE);
		label.setBounds(new Rectangle(14, 17, 510, 30));
		label.setText("Please specify the location for DFC jars and the configuration files. ");
		textArea = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		textArea.setBounds(new Rectangle(14, 208, 498, 91));

		this.setSize(new Point(544, 330));
		setLayout(null);
	}

	private void postInit() {
		String df = this.guessDFCLocation();
		if (df != null)
			txtDfcFolder.setText(df);

		String cf = this.guessConfigLocation();
		if (cf != null) {
			txtConfigFolder.setText(cf);
		}
	}

	public void doInstall() {
		try {

			File home = new File("");

			File dfcPlugLibFldr = InstallerHelp.getDfcPluginLibFolder(home);

			textArea.append("Attempting to copy DFC jars");
			InstallerHelp
					.copyFolderContents(new File(this.txtDfcFolder.getText()),
							dfcPlugLibFldr, null);
			progressBar.setSelection(1);
			textArea.append(textArea.getLineDelimiter());
			textArea.append("DFC jars successfully copied");

			textArea.append(textArea.getLineDelimiter());
			textArea.append("Attempting to create a jar of the properties files");
			File confFldr = InstallerHelp.getDfcPluginConfFolder(home);

			String jarFl = confFldr.getAbsolutePath() + File.separator
					+ "config.jar";
			InstallerHelp.createPropertiesJar(
					new File(this.txtConfigFolder.getText()), new File(jarFl));
			progressBar.setSelection(2);
			textArea.append(textArea.getLineDelimiter());
			textArea.append("config.jar successfully created");

			textArea.append(textArea.getLineDelimiter());
			textArea.append("Modifying repoint.ini file");

			modifyIniFile(home);
			textArea.append(textArea.getLineDelimiter());
			textArea.append("Finished modifying repoint.ini file");

			textArea.append(textArea.getLineDelimiter());
			textArea.append("Attempting to update the plugin.xml file");
			File dfcPluginFolder = InstallerHelp.getDfcPluginLibFolder(home)
					.getParentFile();
			modifyPluginXmlFile(dfcPluginFolder);

			String msg = "Repoint will now attempt to restart."
					+ textArea.getLineDelimiter()
					+ textArea.getLineDelimiter()
					+ "If Repoint does not start successfully or displays errors, please delete the REPOINT_HOME/workspace/.metadata/.plugins/org.eclipse.ui.workbench folder and try starting Repoint again. If that still does not work, try deleting the REPOINT_HOME/workspace folder and restart Repoint";
			MessageDialog.openInformation(getShell(), "Repoint Restart", msg);

		} catch (IOException ioe) {
			MessageDialog.openError(getShell(), "DFC Initialization Error",
					ioe.getMessage());
			textArea.append("Repoint may not function correctly since DFC was not successfully initialized");
		} catch (ParserConfigurationException pce) {
			MessageDialog.openError(getShell(),
					"Plugin.xml Modification Error", pce.getMessage());
			textArea.append("Repoint may not function correctly because the plugin.xml of the DFC plugin was not updated correctly");

		} catch (SAXException se) {
			MessageDialog.openError(getShell(),
					"Plugin.xml Modification Error", se.getMessage());
			textArea.append("Repoint may not function correctly because the plugin.xml of the DFC plugin was not updated correctly");

		} catch (Exception ex) {
			MessageDialog.openError(getShell(), "DFC Initialization Error",
					ex.getMessage());
			textArea.append("Repoint may not function correctly due to DFC initialization error");
		}
	}

	private void modifyPluginXmlFile(File pluginFolder) throws IOException,
			ParserConfigurationException, SAXException,
			TransformerConfigurationException, TransformerException {
		File pluginXml = InstallerHelp.getPluginXmlFile(pluginFolder);

		Document xmlDoc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(pluginXml);

		NodeList nlist = xmlDoc.getElementsByTagName("runtime");

		Element runtimeElem = (Element) nlist.item(0);
		System.out.println("Got runtime elem: " + runtimeElem.getNodeName());

		NodeList chList = runtimeElem.getElementsByTagName("library");
		System.out.println("Got children: " + runtimeElem.hasChildNodes());
		for (int i = 0; i < chList.getLength(); i++) {
			Node nd = chList.item(i);
			nd.getParentNode().removeChild(nd);
			System.out.println("Removed: " + nd.getNodeName());
		}

		File libFldr = InstallerHelp.getDfcPluginLibFolder(new File(""));
		addJarsToPluginXml(libFldr, runtimeElem);
		File confFldr = InstallerHelp.getDfcPluginConfFolder(new File(""));
		addJarsToPluginXml(confFldr, runtimeElem);

		DOMSource src = new DOMSource(xmlDoc);
		StreamResult res = new StreamResult(pluginXml);

		Transformer trans = TransformerFactory.newInstance().newTransformer();
		trans.transform(src, res);

	}

	private void addJarsToPluginXml(File libFldr, Element runtimeElement) {
		File[] lstjars = libFldr.listFiles();
		for (int i = 0; i < lstjars.length; i++) {

			File jf = lstjars[i];

			Element libElem = runtimeElement.getOwnerDocument().createElement(
					"library");
			Attr nameAttr = runtimeElement.getOwnerDocument().createAttribute(
					"name");
			nameAttr.setValue("lib/" + jf.getName());
			libElem.setAttributeNode(nameAttr);

			Element exportElem = runtimeElement.getOwnerDocument()
					.createElement("export");
			Attr expNameAttr = runtimeElement.getOwnerDocument()
					.createAttribute("name");
			expNameAttr.setValue("*");
			exportElem.setAttributeNode(expNameAttr);

			libElem.appendChild(exportElem);

			runtimeElement.appendChild(libElem);

		}
	}

	private void modifyIniFile(File home) throws IOException {
		File iniFile = new File(home.getAbsolutePath() + File.separator
				+ "repoint.ini");
		BufferedReader bufR = new BufferedReader(new FileReader(iniFile));
		StringBuffer bufIniFile = new StringBuffer(128);
		String line = "";
		String nl = System.getProperty("line.separator");
		while ((line = bufR.readLine()) != null) {
			String[] parts = line.split("=");
			if (parts[0].equals("-Djava.library.path")) {
				bufIniFile.append(parts[0]).append("=")
						.append(this.txtDfcFolder.getText());

			} else {
				bufIniFile.append(line);
			}
			bufIniFile.append(nl);
		}
		bufR.close();

		String strIniFile = bufIniFile.toString();
		if (strIniFile.indexOf("java.library.path") == -1) {
			bufIniFile.append("-Djava.library.path=").append(
					txtDfcFolder.getText());
			bufIniFile.append(nl);
		}

		BufferedWriter bufW = new BufferedWriter(new FileWriter(iniFile));
		bufW.write(bufIniFile.toString());
		bufW.close();
		System.getProperties().put("java.library.path",
				this.txtDfcFolder.getText());
	}

	// NOTE This method is platform specific to Windows
	private String guessDFCLocation() {
		return DFCConfigurationHelper.guessDFCLocation();
	}

	// NOTE this method is platform specific to windows.
	private String guessConfigLocation() {
		return DFCConfigurationHelper.guessConfigLocation();
	}

} // @jve:decl-index=0:visual-constraint="10,18"
