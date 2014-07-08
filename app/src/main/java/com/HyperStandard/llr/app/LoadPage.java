package com.HyperStandard.llr.app;

import android.util.Log;

import com.HyperStandard.llr.app.Data.Cookies;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Basic wrapper to load a page in the background
 *
 * @author HyperStandard
 * @since 6/15/2014
 */
public class LoadPage implements Callable<Document>
{
	private String URL;

	//TODO fix the mess that is cookies, maybe send them at login time, skip that whole array garbage
	//
	//I'm not sure about map constructrs since you can't instantiate a Map but Jsoup uses it? ? ?
	private Map<String, String> cookies;

	/**
	 * Send the data needed to load a webpage
	 *
	 * @param URL     The url (currently the whole thing + tags) may change later
	 * @param cookies Map containing cookies needed to successfully query a page
	 */
	public LoadPage( String URL, Map<String, String> cookies )
	{
		this.URL = URL;
	}

	public Document call()
	{
		try
		{
			Document doc = Jsoup.connect( URL )
					.cookies( Cookies.getCookies() )
					.get();
			return doc;
		}
		catch ( IOException e )
		{
			Log.e( "error", "error" );
		}
		return null;
	}
}
