/*
 * Created on Apr 12, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.PluginHelper;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DocbaseListEventListener implements IDoubleClickListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse
	 * .jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) {

		try {
			IStructuredSelection sel = (IStructuredSelection) event
					.getSelection();
			System.out.println("dblck: " + sel.isEmpty());
			if (sel.isEmpty() == false) {
				Object obj = sel.getFirstElement();
				if (obj instanceof DocbaseItem) {
					DocbaseItem di = (DocbaseItem) obj;

					if (di.isFolderType()) {
						System.out
								.println("dblck: about to call Helper:ShowFoldeRContents");
						PluginHelper.showFolderContents(di);
						PluginHelper.getRepoTreeViewer().expandToLevel(di, 2);

					} else {
						IDfTypedObject tObj = (IDfTypedObject) di.getData();
						PluginHelper.showPropertiesView(tObj
								.getString("r_object_id"));

					}
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
