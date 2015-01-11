/*     */ package com.documentum.devprog.common;
/*     */ 
/*     */ import com.documentum.fc.client.DfClient;
/*     */ import com.documentum.fc.client.IDfClient;
/*     */ import com.documentum.fc.client.IDfSession;
/*     */ import com.documentum.fc.client.IDfSessionManager;
/*     */ import com.documentum.fc.common.DfException;
/*     */ import com.documentum.fc.common.DfLoginInfo;
/*     */ import com.documentum.fc.common.IDfLoginInfo;
/*     */ 
/*     */ public class LoginManager
/*     */ {
/* 146 */   private String m_strUsername = null;
/* 147 */   private String m_strPassword = null;
/* 148 */   private String m_strDocbase = null;
/*     */ 
/* 150 */   private IDfSessionManager m_sessMgr = null;
/*     */ 
/* 152 */   private IDfSession m_session = null;
/*     */ 
/* 154 */   private IDfClient m_localClient = null;
/*     */ 
/*     */   public LoginManager(String username, String password, String docbase)
/*     */   {
/*  39 */     this.m_strUsername = username;
/*  40 */     this.m_strPassword = password;
/*  41 */     this.m_strDocbase = docbase;
/*     */ 
/*  43 */     init();
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/*     */     try
/*     */     {
/*  51 */       this.m_localClient = DfClient.getLocalClient();
/*     */ 
/*  55 */       IDfLoginInfo loginInfo = new DfLoginInfo();
/*  56 */       loginInfo.setUser(getUsername());
/*  57 */       loginInfo.setPassword(getPassword());
/*     */ 
/*  59 */       this.m_sessMgr = this.m_localClient.newSessionManager();
/*  60 */       this.m_sessMgr.setIdentity(getDocbase(), loginInfo);
/*     */     }
/*     */     catch (DfException dfe)
/*     */     {
/*  66 */       dfe.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setUsername(String username)
/*     */   {
/*  73 */     this.m_strUsername = username;
/*     */   }
/*     */ 
/*     */   public String getUsername()
/*     */   {
/*  78 */     return this.m_strUsername;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/*  83 */     this.m_strPassword = password;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/*  89 */     return this.m_strPassword;
/*     */   }
/*     */ 
/*     */   public void setDocbase(String docbase)
/*     */   {
/*  99 */     this.m_strDocbase = docbase;
/*     */   }
/*     */ 
/*     */   public String getDocbase()
/*     */   {
/* 109 */     return this.m_strDocbase;
/*     */   }
/*     */ 
/*     */   public IDfSession getSession()
/*     */   {
/*     */     try
/*     */     {
/* 117 */       this.m_session = this.m_sessMgr.getSession(getDocbase());
/* 118 */       return this.m_session;
/*     */     }
/*     */     catch (DfException dfe)
/*     */     {
/* 122 */       dfe.printStackTrace();
/*     */     }
/* 124 */     return null;
/*     */   }
/*     */ 
/*     */   public void releaseSession()
/*     */   {
/* 130 */     if (this.m_session != null)
/*     */     {
/* 132 */       this.m_sessMgr.release(this.m_session);
/*     */     }
/*     */   }
/*     */ 
/*     */   public IDfSessionManager getSessionManager()
/*     */   {
/* 138 */     return this.m_sessMgr;
/*     */   }
/*     */ 
/*     */   public IDfClient getLocalClient()
/*     */   {
/* 143 */     return this.m_localClient;
/*     */   }
/*     */ }

/* Location:           devprogClasses.jar
 * Qualified Name:     com.documentum.devprog.common.LoginManager
 * JD-Core Version:    0.6.2
 */