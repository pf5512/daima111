package com.iLoong.launcher.HotSeat3D;

import com.badlogic.gdx.Gdx;
import com.iLoong.launcher.desktop.iLoongLauncher;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog.Calls;
import com.iLoong.launcher.Desktop3D.Log;

public class MissedCallIntent
{
	public int newMmsCount=0;      
	public int newSmsCount=0;
	public int missedCallCount = 0;
	static final String MISSECALL_EXTRA = "missecall";
	private MissedCallsContentObserver mMissedCallIntentReceiver = null;
	private newMmsContentObserver mnewMmsContentObserver = null;
	public MissedCallIntent(Context context, Handler handler) {
		if(mMissedCallIntentReceiver == null)
			mMissedCallIntentReceiver = new MissedCallsContentObserver();
		if(mnewMmsContentObserver == null)
			mnewMmsContentObserver = new newMmsContentObserver(context,handler);
		// TODO Auto-generated constructor stub
	}
	public MissedCallsContentObserver getMissedCallIntentReceiver()
	{
		return mMissedCallIntentReceiver;
	}
	public newMmsContentObserver getnewMmsContentObserver()
	{
		return mnewMmsContentObserver;
	}
	
	
	public class MissedCallsContentObserver extends ContentObserver
	{
	    public MissedCallsContentObserver()
	    {
	        super(null);
	    }

	    @Override
	    public void onChange(boolean selfChange)
	    {
	        Cursor cursor = iLoongLauncher.getInstance().getContentResolver().query(
	            Calls.CONTENT_URI, 
	            null, 
	            Calls.TYPE +  " = ? AND " + Calls.NEW + " = ?", 
	            new String[] { Integer.toString(Calls.MISSED_TYPE), "1" }, 
	            Calls.DATE + " DESC ");

	        //this is the number of missed calls
	        //for your case you may need to track this number
	        //so that you can figure out when it changes
	        missedCallCount = cursor.getCount(); 
	        if(Gdx.graphics != null)
	        	Gdx.graphics.requestRendering();
	        Log.v("launcher","missedCallCount:" + missedCallCount);
	    }
	}
	public class MissedCallIntentReceiver extends BroadcastReceiver 
	{
		public MissedCallIntentReceiver()
		{
			
		}
		
		@Override  
		public void onReceive(Context context, Intent intent) {  
	               missedCallCount = intent.getIntExtra(MISSECALL_EXTRA, 0);//未接电话数目   
	               
		}  
	}  
	
	private void findNewSmsCount(Context ctx){  
        Cursor  csr = null;  
        try {  
            csr = ctx.getContentResolver().query(Uri.parse("content://sms"), null,"type = 1 and read = 0", null, null);  
            newSmsCount=csr.getCount(); //未读短信数目   
        } catch (Exception e) {  
             e.printStackTrace();  
        } finally {  
             csr.close();  
        }  
       
       // Log.v(TAG,"newSmsCount="+newSmsCount);  
    }  
  
    private void findNewMmsCount(Context ctx){  
        Cursor  csr = null;  
        try {  
             csr = ctx.getContentResolver().query(Uri.parse("content://mms/inbox"), null,  
                      "read = 0", null, null);  
             newMmsCount=csr.getCount();//未读彩信数目   
        } catch (Exception e) {  
             e.printStackTrace();  
        } finally {  
             csr.close();  
        }  
         
    } 
	
	//监控短信，彩信数目变化   
	public class newMmsContentObserver extends ContentObserver{  
	    private Context ctx;         
	    public newMmsContentObserver(Context context, Handler handler) 
	    {       
	        super(handler);       
	        ctx = context;       
	    }       
	
		@Override      
		public void onChange(boolean selfChange)
		{     
		   
	    	newMmsCount=0;      
			newSmsCount=0;          
			findNewMmsCount(ctx);  
			findNewSmsCount(ctx); 
			if(Gdx.graphics != null)
				Gdx.graphics.requestRendering();
			Log.v("launcher","newMmsCount:" + newMmsCount + " newSmsCount:" + newSmsCount);  
		}  
	}
}
	