package com.HyperStandard.llr.app.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.HyperStandard.llr.app.BookmarkLink;
import com.HyperStandard.llr.app.CustomTypefaceSpan;
import com.HyperStandard.llr.app.Data.Cookies;
import com.HyperStandard.llr.app.Fragment.MainPageFragment;
import com.HyperStandard.llr.app.Fragment.PollFragment;
import com.HyperStandard.llr.app.Fragment.TopicFragment;
import com.HyperStandard.llr.app.Fragment.TopicListFragment;
import com.HyperStandard.llr.app.LoadPage;
import com.HyperStandard.llr.app.Navigation.NavigationAdapter;
import com.HyperStandard.llr.app.Navigation.NavigationDrawerFragment;
import com.HyperStandard.llr.app.Navigation.PostDrawerFragment;
import com.HyperStandard.llr.app.PostMessage;
import com.HyperStandard.llr.app.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.tuple.Triple;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class MainActivity extends BaseActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks,
		NavigationAdapter.NavigationDrawerCallback,
		TopicListFragment.Callbacks,
		TopicFragment.Callbacks,
		PollFragment.Callbacks
{
	/**
	 * Used for logging
	 */
	private static String mTag = "LLr -> (Main)";

	private static int userId;

	/**
	 * cached for performance?
	 */
	private static FragmentManager manager;

	/**
	 * Hold the last fragment tag
	 */
	private static String lastFragTag;
	ListView mListView;
	@Optional
	@InjectView( R.id.leftNavigationDrawer )
	ListView listView;
	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	/**
	 * Fragment storing the poll
	 */
	private PollFragment mPollFragment;
	private PostDrawerFragment mPostDrawerFragment;
	private int topicID;
	private String h;

	/**
	 * Used to store the TAG for the last used fragment
	 */

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle = "";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		//Biolerplate stuff set the view, call super()
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		//Get the uer ID number
		userId = getIntent().getIntExtra( "userId", -1 );

		//cache the fragment manager
		manager = getFragmentManager();

		//Who knows
		ButterKnife.inject( this );

		//Get references to the drawer fragments that do things
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById( R.id.left_drawer );
		mPostDrawerFragment = (PostDrawerFragment) getFragmentManager().findFragmentById( R.id.navigation_tabs );

		//Set the title to the applicaiton name TODO maybe change this to something else
		mTitle = getTitle();

		/** Because fragments get called with 0 argument constructors, you can't pass values to them
		 *  except by adding a utility method to pass data to
		 */
		mNavigationDrawerFragment.setUp(
				R.id.left_drawer,
				(DrawerLayout) findViewById( R.id.drawer_layout ),
				userId,
				this );

		/**
		 * Load the main page to get data from it i.e. bookmarks and other stuff later
		 */
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Document> loader = executor.submit( new LoadPage( getString( R.string.ll_main ) ) );
		try
		{
			Document main = loader.get( 5, TimeUnit.SECONDS );
				/*Element poll = main.select( "div.poll" ).first();
				mPollFragment = new PollFragment();
				mPollFragment.setCallbacks( this );
				mPollFragment.setUp( main.select( "div.poll" ) );*///TODO implement poll after frag handling/nav problems fixed
			Elements elements = main.select( "#bookmarks > span" );

			/**
			 * Get the bookmarks, to populate the Navigation drawer with links
			 */
			for ( Element e : elements )
			{
				populateDrawer( new BookmarkLink(
						e.select( "span > a" ).first().ownText(),
						e.select( "span > a" ).attr( "abs:href" ),
						"TOPIC_LIST"
				) );

				Log.v( mTag, "loading bookmark: \"" + e.select( "span > a" ).first().ownText() + "\" @ " + e.select( "span > a" ).attr( "abs:href" ) );
			}

		}
		catch ( InterruptedException e )
		{
			Log.e( mTag, "Interrupted operation" );
		}
		catch ( TimeoutException e )
		{
			Toast.makeText( this, "Operation timed out", Toast.LENGTH_SHORT ).show();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onNavigationDrawerItemSelected( int position, String URL )
	{
		if ( manager == null )
		{
			manager = getFragmentManager();
		}

		/**
		 * make a new topic fragment TODO null check later
		 */
		TopicFragment fragment = TopicFragment.newInstance( URL );

		Fragment lastFragment = manager.findFragmentByTag( lastFragTag );

		/*manager.beginTransaction()
				if (lastFragment != null)
				.hide(  )*/

	}

	//TODO what the hell is this I don't even know
	public void onSectionAttached( int number )
	{
		switch ( number )
		{
			case 1:
				mTitle = getString( R.string.title_section1 );
				break;
			case 2:
				mTitle = getString( R.string.title_section2 );
				break;
			case 3:
				mTitle = getString( R.string.title_section3 );
				break;
			//mNavigationDrawerFragment.get
			case 4:
				//loadPageURL("http://boards.endoftheinter.net/topics/Android");
				break;
			case 5:
				//loadPageURL("http://boards.endoftheinter.net/topics/LUE");
				break;
		}
	}

	public void restoreActionBar()
	{
		ActionBar actionBar = getActionBar();
		if ( actionBar != null )
		{
			actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );
			actionBar.setDisplayShowTitleEnabled( true );
			SpannableStringBuilder t = new SpannableStringBuilder( mTitle );
			t.setSpan( new CustomTypefaceSpan( this, getString( R.string.font_title ) ), 0, t.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE );
			actionBar.setTitle( t );
		}
	}


	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		if ( !mNavigationDrawerFragment.isDrawerOpen() )
		{
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate( R.menu.main, menu );
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if ( item.getItemId() == R.id.action_example )
		{
			//TODO thing
		}
		if ( item.getItemId() == R.id.action_settings )
		{
			final Intent intent = new Intent( this, SettingsActivity.class );
			startActivity( intent );
		}

		if ( item.getItemId() == R.id.action_logout )
		{
			Connection.Response response;
			Document logoutResponse;
			//response = Jsoup.( getString( R.string.url_logout ) )
			Jsoup.connect( getString( R.string.url_logout ) );
			//TODO add logout link to strings and use that for connection
			final Intent intent = new Intent( this, LoginScreen.class );
			startActivity( intent );
			//TODO add extra boolean value from response indicating successful logout
		}
		return id == R.id.action_settings || super.onOptionsItemSelected( item );
	}


	/**
	 * Adds items to the navigation drawer (bookmarks)
	 *
	 * @param data the bookmark to be added
	 */
	public BookmarkLink populateDrawer( BookmarkLink data )
	{
		mNavigationDrawerFragment.addItem( data );
		return data;
	}

	public void addDrawer( View v )
	{
		mNavigationDrawerFragment.addItem( new BookmarkLink( "Testing new drawer item", "test", "test" ) );
	}

	/**
	 * @param view automatically generated variable from button press
	 */


	/**
	 * Fixes the title for small devices
	 * <p/>
	 * TODO something different for tablets? Must acquire a tablet first
	 */
	private void fixTitle( String title )
	{
		mTitle = "ETI - " + title.substring( title.indexOf( " - " ) + 3 );
		restoreActionBar();
	}

	@Override
	public void registerTopic( String message, String h, int topicID )
	{
		this.h = h;
		this.topicID = topicID;
		mPostDrawerFragment.setUp( h, topicID, getApplicationContext() );
	}

	@Override
	public void sendTitle( String title )
	{
		fixTitle( title );
	}

	@Override
	public void loadTopic( String URL )
	{

		FragmentManager manager = getFragmentManager();

		//If I use the topic ID as the tag,  that clears up a lot of difficulties with stuff
		TopicFragment fragment = (TopicFragment) manager.findFragmentByTag( URL );

		//Get the current fragment (the one being replaced/hidden)
		Fragment oldFragment = manager.findFragmentByTag( "TAG_" + lastFragTag );

		if ( fragment == null )
		{
			fragment = TopicFragment.newInstance( URL );
		}

		fragment.setUp( this );
		fragment.setCallbacks( this );
		manager.beginTransaction()
				.add( R.id.container, fragment, URL )
				.hide( oldFragment )
				.addToBackStack( null )
				.commit();
		//TODO better fragment managing, need to hide all types of fragments, currently only switching via nav drawer hides properly
		//workingFragment = URL;*/
	}

	@Override
	public void changeLocation( String URL )
	{
		//TODO implement also what calls this
	}


	public void postMessage( View v )
	{
		EditText editText = (EditText) findViewById( R.id.post_message_edit_text );
		PostMessage postMessage = new PostMessage();
		if ( postMessage.post( editText.getText().toString(), h, topicID, false ) == -2 )
		{
			editText.setText( "" );
		}
		//postMessage.post( "testing", h, topicID, false );
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		private static int position;

		public PlaceholderFragment()
		{
		}

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance( int sectionNumber )
		{
			position = sectionNumber;
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt( ARG_SECTION_NUMBER, sectionNumber );
			fragment.setArguments( args );
			return fragment;
		}

		@Override
		public View onCreateView( LayoutInflater inflater, ViewGroup container,
								  Bundle savedInstanceState )
		{
			if ( position == 3 )
			{
				return inflater.inflate( R.layout.fragment_debug, container, false );
			}
			else
			{
				return inflater.inflate( R.layout.fragment_settings, container, false );
			}
		}

		@Override
		public void onAttach( Activity activity )
		{
			super.onAttach( activity );
			( (MainActivity) activity ).onSectionAttached(
					getArguments().getInt( ARG_SECTION_NUMBER ) );
		}
	}

}
