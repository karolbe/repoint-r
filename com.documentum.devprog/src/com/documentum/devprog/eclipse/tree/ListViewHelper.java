/*
 * Created on Apr 12, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.DfException;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfTypedObject;

import com.documentum.devprog.eclipse.DevprogPlugin;
import com.documentum.devprog.eclipse.common.PluginHelper;
import com.documentum.devprog.eclipse.common.PluginState;
import com.documentum.devprog.eclipse.common.PreferenceConstants;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import com.documentum.com.IDfClientX;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class ListViewHelper {

	private static IDfCollection folderColl = null;

	private static IDfCollection docColl = null;

	// Not used anymore. These are in PluginPreferences now.
	// private static String[] columns =
	// {"object_name","r_modify_date","r_full_content_size","a_content_type","r_modifier"};

	public static void showSingleViewFolderContentsRegular(String objId) {
		try {

			final String folderId = objId;

			IRunnableWithProgress rwp = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					IDfSession sess = null;
					try {
						StringBuffer bufQuery = new StringBuffer(32);
						bufQuery.append("select ")
								.append(DocbaseItem.getDefaultQueryAttributes())
								.append(" from dm_folder where FOLDER(ID('");
						bufQuery.append(folderId).append("'))");

						String strQuery = bufQuery.toString();
						// System.out.println("Folder query: " + strQuery);
						IDfClientX cx = PluginState.getClientX();
						IDfQuery query = cx.getQuery();
						query.setDQL(strQuery);
						sess = PluginState.getSessionById(folderId);
						folderColl = query
								.execute(sess, IDfQuery.DF_READ_QUERY);

						bufQuery = new StringBuffer(32);
						bufQuery.append("select ")
								.append(DocbaseItem.getDefaultQueryAttributes())
								.append(" from dm_document where FOLDER(ID('");
						bufQuery.append(folderId).append("'))");
						strQuery = bufQuery.toString();
						// System.out.println("Doc Query: " + strQuery);
						query = cx.getQuery();
						query.setDQL(strQuery);
						docColl = query.execute(sess, IDfQuery.DF_READ_QUERY);

					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						PluginState.releaseSession(sess);
					}
				}

			};

			IProgressService progServ = PlatformUI.getWorkbench()
					.getProgressService();
			progServ.busyCursorWhile(rwp);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void showSingleViewFolderContentsAll(String objId) {
		try {

			final String folderId = objId;

			IRunnableWithProgress rwp = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					IDfSession sess = null;
					try {
						StringBuffer bufQuery = new StringBuffer(32);
						bufQuery.append("select ")
								.append(DocbaseItem.getDefaultQueryAttributes())
								.append(" from dm_sysobject where FOLDER(ID('");
						bufQuery.append(folderId).append("'))");

						String strQuery = bufQuery.toString();
						// System.out.println("Folder query: " + strQuery);
						IDfClientX cx = PluginState.getClientX();
						IDfQuery query = cx.getQuery();
						query.setDQL(strQuery);
						sess = PluginState.getSessionById(folderId);
						folderColl = query
								.execute(sess, IDfQuery.DF_READ_QUERY);

						System.out.println("Created collection: "
								+ folderColl.getAttrCount());
						/*
						 * bufQuery = new StringBuffer(32);
						 * bufQuery.append("select
						 * ").append(DocbaseDataItem.getDefaultQueryAttributes()).append("
						 * from dm_document where FOLDER(ID('");
						 * bufQuery.append(folderId).append("'))"); strQuery =
						 * bufQuery.toString();
						 * //System.out.println("Doc Query: " + strQuery); query
						 * = cx.getQuery(); query.setDQL(strQuery); docColl =
						 * query.execute(sess, IDfQuery.DF_READ_QUERY);
						 */

					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						PluginState.releaseSession(sess);
					}
				}

			};

			IProgressService progServ = PlatformUI.getWorkbench()
					.getProgressService();
			progServ.busyCursorWhile(rwp);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static IDfCollection getFolderCollection() {
		return folderColl;
	}

	public static IDfCollection getDocumentCollection() {
		return docColl;
	}

	public static void resetCollections() {

		try {
			if (folderColl != null)
				folderColl.close();
		} catch (DfException dfe1) {
		}
		try {
			if (docColl != null)
				docColl.close();
		} catch (DfException dfe2) {
		}
		folderColl = null;
		docColl = null;

	}

	// Commenting out this method. Now the columns are obtained using Plugin
	// Preferences
	/**
	 * Sets the list of columns for the detailed list view. The array should
	 * contain the docbase object attribute names
	 * 
	 * @param cols
	 * 
	 *            public static void setColumns(String[] cols) { columns = cols;
	 *            }
	 */

	/**
	 * Gets the current list of columns for the detailed list view. The array
	 * contains the docbase object attributed names
	 * 
	 * @return
	 */
	public static String[] getColumns() {
		String col = DevprogPlugin.getDefault().getPluginPreferences()
				.getString(PreferenceConstants.P_VISIBLE_COLUMNS);
		return col.split(",");
	}

	/**
	 * Forms a full-text document search query with the given search terms
	 * 
	 * @param searchString
	 * @return
	 */
	public static String formFullTextQuery(String searchString) {
		StringBuffer bufQuery = new StringBuffer(32);
		bufQuery.append("select ")
				.append(DocbaseItem.getDefaultQueryAttributes())
				.append(",SCORE,SUMMARY ");
		bufQuery.append(" from dm_document SEARCH DOCUMENT CONTAINS '")
				.append(searchString).append("' ");
		// bufQuery.append(" WHERE
		// FOLDER('").append(PluginState.getHomeFolderPath()).append("',DESCEND)
		// ");
		bufQuery.append(" ORDER BY SCORE ENABLE (FTDQL) ");

		String strQuery = bufQuery.toString();
		return strQuery;
	}

	public static String formFolderSearchQuery(String searchString) {
		StringBuffer bufQuery = new StringBuffer(32);
		bufQuery.append("SELECT ")
				.append(DocbaseItem.getDefaultQueryAttributes())
				.append(" from dm_folder ");
		// bufQuery.append(" WHERE
		// FOLDER('").append(PluginState.getHomeFolderPath()).append("',
		// DESCEND)");
		bufQuery.append(" AND  (");

		String[] tokens = searchString.toUpperCase().split(" ");

		for (int i = 0; i < tokens.length; i++) {
			if (i > 0) {
				bufQuery.append(" OR ");
			}
			bufQuery.append(" UPPER(object_name) LIKE '%").append(tokens[i])
					.append("%' ");
			bufQuery.append(" OR ");
			bufQuery.append(" UPPER(title) LIKE '%").append(tokens[i])
					.append("%'");
		}

		bufQuery.append(")");

		String strQuery = bufQuery.toString();
		System.out.println("query=" + strQuery);
		return strQuery;
	}

	/**
	 * Forms a DQL query based on search terms provided to search for common
	 * attributes.
	 * 
	 * @param searchString
	 * @return
	 */
	public static String formQueryString(String searchString, String fldrId) {
		StringBuffer bufQuery = new StringBuffer(32);
		bufQuery.append("SELECT ")
				.append(DocbaseItem.getDefaultQueryAttributes())
				.append(" from dm_sysobject ");
		bufQuery.append(" WHERE ");

		if (fldrId != null) {
			bufQuery.append(" FOLDER(ID('").append(fldrId)
					.append("'),DESCEND) AND  ");
		}

		/*
		 * if (fldrId != null) {
		 * bufQuery.append(" FOLDER(ID('").append(fldrId).append( "')) AND  ");
		 * }
		 */

		bufQuery.append(" ( ");

		String[] tokens = searchString.toUpperCase().split(" ");
		for (int i = 0; i < tokens.length; i++) {
			if (i > 0) {
				bufQuery.append(" OR ");
			}
			String tok = tokens[i].trim();
			bufQuery.append(" UPPER(object_name) LIKE '%").append(tok)
					.append("%' ");
			bufQuery.append(" OR ");
			bufQuery.append(" UPPER(title) LIKE '%").append(tok).append("%'");
		}

		bufQuery.append(")");
		bufQuery.append(" ORDER BY 1");
		String strQuery = bufQuery.toString();
		System.out.println("query=" + strQuery);
		return strQuery;
	}// performSearch

}
