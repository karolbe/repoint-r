/*
 * Created on Sep 11, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.types;

import com.documentum.fc.common.DfException;

import com.documentum.fc.client.IDfTypedObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class TempLoadingNode implements ITypeViewData {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.documentum.devprog.eclipse.types.ITypeViewData#getChildren(org.eclipse
	 * .core.runtime.IProgressMonitor,
	 * com.documentum.devprog.eclipse.types.ITypeViewData)
	 */
	public ITypeViewData[] getChildren(IProgressMonitor progMon,
			ITypeViewData parent) throws DfException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.documentum.devprog.eclipse.types.ITypeViewData#getDisplayText()
	 */
	public String getDisplayText() {
		return "Loading...";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.documentum.devprog.eclipse.types.ITypeViewData#getDocbase()
	 */
	public String getDocbase() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.documentum.devprog.eclipse.types.ITypeViewData#getIconPath()
	 */
	public String getIconPath() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.documentum.devprog.eclipse.types.ITypeViewData#getProperties()
	 */
	public IDfTypedObject getProperties() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.documentum.devprog.eclipse.types.ITypeViewData#getShell()
	 */
	public Shell getShell() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.documentum.devprog.eclipse.types.ITypeViewData#hasChildren()
	 */
	public boolean hasChildren() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.documentum.devprog.eclipse.types.ITypeViewData#onDoubleClick(org.
	 * eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void onDoubleClick(DoubleClickEvent dce) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.documentum.devprog.eclipse.types.ITypeViewData#setShell(org.eclipse
	 * .swt.widgets.Shell)
	 */
	public void setShell(Shell sh) {
	}

}
