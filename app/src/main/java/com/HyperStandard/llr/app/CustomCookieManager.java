package com.HyperStandard.llr.app;

import java.net.CookieManager;

/**
 * @Author HyperStandard
 * @Since 11/19/2014.
 */
public class CustomCookieManager extends CookieManager
{
	public String getCookie( String name )
	{
		return getCookieStore().getCookies().get( getCookieStore().getCookies().indexOf( "username" )).getValue();
	}
}
