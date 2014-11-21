package com.HyperStandard.llr.app.Page;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.HyperStandard.llr.app.Activity.Adapters.PostAdapter;
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
	public Topic( int topicId, Callbacks callbacks, Context context )
	{
		ListView listView = new ListView( context );

		ExecutorService executor = Executors.newSingleThreadExecutor();

		Future<Document> request = executor.submit( new LoadPage( "http://boards.endoftheinter.net/showmessages.php?topic=" + topicId ) );
		try
		{
			Document page = request.get( 10, TimeUnit.SECONDS );
			callbacks.setTitle( page.title() );
			//topicID = Integer.parseInt( getArguments().getString( "URL" ) );
			//String hTag = page.select( "input[name=h]" ).attr( "value" );
			//Log.e( mTag, Integer.toString( topicID ) );
			//Log.e( mTag, hTag );
			//callbacks.registerTopic( "", hTag, topicID );
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
		callbacks.setView(listView);
	}

	public interface Callbacks
	{
		public void setView( View view );

		public void setTitle( String title );
	}
}
