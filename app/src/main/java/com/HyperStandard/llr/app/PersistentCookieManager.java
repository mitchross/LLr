package com.HyperStandard.llr.app;

import android.content.Context;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HyperStandard
 * @since 10/9/2014
 */
public class PersistentCookieManager extends CookieHandler
{
	private static Map<String, Map<String, List<String>>> cachedURIs;

	private static Context mContext;

	@Override
	public Map<String, List<String>> get( URI uri, Map<String, List<String>> stringListMap ) throws IOException
	{
		if ( cachedURIs != null )
		{
			if ( cachedURIs.containsKey( uri.toString() ) )
			{
				return cachedURIs.get( uri.toString() );
			}
		}
		throw new IOException();
	}

	@Override
	public void put( URI uri, Map<String, List<String>> stringListMap ) throws IOException
	{
		if ( cachedURIs == null )
		{
			cachedURIs = new HashMap<>();
		}
		cachedURIs.put( uri.toString(), stringListMap );
		//List<String> cookie = Collections.singletonList()
	}

	public void invalidateCache()
	{
		cachedURIs = null;
	}

	public void initialize( Context mContext )
	{
		this.mContext = mContext;
	}
}
