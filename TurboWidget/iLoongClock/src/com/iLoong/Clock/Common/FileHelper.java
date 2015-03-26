package com.iLoong.Clock.Common;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.iLoong.launcher.Desktop3D.Log;


public class FileHelper
{
	
	private Context context;
	private String SDPATH;
	private String FILESPATH;
	
	public FileHelper(
			Context context )
	{
		this.context = context;
		SDPATH = Environment.getExternalStorageDirectory().getPath() + File.separator;
		if( this.context.getFilesDir() == null )
		{
			FILESPATH = File.separator + "data" + File.separator + "data" + File.separator + "com.iLoong" + File.separator + "widget" + File.separator + context.getPackageName().substring(
					context.getPackageName().lastIndexOf( "." ) + 1 ) + File.separator;
		}
		else
		{
			FILESPATH = this.context.getFilesDir().getPath() + File.separator;
		}
	}
	
	/**
	 * ��SD���ϴ����ļ�
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(
			String fileName ) throws IOException
	{
		File file = new File( SDPATH + fileName );
		file.createNewFile();
		return file;
	}
	
	/**
	 * ɾ��SD���ϵ��ļ�
	 * 
	 * @param fileName
	 *            �ļ������SDCard�����Ŀ¼
	 */
	public boolean delSDFile(
			String fileName )
	{
		File file = new File( SDPATH + fileName );
		if( file == null || !file.exists() || file.isDirectory() )
			return false;
		return file.delete();
	}
	
	/**
	 * ��SD���ϴ���Ŀ¼
	 * 
	 * @param dirName
	 */
	public File creatSDDir(
			String dirName )
	{
		File dir = new File( SDPATH + dirName );
		dir.mkdirs();
		return dir;
	}
	
	/**
	 * ɾ��SD���ϵ�Ŀ¼
	 * 
	 * @param dirName
	 */
	public boolean delSDDir(
			String dirName )
	{
		File dir = new File( SDPATH + dirName );
		return delDir( dir );
	}
	
	/**
	 * �޸�SD���ϵ��ļ���Ŀ¼��
	 * 
	 * @param fileName
	 */
	public boolean renameSDFile(
			String oldfileName ,
			String newFileName )
	{
		File oldFile = new File( SDPATH + oldfileName );
		File newFile = new File( SDPATH + newFileName );
		return oldFile.renameTo( newFile );
	}
	
	/**
	 * ����SD���ϵĵ����ļ�
	 * 
	 * @param path
	 * @throws IOException
	 */
	public boolean copySDFileTo(
			String srcFileName ,
			String destFileName ) throws IOException
	{
		File srcFile = new File( SDPATH + srcFileName );
		File destFile = new File( SDPATH + destFileName );
		return copyFileTo( srcFile , destFile );
	}
	
	/**
	 * ����SD����ָ��Ŀ¼�������ļ�
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean copySDFilesTo(
			String srcDirName ,
			String destDirName ) throws IOException
	{
		File srcDir = new File( SDPATH + srcDirName );
		File destDir = new File( SDPATH + destDirName );
		return copyFilesTo( srcDir , destDir );
	}
	
	/**
	 * �ƶ�SD���ϵĵ����ļ�
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean moveSDFileTo(
			String srcFileName ,
			String destFileName ) throws IOException
	{
		File srcFile = new File( SDPATH + srcFileName );
		File destFile = new File( SDPATH + destFileName );
		return moveFileTo( srcFile , destFile );
	}
	
	/**
	 * �ƶ�SD���ϵ�ָ��Ŀ¼�������ļ�
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean moveSDFilesTo(
			String srcDirName ,
			String destDirName ) throws IOException
	{
		File srcDir = new File( SDPATH + srcDirName );
		File destDir = new File( SDPATH + destDirName );
		return moveFilesTo( srcDir , destDir );
	}
	
	/*
	 * ���ļ�д��sd������:writeSDFile("test.txt");
	 */
	public FileOutputStream writeSDFile(
			String fileName ) throws IOException
	{
		File file = new File( SDPATH + fileName );
		FileOutputStream fos = new FileOutputStream( file );
		return fos;
	}
	
