package com.iLoong.launcher.smartLaytout;


public class SLLayout
{
	
	int screen;
	int cellX;
	int cellY;
	
	public SLLayout(int s,int cx,int cy){
		this.screen=s;
		this.cellX=cx;
		this.cellY=cy;
	}

	
	public int getScreen()
	{
		return screen;
	}

	
	public void setScreen(
			int screen )
	{
		this.screen = screen;
	}

	
	public int getCellX()
	{
		return cellX;
	}

	
	public void setCellX(
			int cellX )
	{
		this.cellX = cellX;
	}

	
	public int getCellY()
	{
		return cellY;
	}

	
	public void setCellY(
			int cellY )
	{
		this.cellY = cellY;
	}
	
}
