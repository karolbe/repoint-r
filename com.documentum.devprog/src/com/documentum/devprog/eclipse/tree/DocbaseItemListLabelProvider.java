/*
 * Created on Apr 12, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.client.IDfTypedObject;

import java.util.HashMap;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DocbaseItemListLabelProvider implements ITableLabelProvider {

	// TODO In future this should be localized
	private static HashMap attrColumnLabels = new HashMap();

	static {
		attrColumnLabels.put("object_name", "Name");
		attrColumnLabels.put("r_modify_date", "Modified");
		attrColumnLabels.put("r_modifier", "Modified By");
		attrColumnLabels.put("r_creator_name", "Creator");
		attrColumnLabels.put("r_full_content_size", "Size");
	}

	public Image getImage(Object element) {
		DocbaseItem data = (DocbaseItem) element;
		return data.getIcon();
	}

	public String getText(Object element) {
		DocbaseItem data = (DocbaseItem) element;
		return data.getLabelText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		DocbaseItem data = (DocbaseItem) element;
		if (columnIndex == 0) {
			return data.getIcon();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 * .Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		try {
			DocbaseItem data = (DocbaseItem) element;
			if (data.getType() == DocbaseItem.TYPED_OBJ_TYPE) {
				IDfTypedObject tObj = (IDfTypedObject) data.getData();

				String val = tObj
						.getString(ListViewHelper.getColumns()[columnIndex]
								.trim());
				// System.out.println("colIndex=" + columnIndex + "," +
				// ListViewHelper.getColumns()[columnIndex] + "," + val);
				return val;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 * jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang
	 * .Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
	 * .jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

	private String getColumnLabel(String attrName) {
		return (String) attrColumnLabels.get(attrName);
	}

}
