package cool.sdk.update;


import java.util.Calendar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import cool.sdk.Category.CategoryHelper;
import cool.sdk.download.manager.DlMethod;
import cool.sdk.log.CoolLog;
import cool.sdk.statistics.StatisticsUpdate;


public class UpdateManagerImpl extends UpdateManager
{
	
	public static void Update(
			Context context )
	{
		try
		{
			CategoryHelper.getInstance( context ).Update();
		}
		catch( Exception e )
		{
		}
	}
	
	public static void UpdateSync(
			Context context )
	{
		try
		{
			CategoryHelper.getInstance( context ).UpdateSync( false );
		}
		catch( Exception e )
		{
		}
	}
	
	public static void UpdateOver(
			Context context )
	{
	}
	
	public static boolean allowUpdate(
			Context context )
	{
		return true;
	}
	
	//ME_RTFSC  [start]
	private static long PerUpdateTime = 0L;
	private static int UpdateCount = 0;
	private static long curDay = -1;
	
	public static Boolean IsNetworkAvailableLocal(
			Context context )
	{
		final ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo info = connMgr.getActiveNetworkInfo();
		if( info != null && info.isAvailable() )
		{
			return true;
		}
		return false;
	}
	
	//ME_RTFSC  [end]
	public static void Resume(
			final Context context )
	{
		final CoolLog Log = new CoolLog( context );
		if( !allowUpdate( context ) )
		{
			Log.v( "COOL" , "Resume allowUpdate = false" );
			return;
		}
		long day = SystemClock.elapsedRealtime() / ( 1000 * 60 * 60 * 24 );
		if( day != curDay )
		{
			curDay = day;
			//	if(UpdateCount >=5)
			{
				UpdateCount = 0;
			}
		}
		long time = ( SystemClock.elapsedRealtime() - PerUpdateTime );
		Log.v( "COOL" , "UpdateManagerImpl  Resume:UpdateCount=" + UpdateCount + " time=" + time / ( 1000 * 60 ) + ":" + time / 1000 % 60 );
		if( IsNetworkAvailableLocal( context ) && UpdateCount < 10 && SystemClock.elapsedRealtime() - PerUpdateTime > 1000 * 60 * 5 )
		{
			if( 0 != UpdateCount )
			{
				PerUpdateTime = SystemClock.elapsedRealtime();
				new Thread( new Runnable() {
					
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						try
						{
							Log.v( "COOL" , "========== SystemClock.elapsedRealtime() ===========" + SystemClock.elapsedRealtime() );
							Log.v( "COOL" , "========== UpdateSync  and  checkCategoryRecommend ===========" );
							if( DlMethod.IsWifiConnected( context ) && UpdateCount < 4 )
							{
								CategoryHelper.getInstance( context ).UpdateForSuccess( true );
								StatisticsUpdate.getInstance( context ).UpdateForSuccess( true );
							}
							else
							{
								/********
								 * 检测当天顯示次數
								 *********/
								StatisticsUpdate statisticsUpdate = StatisticsUpdate.getInstance( context );
								long thisDay = (long)Calendar.getInstance().get( Calendar.DAY_OF_YEAR );
								//当前Update的天
								long curUpdateDay = statisticsUpdate.getLong( "curUpdateDay" , 0L );
								//当前天Update次数
								long curDayUpdateCnt = statisticsUpdate.getLong( "curDayUpdateCnt" , 0L );
								Log.v( "COOL" , "check Success: thisDay=" + thisDay + " curUpdateDay=" + curUpdateDay + " curDayUpdateCnt=" + curDayUpdateCnt );
								if( curUpdateDay != thisDay )
								{
									//天改变
									curUpdateDay = thisDay;
									statisticsUpdate.setValue( "curUpdateDay" , curUpdateDay );
									curDayUpdateCnt = 0L;
									statisticsUpdate.setValue( "curDayUpdateCnt" , 0L );
								}
								if( curDayUpdateCnt < 2 )
								{
									statisticsUpdate.setValue( "curDayUpdateCnt" , curDayUpdateCnt + 1 );
									CategoryHelper.getInstance( context ).UpdateForSuccess( true );
									StatisticsUpdate.getInstance( context ).UpdateForSuccess( true );
								}
							}
							CategoryHelper.getInstance( context ).checkCategoryRecommend();
							// 防止以上2个函数运行时间太长， 导致RESUME 重入。放到开始位置。
							//PerUpdateTime = SystemClock.elapsedRealtime();
							Log.v( "ME_RTFSC" , "UpdateManager.UpdateSync: UpdateCount:" + UpdateCount + "  PerUpdateTime:" + PerUpdateTime );
							//CategoryHelper.getInstance( context ).checkCategoryRecommend();
						}
						catch( Exception e )
						{
							// TODO: handle exception
						}
					}
				} ).start();
			}
			UpdateCount += 1;
		}
	}
}
