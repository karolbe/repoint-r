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
 */
package com.documentum.devprog.eclipse.common;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.*;

import java.io.File;
import java.util.*;

/**
 * This class maintains the plugin sessions.
 * 
 * @author Aashish Patil (aashish.patil@documentum.com)
 * 
 * 
 */
public class PluginState {

	static class DocbaseInfo {
		public IDfSessionManager sessionManager;
		public IDfClient dfClient;

		public DocbaseInfo(IDfSessionManager sessionManager, IDfClient dfClient) {
			this.sessionManager = sessionManager;
			this.dfClient = dfClient;
		}
	}

	private static Map<String, DocbaseInfo> sessMgrs = new HashMap<String, DocbaseInfo>();

	private static Map<String, Object> s_state = null;

	private static String currentDocbase = null;

	private static IDfSessionManager currentSessionMgr = null;

	private static String logId = PluginState.class.getName();

	private static String dfcFolder = null;

	private static String dmConfigFolder = null;

	private static Set<String> loggedInDocbases = new HashSet<String>();

	static {
		s_state = new HashMap<String, Object>();
	}

	/**
	 * Gets an existing session manager or creates a new one if one does not
	 * exist.
	 * 
	 * @return
	 */
	public static IDfSessionManager getSessionManager() {
		if (currentSessionMgr == null) {
			try {
				currentSessionMgr = DfClient.getLocalClient().newSessionManager();

			} catch (DfException dfe) {
				DfLogger.error(logId, "Error creating session manager", null,
						dfe);
			}
		}
		return currentSessionMgr;
	}

	public static void setSessionManager(IDfSessionManager sessionMgr) {
		currentSessionMgr = sessionMgr;
	}

	public static Object get(String key) {
		return s_state.get(key);
	}

	public static void set(String key, Object value) {
		s_state.put(key, value);
	}

	public static void setDocbase(String docbase) {
		if(sessMgrs.containsKey(docbase)) {
			currentDocbase = docbase;
			currentSessionMgr = sessMgrs.get(docbase).sessionManager;
		} else {
			currentSessionMgr = null;
			currentDocbase = null;
		}
	}

	public static String getDocbase() {
		return currentDocbase;
	}

	public static List<String> getDocbases() {
		List<String> docbases = new ArrayList<String>();
		docbases.addAll(sessMgrs.keySet());
		return docbases;
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
			sessMgr.clearIdentity(docbase);
			sessMgr.setIdentity(docbase, li);

			if (authenticate) {
				sessMgr.authenticate(docbase);
			}

			IDfClient localClient = DfClient.getLocalClient();
			sessMgrs.put(docbase, new DocbaseInfo(sessMgr, localClient));
			setDocbase(docbase);
			setSessionManager(sessMgr);
			loggedInDocbases.add(docbase);
			return true;
		} catch (DfException dfe) {
			DfLogger.error(logId, "Error while adding idendity", null, dfe);
			return false;
		}
	}

	/**
	 * Adds a new external identity to the session manager. If an identity already
	 * exists, it is cleared and the new identity added.
	 *
	 * @param username
	 * @param password
	 * @param domain
	 * @param docbase
	 * @param docbroker
	 * @param authenticate
	 *            Flag to indicate whether user should be immediately
	 *            authenticated. false indicates that authentication should be
	 *            delayed till a session is actually requested.
	 */
	public static boolean addExternalIdentity(String username, String password,
									  String domain, String docbase, String docbroker, boolean authenticate) {
		try {
			IDfLoginInfo li = new DfLoginInfo();
			li.setUser(username);
			li.setPassword(password);
			li.setDomain(domain);


			IDfClient externalClient;
			externalClient = DfClient.getLocalClient();
			IDfTypedObject config = externalClient.getClientConfig();
			config.setString("primary_host", docbroker);
			config.setInt("primary_port", 1489);
			IDfSessionManager sessMgr = externalClient.newSessionManager();
			sessMgr.clearIdentity(docbase);
			sessMgr.setIdentity(docbase, li);

			if (authenticate) {
				sessMgr.authenticate(docbase);
			}

			sessMgrs.put(docbase, new DocbaseInfo(sessMgr, externalClient));
			setDocbase(docbase);
			setSessionManager(sessMgr);
			loggedInDocbases.add(docbase);
			return true;
		} catch (DfException dfe) {
			DfLogger.error(logId, "Error while adding external identity", null, dfe);
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
		DocbaseInfo docbaseInfo = sessMgrs.get(docbase);
		if(docbaseInfo == null) {
			return false;
		}
		return docbaseInfo.sessionManager.hasIdentity(docbase);
	}

	/**
	 * Releases the session.
	 * 
	 * @param sess
	 */
	public static void releaseSession(IDfSession sess) {
		if (sess != null) {
			try {
				sessMgrs.get(sess.getDocbaseName()).sessionManager.release(sess);
			} catch (DfException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets a session based on object id. It first gets the docbase name based
	 * on id and then gets the session based on docbase name. This is a
	 * convenience method.
	 * 
	 * @param id
	 * @return
	 */
	public static IDfSession getSessionById(String id) {
		try {
			for(DocbaseInfo docbaseInfo: sessMgrs.values()) {
				IDfId objId = new DfId(id);
				try {
					String docbase = docbaseInfo.dfClient.getDocbaseNameFromId(objId);
					setDocbase(docbase);
					return getSession(docbase);
				} catch (DfException d) {
					// ignore
				}
			}
			throw new DfException("No matching session");
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
	 * @param id
	 * @return
	 */
	public static IDfSession getSessionById(IDfId id) {
		return getSessionById(id.toString());
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
