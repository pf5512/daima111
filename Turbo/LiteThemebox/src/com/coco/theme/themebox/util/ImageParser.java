package com.coco.theme.themebox.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageParser
{
	
	private static final String LOG_TAG = "ImageParser";
	
	private static class ByteOffsetArray
	{
		
		public byte[] bytes;
		public int offset;
		
		public ByteOffsetArray(
				byte[] content ,
				int offset )
		{
			this.bytes = content;
			this.offset = offset;
		}
		
		public String getString(
				int startPos ,
				int count )
		{
			if( startPos < 0 || count < 0 || startPos + count >= bytes.length )
			{
				return "";
			}
			return new String( bytes , startPos , count );
		}
		
		public boolean saveFile(
				int startPos ,
				int count ,
				String path )
		{
			if( startPos < 0 || count < 0 || startPos + count >= bytes.length )
			{
				return false;
			}
			FileOutputStream fo = null;
			try
			{
				fo = new FileOutputStream( path );
				fo.write( bytes , startPos , count );
				Log.d( LOG_TAG , "path=" + path + ";size=" + count );
				return true;
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			finally
			{
				if( fo != null )
				{
					try
					{
						fo.close();
					}
					catch( IOException e )
					{
						e.printStackTrace();
					}
				}
			}
			return false;
		}
		
		public int size()
		{
			return bytes.length - offset;
		}
		
		public boolean startWith(
				byte[] dest )
		{
			if( this.size() < dest.length )
			{
				return false;
			}
			int maxLen = dest.length;
			for( int i = 0 ; i < maxLen ; i++ )
			{
				if( bytes[offset + i] != dest[i] )
				{
					return false;
				}
			}
			return true;
		}
	}
	
	public boolean parseThumbFile(
			String downloadFile ,
			String filePath )
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream( downloadFile );
			byte[] readBts = new byte[fis.available()];
			fis.read( readBts );
			return parseImageFile( new ByteOffsetArray( readBts , 0 ) , filePath );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( fis != null )
			{
				try
				{
					fis.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public boolean parsePreviewFile(
			String downloadFile ,
			String dirPath )
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream( downloadFile );
			byte[] readBts = new byte[fis.available()];
			fis.read( readBts );
			ByteOffsetArray fileArray = new ByteOffsetArray( readBts , 0 );
			int index = 1;
			while( true )
			{
				File file = new File( dirPath );
				// 判断文件目录是否存在
				if( !file.exists() )
				{
					file.mkdir();
				}
				String filePath = dirPath + "/preview" + index + ".tupian";
				boolean parseResult = parseImageFile( fileArray , filePath );
				if( !parseResult )
				{
					break;
				}
				index++;
			}
			return true;
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( fis != null )
			{
				try
				{
					fis.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	private boolean parseImageFile(
			ByteOffsetArray content ,
			String localPath )
	{
		if( !findString( content , "<appid>".getBytes() ) )
		{
			return false;
		}
		content.offset += "<appid>".length();
		int appidStartIndex = content.offset;
		if( !findString( content , "</appid>".getBytes() ) )
		{
			return false;
		}
		String appId = content.getString( appidStartIndex , content.offset - appidStartIndex );
		content.offset += "</appid>".length();
		if( !findString( content , "<size>".getBytes() ) )
		{
			return false;
		}
		content.offset += "<size>".length();
		int sizeStartIndex = content.offset;
		if( !findString( content , "</size>".getBytes() ) )
		{
			return false;
		}
		String strSize = content.getString( sizeStartIndex , content.offset - sizeStartIndex );
		Log.d( LOG_TAG , String.format( "appid=%s,size=%s" , appId , strSize ) );
		int imgSize = Integer.parseInt( strSize );
		content.offset += "</size>".length();
		if( !findString( content , "<data>".getBytes() ) )
		{
			return false;
		}
		content.offset += "<data>".length();
		if( !content.saveFile( content.offset , imgSize , localPath ) )
		{
			return false;
		}
		content.offset += imgSize + "</data>".length();
		return true;
	}
	
	private boolean findString(
			ByteOffsetArray content ,
			byte[] findArray )
	{
		for( int i = content.offset ; i < content.bytes.length ; i++ )
		{
			content.offset = i;
			if( content.startWith( findArray ) )
			{
				return true;
			}
		}
		return false;
	}
}
