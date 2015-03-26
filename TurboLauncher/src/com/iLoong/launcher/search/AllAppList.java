package com.iLoong.launcher.search;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.graphics.Bitmap;
import android.view.inputmethod.InputMethodManager;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.coco.theme.themebox.util.Tools;
import com.google.analytics.tracking.android.Log;
import com.iLoong.launcher.Desktop3D.ListView3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class AllAppList extends ListView3D
{
	
	public static Set<String> mLetterList = new TreeSet<String>();
	private View3D mBottomFill;
	private SearchEditTextGroup mSearchEditTextGroup;
	public AllAppList(
			String name )
	{
		super( name );
		setSize( QsearchConstants.W_ALL_APP_LIST , QsearchConstants.H_ALL_APP_LIST );
		mSearchEditTextGroup = new SearchEditTextGroup( "searchEditTextGroup" );
		try
		{
			Bitmap bgBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/qs_bg.9.png" ) );
			NinePatch bgNp = new NinePatch( new BitmapTexture( bgBmp , true ) , 1 , 1 , 1 , 1 );
			setBackgroud( bgNp );
			Bitmap bottomFillBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/qs_bg.9.png" ) );
			NinePatch bottomFillNp = new NinePatch( new BitmapTexture( bottomFillBmp , true ) , 1 , 1 , 1 , 1 );
			mBottomFill = new View3D( "bottomFill" );
			mBottomFill.setBackgroud( bottomFillNp );
			mBottomFill.setSize( QsearchConstants.W_SEARCH_EDIT_GROUP , QsearchConstants.H_SEARCH_EDIT_GROUP );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public void load()
	{
		List<SearchApp> allApp = QSearchGroup.allApps;
		if( mLetterList != null )
		{
			mLetterList.clear();
		}
		for( int i = 0 ; i < allApp.size() ; i++ )
		{
			String name = allApp.get( i ).title_pinyin;
			String firstLetter = name.substring( 0 , 1 );
			if( firstLetter.equals( "A" ) || firstLetter.equals( "a" ) )
			{
				mLetterList.add( "A" );
			}
			else if( firstLetter.equals( "B" ) || firstLetter.equals( "b" ) )
			{
				mLetterList.add( "B" );
			}
			else if( firstLetter.equals( "C" ) || firstLetter.equals( "c" ) )
			{
				mLetterList.add( "C" );
			}
			else if( firstLetter.equals( "D" ) || firstLetter.equals( "d" ) )
			{
				mLetterList.add( "D" );
			}
			else if( firstLetter.equals( "E" ) || firstLetter.equals( "e" ) )
			{
				mLetterList.add( "E" );
			}
			else if( firstLetter.equals( "F" ) || firstLetter.equals( "f" ) )
			{
				mLetterList.add( "F" );
			}
			else if( firstLetter.equals( "G" ) || firstLetter.equals( "g" ) )
			{
				mLetterList.add( "G" );
			}
			else if( firstLetter.equals( "H" ) || firstLetter.equals( "h" ) )
			{
				mLetterList.add( "H" );
			}
			else if( firstLetter.equals( "I" ) || firstLetter.equals( "i" ) )
			{
				mLetterList.add( "I" );
			}
			else if( firstLetter.equals( "J" ) || firstLetter.equals( "j" ) )
			{
				mLetterList.add( "J" );
			}
			else if( firstLetter.equals( "K" ) || firstLetter.equals( "k" ) )
			{
				mLetterList.add( "K" );
			}
			else if( firstLetter.equals( "L" ) || firstLetter.equals( "l" ) )
			{
				mLetterList.add( "L" );
			}
			else if( firstLetter.equals( "M" ) || firstLetter.equals( "m" ) )
			{
				mLetterList.add( "M" );
			}
			else if( firstLetter.equals( "N" ) || firstLetter.equals( "n" ) )
			{
				mLetterList.add( "N" );
			}
			else if( firstLetter.equals( "O" ) || firstLetter.equals( "o" ) )
			{
				mLetterList.add( "O" );
			}
			else if( firstLetter.equals( "P" ) || firstLetter.equals( "p" ) )
			{
				mLetterList.add( "P" );
			}
			else if( firstLetter.equals( "Q" ) || firstLetter.equals( "q" ) )
			{
				mLetterList.add( "Q" );
			}
			else if( firstLetter.equals( "R" ) || firstLetter.equals( "r" ) )
			{
				mLetterList.add( "R" );
			}
			else if( firstLetter.equals( "S" ) || firstLetter.equals( "s" ) )
			{
				mLetterList.add( "S" );
			}
			else if( firstLetter.equals( "T" ) || firstLetter.equals( "t" ) )
			{
				mLetterList.add( "T" );
			}
			else if( firstLetter.equals( "U" ) || firstLetter.equals( "u" ) )
			{
				mLetterList.add( "U" );
			}
			else if( firstLetter.equals( "V" ) || firstLetter.equals( "v" ) )
			{
				mLetterList.add( "V" );
			}
			else if( firstLetter.equals( "W" ) || firstLetter.equals( "w" ) )
			{
				mLetterList.add( "W" );
			}
			else if( firstLetter.equals( "X" ) || firstLetter.equals( "x" ) )
			{
				mLetterList.add( "X" );
			}
			else if( firstLetter.equals( "Y" ) || firstLetter.equals( "y" ) )
			{
				mLetterList.add( "Y" );
			}
			else if( firstLetter.equals( "Z" ) || firstLetter.equals( "z" ) )
			{
				mLetterList.add( "Z" );
			}
			else
			{
				mLetterList.add( "#" );
			}
		}
		for( Iterator<String> it = mLetterList.iterator() ; it.hasNext() ; )
		{
			String fName = it.next();
			int categoryCount = 0;
			for( int i = 0 ; i < allApp.size() ; i++ )
			{
				SearchApp btn = allApp.get( i );
				String firstLetter = btn.title_pinyin.substring( 0 , 1 );
				if( fName.equals( "#" ) )
				{
					if( !firstLetter.equals( "A" ) && !firstLetter.equals( "a" ) && !firstLetter.equals( "B" ) && !firstLetter.equals( "b" ) && !firstLetter.equals( "C" ) && !firstLetter.equals( "c" ) && !firstLetter
							.equals( "D" ) && !firstLetter.equals( "d" ) && !firstLetter.equals( "E" ) && !firstLetter.equals( "e" ) && !firstLetter.equals( "F" ) && !firstLetter.equals( "f" ) && !firstLetter
							.equals( "G" ) && !firstLetter.equals( "g" ) && !firstLetter.equals( "H" ) && !firstLetter.equals( "h" ) && !firstLetter.equals( "I" ) && !firstLetter.equals( "i" ) && !firstLetter
							.equals( "J" ) && !firstLetter.equals( "j" ) && !firstLetter.equals( "K" ) && !firstLetter.equals( "k" ) && !firstLetter.equals( "L" ) && !firstLetter.equals( "l" ) && !firstLetter
							.equals( "M" ) && !firstLetter.equals( "m" ) && !firstLetter.equals( "N" ) && !firstLetter.equals( "n" ) && !firstLetter.equals( "O" ) && !firstLetter.equals( "o" ) && !firstLetter
							.equals( "P" ) && !firstLetter.equals( "p" ) && !firstLetter.equals( "Q" ) && !firstLetter.equals( "q" ) && !firstLetter.equals( "R" ) && !firstLetter.equals( "r" ) && !firstLetter
							.equals( "S" ) && !firstLetter.equals( "s" ) && !firstLetter.equals( "T" ) && !firstLetter.equals( "t" ) && !firstLetter.equals( "U" ) && !firstLetter.equals( "u" ) && !firstLetter
							.equals( "V" ) && !firstLetter.equals( "v" ) && !firstLetter.equals( "W" ) && !firstLetter.equals( "w" ) && !firstLetter.equals( "X" ) && !firstLetter.equals( "x" ) && !firstLetter
							.equals( "Y" ) && !firstLetter.equals( "Z" ) && !firstLetter.equals( "z" ) )
					{
						categoryCount++;
					}
				}
				else if( firstLetter.equals( fName ) )
				{
					categoryCount++;
				}
			}
			int numCells = 4 * 1;
			int row = ( categoryCount + numCells - 1 ) / numCells;
			AppListItem item = new AppListItem( fName , row );
			addItem( item );
		}
		int childCount = getChildCount();
		for( int i = 0 ; i < childCount ; i++ )
		{
			AppListItem item = (AppListItem)getChildAt( i );
			item.load();
		}
		addItem( mBottomFill );
	}
	
	public void reLoad()
	{
		removeAllViews();
		load();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if(mSearchEditTextGroup.mEditText!=null)
		{
		  if(y>mSearchEditTextGroup.mEditText.y)
		   {
		  	mSearchEditTextGroup.mEditText.hideInputKeyboard();
		   }
		}
		if( SearchEditTextGroup.mStatus == SearchEditTextGroup.POS_STATUS_TOP )
		{
			return true;
		}
		return super.onClick( x , y );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		return true;
	}
}
