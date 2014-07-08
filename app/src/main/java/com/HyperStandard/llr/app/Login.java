package com.HyperStandard.llr.app;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Background task to log in to ETI
 *
 * @author HyperStandard
 * @version 0.1
 * @since 6/12/2014
 */
public class Login implements Callable<Connection.Response>
{
	private final static String mTag = "Login debug";
	private String username;
	private String password;

	public Login( String username, String password )
	{
		this.username = username;
		this.password = password;
	}

	public Connection.Response call() throws Exception
	{
		String url = "https://iphone.endoftheinter.net";


		try
		{
			/**
			 * Try to connect to iphone login site and get response
			 */
			Connection.Response res = Jsoup
					.connect( url )
					.data( "username", username, "password", password )
					.method( Connection.Method.POST )
					.execute();
			//Get the login cookies so the pages go through
			Map<String, String> loginCookies = res.cookies();
			Document ErrorPage;
			try
			{
				Document doc = Jsoup.connect( "http://endoftheinter.net/main.php" )
						.cookies( loginCookies )
						.get();
				String responseText = doc.select( "div.poll" ).text();
				return res;
			}
			//Here if there's an exception, just try to re login and parse an error message from that
			//TODO find a less dumb way to do this?
			catch ( Exception e )
			{
				url = "https://endoftheinter.net/";
				Connection.Response errorPage = Jsoup
						.connect( url )
						.data( "username", username, "password", password )
						.method( Connection.Method.POST )
						.execute();
				Document errorDoc = Jsoup.parse( errorPage.body() );
				String error = errorDoc.select( "h2" ).text();
				Log.e( mTag, error );
			}

		}
		catch ( Exception e )
		{
			e.printStackTrace();
			Log.e( mTag, "fatal error" );

		}
		//return new Connection.Response();
		return null;
	}


}
