package com.iLoong.launcher.desktop;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import com.iLoong.launcher.theme.ThemeManager;

public class FeatureConfig {
	public static final String FEATURE_CONFIG_FILENAME = "default/feature_config.xml";

	public static boolean enable_themebox = true;

	// xiatian add start //New Requirement 20130507
	public static boolean enable_WallpaperBox = false;
	public static boolean enable_SceneBox = false;
	// xiatian add end

	// wanghongjian add start //enable_DefaultScene
	public static boolean enable_DefaultScene = false;
	public static boolean isDefaultPkg = false;
	public static String scene_pkg = "";
	public static String scene_cls = "";
	// wanghongjian add end

	public FeatureConfig() {
		LoadFeatureConfigXml();
	}

	class FeatureConfigHandler extends DefaultHandler {

		public static final String GENERAl_CONFIG = "general_config";

		public void startDocument() throws SAXException {
			// Utils3D.showPidMemoryInfo("startDocument");
		}

		public void endDocument() throws SAXException {
			// Utils3D.showPidMemoryInfo("endDocument");
		}

		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (localName.equals(GENERAl_CONFIG)) {
				String temp;
				temp = atts.getValue("enable_themebox");
				if (temp != null) {
					enable_themebox = temp.equals("true");
				}

				// xiatian add start //New Requirement 20130507
				temp = atts.getValue("enable_WallpaperBox");
				if (temp != null) {
					enable_WallpaperBox = temp.equals("true");
				}

				temp = atts.getValue("enable_SceneBox");
				if (temp != null) {
					enable_SceneBox = temp.equals("true");
				}
				// xiatian add end

				// wanghongjian add start //enable_DefaultScene
				temp = atts.getValue("enable_DefaultScene");
				if (temp != null) {
					enable_DefaultScene = temp.equals("true");
				}
				temp = atts.getValue("default_scene_cls");
				if (temp != null) {
					scene_cls = temp;
				}
				temp = atts.getValue("default_scene_pkg");
				if (temp != null) {
					scene_pkg = temp;
				}
				temp = atts.getValue("isDefaultPkg");
				if (temp != null) {
					isDefaultPkg = temp.equals("true");
				}
				// wanghongjian add end

			}
		}

		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {

		}
	}

	private void LoadFeatureConfigXml() {
		// Utils3D.showPidMemoryInfo("default");
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();

			FeatureConfigHandler handler = new FeatureConfigHandler();
			xmlreader.setContentHandler(handler);
			InputSource xmlin;
			// InputStream in =
			// iLoongApplication.getInstance().getAssets().open(FEATURE_CONFIG_FILENAME);
			InputStream in = ThemeManager.getInstance().getInputStream(
					FEATURE_CONFIG_FILENAME);
			if (in == null) {
				return;
			} else {
				xmlin = new InputSource(in);
			}

			xmlreader.parse(xmlin);
			handler = null;
			xmlin = null;
			// Utils3D.showPidMemoryInfo("default2");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
