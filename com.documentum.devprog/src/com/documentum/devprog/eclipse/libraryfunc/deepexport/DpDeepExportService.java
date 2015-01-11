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
 * Created on May 16, 2003
 * Documentum Developer Program 2003
 * 
 * 
 */
package com.documentum.devprog.eclipse.libraryfunc.deepexport;

import com.documentum.com.*;
import com.documentum.fc.client.*;

import com.documentum.fc.common.*;
import com.documentum.operations.*;

import java.io.*;
import java.util.*;

/**
 * The implementation class of the deep export service.
 * 
 * @author Andrew Chapman (andrew.chapman@documentum.com) Original logic for
 *         DeepExport.
 * 
 * @author Aashish Patil (aashish.patil@documentum.com) Wrapped the logic in a
 *         SBO and added support for vDocs.
 * 
 * 
 */
public class DpDeepExportService extends DfService implements
		IDpDeepExportService {

	private IDfSessionManager sm = null;

	/**
	 * SBO version
	 */
	private String m_strVersion = "1.0";

	/**
	 * Descriptive string of the vendor distributing the SBO.
	 */
	private String m_strVendorDescription = "Documentum Developer Program";

	/**
	 * Contains ids of children of a virtual document.
	 */
	private Set m_childIds = new HashSet(5);

	/**
	 * The listeners that want to listen for the completion of deep export of a
	 * folder.
	 * 
	 */
	private List m_listeners = new LinkedList();

	/**
	 * The path on the local filesystem to which the folder will be deep
	 * exported.
	 */
	private String m_strLocalFilesystemPath = null;

	/**
	 * The object id of the folder that needs to be deep exported.
	 */
	private IDfId m_folderId = null;

	/**
	 * Set the path on the local filesystem to which the file will be exported
	 * 
	 * @param path
	 *            local filesystem path
	 */
	protected void setLocalFilesystemPath(String path) {
		m_strLocalFilesystemPath = path;
	}

	/**
	 * Gets the path on the local filesystem to which the folder will be deep
	 * exported.
	 * 
	 * @return the local filesystem path
	 */
	protected String getLocalFilesystemPath() {
		return m_strLocalFilesystemPath;
	}

	/**
	 * sets the id of the folder to be deep exported
	 * 
	 * @param id
	 */
	protected void setFolderId(IDfId id) {
		m_folderId = id;
	}

	/**
	 * Gets the id of the folder to be deep exported.
	 * 
	 * @return
	 */
	protected IDfId getFolderId() {
		return m_folderId;
	}

	/*
	 * 
	 * 
	 * @param folderId @param localFilesystemPath @throws DfException @throws
	 * DpDeepExportServiceException
	 * 
	 * @see
	 * com.documentum.devprog.deepexport.IDpDeepExportService#deepExportFolder
	 * (com.documentum.fc.common.IDfId, java.lang.String)
	 */
	public String deepExportFolder(IDfId folderId, String localFilesystemPath)
			throws DfException, DpDeepExportServiceException {
		IDfSession session = null;

		try {
			DfLogger.debug(this,
					"Folder id of folder to export: " + folderId.getId(), null,
					null);
			// Create the destination folder on the filesystem. This routine
			// will create it
			// even if it is nested a few levels down.
			createFolderPath(localFilesystemPath);

			IDfClient localClient = DfClient.getLocalClient();
			String docbase = localClient.getDocbaseNameFromId(folderId);
			session = getSession(docbase);
			// This is a deep export, so get all of the folders in the specified
			// location
			// and then export the contents of each folder
			String qualification = "select r_object_id from dm_folder where FOLDER(ID('"
					+ folderId + "'), DESCEND)";
			IDfCollection colDQL = execQuery(session, qualification);

			IDfFolder myFolder = null;

			myFolder = (IDfFolder) session.getObject(folderId);
			StringBuffer bufAbsPath = new StringBuffer(32);
			bufAbsPath.append(localFilesystemPath)
					.append(System.getProperty("file.separator"))
					.append(myFolder.getObjectName());
			localFilesystemPath = bufAbsPath.toString();
			doExport(session, myFolder, bufAbsPath.toString());

			String docbaseRootFolderPath = myFolder.getFolderPath(0);

			while (colDQL.next()) {
				// Get the r_object_id of each folder
				IDfAttr attr = colDQL.getAttr(0);
				String objectId = colDQL.getString(attr.getName());

				// Get the docbase folder object
				myFolder = (IDfFolder) session.getObject(new DfId(objectId));

				// Build the absolute filesystem folder path be appending the
				// docbase path to the
				// file system root.
				StringBuffer bufAbsFolderPath = new StringBuffer(32);
				bufAbsFolderPath.append(localFilesystemPath);

				String path0 = null;
				for (int ctrPaths = 0; ctrPaths < myFolder.getFolderPathCount(); ctrPaths++) {
					String tmpPath = myFolder.getFolderPath(ctrPaths);
					if (tmpPath.startsWith(docbaseRootFolderPath)) {
						path0 = tmpPath;
						break;
					}
				}
				if (path0 != null) {

					String subFolderPath = myFolder.getFolderPath(0).substring(
							docbaseRootFolderPath.length(), path0.length());
					bufAbsFolderPath.append(subFolderPath);
					String absFolderPath = bufAbsFolderPath.toString();

					// Export this docbase folder using the Export Operation
					doVirtualDocExport(session, myFolder, absFolderPath);
					doExport(session, myFolder, absFolderPath);
				}
			}
			return bufAbsPath.toString();
		} finally {
			releaseSession(session);
		}
	}

	/**
	 * Exports all virtual documents in a docbase folder to the local
	 * filesystem.
	 * 
	 * @param sess
	 *            docbase session
	 * @param exportFolder
	 *            the docbase folder whose vDocs need to be exported
	 * @param myFilesystemDirectory
	 *            The filesystem directory that will contain the docbase folder.
	 * 
	 * @throws DfException
	 */
	private void doVirtualDocExport(IDfSession sess, IDfFolder exportFolder,
			String myFilesystemDirectory) throws DfException {
		IDfCollection col = null;

		try {

			IDfClientX m_clientx = new DfClientX();
			IDfExportOperation operation = m_clientx.getExportOperation();

			operation.setDestinationDirectory(myFilesystemDirectory);

			// Do not need to call createFolderPath, just createFolder as we
			// know that the rest
			// of the path will already be there already.
			createFolder(myFilesystemDirectory);

			String qualification = "select * from dm_document where FOLDER(ID('"
					+ exportFolder.getObjectId()
					+ "')) AND ( (r_is_virtual_doc = 1) OR (r_link_cnt > 0))";

			IDfQuery q = m_clientx.getQuery(); // Create query object
			q.setDQL(qualification); // Give it the query
			col = q.execute(sess, IDfQuery.DF_READ_QUERY); // Execute
															// synchronously

			while (col.next()) {
				String name = col.getString("object_name");
				IDfFile destFile = m_clientx.getFile(myFilesystemDirectory
						+ name);
				// check if the file has been exported (exists) to avoid another
				// export
				if (!destFile.exists()) {
					// Create an IDfSysObject representing the object in the
					// collection
					String id = col.getString("r_object_id");
					IDfId idObj = m_clientx.getId(id);
					IDfSysObject myObj = (IDfSysObject) sess.getObject(idObj);
					DfLogger.debug(this,
							"About to export object: " + idObj.getId() + ": "
									+ myObj.getObjectName(), null, null);
					if (myObj.isVirtualDocument()) {
						DfLogger.debug(this, "Found a vDoc", null, null);
						IDfVirtualDocument vDoc = myObj.asVirtualDocument(
								"CURRENT", false);
						operation.add(vDoc);
						int childCount = vDoc.getUniqueObjectIdCount();
						// store ids of all children. when adding a dm_document
						// these children will skipped since they have been
						// already
						// added as part of assembly of a vDoc.
						for (int ctrChild = 0; ctrChild < childCount; ctrChild++) {
							IDfId childId = vDoc.getUniqueObjectId(ctrChild);
							m_childIds.add(childId.getId());
						}
					}

				}
			}

			// see sample: Operations- Execute and Check Errors
			executeOperation(operation);
		} finally {
			if (col != null)
				col.close();

		}

	}

	/**
	 * This method exports a single docbase folder to a filesystem directory -
	 * it will create the filesystem sbdirectory if necessary
	 * 
	 * @param sess
	 *            docbaseSession
	 * @param exportFolder
	 *            The docbase folder to export
	 * @param myFilesystemDirectory
	 *            The local filesystem directory to export to.
	 * 
	 * @throws DfException
	 */
	private void doExport(IDfSession sess, IDfFolder exportFolder,
			String myFilesystemDirectory) throws DfException {
		IDfCollection col = null;

		try {

			IDfClientX m_clientx = new DfClientX();
			IDfExportOperation operation = m_clientx.getExportOperation();

			operation.setDestinationDirectory(myFilesystemDirectory);

			// Do not need to call createFolderPath, just createFolder as we
			// know that the rest
			// of the path will already be there already.
			createFolder(myFilesystemDirectory);

			String qualification = "select * from dm_document where FOLDER(ID('"
					+ exportFolder.getObjectId() + "'))";

			IDfQuery q = m_clientx.getQuery(); // Create query object
			q.setDQL(qualification); // Give it the query
			col = q.execute(sess, IDfQuery.DF_READ_QUERY); // Execute
															// synchronously

			while (col.next()) {
				String name = col.getString("object_name");
				IDfFile destFile = m_clientx.getFile(myFilesystemDirectory
						+ name);
				// check if the file has been exported (exists) to avoid another
				// export
				if (!destFile.exists()) {
					// Create an IDfSysObject representing the object in the
					// collection
					String id = col.getString("r_object_id");
					IDfId idObj = m_clientx.getId(id);
					IDfSysObject myObj = (IDfSysObject) sess.getObject(idObj);

					if (!m_childIds.contains(idObj.getId()))
					// do not export children of vDocs
					{
						DfLogger.debug(this,
								"About to export object: " + idObj.getId()
										+ ": " + myObj.getObjectName(), null,
								null);
						// add the IDfSysObject to the operation
						operation.add(myObj);
					}
				}
			}

			// see sample: Operations- Execute and Check Errors
			executeOperation(operation);
		} finally {
			if (col != null)
				col.close();

		}
	}

	/**
	 * Execute a DQL query and return the result
	 * 
	 * @param sess
	 *            The docbase session
	 * @param queryString
	 *            The DQL query
	 * 
	 * @return IDfCollection the results of executing the query.
	 * @throws DfException
	 * 
	 */
	private IDfCollection execQuery(IDfSession sess, String queryString)
			throws DfException {
		IDfCollection col = null; // For the result

		IDfQuery q = new DfQuery(); // Create query object
		q.setDQL(queryString); // Give it the query
		col = q.execute(sess, DfQuery.DF_READ_QUERY); // Execute synchronously

		return col;
	}

	/**
	 * Executes the export operation and prints any errors generated to the log
	 * file
	 * 
	 * @param operation
	 *            The operation to execute
	 * @throws DfException
	 * 
	 */
	protected void executeOperation(IDfOperation operation) throws DfException {
		// operation.setOperationMonitor( new Progress() );

		// Execute the operation
		boolean executeFlag = operation.execute();

		// Check if any errors occured during the execution of the operation
		if (executeFlag == false) {
			// Get the list of errors
			IDfList errorList = operation.getErrors();
			StringBuffer bufMessage = new StringBuffer(32);
			IDfOperationError error = null;

			// Iterate through the errors and concatenate the error messages
			for (int i = 0; i < errorList.getCount(); i++) {
				error = (IDfOperationError) errorList.get(i);
				bufMessage.append(error.getMessage());
			}

			DfLogger.warn(this, bufMessage.toString(), null, null);

		}
	} // end: executeOperation(...)

	/**
	 * Recursively create folders in a path.
	 * 
	 * @param myFilesystemDirectory
	 *            The local filesystem folder to create with full path info
	 * 
	 */
	protected void createFolderPath(String myFilesystemDirectory) {

		File dir = new File(myFilesystemDirectory);
		if (dir.exists() == false) {
			boolean res = dir.mkdirs();
			if (!res) {
				DfLogger.error(this, "Error creating folder path: "
						+ myFilesystemDirectory, null, null);
			}
		}
		/*
		 * StringBuffer bufFolderPath = new StringBuffer(64);
		 * 
		 * String pathSeparator = System.getProperty("file.separator");
		 * java.util.StringTokenizer st = new
		 * java.util.StringTokenizer(myFilesystemDirectory, pathSeparator);
		 * 
		 * while (st.hasMoreTokens()) { bufFolderPath.append(st.nextToken());
		 * bufFolderPath.append(pathSeparator);
		 * createFolder(bufFolderPath.toString()); }
		 */
	}

	/**
	 * Creates a single folder on the local filesystem directory.
	 * 
	 * @param myFilesystemDirectory
	 *            The folder to create.
	 * 
	 */
	private void createFolder(String myFilesystemDirectory) {
		// if the directory doesn't exist, make it

		DfLogger.debug(this, "About to create folder " + myFilesystemDirectory,
				null, null);
		File file = new File(myFilesystemDirectory);
		try {
			if (!file.exists()) {
				file.mkdirs();
				DfLogger.debug(this, "Created directory "
						+ myFilesystemDirectory, null, null);
			} else {
				DfLogger.debug(this, "Folder: " + myFilesystemDirectory
						+ " already exists", null, null);
			}
		} catch (SecurityException e) {
			DfLogger.debug(this, "Seurity Exception: " + e.toString(), null,
					null);

		}
	}

	/**
	 * Notify all listeners of the completion of deep export.
	 * 
	 */
	private void notifyAllListeners() {
		int numListeners = m_listeners.size();
		for (int ctrListeners = 0; ctrListeners < numListeners; ctrListeners++) {
			IDpDeepExportCompleteListener listener = (IDpDeepExportCompleteListener) m_listeners
					.get(ctrListeners);
			DpDeepExportCompleteEvent event = new DpDeepExportCompleteEvent(
					getLocalFilesystemPath());
			listener.deepExportComplete(event);
		}
	}

	/*
	 * @return
	 * 
	 * @see com.documentum.fc.client.IDfService#getVendorString()
	 */
	public String getVendorString() {
		return m_strVendorDescription;
	}

	/*
	 * @return
	 * 
	 * @see com.documentum.fc.client.IDfService#getVersion()
	 */
	public String getVersion() {
		return m_strVersion;
	}

	public void setSM(IDfSessionManager sm) {
		super.setSessionManager(sm);
	}

	/*
	 * Performs an exact version string match
	 * 
	 * @param version @return
	 * 
	 * @see com.documentum.fc.client.IDfService#isCompatible(java.lang.String)
	 */
	public boolean isCompatible(String version) {
		if (version.equals(getVersion())) {
			return true;
		}
		return false;
	}

	public void run() {
		// TODO still to be implemented. Design needs a rethink. This is not a
		// good
		// one.
	}

}
