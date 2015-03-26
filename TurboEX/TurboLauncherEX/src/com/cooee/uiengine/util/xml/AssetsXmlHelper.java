package com.cooee.uiengine.util.xml;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;

public class AssetsXmlHelper {
	private Context context;

	public AssetsXmlHelper(Context context) {
		this.context = context;
	}

	public void parse(AssetsXmlHandler handler) {
		try {
			SAXParserFactory factoey = SAXParserFactory.newInstance();
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			xmlreader.setContentHandler(handler);
			InputSource xmlin;
			xmlin = new InputSource(context.getAssets().open(handler.getXml()));
			xmlreader.parse(xmlin);
			xmlin = null;
			handler = null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
