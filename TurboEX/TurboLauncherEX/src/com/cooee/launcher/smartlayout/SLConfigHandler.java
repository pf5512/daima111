package com.cooee.launcher.smartlayout;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.util.Log;

import com.cooee.launcher.LauncherAppState;
import com.cooee.launcher.LauncherAppState.APP_CHANNEL;
import com.cooee.uiengine.util.xml.AssetsXmlHandler;
import com.cooee.uiengine.util.xml.ResXmlHandler;
import com.cooee.uiengine.util.xml.ResXmlHelper;

public class SLConfigHandler {

	String TAG = "SLConfigHandler";
	private final String configFile = "theme/smartLayout/config.xml";
	public LinkedList<SLApplicationMetaData> appList = new LinkedList<SLApplicationMetaData>();
	LinkedList<SLLayout> layout = new LinkedList<SLLayout>();
	LinkedList<ResolveInfo> infoList = new LinkedList<ResolveInfo>();
	private Context context;

	public SLConfigHandler(Context context) {
		this.context = context;
		loadConfig();
	}

	public void clear() {
		appList.clear();
		layout.clear();
		infoList.clear();
		System.gc();
	}

	public LinkedList<SLLayout> getLayout() {
		return layout;
	}

	public LinkedList<ResolveInfo> getApplicationInfo() {
		infoList.clear();
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final PackageManager packageManager = this.context.getPackageManager();
		List<ResolveInfo> resInfos = packageManager.queryIntentActivities(
				mainIntent, 0);
		Iterator<SLApplicationMetaData> ite = appList.iterator();
		while (ite.hasNext()) {
			boolean find = false;
			int index = -1;
			SLApplicationMetaData data = ite.next();
			for (int j = 0; j < resInfos.size(); j++) {
				if (data.getPackageName() != null
						&& !data.getPackageName().equals("")) {
					if (resInfos.get(j).activityInfo.applicationInfo.packageName != null) {
						if (data.getPackageName()
								.equalsIgnoreCase(
										resInfos.get(j).activityInfo.applicationInfo.packageName)) {
							if (data.getClassName() != null
									&& !data.getClassName().equals("")) {
								if (resInfos.get(j).activityInfo.applicationInfo.packageName != null) {
									if (data.getClassName()
											.equalsIgnoreCase(
													resInfos.get(j).activityInfo.applicationInfo.className)) {
										index = j;
										break;
									}
								}
							} else {
								index = j;
								break;
							}
						}
					}

				}

			}
			if (index != -1) {
				infoList.add(resInfos.get(index));

			}

		}

		return infoList;
	}

	public LinkedList<SLApplicationMetaData> getAppMetaData() {
		return appList;
	}

	public void loadConfig() {
		// SAXParserFactory factory = SAXParserFactory.newInstance();
		// try {
		// // 加载自身配置文件
		// SAXParser parser = null;
		// XMLReader xmlreader = null;
		// InputSource xmlin = null;
		// // 加载config_base文件
		// InputStream is = ThemeManager.getInstance().getInputStream(
		// configFile);
		// if (is != null) {
		// xmlin = new InputSource(is);
		// parser = factory.newSAXParser();
		// xmlreader = parser.getXMLReader();
		// xmlreader.setContentHandler(this);
		// xmlreader.parse(xmlin);
		// is.close();
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// new AssetsXmlHelper(context).parse(new AssetsLayout(""));

		new ResXmlHelper(context).parse(new ResLayout(context, context
				.getPackageName()));
	}

	class ResLayout implements ResXmlHandler {

		private final String TAG = "ResLayout";
		private final String TAG_ICONS = "resources";
		private final String xml = "smart_layout_config";
		private Context targetContext;
		private final String APP_CN = "application_cn";
		private final String APP_EN = "application_en";
		private final String TAG_LAYOUT = "layout";
		private String channel = null;

		public ResLayout(Context context, String pkgName) {

			if (LauncherAppState.app_chennel == APP_CHANNEL.CHINA) {
				channel = APP_CN;

			} else {
				channel = APP_EN;
			}

			this.targetContext = context;
		}

		@Override
		public String getStartTag() {
			return TAG_ICONS;
		}

		@Override
		public int file() {
			if (targetContext == null)
				return -1;

			return targetContext.getResources().getIdentifier(xml, "xml",
					targetContext.getPackageName());
		}

		@Override
		public boolean handle(XmlPullParser parser, AttributeSet attrs) {

			resolve(parser, channel);

			return true;
		}

		public void resolve(XmlPullParser parser, String channel) {

			String tagName = parser.getName();
			if (tagName != null && tagName.equals(channel)) {
				String clsName = parser.getAttributeValue(null, "class");
				String label = parser.getAttributeValue(null, "label");
				String pkgName = parser.getAttributeValue(null, "package");
				SLApplicationMetaData meta = new SLApplicationMetaData(label,
						pkgName, clsName);

				Log.v(TAG, "meta " + meta);
				appList.add(meta);
			}
			if (tagName != null & tagName.equals(TAG_LAYOUT)) {
				int cellX = Integer.parseInt(parser.getAttributeValue(null,
						"cellX"));
				int cellY = Integer.parseInt(parser.getAttributeValue(null,
						"cellY"));
				int screen = Integer.parseInt(parser.getAttributeValue(null,
						"screen"));
				SLLayout sl = new SLLayout(screen, cellX, cellY);
				layout.add(sl);
			}

		}

		@Override
		public void parseEnd(boolean complete) {
			// TODO Auto-generated method stub

		}

	}

	class AssetsLayout extends AssetsXmlHandler {
		private final String configFile = "smartLayout/config.xml";

		public AssetsLayout(String path) {
			super(path);

		}

		public String getXml() {
			return configFile;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			if (LauncherAppState.app_chennel == APP_CHANNEL.CHINA) {
				if (localName.equals("application_cn")) {
					String label = atts.getValue("label");
					String pkgname = atts.getValue("package");
					String clsname = atts.getValue("class");
					SLApplicationMetaData meta = new SLApplicationMetaData(
							label, pkgname, clsname);
					appList.add(meta);
				}
			} else {
				if (localName.equals("application_en")) {
					String label = atts.getValue("label");
					String pkgname = atts.getValue("package");
					String clsname = atts.getValue("class");
					SLApplicationMetaData meta = new SLApplicationMetaData(
							label, pkgname, clsname);
					appList.add(meta);
				}
			}
			if (localName.equals("layout")) {
				int cellX = Integer.parseInt(atts.getValue("cellX"));
				int cellY = Integer.parseInt(atts.getValue("cellY"));
				int screen = Integer.parseInt(atts.getValue("screen"));
				SLLayout sl = new SLLayout(screen, cellX, cellY);
				Log.v(TAG, "cellX " + cellX + " ,cellY " + cellY + " ,screen "
						+ screen);
				layout.add(sl);
			}
		}
	}

}
