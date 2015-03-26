package com.iLoong.launcher.Desktop3D.APageEase;


import java.util.Map;
import java.util.TreeMap;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.miui.WorkspaceEditView;


public class APageEase
{
	
	public static final int COOLTOUCH_EFFECT_DEFAULT = 0; //默认
	public static final int COOLTOUCH_EFFECT_RANDOM = 1; //随机
	public static final int COOLTOUCH_EFFECT_SEQUENCE = 2; //顺序
	public static final int COOLTOUCH_EFFECT_BINARIES = 3;//双子�? OK
	public static final int COOLTOUCH_EFFECT_BLIND = 4; //百叶�? OK
	public static final int COOLTOUCH_EFFECT_FAN = 5; //风车0 OK
	public static final int COOLTOUCH_EFFECT_BIGFAN = 6; //大风�?OK
	public static final int COOLTOUCH_EFFECT_TORNADO = 7; //龙卷�? Modify to box OK
	public static final int COOLTOUCH_EFFECT_WHEEL = 8; //翻滚1 OK
	public static final int COOLTOUCH_EFFECT_CYLINDER = 9;//圆柱4 OK
	public static final int COOLTOUCH_EFFECT_BALL = 10; //球体4  OK
	public static final int COOLTOUCH_EFFECT_CROSS = 11; //交叉1 OK
	public static final int COOLTOUCH_EFFECT_JUMP = 12; //弹跳0 OK
	public static final int COOLTOUCH_EFFECT_MELT = 13; //溶解0 OK
	public static final int COOLTOUCH_EFFECT_UPRIGHT = 14; //垂直0 OK
	public static final int COOLTOUCH_EFFECT_FLIP = 15; //翻转 4 OK
	public static final int COOLTOUCH_EFFECT_SCROLL = 16; //滚轮 1 OK
	public static final int COOLTOUCH_EFFECT_ROTATE = 17; //旋转 1 OK
	public static final int COOLTOUCH_EFFECT_WAVE = 18; //波浪0 OK
	public static final int COOLTOUCH_EFFECT_PRESS = 19; //挤压0 OK
	public static final int COOLTOUCH_EFFECT_INBOX = 20; //内盒�?
	public static final int COOLTOUCH_EFFECT_FADEIN = 21; //淡入
	public static final int COOLTOUCH_EFFECT_OUTBOX = 22; //外盒�?
	public static final int COOLTOUCH_EFFECT_GS3 = 23; //GS3高仿默认效果
	//teapotXu_20130306 add start: adding new effects
	public static final int COOLTOUCH_EFFECT_ROLL = 24; //卷帘
	public static final int COOLTOUCH_EFFECT_WINDOW = 25; //窗户
	public static final int COOLTOUCH_EFFECT_ERASE = 26; //擦除
	public static final int COOLTOUCH_EFFECT_WIND = 27; //暴风
	public static final int COOLTOUCH_EFFECT_HUMP = 28; //凸起
	public static final int COOLTOUCH_EFFECT_ELECTRIC_FAN = 29; //风扇
	public static final int COOLTOUCH_EFFECT_CERAUNITE = 30; //陨石
	public static final int COOLTOUCH_EFFECT_ELASTICITY = 31; //弹性
	public static final int COOLTOUCH_EFFECT_SNAKE = 32; //贪吃蛇
	public static final int COOLTOUCH_EFFECT_S4_DRAG = 33;
	public static final int COOLTOUCH_EFFECT_CRYSTAL = 34; // 水晶切页
	public static final int COOLTOUCH_EFFECT_MAX = 35;
	public static Map<String , Integer> mEffectMap = new TreeMap<String , Integer>();
	//add scroll direction
	public static boolean is_scroll_from_right_to_left = false;
	//标识是否是touchup 才播放的动画
	public static boolean is_anim_effect_shown_whn_touch_up = false;
	public static float original_degree_whn_touch_up = 0f;
	//teapotXu_20130306: add end
	public static boolean is_standard = false;
	public static boolean is_scroll_anim_in_eidt_mode;
	
