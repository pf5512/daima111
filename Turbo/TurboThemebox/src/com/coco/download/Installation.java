package com.coco.download;


import java.util.UUID;

import android.content.Context;


public class Installation
{
	
	// private static String sID = null;
	// private static final String INSTALLATION = "INSTALLATION";
	public synchronized static String id(
			Context context )
	{
		// if (sID == null)
		// {
		// File installation = new File(context.getFilesDir(), INSTALLATION);
		// try
		// {
		// if (!installation.exists())
		// writeInstallationFile(installation);
		// sID = readInstallationFile(installation);
		// }
		// catch (Exception e)
		// {
		// throw new RuntimeException(e);
		// }
		// }
		// Log.v("http", "uniqueId:" + sID);
		// return sID;
		return getMyUUID( context );
	}
	
	public static String getMyUUID(
			Context context )
	{
		// final TelephonyManager tm = (TelephonyManager)
		// context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice , tmSerial , androidId;
		// tmDevice = "" + tm.getDeviceId();
		// tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString( context.getContentResolver() , android.provider.Settings.Secure.ANDROID_ID );
		// UUID deviceUuid2 = new UUID(androidId.hashCode(),
		// ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		UUID deviceUuid = new UUID( androidId.hashCode() , androidId.hashCode() );
		String uniqueId = deviceUuid.toString();
		// String uniqueId2 = deviceUuid2.toString();
		// Log.v("statistics", "uniqueId:" + uniqueId + " uniqueId2 = " +
		// uniqueId2);
		// Log.v("statistics", "tmDevice:" + tmDevice + " tmSerial = " +
		// tmSerial+" androidId = "+androidId);
		return uniqueId;
	}
	// private static String readInstallationFile(File installation) throws
	// IOException
	// {
	// RandomAccessFile f = new RandomAccessFile(installation, "r");
	// byte[] bytes = new byte[(int) f.length()];
	// f.readFully(bytes);
	// f.close();
	// return new String(bytes);
	// }
	//
	// private static void writeInstallationFile(File installation) throws
	// IOException
	// {
	// FileOutputStream out = new FileOutputStream(installation);
	// String id = UUID.randomUUID().toString();
	// out.write(id.getBytes());
	// out.close();
	// }
}
