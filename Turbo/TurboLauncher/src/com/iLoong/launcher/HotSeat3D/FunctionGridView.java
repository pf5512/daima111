package com.iLoong.launcher.HotSeat3D;

import java.util.ArrayList;

import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class FunctionGridView extends ViewGroup3D
{
	ArrayList<ViewGroup3D> list =new ArrayList<ViewGroup3D>();
	public FunctionGridView(String name){
		
		super(name);
	}
	
	public ViewGroup3D getFunctionPage(int index){
		if(list!=null &&index<list.size())
			return list.get( index );
		else
			return null;
	}
	
	
	
}
