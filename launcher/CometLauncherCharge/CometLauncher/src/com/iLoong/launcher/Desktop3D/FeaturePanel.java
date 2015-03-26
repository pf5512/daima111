package com.iLoong.launcher.Desktop3D;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;

import java.io.IOException;
import java.util.Vector;


public class FeaturePanel
{
	
	public DetectWallpaperChanged wallpaperChaned;
	public FrameSurface frameSurface[] = new FrameSurface[2];
	public Vector2 splitPoint;
	public float frameScaleX;
	public float frameHeight;
	public Texture baseTexture;
	public TextureRegion lineRegion;
	public float lineHeight = 37f;
	public float lineWidth = 262;
	public boolean isStart = false;
	public static int currScreen = 0;
	public static int StepCount = 0;
	
	public FeaturePanel()
	{
		wallpaperChaned = new DetectWallpaperChanged();
		frameScaleX = Utils3D.getScreenWidth() / 720.0f;
		frameHeight = Utils3D.getScreenWidth() / 2.0f * frameScaleX;
		lineWidth = 4 / 720.0f * Utils3D.getScreenWidth();
		lineHeight = lineWidth * 37;
		Log.v( "Root3D" , "lineHeight " + lineHeight );
		try
		{
			Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/frame_surface.png" ) );
			if( bmp.getConfig() != Config.ARGB_8888 )
			{
				bmp = bmp.copy( Config.ARGB_8888 , false );
			}
			baseTexture = new BitmapTexture( bmp );
			bmp.recycle();
			Bitmap bmpl = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/frame_line.png" ) );
			if( bmpl.getConfig() != Config.ARGB_8888 )
			{
				bmpl = bmpl.copy( Config.ARGB_8888 , false );
			}
			Texture t = new BitmapTexture( bmpl );
			bmpl.recycle();
			lineRegion = new TextureRegion( t , 0 , 0 , t.getWidth() , t.getHeight() );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction( Intent.ACTION_WALLPAPER_CHANGED );
		iLoongLauncher.getInstance().registerReceiver( wallpaperChaned , filter );
	}
	
	public void initData(
			Vector2 point )
	{
		splitPoint = point;
		initSurface();
	}
	
	public void initSurface()
	{
		//        if(frameSurface[0]!=null&&frameSurface[0].region!=null&&frameSurface[0].region.getTexture()!=null){
		//            frameSurface[0].region.getTexture().dispose();
		//        }
		//        if(frameSurface[1]!=null&&frameSurface[1].region!=null&&frameSurface[1].region.getTexture()!=null){
		//            frameSurface[1].region.getTexture().dispose();
		//        }
		frameSurface[0] = new FrameSurface( "frameSurface[0]" );
		frameSurface[0].x = 0;
		frameSurface[0].y = 0;
		frameSurface[0].width = Utils3D.getScreenWidth();
		frameSurface[0].height = Utils3D.getScreenHeight() - splitPoint.y;
		frameSurface[0].initType( 0 );
		frameSurface[1] = new FrameSurface( "frameSurface[1]" );
		frameSurface[1].x = 0;
		frameSurface[1].y = 0;
		frameSurface[1].width = Utils3D.getScreenWidth();
		frameSurface[1].height = splitPoint.y;
		frameSurface[1].initType( 1 );
		//        if(frameSurface[0]!=null){
		//            try{
		//                Bitmap bmp = BitmapFactory.decodeStream(iLoongLauncher.getInstance().getAssets()
		//                        .open("theme/pack_source/frame_surface.png"));
		//                if (bmp.getConfig() != Config.ARGB_8888) {
		//                    bmp = bmp.copy(Config.ARGB_8888, false);
		//                }
		//                Texture t = new BitmapTexture(bmp);
		//       
		//                bmp.recycle();
		//                frameSurface[0].region = new TextureRegion(t,0,
		//                        t.getHeight(), t.getWidth(),-t.getHeight());
		//                //frameSurface[0].region.setRegion();
		//       
		//       
		//             
		////                frameSurface[1].region = new TextureRegion(t,0,
		////                        -t.getHeight(), t.getWidth(),0);
		//                frameSurface[1].region.setRegion(t);
		//            }catch(IOException e){
		//                e.printStackTrace();
		//            }
		//        }
	}
	
	public View3D getFrameSurface0()
	{
		return frameSurface[0];
	}
	
	public View3D getFrameSurface1()
	{
		return frameSurface[1];
	}
	
	class FrameSurface extends View3D
	{
		
		public TextureRegion baseRegion;
		public int type = 0;
		
		public FrameSurface(
				String name )
		{
			super( name );
			//  this.setScale(frameScaleX, 1);
		}
		
		public void initType(
				int type )
		{
			this.type = type;
			if( baseRegion != null )
			{
				baseRegion.getTexture().dispose();
			}
			if( type == 0 )
			{
				//                this.region = new TextureRegion(baseTexture,0,
				//                        baseTexture.getHeight(), baseTexture.getWidth(),-baseTexture.getHeight());
				//                
				baseRegion = new TextureRegion( baseTexture , 0 , baseTexture.getHeight() , baseTexture.getWidth() , -baseTexture.getHeight() );
			}
			else
			{
				baseRegion = new TextureRegion();
				baseRegion.setRegion( baseTexture );
				// this.region.setRegion(baseTexture);
			}
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			//  super.draw(batch, parentAlpha);
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			if( baseRegion != null )
			{
				if( type == 0 )
				{
					batch.draw( baseRegion , x , y , this.width , frameHeight );
					//draw left line first from bottom to  top
					for( int i = 0 ; i < ( this.height - frameHeight ) / lineHeight + 1 ; i++ )
					{
						batch.draw( lineRegion , x , y + frameHeight + i * lineHeight , originX , originY , lineWidth , lineHeight , scaleX , scaleY , rotation );
						// batch.draw(lineRegion, x,y+frameHeight+i*lineHeight,lineWidth,lineHeight);
					}
					//then draw right line.
					for( int i = 0 ; i < ( this.height - frameHeight ) / lineHeight + 1 ; i++ )
					{
						batch.draw( lineRegion , this.width - lineWidth , y + frameHeight + i * lineHeight , originX , originY , lineWidth , lineHeight , scaleX , scaleY , rotation );
						//batch.draw(lineRegion, this.width-lineWidth,y+frameHeight+i*lineHeight,lineWidth,lineHeight);
					}
				}
				else
				{
					batch.draw( baseRegion , x , this.height - frameHeight , this.width , frameHeight );
					//draw left line first from bottom to  top
					for( int i = 0 ; i < ( this.height - frameHeight ) / lineHeight + 1 ; i++ )
					{
						batch.draw( lineRegion , x , y + this.height - frameHeight - i * lineHeight , originX , originY , lineWidth , lineHeight , scaleX , scaleY , rotation );
						// batch.draw(lineRegion, x,y+this.height-frameHeight-i*lineHeight,lineWidth,lineHeight);
					}
					//then draw right line.
					for( int i = 0 ; i < ( this.height - frameHeight ) / lineHeight + 1 ; i++ )
					{
						batch.draw( lineRegion , this.width - lineWidth , y + this.height - frameHeight - i * lineHeight , originX , originY , lineWidth , lineHeight , scaleX , scaleY , rotation );
						//  batch.draw(lineRegion, this.width-lineWidth,y+this.height-frameHeight-i*lineHeight,lineWidth,lineHeight);
					}
					// batch.draw(baseRegion, x,y,this.width,this.height);
				}
			}
		}
	}
	
	class DetectWallpaperChanged extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context arg0 ,
				Intent intent )
		{
			String action = intent.getAction();
			if( action.equals( Intent.ACTION_WALLPAPER_CHANGED ) )
			{
				android.util.Log.v( "Root3D" , "ACTION_WALLPAPER_CHANGED xxxx" );
				// Root3D.getSceenDrawable();
				Root3D.screenUtils.setWallpagerBitmap();
			}
		}
	}
	
	public void finish()
	{
		if( wallpaperChaned != null )
			iLoongLauncher.getInstance().unregisterReceiver( wallpaperChaned );
	}
	
	public void setStart(
			boolean start )
	{
		this.isStart = start;
	}
	
	public boolean getStart()
	{
		return isStart;
	}
	
	public void onEffectionComplete()
	{
		setStart( false );
	}
}
