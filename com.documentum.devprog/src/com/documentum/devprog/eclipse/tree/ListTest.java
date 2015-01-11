/*
 * Created on Apr 15, 2007
 *
 * EMC Documentum Developer Program 2006
 */
package com.documentum.devprog.eclipse.tree;

import com.documentum.fc.common.*;

import com.documentum.com.*;
import com.documentum.fc.client.*;

/**
 * 
 * 
 * 
 * @author Aashish Patil(patil_aashish@emc.com)
 */
public class ListTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			// testFullTextQuery();
			testFolderSearchQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void testFolderSearchQuery() throws Exception {
		IDfClientX cx = new DfClientX();

		IDfClient client = cx.getLocalClient();

		// setup session info
		String repoName = "aashishRepo";
		IDfSessionManager sm = client.newSessionManager();
		IDfLoginInfo li = cx.getLoginInfo();
		li.setUser("patila");
		sm.setIdentity(repoName, li);

		IDfSession sess = null;
		IDfCollection coll = null;
		// String strQuery = "SELECT object_name,SUMMARY,SCORE,TEXT FROM
		// dm_document SEARCH DOCUMENT CONTAINS 'test' ORDER BY SCORE
		// enable(FTDQL)";
		String strQuery = ListViewHelper.formFolderSearchQuery("query SuB");
		try {
			IDfQuery query = cx.getQuery();
			query.setDQL(strQuery);

			sess = sm.getSession(repoName);
			coll = query.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				IDfTypedObject tObj = coll.getTypedObject();
				/*
				 * int attrCnt = tObj.getAttrCount();
				 * System.out.println("attrCnt: " + attrCnt); for(int
				 * i=0;i<attrCnt;i++) { String attrName =
				 * tObj.getAttr(i).getName(); System.out.println(attrName);
				 * //System.out.println(tObj.getString(attrName)); }
				 */
				System.out.println(tObj.getString("object_name"));
				// System.out.println(tObj.getString("SUM"));
			}
		} finally {
			if (coll != null) {
				coll.close();
			}
			if (sess != null) {
				sm.release(sess);
			}

		}

	}

	private static void testFullTextQuery() throws Exception {
		IDfClientX cx = new DfClientX();

		IDfClient client = cx.getLocalClient();

		// setup session info
		String repoName = "aashishRepo";
		IDfSessionManager sm = client.newSessionManager();
		IDfLoginInfo li = cx.getLoginInfo();
		li.setUser("patila");
		sm.setIdentity(repoName, li);

		IDfSession sess = null;
		IDfCollection coll = null;
		// String strQuery = "SELECT object_name,SUMMARY,SCORE,TEXT FROM
		// dm_document SEARCH DOCUMENT CONTAINS 'test' ORDER BY SCORE
		// enable(FTDQL)";
		String strQuery = ListViewHelper.formFullTextQuery("WoRlD");
		try {
			IDfQuery query = cx.getQuery();
			query.setDQL(strQuery);

			sess = sm.getSession(repoName);
			coll = query.execute(sess, IDfQuery.DF_READ_QUERY);
			while (coll.next()) {
				IDfTypedObject tObj = coll.getTypedObject();
				/*
				 * int attrCnt = tObj.getAttrCount();
				 * System.out.println("attrCnt: " + attrCnt); for(int
				 * i=0;i<attrCnt;i++) { String attrName =
				 * tObj.getAttr(i).getName(); System.out.println(attrName);
				 * //System.out.println(tObj.getString(attrName)); }
				 */
				System.out.println(tObj.getString("object_name") + ","
						+ tObj.getString("r_full_score"));
				// System.out.println(tObj.getString("SUM"));
			}
		} finally {
			if (coll != null) {
				coll.close();
			}
			if (sess != null) {
				sm.release(sess);
			}

		}

	}

}
