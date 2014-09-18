package com.HyperStandard.llr.app.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.HyperStandard.llr.app.Activity.Adapters.TopicAdapter;
import com.HyperStandard.llr.app.Activity.MainActivity;
import com.HyperStandard.llr.app.Data.Cookies;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author HyperStandard
 * @since 6/30/2014
 */
public class TopicListFragment extends Fragment
{
	private static final String mTag = "LLr-> (TVF)";

	@InjectView(R.id.topic_listview)
	ListView listView;
	private Callbacks callbacks;
	private Context context;
	private String title;



	public TopicListFragment()
	{

	}

	public static TopicListFragment newInstance( int position, String URL )
	{
		TopicListFragment fragment = new TopicListFragment();
		Bundle args = new Bundle();
		args.putString( "URL", URL );
		args.putInt( "position", position );
		fragment.setArguments( args );
		return fragment;
	}

	public void setUp( Context context )
	{
		this.context = context;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.fragment_container, container, false );

		//Butterknife Injection, we are in a fragment so pass the view
		ButterKnife.inject( this, v );

		return v;

	}

	@Override
	public void onHiddenChanged( boolean hidden )
	{
		super.onHiddenChanged( hidden );
		if ( !hidden )
		{
			callbacks.sendTitle( title );
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getTopics();
	}

	protected void getTopics()
	{
		ExecutorService executor = Executors.newFixedThreadPool( 2 );

		String URL = getArguments().getString( "URL" );
		Future<Document> request = executor.submit( new LoadPage( URL, Cookies.getCookies() ) );

		//Future<Document> request = executor.submit( new LoadPage( "http://boards.endoftheinter.net/topics/LUE", MainActivity.cookies ) );

		try
		{
			Document page = request.get( 5, TimeUnit.SECONDS );
			title = page.title();
			callbacks.sendTitle( title );
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
			TopicAdapter adapter = new TopicAdapter( getActivity().getApplicationContext(), R.id.topic_listview, topics, callbacks );
			listView.setAdapter( adapter );
		}
		catch ( InterruptedException e )
		{
			Log.e( "list fragment", "Interrupted operation" );
		}
		catch ( TimeoutException e )
		{
			Toast.makeText( getActivity().getApplicationContext(), "Operation timed out", Toast.LENGTH_SHORT ).show();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	public void setCallbacks( Callbacks callbacks )
	{
		this.callbacks = callbacks;
	}

	public interface Callbacks
	{
		public void sendTitle( String title );

		public void loadTopic( String URL );
	}


}


