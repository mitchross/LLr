package com.HyperStandard.llr.app.Activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.HyperStandard.llr.app.Cache;
import com.HyperStandard.llr.app.CustomTypefaceSpan;
import com.HyperStandard.llr.app.Exceptions.LoggedOutException;
import com.HyperStandard.llr.app.Exceptions.WaitException;
import com.HyperStandard.llr.app.Fragment.PollFragment;
import com.HyperStandard.llr.app.LoadPage;
import com.HyperStandard.llr.app.Models.BookmarkLink;
import com.HyperStandard.llr.app.Navigation.NavigationDrawerFragment;
import com.HyperStandard.llr.app.Navigation.PostDrawerFragment;
import com.HyperStandard.llr.app.Page.Topic;
import com.HyperStandard.llr.app.Page.TopicList;
import com.HyperStandard.llr.app.PostMessage;
import com.HyperStandard.llr.app.R;
import com.HyperStandard.llr.app.Type;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class MainActivity extends BaseActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks,
		//NavigationAdapter.NavigationDrawerCallback,
		PollFragment.Callbacks,
		TopicList.Callbacks,
		Topic.Callbacks

{
	/**
	 * Used for logging
	 */
	private static String mTag = "LLr -> (Main)";

	private static int userId;

	ListView mListView;
	@Optional
	@InjectView(R.id.leftNavigationDrawer)
	ListView listView;

	@InjectView(R.id.container)
	ViewAnimator container;

	@InjectView(R.id.post_message_edit_text)
	EditText messageEditText;

	@InjectView(R.id.post_message_signature_box)
	EditText signatureEditText;

	FragmentManager manager;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Fragment storing the poll
	 */
	private PollFragment mPollFragment;

	/**
	 * Fragment used for the posting drawer
	 */
	private PostDrawerFragment mPostDrawerFragment;

	//Used to control posting
	private String post_topic;
	private String post_validation;

	private String signature;

	private Queue<ImmutablePair<String, Type>> topicHistory = new ArrayDeque<>();

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle = "";

	private int ANIMATION_TIME = 500;

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

		//Inject all the views with butterknife bluh bluh
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
				(Toolbar) findViewById( R.id.toolbar ),
				userId,
				getApplicationContext() );


		/**
		 * Load the main page to get data from it i.e. bookmarks and other stuff later
		 */
		ExecutorService executor = Executors.newSingleThreadExecutor();
		//Future<Document> loader = executor.submit( new LoadPage( getString( R.string.ll_main ) ) );
		Future<Document> loader = executor.submit( new LoadPage( "http://endoftheinter.net/profile.php?user=" + userId ) );
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
						"http:" + e.select( "span > a" ).attr( "href" ),
						"TOPIC_LIST"
				) );

				Log.v( mTag, "loading bookmark: \"" + e.select( "span > a" ).first().ownText() + "\" @ " + e.select( "span > a" ).attr( "href" ) );
			}

			int sigloc = 0;
			Elements sigcheck = main.select( "tr:has(td)" );
			for ( Element e : sigcheck )
			{
				//todo handle edge cases where users have the string "Signature" elsewhere in their profile page
				//Log.e( mTag, e.select( "tr" ).text() );
				if ( e.select( "tr" ).text().contains( "Signature" ) )
				{
					sigloc = e.siblingIndex() / 2;
					break;
				}
			}
			if ( sigloc > 0 )
			{
				signature = sigcheck.get( sigloc ).child( 1 ).html();
				signature = StringEscapeUtils.unescapeHtml4( signature ).replace( "<br>", "\n" );
				signatureEditText.setText( signature );
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
	public void onNavigationDrawerItemSelected( int position, String URL, Type types )
	{
		//if ( types == Type.TOPICLIST )
		//{
		new TopicList( URL, this, getApplicationContext() );
		//}
	}

	public void restoreActionBar()
	{
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
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
			//get prefes so we can make sure not to auto login
			getSharedPreferences( getString( R.string.preferences_name ), MODE_PRIVATE )
					.edit()
					.putBoolean( getString( R.string.prefs_autologin ), false )
					.commit();

			Request logoutRequest = new Request.Builder().url( getString( R.string.url_logout ) ).build();
			try
			{
				Response response = Cache.Web.Client().newCall( logoutRequest ).execute();
				Log.v( mTag, "Logging out, response code: " + response.code() );
				final Intent intent = new Intent( this, LoginScreen.class );
				startActivity( intent );
			}
			catch ( IOException e )
			{
				Toast.makeText( this, "Failed to log out", Toast.LENGTH_LONG ).show();
				e.printStackTrace();
			}


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
	public void setTitle( String title )
	{
		fixTitle( title );
	}

	@Override
	public void registerTopic( String hTag, String topicId )
	{
		post_validation = hTag;
		post_topic = topicId;
	}


	@Override
	public void loadTopic( String URL )
	{
		Topic topic = new Topic(  URL , this, getApplicationContext() );
	}


	public void postMessage( View v )
	{
		PostMessage postMessage = new PostMessage();
		try
		{
			if ( postMessage.post(
					messageEditText.getText().toString(),
					"---\n" + signatureEditText.getText().toString(),
					post_validation,
					post_topic,
					false ).getLeft() == com.HyperStandard.llr.app.Response.SUCCEEDED )
			{
				messageEditText.setText( "" );
			}
		}
		catch ( LoggedOutException e )
		{
			Toast.makeText( this, "logged out", Toast.LENGTH_LONG ).show();
			e.printStackTrace();
		}
		catch ( WaitException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setTopicListView( View view, String url )
	{
		Log.v( mTag, "Loading TopicListView @ " + url );
		Animation animation = AnimationUtils.loadAnimation( this, android.R.anim.slide_in_left );
		animation.setDuration( ANIMATION_TIME );

		container.setAnimation( animation );

		if ( container.getChildCount() > 0 )
		{
			container.removeAllViews();
		}
		container.addView( view );
		topicHistory.add( new ImmutablePair<>( url, Type.TOPICLIST ) );

	}

	@Override
	public void setTopicView( View view, String url )
	{
		Log.v( mTag, "Loading TopicView @ " + url );
		Animation animation = AnimationUtils.loadAnimation( this, android.R.anim.slide_in_left );
		animation.setDuration( ANIMATION_TIME );

		container.setAnimation( animation );

		if ( container.getChildCount() > 0 )
		{
			container.removeAllViews();
		}
		container.removeAllViews();
		container.addView( view );
		topicHistory.add( new ImmutablePair<>( url, Type.TOPIC ) );

	}

	@Override
	public void onBackPressed()
	{
		if ( topicHistory.peek() != null )
		{
			if ( topicHistory.peek().getRight().equals( Type.TOPICLIST ) )
			{
				new TopicList( topicHistory.poll().getLeft(), this, getApplicationContext() );
			}
			else
			{
				new Topic( topicHistory.poll().getLeft() , this, getApplicationContext() );
			}
		}
	}
}
