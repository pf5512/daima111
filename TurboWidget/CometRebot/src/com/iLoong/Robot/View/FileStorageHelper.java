package com.iLoong.Robot.View;

import android.content.Context;
import android.graphics.Bitmap;

public class FileStorageHelper {
	public static Bitmap getWidgetImageFromStorage(Context context,
			FileType fileType, int widgetId) {
		// Log.v("filepath", generateFilePath(FileType.original));
		// �Ӵ洢����ȡ���ϴα�����ļ������ص���ǰImageView��
		FileHelper mFileHelper = new FileHelper(context);
		byte[] imageBytes = mFileHelper
				.readInternalStoragePublic(generateFilePath(context, fileType,
						widgetId));
		return ImageHelper.Bytes2Bimap(imageBytes);
	}

	public static String generateFilePath(Context context,
			FileType fileNameType, int widgetId) {
		return "robot/robot_" + widgetId + ".png";

	}

	public static boolean saveImages(Context context, int widgetId,
			Bitmap drawBitmap) {
		FileHelper fileHelper = new FileHelper(context);
		fileHelper.writeToExternalStoragePublic(context,
				generateFilePath(context, FileType.original, widgetId),
				drawBitmap);
		return true;
	}
}
