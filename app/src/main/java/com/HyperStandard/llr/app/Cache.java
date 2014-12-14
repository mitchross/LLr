package com.HyperStandard.llr.app;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.net.CookiePolicy;

/**
 * @author HyperStandard
 * @since 11/2/2014
 */
public enum Cache
{
	Web;

	/**
	 * Shared client between parts of the program
	 */
	private OkHttpClient mClient;

	private Context mContext;

	private final String mTag = "LLr -> (Cache)";

	Cache()
	{
		Log.e( mTag, "in Cache()" );
		mClient = new OkHttpClient();

		/**
		 * Set the cookie handling
		 */
		CustomCookieManager cookieManager = new CustomCookieManager();
		cookieManager.setCookiePolicy( CookiePolicy.ACCEPT_ALL );
		mClient.setCookieHandler( cookieManager );
	}

	public void Setup(Context context)
	{
		long cacheSize = 10 * 1024 * 1024; // 10 MiB

		mContext = context;

		try
		{
			com.squareup.okhttp.Cache cache = new com.squareup.okhttp.Cache( mContext.getCacheDir(), cacheSize );
			mClient.setCache( cache );
		}
		catch ( IOException e )
		{
			Log.e( mTag, "Cache IO failure, not using cache" );
		}
	}

	public OkHttpClient Client()
	{
		return mClient;
	}

}
