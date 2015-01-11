/*    */ package com.documentum.devprog.common;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.lang.reflect.Method;
/*    */ import java.net.URL;
/*    */ import java.net.URLClassLoader;
/*    */ 
/*    */ public class ClasspathAugmenter
/*    */ {
/* 26 */   private static final Class[] parameters = { URL.class };
/*    */ 
/*    */   public static void addFile(String s)
/*    */     throws IOException
/*    */   {
/* 35 */     File f = new File(s);
/* 36 */     addFile(f);
/*    */   }
/*    */ 
/*    */   public static void addFile(File f) throws IOException
/*    */   {
/* 41 */     addURL(f.toURL());
/*    */   }
/*    */ 
/*    */   public static void addURL(URL u) throws IOException
/*    */   {
/* 46 */     URLClassLoader sysloader = 
/* 47 */       (URLClassLoader)ClassLoader.getSystemClassLoader();
/* 48 */     Class sysclass = URLClassLoader.class;
/*    */     try
/*    */     {
/* 51 */       Method method = sysclass.getDeclaredMethod("addURL", parameters);
/* 52 */       method.setAccessible(true);
/* 53 */       System.out.println("Augmenting classpath by " + u.toString());
/* 54 */       method.invoke(sysloader, new Object[] { u });
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 58 */       t.printStackTrace();
/* 59 */       throw new IOException("Error, could not add URL to system classloader");
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void addJarsInFolderRecursively(File folder) throws IOException
/*    */   {
/* 65 */     if (folder.isDirectory())
/*    */     {
/* 67 */       File[] fldrCont = folder.listFiles();
/* 68 */       for (int i = 0; i < fldrCont.length; i++)
/*    */       {
/* 70 */         File cur = fldrCont[i];
/* 71 */         if (cur.isFile())
/*    */         {
/* 73 */           String name = cur.getName();
/* 74 */           if (name.endsWith(".jar"))
/*    */           {
/* 76 */             addFile(cur);
/*    */           }
/*    */         }
/* 79 */         else if (cur.isDirectory())
/*    */         {
/* 81 */           addJarsInFolderRecursively(cur);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void addClassFolder(File folder) throws IOException
/*    */   {
/* 89 */     if (folder.isDirectory())
/*    */     {
/* 91 */       addURL(folder.toURL());
/*    */     }
/*    */   }
/*    */ }

/* Location:           devprogClasses.jar
 * Qualified Name:     com.documentum.devprog.common.ClasspathAugmenter
 * JD-Core Version:    0.6.2
 */