/*
 * Created on Sep 27, 2006
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.common;

import com.documentum.devprog.eclipse.rcpapp.InstallerHelp;
import com.documentum.devprog.eclipse.rcpapp.RepointInstallerDialog;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class DFCConfigurationHelper {
	// Windows specific filesystem roots
	public static final String[] FS_ROOTS = { "c", "d", "e", "f", "g", "h",
			"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z" };

	/**
	 * Prompts the user to get the location of the DFC folder and the
	 * configuration folder and then configures Repoint for using DFC.
	 * 
	 * @return -1 to perform a restart. All other values do not mean anything
	 *         for now.
	 */
	public static int configureDfcJars(boolean overwrite) {
		try {
			File home = new File("");
			String dfcFldr = DFCConfigurationHelper.guessDFCLocation();
			String cFldr = DFCConfigurationHelper.guessConfigLocation();

			File libFldr = InstallerHelp.getDfcPluginLibFolder(home);

			if (libFldr != null) {
				File dfcJar = new File(libFldr.getAbsolutePath()
						+ File.separator + "dfc.jar");
				if (dfcJar.exists() == false || overwrite) {
					RepointInstallerDialog rd = new RepointInstallerDialog(
							Display.getDefault().getActiveShell());
					int status = rd.open();
					if (status == RepointInstallerDialog.OK) {
						return -1;
					} else {
						return 0;
					}

				}
			} else {
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						"Error", "Cannot locate DFC Plugin. ");

			}

			/*
			 * System.out.println("lib fldr: " + libFldr.getAbsolutePath());
			 * 
			 * File confFldr = InstallerHelp.getDfcPluginConfFolder(home);
			 * System.out.println("conf fldr: " + confFldr.getAbsolutePath());
			 * 
			 * InstallerHelp.copyFolderContents(new File(dfcFldr), libFldr,
			 * null);
			 * 
			 * File confJar = new File(confFldr.getAbsolutePath() +
			 * File.separator + "config.jar");
			 * InstallerHelp.createPropertiesJar(new File(cFldr), confJar);
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"Error initializing DFC", ex.getMessage());
		}
		return 0;
	}

	// NOTE This method is platform specific to Windows
	public static String guessDFCLocation() {
		if (SWT.getPlatform().equalsIgnoreCase("win32")) {
			String path = "Program Files\\Documentum\\Shared\\";
			for (int i = 0; i < FS_ROOTS.length; i++) {
				String cp = FS_ROOTS[i] + ":\\" + path;
				File fl = new File(cp);
				if (fl.exists()) {
					return fl.getAbsolutePath();
				}

			}
		}
		return null;
	}

	// NOTE this method is platform specific to windows.
	public static String guessConfigLocation() {
		if (SWT.getPlatform().equalsIgnoreCase("win32")) {
			String path = "Documentum\\Config\\";

			for (int i = 0; i < FS_ROOTS.length; i++) {
				String cp = FS_ROOTS[i] + ":\\" + path;
				File fl = new File(cp);
				if (fl.exists()) {
					return fl.getAbsolutePath();
				}

			}
		}
		return null;
	}

}
