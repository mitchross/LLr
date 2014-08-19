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

import com.HyperStandard.llr.app.Activity.Adapters.MainPageAdapter;
import com.HyperStandard.llr.app.Activity.Adapters.TopicAdapter;
import com.HyperStandard.llr.app.Activity.MainActivity;
import com.HyperStandard.llr.app.LoadPage;
import com.HyperStandard.llr.app.Models.BoardLink;
import com.HyperStandard.llr.app.Models.TopicLink;
import com.HyperStandard.llr.app.R;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nonex_000 on 8/14/2014.
 */
public class MainPageFragment extends Fragment
{

	private static final String mTag = "LLr-> (MainPageFragment)";

	@InjectView( R.id.topic_listview )
	ListView listView;
	private Callbacks callbacks;
	private Context context;

	private String URL;


	public MainPageFragment()
	{

	}

	public static MainPageFragment newInstance( int position, String URL )
	{
		MainPageFragment fragment = new MainPageFragment();
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
		//URL = savedInstanceState.getString( "URL" );
		URL = "http://endoftheinter.net/main.php";
		View v = inflater.inflate( R.layout.fragment_container, container, false );

		//Butterknife Injection, we are in a fragment so pass the view
		ButterKnife.inject( this, v );

		return v;

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
		Future<Document> request = executor.submit( new LoadPage( URL, MainActivity.cookies ) );

		try
		{
			Document page = request.get( 5, TimeUnit.SECONDS );
			final Elements elements = page.select( "tr:has(td)" );
			Future<ArrayList<Object>> arrayListFuture = executor.submit( new Callable<ArrayList<Object>>()
			{
				@Override
				public ArrayList<Object> call() throws Exception
				{

					ArrayList<Object> array = new ArrayList<>( elements.size() );
					for ( Element e : elements )
					{
						if ( e.select( "a" ).first().attr( "href" ).lastIndexOf( "=" ) != -1 )
						{
							Object o = (Object) new TopicLink( e );
							array.add( o );
						}
						else
						{//TODO move this logic elsewhere probably
							Object o = (Object) new BoardLink(
									//e.select( "a" ).first().attr( "href" ).lastIndexOf( "=" )
									"LUE", "LEELOO"
							);
							array.add( o );
						}

					}
					return array;
				}
			} );


			ArrayList<Object> topics = arrayListFuture.get();

			MainPageAdapter adapter = new MainPageAdapter( getActivity().getApplicationContext(), R.id.topic_listview, topics);
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

		public void loadBoard( String URL );
	}


}




