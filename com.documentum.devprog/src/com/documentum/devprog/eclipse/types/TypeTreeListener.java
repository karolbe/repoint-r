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
 * Created on Jul 20, 2004
 *
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.types;

import com.documentum.fc.common.DfLogger;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 * 
 * 
 * @author Aashish Patil(aashish.patil@documentum.com)
 */
public class TypeTreeListener implements IDoubleClickListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse
	 * .jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) {
		try {
			// TreeViewer viewer = (TreeViewer) dce.getSource();
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			ITypeViewData tvd = (ITypeViewData) selection.getFirstElement();
			tvd.onDoubleClick(event);
		} catch (Exception ex) {
			DfLogger.error(this, "Error in type tree event listener", null, ex);
		}

	}

}
