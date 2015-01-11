/*
 * Created on Sep 6, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.rcpapp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class RcpActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exitAction = null;
	private IWorkbenchAction aboutAction = null;
	private IWorkbenchAction prefAction = null;
	private IWorkbenchAction updateDfcAction = null;

	public RcpActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager repointMenu = new MenuManager("&Repoint", "repoint");
		repointMenu.add(prefAction);
		repointMenu.add(updateDfcAction);
		repointMenu.add(exitAction);

		MenuManager helpMenu = new MenuManager("&Help", "help");
		helpMenu.add(aboutAction);

		menuBar.add(repointMenu);
		menuBar.add(helpMenu);

		super.fillMenuBar(menuBar);
	}

	protected void makeActions(IWorkbenchWindow window) {
		exitAction = ActionFactory.QUIT.create(window);
		aboutAction = ActionFactory.ABOUT.create(window);
		prefAction = ActionFactory.PREFERENCES.create(window);

		updateDfcAction = new UpdateDfcAction("Update DFC Configuration");

		super.makeActions(window);
	}

}
