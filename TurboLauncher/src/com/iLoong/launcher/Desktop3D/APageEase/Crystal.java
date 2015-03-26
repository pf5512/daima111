package com.iLoong.launcher.Desktop3D.APageEase;


import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.APageEase.CrystalCore.Face;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class Crystal
{
	
	public static void updateEffect(
			ViewGroup3D cur_view ,
			ViewGroup3D next_view ,
			float degree ,
			float yScale ,
			float this_width ,
			boolean rightToLeft )
	{
		CrystalCore crystal = NPageBase.crystalGroup;
		if( crystal == null )
			return;
		//			cur_view.setBackgroud(new NinePatch(new Texture(Gdx.files.internal("launcher/crystal/test_1__.png"))));
		crystal.setFace( Face.Front , cur_view );
		crystal.setFace( Face.Verso , next_view );
		float rotate = degree * 180;
		crystal.setRotate( rotate );
	}
}
