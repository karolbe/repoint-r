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
 * Created on Aug 8, 2005
 *
 * EMC Documentum Developer Program 2005
 */
package com.documentum.devprog.eclipse.libraryfunc;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfList;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.documentum.operations.IDfOperation;
import com.documentum.operations.IDfOperationError;
import com.documentum.operations.IDfOperationMonitor;
import com.documentum.operations.IDfOperationNode;
import com.documentum.operations.IDfOperationStep;

public class OperationProgressMonitor implements IDfOperationMonitor

{

	private IProgressMonitor monitor = null;

	private int prevWork = 0;

	private IDfOperation oper = null;

	private boolean success = false;

	private StringBuffer errors = new StringBuffer(32);

	public void setMonitor(IProgressMonitor mon) {
		monitor = mon;
	}

	public IProgressMonitor getMonitor() {
		return monitor;
	}

	public int reportError(IDfOperationError arg0) throws DfException {
		addErrors(arg0.getMessage());
		DfLogger.debug(this, arg0.getMessage(), null, null);
		monitor.setCanceled(true);
		setSuccess(false);

		return IDfOperationMonitor.CONTINUE;
	}

	public int getYesNoAnswer(IDfOperationError arg0) throws DfException {
		// System.out.println("yesno " + arg0.getMessage());
		// DfLogger.debug(this, arg0.getMessage(), null, null);
		addErrors(arg0.getMessage());
		monitor.setCanceled(true);
		monitor.done();
		setSuccess(false);

		return IDfOperationMonitor.YES;
	}

	public int progressReport(IDfOperation operation, int operationPercentDone,
			IDfOperationStep step, int stepPercentDone, IDfOperationNode node)
			throws DfException {
		// System.out.println("progressReport: " + operationPercentDone);
		monitor.subTask(step.getDescription());
		monitor.worked(operationPercentDone - prevWork);
		prevWork = operationPercentDone;
		if (monitor.isCanceled()) {
			monitor.done();
			return IDfOperationMonitor.ABORT;
		}
		return IDfOperationMonitor.CONTINUE;
	}

	public String getErrors() {
		try {
			if (oper != null) {
				IDfList errLst = oper.getErrors();
				for (int i = 0; i < errLst.getCount(); i++) {
					String err = errLst.getString(i);
					addErrors(err);
				}
			}

		} catch (DfException dfe) {
			DfLogger.info(this, "", null, dfe);
			addErrors(dfe.getMessage());
		}
		return errors.toString();
	}

	public boolean success() {
		return success;
	}

	public void setSuccess(boolean succ) {
		success = succ;
	}

	public void setOperation(IDfOperation oper) {
		this.oper = oper;
	}

	public void addErrors(String msg) {
		errors.append(msg).append("\r\n");
	}

}
