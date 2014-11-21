package com.HyperStandard.llr.app;

import com.squareup.okhttp.OkHttpClient;

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
	private OkHttpClient client;

	Cache()
	{

		client = new OkHttpClient();

		/**
		 * Set the cookie handling
		 */
		CustomCookieManager cookieManager = new CustomCookieManager();
		cookieManager.setCookiePolicy( CookiePolicy.ACCEPT_ALL);
		client.setCookieHandler(cookieManager);
	}

	public OkHttpClient Client()
	{
		return client;
	}

}