	public static void initEffectMap()
	{
		mEffectMap.clear();
		mEffectMap.put( R3D.getString( RR.string.effect_standard ) , COOLTOUCH_EFFECT_DEFAULT );
		mEffectMap.put( R3D.getString( RR.string.effect_random ) , COOLTOUCH_EFFECT_RANDOM );
		mEffectMap.put( R3D.getString( RR.string.effect_cascade ) , COOLTOUCH_EFFECT_FADEIN );
		mEffectMap.put( R3D.getString( RR.string.effect_inbox ) , COOLTOUCH_EFFECT_INBOX );
		mEffectMap.put( R3D.getString( RR.string.effect_outbox ) , COOLTOUCH_EFFECT_OUTBOX );
		mEffectMap.put( R3D.getString( RR.string.effect_flip ) , COOLTOUCH_EFFECT_FLIP );
		mEffectMap.put( R3D.getString( RR.string.effect_bigfan ) , COOLTOUCH_EFFECT_BIGFAN );
		mEffectMap.put( R3D.getString( RR.string.effect_fan ) , COOLTOUCH_EFFECT_FAN );
		mEffectMap.put( R3D.getString( RR.string.effect_wave ) , COOLTOUCH_EFFECT_WAVE );
		mEffectMap.put( R3D.getString( RR.string.effect_wheel ) , COOLTOUCH_EFFECT_WHEEL );
		mEffectMap.put( R3D.getString( RR.string.effect_ball ) , COOLTOUCH_EFFECT_BALL );
		mEffectMap.put( R3D.getString( RR.string.effect_cylinder ) , COOLTOUCH_EFFECT_CYLINDER );
		mEffectMap.put( R3D.getString( RR.string.effect_default ) , COOLTOUCH_EFFECT_GS3 );
		mEffectMap.put( R3D.getString( RR.string.effect_crystal ) , COOLTOUCH_EFFECT_CRYSTAL ); //水晶切页
		//teapotXu add start for pages effects in Mainmenu
		if( DefaultLayout.external_applist_page_effect )
		{
			// below effects are all effects that is debug OK
			mEffectMap.put( R3D.getString( RR.string.effect_binaries ) , COOLTOUCH_EFFECT_BINARIES ); //双子星
			mEffectMap.put( R3D.getString( RR.string.effect_blind ) , COOLTOUCH_EFFECT_BLIND ); //百叶窗
			mEffectMap.put( R3D.getString( RR.string.effect_jump ) , COOLTOUCH_EFFECT_JUMP ); //弹跳
			mEffectMap.put( R3D.getString( RR.string.effect_melt ) , COOLTOUCH_EFFECT_MELT ); //溶解
			mEffectMap.put( R3D.getString( RR.string.effect_upright ) , COOLTOUCH_EFFECT_UPRIGHT ); //垂直
			mEffectMap.put( R3D.getString( RR.string.effect_rotate ) , COOLTOUCH_EFFECT_SCROLL ); //旋转
			mEffectMap.put( R3D.getString( RR.string.effect_press ) , COOLTOUCH_EFFECT_PRESS );//挤压
			mEffectMap.put( R3D.getString( RR.string.effect_roll ) , COOLTOUCH_EFFECT_ROLL );//卷帘
			mEffectMap.put( R3D.getString( RR.string.effect_window ) , COOLTOUCH_EFFECT_WINDOW );//窗户
			mEffectMap.put( R3D.getString( RR.string.effect_tornado ) , COOLTOUCH_EFFECT_TORNADO );//龙卷风
			mEffectMap.put( R3D.getString( RR.string.effect_erase ) , COOLTOUCH_EFFECT_ERASE );//擦除
			mEffectMap.put( R3D.getString( RR.string.effect_wind ) , COOLTOUCH_EFFECT_WIND );//风暴
			mEffectMap.put( R3D.getString( RR.string.effect_hump ) , COOLTOUCH_EFFECT_HUMP );//凸起
			mEffectMap.put( R3D.getString( RR.string.effect_cross ) , COOLTOUCH_EFFECT_CROSS );//交叉
			mEffectMap.put( R3D.getString( RR.string.effect_electric_fan ) , COOLTOUCH_EFFECT_ELECTRIC_FAN );//风扇  
			mEffectMap.put( R3D.getString( RR.string.effect_ceraunite ) , COOLTOUCH_EFFECT_CERAUNITE );//陨石
			mEffectMap.put( R3D.getString( RR.string.effect_elasticity ) , COOLTOUCH_EFFECT_ELASTICITY );//弹性 
			mEffectMap.put( R3D.getString( RR.string.effect_snake ) , COOLTOUCH_EFFECT_SNAKE );//贪吃蛇
		}
		//teapotXu_20130306: add end
	}
	
	public static void setStandard(
			boolean standard )
	{
		is_standard = standard;
	}
	
