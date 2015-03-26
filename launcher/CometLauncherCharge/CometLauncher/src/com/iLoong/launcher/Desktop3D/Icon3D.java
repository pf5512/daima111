package com.iLoong.launcher.Desktop3D;


import javax.microedition.khronos.opengles.GL11;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.net.Uri;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.iLoong.RR;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.ParticleAnim.ParticleCallback;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewCircled3D;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class Icon3D extends ViewCircled3D implements IconBase3D
{
	
	public static final int MSG_ICON_DOUBLECLICK = 0;
	public static final int MSG_ICON_LONGCLICK = 1;
	public static final int MSG_ICON_CLICK = 2;
	public static final int MSG_ICON_SELECTED = 3;
	public static final int MSG_ICON_UNSELECTED = 4;
	private boolean hasTouchDown = false;
	ItemInfo info;
	// ImageView3D uninstallImage;
	// ImageView3D hideImage;
	// ImageView3D showImage;
	private boolean isSelected = false;
	protected boolean uninstall = false;
	protected boolean canUninstall;
	protected boolean hide = false;
	protected boolean isHide = false;
	// public static Bitmap iconBg;
	// public static Bitmap iconBg_hl;
	public static Bitmap titleBg;
	private static Bitmap[] iconBgs;
	private static Bitmap mask;
	public static TextureRegion resumeTexture;
	public static TextureRegion hideTexture;
	public static TextureRegion uninstallTexture;
	public static TextureRegion paopaoRegion1;
	public static TextureRegion paopaoRegion2;
	public static TextureRegion[] numTextureRegion;
	private TextureRegion paopaoRegion;
	protected static int stateIconWidth;
	protected static int stateIconHeight;
	protected static int paopaoStateIconWidth1;
	protected static int paopaoStateIconWidth2;
	protected static int paopaoNumStateIconWidth;
	protected static int paopaoNumStateIconHeight;
	public static TextureRegion selectedTexture;
	private int paopaoStateIconWidth;
	private View3D tmpView = null;
	protected boolean needShowPaoPao = false;
	protected boolean inShowFolder = false;
	private boolean isSmsFlag = true; // 是短信还是电�?
	// private TextureRegion uninstallRegion = R3D.findRegion("app-uninstall");
	// private TextureRegion stopRegion = R3D.findRegion("task_stop");
	// private Tween shakeAnimation = null;
	// private Tween scaleAnimation = null;
	int number = 0;
	float paopao_stateX = 0f;
	float paopao_stateY = 0f;
	public float tempWidth;
	public float tempHeight;
	// xiatian add start //DownloadIcon
	public static TextureRegion dynamicMenuDownloadTexture;
	protected static int dynamicMenuDownloadIconWidth;
	protected static int dynamicMenuDownloadIconHeight;
	// xiatian add end
	private boolean isDark = false; // 是否接收down事件后变暗
	/************************ added by zhenNan.ye begin ***************************/
	protected boolean doubleClickFlag = false;
	/************************ added by zhenNan.ye end ***************************/
	private static Bitmap cover;
	static
	{
		iconBgs = new Bitmap[R3D.icon_bg_num];
		for( int i = 0 ; i < iconBgs.length ; i++ )
		{
			Bitmap bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/icon_" + i + ".png" );
			Bitmap bmp2 = Bitmap.createScaledBitmap( bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			iconBgs[i] = bmp2;
			if( bmp != bmp2 )
				bmp.recycle();
		}
		Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/icon_cover_plate.png" );
		if( original_icon_cover_bmp != null )
		{
			cover = Bitmap.createScaledBitmap( original_icon_cover_bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			if( original_icon_cover_bmp != cover )
				original_icon_cover_bmp.recycle();
		}
		Bitmap oriMask = ThemeManager.getInstance().getBitmap( "theme/iconbg/mask.png" );
		if( oriMask != null )
		{
			mask = Bitmap.createScaledBitmap( oriMask , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			if( oriMask != mask )
				oriMask.recycle();
		}
		// FileHandle file =
		// ThemeManager.getInstance().getGdxTextureResource("theme/iconbg/icon_hl.png");
		// Bitmap bmp = BitmapFactory.decodeStream(file.read());
		// iconBg_hl = Bitmap.createScaledBitmap(bmp, R3D.workspace_cell_width,
		// R3D.workspace_cell_width,false);
		if( DefaultLayout.show_font_bg && !DefaultLayout.font_double_line )
		{
			Bitmap bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/title_bg.png" );
			Paint p = new Paint();
			p.setTextSize( R3D.icon_title_font );
			FontMetrics fontMetrics = p.getFontMetrics();
			float singleLineHeight = (float)Math.ceil( fontMetrics.bottom - fontMetrics.top );
			titleBg = Bitmap.createScaledBitmap( bmp , R3D.workspace_cell_width , (int)( singleLineHeight + singleLineHeight / 5 ) , true );
			bmp.recycle();
		}
		stateIconWidth = R3D.workspace_multicon_width;
		stateIconHeight = R3D.workspace_multicon_height;
		paopaoStateIconWidth1 = stateIconWidth;
		paopaoStateIconWidth2 = R3D.workspace_paopao_big_width;
		paopaoNumStateIconWidth = R3D.workspace_paopao_num_width;
		paopaoNumStateIconHeight = R3D.workspace_paopao_num_height;
		// xiatian add start //DownloadIcon
		dynamicMenuDownloadIconWidth = R3D.dynamic_menu_download_icon_width;
		dynamicMenuDownloadIconHeight = R3D.dynamic_menu_download_icon_height;
		// xiatian add end
		// resumeTexture = new TextureRegion(new Texture3D(new
		// Pixmap(Gdx.files.internal("theme/pack_source/app-resume.png"))));
		resumeTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-resume.png" ) , true ) );
		hideTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-hide.png" ) , true ) );
		uninstallTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall.png" ) , true ) );
		paopaoRegion1 = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/icon_dian.png" ) , true ) );
		paopaoRegion2 = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/icon_dian_2.png" ) , true ) );
		numTextureRegion = new TextureRegion[10];
		for( int i = 0 ; i < 10 ; i++ )
		{
			numTextureRegion[i] = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/icon_dian_num" + i + ".png" ) , true ) );
		}
		selectedTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-multi-choice.png" ) , true ) );
		// xiatian add start //DownloadIcon
		dynamicMenuDownloadTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/dynamic_menu_download.png" ) , true ) );
		// xiatian add end
	}
	
	public Icon3D(
			String name )
	{
		super( name );
	}
	
	public Icon3D(
			String name ,
			Texture texture )
	{
		super( name , texture );
	}
	
	public Icon3D(
			String name ,
			Bitmap bmp ,
			String title )
	{
		super( name , new IconToTexture3D( bmp , title , getIconBg() , titleBg ) );
	}
	
	public Icon3D(
			String name ,
			Bitmap bmp ,
			String title ,
			Bitmap IconBg )
	{
		super( name , new IconToTexture3D( bmp , title , IconBg , titleBg ) );
	}
	
	public Icon3D(
			String name ,
			TextureRegion region )
	{
		super( name , region );
	}
	
	public void updateNumber()
	{
		//		if( isSmsFlag == true )
		//		{
		//			number = iLoongLauncher.getInstance().getMissedSmsNum();
		//		}
		//		else
		//		{
		//			number = iLoongLauncher.getInstance().getMissedCallNum();
		//		}
	}
	
	public void setting()
	{
		if( !DefaultLayout.show_missed_call_sms )
			return;
		Log.v( "Icon3D" , "paopao setting" );
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		if( info.intent.getComponent() == null )
		{
			if( info.intent.getAction() != null )
			{
				if( info.intent.getAction().equals( "android.intent.action.MAIN" ) )
				{
					if( info.intent.getType() != null && info.intent.getType().equals( "vnd.android-dir/mms-sms" ) )
					{
						needShowPaoPao = true;
						isSmsFlag = true;
					}
				}
				else if( info.intent.getAction().equals( "android.intent.action.DIAL" ) )
				{
					needShowPaoPao = true;
					isSmsFlag = false;
				}
			}
		}
		else
		{
			String packageName = info.intent.getComponent().getPackageName();
			String className = info.intent.getComponent().getClassName();
			needShowPaoPao = false;
			if( packageName != null && className != null )
			{
				if( packageName.equals( "com.android.mms" ) )
				{
					needShowPaoPao = true;
					isSmsFlag = true;
				}
				else if( packageName.equals( "com.android.contacts" ) && className.equals( DefaultLayout.call_component_name ) )
				{
					needShowPaoPao = true;
					isSmsFlag = false;
				}
			}
		}
		if( needShowPaoPao == true )
		{
			Log.v( "Icon3D" , "setting ok" );
			// regsitedMissCall();
			updateNumber();
		}
	}
	
	static final Vector2 point = new Vector2();
	static final Vector2 coords = new Vector2();
	
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Log.v( "click" , "View3D onClick:" + name + " x:" + x + " y:" + y );
		SendMsgToAndroid.sysPlaySoundEffect();
		if( isSelected() )
		{
			cancelSelected();
			/************************ added by zhenNan.ye begin *************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					doubleClickFlag = true;
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			return true;
		}
		else if( uninstall )
		{
			if( !canUninstall )
			{
			}
			else if( info instanceof ShortcutInfo )
			{
				uninstall();
			}
			return true;
		}
		else if( hide )
		{
			ShortcutInfo temp = (ShortcutInfo)this.getItemInfo();
			ComponentName name = temp.intent.getComponent();
			if( name != null )
			{
				isHide = !isHide;
				PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putBoolean( "HIDE:" + name.toString() , isHide ).commit();
				if( temp.appInfo != null )
					temp.appInfo.isHideIcon = isHide;
			}
			return true;
		}
		else
		{
			if( ( iLoongLauncher.getInstance().getD3dListener().getAppList().appList.mode != iLoongLauncher.getInstance().getD3dListener().getAppList().appList.APPLIST_MODE_NORMAL ) && ( isApplistVirtualIcon() == true ) )
			{
				return true;
			}
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					if( particleCanRender )
					{
						return true;
					}
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			return viewParent.onCtrlEvent( this , MSG_ICON_CLICK );
		}
		// return viewParent.onCtrlEvent(this, MSG_ICON_CLICK);
	}
	
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( hide || uninstall )
			return true;
		if( isApplistVirtualIcon() == true )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.download_to_install ) );
			return true;
		}
		if( isSelected() )
		{
			cancelSelected();
		}
		else
		{
			selected();
		}
		return true;
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		releaseDark();
		Log.v( "click" , " icon3d onLongClick:" + name + " x:" + x + " y:" + y );
		if( isApplistVirtualIcon() == true )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.download_to_install ) );
			return true;
		}
		//teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( hide )
				return true;
		}
		else if( hide || uninstall )
			return true;
		//teapotXu add end for Folder in Mainmenu
		if( !this.isDragging )
		{
			// if(this.getParent().getParent() instanceof AppList3D){
			// ClingManager.getInstance().cancelAllAppCling();
			// }
			this.toAbsoluteCoords( point );
			this.setTag( new Vector2( point.x , point.y ) );
			point.x = x;
			point.y = y;
			this.toAbsolute( point );
			DragLayer3D.dragStartX = point.x;
			DragLayer3D.dragStartY = point.y;
			return viewParent.onCtrlEvent( this , MSG_ICON_LONGCLICK );
		}
		return false;
	}
	
	/* 画数�?x,y是中心点 */
	public void drawNumber(
			SpriteBatch batch ,
			float parentAlpha ,
			float f ,
			float g ,
			int number )
	{
		int num = number;
		int i = 0 , w = 0;
		if( num != 0 )
		{
			while( num != 0 )
			{
				num /= 10;
				i++;
			}
		}
		else
		{
			i = 1;
		}
		w = ( i + 1 ) * paopaoNumStateIconWidth / 2;
		g -= paopaoNumStateIconHeight / 2;
		for( int j = 0 ; j <= i ; j++ )
		{
			int k = number % 10;
			number /= 10;
			batch.draw( numTextureRegion[k] , f - w + ( i - 1 - j ) * paopaoNumStateIconHeight , g );
		}
	}
	
	/* 画数�?x,y是中心点 */
	public void drawNumber(
			SpriteBatch batch ,
			float parentAlpha ,
			float f ,
			float g ,
			int number ,
			float orgx ,
			float orgy ,
			float scalex ,
			float scaley ,
			float rotate )
	{
		int num = number;
		int i = 0 , w = 0;
		if( num != 0 )
		{
			while( num != 0 )
			{
				num /= 10;
				i++;
			}
		}
		else
		{
			i = 1;
		}
		w = (int)( i * paopaoNumStateIconWidth / 2 );
		g -= paopaoNumStateIconHeight / 2;
		for( int j = 0 ; j < i ; j++ )
		{
			int k = number % 10;
			number /= 10;
			float x = f - w + ( i - 1 - j ) * paopaoNumStateIconWidth;
			batch.draw( numTextureRegion[k] , x , g , this.x + orgx - x , orgy - this.height + paopaoNumStateIconHeight , paopaoNumStateIconWidth , paopaoNumStateIconHeight , scalex , scaley , rotate );
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( region.getTexture() == null )
			return;
		/************************ added by zhenNan.ye begin *************************/
		drawParticleEffect( batch );
		/************************ added by zhenNan.ye end ***************************/
		if( region.getTexture() instanceof BitmapTexture )
		{
			BitmapTexture texture = (BitmapTexture)region.getTexture();
			if( needAntiAlias() )
				texture.changeFilter( TextureFilter.Linear , TextureFilter.Linear );
			else
				texture.changeFilter( TextureFilter.Nearest , TextureFilter.Nearest );
		}
		//		Log.d("icon", "AA:"+(region.getTexture().getMinFilter()==TextureFilter.Linear));
		int srcBlendFunc = 0 , dstBlendFunc = 0;
		if( DefaultLayout.blend_func_dst_gl_one )
		{
			/* 获取获取混合方式 */
			srcBlendFunc = batch.getSrcBlendFunc();
			dstBlendFunc = batch.getDstBlendFunc();
		}
		//teapotXu add start for Folder in Mainmenu
		float iconRotation = this.rotation;
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( isShake )
				iconRotation = this.rotation - SHAKE_ROTATE_ANGLE / 2;
		}
		//teapotXu add end for Folder in Mainmenu		
		int x = Math.round( this.x );
		int y = Math.round( this.y );
		float alpha = color.a;
		if( isHide && ( uninstall || hide ) )
		{
			alpha *= 0.2;
		}
		updateNumber();
		if( number > 9 )
		{
			paopaoRegion = paopaoRegion2;
			paopaoStateIconWidth = paopaoStateIconWidth2;
		}
		else
		{
			paopaoRegion = paopaoRegion1;
			paopaoStateIconWidth = paopaoStateIconWidth1;
		}
		batch.setColor( color.r , color.g , color.b , alpha * parentAlpha );
		float stateX = this.x + this.width - stateIconWidth;
		float stateY = this.y + this.height - stateIconHeight;
		paopao_stateX = this.x + this.width - paopaoStateIconWidth;
		paopao_stateY = this.y + this.height - stateIconHeight;
		// xiatian add start //DownloadIcon
		float dynamicMenuDownloadX = 0f , dynamicMenuDownloadY = 0f;
		dynamicMenuDownloadX = getDownloadIconX();
		dynamicMenuDownloadY = getDownloadIconY();
		// xiatian add end
		if( is3dRotation() )
		{
			batch.draw( region , x , y , width , height );
			if( DefaultLayout.blend_func_dst_gl_one && ( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA ) )
				batch.setBlendFunction( GL11.GL_SRC_ALPHA , GL11.GL_ONE_MINUS_SRC_ALPHA );
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			if( uninstall && canUninstall )
			{
				if( !isApplistVirtualIcon() )
				{
					batch.draw( uninstallTexture , stateX , stateY , stateIconWidth , stateIconHeight );
				}
			}
			if( hide && isHide )
			{
				batch.draw( resumeTexture , stateX , stateY , stateIconWidth , stateIconHeight );
			}
			if( hide && !isHide )
			{
				batch.draw( hideTexture , stateX , stateY , stateIconWidth , stateIconHeight );
			}
			if( !isSelected && needShowPaoPao && number > 0 )
			{
				batch.draw( paopaoRegion , paopao_stateX , paopao_stateY , paopaoStateIconWidth , stateIconHeight );
				drawNumber( batch , parentAlpha , paopao_stateX + paopaoStateIconWidth / 2 , paopao_stateY + stateIconHeight / 2 , number );
			}
			if( DefaultLayout.blend_func_dst_gl_one && ( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA ) )
				batch.setBlendFunction( srcBlendFunc , dstBlendFunc );
			if( isApplistVirtualIcon() == true )
			{
				batch.draw( dynamicMenuDownloadTexture , dynamicMenuDownloadX , dynamicMenuDownloadY , dynamicMenuDownloadIconWidth , dynamicMenuDownloadIconHeight );
			}
			if( this instanceof WidgetIcon )
			{
				WidgetIcon icon = (WidgetIcon)this;
				ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
				if( !icon.isFeatureShortcut() && info.intent != null && info.intent.getAction().equals( Intent.ACTION_PACKAGE_INSTALL ) )
				{
					batch.draw( dynamicMenuDownloadTexture , dynamicMenuDownloadX , dynamicMenuDownloadY , dynamicMenuDownloadIconWidth , dynamicMenuDownloadIconHeight );
				}
			}
			if( isSelected )
			{
				batch.draw( selectedTexture , stateX , stateY , stateIconWidth , stateIconHeight );
				ClingManager.getInstance().cancelSelectCling();
			}
		}
		else
		{
			batch.draw( region , x , y , originX , originY , width , height , scaleX , scaleY , iconRotation );
			if( DefaultLayout.blend_func_dst_gl_one && ( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA ) )
				batch.setBlendFunction( GL11.GL_SRC_ALPHA , GL11.GL_ONE_MINUS_SRC_ALPHA );
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			float stateOriginX = originX - this.width + stateIconWidth;
			float stateOriginY = originY - this.height + stateIconHeight;
			// xiatian add start //DownloadIcon
			float dynamicMenuDownloadOriginX = 0f , dynamicMenuDownloadOriginY = 0f;
			dynamicMenuDownloadOriginX = this.x + this.originX - dynamicMenuDownloadX;
			dynamicMenuDownloadOriginY = this.y + this.originY - dynamicMenuDownloadY - 2;
			// xiatian add end
			if( uninstall && canUninstall )
			{
				if( !isApplistVirtualIcon() )
				{
					batch.draw( uninstallTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation );
				}
			}
			if( hide && isHide )
			{
				batch.draw( resumeTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation );
			}
			if( hide && !isHide )
			{
				batch.draw( hideTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation );
			}
			if( !isSelected && needShowPaoPao && number > 0 )
			{
				float stateOriginX2 = this.x + this.originX - paopao_stateX;
				float stateOriginY2 = originY - this.height + stateIconHeight;
				batch.draw( paopaoRegion , paopao_stateX , paopao_stateY , stateOriginX2 , stateOriginY2 , paopaoStateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation/*this.rotation*/);
				drawNumber(
						batch ,
						parentAlpha ,
						paopao_stateX + paopaoStateIconWidth / 2 ,
						paopao_stateY + stateIconHeight / 2 ,
						number ,
						this.originX ,
						this.originY ,
						scaleX ,
						scaleY ,
						iconRotation/*this.rotation*/);
			}
			if( DefaultLayout.blend_func_dst_gl_one && ( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA ) )
				batch.setBlendFunction( srcBlendFunc , dstBlendFunc );
			if( isApplistVirtualIcon() == true )
			{
				batch.draw(
						dynamicMenuDownloadTexture ,
						dynamicMenuDownloadX ,
						dynamicMenuDownloadY ,
						dynamicMenuDownloadOriginX ,
						dynamicMenuDownloadOriginY ,
						dynamicMenuDownloadIconWidth ,
						dynamicMenuDownloadIconHeight ,
						scaleX ,
						scaleY ,
						iconRotation/*rotation*/);
			}
			if( this instanceof WidgetIcon )
			{
				WidgetIcon icon = (WidgetIcon)this;
				ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
				if( !icon.isFeatureShortcut() && info.intent != null && info.intent.getAction().equals( Intent.ACTION_PACKAGE_INSTALL ) )
				{
					batch.draw(
							dynamicMenuDownloadTexture ,
							dynamicMenuDownloadX ,
							dynamicMenuDownloadY ,
							dynamicMenuDownloadOriginX ,
							dynamicMenuDownloadOriginY ,
							dynamicMenuDownloadIconWidth ,
							dynamicMenuDownloadIconHeight ,
							scaleX ,
							scaleY ,
							iconRotation/*rotation*/
					);
				}
			}
			if( isSelected )
			{
				batch.draw( selectedTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation/*rotation*/);
				ClingManager.getInstance().cancelSelectCling();
			}
		}
		if( tmpView != null )
		{
			tmpView.applyTransformChild( batch );
			tmpView.draw( batch , parentAlpha );
			tmpView.resetTransformChild( batch );
		}
		// point.x = 0;
		// point.y = 0;
		// this.toAbsoluteCoords(point);
		// Log.v("coords"," name:" + name + " x:" + x + " y:" + y + " absX:" +
		// point.x + " absY:" + point.y);
	}
	
	public boolean belongShowContainer(
			ShortcutInfo shortcutInfo )
	{
		// Log.v("jbc","belongShowContainer");
		long container_id = shortcutInfo.container;
		if( container_id == LauncherSettings.Favorites.CONTAINER_DESKTOP )
		{
			return true;
		}
		if( container_id == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
		{
			return true;
		}
		if( container_id > 0 )
		{// 文件�?
			return false;
		}
		if( container_id == shortcutInfo.NO_ID )
		{
			return false;
		}
		return false;
	}
	
	public void setInShowFolder(
			boolean canshow )
	{
		inShowFolder = canshow;
	}
	
	public boolean getInShowFolder()
	{
		return inShowFolder;
	}
	
	public boolean canShowPop(
			ShortcutInfo shortcutInfo )
	{
		if( belongShowContainer( shortcutInfo ) || getInShowFolder() )
			return true;
		needShowPaoPao = false;
		return false;
	}
	
	public void setItemInfo(
			ItemInfo info )
	{
		this.info = info;
		if( info instanceof ShortcutInfo )
		{
			ShortcutInfo shortcutInfo = (ShortcutInfo)info;
			if( ( shortcutInfo.flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 )
			{
				Intent intent = shortcutInfo.intent;
				if( intent != null && intent.getComponent() != null && intent.getComponent().getPackageName().equals( RR.getPackageName() ) )
					canUninstall = false;
				else
					canUninstall = true;
			}
			else if( ( shortcutInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) == 0 )
			{
				Intent intent = shortcutInfo.intent;
				if( intent != null && intent.getComponent() != null && intent.getComponent().getPackageName().equals( RR.getPackageName() ) )
					canUninstall = false;
				else
					canUninstall = true;
			}
			else
				canUninstall = false;
			Intent intent = shortcutInfo.intent;
			if( canShowPop( shortcutInfo ) )
				setting();
			if( intent != null && intent.getComponent() != null )
			{
				isHide = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + intent.getComponent().toString() , false );
			}
		}
	}
	
	@Override
	public Icon3D clone()
	{
		if( this.region.getTexture() == null )
		{
			Log.e( "iLoong" , " icon:" + this + " region is null!!" );
			return null;
		}
		Icon3D icon = new Icon3D( this.name , this.region );
		if( this.background9 != null )
		{
			icon.setBackgroud( this.background9 );
		}
		// this.info.itemType=
		// LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
		icon.setItemInfo( new ShortcutInfo( (ShortcutInfo)this.info ) );
		icon.needShowPaoPao = this.needShowPaoPao;
		//teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			//	该标志也需要clone
			icon.canUninstall = this.canUninstall;
			icon.uninstall = this.uninstall;
			icon.hide = this.hide;
			icon.isHide = this.isHide;
		}
		//teapotXu add end for Folder in Mainmenu		
		return icon;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		// if(type == TweenCallback.COMPLETE){
		// isSelected = false;
		// }
		if( tmpView != null && source == tmpView.getTween() && type == TweenCallback.COMPLETE )
		{
			tmpView.remove();
			tmpView.stopTween();
			tmpView = null;
		}
		//teapotXu add start for FolderMainMenu && shake
		if( DefaultLayout.mainmenu_folder_function == true && source == shakeAnimation && type == TweenCallback.COMPLETE )
		{
			float r;
			if( this.rotation == SHAKE_ROTATE_ANGLE )
			{
				//				this.setOrigin(point1.x, point1.y);
				r = 0f;
			}
			else
			{
				//				this.setOrigin(point2.x, point2.y);
				r = SHAKE_ROTATE_ANGLE;
			}
			shakeAnimation = this.startTween( View3DTweenAccessor.ROTATION , Linear.INOUT , shakeTime , r , 0 , 0 ).setCallback( this );
		}
		//teapotXu add end for FolderMainMenu && shake				
	}
	
	public void selected()
	{
		isSelected = true;
		viewParent.onCtrlEvent( this , MSG_ICON_SELECTED );
	}
	
	public void cancelSelected()
	{
		// selectedImage.setScale(1, 1);
		// selectedImage.startTween(View3DTweenAccessor.SCALE_XY, Linear.INOUT,
		// 0.3f, 0.3f, 0.3f, 0).setCallback(this);
		if( !isSelected )
			return;
		isSelected = false;
		if( viewParent == null )
		{
			// Log.d("launcher", "error");
		}
		else
			viewParent.onCtrlEvent( this , MSG_ICON_UNSELECTED );
	}
	
	public void hideSelectedIcon()
	{
		// selectedImage.setScale(1, 1);
		// selectedImage.startTween(View3DTweenAccessor.SCALE_XY, Linear.INOUT,
		// 0.3f, 0.3f, 0.3f, 0).setCallback(this);
		isSelected = false;
	}
	
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public void showUninstall()
	{
		uninstall = true;
		hide = false;
		cancelSelected();
		//teapotXu add start for FolderMainMenu && shake
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			//widget icon donot need shaking
			//			if(!(this instanceof Widget3DVirtual))
			{
				shake( true );
			}
		}
		//teapotXu add end for FolderMainMenu && shake		
	}
	
	public void showHide()
	{
		if( isApplistVirtualIcon() == true )
		{
			return;
		}
		hide = true;
		uninstall = false;
		cancelSelected();
	}
	
	public void clearState()
	{
		cancelSelected();
		uninstall = false;
		hide = false;
		//teapotXu add start for FolderMainMenu && shake
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			//widget icon donot need shaking
			//			if(!(this instanceof Widget3DVirtual))
			{
				shake( false );
			}
		}
		//teapotXu add end for FolderMainMenu && shake		
	}
	
	//teapotXu add start for Folder in Mainmenu
	public void setUninstallStatus(
			boolean is_uninstall )
	{
		uninstall = is_uninstall;
	}
	
	public boolean getUninstallStatus()
	{
		return uninstall;
	}
	
	public boolean getHideStatus()
	{
		return hide;
	}
	
	// add for shake
	private boolean isShake = false;
	private Vector2 point1 = new Vector2();
	private Vector2 point2 = new Vector2();
	private float shakeTime;
	private Tween shakeAnimation = null;
	private final static float SHAKE_ROTATE_ANGLE = 5.0f;
	
	public void reset_shake_status()
	{
		isShake = false;
		if( shakeAnimation != null )
		{
			shakeAnimation.kill();
		}
		shakeAnimation = null;
		this.setRotation( 0 );
	}
	
	public void shake(
			boolean shake )
	{
		if( isShake == shake )
			return;
		if( !shake )
		{
			//Log.e(TAG, "icon:" + this + " shake:" + shake);
		}
		isShake = shake;
		if( shake )
		{
			//			Log.e("cooee", "shake:" + shake + " isShake:" + isShake + " icon:" + this);
			//			Log.e("cooee", "shake: shakeAnimation = " + shakeAnimation);
			if( shakeAnimation != null )
			{
				shakeAnimation.kill();
			}
			Double d = Math.random();
			shakeTime = (float)( d * 0.03f + 0.09f );
			point1.x = width / 4 + (float)( d * ( width / 2 ) );
			point1.y = height / 4 + (float)( d * height / 2 );
			d = Math.random();
			point2.x = width / 4 + (float)( d * ( width / 2 ) );
			point2.y = height / 4 + (float)( d * height / 2 );
			//			this.setOrigin(point1.x, point1.y);
			shakeAnimation = this.startTween( View3DTweenAccessor.ROTATION , Linear.INOUT , shakeTime , SHAKE_ROTATE_ANGLE , 0 , 0 ).setCallback( this );
		}
		else
		{
			// this.stopTween();
			if( shakeAnimation != null )
			{
				shakeAnimation.kill();
			}
			shakeAnimation = null;
			//this.setOrigin(width / 2, height / 2);
			this.setRotation( 0 );
		}
	}
	
	//teapotXu add end for Folder in Mainmenu	
	public boolean uninstall()
	{
		if( !this.canUninstall )
			return false;
		ShortcutInfo temp = (ShortcutInfo)this.getItemInfo();
		ComponentName name = temp.intent.getComponent();
		if( !( name == null ) )
		{
			String packageName = name.getPackageName();
			if( packageName != null && !packageName.equals( "" ) )
			{
				Uri packageURI = Uri.parse( "package:" + packageName );
				Intent intent = new Intent( Intent.ACTION_DELETE , packageURI );
				// 执行卸载程序
				iLoongLauncher.getInstance().startActivity( intent );
				return true;
			}
		}
		if( isApplistVirtualIcon() == true )
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( !hide )
		{
			hasTouchDown = true;
			Log.v( "isTouchDown" , "value" + hasTouchDown );
			Desktop3D.doubleClick = true;
			if( DefaultLayout.enable_icon_effect )
			{
				if( tmpView == null )
				{
					tmpView = this.clone();
					if( tmpView != null )
					{
						this.toAbsoluteCoords( point );
						tmpView.x = point.x;
						tmpView.y = point.y;
						tmpView.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.3f , 0 , 0 , 0 );
						tmpView.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , 1.5f , 1.5f , 0 ).setCallback( this );
					}
				}
			}
			else
			{
				requestDark();
			}
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					ParticleManager.disableClickIcon = false;
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		return true;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		releaseDark();
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( !hasTouchDown )
		{
			Log.v( "isTouchDown" , "value1111" + hasTouchDown );
			releaseDark();
			return super.onTouchUp( x , y , pointer );
		}
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				if( !particleCanRender && !ParticleManager.disableClickIcon )
				{
					Vector2 point = new Vector2();
					this.toAbsoluteCoords( point );
					float iconHeight = Utils3D.getIconBmpHeight();
					float positionX = point.x + this.width / 2;
					float positionY = point.y + ( this.height - iconHeight ) + iconHeight / 2;
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_ICON , positionX , positionY ).setCallback( this );
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		hasTouchDown = false;
		Log.v( "isTouchDown" , "value222222" + hasTouchDown );
		releaseDark();
		return super.onTouchUp( x , y , pointer );
	}
	
	public static Bitmap getIconBg()
	{
		if( iconBgs == null )
			return null;
		if( iconBgs.length == 0 )
			return null;
		return iconBgs[(int)( Math.random() * iconBgs.length )];
	}
	
	public static Bitmap getMask()
	{
		return mask;
	}
	
	public static Bitmap getCover()
	{
		return cover;
	}
	
	@Override
	public ItemInfo getItemInfo()
	{
		// TODO Auto-generated method stub
		return info;
	}
	
	public int getClingX()
	{
		point.x = 0;
		point.y = 0;
		this.toAbsolute( point );
		return (int)( point.x + width / 2 );
	}
	
	public int getClingY()
	{
		point.x = 0;
		point.y = 0;
		this.toAbsolute( point );
		return (int)( point.y + height / 2 + 5 );
	}
	
	public int getClingR()
	{
		return (int)( width / 2 );
	}
	
	public void setShowPop(
			boolean show )
	{
		this.needShowPaoPao = show;
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( DefaultLayout.hotseat_title_disable_click )
		{
			if( this.info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
			{
				Group.toChildCoordinates( this , x , y , point );
				return( ( point.x >= 0 && point.x < width ) && ( point.y >= height - Utilities.sIconTextureHeight && point.y < height ) );
			}
		}
		return super.pointerInParent( x , y );
	}
	
	public boolean isApplistVirtualIcon()
	{
		ShortcutInfo mShortcutInfo = (ShortcutInfo)this.getItemInfo();
		if( mShortcutInfo.appInfo != null && mShortcutInfo.appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	// xiatian add start //DownloadIcon
	public float getDownloadIconX()
	{
		float ret = 0f;
		float scale = 1f;
		if( ( this.getItemInfo().container == -101 ) && ( this.getItemInfo().angle == 0 ) )
		{//mainGroup
			scale = this.height / iLoongLauncher.getInstance().d3dListener.getRoot().getHotSeatBar().getMainGroup().height;
		}
		ret = this.x + ( ( this.width - scale * Utilities.sIconTextureWidth ) / 2 ) + scale * Utilities.sIconTextureWidth - dynamicMenuDownloadIconWidth + 2;
		return ret;
	}
	
	public float getDownloadIconY()
	{
		float ret = 0f;
		float bmpHeight = Utilities.sIconTextureHeight;
		float space_height = 0;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
		}
		else
		{
			space_height = bmpHeight / 10;
		}
		ret = this.y + ( this.height - Utils3D.getIconBmpHeight() + space_height ) - 2;
		if( ( this.getItemInfo().container == -101 ) && ( this.getItemInfo().angle == 0 ) )
		{//mainGroup
			float scale = this.height / iLoongLauncher.getInstance().d3dListener.getRoot().getHotSeatBar().getMainGroup().height;
			ret = this.y + ( this.height - scale * bmpHeight - scale * space_height ) / 2 - 3;
		}
		return ret;
	}
	
	// xiatian add end
	/************************ added by zhenNan.ye begin *************************/
	@Override
	public void onParticleCallback(
			int type )
	{
		// TODO Auto-generated method stub
		if( type == ParticleCallback.END )
		{
			if( !isSelected && !doubleClickFlag && !uninstall )
			{
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_ICON );
				viewParent.onCtrlEvent( this , MSG_ICON_CLICK );
				stopAllParticle();
			}
			else
			{
				doubleClickFlag = false;
			}
		}
	}
	
	private void drawParticleEffect(
			SpriteBatch batch )
	{
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				if( ParticleManager.dropEnable )
				{
					Tween tween = getTween();
					if( tween != null )
					{
						float targetValues[] = tween.getTargetValues();
						float iconHeight = Utils3D.getIconBmpHeight();
						float targetCenterX = targetValues[0] + width / 2;
						float targetCenterY = targetValues[1] + ( height - iconHeight ) + iconHeight / 2;
						float curCenterX = x + width / 2;
						float curCenterY = y + ( height - iconHeight ) + iconHeight / 2;
						if( Math.abs( curCenterX - targetCenterX ) < 2 || Math.abs( curCenterY - targetCenterY ) < 2 )
						{
							stopParticle( ParticleManager.PARTICLE_TYPE_NAME_DROP );
							startParticle( ParticleManager.PARTICLE_TYPE_NAME_DROP , targetCenterX , targetCenterY );
							ParticleManager.dropEnable = false;
						}
					}
				}
				drawParticle( batch );
			}
		}
	}
	
	/************************ added by zhenNan.ye end ***************************/
	public void requestDark()
	{
		if( !isDark )
		{
			this.color.a = 0.6f;
			isDark = true;
		}
	}
	
	public void releaseDark()
	{
		if( isDark )
		{
			this.color.a = 1f;
			isDark = false;
			hasTouchDown = false;
		}
	}
}

class IconToTexture3D extends BitmapTexture
{
	
	public IconToTexture3D(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg )
	{
		super( Utils3D.IconToPixmap3D( b , title , icn_bg , title_bg ) );
		// setFilter(TextureFilter.Linear,TextureFilter.Linear);
		setFilter( R3D.filter , R3D.Magfilter );
	}
}
