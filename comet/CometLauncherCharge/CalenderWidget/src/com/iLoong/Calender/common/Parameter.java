package com.iLoong.Calender.common;
//根据基准分辨率的一系列参数（720*1280）工作空间：720*960  以工作空间为外面大的容器
public class Parameter {
	//widget距离上表面高度
	public static final float Calender_Workspace_Top=40f;
	//widget距离下表面的高度
	public static final float Calender_Workspace_Bottom=38f;
	//widget距离左边的宽度
	public static final float Calender_Workspace_Left=43f;
	//widget距离右边的宽度
	public static final float Calender_Workspace_Right=43f;
	//widget年月日模型的高度
	public static final float Calender_Year_Month=87f;
	//widget上金属条的高度
	public static final float Calender_Top_JinShuTiao=83f;
	//widget下金属条的高度
	public static final float Calender_Bottom_JinShuTiao=21f;
	//返回键的宽度
	public static final float Calender_Back=74f;
	//星期玻璃块的高度
	public static final float Calender_Week_Glass_Height=66f;
	//星期玻璃块的宽度
	public static final float Calender_Week_Glass_Width=90f;
	//星期面片的高度
	public static final float Calender_Week_Patch_Height=66f;
	//星期面片的宽度
	public static final float Calender_Week_Patch_Width=90f;
	//日期玻璃块的高度
	public static final float Calender_Day_Glass_Height=118f;
	//日期玻璃块的宽度
	public static final float Calender_Day_Glass_Width=90f;
	//日期面片的高度
	public static final float Calender_Day_Patch_Height=90f;
	//日期面片的宽度
	public static final float Calender_Day_Patch_Width=90f;
	
	//模型的中心点距离屏幕中心点的位置（360.098，475.606）
	public static final float Origin_To_Origin_Width=360.098f;
	public static final float Origin_To_Origin_Height=475.606f;
	//中心点距离Sun的位置（x:-271.498  y:274.544）
//	mod_week,mod_week_glass01 x:-269.401  y:321.73
	public static final float Origin_To_Sun_Width=-269.401f;
	public static final float Origin_To_Sun_Height=321.73f;
	//中心点距离Month的距离(x:-129.621  y:348.221)
//mod_month(year)           x:-130.545  y:395.343
	public static final float Origin_To_Month_Width=-130.545f;
	public static final float Origin_To_Month_Height=395.343f;
//mod_01(year)              x:-79.019   y:398.729
	public static final float Origin_To_Year01_Width=-79.019f;
	public static final float Origin_To_Year01_Height=398.729f;
//mod_02(year)              x:-59.449   y:398.729
	public static final float Origin_To_Year02_Width=-59.449f;
	public static final float Origin_To_Year02_Height=398.729f;
//mod_03(year)              x:-39.879   y:398.729
	public static final float Origin_To_Year03_Width=-39.879f;
	public static final float Origin_To_Year03_Height=398.729f;
//mod_04(year)              x:-20.308   y:398.729
	public static final float Origin_To_Year04_Width=-20.308f;
	public static final float Origin_To_Year04_Height=398.729f;
	//中心点距离back的距离（ x:-252.837  y:351.764）
//mod_back                  x:-255.27   y:399.479
	public static final float Origin_To_Back_Width=-255.27f;
	public static final float Origin_To_Back_Height=399.479f;
	//中心点距离up01的距离 （x:-82.802   y:349.528）
//mod_calendarUp01          x:-82.802   y:401.185
	public static final float Origin_To_Up01_Width=-82.802f;
	public static final float Origin_To_Up01_Height=401.185f;
	//中心点距离up的距离（x:0.384     y:341.208）
//mod_calendarUp            x:-0.384    y:392.864
	public static final float Origin_To_Up_Width=0.384f;
	public static final float Origin_To_Up_Height=392.864f;
	//中心点距离down的距离（x:0.117     y:-364.456）
//mod_calendarDown          x:0.117     y:430.41
	public static final float Origin_To_Down_Width=0.117f;
	public static final float Origin_To_Down_Height=-430.41f;
	
	//除了可变的其余的高度
	public static final float GlassesHeight=Calender_Workspace_Top+Calender_Workspace_Bottom+Calender_Year_Month+Calender_Bottom_JinShuTiao+Calender_Week_Glass_Height;
	
	//定义星期贴片的数组
	public  static final String[] CalenderPitchWeek={"week_7.png","week_1.png","week_2.png","week_3.png","week_4.png","week_5.png","week_6.png"};


//添加页面参数
	
	public static final float Calender_BianKuang_Width=0.526f;
	public static final float Calender_BianKuang_Height=-124.225f;
	
	
	public static final float Calender_Little_BianKuang_Width=0;
	public static final float Calender_Little_BianKuang_Height=85.727f;
	
	
	public static final float BianKuang_To_Top=30f;
	public static final float BianKuang_To_Left=20f;
	public static final float BianKuang_To_Right=20f;
	
	public static final float BianKuang_Width=602f;
	public static final float BianKuang_Height=112f;

	
	public static final float JiaHao_Width=120f;
	public static final float JiaHao_Height=120f;
	
	public static final float BigBackgroundWidth=630f;
	public static final float BigBackgroundHeight=590f;
	
	
	public static final float Each_To_EachBianKuang=30f;
	public static final float BianKuang_To_Bottom=22f;
}
