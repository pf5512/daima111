package com.iLoong.launcher.excpetion;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cooee.android.launcher.framework.LauncherModel;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Actions.CommitReport;
import com.iLoong.launcher.desktop.iLoongApplication;


public class ActErrorReport extends Activity
{
	
	private iLoongApplication softApplication;
	private String info;
	/** 标识来处。 */
	private String by;
	private Button btnReport;
	private Button btnCancel;
	private BtnListener btnListener;
	
	public void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( RR.layout.report );
		softApplication = (iLoongApplication)getApplication();
		by = getIntent().getStringExtra( "by" );
		info = getIntent().getStringExtra( "error" );
		TextView txtHint = (TextView)findViewById( RR.id.txtErrorHint );
		txtHint.setText( getErrorHint( by ) );
		EditText editError = (EditText)findViewById( RR.id.editErrorContent );
		editError.setText( info );
		btnListener = new BtnListener();
		btnReport = (Button)findViewById( RR.id.btnREPORT );
		btnCancel = (Button)findViewById( RR.id.btnCANCEL );
		btnReport.setOnClickListener( btnListener );
		btnCancel.setOnClickListener( btnListener );
	}
	
	private String getErrorHint(
			String by )
	{
		String hint = "";
		String append = "";
		if( "uehandler".equals( by ) )
		{
			append = " when the app running";
		}
		else if( "error.log".equals( by ) )
		{
			append = " when last time the app running";
		}
		hint = String.format( getResources().getString( RR.string.errorHint ) , append , 1 );
		return hint;
	}
	
	public void onStart()
	{
		super.onStart();
		if( softApplication.need2Exit() )
		{
			// 上一个退栈的Activity有执行“退出”的操作。
			Log.d( "ANDROID_LAB" , "ActErrorReport.finish()" );
			ActErrorReport.this.finish();
		}
		else
		{
			// go ahead normally
		}
	}
	
	class BtnListener implements Button.OnClickListener
	{
		
		@Override
		public void onClick(
				View v )
		{
			if( v == btnReport )
			{
				// 需要 android.permission.SEND权限
				NameValuePair param1 = new BasicNameValuePair( "type" , "bug" );
				NameValuePair param2 = new BasicNameValuePair( "msg" , info );
				NameValuePair param3 = new BasicNameValuePair( "phoneinfo" , SetupMenu.phoneinfo() );
				NameValuePair param4 = new BasicNameValuePair( "client" , iLoongApplication.getInstance().getVersionCode() );
				List<NameValuePair> formparams = new ArrayList<NameValuePair>();
				formparams.add( param1 );
				formparams.add( param2 );
				formparams.add( param3 );
				formparams.add( param4 );
				LauncherModel.getWorkerThread().post( new CommitReport( formparams ) );
				//				Intent mailIntent = new Intent(Intent.ACTION_SEND);
				//				mailIntent.setType("plain/text");
				//				String[] arrReceiver = { "jiangxuewen@cooee.cn" };
				//				String mailSubject = "App Error Info[" + getPackageName() + "]";
				//				String mailBody = info;
				//				mailIntent.putExtra(Intent.EXTRA_EMAIL, arrReceiver);
				//				mailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
				//				mailIntent.putExtra(Intent.EXTRA_TEXT, mailBody);
				//				startActivity(Intent.createChooser(mailIntent, "Mail Sending..."));
				ActErrorReport.this.finish();
			}
			else if( v == btnCancel )
			{
				ActErrorReport.this.finish();
			}
		}
	}
	
	public void finish()
	{
		super.finish();
		if( "error.log".equals( by ) )
		{
			// do nothing
		}
		else if( "uehandler".equals( by ) )
		{
			// 1.
			// android.os.Process.killProcess(android.os.Process.myPid());
			// 2.
			// ActivityManager am = (ActivityManager)
			// getSystemService(ACTIVITY_SERVICE);
			// am.restartPackage("lab.sodino.errorreport");
			// 3.
			// System.exit(0);
			// 1.2.3.都失效了，Google你让悲催的程序员情何以堪啊。
			softApplication.setNeed2Exit( true );
			// ////////////////////////////////////////////////////
			// // 另一个替换方案是直接返回“HOME”
			// Intent i = new Intent(Intent.ACTION_MAIN);
			// // 如果是服务里调用，必须加入newtask标识
			// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// i.addCategory(Intent.CATEGORY_HOME);
			// startActivity(i);
			// ////////////////////////////////////////////////////
		}
	}
}
