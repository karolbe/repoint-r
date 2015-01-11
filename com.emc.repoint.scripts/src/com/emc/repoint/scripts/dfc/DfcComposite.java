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

package com.emc.repoint.scripts.dfc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfSession;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Rectangle;
import java.util.regex.*;

import bsh.*;
import bsh.classpath.*;
import bsh.util.*;
import java.io.*;
import java.net.URL;
import java.awt.event.*;
import org.eclipse.swt.widgets.Shell;

import com.emc.repoint.scripts.Activator;

import java.util.*;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;


// details on known bug with beanshell and postfix array operators "String args[]"
// http://www.beanshell.org/bsh20announce.txt



/**
 * DFC Composite.
 * 
 * Use the visual editor for modfications
 *
 * 
* @author Fabian Lee(fabian.lee@flatironssolutions.com)
 */

public class DfcComposite extends Composite {

	private StyledText resultsArea = null;

	private Button butExecute = null;

	private StyledText cmdArea = null;

	private Button butScriptFile = null;


	private Label lblCmd = null;

	private Label lblResuts = null;

	private Button butClearResults = null;

	private Button cpButton = null;

	private Button obrowserButton = null;
	
	Interpreter interpreter = null;  //  @jve:decl-index=0:
	ArrayList customClasspath = new ArrayList();  //  @jve:decl-index=0:

    private Button butNewScript = null;

    private Button butSave = null;

