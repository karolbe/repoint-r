/*
 * Created on Mar 27, 2006
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.api;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public class TestST extends Composite {

	private StyledText styledText = null;

	public TestST(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		styledText = new StyledText(this, SWT.V_SCROLL | SWT.H_SCROLL);
		styledText.setBounds(new org.eclipse.swt.graphics.Rectangle(51, 47,
				404, 237));
		setSize(new org.eclipse.swt.graphics.Point(474, 303));
	}

} // @jve:decl-index=0:visual-constraint="10,10"
