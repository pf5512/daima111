/* ----------------------------------------------------------------------- Copyright 2012 - Alistair Rutherford - www.netthreads.co.uk
 * -----------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import android.view.KeyEvent;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupCircled3D;
import com.iLoong.launcher.data.ItemInfo;


/**
 * Scene layer.
 * 
 */
public class IconGroup3D extends ViewGroup3D
{
	
	private int mRow;
	private int mCol;
	private ImageView3D buttonOK;
	private ResizeButton3D buttonResize;
	private GridView3D iconContain;
	private NinePatch gridbackground;
	private int gridLeft;
	private int gridTop;
	private int gridRight;
	private int gridBottom;
	float ScaleX = 0;
	float ScaleY = 0;
	float transLateX = 0;
	float translateY = 0;
	boolean bPermitTrans = false;
	private static final int MIN_FLING_DISTANCE = 10;
	private int iconW = R3D.workspace_cell_width;
	private int iconH = R3D.workspace_cell_height;
	private int childMargin = R3D.icongroup_margin_left;
	View3D myActor;
	private static final String TAG = "ICONGROUP3D";
	
	/**
	 * Construct the screen.
	 * 
	 * @param stage
	 */
	public IconGroup3D(
			List<View3D> circleGroup )
	{
		this( null , circleGroup );
	}
	
	public IconGroup3D(
			String name ,
			List<View3D> circleGroup )
	{
		super( name );
		buildElements( circleGroup );
	}
	
