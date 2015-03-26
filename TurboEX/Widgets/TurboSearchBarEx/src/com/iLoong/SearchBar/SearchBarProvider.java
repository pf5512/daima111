package com.iLoong.SearchBar;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.iLoong.SearchBar.common.Parameter;
import com.iLoong.SearchBarEx.R;

public class SearchBarProvider extends AppWidgetProvider {

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// Log.d("mytag", "appwidget--->onDeleted()");
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		// Log.d("mytag", "appwidget--->onDisabled()");
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		// Log.d("mytag", "appwidget--->onEnabled()");
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Log.d("mytag", "appwidget--->onReceive()");
		super.onReceive(context, intent);
		if (intent.getAction().equals(Parameter.BROADCASE_SearchBarClick)) {
			// Toast.makeText(context, "点到了", Toast.LENGTH_SHORT).show();
			goWebSearch(context);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d("mytag", "appwidget--->onUpdate()");
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.appwidget_searchbar);
		Intent clickIntent = new Intent(Parameter.BROADCASE_SearchBarClick);
		PendingIntent Pintent = PendingIntent.getBroadcast(context, 0,
				clickIntent, 0);
		views.setOnClickPendingIntent(R.id.bg_imageview, Pintent);

		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}

	private void goWebSearch(Context mContext) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, "searchString");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}
}
