package com.coco.theme.themebox.database.model;


public class UrlAddressItem
{
	
	private String address = "";
	private ApplicationType applicationType;
	private AddressType addressType;
	
	public String getAddress()
	{
		return address;
	}
	
	public void setAddress(
			String address )
	{
		this.address = address;
	}
	
	public ApplicationType getApplicationType()
	{
		return applicationType;
	}
	
	public void setApplicationType(
			ApplicationType applicationType )
	{
		this.applicationType = applicationType;
	}
	
	public AddressType getAddressType()
	{
		return addressType;
	}
	
	public void setAddressType(
			AddressType addressType )
	{
		this.addressType = addressType;
	}
}
