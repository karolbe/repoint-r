/*
 * Created on Jul 12, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.PluginHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DumpPropertiesAction extends Action {

	StructuredViewer viewer = null;

	DumpPropertiesAction(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	public void run() {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if (sel.isEmpty() == false) {
			Object obj = sel.getFirstElement();
			if (obj instanceof DocbaseItem) {
				DocbaseItem di = (DocbaseItem) obj;
				if (di.getType() == DocbaseItem.TYPED_OBJ_TYPE) {
					try {
						IDfTypedObject to = (IDfTypedObject) di.getData();
						String objId = to.getString("r_object_id");
						PluginHelper.showPropertiesView(objId);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

}
