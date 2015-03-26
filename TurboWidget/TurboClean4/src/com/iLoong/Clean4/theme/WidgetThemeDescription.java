package com.iLoong.Clean4.theme;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


import android.content.Context;
import android.util.DisplayMetrics;

public class WidgetThemeDescription {

	public String autoAdaptThemeDir = "theme";
	public String specificThemeDir = "";
	public String defaultThemeDir = "";

	private Context mContext;

	public WidgetThemeDescription(Context context) {
		mContext = context;
	}

	public WidgetThemeDescription(Context context, String fileName) {
		mContext = context;
	}

	public Context getThemeContext() {
		return mContext;
	}

	public InputStream getInputStream(boolean autoAdapt, String fileName) {
		InputStream instr = null;
		if (autoAdapt) {
			String filePrefix = fileName.substring(0, fileName.indexOf("/"));
			if (!filePrefix.contains("-")) {
				filePrefix = filePrefix
						+ "-"
						+ mContext.getResources().getDisplayMetrics().heightPixels
						+ "x"
						+ mContext.getResources().getDisplayMetrics().widthPixels;
			}
			// 查找精确分辨率如960*540
			try {
				String tempFileName = filePrefix
						+ fileName.substring(fileName.indexOf("/"));
				instr = mContext.getAssets().open(tempFileName);
			} catch (IOException e) {
				instr = null;
			}

			String dpiFilePrefix = this.getAutoAdaptDir(mContext,
					filePrefix.substring(0, filePrefix.indexOf("-")));

			if (instr == null) {
				String dpiFileName = dpiFilePrefix
						+ fileName.substring(fileName.indexOf("/"));
				try {
					instr = mContext.getAssets().open(dpiFileName);
				} catch (IOException e) {
				}
			}

			// 在不带dpi的目录下寻找资源，目前系统资源统一放在不带dpi的目录，所以首先寻找不带dpi的目录
			if (instr == null) {
				filePrefix = fileName.substring(0, fileName.indexOf("/"));
				if (!filePrefix.equals(dpiFilePrefix)) {
					try {
						instr = mContext.getAssets().open(fileName);
					} catch (IOException e) {

					}
				}
			}

		} else {
			if (instr == null) {
				try {
					instr = mContext.getAssets().open(fileName);
				} catch (IOException e) {
				}
			}
		}
		return instr;
	}

	public String getAutoAdaptDir(Context context, String prefix) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
			prefix = prefix + "-xhdpi";
		} else if (metrics.densityDpi == DisplayMetrics.DENSITY_HIGH) {
			prefix = prefix + "-hdpi";
		} else if (metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
			prefix = prefix + "-mdpi";
		} else if (metrics.densityDpi == DisplayMetrics.DENSITY_LOW) {
			prefix = prefix + "-ldpi";
		}
		return prefix;
	}

	public void LoadXml(String fileName, DefaultHandler handler) {
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			xmlreader.setContentHandler(handler);
			InputStream inputStream = getInputStream(false, fileName);
			InputSource xmlin = null;
			if (inputStream != null) {
				xmlin = new InputSource(inputStream);
			}
			// new InputSource(mContext.getAssets().open(
			// Filename));
			xmlreader.parse(xmlin);
			if (inputStream != null) {
				inputStream.close();
			}
			handler = null;
			xmlin = null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
