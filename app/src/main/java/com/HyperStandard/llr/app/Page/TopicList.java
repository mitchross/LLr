package com.HyperStandard.llr.app.Page;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.HyperStandard.llr.app.Activity.Adapters.TopicAdapter;
import com.HyperStandard.llr.app.LoadPage;
import com.HyperStandard.llr.app.Models.TopicLink;
import com.HyperStandard.llr.app.R;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author HyperStandard
 * @Since 11/20/2014.
 */
public class TopicList
{
	Callbacks callbacks;

	public TopicList( String url, Callbacks callbacks, Context context )
	{
		this.callbacks = callbacks;

		ExecutorService executor = Executors.newSingleThreadExecutor();

		Future<Document> request = executor.submit( new LoadPage( url ) );

		ListView listView = new ListView( context );

		try
		{
			Document page = request.get( 5, TimeUnit.SECONDS );
			String title = page.title();
			callbacks.setTitle( title );
			final Elements elements = page.select( "tr:has(td)" );
			Future<ArrayList<TopicLink>> arrayListFuture = executor.submit( new Callable<ArrayList<TopicLink>>()
			{
				@Override
				public ArrayList<TopicLink> call() throws Exception
				{

					ArrayList<TopicLink> array = new ArrayList<>( elements.size() );
					for ( Element e : elements )
					{
						array.add( new TopicLink( e ) );
					}
					return array;
				}
			} );

			ArrayList<TopicLink> topics = arrayListFuture.get();
			TopicAdapter adapter = new TopicAdapter( context.getApplicationContext(), R.id.topic_listview, topics, callbacks );
			listView.setAdapter( adapter );

			//todo should these be put into a single method? ? ?
			callbacks.setView( listView );
			callbacks.setTitle( title );
		}
		catch ( InterruptedException e )
		{
			Log.e( "list fragment", "Interrupted operation" );
		}
		catch ( TimeoutException e )
		{
			Toast.makeText( context, "Operation timed out", Toast.LENGTH_SHORT ).show();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	public interface Callbacks
	{
		/**
		 * Sets the view for the page to be loaded
		 * @param view
		 */
		public void setView( View view );

		/**
		 * Tells the app the page title which can then be displayed
		 * @param title
		 */
		public void setTitle( String title );

		/**
		 * Tells the main activity to load a page, which is clicked in the adapter
		 * @param url
		 */
		public void loadTopic(String url);
	}
}
