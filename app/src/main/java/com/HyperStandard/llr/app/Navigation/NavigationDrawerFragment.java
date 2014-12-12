package com.HyperStandard.llr.app.Navigation;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.HyperStandard.llr.app.Activity.BaseActivity;
import com.HyperStandard.llr.app.Activity.MainActivity;
import com.HyperStandard.llr.app.CustomTypefaceSpan;
import com.HyperStandard.llr.app.Models.BookmarkLink;
import com.HyperStandard.llr.app.R;
import com.HyperStandard.llr.app.Type;

import java.util.ArrayList;

import static com.HyperStandard.llr.app.R.layout.listview_navigation_row;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment
{

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually
	 * expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
	private final String mTag = "NavDAct debug";
	BookmarkLink currentURL;
	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;
	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private Toolbar mToolbar;
	private DrawerLayout mDrawerLayout;


	private ListView mDrawerListView;
	private View mFragmentContainerView;
	private int mCurrentSelectedPosition = 4;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;


	private int userId;
	private String URL;
	private Context context;

	public NavigationDrawerFragment()
	{
	}

	public String getURL()
	{
		return URL;
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		mUserLearnedDrawer = sp.getBoolean( PREF_USER_LEARNED_DRAWER, false );

		if ( savedInstanceState != null )
		{
			mCurrentSelectedPosition = savedInstanceState.getInt( STATE_SELECTED_POSITION );
			mFromSavedInstanceState = true;
		}

		// Select either the default item (0) or the last selected item.
		selectItem( mCurrentSelectedPosition, "http://boards.endoftheinter.net/topics/LUE" );
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState )
	{
		super.onActivityCreated( savedInstanceState );
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu( true );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
							  Bundle savedInstanceState )
	{
		mDrawerListView = (ListView) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false );

		/**
		 * Use this to set actions done with the Nav drawer why did I not see this oh my god I was literally duplicating it ugh
		 */


		//TODO put this code somewhere else maybe?
		ArrayList<BookmarkLink> bookmarks = new ArrayList<>();

		/*if ( context == null )
		{
			Log.e( mTag, "Context is null" );
			return null;
		}
		mDrawerListView.setAdapter( new NavigationAdapter(
						//getActionBar().getThemedContext(),
						context,
						listview_navigation_row,
						bookmarks,
						userId
				)
		);*/
		mDrawerListView.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				currentURL = (BookmarkLink) parent.getItemAtPosition( position );
				selectItem( position, currentURL.getBookmarkTags() );
			}
		} );
		mDrawerListView.setItemChecked( mCurrentSelectedPosition, true );
		return mDrawerListView;
	}

	public boolean isDrawerOpen()
	{
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen( mFragmentContainerView );
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	public void setUp( int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar, int UserID, Context context )
	{
		this.context = context;
		mFragmentContainerView = getActivity().findViewById( fragmentId );
		mDrawerLayout = drawerLayout;
		this.userId = UserID;
		mToolbar = toolbar;

		// set a custom shadow that overlays the main content when the drawer opens
		//just kidding no shadow bc we're material suck it a hahaha haha a haha
		//mDrawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
		// set up the drawer's list view with items and click listener

		android.support.v7.app.ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled( true );
		actionBar.setHomeButtonEnabled( true );

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		this.mDrawerToggle = new ActionBarDrawerToggle(
				getActivity(),                    /* host Activity */
				mDrawerLayout,                    /* DrawerLayout object */
				mToolbar,       /* nav drawer image to replace 'Up' caret */
				R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
				R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
		)
		{

			@Override
			public void onDrawerClosed( View drawerView )
			{
				super.onDrawerClosed( drawerView );
				if ( !isAdded() )
				{
					return;
				}

				mCallbacks.closeKeyboard();

				getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened( View drawerView )
			{
				super.onDrawerOpened( drawerView );
				if ( !isAdded() )
				{
					return;
				}

				if ( !mUserLearnedDrawer )
				{
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences( getActivity() );
					sp.edit().putBoolean( PREF_USER_LEARNED_DRAWER, true ).apply();
				}

				getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};


		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if ( !mUserLearnedDrawer && !mFromSavedInstanceState )
		{
			mDrawerLayout.openDrawer( mFragmentContainerView );
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post( new Runnable()
		{
			@Override
			public void run()
			{
				mDrawerToggle.syncState();
			}
		} );

		mDrawerLayout.setDrawerListener( mDrawerToggle );
		ArrayList<BookmarkLink> bookmarks = new ArrayList<>();

		NavigationAdapter adapter = new NavigationAdapter( getActionBar().getThemedContext(),
				listview_navigation_row,
				bookmarks,
				userId
		);
		mDrawerListView.setAdapter( adapter );
		mDrawerListView.invalidate();
	}

	private void selectItem( int position, String URL )
	{
		mCurrentSelectedPosition = position;
		if ( mDrawerListView != null )
		{
			mDrawerListView.setItemChecked( position, true );
		}
		if ( mDrawerLayout != null )
		{
			mDrawerLayout.closeDrawer( mFragmentContainerView );
		}
		if ( mCallbacks != null )
		{
			Log.e( "url is ", URL );
			//fixme this logic is whack fix it
			if ( position == 0 )
			{
				mCallbacks.onNavigationDrawerItemSelected( position, URL, Type.IMAGE );
			}
			else
			{
				mCallbacks.onNavigationDrawerItemSelected( position, URL, Type.TOPICLIST );
			}
		}
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
		try
		{
			mCallbacks = (NavigationDrawerCallbacks) activity;
		}
		catch ( ClassCastException e )
		{
			throw new ClassCastException( "Activity must implement NavigationDrawerCallbacks." );
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
		outState.putInt( STATE_SELECTED_POSITION, mCurrentSelectedPosition );
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged( newConfig );
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged( newConfig );
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater )
	{
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if ( mDrawerLayout != null && isDrawerOpen() )
		{
			inflater.inflate( R.menu.global, menu );
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{

		if ( mDrawerToggle.onOptionsItemSelected( item ) )
		{
			return true;
		}

		if ( item.getItemId() == R.id.action_example )
		{
			Toast.makeText( getActivity(), "Example action.", Toast.LENGTH_SHORT ).show();
			return true;
		}

		return super.onOptionsItemSelected( item );
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app
	 * 'context', rather than just what's in the current screen.
	 */
	private void showGlobalContextActionBar()
	{
		android.support.v7.app.ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled( true );
		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );
		//Necessary to change font on the action bar
		SpannableStringBuilder t = new SpannableStringBuilder( getString( R.string.app_name ) );
		t.setSpan( new CustomTypefaceSpan( context, getString( R.string.font_title ) ), 0, t.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE );
		actionBar.setTitle( t );
	}

	private android.support.v7.app.ActionBar getActionBar()
	{
		return ((BaseActivity) getActivity()).getSupportActionBar();
		//return getActivity().getActionBar();
	}

	/**
	 * Add an item to the nav drawer
	 *
	 * @param data the item to be added
	 */
	public void addItem( BookmarkLink data )
	{
		NavigationAdapter adapter = (NavigationAdapter) mDrawerListView.getAdapter();
		adapter.add( data );
		mDrawerListView.setAdapter( adapter );
	}

	public ArrayList<BookmarkLink> getLinks()
	{
		NavigationAdapter adapter = (NavigationAdapter) mDrawerListView.getAdapter();
		ArrayList<BookmarkLink> bookmarks = new ArrayList<>( adapter.getCount() );
		for ( int i = 0; i < adapter.getCount(); i++ )
		{
			bookmarks.add( adapter.getItem( i ) );
		}
		return bookmarks;
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks
	{
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected( int position, String URL, Type type );

		public void closeKeyboard();
	}

}
