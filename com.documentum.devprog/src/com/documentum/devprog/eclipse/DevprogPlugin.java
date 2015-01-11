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
 * Created on Apr 29, 2004
 * 
 * Documentum Developer Program 2004
 *
 */
package com.documentum.devprog.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.service.prefs.Preferences;

/**
 * 
 * Main plugin class.
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 */
public class DevprogPlugin extends AbstractUIPlugin {

	// The shared instance.
	private static DevprogPlugin plugin;

	// Resource bundle.
	private ResourceBundle resourceBundle;

	public static String PLUGIN_ID = "com.documentum.devprog.eclipsePlugin";

	public static String VIEW_EXT_ID = PLUGIN_ID + ".viewExtension";

	public static String PERSPECTIVE_EXT_ID = "com.documentum.devprog.eclipsePlugin.perspectivesExtension";

	public static String PERSPECTIVE_ID = PERSPECTIVE_EXT_ID + ".dmPerspective";

	// ////////View Ids

	public final static String TREE_VIEW_ID = VIEW_EXT_ID + ".treeView";

	public final static String DOCBASE_ITEM_LIST_VIEW_ID = VIEW_EXT_ID
			+ ".docbaseItemListView";

	final public static String TYPE_VIEW_ID = VIEW_EXT_ID + ".typeTreeView";

	public static final String TRACE_VIEW_ID = VIEW_EXT_ID + ".traceView";

	final public static String QUERY_VIEW_ID = VIEW_EXT_ID + ".queryView";

	public final static String PROP_VIEW_EXT_ID = PLUGIN_ID + ".propViewAction";

	/**
	 * Id of the properties view.
	 */
	public static String PROP_VIEW_ID = VIEW_EXT_ID + ".propertiesView";

	final public static String QUICK_TYPE_HIERARCHY_VIEW_ID = VIEW_EXT_ID
			+ ".quickTypeHierarchyView";

	// ////Extension IDs
	public final static String REPO_TREE_EXT_ID = PLUGIN_ID + ".repoTreeAction";

	final public static String TYPE_VIEW_EXT_ID = PLUGIN_ID + ".typeTreeAction";

	/**
	 * The constructor.
	 */
	public DevprogPlugin() {
		super();
		plugin = this;
		
		// configureLog4j();
		// This is necessary for DFC to work correctly.
/*		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
*/
		try {
			resourceBundle = ResourceBundle
					.getBundle("com.documentum.devprog.DevprogPluginResources");

		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static DevprogPlugin getDefault() {
		return plugin;
	}

	// does not seem to work. Kept it for reference -aashish
	protected void configureLog4j() {

		try {
			URL installURL = getDefault().getDescriptor().getInstallURL();
			URL url = new URL(installURL, "config/log4j.properties");
			// url = Platform.r
			PropertyConfigurator.configure(url);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DevprogPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

}