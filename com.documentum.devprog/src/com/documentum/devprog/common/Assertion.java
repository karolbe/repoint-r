/*     */ package com.documentum.devprog.common;
/*     */ 
/*     */ /** @deprecated */
/*     */ public class Assertion
/*     */   implements IAssertion
/*     */ {
/* 118 */   private static boolean m_blAssertionsEnabled = false;
/*     */ 
/* 124 */   int m_bytInformLevel = 2;
/*     */ 
/*     */   public Assertion()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Assertion(byte informLevel)
/*     */   {
/*  39 */     this.m_bytInformLevel = informLevel;
/*     */   }
/*     */ 
/*     */   public void jdMethod_assert(boolean condition, String failureMessage)
/*     */   {
/*  51 */     if (isAssertionsEnabled())
/*     */     {
/*  54 */       if (!condition)
/*     */       {
/*  56 */         String assertionFailed = null;
/*  57 */         if ((failureMessage == null) || (failureMessage.length() <= 0))
/*     */         {
/*  59 */           assertionFailed = "Assertion failed ";
/*     */         }
/*     */         else
/*     */         {
/*  64 */           assertionFailed = "Assertion Failed: " + failureMessage;
/*  65 */           throw new AssertionError(assertionFailed);
/*     */         }
/*     */ 
/*  68 */         if (this.m_bytInformLevel == 1)
/*     */         {
/*  70 */           throw new AssertionError(assertionFailed);
/*     */         }
/*  72 */         if (this.m_bytInformLevel == 2)
/*     */         {
/*  74 */           throw new AssertionFailureException(assertionFailed);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setInformLevel(int level)
/*     */   {
/*  85 */     this.m_bytInformLevel = level;
/*     */   }
/*     */ 
/*     */   private static boolean isAssertionsEnabled()
/*     */   {
/*  96 */     return m_blAssertionsEnabled;
/*     */   }
/*     */ 
/*     */   public static void setAssertionsEnabled(boolean enableAssertion)
/*     */   {
/* 111 */     m_blAssertionsEnabled = enableAssertion;
/*     */   }
/*     */ }

/* Location:           devprogClasses.jar
 * Qualified Name:     com.documentum.devprog.common.Assertion
 * JD-Core Version:    0.6.2
 */