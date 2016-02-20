/*
 * Created on Oct 31, 2008
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.properties;

import com.documentum.devprog.eclipse.model.AttrInfo;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class AttrValueInputValidator implements IInputValidator {

	private AttrInfo attr;

	public AttrValueInputValidator(AttrInfo attr) {
		this.attr = attr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
	 */
	public String isValid(String newText) {
		if (newText == null || newText.trim().length() == 0) {
			return "Empty Value is Not Valid";
		} else {
			if (attr.getDataType().equals("boolean")) {
				if (!(newText.equalsIgnoreCase("true") || newText
						.equalsIgnoreCase("false"))) {
					return "Not a Valid Boolean Value";
				}
			} else if (attr.getDataType().equals("int")) {
				try {
					Integer.parseInt(newText);
				} catch (NumberFormatException nfe) {
					return "Not a Valid Number";
				}

			} else if (attr.getDataType().equals("double")) {
				try {
					Double.parseDouble(newText);
				} catch (NumberFormatException nfe) {
					return "Not a Valid Number";
				}

			}
			return null;
		}

	}

}
