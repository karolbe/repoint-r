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
 * Created on Jun 28, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.properties;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfLoginInfo;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class TestCustomAttrDetect {

	public static void main(String[] args) {
		try {
			IDfLoginInfo li = new DfLoginInfo();
			li.setUser("tuser8");
			li.setPassword("n0lif3");
			String docbase = "wdk52no";
			IDfClient lc = DfClient.getLocalClient();
			IDfSessionManager sm = lc.newSessionManager();
			sm.setIdentity(docbase, li);

			IDfSession sess = sm.getSession(docbase);
			IDfSysObject sobj = (IDfSysObject) sess.getObject(new DfId(
					"0912d6968000dc01"));

			IDfType tp = sobj.getType();

			int start_pos = tp.getInt("start_pos");
			System.out.println("start_pos " + start_pos);

			int attrCnt = sobj.getAttrCount();
			for (int i = start_pos; i < attrCnt; i++) {
				IDfAttr at = sobj.getAttr(i);
				System.out.println(at.getName());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