	/*
	 * ��ԭ���ļ��ϼ���д�ļ�����:appendSDFile("test.txt");
	 */
	public FileOutputStream appendSDFile(
			String fileName ) throws IOException
	{
		File file = new File( SDPATH + fileName );
		FileOutputStream fos = new FileOutputStream( file , true );
		return fos;
	}
	
	/*
	 * ��SD����ȡ�ļ�����:readSDFile("test.txt");
	 */
	public FileInputStream readSDFile(
			String fileName ) throws IOException
	{
		File file = new File( SDPATH + fileName );
		FileInputStream fis = new FileInputStream( file );
		return fis;
	}
	
	public File readSdFile(
			String fileName )
	{
		return new File( SDPATH + fileName );
	}
	
	/**
	 * ����˽���ļ�
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File creatPrivateFile(
			String fileName ) throws IOException
	{
		File file = new File( FILESPATH + fileName );
		file.createNewFile();
		return file;
	}
	
	/**
	 * ����˽��Ŀ¼
	 * 
	 * @param dirName
	 * @return
	 */
	public File creatDataDir(
			String dirName )
	{
		File dir = new File( FILESPATH + dirName );
		if( !dir.exists() )
		{
			dir.mkdirs();
		}
		return dir;
	}
	
	/**
	 * ɾ��˽���ļ�
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean delDataFile(
			String fileName )
	{
		File file = new File( FILESPATH + fileName );
		return delFile( file );
	}
	
	/**
	 * ɾ��˽��Ŀ¼
	 * 
	 * @param dirName
	 * @return
	 */
	public boolean delDataDir(
			String dirName )
	{
		File file = new File( FILESPATH + dirName );
		return delDir( file );
	}
	
	/**
	 * ���˽���ļ���
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public boolean renameDataFile(
			String oldName ,
			String newName )
	{
		File oldFile = new File( FILESPATH + oldName );
		File newFile = new File( FILESPATH + newName );
		return oldFile.renameTo( newFile );
	}
	
	/**
	 * ��˽��Ŀ¼�½����ļ�����
	 * 
	 * @param srcFileName
	 *            �� ��·�����ļ���
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean copyDataFileTo(
			String srcFileName ,
			String destFileName ) throws IOException
	{
		File srcFile = new File( FILESPATH + srcFileName );
		File destFile = new File( FILESPATH + destFileName );
		return copyFileTo( srcFile , destFile );
	}
	
	/**
	 * ����˽��Ŀ¼��ָ��Ŀ¼�������ļ�
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean copyDataFilesTo(
			String srcDirName ,
			String destDirName ) throws IOException
	{
		File srcDir = new File( FILESPATH + srcDirName );
		File destDir = new File( FILESPATH + destDirName );
		return copyFilesTo( srcDir , destDir );
	}
	
	/**
	 * �ƶ�˽��Ŀ¼�µĵ����ļ�
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean moveDataFileTo(
			String srcFileName ,
			String destFileName ) throws IOException
	{
		File srcFile = new File( FILESPATH + srcFileName );
		File destFile = new File( FILESPATH + destFileName );
		return moveFileTo( srcFile , destFile );
	}
	
	/**
	 * �ƶ�˽��Ŀ¼�µ�ָ��Ŀ¼�µ������ļ�
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean moveDataFilesTo(
			String srcDirName ,
			String destDirName ) throws IOException
	{
		File srcDir = new File( FILESPATH + srcDirName );
		File destDir = new File( FILESPATH + destDirName );
		return moveFilesTo( srcDir , destDir );
	}
	
	/*
	 * ���ļ�д��Ӧ��˽�е�filesĿ¼����:writeFile("test.txt");
	 */
	public OutputStream wirteFile(
			String fileName ) throws IOException
	{
		OutputStream os = context.openFileOutput( fileName , Context.MODE_WORLD_WRITEABLE );
		return os;
	}
	
	/*
	 * ��ԭ���ļ��ϼ���д�ļ�����:appendFile("test.txt");
	 */
	public FileOutputStream appendFile(
			String fileName ) throws IOException
	{
		FileOutputStream os = new FileOutputStream( FILESPATH + fileName );
		return os;
	}
	
