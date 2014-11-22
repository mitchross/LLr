package com.HyperStandard.llr.app;

import com.HyperStandard.llr.app.Data.Cookies;
import com.HyperStandard.llr.app.Exceptions.LoggedOutException;
import com.HyperStandard.llr.app.Exceptions.WaitException;

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
	public int post( String message, String h, String topicId, boolean autoRetry ) throws LoggedOutException, WaitException
	{
		int i = -1;
		//TODO figure out how this performs when being called from multiple places maybe do a factory?
		ExecutorService executors = Executors.newFixedThreadPool( 2 );
		AsyncPost asyncPost = new AsyncPost( message, h, topicId );
		Future<Connection.Response> responseFuture = executors.submit( asyncPost );
		try
		{
			Connection.Response response = responseFuture.get();
			String res = response.body();
			if ( res.equals( success ) )
			{
				return -2;
			}

		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
			return -1;
		}
		catch ( ExecutionException e )
		{
			e.printStackTrace();
			return -1;
		}
		return i;
	}


	/**
	 * inner class for convenience, POSTs the data and returns the response
	 */
	private class AsyncPost implements Callable<Connection.Response>
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

		//todo convert to okhttp
		@Override
		public Connection.Response call() throws Exception
		{
			return Jsoup.connect( "http://boards.endoftheinter.net/async-post.php" )
					.data( "message", message, "h", h, "topic", topicId )
					.cookies( Cookies.getCookies() )
					.method( Connection.Method.POST )
					.execute();
		}
	}
}
