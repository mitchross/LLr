package com.HyperStandard.llr.app;

import android.util.Log;

import com.HyperStandard.llr.app.Exceptions.LoggedOutException;
import com.squareup.okhttp.Request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
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

	/**
	 * Send the data needed to load a webpage
	 *
	 * @param URL The url (currently the whole thing + tags) may change later
	 */
	public LoadPage( String URL )
	{
		this.URL = URL;
	}
	
	public Document call() throws LoggedOutException
	{
		try
		{

			Request request = new Request.Builder()
					.url( URL )
					.build();


			//Call the webpage w OKHttp, then turn the Body into a String and parse it into a JSoup document
			Document doc = Jsoup.parse( Cache.Web.Client().newCall( request ).execute().body().string());

			if ( doc.title().equals( "Das Ende des Internets" ) )
			{
				Log.e( "error", "logged out" );
				throw new LoggedOutException();
			}
			return doc;
		}//TODO fix these errors
		catch ( IOException e )
		{
			Log.e( "error", "error" );
		}
		return null;
	}

}