	/*
	 * ��Ӧ�õ�˽��Ŀ¼files��ȡ�ļ�����:readFile("test.txt");
	 */
	public InputStream readFile(
			String fileName ) throws IOException
	{
		InputStream is = context.openFileInput( fileName );
		return is;
	}
	
	/**********************************************************************************************************/
	/*********************************************************************************************************/
	/**
	 * ɾ��һ���ļ�
	 * 
	 * @param file
	 * @return
	 */
	public boolean delFile(
			File file )
	{
		if( file.isDirectory() )
			return false;
		return file.delete();
	}
	
	/**
	 * ɾ��һ��Ŀ¼�������Ƿǿ�Ŀ¼��
	 * 
	 * @param dir
	 */
	public boolean delDir(
			File dir )
	{
		if( dir == null || !dir.exists() || dir.isFile() )
		{
			return false;
		}
		for( File file : dir.listFiles() )
		{
			if( file.isFile() )
			{
				file.delete();
			}
			else if( file.isDirectory() )
			{
				delDir( file );// �ݹ�
			}
		}
		dir.delete();
		return true;
	}
	
	/**
	 * ����һ���ļ�,srcFileԴ�ļ���destFileĿ���ļ�
	 * 
	 * @param path
	 * @throws IOException
	 */
	public boolean copyFileTo(
			File srcFile ,
			File destFile ) throws IOException
	{
		if( srcFile.isDirectory() || destFile.isDirectory() )
			return false;// �ж��Ƿ����ļ�
		FileInputStream fis = new FileInputStream( srcFile );
		FileOutputStream fos = new FileOutputStream( destFile );
		int readLen = 0;
		byte[] buf = new byte[1024];
		while( ( readLen = fis.read( buf ) ) != -1 )
		{
			fos.write( buf , 0 , readLen );
		}
		fos.flush();
		fos.close();
		fis.close();
		return true;
	}
	
	/**
	 * ����Ŀ¼�µ������ļ���ָ��Ŀ¼
	 * 
	 * @param srcDir
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public boolean copyFilesTo(
			File srcDir ,
			File destDir ) throws IOException
	{
		if( !srcDir.isDirectory() || !destDir.isDirectory() )
			return false;// �ж��Ƿ���Ŀ¼
		if( !destDir.exists() )
			return false;// �ж�Ŀ��Ŀ¼�Ƿ����
		File[] srcFiles = srcDir.listFiles();
		for( int i = 0 ; i < srcFiles.length ; i++ )
		{
			if( srcFiles[i].isFile() )
			{
				// ���Ŀ���ļ�
				File destFile = new File( destDir.getPath() + "//" + srcFiles[i].getName() );
				copyFileTo( srcFiles[i] , destFile );
			}
			else if( srcFiles[i].isDirectory() )
			{
				File theDestDir = new File( destDir.getPath() + "//" + srcFiles[i].getName() );
				copyFilesTo( srcFiles[i] , theDestDir );
			}
		}
		return true;
	}
	
	/**
	 * �ƶ�һ���ļ�
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 * @throws IOException
	 */
	public boolean moveFileTo(
			File srcFile ,
			File destFile ) throws IOException
	{
		boolean iscopy = copyFileTo( srcFile , destFile );
		if( !iscopy )
			return false;
		delFile( srcFile );
		return true;
	}
	
	/**
	 * �ƶ�Ŀ¼�µ������ļ���ָ��Ŀ¼
	 * 
	 * @param srcDir
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public boolean moveFilesTo(
			File srcDir ,
			File destDir ) throws IOException
	{
		if( !srcDir.isDirectory() || !destDir.isDirectory() )
		{
			return false;
		}
		File[] srcDirFiles = srcDir.listFiles();
		for( int i = 0 ; i < srcDirFiles.length ; i++ )
		{
			if( srcDirFiles[i].isFile() )
			{
				File oneDestFile = new File( destDir.getPath() + "//" + srcDirFiles[i].getName() );
				moveFileTo( srcDirFiles[i] , oneDestFile );
				delFile( srcDirFiles[i] );
			}
			else if( srcDirFiles[i].isDirectory() )
			{
				File oneDestFile = new File( destDir.getPath() + "//" + srcDirFiles[i].getName() );
				moveFilesTo( srcDirFiles[i] , oneDestFile );
				delDir( srcDirFiles[i] );
			}
		}
		return true;
	}
	
	/**
	 * Helper Method to Test if external Storage is Available
	 */
	public boolean isExternalStorageAvailable()
	{
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if( Environment.MEDIA_MOUNTED.equals( extStorageState ) )
		{
			state = true;
		}
		return state;
	}
	
