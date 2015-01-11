/*    */ package com.documentum.devprog.common;
/*    */ 
/*    */ import com.documentum.fc.client.DfService;
/*    */ import com.documentum.fc.client.IDfService;
/*    */ 
/*    */ public class DpBaseService extends DfService
/*    */   implements IDfService
/*    */ {
/*    */   public String getVersion()
/*    */   {
/* 26 */     return null;
/*    */   }
/*    */ 
/*    */   public String getVendorString()
/*    */   {
/* 35 */     return null;
/*    */   }
/*    */ 
/*    */   public boolean isCompatible(String version)
/*    */   {
/* 44 */     return false;
/*    */   }
/*    */ }

/* Location:           devprogClasses.jar
 * Qualified Name:     com.documentum.devprog.common.DpBaseService
 * JD-Core Version:    0.6.2
 */