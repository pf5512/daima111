package com.iLoong.launcher.cling;

import java.util.List;

import com.iLoong.RR;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class Introduction extends RelativeLayout{

	private iLoongLauncher launcher;
	//private ScrollScreen screen;
	//private ScrollScreen.ScreenIndicator indicator;
	//private ImageView okButton;
	private Bitmap[] bmps;
	private BitmapFactory.Options options;
	public FlipView flipView;
	
	public Introduction(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	public void setLauncher(iLoongLauncher launcher){
		this.launcher = launcher;
	}
	
	public void init(){
		bmps = new Bitmap[5];
		options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
//		createBmps();
//		flipView = new FlipView(this.getContext());		
//		flipView.setBitmaps(bmps);
		//this.addView(flipView);
		//flipView.setOnTouchListener(flipView);
		//flipView.setOnClickListener(flipView);
		//flipView.setIntroduction(this);
	}

//	public void createBmps() {
//		Bitmap bmp0 = BitmapFactory.decodeResource(getResources(), R.drawable.introduction_1, options);
//		bmps[0] = bmp0;
//		Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.introduction_2, options);
//		bmps[1] = bmp1;	
//		Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.introduction_1, options);
//		bmps[2] = bmp2;			
//		Bitmap bmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.introduction_2, options);
//		bmps[3] = bmp3;
//		Bitmap bmp4 = BitmapFactory.decodeResource(getResources(), R.drawable.introduction_1, options);
//		bmps[4] = bmp4;
//	}
	
	public void recycle(){
		for(int i = 0;i < 4;i++){
			bmps[i].recycle();
			bmps[i] = null;
		}
	}

}
