/*     */ package com.documentum.devprog.common;
/*     */ 
/*     */ import com.documentum.fc.common.DfLogger;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.LinkedList;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class BOConfig
/*     */ {
/*  73 */   private static Hashtable m_htblAllConfigs = null;
/*     */ 
/*  85 */   private Hashtable m_htblCurrentConfigByScope = null;
/*     */ 
/*  90 */   private Class m_clsImplClass = null;
/*     */ 
/*  96 */   private boolean m_blEnableInMemoryCaching = true;
/*     */ 
/* 101 */   private static boolean m_blGlobalEnableInMemoryCaching = true;
/*     */ 
/* 106 */   private final String m_strConfigFilename = "boconfig.xml";
/*     */ 
/* 112 */   private String m_strDefaultScopedByVal = Long.toString(System.currentTimeMillis());
/*     */ 
/* 118 */   public static String DEFAULT_SCOPE = "";
/*     */ 
/* 122 */   private String m_strScopedBy = "scopedBy";
/*     */ 
/*     */   public BOConfig(Object boImplClass, boolean enableInMemoryCaching)
/*     */   {
/* 135 */     this.m_clsImplClass = boImplClass.getClass();
/* 136 */     this.m_blEnableInMemoryCaching = enableInMemoryCaching;
/*     */ 
/* 138 */     if (m_htblAllConfigs == null)
/*     */     {
/* 140 */       if (m_blGlobalEnableInMemoryCaching)
/*     */       {
/* 142 */         m_htblAllConfigs = new Hashtable(5);
/*     */       }
/*     */ 
/* 145 */       readConfigFile();
/*     */     }
/*     */     else
/*     */     {
/* 150 */       String strImplClassName = this.m_clsImplClass.getName();
/* 151 */       if (m_htblAllConfigs.contains(strImplClassName))
/*     */       {
/* 153 */         System.out.println("Found old config");
/*     */       }
/*     */       else
/*     */       {
/* 157 */         readConfigFile();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readConfigFile()
/*     */   {
/* 187 */     InputStream insConfigFile = this.m_clsImplClass.getResourceAsStream("boconfig.xml");
/*     */     try
/*     */     {
/* 191 */       DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
/* 192 */       DocumentBuilder docBldr = docFactory.newDocumentBuilder();
/* 193 */       Document document = docBldr.parse(insConfigFile);
/* 194 */       Element docElement = document.getDocumentElement();
/*     */ 
/* 196 */       NodeList attrsList = docElement.getElementsByTagName("attributes");
/* 197 */       if (attrsList == null)
/*     */       {
/* 199 */         return;
/*     */       }
/* 201 */       int numScopedByElems = attrsList.getLength();
/* 202 */       this.m_htblCurrentConfigByScope = new Hashtable(numScopedByElems);
/*     */ 
/* 204 */       for (int cntrAttrsByScope = 0; 
/* 205 */         cntrAttrsByScope < attrsList.getLength(); 
/* 206 */         cntrAttrsByScope++)
/*     */       {
/* 209 */         Element attrsElement = (Element)attrsList.item(cntrAttrsByScope);
/*     */ 
/* 211 */         String strScopedByVal = attrsElement.getAttribute(this.m_strScopedBy);
/* 212 */         if (UtilityMethods.isStringEmpty(strScopedByVal))
/*     */         {
/* 214 */           strScopedByVal = this.m_strDefaultScopedByVal;
/*     */         }
/* 216 */         NodeList attrAll = attrsElement.getElementsByTagName("attribute");
/*     */ 
/* 218 */         if (attrAll == null)
/*     */         {
/* 220 */           return;
/*     */         }
/* 222 */         int numAttr = attrAll.getLength();
/* 223 */         Hashtable htblCurrentConfig = new Hashtable(numAttr);
/*     */ 
/* 226 */         for (int cntrAttrs = 0; cntrAttrs < numAttr; cntrAttrs++)
/*     */         {
/* 228 */           Element attrCurrent = (Element)attrAll.item(cntrAttrs);
/*     */ 
/* 230 */           NodeList lstName = attrCurrent.getElementsByTagName("name");
/* 231 */           Element elemName = (Element)lstName.item(0);
/* 232 */           Node nameChild = elemName.getFirstChild();
/* 233 */           String strName = nameChild.getNodeValue().trim();
/*     */ 
/* 235 */           NodeList lstType = attrCurrent.getElementsByTagName("type");
/* 236 */           String strType = null;
/* 237 */           if ((lstType != null) && (lstType.getLength() > 0))
/*     */           {
/* 239 */             Element elemType = (Element)lstType.item(0);
/* 240 */             Node typeChild = elemType.getFirstChild();
/* 241 */             strType = typeChild.getNodeValue().trim();
/*     */           }
/*     */ 
/* 244 */           NodeList lstValues = attrCurrent.getElementsByTagName("value");
/* 245 */           String[] strArrayValues = (String[])null;
/* 246 */           if ((lstValues != null) && (lstValues.getLength() > 0))
/*     */           {
/* 248 */             int numValues = lstValues.getLength();
/* 249 */             strArrayValues = new String[numValues];
/* 250 */             for (int cntrValues = 0; cntrValues < numValues; cntrValues++)
/*     */             {
/* 252 */               Element elemValue = (Element)lstValues.item(cntrValues);
/* 253 */               Node valueChild = elemValue.getFirstChild();
/* 254 */               strArrayValues[cntrValues] = new String();
/* 255 */               strArrayValues[cntrValues] = valueChild.getNodeValue().trim();
/*     */             }
/*     */           }
/*     */ 
/* 259 */           AttributeEntry entry = new AttributeEntry();
/* 260 */           entry.setAttrName(strName);
/*     */ 
/* 262 */           entry.setAttrType(strType);
/*     */ 
/* 264 */           entry.setAttrValues(strArrayValues);
/*     */ 
/* 267 */           htblCurrentConfig.put(strName, entry);
/*     */         }
/*     */ 
/* 270 */         this.m_htblCurrentConfigByScope.put(strScopedByVal, htblCurrentConfig);
/*     */       }
/*     */ 
/* 274 */       m_htblAllConfigs.put(this.m_clsImplClass.getName(), this.m_htblCurrentConfigByScope);
/*     */     }
/*     */     catch (ParserConfigurationException pce)
/*     */     {
/* 288 */       DfLogger.warn(this, "PArser config exception", null, pce);
/*     */     }
/*     */     catch (SAXException se)
/*     */     {
/* 292 */       DfLogger.warn(this, "SAX Parser", null, se);
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/* 297 */       DfLogger.warn(this, "IO Error", null, ioe);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 302 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasAttribute(String attrName)
/*     */   {
/* 319 */     return hasAttribute(this.m_strDefaultScopedByVal, attrName);
/*     */   }
/*     */ 
/*     */   public boolean hasAttribute(String scopeName, String attrName)
/*     */   {
/* 335 */     if (scopeName == null)
/*     */     {
/* 337 */       return false;
/*     */     }
/*     */ 
/* 340 */     if (this.m_htblCurrentConfigByScope != null)
/*     */     {
/* 342 */       if (scopeName.equals(""))
/*     */       {
/* 344 */         scopeName = this.m_strDefaultScopedByVal;
/*     */       }
/*     */ 
/* 347 */       Hashtable htbl = (Hashtable)this.m_htblCurrentConfigByScope.get(scopeName);
/*     */ 
/* 349 */       return htbl.containsKey(attrName);
/*     */     }
/*     */ 
/* 352 */     return false;
/*     */   }
/*     */ 
/*     */   public String[] getAttributeValue(String scopeName, String attrName)
/*     */   {
/* 372 */     if (scopeName == null)
/*     */     {
/* 374 */       return null;
/*     */     }
/*     */ 
/* 377 */     if (this.m_htblCurrentConfigByScope != null)
/*     */     {
/* 379 */       if (scopeName.equals(""))
/*     */       {
/* 381 */         scopeName = this.m_strDefaultScopedByVal;
/*     */       }
/*     */ 
/* 384 */       Hashtable htbl = (Hashtable)this.m_htblCurrentConfigByScope.get(scopeName);
/*     */ 
/* 386 */       if (htbl == null)
/*     */       {
/* 388 */         return null;
/*     */       }
/* 390 */       if (htbl.containsKey(attrName))
/*     */       {
/* 393 */         AttributeEntry attrEntry = (AttributeEntry)htbl.get(attrName);
/*     */ 
/* 395 */         String[] allValues = attrEntry.getAttrValues();
/*     */ 
/* 397 */         return allValues;
/*     */       }
/*     */     }
/* 400 */     return null;
/*     */   }
/*     */ 
/*     */   public String[] getAttributeValue(String attrName)
/*     */   {
/* 418 */     return getAttributeValue(this.m_strDefaultScopedByVal, attrName);
/*     */   }
/*     */ 
/*     */   public String getType(String scopeName, String attrName)
/*     */   {
/* 437 */     if (scopeName == null)
/*     */     {
/* 439 */       return null;
/*     */     }
/*     */ 
/* 442 */     if (this.m_htblCurrentConfigByScope != null)
/*     */     {
/* 444 */       if (scopeName.equals(""))
/*     */       {
/* 446 */         scopeName = this.m_strDefaultScopedByVal;
/*     */       }
/* 448 */       Hashtable htbl = (Hashtable)this.m_htblCurrentConfigByScope.get(scopeName);
/* 449 */       if (htbl == null)
/*     */       {
/* 451 */         return null;
/*     */       }
/* 453 */       if (htbl.containsKey(attrName))
/*     */       {
/* 456 */         AttributeEntry entry = (AttributeEntry)htbl.get(attrName);
/* 457 */         String type = entry.getAttrType();
/* 458 */         return type;
/*     */       }
/*     */     }
/* 461 */     return null;
/*     */   }
/*     */ 
/*     */   public String getType(String attrName)
/*     */   {
/* 478 */     return getType(this.m_strDefaultScopedByVal, attrName);
/*     */   }
/*     */ 
/*     */   public LinkedList getAllAttributeNames(String scopeName)
/*     */   {
/* 493 */     if (scopeName == null)
/*     */     {
/* 495 */       return null;
/*     */     }
/*     */ 
/* 498 */     if (this.m_htblCurrentConfigByScope != null)
/*     */     {
/* 500 */       if (scopeName.equals(""))
/*     */       {
/* 502 */         scopeName = this.m_strDefaultScopedByVal;
/*     */       }
/*     */ 
/* 505 */       Hashtable htbl = (Hashtable)this.m_htblCurrentConfigByScope.get(scopeName);
/* 506 */       LinkedList list = new LinkedList();
/*     */ 
/* 508 */       Enumeration e = htbl.keys();
/* 509 */       while (e.hasMoreElements())
/*     */       {
/* 511 */         list.add(e.nextElement());
/*     */       }
/* 513 */       return list;
/*     */     }
/* 515 */     return null;
/*     */   }
/*     */ 
/*     */   public LinkedList getAllAttributeNames()
/*     */   {
/* 527 */     return getAllAttributeNames(this.m_strDefaultScopedByVal);
/*     */   }
/*     */ 
/*     */   public int getAttributeCount(String scopeName)
/*     */   {
/* 538 */     if (scopeName == null)
/*     */     {
/* 540 */       return -1;
/*     */     }
/*     */ 
/* 543 */     if (this.m_htblCurrentConfigByScope != null)
/*     */     {
/* 545 */       if (scopeName.equals(""))
/*     */       {
/* 547 */         scopeName = this.m_strDefaultScopedByVal;
/*     */       }
/*     */ 
/* 550 */       Hashtable htbl = (Hashtable)this.m_htblCurrentConfigByScope.get(scopeName);
/* 551 */       if (htbl == null)
/*     */       {
/* 553 */         return -1;
/*     */       }
/* 555 */       return htbl.size();
/*     */     }
/* 557 */     return -1;
/*     */   }
/*     */ 
/*     */   public int getAttributeCount()
/*     */   {
/* 568 */     return getAttributeCount(this.m_strDefaultScopedByVal);
/*     */   }
/*     */ 
/*     */   public LinkedList getAllScopes()
/*     */   {
/* 581 */     if (this.m_htblCurrentConfigByScope != null)
/*     */     {
/* 583 */       LinkedList lstScopes = new LinkedList();
/* 584 */       Enumeration enumScopes = this.m_htblCurrentConfigByScope.keys();
/* 585 */       while (enumScopes.hasMoreElements())
/*     */       {
/* 587 */         String strScope = (String)enumScopes.nextElement();
/* 588 */         if (!strScope.equals(this.m_strDefaultScopedByVal))
/*     */         {
/* 590 */           lstScopes.add(strScope);
/*     */         }
/*     */       }
/*     */ 
/* 594 */       return lstScopes;
/*     */     }
/* 596 */     return null;
/*     */   }
/*     */ 
/*     */   class AttributeEntry
/*     */   {
/* 614 */     private String m_attrType = null;
/*     */ 
/* 619 */     private String[] m_attrValueArray = null;
/*     */ 
/* 624 */     private String m_attrName = null;
/*     */ 
/*     */     AttributeEntry()
/*     */     {
/*     */     }
/*     */ 
/*     */     void setAttrName(String name)
/*     */     {
/* 635 */       this.m_attrName = name;
/*     */     }
/*     */ 
/*     */     String getAttrName()
/*     */     {
/* 645 */       return this.m_attrName;
/*     */     }
/*     */ 
/*     */     void setAttrType(String type)
/*     */     {
/* 655 */       this.m_attrType = type;
/*     */     }
/*     */ 
/*     */     String getAttrType()
/*     */     {
/* 666 */       return this.m_attrType;
/*     */     }
/*     */ 
/*     */     void setAttrValues(String[] values)
/*     */     {
/* 677 */       this.m_attrValueArray = values;
/*     */     }
/*     */ 
/*     */     String[] getAttrValues()
/*     */     {
/* 690 */       return this.m_attrValueArray;
/*     */     }
/*     */   }
/*     */ }

/* Location:           devprogClasses.jar
 * Qualified Name:     com.documentum.devprog.common.BOConfig
 * JD-Core Version:    0.6.2
 */