	//teapotXu_20130307: add start
	public static void setScrolldirection(
			boolean is_right_to_left )
	{
		is_scroll_from_right_to_left = is_right_to_left;
	}
	
	// set the status of is_anim_effect_shown_whn_touch_up
	public static void setTouchUpAnimEffectStatus(
			boolean is_touch_up_effect )
	{
		is_anim_effect_shown_whn_touch_up = is_touch_up_effect;
	}
	
	// save the original degree when touch up
	public static void saveDegreeInfoWhnTouchUp(
			float degree_touch_up )
	{
		original_degree_whn_touch_up = degree_touch_up;
	}
	
	//teapotXu_20130307: add end
	public static void updateEffect(
			ViewGroup3D pre_view ,
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			int type )
	{
		//		Log.v("APageEase", "updateEffect");	
		//		Log.v("APageEase", "cur_name="+cur_view.getChildAt(0).name);		
		//		Log.v("APageEase", "next_name="+next_view.getChildAt(0).name);
		float this_width = cur_view.getWidth();
		if( degree == 0 )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				if( cur_view != null )
				{
					cur_view.show();
				}
				if( next_view != null )
				{
					next_view.show();
				}
			}
			else
			{
				if( next_view != null )
				{
					next_view.hide();
				}
				if( cur_view != null )
				{
					cur_view.show();
				}
			}
		}
		else
		{
			if( cur_view != null )
			{
				cur_view.show();
			}
			if( next_view != null )
			{
				next_view.show();
			}
		}
		if( pre_view == cur_view )
		{
			if( DefaultLayout.workspace_npages_circle_scroll_config || ( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) || DefaultLayout.drawer_npages_circle_scroll_config )
			{
				if( is_scroll_anim_in_eidt_mode )
				{
					pre_view = null;
				}
				else
				{
					if( is_scroll_from_right_to_left )
					{
						pre_view = null;
					}
					else
					{
						next_view = null;
					}
				}
			}
		}
		if( pre_view == next_view )
		{
			if( DefaultLayout.workspace_npages_circle_scroll_config || ( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) || DefaultLayout.drawer_npages_circle_scroll_config )
			{
				if( is_scroll_anim_in_eidt_mode )
				{
					pre_view = null;
				}
				else
				{
					if( is_scroll_from_right_to_left )
					{
						next_view = null;
					}
					else
					{
						pre_view = null;
					}
				}
			}
		}
		if( next_view == cur_view )
		{
			//teapotXu add start for workspace circle scroll config
			if( DefaultLayout.workspace_npages_circle_scroll_config || ( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) || DefaultLayout.drawer_npages_circle_scroll_config //teapotXu added
			)
			{
				if( is_scroll_from_right_to_left )
				{
					next_view = null;
				}
				else
				{
					cur_view = null;
				}
			}
			else
			{
				type = COOLTOUCH_EFFECT_DEFAULT;
			}
			//type = COOLTOUCH_EFFECT_DEFAULT;
			//teapotXu add end
		}
		//		else if(cur_view instanceof GridView3D)
		//			type = COOLTOUCH_EFFECT_MELT;
		switch( type )
		{
			case COOLTOUCH_EFFECT_DEFAULT:
			default:
				Default.updateEffect( pre_view , cur_view , next_view , degree , yScale , this_width , is_standard );
				break;
			case COOLTOUCH_EFFECT_BINARIES:
				Binaries.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_FAN:
				Fan.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_WHEEL:
				Wheel.updateEffect( (GridView3D)cur_view , (GridView3D)next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_BLIND:
				//Default.updateEffect(cur_view, next_view, degree, this_width);
				Blind.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			//		case COOLTOUCH_EFFECT_CHORD:
			//			//Default.updateEffect(cur_view, next_view, degree, this_width);
			//			Chord.updateEffect(cur_view, next_view, degree, this_width);
			//			break;
			//		case COOLTOUCH_EFFECT_TILT:
			//			Tilt.updateEffect(cur_view, next_view, degree, this_width);
			//			break;
			case COOLTOUCH_EFFECT_BIGFAN:
				Bigfan.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_TORNADO:
				Tornado.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_CYLINDER:
				Cylinder.updateEffect( (GridView3D)cur_view , (GridView3D)next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_BALL:
				Ball.updateEffect( (GridView3D)cur_view , (GridView3D)next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_CROSS:
				Cross.updateEffect( (GridView3D)cur_view , (GridView3D)next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_JUMP:
				Jump.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_MELT:
				Melt.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_UPRIGHT:
				Upright.updateEffect( cur_view , next_view , degree , this_width );
				break;
			case COOLTOUCH_EFFECT_FLIP:
				Flip.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_SCROLL:
				Scroll.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_ROTATE:
				Rotate.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_WAVE:
				Wave.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_PRESS:
				Press.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_OUTBOX:
				Outbox.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_INBOX:
				Inbox.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_FADEIN:
				FadeIn.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_GS3:
				GS3.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			//teapotXu_20130306 add start: adding new effects
			//add new effects
			case COOLTOUCH_EFFECT_CERAUNITE:
				Ceraunite.updateEffect( cur_view , next_view , degree , yScale , this_width , is_scroll_from_right_to_left );
				break;
			case COOLTOUCH_EFFECT_ELASTICITY:
				Elasticity.updateEffect( cur_view , next_view , degree , original_degree_whn_touch_up , yScale , this_width , is_anim_effect_shown_whn_touch_up );
				break;
			case COOLTOUCH_EFFECT_WINDOW:
				Window.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_ROLL:
				Roll.updateEffect( cur_view , next_view , degree , yScale , this_width , is_scroll_from_right_to_left );
				break;
			case COOLTOUCH_EFFECT_ELECTRIC_FAN:
				ElectricFan.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_ERASE:
				Erase.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_WIND:
				Wind.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			case COOLTOUCH_EFFECT_HUMP:
				Hump.updateEffect( cur_view , next_view , degree , yScale , this_width , is_scroll_from_right_to_left );
				break;
			case COOLTOUCH_EFFECT_SNAKE:
				Snake.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
			//teapotXu_20130306: add end
			case COOLTOUCH_EFFECT_CRYSTAL:
				Crystal.updateEffect( cur_view , next_view , degree , yScale , this_width , is_scroll_from_right_to_left );
				break;
			case COOLTOUCH_EFFECT_S4_DRAG: // 仅用于拖动图标时桌面的滑动
				GS4Drag.updateEffect( cur_view , next_view , degree , yScale , this_width );
				break;
		}
		if( type == COOLTOUCH_EFFECT_S4_DRAG )
		{
			if( cur_view instanceof CellLayout3D )
			{
				( (CellLayout3D)cur_view ).hl_x = -degree * this_width;
				( (CellLayout3D)cur_view ).hl_u0 = 0.0f;
				( (CellLayout3D)cur_view ).hl_u1 = degree + 1;
				( (CellLayout3D)cur_view ).hl_w = ( degree + 1 ) * this_width;
				( (CellLayout3D)cur_view ).alpha = 1.0f;
			}
			if( next_view instanceof CellLayout3D )
			{
				( (CellLayout3D)next_view ).hl_x = 0;
				( (CellLayout3D)next_view ).hl_u0 = degree + 1;
				( (CellLayout3D)next_view ).hl_u1 = 1.0f;
				( (CellLayout3D)next_view ).hl_w = -degree * this_width;
				( (CellLayout3D)next_view ).alpha = 1.0f;
			}
		}
		else
		{
			if( cur_view instanceof CellLayout3D )
			{
				( (CellLayout3D)cur_view ).hl_x = -degree * this_width;
				( (CellLayout3D)cur_view ).hl_u0 = 0.0f;
				( (CellLayout3D)cur_view ).hl_u1 = degree + 1;
				( (CellLayout3D)cur_view ).hl_w = ( degree + 1 ) * this_width;
				( (CellLayout3D)cur_view ).alpha = 1 - Math.abs( degree );
			}
			if( next_view instanceof CellLayout3D )
			{
				( (CellLayout3D)next_view ).hl_x = 0;
				( (CellLayout3D)next_view ).hl_u0 = degree + 1;
				( (CellLayout3D)next_view ).hl_u1 = 1.0f;
				( (CellLayout3D)next_view ).hl_w = -degree * this_width;
				( (CellLayout3D)next_view ).alpha = Math.abs( degree );
			}
		}
	}
	
	public static void updateEffect(
			ViewGroup3D pre_view ,
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			boolean is_thumbnail )
	{
		float this_width = cur_view.getWidth();
		cur_view.show();
		if( !( pre_view instanceof WorkspaceEditView ) )
		{
			pre_view.show();
		}
		if( !( next_view instanceof WorkspaceEditView ) )
		{
			next_view.show();
		}
		GS4.updateEffect( pre_view , cur_view , next_view , degree , this_width , is_thumbnail );
	}
}
