/*
 * Created on Nov 1, 2008
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfTypedObject;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DocbaseItemListSorter extends ViewerSorter {

	/*
	 * The property on which sorting takes place.
	 */
	private String propertyName = null;

	DocbaseItemListSorter(String propName) {
		propertyName = propName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		try {
			DocbaseItem d1 = (DocbaseItem) e1;
			DocbaseItem d2 = (DocbaseItem) e2;

			if (d1.getType() == DocbaseItem.TYPED_OBJ_TYPE
					&& d2.getType() == DocbaseItem.TYPED_OBJ_TYPE) {
				if ((propertyName == null) || (propertyName.length() == 0)) {
					return 0;
				}

				IDfTypedObject t1 = (IDfTypedObject) d1.getData();
				IDfTypedObject t2 = (IDfTypedObject) d2.getData();

				if (t1.isAttrRepeating(propertyName)) {
					String val1 = t1.getAllRepeatingStrings(propertyName, ",");
					String val2 = t2.getAllRepeatingStrings(propertyName, ",");
					// System.out.println("comparing " + val1 + " " + val2);
					return super.compare(viewer, val1, val2);
				} else {
					String val1 = t1.getString(propertyName);
					String val2 = t2.getString(propertyName);
					// System.out.println("Comparing: " + val1 + ":" + val2 );
					return super.compare(viewer, val1, val2);
				}
			} else {
				return super.compare(viewer, e1, e2);
			}

		} catch (DfException dfe) {
			DfLogger.warn(this, "Error obtaining property", null, null);
			return 0;
		}

	}

}
