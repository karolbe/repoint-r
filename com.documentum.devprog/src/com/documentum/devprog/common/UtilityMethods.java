/*      */ package com.documentum.devprog.common;
/*      */ 
/*      */ import com.documentum.fc.client.DfClient;
/*      */ import com.documentum.fc.client.DfQuery;
/*      */ import com.documentum.fc.client.IDfClient;
/*      */ import com.documentum.fc.client.IDfCollection;
/*      */ import com.documentum.fc.client.IDfDocument;
/*      */ import com.documentum.fc.client.IDfFolder;
/*      */ import com.documentum.fc.client.IDfFormat;
/*      */ import com.documentum.fc.client.IDfPersistentObject;
/*      */ import com.documentum.fc.client.IDfQuery;
/*      */ import com.documentum.fc.client.IDfSession;
/*      */ import com.documentum.fc.client.IDfSessionManager;
/*      */ import com.documentum.fc.client.IDfSysObject;
/*      */ import com.documentum.fc.client.IDfType;
/*      */ import com.documentum.fc.client.IDfTypedObject;
/*      */ import com.documentum.fc.common.DfException;
/*      */ import com.documentum.fc.common.DfId;
/*      */ import com.documentum.fc.common.DfLogger;
/*      */ import com.documentum.fc.common.DfLoginInfo;
/*      */ import com.documentum.fc.common.IDfId;
/*      */ import com.documentum.fc.common.IDfList;
/*      */ import com.documentum.fc.common.IDfLoginInfo;
/*      */ import com.documentum.operations.IDfOperationError;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.util.Hashtable;
/*      */ import java.util.LinkedList;
/*      */ import java.util.StringTokenizer;
/*      */ 
/*      */ public class UtilityMethods
/*      */ {
/*   27 */   private static String logId = "com.documentum.devprog.common.UtilityMethods";
/*      */ 
/*      */   public static IDfId importDocument(IDfSession session, String localFilepath, String contentType, String docbaseFolderPath)
/*      */     throws DfException
/*      */   {
/*   49 */     String fileSeperator = System.getProperty("file.separator");
/*   50 */     int indexLastFileSep = localFilepath.lastIndexOf(fileSeperator);
/*   51 */     String filename = localFilepath.substring(indexLastFileSep + 1);
/*      */ 
/*   53 */     IDfDocument dfDoc = (IDfDocument)session.newObject("dm_document");
/*   54 */     dfDoc.setTitle(filename);
/*   55 */     dfDoc.setObjectName(filename);
/*   56 */     dfDoc.setContentType(contentType);
/*   57 */     dfDoc.setFile(localFilepath);
/*   58 */     dfDoc.link(docbaseFolderPath);
/*   59 */     dfDoc.save();
/*   60 */     return dfDoc.getObjectId();
/*      */   }
/*      */ 
/*      */   public static void printOperationErrors(OutputStream out, IDfList errorList)
/*      */   {
/*      */     try
/*      */     {
/*   77 */       String beginMessage = "\n--Printing Operation Error List--\n";
/*   78 */       out.write(beginMessage.getBytes());
/*   79 */       for (int errorIndex = 0; 
/*   80 */         errorIndex < errorList.getCount(); 
/*   81 */         errorIndex++)
/*      */       {
/*   83 */         IDfOperationError error = 
/*   84 */           (IDfOperationError)errorList.get(errorIndex);
/*      */ 
/*   86 */         String strErrMsg = error.getMessage();
/*   87 */         out.write(strErrMsg.getBytes());
/*      */       }
/*   89 */       String endMessage = "\n--End Printing Operation Error List--\n";
/*   90 */       out.write(endMessage.getBytes());
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/*   94 */       ex.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static boolean hasRendition(IDfSession session, String objectId, String renditionFormat)
/*      */     throws DfException
/*      */   {
/*  113 */     IDfCollection coll = null;
/*      */     try
/*      */     {
/*  116 */       IDfSysObject sysObject = 
/*  117 */         (IDfSysObject)session.getObject(new DfId(objectId));
/*      */ 
/*  119 */       coll = sysObject.getRenditions("r_object_id");
/*  120 */       while (coll.next())
/*      */       {
/*  122 */         String tmpId = coll.getString("r_object_id");
/*  123 */         IDfPersistentObject dfPers = 
/*  124 */           session.getObject(new DfId(tmpId));
/*      */ 
/*  126 */         String contentType = dfPers.apiGet("get", "full_format");
/*  127 */         if (contentType.equals(renditionFormat))
/*      */         {
/*  129 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  135 */       if (coll != null)
/*      */       {
/*  137 */         coll.close();
/*      */       }
/*      */     }
/*      */ 
/*  141 */     return false;
/*      */   }
/*      */ 
/*      */   public static void createObjectType(IDfSession session, String typeName, String supertype, Hashtable attributes)
/*      */     throws DfException
/*      */   {
/*  161 */     DfLogger.debug(
/*  162 */       "com.documentum.devprog", 
/*  163 */       "About to create an object of type: " + typeName, 
/*  164 */       null, 
/*  165 */       null);
/*      */ 
/*  167 */     IDfQuery dqlQuery = new DfQuery();
/*  168 */     String strQuery = 
/*  169 */       "create type " + typeName + " with supertype " + supertype + " ";
/*  170 */     dqlQuery.setDQL(strQuery);
/*  171 */     DfLogger.debug(
/*  172 */       "com.documentum.devprog", 
/*  173 */       "About to execute query: " + strQuery, 
/*  174 */       null, 
/*  175 */       null);
/*  176 */     dqlQuery.execute(session, 3);
/*  177 */     System.out.println("Object Type " + typeName + " Created");
/*      */   }
/*      */ 
/*      */   public static boolean hasSupertype(IDfSession session, String docbaseType, String superType)
/*      */     throws DfException
/*      */   {
/*  205 */     IDfType currentType = session.getType(docbaseType);
/*      */ 
/*  207 */     if (docbaseType.equals(superType))
/*      */     {
/*  209 */       return true;
/*      */     }
/*      */ 
/*      */     while (true)
/*      */     {
/*  214 */       IDfType currentSuperType = currentType.getSuperType();
/*  215 */       if (currentSuperType == null)
/*      */       {
/*      */         break;
/*      */       }
/*  219 */       String strCurrentSuperType = currentSuperType.getName();
/*  220 */       if (strCurrentSuperType.equals(superType))
/*      */       {
/*  222 */         return true;
/*      */       }
/*  224 */       currentType = currentSuperType;
/*      */     }
/*      */ 
/*  227 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean isStringEmpty(String strToCheck)
/*      */   {
/*  244 */     if ((strToCheck == null) || (strToCheck.trim().length() == 0))
/*      */     {
/*  246 */       return true;
/*      */     }
/*      */ 
/*  249 */     return false;
/*      */   }
/*      */ 
/*      */   public static IDfId getIdFromDocumentName(IDfSession sess, String objName)
/*      */     throws DfException
/*      */   {
/*  264 */     StringBuffer bufQual = new StringBuffer(32);
/*  265 */     bufQual.append("dm_document where object_name like '").append(
/*  266 */       objName).append(
/*  267 */       "'");
/*  268 */     String strQual = bufQual.toString();
/*      */ 
/*  270 */     IDfId objId = sess.getIdByQualification(strQual);
/*  271 */     return objId;
/*      */   }
/*      */ 
/*      */   public static IDfSessionManager getSessionManager51(IDfSession session)
/*      */     throws DfException
/*      */   {
/*  285 */     IDfClient client = DfClient.getLocalClient();
/*  286 */     IDfSessionManager sessMgr = client.newSessionManager();
/*      */ 
/*  288 */     IDfLoginInfo loginInfo = new DfLoginInfo();
/*  289 */     loginInfo.setUser(session.getLoginInfo().getUser());
/*  290 */     loginInfo.setDomain(session.getLoginInfo().getDomain());
/*  291 */     loginInfo.setPassword(session.getLoginTicket());
/*      */ 
/*  293 */     sessMgr.setIdentity(session.getDocbaseName(), loginInfo);
/*      */ 
/*  295 */     return sessMgr;
/*      */   }
/*      */ 
/*      */   public static String[] getAllSubtypes(IDfSession sess, String superType)
/*      */     throws DfException
/*      */   {
/*  311 */     IDfCollection coll = null;
/*      */     try
/*      */     {
/*  315 */       StringBuffer bufQuery = new StringBuffer(32);
/*  316 */       bufQuery
/*  317 */         .append("select r_type_name from dmi_type_info where ANY r_supertype IN ('")
/*  318 */         .append(superType)
/*  319 */         .append("')");
/*      */ 
/*  321 */       IDfQuery query = new DfQuery();
/*  322 */       query.setDQL(bufQuery.toString());
/*      */ 
/*  324 */       LinkedList subTypeList = new LinkedList();
/*      */ 
/*  326 */       coll = query.execute(sess, 0);
/*  327 */       while (coll.next())
/*      */       {
/*  329 */         String strType = coll.getString("r_type_name");
/*  330 */         subTypeList.add(strType);
/*      */       }
/*      */ 
/*  333 */       String[] allSubTypes = new String[subTypeList.size()];
/*  334 */       subTypeList.toArray(allSubTypes);
/*  335 */       return allSubTypes;
/*      */     }
/*      */     finally
/*      */     {
/*  339 */       if (coll != null)
/*      */       {
/*  341 */         coll.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String[] getAllRenditionFormatNames(IDfSession sess, IDfId docId)
/*      */     throws DfException
/*      */   {
/*  359 */     IDfDocument doc = (IDfDocument)sess.getObject(docId);
/*  360 */     IDfCollection coll = null;
/*      */     try
/*      */     {
/*  363 */       coll = doc.getRenditions("full_format");
/*  364 */       LinkedList lstFmts = new LinkedList();
/*  365 */       while (coll.next())
/*      */       {
/*  367 */         String fmtName = coll.getString("full_format");
/*  368 */         lstFmts.add(fmtName);
/*      */       }
/*  370 */       String[] arrFmts = new String[lstFmts.size()];
/*  371 */       lstFmts.toArray(arrFmts);
/*  372 */       return arrFmts;
/*      */     }
/*      */     finally
/*      */     {
/*  376 */       if (coll != null)
/*      */       {
/*  378 */         coll.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String[] getPaths(IDfSysObject object, boolean addDocName)
/*      */     throws DfException
/*      */   {
/*  400 */     LinkedList result = new LinkedList();
/*  401 */     if ((object instanceof IDfFolder))
/*      */     {
/*  403 */       IDfFolder folder = (IDfFolder)object;
/*  404 */       int pathCount = folder.getFolderPathCount();
/*  405 */       for (int i = 0; i < pathCount; i++)
/*  406 */         result.add(folder.getFolderPath(i));
/*      */     }
/*      */     else
/*      */     {
/*  410 */       String objectName = object.getObjectName();
/*  411 */       IDfSession session = object.getSession();
/*  412 */       int folderCount = object.getFolderIdCount();
/*  413 */       for (int i = 0; i < folderCount; i++)
/*      */       {
/*  415 */         IDfFolder folder = 
/*  416 */           (IDfFolder)session.getObject(object.getFolderId(i));
/*  417 */         String[] folderPaths = getPaths(folder, addDocName);
/*  418 */         int j = 0; for (int limit = folderPaths.length; j < limit; j++)
/*      */         {
/*  420 */           String folderPath = folderPaths[j];
/*  421 */           if (addDocName)
/*      */           {
/*  423 */             result.add(folderPath + "/" + objectName);
/*      */           }
/*      */           else
/*      */           {
/*  427 */             result.add(folderPath);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  434 */     String[] arrRes = new String[result.size()];
/*  435 */     result.toArray(arrRes);
/*  436 */     return arrRes;
/*      */   }
/*      */ 
/*      */   public static boolean isAssembly(IDfTypedObject sObj)
/*      */     throws DfException
/*      */   {
/*  448 */     String strAssembledFromId = sObj.getString("r_assembled_from_id");
/*  449 */     IDfId id = new DfId(strAssembledFromId);
/*  450 */     if ((id.isNull()) || (!id.isObjectId()))
/*      */     {
/*  452 */       return false;
/*      */     }
/*      */ 
/*  455 */     return true;
/*      */   }
/*      */ 
/*      */   public static String getURL(String sessId, String objId, String basePath, String linkbase, String format)
/*      */     throws DfException
/*      */   {
/*  490 */     StringBuffer dql = new StringBuffer(600);
/*  491 */     dql.append(
/*  492 */       "select f.r_folder_path, s1.a_content_type, s1.object_name from dm_folder_r f, dm_sysobject_s s1, dm_sysobject_r s2 where s1.r_object_id = s2.r_object_id and f.r_object_id = s2.i_folder_id and s1.r_object_id ='");
/*  493 */     dql.append(objId);
/*  494 */     dql.append("' and f.r_folder_path like '");
/*  495 */     dql.append(basePath);
/*  496 */     dql.append("%'");
/*      */ 
/*  498 */     IDfQuery query = new DfQuery();
/*  499 */     query.setDQL(dql.toString());
/*      */ 
/*  502 */     IDfSession session = DfClient.getLocalClient().findSession(sessId);
/*  503 */     IDfCollection coll = query.execute(session, 0);
/*  504 */     String folderPath = null;
/*  505 */     String contentType = null;
/*  506 */     String objName = null;
/*      */     try
/*      */     {
/*  510 */       while (coll.next())
/*      */       {
/*  512 */         folderPath = coll.getString("r_folder_path");
/*  513 */         contentType = coll.getString("a_content_type");
/*  514 */         objName = coll.getString("object_name");
/*      */ 
/*  516 */         if ((folderPath != null) && (folderPath.length() != 0))
/*      */         {
/*  518 */           StringBuffer bufDefaultFile = new StringBuffer(32);
/*  519 */           if (linkbase != null)
/*      */           {
/*  521 */             bufDefaultFile.append(linkbase);
/*      */           }
/*  523 */           bufDefaultFile.append(folderPath).append("/").append(objName);
/*      */ 
/*  525 */           String extension = "";
/*  526 */           if ((format != null) && (format.length() > 0))
/*      */           {
/*  528 */             IDfFormat fmt = session.getFormat(format);
/*  529 */             if (fmt != null)
/*      */             {
/*  531 */               extension = fmt.getDOSExtension();
/*      */             }
/*      */ 
/*      */           }
/*  536 */           else if (objName.lastIndexOf('.') == -1)
/*      */           {
/*  538 */             IDfFormat fmt = session.getFormat(contentType);
/*  539 */             if (fmt != null)
/*      */             {
/*  541 */               extension = fmt.getDOSExtension();
/*      */             }
/*      */           }
/*      */ 
/*  545 */           if (extension.length() > 0)
/*      */           {
/*  547 */             bufDefaultFile.append(".");
/*  548 */             bufDefaultFile.append(extension);
/*      */           }
/*      */ 
/*  551 */           String strDefaultFile = bufDefaultFile.toString();
/*  552 */           return strDefaultFile;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  559 */       if (coll != null)
/*  560 */         coll.close();
/*  561 */       query = null;
/*      */     }
/*      */ 
/*  563 */     return "";
/*      */   }
/*      */ 
/*      */   public static byte[] generateChecksum(IDfSession sess, IDfId objId, String format, String algorithm)
/*      */     throws DfException
/*      */   {
/*      */     try
/*      */     {
/*  586 */       if ((algorithm == null) || (algorithm.length() == 0))
/*      */       {
/*  588 */         algorithm = "MD5";
/*      */       }
/*      */ 
/*  591 */       IDfSysObject sysObj = (IDfSysObject)sess.getObject(objId);
/*  592 */       ByteArrayInputStream bin = null;
/*  593 */       if ((format == null) || (format.length() == 0))
/*      */       {
/*  595 */         bin = sysObj.getContent();
/*      */       }
/*      */       else
/*      */       {
/*  599 */         bin = sysObj.getContentEx(format, 0);
/*      */       }
/*      */ 
/*  602 */       byte[] buf = new byte[4096];
/*      */ 
/*  604 */       MessageDigest digest = MessageDigest.getInstance(algorithm);
/*  605 */       int numRead = 0;
/*      */ 
/*  607 */       while ((numRead = bin.read(buf)) != -1)
/*      */       {
/*  609 */         digest.update(buf, 0, numRead);
/*      */       }
/*      */ 
/*  612 */       return digest.digest();
/*      */     }
/*      */     catch (IOException ioe)
/*      */     {
/*  617 */       DfLogger.error(
/*  618 */         "UtilityMethods#generateChecksum()", 
/*  619 */         "IO Exception while reading content from docbase", 
/*  620 */         null, 
/*  621 */         ioe);
/*      */     }
/*      */     catch (NoSuchAlgorithmException nse)
/*      */     {
/*  625 */       DfLogger.error(
/*  626 */         "UtilityMethods#generateChecksum()", 
/*  627 */         "Unable to locate the specified algorithm for generating a hash: " + 
/*  628 */         algorithm, 
/*  629 */         null, 
/*  630 */         nse);
/*      */     }
/*      */ 
/*  633 */     return null;
/*      */   }
/*      */ 
/*      */   public static String generateChecksumOfStream(InputStream is, String algorithm)
/*      */   {
/*      */     try
/*      */     {
/*  652 */       if (is == null)
/*      */       {
/*  654 */         return null;
/*      */       }
/*      */ 
/*  657 */       if ((algorithm == null) || (algorithm.length() == 0))
/*      */       {
/*  659 */         algorithm = "MD5";
/*      */       }
/*      */ 
/*  662 */       byte[] buf = new byte[4096];
/*      */ 
/*  664 */       MessageDigest digest = MessageDigest.getInstance(algorithm);
/*  665 */       int numRead = 0;
/*      */ 
/*  667 */       while ((numRead = is.read(buf)) != -1)
/*      */       {
/*  670 */         digest.update(buf, 0, numRead);
/*      */       }
/*      */ 
/*  673 */       byte[] hashValue = digest.digest();
/*      */ 
/*  675 */       return new String(hashValue);
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/*  680 */       ex.printStackTrace();
/*      */     }
/*  682 */     return null;
/*      */   }
/*      */ 
/*      */   public static byte[] generateChecksumOfFile(String filename, String algorithm)
/*      */   {
/*      */     try
/*      */     {
/*  699 */       FileInputStream is = new FileInputStream(filename);
/*      */ 
/*  701 */       if (is == null)
/*      */       {
/*  703 */         return null;
/*      */       }
/*      */ 
/*  706 */       if ((algorithm == null) || (algorithm.length() == 0))
/*      */       {
/*  708 */         algorithm = "MD5";
/*      */       }
/*      */ 
/*  711 */       byte[] buf = new byte[4096];
/*      */ 
/*  713 */       MessageDigest digest = MessageDigest.getInstance(algorithm);
/*  714 */       int numRead = 0;
/*  715 */       int cntr = 0;
/*  716 */       while ((numRead = is.read(buf)) != -1)
/*      */       {
/*  718 */         cntr++;
/*  719 */         digest.update(buf, 0, numRead);
/*      */       }
/*      */ 
/*  722 */       byte[] hashValue = digest.digest();
/*      */ 
/*  724 */       is.close();
/*  725 */       return hashValue;
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/*  729 */       DfLogger.warn(logId, "Error while generating checksum ", null, ex);
/*      */     }
/*  731 */     return null;
/*      */   }
/*      */ 
/*      */   public static String convertBytesToHex(byte[] val)
/*      */   {
/*  743 */     char[] hexChars = 
/*  744 */       { 
/*  745 */       '0', 
/*  746 */       '1', 
/*  747 */       '2', 
/*  748 */       '3', 
/*  749 */       '4', 
/*  750 */       '5', 
/*  751 */       '6', 
/*  752 */       '7', 
/*  753 */       '8', 
/*  754 */       '9', 
/*  755 */       'a', 
/*  756 */       'b', 
/*  757 */       'c', 
/*  758 */       'd', 
/*  759 */       'e', 
/*  760 */       'f' };
/*      */ 
/*  764 */     StringBuffer bufHexVal = new StringBuffer(val.length * 2);
/*  765 */     for (int i = 0; i < val.length; i++)
/*      */     {
/*  767 */       byte b = val[i];
/*  768 */       int hiNibble = (b & 0xF0) >> 4;
/*  769 */       int loNibble = b & 0xF;
/*  770 */       bufHexVal.append(hexChars[hiNibble]).append(hexChars[loNibble]);
/*      */     }
/*      */ 
/*  773 */     String hexString = bufHexVal.toString();
/*      */ 
/*  775 */     return hexString;
/*      */   }
/*      */ 
/*      */   public static IDfId createFolder(IDfSession sess, String path, String pathSep, String type, String basePath)
/*      */     throws DfException
/*      */   {
/*  837 */     boolean cabinetStillToBeProcessed = true;
/*      */ 
/*  839 */     if ((pathSep == null) || (pathSep.length() == 0))
/*      */     {
/*  841 */       pathSep = "/";
/*      */     }
/*      */ 
/*  844 */     if ((type == null) || (type.length() == 0))
/*      */     {
/*  846 */       type = "dm_folder";
/*      */     }
/*      */ 
/*  849 */     IDfId currentId = null;
/*      */ 
/*  851 */     StringBuffer bufFldrPath = new StringBuffer(48);
/*  852 */     if ((basePath != null) && (basePath.length() > 1))
/*      */     {
/*  854 */       currentId = getIdByPath(sess, basePath);
/*  855 */       if (!currentId.isNull())
/*      */       {
/*  858 */         bufFldrPath.append(basePath);
/*  859 */         cabinetStillToBeProcessed = false;
/*      */ 
/*  861 */         int basePathLen = basePath.length();
/*  862 */         if (basePathLen < path.length())
/*      */         {
/*  864 */           path = path.substring(basePath.length());
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  870 */     StringTokenizer tokFldrNames = new StringTokenizer(path, pathSep);
/*      */ 
/*  872 */     if (cabinetStillToBeProcessed)
/*      */     {
/*  876 */       String cabinetName = tokFldrNames.nextToken();
/*  877 */       StringBuffer cabQual = new StringBuffer(32);
/*  878 */       cabQual.append("dm_cabinet where object_name='").append(
/*  879 */         cabinetName).append(
/*  880 */         "'");
/*      */ 
/*  882 */       currentId = sess.getIdByQualification(cabQual.toString());
/*  883 */       if (currentId.isNull())
/*      */       {
/*  886 */         IDfFolder cab = (IDfFolder)sess.newObject("dm_cabinet");
/*  887 */         cab.setObjectName(cabinetName);
/*  888 */         cab.save();
/*  889 */         currentId = cab.getObjectId();
/*      */       }
/*  891 */       bufFldrPath.append(pathSep).append(cabinetName);
/*      */     }
/*      */ 
/*  897 */     while (tokFldrNames.hasMoreTokens())
/*      */     {
/*  899 */       String parentPath = bufFldrPath.toString();
/*      */ 
/*  901 */       String fldrName = tokFldrNames.nextToken();
/*  902 */       bufFldrPath.append(pathSep).append(fldrName);
/*      */ 
/*  905 */       currentId = getIdByPath(sess, bufFldrPath.toString());
/*  906 */       if (currentId.isNull())
/*      */       {
/*  909 */         IDfFolder newFldr = (IDfFolder)sess.newObject(type);
/*  910 */         newFldr.setObjectName(fldrName);
/*  911 */         newFldr.link(parentPath);
/*  912 */         newFldr.save();
/*  913 */         currentId = newFldr.getObjectId();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  918 */     return currentId;
/*      */   }
/*      */ 
/*      */   public static boolean isFolderType(IDfSession sess, String type)
/*      */     throws DfException
/*      */   {
/* 1028 */     return (hasSupertype(sess, type, "dm_folder")) || (type.equals("dm_folder"));
/*      */   }
/*      */ 
/*      */   public static String[] getFolderPathsHavingBase(IDfSession sess, IDfId objId, String basePath)
/*      */     throws DfException
/*      */   {
/* 1048 */     IDfSysObject sObj = (IDfSysObject)sess.getObject(objId);
/* 1049 */     String[] allPaths = getPaths(sObj, false);
/* 1050 */     LinkedList newPaths = new LinkedList();
/* 1051 */     if (basePath == null)
/*      */     {
/* 1053 */       basePath = "";
/*      */     }
/*      */     else
/*      */     {
/* 1057 */       basePath = basePath.trim();
/*      */     }
/*      */ 
/* 1060 */     for (int i = 0; i < allPaths.length; i++)
/*      */     {
/* 1062 */       String path = allPaths[i];
/* 1063 */       if (path.startsWith(basePath))
/*      */       {
/* 1065 */         newPaths.add(path);
/*      */       }
/*      */     }
/* 1068 */     String[] arrPaths = new String[newPaths.size()];
/* 1069 */     newPaths.toArray(arrPaths);
/* 1070 */     return arrPaths;
/*      */   }
/*      */ 
/*      */   public static String getFirstPathHavingBase(IDfSysObject object, String base, boolean addDocName)
/*      */     throws DfException
/*      */   {
/* 1126 */     String sessId = object.getSession().getSessionId();
/* 1127 */     String objId = object.getObjectId().getId();
/*      */ 
/* 1129 */     String path = getURL(sessId, objId, base, null, null);
/* 1130 */     if ((path != null) && (path.length() > 0))
/*      */     {
/* 1133 */       int lastIndexPathSep = path.lastIndexOf('/');
/* 1134 */       path = path.substring(0, lastIndexPathSep);
/* 1135 */       StringBuffer bufPath = new StringBuffer(32);
/* 1136 */       bufPath.append(path);
/* 1137 */       if (addDocName)
/*      */       {
/* 1139 */         bufPath.append("/");
/* 1140 */         bufPath.append(object.getObjectName());
/*      */       }
/*      */ 
/* 1143 */       return bufPath.toString();
/*      */     }
/*      */ 
/* 1146 */     return null;
/*      */   }
/*      */ 
/*      */   public static boolean isLinkedTo(IDfSysObject sObj, IDfId fldrId)
/*      */     throws DfException
/*      */   {
/* 1161 */     for (int i = 0; i < sObj.getFolderIdCount(); i++)
/*      */     {
/* 1163 */       if (sObj.getFolderId(i).equals(fldrId))
/*      */       {
/* 1165 */         return true;
/*      */       }
/*      */     }
/* 1168 */     return false;
/*      */   }
/*      */ 
/*      */   public static IDfId createNewObject(IDfSession sess, String type, String objectName, String folder)
/*      */     throws DfException
/*      */   {
/* 1188 */     IDfSysObject sObj = (IDfSysObject)sess.newObject(type);
/* 1189 */     sObj.setObjectName(objectName);
/* 1190 */     sObj.link(folder);
/* 1191 */     sObj.save();
/* 1192 */     return sObj.getObjectId();
/*      */   }
/*      */ 
/*      */   public static String createVirtualLink(IDfSession sess, String linkBase, String basePath, IDfId objId, String format)
/*      */     throws DfException
/*      */   {
/* 1231 */     String docbaseName = sess.getDocbaseName();
/*      */ 
/* 1233 */     StringBuffer bufLinkBase = new StringBuffer(32);
/* 1234 */     bufLinkBase.append(linkBase);
/* 1235 */     if (!linkBase.endsWith("/"))
/*      */     {
/* 1237 */       bufLinkBase.append("/");
/*      */     }
/* 1239 */     bufLinkBase.append(docbaseName);
/* 1240 */     bufLinkBase.append(":");
/* 1241 */     String strLinkBase = bufLinkBase.toString();
/*      */ 
/* 1243 */     String sessId = sess.getSessionId();
/* 1244 */     String strVLink = 
/* 1245 */       getURL(sessId, objId.getId(), basePath, strLinkBase, format);
/*      */ 
/* 1247 */     return strVLink;
/*      */   }
/*      */ 
/*      */   public static IDfTypedObject getAttrByQual(IDfSession sess, String attrs, String qual)
/*      */     throws DfException
/*      */   {
/* 1269 */     StringBuffer bufQuery = new StringBuffer(32);
/* 1270 */     bufQuery.append("select ").append(attrs).append(" from ");
/* 1271 */     bufQuery.append(qual);
/* 1272 */     String strQuery = bufQuery.toString();
/*      */ 
/* 1274 */     IDfQuery query = new DfQuery();
/* 1275 */     query.setDQL(strQuery);
/* 1276 */     IDfCollection coll = null;
/*      */     try
/*      */     {
/* 1279 */       coll = query.execute(sess, 0);
/* 1280 */       if (coll.next())
/*      */       {
/* 1282 */         IDfTypedObject tObj = coll.getTypedObject();
/* 1283 */         return tObj;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1288 */       if (coll != null)
/*      */       {
/* 1290 */         coll.close();
/*      */       }
/*      */     }
/*      */ 
/* 1293 */     return null;
/*      */   }
/*      */ 
/*      */   public static IDfId getIdByPath(IDfSession sess, String path)
/*      */     throws DfException
/*      */   {
/* 1315 */     if (DfLogger.isDebugEnabled(logId))
/*      */     {
/* 1317 */       DfLogger.debug(logId, "getIdByPath got path: " + path, null, null);
/*      */     }
/*      */ 
/* 1320 */     int pathSepIndex = path.lastIndexOf('/');
/* 1321 */     if (pathSepIndex == -1)
/*      */     {
/* 1323 */       return new DfId("000");
/*      */     }
/*      */ 
/* 1326 */     StringBuffer bufQual = new StringBuffer(32);
/* 1327 */     if (pathSepIndex == 0)
/*      */     {
/* 1330 */       bufQual.append(" dm_cabinet where object_name='");
/* 1331 */       bufQual.append(path.substring(1));
/* 1332 */       bufQual.append("'");
/*      */     }
/*      */     else
/*      */     {
/* 1336 */       bufQual.append(" dm_sysobject where FOLDER('");
/* 1337 */       bufQual.append(path.substring(0, pathSepIndex));
/* 1338 */       bufQual.append("') ");
/* 1339 */       bufQual.append(" and object_name='");
/* 1340 */       bufQual.append(path.substring(pathSepIndex + 1));
/* 1341 */       bufQual.append("'");
/*      */     }
/*      */ 
/* 1344 */     String strQual = bufQual.toString();
/* 1345 */     IDfId id = sess.getIdByQualification(strQual);
/* 1346 */     return id;
/*      */   }
/*      */ }

/* Location:           devprogClasses.jar
 * Qualified Name:     com.documentum.devprog.common.UtilityMethods
 * JD-Core Version:    0.6.2
 */