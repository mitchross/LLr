package com.HyperStandard.llr.app.Data;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nonex_000 on 7/5/2014.
 */
public class Cookies
{
	private static Map<String, String> cookies;

	public static Map<String, String> getCookies()
	{
		if ( cookies != null )
		{
			return cookies;
		}
		else
		{
			Log.e( "LLr -> Cookies", "cookies was null" );
			return null;
		}
	}

	public static void setCookies( Map<String, String> cookies )
	{
		Cookies.cookies = new HashMap<String, String>( cookies );
	}
}
