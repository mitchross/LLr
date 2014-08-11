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
import android.widget.ListView;
import android.widget.Toast;

import com.HyperStandard.llr.app.BookmarkLink;
import com.HyperStandard.llr.app.CustomTypefaceSpan;
import com.HyperStandard.llr.app.Fragment.PollFragment;
import com.HyperStandard.llr.app.Fragment.TopicFragment;
import com.HyperStandard.llr.app.Fragment.TopicListFragment;
import com.HyperStandard.llr.app.LoadPage;
import com.HyperStandard.llr.app.Navigation.NavigationAdapter;
import com.HyperStandard.llr.app.Navigation.NavigationDrawerFragment;
import com.HyperStandard.llr.app.R;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.tuple.Pair;

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

	private static final int TYPE_TOPICLIST = 1;
	private static final int TYPE_TOPIC = 2;
	private static final int TYPE_POLL = 3;
	private static final int TYPE_BACK_BUTTON = 4;
	public static Map<String, String> cookies;
	private static String mTag = "LLr -> (Main)";
	public int UserID;
	ListView mListView;
	@Optional
	@InjectView( R.id.leftNavigationDrawer )
	ListView listView;
	private Queue<Pair<String, Integer>> pagesHistory = new LinkedList<>();
	private ArrayList<String> pageHistory = new ArrayList<>();
	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	/**
	 * Fragment storing the poll
	 */
	private PollFragment mPollFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	/**
	 * stores the tag of the active fragment
	 */
	private int currentFragment;
	private String currentFragmentTag = "";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		currentFragment = -1;
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		UserID = getIntent().getIntExtra( "userId", -1 );

		ButterKnife.inject( this );

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById( R.id.left_drawer );


		mTitle = getTitle();

		mNavigationDrawerFragment.setUp(
				R.id.left_drawer,
				(DrawerLayout) findViewById( R.id.drawer_layout ),
				UserID,
				this );

		if ( true )//TODO why is this still here goodness
		{
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<Document> loader = executor.submit( new LoadPage( getString( R.string.ll_main ), cookies ) );
			try
			{
				Document main = loader.get( 5, TimeUnit.SECONDS );
				String ImageURLTEMP;//TODO deleteme later
				/*Element poll = main.select( "div.poll" ).first();
				mPollFragment = new PollFragment();
				mPollFragment.setCallbacks( this );
				mPollFragment.setUp( main.select( "div.poll" ) );*///TODO implement poll after frag handling/nav problems fixed
				Elements elements = main.select( "#bookmarks > span" );
				ArrayList<BookmarkLink> bookmarks = new ArrayList<>( elements.size() );

				/**
				 * Get the bookmarks, to populate the Navigation drawer with links
				 */
				for ( Element e : elements )
				{//TODO fix this thing what's the deal with it
					bookmarks.add( populateDrawer( new BookmarkLink(
							e.select( "span > a" ).first().ownText(),
							e.select( "span > a" ).attr( "abs:href" ),
							"TOPIC_LIST"
					) ) );

					Log.e( e.select( "span > a" ).first().ownText(), e.select( "span > a" ).attr( "abs:href" ) );
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
	}

	@Override
	public void onNavigationDrawerItemSelected( int position, String URL )
	{
		Log.e( mTag, Integer.toString( position ) );
		if ( position == 0 )
		{
			FragmentOverlord( TYPE_POLL, "" );
		}
		else
		{
			FragmentOverlord( TYPE_TOPICLIST, URL );
		}
		/*
		String tag = "TAG_" + position;
		if ( position == currentFragment )
		{//No point in changing fragments if you're on the current one
			return;
		}
		FragmentManager manager = getFragmentManager();
		if ( currentFragment == -1 )
		{//During first initilization
			TopicListFragment fragment = TopicListFragment.newInstance( position, URL );
			//TODO merge these calls maybe? ? ? ? they both have 'this' but 'this' means diff things
			fragment.setUp( getApplicationContext() );
			fragment.setCallbacks( this );
			manager.beginTransaction()
					.add( R.id.container, fragment, tag )//This needs to use the container found in activity_main.xml
					.addToBackStack( null )
					.commit();
			currentFragment = position;
			return;
		}
		TopicListFragment newFragment = (TopicListFragment) manager.findFragmentByTag( tag );
		Fragment oldFragment = manager.findFragmentByTag( "TAG_" + currentFragment );
		if ( newFragment == null )
		{//If the new Fragment is null then it needs to be inflated and added
			newFragment = TopicListFragment.newInstance( position, URL );
			newFragment.setCallbacks( this );
			newFragment.setUp( getApplicationContext() );
			manager.beginTransaction()
					.add( R.id.container, newFragment, tag )
					.hide( oldFragment )
					.addToBackStack( null )
					.commit();

			currentFragment = position;
			return;
		}//fallback to the new Fragment being already inflated and not the current one, therefore it's been hidden and can be shown
		manager.beginTransaction()
				.show( newFragment )
				.hide( oldFragment )
				.addToBackStack( null )
				.commit();
		currentFragment = position;*/

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
	public void onBackPressed()
	{
		/* This captures the back button, and tells the fragment manager that it should remove the latest fragment
		 * poll() is necessary because we want to remove the info for the latest (read: current) fragment so that the second to last
		 * fragment (read: previous) fragment can be shown. At the moment the current fragment gets removed.
		 */
		Pair<String, Integer> t;
		t = pagesHistory.poll();
		FragmentOverlord( TYPE_BACK_BUTTON, t.getLeft() );
	}

	public void FragmentOverlord( int type, String URL )
	{
		Fragment fragmentToHide;
		String fragmentTagToShow;
		int newFragmentType;


		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		if ( !pagesHistory.isEmpty() )
		{
			fragmentToHide = manager.findFragmentByTag( pagesHistory.poll().getLeft() );
		}
		else
		{
			fragmentToHide = null;
		}

		//TODO implement sliding animations
		transaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );


		/* This lets the one method both add new fragments and also override the back button
		 * later on I'll implement tabs and such, which is why I'm using fragments, instead of activities
		 * I could use views I suppose but I like this way, it's modular.
		 * There needs to be a null check on the fragmentToHide later on in the switch statement
		 * using poll() before calling the method means that the last item on the Queue gets removed ahead of time and the tag is passed
		 * to the method for removal
		 */
		if ( type == TYPE_BACK_BUTTON )
		{
			fragmentToHide = manager.findFragmentByTag( URL );
			transaction.remove( fragmentToHide );
			if ( pagesHistory.isEmpty() )
			{
				fragmentTagToShow = "http://boards.endoftheinter.net/topics/Posted";
				newFragmentType = TYPE_TOPICLIST;
			}
			else
			{
				fragmentTagToShow = pagesHistory.peek().getLeft();
				newFragmentType = pagesHistory.peek().getRight();
			}
		}
		/* If the type isn't TYPE_BACK_BUTTON, i.e. any type that involves adding new fragments
		 * then you keep the inputs as they are. The URL serves both as the tag, for finding by tag, and also
		 * the actual URL that gets passed to the fragment on instantiation (to actually load the appropriate page)
		 * TODO handle cases in which, in different tabs, the same URL gets loaded
		 * That can probably be handled by appending a tab number or ID to the URL before passing it as a tag
		 */
		else
		{
			fragmentTagToShow = URL;
			newFragmentType = type;
		}
		/*for ( String s : pageHistory )
		{
			//TODO optimize this
			if ( manager.findFragmentByTag( s ) != null && s != URL )
			{ //Make sure fragment isn't null or the wanted one
				if ( !manager.findFragmentByTag( s ).isHidden() )
				{//Make sure the fragment isn't already hidden
					transaction.hide( manager.findFragmentByTag( s ) );//Hide the fragment
				}
			}
			if ( manager.findFragmentByTag( s ) != null && s == URL )
			{
				if ( manager.findFragmentByTag( s ).isHidden() )
				{
					transaction.show( manager.findFragmentByTag( s ) );
					return;
				}
			}
		}
		if ( pageHistory.contains( URL ) )
		{
			pageHistory.add( URL );
		}*/

		/* In theory, there should only be one visible fragment at a time, since back button presses are being captured, so
		 * there's not much reason to iterate through the Queue of tags to hide them all. The null check is because the back
		 * button handling makes the current fragment null. I could add a proper back/forward history type but that's not really normative
		 * I think in a no web browser environment.
		 * TODO figure out if there are edge cases where that's not true
		 */
		if ( fragmentToHide != null )
		{
			transaction.hide( fragmentToHide );
		}

		switch ( newFragmentType )
		{
			case TYPE_TOPICLIST:
				TopicListFragment topicListFragment = (TopicListFragment) manager.findFragmentByTag( fragmentTagToShow );
				pagesHistory.add( Pair.of( fragmentTagToShow, newFragmentType ) );
				if ( topicListFragment == null )
				{
					topicListFragment = TopicListFragment.newInstance( 0, fragmentTagToShow );
					topicListFragment.setUp( getApplicationContext() );
					topicListFragment.setCallbacks( this );
					transaction.add( R.id.container, topicListFragment, fragmentTagToShow );
				}
				else
				{
					transaction.show( topicListFragment );
				}
				Log.e( mTag, pagesHistory.peek().getLeft() );
			{

			}
			break;
			case TYPE_TOPIC:
				pagesHistory.add( Pair.of( fragmentTagToShow, newFragmentType ) );
				Log.e( mTag, pagesHistory.peek().getLeft() );

				TopicFragment topicFragment = (TopicFragment) manager.findFragmentByTag( URL );
				if ( topicFragment == null )
				{
					topicFragment = TopicFragment.newInstance( fragmentTagToShow );
					topicFragment.setCallbacks( this );
					topicFragment.setUp( getApplicationContext() );
					transaction.add( R.id.container, topicFragment, fragmentTagToShow );
				}
				else
				{
					transaction.show( topicFragment );
				}

				break;
			case TYPE_POLL:
				transaction.replace( R.id.container, mPollFragment );
				break;
			default:
				break;

		}
		Log.e( mTag, "loading: " + URL );
		transaction.commit();
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
		if ( item.getItemId() == R.id.action_settings )
		{
			final Intent intent = new Intent( this, SettingsActivity.class );
			startActivity( intent );
		}

		if ( item.getItemId() == R.id.action_logout )
		{
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
	public void sendTitle( String title )
	{
		fixTitle( title );
	}

	@Override
	public void loadTopic( String URL )
	{

		FragmentOverlord( TYPE_TOPIC, URL );
	/*
		FragmentManager manager = getFragmentManager();

		//If I use the topic ID as the tag,  that clears up a lot of difficulties with stuff
		TopicFragment fragment = (TopicFragment) manager.findFragmentByTag( URL );

		//Get the current fragment (the one being replaced/hidden)
		Fragment oldFragment = manager.findFragmentByTag( "TAG_" + currentFragment );

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
