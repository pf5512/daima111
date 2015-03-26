package com.iLoong.launcher.search;


import com.thirdParty.pinyin4j.net.sourceforge.PinyinHelper;
import com.thirdParty.pinyin4j.net.sourceforge.format.HanyuPinyinCaseType;
import com.thirdParty.pinyin4j.net.sourceforge.format.HanyuPinyinOutputFormat;
import com.thirdParty.pinyin4j.net.sourceforge.format.HanyuPinyinToneType;
import com.thirdParty.pinyin4j.net.sourceforge.format.HanyuPinyinVCharType;
import com.thirdParty.pinyin4j.net.sourceforge.format.exception.BadHanyuPinyinOutputFormatCombination;


public class PinYin
{
	
	public static String getPingYin(
			String src )
	{
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType( HanyuPinyinCaseType.UPPERCASE );
		t3.setToneType( HanyuPinyinToneType.WITHOUT_TONE );
		t3.setVCharType( HanyuPinyinVCharType.WITH_V );
		String t4 = "";
		int t0 = t1.length;
		try
		{
			for( int i = 0 ; i < t0 ; i++ )
			{
				if( java.lang.Character.toString( t1[i] ).matches( "[\\u4E00-\\u9FA5]+" ) )
				{
					t2 = PinyinHelper.toHanyuPinyinStringArray( t1[i] , t3 );
					t4 += t2[0];
				}
				else
				{
					t4 += java.lang.Character.toString( t1[i] );
				}
			}
			return t4;
		}
		catch( BadHanyuPinyinOutputFormatCombination e1 )
		{
			e1.printStackTrace();
		}
		return t4;
	}
	
	public static String getPinYinHeadChar(
			String str )
	{
		String convert = "";
		for( int j = 0 ; j < str.length() ; j++ )
		{
			char word = str.charAt( j );
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray( word );
			if( pinyinArray != null )
			{
				convert += pinyinArray[0].charAt( 0 );
			}
			else
			{
				convert += word;
			}
		}
		return convert;
	}
	
	public static String getCnASCII(
			String cnStr )
	{
		StringBuffer strBuf = new StringBuffer();
		byte[] bGBK = cnStr.getBytes();
		for( int i = 0 ; i < bGBK.length ; i++ )
		{
			strBuf.append( Integer.toHexString( bGBK[i] & 0xff ) );
		}
		return strBuf.toString();
	}
}
