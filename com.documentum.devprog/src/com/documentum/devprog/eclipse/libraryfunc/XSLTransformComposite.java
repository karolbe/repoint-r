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
 * Created on Oct 18, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;

import com.documentum.devprog.eclipse.common.DocLauncher;
import com.documentum.devprog.eclipse.common.DocbaseListDialog;
import com.documentum.devprog.eclipse.common.DocbaseLoginDialog;
import com.documentum.devprog.eclipse.common.HTMLDialog;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.common.SimpleListDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.operations.IDfFile;
import com.documentum.operations.IDfXMLTransformOperation;

/**
 * 
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class XSLTransformComposite {
	private String targetFile = "";

	private String xslSource = "";

	private IDfSessionManager sessMgr = null;

	private String repoName = "";

	private Group xslGroup = null;

	private Group sourceGroup = null;

	private String xmlSource = "";

	private Text xmlSourceTxt = null;

	private Color backColor = null;

	private Color textBackColor = null;

	private Label lblRepo = null;

	private Text destFileTxt;

	private Text xslObjTxt;

	private String CANNOT_EDIT = "Cannot edit file. Either the path is invalid or the file is not local. Editing is not supported for repository objects. ";

	public XSLTransformComposite() {

	}

	public void setSessionData(IDfSessionManager sm, String repo) {
		sessMgr = sm;
		repoName = repo;
	}

	public Composite createComposite(Composite parent) {
		backColor = parent.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		textBackColor = new Color(parent.getDisplay(), 232, 236, 240);
		// parent.setBackground(whiteColor);

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(backColor);
		comp.setLayout(new FormLayout());

		FormData fdRepoSel = PluginHelper.getFormData(0, 10, 0, 100);
		createRepoSelector(comp, fdRepoSel);

		FormData fdSrc = PluginHelper.getFormData(10, 20, 0, 100);
		createSourceControls(comp, fdSrc);

		FormData fdXSL = PluginHelper.getFormData(20, 30, 0, 100);
		createXSLGroup(comp, fdXSL);

		FormData fdDest = PluginHelper.getFormData(30, 40, 0, 100);
		createDestination(comp, fdDest);

		FormData fdActBut = PluginHelper.getFormData(40, 50, 80, 98);
		createActionButton(comp, fdActBut);

		return comp;
	}

	protected void createRepoSelector(Composite parent, FormData fd) {
		Group repoSel = new Group(parent, SWT.NONE);
		repoSel.setText("Repository");
		repoSel.setBackground(backColor);
		repoSel.setLayoutData(fd);
		repoSel.setLayout(new FormLayout());

		lblRepo = new Label(repoSel, SWT.SINGLE);
		if (repoName != null) {
			lblRepo.setText(repoName);
		}
		lblRepo.setLayoutData(PluginHelper.getFormData(0, 100, 0, 70));
		lblRepo.setBackground(textBackColor);

		final Link lnkSelRepo = new Link(repoSel, SWT.NONE);
		lnkSelRepo.setText("<A>Choose Repository</A>");
		lnkSelRepo.setLayoutData(PluginHelper.getFormData(0, 100, 70, 100));
		lnkSelRepo.setBackground(backColor);
		lnkSelRepo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				DocbaseListDialog dsd = new DocbaseListDialog(lnkSelRepo
						.getShell());
				int status = dsd.open();
				if (status == DocbaseListDialog.OK) {
					repoName = dsd.getSelectedRepo();
					if (PluginState.hasIdentity(repoName) == false) {
						DocbaseLoginDialog dld = new DocbaseLoginDialog(
								lnkSelRepo.getShell());
						dld.setDocbaseName(repoName);
						int dldStatus = dld.open();
						if (dldStatus == Dialog.OK) {
							lblRepo.setText(repoName);
						}
					} else {
						lblRepo.setText(repoName);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});

	}

	protected void createSourceControls(Composite parent, FormData fd) {
		sourceGroup = new Group(parent, SWT.NONE);
		sourceGroup.setText("XML Source - (file path/r_object_id)");
		sourceGroup.setLayoutData(fd);
		sourceGroup.setLayout(new FormLayout());
		sourceGroup.setBackground(backColor);

		xmlSourceTxt = new Text(sourceGroup, SWT.FLAT);
		xmlSourceTxt.setBackground(textBackColor);
		xmlSourceTxt.setText(xmlSource);
		xmlSourceTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				xmlSource = xmlSourceTxt.getText();
			}
		});
		xmlSourceTxt.setLayoutData(PluginHelper.getFormData(0, 90, 0, 80));

		final Link selectFileSource = new Link(sourceGroup, SWT.NONE);
		selectFileSource.setText("<A>Browse</A>");
		selectFileSource.setBackground(backColor);
		selectFileSource.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				FileDialog fd = new FileDialog(selectFileSource.getShell(),
						SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.xml" });
				String filePath = fd.open();
				if (filePath != null) {
					xmlSourceTxt.setText(filePath);
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});
		selectFileSource
				.setLayoutData(PluginHelper.getFormData(0, 100, 80, 90));

		final Link editFileSource = new Link(sourceGroup, SWT.NONE);
		editFileSource.setText("<A>Edit</A>");
		editFileSource.setBackground(backColor);
		editFileSource.setLayoutData(PluginHelper.getFormData(0, 100, 90, 100));
		editFileSource.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				String path = xmlSourceTxt.getText().trim();
				File fl = new File(path);
				if (fl.exists()) {
					DocLauncher dl = new DocLauncher();
					dl.setDocPath(fl.getAbsolutePath());
					dl.run();
				} else {
					MessageDialog.openInformation(editFileSource.getShell(),
							"Edit File", CANNOT_EDIT);
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}

		});
	}

	protected void createXSLGroup(Composite parent, FormData fd) {
		xslGroup = new Group(parent, SWT.NONE);
		xslGroup.setText("XSL File  (file path/r_object_id/URL)");
		xslGroup.setLayoutData(fd);
		xslGroup.setLayout(new FormLayout());
		xslGroup.setBackground(backColor);

		xslObjTxt = new Text(xslGroup, SWT.SINGLE);
		xslObjTxt.setLayoutData(PluginHelper.getFormData(0, 90, 0, 60));
		xslObjTxt.setBackground(textBackColor);
		xslObjTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				xslSource = xslObjTxt.getText();
			}
		});
		Link selFile = new Link(xslGroup, SWT.PUSH);
		selFile.setText("<A>Browse</A>");
		selFile.setBackground(backColor);
		selFile.setLayoutData(PluginHelper.getFormData(0, 100, 60, 70));
		selFile.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				FileDialog fd = new FileDialog(xslGroup.getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.xsl" });
				String filepath = fd.open();
				if (filepath != null) {
					xslObjTxt.setText(filepath);
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});

		Link butLoadXSLList = new Link(xslGroup, SWT.NONE);
		butLoadXSLList.setText("<A>Choose from Repository</A>");
		butLoadXSLList.setBackground(backColor);
		butLoadXSLList.setLayoutData(PluginHelper.getFormData(0, 100, 70, 90));
		butLoadXSLList.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				try {
					String[] xslFiles = getXSLFileList();
					SimpleListDialog sld = new SimpleListDialog(xslGroup
							.getShell(), xslFiles, "Select XSL File: "
							+ repoName);
					int status = sld.open();
					if (status == SimpleListDialog.OK) {
						String selData = sld.getSelection()[0];
						xslObjTxt.setText(selData);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});

		final Link editFileSource = new Link(xslGroup, SWT.NONE);
		editFileSource.setText("<A>Edit</A>");
		editFileSource.setBackground(backColor);
		editFileSource.setLayoutData(PluginHelper.getFormData(0, 100, 90, 100));
		editFileSource.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				String path = xslObjTxt.getText().trim();
				File fl = new File(path);
				if (fl.exists()) {
					DocLauncher dl = new DocLauncher();
					dl.setDocPath(fl.getAbsolutePath());
					dl.run();
				} else {
					MessageDialog.openInformation(editFileSource.getShell(),
							"Edit File", CANNOT_EDIT);
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}

		});

	}

	protected void createActionButton(Composite parent, FormData fd) {
		Link doTransform = new Link(parent, SWT.NONE);
		doTransform.setText("<A>Perform Transform</A>");
		doTransform.setBackground(backColor);
		doTransform.setLayoutData(fd);
		doTransform.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				performTransform();
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}

		});
	}

	protected void createDestination(Composite parent, FormData fd) {
		Group destGrp = new Group(parent, SWT.NONE);
		destGrp.setText("Destination File (optional)");
		destGrp.setLayoutData(fd);
		destGrp.setLayout(new FormLayout());
		destGrp.setBackground(backColor);

		destFileTxt = new Text(destGrp, SWT.SINGLE);
		destFileTxt.setLayoutData(PluginHelper.getFormData(0, 90, 0, 80));
		destFileTxt.setBackground(textBackColor);
		destFileTxt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				targetFile = destFileTxt.getText();
			}
		});
		destFileTxt.setText(targetFile);

		Link selDestFile = new Link(destGrp, SWT.NONE);
		selDestFile.setText("<A>Browse</A>");
		selDestFile.setBackground(backColor);
		selDestFile.setLayoutData(PluginHelper.getFormData(0, 100, 80, 90));
		selDestFile.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				FileDialog fd = new FileDialog(destFileTxt.getShell(), SWT.SAVE);
				String[] exts = { "*.*", "*.htm", "*.html", "*.xml", "*.xsl" };
				fd.setFilterExtensions(exts);
				String filePath = fd.open();
				if (filePath != null) {
					destFileTxt.setText(filePath);
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}
		});

		final Link openDestFile = new Link(destGrp, SWT.NONE);
		openDestFile.setText("<A>Open</A>");
		openDestFile.setBackground(backColor);
		openDestFile.setLayoutData(PluginHelper.getFormData(0, 100, 90, 100));
		openDestFile.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				String path = destFileTxt.getText().trim();
				File fl = new File(path);
				if (fl.exists()) {
					DocLauncher dl = new DocLauncher();
					dl.setDocPath(fl.getAbsolutePath());
					dl.run();
				} else {
					MessageDialog.openInformation(openDestFile.getShell(),
							"Edit File", CANNOT_EDIT);
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				widgetSelected(se);
			}

		});

	}

	public void performTransform() {
		IDfSession sess = null;
		try {
			IDfClientX cx = new DfClientX();
			IDfXMLTransformOperation xmlt = cx.getXMLTransformOperation();

			Object xmlObj = null;
			Object xslObj = null;

			if (xmlSource != null) {
				try {
					xmlObj = resolveXMLObject();
				} catch (Exception ex) {
					DfLogger.error(this, "Error resolving xml source:", null,
							ex);
					MessageDialog.openError(xslGroup.getShell(), "XML Source",
							"Unable to resolve xml source: " + ex.getMessage());
					return;
				}
			} else {
				MessageDialog
						.openInformation(xslGroup.getShell(), "XML Source",
								"XML Source not set. Please set the XML source before executing transform");
			}

			if (xslSource != null) {
				xslObj = resolveTransformObject();
			}

			xmlt.add(xmlObj);
			xmlt.setTransformation(xslObj);

			if ((repoName != null) && (repoName.length() > 0)) {
				sess = sessMgr.getSession(repoName);
				xmlt.setSession(sess);
			} else if ((xmlObj instanceof IDfFile)
					&& (xslObj instanceof IDfFile)) {
				// looks like both are local files and repo name is not set too
				IDfFile xf = (IDfFile) xmlObj;
				IDfFile xslf = (IDfFile) xslObj;
				performXSLTransform(xf.getFullPath(), xslf.getFullPath());
				return;
			}

			IDfFile tf = null;
			if (targetFile != null && targetFile.length() > 0) {
				tf = cx.getFile(targetFile);

			} else {
				tf = cx.getFile(getTempFile());
			}
			xmlt.setDestination(tf);

			XSLTransformMonitor transMon = new XSLTransformMonitor(xmlt);
			IProgressService progServ = PlatformUI.getWorkbench()
					.getProgressService();
			progServ.busyCursorWhile(transMon);

			if (transMon.success()) {
				{
					File fl = new File(tf.getFullPath());
					URL flUrl = fl.toURL();
					HTMLDialog hd = new HTMLDialog(xslGroup.getShell(),
							"Transform Result", flUrl);
					hd.setSize(700, 600);
					hd.open();
				}
			} else {
				IDfList errs = xmlt.getErrors();
				ByteArrayOutputStream errors = new ByteArrayOutputStream();
				PluginHelper.printOperationErrors(errors, errs);
				MessageDialog.openError(xslGroup.getShell(),
						"XSL Transform Errors", errors.toString());

			}
		} catch (Exception ex) {
			MessageDialog.openError(xslGroup.getShell(),
					"XSL Transform Errors", ex.getMessage());
		} finally {
			if (sess != null) {
				sessMgr.release(sess);
			}
		}
	}

	private Object resolveXMLObject() throws DfException {
		IDfFile fl = null;
		IDfId id = new DfId(xmlSource);

		IDfClientX cx = new DfClientX();

		if (id.isObjectId()) {
			IDfSession sess = null;
			try {
				sess = sessMgr.getSession(repoName);
				IDfSysObject xmlObj = (IDfSysObject) sess.getObject(id);
				return xmlObj;
			} finally {
				if (sess != null) {
					sessMgr.release(sess);
				}
			}

		} else if ((fl = cx.getFile(xmlSource)).exists()) {
			return fl;
		} else {
			try {
				URL url = new URL(xmlSource);
				return url;
			} catch (MalformedURLException mfe) {

			}
			return null;
		}
	}

	private Object resolveTransformObject() {
		IDfSession sess = null;
		try {
			IDfClientX cx = new DfClientX();

			IDfFile fl = cx.getFile(xslSource);
			if (fl.exists()) {
				return fl;
			}

			int indx = xslSource.indexOf("-->");
			if (indx != -1) {
				IDfId id = new DfId(xslSource.substring(indx + 3).trim());
				sess = PluginState.getSessionById(id);
				IDfSysObject so = (IDfSysObject) sess.getObject(id);
				return so;
			}

			IDfId objId = new DfId(xslSource);
			if (objId.isObjectId()) {
				sess = PluginState.getSessionById(objId);
				IDfSysObject so = (IDfSysObject) sess.getObject(objId);
				return so;
			}

			try {
				URL url = new URL(xslSource.trim());
				return url;
			} catch (MalformedURLException mfe) {
				return null;
			}
		} catch (DfException dfe) {

		}
		return null;

	}

	private String[] getXSLFileList() {
		IDfSession sess = null;
		IDfCollection coll = null;
		ArrayList lst = new ArrayList();
		try {
			String dql = "select r_object_id,object_name from dm_document where a_content_type='xsl'";
			IDfClientX cx = new DfClientX();
			IDfQuery queryObj = cx.getQuery();
			queryObj.setDQL(dql);
			sess = sessMgr.getSession(repoName);
			coll = queryObj.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				String data = coll.getString("object_name") + " --> "
						+ coll.getString("r_object_id");
				lst.add(data);
			}

			Collections.sort(lst);

		} catch (DfException ex) {
			DfLogger.error(this, "Error obtaining XSL Files", null, ex);
			MessageDialog.openError(xslGroup.getShell(), "XSL File",
					"Error obtaining XSL files: " + ex.getMessage());

		} finally {
			if (coll != null) {
				try {
					coll.close();
				} catch (DfException dfe2) {
				}
			}
			if (sess != null) {
				sessMgr.release(sess);
			}
		}
		return (String[]) lst.toArray(new String[] {});

	}

	public void setSource(String source) {
		xmlSource = source;
		if (xmlSource != null) {
			IDfId id = new DfId(xmlSource);
			if (id.isObjectId()) {
				try {
					repoName = DfClient.getLocalClient().getDocbaseNameFromId(
							id);
					if (lblRepo != null) {
						lblRepo.setText(repoName);
					}
				} catch (Exception dfe) {
				}
			}

			if (xmlSourceTxt != null) {
				xmlSourceTxt.setText(xmlSource);
			}
		}
	}

	public String getSource() {
		return xmlSource;
	}

	/**
	 * 
	 * @param xsl
	 */
	public void setXSLFile(String xsl) {
		xslSource = xsl;
		if ((xslObjTxt != null) && (xslSource != null)) {
			xslObjTxt.setText(xslSource);
		}

	}

	public String getXSLFile() {
		return xslSource;
	}

	public String getTargetFile() {
		return targetFile;
	}

	/**
	 * 
	 * @param tf
	 */
	public void setTargetFile(String tf) {
		targetFile = tf;
		if (destFileTxt != null && targetFile != null) {
			destFileTxt.setText(targetFile);
		}
	}

	private void performXSLTransform(String xmlFilePath, String xslFilePath) {
		try {
			File xslFile = new File(xslFilePath);
			StreamSource xslSrc = new StreamSource(xslFile);

			TransformerFactory tfact = TransformerFactory.newInstance();
			Transformer trans = tfact.newTransformer(xslSrc);

			File xmlFile = new File(xmlFilePath);
			StreamSource xmlSrc = new StreamSource(xmlFile);

			StreamResult result = null;
			File resultantFile = null;
			if (targetFile != null && (targetFile.length() > 0)) {
				resultantFile = new File(targetFile);
				result = new StreamResult(resultantFile);
			} else {
				resultantFile = new File(getTempFile());
				result = new StreamResult(resultantFile);
			}

			trans.transform(xmlSrc, result);

			if (resultantFile != null) {
				HTMLDialog hd = new HTMLDialog(xslGroup.getShell(),
						"Transform Result", resultantFile.toURL());
				hd.setSize(700, 600);
				hd.open();
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error performing transform", null, ex);
			MessageDialog.openError(xslGroup.getShell(), "Transform Error",
					ex.getMessage());
		}

	}

	/**
	 * Creates a temp file for use when the destination is not set. the file is
	 * used for display purposes only
	 * 
	 * @return
	 */
	private String getTempFile() {
		try {
			String suffix = "" + (int) (Math.random() * 1000);
			File tmpFile = File.createTempFile(suffix, "");
			return tmpFile.getAbsolutePath();
		} catch (Exception ex) {
			DfLogger.debug(this, "Error creating temp file", null, ex);
			return "";
		}
	}

}
