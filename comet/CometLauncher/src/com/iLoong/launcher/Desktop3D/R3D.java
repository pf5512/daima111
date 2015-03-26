package com.iLoong.launcher.Desktop3D;


import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapPacker;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherSettings;
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
	public static int Workspace_celllayout_toppadding;
	public static int Workspace_celllayout_bottompadding;
	// public static int celllayout_grid_height;
	public static int workspace_cell_width;
	public static int workspace_cell_height;
	public static int workspace_cell_adjust;
	public static int workspace_multicon_width;
	public static int workspace_multicon_height;
	public static int workspace_paopao_big_width;
	public static int workspace_paopao_num_width;
	public static int workspace_paopao_num_height;
	public static int workspace_multiviews_offset;
	public static int icon_title_font;
	public static int reminder_font;
	public static int pageview_indicator_height;
	public static float widget_preview_title_weight = 0.25f;
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
	public static int appbar_menu_color;
	public static int folder_title_color;
	public static int appbar_height;
	// public static int appbar_tab_width;
	public static int appbar_tab_padding;
	public static int appbar_indicator_height;
	public static int appbar_home_right;
	public static int appbar_menu_right;
	public static int appbar_menu_width;
	public static int appbar_menu_height;
	public static int appbar_menuitem_height;
	public static int appbar_menuitem_width;
	public static int appbar_menuitem_paddingleft;
	// public static int appbar_divider_width;
	public static int appbar_padding_left;
	public static int applist_padding_left;
	public static int applist_padding_right;
	public static int applist_padding_left_ex;
	public static int applist_padding_right_ex;
	public static int applist_padding_top;
	public static int applist_padding_bottom;
	public static int applist_indicator_y;
	public static int app_widget3d_gap;
	public static int sidebar_height;
	public static int sidebar_button_width;
	public static int sidebar_button_height;
	public static int sidebar_button_icon_width;
	public static int sidebar_button_icon_height;
	public static int sidebar_button_icon1_x;
	public static int sidebar_button_icon1_y;
	public static int sidebar_button_icon2_x;
	public static int sidebar_button_icon2_y;
	public static int sidebar_button_foucs_size;
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
	public static int trash_icon_width;
	public static int trash_icon_height;
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
	public static int folder_max_num;
	public static int folder_name_length_max = 24;
	// public static int folder_title_font;
	public static String folder3D_name;
	public static String folder3D_full;
	public static int folder_icon_scale_factor;
	public static String folder_bg = "folder-bg";
	public static String folder_cover = "folder-cover";
	public static int trash_cap_x;
	public static int trash_cap_y;
	public static int trash_cap_w;
	public static int trash_cap_h;
	public static int icon_bg_num;
	// public static int icon_bg_width;
	// public static int icon_bg_height;
	// teapotXu_20130206: add start
	// this variable is used to replace the one defined in DefaultLayout.java:
	// thirdapk_icon_scaleFactor
	public static int theme_thirdapk_icon_scaleFactor;
	// teapotXu_20130206: add end
	public static int vibrator_duration = 25;//50;
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
	public static int hot_dock_grid_pos_y;
	public static int page_indicator_size;
	public static int page_indicator_y;
	public static int page_indicator_focus_w;
	public static int page_indicator_normal_w;
	public static int page_indicator_style;
	public static int page_indicator_total_size;
	public static int s4_page_indicator_bg_height;
	public static int s4_page_indicator_scroll_width;
	public static int s4_page_indicator_scroll_height;
	public static int s4_page_indicator_number_bg_size;
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
	public static int folder_group_child_count_x;
	public static int folder_group_child_count_y;
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
	// desktopEdit added by zhenNan.ye begin
	public static String desktopEdit_canNotDeletePage;
	public static String desktopEdit_page_bg = "page-bg";
	public static String desktopEdit_page_home = "page-home";
	public static String desktopEdit_page_cur_home = "page-cur-home";
	public static String desktopEdit_page_home_hilight = "page-home-hilight";
	public static String desktopEdit_page_add = "page-add";
	public static String desktopEdit_page_add_hilight = "page-add-hilight";
	public static String desktopEdit_mode_plane = "editMode-plane";
	public static String desktopEdit_mode_plane_hilight = "editMode-plane-hilight";
	public static String desktopEdit_mode_cylinder = "editMode-cylinder";
	public static String desktopEdit_mode_cylinder_hilight = "editMode-cylinder-hilight";
	public static String desktopEdit_page_groove = "page-groove";
	public static String dockbar_add_applications = "add_applications";
	public static String dockbar_add_app_bg = "add_app_bg";
	public static String dockbar_add_button_back_hilight = "add_button_back_hilight";
	public static String dockbar_add_button_back = "add_button_back";
	public static String dockbar_add_button_cancel_hilight = "add_button_cancel_hilight";
	public static String dockbar_add_button_cancel = "add_button_cancel";
	public static String dockbar_add_button_done_hilight = "add_button_done_hilight";
	public static String dockbar_add_button_done = "add_button_done";
	public static String dockbar_add_shortcut = "add_shortcut";
	public static String dockbar_add_shortcut_bg = "add_shortcut_bg";
	public static String dockbar_comet_shortcut = "comet_shortcut";
	public static String dockbar_comet_shortcut_hilight = "comet_shortcut_hilight";
	public static String dockbar_system_shortcut = "system_shortcut";
	public static String dockbar_system_shortcut_hilight = "system_shortcut_hilight";
	public static String dockbar_selected_table1 = "selected_table1";
	public static String dockbar_unselected_table1 = "unselected_table1";
	public static String dockbar_selected_table2 = "selected_table2";
	public static String dockbar_unselected_table2 = "unselected_table2";
	public static String apphost_bg = "apphost_black_bg";
	//编辑模式
	public static String dockbar_editmode_add_bg = "dockbar_editmode_add_bg";
	//添加Widget
	public static String dockbar_editmode_addwdiget_comet = "dockbar_editmode_addwdiget_comet";
	public static String dockbar_editmode_addwdiget_cometed = "dockbar_editmode_addwdiget_cometed";
	public static String dockbar_editmode_addwdiget_sys = "dockbar_editmode_addwdiget_sys";
	public static String dockbar_editmode_addwdiget_sysed = "dockbar_editmode_addwdiget_sysed";
	public static String dockbar_editmode_addwdiget_title = "dockbar_editmode_addwdiget_title";
	//添加Widget end
	//添加文件夹
	public static String dockbar_editmode_addfolder_cancel = "dockbar_editmode_addfolder_cancel";
	public static String dockbar_editmode_addfolder_canceled = "dockbar_editmode_addfolder_canceled";
	public static String dockbar_editmode_addfolder_exit = "dockbar_editmode_addfolder_exit";
	public static String dockbar_editmode_addfolder_exited = "dockbar_editmode_addfolder_exited";
	public static String dockbar_editmode_addfolder_done = "dockbar_editmode_addfolder_done";
	public static String dockbar_editmode_addfolder_doned = "dockbar_editmode_addfolder_doned";
	public static String dockbar_editmode_addfolder_frame = "dockbar_editmode_addfolder_frame";
	public static String dockbar_editmode_addfolder_frame2 = "dockbar_editmode_addfolder_frame2";
	public static String dockbar_editmode_addfolder_input = "dockbar_editmode_addfolder_input";
	//添加文件夹 end
	//编辑模式 end
	// desktopEdit added zhenNan.ye end
	public static int specifiedScreenWidth = 720;
	
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
		// name = info.intent.toString();
		return name;
	}
	
	public static String getString(
			int resID )
	{
		return iLoongLauncher.getInstance().getString( resID );
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
		String suffix = "";
		Bitmap titleBg = Icon3D.titleBg;
		if( R3D.packer == null )
		{
			// Log.e("launcher", "pack=null");
			return false;
		}
		if( R3D.packer.getRect( getInfoName( info ) ) != null && !R3D.packer.getRect( getInfoName( info ) ).isDefault )
		{
			// Log.e("launcher", "hasPack");
			return false;
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
		if( DefaultLayout.hotseat_title_no_background == true )
		{
			titleBg = null;
		}
		//teapotXu_20130328 add start: 增加判断是否是HotSeat Icon且更换了图标
		if( info.intent != null && info.hotseatDefaultIcon )
		{
			needAddBg = false;
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
			pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , Icon3D.getIconBg() , titleBg ) , false , isDefault );
		}
		else
		{
			pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , null , titleBg ) , false , isDefault );
		}
		// Log.e("launcher", "pack1:success");
		return true;
	}
	
	public static boolean isVirtureIcon(
			ShortcutInfo info )
	{
		if( info.intent.getClass() != null )
		{
			for( int i = 0 ; i < DefaultLayout.allVirture.size() ; i++ )
			{
				if( DefaultLayout.allVirture.get( i ).pkgName.equals( info.intent.getComponent().getPackageName() ) )
				{
					Log.v( "MainMenu" , "clss name: " + info.intent.getClass().toString() );
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean useEmbedIcon(
			ShortcutInfo info )
	{
		if( info.intent != null && info.intent.getComponent() != null )
		{
			return DefaultLayout.hasReplaceIcon( info.intent.getComponent().getPackageName().toString() , info.intent.getComponent().getClassName().toString() );
		}
		return false;
	}
	
	public static boolean pack(
			ShortcutInfo info ,
			String suffix )
	{
		// Log.e("launcher", "pack1:"+getInfoName(info));
		if( R3D.packer == null )
		{
			// Log.e("launcher", "pack=null");
			return false;
		}
		if( R3D.packer.getRect( getInfoName( info ) ) != null && !R3D.packer.getRect( getInfoName( info ) ).isDefault )
		{
			// Log.e("launcher", "hasPack");
			return false;
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
		// Log.e("launcher", "pack2:"+getInfoName(info));
		if( info.intent == null || info.intent.getComponent() == null || info.intent.getComponent().getPackageName() == null )
		{
			//		    if(info.title.equals(iLoongLauncher.getInstance().getResources().getString(R.string.mainmenu))){
			//		        pack(getInfoName(info) + suffix, Utils3D.IconToPixmap3D(bmp,
			//                        info.title.toString(), null, Icon3D.titleBg), false,
			//                        isDefault);
			//		    }
			if( info.intent != null && info.intent.getAction() != null && iLoongLauncher.getInstance().isDefaultHotseats( info.intent.getAction() ) )
			{
				pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , null , Icon3D.titleBg ) , false , isDefault );
				Log.v( "MainMenu" , "pcktitle  " + info.title );
			}
			else if( !doNotNeedScale( null , null ) )
			{
				if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
				{
					bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
				}
				pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , Icon3D.getIconBg() , Icon3D.titleBg ) , false , isDefault );
			}
			else
			{
				pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , Icon3D.getIconBg() , Icon3D.titleBg ) , false , isDefault );
			}
		}
		else
		{
			//		    if(isVirtureIcon(info)){
			//                
			//            }
			//		    else if(useEmbedIcon(info)){
			//		        pack(getInfoName(info) + suffix, Utils3D.IconToPixmap3D(bmp,
			//                        info.title.toString(), null, Icon3D.titleBg), false,
			//                        isDefault);
			//		       Log.v("MainMenu", "pckname: "+info.intent.getComponent().getPackageName()+" className: "+info.intent.getComponent().getClassName());
			//		    }
			//		    else if(!iLoongLauncher.getInstance().isDefaultHotseats(info.intent.getAction()){
			//		        
			//		    }
			//		    else
			if( !doNotNeedScale( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
			{
				if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
				{
					bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
				}
				pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , Icon3D.getIconBg() , Icon3D.titleBg ) , false , isDefault );
			}
			else
			{
				pack( getInfoName( info ) + suffix , Utils3D.IconToPixmap3D( bmp , info.title.toString() , null , Icon3D.titleBg ) , false , isDefault );
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
		// if (DefaultLayout.ThirdAPK_add_background == false)
		// {
		// return false;
		// }
		//
		// if (info.intent==null
		// ||info.intent.getComponent()==null||info.intent.getComponent().getPackageName()==null)
		// {
		// return false;
		// }
		// String pname = info.intent.getComponent().getPackageName();
		// String compname = info.intent.getComponent().getClassName();
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
	
	public static AtlasRegion findRegion(
			String name )
	{
		AtlasRegion ret = packerAtlas.findRegion( name );
		// if(ret == null)
		// ret = system_img_pack.findRegion(name);
		if( ret == null )
		{
			Log.e( "pack" , "region can not be found:" + name );
			ret = findRegion( "app-default-icon" );
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
			// Magfilter=TextureFilter.Nearest;
		}
		app_list_string = r.getStringArray( RR.array.app_effects_list_preference );
		workSpace_list_string = r.getStringArray( RR.array.workspace_effects_list_preference );
		trash_icon_width = getInteger( "trash_icon_width" );
		trash_icon_height = getInteger( "trash_icon_height" );
		workspace_cell_width = getInteger( "workspace_cell_width" );
		if( Utils3D.getScreenWidth() > 300 && ( DefaultLayout.hotseat_hide_title == true || DefaultLayout.hotseat_style_ex == true ) )
		{
			workspace_cell_width = getInteger( "workspace_cell_width_large" );
		}
		workspace_cell_height = getInteger( "workspace_cell_height" );
		// if (Utils3D.getScreenHeight() > 800)
		// workspace_cell_height = getInteger("workspace_cell_height_large");
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
		folder_group_child_count_y = getInteger( "folder_group_child_count_y" );
		folder3D_name = r.getString( RR.string.folder_name );
		folder3D_full = r.getString( RR.string.folder3D_full );
		iLoong_Name = r.getString( RR.string.app_name );
		workspace_multicon_width = (int)r.getDimension( RR.dimen.multi_icon_width );
		workspace_multicon_height = (int)r.getDimension( RR.dimen.multi_icon_height );
		workspace_paopao_big_width = (int)r.getDimension( RR.dimen.paopao_big_width );
		workspace_paopao_num_width = (int)r.getDimension( RR.dimen.paopao_num_width );
		workspace_paopao_num_height = (int)r.getDimension( RR.dimen.paopao_num_height );
		workspace_multiviews_offset = (int)r.getDimension( RR.dimen.multi_views_offset3D );
		// icon_title_font = (int) r.getDimension(R.dimen.icon_title_font);
		icon_title_font = Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_title_font );
		reminder_font = Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.reminder_font );
		widget_preview_title_weight = DefaultLayout.widget_title_weight;
		appbar_tab_app = r.getString( RR.string.appbar_tab_app );
		appbar_tab_widget = r.getString( RR.string.appbar_tab_widget );
		if( DefaultLayout.appbar_widgets_special_name )
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
		folder_title_color = Color.parseColor( getString( "folder_title_color" ) );
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
		Workspace_cellCountX = r.getInteger( RR.integer.Workspace_cellCountX );
		Workspace_cellCountY = r.getInteger( RR.integer.Workspace_cellCountY );
		applist_padding_left = (int)r.getDimension( RR.dimen.applist_padding_left );
		applist_padding_right = (int)r.getDimension( RR.dimen.applist_padding_right );
		applist_padding_left_ex = (int)r.getDimension( RR.dimen.applist_padding_left_ex );
		applist_padding_right_ex = (int)r.getDimension( RR.dimen.applist_padding_right_ex );
		applist_padding_top = (int)r.getDimension( RR.dimen.applist_padding_top );
		applist_padding_bottom = (int)r.getDimension( RR.dimen.applist_padding_bottom );
		applist_indicator_y = getInteger( "applist_indicator_y" );
		if( Utils3D.getScreenHeight() > 800 )
		{
			applist_padding_bottom = getInteger( "applist_padding_bottom_large" );
			applist_indicator_y = getInteger( "applist_indicator_y_large" );
		}
		def_layout_y = (int)r.getDimension( RR.dimen.def_layout_y );
		def_layout_y_dura = (int)r.getDimension( RR.dimen.def_layout_y_dura );
		sidebar_height = getInteger( "sidebar_height" );
		sidebar_button_width = getInteger( "sidebar_button_width" );
		sidebar_button_height = getInteger( "sidebar_button_height" );
		sidebar_button_icon_width = getInteger( "sidebar_button_icon_width" );
		sidebar_button_icon_height = getInteger( "sidebar_button_icon_height" );
		sidebar_button_icon1_x = getInteger( "sidebar_button_icon1_x" );
		sidebar_button_icon1_y = getInteger( "sidebar_button_icon1_y" );
		sidebar_button_icon2_x = getInteger( "sidebar_button_icon2_x" );
		sidebar_button_icon2_y = getInteger( "sidebar_button_icon2_y" );
		sidebar_button_foucs_size = getInteger( "sidebar_button_focus_size" );
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
		if( Utils3D.getScreenDisplayMetricsHeight() >= 1280 && Utils3D.getScreenWidth() == 800 )
		{
			hot_obj_height = getInteger( "hot_obj_height_large" );
		}
		else if( Utils3D.getScreenWidth() == 480 && Utils3D.getScreenDisplayMetricsHeight() <= 800 )
		{
			hot_obj_height = getInteger( "hot_obj_height_small" );
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
		hot_dock_grid_pos_y = getInteger( "hot_dock_grid_pos_y" );
		hot_grid_bottom_margin = getInteger( "hot_grid_bottom_margin" );
		if( Utils3D.getScreenHeight() > 800 )
			hot_grid_bottom_margin = getInteger( "hot_grid_bottom_margin_large" );
		folder_name = r.getString( RR.string.folder_name );
		contact_name = r.getString( RR.string.contact_name );
		hot_sidebar_top_margin = getInteger( "hot_sidebar_top_margin" );
		tip_point_width = getInteger( "tip_point_width" );
		tip_point_height = getInteger( "tip_point_height" );
		icon_bg_num = getInteger( "icon_bg_num" );
		// teapotXu_20130206: add start
		theme_thirdapk_icon_scaleFactor = getInteger( "theme_thirdapk_icon_scaleFactor" );
		// here reset the value of thirdapk_icon_scaleFactor by
		// theme_thirdapk_icon_scaleFactor.
		DefaultLayout.thirdapk_icon_scaleFactor = theme_thirdapk_icon_scaleFactor / 100f;
		// teapotXu_20130206: add end
		bottom_bar_height = getInteger( "bottombar_height" );
		bottom_bar_title_height = getInteger( "bottombar_title_height" );
		Workspace_celllayout_toppadding = R3D.getInteger( "celllayout_topPadding" );
		Workspace_celllayout_bottompadding = R3D.getInteger( "celllayout_bottomPadding" );
		page_indicator_size = R3D.getInteger( "page_indicator_size" );
		page_indicator_y = R3D.getInteger( "page_indicator_y" );
		page_indicator_focus_w = R3D.getInteger( "page_indicator_focus_w" );
		page_indicator_normal_w = R3D.getInteger( "page_indicator_normal_w" );
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
		}
		int folder_max_height = Utils3D.getScreenHeight()
		// - R3D.workspace_cell_height - R3D.icongroup_button_height / 2
		- R3D.workspace_cell_height - R3D.folder_group_top_round - R3D.icongroup_margin_top - R3D.icongroup_margin_left;
		//zhujieping add
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			folder_max_num = ThemeManager.getInstance().getInteger( "folder_max_num" );
			if( folder_max_num <= 0 )
			{
				folder_max_num = folder_group_child_count_x * ( folder_max_height / R3D.workspace_cell_height );
			}
		}
		else
		{
			folder_max_num = 4 * ( folder_max_height / R3D.workspace_cell_height );
		}
		if( folder_max_num > 16 )
		{
			folder_max_num = 9;
		}
		if( folder_max_num < 8 )
		{
			folder_max_num = 9;
		}
		if( Utils3D.getScreenHeight() < 500 )
		{
			// hot_grid_bottom_margin = 0;
			hot_obj_height = R3D.workspace_cell_height;
			page_indicator_y = R3D.getInteger( "page_indicator_y_small" );
			Workspace_celllayout_toppadding = R3D.getInteger( "celllayout_topPadding_small" );
			Workspace_celllayout_bottompadding = R3D.getInteger( "celllayout_bottomPadding_small" );
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
		Workspace_cell_each_width_ori = (int)r.getDimension( RR.dimen.workspace_cell_width );
		Workspace_cell_each_height_ori = (int)r.getDimension( RR.dimen.workspace_cell_height );
		Workspace_cell_each_width = Utils3D.getScreenWidth() / R3D.Workspace_cellCountX;
		Workspace_cell_each_height = ( Utils3D.getScreenHeight() - Workspace_celllayout_toppadding - Workspace_celllayout_bottompadding ) / R3D.Workspace_cellCountY;
		/*
		 * 对于QVGA的屏，计算出来的Workspace_cell_each_height还没有workspace_cell_height高，
		 * 动态调整Workspace_cellCountY added by zfshi 2012-11-14
		 */
		if( Utils3D.getScreenHeight() < 400 && Workspace_cell_each_height < workspace_cell_height )
		{
			Workspace_cellCountY = Workspace_cellCountY - 1;
			Workspace_cell_each_height = ( Utils3D.getScreenHeight() - Workspace_celllayout_toppadding - Workspace_celllayout_bottompadding ) / R3D.Workspace_cellCountY;
		}
		ConfigBase.Workspace_cell_each_height = Workspace_cell_each_height;
		ConfigBase.Workspace_cell_each_width = Workspace_cell_each_width;
		ConfigBase.Workspace_cell_each_height_ori = Workspace_cell_each_height_ori;
		ConfigBase.Workspace_cell_each_width_ori = Workspace_cell_each_width_ori;
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
		final Intent mainIntent = new Intent( Intent.ACTION_MAIN , null );
		mainIntent.addCategory( Intent.CATEGORY_LAUNCHER );
		// removed by zhenNan.ye for /*packageInfos.size()*/64
		//		PackageManager pm = c.getPackageManager();
		//		List<ResolveInfo> packageInfos = pm
		//				.queryIntentActivities(mainIntent, 0);
		packerAtlas = new TextureAtlas();
		packer = new BitmapPacker(
				iLoongLauncher.getInstance() ,/*packageInfos.size()*/
				32 ,
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
		pack( "move_to_left_screen_bar_bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/move_to_left_screen_bar_bg.png" ) );
		pack( "move_to_right_screen_bar_bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/move_to_right_screen_bar_bg.png" ) );
		// if(6 == folder_max_num){
		// R3D.pack("folder-maxnumber-" + 6,
		// ThemeManager.getInstance().getBitmap("theme/folder/folder-maxnumber-"+6+".png"));
		// }
		if( 8 == folder_max_num )
		{
			pack( "folder-maxnumber-" + 8 , ThemeManager.getInstance().getBitmap( "theme/folder/folder-maxnumber-" + 8 + ".png" ) );
		}
		if( 9 == folder_max_num )
		{
			pack( "folder-maxnumber-" + 9 , ThemeManager.getInstance().getBitmap( "theme/folder/folder-maxnumber-" + 9 + ".png" ) );
		}
		if( 12 == folder_max_num )
		{
			pack( "folder-maxnumber-" + 12 , ThemeManager.getInstance().getBitmap( "theme/folder/folder-maxnumber-" + 12 + ".png" ) );
		}
		if( 16 == folder_max_num )
		{
			pack( "folder-maxnumber-" + 16 , ThemeManager.getInstance().getBitmap( "theme/folder/folder-maxnumber-" + 16 + ".png" ) );
		}
		for( int j = 0 ; j < folder_max_num ; j++ )
		{
			pack( "folder-number-" + j , ThemeManager.getInstance().getBitmap( "theme/folder/folder-number-" + j + ".png" ) );
		}
		pack( "widget-folder-bg2" , ThemeManager.getInstance().getBitmap( "theme/folder/widget-folder-bg2.png" ) );
		pack( "icon_focus" , ThemeManager.getInstance().getBitmap( "theme/pack_source/focus_bg.png" ) );
		//xiatian add start	//New AppList Popmenu
		applist_menu_color = Color.parseColor( getString( "applist_menu_color" ) );
		applist_menu_padding_top = getInteger( "applist_menu_padding_top" );
		applist_menu_height = getInteger( "applist_menu_height" );
		//xiatian add end
		R3D.pack( folder_bg , ThemeManager.getInstance().getBitmap( "theme/folder/folder-bg.png" ) );
		R3D.pack( folder_cover , ThemeManager.getInstance().getBitmap( "theme/folder/folder-cover.png" ) );
		//zhujieping add start
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			Bitmap oriBitmap = ThemeManager.getInstance().getBitmap( "theme/pack_source/translucent-black.png" );
			Bitmap screenBackBitmap = null;
			if( oriBitmap != null )
			{
				if( Utils3D.getScreenWidth() != oriBitmap.getWidth() || Utils3D.getScreenHeight() != oriBitmap.getHeight() )
				{
					screenBackBitmap = Tools.resizeBitmap( oriBitmap , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
					oriBitmap.recycle();
				}
				else
				{
					screenBackBitmap = oriBitmap;
				}
			}
			if( screenBackBitmap != null )
			{
				screenBackRegion = new TextureRegion( new BitmapTexture( screenBackBitmap ) );
				screenBackBitmap.recycle();
			}
		}
		//zhujieping add end
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			R3D.pack( "widget-folder-open-line" , ThemeManager.getInstance().getBitmap( "theme/pack_source/widget-folder-open-line.png" ) );
			R3D.pack( "cursor_patch" , ThemeManager.getInstance().getBitmap( "theme/pack_source/cursor_patch.png" ) );
		}
		R3D.pack( apphost_bg , ThemeManager.getInstance().getBitmap( "theme/pack_source/translucent-black.png" ) );
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
		FolderIcon3D.createFrontBmp();
		FolderIcon3D.createFolderCoverBmp();
		if( DefaultLayout.trash_icon_pos == -1 )
		{
			if( DefaultLayout.mainmenu_pos == 4 )
			{
				DefaultLayout.trash_icon_pos = TrashIcon3D.TRASH_POS_RIGHT;
				;
			}
			else
			{
				DefaultLayout.trash_icon_pos = TrashIcon3D.TRASH_POS_TOP;
			}
		}
		else
		{
			if( DefaultLayout.hot_dock_icon_number == 4 && DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_RIGHT && DefaultLayout.mainmenu_pos == 2 )
			{
				DefaultLayout.trash_icon_pos = TrashIcon3D.TRASH_POS_MIDDLE;
			}
			if( DefaultLayout.mainmenu_pos == 4 )
			{
				DefaultLayout.trash_icon_pos = TrashIcon3D.TRASH_POS_RIGHT;
			}
		}
		if( DefaultLayout.hotseat_icon_pos_fixed == false )
		{
			DefaultLayout.hotseat_title_no_background = false;
		}
		if( DefaultLayout.hotseat_style_ex == true )
		{
			DefaultLayout.trash_icon_pos = TrashIcon3D.TRASH_POS_TOP;
			DefaultLayout.mainmenu_pos = 2;
			DefaultLayout.hot_dock_icon_number = 5;
			DefaultLayout.hotseat_hide_title = true;
			hot_dock_icon_number = DefaultLayout.hot_dock_icon_number;
		}
		// Log.v("jbc","abcabcgetInteger"+R3D.getInteger("pop_setupmenu_style"));
		// if (R3D.getInteger("pop_setupmenu_style") != -1)
		// DefaultLayout.popmenu_style = R3D.getInteger("pop_setupmenu_style");
		// Log.v("jbc","abcabcpopmenu_style"+DefaultLayout.popmenu_style);
	}
	
	public static void initialize2(
			iLoongLauncher c )
	{
		Utils3D.showTimeFromStart( "start pack" );
		Resources r = c.getResources();
		// 图片打包
		// R3D.pack("application-page-nv-point1",
		// ThemeManager.getInstance().getBitmap("theme/pack_source/application-page-nv-point1.png"));
		// R3D.pack("application-page-nv-point2",
		// ThemeManager.getInstance().getBitmap("theme/pack_source/application-page-nv-point2.png"));
		// R3D.pack("default_indicator",
		// ThemeManager.getInstance().getBitmap("theme/pack_source/default_indicator.png"));
		// R3D.pack("default_indicator_current",
		// ThemeManager.getInstance().getBitmap("theme/pack_source/default_indicator_current.png"));
		// R3D.pack("contacts",
		// ThemeManager.getInstance().getBitmap("theme/pack_source/Contactperson-icon.png"));
		R3D.pack( "control-del-close" , ThemeManager.getInstance().getBitmap( "theme/pack_source/control-del-close.png" ) );
		R3D.pack( "control-del-open" , ThemeManager.getInstance().getBitmap( "theme/pack_source/control-del-open.png" ) );
		R3D.pack( "control-del-Telephone-dial" , ThemeManager.getInstance().getBitmap( "theme/pack_source/control-del-Telephone-dial.png" ) );
		R3D.pack( "control-del-Telephone-dial2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/control-del-Telephone-dial2.png" ) );
		R3D.pack( "menu-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/menu-bg.png" ) );
		R3D.pack( "menu-tool-button1" , ThemeManager.getInstance().getBitmap( "theme/pack_source/menu-tool-button1.png" ) );
		R3D.pack( "menu-tool-button2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/menu-tool-button2.png" ) );
		R3D.pack( "menu-tool-button3" , ThemeManager.getInstance().getBitmap( "theme/pack_source/menu-tool-button3.png" ) );
		R3D.pack( "menu-user-button1" , ThemeManager.getInstance().getBitmap( "theme/pack_source/menu-user-button1.png" ) );
		R3D.pack( "menu-user-button2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/menu-user-button2.png" ) );
		R3D.pack( "menu-user-button3" , ThemeManager.getInstance().getBitmap( "theme/pack_source/menu-user-button3.png" ) );
		R3D.pack( "public-button-return" , ThemeManager.getInstance().getBitmap( "theme/pack_source/public-button-return.png" ) );
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
		R3D.pack( "appbar-content-pop-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-content-pop-bg.png" ) );
		R3D.pack( "appbar-popmenu-divider" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-popmenu-divider.png" ) );
		R3D.pack( "shell-picker-menu-item7b" , ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-picker-menu-item7b.png" ) );
		// desktopEdit added by zhenNan.ye begin
		desktopEdit_canNotDeletePage = r.getString( RR.string.can_not_delete_page );
		if( Utils3D.getScreenWidth() >= specifiedScreenWidth )
		{
			R3D.pack( desktopEdit_page_bg , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/page-bg.png" ) );
		}
		else
		{
			R3D.pack( desktopEdit_page_bg , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/desktopEdit/page-bg.png" ) );
		}
		R3D.pack( desktopEdit_page_home , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/page-home.png" ) );
		R3D.pack( desktopEdit_page_cur_home , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/page-cur-home.png" ) );
		R3D.pack( desktopEdit_page_home_hilight , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/page-home-hilight.png" ) );
		R3D.pack( desktopEdit_page_add , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/page-add.png" ) );
		R3D.pack( desktopEdit_page_add_hilight , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/page-add-hilight.png" ) );
		R3D.pack( desktopEdit_mode_plane , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/editMode-plane.png" ) );
		R3D.pack( desktopEdit_mode_plane_hilight , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/editMode-plane-hilight.png" ) );
		R3D.pack( desktopEdit_mode_cylinder , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/editMode-cylinder.png" ) );
		R3D.pack( desktopEdit_mode_cylinder_hilight , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/editMode-cylinder-hilight.png" ) );
		R3D.pack( desktopEdit_page_groove , ThemeManager.getInstance().getBitmap( "theme/desktopEdit/page-groove.png" ) );
		// desktopEdit added by zhenNan.ye end
		if( iLoongLauncher.curLanguage == 0 )
		{
			R3D.pack( dockbar_add_applications , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_applications_cn.png" ) );
			R3D.pack( dockbar_add_button_cancel_hilight , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_cancel_hilight_cn.png" ) );
			R3D.pack( dockbar_add_button_cancel , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_cancel_cn.png" ) );
			R3D.pack( dockbar_add_button_done_hilight , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_done_hilight_cn.png" ) );
			R3D.pack( dockbar_add_button_done , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_done_cn.png" ) );
			R3D.pack( dockbar_add_shortcut , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_add_shortcut_cn.png" ) );
			R3D.pack( dockbar_comet_shortcut , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_comet_shortcut_cn.png" ) );
			R3D.pack( dockbar_comet_shortcut_hilight , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_comet_shortcut_hilight_cn.png" ) );
			R3D.pack( dockbar_system_shortcut , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_system_shortcut_cn.png" ) );
			R3D.pack( dockbar_system_shortcut_hilight , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_system_shortcut_hilight_cn.png" ) );
			//添加Widget
			R3D.pack( dockbar_editmode_addwdiget_title , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/addwidget_title_cn.png" ) );
			R3D.pack( dockbar_editmode_addwdiget_comet , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/comet-wigdet_cn.png" ) );
			R3D.pack( dockbar_editmode_addwdiget_cometed , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/comet-wigdet2_cn.png" ) );
			R3D.pack( dockbar_editmode_addwdiget_sys , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/System-widget_cn.png" ) );
			R3D.pack( dockbar_editmode_addwdiget_sysed , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/System-widget2_cn.png" ) );
		}
		else
		{
			R3D.pack( dockbar_add_applications , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_applications.png" ) );
			R3D.pack( dockbar_add_button_cancel_hilight , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_cancel_hilight.png" ) );
			R3D.pack( dockbar_add_button_cancel , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_cancel.png" ) );
			R3D.pack( dockbar_add_button_done_hilight , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_done_hilight.png" ) );
			R3D.pack( dockbar_add_button_done , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_done.png" ) );
			R3D.pack( dockbar_add_shortcut , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_add_shortcut.png" ) );
			R3D.pack( dockbar_comet_shortcut , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_comet_shortcut.png" ) );
			R3D.pack( dockbar_comet_shortcut_hilight , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_comet_shortcut_hilight.png" ) );
			R3D.pack( dockbar_system_shortcut , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_system_shortcut.png" ) );
			R3D.pack( dockbar_system_shortcut_hilight , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_system_shortcut_hilight.png" ) );
			//添加Widget
			R3D.pack( dockbar_editmode_addwdiget_title , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/addwidget_title.png" ) );
			R3D.pack( dockbar_editmode_addwdiget_comet , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/comet-wigdet.png" ) );
			R3D.pack( dockbar_editmode_addwdiget_cometed , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/comet-wigdet2.png" ) );
			R3D.pack( dockbar_editmode_addwdiget_sys , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/System-widget.png" ) );
			R3D.pack( dockbar_editmode_addwdiget_sysed , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addWidget/System-widget2.png" ) );
		}
		R3D.pack( dockbar_add_app_bg , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_app_bg.png" ) );
		R3D.pack( dockbar_add_button_back_hilight , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_back_hilight.png" ) );
		R3D.pack( dockbar_add_button_back , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_app/dockbar_add_button_back.png" ) );
		R3D.pack( dockbar_add_shortcut_bg , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_add_shortcut_bg.png" ) );
		R3D.pack( dockbar_selected_table1 , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_selected_table1.png" ) );
		R3D.pack( dockbar_unselected_table1 , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_unselected_table1.png" ) );
		R3D.pack( dockbar_selected_table2 , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_selected_table2.png" ) );
		R3D.pack( dockbar_unselected_table2 , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/add_shortcut/dockbar_unselected_table2.png" ) );
		R3D.pack( "long_touch_light" , ThemeManager.getInstance().getBitmap( "theme/pack_source/long_touch_light.png" ) );
		R3D.pack( "tip-point" , ThemeManager.getInstance().getBitmap( "theme/pack_source/tip-point.png" ) );
		if( !( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) )
		{
			R3D.pack( "widget-folder-windows-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/widget-folder-windows-bg.png" ) );
			R3D.pack( "widget-folder-icon" , ThemeManager.getInstance().getBitmap( "theme/pack_source/widget-folder-icon.png" ) );
			R3D.pack( "widget-folder-windows-title" , ThemeManager.getInstance().getBitmap( "theme/pack_source/widget-folder-windows-title.png" ) );
		}
		//编辑模式 
		R3D.pack( dockbar_editmode_add_bg , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/bg.png" ) );
		//添加文件夹
		R3D.pack( dockbar_editmode_addfolder_cancel , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/cancel.png" ) );
		R3D.pack( dockbar_editmode_addfolder_canceled , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/cancel1.png" ) );
		R3D.pack( dockbar_editmode_addfolder_exit , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/cha.png" ) );
		R3D.pack( dockbar_editmode_addfolder_exited , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/chacha.png" ) );
		R3D.pack( dockbar_editmode_addfolder_done , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/done.png" ) );
		R3D.pack( dockbar_editmode_addfolder_doned , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/done1.png" ) );
		R3D.pack( dockbar_editmode_addfolder_frame , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/di.png" ) );
		R3D.pack( dockbar_editmode_addfolder_frame2 , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/rename.png" ) );
		R3D.pack( dockbar_editmode_addfolder_input , ThemeManager.getInstance().getBitmap( "theme/dock3dbar/addFolder/gang.png" ) );
		//guid	
		SharedPreferences sp = iLoongLauncher.getInstance().getSharedPreferences( "saveHelp" , 1 );
		boolean iffirst = sp.getBoolean( "iffirst" , true );
		if( iffirst )
		{
			if( Utils3D.getScreenWidth() >= specifiedScreenWidth )
			{
				R3D.pack( "beijing" , ThemeManager.getInstance().getBitmap( "theme/guid/beijing.jpg" ) );
				R3D.pack( "dian" , ThemeManager.getInstance().getBitmap( "theme/guid/dian.png" ) );
				R3D.pack( "dian_click" , ThemeManager.getInstance().getBitmap( "theme/guid/dian_click.png" ) );
				R3D.pack( "guid_1_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_1_bg.png" ) );
				R3D.pack( "guid_2_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_2_bg.png" ) );
				R3D.pack( "guid_3_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_3_bg.png" ) );
				R3D.pack( "guid_4_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_4_bg.png" ) );
				//			R3D.pack( "guid_5_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_5_bg.png" ) );
				R3D.pack( "guid_6_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_6_bg.png" ) );
				//			R3D.pack( "guid_7_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_7_bg.png" ) );
				R3D.pack( "guid_8_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_8_bg.png" ) );
				R3D.pack( "guid_9_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_9_bg.png" ) );
			}
			else
			{
				R3D.pack( "beijing" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/beijing.jpg" ) );
				R3D.pack( "dian" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/dian.png" ) );
				R3D.pack( "dian_click" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/dian_click.png" ) );
				R3D.pack( "guid_1_bg" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_1_bg.png" ) );
				R3D.pack( "guid_2_bg" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_2_bg.png" ) );
				R3D.pack( "guid_3_bg" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_3_bg.png" ) );
				R3D.pack( "guid_4_bg" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_4_bg.png" ) );
				//			R3D.pack( "guid_5_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_5_bg.png" ) );
				R3D.pack( "guid_6_bg" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_6_bg.png" ) );
				//			R3D.pack( "guid_7_bg" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_7_bg.png" ) );
				R3D.pack( "guid_8_bg" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_8_bg.png" ) );
				R3D.pack( "guid_9_bg" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_9_bg.png" ) );
			}
			if( iLoongLauncher.curLanguage == 0 )
			{
				if( Utils3D.getScreenWidth() >= specifiedScreenWidth )
				{
					R3D.pack( "guid_1_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_1_title_cn.png" ) );
					R3D.pack( "guid_2_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_2_title_cn.png" ) );
					R3D.pack( "guid_3_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_3_title_cn.png" ) );
					R3D.pack( "guid_4_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_4_title_cn.png" ) );
					//				R3D.pack( "guid_5_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_5_title_cn.png" ) );
					R3D.pack( "guid_6_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_6_title_cn.png" ) );
					//				R3D.pack( "guid_7_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_7_title_cn.png" ) );
					R3D.pack( "guid_8_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_8_title_cn.png" ) );
					R3D.pack( "guid_9_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_9_title_cn.png" ) );
					R3D.pack( "guid_start" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_start_cn.png" ) );
				}
				else
				{
					R3D.pack( "guid_1_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_1_title_cn.png" ) );
					R3D.pack( "guid_2_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_2_title_cn.png" ) );
					R3D.pack( "guid_3_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_3_title_cn.png" ) );
					R3D.pack( "guid_4_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_4_title_cn.png" ) );
					//				R3D.pack( "guid_5_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_5_title_cn.png" ) );
					R3D.pack( "guid_6_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_6_title_cn.png" ) );
					//				R3D.pack( "guid_7_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_7_title_cn.png" ) );
					R3D.pack( "guid_8_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_8_title_cn.png" ) );
					R3D.pack( "guid_9_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_9_title_cn.png" ) );
					R3D.pack( "guid_start" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_start_cn.png" ) );
				}
			}
			else
			{
				if( Utils3D.getScreenWidth() >= specifiedScreenWidth )
				{
					R3D.pack( "guid_1_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_1_title.png" ) );
					R3D.pack( "guid_2_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_2_title.png" ) );
					R3D.pack( "guid_3_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_3_title.png" ) );
					R3D.pack( "guid_4_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_4_title.png" ) );
					//				R3D.pack( "guid_5_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_5_title.png" ) );
					R3D.pack( "guid_6_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_6_title.png" ) );
					//				R3D.pack( "guid_7_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_7_title.png" ) );
					R3D.pack( "guid_8_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_8_title.png" ) );
					R3D.pack( "guid_9_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_9_title.png" ) );
					R3D.pack( "guid_start" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_start.png" ) );
				}
				else
				{
					R3D.pack( "guid_1_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_1_title.png" ) );
					R3D.pack( "guid_2_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_2_title.png" ) );
					R3D.pack( "guid_3_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_3_title.png" ) );
					R3D.pack( "guid_4_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_4_title.png" ) );
					//				R3D.pack( "guid_5_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_5_title.png" ) );
					R3D.pack( "guid_6_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_6_title.png" ) );
					//				R3D.pack( "guid_7_title" , ThemeManager.getInstance().getBitmap( "theme/guid/guid_7_title.png" ) );
					R3D.pack( "guid_8_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_8_title.png" ) );
					R3D.pack( "guid_9_title" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_9_title.png" ) );
					R3D.pack( "guid_start" , Tools.getScaledBitmapBySpecifiedRes( specifiedScreenWidth , Utils3D.getScreenWidth() , "theme/guid/guid_start.png" ) );
				}
			}
		}
		//添加文件夹end
		//编辑模式  end
		// R3D.pack("widget-folder-bg",
		// ThemeManager.getInstance().getBitmap("theme/folder/widget-folder-bg.png"));
		// R3D.pack("xiezai-bg",
		// ThemeManager.getInstance().getBitmap("theme/pack_source/xiezai-bg.png"));
		// R3D.pack("xiezai-bg2",
		// ThemeManager.getInstance().getBitmap("theme/pack_source/xiezai-bg2.png"));
		R3D.pack( "gardening_crosshairs" , ThemeManager.getInstance().getBitmap( "theme/home/gardening_crosshairs.png" ) );
		if( DefaultLayout.show_widget_shortcut_bg )
		{
			R3D.pack( "widget-shortcut-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/widget-shortcut-bg.png" ) );
		}
		// R3D.pack("paged_view_indicator",
		// ThemeManager.getInstance().getBitmap("themetheme/pack_source/application-page-nv-point2.png"));
		if( Utils3D.getScreenHeight() > 700 )
		{
			R3D.pack( "app-menu-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-menu-button.png" ) );
			R3D.pack( "app-menu-downarray" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-downarray.png" ) );
			R3D.pack( "app-menu-bag" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-bag.png" ) );
			R3D.pack( "app-home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-button.png" ) );
		}
		else
		{
			R3D.pack( "app-menu-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-menu-button-small.png" ) );
			R3D.pack( "app-menu-downarray" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-downarray-small.png" ) );
			R3D.pack( "app-menu-bag" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-bag-small.png" ) );
			R3D.pack( "app-home-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-home-button-small.png" ) );
		}
		//		R3D.pack(
		//				"translucent-bg",
		//				ThemeManager.getInstance().getBitmap(
		//						"theme/pack_source/translucent-bg.png"));
		// R3D.pack("translucent-bg-small",
		// ThemeManager.getInstance().getBitmap("themetheme/pack_source/translucent-bg-small.png"));
		//		if (DefaultLayout.mainmenu_add_black_ground) {
		//			R3D.pack("translucent-bg-opa", ThemeManager.getInstance()
		//					.getBitmap("theme/pack_source/translucent-black.png"));
		//		} else {
		//			R3D.pack("translucent-bg-opa", ThemeManager.getInstance()
		//					.getBitmap("theme/pack_source/translucent-bg-opa.png"));
		//		}
		// R3D.pack("appbar-bg",
		// ThemeManager.getInstance().getBitmap("themetheme/pack_source/appbar-bg.png"));
		// R3D.pack("app-hide",
		// ThemeManager.getInstance().getBitmap("themetheme/pack_source/app-hide.png"));
		// R3D.pack("app-resume",
		// ThemeManager.getInstance().getBitmap("themetheme/pack_source/app-resume.png"));
		R3D.pack( "app-uninstall" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall.png" ) );
		R3D.pack( "appbar-divider" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-divider.png" ) );
		R3D.pack( "appbar-indicator" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-indicator.png" ) );
		//		R3D.pack( "app-sort-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-sort-button.png" ) );
		//		R3D.pack( "app-uninstall-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall-button.png" ) );
		//		R3D.pack( "app-hide-button" , ThemeManager.getInstance().getBitmap( "theme/pack_source/app-hide-button.png" ) );
		R3D.pack( "appbar-tab-item-music" , ThemeManager.getInstance().getBitmap( "theme/pack_source/res_popup_music.png" ) );
		R3D.pack( "appbar-tab-item-media" , ThemeManager.getInstance().getBitmap( "theme/pack_source/res_popup_media.png" ) );
		R3D.pack( "appbar-tab-item-album" , ThemeManager.getInstance().getBitmap( "theme/pack_source/res_popup_album.png" ) );
		R3D.pack( "appbar-tab-item-app" , ThemeManager.getInstance().getBitmap( "theme/pack_source/res_popup_app.png" ) );
		R3D.pack( "appbar-tab-highlight-item-music" , ThemeManager.getInstance().getBitmap( "theme/pack_source/res_popup_music_highlight.png" ) );
		R3D.pack( "appbar-tab-highlight-item-media" , ThemeManager.getInstance().getBitmap( "theme/pack_source/res_popup_media_highlight.png" ) );
		R3D.pack( "appbar-tab-highlight-item-album" , ThemeManager.getInstance().getBitmap( "theme/pack_source/res_popup_album_highlight.png" ) );
		R3D.pack( "appbar-tab-highlight-item-app" , ThemeManager.getInstance().getBitmap( "theme/pack_source/res_popup_app_highlight.png" ) );
		// R3D.pack("app-item-bg",
		// ThemeManager.getInstance().getBitmap("themetheme/pack_source/app-item-bg.png"));
		if( DefaultLayout.appbar_show_userapp_list == true )
		{
			R3D.pack( "appbar-navi-back" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-show-userapp-navigation-back.png" ) );
		}
		else
		{
			R3D.pack( "appbar-navi-back" , ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-navigation-back.png" ) );
		}
		R3D.pack( "workspace-reflect-view" , ThemeManager.getInstance().getBitmap( "theme/home/homescreen_blue_strong_holo.png" ) );
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
		//		R3D.pack(
		//				"appbar-tab-arrow",
		//				ThemeManager.getInstance().getBitmap(
		//						"theme/pack_source/appbar-tab-arrow.png"));
		//		R3D.pack("appbar-tab-app", AppBar3D.titleToPixmap(R3D.appbar_tab_app,
		//				(int) R3D.appbar_height, false, R3D.appbar_tab_color, false));
		//		R3D.pack("appbar-tab-widget", AppBar3D.titleToPixmap(
		//				R3D.appbar_tab_widget, (int) R3D.appbar_height, false,
		//				R3D.appbar_tab_color, false));
		//				
		//		R3D.pack("appbar-" + R3D.getString(RR.string.uninstall_app), AppBar3D
		//				.titleToPixmap(R3D.appbar_tab_uninstall,
		//						(int) R3D.appbar_height, false, R3D.appbar_tab_color,
		//						false));
		//		R3D.pack("appbar-" + R3D.getString(RR.string.hide_icon), AppBar3D
		//				.titleToPixmap(R3D.appbar_tab_hide, (int) R3D.appbar_height,
		//						false, R3D.appbar_tab_color, false));
		// zqh start
		R3D.pack( "appbar-tab-navigator-video" , AppBar3D.titleToPixmap( R3D.app_bar_navigator_video , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) );
		R3D.pack( "appbar-tab-navigator-audioalbum" , AppBar3D.titleToPixmap( R3D.app_bar_navigator_audioalbum , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) );
		R3D.pack( "appbar-tab-navigator-photobucket" , AppBar3D.titleToPixmap( R3D.app_bar_navigator_photobucket , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) );
		R3D.pack( "appbar-pop-app" , AppBar3D.titleToPixmap( R3D.appbar_tab_app , (int)R3D.appbar_height , false , R3D.appbar_menu_color , false ) );
		R3D.pack( "appbar-pop-video" , AppBar3D.titleToPixmap( R3D.app_bar_navigator_video , (int)R3D.appbar_height , false , R3D.appbar_menu_color , false ) );
		R3D.pack( "appbar-pop-audioalbum" , AppBar3D.titleToPixmap( R3D.app_bar_navigator_audioalbum , (int)R3D.appbar_height , false , R3D.appbar_menu_color , false ) );
		R3D.pack( "appbar-pop-photobucket" , AppBar3D.titleToPixmap( R3D.app_bar_navigator_photobucket , (int)R3D.appbar_height , false , R3D.appbar_menu_color , false ) );
		//
		//		// zqh end
		//		if (DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4) {
		//			
		//xiatian start	//New AppList Popmenu
		//			int title_height = Tools.dip2px(iLoongLauncher.getInstance(), 60);	//xiatian del
		int title_height = R3D.applist_menu_height; //xiatian add
		//xiatian end
		R3D.pack( R3D.getString( RR.string.uninstall_app ) , AppBar3D.titleToPixmap( R3D.getString( RR.string.uninstall_app ) , title_height , false ,
		//xiatian start	//New AppList Popmenu
		//							Color.parseColor("#FFFFFF"),	//xiatian del
				R3D.applist_menu_color , //xiatian add
				//xiatian end
				false ) );
		R3D.pack( R3D.getString( RR.string.download_apps ) , AppBar3D.titleToPixmap( R3D.getString( RR.string.download_apps ) , title_height , false ,
		//xiatian start	//New AppList Popmenu
		//							Color.parseColor("#FFFFFF"),	//xiatian del
				R3D.applist_menu_color , //xiatian add
				//xiatian end
				false ) );
		// (int) (R3D.appbar_menuitem_width - 10 - 39 *
		// Utils3D.getDensity()/ 1.5f)
		R3D.pack( R3D.getString( RR.string.hide_icon ) , AppBar3D.titleToPixmap( R3D.getString( RR.string.hide_icon ) , title_height , false ,
		//xiatian start	//New AppList Popmenu
		//							Color.parseColor("#FFFFFF"),	//xiatian del
				R3D.applist_menu_color , //xiatian add
				//xiatian end
				false ) );
		R3D.pack( R3D.getString( RR.string.sort_icon ) , AppBar3D.titleToPixmap( R3D.getString( RR.string.sort_icon ) , title_height , false ,
		//xiatian start	//New AppList Popmenu
		//							Color.parseColor("#FFFFFF"),	//xiatian del
				R3D.applist_menu_color , //xiatian add
				//xiatian end
				false ) );
		R3D.pack( R3D.getString( RR.string.effect_icon ) , AppBar3D.titleToPixmap( R3D.getString( RR.string.effect_icon ) , title_height , false ,
		//xiatian start	//New AppList Popmenu
		//							Color.parseColor("#FFFFFF"),	//xiatian del
				R3D.applist_menu_color , //xiatian add
				//xiatian end
				false ) );
		//teapotXu add start for Folder in Mainmenu
		R3D.pack( R3D.getString( RR.string.edit_mode ) , AppBar3D.titleToPixmap( R3D.getString( RR.string.edit_mode ) , title_height , false , R3D.applist_menu_color , false ) );
		//teapotXu add end for Folder in Mainmenu			
		//		} else {
		//			R3D.pack(R3D.getString(RR.string.uninstall_app), AppBar3D
		//					.titleToPixmap(R3D.getString(RR.string.uninstall_app),
		//							R3D.appbar_menuitem_height, false,
		//							R3D.appbar_menu_color, false));
		//			R3D.pack(R3D.getString(RR.string.download_apps), AppBar3D
		//					.titleToPixmap(R3D.getString(RR.string.download_apps),
		//							R3D.appbar_menuitem_height, false,
		//							R3D.appbar_menu_color, false));
		//			// (int) (R3D.appbar_menuitem_width - 10 - 39 *
		//			// Utils3D.getDensity()/ 1.5f)
		//			R3D.pack(R3D.getString(RR.string.hide_icon), AppBar3D
		//					.titleToPixmap(R3D.getString(RR.string.hide_icon),
		//							R3D.appbar_menuitem_height, false,
		//							R3D.appbar_menu_color, false));
		//			R3D.pack(R3D.getString(RR.string.sort_icon), AppBar3D
		//					.titleToPixmap(R3D.getString(RR.string.sort_icon),
		//							R3D.appbar_menuitem_height, false,
		//							R3D.appbar_menu_color, false));
		//			R3D.pack(R3D.getString(RR.string.effect_icon), AppBar3D
		//					.titleToPixmap(R3D.getString(RR.string.effect_icon),
		//							R3D.appbar_menuitem_height, false,
		//							R3D.appbar_menu_color, false));
		//			
		//			//teapotXu add start for Folder in Mainmenu
		//			R3D.pack(R3D.getString(RR.string.edit_mode), AppBar3D
		//					.titleToPixmap(R3D.getString(RR.string.edit_mode),
		//							R3D.appbar_menuitem_height, false,
		//							R3D.appbar_menu_color, false));
		//			//teapotXu add end for Folder in Mainmenu				
		//		}
		// R3D.pack("panel-frame",
		// ThemeManager.getInstance().getBitmap("themetheme/pack_source/panel-frame.png"));
		// R3D.pack("panel-highlight",
		// ThemeManager.getInstance().getBitmap("themetheme/pack_source/panel-highlight.png"));
		// R3D.pack("default-indicator-current",
		// ThemeManager.getInstance().getBitmap("themetheme/pack_source/default_indicator_current.png"));
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			R3D.pack( "xiezai-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg.png" ) );
			R3D.pack( "xiezai-bg2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/xiezai-bg2.png" ) );
		}
		else
		{
			// Bitmap titleBg=Icon3D.titleBg;
			// if (DefaultLayout.hotseat_title_no_background==true)
			// {
			// titleBg=null;
			// }
			// R3D.pack("xiezai-bg",
			// Utils3D.bmp2Pixmap(Utils3D.IconToPixmap3D(Utils3D.GetBmpFromImageName("theme/hotseatbar/xiezai-bg.png"),
			// R3D.trash_icon_title,null,titleBg)));
			// R3D.pack("xiezai-bg2",
			// Utils3D.bmp2Pixmap(Utils3D.IconToPixmap3D(Utils3D.GetBmpFromImageName("theme/hotseatbar/xiezai-bg2.png"),
			// R3D.trash_icon_title,null,titleBg)));
		}
		// R3D.pack("folder-front", FolderIcon3D.folder_front_ToPixmap3D());
		//		R3D.pack(
		//				"3ddockUV",
		//				ThemeManager.getInstance().getBitmap(
		//						"theme/dock3dbar/3ddockUV.png"));
		//		R3D.pack(
		//				"dock-middle",
		//				ThemeManager.getInstance().getBitmap(
		//						"theme/dock3dbar/middle.png"));
		// R3D.pack(contact_name,
		// ThemeManager.getInstance().getBitmap("theme/hotseatbar/contacts.png"));
		R3D.pack( "folders" , ThemeManager.getInstance().getBitmap( "theme/folder/widget-folder-bg.png" ) );
		//		R3D.pack( "folderswidget" , ThemeManager.getInstance().getBitmap( "theme/iconbg/widget-folder-icon.png" ) );
		int tmpH = (int)( ( Utils3D.getScreenHeight() - R3D.appbar_height - R3D.applist_padding_top - R3D.applist_padding_bottom ) / 3 ) - R3D.app_widget3d_gap;
		for( int i = 1 ; i < 6 ; i++ )
		{
			R3D.pack( i + "" , AppBar3D.titleToPixmap( i + "" , (int)( R3D.widget_preview_title_weight * tmpH ) , false , Color.WHITE , true ) );
		}
		R3D.pack( "x" , AppBar3D.titleToPixmap( "x" , (int)( R3D.widget_preview_title_weight * tmpH ) , false , Color.WHITE , true ) );
		R3D.pack( "left-bracket" , Utils3D.titleToPixmapWidthLimit( "(" , 1000 , R3D.photo_title_size , Color.WHITE , 1 ) );
		R3D.pack( "right-bracket" , Utils3D.titleToPixmapWidthLimit( ")" , 1000 , R3D.photo_title_size , Color.WHITE , 1 ) );
		for( int i = 0 ; i < 10 ; i++ )
		{
			R3D.pack( "photo-" + i , Utils3D.titleToPixmapWidthLimit( i + "" , 1000 , R3D.photo_title_size , Color.WHITE , 1 ) );
		}
		R3D.pack( "default-video" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-video.png" ) );
		R3D.pack( "media-unselect" , ThemeManager.getInstance().getBitmap( "theme/pack_source/unselect.png" ) );
		R3D.pack( "media-selected" , ThemeManager.getInstance().getBitmap( "theme/pack_source/selected.png" ) );
		R3D.pack( "default-audio-0" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-audio-1.png" ) );
		R3D.pack( "default-audio-1" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-audio-2.png" ) );
		R3D.pack( "default-audio-2" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-audio-3.png" ) );
		R3D.pack( "default-audio-3" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-audio-4.png" ) );
		R3D.pack( "default-audio-4" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-audio-5.png" ) );
		R3D.pack( "default-audio-5" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-audio-6.png" ) );
		R3D.pack( "default-photo" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-photo.png" ) );
		R3D.pack( "default-photobucket" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-photobucket.png" ) );
		R3D.pack( "default-audio-bg" , ThemeManager.getInstance().getBitmap( "theme/pack_source/default-audio-bg.png" ) );
		R3D.pack( "ic_menu_edit" , ThemeManager.getInstance().getBitmap( "theme/pack_source/ic_menu_edit.png" ) );
		R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
		// FileHandle file =
		// ThemeManager.getInstance().getBitmap("theme/virture/appStore.png");
		// Bitmap bmp1 = BitmapFactory.decodeStream(file.read());
		// Bitmap bmp = Bitmap.createScaledBitmap(bmp1, R3D.sidebar_widget_w,
		// R3D.sidebar_widget_h, true);
		// defVirCooeeStore = new IconToTexture3D(bmp,"酷易市场",Icon3D.getIconBg(),
		// Icon3D.titleBg);
		// bmp1.recycle();
		// bmp.recycle();
		// clockTexture = new
		// Texture3D(Gdx.files.internal("theme/widget/clock.png"));
		if( unselectRegion == null )
			unselectRegion = R3D.findRegion( "media-unselect" );
		if( selectedRegion == null )
			selectedRegion = R3D.findRegion( "media-selected" );
		Utils3D.showTimeFromStart( "end pack" );
	}
}
