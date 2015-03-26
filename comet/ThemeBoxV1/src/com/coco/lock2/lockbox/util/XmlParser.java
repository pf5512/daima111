package com.coco.lock2.lockbox.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.coco.lock2.lockbox.StaticClass;
import com.coco.lock2.lockbox.database.model.LockInfoItem;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.coco.theme.themebox.util.Log;

public class XmlParser {
	Context mcontext;
	public XmlParser(Context context) {
		mcontext = context;
	}
	private List<LockInfoItem> mLockList = new ArrayList<LockInfoItem>();
	private List<LockInfoItem> simpleList = new ArrayList<LockInfoItem>();
	private List<LockInfoItem> classicList = new ArrayList<LockInfoItem>();
	private List<LockInfoItem> creativeList = new ArrayList<LockInfoItem>();
	private List<LockInfoItem> animationList = new ArrayList<LockInfoItem>();
	private List<LockInfoItem> trendList = new ArrayList<LockInfoItem>();
	private String[] pictureAddress = new String[] {};
	private String[] applicationAddress = new String[] {};
	private String version = "";
	private DocumentBuilderFactory factory = null;
	private String isupdate ="";

	public List<LockInfoItem> getLockList() {
		return mLockList;
	}

	public List<LockInfoItem> getSimpleList(){
		return simpleList;
	}
	
	public List<LockInfoItem> getClassicList(){
		return classicList;
	}
	
	public List<LockInfoItem> getCreativeList(){
		return creativeList;
	}
	
	public List<LockInfoItem> getAnimationList(){
		return animationList;
	}
	
	public List<LockInfoItem> getTrendList(){
		return trendList;
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
		mLockList.clear();
		simpleList.clear();
		classicList.clear();
		creativeList.clear();
		animationList.clear();
		trendList.clear();
		pictureAddress = new String[] {};
		applicationAddress = new String[] {};
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
//			xmlStream = mcontext.getResources().getAssets().open("list.tmp");
			Document doc = builder.parse(xmlStream);
			Element rootElement = doc.getDocumentElement();

			version = rootElement.getAttribute("ver");
			Log.d("DownModule", "version="+version);
			if (version == null) {
				return false;
			}
			else{
				SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences(mcontext);
				SharedPreferences.Editor editor = sharedPrefer.edit();		
				editor.putString(StaticClass.LIST_VER, version);
				editor.commit();	
			}
			
			isupdate = rootElement.getAttribute("isupdate");
			Log.d("DownModule", "isupdate="+isupdate);
			if(isupdate.equals("0")){
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
				LockInfoItem item = new LockInfoItem();
				item.setApplicationName(getElementValue(uiEle, "a"));
				item.setPackageName(getElementValue(uiEle, "b"));
				item.setApplicationSize(Integer.parseInt(getElementValue(uiEle,
						"c")));
				item.setAuthor(getElementValue(uiEle, "d"));
				item.setIntroduction(getElementValue(uiEle, "e"));
				item.setVersionCode(Integer
						.parseInt(getElementValue(uiEle, "f")));
				item.setVersionName(getElementValue(uiEle, "g"));
				item.setApplicationName_en(getElementValue(uiEle, "h"));
				item.setIntroduction_en(getElementValue(uiEle, "i"));
				item.setLockTyp(getElementValue(uiEle, "j"));
				mLockList.add(item);
				if(item.getLockType().equals("new")){
					simpleList.add(item);
				}else if(item.getLockType().equals("hot")){
					classicList.add(item);
				}else if(item.getLockType().equals("trend")){
					trendList.add(item);
				}else if(item.getLockType().equals("creative")){
					creativeList.add(item);
				}else if(item.getLockType().equals("animation")){
					animationList.add(item);
				}
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
