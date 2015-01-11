package com.documentum.devprog.eclipse.common;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	/*
	 * public static final String P_PATH = "pathPreference";
	 * 
	 * public static final String P_BOOLEAN = "booleanPreference";
	 * 
	 * public static final String P_CHOICE = "choicePreference";
	 * 
	 * public static final String P_STRING = "stringPreference";
	 */

	public static final String P_PORT = "portNumber";

	public static final String P_BUFFER_SIZE = "10000";

	public static final String P_TRACE_DELAY = "traceDelay";

	/**
	 * This is default attribute list used to query sysobjects within a folder.
	 * The results of this query are used to display contents of a folder.
	 */
	public static final String P_ATTR_LIST = "defaultAttrQueryList";

	/**
	 * Default columns that are displayed in the folder contents view
	 */
	public static final String P_VISIBLE_COLUMNS = "defaultVisibleColumns";

	/**
	 * Show all objects or just docs and folders.
	 */
	public static final String P_SHOW_ALL_OBJECTS = "showAllObjects";

}
