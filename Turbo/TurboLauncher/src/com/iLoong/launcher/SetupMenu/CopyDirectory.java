package com.iLoong.launcher.SetupMenu;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class CopyDirectory
{
	
	static String url1 = "";
	static String url2 = "";
	
	public static void main(
			String args[] ) throws IOException
	{
		( new File( url2 ) ).mkdirs();
		File[] file = ( new File( url1 ) ).listFiles();
		for( int i = 0 ; i < file.length ; i++ )
		{
			if( file[i].isFile() )
			{
				copyFile( file[i] , new File( url2 + file[i].getName() ) );
			}
			if( file[i].isDirectory() )
			{
				String sourceDir = url1 + File.separator + file[i].getName();
				String targetDir = url2 + File.separator + file[i].getName();
				copyDirectiory( sourceDir , targetDir );
			}
		}
	}
	
	public static void copyFile(
			File sourceFile ,
			File targetFile ) throws IOException
	{
		FileInputStream input = new FileInputStream( sourceFile );
		BufferedInputStream inBuff = new BufferedInputStream( input );
		FileOutputStream output = new FileOutputStream( targetFile );
		BufferedOutputStream outBuff = new BufferedOutputStream( output );
		byte[] b = new byte[1024 * 5];
		int len;
		while( ( len = inBuff.read( b ) ) != -1 )
		{
			outBuff.write( b , 0 , len );
		}
		outBuff.flush();
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}
	
	public static void copyDirectiory(
			String sourceDir ,
			String targetDir ) throws IOException
	{
		( new File( targetDir ) ).mkdirs();
		File[] file = ( new File( sourceDir ) ).listFiles();
		for( int i = 0 ; i < file.length ; i++ )
		{
			if( file[i].isFile() )
			{
				File sourceFile = file[i];
				File targetFile = new File( new File( targetDir ).getAbsolutePath() + File.separator + file[i].getName() );
				copyFile( sourceFile , targetFile );
			}
			if( file[i].isDirectory() )
			{
				String dir1 = sourceDir + "/" + file[i].getName();
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory( dir1 , dir2 );
			}
		}
	}
	
	public static void delete(
			File file )
	{
		if( !file.exists() )
			return;
		if( file.isFile() )
		{
			file.delete();
			return;
		}
		if( file.isDirectory() )
		{
			File[] childFiles = file.listFiles();
			if( childFiles == null || childFiles.length == 0 )
			{
				file.delete();
				return;
			}
			for( int i = 0 ; i < childFiles.length ; i++ )
			{
				delete( childFiles[i] );
			}
			file.delete();
		}
	}
}
