package com.cooee.uiengine.util.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AssetsXmlHandler extends DefaultHandler {

	private final String xml;

	public AssetsXmlHandler(String path) {

		this.xml = path;
	}

	public String getXml() {
		return xml;
	}

	public String getPath() {
		return xml;
	}

	public void startDocument() throws SAXException {
		//
	}

	public void endDocument() throws SAXException {
		//
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

	}
}
