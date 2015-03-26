package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;

import com.cooeeui.brand.turbolauncher.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class RateDialogActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dsrate_dialog);

        Button button1 = (Button) findViewById(R.id.button_rate_1); 
        button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="
						+ getApplicationContext().getPackageName());
				final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);
				rateAppIntent.setClassName( "com.android.vending" , "com.android.vending.AssetBrowserActivity" );
				if (getPackageManager().queryIntentActivities(rateAppIntent, 0)
						.size() > 0) {
					startActivity(rateAppIntent);
					RateDialogActivity.this.finish();
				} else {
					Toast.makeText(RateDialogActivity.this, "Google play application is not installed", 3000).show();
				}
			}
		});
        
        Button button2 = (Button) findViewById(R.id.button_rate_2); 
        button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RateDialogActivity.this.finish();
			}
		});
        
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
        ImageView animation1;
        ImageView animation2;
        ImageView animation3;
        ImageView animation4;
        ImageView animation5;
        animation1 = (ImageView) findViewById(R.id.rate_star_1);
        animation2 = (ImageView) findViewById(R.id.rate_star_2);
        animation3 = (ImageView) findViewById(R.id.rate_star_3);
        animation4 = (ImageView) findViewById(R.id.rate_star_4);
        animation5 = (ImageView) findViewById(R.id.rate_star_5);
        
        AnimationDrawable animationDrawable1;
        AnimationDrawable animationDrawable2; 
        AnimationDrawable animationDrawable3; 
        AnimationDrawable animationDrawable4; 
        AnimationDrawable animationDrawable5; 
        
        animationDrawable1 = (AnimationDrawable) animation1.getDrawable();  
        animationDrawable1.start();  
        animationDrawable2 = (AnimationDrawable) animation2.getDrawable();  
        animationDrawable2.start();  
        animationDrawable3 = (AnimationDrawable) animation3.getDrawable();  
        animationDrawable3.start();  
        animationDrawable4 = (AnimationDrawable) animation4.getDrawable();  
        animationDrawable4.start();  
        animationDrawable5 = (AnimationDrawable) animation5.getDrawable();  
        animationDrawable5.start();  
        
		super.onWindowFocusChanged(hasFocus);
	}
	
	

}
