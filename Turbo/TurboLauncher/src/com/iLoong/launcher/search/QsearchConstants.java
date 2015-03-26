package com.iLoong.launcher.search;


import com.iLoong.launcher.UI3DEngine.UtilsBase;


public class QsearchConstants
{
	
	public static final float S_SCALE = UtilsBase.getScreenWidth() / 720f;
	public static final float W_QUICK_SEARCH = UtilsBase.getScreenWidth();
	public static final float H_QUICK_SEARCH = UtilsBase.getScreenHeight() - UtilsBase.getStatusBarHeight();
	public static final float W_GUESS_GROUP = UtilsBase.getScreenWidth();
	public static final float H_GUESS_GROUP = S_SCALE * 246;
	public static final float W_GUESS_GROUP_TITLE = UtilsBase.getScreenWidth();
	public static final float H_GUESS_GROUP_TITLE = S_SCALE * 56;
	public static final float W_GUESS_PAGE = UtilsBase.getScreenWidth();
	public static final float H_GUESS_PAGE = S_SCALE * 190;
	public static final float W_GUESS_GROUP_DIVIDER = UtilsBase.getScreenWidth();
	public static final float H_GUESS_GROUP_DIVIDER = S_SCALE * 1;
	public static final float W_ALL_APP_GROUP = UtilsBase.getScreenWidth();
	public static final float H_ALL_APP_GROUP = UtilsBase.getScreenHeight() - H_GUESS_GROUP;
	public static final float W_ALL_APP_LIST = UtilsBase.getScreenWidth();
	public static final float H_ALL_APP_LIST = UtilsBase.getScreenHeight() - H_GUESS_GROUP;
	public static final float W_ALL_APP_LIST_ITEM_TITLE_GROUP = UtilsBase.getScreenWidth();
	public static final float H_ALL_APP_LIST_ITEM_TITLE_GROUP = S_SCALE * 36;
	public static final float W_ALL_APP_LIST_ITEM = UtilsBase.getScreenWidth();
	public static final float W_SEARCH_EDIT_GROUP = UtilsBase.getScreenWidth();
	public static final float H_SEARCH_EDIT_GROUP = S_SCALE * 171;
	public static final float W_SEATCH_EDIT_FRAME = S_SCALE * 672;
	public static final float H_SEARCH_EDIT_FRAME = S_SCALE * 84;
	public static final float X_SEARCH_EDIT_FRAME = S_SCALE * 24;
	public static final float Y_SEARCH_EDIT_FRAME = S_SCALE * 47;
	public static final float W_SEARCH_EDIT_FILL = S_SCALE * 666;
	public static final float H_SEARCH_EDIT_FILL = S_SCALE * 78;
	public static final float X_SEARCH_EDIT_FILL = S_SCALE * 27;
	public static final float Y_SEARCH_EDIT_FILL = S_SCALE * 50;
	public static final float W_SEARCH_BTN = S_SCALE * 55;
	public static final float H_SEARCH_BTN = S_SCALE * 55;
	public static final float X_SEARCH_BTN = S_SCALE * 44;
	public static final float Y_SEARCH_BTN = S_SCALE * 62;
	public static final float W_SEARCH_HINT = S_SCALE * 207;
	public static final float H_SEARCH_HINT = S_SCALE * 37;
	public static final float X_SEARCH_HINT = S_SCALE * 115;
	public static final float Y_SEARCH_HINT = S_SCALE * 71;
	public static final float W_SEARCH_TEXT_FIELD = S_SCALE * 666 - ( X_SEARCH_HINT - X_SEARCH_EDIT_FILL );
	public static final float H_SEARCH_TEXT_FIELD = S_SCALE * 78;
	public static final float X_SEARCH_TEXT_FIELD = X_SEARCH_HINT;
	public static final float Y_SEARCH_TEXT_FIELD = S_SCALE * 50;
	public static final float W_LETTERS_GROUP = S_SCALE * 40;
	public static final float H_LETTERS_GROUP = H_QUICK_SEARCH - H_GUESS_GROUP - 140 * S_SCALE;
	public static final float X_LETTERS_GROUP = UtilsBase.getScreenWidth() - W_LETTERS_GROUP;
	public static final float Y_LETTERS_GROUP = 140 * S_SCALE;
	public static final float W_LETTER = S_SCALE * 40;
	public static final float H_LETTER = ( UtilsBase.getScreenHeight() - H_SEARCH_EDIT_GROUP - H_GUESS_GROUP ) / 27;
	public static final float W_SEARCH_RESULT_GROUP = UtilsBase.getScreenWidth();
	public static final float H_SEARCH_RESULT_GROUP = UtilsBase.getScreenHeight();
	public static final float W_SEARCH_RESULT_TOP_FILL = UtilsBase.getScreenWidth();
	public static final float H_SEARCH_RESULT_TOP_FILL = S_SCALE * 127 + UtilsBase.getStatusBarHeight();
	public static final float W_SEARCH_RESULT_LIST = UtilsBase.getScreenWidth();
	public static final float H_SEARCH_RESULT_LIST = UtilsBase.getScreenHeight() - H_SEARCH_RESULT_TOP_FILL;
	public static final float W_QUICK_SEARCH_FILL_TOP = UtilsBase.getScreenWidth();
	public static final float H_QUICK_SEARCH_FILL_TOP = UtilsBase.getStatusBarHeight();
	public static final float W_NO_SEARCH_RESULT_TIP = UtilsBase.getScreenWidth();
	public static final float H_NO_SEARCH_RESULT_TIP = S_SCALE * 40;
	public static final float X_NO_SEARCH_RESULT_TIP = 0;
	public static final float Y_NO_SEARCH_RESULT_TIP = UtilsBase.getScreenHeight() - S_SCALE * 400;
	public static final float W_LETTER_POP = S_SCALE * 100;
	public static final float H_LETTER_POP = S_SCALE * 100;
	public static final float X_LETTER_POP = UtilsBase.getScreenWidth() - W_LETTER_POP - W_LETTERS_GROUP + S_SCALE * 5;
	public static final float Y_LETTER_POP = H_SEARCH_EDIT_GROUP - S_SCALE * 35;
	public static final float S_GUESS_TITLE_TEXT = S_SCALE * 32;
	public static final float S_APP_LIST_ITEM_TITLE_TEXT = S_SCALE * 28;
	public static final float S_LETTER = S_SCALE * 25;
	public static final float S_INPUT_TEXT = S_SCALE * 40;
	public static final float S_NO_SEARCH_RESULT_TIP = S_SCALE * 35;
	public static final float S_LETTER_POP_TEXT = S_SCALE * 50;
}
