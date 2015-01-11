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
 * Created on May 26, 2004
 * 
 * Documentum Developer Program 2004
 *
 */
package com.documentum.devprog.eclipse.perspective;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.api.ApiView;
import com.documentum.devprog.eclipse.bof.BOFView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * 
 * 
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 */
public class DevprogPerspective implements IPerspectiveFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui
	 * .IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		createLayout(layout);
	}

	protected void createLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		IFolderLayout fldrLayout = layout.createFolder("left",
				IPageLayout.LEFT, (float) 0.75, editorArea);
		fldrLayout.addView(DevprogPlugin.TREE_VIEW_ID);
		layout.getViewLayout(DevprogPlugin.TREE_VIEW_ID).setCloseable(false);

		fldrLayout.addView(DevprogPlugin.TYPE_VIEW_ID);
		layout.getViewLayout(DevprogPlugin.TYPE_VIEW_ID).setCloseable(false);

		IFolderLayout fldrCenter = layout.createFolder("center",
				IPageLayout.RIGHT, (float) .20, "left");

		fldrCenter.addView(DevprogPlugin.DOCBASE_ITEM_LIST_VIEW_ID);
		// fldrCenter.addView(DevprogPlugin.PROP_VIEW_ID);

		fldrCenter.addView(DevprogPlugin.QUERY_VIEW_ID);
		layout.getViewLayout(DevprogPlugin.QUERY_VIEW_ID).setCloseable(false);

		fldrCenter.addView(ApiView.API_VIEW_ID);
		layout.getViewLayout(ApiView.API_VIEW_ID).setCloseable(false);

		fldrCenter.addView(IProgressConstants.PROGRESS_VIEW_ID);

		fldrCenter.addView(BOFView.BOF_VIEW_ID);
		layout.getViewLayout(BOFView.BOF_VIEW_ID).setCloseable(false);

		fldrCenter.addView(DevprogPlugin.TRACE_VIEW_ID);
		layout.getViewLayout(DevprogPlugin.TRACE_VIEW_ID).setCloseable(false);

		IFolderLayout fldrRight = layout.createFolder("right",
				IPageLayout.RIGHT, (float) 0.65, "center");
		fldrRight.addView(DevprogPlugin.PROP_VIEW_ID);
		layout.getViewLayout(DevprogPlugin.PROP_VIEW_ID).setCloseable(false);
		fldrRight.addView(DevprogPlugin.QUICK_TYPE_HIERARCHY_VIEW_ID);
		layout.getViewLayout(DevprogPlugin.QUICK_TYPE_HIERARCHY_VIEW_ID)
				.setCloseable(false);

	}

}
