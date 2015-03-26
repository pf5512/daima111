package com.iLoong.launcher.UI3DEngine;


public interface TextFieldFilter
{
	
	/**
	 * @param textField
	 * @param key
	 * @return whether to accept the character
	 */
	public boolean acceptChar(
			TextField3D textField ,
			char key );
	
	static public class DigitsOnlyFilter implements TextFieldFilter
	{
		
		@Override
		public boolean acceptChar(
				TextField3D textField ,
				char key )
		{
			return Character.isDigit( key );
		}
	}
}
