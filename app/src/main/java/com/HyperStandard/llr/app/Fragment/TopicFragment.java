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

import com.HyperStandard.llr.app.Activity.Adapters.PostAdapter;
import com.HyperStandard.llr.app.Data.Cookies;
import com.HyperStandard.llr.app.LoadPage;
import com.HyperStandard.llr.app.Models.TopicPost;
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
 * @since 7/6/2014
 */
public class TopicFragment extends Fragment
{
	private static final String mTag = "LLr -> (TF)";


	@InjectView( R.id.topic_listview )
	ListView listView;

	private Callbacks callbacks;
	private Context context;

	public TopicFragment()
	{

	}

	public static TopicFragment newInstance( String URL )
	{
		TopicFragment fragment = new TopicFragment();
		Bundle args = new Bundle();
		args.putString( "URL", URL );
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
		ButterKnife.inject( this , v );

		//ExecutorService executor = Executors.newSingleThreadExecutor();
		ExecutorService executor = Executors.newFixedThreadPool( 2 );
		Future<Document> request = executor.submit( new LoadPage( "http://boards.endoftheinter.net/showmessages.php?topic=" + getArguments().getString( "URL" ), Cookies.getCookies() ) );
		try
		{
			Document page = request.get( 10, TimeUnit.SECONDS );
			callbacks.sendTitle( page.title() );
			final Elements elements = page.select( "div.message-container" );

			Future<ArrayList<TopicPost>> arrayListFuture = executor.submit( new Callable<ArrayList<TopicPost>>()
			{
				@Override
				public ArrayList<TopicPost> call() throws Exception
				{
					ArrayList<TopicPost> array = new ArrayList<>( elements.size() );
					for ( Element e : elements )
					{
						array.add( new TopicPost( e ) );
					}
					return array;
				}
			} );


			ArrayList<TopicPost> posts = arrayListFuture.get();
			PostAdapter adapter = new PostAdapter( context, R.id.topic_listview, posts );
			listView.setAdapter( adapter );
		}
		catch ( InterruptedException e )
		{
			Log.e( mTag, "Interrupted operation" );
		}
		catch ( TimeoutException e )
		{
			Toast.makeText( context, "Operation timed out", Toast.LENGTH_LONG ).show();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		return v;
	}

	public void setCallbacks( Callbacks callbacks )
	{
		this.callbacks = callbacks;
	}

	public interface Callbacks
	{
		public void sendTitle( String title );
	}

}
