package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.Actions.UpdateManager;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.ConversationActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;


public class AboutActivity extends Activity implements View.OnClickListener
{
	
	String qqGroupKey = "RDorNY7zvFJjUvbxj41y9pgkSx4DigUQ";
	LinearLayout llBack = null;
	LinearLayout llCheckUpdates = null;
	LinearLayout llSocial = null;
	LinearLayout llContact = null;
	LinearLayout llFeedback = null;
	LinearLayout llPrivacyNotice = null;
	public static boolean isAllowOnclick = true;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		String appVersionName = getAppVersionName(iLoongLauncher.getInstance());
		TextView versionTextView = null;
		if( !DefaultLayout.enable_google_version )
		{
			setContentView( R.layout.dsabout_cn );
			
			versionTextView = (TextView)findViewById( R.id.dsabout_id_launcher_version_cn );
			
		}
		else
		{
			setContentView( R.layout.dsabout );
			
			versionTextView = (TextView)findViewById( R.id.dsabout_id_launcher_version );
		}
		
		versionTextView.setText( appVersionName );
		
		llBack = (LinearLayout)findViewById( R.id.about_backll );
		llBack.setOnClickListener( this );
		llCheckUpdates = (LinearLayout)findViewById( R.id.dsabout_button_check_updates );
		llCheckUpdates.setOnClickListener( this );
		llSocial = (LinearLayout)findViewById( R.id.dsabout_button_social );
		llSocial.setOnClickListener( this );
		llContact = (LinearLayout)findViewById( R.id.dsabout_button_contact );
		llContact.setOnClickListener( this );
		llFeedback = (LinearLayout)findViewById( R.id.dsabout_button_feedback );
		llFeedback.setOnClickListener( this );
		llPrivacyNotice = (LinearLayout)findViewById( R.id.dsabout_button_privacy_notice );
		llPrivacyNotice.setOnClickListener( this );
		
		
		
		
		
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.about_backll:
				finish();
				overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
				break;
			case R.id.dsabout_button_check_updates:
				MobclickAgent.onEvent( this , "DSettingCheckUpdate" );
				if( isAllowOnclick )
				{
					isAllowOnclick = false;
					checkUpdates();
				}
				break;
			case R.id.dsabout_button_social:
				social();
				break;
			case R.id.dsabout_button_contact:
				contact();
				break;
			case R.id.dsabout_button_feedback:
				MobclickAgent.onEvent( this , "DSettingToFeedback" );
				startActivityForId( ConversationActivity.class );
				break;
			case R.id.dsabout_button_privacy_notice:
				startActivityForId( PrivacyNoticeActivity.class );
				break;
			default:
				break;
		}
	}
	
	UmengUpdateListener updateListener = new UmengUpdateListener() {
		
		@Override
		public void onUpdateReturned(
				int updateStatus ,
				UpdateResponse updateInfo )
		{
			switch( updateStatus )
			{
				case UpdateStatus.Yes: //has update
					UmengUpdateAgent.showUpdateDialog( AboutActivity.this , updateInfo );
					break;
				case UpdateStatus.No: //has no update
					showDialog( AboutActivity.this.getResources().getString( R.string.update_dialogmsg ) );
					break;
				case UpdateStatus.NoneWifi: //none wifi
					Toast.makeText( AboutActivity.this , AboutActivity.this.getResources().getString( R.string.update_wifi ) , Toast.LENGTH_SHORT ).show();
					break;
				case UpdateStatus.Timeout: //time out
					Toast.makeText( AboutActivity.this , AboutActivity.this.getResources().getString( R.string.update_timeout ) , Toast.LENGTH_SHORT ).show();
					break;
				default:
					break;
			}
			isAllowOnclick = true;
		}
	};
	
	private void showDialog(
			String content )
	{
		AlertDialog.Builder builder = new Builder( this ).setTitle( RR.string.version_update ).setMessage( content ).setPositiveButton( android.R.string.ok , null );
		builder.show();
	}
	
	@Override
	public boolean onKeyDown(
			int keyCode ,
			KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_BACK )
		{
			finish();
			overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
		}
		return super.onKeyDown( keyCode , event );
	}
	
	private void startActivityForId(
			Class<?> cls )
	{
		Intent in = new Intent( AboutActivity.this , cls );
		startActivity( in );
		if( Integer.valueOf( Build.VERSION.SDK ).intValue() >= 5 )
		{
			overridePendingTransition( R.anim.dsmove_in_right , R.anim.dsalphaout );
		}
	}
	
	/****************
	*
	* 发起添加群流程。群号：Turbo桌面官方群(223934516) 的 key 为： RDorNY7zvFJjUvbxj41y9pgkSx4DigUQ
	* 调用 joinQQGroup(RDorNY7zvFJjUvbxj41y9pgkSx4DigUQ) 即可发起手Q客户端申请加群 Turbo桌面官方群(223934516)
	*
	* @param key 由官网生成的key
	* @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	******************/
	public boolean joinQQGroup(
			String key )
	{
		Intent intent = new Intent();
		intent.setData( Uri.parse( "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key ) );
		// 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		try
		{
			startActivity( intent );
			return true;
		}
		catch( Exception e )
		{
			// 未安装手Q或安装的版本不支持
			SendMsgToAndroid.sendToastMsg( getResources().getText( R.string.dsabout_qq_installation_alert ).toString() );
			return false;
		}
	}
	
	public void contact()
	{
		if( !DefaultLayout.enable_google_version )
		{
			joinQQGroup( qqGroupKey );
		}
		else
		{
			String[] receiver = new String[]{ "turbolauncher@cooee.cn" };
			String subject = "Feedback on Turbo Launcher";
			String content = "";
			Intent email = new Intent( Intent.ACTION_SEND );
			email.setType( "message/rfc822" );
			// 设置邮件发收人
			email.putExtra( Intent.EXTRA_EMAIL , receiver );
			// 设置邮件标题
			email.putExtra( Intent.EXTRA_SUBJECT , subject );
			// 设置邮件内容
			email.putExtra( Intent.EXTRA_TEXT , content );
			// 调用系统的邮件系统
			startActivity( Intent.createChooser( email , "Please select." ) );
		}
	}
	
	public void social()
	{
		if( !DefaultLayout.enable_google_version )
		{
			final Uri uri = Uri.parse( "http://weibo.com/turboui" );
			Intent intent = new Intent( Intent.ACTION_VIEW , uri );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity( intent );
		}
		else
		{
			final Uri uri = Uri.parse( "http://www.facebook.com/turbolauncher" );
			Intent intent = new Intent( Intent.ACTION_VIEW , uri );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity( intent );
		}
	}
	
	public void checkUpdates()
	{
		Log.i( "UmengUpdate" , "checkUpdates" + System.currentTimeMillis() );
		Toast.makeText( this , R.string.dstoastmsg , Toast.LENGTH_SHORT ).show();
		if( RR.net_version )
		{
			UmengUpdateAgent.setDefault();
			UmengUpdateAgent.update( iLoongLauncher.getInstance() );
			UmengUpdateAgent.setUpdateOnlyWifi( true );
			UmengUpdateAgent.setUpdateAutoPopup( false );
			//UmengUpdateAgent.setDeltaUpdate(true);//增量更新
			UmengUpdateAgent.setUpdateListener( updateListener );
		}
		else
		{
			UpdateManager.getInstance().checkClientVersion();
		}
	}
	
	public static String getAppVersionName(Context context) {    
	    String versionName = "";    
	    try {    
	        // ---get the package info---    
	        PackageManager pm = context.getPackageManager();    
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);    
	        versionName = pi.versionName;    
	        if (versionName == null || versionName.length() <= 0) {    
	            return "";    
	        }    
	    } catch (Exception e) {    
	        Log.e("VersionInfo", "Exception", e);    
	    }    
	    return versionName;    
	}   
}