	public DfcComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {

		interpreter = new Interpreter();
		interpreter.setStrictJava(false);
		
		resultsArea = new StyledText(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		resultsArea.setBounds(new Rectangle(16, 390, 587, 170));
		butExecute = new Button(this, SWT.NONE);
		butExecute.setBounds(new Rectangle(463, 5, 63, 28));
		butExecute.setText("Execute");
		butExecute
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						 if ( PluginState.getDocbase() == null ||
								 PluginState.getDocbase().length() == 0) {
							 MessageDialog .openWarning(cmdArea.getShell(),
									 	"Repository", 
									 	"No Repository Selected. Please Select a Repository");
							 return;
						 }
							 
						String cmdStr = cmdArea.getText().trim();
						String retVal = executeCommand(cmdStr);
						resultsArea.append(retVal);
						resultsArea.append(resultsArea.getLineDelimiter());
						// move caret to end of output
						resultsArea.setSelection(resultsArea.getCharCount(), resultsArea.getCharCount());

					}
				});

		cmdArea = new StyledText(this, SWT.MULTI | SWT.V_SCROLL);
		cmdArea
				.setFont(new Font(Display.getDefault(), "Arial", 10, SWT.NORMAL));

		// read default dfc program
	    URL cmdFile = PluginHelper.getPluginFile(Activator.PLUGIN_ID,"data/dfc_default.txt");
		String defaultCommands = readFile(cmdFile); 
		cmdArea.setText(defaultCommands);
		
		cmdArea.setBounds(new Rectangle(17, 44, 582, 296));
		cmdArea.setEditable(true);
		/*	TODO: figure out way to eat ENTER when enabling this shortcut combo	
		cmdArea.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
		
			public void keyPressed(KeyEvent e) {
				e.doit = false;
				super.keyPressed(e);
				
				// alt-enter
				if(e.keyCode==13 && e.stateMask==SWT.ALT) {
					MessageDialog.openWarning(null,"enter","char=" + e.keyCode + ";mod=" + e.stateMask);
					butExecute.forceFocus();
				}
			}
		}
		);
*/
		butScriptFile = new Button(this, SWT.NONE);
		butScriptFile.setBounds(new Rectangle(370, 5, 86, 29));
		butScriptFile.setText("Load Script");
		lblCmd = new Label(this, SWT.NONE);
		lblCmd.setBounds(new Rectangle(15, 18, 64, 19));
		lblCmd.setText("Code View");
		lblResuts = new Label(this, SWT.NONE);
		lblResuts.setBounds(new Rectangle(16, 366, 76, 17));
		lblResuts.setText("Output");
		butClearResults = new Button(this, SWT.NONE);
		butClearResults.setBounds(new Rectangle(454, 351, 133, 29));
		butClearResults.setText("Clear Output Area");
		cpButton = new Button(this, SWT.NONE);
		cpButton.setBounds(new Rectangle(90, 7, 77, 29));
		cpButton.setText("Classpath");
		final Shell myshell = (Shell) this.getShell();		
		cpButton
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				
				// gather classpath entries in custom list
				ClasspathShell cpshell = new ClasspathShell(customClasspath,myshell,interpreter);
				try {
					for(int i=0;i<customClasspath.size();i++)
						interpreter.getNameSpace().getClassManager().addClassPath((URL) customClasspath.get(i));
				}catch(IOException ioe) {
		            DfLogger.error(DfcComposite.class.getName(),
		                    "Error reading custom classpath list", null, ioe);
					MessageDialog.openError(cmdArea.getShell(), "custom cp","error setting classpath: " + customClasspath.size());
				}
				
			}
		});
		
		
		
		obrowserButton = new Button(this, SWT.NONE);
		obrowserButton.setBounds(new Rectangle(176, 7, 88, 29));
		obrowserButton.setText("Object Browser");
		butNewScript = new Button(this, SWT.NONE);
		butNewScript.setBounds(new Rectangle(273, 5, 89, 29));
		butNewScript.setText("New Script");
		butSave = new Button(this, SWT.NONE);
		butSave.setBounds(new Rectangle(532, 4, 66, 30));
		butSave.setText("Save");
		butSave.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
		    public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
		    {
		        FileDialog fdSave = new FileDialog(DfcComposite.this.getShell(),SWT.SAVE);
                String fpath  = fdSave.open();
                if(fpath != null)
                {
                    saveScript(fpath);
                }
		    }
		});
		butNewScript.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
		    public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
		    {
		        URL templateFile = PluginHelper.getPluginFile(Activator.PLUGIN_ID, "data/new_script_template.txt");
                String fileTxt = readFile(templateFile);
                cmdArea.setText(fileTxt);
		    }
		});
		obrowserButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						ObjectBrowserShell my = new ObjectBrowserShell(myshell,interpreter);
					}
				});
		butClearResults
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						resultsArea.setText("");
					}
				});
		butScriptFile
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						FileDialog fd = new FileDialog(cmdArea.getShell());
						fd.setFilterExtensions(new String[] { "*.*", "*.java", "*.txt" });
						String file = fd.open();
						if(file!=null) {
							loadScriptFile(file);
						}
					}
				});
		this.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		this.setSize(new org.eclipse.swt.graphics.Point(696, 590));
		
	}
	
	private String executeCommand(String cmdStr) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		try {
			// setup a new interpreter for each run because
			// otherwise we end up with errors if names and types
			// mismatch on subsequent runs 'Typed variable declaration'
			// we add all custom classpaths to interpreter before running script
			interpreter = new Interpreter();
			try {
				for(int i=0;i<customClasspath.size();i++)
					interpreter.getNameSpace().getClassManager().addClassPath((URL) customClasspath.get(i));
			}catch(IOException ioe) {
	            DfLogger.error(DfcComposite.class.getName(),
	                    "Error reading custom classpath list", null, ioe);
				MessageDialog.openError(cmdArea.getShell(), "custom cp","error setting classpath: " + customClasspath.size());
			}

			// do execution
			interpreter.eval(cmdStr);
			
		} catch (EvalError err) {
			String msg = err.getMessage();
            DfLogger.warn(this, "Evaluation Error: " + msg, null, err);			
			MessageDialog.openWarning(cmdArea.getShell(), "Evaluation Exception",err.getMessage());
			
			// TODO: want to try to highlight error? 
			// is there 
			int lineNumber = -1;
			try {
				Pattern pattern = Pattern.compile(".*line (\\d*), column (\\d*).*");
				Matcher matcher = pattern.matcher(msg);
				if(matcher.matches()) {
					//MessageDialog.openWarning(null,"Match","matches! " + matcher.group(1) + "," + matcher.group(2));
					int linenum = Integer.parseInt(matcher.group(1));
					int colnum =  Integer.parseInt(matcher.group(2));
				}
			}catch(NumberFormatException exc) {
				MessageDialog.openWarning(null,"pattern error with ints",exc.toString());
			}
		}
		return baos.toString();
	}

	private void loadScriptFile(String filename) {
		try {
			BufferedReader bufr = new BufferedReader(new FileReader(filename));
			StringBuffer sbuf = new StringBuffer();
			String line = null;
			while ((line = bufr.readLine()) != null) {
				sbuf.append(line).append(cmdArea.getLineDelimiter());
			}
			bufr.close();
			
			cmdArea.setText(sbuf.toString());
		} catch (Exception ex) {
            DfLogger.error(this, "Error loading script: " + filename, null, ex);
            MessageDialog.openError(cmdArea.getShell(),
                    "Error loading script: " + filename, ex.getMessage());
		}

	}
	
    private String readFile(URL file)
    {
        try
        {
            BufferedReader bufw = new BufferedReader(
            		new InputStreamReader(file.openStream()));
            StringBuffer sbuf = new StringBuffer();
            String line = null;
            while ((line = bufw.readLine()) != null)
            {
                sbuf.append(line).append(cmdArea.getLineDelimiter());
            }
            return sbuf.toString();
        }
        catch (Exception ioe)
        {
            DfLogger.error(DfcComposite.class.getName(),
                    "Error reading default command text", null, ioe);
            return "for(int i=0;i<3;i++)\n\tSystem.out.println(\"hello, world\");";
        }
    }
    
    private void saveScript(String fpath)
    {
        try
        {
            BufferedWriter bufWr = new BufferedWriter(new FileWriter(fpath));
            bufWr.write(cmdArea.getText());
            bufWr.close();
        }
        catch(IOException ioe)
        {
            DfLogger.error(DfcComposite.class.getName(),"Error writing file: " + fpath, null   , ioe);
            
        }
    }


}  //  @jve:decl-index=0:visual-constraint="11,9"



