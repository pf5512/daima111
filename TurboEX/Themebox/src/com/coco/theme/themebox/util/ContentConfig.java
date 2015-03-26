package com.coco.theme.themebox.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.service.ThemesDB;

public class ContentConfig {

	private static final String LOG_TAG = "ContentConfig";
	private DocumentBuilderFactory factory = null;
	private String thumbPath = "";
	private String previewPath = "";
	private String[] previewFiles = new String[] {};
	private String packageName = "";
	private String applicationName = "";
	private int versionCode = 0;
	private String versionName = "";
	private long applicationSize = 0;
	private String author = "";
	private String introduction = "";
	private String updateTime = "";
	private boolean reflection = false;

	public boolean getReflection() {
		return reflection;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public long getApplicationSize() {
		return applicationSize;
	}

	public String getAuthor() {
		return author;
	}

	public String getIntroduction() {
		return introduction;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public boolean saveThumb(Context remoteContext, String filePath) {
		InputStream thumbStream = null;
		FileOutputStream fileOut = null;
		byte[] buffer = new byte[10 * 1024];
		try {
			fileOut = new FileOutputStream(filePath);
			thumbStream = remoteContext.getAssets().open(thumbPath);
			int readSize = thumbStream.read(buffer);
			while (readSize > 0) {
				fileOut.write(buffer, 0, readSize);
				readSize = thumbStream.read(buffer);
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (thumbStream != null) {
				try {
					thumbStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Bitmap loadThumbImage(Context remoteContext) {
		Bitmap result = null;
		try {
			try {
				result = Tools.getPurgeableBitmap(remoteContext.getAssets()
						.open(thumbPath), Tools.dip2px(remoteContext,
						FunctionConfig.getGridWidth()), Tools.dip2px(
						remoteContext, FunctionConfig.getGridHeight()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			result = null;
		}
		return result;
	}

	public Bitmap loadPreviewImage(Context remoteContext, int index) {
		if (index < 0 || index >= previewFiles.length) {
			return null;
		}
		Bitmap result = null;
		try {
			DisplayMetrics dis = remoteContext.getResources()
					.getDisplayMetrics();
			result = Tools.getPurgeableBitmap(
					remoteContext.getAssets().open(
							previewPath + "/" + previewFiles[index]),
					dis.widthPixels / 2, dis.heightPixels / 2);
		} catch (IOException e) {
			e.printStackTrace();
			result = null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	public int getPreviewArrayLength() {
		Log.v(LOG_TAG, "getPreviewArrayLength()=" + previewFiles.length);
		return previewFiles.length;
	}

	public void reset() {
		thumbPath = "";
		previewPath = "";
		previewFiles = new String[] {};
		versionCode = 0;
		versionName = "";
		packageName = "";
		applicationName = "";
		applicationSize = 0;
		author = "";
		introduction = "";
		updateTime = "";
		reflection = false;
	}

	public boolean loadConfig(Context remoteContext, String className) {
		reset();
		if (factory == null) {
			factory = DocumentBuilderFactory.newInstance();
		}
		InputStream configStream = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			if (remoteContext.getPackageName().equals(
					ThemesDB.LAUNCHER_PACKAGENAME)) {
				if (StaticClass.set_default_theme_thumb != null
						&& !StaticClass.set_default_theme_thumb.equals("")) {
					File file = new File(StaticClass.set_default_theme_thumb
							+ "/preview.xml");
					configStream = new FileInputStream(file);
					previewPath = StaticClass.set_default_theme_thumb;
				} else {
					configStream = remoteContext.getAssets().open(
							"theme/preview/preview.xml");
					previewPath = "theme/preview";
				}
			} else {
				configStream = remoteContext.getAssets().open(
						"theme/preview/preview.xml");
				previewPath = "theme/preview";
			}
			Document doc = builder.parse(configStream);
			Element rootElement = doc.getDocumentElement();
			Element parentElement = rootElement;
			{
				// previewPath = "theme/preview";
				List<Node> itemList = getChildNodeList(parentElement, "item");
				previewFiles = new String[itemList.size()];
				for (int i = 0; i < itemList.size(); i++) {
					Element item = (Element) itemList.get(i);
					previewFiles[i] = item.getAttribute("image");
				}
			}
			if (isSystemApp(remoteContext)) {
				reflection = true;
			} else {
				reflection = false;
			}
			String strThumbPath = getAttributeValue(parentElement, "thumb",
					"path");
			if (!strThumbPath.equals("")) {
				thumbPath = previewPath + "/" + strThumbPath;
			} else if (strThumbPath.equals("") && previewFiles.length > 0) {
				thumbPath = previewPath + "/" + previewFiles[0];
			} else {
				thumbPath = "";
			}
			Log.d(LOG_TAG, "thumb=" + thumbPath);
			author = getAttributeValue(parentElement, "info", "author");
			Log.d(LOG_TAG, "author=" + author);
			updateTime = getAttributeValue(parentElement, "info", "date");
			Log.d(LOG_TAG, "updatetime=" + updateTime);
			String language = Locale.getDefault().getLanguage();// zjp
			if (!"zh".equals(language)) {
				introduction = getElementValue(parentElement, "introduction_en");
				if (introduction == null || introduction.length() == 0) {
					introduction = getElementValue(parentElement,
							"introduction");
				}
			} else
				introduction = getElementValue(parentElement, "introduction");
			Log.d(LOG_TAG, "introduction=" + introduction);
			packageName = remoteContext.getPackageName();
			PackageInfo pkgInfo = remoteContext.getPackageManager()
					.getPackageInfo(packageName, 0);
			versionCode = pkgInfo.versionCode;
			versionName = pkgInfo.versionName;
			ApplicationInfo appInfo = pkgInfo.applicationInfo;
			applicationName = remoteContext.getPackageManager()
					.getApplicationLabel(appInfo).toString();
			applicationSize = new File(appInfo.publicSourceDir).length();
			Log.d(LOG_TAG, String.format(
					"pkgName=%s,appName=%s,size=%d,vCode=%d,vName=%s",
					packageName, applicationName, applicationSize, versionCode,
					versionName));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (configStream != null) {
				try {
					configStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private String getAttributeValue(Element parent, String tagName,
			String attName) {
		Element ele = getChildElementByTag(parent, tagName);
		if (ele == null) {
			return "";
		}
		attName = ele.getAttribute(attName);
		return attName;
	}

	private String getElementValue(Element parent, String tagName) {
		Element ele = getChildElementByTag(parent, tagName);
		if (ele == null) {
			return "";
		}
		return ele.getTextContent();
	}

	private Element getChildElementByTag(Element parent, String name) {
		List<Node> nodeList = getChildNodeList(parent, name);
		if (nodeList.size() <= 0) {
			return null;
		}
		Node node = nodeList.get(0);
		if (node instanceof Element) {
			return (Element) node;
		}
		return null;
	}

	private List<Node> getChildNodeList(Element parent, String name) {
		List<Node> ret = new ArrayList<Node>();
		NodeList childList = parent.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			Node node = childList.item(i);
			if (node.getNodeName().equals(name)) {
				ret.add(node);
			}
		}
		return ret;
	}

	private boolean isSystemApp(Context context) {
		if ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) != 0
				|| (context.getApplicationInfo().flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			Log.d("StartReceiver", "isSystemApp");
			return true;
		}
		Log.d("StartReceiver", "not SystemApp");
		return false;
	}
}
