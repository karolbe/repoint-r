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
 * Created on Apr 29, 2004
 * 
 * Documentum Developer Program 2004
 */
package com.documentum.devprog.eclipse.common;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;

/**
 * This class maintains the plugin sessions.
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 * 
 * 
 */
public class PluginState {

	private static IDfSessionManager sessMgr = null;

	private static HashMap s_state = null;

	private static String currentDocbase = null;

	private static String logId = PluginState.class.getName();

	private static String dfcFolder = null;

	private static String dmConfigFolder = null;

	private static Set loggedInDocbases = new HashSet();

	static {
		s_state = new HashMap();
	}

	/**
	 * Gets an existing session manager or creates a new one if one does not
	 * exist.
	 * 
	 * @return
	 */
	public static IDfSessionManager getSessionManager() {
		if (sessMgr == null) {
			try {
				sessMgr = DfClient.getLocalClient().newSessionManager();
			} catch (DfException dfe) {
				DfLogger.error(logId, "Error creating session manager", null,
						dfe);
			}
		}
		return sessMgr;
	}

	public static void setSessionManager(IDfSessionManager sessionMgr) {
		sessMgr = sessionMgr;
	}

	public static Object get(String key) {
		return s_state.get(key);
	}

	public static void set(String key, Object value) {
		s_state.put(key, value);
	}

	public static void setDocbase(String docbase) {
		currentDocbase = docbase;
	}

	public static String getDocbase() {
		return currentDocbase;
	}

	/**
	 * Adds a new identity to the session manager. If an identity already
	 * exists, it is cleared and the new identity added.
	 * 
	 * @param username
	 * @param password
	 * @param domain
	 * @param docbase
	 * @param authenticate
	 *            Flag to indicate whether user should be immediately
	 *            authenticated. false indicates that authentication should be
	 *            delayed till a session is actually requested.
	 */
	public static boolean addIdentity(String username, String password,
			String domain, String docbase, boolean authenticate) {
		try {
			IDfLoginInfo li = new DfLoginInfo();
			li.setUser(username);
			li.setPassword(password);
			li.setDomain(domain);

			IDfSessionManager sessMgr = getSessionManager();
			// if (sessMgr.hasIdentity(docbase))
			{
				sessMgr.clearIdentity(docbase);
				/*
				 * try { Thread.sleep(3000); } catch(InterruptedException ie){}
				 */
			}
			sessMgr.setIdentity(docbase, li);

			if (authenticate) {
				sessMgr.authenticate(docbase);

			}
			setDocbase(docbase);
			loggedInDocbases.add(docbase);
			return true;
		} catch (DfException dfe) {
			DfLogger.error(logId, "Error while adding idendity", null, dfe);
			return false;
		}
	}

	/**
	 * Checks if a identity has been associated for the specified docbase.
	 * 
	 * @param docbase
	 * @return
	 */
	public static boolean hasIdentity(String docbase) {
		IDfSessionManager sMgr = getSessionManager();
		return sMgr.hasIdentity(docbase);
	}

	/**
	 * Releases the session.
	 * 
	 * @param sess
	 */
	public static void releaseSession(IDfSession sess) {
		if (sess != null) {
			getSessionManager().release(sess);
		}
	}

	/**
	 * Gets a session based on object id. It first gets the docbase name based
	 * on id and then gets the session based on docbase name. This is a
	 * convenience method.
	 * 
	 * @param objectId
	 * @return
	 */
	public static IDfSession getSessionById(String id) {
		try {
			IDfClient localClient = DfClient.getLocalClient();
			IDfId objId = new DfId(id);
			String docbase = localClient.getDocbaseNameFromId(objId);
			setDocbase(docbase);
			return getSession(docbase);
		} catch (DfException dfe) {
			DfLogger.error(logId, "Error getting session by id", null, dfe);
			return null;
		}
	}

	/**
	 * Gets a session based on object id. It first gets the docbase name based
	 * on id and then gets the session based on docbase name. This is a
	 * convenience method.
	 * 
	 * @param objectId
	 * @return
	 */
	public static IDfSession getSessionById(IDfId id) {
		try {
			IDfClient localClient = DfClient.getLocalClient();
			String docbase = localClient.getDocbaseNameFromId(id);
			setDocbase(docbase);
			return getSession(docbase);
		} catch (DfException dfe) {
			DfLogger.error(logId, "Error getting session by id", null, dfe);
			return null;
		}
	}

	/**
	 * Gets a session based on docbase name.
	 * 
	 * @param docbase
	 * @return
	 */
	public static IDfSession getSession(String docbase) {
		try {
			setDocbase(docbase);
			return getSessionManager().getSession(docbase);
		} catch (DfException dfe) {
			DfLogger.debug(logId, "Error getting session", null, dfe);
			return null;
		}
	}

	/**
	 * Gets the session based on current docbase. This session must be released
	 * using releaseSession(...) method call.
	 * 
	 * @return
	 */
	public static IDfSession getSession() {
		try {
			return getSessionManager().getSession(getDocbase());
		} catch (DfException dfe) {
			return null;
		}
	}

	public static IDfClientX getClientX() {
		IDfClientX cx = new DfClientX();
		return cx;
	}

	public static IDfClient getLocalClient() throws DfException {
		return getClientX().getLocalClient();
	}

	public static String[] getLoggedInDocbases() {
		return (String[]) loggedInDocbases.toArray(new String[] {});
	}

	// NOTE This method is platform specific to Windows
	private static String guessDFCLocation() {
		return DFCConfigurationHelper.guessDFCLocation();
	}

	// NOTE this method is platform specific to windows.
	private static String guessConfigLocation() {
		return DFCConfigurationHelper.guessConfigLocation();
	}

	/**
	 * NOTE This is a System dependent method and currently only works on
	 * Windows
	 * 
	 * @return
	 */
	public static String getDfcFolder() {
		if (dfcFolder == null) {
			dfcFolder = guessDFCLocation();
		}
		return dfcFolder;
	}

	/**
	 * NOTE This is a System dependent method and currently only works on
	 * Windows
	 * 
	 * @return
	 */
	public static String getDmConfigFolder() {
		if (dmConfigFolder == null) {
			dmConfigFolder = guessConfigLocation();
		}
		return dmConfigFolder;
	}

	/**
	 * Get the location of the dfc.properties file. Note that this is a best
	 * guess
	 * 
	 * @return
	 */
	public static String getDFCPropertiesFile() {
		String cfldr = PluginState.getDmConfigFolder();
		if (cfldr != null) {
			if (cfldr.endsWith(File.separator) == false) {
				cfldr += File.separator;
			}
			cfldr += "dfc.properties";
			return cfldr;
		} else {
			return null;
		}
	}

	/**
	 * Get the location of the log4j.properties file. Note that this is a best
	 * guess
	 * 
	 * @return
	 */
	public static String getLog4jPropertiesFile() {
		String cfldr = PluginState.getDmConfigFolder();
		if (cfldr != null) {
			if (cfldr.endsWith(File.separator) == false) {
				cfldr += File.separator;
			}
			cfldr += "log4j.properties";
			return cfldr;
		} else {
			return null;
		}
	}

}