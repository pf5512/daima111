package com.coco.theme.themebox.database.model;


public enum DownloadStatus
{
	StatusInit( 0 ) , StatusDownloading( 1 ) , StatusPause( 2 ) , StatusFinish( 3 );
	
	private int value;
	
	private DownloadStatus(
			int v )
	{
		this.value = v;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public static DownloadStatus fromValue(
			int v )
	{
		for( DownloadStatus status : DownloadStatus.values() )
		{
			if( status.getValue() == v )
			{
				return status;
			}
		}
		return StatusInit;
	}
}
