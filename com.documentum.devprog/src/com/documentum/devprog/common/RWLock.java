/*    */ package com.documentum.devprog.common;
/*    */ 
/*    */ public class RWLock
/*    */ {
/* 35 */   private int givenLocks = 0;
/*    */ 
/* 40 */   private int waitingReaders = 0;
/*    */ 
/* 45 */   private int waitingWriters = 0;
/*    */ 
/* 50 */   private Object mutex = null;
/*    */ 
/*    */   public RWLock()
/*    */   {
/* 54 */     this.mutex = new Object();
/*    */   }
/*    */ 
/*    */   public void getReadLock() throws InterruptedException
/*    */   {
/* 59 */     synchronized (this.mutex)
/*    */     {
/* 61 */       while ((this.givenLocks == -1) || (this.waitingWriters > 0))
/*    */       {
/* 63 */         this.mutex.wait();
/*    */       }
/*    */ 
/* 66 */       this.givenLocks += 1;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void getWriteLock() throws InterruptedException
/*    */   {
/* 72 */     synchronized (this.mutex)
/*    */     {
/* 74 */       this.waitingWriters += 1;
/*    */ 
/* 76 */       while (this.givenLocks != 0)
/*    */       {
/* 78 */         this.mutex.wait();
/*    */       }
/*    */ 
/* 81 */       this.waitingWriters -= 1;
/* 82 */       this.givenLocks = -1;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void releaseLock()
/*    */   {
/* 88 */     synchronized (this.mutex)
/*    */     {
/* 90 */       if (this.givenLocks == 0)
/*    */       {
/* 92 */         return;
/*    */       }
/*    */ 
/* 95 */       if (this.givenLocks == -1)
/*    */       {
/* 97 */         this.givenLocks = 0;
/*    */       }
/*    */       else
/*    */       {
/* 101 */         this.givenLocks -= 1;
/*    */       }
/*    */ 
/* 104 */       this.mutex.notifyAll();
/*    */     }
/*    */   }
/*    */ }

/* Location:           devprogClasses.jar
 * Qualified Name:     com.documentum.devprog.common.RWLock
 * JD-Core Version:    0.6.2
 */