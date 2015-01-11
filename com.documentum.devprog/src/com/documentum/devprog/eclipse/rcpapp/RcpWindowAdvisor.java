/*
 * Created on Sep 6, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.rcpapp;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class RcpWindowAdvisor extends WorkbenchWindowAdvisor {

	/**
	 * @param configurer
	 */
	public RcpWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);

	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new RcpActionBarAdvisor(configurer);
	}

}
