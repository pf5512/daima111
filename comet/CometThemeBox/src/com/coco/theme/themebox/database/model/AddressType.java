package com.coco.theme.themebox.database.model;


public enum AddressType
{
	AddressThumb( 0 ) , AddressPreview( 1 ) , AddressApp( 2 );
	
	private int value;
	
	private AddressType(
			int v )
	{
		this.value = v;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public static AddressType fromValue(
			int v )
	{
		for( AddressType addrType : AddressType.values() )
		{
			if( addrType.getValue() == v )
			{
				return addrType;
			}
		}
		return AddressPreview;
	}
}
