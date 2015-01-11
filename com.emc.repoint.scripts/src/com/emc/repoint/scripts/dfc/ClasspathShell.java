package com.emc.repoint.scripts.dfc;

import java.net.URL;
import java.util.Set;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Button;

import bsh.BshClassManager;
import bsh.ClassPathException;
import bsh.Interpreter;
import bsh.classpath.BshClassPath;
import bsh.classpath.ClassManagerImpl;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import com.documentum.fc.common.DfLogger;

import java.net.URL;
import java.net.MalformedURLException;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * Creates popup dialog for adding entries to the claspath 
 * 
 * Use the visual editor for modfications (may need to use no-arg constructor for Shell)
 * 
 * @author Fabian Lee(fabian.lee@flatironssolutions.com)
*/

public class ClasspathShell {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Text text = null;
	private List cpList = null;
	private Button browseButton = null;
	private Button addButton = null;
	
	Interpreter interpreter = null;
	ArrayList customClasspath = null;
	Shell parent = null;
	
	public ClasspathShell(ArrayList customClasspath,Shell parent,Interpreter interpreter) {
		this.parent = parent;
		this.interpreter = interpreter;
		this.customClasspath = customClasspath;
		createSShell();
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		
		// this breaks the visual editor, but allows us modality with the dialog
		// use the no-args constructor for Shell
		sShell = new Shell(parent,SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM);
		
		sShell.setText("Classpath");
		sShell.setSize(new Point(545, 341));
		sShell.setLayout(null);
		text = new Text(sShell, SWT.BORDER);
		text.setBounds(new Rectangle(62, 5, 368, 24));
		cpList = new List(sShell, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
		cpList.setBounds(new Rectangle(8, 46, 517, 246));
		browseButton = new Button(sShell, SWT.NONE);
		browseButton.setBounds(new Rectangle(6, 6, 47, 24));
		browseButton.setText("Browse");
		browseButton
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				FileDialog fd = new FileDialog(sShell);
				fd.setFilterExtensions(new String[] { "*.jar", "*.*" });
				String file = fd.open();
				if (file != null) {
						File f = new File(file);
						if(f.exists()) {
							text.setText(f.getAbsolutePath());
						}else {
				            DfLogger.warn(this, "File does not exist: " + file, null, null);
				            MessageDialog.openWarning(null,
				                    "File does not exist: " + file, null);
						}
				}
				sShell.forceActive();
			}
		});
		
		
		
		
		addButton = new Button(sShell, SWT.NONE);
		addButton.setBounds(new Rectangle(435, 6, 87, 24));
		addButton.setText("Add");
		addButton
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				String file = text.getText();
				if (file != null) {
					File f = new File(file);
					if(!f.exists()) {
			            DfLogger.warn(this, "File does not exist: " + file, null, null);
			            MessageDialog.openWarning(null,
			                    "File does not exist: " + file, null);
					}else {
						URL cpath;
						try {
							cpath = f.toURL();
							interpreter.getNameSpace().getClassManager().addClassPath(cpath);
							customClasspath.add(cpath);
							text.setText("");
							reloadClasspath();
						} catch (Exception exc) {
				            DfLogger.error(this, "Problem appending to classpath: " + file, null, exc);
				            MessageDialog.openError(null,
				                    "Problem appending to classpath: " + file, null);
						}

					}
				}
			}
		});
		

		// populate classpath list
		reloadClasspath();
		
		sShell.pack();
		sShell.open();
		
		// gather all input before returning
		while ( !sShell.isDisposed() )
        {
            if ( !sShell.getDisplay().readAndDispatch() )
                sShell.getDisplay().sleep();
        } 		
	
	}
	
	private void reloadClasspath() {
		cpList.removeAll();
		try {
			BshClassManager cmgr = interpreter.getNameSpace().getClassManager();
			ClassManagerImpl cmImpl = (ClassManagerImpl) cmgr;			
		
			BshClassPath bcp = cmImpl.getClassPath();
			URL urlClasspath[] = bcp.getPathComponents();
			
			for(int i=0;i<urlClasspath.length;i++) {
				cpList.add(urlClasspath[i].toString());
			}
		} catch (ClassPathException e) {
			e.printStackTrace();
		}
		cpList.redraw();
		
	}

}
