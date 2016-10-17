/*******************************************************************************
 * Copyright (c) 2005-2006, EMC Corporation 
 * All rights reserved.

 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided that 
 * the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * - Neither the name of the EMC Corporation nor the names of its 
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
�*�
�*******************************************************************************/

/*
 * Created on Mar 31, 2005
 * Documentum Developer Program 2003
 * 
 */
package com.documentum.devprog.eclipse.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

//import org.apache.xerces.parsers.DOMParser;
//import org.apache.xpath.domapi.XPathEvaluatorImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathNSResolver;
import org.w3c.dom.xpath.XPathResult;
import org.xml.sax.SAXException;

import com.documentum.fc.common.DfLogger;
import com.documentum.services.config.ConfigException;

/**
 * This class represents a helper to access and manipulate an XML document using
 * XPath.
 * 
 * @author Aashish Patil (patil_aashish@emc.com)
 */
public class XPathHelper {

	/**
	 * The XML Doc from which config values are read in.
	 */
	private Document m_confDoc = null;

	/**
	 * The evaluator used to evaluate the xpath expressions.
	 */
	private XPathEvaluator m_xpathEval = null;

	/**
	 * The namespace resolver used for evaluating xpath expressions.
	 */
	private XPathNSResolver m_nsResolver = null;

	/**
	 * This represents the context node corresponding to the \&lt;boconfig\&gt;
	 * element. All xpath expressions are evaluated relative to this.
	 */
	private Element m_contextNode = null;

	/**
	 * Better to have a static instance of the Document Builder. Creating a
	 * document builder is quite an expensive process. Also, a doc builder can
	 * be reused to parse new documents. Thus, it makes sense to have this
	 * static and shared amongst all instances.
	 */
	private static DocumentBuilder s_docBldr = null;

	// private static DOMParser s_docBldr = null;

	//private DOMParser m_domParser = null;

	private static String XERCES_DOM_FACT = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";

	private static String XERCES_SAX_FACT = "org.apache.xerces.jaxp.SAXParserFactoryImpl";

	private static void initDocBldr() {
		// WDKLogger.debug("initDocBldr called");
		if (s_docBldr == null) {
			/*
			 * String domKey = DocumentBuilderFactory.class.getName(); String
			 * saxKey = SAXParserFactory.class.getName(); String oldDOMFact =
			 * System.getProperty(domKey); String oldSAXFact =
			 * System.getProperty(saxKey);
			 */
			// ClassLoader oldLoader =
			// Thread.currentThread().getContextClassLoader();
			try {

				DocumentBuilderFactory fact = DocumentBuilderFactory
						.newInstance();
				fact.setIgnoringComments(false);
				fact.setValidating(false);
				fact.setExpandEntityReferences(false);
				// fact.setAttribute("http://apache.org/xml/features/dom/defer-node-expansion","false");
				s_docBldr = fact.newDocumentBuilder();

				// WDKLogger.debug("s_docBldr mod: " + s_docBldr.getClass());
				s_docBldr.setEntityResolver(new NullEntityResolver());

			} catch (ParserConfigurationException pce) {
				throw new RuntimeException(
						"Unable to configure XML Parser. BOConfigService will be unavailable");
			} finally {
				/*
				 * if(oldLoader != null) {
				 * Thread.currentThread().setContextClassLoader(oldLoader); }
				 * if(oldDOMFact != null) {
				 * System.setProperty(domKey,oldDOMFact); } if(oldSAXFact !=
				 * null) { System.setProperty(saxKey,oldSAXFact); }
				 */
			}
		}
	}

