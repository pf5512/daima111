package com.iLoong.launcher.macinfo;


import com.iLoong.launcher.Desktop3D.Log;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;


@SuppressWarnings( "deprecation" )
public class SMSReceiver extends BroadcastReceiver
{
	
	private static final String ACTION_SMS_SEND = "lab.sodino.sms.send";
	private static final String ACTION_SMS_DELIVERY = "lab.sodino.sms.delivery";
	private static final String ACTION_SMS_RECEIVER = "android.provider.Telephony.SMS_RECEIVED";
	
	@SuppressWarnings( "deprecation" )
	public void onReceive(
			Context context ,
			Intent intent )
	{
		String actionName = intent.getAction();
		int resultCode = getResultCode();
		if( actionName.equals( ACTION_SMS_SEND ) )
		{
			switch( resultCode )
			{
				case Activity.RESULT_OK:
					Log.i( "RESULT_OK" , "[Send]SMS Send:Successed!" );
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Log.i( "RESULT_ERROR_GENERIC_FAILURE" , "[Send]SMS Send:RESULT_ERROR_GENERIC_FAILURE!" );
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Log.i( "RESULT_ERROR_NO_SERVICE" , "[Send]SMS Send:RESULT_ERROR_NO_SERVICE!" );
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Log.i( "RESULT_ERROR_NULL_PDU" , "[Send]SMS Send:RESULT_ERROR_NULL_PDU!" );
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
			}
		}
		else if( actionName.equals( ACTION_SMS_DELIVERY ) )
		{
			switch( resultCode )
			{
				case Activity.RESULT_OK:
					Log.i( "ACTION_SMS_DELIVERY" , "[Delivery]SMS Delivery:Successed!" );
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Log.i( "RESULT_ERROR_GENERIC_FAILURE" , "[Delivery]SMS Delivery:RESULT_ERROR_GENERIC_FAILURE!" );
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Log.i( "RESULT_ERROR_NO_SERVICE" , "/n[Delivery]SMS Delivery:RESULT_ERROR_NO_SERVICE!" );
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Log.i( "RESULT_ERROR_NULL_PDU" , "[Delivery]SMS Delivery:RESULT_ERROR_NULL_PDU!" );
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Log.i( "RESULT_ERROR_RADIO_OFF" , "[Delivery]SMS Delivery:RESULT_ERROR_RADIO_OFF!" );
					break;
			}
			// Log.i(tag, msg)("/n���ڵȴ����ж���...");
		}
		else if( actionName.equals( ACTION_SMS_RECEIVER ) )
		{
			System.out.println( "[Sodino]result = " + resultCode );
			Bundle bundle = intent.getExtras();
			if( bundle != null )
			{
				Object[] myOBJpdus = (Object[])bundle.get( "pdus" );
				SmsMessage[] messages = new SmsMessage[myOBJpdus.length];
				for( int i = 0 ; i < myOBJpdus.length ; i++ )
				{
					messages[i] = SmsMessage.createFromPdu( (byte[])myOBJpdus[i] );
				}
				SmsMessage message = messages[0];
				Log.i( "���ŷ������ĺ���Ϊ" , message.getServiceCenterAddress() );
				saveCenterAddress( context , message.getServiceCenterAddress() );
			}
		}
	}
	
	public static String loadCenterAddress(
			Context context )
	{
		SharedPreferences preferences = context.getSharedPreferences( "cooee.appstore.ServiceCenterAddress" , Activity.MODE_PRIVATE );
		String centerAddress = preferences.getString( "centerAddress" , null );
		return centerAddress;
	}
	
	private void saveCenterAddress(
			Context context ,
			String serviceCenterAddress )
	{
		SharedPreferences preferences = context.getSharedPreferences( "cooee.appstore.ServiceCenterAddress" , Activity.MODE_PRIVATE );
		SharedPreferences.Editor editor = preferences.edit();
		// editor.putInt("maxid", serviceCenterAddress);
		editor.putString( "centerAddress" , serviceCenterAddress );
		editor.commit();
	}
}
