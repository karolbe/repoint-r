/*******************************************************************************
 * Copyright (c) 2005-2006, EMC Corporation 
 * All rights reserved.

 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided that 
 * the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * - Neither the name of the EMC Corporation nor the names of its 
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *******************************************************************************/

/*
 * Created on Jun 29, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.tree.DocbaseItem;
import com.documentum.devprog.eclipse.tree.RepoTreeExtension;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class CheckoutAction extends RepoTreeExtension {

	public boolean showAction() {
		try {
			DocbaseItem dtd = super.getDocbaseItem();

			if ((dtd != null) && (dtd.isDocumentType())) {
				IDfTypedObject to = (IDfTypedObject) dtd.getData();
				String lockOwner = to.getString("r_lock_owner");
				return (lockOwner == null || lockOwner.length() == 0);
			}
		} catch (Exception ex) {
			DfLogger.error(this, "Error checkout showACtion", null, ex);
		}
		return false;
	}

	public void run() {
		try {
			DocbaseItem dtd = super.getDocbaseItem();
			IDfTypedObject to = (IDfTypedObject) dtd.getData();

			CheckoutJob cj = new CheckoutJob();

			String objId = to.getString("r_object_id");
			cj.setObjectId(new DfId(objId));

			if (PluginHelper.hasDFCProperty("dfc.checkout.dir") == false) {
				DirectoryDialog dd = new DirectoryDialog(super.getShell());
				dd.setMessage("No Checkout Folder Configured in dfc.properties. Please Select a Folder For Checkout ");
				String fldr = dd.open();
				if (fldr != null) {
					cj.setDestinationFolder(fldr);
				}
			}
			cj.setUIData(super.getObjectListViewer(), dtd);
			cj.setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
			cj.setUser(true);
			cj.schedule();
		} catch (Exception ex) {

		}

	}

}