	private void initDOMParser() {

		javax.xml.parsers.DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		//dfactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		try {
			DocumentBuilder docBuilder  = dfactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			DfLogger.debug(this, "", null, e);
			
		}
//		try {
//			m_domParser = new DOMParser();
//			m_domParser
//					.setFeature(
//							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
//							false);
//			m_domParser.setFeature(
//					"http://apache.org/xml/features/dom/defer-node-expansion",
//					false);
//
//		} catch (Exception ex) {
//			DfLogger.debug(this, "", null, ex);
//		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.documentum.devprog.common.config.IBOConfig#getValue(java.lang.String)
	 */
	public String getValue(String expression) {
		String[] values = getValues(expression);
		if (values.length > 0) {
			return values[0];
		}
		return "";
	}

	/**
	 * Returns the text children of the nodes found as a result of executing the
	 * xpath expression. If no results are found an empty array (length=0) is
	 * returned.
	 * 
	 * 
	 */
	public String[] getValues(String expression) {
		// WDKLogger.debug("evaluating " + expression);
		XPathResult results = (XPathResult) m_xpathEval.evaluate(expression,
				m_contextNode, m_nsResolver,
				XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
		// WDKLogger.debug("got results " + results);
		ArrayList resultList = new ArrayList(5);
		Node res;
		while ((res = results.iterateNext()) != null) {
			// WDKLogger.debug("Iterating thru results");
			NodeList childs = res.getChildNodes();
			StringBuffer bufText = new StringBuffer(32);
			for (int i = 0; i < childs.getLength(); i++) {
				Node childNode = childs.item(i);
				if (childNode.getNodeType() == Node.TEXT_NODE) {
					bufText.append(childNode.getNodeValue());
				} else {
					break;
				}

			} // for(allChildsForEachNode)
			resultList.add(bufText.toString().trim());
		} // while(allResultNodes)
		String[] strArr = new String[resultList.size()];
		resultList.toArray(strArr);
		return strArr;
	}

	/**
	 * Sets the value of an XML element identified by the xpath expression. If
	 * multiple results are returned by the expression the first is used. Order
	 * of results is not known in case of multiple results. Caller should try to
	 * ensure that the expression identifies one element.
	 * 
	 * @param expression
	 * @param value
	 */
	public void setValue(String expression, String value) {
		Node node = getValueAsXMLNode(expression);
		if (node == null) {
			// no results obtained
			return;
		}

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element elem = (Element) node;
			setElementValue(elem, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.documentum.devprog.common.config.IBOConfig#getXMLAttributeValue(java
	 * .lang.String, java.lang.String)
	 */
	public String getXMLAttributeValue(String expression, String attrName) {
		String[] values = getXMLAttributeValues(expression, attrName);
		if (values.length > 0) {
			return values[0];
		}
		return "";
	}

	/**
	 * Returns the values of the attribute specified in the list of XML Nodes
	 * obtained by evaluating the specified XPath expression. The values
	 * returned are unordered.
	 * 
	 * 
	 */
	public String[] getXMLAttributeValues(String expression, String attrName) {
		XPathResult results = (XPathResult) m_xpathEval.evaluate(expression,
				m_contextNode, m_nsResolver,
				XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);

		ArrayList lstResults = new ArrayList(5);
		Node curResult = null;
		while ((curResult = results.iterateNext()) != null) {
			if (curResult.getNodeType() == Node.ELEMENT_NODE) {
				Element elemResult = (Element) curResult;
				String attrValue = elemResult.getAttribute(attrName);
				if ((attrValue != null) && (attrValue.length() > 0)) {
					lstResults.add(attrValue);
				}
			}
		}

		String[] arrResults = new String[lstResults.size()];
		lstResults.toArray(arrResults);
		return arrResults;
	}

	/**
	 * Sets the value of attribute on the XML element identified by the
	 * expression. If multiple elements are identified by the expression the
	 * first one is used.
	 * 
	 * if attribute does not exist on the element it is not added.
	 * 
	 * @param expression
	 * @param attrName
	 * @param value
	 */
	public void setAttributeValue(String expression, String attrName,
			String value) {
		Node node = getValueAsXMLNode(expression);
		DfLogger.debug(this, "setAttributeValue got node: " + node, null, null);
		if (node == null) {
			return;
		}

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element elem = (Element) node;
			elem.setAttribute(attrName, value);

		}
	}

	/**
	 * Removes the XML node identified by the expression.All children of the
	 * element are removed by default.
	 * 
	 * @param expression
	 */
	public void removeElement(String expression) {
		Node node = getValueAsXMLNode(expression);
		if (node == null) {
			return;
		}

		Node parent = node.getParentNode();
		if (parent != null) {
			parent.removeChild(node);
		}

	}

	/**
	 * Removes the attribute from the XML element identified by the xpath
	 * expression.
	 * 
	 * @param expression
	 * @param attrName
	 */
	public void removeAttribute(String expression, String attrName) {
		Node node = getValueAsXMLNode(expression);
		if (node == null) {
			return;
		}

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element elem = (Element) node;
			if (elem.hasAttribute(attrName)) {
				elem.removeAttribute(attrName);
			}
		}
	}

