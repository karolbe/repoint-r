package com.emc.repoint.scripts.dfc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import java.net.URL;
import java.net.MalformedURLException;
import org.eclipse.jface.resource.ImageDescriptor;

import com.documentum.devprog.eclipse.common.PluginHelper;

/**
* @author Fabian Lee(fabian.lee@flatironssolutions.com)
*/


public class SourcePackageLabelProvider extends LabelProvider {	
	
    private static String FOLDER_ICON = "type/t_dm_folder_16.gif";	
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		return PluginHelper.getImage(FOLDER_ICON);
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof SourcePackage) {
			if(((SourcePackage)element).getName() == null) {
				return "";
			} else {
				return ((SourcePackage)element).getName();
			}
		}else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
		super.dispose();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

}