	private void IconContainInit(
			List<View3D> circleGroup )
	{
		iconContain = new GridView3D( "iconGroupGrid" , gridRight - gridLeft , gridTop - gridBottom , 0 , 0 );
		iconContain.enableAnimation( true );
		iconContain.setPosition( gridLeft , gridBottom );
		int Count = circleGroup.size();
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = circleGroup.get( i );
			myActor.x = myActor.x - iconContain.x;
			myActor.y = myActor.y - iconContain.y;
			iconContain.addItem( myActor );
		}
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = circleGroup.get( i );
			if( myActor instanceof ViewGroupCircled3D )
			{
				if( myActor.width > R3D.workspace_cell_width )
				{
					iconW = (int)myActor.width;
				}
				if( myActor.height > R3D.workspace_cell_height )
				{
					iconH = (int)myActor.width;
				}
				break;
			}
		}
		//		if (iconContain.getChildCount()>0)
		//		{
		//		iconW = (int) iconContain.getChildAt(0).width;
		//		iconH = (int) iconContain.getChildAt(0).height;
		//		}
		iconContain.setPadding( childMargin , childMargin , childMargin , childMargin );
		adjustGridView();
	}
	
	private void buildElements(
			List<View3D> circleGroup )
	{
		//AtlasRegion mTexture_button_icon = R3D.getTextureRegion("menu-user-button1");
		buttonOK = new ImageView3D( "button_ok" , R3D.getTextureRegion( "public-button-return" ) );
		buttonResize = new ResizeButton3D( "button_resize" , R3D.getTextureRegion( "shell-interactive-grid-scale-button" ) );
		TextureRegion gridbgTexture = R3D.getTextureRegion( "shell-interactive-grid-bg" );
		gridbackground = new NinePatch( gridbgTexture , R3D.icongroup_round_radius , R3D.icongroup_round_radius , R3D.icongroup_round_radius , R3D.icongroup_round_radius );
		buttonOK.setSize( R3D.icongroup_button_width , R3D.icongroup_button_height );
		buttonResize.setSize( R3D.icongroup_button_width , R3D.icongroup_button_height );
		gridLeft = (int)( R3D.icongroup_button_width / 2 );
		gridBottom = gridLeft;
		gridTop = (int)( height - gridBottom - buttonOK.height / 2 );
		gridRight = (int)( width - gridLeft - buttonOK.width / 4 );
		if( gridTop - gridBottom > gridRight - gridLeft )
		{
			gridBottom = (int)( ( height - ( gridRight - gridLeft ) ) / 2 );
			gridTop = gridBottom + gridRight - gridLeft;
		}
		else
		{
			gridLeft = (int)( ( width - ( gridTop - gridBottom ) ) / 2 );
			gridRight = gridLeft + gridTop - gridBottom;
		}
		setButtonPos();
		IconContainInit( circleGroup );
		addView( iconContain );
		addView( buttonOK );
		addView( buttonResize );
	}
	
	private void setButtonPos()
	{
		buttonOK.x = gridRight - 3 * buttonOK.width / 4;
		buttonOK.y = gridTop - 3 * buttonOK.height / 4;
		buttonResize.x = gridRight - 3 * buttonOK.width / 4;
		buttonResize.y = gridBottom - buttonOK.height / 4;
		//		buttonOK.x = gridRight - buttonOK.width;
		//		buttonOK.y = gridTop - buttonOK.height ;
		//
		//		buttonResize.x = gridRight - buttonOK.width ;
		//		buttonResize.y = gridBottom;
	}
	
	private boolean GridOutRegion()
	{
		if( gridBottom > ( height / 2 ) - iconH )
		{
			return true;
		}
		if( gridTop < height / 2 + iconH )
		{
			return true;
		}
		if( gridRight < width / 2 + iconW )
		{
			return true;
		}
		if( gridLeft > ( width / 2 - iconW ) )
		{
			return true;
		}
		return false;
	}
	
	private void normalCalcGrid()
	{
		/*得到GRID的位置*/
		gridLeft = gridLeft - (int)ScaleX;
		gridTop = gridTop + (int)ScaleY;
		gridRight = gridRight + (int)ScaleX;
		gridBottom = gridBottom - (int)ScaleY;
		/*gridleft*/
		//		if (gridLeft<buttonOK.width/2)
		//		{
		//			gridLeft = (int) (buttonOK.width/2);
		//		}
		if( gridLeft <= 0 )
		{
			gridLeft = 0;
		}
		if( gridLeft > ( width / 2 - iconW ) )
		{
			gridLeft = (int)( width / 2 - iconW );
		}
		/*gripTop*/
		if( gridBottom < R3D.icongroup_bottom_limit + buttonOK.height / 4 )
		{
			gridBottom = (int)( R3D.icongroup_bottom_limit + buttonOK.height / 4 );
		}
		//if (gridBottom>(screen_height/2-60))
		if( gridBottom + iconH > height / 2 )
		{
			gridBottom = (int)( ( height / 2 ) - iconH );
		}
		/*grid right*/
		if( gridRight < width / 2 + iconW )
		{
			gridRight = (int)( width / 2 + iconW );
		}
		if( gridRight > ( width - buttonOK.width / 4 ) )
		{
			gridRight = (int)( width - buttonOK.width / 4 );
		}
		/*grid bottom*/
		if( gridTop < height / 2 + iconH )
		{
			gridTop = (int)( height / 2 + iconH );
		}
		if( gridTop > ( height - buttonOK.height / 4 ) )
		{
			gridTop = (int)( height - buttonOK.height / 4 );
		}
		//	Log.v(TAG," ReDrawLayout gridTop="+gridTop+ " gridBottom="+gridBottom);
		//	Log.v(TAG," ReDrawLayout gridLeft="+gridLeft+ " gridRight="+gridRight);
		/*得到背景图的位置*/
		/*得到两个按钮的位置*/
		//		  buttonOK.x= gridRight-buttonOK.width/2;
		//		  buttonOK.y=gridTop-buttonOK.height/2;
		//		  buttonResize.x=gridRight-buttonOK.width/2;
		//		  buttonResize.y=gridBottom-buttonOK.height/2;
		setButtonPos();
		adjustGridView();
		Log.v( TAG , " ReDrawLayout iconContain.x=" + iconContain.x + " iconContain.y=" + iconContain.y );
	}
	
	private void outRegionCalcGrid()
	{
		/*得到GRID的位置*/
		boolean bNeedDealHor = true;
		boolean bNeedDealVer = true;
		if( ScaleX >= 0 )
		{
			if( ( gridRight + (int)ScaleX ) > ( width - buttonOK.width / 4 ) )
			{
				bNeedDealHor = false;
			}
		}
		else
		{
			if( ( gridRight + (int)ScaleX ) <= 2 * iconW )
			{
				bNeedDealHor = false;
			}
		}
		if( ScaleY >= 0 )
		{
			if( gridBottom - (int)ScaleY < buttonOK.height / 4 )
			{
				bNeedDealVer = false;
			}
		}
		else
		{
			if( gridBottom - (int)ScaleY > this.getHeight() - 2 * iconH - buttonResize.getHeight() / 4 )
			{
				bNeedDealVer = false;
			}
		}
		if( bNeedDealVer )
		{
			gridTop = gridTop + (int)ScaleY;
			if( gridTop > ( height - buttonOK.height / 4 ) )
			{
				gridTop = (int)( height - buttonOK.height / 4 );
			}
			gridBottom = gridBottom - (int)ScaleY;
			if( gridTop - gridBottom <= 2 * iconH )
			{
				gridTop = gridTop - (int)ScaleY;
				gridBottom = gridBottom + (int)ScaleY;
			}
		}
		if( bNeedDealHor )
		{
			gridLeft = gridLeft - (int)ScaleX;
			if( gridLeft <= 0 )
			{
				gridLeft = 0;
			}
			gridRight = gridRight + (int)ScaleX;
			if( gridRight - gridLeft <= 2 * iconW )
			{
				gridLeft = gridLeft + (int)ScaleX;
				gridRight = gridRight - (int)ScaleX;
			}
		}
		/* 得到背景图的位置 */
		/* 得到两个按钮的位置 */
		//		buttonOK.x = gridRight - buttonOK.width / 2;
		//		buttonOK.y = gridTop - buttonOK.height / 2;
		//		buttonResize.x = gridRight - buttonOK.width / 2;
		//		buttonResize.y = gridBottom - buttonOK.height / 2;
		setButtonPos();
		adjustGridView();
	}
	
	public void foucsReDrawLayout()
	{
		boolean bOutRegion = GridOutRegion();
		if( bOutRegion )
		{
			outRegionCalcGrid();
		}
		else
		{
			normalCalcGrid();
		}
	}
	
	private void adjustGridView()
	{
		iconContain.setPosition( gridLeft , gridBottom );
		iconContain.setSize( gridRight - gridLeft , gridTop - gridBottom );
		iconContain.setBackgroud( gridbackground );
		CalcGridRowAndCol();
		iconContain.setCellCount( mCol , mRow );
	}
	
	private void translateIconGroup(
			float offsetX ,
			float offsetY )
	{
		gridLeft += offsetX;
		gridBottom -= offsetY;
		if( gridBottom <= buttonResize.getHeight() / 4 )
		{
			gridBottom = (int)( buttonResize.getHeight() / 4 );
		}
		if( gridLeft <= 0 )
		{
			gridLeft = 0;
		}
		if( gridLeft >= this.getWidth() - iconContain.getWidth() - buttonResize.getWidth() / 4 )
		{
			gridLeft = (int)( this.getWidth() - iconContain.getWidth() - buttonResize.getWidth() / 4 );
		}
		if( gridBottom >= this.getHeight() - iconContain.getHeight() - buttonResize.getHeight() / 4 )
		{
			gridBottom = (int)( this.getHeight() - iconContain.getHeight() - buttonResize.getHeight() / 4 );
		}
		gridTop = (int)( gridBottom + iconContain.getHeight() );
		gridRight = (int)( gridLeft + iconContain.getWidth() );
		//		buttonOK.setPosition(gridRight-buttonResize.getWidth()/2, 
		//				             gridTop-buttonResize.getHeight()/2);
		//		buttonResize.setPosition(gridRight-buttonResize.getWidth()/2,
		//				gridBottom-buttonResize.getHeight()/2);
		setButtonPos();
		iconContain.setPosition( gridLeft , gridBottom );
	}
	
	//	private boolean pointInView3D(float x,float y,float offsetX, float offsetY,View3D view3D)
	//	{
	//		if ( x>view3D.x-offsetX && x<view3D.x +view3D.getWidth()
	//				&& y>view3D.y && y< view3D.y+view3D.getHeight()+offsetY)
	//		{
	//			Log.v(TAG," pointInView3D true ");
	//			return true;
	//		}
	//		else
	//		{
	//			Log.v(TAG," pointInView3D false ");
	//			return false;
	//		}
	//	}
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		View3D hitView = hit( x , y );
		ScaleX = 0;
		ScaleY = 0;
		if( bPermitTrans == true )
		{
			releaseFocus();
			bPermitTrans = false;
			return true;
		}
		if( hitView.name == buttonOK.name )
		{
			DealButtonOKDown();
			return true;
		}
		if( x > buttonOK.x - buttonOK.width / 2 && x < buttonOK.x + buttonOK.width + buttonOK.width / 2 && y > buttonOK.y - buttonOK.height / 2 && y < buttonOK.y + buttonOK.height + buttonOK.height / 2 )
		{
			DealButtonOKDown();
			return true;
		}
		if( hitView.name == this.name )
		{
			DealButtonOKDown();
			return true;
		}
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		View3D hitView = hit( x , y );
		ScaleX = 0;
		ScaleY = 0;
		bPermitTrans = false;
		if( this.viewParent == null )
		{
			return true;
		}
		if( hitView == null )
		{
			super.onTouchDown( x , y , pointer );
			return true;
		}
		//Log.v(TAG," iconGroup3d on onTouchDown hitView.name"+hitView.name);
		if( hitView.name == buttonResize.name )
		{
			super.onTouchDown( x , y , pointer );
			return true;
		}
		if( x > buttonResize.x - buttonResize.width / 2 && x < ( buttonResize.x + buttonResize.width * 1.5 ) && y < ( buttonResize.y + buttonResize.height * 1.5 ) && y > ( buttonResize.y - buttonResize.height * 0.5 ) )
		{
			super.onTouchDown( x , y , pointer );
			return true;
		}
		if( x > buttonOK.x - buttonOK.width / 2 && x < buttonOK.x + buttonOK.width + buttonOK.width / 2 && y > buttonOK.y - buttonOK.height / 2 && y < buttonOK.y + buttonOK.height + buttonOK.height / 2 )
		{
			return true;
		}
		if( ( hitView.name == iconContain.name ) || ( x > gridLeft && x < gridRight && y > gridBottom && y < gridTop ) )
		{
			bPermitTrans = true;
			transLateX = x;
			translateY = y;
			requestFocus();
			return true;
		}
		return true;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		View3D hitView = hit( x , y );
		if( hitView == null )
		{
			return super.scroll( x , y , deltaX , deltaY );
		}
		if( bPermitTrans )
		{
			float transX;
			float transY;
			transX = ( x - transLateX );
			transY = ( translateY - y );
			translateIconGroup( transX , transY );
			transLateX = x;
			translateY = y;
			return true;
		}
		if( hitView.name == buttonResize.name )
		{
			point.x = x;
			point.y = y;
			buttonResize.toLocalCoordinates( point );
			buttonResize.startX = point.x;
			buttonResize.startY = point.y;
			//Log.v(TAG, "parent scroll startX= " + point.x + " startY= " + point.y);
			return buttonResize.scroll( point.x , point.y , deltaX , deltaY );
		}
		return true;
	}
	
	public void DealButtonOKDown()
	{
		final CellLayout3D currentLayout = ( (Workspace3D)this.viewParent ).getCurrentCellLayout();
		int Count = iconContain.getChildCount();
		iconContain.enableAnimation( false );
		ArrayList<View3D> templist = new ArrayList<View3D>();
		View3D tempActor;
		templist.clear();
		releaseFocus();
		for( int i = 0 ; i < Count ; i++ )
		{
			tempActor = iconContain.getChildAt( i );
			tempActor.x = tempActor.x + iconContain.x;
			tempActor.y = tempActor.y + iconContain.y;
			templist.add( tempActor );
		}
		iconContain.removeAllViews();
		for( int i = 0 ; i < templist.size() ; i++ )
		{
			tempActor = templist.get( i );
			if( currentLayout != null )
				currentLayout.addView( tempActor );
			if( tempActor instanceof IconBase3D )
			{
				ItemInfo info = ( (IconBase3D)tempActor ).getItemInfo();
				info.screen = ( (Workspace3D)this.viewParent ).getCurrentScreen();
				info.x = (int)tempActor.x;
				info.y = (int)tempActor.y;
				Root3D.addOrMoveDB( info );
			}
		}
		this.viewParent.removeView( this );
	}
	
	/*重叠的情况下的图标排列*/
	protected void onLayout_Get_RowAndColNum(
			int count ,
			int max_row_num ,
			int max_col_num ,
			float width ,
			float height )
	{
		/* 确定每行和列显示的实际数目 */
		int sqrtNum = (int)Math.sqrt( count );
		if( sqrtNum * sqrtNum == count )
		{
			mRow = mCol = sqrtNum;
		}
		else if( ( sqrtNum + 1 ) * sqrtNum >= count )
		{
			if( height / width > ( ( sqrtNum + 1 ) / ( (float)sqrtNum ) ) )
			{
				mRow = sqrtNum + 1;
				mCol = sqrtNum;
			}
			else
			{
				mRow = sqrtNum;
				mCol = sqrtNum + 1;
			}
		}
		else if( ( sqrtNum + 1 ) * sqrtNum < count )
		{
			if( height >= width )
			{
				if( height / width >= ( ( sqrtNum + 2 ) / (float)sqrtNum ) )
				{
					mRow = sqrtNum + 2;
					mCol = sqrtNum;
				}
				else
				{
					mRow = mCol = sqrtNum + 1;
				}
			}
			else
			{
				if( width / height >= ( ( sqrtNum + 2 ) / (float)sqrtNum ) )
				{
					mCol = sqrtNum + 2;
					mRow = sqrtNum;
				}
				else
				{
					mRow = mCol = sqrtNum + 1;
				}
			}
		}
		//		/* 行列的最小值不能小于2 */
		//		if (mCol <= 2) {
		//			mCol = 2;
		//			mRow = (count + 1) / 2;
		//		}
		//
		//		if (mRow <= 2) {
		//			mRow = 2;
		//			mCol = (count + 1) / 2;
		//		}
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
			return true;
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			DealButtonOKDown();
			return true;
		}
		return super.keyUp( keycode );
	}
	
	private void CalcGridRowAndCol()
	{
		int count = iconContain.getChildCount();
		float width = iconContain.getWidth();
		float height = iconContain.getHeight();
		// int max_row_num = (height-childTopMargin-childBottomMargin)/iconH;
		// int max_col_num = (width-childRightMargin-childLeftMargin)/iconW;
		int max_row_num = (int)( ( height - childMargin - childMargin ) / iconH );
		int max_col_num = (int)( ( width - childMargin - childMargin ) / iconW );
		//		if (count==4)
		//	     {
		//	    	 mRow = mCol=2;
		//	    	 return;
		//	     }
		//	     
		//	     if (count==3)
		//	     {
		//	    	 mRow = mCol =2;
		//	    	 return;
		//	     }
		//	     
		//	     if (count==2)
		//	     {
		//	    	 mRow=1;
		//	    	 mCol=2;
		//	    	 return;
		//	     }
		//	     
		//	     if (count==1)
		//	     {
		//	    	 mRow = mCol =1;
		//	    	 return;
		//	     }
		if( max_row_num == 1 && max_col_num >= count )
		{
			mCol = count;
			mRow = 1;
		}
		else if( max_col_num == 1 && max_row_num >= count )
		{
			mRow = count;
			mCol = 1;
		}
		else
		{
			onLayout_Get_RowAndColNum( count , max_row_num , max_col_num , width , height );
		}
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof Icon3D )
		{
			return true;
		}
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	/*内部内，让拖放Button有获取焦点功能*/
	public class ResizeButton3D extends ImageView3D
	{
		
		float startX = 0;
		float startY = 0;
		
		public ResizeButton3D(
				String name )
		{
			super( name );
			// TODO Auto-generated constructor stub
		}
		
		public ResizeButton3D(
				String name ,
				TextureRegion textureRegion )
		{
			super( name , textureRegion );
			// TODO Auto-generated constructor stub
		}
		
		public ResizeButton3D(
				String name ,
				Texture texture )
		{
			super( name , texture );
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			Log.v( TAG , " ResizeButton3D on onTouchDown " );
			if( pointer == 0 )
			{
				startX = x;
				startY = y;
				requestFocus();
			}
			return true;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			Log.v( TAG , " ResizeButton3D on onTouchUp " );
			if( pointer == 0 )
			{
				releaseFocus();
				return false;
			}
			return true;
		}
		
		@Override
		public boolean scroll(
				float x ,
				float y ,
				float deltaX ,
				float deltaY )
		{
			requestFocus();
			// Log.v(TAG, "x= " + x + " y= " + y);
			// Log.v(TAG, "scroll startX= " + startX + " startY= " + startY);
			if( Math.sqrt( ( x - startX ) * ( x - startX ) + ( y - startY ) * ( y - startY ) ) > MIN_FLING_DISTANCE )
			{
				/* 重新得到放大比例因子 */
				ScaleX = ( x - startX );
				ScaleY = ( startY - y );
				//Log.v(TAG, "ScaleX= " + ScaleX + " ScaleY= " + ScaleY);
				foucsReDrawLayout();
				//	this.viewParent.onCtrlEvent(this,0);
			}
			return true;
		}
	}
	/*内部内定义结束*/
}
