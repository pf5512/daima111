package com.iLoong.Calender.view;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Calender.common.CalenderEvent;
import com.iLoong.Calender.common.CalenderHelper;
import com.iLoong.Calender.common.Parameter;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class AddEventGroup extends ViewGroup3D
{
	
	private MainAppContext maincontext = null;
	private Context context=null;
	private final float OrigonToBigXianKuangWidth = Parameter.Origin_To_Origin_Width + Parameter.Calender_BianKuang_Width;
	private final float OrigonToBigXianKuangHeight = Parameter.Origin_To_Origin_Height + Parameter.Calender_BianKuang_Height;
	private final float OrigonToSmallXianKuangWidth = Parameter.Origin_To_Origin_Width + Parameter.Calender_Little_BianKuang_Width;
	private final float OrigonToSmallXianKuangHeight = Parameter.Origin_To_Origin_Height + Parameter.Calender_Little_BianKuang_Height;
	private float nowHeight;
	private float nowWidth;
	private final float BigMoveOff = ( WidgetCalender.scale - WidgetCalender.height_scale ) / 2 * Parameter.BigBackgroundHeight;
	private final float LittleMoveOff = ( WidgetCalender.scale - WidgetCalender.height_scale ) / 2 * Parameter.BianKuang_Height;
	private final float moveDistance = Parameter.BianKuang_Height + Parameter.BianKuang_To_Top;
	public static float kuangoffset = 1.0f;
	private float moveHeight;
	private float addscale = WidgetCalender.scale;
	private float addHeight_scale = WidgetCalender.height_scale;
	public static int year;
	public static int month;
	public static int day;
	
	public AddEventGroup(
			String name ,
			MainAppContext maincontext ,
			int year ,
			int month ,
			int day,Context context )
	{
		super( name );
		this.maincontext = maincontext;
		this.context=context;
		this.year = year;
		this.month = month;
		this.day = day;
		addscale = WidgetCalender.scale;
		addHeight_scale = WidgetCalender.height_scale;
		nowHeight = Parameter.Calender_Day_Glass_Height * addHeight_scale * 5;
		nowWidth=Parameter.Calender_Day_Glass_Width*addscale*7;
		kuangoffset = ( nowHeight - 142f ) / ( 112 * 4f );
		moveHeight = ( Parameter.Calender_Workspace_Bottom + Parameter.Calender_Bottom_JinShuTiao ) * addscale + ( 112 / 2 ) * kuangoffset;
		DayGlasses dayglasses = new DayGlasses( maincontext , "dayglasses" , getRegion( "background.png" ) , "mod_add_glass.obj" );
		dayglasses.build();
		dayglasses.move( OrigonToBigXianKuangWidth * addscale , OrigonToBigXianKuangHeight * addscale - BigMoveOff , 0f );
		this.addView( dayglasses );
		init(year,month,day);
	}
	
	private ListViewMove3D lv;
	private	EventGroup addEvent;
	private void init(int nowyear,int nowmonth,int nowday)
	{
		String nowstartTime = CalenderHelper.DateToLong( nowyear , nowmonth , nowday , 0 , 0 , 0 ) + "";
		String nowendTime = CalenderHelper.DateToLong( nowyear , nowmonth , nowday , 23 , 59 , 59 ) + "";
		List<CalenderEvent> allEvents = CalenderHelper.QueryAllCalendar( maincontext.mContainerContext.getContentResolver());
		List<CalenderEvent> needEvents=new ArrayList<CalenderEvent>();
		Long a=Long.parseLong( nowstartTime );
		Long b=Long.parseLong( nowendTime );
		for( int i = 0 ; i < allEvents.size() ; i++ )
		{
			String startTime=allEvents.get( i ).getDtstart();
			String endTime=allEvents.get( i ).getDtend();
			Long x=Long.parseLong( startTime );
			Long y=Long.parseLong( endTime );
			
			if((x>a&&y<b)||(x<a&&y>b)||(x<a&&y>a&&y<b)||(x>a&&x<b&&y>b)){
				needEvents.add( allEvents.get( i ) );
			}
		}
		
		
		
		
		if( needEvents.size() == 0 )
		{
			addEvent = new EventGroup( "addEvent" ,maincontext,context);
			AddGlasses jiahao = new AddGlasses( maincontext , "jiahao" , getRegion( "jiahao.png" ) , "mod_add.obj" );
			jiahao.build();
			jiahao.move( Parameter.Calender_Workspace_Left * addscale+(Parameter.JiaHao_Width/2)*kuangoffset, moveHeight , 0f );
			addEvent.addView( jiahao );
			addEvent.setSize( (Parameter.JiaHao_Width+30) * addHeight_scale , (Parameter.JiaHao_Height+30) * addHeight_scale );
			addEvent.setPosition( nowWidth/2 -Parameter.JiaHao_Width/2*addHeight_scale, nowHeight/2 );
			this.addView( addEvent );
		}else{
			lv = new ListViewMove3D( "lv" );
			lv.setSize( Parameter.BigBackgroundWidth * addscale , Parameter.BigBackgroundHeight * addHeight_scale );
			lv.setPosition( 0 , 0 );
			lv.paddingBottom = 30f * addHeight_scale;
			
			for( int i = 0 ; i < needEvents.size() ; i++ )
			{
				EventGroup dg = new EventGroup(needEvents.get( i ).get_id() ,maincontext,context);
				EventGlasses string = new EventGlasses( maincontext , "string" , getTextureRegion( maincontext , needEvents.get( i ).getTitle(),CalenderHelper.LongToDate( Long.parseLong( needEvents.get( i ).getDtstart() ) )+"-"+CalenderHelper.LongToDate( Long.parseLong( needEvents.get( i ).getDtend()) ) ) , "mod_add01.obj" );
				string.build();
				string.move( OrigonToSmallXianKuangWidth * addscale , moveHeight , 0f );
				dg.addView( string );
				dg.setSize( Parameter.BianKuang_Width * addscale , Parameter.BianKuang_Height * kuangoffset + 30 * addHeight_scale );
				lv.addItem( dg );
			}
			
			EventGroup addEvent = new EventGroup( "addEvent" ,maincontext,context);
			AddGlasses jiahao = new AddGlasses( maincontext , "jiahao" , getRegion( "jiahao.png" ) , "mod_add.obj" );
			jiahao.build();
			jiahao.move( OrigonToSmallXianKuangWidth * addscale, moveHeight , 0f );
			addEvent.addView( jiahao );
			addEvent.setSize( Parameter.BianKuang_Width * addscale , Parameter.JiaHao_Height * addHeight_scale +30 * addHeight_scale);
			lv.addItem( addEvent );
			this.addView( lv );
		}
		
	}
	
	private TextureRegion getRegion(
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( "theme/widget/cometcalendar/comet/image/" + name ) ) ,true);
			bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			TextureRegion mBackRegion = new TextureRegion( bt );
			return mBackRegion;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public TextureRegion getTextureRegion(
			MainAppContext appContext ,
			String title,String time)
	{
		Bitmap backImage = null;
		float width = Parameter.BianKuang_Width * addscale;
		float height = Parameter.BianKuang_Height * kuangoffset;
		float Titletextsize = 35f * kuangoffset;
		float Timetextsize = 20f * kuangoffset;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );//.TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );//防锯齿
		paint.setDither( true );//防抖动
		paint.setARGB( 255 , 82 , 178 , 255 );
		paint.setSubpixelText( true );
		paint.setStrokeJoin( Paint.Join.ROUND );
		paint.setStrokeCap( Paint.Cap.ROUND );
		paint.setTextSize( Titletextsize );
		paint.setShadowLayer( (float)( 4 / 1.7 ) , (float)( 2 / 1.7 ) , (float)( 2 / 1.7 ) , Color.BLACK );//Color.BLACK
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		Bitmap bt = null;
		Bitmap newbt = null;
		try
		{
			bt = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( "theme/widget/cometcalendar/comet/image/" + "changtiao.png" ) );
			newbt = resizeBitmap( bt , (int)width , (int)height );
			canvas.drawBitmap( newbt , 0f , 0f , paint );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		if( paint.measureText( title ) > width - 15*addscale )
		{
			while( paint.measureText( title ) > width - paint.measureText( "..." ) - 2*addscale - 26 * addscale )
			{
				title = title.substring( 0 , title.length() - 1 );
			}
			title += "...";
		}
		canvas.drawText( title , 26 * addscale , posY-10*kuangoffset , paint );
		
		paint.setTextSize( Timetextsize );
		canvas.drawText( time , 590*addscale-paint.measureText( time )-12 * addscale , posY+25*kuangoffset , paint );
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		backImage.recycle();
		bt.recycle();
		newbt.recycle();
		return newTextureRegion;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		float k = 0;
		if( deltaY != 0 && deltaY != 0 )
		{
			k = deltaY / deltaX;
		}
		if( k > 1.7 || k < -1.7 )
		{
			return true;
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	private Bitmap resizeBitmap(
			Bitmap bm ,
			int w ,
			int h )
	{
		Bitmap BitmapOrg = bm;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		if( width == w && height == h )
			return bm;
		float scaleWidth = ( (float)w ) / width;
		float scaleHeight = ( (float)h ) / height;
		Matrix matrix = new Matrix();
		matrix.postScale( scaleWidth , scaleHeight );
		Bitmap tmp = Bitmap.createBitmap( BitmapOrg , 0 , 0 , width , height , matrix , true );
		bm.recycle();
		return tmp;
	}
	public void updateNowEvents(int newyear,int newmonth,int newday){
		String nowstartTime = CalenderHelper.DateToLong( newyear , newmonth , newday , 0 , 0 , 0 ) + "";
		String nowendTime = CalenderHelper.DateToLong( newyear , newmonth , newday , 23 , 59 , 59 ) + "";
		List<CalenderEvent> allEvents = CalenderHelper.QueryAllCalendar( maincontext.mContainerContext.getContentResolver());
		List<CalenderEvent> newneedEvents=new ArrayList<CalenderEvent>();
		Long a=Long.parseLong( nowstartTime );
		Long b=Long.parseLong( nowendTime );
		for( int i = 0 ; i < allEvents.size() ; i++ )
		{
			String startTime=allEvents.get( i ).getDtstart();
			String endTime=allEvents.get( i ).getDtend();
			Long x=Long.parseLong( startTime );
			Long y=Long.parseLong( endTime );
			
			if((x>a&&y<b)||(x<a&&y>b)||(x<a&&y>a&&y<b)||(x>a&&x<b&&y>b)){
				newneedEvents.add( allEvents.get( i ) );
			}
		}
		if(lv!=null&&newneedEvents.size()!=0){
			lv.removeAllViews();
			for( int i = 0 ; i < newneedEvents.size() ; i++ )
			{
				EventGroup dg = new EventGroup(newneedEvents.get( i ).get_id() ,maincontext,context);
				EventGlasses string = new EventGlasses( maincontext , "string" , getTextureRegion( maincontext , newneedEvents.get( i ).getTitle(),CalenderHelper.LongToDate( Long.parseLong( newneedEvents.get( i ).getDtstart() ) )+"-"+CalenderHelper.LongToDate( Long.parseLong( newneedEvents.get( i ).getDtend()) ) ) , "mod_add01.obj" );
				string.build();
				string.move( OrigonToSmallXianKuangWidth * addscale , moveHeight , 0f );
				dg.addView( string );
				dg.setSize( Parameter.BianKuang_Width * addscale , Parameter.BianKuang_Height * kuangoffset + 30 * addHeight_scale );
				lv.addItem( dg );
			}
			
			EventGroup addEvent = new EventGroup( "addEvent" ,maincontext,context);
			AddGlasses jiahao = new AddGlasses( maincontext , "jiahao" , getRegion( "jiahao.png" ) , "mod_add.obj" );
			jiahao.build();
			jiahao.move( OrigonToSmallXianKuangWidth * addscale, moveHeight , 0f );
			addEvent.addView( jiahao );
			addEvent.setSize( Parameter.BianKuang_Width * addscale , Parameter.JiaHao_Height * addHeight_scale +30 * addHeight_scale);
			lv.addItem( addEvent );
		}else if(lv!=null&&newneedEvents.size()==0){
			this.removeView( lv );
			lv=null;
			init(newyear,newmonth,newday);
		}else if(lv==null&&newneedEvents.size()!=0){
			this.removeView( addEvent );
			init(newyear,newmonth,newday);
		}
	}
}
