package com.HyperStandard.llr.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by nonex_000 on 8/7/2014.
 */
public class LoadImage implements Callable<Bitmap>
{
	private final static String mTag = "LLr -> LoadImage";
	private URL mURL;

	public LoadImage( String URLString )
	{
		try
		{
			mURL = new URL( URLString );
		}
		catch ( MalformedURLException e )
		{
			Log.e( mTag, "Malformed URL : " + URLString );
		}
	}

	@Override
	public Bitmap call() throws Exception
	{
		Bitmap bmp = BitmapFactory.decodeStream( mURL.openConnection().getInputStream() );
		return bmp;
	}
}
