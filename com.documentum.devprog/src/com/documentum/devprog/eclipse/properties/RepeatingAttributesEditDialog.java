/*
 * Created on Nov 1, 2008
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.properties;

import com.documentum.devprog.eclipse.model.AttrInfo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class RepeatingAttributesEditDialog extends Dialog {

	private String[] currentVals;
	private AttrInfo attr;
	private RepeatingAttributeEditComposite editComposite;

	/**
	 * @param parentShell
	 */
	protected RepeatingAttributesEditDialog(Shell parentShell, AttrInfo attr) {
		super(parentShell);
		this.attr = attr;

	}

	protected Control createDialogArea(Composite parent) {
		editComposite = new RepeatingAttributeEditComposite(parent, SWT.NONE);
		editComposite.setAttributeValues(attr);
		return editComposite;
	}

	public AttrInfo getValues() {
		return attr;
	}

	protected void configureShell(Shell newShell) {
		newShell.setText("Edit Repeating Attribute");
		super.configureShell(newShell);
	}

	protected void okPressed() {
		String vals[] = editComposite.getValues();
		attr.setValues(vals);
		attr.setModified(true);
		super.okPressed();
	}

}
