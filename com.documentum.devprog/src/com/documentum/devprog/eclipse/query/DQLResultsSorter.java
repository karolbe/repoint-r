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
 * Created on Aug 5, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.query;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfTypedObject;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class DQLResultsSorter extends ViewerSorter {
	/*
	 * The property on which sorting takes place.
	 */
	private String propertyName = null;

	DQLResultsSorter(String propName) {
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
			IDfTypedObject t1 = (IDfTypedObject) e1;
			IDfTypedObject t2 = (IDfTypedObject) e2;
			if ((propertyName == null) || (propertyName.length() == 0)) {
				return 0;
			}

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
		} catch (DfException dfe) {
			DfLogger.warn(this, "Error obtaining property", null, null);
			return 0;
		}

	}
}