	/**
	 * Reads the configuration information from the specified config source. No
	 * caching is done.
	 * 
	 * @param confSrc
	 *            InputStream from which to read config information.
	 */
	public XPathHelper(InputStream confSrc) throws Exception {
		readConfig(confSrc);
		setupXPathParams();
	}

	/**
	 * Sets up an xpath helper for the specified file.
	 * 
	 * @param filePath
	 */
	public XPathHelper(String filePath) throws Exception {
		try {
			FileInputStream fin = new FileInputStream(filePath);
			readConfig(fin);
			setupXPathParams();
		} catch (IOException ioe) {
			throw new Exception("Error while accessing XML file", ioe);
		}
	}

	/**
	 * Sets up an xpath helper with the specified node as the root node. All
	 * 
	 * @param rootNode
	 * @throws ConfigException
	 */
	public XPathHelper(Element rootNode) throws Exception {
		if (rootNode == null) {
			throw new Exception(
					"Unable to setup xpath helper as root(context) node is null");
		}
		m_contextNode = rootNode;
		m_confDoc = m_contextNode.getOwnerDocument();
		setupXPathParams();
	}

	/**
	 * Reads and parses the XML Config file. If <code>confSrc</code> is null,
	 * then attempt is made to read from the default location in the jar from a
	 * file named boconfig.xml
	 * 
	 * @param confSrc
	 */
	private void readConfig(InputStream confSrc) throws Exception {
		try {
			// initDOMParser();
			initDocBldr();
			synchronized (s_docBldr) {
				m_confDoc = s_docBldr.parse(confSrc);
				// s_docBldr.parse(new InputSource(confSrc));
				// m_confDoc = s_docBldr.getDocument();
			}

			/*
			 * m_domParser.parse(new InputSource(confSrc)); m_confDoc =
			 * m_domParser.getDocument();
			 */
			DfLogger.debug(this, "Document class: " + m_confDoc.getClass(),
					null, null);
		} catch (IOException ioe) {
			throw new Exception("IO Error while reading config info ",

			ioe);
		} catch (SAXException se) {
			throw new Exception("SAX Parser Error while reading config info ",
					se);
		} catch (RuntimeException re) {
			throw new Exception("BOConfigService runtime error", re);
		}
	}

