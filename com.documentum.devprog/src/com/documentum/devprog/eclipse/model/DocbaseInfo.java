package com.documentum.devprog.eclipse.model;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import java.io.Serializable;

/**
 * Created by kbryd on 2/23/16.
 */

public class DocbaseInfo implements Serializable {
    private IDfSessionManager sessionManager;
    private String userName;
    private String userPassword;
    private String docbaseName;
    private String docbaseId = "";

    transient private IDfClient dfClient;

    private String docbrokerHost;
    private boolean savePassword;

    public DocbaseInfo(IDfSessionManager sessionManager, IDfClient dfClient, String docbase, String docbrokerHost, String userName, String password, boolean savePassword) {
        this.sessionManager = sessionManager;
        this.dfClient = dfClient;
        this.docbrokerHost = docbrokerHost;
        this.userName = userName;
        this.userPassword = password;
        this.savePassword = savePassword;
        this.docbaseName = docbase;
        try {
            this.docbaseId = Integer.toHexString(Integer.parseInt(sessionManager.getSession(docbase).getDocbaseId()));	//this is not correct. leading zeros are missing
            //sessionManager.getSession(docbase).getDocbaseConfig().getObjectId()
        } catch (DfException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }
    
    public IDfClient getClient()
    {
    	return  dfClient;
    }

    public IDfSessionManager getSessionManager() {
        return sessionManager;
    }

    public String getDocbrokerHost() {
        return docbrokerHost;
    }

    public String getDocbaseId() {
        return docbaseId;
    }

    public String getDocbaseName() {
        return docbaseName;
    }

    public void prepareForSave() {
        if(!savePassword) {
            userPassword = null;
        }
    }

    public void restore() {
        try {
            System.out.println("Restoring " + docbrokerHost + " " +userName + " " + userPassword + " " + docbaseName);
            dfClient = DfClient.getLocalClientEx();
            IDfTypedObject config = dfClient.getClientConfig();
            config.setString("primary_host", docbrokerHost);
            config.setInt("primary_port", 1489);

            IDfLoginInfo li = new DfLoginInfo();
            li.setUser(userName);
            li.setPassword(userPassword);
            li.setDomain(null);

            sessionManager = dfClient.newSessionManager();
            sessionManager .clearIdentity(docbaseName);
            sessionManager .setIdentity(docbaseName, li);
        } catch(DfException dfe) {
            dfe.printStackTrace();
        }
    }
}