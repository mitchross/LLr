package com.HyperStandard.llr.app;

import android.util.Log;

import com.HyperStandard.llr.app.Data.Cookies;
import com.HyperStandard.llr.app.Exceptions.LoggedOutException;
import com.HyperStandard.llr.app.Exceptions.WaitException;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Posts to topics yahooooo boy howdy we are cooking with petrol now m'boy!
 *
 * @author HyperStandard
 * @since 8/26/2014
 */
public class PostMessage
{
	private static final String mTag = "LLr -> (PostMessage)";
	private static final String success = "}{\"success\":\"Message posted.\"}";

	/**
	 * @param message   the message to send (includes the signature
	 * @param h         magic number, parse this from select("input[name=h]").attr("value")
	 * @param topicId   the topic ID number
	 * @param autoRetry whether to auto post when ready
	 * @return an integer corresponding to -2 for success, -1 for failure, 0+ for number of seconds before you can repost
	 */
	public ImmutablePair<Response, Integer> post( String message, String h, String topicId, boolean autoRetry ) throws LoggedOutException, WaitException
	{
		int i = -1;
		//TODO figure out how this performs when being called from multiple places maybe do a factory?
		ExecutorService executors = Executors.newFixedThreadPool( 2 );
		AsyncPost asyncPost = new AsyncPost( message, h, topicId );
		Future<String> responseFuture = executors.submit( asyncPost );
		try
		{
			String response = responseFuture.get();

			Log.e( mTag, response );

			if ( response.equals( success ) )
			{
				return new ImmutablePair<>( Response.SUCCEEDED, 0 );
			}

		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
			return new ImmutablePair<>( Response.TIMEOUT, -1 );
		}
		catch ( ExecutionException e )
		{
			e.printStackTrace();
			return new ImmutablePair<>( Response.FAILED, -1 );
		}
		return new ImmutablePair<>( Response.FAILED, -2 );
	}


	/**
	 * inner class for convenience, POSTs the data and returns the response
	 */
	private class AsyncPost implements Callable<String>
	{
		String message;
		String h;
		String topicId;

		public AsyncPost( String message, String h, String topicId )
		{
			this.message = message;
			this.h = h;
			this.topicId = topicId;
		}

		@Override
		public String call() throws Exception
		{
			//Build the form used to successfully submit an async post
			final RequestBody formBody = new FormEncodingBuilder()
					.add( "message", message )
					.add( "h", h )
					.add( "topic", topicId )
					.build();

			//put the form into the request (why is this library so verbose) who cares it's fast
			final Request request = new Request.Builder()
					.url( "http://boards.endoftheinter.net/async-post.php" )
					.post( formBody )
					.build();

			return Cache.Web.Client().newCall( request ).execute().body().string();
		}
	}
}
