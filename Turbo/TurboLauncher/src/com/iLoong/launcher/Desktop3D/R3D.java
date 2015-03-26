package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ComponentName;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.android.launcher.framework.IconCache;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapPacker;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class R3D
{
	
	// public static TextureAtlas img_pack;
	// public static TextureAtlas system_img_pack;
	// public static MyPixmapPacker packer;
	public static BitmapPacker packer;
	public static TextureAtlas packerAtlas;
	// public static Texture3D clockTexture;
	// public static Texture3D defVirCooeeStore;
	public static int Workspace_cell_each_width;
	public static int Workspace_cell_each_height;
	public static int Workspace_cell_each_width_ori;
	public static int Workspace_cell_each_height_ori;
	public static int Workspace_cellCountX;
	public static int Workspace_cellCountY;
	public static int Applist_cellCountX;
	public static int Applist_cellCountY;
	public static int Workspace_celllayout_toppadding;
	public static int Workspace_celllayout_bottompadding;
	public static int Workspace_celllayout_editmode_padding;
	// public static int celllayout_grid_height;
	public static int workspace_cell_width;
	public static int workspace_cell_height;
	public static float seatbar_hide_height;
	public static int workspace_cell_adjust;
	public static int workspace_multicon_width;
	public static int workspace_multicon_height;
	public static int workspace_paopao_big_width;
	public static int workspace_paopao_num_width;
	public static int workspace_paopao_num_height;
	public static int workspace_multiviews_offset;
	public static int icon_title_font;
	public static int reminder_font;
	public static int paopao_state_icon_padding_x;
	public static int paopao_state_icon_padding_y;
	public static int icon_and_text_spaceing = 0;
	public static int setupmenu_icon_and_text_spacing = 0;
	public static int setupmenu_icon_padding_top = 0;
	public static int appmenu_icon_padding_top = 0;
	public static int pageview_indicator_height;
	public static float widget_preview_title_weight = 0.25f;
	public static int workspace_editmode_optimum_widget_icon_size = 0;
	//teapotXu add start
	public static float setupmenu_square_item_height = 0;
	public static float setupmenu_android4_item_height = 0;
	public static int setupmenu_text_font_size = 0;
	public static int setupmenu_item_padding_y = 0;
	public static int setupmenu_items_btw_space = 0;
	//teapotXu add end
	public static String widget_otherTools_title;
	public static String widget_shortcut_title;
	public static String appbar_tab_app;
	public static String appbar_tab_widget;
	public static String appbar_tab_uninstall;
	//teapotXu add start for Folder in Mainmenu
	public static String appbar_tab_edit_mode;
	//teapotXu add end for Folder in Mainmenu	
	// zqh start
	public static String app_bar_navigator_audioalbum;
	public static String app_bar_navigator_video;
	// public static String app_bar_navigator_application;
	public static String app_bar_navigator_photobucket;
	public static String app_bar_title_audio;
	public static String app_bar_title_photo;
	// zqh end
	public static String appbar_tab_hide;
	public static int appbar_tab_color;
	public static int appbar_tab_select_color;
	public static int appbar_tab_pop_color;
	public static int appbar_tab_pop_select_color;
	public static int appbar_menu_color;
	public static int pop_menu_title_color;
	public static int appbar_height;
	// public static int appbar_tab_width;
	public static int appbar_tab_padding;
	public static int appbar_indicator_height;
	public static int icon_title_gap = -1;
	public static int appbar_home_right;
	public static int appbar_menu_right;
	public static int appbar_menu_width;
	public static int appbar_menu_height;
	public static int appbar_menuitem_height;
	public static int appbar_menuitem_width;
	public static int appbar_menuitem_paddingleft;
	public static float appbar_more_width;
	public static float appbar_tab_width;
	public static float appbar_tab_popitem_height;
	// public static int appbar_divider_width;
	public static int appbar_padding_left;
	public static int applist_padding_left;
	public static int applist_padding_right;
	public static int applist_padding_top;
	public static int applist_padding_bottom;
	public static int applist_indicator_y;
	public static int app_widget3d_gap;
	public static int sidebar_widget_w;
	public static int sidebar_widget_h;
	public static String[] app_list_string;
	public static String[] workSpace_list_string;
	public static String circle_autoSort;
	public static String circle_overLap;
	public static String circle_delAll;
	public static String circle_multiSelect;
	public static String circle_createFolder;
	public static String circle_DstOverToast;
	public static String circle_notSupportToast;
	public static String circle_selectMutiToOperToast;
	public static String circle_iconTrans;
	public static String circle_unselectAppIconToast;
	public static String iLoong_Name;
	public static int miui_widget_indicator_height;
	public static int trash_icon_width;
	public static int trash_icon_height;
	public static int toolbar_icon_region_width;
	public static int toolbar_icon_region_height;
	public static int toolbar_icon_region_y;
	public static int toolbar_title_region_y;
	public static int toolbar_height;
	public static int def_layout_y;
	public static int def_layout_y_dura;
	public static int pageselect_origin_x;
	public static int pageselect_origin_y;
	public static int page_indicator_radius;
	public static String pageselect_canNotAddWidgetToast;
	public static String pageselect_canNotDeletePage;
	public static int tip_point_width;
	public static int tip_point_height;
	public static int icongroup_button_height;
	public static int icongroup_button_width;
	public static int icongroup_round_radius;
	public static int circle_unfocus_backgroud_width;
	public static int circle_unfocus_backgroud_height;
	public static int circle_focus_backgroud_width;
	public static int circle_focus_backgroud_height;
	public static int circle_focus_offset_y;
	public static int circle_autosort_width;
	public static int circle_autosort_height;
	public static int circle_overlap_width;
	public static int circle_overlap_height;
	public static int circle_multiselect_width;
	public static int circle_multiselect_height;
	public static int circle_delall_width;
	public static int circle_delall_height;
	public static int circle_folder_width;
	public static int circle_folder_height;
	public static int circle_drawtext_x;
	public static int circle_drawtext_y;
	public static int pop_toast_width;
	public static int pop_toast_height;
	public static int icongroup_margin_left;
	public static int icongroup_margin_top;
	// public static int icongroup_cell_width;
	// public static int icongroup_cell_height;
	public static int icongroup_bottom_limit;
	public static String contact_name;
	public static String folder_name;
	public static int folder_num_width;
	public static int folder_num_height;
	public static int folder_num_offset_y;
	public static int folder_num_circle_width;
	public static int folder_front_width;
	public static int folder_front_height;
	public static int folder_back_width;
	public static int folder_back_height;
	public static int folder_gap_height;
	public static int workspace_cell_text_height;
	public static int android4_seekbar_padding_left = 0;
	public static int android4_seekbar_padding_top = 0;
	public static int android4_seekbar_padding_right = 0;
	public static int android4_seekbar_padding_bottom = 0;
	public static int folder_icon_row_num;
	public static int folder_icon_posy;
	public static int folder_icon_rotation_degree;
	public static int folder_icon_rotation_offsetx;
	public static int folder_icon_rotation_offsety;
	public static int folder_transform_num;
	public static int folder_group_left_margin;
	public static int folder_group_right_margin;
	public static int folder_group_top_margin;
	public static int folder_group_bottom_margin;
	public static int folder_group_left_round;
	public static int folder_group_top_round;
	public static int folder_group_right_round;
	public static int folder_group_bottom_round;
	public static int folder_group_text_height;
	public static int folder_group_text_round;
	public static int folder_max_num = 108;
	public static String folder_bg_name = "folder_bg";
	// public static int folder_title_font;
	public static String folder3D_name;
	public static String folder3D_full;
	public static int folder_icon_scale_factor;
	public static int trash_cap_x;
	public static int trash_cap_y;
	public static int trash_cap_w;
	public static int trash_cap_h;
	public static int icon_bg_num;
	// public static int icon_bg_width;
	// public static int icon_bg_height;
	public static int hot_dock_item_num;
	// teapotXu_20130206: add start
	// this variable is used to replace the one defined in DefaultLayout.java:
	// thirdapk_icon_scaleFactor
	public static int theme_thirdapk_icon_scaleFactor;
	// teapotXu_20130206: add end
	/** add for butterfly**/
	public static int page_edit_ygap234;
	public static int page_edit_xgap234;
	public static int page_edit_ygap567;
	public static int page_edit_xgap567;
	/**end**/
	public static int addList_item_text_color = 0;
	public static int folder_rename_text_color = 0;
	public static int vibrator_duration = 25;
	public static int hot_obj_origin_z;
	public static int hot_obj_height;
	public static int hot_obj_rot_deg;
	public static int hot_obj_trans_y;
	public static int hot_obj_trans_z;
	public static int hot_grid_left_margin;
	public static int hot_grid_right_margin;
	public static int hot_frontgrid_origin_z;
	public static int hot_dock_trans_y;
	public static int hot_grid_bottom_margin;
	public static int hot_dock_icon_size;
	public static int hot_sidebar_top_margin;
	public static int hot_dock_icon_number;
	public static int hot_top_ascent_distance_hide_title;
	public static int page_indicator_size;
	public static int page_indicator_y;
	public static int page_indicator_y_high;
	public static int page_indicator_focus_w;
	public static int page_indicator_normal_w;
	public static int page_indicator_focus_h;
	public static int page_indicator_normal_h;
	public static int page_indicator_style;
	public static int page_indicator_total_size;
	public static int s4_page_indicator_bg_height;
	public static int s4_page_indicator_scroll_width;
	public static int s4_page_indicator_scroll_height;
	public static int s4_page_indicator_number_bg_size;
	public static int s4_page_indicator_number_x_offset;
	public static int bottom_bar_height;
	public static int bottom_bar_title_height;
	public static int photo_width;
	public static int photo_height;
	public static int photo_padding;
	public static int photo_bucket_width;
	public static int photo_bucket_height;
	public static int photo_title_size;
	public static int photo_title_line;
	public static int audio_width;
	public static int audio_height;
	public static int audio_bottom_padding;
	public static int audio_left_padding;
	public static int audio_item_height;// xiatian add //explorer to adaptive
										// difference resolution
	public static int video_width;
	public static int video_height;
	public static int video_padding;
	public static TextureRegion unselectRegion;
	public static TextureRegion selectedRegion;
	private static iLoongLauncher launcher;
	public static TextureFilter filter = TextureFilter.Nearest;
	public static TextureFilter Magfilter = TextureFilter.Nearest;
	// xiatian add start //DownloadIcon
	public static int dynamic_menu_download_icon_width;
	public static int dynamic_menu_download_icon_height;
	// xiatian add end
	//xiatian add start	//New AppList Popmenu
	public static int applist_menu_color;
	public static int applist_menu_padding_top;
	public static int applist_menu_height;
	//xiatian add end
	//zhujieping add start
	public static TextureRegion screenBackRegion = null;
	public static int folder_opend_bg_alpha = 0xff;
	public static int folder_group_child_count_x;
	//zhujieping add end
	//xiatian add start	//adjust third apk icon offset when have iconbg
	public static int Third_APK_Icon_Offset_X;
	//	第三方图标的水平偏移量控制参数（当前主题有iconbg时，该参数生效）
	//		<0：相对于iconbg中心点，水平左移
	//		=0：相对于iconbg中心点，水平居中
	//		>0：相对于iconbg中心点，水平右移	
	public static int Third_APK_Icon_Offset_Y;
	//	第三方图标的垂直偏移量控制参数（当前主题有iconbg时，该参数生效）
	//		<0：相对于iconbg中心点，垂直下移
	//		=0：相对于iconbg中心点，垂直居中
	//		>0：相对于iconbg中心点，垂直上移	
	//xiatian add end
	//xiatian add start	//folder transform icon offset
	public static int Folder_Transform_Icon_Offset_X;
	//	文件夹关闭时的缩略图的水平偏移量控制参数
	//		<0：相对于该图中心点，水平左移
	//		=0：相对于该图中心点，水平居中
	//		>0：相对于该图中心点，水平右移
	public static int Folder_Transform_Icon_Offset_Y;
	//	文件夹关闭时的缩略图的垂直偏移量控制参数
	//		<0：相对于该图中心点，垂直下移
	//		=0：相对于该图中心点，垂直居中
	//		>0：相对于该图中心点，垂直上移
	//xiatian add end
	//xiatian add start //EffectPreview
	public final static String mEffectPreviewBgRegionName = "mEffectPreviewBgRegionName";
	public final static String mEffectPreviewButtonRegionName = "mEffectPreviewButtonRegionName";
	public final static String mEffectPreviewButtonFocusRegionName = "mEffectPreviewButtonFocusRegionName";
	public final static String mEffectPreviewSelectRegionName = "mEffectPreviewSelectRegionName";
	//xiatian add end
	public static List<String> replacedRecord = new ArrayList<String>();
	public static int pop_menu_height = 200;
	public static int pop_menu_indicator_height = 20;
	public static int pop_menu_indicator_bar_height = 40;
	public static int pop_menu_title_height = 10;
	public static int pop_menu_page_grid_padding_top = 10;
	public static int pop_menu_page_grid_padding_bottom = 10;
	public static int pop_menu_container_height = 240;
	public static int pop_menu_bottom_ind_height = 2;
	public static int pop_menu_bottom_ind_bar_height = 1;
	public static int workspace_offset_low_y;
	public static int workspace_offset_high_y;
	public static int workspace_celllayout_offset_y;
	public static int workspace_celllayout_bg_padding_top;
	public static int edit_mode_indicator_offset_4x2;
	public static int edit_mode_indicator_offset_4x1;
	public static int edit_mode_celllayout_offset;
	public static int app_pop_menu_padding_top;
	public static int app_pop_menu_padding_right;
	public static int app_pop_menu_item_height;
	public static int app_pop_menu_item_width;
	public static int app_pop_menu_item_gap;
	public static int app_pop_menu_img_size;
	public static int hot_dock_grid_pos_y = 0;
	public static int qs_letter_text_color;
	public static int qs_guess_title_text_color;
	public static int qs_app_list_item_title_text_color;
	public static int qs_no_search_result_tip_color;
	public static int qs_app_list_row_height;
	public static int qs_category_grid_padding_left;
	public static int qs_category_grid_padding_right;
	public static int qs_category_grid_padding_top;
	public static int qs_category_grid_padding_bottom;
	public static int qs_all_app_list_icon_text_color;
	
	public static String getInfoName(
			ShortcutInfo info )
	{
		ComponentName cmp = info.intent.getComponent();
		String name = "coco";
		if( cmp != null )
			name = cmp.toString();
		else
		{
			String temp = null;
			if( info.intent.getAction() != null )
			{
				name = info.intent.getAction().toString();
			}
			if( info.intent.getType() != null )
			{
				temp = info.intent.getType().toString();
				name += temp;
			}
			if( info.intent.getDataString() != null )
			{
				temp = info.intent.getDataString();
				name += temp;
			}
		}
		name += info.itemType;
		info.title = info.title.subSequence( 0 , info.title.length() );
		name += info.title.toString().replaceAll( " " , "" ).replaceAll( " " , "" ).trim();
		// name = info.intent.toString();
		return name;
	}
	
	public static String getString(
			int resID )
	{
		return iLoongLauncher.getInstance().getString( resID );
	}
	
	public static int getEffectResId(
			String effect_str )
	{
		int resId = 0;
		if( effect_str == null )
			return 0;
		if( effect_str.equals( "effect_standard" ) )
			resId = RR.string.effect_standard;
		else if( effect_str.equals( "effect_random" ) )
			resId = RR.string.effect_random;
		else if( effect_str.equals( "effect_cascade" ) )
			resId = RR.string.effect_cascade;
		else if( effect_str.equals( "effect_outbox" ) )
			resId = RR.string.effect_outbox;
		else if( effect_str.equals( "effect_inbox" ) )
			resId = RR.string.effect_inbox;
		else if( effect_str.equals( "effect_flip" ) )
			resId = RR.string.effect_flip;
		else if( effect_str.equals( "effect_bigfan" ) )
			resId = RR.string.effect_bigfan;
		else if( effect_str.equals( "effect_fan" ) )
			resId = RR.string.effect_fan;
		else if( effect_str.equals( "effect_wave" ) )
			resId = RR.string.effect_wave;
		else if( effect_str.equals( "effect_ball" ) )
			resId = RR.string.effect_ball;
		else if( effect_str.equals( "effect_wheel" ) )
			resId = RR.string.effect_wheel;
		else if( effect_str.equals( "effect_cylinder" ) )
			resId = RR.string.effect_cylinder;
		else if( effect_str.equals( "effect_default" ) )
			resId = RR.string.effect_default;
		else if( effect_str.equals( "effect_crystal" ) )
			resId = RR.string.effect_crystal;
		else if( effect_str.equals( "effect_binaries" ) )
			resId = RR.string.effect_binaries;
		else if( effect_str.equals( "effect_blind" ) )
			resId = RR.string.effect_blind;
		else if( effect_str.equals( "effect_jump" ) )
			resId = RR.string.effect_jump;
		else if( effect_str.equals( "effect_upright" ) )
			resId = RR.string.effect_upright;
		else if( effect_str.equals( "effect_rotate" ) )
			resId = RR.string.effect_rotate;
		else if( effect_str.equals( "effect_press" ) )
			resId = RR.string.effect_press;
		else if( effect_str.equals( "effect_roll" ) )
			resId = RR.string.effect_roll;
		else if( effect_str.equals( "effect_wind" ) )
			resId = RR.string.effect_wind;
		else if( effect_str.equals( "effect_hump" ) )
			resId = RR.string.effect_hump;
		else if( effect_str.equals( "effect_electric_fan" ) )
			resId = RR.string.effect_electric_fan;
		else if( effect_str.equals( "effect_elasticity" ) )
			resId = RR.string.effect_elasticity;
		else if( effect_str.equals( "effect_melt" ) )
			resId = RR.string.effect_melt;
		else if( effect_str.equals( "effect_window" ) )
			resId = RR.string.effect_window;
		else if( effect_str.equals( "effect_tornado" ) )
			resId = RR.string.effect_tornado;
		else if( effect_str.equals( "effect_erase" ) )
			resId = RR.string.effect_erase;
		else if( effect_str.equals( "effect_cross" ) )
			resId = RR.string.effect_cross;
		else if( effect_str.equals( "effect_ceraunite" ) )
			resId = RR.string.effect_ceraunite;
		else if( effect_str.equals( "effect_snake" ) )
			resId = RR.string.effect_snake;
		return resId;
	}
	
	public static boolean hasPack(
			String name )
	{
		if( R3D.packer == null )
			return false;
		if( R3D.packer.getRect( name ) != null )
			return true;
		return false;
	}
	
	public static boolean packHotseat(
			ShortcutInfo info ,
			boolean needAddBg )
	{
		return packHotseat( info , needAddBg , false );
	}
	
	public static boolean packHotseat(
			ShortcutInfo info ,
			boolean needAddBg ,
			boolean replace )
	{
		String suffix = "";
		Bitmap titleBg = Icon3D.titleBg;
		Bitmap replaceIcon = null;
		if( R3D.packer == null )
		{
			// Log.e("launcher", "pack=null");
			return false;
		}
		if( !replace )
		{
			if( R3D.packer.getRect( getInfoName( info ) + suffix ) != null && !R3D.packer.getRect( getInfoName( info ) + suffix ).isDefault )
			{
				Log.v( "launcher" , "hasPack" );
				return false;
			}
		}
		else
		{
			if( replacedRecord.contains( getInfoName( info ) + suffix ) )
			{
				Log.v( "launcher" , "hasPack" );
				return false;
			}
		}
		boolean isDefault = false;
		if( info.usingFallbackIcon && info.mIcon != null )
		{
			// Log.e("launcher", "hasPack");
			isDefault = true;
		}
		Bitmap bmp = info.getIcon( iLoongApplication.mIconCache );
		if( bmp == IconCache.mDefaultIcon )
		{
			// Log.e("launcher", "pack!!!!!!!!!!!!!!!!!!!!!");
			isDefault = true;
			info.usingFallbackIcon = true;
		}
		if( bmp.isRecycled() )
		{
			return false;
		}
		//sunyinwei add for default system shortcut start 20131031
		if( info.intent != null && info.intent.getComponent() != null && info.intent.getComponent().getPackageName() != null && info.intent.getComponent().getClassName() != null )
		{
			replaceIcon = DefaultLayout.getInstance().getDefaultShortcutIcon( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() );
			if( replaceIcon != null )
			{
				bmp = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
				info.setIcon( bmp );
				needAddBg = false;
			}
		}
		//sunyinwei add for default system shortcut end 20131031
		//teapotXu_20130328 add start: 增加判断是否是HotSeat Icon且更换了图标
		if( info.intent != null && info.hotseatDefaultIcon )
		{
			//needAddBg = false;
		}
		//if (info.intent==null ||info.intent.getComponent()==null||info.intent.getComponent().getPackageName()==null){
		else if( info.intent == null || info.intent.getComponent() == null || info.intent.getComponent().getPackageName() == null )
		{
			//teapotXu_20130328 add end 			
			if( doNotNeedScale( null , null ) )
			{
				needAddBg = false;
			}
			else
			{
				bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
				needAddBg = true;
			}
		}
		else if( !doNotNeedScale( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
		{
			if( !replace )
			{
				bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
			}
			needAddBg = true;
		}
		else
		{
			if( doNotNeedScale( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
			{
				needAddBg = false;
			}
			else
				needAddBg = true;
		}
		if( needAddBg )
		{
			if( replace )
			{
				repack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , Icon3D.getIconBg() , titleBg ) );
			}
			else
			{
				pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , Icon3D.getIconBg() , titleBg ) , false , isDefault );
			}
		}
		else
		{
			if( replace )
			{
				repack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , null , titleBg ) );
			}
			else
			{
				pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , null , titleBg ) , false , isDefault );
			}
		}
		// Log.e("launcher", "pack1:success");
		return true;
	}
	
	public static boolean pack(
			ShortcutInfo info ,
			String suffix ,
			boolean replace )
	{
		// Log.e("launcher", "pack1:"+getInfoName(info));
		Bitmap replaceIcon = null;
		Bitmap iconnewbmp;
		if( R3D.packer == null )
		{
			// Log.e("launcher", "pack=null");
			return false;
		}
		if( !replace )
		{
			if( R3D.packer.getRect( getInfoName( info ) + suffix ) != null && !R3D.packer.getRect( getInfoName( info ) + suffix ).isDefault )
			{
				Log.v( "launcher" , "hasPack" );
				return false;
			}
		}
		else
		{
			if( replacedRecord.contains( getInfoName( info ) + suffix ) )
			{
				Log.v( "launcher" , "hasPack" );
				//sunyinwei add for default system shortcut start 20131031
				if( info.intent != null && info.intent.getComponent() != null && info.intent.getComponent().getPackageName() != null && info.intent.getComponent().getClassName() != null )
				{
					replaceIcon = DefaultLayout.getInstance().getDefaultShortcutIcon( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() );
					if( replaceIcon != null )
					{
						iconnewbmp = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
						info.setIcon( iconnewbmp );
					}
				}
				//sunyinwei add for default system shortcut end 20131031
				return false;
			}
		}
		boolean isDefault = false;
		if( info.usingFallbackIcon && info.mIcon != null )
		{
			// Log.e("launcher", "hasPack");
			isDefault = true;
		}
		Bitmap bmp = info.getIcon( iLoongApplication.mIconCache );
		if( bmp == IconCache.mDefaultIcon )
		{
			// Log.e("launcher", "pack!!!!!!!!!!!!!!!!!!!!!");
			isDefault = true;
			info.usingFallbackIcon = true;
			bmp = IconCache.makeDefaultIcon();
		}
		if( bmp.isRecycled() )
		{
			return false;
		}
		// Log.e("launcher", "pack2:"+getInfoName(info));
		Bitmap bg;
		//sunyinwei add for default system shortcut start 20131031
		bg = Icon3D.getIconBg();
		if( info.intent != null && info.intent.getComponent() != null && info.intent.getComponent().getPackageName() != null && info.intent.getComponent().getClassName() != null )
		{
			replaceIcon = DefaultLayout.getInstance().getDefaultShortcutIcon( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() );
			if( replaceIcon != null )
			{
				bmp = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
				info.setIcon( bmp );
				bg = null;
			}
			else if( DefaultLayout.getInstance().hasSysShortcutIcon( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
			{
				replaceIcon = info.getIcon( iLoongApplication.mIconCache );
				if( replaceIcon != null )
				{
					bmp = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
					info.setIcon( bmp );
					bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
				}
			}
		}
		//sunyinwei add for default system shortcut end 20131031
		if( info.intent == null || info.intent.getComponent() == null || info.intent.getComponent().getPackageName() == null )
		{
			if( !doNotNeedScale( null , null ) )
			{
				if( info.intent == null || info.intent.getAction() == null )
				{
					if( Icon3D.getIconBg() != null && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
					{
						bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
						bg = Icon3D.getIconBg();
					}
					else
					{
						bg = null;
					}
				}
				else
				{
					if( Icon3D.getIconBg() != null && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT && !iLoongLauncher.getInstance().isDefaultHotseats( info.intent ) )
					{
						bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
						bg = Icon3D.getIconBg();
					}
					else
					{
						bg = null;
					}
				}
				if( replace )
				{
					repack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , bg , Icon3D.titleBg ) );
				}
				else
				{
					pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , bg , Icon3D.titleBg ) , false , isDefault );
				}
			}
			else
			{
				if( replace )
				{
					repack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , Icon3D.getIconBg() , Icon3D.titleBg ) );
				}
				else
				{
					pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , Icon3D.getIconBg() , Icon3D.titleBg ) , false , isDefault );
				}
			}
		}
		else
		{
			if( !doNotNeedScale( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
			{
				if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT && replaceIcon == null )
				{
					bmp = Tools.resizeBitmap( bmp , (int)( DefaultLayout.app_icon_size * 0.98f ) , (int)( DefaultLayout.app_icon_size * 0.98f ) );
				}
				if( replace )
				{
					repack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , bg , Icon3D.titleBg ) );
				}
				else
				{
					pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , bg , Icon3D.titleBg ) , false , isDefault );
				}
			}
			else
			{
				if( replace )
				{
					repack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , null , Icon3D.titleBg ) );
				}
				else
				{
					pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , null , Icon3D.titleBg ) , false , isDefault );
				}
			}
		}
		// Log.e("launcher", "pack1:success");
		return true;
	}
	
	public static boolean doNotNeedScale(
			String pname ,
			String compname )
	{
		boolean ret = false;
		if( Icon3D.getIconBg() == null )
		{
			return true;
		}
		if( pname != null && compname != null && pname.equals( "com.android.contacts" ) && compname.equals( "com.android.contacts.ContactShortcut" ) )
		{
			return true;
		}
		//sunyinwei add for default system shortcut start 20131031
		if( pname != null && compname != null )
		{
			if( DefaultLayout.getInstance().getDefaultShortcutIcon( pname , compname ) != null )
			{
				return true;
			}
		}
		//sunyinwei add for default system shortcut end 20131031
		if( pname != null )
		{
			ret = DefaultLayout.getInstance().hasReplaceIcon( pname , compname );
			//xiatian add start
			//fix bug:when a icon in replace list,but a theme do not have this icon.in the theme this icon use default Theme icon and not have iconBg and not scale
			if( ret )
			{
				String iconPath = DefaultLayout.getInstance().getReplaceIconPath( pname , compname );
				if( iconPath != null )
					ret = ThemeManager.getInstance().loadFromTheme( iconPath );
			}
			//xiatian add end
		}
		return ret;
	}
	
	public static boolean pack(
			ShortcutInfo info ,
			String suffix )
	{
		// Log.e("launcher", "pack1:"+getInfoName(info));
		return pack( info , suffix , false );
	}
	
	public static boolean pack(
			ShortcutInfo info )
	{
		return pack( info , "" );
	}
	
	public static void pack(
			ShortcutInfo info ,
			Bitmap p )
	{
		// Log.e("launcher", "pack2:"+getInfoName(info));
		pack( info , "" , p , true );
	}
	
	public static void pack(
			ShortcutInfo info ,
			String suffix ,
			Bitmap p )
	{
		// Log.e("launcher", "pack2:"+getInfoName(info));
		pack( info , suffix , p , true );
	}
	
	public static void pack(
			ShortcutInfo info ,
			String suffix ,
			Bitmap p ,
			boolean recycle )
	{
		// Log.e("launcher", "pack2:"+getInfoName(info));
		pack( getInfoName( info ) + suffix , p , recycle , false );
	}
	
	public static void pack(
			String name ,
			Bitmap p )
	{
		//		pack(name, Utils3D.bmp2Pixmap(p,recycle),isDefault);
		pack( name , p , true , false );
	}
	
	public static void pack(
			String name ,
			Bitmap p ,
			boolean recycle ,
			boolean isDefault )
	{
		//		pack(name, Utils3D.bmp2Pixmap(p,recycle),isDefault);
		packer.pack( name , p , isDefault );
		if( p != IconCache.mDefaultIcon && recycle && !p.isRecycled() )
			p.recycle();
	}
	
	public static void repack(
			String name ,
			Bitmap p )
	{
		if( p == null || p.isRecycled() )
		{
			Log.v( "repack" , "repack:null or recycled" );
			return;
		}
		R3D.packer.repack( name , p );
		replacedRecord.add( name );
		if( p != IconCache.mDefaultIcon && !p.isRecycled() )
			p.recycle();
	}
	
	public static AtlasRegion findRegion(
			ShortcutInfo info )
	{
		AtlasRegion ret = packerAtlas.findRegion( getInfoName( info ) );
		if( ret == null )
		{
			ret = packerAtlas.findRegion( getInfoName( info ) + "default" );
		}
		if( ret == null )
		{
			Log.e( "pack" , "region can not be found:" + getInfoName( info ) );
			ret = findRegion( "app-default-icon-with-title" );
		}
		return ret;
	}
	
	public static AtlasRegion findRegion(
			ShortcutInfo info ,
			String suffix )
	{
		AtlasRegion ret = packerAtlas.findRegion( getInfoName( info ) + suffix );
		if( ret == null )
		{
			ret = packerAtlas.findRegion( getInfoName( info ) + "default" + suffix );
		}
		if( ret == null )
		{
			Log.e( "pack" , "region can not be found:" + getInfoName( info ) );
			ret = findRegion( "app-default-icon-with-title" );
		}
		return ret;
	}
	
	static int findCount = 0;
	
	public static AtlasRegion findRegion(
			String name )
	{
		AtlasRegion ret = packerAtlas.findRegion( name );
		// if(ret == null)
		// ret = system_img_pack.findRegion(name);
		if( ret == null )
		{
			//zhongqihong修改，这个地方本来可以做下处理如果再次没搜索到就应该使用一张默认主题中的图片。
			//但是为了找到具体的原因，就先牺牲下用户让他去死吧，找到日志下载个版本中修改。
			if( findCount >= 10 )
			{
				throw new RuntimeException( "Error: unsuccessfully find region with name: " + name + " ---zqh loged." + "current theme name is: " + ThemeManager.getInstance()
						.getCurrentThemeDescription().title );
			}
			findCount++;
			ret = findRegion( "app-default-icon" );
			if( ret != null )
			{
				findCount = 0;
			}
		}
		// Log.v("find",(ret != null) + ":" + name );
		return ret;
	}
	
	public static AtlasRegion getTextureRegion(
			String name )
	{
		// AtlasRegion ret = img_pack.findRegion(name);
		// if(ret == null)
		// ret = system_img_pack.findRegion(name);
		// return ret;
		return findRegion( name );
	}
	
	public static int getInteger(
			final String s )
	{
		//xiatian add start	//adjust third apk icon offset when have iconbg
		if( ( s.equals( "third_apk_icon_offset_x" ) ) || ( s.equals( "third_apk_icon_offset_y" ) )
		//xiatian add start	//folder transform icon offset
		|| ( s.equals( "folder_transform_icon_offset_x" ) ) || ( s.equals( "folder_transform_icon_offset_y" ) )
		//xiatian add end
		)
		{
			return ThemeManager.getInstance().getSignedInteger( s );
		}
		//xiatian add end
		return ThemeManager.getInstance().getInteger( s );
	}
	
	public static String getString(
			final String s )
	{
		return ThemeManager.getInstance().getString( s );
	}
	
	public static void onThemeChanged()
	{
		replacedRecord.clear();
		trash_icon_width = getInteger( "trash_icon_width" );
		trash_icon_height = getInteger( "trash_icon_height" );
		//teapotXu add start for add new foler in top-trash bar 
		if( DefaultLayout.generate_new_folder_in_top_trash_bar && DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			toolbar_height = getInteger( "toolbar_height" );
			trash_icon_height = toolbar_height;
			toolbar_icon_region_width = getInteger( "toolbar_icon_region_width" );
			toolbar_icon_region_height = getInteger( "toolbar_icon_region_height" );
			toolbar_icon_region_y = getInteger( "toolbar_icon_region_y" );
			toolbar_title_region_y = getInteger( "toolbar_title_region_y" );
		}
		//teapotXu add end
		if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			workspace_cell_width = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_CELL_WIDTH );
			workspace_cell_height = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT );
		}
		else
		{
			workspace_cell_width = getInteger( "workspace_cell_width" );
			workspace_cell_height = getInteger( "workspace_cell_height" );
		}
		workspace_cell_adjust = getInteger( "workspace_cell_adjust" );
		pageview_indicator_height = getInteger( "pageview_indicator_height" );
		circle_drawtext_x = getInteger( "circle_drawtext_x" );
		circle_drawtext_y = getInteger( "circle_drawtext_y" );
		icongroup_button_height = getInteger( "icongroup_button_height" );
		icongroup_button_width = getInteger( "icongroup_button_width" );
		icongroup_round_radius = getInteger( "icongroup_round_radius" );
		circle_unfocus_backgroud_width = getInteger( "circle_unfocus_backgroud_width" );
		circle_unfocus_backgroud_height = getInteger( "circle_unfocus_backgroud_height" );
		circle_focus_backgroud_width = getInteger( "circle_focus_backgroud_width" );
		circle_focus_backgroud_height = getInteger( "circle_focus_backgroud_height" );
		circle_focus_offset_y = getInteger( "circle_focus_offset_y" );
		circle_autosort_width = getInteger( "circle_autosort_width" );
		circle_autosort_height = getInteger( "circle_autosort_height" );
		circle_overlap_width = getInteger( "circle_overlap_width" );
		circle_overlap_height = getInteger( "circle_overlap_height" );
		circle_multiselect_width = getInteger( "circle_multiselect_width" );
		circle_multiselect_height = getInteger( "circle_multiselect_height" );
		circle_delall_width = getInteger( "circle_delall_width" );
		circle_delall_height = getInteger( "circle_delall_height" );
		circle_folder_width = getInteger( "circle_folder_width" );
		circle_folder_height = getInteger( "circle_folder_height" );
		pop_toast_width = getInteger( "pop_toast_width" );
		pop_toast_height = getInteger( "pop_toast_height" );
		folder_num_width = getInteger( "folder_num_width" );
		folder_num_height = getInteger( "folder_num_height" );
		folder_num_circle_width = getInteger( "folder_num_circle_width" );
		folder_icon_rotation_degree = getInteger( "folder_icon_rotation_degree" );
		folder_icon_rotation_offsetx = getInteger( "folder_icon_rotation_offsetx" );
		folder_icon_rotation_offsety = getInteger( "folder_icon_rotation_offsety" );
		folder_num_offset_y = getInteger( "folder_num_offset_y" );
		folder_front_width = getInteger( "folder_front_width" );
		folder_back_width = getInteger( "folder_back_width" );
		folder_back_height = getInteger( "folder_back_height" );
		// folder_title_font = getInteger("folder_title_font");
		folder_gap_height = getInteger( "folder_gap_height" );
		folder_transform_num = getInteger( "folder_transform_num" );
		workspace_cell_text_height = getInteger( "workspace_cell_text_height" );
		folder_icon_row_num = getInteger( "folder_icon_row_num" );
		folder_icon_posy = getInteger( "folder_icon_posy" );
		folder_group_left_margin = getInteger( "folder_group_left_margin" );
		folder_group_right_margin = getInteger( "folder_group_right_margin" );
		folder_group_top_margin = getInteger( "folder_group_top_margin" );
		folder_group_bottom_margin = getInteger( "folder_group_bottom_margin" );
		folder_group_left_round = getInteger( "folder_group_left_round" );
		folder_group_top_round = getInteger( "folder_group_top_round" );
		folder_group_right_round = getInteger( "folder_group_right_round" );
		folder_group_bottom_round = getInteger( "folder_group_bottom_round" );
		folder_group_text_height = getInteger( "folder_group_text_height" );
		folder_group_text_round = getInteger( "folder_group_text_round" );
		folder_icon_scale_factor = getInteger( "folder_icon_scale_factor" );
		folder_group_child_count_x = getInteger( "folder_group_child_count_x" );
		paopao_state_icon_padding_x = getInteger( "paopao_state_icon_padding_x" );
		paopao_state_icon_padding_y = getInteger( "paopao_state_icon_padding_y" );
		icon_and_text_spaceing = getInteger( "icon_and_text_spaceing" );
		setupmenu_icon_and_text_spacing = getInteger( "setupmenu_icon_and_text_spacing" );
		//teapotXu add start
		R3D.android4_seekbar_padding_left = getInteger( "android4_seekbar_padding_left" );
		if( R3D.android4_seekbar_padding_left == -1 )
			R3D.android4_seekbar_padding_left = 0;
		R3D.android4_seekbar_padding_top = getInteger( "android4_seekbar_padding_top" );
		if( R3D.android4_seekbar_padding_top == -1 )
			R3D.android4_seekbar_padding_top = 0;
		R3D.android4_seekbar_padding_right = getInteger( "android4_seekbar_padding_right" );
		if( R3D.android4_seekbar_padding_right == -1 )
			R3D.android4_seekbar_padding_right = 0;
		R3D.android4_seekbar_padding_bottom = getInteger( "android4_seekbar_padding_bottom" );
		if( R3D.android4_seekbar_padding_bottom == -1 )
			R3D.android4_seekbar_padding_bottom = 0;
		//teapotXu add end
		//teapotXu add start
		workspace_editmode_optimum_widget_icon_size = getInteger( "workspace_editmode_optimum_widget_icon_size" );
		if( workspace_editmode_optimum_widget_icon_size == -1 )
		{
			if( DefaultLayout.show_icon_size )
			{
				if( DefaultLayout.show_icon_size_different_layout )
				{
					if( Utils3D.getScreenWidth() > 700 )
					{//大屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_big_screen_big_icon" );
					}
					else if( Utils3D.getScreenWidth() > 400 )
					{//中屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_normal_screen_big_icon" );
					}
					else
					{//小屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_small_screen_big_icon" );
					}
				}
				else
				{
					if( Utils3D.getScreenWidth() > 700 )
					{//大屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_big_screen_big_icon_same_layout" );
					}
					else if( Utils3D.getScreenWidth() > 400 )
					{//中屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_normal_screen_big_icon_same_layout" );
					}
					else
					{//小屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_small_screen_big_icon_same_layout" );
					}
				}
			}
			else
			{
				workspace_editmode_optimum_widget_icon_size = DefaultLayout.app_icon_size;
				if( workspace_editmode_optimum_widget_icon_size <= 0 )
				{
					workspace_editmode_optimum_widget_icon_size = (int)iLoongLauncher.getInstance().getResources().getDimension( android.R.dimen.app_icon_size );
				}
			}
		}
		//teapotXu add end
		if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			icon_title_font = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_TITLE_SIZE );
		}
		else
		{
			icon_title_font = Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_title_font );
		}
		//teapotXu add start
		setupmenu_android4_item_height = getInteger( "setupmenu_android4_item_height" );
		if( setupmenu_android4_item_height == -1 )
		{
			setupmenu_android4_item_height = 48;
		}
		setupmenu_square_item_height = getInteger( "setupmenu_square_item_height" );
		if( setupmenu_square_item_height == -1 )
		{
			setupmenu_square_item_height = 70;
		}
		setupmenu_text_font_size = Tools.dip2px( iLoongLauncher.getInstance() , getInteger( "setupmenu_text_font_size" ) );
		if( setupmenu_text_font_size <= 0 )
		{
			setupmenu_text_font_size = R3D.icon_title_font;
		}
		//Jone add
		if( Utils3D.getScreenWidth() > 1000 )
		{
			pop_menu_height = getInteger( "pop_menu_height_xxh" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height_xxh" );
			pop_menu_title_height = getInteger( "pop_menu_title_height_xxh" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height_xxh" );
			pop_menu_container_height = getInteger( "pop_menu_container_height_xxh" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y_xxh" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y_xxh" );
		}
		else if( Utils3D.getScreenWidth() > 700 )
		{
			pop_menu_height = getInteger( "pop_menu_height" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height" );
			pop_menu_title_height = getInteger( "pop_menu_title_height" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height" );
			pop_menu_container_height = getInteger( "pop_menu_container_height" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y" );
		}
		else if( Utils3D.getScreenWidth() > 500 )
		{
			pop_menu_container_height = getInteger( "pop_menu_container_height_zdy" );
			edit_mode_indicator_offset_4x2 = getInteger( "edit_mode_indicator_offset_4x2_zdy" );
		}
		else if( Utils3D.getScreenWidth() > 400 )
		{
			pop_menu_height = getInteger( "pop_menu_height_h" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height_h" );
			pop_menu_title_height = getInteger( "pop_menu_title_height_h" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height_h" );
			pop_menu_container_height = getInteger( "pop_menu_container_height_h" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y_h" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y_h" );
		}
		else if( Utils3D.getScreenWidth() > 300 )
		{
			pop_menu_height = getInteger( "pop_menu_height_s" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height_s" );
			pop_menu_title_height = getInteger( "pop_menu_title_height_s" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height_s" );
			pop_menu_container_height = getInteger( "pop_menu_container_height_s" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y_s" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y_s" );
		}
		else
		{
			pop_menu_height = getInteger( "pop_menu_height" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height" );
			pop_menu_title_height = getInteger( "pop_menu_title_height" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height" );
			pop_menu_container_height = getInteger( "pop_menu_container_height" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y" );
		}
		//Jone end 
		setupmenu_item_padding_y = getInteger( "setupmenu_item_padding_y" );
		setupmenu_items_btw_space = getInteger( "setupmenu_items_btw_space" );
		//teapotXu add end
		setupmenu_icon_padding_top = getInteger( "setupmenu_icon_padding_top" );//zjp
		appmenu_icon_padding_top = getInteger( "appmenu_icon_padding_top" );
		appbar_height = getInteger( "appbar_height" );
		appbar_tab_color = Color.parseColor( getString( "appbar_tab_color" ) );
		pop_menu_title_color = Color.parseColor( getString( "pop_menu_title_color" ) );
		appbar_tab_select_color = Color.parseColor( getString( "appbar_tab_select_color" ) );
		appbar_tab_pop_color = Color.parseColor( getString( "appbar_tab_pop_color" ) );
		appbar_tab_pop_select_color = Color.parseColor( getString( "appbar_tab_pop_select_color" ) );
		appbar_menu_color = Color.parseColor( getString( "appbar_menu_color" ) );
		// appbar_tab_width = getInteger("appbar_tab_width");
		appbar_tab_padding = getInteger( "appbar_tab_padding" );
		appbar_indicator_height = getInteger( "appbar_indicator_height" );
		appbar_home_right = getInteger( "appbar_home_right" );
		appbar_menu_right = getInteger( "appbar_menu_right" );
		appbar_menu_width = getInteger( "appbar_menu_width" );
		appbar_menu_height = getInteger( "appbar_menu_height" );
		// appbar_divider_width = getInteger("appbar_divider_width");
		appbar_padding_left = getInteger( "appbar_padding_left" );
		appbar_menuitem_width = getInteger( "appbar_menuitem_width" );
		appbar_menuitem_height = getInteger( "appbar_menuitem_height" );
		appbar_menuitem_paddingleft = getInteger( "appbar_menuitem_paddingleft" );
		app_widget3d_gap = getInteger( "app_widget3d_gap" );
		appbar_more_width = getInteger( "appbar_more_width" ) * Utils3D.getScreenWidth() / 720f;
		appbar_tab_width = getInteger( "appbar_tab_width" ) * Utils3D.getScreenWidth() / 720f;
		appbar_tab_popitem_height = getInteger( "appbar_tab_popitem_height" ) * Utils3D.getScreenWidth() / 720f;
		if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			Workspace_cellCountX = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_COL );
			Workspace_cellCountY = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_ROW );
			Workspace_celllayout_toppadding = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_PADDING_TOP );
			Workspace_celllayout_bottompadding = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM );
			page_indicator_y = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_INDICATOR_Y );
		}
		else
		{
			//			Workspace_cellCountX = r.getInteger(RR.integer.Workspace_cellCountX);
			//			Workspace_cellCountY = r.getInteger(RR.integer.Workspace_cellCountY);
			Workspace_celllayout_toppadding = R3D.getInteger( "celllayout_topPadding" );
			Workspace_celllayout_bottompadding = R3D.getInteger( "celllayout_bottomPadding" );
			page_indicator_y = R3D.getInteger( "page_indicator_y" );
			if( Utils3D.getScreenHeight() < 500 )
			{
				Workspace_celllayout_toppadding = R3D.getInteger( "celllayout_topPadding_small" );
				Workspace_celllayout_bottompadding = R3D.getInteger( "celllayout_bottomPadding_small" );
				page_indicator_y = R3D.getInteger( "page_indicator_y_small" );
			}
		}
		if( !DefaultLayout.enable_hotseat_rolling )
		{
			page_indicator_y -= 20;
		}
		page_indicator_y_high = getInteger( "page_indicator_y_high" );
		if( DefaultLayout.dispose_cell_count )
		{
			Applist_cellCountX = DefaultLayout.cellCountX;
			Applist_cellCountY = DefaultLayout.cellCountY;
		}
		else if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			Applist_cellCountX = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_COL );
			Applist_cellCountY = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_ROW );
		}
		else
		{
			if( Utils3D.getScreenDisplayMetricsHeight() >= 800 )
			{
				Applist_cellCountX = 4;
				Applist_cellCountY = 5;
			}
			else
			{
				Applist_cellCountX = 4;
				Applist_cellCountY = 4;
			}
		}
		Applist_cellCountX = Applist_cellCountX > 5 ? 5 : Applist_cellCountX;
		Applist_cellCountY = Applist_cellCountY > 6 ? 6 : Applist_cellCountY;
		if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			applist_padding_left = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_PADDING_LEFT );
			applist_padding_right = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_PADDING_RIGHT );
			applist_padding_top = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_PADDING_TOP );
			applist_padding_bottom = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_PADDING_BOTTOM );
			applist_indicator_y = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_INDICATOR_Y );
		}
		else
		{
			if( Applist_cellCountX == 3 )
			{
				applist_padding_left = getInteger( "applist_padding_left_ex" );
				applist_padding_right = getInteger( "applist_padding_right_ex" );
			}
			else
			{
				applist_padding_left = getInteger( "applist_padding_left" );
				applist_padding_right = getInteger( "applist_padding_right" );
			}
			applist_padding_top = getInteger( "applist_padding_top" );
			applist_padding_bottom = getInteger( "applist_padding_bottom" );
			applist_indicator_y = getInteger( "applist_indicator_y" );
			if( Utils3D.getScreenHeight() > 800 )
			{
				applist_padding_top = getInteger( "applist_padding_top_large" );
				applist_padding_bottom = getInteger( "applist_padding_bottom_large" );
				applist_indicator_y = getInteger( "applist_indicator_y_large" );
			}
		}
		//		Workspace_cell_each_width_ori = (int) r.getDimension(RR.dimen.workspace_cell_width);
		//		Workspace_cell_each_height_ori = (int) r.getDimension(RR.dimen.workspace_cell_height);
		Workspace_cell_each_width = Utils3D.getScreenWidth() / R3D.Workspace_cellCountX;
		Workspace_cell_each_height = ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - Workspace_celllayout_toppadding - Workspace_celllayout_bottompadding ) / R3D.Workspace_cellCountY;
		/*
		 * 对于QVGA的屏，计算出来的Workspace_cell_each_height还没有workspace_cell_height高，
		 * 动态调整Workspace_cellCountY added by zfshi 2012-11-14
		 */
		if( Utils3D.getScreenHeight() < 400 && Workspace_cell_each_height < workspace_cell_height )
		{
			Workspace_cellCountY = Workspace_cellCountY - 1;
			Workspace_cell_each_height = ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - Workspace_celllayout_toppadding - Workspace_celllayout_bottompadding ) / R3D.Workspace_cellCountY;
		}
		ConfigBase.Workspace_cell_each_height = Workspace_cell_each_height;
		ConfigBase.Workspace_cell_each_width = Workspace_cell_each_width;
		ConfigBase.Workspace_cell_each_height_ori = Workspace_cell_each_height_ori;
		ConfigBase.Workspace_cell_each_width_ori = Workspace_cell_each_width_ori;
		if( workspace_cell_width > ( Utils3D.getScreenWidth() - applist_padding_left - applist_padding_right ) / Applist_cellCountX )
		{
			workspace_cell_width = ( Utils3D.getScreenWidth() - applist_padding_left - applist_padding_right ) / Applist_cellCountX;
		}
		if( workspace_cell_height > Workspace_cell_each_height )
		{
			workspace_cell_height = Workspace_cell_each_height;
		}
		seatbar_hide_height = workspace_cell_height * DefaultLayout.seatbar_hide_height_ratio;
		sidebar_widget_w = sidebar_widget_h = Utilities.sIconTextureWidth;
		icongroup_margin_left = getInteger( "icongroup_margin_left" );
		icongroup_margin_top = getInteger( "icongroup_margin_top" );
		// icongroup_cell_width = getInteger("icongroup_cell_width");
		// icongroup_cell_height = getInteger("icongroup_cell_height");
		icongroup_bottom_limit = getInteger( "icongroup_bottom_limit" );
		page_indicator_radius = getInteger( "page_indicator_radius" );
		pageselect_origin_x = getInteger( "page_indicator_origin_x" );
		pageselect_origin_y = getInteger( "page_indicator_origin_y" );
		trash_cap_x = getInteger( "trash_cap_x" );
		trash_cap_y = getInteger( "trash_cap_y" );
		trash_cap_w = getInteger( "trash_cap_w" );
		trash_cap_h = getInteger( "trash_cap_h" );
		hot_obj_origin_z = getInteger( "hot_obj_origin_z" );
		//hot_obj_height = getInteger( "hot_obj_height" );
		hot_obj_rot_deg = getInteger( "hot_obj_rot_deg" );
		hot_obj_trans_y = getInteger( "hot_obj_trans_y" );
		if( Utils3D.getScreenHeight() < 700 )
			hot_obj_trans_y = getInteger( "hot_obj_trans_y_small" );
		else if( Utils3D.getScreenHeight() > 800 )
			hot_obj_trans_y = getInteger( "hot_obj_trans_y_large" );
		hot_obj_trans_z = getInteger( "hot_obj_trans_z" );
		hot_grid_left_margin = getInteger( "hot_grid_left_margin" );
		hot_grid_right_margin = getInteger( "hot_grid_right_margin" );
		hot_frontgrid_origin_z = getInteger( "hot_frontgrid_origin_z" );
		hot_dock_icon_size = Utilities.sIconTextureWidth;
		hot_dock_trans_y = getInteger( "hot_dock_trans_y" );
		hot_grid_bottom_margin = getInteger( "hot_grid_bottom_margin" );
		if( Utils3D.getScreenWidth() > 400 )
			hot_grid_bottom_margin = getInteger( "hot_grid_bottom_margin_large" );
		if( !DefaultLayout.enable_hotseat_rolling )
		{
			hot_grid_bottom_margin -= 20;
		}
		icon_title_gap = (int)getInteger( "icon_title_gap" );
		if( icon_title_gap == -1 )
		{
			icon_title_gap = 10;
		}
		hot_sidebar_top_margin = getInteger( "hot_sidebar_top_margin" );
		tip_point_width = getInteger( "tip_point_width" );
		tip_point_height = getInteger( "tip_point_height" );
		icon_bg_num = getInteger( "icon_bg_num" );
		hot_dock_item_num = getInteger( "hot_dock_item_num" );//zjp
		// teapotXu_20130206: add start
		theme_thirdapk_icon_scaleFactor = getInteger( "theme_thirdapk_icon_scaleFactor" );
		// here reset the value of thirdapk_icon_scaleFactor by
		// theme_thirdapk_icon_scaleFactor.
		if( theme_thirdapk_icon_scaleFactor != -1 )
		{
			DefaultLayout.thirdapk_icon_scaleFactor = theme_thirdapk_icon_scaleFactor / 100f;
		}
		else
		{
			DefaultLayout.thirdapk_icon_scaleFactor = 0.70f;
		}
		// teapotXu_20130206: add end
		bottom_bar_height = getInteger( "bottombar_height" );
		bottom_bar_title_height = getInteger( "bottombar_title_height" );
		//teapotXu add for butter fly style
		page_edit_ygap234 = R3D.getInteger( "page_edit_ygap234" );
		if( Utils3D.getScreenHeight() <= 800 )
			page_edit_ygap567 = R3D.getInteger( "page_edit_ygap567_small" );
		else
		{
			page_edit_ygap567 = R3D.getInteger( "page_edit_ygap567" );
		}
		page_edit_xgap234 = R3D.getInteger( "page_edit_xgap234" );
		page_edit_xgap567 = R3D.getInteger( "page_edit_xgap567" );
		//teapotXu add end
		hot_top_ascent_distance_hide_title = R3D.getInteger( "hot_top_ascent_distance_hide_title" );
		if( getString( "addList_item_text_color" ) == null )
		{
			addList_item_text_color = Color.WHITE;
		}
		else
		{
			addList_item_text_color = Color.parseColor( getString( "addList_item_text_color" ) );
		}
		if( getString( "folder_rename_text_color" ) == null )
		{
			folder_rename_text_color = Color.WHITE;
		}
		else
		{
			folder_rename_text_color = Color.parseColor( getString( "folder_rename_text_color" ) );
		}
		page_indicator_size = R3D.getInteger( "page_indicator_size" );
		page_indicator_focus_w = R3D.getInteger( "page_indicator_focus_w" );
		page_indicator_normal_w = R3D.getInteger( "page_indicator_normal_w" );
		page_indicator_focus_h = R3D.getInteger( "page_indicator_focus_h" );
		page_indicator_normal_h = R3D.getInteger( "page_indicator_normal_h" );
		page_indicator_style = R3D.getInteger( "page_indicator_style" );
		page_indicator_total_size = R3D.getInteger( "page_indicator_total_size" );
		if( page_indicator_style == 2 )
		{// S2
			page_indicator_size = R3D.getInteger( "page_indicator_size_s2" );
			page_indicator_focus_w = R3D.getInteger( "page_indicator_focus_w_s2" );
			page_indicator_normal_w = R3D.getInteger( "page_indicator_normal_w_s2" );
		}
		if( DefaultLayout.enable_DesktopIndicatorScroll )
		{
			s4_page_indicator_bg_height = R3D.getInteger( "s4_page_indicator_bg_height" );
			s4_page_indicator_scroll_width = R3D.getInteger( "s4_page_indicator_scroll_width" );
			s4_page_indicator_scroll_height = R3D.getInteger( "s4_page_indicator_scroll_height" );
			s4_page_indicator_number_bg_size = R3D.getInteger( "s4_page_indicator_number_bg_size" );
			s4_page_indicator_number_x_offset = R3D.getInteger( "s4_page_indicator_number_x_offset" );
			if( s4_page_indicator_number_x_offset == -1 )
			{
				s4_page_indicator_number_x_offset = Tools.dip2px( iLoongLauncher.getInstance() , 2 );
			}
		}
		int folder_max_height = Utils3D.getScreenHeight()
		// - R3D.workspace_cell_height - R3D.icongroup_button_height / 2
		- R3D.folder_group_text_height - R3D.workspace_cell_height - R3D.folder_group_top_round - R3D.icongroup_margin_top - R3D.icongroup_margin_left;
		//zhujieping add
		if( folder_group_child_count_x <= 0 )
		{
			folder_group_child_count_x = 4;
		}
		if( Utils3D.getScreenHeight() < 500 )
		{
			// hot_grid_bottom_margin = 0;
			hot_obj_height = R3D.workspace_cell_height;
			page_indicator_focus_w = R3D.getInteger( "page_indicator_focus_w_small" );
			page_indicator_normal_w = R3D.getInteger( "page_indicator_normal_w_small" );
			if( page_indicator_style == 2 )
			{// S2
				// page_indicator_size =
				// R3D.getInteger("page_indicator_size_s2");
				page_indicator_focus_w = R3D.getInteger( "page_indicator_focus_w_s2_small" );
				page_indicator_normal_w = R3D.getInteger( "page_indicator_normal_w_s2_small" );
			}
		}
		/* added by zfshi ended 2012-11-14 */
		folder_front_width = R3D.workspace_cell_width;
		// xiatian start // explorer to adaptive difference resolution
		// xiatian del start
		// /*
		photo_width = R3D.getInteger( "photo_width" );
		photo_height = R3D.getInteger( "photo_height" );
		photo_padding = R3D.getInteger( "photo_padding" );
		photo_bucket_width = R3D.getInteger( "photo_bucket_width" );
		photo_bucket_height = R3D.getInteger( "photo_bucket_height" );
		photo_title_size = R3D.getInteger( "photo_title_size" );
		photo_title_line = R3D.getInteger( "photo_title_line" );
		audio_width = R3D.getInteger( "audio_width" );
		audio_height = R3D.getInteger( "audio_height" );
		video_width = R3D.getInteger( "video_width" );
		video_height = R3D.getInteger( "video_height" );
		video_padding = R3D.getInteger( "video_padding" );
		audio_bottom_padding = R3D.getInteger( "audio_bottom_padding" );
		audio_left_padding = R3D.getInteger( "audio_left_padding" );
		//xiatian add start	//adjust third apk icon offset when have iconbg
		Third_APK_Icon_Offset_X = getInteger( "third_apk_icon_offset_x" );
		if( Third_APK_Icon_Offset_X == -999 )
		{
			Third_APK_Icon_Offset_X = 0;
		}
		Third_APK_Icon_Offset_Y = getInteger( "third_apk_icon_offset_y" );
		if( Third_APK_Icon_Offset_Y == -999 )
		{
			Third_APK_Icon_Offset_Y = 0;
		}
		//xiatian add end
		//xiatian add start	//folder transform icon offset
		Folder_Transform_Icon_Offset_X = getInteger( "folder_transform_icon_offset_x" );
		if( Folder_Transform_Icon_Offset_X == -999 )
		{
			Folder_Transform_Icon_Offset_X = 0;
		}
		Folder_Transform_Icon_Offset_Y = getInteger( "folder_transform_icon_offset_y" );
		if( Folder_Transform_Icon_Offset_Y == -999 )
		{
			Folder_Transform_Icon_Offset_Y = 0;
		}
		//xiatian add end
		if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
		{
			folder_front_height = getInteger( "folder_front_two_line_rotate_height" );
		}
		else
		{
			folder_front_height = R3D.workspace_cell_height;
		}
		if( !DefaultLayout.show_icon_size )
		{
			int app_icon_size = R3D.getInteger( "app_icon_size" );
			if( app_icon_size != -1 && app_icon_size != DefaultLayout.app_icon_size )
			{
				Utilities.reInitStatics( app_icon_size );
				DefaultLayout.app_icon_size = app_icon_size;
			}
		}
		FolderIcon3D.changeFrontBmp();
		/*Icon3D.init必须放到前面，便于判断是否有背景框*/
		Icon3D.reInit();
		launcher.changeHotseats();
		repack(
				"app-default-icon-with-title" ,
				Utils3D.IconToPixmap3D( IconCache.makeDefaultIcon() , iLoongLauncher.getInstance().getResources().getString( RR.string.app_unknow ) , Icon3D.getIconBg() , Icon3D.titleBg ) );
		repack( R3D.folder3D_name , FolderIcon3D.titleToTexture( R3D.folder3D_name , Color.WHITE ) );
		for( int i = 1 ; i <= 5 ; i++ )
		{
			repack( "" + i + "x.z" , FolderIcon3D.titleToTexture( "" + i , Color.WHITE ) );
		}
		repack( "move_to_left_screen_bar_bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/move_to_left_screen_bar_bg.png" ) );
		repack( "move_to_right_screen_bar_bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/move_to_right_screen_bar_bg.png" ) );
		if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
		{
			repack( "widget-folder-bg2" , ThemeManager.getInstance().getBitmap( "theme/folder/widget-folder-bg2.png" ) );
		}
		repack( "icon_focus" , ThemeManager.getInstance().getBitmap( "theme/pack_source/focus_bg.png" ) );
		//xiatian add start	//New AppList Popmenu
		applist_menu_color = Color.parseColor( getString( "applist_menu_color" ) );
		applist_menu_padding_top = getInteger( "applist_menu_padding_top" );
		applist_menu_height = getInteger( "applist_menu_height" );
		//xiatian add end
		folder_opend_bg_alpha = getInteger( "folder_opend_bg_alpha" );
		R3D.repack( "cursor_patch" , ThemeManager.getInstance().getBitmap( "theme/pack_source/cursor_patch.png" ) );
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			R3D.repack( "widget-folder-open-line" , ThemeManager.getInstance().getBitmap( "theme/pack_source/widget-folder-open-line.png" ) );
			R3D.repack( "miui-input-ack" , ThemeManager.getInstance().getBitmap( "theme/miui_source/miui-input-ack.png" ) );
			R3D.repack( "miui-input-ack-focus" , ThemeManager.getInstance().getBitmap( "theme/miui_source/miui-input-ack-focus.png" ) );
		}
		R3D.repack( folder_bg_name , ThemeManager.getInstance().getBitmap( "theme/folder/folder_bg.9.png" ) );
		R3D.repack( "page-add-icon" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-add-icon.png" ) );
		R3D.repack( "page-controlle-c" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-controlle-c.png" ) );
		R3D.repack( "page-controller-indicator-a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-controller-indicator-a.png" ) );
		R3D.repack( "page-controller-indicator-b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-controller-indicator-b.png" ) );
		R3D.repack( "page-edit3" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit3.png" ) );
		R3D.repack( "public-button-return" , ThemeManager.getInstance().getBitmap( "theme/pack_source/public-button-return.png" ) );
		if( !DefaultLayout.disable_circled )
		{
			R3D.repack( "shell-interactive-grid-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-interactive-grid-bg.png" ) );
			R3D.repack( "shell-interactive-grid-scale-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-interactive-grid-scale-button.png" ) );
			R3D.repack( "shell-picker-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-bg.png" ) );
			R3D.repack( "shell-picker-connect-line" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-connect-line.png" ) );
			R3D.repack( "shell-picker-menu-item1" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item1.png" ) );
			R3D.repack( "shell-picker-menu-item2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item2.png" ) );
			R3D.repack( "shell-picker-menu-item3a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item3a.png" ) );
			R3D.repack( "shell-picker-menu-item3b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item3b.png" ) );
			R3D.repack( "shell-picker-menu-item4a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item4a.png" ) );
			R3D.repack( "shell-picker-menu-item4b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item4b.png" ) );
			R3D.repack( "shell-picker-menu-item5a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item5a.png" ) );
			R3D.repack( "shell-picker-menu-item5b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item5b.png" ) );
			R3D.repack( "shell-picker-menu-item6a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item6a.png" ) );
			R3D.repack( "shell-picker-menu-item6b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item6b.png" ) );
			R3D.repack( "shell-picker-menu-item7a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item7a.png" ) );
			R3D.repack( "shell-picker-menu-item7b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item7b.png" ) );
		}
		R3D.repack( "shell-select-page-bg-select" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-select-page-bg-select.png" ) );
		R3D.repack( "shell-select-page-bg-unselect" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-select-page-bg-unselect.png" ) );
		R3D.repack( "tip-point" , ThemeManager.getInstance().getBitmap( "theme/pack_source/tip-point.png" ) );
		if( DefaultLayout.show_widget_shortcut_bg )
			R3D.repack( "widget-shortcut-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/widget-shortcut-bg.png" ) );
		R3D.repack( "delete-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/delete-button.png" ) );
		R3D.repack( "home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/home-button.png" ) );
		R3D.repack( "more-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/more-button.png" ) );
		if( Utils3D.getScreenHeight() > 700 )
		{
			R3D.repack( "page-edit2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit2.png" ) );
			R3D.repack( "page-edit2b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit2b.png" ) );
			R3D.repack( "app-menu-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-menu-button.png" ) );
			R3D.repack( "app-menu-downarray" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-downarray.png" ) );
			R3D.repack( "app-menu-bag" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-bag.png" ) );
			if( Utils3D.getScreenHeight() > 854 )
			{
				R3D.repack( "app-home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-button-larges.png" ) );
				R3D.repack( "app-home-personal" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-personal-large.png" ) );
			}
			else
			{
				R3D.repack( "app-home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-buttons.png" ) );
				R3D.repack( "app-home-personal" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-personal.png" ) );
			}
		}
		else
		{
			R3D.repack( "page-edit2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit2-small.png" ) );
			R3D.repack( "page-edit2b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit2b-small.png" ) );
			R3D.repack( "app-menu-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-menu-button-small.png" ) );
			R3D.repack( "app-menu-downarray" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-downarray-small.png" ) );
			R3D.repack( "app-menu-bag" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-bag-small.png" ) );
			R3D.repack( "app-home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-button-smalls.png" ) );
			R3D.repack( "app-home-personal" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-personal-small.png" ) );
		}
		R3D.repack( "app-uninstall" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall.png" ) );
		R3D.repack( "appbar-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-bgs.png" ) );
		R3D.repack( "appbar-tabbar-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tabbar-bg.png" ) );
		R3D.repack( "appbar-tab-select-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-select-bg.png" ) );
		R3D.repack( "appbar-divider" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-divider.png" ) );
		R3D.repack( "appbar-tab-divider" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-divider.png" ) );
		R3D.repack( "app-item-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-item-bg.png" ) );
		R3D.repack( "appbar-indicator" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-indicator.png" ) );
		R3D.repack( "appbar-tab-arrow" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-arrow.png" ) );
		R3D.repack( "appbar-more-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-more-button.png" ) );
		R3D.repack( "appbar-more-button-select" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-more-button-select.png" ) );
		R3D.repack( "appbar-tab-pop-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-pop-bg.png" ) );
		R3D.repack( "appbar-tab-pop-item-select-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-pop-item-select-bg.png" ) );
		R3D.repack( "appbar-tab-pop-item-divider" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-pop-item-divider.png" ) );
		if( DefaultLayout.appbar_show_userapp_list == true )
		{
			R3D.repack( "appbar-navi-back" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-show-userapp-navigation-back.png" ) );
		}
		else
		{
			R3D.repack( "appbar-navi-back" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-navigation-back.png" ) );
		}
		R3D.repack( "workspace-reflect-view" , ThemeManager.getInstance().getBitmap( "theme/home/homescreen_blue_strong_holo.png" ) );
		R3D.repack( "workspace-zoom-view" , ThemeManager.getInstance().getBitmap( "theme/home/workspace_zoom_view.png" ) );
		R3D.repack( "workspace-zoomarrow" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow.png" ) );
		R3D.repack( "workspace-zoomarrow_top" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow_top.png" ) );
		R3D.repack( "workspace-zoomarrow_bottom" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow_bottom.png" ) );
		R3D.repack( "workspace-zoomarrow_left" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow_left.png" ) );
		R3D.repack( "workspace-zoomarrow_right" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow_right.png" ) );
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_MIDDLE )
		{
			R3D.repack( "trash-background" , ThemeManager.getInstance().getBitmap( "theme/pack_source/trash-background-middle.png" ) );
		}
		else if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
		}
		else
		{
			R3D.repack( "trash-background" , ThemeManager.getInstance().getBitmap( "theme/pack_source/trash-background.png" ) );
		}
		//xiatian end
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			if( DefaultLayout.generate_new_folder_in_top_trash_bar == true )
			{
				if( !DefaultLayout.isScaleBitmap )
				{
					//R3D.repack( "create_folder" , ThemeManager.getInstance().getBitmap( "theme/pack_source/create_folder.png" ) );
					//R3D.repack( "create_folder2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/create_folder2.png" ) );
					R3D.repack( "xiezai-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg-top.png" ) );
					R3D.repack( "xiezai-bg2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg2-top.png" ) );
					//R3D.repack( "xiezai-bg_screen_width" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg-top2.png" ) );
					//R3D.repack( "xiezai-bg2_screen_width" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg2-top2.png" ) );
				}
			}
			else
			{
				R3D.repack( "xiezai-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg.png" ) );
				R3D.repack( "xiezai-bg2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg2.png" ) );
			}
		}
		int tmpH = (int)( ( Utils3D.getScreenHeight() - R3D.appbar_height - R3D.applist_padding_top - R3D.applist_padding_bottom ) / 3 ) - R3D.app_widget3d_gap;
		//xiatian add start //EffectPreview
		if( DefaultLayout.enable_effect_preview )
		{
			R3D.repack( mEffectPreviewBgRegionName , ThemeManager.getInstance().getBitmap( "theme/pack_source/effect_preview_bg.png" ) );
			R3D.repack( mEffectPreviewButtonRegionName , ThemeManager.getInstance().getBitmap( "theme/pack_source/effect_preview_button.png" ) );
			R3D.repack( mEffectPreviewButtonFocusRegionName , ThemeManager.getInstance().getBitmap( "theme/pack_source/effect_preview_button_focus.png" ) );
			R3D.repack( mEffectPreviewSelectRegionName , ThemeManager.getInstance().getBitmap( "theme/pack_source/effect_preview_select.png" ) );
		}
		//xiatian add end		
		//xiatian add start //for mainmenu sort by user
		if( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			R3D.repack( "app-to-workspace-normal" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-to-workspace-normal.png" ) );
			R3D.repack( "app-to-workspace-focus" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-to-workspace-focus.png" ) );
		}
		//xiatian add end
		R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
	}
	
	public static void initialize(
			iLoongLauncher c )
	{
		launcher = c;
		Resources r = c.getResources();
		if( DefaultLayout.anti_aliasing )
		{
			filter = TextureFilter.Linear;
			Magfilter = TextureFilter.Linear;
			ConfigBase.filter = TextureFilter.Linear;
			ConfigBase.Magfilter = TextureFilter.Linear;
		}
		else
		{
			filter = TextureFilter.Nearest;
			ConfigBase.filter = TextureFilter.Nearest;
		}
		workSpace_list_string = new String[DefaultLayout.desktop_effect_list.length];
		app_list_string = new String[DefaultLayout.mainmenu_effect_list.length];
		for( int i = 0 ; i < DefaultLayout.desktop_effect_list.length ; i++ )
		{
			int effect_resId = getEffectResId( DefaultLayout.desktop_effect_list[i] );
			workSpace_list_string[i] = getString( effect_resId == 0 ? RR.string.effect_default : effect_resId );
		}
		for( int i = 0 ; i < DefaultLayout.mainmenu_effect_list.length ; i++ )
		{
			int effect_resId = getEffectResId( DefaultLayout.mainmenu_effect_list[i] );
			app_list_string[i] = getString( effect_resId == 0 ? RR.string.effect_default : effect_resId );
		}
		if( DefaultLayout.desktop_page_effect_id >= workSpace_list_string.length )
		{
			DefaultLayout.desktop_page_effect_id = 0;
		}
		if( DefaultLayout.mainmenu_page_effect_id >= app_list_string.length )
		{
			DefaultLayout.desktop_page_effect_id = 0;
		}
		//teapotXu add end
		if( DefaultLayout.enable_effect_preview )
		{
			iLoongApplication.themeConfig.app_list_string = app_list_string;
			iLoongApplication.themeConfig.workSpace_list_string = workSpace_list_string;
		}
		if( RR.net_version )
			miui_widget_indicator_height = 0;
		else
			miui_widget_indicator_height = getInteger( "miui_widget_indicator_height" );
		trash_icon_width = getInteger( "trash_icon_width" );
		trash_icon_height = getInteger( "trash_icon_height" );
		//teapotXu add start for add new foler in top-trash bar 
		if( DefaultLayout.generate_new_folder_in_top_trash_bar && DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			toolbar_height = getInteger( "toolbar_height" );
			trash_icon_height = toolbar_height;
			toolbar_icon_region_width = getInteger( "toolbar_icon_region_width" );
			toolbar_icon_region_height = getInteger( "toolbar_icon_region_height" );
			toolbar_icon_region_y = getInteger( "toolbar_icon_region_y" );
			toolbar_title_region_y = getInteger( "toolbar_title_region_y" );
		}
		//teapotXu add end
		if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			workspace_cell_width = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_CELL_WIDTH );
			workspace_cell_height = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT );
		}
		else
		{
			workspace_cell_width = getInteger( "workspace_cell_width" );
			workspace_cell_height = getInteger( "workspace_cell_height" );
		}
		workspace_cell_adjust = getInteger( "workspace_cell_adjust" );
		pageview_indicator_height = getInteger( "pageview_indicator_height" );
		circle_drawtext_x = getInteger( "circle_drawtext_x" );
		circle_drawtext_y = getInteger( "circle_drawtext_y" );
		icongroup_button_height = getInteger( "icongroup_button_height" );
		icongroup_button_width = getInteger( "icongroup_button_width" );
		icongroup_round_radius = getInteger( "icongroup_round_radius" );
		circle_unfocus_backgroud_width = getInteger( "circle_unfocus_backgroud_width" );
		circle_unfocus_backgroud_height = getInteger( "circle_unfocus_backgroud_height" );
		circle_focus_backgroud_width = getInteger( "circle_focus_backgroud_width" );
		circle_focus_backgroud_height = getInteger( "circle_focus_backgroud_height" );
		circle_focus_offset_y = getInteger( "circle_focus_offset_y" );
		circle_autosort_width = getInteger( "circle_autosort_width" );
		circle_autosort_height = getInteger( "circle_autosort_height" );
		circle_overlap_width = getInteger( "circle_overlap_width" );
		circle_overlap_height = getInteger( "circle_overlap_height" );
		circle_multiselect_width = getInteger( "circle_multiselect_width" );
		circle_multiselect_height = getInteger( "circle_multiselect_height" );
		circle_delall_width = getInteger( "circle_delall_width" );
		circle_delall_height = getInteger( "circle_delall_height" );
		circle_folder_width = getInteger( "circle_folder_width" );
		circle_folder_height = getInteger( "circle_folder_height" );
		pop_toast_width = getInteger( "pop_toast_width" );
		pop_toast_height = getInteger( "pop_toast_height" );
		folder_num_width = getInteger( "folder_num_width" );
		folder_num_height = getInteger( "folder_num_height" );
		folder_num_circle_width = getInteger( "folder_num_circle_width" );
		folder_icon_rotation_degree = getInteger( "folder_icon_rotation_degree" );
		folder_icon_rotation_offsetx = getInteger( "folder_icon_rotation_offsetx" );
		folder_icon_rotation_offsety = getInteger( "folder_icon_rotation_offsety" );
		folder_num_offset_y = getInteger( "folder_num_offset_y" );
		folder_front_width = getInteger( "folder_front_width" );
		folder_back_width = getInteger( "folder_back_width" );
		folder_back_height = getInteger( "folder_back_height" );
		// folder_title_font = getInteger("folder_title_font");
		folder_gap_height = getInteger( "folder_gap_height" );
		folder_transform_num = getInteger( "folder_transform_num" );
		workspace_cell_text_height = getInteger( "workspace_cell_text_height" );
		folder_icon_row_num = getInteger( "folder_icon_row_num" );
		folder_icon_posy = getInteger( "folder_icon_posy" );
		folder_group_left_margin = getInteger( "folder_group_left_margin" );
		folder_group_right_margin = getInteger( "folder_group_right_margin" );
		folder_group_top_margin = getInteger( "folder_group_top_margin" );
		folder_group_bottom_margin = getInteger( "folder_group_bottom_margin" );
		folder_group_left_round = getInteger( "folder_group_left_round" );
		folder_group_top_round = getInteger( "folder_group_top_round" );
		folder_group_right_round = getInteger( "folder_group_right_round" );
		folder_group_bottom_round = getInteger( "folder_group_bottom_round" );
		folder_group_text_height = getInteger( "folder_group_text_height" );
		folder_group_text_round = getInteger( "folder_group_text_round" );
		folder_icon_scale_factor = getInteger( "folder_icon_scale_factor" );
		folder_group_child_count_x = getInteger( "folder_group_child_count_x" );
		paopao_state_icon_padding_x = getInteger( "paopao_state_icon_padding_x" );
		paopao_state_icon_padding_y = getInteger( "paopao_state_icon_padding_y" );
		icon_and_text_spaceing = getInteger( "icon_and_text_spaceing" );
		setupmenu_icon_and_text_spacing = getInteger( "setupmenu_icon_and_text_spacing" );
		folder3D_name = r.getString( RR.string.folder_name );
		folder3D_full = r.getString( RR.string.folder3D_full );
		iLoong_Name = r.getString( RR.string.app_name );
		workspace_multicon_width = (int)r.getDimension( RR.dimen.multi_icon_width );
		workspace_multicon_height = (int)r.getDimension( RR.dimen.multi_icon_height );
		workspace_paopao_big_width = (int)r.getDimension( RR.dimen.paopao_big_width );
		workspace_paopao_num_width = (int)r.getDimension( RR.dimen.paopao_num_width );
		workspace_paopao_num_height = (int)r.getDimension( RR.dimen.paopao_num_height );
		workspace_multiviews_offset = (int)r.getDimension( RR.dimen.multi_views_offset3D );
		//teapotXu add start
		R3D.android4_seekbar_padding_left = getInteger( "android4_seekbar_padding_left" );
		if( R3D.android4_seekbar_padding_left == -1 )
			R3D.android4_seekbar_padding_left = 0;
		R3D.android4_seekbar_padding_top = getInteger( "android4_seekbar_padding_top" );
		if( R3D.android4_seekbar_padding_top == -1 )
			R3D.android4_seekbar_padding_top = 0;
		R3D.android4_seekbar_padding_right = getInteger( "android4_seekbar_padding_right" );
		if( R3D.android4_seekbar_padding_right == -1 )
			R3D.android4_seekbar_padding_right = 0;
		R3D.android4_seekbar_padding_bottom = getInteger( "android4_seekbar_padding_bottom" );
		if( R3D.android4_seekbar_padding_bottom == -1 )
			R3D.android4_seekbar_padding_bottom = 0;
		//teapotXu add end
		//teapotXu add start
		workspace_editmode_optimum_widget_icon_size = getInteger( "workspace_editmode_optimum_widget_icon_size" );
		if( workspace_editmode_optimum_widget_icon_size == -1 )
		{
			if( DefaultLayout.show_icon_size )
			{
				if( DefaultLayout.show_icon_size_different_layout )
				{
					if( Utils3D.getScreenWidth() > 700 )
					{//大屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_big_screen_big_icon" );
					}
					else if( Utils3D.getScreenWidth() > 400 )
					{//中屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_normal_screen_big_icon" );
					}
					else
					{//小屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_small_screen_big_icon" );
					}
				}
				else
				{
					if( Utils3D.getScreenWidth() > 700 )
					{//大屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_big_screen_big_icon_same_layout" );
					}
					else if( Utils3D.getScreenWidth() > 400 )
					{//中屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_normal_screen_big_icon_same_layout" );
					}
					else
					{//小屏
						workspace_editmode_optimum_widget_icon_size = R3D.getInteger( "app_icon_size_small_screen_big_icon_same_layout" );
					}
				}
			}
			else
			{
				workspace_editmode_optimum_widget_icon_size = DefaultLayout.app_icon_size;
				if( workspace_editmode_optimum_widget_icon_size <= 0 )
				{
					workspace_editmode_optimum_widget_icon_size = (int)iLoongLauncher.getInstance().getResources().getDimension( android.R.dimen.app_icon_size );
				}
			}
		}
		//teapotXu add end
		if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			icon_title_font = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_TITLE_SIZE );
		}
		else
		{
			icon_title_font = Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_title_font );
		}
		//teapotXu add start
		setupmenu_android4_item_height = getInteger( "setupmenu_android4_item_height" );
		if( setupmenu_android4_item_height == -1 )
		{
			setupmenu_android4_item_height = 48;
		}
		setupmenu_square_item_height = getInteger( "setupmenu_square_item_height" );
		if( setupmenu_square_item_height == -1 )
		{
			setupmenu_square_item_height = 70;
		}
		setupmenu_text_font_size = Tools.dip2px( iLoongLauncher.getInstance() , getInteger( "setupmenu_text_font_size" ) );
		if( setupmenu_text_font_size <= 0 )
		{
			setupmenu_text_font_size = R3D.icon_title_font;
		}
		//Jone add
		if( Utils3D.getScreenWidth() == 800 )
		{
			pop_menu_height = getInteger( "pop_menu_height" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height" );
			pop_menu_title_height = getInteger( "pop_menu_title_height" );
			pop_menu_page_grid_padding_top = getInteger( "pop_menu_page_grid_padding_top" );
			pop_menu_page_grid_padding_bottom = getInteger( "pop_menu_page_grid_padding_bottom" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height" );
			pop_menu_container_height = getInteger( "pop_menu_container_height" );
			pop_menu_bottom_ind_height = getInteger( "pop_menu_bottom_ind_height" );
			pop_menu_bottom_ind_bar_height = getInteger( "pop_menu_bottom_ind_bar_height" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y" );
			workspace_celllayout_offset_y = getInteger( "workspace_celllayout_offset_y_w" );
			workspace_celllayout_bg_padding_top = getInteger( "workspace_celllayout_bg_padding_top_w" );
			pop_menu_bottom_ind_height = R3D.getInteger( "pop_menu_bottom_ind_height" );
			pop_menu_bottom_ind_bar_height = R3D.getInteger( "pop_menu_bottom_ind_bar_height" );
			Workspace_celllayout_editmode_padding = R3D.getInteger( "celllayout_editmode_padding" );
			edit_mode_indicator_offset_4x2 = getInteger( "edit_mode_indicator_offset_4x2" );
			edit_mode_indicator_offset_4x1 = getInteger( "edit_mode_indicator_offset_4x1" );
			edit_mode_celllayout_offset = getInteger( "edit_mode_celllayout_offset" );
			qs_app_list_row_height = getInteger( "qs_app_list_row_height_l" );
			qs_category_grid_padding_left = getInteger( "qs_category_grid_padding_left_l" );
			qs_category_grid_padding_right = getInteger( "qs_category_grid_padding_right_l" );
			qs_category_grid_padding_top = getInteger( "qs_category_grid_padding_top_l" );
			qs_category_grid_padding_bottom = getInteger( "qs_category_grid_padding_bottom_l" );
		}
		else if( Utils3D.getScreenWidth() > 1000 )
		{
			pop_menu_height = getInteger( "pop_menu_height_xxh" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height_xxh" );
			pop_menu_title_height = getInteger( "pop_menu_title_height_xxh" );
			pop_menu_page_grid_padding_top = getInteger( "pop_menu_page_grid_padding_top_xxh" );
			pop_menu_page_grid_padding_bottom = getInteger( "pop_menu_page_grid_padding_bottom_xxh" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height_xxh" );
			pop_menu_container_height = getInteger( "pop_menu_container_height_xxh" );
			pop_menu_bottom_ind_height = getInteger( "pop_menu_bottom_ind_height_xxh" );
			pop_menu_bottom_ind_bar_height = getInteger( "pop_menu_bottom_ind_bar_height_xxh" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y_xxh" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y_xxh" );
			workspace_celllayout_offset_y = getInteger( "workspace_celllayout_offset_y_xxh" );
			workspace_celllayout_bg_padding_top = getInteger( "workspace_celllayout_bg_padding_top_xxh" );
			pop_menu_bottom_ind_height = R3D.getInteger( "pop_menu_bottom_ind_height_xxh" );
			pop_menu_bottom_ind_bar_height = R3D.getInteger( "pop_menu_bottom_ind_bar_height_xxh" );
			Workspace_celllayout_editmode_padding = R3D.getInteger( "celllayout_editmode_padding_xxh" );
			edit_mode_indicator_offset_4x2 = getInteger( "edit_mode_indicator_offset_4x2_xxh" );
			edit_mode_indicator_offset_4x1 = getInteger( "edit_mode_indicator_offset_4x1_xxh" );
			edit_mode_celllayout_offset = getInteger( "edit_mode_celllayout_offset_xxh" );
			qs_app_list_row_height = getInteger( "qs_app_list_row_height_xh" );
			qs_category_grid_padding_left = getInteger( "qs_category_grid_padding_left_xh" );
			qs_category_grid_padding_right = getInteger( "qs_category_grid_padding_right_xh" );
			qs_category_grid_padding_top = getInteger( "qs_category_grid_padding_top_xh" );
			qs_category_grid_padding_bottom = getInteger( "qs_category_grid_padding_bottom_xh" );
		}
		else if( Utils3D.getScreenWidth() > 700 )
		{
			pop_menu_height = getInteger( "pop_menu_height" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height" );
			pop_menu_title_height = getInteger( "pop_menu_title_height" );
			pop_menu_page_grid_padding_top = getInteger( "pop_menu_page_grid_padding_top" );
			pop_menu_page_grid_padding_bottom = getInteger( "pop_menu_page_grid_padding_bottom" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height" );
			pop_menu_container_height = getInteger( "pop_menu_container_height" );
			pop_menu_bottom_ind_height = getInteger( "pop_menu_bottom_ind_height" );
			pop_menu_bottom_ind_bar_height = getInteger( "pop_menu_bottom_ind_bar_height" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y" );
			workspace_celllayout_offset_y = getInteger( "workspace_celllayout_offset_y" );
			workspace_celllayout_bg_padding_top = getInteger( "workspace_celllayout_bg_padding_top" );
			pop_menu_bottom_ind_height = R3D.getInteger( "pop_menu_bottom_ind_height" );
			pop_menu_bottom_ind_bar_height = R3D.getInteger( "pop_menu_bottom_ind_bar_height" );
			Workspace_celllayout_editmode_padding = R3D.getInteger( "celllayout_editmode_padding" );
			edit_mode_indicator_offset_4x2 = getInteger( "edit_mode_indicator_offset_4x2" );
			edit_mode_indicator_offset_4x1 = getInteger( "edit_mode_indicator_offset_4x1" );
			edit_mode_celllayout_offset = getInteger( "edit_mode_celllayout_offset" );
			qs_app_list_row_height = getInteger( "qs_app_list_row_height_l" );
			qs_category_grid_padding_left = getInteger( "qs_category_grid_padding_left_l" );
			qs_category_grid_padding_right = getInteger( "qs_category_grid_padding_right_l" );
			qs_category_grid_padding_top = getInteger( "qs_category_grid_padding_top_l" );
			qs_category_grid_padding_bottom = getInteger( "qs_category_grid_padding_bottom_l" );
			qs_app_list_row_height = getInteger( "qs_app_list_row_height_m" );
			qs_category_grid_padding_left = getInteger( "qs_category_grid_padding_left_m" );
			qs_category_grid_padding_right = getInteger( "qs_category_grid_padding_right_m" );
			qs_category_grid_padding_top = getInteger( "qs_category_grid_padding_top_m" );
			qs_category_grid_padding_bottom = getInteger( "qs_category_grid_padding_bottom_m" );
		}
		else if( Utils3D.getScreenWidth() > 500 )
		{
			pop_menu_height = getInteger( "pop_menu_height_h" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height_h" );
			pop_menu_title_height = getInteger( "pop_menu_title_height_h" );
			pop_menu_page_grid_padding_top = getInteger( "pop_menu_page_grid_padding_top_h" );
			pop_menu_page_grid_padding_bottom = getInteger( "pop_menu_page_grid_padding_bottom_h" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height_h" );
			pop_menu_container_height = getInteger( "pop_menu_container_height_zdy" );
			pop_menu_bottom_ind_height = getInteger( "pop_menu_bottom_ind_height_h" );
			pop_menu_bottom_ind_bar_height = getInteger( "pop_menu_bottom_ind_bar_height_h" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y_h" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y_h" );
			workspace_celllayout_offset_y = getInteger( "workspace_celllayout_offset_y_h" );
			workspace_celllayout_bg_padding_top = getInteger( "workspace_celllayout_bg_padding_top_h" );
			pop_menu_bottom_ind_height = R3D.getInteger( "pop_menu_bottom_ind_height_h" );
			pop_menu_bottom_ind_bar_height = R3D.getInteger( "pop_menu_bottom_ind_bar_height_h" );
			Workspace_celllayout_editmode_padding = R3D.getInteger( "celllayout_editmode_padding_h" );
			edit_mode_indicator_offset_4x2 = getInteger( "edit_mode_indicator_offset_4x2_zdy" );
			edit_mode_indicator_offset_4x1 = getInteger( "edit_mode_indicator_offset_4x1_h" );
			edit_mode_celllayout_offset = getInteger( "edit_mode_celllayout_offset_h" );
			qs_app_list_row_height = getInteger( "qs_app_list_row_height_s" );
			qs_category_grid_padding_left = getInteger( "qs_category_grid_padding_left_s" );
			qs_category_grid_padding_right = getInteger( "qs_category_grid_padding_right_s" );
			qs_category_grid_padding_top = getInteger( "qs_category_grid_padding_top_s" );
			qs_category_grid_padding_bottom = getInteger( "qs_category_grid_padding_bottom_s" );
		}
		else if( Utils3D.getScreenWidth() > 400 )
		{
			pop_menu_height = getInteger( "pop_menu_height_h" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height_h" );
			pop_menu_title_height = getInteger( "pop_menu_title_height_h" );
			pop_menu_page_grid_padding_top = getInteger( "pop_menu_page_grid_padding_top_h" );
			pop_menu_page_grid_padding_bottom = getInteger( "pop_menu_page_grid_padding_bottom_h" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height_h" );
			pop_menu_container_height = getInteger( "pop_menu_container_height_h" );
			pop_menu_bottom_ind_height = getInteger( "pop_menu_bottom_ind_height_h" );
			pop_menu_bottom_ind_bar_height = getInteger( "pop_menu_bottom_ind_bar_height_h" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y_h" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y_h" );
			workspace_celllayout_offset_y = getInteger( "workspace_celllayout_offset_y_h" );
			workspace_celllayout_bg_padding_top = getInteger( "workspace_celllayout_bg_padding_top_h" );
			pop_menu_bottom_ind_height = R3D.getInteger( "pop_menu_bottom_ind_height_h" );
			pop_menu_bottom_ind_bar_height = R3D.getInteger( "pop_menu_bottom_ind_bar_height_h" );
			Workspace_celllayout_editmode_padding = R3D.getInteger( "celllayout_editmode_padding_h" );
			edit_mode_indicator_offset_4x2 = getInteger( "edit_mode_indicator_offset_4x2_h" );
			edit_mode_indicator_offset_4x1 = getInteger( "edit_mode_indicator_offset_4x1_h" );
			edit_mode_celllayout_offset = getInteger( "edit_mode_celllayout_offset_h" );
			qs_app_list_row_height = getInteger( "qs_app_list_row_height_s" );
			qs_category_grid_padding_left = getInteger( "qs_category_grid_padding_left_s" );
			qs_category_grid_padding_right = getInteger( "qs_category_grid_padding_right_s" );
			qs_category_grid_padding_top = getInteger( "qs_category_grid_padding_top_s" );
			qs_category_grid_padding_bottom = getInteger( "qs_category_grid_padding_bottom_s" );
		}
		else if( Utils3D.getScreenWidth() > 300 )
		{
			pop_menu_height = getInteger( "pop_menu_height_s" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height_s" );
			pop_menu_title_height = getInteger( "pop_menu_title_height_s" );
			pop_menu_page_grid_padding_top = getInteger( "pop_menu_page_grid_padding_top_s" );
			pop_menu_page_grid_padding_bottom = getInteger( "pop_menu_page_grid_padding_bottom_s" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height_s" );
			pop_menu_container_height = getInteger( "pop_menu_container_height_s" );
			pop_menu_bottom_ind_height = getInteger( "pop_menu_bottom_ind_height_s" );
			pop_menu_bottom_ind_bar_height = getInteger( "pop_menu_bottom_ind_bar_height_s" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y_s" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y_s" );
			workspace_celllayout_offset_y = getInteger( "workspace_celllayout_offset_y_s" );
			workspace_celllayout_bg_padding_top = getInteger( "workspace_celllayout_bg_padding_top_s" );
			pop_menu_bottom_ind_height = R3D.getInteger( "pop_menu_bottom_ind_height_s" );
			pop_menu_bottom_ind_bar_height = R3D.getInteger( "pop_menu_bottom_ind_bar_height_s" );
			Workspace_celllayout_editmode_padding = R3D.getInteger( "celllayout_editmode_padding_s" );
			edit_mode_indicator_offset_4x2 = getInteger( "edit_mode_indicator_offset_4x2_s" );
			edit_mode_indicator_offset_4x1 = getInteger( "edit_mode_indicator_offset_4x1_s" );
			edit_mode_celllayout_offset = getInteger( "edit_mode_celllayout_offset_s" );
			qs_app_list_row_height = getInteger( "qs_app_list_row_height_s" );
			qs_category_grid_padding_left = getInteger( "qs_category_grid_padding_left_s" );
			qs_category_grid_padding_right = getInteger( "qs_category_grid_padding_right_s" );
			qs_category_grid_padding_top = getInteger( "qs_category_grid_padding_top_s" );
			qs_category_grid_padding_bottom = getInteger( "qs_category_grid_padding_bottom_s" );
		}
		else
		{
			pop_menu_height = getInteger( "pop_menu_height" );
			pop_menu_indicator_height = getInteger( "pop_menu_indicator_height" );
			pop_menu_title_height = getInteger( "pop_menu_title_height_h" );
			pop_menu_page_grid_padding_top = getInteger( "pop_menu_page_grid_padding_top" );
			pop_menu_page_grid_padding_bottom = getInteger( "pop_menu_page_grid_padding_bottom" );
			pop_menu_indicator_bar_height = getInteger( "pop_menu_indicator_bar_height" );
			pop_menu_container_height = getInteger( "pop_menu_container_height" );
			pop_menu_bottom_ind_height = getInteger( "pop_menu_bottom_ind_height" );
			pop_menu_bottom_ind_bar_height = getInteger( "pop_menu_bottom_ind_bar_height" );
			workspace_offset_low_y = getInteger( "workspace_offset_low_y" );
			workspace_offset_high_y = getInteger( "workspace_offset_high_y" );
			workspace_celllayout_offset_y = getInteger( "workspace_celllayout_offset_y" );
			workspace_celllayout_bg_padding_top = getInteger( "workspace_celllayout_bg_padding_top" );
			pop_menu_bottom_ind_height = R3D.getInteger( "pop_menu_bottom_ind_height" );
			pop_menu_bottom_ind_bar_height = R3D.getInteger( "pop_menu_bottom_ind_bar_height" );
			Workspace_celllayout_editmode_padding = R3D.getInteger( "celllayout_editmode_padding" );
			edit_mode_indicator_offset_4x2 = getInteger( "edit_mode_indicator_offset_4x2" );
			edit_mode_indicator_offset_4x1 = getInteger( "edit_mode_indicator_offset_4x1" );
			edit_mode_celllayout_offset = getInteger( "edit_mode_celllayout_offset" );
			qs_app_list_row_height = getInteger( "qs_app_list_row_height_l" );
			qs_category_grid_padding_left = getInteger( "qs_category_grid_padding_left_l" );
			qs_category_grid_padding_right = getInteger( "qs_category_grid_padding_right_l" );
			qs_category_grid_padding_top = getInteger( "qs_category_grid_padding_top_l" );
			qs_category_grid_padding_bottom = getInteger( "qs_category_grid_padding_bottom_l" );
		}
		app_pop_menu_padding_top = R3D.getInteger( "app_pop_menu_padding_top" );
		app_pop_menu_padding_right = R3D.getInteger( "app_pop_menu_padding_right" );
		app_pop_menu_item_height = R3D.getInteger( "app_pop_menu_item_height" );
		app_pop_menu_item_width = R3D.getInteger( "app_pop_menu_item_width" );
		app_pop_menu_item_gap = R3D.getInteger( "app_pop_menu_item_gap" );
		app_pop_menu_img_size = R3D.getInteger( "app_pop_menu_img_size" );
		//Jone end 
		setupmenu_item_padding_y = getInteger( "setupmenu_item_padding_y" );
		setupmenu_items_btw_space = getInteger( "setupmenu_items_btw_space" );
		//teapotXu add end
		setupmenu_icon_padding_top = getInteger( "setupmenu_icon_padding_top" );//zjp
		appmenu_icon_padding_top = getInteger( "appmenu_icon_padding_top" );
		reminder_font = Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.reminder_font );
		widget_preview_title_weight = DefaultLayout.widget_title_weight;
		widget_otherTools_title = r.getString( RR.string.widget_other_tools );
		widget_shortcut_title = r.getString( RR.string.group_shortcuts );
		appbar_tab_app = r.getString( RR.string.appbar_tab_app );
		appbar_tab_widget = r.getString( RR.string.appbar_tab_widget );
		if( DefaultLayout.appbar_widgets_special_name && "CN".equals( Locale.getDefault().getCountry() ) )
			appbar_tab_widget = r.getString( RR.string.appbar_tab_widget_ex );
		appbar_tab_uninstall = r.getString( RR.string.appbar_tab_uninstall );
		//teapotXu add start for Folder in Mainmenu
		appbar_tab_edit_mode = r.getString( RR.string.appbar_tab_edit_mode );
		//teapotXu add end for Folder in Mainmenu				
		// zqh start
		app_bar_navigator_audioalbum = r.getString( RR.string.app_bar_navigator_audioalbum );
		app_bar_navigator_video = r.getString( RR.string.app_bar_navigator_video );
		// app_bar_navigator_application=r.getString(RR.string.app_bar_navigator_application);
		app_bar_navigator_photobucket = r.getString( RR.string.app_bar_navigator_photobucket );
		app_bar_title_audio = r.getString( RR.string.app_bar_title_audio );
		app_bar_title_photo = r.getString( RR.string.app_bar_title_photo );
		// zqh end
		appbar_tab_hide = r.getString( RR.string.appbar_tab_hide );
		appbar_height = getInteger( "appbar_height" );
		appbar_tab_color = Color.parseColor( getString( "appbar_tab_color" ) );
		pop_menu_title_color = Color.parseColor( getString( "pop_menu_title_color" ) );
		qs_letter_text_color = Color.parseColor( getString( "qs_letter_text_color" ) );
		qs_guess_title_text_color = Color.parseColor( getString( "qs_guess_title_text_color" ) );
		qs_all_app_list_icon_text_color = Color.parseColor( getString( "qs_all_app_list_icon_text_color" ) );
		qs_app_list_item_title_text_color = Color.parseColor( getString( "qs_app_list_item_title_text_color" ) );
		qs_no_search_result_tip_color = Color.parseColor( getString( "qs_no_search_result_tip_color" ) );
		appbar_tab_select_color = Color.parseColor( getString( "appbar_tab_select_color" ) );
		appbar_tab_pop_color = Color.parseColor( getString( "appbar_tab_pop_color" ) );
		appbar_tab_pop_select_color = Color.parseColor( getString( "appbar_tab_pop_select_color" ) );
		appbar_menu_color = Color.parseColor( getString( "appbar_menu_color" ) );
		// appbar_tab_width = getInteger("appbar_tab_width");
		appbar_tab_padding = getInteger( "appbar_tab_padding" );
		appbar_indicator_height = getInteger( "appbar_indicator_height" );
		appbar_home_right = getInteger( "appbar_home_right" );
		appbar_menu_right = getInteger( "appbar_menu_right" );
		appbar_menu_width = getInteger( "appbar_menu_width" );
		appbar_menu_height = getInteger( "appbar_menu_height" );
		// appbar_divider_width = getInteger("appbar_divider_width");
		appbar_padding_left = getInteger( "appbar_padding_left" );
		appbar_menuitem_width = getInteger( "appbar_menuitem_width" );
		appbar_menuitem_height = getInteger( "appbar_menuitem_height" );
		appbar_menuitem_paddingleft = getInteger( "appbar_menuitem_paddingleft" );
		app_widget3d_gap = getInteger( "app_widget3d_gap" );
		appbar_more_width = getInteger( "appbar_more_width" ) * Utils3D.getScreenWidth() / 720f;
		appbar_tab_width = getInteger( "appbar_tab_width" ) * Utils3D.getScreenWidth() / 720f;
		appbar_tab_popitem_height = getInteger( "appbar_tab_popitem_height" ) * Utils3D.getScreenWidth() / 720f;
		if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			Workspace_cellCountX = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_COL );
			Workspace_cellCountY = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_ROW );
			Workspace_celllayout_toppadding = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_PADDING_TOP );
			Workspace_celllayout_bottompadding = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM );
			page_indicator_y = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_WORKSPACE_INDICATOR_Y );
		}
		else
		{
			Workspace_cellCountX = r.getInteger( RR.integer.Workspace_cellCountX );
			Workspace_cellCountY = r.getInteger( RR.integer.Workspace_cellCountY );
			Workspace_celllayout_toppadding = R3D.getInteger( "celllayout_topPadding" );
			Workspace_celllayout_bottompadding = R3D.getInteger( "celllayout_bottomPadding" );
			page_indicator_y = R3D.getInteger( "page_indicator_y" );
			if( Utils3D.getScreenHeight() < 500 )
			{
				Workspace_celllayout_toppadding = R3D.getInteger( "celllayout_topPadding_small" );
				Workspace_celllayout_bottompadding = R3D.getInteger( "celllayout_bottomPadding_small" );
				page_indicator_y = R3D.getInteger( "page_indicator_y_small" );
			}
		}
		if( !DefaultLayout.enable_hotseat_rolling )
		{
			page_indicator_y -= 20;
		}
		if( Utils3D.hasMeiZuSmartBar() )
		{
			page_indicator_y = R3D.getInteger( "page_indicator_y_meizu" );
		}
		page_indicator_y_high = getInteger( "page_indicator_y_high" );
		if( DefaultLayout.dispose_cell_count )
		{
			Applist_cellCountX = DefaultLayout.cellCountX;
			Applist_cellCountY = DefaultLayout.cellCountY;
		}
		else if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			Applist_cellCountX = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_COL );
			Applist_cellCountY = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_ROW );
		}
		else
		{
			if( Utils3D.getScreenDisplayMetricsHeight() >= 800 )
			{
				Applist_cellCountX = 4;
				Applist_cellCountY = 5;
			}
			else
			{
				Applist_cellCountX = 4;
				Applist_cellCountY = 4;
			}
		}
		Applist_cellCountX = Applist_cellCountX > 5 ? 5 : Applist_cellCountX;
		Applist_cellCountY = Applist_cellCountY > 6 ? 6 : Applist_cellCountY;
		if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			applist_padding_left = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_PADDING_LEFT );
			applist_padding_right = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_PADDING_RIGHT );
			applist_padding_top = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_PADDING_TOP );
			applist_padding_bottom = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_PADDING_BOTTOM );
			applist_indicator_y = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_INDICATOR_Y );
		}
		else
		{
			if( Applist_cellCountX == 3 )
			{
				applist_padding_left = getInteger( "applist_padding_left_ex" );
				applist_padding_right = getInteger( "applist_padding_right_ex" );
			}
			else
			{
				applist_padding_left = getInteger( "applist_padding_left" );
				applist_padding_right = getInteger( "applist_padding_right" );
			}
			applist_padding_top = getInteger( "applist_padding_top" );
			applist_padding_bottom = getInteger( "applist_padding_bottom" );
			applist_indicator_y = getInteger( "applist_indicator_y" );
			if( Utils3D.getScreenHeight() > 800 )
			{
				applist_padding_top = getInteger( "applist_padding_top_large" );
				applist_padding_bottom = getInteger( "applist_padding_bottom_large" );
				applist_indicator_y = getInteger( "applist_indicator_y_large" );
			}
		}
		Workspace_cell_each_width_ori = (int)r.getDimension( RR.dimen.workspace_cell_width );
		Workspace_cell_each_height_ori = (int)r.getDimension( RR.dimen.workspace_cell_height );
		Workspace_cell_each_width = Utils3D.getScreenWidth() / R3D.Workspace_cellCountX;
		Workspace_cell_each_height = ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - Workspace_celllayout_toppadding - Workspace_celllayout_bottompadding ) / R3D.Workspace_cellCountY;
		/*
		 * 对于QVGA的屏，计算出来的Workspace_cell_each_height还没有workspace_cell_height高，
		 * 动态调整Workspace_cellCountY added by zfshi 2012-11-14
		 */
		if( Utils3D.getScreenHeight() < 400 && Workspace_cell_each_height < workspace_cell_height )
		{
			Workspace_cellCountY = Workspace_cellCountY - 1;
			Workspace_cell_each_height = ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - Workspace_celllayout_toppadding - Workspace_celllayout_bottompadding ) / R3D.Workspace_cellCountY;
		}
		ConfigBase.Workspace_cell_each_height = Workspace_cell_each_height;
		ConfigBase.Workspace_cell_each_width = Workspace_cell_each_width;
		ConfigBase.Workspace_cell_each_height_ori = Workspace_cell_each_height_ori;
		ConfigBase.Workspace_cell_each_width_ori = Workspace_cell_each_width_ori;
		if( workspace_cell_width > ( Utils3D.getScreenWidth() - applist_padding_left - applist_padding_right ) / Applist_cellCountX )
		{
			workspace_cell_width = ( Utils3D.getScreenWidth() - applist_padding_left - applist_padding_right ) / Applist_cellCountX;
		}
		if( workspace_cell_height > Workspace_cell_each_height )
		{
			workspace_cell_height = Workspace_cell_each_height;
		}
		seatbar_hide_height = workspace_cell_height * DefaultLayout.seatbar_hide_height_ratio;
		def_layout_y = (int)r.getDimension( RR.dimen.def_layout_y );
		def_layout_y_dura = (int)r.getDimension( RR.dimen.def_layout_y_dura );
		// sidebar_widget_w = (int) r.getDimension(RR.dimen.app_icon_size);
		// sidebar_widget_h = (int) r.getDimension(RR.dimen.app_icon_size);
		sidebar_widget_w = sidebar_widget_h = Utilities.sIconTextureWidth;
		;
		icongroup_margin_left = getInteger( "icongroup_margin_left" );
		icongroup_margin_top = getInteger( "icongroup_margin_top" );
		// icongroup_cell_width = getInteger("icongroup_cell_width");
		// icongroup_cell_height = getInteger("icongroup_cell_height");
		icongroup_bottom_limit = getInteger( "icongroup_bottom_limit" );
		circle_autoSort = r.getString( RR.string.circle_autoSort );
		circle_overLap = r.getString( RR.string.circle_iconOverlap );
		circle_iconTrans = r.getString( RR.string.circle_iconTrans );
		circle_delAll = r.getString( RR.string.circle_deleteAll );
		circle_multiSelect = r.getString( RR.string.circle_multiSelect );
		circle_createFolder = r.getString( RR.string.circle_createFolder );
		circle_DstOverToast = r.getString( RR.string.circle_dstOverToast );
		circle_selectMutiToOperToast = r.getString( RR.string.circle_selectMutiToOperToast );
		circle_notSupportToast = r.getString( RR.string.circle_notSupportToast );
		circle_unselectAppIconToast = r.getString( RR.string.circle_unselectAppIconToast );
		page_indicator_radius = getInteger( "page_indicator_radius" );
		pageselect_origin_x = getInteger( "page_indicator_origin_x" );
		pageselect_origin_y = getInteger( "page_indicator_origin_y" );
		pageselect_canNotAddWidgetToast = r.getString( RR.string.can_not_add_widget );
		pageselect_canNotDeletePage = r.getString( RR.string.can_not_delete_page );
		trash_cap_x = getInteger( "trash_cap_x" );
		trash_cap_y = getInteger( "trash_cap_y" );
		trash_cap_w = getInteger( "trash_cap_w" );
		trash_cap_h = getInteger( "trash_cap_h" );
		hot_obj_origin_z = getInteger( "hot_obj_origin_z" );
		hot_obj_height = getInteger( "hot_obj_height" );
		if( RR.net_version )
		{
			hot_obj_height = (int)( 179 * Utils3D.getScreenWidth() / 720.0f );
		}
		hot_obj_rot_deg = getInteger( "hot_obj_rot_deg" );
		hot_obj_trans_y = getInteger( "hot_obj_trans_y" );
		if( Utils3D.getScreenHeight() < 700 )
			hot_obj_trans_y = getInteger( "hot_obj_trans_y_small" );
		else if( Utils3D.getScreenHeight() > 800 )
			hot_obj_trans_y = getInteger( "hot_obj_trans_y_large" );
		hot_obj_trans_z = getInteger( "hot_obj_trans_z" );
		hot_grid_left_margin = getInteger( "hot_grid_left_margin" );
		hot_grid_right_margin = getInteger( "hot_grid_right_margin" );
		hot_frontgrid_origin_z = getInteger( "hot_frontgrid_origin_z" );
		hot_dock_icon_size = Utilities.sIconTextureWidth;
		hot_dock_trans_y = getInteger( "hot_dock_trans_y" );
		hot_grid_bottom_margin = getInteger( "hot_grid_bottom_margin" );
		if( Utils3D.getScreenWidth() > 400 )
			hot_grid_bottom_margin = getInteger( "hot_grid_bottom_margin_large" );
		if( !DefaultLayout.enable_hotseat_rolling )
		{
			if( DefaultLayout.hotseat_hide_title )
			{
				hot_grid_bottom_margin -= 20;
			}
			else
			{
				hot_grid_bottom_margin = 0;
			}
		}
		icon_title_gap = (int)getInteger( "icon_title_gap" );
		if( icon_title_gap == -1 )
		{
			icon_title_gap = 10;
		}
		folder_name = r.getString( RR.string.folder_name );
		contact_name = r.getString( RR.string.contact_name );
		hot_sidebar_top_margin = getInteger( "hot_sidebar_top_margin" );
		tip_point_width = getInteger( "tip_point_width" );
		tip_point_height = getInteger( "tip_point_height" );
		icon_bg_num = getInteger( "icon_bg_num" );
		hot_dock_item_num = getInteger( "hot_dock_item_num" );//zjp
		// teapotXu_20130206: add start
		theme_thirdapk_icon_scaleFactor = getInteger( "theme_thirdapk_icon_scaleFactor" );
		// here reset the value of thirdapk_icon_scaleFactor by
		// theme_thirdapk_icon_scaleFactor.
		//		if(Utils3D.getScreenWidth()==800){
		//			DefaultLayout.thirdapk_icon_scaleFactor = 0.98f;
		//		}
		//		else 
		if( theme_thirdapk_icon_scaleFactor != -1 )
		{
			DefaultLayout.thirdapk_icon_scaleFactor = theme_thirdapk_icon_scaleFactor / 100f;
		}
		else
		{
			DefaultLayout.thirdapk_icon_scaleFactor = 0.70f;
		}
		// teapotXu_20130206: add end
		bottom_bar_height = getInteger( "bottombar_height" );
		bottom_bar_title_height = getInteger( "bottombar_title_height" );
		//teapotXu add for butter fly style
		page_edit_ygap234 = R3D.getInteger( "page_edit_ygap234" );
		if( Utils3D.getScreenHeight() <= 800 )
			page_edit_ygap567 = R3D.getInteger( "page_edit_ygap567_small" );
		else
		{
			page_edit_ygap567 = R3D.getInteger( "page_edit_ygap567" );
		}
		page_edit_xgap234 = R3D.getInteger( "page_edit_xgap234" );
		page_edit_xgap567 = R3D.getInteger( "page_edit_xgap567" );
		//teapotXu add end
		hot_top_ascent_distance_hide_title = R3D.getInteger( "hot_top_ascent_distance_hide_title" );
		if( getString( "addList_item_text_color" ) == null )
		{
			addList_item_text_color = Color.WHITE;
		}
		else
		{
			addList_item_text_color = Color.parseColor( getString( "addList_item_text_color" ) );
		}
		if( getString( "folder_rename_text_color" ) == null )
		{
			folder_rename_text_color = Color.WHITE;
		}
		else
		{
			folder_rename_text_color = Color.parseColor( getString( "folder_rename_text_color" ) );
		}
		page_indicator_size = R3D.getInteger( "page_indicator_size" );
		page_indicator_focus_w = R3D.getInteger( "page_indicator_focus_w" );
		page_indicator_normal_w = R3D.getInteger( "page_indicator_normal_w" );
		page_indicator_focus_h = R3D.getInteger( "page_indicator_focus_h" );
		page_indicator_normal_h = R3D.getInteger( "page_indicator_normal_h" );
		page_indicator_style = R3D.getInteger( "page_indicator_style" );
		page_indicator_total_size = R3D.getInteger( "page_indicator_total_size" );
		if( page_indicator_style == 2 )
		{// S2
			page_indicator_size = R3D.getInteger( "page_indicator_size_s2" );
			page_indicator_focus_w = R3D.getInteger( "page_indicator_focus_w_s2" );
			page_indicator_normal_w = R3D.getInteger( "page_indicator_normal_w_s2" );
		}
		if( DefaultLayout.enable_DesktopIndicatorScroll )
		{
			s4_page_indicator_bg_height = R3D.getInteger( "s4_page_indicator_bg_height" );
			s4_page_indicator_scroll_width = R3D.getInteger( "s4_page_indicator_scroll_width" );
			s4_page_indicator_scroll_height = R3D.getInteger( "s4_page_indicator_scroll_height" );
			s4_page_indicator_number_bg_size = R3D.getInteger( "s4_page_indicator_number_bg_size" );
			s4_page_indicator_number_x_offset = R3D.getInteger( "s4_page_indicator_number_x_offset" );
			if( s4_page_indicator_number_x_offset == -1 )
			{
				s4_page_indicator_number_x_offset = Tools.dip2px( iLoongLauncher.getInstance() , 2 );
			}
		}
		int folder_max_height = Utils3D.getScreenHeight()
		// - R3D.workspace_cell_height - R3D.icongroup_button_height / 2
		- R3D.folder_group_text_height - R3D.workspace_cell_height - R3D.folder_group_top_round - R3D.icongroup_margin_top - R3D.icongroup_margin_left;
		//zhujieping add
		if( folder_group_child_count_x <= 0 )
		{
			folder_group_child_count_x = 4;
		}
		if( Utils3D.getScreenHeight() < 500 )
		{
			// hot_grid_bottom_margin = 0;
			hot_obj_height = R3D.workspace_cell_height;
			page_indicator_focus_w = R3D.getInteger( "page_indicator_focus_w_small" );
			page_indicator_normal_w = R3D.getInteger( "page_indicator_normal_w_small" );
			if( page_indicator_style == 2 )
			{// S2
				// page_indicator_size =
				// R3D.getInteger("page_indicator_size_s2");
				page_indicator_focus_w = R3D.getInteger( "page_indicator_focus_w_s2_small" );
				page_indicator_normal_w = R3D.getInteger( "page_indicator_normal_w_s2_small" );
			}
		}
		/* added by zfshi ended 2012-11-14 */
		folder_front_width = R3D.workspace_cell_width;
		// xiatian start // explorer to adaptive difference resolution
		// xiatian del start
		// /*
		photo_width = R3D.getInteger( "photo_width" );
		photo_height = R3D.getInteger( "photo_height" );
		photo_padding = R3D.getInteger( "photo_padding" );
		photo_bucket_width = R3D.getInteger( "photo_bucket_width" );
		photo_bucket_height = R3D.getInteger( "photo_bucket_height" );
		photo_title_size = R3D.getInteger( "photo_title_size" );
		photo_title_line = R3D.getInteger( "photo_title_line" );
		audio_width = R3D.getInteger( "audio_width" );
		audio_height = R3D.getInteger( "audio_height" );
		video_width = R3D.getInteger( "video_width" );
		video_height = R3D.getInteger( "video_height" );
		video_padding = R3D.getInteger( "video_padding" );
		audio_bottom_padding = R3D.getInteger( "audio_bottom_padding" );
		audio_left_padding = R3D.getInteger( "audio_left_padding" );
		// xiatian add start //DownloadIcon
		dynamic_menu_download_icon_width = (int)r.getDimension( RR.dimen.dynamic_menu_download_icon_width );
		dynamic_menu_download_icon_height = (int)r.getDimension( RR.dimen.dynamic_menu_download_icon_height );
		// xiatian add end
		//xiatian add start	//adjust third apk icon offset when have iconbg
		Third_APK_Icon_Offset_X = getInteger( "third_apk_icon_offset_x" );
		if( Third_APK_Icon_Offset_X == -999 )
		{
			Third_APK_Icon_Offset_X = 0;
		}
		Third_APK_Icon_Offset_Y = getInteger( "third_apk_icon_offset_y" );
		if( Third_APK_Icon_Offset_Y == -999 )
		{
			Third_APK_Icon_Offset_Y = 0;
		}
		//xiatian add end
		//xiatian add start	//folder transform icon offset
		Folder_Transform_Icon_Offset_X = getInteger( "folder_transform_icon_offset_x" );
		if( Folder_Transform_Icon_Offset_X == -999 )
		{
			Folder_Transform_Icon_Offset_X = 0;
		}
		Folder_Transform_Icon_Offset_Y = getInteger( "folder_transform_icon_offset_y" );
		if( Folder_Transform_Icon_Offset_Y == -999 )
		{
			Folder_Transform_Icon_Offset_Y = 0;
		}
		//xiatian add end
		if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
		{
			folder_front_height = getInteger( "folder_front_two_line_rotate_height" );
			// if (Utils3D.getScreenHeight() > 800)
			// folder_front_height = getInteger("folder_front_height_large");
		}
		else
		{
			folder_front_height = R3D.workspace_cell_height;
		}
		initDefaultValue();
		Utils3D.showTimeFromStart( "r3d init1 1" );
		launcher.loadHotseats( DefaultLayout.defaultUri );
		Utils3D.showTimeFromStart( "r3d init1 2" );
		packerAtlas = new TextureAtlas();
		// packer = new MyPixmapPacker(packageInfos.size(), Format.RGBA8888, 1,
		// false);
		packer = new BitmapPacker(
				iLoongLauncher.getInstance() ,
				40 ,
				1 ,
				false ,
				R3D.filter ,
				R3D.Magfilter ,
				R3D.workspace_cell_width ,
				R3D.workspace_cell_height ,
				DefaultLayout.enable_texture_pack ,
				iLoongLauncher.getInstance().mainThreadId ,
				iLoongLauncher.getInstance().getApplicationInfo().dataDir + "/" );
		pack( "app-default-icon" , IconCache.makeDefaultIcon() );
		pack(
				"app-default-icon-with-title" ,
				Utils3D.IconToPixmap3D( IconCache.makeDefaultIcon() , iLoongLauncher.getInstance().getResources().getString( RR.string.app_unknow ) , Icon3D.getIconBg() , Icon3D.titleBg ) );
		pack( R3D.folder3D_name , FolderIcon3D.titleToTexture( R3D.folder3D_name , Color.WHITE ) );
		for( int i = 1 ; i <= 5 ; i++ )
		{
			pack( "" + i + "x.z" , FolderIcon3D.titleToTexture( "" + i , Color.WHITE ) );
		}
		pack( "move_to_left_screen_bar_bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/move_to_left_screen_bar_bg.png" ) );
		pack( "move_to_right_screen_bar_bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/move_to_right_screen_bar_bg.png" ) );
		if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
		{
			pack( "widget-folder-bg2" , ThemeManager.getInstance().getBitmap( "theme/folder/widget-folder-bg2.png" ) );
		}
		if( !DefaultLayout.net_lite )
			pack( "icon_focus" , ThemeManager.getInstance().getBitmap( "theme/pack_source/focus_bg.png" ) );
		//xiatian add start	//New AppList Popmenu
		applist_menu_color = Color.parseColor( getString( "applist_menu_color" ) );
		applist_menu_padding_top = getInteger( "applist_menu_padding_top" );
		applist_menu_height = getInteger( "applist_menu_height" );
		//xiatian add end
		//zhujieping add start
		if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) )
		{
			//			Bitmap oriBitmap = ThemeManager.getInstance().getBitmap( "theme/folder/folder-open-screen-bg.png" );
			//			Bitmap screenBackBitmap = null;
			//			if( oriBitmap != null )
			//			{
			//				if( Utils3D.getScreenWidth() != oriBitmap.getWidth() || Utils3D.getScreenHeight() != oriBitmap.getHeight() )
			//				{
			//					screenBackBitmap = Tools.resizeBitmap( oriBitmap , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
			//					oriBitmap.recycle();
			//				}
			//				else
			//				{
			//					screenBackBitmap = oriBitmap;
			//				}
			//				screenBackBitmap = oriBitmap;
			//			}
			folder_opend_bg_alpha = getInteger( "folder_opend_bg_alpha" );
			if( folder_opend_bg_alpha == -1 )
			{
				folder_opend_bg_alpha = 0xdd;
			}
			Bitmap screenBackBitmap = Bitmap.createBitmap( 1 , 1 , Bitmap.Config.ARGB_8888 );
			int color = Color.argb( folder_opend_bg_alpha , 0 , 0 , 0 );
			screenBackBitmap.eraseColor( color );
			if( screenBackBitmap != null )
			{
				screenBackRegion = new TextureRegion( new BitmapTexture( screenBackBitmap ) );
				screenBackBitmap.recycle();
			}
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode == true )
		{
			R3D.pack( "pageedit-homeicon-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/pageedit-homeicon-bg.png" ) );
			R3D.pack( "miui-addpage" , ThemeManager.getInstance().getBitmap( "theme/miui_source/addpage.png" ) );
			R3D.pack( "miui-addpage-focus" , ThemeManager.getInstance().getBitmap( "theme/miui_source/addpage-focus.png" ) );
			R3D.pack( "miui-delpage" , ThemeManager.getInstance().getBitmap( "theme/miui_source/delpage.png" ) );
			R3D.pack( "miui-delpage-focus" , ThemeManager.getInstance().getBitmap( "theme/miui_source/delpage-focus.png" ) );
			R3D.pack( "miui-leftArrow" , ThemeManager.getInstance().getBitmap( "theme/miui_source/leftarrow-begin.png" ) );
			R3D.pack( "miui-rightArrow" , ThemeManager.getInstance().getBitmap( "theme/miui_source/rightarrow-begin.png" ) );
			R3D.pack( "miui-shadow-leftArrow" , ThemeManager.getInstance().getBitmap( "theme/miui_source/leftarrow.png" ) );
			R3D.pack( "miui-shadow-rightArrow" , ThemeManager.getInstance().getBitmap( "theme/miui_source/rightarrow.png" ) );
			R3D.pack( "miui-input-ack" , ThemeManager.getInstance().getBitmap( "theme/miui_source/miui-input-ack.png" ) );
			R3D.pack( "miui-input-ack-focus" , ThemeManager.getInstance().getBitmap( "theme/miui_source/miui-input-ack-focus.png" ) );
			for( int i = 1 ; i <= 4 ; i++ )
			{
				for( int j = 1 ; j <= 4 ; j++ )
				{
					R3D.pack( "miui-widget-indicator" + i + j , ThemeManager.getInstance().getBitmap( "theme/miui_source/widget-" + i + j + ".png" ) );
				}
			}
		}
		R3D.pack( "cursor_patch" , ThemeManager.getInstance().getBitmap( "theme/pack_source/cursor_patch.png" ) );
		//zhujieping add end
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			R3D.pack( "widget-folder-open-line" , ThemeManager.getInstance().getBitmap( "theme/pack_source/widget-folder-open-line.png" ) );
			R3D.pack( "miui-input-ack" , ThemeManager.getInstance().getBitmap( "theme/miui_source/miui-input-ack.png" ) );
			R3D.pack( "miui-input-ack-focus" , ThemeManager.getInstance().getBitmap( "theme/miui_source/miui-input-ack-focus.png" ) );
		}
		R3D.pack( folder_bg_name , ThemeManager.getInstance().getBitmap( "theme/folder/folder_bg.9.png" ) );
	}
	
	private static void initDefaultValue()
	{
		if( !DefaultLayout.show_icon_size )
		{
			int app_icon_size = R3D.getInteger( "app_icon_size" );
			if( app_icon_size != -1 && app_icon_size != DefaultLayout.app_icon_size )
			{
				Utilities.reInitStatics( app_icon_size );
				DefaultLayout.app_icon_size = app_icon_size;
			}
		}
		hot_dock_icon_number = DefaultLayout.hot_dock_icon_number;
		if( hot_dock_icon_number == 4 )
		{
			DefaultLayout.enable_hotseat_middle_icon_horizontal = true;
		}
	}
	
	public static void initialize2(
			iLoongLauncher c )
	{
		Utils3D.showTimeFromStart( "start pack" );
		R3D.pack( "page-add-icon" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-add-icon.png" ) );
		//		R3D.pack(
		//				"page-controlle-b",
		//				ThemeManager.getInstance().getBitmap(
		//						"theme/pack_source/page-controlle-b.png"));
		R3D.pack( "page-controlle-c" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-controlle-c.png" ) );
		R3D.pack( "page-controller-indicator-a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-controller-indicator-a.png" ) );
		R3D.pack( "page-controller-indicator-b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-controller-indicator-b.png" ) );
		R3D.pack( "page-edit3" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit3.png" ) );
		R3D.pack( "public-button-return" , ThemeManager.getInstance().getBitmap( "theme/pack_source/public-button-return.png" ) );
		if( !DefaultLayout.disable_circled )
		{
			R3D.pack( "shell-interactive-grid-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-interactive-grid-bg.png" ) );
			R3D.pack( "shell-interactive-grid-scale-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-interactive-grid-scale-button.png" ) );
			R3D.pack( "shell-picker-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-bg.png" ) );
			R3D.pack( "shell-picker-connect-line" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-connect-line.png" ) );
			R3D.pack( "shell-picker-menu-item1" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item1.png" ) );
			R3D.pack( "shell-picker-menu-item2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item2.png" ) );
			R3D.pack( "shell-picker-menu-item3a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item3a.png" ) );
			R3D.pack( "shell-picker-menu-item3b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item3b.png" ) );
			R3D.pack( "shell-picker-menu-item4a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item4a.png" ) );
			R3D.pack( "shell-picker-menu-item4b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item4b.png" ) );
			R3D.pack( "shell-picker-menu-item5a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item5a.png" ) );
			R3D.pack( "shell-picker-menu-item5b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item5b.png" ) );
			R3D.pack( "shell-picker-menu-item6a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item6a.png" ) );
			R3D.pack( "shell-picker-menu-item6b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item6b.png" ) );
			R3D.pack( "shell-picker-menu-item7a" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item7a.png" ) );
			R3D.pack( "shell-picker-menu-item7b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item7b.png" ) );
		}
		R3D.pack( "shell-select-page-bg-select" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-select-page-bg-select.png" ) );
		R3D.pack( "shell-select-page-bg-unselect" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-select-page-bg-unselect.png" ) );
		R3D.pack( "tip-point" , ThemeManager.getInstance().getBitmap( "theme/pack_source/tip-point.png" ) );
		if( DefaultLayout.show_widget_shortcut_bg )
			R3D.pack( "widget-shortcut-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/widget-shortcut-bg.png" ) );
		R3D.pack( "delete-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/delete_button.png" ) );
		R3D.pack( "home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/home_button.png" ) );
		R3D.pack( "more-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/more_button.png" ) );
		if( Utils3D.getScreenHeight() > 700 )
		{
			R3D.pack( "page-edit2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit2.png" ) );
			R3D.pack( "page-edit2b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit2b.png" ) );
			R3D.pack( "app-menu-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-menu-button.png" ) );
			R3D.pack( "app-menu-downarray" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-downarray.png" ) );
			R3D.pack( "app-menu-bag" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-bag.png" ) );
			if( Utils3D.getScreenHeight() > 854 )
			{
				R3D.pack( "app-home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-button-larges.png" ) );
				R3D.pack( "app-home-personal" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-personal-large.png" ) );
			}
			else
			{
				R3D.pack( "app-home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-buttons.png" ) );
				R3D.pack( "app-home-personal" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-personal.png" ) );
			}
		}
		else
		{
			R3D.pack( "page-edit2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit2-small.png" ) );
			R3D.pack( "page-edit2b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/page-edit2b-small.png" ) );
			R3D.pack( "app-menu-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-menu-button-small.png" ) );
			R3D.pack( "app-menu-downarray" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-downarray-small.png" ) );
			R3D.pack( "app-menu-bag" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-bag-small.png" ) );
			R3D.pack( "app-home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-button-smalls.png" ) );
			R3D.pack( "app-home-personal" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-personal-small.png" ) );
		}
		R3D.pack( "app-uninstall" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall.png" ) );
		R3D.pack( "appbar-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-bgs.png" ) );
		R3D.pack( "appbar-tabbar-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tabbar-bg.png" ) );
		R3D.pack( "appbar-tab-select-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-select-bg.png" ) );
		R3D.pack( "appbar-divider" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-divider.png" ) );
		R3D.pack( "appbar-tab-divider" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-divider.png" ) );
		R3D.pack( "app-item-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-item-bg.png" ) );
		R3D.pack( "appbar-indicator" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-indicator.png" ) );
		R3D.pack( "appbar-tab-arrow" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-arrow.png" ) );
		R3D.pack( "appbar-more-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-more-button.png" ) );
		R3D.pack( "appbar-more-button-select" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-more-button-select.png" ) );
		R3D.pack( "appbar-tab-pop-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-pop-bg.png" ) );
		R3D.pack( "appbar-tab-pop-item-select-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-pop-item-select-bg.png" ) );
		R3D.pack( "appbar-tab-pop-item-divider" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-pop-item-divider.png" ) );
		//		R3D.pack( "app-sort-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-sort-button.png" ) );
		//		R3D.pack( "app-uninstall-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall-button.png" ) );
		//		R3D.pack( "app-hide-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-hide-button.png" ) );
		if( DefaultLayout.appbar_show_userapp_list == true )
		{
			R3D.pack( "appbar-navi-back" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-show-userapp-navigation-back.png" ) );
		}
		else
		{
			R3D.pack( "appbar-navi-back" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-navigation-back.png" ) );
		}
		R3D.pack( "workspace-reflect-view" , ThemeManager.getInstance().getBitmap( "theme/home/homescreen_blue_strong_holo.png" ) );
		R3D.pack( "workspace-zoom-view" , ThemeManager.getInstance().getBitmap( "theme/home/workspace_zoom_view.png" ) );
		R3D.pack( "workspace-zoomarrow" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow.png" ) );
		R3D.pack( "workspace-zoomarrow_top" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow_top.png" ) );
		R3D.pack( "workspace-zoomarrow_bottom" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow_bottom.png" ) );
		R3D.pack( "workspace-zoomarrow_left" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow_left.png" ) );
		R3D.pack( "workspace-zoomarrow_right" , ThemeManager.getInstance().getBitmap( "theme/home/zoomarrow_right.png" ) );
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_MIDDLE )
		{
			R3D.pack( "trash-background" , ThemeManager.getInstance().getBitmap( "theme/pack_source/trash-background-middle.png" ) );
		}
		else if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
		}
		else
		{
			R3D.pack( "trash-background" , ThemeManager.getInstance().getBitmap( "theme/pack_source/trash-background.png" ) );
		}
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			if( DefaultLayout.generate_new_folder_in_top_trash_bar == true )
			{
				if( !DefaultLayout.isScaleBitmap )
				{
					//R3D.pack( "create_folder" , ThemeManager.getInstance().getBitmap( "theme/pack_source/create_folder.png" ) );
					//R3D.pack( "create_folder2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/create_folder2.png" ) );
					R3D.pack( "xiezai-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg-top.png" ) );
					R3D.pack( "xiezai-bg2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg2-top.png" ) );
					//R3D.pack( "xiezai-bg_screen_width" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg-top2.png" ) );
					//R3D.pack( "xiezai-bg2_screen_width" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg2-top2.png" ) );
				}
				int trash_title_height = Tools.dip2px( iLoongLauncher.getInstance() , 36 );
				R3D.pack(
						R3D.getString( RR.string.Create_folder ) ,
						AppBar3D.titleToPixmap( R3D.getString( RR.string.Create_folder ) , trash_title_height , false , Color.parseColor( "#FFFFFF" ) , false ) );
				R3D.pack( R3D.getString( RR.string.Remove ) , AppBar3D.titleToPixmap( R3D.getString( RR.string.Remove ) , trash_title_height , false , Color.parseColor( "#FFFFFF" ) , false ) );
			}
			else
			{
				R3D.pack( "xiezai-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg.png" ) );
				R3D.pack( "xiezai-bg2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg2.png" ) );
			}
		}
		int tmpH = (int)( ( Utils3D.getScreenHeight() - R3D.appbar_height - R3D.applist_padding_top - R3D.applist_padding_bottom ) / 3 ) - R3D.app_widget3d_gap;
		for( int i = 1 ; i < 6 ; i++ )
		{
			R3D.pack( i + "" , AppBar3D.titleToPixmap( i + "" , (int)( R3D.widget_preview_title_weight * tmpH ) , false , Color.WHITE , true ) );
		}
		R3D.pack( "x" , AppBar3D.titleToPixmap( "x" , (int)( R3D.widget_preview_title_weight * tmpH ) , false , Color.WHITE , true ) );
		//xiatian add start //EffectPreview
		if( DefaultLayout.enable_effect_preview )
		{
			R3D.pack( mEffectPreviewBgRegionName , ThemeManager.getInstance().getBitmap( "theme/pack_source/effect_preview_bg.png" ) );
			R3D.pack( mEffectPreviewButtonRegionName , ThemeManager.getInstance().getBitmap( "theme/pack_source/effect_preview_button.png" ) );
			R3D.pack( mEffectPreviewButtonFocusRegionName , ThemeManager.getInstance().getBitmap( "theme/pack_source/effect_preview_button_focus.png" ) );
			R3D.pack( mEffectPreviewSelectRegionName , ThemeManager.getInstance().getBitmap( "theme/pack_source/effect_preview_select.png" ) );
		}
		//xiatian add end	
		//xiatian add start //for mainmenu sort by user
		if( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			R3D.pack( "app-to-workspace-normal" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-to-workspace-normal.png" ) );
			R3D.pack( "app-to-workspace-focus" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-to-workspace-focus.png" ) );
		}
		//xiatian add end
		R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
		Utils3D.showTimeFromStart( "end pack" );
	}
}
