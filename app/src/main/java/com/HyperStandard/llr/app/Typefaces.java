package com.HyperStandard.llr.app;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Caches typefaces for application wide usage
 * Created by nonex_000 on 6/30/2014.
 */
public class Typefaces
{
	private static HashMap<String, Typeface> cache = new HashMap<>();

	public static Typeface getTypface( Context context, String name )
	{
		if ( cache.containsKey( name ) )
		{
			return cache.get( name );
		}
		else
		{
			Typeface t = Typeface.createFromAsset( context.getAssets(), name );
			cache.put( name, t );
			return t;
		}
	}
}
