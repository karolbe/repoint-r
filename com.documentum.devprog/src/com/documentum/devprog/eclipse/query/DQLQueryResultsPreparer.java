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
�*�
�*******************************************************************************/

/*
 * Created on Aug 6, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.query;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class DQLQueryResultsPreparer implements IRunnableWithProgress {

	private IDfCollection results = null;

	private LinkedList resultsList = null;

	DQLQueryResultsPreparer(IDfCollection coll) {
		results = coll;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			resultsList = new LinkedList();
			if (results == null) {
				return;
			}

			monitor.beginTask("Iterating collection ...",
					IProgressMonitor.UNKNOWN);
			while (results.next()) {

				if (monitor.isCanceled()) {
					monitor.setCanceled(true);
					break;
				}
				IDfTypedObject tObj = results.getTypedObject();
				resultsList.add(tObj);
				monitor.worked(1);
			}
			monitor.done();

		} catch (DfException dfe) {
			DfLogger.warn(this, "Error preparing results", null, dfe);

		}

	}

	LinkedList getResults() {
		return resultsList;
	}

}