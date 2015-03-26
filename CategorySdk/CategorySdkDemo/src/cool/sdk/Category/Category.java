package cool.sdk.Category;


import android.content.Context;
import cool.sdk.download.CoolDLMgr;


public class Category
{
	
	public static final int h12 = 4;
	public static final String h13 = "category";
	
	public static CoolDLMgr CoolDLMgr(
			Context context ,
			String moudleName )
	{
		return CoolDLMgr.getInstance( context , moudleName + h12 + "C" , h12 , h13 );
	}
}
