package com.HyperStandard.llr.app.Page;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.HyperStandard.llr.app.Adapters.PostAdapter;
import com.HyperStandard.llr.app.Converters.TopicArray;
import com.HyperStandard.llr.app.LoadPage;
import com.HyperStandard.llr.app.Models.TopicPost;
import com.HyperStandard.llr.app.R;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author HyperStandard
 * @Since 11/20/2014.
 */
public class Topic
{
	public Topic( String topicId, Callbacks callbacks, Context context )
	{
		ListView listView = new ListView( context );

		ExecutorService executor = Executors.newSingleThreadExecutor();

		Future<Document> request;

		if ( topicId.contains( "http" ) )
		{
			request = executor.submit( new LoadPage( topicId ) );

		}
		else
		{
			 request = executor.submit( new LoadPage( "http://boards.endoftheinter.net/showmessages.php?topic=" + topicId ) );
		}
		try
		{
			Document page = request.get( 10, TimeUnit.SECONDS );
			callbacks.setTitle( page.title() );
			String hTag = page.select( "input[name=h]" ).attr( "value" );
			//Log.e( mTag, Integer.toString( topicID ) );
			//Log.e( mTag, hTag );
			callbacks.registerTopic( hTag, topicId );
			final Elements elements = page.select( "div.message-container" );

			Future<ArrayList<TopicPost>> arrayListFuture = executor.submit( new Callable<ArrayList<TopicPost>>()
			{
				@Override
				public ArrayList<TopicPost> call() throws Exception
				{
					return TopicArray.newArrayFromShowMessages( elements );
				}
			} );


			ArrayList<TopicPost> posts = arrayListFuture.get();
			PostAdapter adapter = new PostAdapter( context, R.id.topic_listview, posts );
			listView.setAdapter( adapter );
		}
		catch ( InterruptedException e )
		{
			//Log.e( mTag, "Interrupted operation" );
		}
		catch ( TimeoutException e )
		{
			Toast.makeText( context, "Operation timed out", Toast.LENGTH_LONG ).show();
		}
		catch ( ExecutionException e )
		{
			e.printStackTrace();
		}
		callbacks.setTopicView( listView, "http://boards.endoftheinter.net/showmessages.php?topic=" + topicId );
	}

	public interface Callbacks
	{
		public void setTopicView( View view, String url );

		public void setTitle( String title );

		/**
		 * Use this to register the current active topic for posting
		 *
		 * @param hTag    the per page validation code needed to submit posts
		 * @param topicId the topicId number in String format to reduce conversions
		 */
		public void registerTopic( String hTag, String topicId );
	}
}
