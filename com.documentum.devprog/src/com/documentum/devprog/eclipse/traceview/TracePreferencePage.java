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

package com.documentum.devprog.eclipse.traceview;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PreferenceConstants;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class TracePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public TracePreferencePage() {
		super(GRID);
		setPreferenceStore(DevprogPlugin.getDefault().getPreferenceStore());
		setDescription("DFC Trace View Preferences");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		/*
		 * addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH,
		 * "&Directory preference:", getFieldEditorParent())); addField( new
		 * BooleanFieldEditor( PreferenceConstants.P_BOOLEAN,
		 * "&An example of a boolean preference", getFieldEditorParent()));
		 * 
		 * addField(new RadioGroupFieldEditor( PreferenceConstants.P_CHOICE,
		 * "An example of a multiple-choice preference", 1, new String[][] { {
		 * "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } },
		 * getFieldEditorParent()));
		 */

		IntegerFieldEditor portEditor = new IntegerFieldEditor(
				PreferenceConstants.P_PORT, "Port Number",
				getFieldEditorParent());
		portEditor.setValidRange(1024, 65535);
		addField(portEditor);

		IntegerFieldEditor traceDelayEditor = new IntegerFieldEditor(
				PreferenceConstants.P_TRACE_DELAY, "Trace Delay (ms)",
				getFieldEditorParent());
		traceDelayEditor.setValidRange(0, Integer.MAX_VALUE);
		addField(traceDelayEditor);

		// addField(new
		// StringFieldEditor(PreferenceConstants.P_BUFFER_SIZE,"Max Trace Entries",getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}