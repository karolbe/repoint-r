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
 * Created on Jun 29, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.rcpapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class ClasspathCreator {

	private static String classpathSep = System.getProperty("path.separator");

	public static void main(String args[]) {
		final Text txtPath;
		final Text txtSubst;
		try {
			Display display = new Display();
			final Shell shell = new Shell(display);
			shell.setText("Classpath Creator");

			FormLayout frmlout = new FormLayout();
			shell.setLayout(frmlout);

			txtPath = new Text(shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			FormData fdPath = new FormData();
			fdPath.left = new FormAttachment(0, 5);
			fdPath.bottom = new FormAttachment(50, -5);
			fdPath.top = new FormAttachment(0, 5);
			fdPath.right = new FormAttachment(100, -5);
			txtPath.setLayoutData(fdPath);

			/*
			 * txtSubst = new Text(shell,SWT.SINGLE | SWT.BORDER);
			 * txtSubst.setToolTipText(
			 * "Enter the text that should be substituted instead of the folder path (e.g evironment variable name - %DM_SHARED%"
			 * ); FormData fdSubst = new FormData(); fdSubst.left = new
			 * FormAttachment(0,5); fdSubst.right = new FormAttachment(20,5);
			 * fdSubst.top = new FormAttachment(txtPath);
			 * txtSubst.setLayoutData(fdSubst);
			 */

			Button butChoose = new Button(shell, SWT.PUSH);
			butChoose.setText("Choose Folder");
			FormData fdBut = new FormData();
			fdBut.left = new FormAttachment(0, 5);
			fdBut.right = new FormAttachment(20, -5);
			fdBut.top = new FormAttachment(txtPath);
			butChoose.setLayoutData(fdBut);

			// txtSubst.setLayoutData(getGridData(1,1));
			// txtPath.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true,
			// 1, 5));
			butChoose.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent se) {
					DirectoryDialog dd = new DirectoryDialog(shell);

					String folder = dd.open();
					if (folder != null) {
						File fldr = new File(folder);
						StringBuffer bufPath = new StringBuffer();
						addJars(fldr, bufPath);
						txtPath.append(bufPath.toString());
					}
				}

				public void widgetDefaultSelected(SelectionEvent se) {
					widgetSelected(se);
				}
			});

			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/*
	 * public static void main(String[] args) { try { //classpath buffer
	 * StringBuffer cpBuf = new StringBuffer(256);
	 * 
	 * FileReader fr = new FileReader("jarFolders.txt"); BufferedReader br = new
	 * BufferedReader(fr); String fldrPath = ""; while((fldrPath =
	 * br.readLine()) != null) { File fldr = new File(fldrPath);
	 * addJars(fldr,cpBuf); }
	 * 
	 * printClasspath(cpBuf); } catch(Exception ex) { ex.printStackTrace(); }
	 * 
	 * 
	 * }
	 */

	public static void addJars(File fldr, StringBuffer cpBuf) {
		if (fldr.isDirectory()) {
			File[] files = fldr.listFiles();
			for (int i = 0; i < files.length; i++) {
				File fl = files[i];
				if (fl.isDirectory()) {
					addJars(fl, cpBuf);
				}

				if (fl.getName().endsWith(".jar")) {
					cpBuf.append(fl.getAbsolutePath());
					cpBuf.append(classpathSep);

				}
			}
		}
	}

	private static void printClasspath(StringBuffer cpBuf) throws IOException {
		String path = cpBuf.toString();
		System.out.println("Got classpath:");
		System.out.println(path);

		FileWriter fw = new FileWriter("classpath.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(path);
		bw.write(System.getProperty("line.separator"));
		bw.close();

		System.out
				.println("Wrote classpath to file classpath.txt on a single line. You may copy it from there");

	}

	public static GridData getGridData(int horizSpan, int vertSpan) {
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = horizSpan;
		gd.verticalSpan = vertSpan;
		gd.horizontalAlignment = SWT.LEFT;
		gd.verticalAlignment = SWT.TOP;
		gd.grabExcessVerticalSpace = true;
		return gd;
	}
}
