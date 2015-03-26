package com.cooee.uiengine.util.xml;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

/**
 * 
 * @author zhongqihong
 * 
 *         XmlHelper a xml helper that helps you to resolve a specific xmlfile
 *         in which all text
 * 
 *         is stored in the standard .
 * 
 **/

public class ResXmlHelper {
	private final String TAG = "ThemeParser";
	private final Context context;

	public ResXmlHelper(Context context) {
		this.context = context;
	}

	public void parse(ResXmlHandler handler) {
		try {
			int resId = handler.file();
			if (resId < 0) {
				throw new XmlPullParserException("input not found");
			}
			// XmlPullParser parser = Xml.newPullParser();
			// parser.setInput(input,"utf-8" );

			XmlResourceParser parser = context.getResources().getXml(resId);
			AttributeSet attrs = Xml.asAttributeSet(parser);
			beginDocument(parser, handler.getStartTag());
			final int depth = parser.getDepth();
			int type;
			while (((type = parser.next()) != XmlPullParser.END_TAG || parser
					.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
				if (type != XmlPullParser.START_TAG) {
					continue;
				}
				if (!handler.handle(parser, attrs))
					break;
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		endDocument(handler);
	}

	private final void beginDocument(XmlPullParser parser,
			String firstElementName) throws XmlPullParserException, IOException {
		int type;
		while ((type = parser.next()) != XmlPullParser.START_TAG
				&& type != XmlPullParser.END_DOCUMENT) {
			;
		}
		if (type != XmlPullParser.START_TAG) {
			throw new XmlPullParserException("No start tag found");
		}
		if (!parser.getName().equals(firstElementName)) {
			throw new XmlPullParserException("Unexpected start tag: found "
					+ parser.getName() + ", expected " + firstElementName);
		}
	}

	private final void endDocument(ResXmlHandler handler) {
		handler.parseEnd(true);
	}

}
