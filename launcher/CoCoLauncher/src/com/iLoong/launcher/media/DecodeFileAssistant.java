package com.iLoong.launcher.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.iLoong.launcher.Desktop3D.Log;

public class DecodeFileAssistant {
	
	private static BitmapFactory.Options optsJustBounds;
	private static BitmapFactory.Options optsDecode;
	
	static{
		optsJustBounds = new BitmapFactory.Options();
		optsJustBounds.inJustDecodeBounds = true;
		optsDecode = new BitmapFactory.Options();
	}
	public static Bitmap DecodeFile(String path,int width,int height){
		BitmapFactory.decodeFile(path, optsJustBounds);
		optsDecode.inSampleSize = computeSampleSize(optsJustBounds, -1, width*height);
		try {
			 Bitmap bmp = BitmapFactory.decodeFile(path, optsDecode);
			 return bmp;
		} catch (OutOfMemoryError err) {
			return null;
		}
	}

	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }
	    return roundedSize;       
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;
	    int lowerBound = (maxNumOfPixels == -1) ? 1 :
	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
//	    int upperBound = (minSideLength == -1) ? 128 :
//	            (int) Math.min(Math.floor(w / minSideLength),
//	            Math.floor(h / minSideLength));
//	    if (upperBound < lowerBound) {
//	        // return the larger one when there is no overlapping zone.
//	        return lowerBound;
//	    }
//	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
//	        return 1;
//	    } else if (minSideLength == -1) {
	        return lowerBound;
//	    } else {
//	        return upperBound;
//	    }
	} 

}