	/**
	 * Helper Method to Test if external Storage is read only
	 */
	public boolean isExternalStorageReadOnly()
	{
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if( Environment.MEDIA_MOUNTED_READ_ONLY.equals( extStorageState ) )
		{
			state = true;
		}
		return state;
	}
	
	public boolean createDir(
			String path )
	{
		boolean ret = false;
		try
		{
			File file = new File( path );
			if( !file.exists() )
				file.mkdirs();
			if( !file.isDirectory() )
			{
				ret = file.mkdirs();
			}
		}
		catch( Exception e )
		{
			Log.e( "create dir" , e.toString() );
			e.printStackTrace();
		}
		return ret;
	}
	
	public void writeToExernalStorage(
			String outFile ,
			float[] bytes ) throws IOException
	{
		File f = this.creatSDFile( outFile );
		try
		{
			FileOutputStream fileOutStream = new FileOutputStream( f );
			ObjectOutputStream oos = new ObjectOutputStream( fileOutStream );
			for( int i = 0 ; i < bytes.length ; i++ )
			{
				oos.writeFloat( bytes[i] );
			}
			fileOutStream.close();
			oos.close();
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean writeToExternalStoragePublic(
			Context context ,
			String filePath ,
			Bitmap bitmap )
	{
		boolean writeSuccessful = true;
		File tempFile = new File( filePath );
		if( isExternalStorageAvailable() && !isExternalStorageReadOnly() )
		{
			File fileDir = this.creatSDDir( tempFile.getParent() );
			if( fileDir.exists() )
			{
				FileOutputStream fileOutStream = null;
				ByteArrayOutputStream bytesOutStream = null;
				try
				{
					File file = this.creatSDFile( filePath );
					fileOutStream = new FileOutputStream( file );
					bytesOutStream = new ByteArrayOutputStream();
					bitmap.compress( Bitmap.CompressFormat.PNG , 100 , bytesOutStream );
					fileOutStream.write( bytesOutStream.toByteArray() );
					fileOutStream.flush();
				}
				catch( FileNotFoundException e )
				{
					writeSuccessful = false;
					Log.e( "writeToExternalStoragePublic" , e.toString() );
					e.printStackTrace();
				}
				catch( IOException e )
				{
					writeSuccessful = false;
					Log.e( "writeToExternalStoragePublic" , e.toString() );
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if( fileOutStream != null )
						{
							fileOutStream.close();
						}
						if( bytesOutStream != null )
						{
							bytesOutStream.close();
						}
					}
					catch( IOException e )
					{
						// TODO Auto-generated catch block
						Log.e( "writeToExternalStoragePublic" , e.toString() );
						e.printStackTrace();
						writeSuccessful = false;
					}
				}
			}
		}
		return writeSuccessful;
	}
	
	public byte[] readExternallStoragePublic(
			String filePath )
	{
		int len = 1024;
		byte[] buffer = new byte[len];
		if( !isExternalStorageReadOnly() )
		{
			FileInputStream inputStream = null;
			ByteArrayOutputStream byteOutStream = null;
			try
			{
				File file = new File( SDPATH + filePath );
				if( file.exists() )
				{
					inputStream = new FileInputStream( file );
					byteOutStream = new ByteArrayOutputStream();
					int byteCount = inputStream.read( buffer , 0 , len ); // read up
					while( byteCount != -1 )
					{
						byteOutStream.write( buffer , 0 , byteCount );
						byteCount = inputStream.read( buffer , 0 , len );
					}
					buffer = byteOutStream.toByteArray();
					inputStream.close();
					byteOutStream.close();
				}
				else
				{
					buffer = new byte[0];
				}
			}
			catch( FileNotFoundException e )
			{
				Log.e( "readExternallStoragePublic" , e.toString() );
				e.printStackTrace();
				buffer = new byte[0];
			}
			catch( IOException e )
			{
				Log.e( "readExternallStoragePublic" , e.toString() );
				e.printStackTrace();
				buffer = new byte[0];
			}
			finally
			{
				try
				{
					if( inputStream != null )
						inputStream.close();
					if( byteOutStream != null )
					{
						byteOutStream.close();
					}
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					Log.e( "readExternallStoragePublic" , e.toString() );
					e.printStackTrace();
				}
			}
		}
		return buffer;
	}
	
	public byte[] readInternalStoragePublic(
			String filePath )
	{
		int len = 1024;
		byte[] buffer = new byte[len];
		if( !isExternalStorageReadOnly() )
		{
			FileInputStream inputStream = null;
			ByteArrayOutputStream byteOutStream = null;
			try
			{
				File file = new File( FILESPATH + filePath );
				if( file.exists() )
				{
					inputStream = new FileInputStream( file );
					byteOutStream = new ByteArrayOutputStream();
					int byteCount = inputStream.read( buffer , 0 , len );
					while( byteCount != -1 )
					{
						byteOutStream.write( buffer , 0 , byteCount );
						byteCount = inputStream.read( buffer , 0 , len );
					}
					buffer = byteOutStream.toByteArray();
					inputStream.close();
					byteOutStream.close();
				}
				else
				{
					buffer = new byte[0];
				}
			}
			catch( FileNotFoundException e )
			{
				Log.e( "readInternalStoragePublic" , e.toString() );
				e.printStackTrace();
				buffer = new byte[0];
			}
			catch( IOException e )
			{
				Log.e( "readInternalStoragePublic" , e.toString() );
				e.printStackTrace();
				buffer = new byte[0];
			}
			finally
			{
				try
				{
					if( inputStream != null )
						inputStream.close();
					if( byteOutStream != null )
					{
						byteOutStream.close();
					}
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					Log.e( "readExternallStoragePublic" , e.toString() );
					e.printStackTrace();
				}
			}
		}
		return buffer;
	}
	
	public Bitmap decodeFile(
			File f )
	{
		int IMAGE_MAX_SIZE = 536;
		Bitmap b = null;
		FileInputStream fis = null;
		try
		{
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			fis = new FileInputStream( f );
			BitmapFactory.decodeStream( fis , null , o );
			fis.close();
			int scale = 1;
			if( o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE )
			{
				scale = (int)Math.pow( 2 , (int)Math.round( Math.log( IMAGE_MAX_SIZE / (double)Math.max( o.outHeight , o.outWidth ) ) / Math.log( 0.5 ) ) );
			}
			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream( f );
			b = BitmapFactory.decodeStream( fis , null , o2 );
			fis.close();
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
		return b;
	}
	
	public boolean writeToInternalStoragePublic(
			Context context ,
			String filePath ,
			Bitmap bitmap )
	{
		boolean writeSuccessful = true;
		// ����Ŀ¼
		File tempFile = new File( filePath );
		File fileDir = this.creatDataDir( tempFile.getParent() );
		// �ļ�д��
		if( fileDir.exists() )
		{
			OutputStream fileOutStream = null;
			ByteArrayOutputStream bytesOutStream = null;
			try
			{
				fileOutStream = this.appendFile( filePath );
				bytesOutStream = new ByteArrayOutputStream();
				bitmap.compress( Bitmap.CompressFormat.PNG , 100 , bytesOutStream );
				fileOutStream.write( bytesOutStream.toByteArray() );
				fileOutStream.flush();
			}
			catch( FileNotFoundException e )
			{
				writeSuccessful = false;
				Log.e( "writeToExternalStoragePublic" , e.toString() );
				e.printStackTrace();
			}
			catch( IOException e )
			{
				writeSuccessful = false;
				Log.e( "writeToExternalStoragePublic" , e.toString() );
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if( fileOutStream != null )
					{
						fileOutStream.close();
					}
					if( bytesOutStream != null )
					{
						bytesOutStream.close();
					}
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					Log.e( "writeToExternalStoragePublic" , e.toString() );
					e.printStackTrace();
					writeSuccessful = false;
				}
			}
		}
		return writeSuccessful;
	}
}
