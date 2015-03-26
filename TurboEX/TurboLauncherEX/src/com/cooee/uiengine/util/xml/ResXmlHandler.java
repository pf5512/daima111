package com.cooee.uiengine.util.xml;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.AttributeSet;

/**
 * 
 * @author zhognqihong
 * 
 * 
 * **/

public interface ResXmlHandler {
	// firstly ,we should ensure the tag being resolved currently is in the type
	// of "icon".
	public String getStartTag();

	// return a resId to be resolved.
	public int file();

	// this is a callback where we resolve text.
	public boolean handle(XmlPullParser parser, AttributeSet attrs);

	/**
	 * @param complete
	 *            : whether all useful text has already been resolved.
	 * 
	 * **/
	public void parseEnd(boolean complete);
}
