package com.iLoong.NumberClock.Theme;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.DisplayMetrics;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class ThemeHelper {

	/**
	 * 获取widget内部图片目录
	 * 
	 * @param themeName
	 *            主题名
	 * @param imageName
	 *            图片名
	 * @return
	 */
	public static String getThemeImagePath(String themeName, String imageName) {
		return themeName + "/image/" + imageName;
	}

	/**
	 * 获取widget内部obj目录
	 * 
	 * @param themeName
	 *            主题名
	 * @param objName
	 *            obj名
	 * @param original
	 *            压缩或者原始模型
	 * @return
	 */
	public static String getThemeObjPath(String themeName, String objName,
			boolean original) {
		if (original) {
			return themeName + "/original_obj/" + objName;
		} else {
			return themeName + "/obj/" + objName;
		}
	}

	/**
	 * 获取launcher或者主题包下widget图片路径
	 * 
	 * @param packageName
	 *            widget包名
	 * @param themeName
	 *            主题名
	 * @param imageName
	 *            图片名
	 * @return
	 */
	public static String getLauncherThemeImagePath(String packageName,
			String themeName, String imageName) {
		return "theme/widget/" + packageName + "/" + themeName + "/image/" + imageName;
	}

	/**
	 * 获取launcher或者主题包下widget模型路径
	 * 
	 * @param packageName
	 *            widget包名
	 * @param themeName
	 *            主题名
	 * @param objName
	 *            obj名
	 * @param original
	 *            压缩或者原始
	 * @return
	 */
	public static String getLauncherThemeObjPath(String packageName,
			String themeName, String objName, boolean original) {
		if (original) {
			return "theme/widget/" + "com.iLoong.NumberClock" + "/" + themeName
					+ "/original_obj/" + objName;
		} else {
			return "theme/widget/" + "com.iLoong.NumberClock" + "/" + themeName + "/obj/"
					+ objName;
		}
	}

	public static String getLauncherThemeSubImagePath(String packageName,
			String subPath, String imageName) {
		return "theme/widget/" + packageName + "/" + subPath + "/" + imageName;
	}

	public static String getThemeSubImagePath(String subPath, String imageName) {
		return subPath + "/" + imageName;
	}

	public static String getThemeName(
			MainAppContext appContext )
	{
		if( appContext.mThemeName == null )
		{
			return "iLoong";
		}
		String themeName = appContext.mThemeName;
		if( themeName == null || themeName.length() == 0 )
		{
			return "iLoong";
		}
		if( !themeName.equals( "iLoong" ) && !themeName.equals( "female" ) )
		{
			themeName = "iLoong";
		}
		return themeName;
	}

	public static InputStream getThemeObjStream(
			MainAppContext appContext ,
			String objName ,
			boolean original )
	{
		InputStream is = null;
		String packagename = null;
		packagename = (String)appContext.paramsMap.get( "widgetpackagename" );
		if( packagename == null )
		{
			packagename = appContext.mWidgetContext.getPackageName();
		}
		String objPath = getLauncherThemeObjPath( packagename , appContext.mThemeName , objName , original );
		is = WidgetThemeManager.getInstance().getCurrentThemeInputStream( objPath );
		if( is == null )
		{
			if( appContext.mWidgetContext.equals( appContext.mContainerContext ) )
			{
				is = WidgetThemeManager.getInstance().getSysteThemeInputStream( objPath );
				if( is == null )
				{
					objPath = getLauncherThemeObjPath( packagename , "iLoong" , objName , original );
					is = WidgetThemeManager.getInstance().getCurrentThemeInputStream( objPath );
					if( is == null )
					{
						is = WidgetThemeManager.getInstance().getSysteThemeInputStream( objPath );
					}
				}
			}
			else
			{
				objPath = getThemeObjPath( appContext.mThemeName , objName , original );
				try
				{
					is = getInputStream( appContext.mWidgetContext , true , objPath );
					// is = appContext.mWidgetContext.getAssets().open(objPath);
				}
				catch( Exception e )
				{
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
				if( is == null )
				{
					objPath = getLauncherThemeObjPath( packagename , "iLoong" , objName , original );
					is = WidgetThemeManager.getInstance().getCurrentThemeInputStream( objPath );
					if( is == null )
					{
						objPath = getThemeObjPath( "iLoong" , objName , original );
						is = getInputStream( appContext.mWidgetContext , true , objPath );
						// is = appContext.mWidgetContext.getAssets().open(
						// objPath);
					}
				}
			}
		}
		return is;
	}

	public static BitmapTexture getThemeSubTexture(MainAppContext appContext,
			String subImageFolder, String fileName) {
		InputStream stream = null;
		BitmapTexture texture = null;
		subImageFolder = appContext.mThemeName + "/" + subImageFolder;
		String bitmapPath = getLauncherThemeSubImagePath(
				appContext.mWidgetContext.getPackageName(), subImageFolder,
				fileName);
		stream = WidgetThemeManager.getInstance().getCurrentThemeInputStream(
				bitmapPath);
		if (stream != null) {
			texture = new BitmapTexture(WidgetThemeManager.getInstance()
					.getBitmap(stream));
		} else {
			if (appContext.mWidgetContext.equals(appContext.mContainerContext)) {
				stream = WidgetThemeManager.getInstance()
						.getSysteThemeInputStream(bitmapPath);
				if (stream == null) {
					bitmapPath = getLauncherThemeImagePath(
							appContext.mWidgetContext.getPackageName(),
							"iLoong", fileName);
					stream = WidgetThemeManager.getInstance()
							.getCurrentThemeInputStream(bitmapPath);
					if (stream == null) {
						stream = WidgetThemeManager.getInstance()
								.getSysteThemeInputStream(bitmapPath);
					}
				}
				if (stream != null) {
					texture = new BitmapTexture(WidgetThemeManager
							.getInstance().getBitmap(stream));
				}
			} else {
				AndroidFiles widgetAsset = new AndroidFiles(
						appContext.mWidgetContext.getAssets());
				String filePath = getThemeSubImagePath(subImageFolder, fileName);
				FileHandle file = widgetAsset.internal(filePath);
				if (!file.exists()) {
					filePath = getLauncherThemeImagePath(
							appContext.mWidgetContext.getPackageName(),
							"iLoong", fileName);
					stream = WidgetThemeManager.getInstance()
							.getCurrentThemeInputStream(filePath);
					if (stream == null) {
						filePath = getThemeImagePath("iLoong", fileName);
						file = widgetAsset.internal(filePath);
						if (file != null) {
							texture = new BitmapTexture(WidgetThemeManager
									.getInstance().getBitmap(file.read()));
						}
					} else {
						texture = new BitmapTexture(WidgetThemeManager
								.getInstance().getBitmap(stream));
					}
				} else {
					texture = new BitmapTexture(WidgetThemeManager
							.getInstance().getBitmap(file.read()));
				}
			}
		}
		return texture;
	}

	public static InputStream getInputStream(Context context,
			boolean autoAdapt, String fileName) {
		InputStream instr = null;
		if (autoAdapt) {
			String filePrefix = fileName.substring(0, fileName.indexOf("/"));
			if (!filePrefix.contains("-")) {
				filePrefix = filePrefix
						+ "-"
						+ context.getResources().getDisplayMetrics().heightPixels
						+ "x"
						+ context.getResources().getDisplayMetrics().widthPixels;
			}
			// 查找精确分辨率如960*540
			try {
				String tempFileName = filePrefix
						+ fileName.substring(fileName.indexOf("/"));
				instr = context.getAssets().open(tempFileName);
			} catch (IOException e) {
				instr = null;
			}

			String dpiFilePrefix = getAutoAdaptDir(context,
					filePrefix.substring(0, filePrefix.indexOf("-")));

			if (instr == null) {
				String dpiFileName = dpiFilePrefix
						+ fileName.substring(fileName.indexOf("/"));
				try {
					instr = context.getAssets().open(dpiFileName);
				} catch (IOException e) {
				}
			}

			// 在不带dpi的目录下寻找资源，目前系统资源统一放在不带dpi的目录，所以首先寻找不带dpi的目录
			if (instr == null) {
				filePrefix = fileName.substring(0, fileName.indexOf("/"));
				if (!filePrefix.equals(dpiFilePrefix)) {
					try {
						instr = context.getAssets().open(fileName);
					} catch (IOException e) {

					}
				}
			}

		} else {
			if (instr == null) {
				try {
					instr = context.getAssets().open(fileName);
				} catch (IOException e) {
				}
			}
		}
		return instr;
	}

	public static String getAutoAdaptDir(Context context, String prefix) {
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

	public static void LoadXml(Context context, String fileName,
			DefaultHandler handler) {
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			xmlreader.setContentHandler(handler);
			InputStream inputStream = getInputStream(context, false, fileName);
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
