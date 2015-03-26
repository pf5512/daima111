package com.cooee.searchbar;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.easou.search.sdk.core.EasouAdHotWordListener;
import com.easou.search.sdk.core.EasouSearchBoxListener;
import com.easou.search.sdk.core.EasouAdHotWordListener.EasouAdHotWordParams;
import com.easou.search.sdk.util.Constants.ColorIdx;
import com.iLoong.launcher.Desktop3D.Log;


public class EasouActivity extends Activity
{
	private EasouSearchBoxListener blueBoxRecommend;
	private EasouAdHotWordListener hotWordListenerGray;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.easou_search_activity );

		blueBoxRecommend = getSearchBox(R.id.easou_search_sdk_search_box_blue_recomend, R.array.easou_search_sdk_blue_box_recommend);
		
		EasouAdHotWordParams greyHotWordParams = new EasouAdHotWordParams();
		greyHotWordParams.setMaxNum(10);
		greyHotWordParams.getRollParams().setInterval(10000);
		greyHotWordParams.setShowAd(false);
		greyHotWordParams.setLineNum(2);
		greyHotWordParams.getRollParams().setAutoRoll(true);
		
		hotWordListenerGray = new EasouAdHotWordListener(this, greyHotWordParams);
		hotWordListenerGray.init((LinearLayout)findViewById(R.id.easou_search_sdk_hot_word_gray));
		setHotWordColor(hotWordListenerGray, R.array.easou_search_sdk_gray);
		hotWordListenerGray.getRollSchedule().startRoll();
	}

	@Override
	public boolean onCreateOptionsMenu(
			Menu menu )
	{
		getMenuInflater().inflate( R.menu.search_bar_menu_main , menu );
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		blueBoxRecommend.getRollSchedule().startRoll();
//		hotWordListenerGray.getRollSchedule().startRoll();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		blueBoxRecommend.getRollSchedule().stopRoll();
//		hotWordListenerGray.getRollSchedule().stopRoll();
	}
	
	private EasouSearchBoxListener getSearchBox(int layoutId,int colorArrayId) {
		EasouSearchBoxListener easouSearchBox = new EasouSearchBoxListener(this);
		easouSearchBox.init(findViewById(layoutId));
		setBoxColor(easouSearchBox,colorArrayId);
		return easouSearchBox;
	}
	
	private void setBoxColor(EasouSearchBoxListener easouSearchBox, int colorArrayId) {
		int[] colors = getResources().getIntArray(colorArrayId);
		easouSearchBox.setBtnColor(colors[ColorIdx.NORMAL], colors[ColorIdx.PRESS]);
		easouSearchBox.setSearchBoxColor(colors[ColorIdx.LIGHT]);
		easouSearchBox.setIconBgColor(colors[ColorIdx.ICON]);
		easouSearchBox.setSearchBoxMarginColor(colors.length > ColorIdx.STROKE ? colors[ColorIdx.STROKE] : colors[ColorIdx.PRESS]);
	}
	
	private void setHotWordColor(EasouAdHotWordListener hotWordListener,int colorArrayId) {
		int[] colors = getResources().getIntArray(colorArrayId);
		hotWordListener.setBackGroundColor(colors[ColorIdx.NORMAL], colors[ColorIdx.LIGHT]);
		hotWordListener.getTextStyle().setStateBGColor(colors[ColorIdx.PRESS]);
	}
}
