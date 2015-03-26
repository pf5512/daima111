package com.iLoong.launcher.Desktop3D;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.content.Context;
import android.content.res.AssetManager;

public class AppReminder {
	private Context context;
	
	public class Info {
		String packageName;
		int remindNo;
	}

	private class IconInfo {
		int remindNo;
		Icon3D toRemind;
	}
	
	private ArrayList<Info> packageList = new ArrayList<Info>();
	private HashMap<String, IconInfo> inRemindMap = new HashMap<String, IconInfo>();
	
	public AppReminder(Context context) {
		this.context = context;
		readProfile();
	}
	
	public boolean isRemindApp(String packageName, Info info) {
		boolean ret = false;
		
		for (int i = 0; i < packageList.size(); i++) {
			if (packageName.equals(packageList.get(i).packageName)) {
				info.packageName = packageList.get(i).packageName;
				info.remindNo = packageList.get(i).remindNo;
				ret = true;
				break;
			} 
		}
			
		return ret;
	}
	
	public boolean isRemindApp(String packageName) {
		boolean ret = false;
		
		for (int i = 0; i < packageList.size(); i++) {
			if (packageName.equals(packageList.get(i).packageName)) {
				ret = true;
				break;
			} 
		}
			
		return ret;
	}
	
	public boolean isInRemind(String packageName) {
		if (inRemindMap.containsKey(packageName)) {
			return true;
		} else {
			return false;
		} 
	}
	
	public void endRemind(String packageName) {
		if (inRemindMap.containsKey(packageName)) {
			IconInfo info = inRemindMap.get(packageName);
			info.toRemind.setAppRemind(false);
			Preferences p = Gdx.app.getPreferences("app.remind");
			p.putInteger(packageName + ".remind", info.remindNo);
			p.flush();
			inRemindMap.remove(packageName);
		}
	}
	
	public void startReminding(String packageName, Icon3D toRemind, int remindNo) {
		// FIXME: As there is a bug in desktop loading that the icons will
		// load several times. We have to add this judgment so that only
		// the last icon3d will add to the map.
		if (inRemindMap.containsKey(packageName)) {
			return;
//			inRemindMap.remove(packageName);
//			Gdx.app.log("diaosixu", "startReminding complicated");
		}
		IconInfo info = new IconInfo();
		info.toRemind = toRemind;
		info.remindNo = remindNo;
		inRemindMap.put(packageName, info); 
	}
	
	private void readProfile() {
		JSONObject jObject = null;
		InputStream inputStream = null;
		AssetManager assetManager = context.getAssets();
		try
		{
			if (DefaultLayout.enable_google_version) {
				inputStream = assetManager.open( "default/app_remind.ini" );	
			} else {
				inputStream = assetManager.open( "default/app_remind_cn.ini" );
			}
			
			String config = readTextFile( inputStream );
			try
			{
				jObject = new JSONObject( config );
				JSONArray array =  jObject.getJSONArray("apps");
				for (int i = 0; i < array.length(); i++) {
					Info remindApp = new Info();
					JSONObject jRes = (JSONObject)array.get(i);
					remindApp.packageName = jRes.getString("packageName");
					remindApp.remindNo = jRes.getInt("remindNo");
					Preferences p = Gdx.app.getPreferences("app.remind");
					int remindNo = p.getInteger(remindApp.packageName + ".remind");
					if (remindNo < remindApp.remindNo) {
						packageList.add(remindApp);			
					}
				}
			}
			catch( JSONException e1 )
			{
				e1.printStackTrace();
			}
		}
		catch( IOException e )
		{
			Log.v( "iLoongLauncher" , e.getMessage() );
		}
		finally
		{
			try
			{
				if( inputStream != null )
				{
					inputStream.close();
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	private String readTextFile(
			InputStream inputStream )
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try
		{
			while( ( len = inputStream.read( buf ) ) != -1 )
			{
				outputStream.write( buf , 0 , len );
			}
			outputStream.close();
			inputStream.close();
		}
		catch( IOException e )
		{
		}
		return outputStream.toString();
	}
	
	public void dispose() {
		packageList.clear();
		inRemindMap.clear();
	}
}
