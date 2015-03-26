package com.cooee.launcher.theme;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.AssetManager;

import com.coco.theme.themebox.util.Tools;
import com.cooee.uiengine.util.xml.AssetsXmlHandler;

public class ThemeConfigHandler extends AssetsXmlHandler {

	private final String TAG_ICON = "icon";
	private final String TAG = "AssetXmlHandler";
	private Context context;
	private InputStream input = null;
	private AssetManager assets = null;
	//
	private String CONFIG_FILENAME = "theme/config.xml";
	private HashMap<String, Integer> mInteger = new HashMap<String, Integer>();
	private HashMap<String, String> mStrings = new HashMap<String, String>();
	private HashMap<String, String> mIcons = new HashMap<String, String>();

	public ThemeConfigHandler(String path) {
		super(path);

	}

	public String getXml() {
		return CONFIG_FILENAME;
	}

	public ThemeConfigHandler(Context context, String path) {
		super(path);
		this.context = context;
	}

	public InputStream getXmlInput() {

		assets = context.getAssets();
		try {
			input = assets.open(super.getXml());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return input;
	}

	public void startDocument() throws SAXException {

	}

	public void endDocument() throws SAXException {

	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		// if (localName.equals(TAG_ICON)) {
		// DefaultIcon temp = new DefaultIcon();
		// temp.title = atts.getValue("name");
		// temp.pkgName = atts.getValue("pkgname");
		// temp.imageName = atts.getValue("image");
		// temp.className = atts.getValue("componentName");
		// if (temp.className == null)
		// temp.className = "";
		// Log.v(TAG, "title:" + temp.title + " ,pkgname:" + temp.pkgName
		// + " ,imgName:" + temp.imageName + " ,clsName"
		// + temp.className);
		// }

		if (localName.equals("resources")) {

		} else if (localName.equals("interge")) {
			if (atts.getValue("type").equals("dip")) {
				mInteger.put(
						atts.getValue("name"),
						Tools.dip2px(context,
								Integer.valueOf(atts.getValue("value"))));
			} else {
				mInteger.put(atts.getValue("name"),
						Integer.valueOf(atts.getValue("value")));
			}
		} else if (localName.equals("string")) {
			mStrings.put(atts.getValue("name"), atts.getValue("value"));
		}

	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

	}

	public int getInteger(String key) {
		int result = -1;
		Integer value = mInteger.get(key);
		if (value != null) {
			result = value.intValue();
		}
		return result;
	}

	public String getString(String key) {
		String value = mStrings.get(key);
		if (value == null) {
			return null;
		}
		return value;
	}

}
