package com.coco.theme.themebox.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.coco.download.ItemInfo;
import com.coco.download.ListInfo;
import com.coco.theme.themebox.database.model.ThemeInfoItem;

public class ThemeXmlParser {

	Context mcontext;

	public ThemeXmlParser(Context context) {
		mcontext = context;
	}

	private List<ThemeInfoItem> mThemeList = new ArrayList<ThemeInfoItem>();
	private String[] pictureAddress = new String[] {};
	private String[] applicationAddress = new String[] {};
	private String version = "";
	private DocumentBuilderFactory factory = null;

	public List<ThemeInfoItem> getThemeList() {
		return mThemeList;
	}

	public String getVersion() {
		return version;
	}

	public String[] getPictureAddress() {
		return pictureAddress;
	}

	public String[] getApplicationAddress() {
		return applicationAddress;
	}

	private void reset() {
		mThemeList.clear();
		pictureAddress = new String[] {};
		applicationAddress = new String[] {};
	}

	public boolean parseList(List<ListInfo> listInfo, String type,
			Context context) {
		reset();
		if (listInfo == null || listInfo.size() == 0) {
			return false;
		}
		ListInfo info = null;
		for (int i = 0; i < listInfo.size(); i++) {
			if (listInfo.get(i).getTabid().equals(type)) {
				info = listInfo.get(i);
				break;
			}
		}
		if (info == null) {
			return false;
		}
		for (int i = 0; i < info.getItemList().size(); i++) {
			ThemeInfoItem item = new ThemeInfoItem();
			ItemInfo iteminfo = info.getItemList().get(i);
			item.setApplicationName(iteminfo.getCnname());
			item.setApplicationName_en(iteminfo.getEnname());
			item.setPackageName(iteminfo.getPackname());
			item.setApplicationSize(Integer.parseInt(iteminfo.getSize()));
			item.setAuthor(iteminfo.getAuthor());
			item.setIntroduction(iteminfo.getAboutchinese());
			item.setVersionCode(Integer.parseInt(iteminfo.getVersion()));
			item.setVersionName(iteminfo.getVersionname());
			item.setThumbimgUrl(iteminfo.getThumbimg());
			item.setPreviewlist(iteminfo.getPreviewlist());
			item.setResurl(iteminfo.getResurl());
			item.setResid(iteminfo.getResid());
			item.setType(type);
			try {
				item.setPrice(Integer.parseInt(iteminfo.getPrice()));
			} catch (NumberFormatException e) {
				item.setPrice(0);
			}
			item.setPricepoint(iteminfo.getPricePoint());
			item.setIndex(iteminfo.getIndex());
			item.setEnginepackname(iteminfo.getEnginepackname());
			item.setEngineurl(iteminfo.getEngineurl());
			item.setEnginesize(iteminfo.getEnginesize());
			item.setEnginedesc(iteminfo.getEnginedesc());
			item.setThirdparty(iteminfo.getThirdparty());
			mThemeList.add(item);
		}
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		preferences.edit().putInt("list-" + type, mThemeList.size()).commit();
		Collections.sort(mThemeList, new ByStringValue());
		return true;
	}

	class ByStringValue implements Comparator<ThemeInfoItem> {

		@Override
		public int compare(ThemeInfoItem lhs, ThemeInfoItem rhs) {
			// TODO Auto-generated method stub
			try {
				int left = Integer.parseInt(lhs.getIndex());
				int right = Integer.parseInt(rhs.getIndex());
				if (left > right) {
					return 1;
				} else if (left < right) {
					return -1;
				}
				return 0;
			} catch (Exception e) {
				return 0;
			}
		}
	}

	public boolean parseList(String xmlPath) {
		reset();
		if (factory == null) {
			factory = DocumentBuilderFactory.newInstance();
		}
		InputStream xmlStream = null;
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			xmlStream = new FileInputStream(xmlPath);
			Document doc = builder.parse(xmlStream);
			Element rootElement = doc.getDocumentElement();
			version = rootElement.getAttribute("ver");
			if (version == null) {
				return false;
			}
			{
				Element eleHead = getChildElementByTag(rootElement, "header");
				if (eleHead == null) {
					return false;
				}
				String pic = getElementValue(eleHead, "pic");
				if (pic == null) {
					return false;
				}
				pictureAddress = pic.split(",");
				String app = getElementValue(eleHead, "app");
				if (app == null) {
					return false;
				}
				applicationAddress = app.split(",");
			}
			Element eleLabel = getChildElementByTag(rootElement, "label");
			if (eleLabel == null) {
				return false;
			}
			List<Node> uiList = getChildNodeList(eleLabel, "ui");
			for (int i = 0; i < uiList.size(); i++) {
				Node uiNode = uiList.get(i);
				if (!(uiNode instanceof Element)) {
					return false;
				}
				Element uiEle = (Element) uiNode;
				ThemeInfoItem item = new ThemeInfoItem();
				item.setApplicationName(getElementValue(uiEle, "a"));
				item.setPackageName(getElementValue(uiEle, "b"));
				item.setApplicationSize(Integer.parseInt(getElementValue(uiEle,
						"c")));
				item.setAuthor(getElementValue(uiEle, "d"));
				item.setIntroduction(getElementValue(uiEle, "e"));
				String temp = getElementValue(uiEle, "f");
				if (temp == null || temp.equals("")) {
					item.setVersionCode(0);
				} else {
					item.setVersionCode(Integer.parseInt(temp));
				}
				item.setVersionName(getElementValue(uiEle, "g"));
				item.setPrice(Integer.parseInt(getElementValue(uiEle, "k")));// zjp
				mThemeList.add(item);
			}
			return true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (xmlStream != null) {
				try {
					xmlStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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
}
