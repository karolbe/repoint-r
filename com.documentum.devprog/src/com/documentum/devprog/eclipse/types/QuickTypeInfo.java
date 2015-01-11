/*
 * Created on Sep 10, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.types;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class QuickTypeInfo extends TypeInfo {

	/*
	 * A tree path that culminates on the type for which the Type Tree is needed
	 * 0=>the type under consideration n-1=>the root type (e.g. dm_sysobject)
	 */
	private List typeTreePath = null;

	/**
	 * @param name
	 * @param type
	 * @param docbaseName
	 */
	QuickTypeInfo(String name, int type, String docbaseName) {
		super(name, type, docbaseName);

	}

	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) {
		TypeInfo child = findChild();
		if (child != null) {
			if (hasTBO(getDocbase(), child.getTypeName())) {
				child.setTBO(true);
			}
			return new ITypeViewData[] { child };
		} else {
			return new ITypeViewData[] {};
		}

		// System.out.println(super.getTypeName() + " returning subtype " +
		// getDisplaySubtype());

	}

	private TypeInfo findChild() {
		// System.out.println("Finding child...");
		int topIndx = typeTreePath.size() - 1;
		String type = super.getTypeName();
		// System.out.println("cur type: " + type);
		for (int i = topIndx; i >= 0; i--) {
			String cur = (String) typeTreePath.get(i);
			// System.out.println("searching: " + "," + i + ","+ cur);
			if (cur.equals(type)) {
				if (i != 0) {
					String childName = (String) typeTreePath.get(i - 1);
					if ((i - 1) == 0) {
						// If zero return the dynamic TypeInfo class
						// that can get its own children
						TypeInfo ti = new TypeInfo(childName,
								TypeInfo.OBJECT_TYPE, getDocbase());
						return ti;
					} else {
						QuickTypeInfo qti = new QuickTypeInfo(childName,
								TypeInfo.OBJECT_TYPE, getDocbase());
						qti.setTypeTreePath(typeTreePath);
						return qti;
					}

				} else {
					// This is actually an error condition
					throw new RuntimeException(
							"QuickTypeInfo object cannot be in index 0");
				}
			}// if(equalType)

		}// for
		return null;
	}

	void setTypeTreePath(List treePath) {
		this.typeTreePath = treePath;
	}

}