	/**
	 * Sets up the evaluator, nsresolver and context node from the config file
	 * Document
	 * 
	 */
	private void setupXPathParams() {
		XPathFactory xfac = XPathFactory.newInstance();		
		XPath xpath  = xfac.newXPath();
		
		//xpath.evaluate("", m_contextNode, XPathConstants.NODESET);
		//NamespaceContext nsContext =  null;
		//m_contextNode.getOwnerDocument().getNamespaceURI();
		//xpath.setNamespaceContext(nsContext);
		
//		m_xpathEval = new XPathEvaluatorImpl(m_confDoc);
//
		if ((m_contextNode == null) && (m_confDoc != null)) {
			Element boconfigElem = m_confDoc.getDocumentElement();
			m_contextNode = boconfigElem;
		}
//
//		XPathNSResolver nsResolver = m_xpathEval.createNSResolver(m_confDoc);		
//		m_nsResolver = nsResolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.documentum.devprog.common.boconfig.IBOConfig#getValuesAsXMLNodes(
	 * java.lang.String)
	 */
	public Node[] getValuesAsXMLNodes(String expression) {
//
//		XPathResult results = (XPathResult) m_xpathEval.evaluate(expression,
//				m_contextNode, m_nsResolver,
//				XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
		
		XPathFactory xfac = XPathFactory.newInstance();		
		XPath xpath  = xfac.newXPath();
		NodeList results =null;
		ArrayList<Node> lstResults = new ArrayList<>(5);
		try {
			results = (NodeList)xpath.evaluate("", m_contextNode, XPathConstants.NODESET);
			Node curResult = null;
		//while ((curResult = results.iterateNext()) != null) {
		int index = 0;
		while( index <results.getLength()) {
			curResult = results.item(index++);
			lstResults.add(curResult);
		}
		
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node arrNode[] = new Node[lstResults.size()];
		lstResults.toArray(arrNode);
		
		return arrNode;

	}

	/**
	 * Gets the first Node found by the expression or returns a null.
	 * 
	 * @param expression
	 * @return
	 */
	public Node getValueAsXMLNode(String expression) {
		Node[] arr = getValuesAsXMLNodes(expression);
		if (arr.length > 0) {
			return arr[0];
		}
		return null;

	}

	/**
	 * Set the node from which the XPath expressions will be evaluated relative
	 * to. After using this method use relative expressions i.e. don't precede
	 * the expressions with a front slash.
	 * 
	 * 
	 * @param node
	 */
	public void setContextNode(Element node) {
		m_contextNode = node;
	}

	/**
	 * Gets the context node relative to which all XPath expressions are
	 * evaluated.
	 * 
	 * @return
	 */
	public Element getContextNode() {
		return m_contextNode;
	}

	/**
	 * Gets the text value of an XML element. Example -
	 * &lt;class&gt;com.documentum.web.formext.Component&lt;/class&gt;
	 * 
	 * would return the string 'com.documentum.web.formext.Component' for the
	 * 'class' <code>Element</code>
	 * 
	 * @param elem
	 * @return value of element or an empty string if no value.
	 */
	public static String getElementValue(Element elem) {
		NodeList childs = elem.getChildNodes();
		StringBuffer bufText = new StringBuffer(32);
		for (int i = 0; i < childs.getLength(); i++) {
			Node childNode = childs.item(i);
			if (childNode.getNodeType() == Node.TEXT_NODE) {
				bufText.append(childNode.getNodeValue());
			} else {
				break;
			}

		} // for(allChildsForEachNode)
		String val = bufText.toString();
		return val;
	}

	/**
	 * This is a utility method to set the text value of an element.
	 * 
	 * @param elem
	 * @param value
	 */
	public static void setElementValue(Element elem, String value) {
		NodeList children = elem.getChildNodes();
		int numChild = children.getLength();
		ArrayList arrLst = new ArrayList(numChild);
		// isolate all text child nodes
		for (int i = 0; i < numChild; i++) {
			Node childNode = children.item(i);
			if (childNode.getNodeType() == Node.TEXT_NODE) {
				arrLst.add(childNode);
			}
		}

		// remove all old text child nodes
		for (int i = 0; i < arrLst.size(); i++) {
			Node txtChild = (Node) arrLst.get(i);
			elem.removeChild(txtChild);
		}

		// add new text node with value specified.
		Node newTxtNode = elem.getOwnerDocument().createTextNode(value);
		elem.appendChild(newTxtNode);
	}

	/**
	 * Checks if atleast one node referenced by the specified expression exists.
	 * 
	 * @param expr
	 * @return
	 */
	public boolean hasNode(String expr) {
		return (getValueAsXMLNode(expr) != null);
	}

} // class XPathHelper
