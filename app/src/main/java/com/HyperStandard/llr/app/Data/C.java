package com.HyperStandard.llr.app.Data;

/**
 * @author HyperStandard
 * @since 6/20/2014
 * just a static dummy class to hold strings I guess suck it fight the power
 */
public class C
{
	//Strings to use for shared preferences, preface with PREFS_
	public static final String PREFS_USELOGIN = "userlogin";
	public static final String PREFS_USERNAME = "username";
	public static final String PREFS_PASSWORD = "password";

	public static final String LL_HOME = "http://endoftheinter.net/main.php";
	public static final String LL_LUE = "http://boards.endoftheinter.net/topics/LUE";
	public static final String LL_LOGGEDOUT = "Das Ende des Internets";

	//Font names, preface with FONT_
	//Use a name that defines the purpose, not the file name
	public static final String FONT_LISTVIEW = "fonts/Roboto-Light.ttf";
	public static final String FONT_TITLE = "fonts/RobotoCondensed-Light.ttf";
	public static final String FONT_COMICRELIEF = "fonts/ComicRelief.ttf";
	//Used for the font cache, make sure this equals the number of fonts to be loaded
	//Non critical but should help performance
	public static final int FONT_AMOUNT = 3;

}

