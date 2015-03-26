package com.iLoong.NumberClock.Theme;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.TypedValue;

import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class ConfigHandler extends DefaultHandler {
	private String internalConfigBaseFile = "config/config_base.xml";
	private String internalConfigFile = "config/config.xml";
	private String launcherConfigFile = "theme/widget/{0}/config.xml";
	private String systemConfigFile = "/system/launcher/widget/{0}/config.xml";
	private HashMap<String, Integer> mInteger = new HashMap<String, Integer>();
	private HashMap<String, Float> mFloat = new HashMap<String, Float>();
	private HashMap<String, ConfigDimen> mDimen = new HashMap<String, ConfigDimen>();

	private HashMap<String, ConfigString> mString = new HashMap<String, ConfigString>();
	private HashMap<String, ConfigBoolean> mBoolean = new HashMap<String, ConfigBoolean>();
	private HashMap<String, ArrayList<String>> mStringArray = new HashMap<String, ArrayList<String>>();
	private ArrayList<String> stringList = new ArrayList<String>();
	private String parentTag = "";
	private String parentName = "";
	private Context mContext;
	private MainAppContext appContext;
	private StringBuilder builder;
	private ConfigDimen configDimen = null;
	private ConfigBoolean configBoolean = null;
	private ConfigString configString = null;

	public HashMap<String, Integer> getmInteger() {
		return mInteger;
	}

	public HashMap<String, ConfigString> getmString() {
		return mString;
	}

	public HashMap<String, ConfigBoolean> getmBoolean() {
		return mBoolean;
	}

	public HashMap<String, Float> getmFloat() {
		return mFloat;
	}

	public HashMap<String, ArrayList<String>> getmStringArray() {
		return mStringArray;
	}

	public HashMap<String, ConfigDimen> getmDimen() {
		return mDimen;
	}

	public ConfigHandler(MainAppContext appContext) {
		this.appContext = appContext;
		mContext = appContext.mContainerContext;
		launcherConfigFile = launcherConfigFile.replace("{0}",
				"com.iLoong.NumberClock");
		systemConfigFile = systemConfigFile.replace("{0}",
				appContext.mWidgetContext.getPackageName());
		LoadDefaultLayoutXml();
	}

	public void LoadDefaultLayoutXml() {
		// Utils3D.showPidMemoryInfo("default");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// 加载自身配置文件
			SAXParser parser = null;
			XMLReader xmlreader = null;
			InputSource xmlin = null;

			// 加载config_base文件
//			InputStream is = ThemeHelper.getInputStream(
//					appContext.mWidgetContext, true, internalConfigBaseFile);
//			if (is != null) {
//				xmlin = new InputSource(is);
//				parser = factory.newSAXParser();
//				xmlreader = parser.getXMLReader();
//				xmlreader.setContentHandler(this);
//				xmlreader.parse(xmlin);
//				is.close();
//			}
//
//			// 加载config.xml文件
//			is = ThemeHelper.getInputStream(appContext.mWidgetContext, true,
//					internalConfigFile);
//			if (is != null) {
//				xmlin = new InputSource(is);
//				parser = factory.newSAXParser();
//				xmlreader = parser.getXMLReader();
//				xmlreader.setContentHandler(this);
//				xmlreader.parse(xmlin);
//				is.close();
//			}

			// 加载launcher目录下配置文件，覆盖Widget自身配置文件
			InputStream is = WidgetThemeManager.getInstance().getSysteThemeInputStream(
					launcherConfigFile);
			if (is != null) {
				xmlin = new InputSource(is);
				parser = factory.newSAXParser();
				xmlreader = parser.getXMLReader();
				xmlreader.setContentHandler(this);
				xmlreader.parse(xmlin);
				is.close();
			}

//			// 加载主题个性化配置
//			if (!WidgetThemeManager.getInstance().currentThemeIsSystemTheme()) {
//				is = WidgetThemeManager.getInstance()
//						.getCurrentThemeInputStream(launcherConfigFile);
//				if (is != null) {
//					xmlin = new InputSource(is);
//					parser = factory.newSAXParser();
//					xmlreader = parser.getXMLReader();
//					xmlreader.setContentHandler(this);
//					xmlreader.parse(xmlin);
//					is.close();
//				}
//			}
//
//			// 如果应用为内置，则加载内置后的配置文件
//			ApplicationInfo appInfo = appContext.mWidgetContext
//					.getApplicationInfo();
//			boolean BuiltIn = (appInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0;
//			if (BuiltIn) {
//				File file = new File(systemConfigFile);
//				if (file.exists()) {
//					is = new FileInputStream(file.getAbsolutePath());
//					xmlin = new InputSource(is);
//					parser = factory.newSAXParser();
//					xmlreader = parser.getXMLReader();
//					xmlreader.setContentHandler(this);
//					xmlreader.parse(xmlin);
//					is.close();
//				}
//			}
			// Utils3D.showPidMemoryInfo("default2");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		if (localName.equals("dimen")) {
			String value = builder.toString();
			if (configDimen != null && value.trim().length() > 0) {
				if (value.contains("px")) {
					value = value.substring(0, value.indexOf("px"));
					configDimen.type = "px";
					configDimen.value = TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_PX,
							Float.parseFloat(value),
							appContext.mContainerContext.getResources()
									.getDisplayMetrics());
				} else if (value.contains("dp")) {
					value = value.substring(0, value.indexOf("dp"));
					configDimen.type = "dp";
					configDimen.value = TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, Float
									.parseFloat(value),
							appContext.mContainerContext.getResources()
									.getDisplayMetrics());
				} else if (value.contains("dip")) {
					configDimen.type = "dip";
					value = value.substring(0, value.indexOf("dip"));
					configDimen.value = TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, Float
									.parseFloat(value),
							appContext.mContainerContext.getResources()
									.getDisplayMetrics());
				} else if (value.contains("sp")) {
					configDimen.type = "sp";
					value = value.substring(0, value.indexOf("sp"));
					configDimen.value = TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_SP,
							Float.parseFloat(value),
							appContext.mContainerContext.getResources()
									.getDisplayMetrics());
				}
				mDimen.put(configDimen.name, configDimen);
			}
		} else if (localName.equals("boolean")) {
			String value = builder.toString();
			if (configBoolean != null) {
				if (value == null || value.length() == 0) {
					configBoolean.value = false;
				} else {
					configBoolean.value = Boolean.parseBoolean(value);
				}
				mBoolean.put(configBoolean.name, configBoolean);
			}
		} else if (localName.equals("item")) {
			if (parentTag.equals("string-array")) {
				stringList.add(builder.toString());
			}
		} else if (localName.equals("string-array")) {
			if (stringList.size() > 0) {
				ArrayList<String> value = new ArrayList<String>();
				value.addAll(stringList);
				mStringArray.put(parentName, value);
				parentTag = "";
				parentName = "";
				stringList.clear();
			}
		} else if (localName.equals("string")) {
			if (configString != null) {
				configString.value = builder.toString();
				mString.put(configString.name, configString);
			}
		}
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		builder = new StringBuilder();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		builder.append(ch, start, length); // 将读取的字符数组追加到builder中
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		// TODO Auto-generated method stub
		builder.setLength(0);
		if (localName.equals("dimen")) {
			configDimen = new ConfigDimen();
			builder.setLength(0);
			String name = atts.getValue("name");
			String type = atts.getValue("type");
			configDimen.name = name;
			configDimen.type = type;
		} else if (localName.equals("boolean")) {
			configBoolean = new ConfigBoolean();
			configBoolean.name = atts.getValue("name");
		} else if (localName.equals("interger")) {
			String name = atts.getValue("name");
			String value = atts.getValue("value");
			String type = atts.getValue("type");
			if (type == null || type.isEmpty() || type.equals("px")) {
				mInteger.put(name, Integer.parseInt(value));
			} else if (type.equals("dp") || type.equals("dip")) {
				mInteger.put(name,
						Tools.dip2px(mContext, Integer.parseInt(value)));
			}
		} else if (localName.equals("float")) {
			String name = atts.getValue("name");
			String value = atts.getValue("value");
			String type = atts.getValue("type");
			if (type == null || type.isEmpty() || type.equals("px")) {
				mFloat.put(name, Float.parseFloat(value));
			} else if (type.equals("dp") || type.equals("dip")) {
				final float scale = appContext.mContainerContext.getResources()
						.getDisplayMetrics().density;
				mFloat.put(name, (Float.parseFloat(value) * scale));
			}
		} else if (localName.equals("string")) {
			configString = new ConfigString();
			configString.name = atts.getValue("name");

		} else if (localName.equals("string-array")) {
			parentName = atts.getValue("name");
			parentTag = "string-array";
			stringList = new ArrayList<String>();
			if (mStringArray.containsKey(parentName)) {
				mStringArray.remove(parentName);
			}
		} else if (localName.equals("item")) {
			if (parentTag.equals("string-array")) {

			}
		}
	}

}
