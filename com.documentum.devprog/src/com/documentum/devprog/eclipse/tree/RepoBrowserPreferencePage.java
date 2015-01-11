/*
 * Created on Jul 11, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PreferenceConstants;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class RepoBrowserPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public RepoBrowserPreferencePage() {
		super(GRID);
		setPreferenceStore(DevprogPlugin.getDefault().getPreferenceStore());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	protected void createFieldEditors() {
		StringFieldEditor sfe = new StringFieldEditor(
				PreferenceConstants.P_ATTR_LIST, "Attributes to Query",
				getFieldEditorParent());
		sfe.setEmptyStringAllowed(false);
		addField(sfe);

		StringFieldEditor sfeDefCols = new StringFieldEditor(
				PreferenceConstants.P_VISIBLE_COLUMNS, "Visible Columns",
				getFieldEditorParent());
		sfeDefCols.setEmptyStringAllowed(false);
		addField(sfeDefCols);

		BooleanFieldEditor bfe = new BooleanFieldEditor(
				PreferenceConstants.P_SHOW_ALL_OBJECTS, "Show All Objects",
				getFieldEditorParent());
		addField(bfe);
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
