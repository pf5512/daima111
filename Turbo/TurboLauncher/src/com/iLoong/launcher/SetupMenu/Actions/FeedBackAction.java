package com.iLoong.launcher.SetupMenu.Actions;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cooee.android.launcher.framework.LauncherModel;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.desktop.iLoongApplication;


public class FeedBackAction extends Action
{
	
	public FeedBackAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , FeedBack.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_FEEDBACK , new FeedBackAction( ActionSetting.ACTION_FEEDBACK , FeedBackAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		if( SetupMenu.getInstance() != null )
			SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		SynRunAction();
	}
	
	@Override
	protected void OnActionFinish()
	{
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
	}
	
	public static String getContactPhone(
			Cursor cursor )
	{
		int phoneColumn = cursor.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER );
		int phoneNum = cursor.getInt( phoneColumn );
		String phoneResult = "";
		if( phoneNum > 0 )
		{
			int idColumn = cursor.getColumnIndex( ContactsContract.Contacts._ID );
			String contactId = cursor.getString( idColumn );
			Cursor phones = SetupMenu.getContext().getContentResolver()
					.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI , null , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId , null , null );
			if( phones.moveToFirst() )
			{
				for( ; !phones.isAfterLast() ; phones.moveToNext() )
				{
					int index = phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER );
					int typeindex = phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.TYPE );
					int phone_type = phones.getInt( typeindex );
					String phoneNumber = phones.getString( index );
					switch( phone_type )
					{
						case 2:
							phoneResult = phoneNumber;
							break;
					}
				}
				if( !phones.isClosed() )
				{
					phones.close();
				}
			}
		}
		return phoneResult;
	}
	
	public static class FeedBack extends Activity implements View.OnClickListener
	{
		
		private Button mbOk;
		private Button mbCancel;
		private EditText mReport;
		
		@Override
		public void onCreate(
				Bundle icicle )
		{
			super.onCreate( icicle );
			setContentView( RR.layout.feedback );
			mReport = (EditText)findViewById( RR.id.editfeedbackContent );
			mbOk = (Button)findViewById( RR.id.btnfeedbackok );
			mbCancel = (Button)findViewById( RR.id.btnfeedbackcancel );
			mbOk.setOnClickListener( this );
			mbCancel.setOnClickListener( this );
		}
		
		@Override
		public void onClick(
				View v )
		{
			if( v == null )
				return;
			if( v instanceof Button )
			{
				Button button = (Button)v;
				if( button.getId() == RR.id.btnfeedbackok )
				{
					NameValuePair param1 = new BasicNameValuePair( "type" , "report" );
					NameValuePair param2 = new BasicNameValuePair( "msg" , mReport.getText().toString() );
					NameValuePair param3 = new BasicNameValuePair( "phoneinfo" , SetupMenu.phoneinfo() );
					NameValuePair param4 = new BasicNameValuePair( "client" , iLoongApplication.getInstance().getVersionCode() );
					List<NameValuePair> formparams = new ArrayList<NameValuePair>();
					formparams.add( param1 );
					formparams.add( param2 );
					formparams.add( param3 );
					formparams.add( param4 );
					LauncherModel.getWorkerThread().post( new CommitReport( formparams ) );
					finish();
				}
				else if( button.getId() == RR.id.btnfeedbackcancel )
				{
					finish();
				}
			}
		}
	}
}
