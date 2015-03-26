package com.iLoong.launcher.macinfo;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;


public class TelephonyInfo
{
	
	static Context mContext;
	
	public static void initTelephonyInfo(
			Context contex )
	{
		mContext = contex;
	}
	
	public static JSONObject getInfo()
	{
		TelephonyManager tm = (TelephonyManager)mContext.getSystemService( Context.TELEPHONY_SERVICE );
		JSONObject jObject = new JSONObject();
		try
		{
			JSONObjectUitl.put( jObject , "device_id_imei" , tm.getDeviceId() );
			JSONObjectUitl.put( jObject , "line1_number" , tm.getLine1Number() );
			// �����������ڵĹ�Ҵ��루ISO��׼��ʽ��
			// ע1: ֻ���ֻ�ע�ᵽ�����磬�ù��ܲſ���.
			// ע2���������CDMA�������У����صĽ����ܲ��ɿ�����ͨ��getPhoneType()���ж��Ƿ���CDMA�����У���
			JSONObjectUitl.put( jObject , "network_country_iso" , tm.getNetworkCountryIso() );
			// ���ص�ǰ�����ṩ�̵��������֣�MCC,MNC����ʽ��
			// �������CDMA�������У����صĽ����ܲ��ɿ�����ͨ��getPhoneType()���ж��Ƿ���CDMA�����У���
			JSONObjectUitl.put( jObject , "network_operator" , tm.getNetworkOperator() );
			// ���ص�ǰ�����ṩ�̵����֣���ĸ��ʽ��
			// �������CDMA�������У����صĽ����ܲ��ɿ�����ͨ��getPhoneType()���ж��Ƿ���CDMA�����У���
			JSONObjectUitl.put( jObject , "network_operator_name" , tm.getNetworkOperatorName() );
			// �������ڴ�����ݵ�������������͡�����GPRS
			JSONObjectUitl.put( jObject , "network_type" , tm.getNetworkType() );
			// �����ֻ����ڴ������Ե��������͡�����GSM��CDMA
			JSONObjectUitl.put( jObject , "phone_type" , tm.getPhoneType() );
			// ����SIM���ṩ�̵Ĺ�Ҵ��롣
			JSONObjectUitl.put( jObject , "sim_country_iso" , tm.getSimCountryIso() );
			// ����SIM�����ṩ�̴��룬��MCC,MNC����ʽ(mobile country code , mobile network
			// code)��������5��6Ϊ��ʮ������ʽ��
			JSONObjectUitl.put( jObject , "sim_operator" , tm.getSimOperator() );
			// ����SIM�������̵����֡�
			JSONObjectUitl.put( jObject , "sim_operator_name" , tm.getSimOperatorName() );
			// ����SIM���ı��롣����ȡʧ�ܣ��򷵻�null��
			JSONObjectUitl.put( jObject , "sim_serial_number" , tm.getSimSerialNumber() );
			// ����SIM����״̬
			JSONObjectUitl.put( jObject , "sim_state" , tm.getSimState() );
			// ���ع���ƶ��û�ʶ���롣����GSM�ֻ���˵��IMSI������ȡʧ�ܣ��򷵻�null��
			JSONObjectUitl.put( jObject , "subscriber_id_imsi" , tm.getSubscriberId() );
		}
		catch( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObject;
	}
}
