package com.tuyennta.automation.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.scene.web.WebEngine;

public class XPathHelper {
	
	static WebEngine webEngine;
	private static XPathHelper INSTANCE;
	private XPathHelper() {}
	
	public static XPathHelper getInstance (WebEngine engine) {
		if (INSTANCE == null) {
			INSTANCE = new XPathHelper();
		}
		webEngine = engine;
		return INSTANCE;
	}
	

	public String getXpath(Node n) {
		String xpath = "";
		String nodeName = n.getNodeName();
		if (!((Element) n).getNodeName().equalsIgnoreCase("HTML")) {
			String id_attr = ((Element) n).getAttribute("id");
			String name_attr = ((Element) n).getAttribute("name");
			String class_attr = ((Element) n).getAttribute("class");
			if (id_attr == null || id_attr.isEmpty()) {
				xpath = getAbsoluteXpath(n);
			} else {
				xpath += "//" + nodeName + "[";
				xpath += "@id='" + id_attr;
				if (class_attr != null && !class_attr.isEmpty()) {
					xpath += "' and @class='" + class_attr;
				}
				if (name_attr != null && !name_attr.isEmpty()) {
					xpath += "' and @name='" + name_attr;
				}
				xpath += "']";

				if (!checkUniqueXpath(xpath)) {
					xpath = getAbsoluteXpath(n);
				}
			}
		}

		return xpath;
	}

	public String getAbsoluteXpath(Node node) {

		if (node.getNodeName().equalsIgnoreCase("body"))
			return "//" + node.getNodeName();

		NodeList siblings = node.getParentNode().getChildNodes();
		int ix = 0;
		for (int i = 0; i < siblings.getLength(); i++) {
			Node sibling = siblings.item(i);
			// if this sibling is current node ==> get the index
			if (sibling.equals(node)) {
				return getAbsoluteXpath(node.getParentNode()) + '/' + node.getNodeName() + '[' + (ix + 1) + ']';
			}
			// if node type is Element and node name is equal (but node is not equal)
			// https://www.w3schools.com/xml/dom_nodetype.asp
			if (sibling.getNodeType() == 1 && sibling.getNodeName().equals(node.getNodeName())) {
				ix++;
			}
		}

		return "";
	}

	public String getSelectionValueByXpath(String xpath) {
		String script = "function getSelectionValueByXpath() \r\n" + "{\r\n" + "	var iterator = document.evaluate(\""
				+ xpath + "\", document, null, XPathResult.ANY_TYPE, null);\r\n"
				+ "	var next = iterator.iterateNext();\r\n" + "	var options = next.selectedOptions;\r\n"
				+ "	var value = \"\";\r\n" + "	for (var i = 0; i < options.length; i++){ \r\n"
				+ "		value += options[i].value + ((i == options.length -1) ? \"\" : \";\");\r\n" + "	}\r\n"
				+ "	return value;\r\n" + "}getSelectionValueByXpath();";
		return (String) webEngine.executeScript(script);
	}

	public String getElementValueByXpath(String xpath) {
		String script = "function getElementValueByXpath() \r\n" + "{\r\n" + "	var iterator = document.evaluate(\""
				+ xpath + "\", document, null, XPathResult.ANY_TYPE, null);\r\n"
				+ "	return iterator.iterateNext().value;	\r\n" + "}getElementValueByXpath();";
		return (String) webEngine.executeScript(script);
	}

	public boolean checkUniqueXpath(String xpath) {
		String script = "function getElementsByXpath() \r\n" + "{\r\n" + "	var iterator = document.evaluate(\"" + xpath
				+ "\", document, null, XPathResult.ANY_TYPE, null);\r\n"
				+ "	var thisNode = iterator.iterateNext();\r\n" + "	var i = 0;\r\n" + "	while (thisNode) {\r\n"
				+ "		i ++;\r\n" + "		thisNode = iterator.iterateNext();\r\n" + "	}	\r\n" + "	return i;\r\n"
				+ "}getElementsByXpath();";
		return ((int) webEngine.executeScript(script)) == 1;
	}
}
