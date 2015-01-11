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
 * Created on Sep 24, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.bof;

import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfGlobalModuleRegistry;
import com.documentum.fc.client.IDfModuleDescriptor;

public class BOF53Helper {

	private static String logId = BOF53Helper.class.getName();

	public static String prepareInfo(Object obj) {

		StringBuffer bufInfo = new StringBuffer(32);
		bufInfo.append("<html><body style=\"font-family:Arial,Helvetica;color:blue;font-size:12px\">");
		if (obj instanceof IDfModuleDescriptor) {

			IDfModuleDescriptor desc = (IDfModuleDescriptor) obj;
			String name = desc.getObjectName();
			bufInfo.append("<br/><b>Name:</b> ").append(name);

			try {
				bufInfo.append("<br/><br/><b>Interfaces</b>");
				IDfEnumeration ienum = desc.getInterfaceNames();
				while (ienum.hasMoreElements()) {
					String intf = (String) ienum.nextElement();
					bufInfo.append("<br/>").append(intf);
				}
			} catch (Exception ex) {
				DfLogger.error(logId, "Error getting desc interface", null, ex);
				bufInfo.append("<br/>Error: ").append(ex.getMessage());
			}

			try {
				String implCl = desc.getImplementationClassName();
				bufInfo.append("<br/><br/><b>Implementation Class:</b> ")
						.append(implCl);
			} catch (Exception ex) {
				DfLogger.error(logId, "Error getting impl class", null, ex);
				bufInfo.append("<br/>Error: ").append(ex.getMessage());
			}

			try {
				bufInfo.append("<br/><br/><b>Version:</b> ").append(
						desc.getVersion());
			} catch (Exception ex) {
				bufInfo.append("<br/>Error: ").append(ex.getMessage());
			}

			try {
				bufInfo.append("<br/><br/><b>Is Dynamically Provisioned:</b> ")
						.append(desc.isDynamicallyProvisioned());
			} catch (Exception ex) {
				bufInfo.append("<br/>Error: ").append(ex.getMessage());
			}

			try {
				bufInfo.append("<br/><br/><b>Dependency List</b>");
				IDfEnumeration denum = desc.getDependencyList();
				while (denum.hasMoreElements()) {
					String dep = (String) denum.nextElement();
					bufInfo.append("<br/>").append(dep);
				}
			} catch (Exception ex) {
				DfLogger.error(logId, "Error getting dependency list", null, ex);
				bufInfo.append("<br/>Error: ").append(ex.getMessage());
			}

		}
		bufInfo.append("</body></html>");
		return bufInfo.toString();

	}

	/**
	 * Checks if the global module registry is accessible This method will also
	 * return false if the DFC version is not 5.3
	 * 
	 * @return
	 */
	public static boolean isGlobalRegistryAccessible() {
		try {

			IDfGlobalModuleRegistry modReg = DfClient.getLocalClient()
					.getModuleRegistry();
			IDfEnumeration enumTmp = modReg.getServiceDescriptors();
			return true;

		} catch (Exception ex) {
			DfLogger.error(logId, "Global Module Registry is not Accessible: "
					+ ex.getClass().getName() + ": " + ex.getMessage(), null,
					null);

		}
		return false;

	}

}
