package com.HyperStandard.llr.app.Activity

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.InjectView
import butterknife.OnClick
import butterknife.OnItemSelected
import com.HyperStandard.llr.app.R
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import groovy.transform.CompileStatic

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors;

@CompileStatic
public class LoginActiviy extends Activity
{
	private final static String mTag = "LLr -> (LoginScreen)";

	private boolean autoLogin = false;

	private int preferred_account;
	private String[] usernames;
	private String[] passwords;

	private String username;
	private String password;

	private SharedPreferences prefs;

	@InjectView(R.id.username)
	protected EditText userNameEditText;
	@InjectView(R.id.password)
	protected EditText passwordEditText;
	@InjectView(R.id.progressBar)
	protected ProgressBar progressBar;
	@InjectView(R.id.loginbutton)
	protected Button loginButton;
	@InjectView(R.id.loginspinner)	//TODO shape up var names
	protected Spinner loginspinner;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		//Log.e(mTag, getDatabasePath("testMe").toString());

		//Call super who cares
		super.onCreate savedInstanceState;

		//Set the view pretty basic stuff here
		setContentView R.layout.login;

		//This only gets called if the default preferences haven't been assigned yet
		PreferenceManager.setDefaultValues( getApplicationContext(), "prefs", MODE_PRIVATE, R.xml.pref_general, false )

		//initialize the ACTION BAR, HERO OF HUMANITY
		getActionBar()

		//Give butterknife the reference so it can inject all the views
		ButterKnife.inject( this )

		//Get the shared prefs that everything is getting put in/taken out of
		prefs = getSharedPreferences( getString( R.string.pref_name ), MODE_PRIVATE );

		preferred_account = prefs.getInt( getString( R.string.prefs_preferred ), 0 );

		usernames = prefs.getString( getString( R.string.prefs_username ), null )?.split( "✓" );
		passwords = prefs.getString( getString( R.string.prefs_password ), null )?.split( "✓" );

		username = usernames[preferred_account];
		password = passwords[preferred_account];

		//Check to see if we're auto logging in
		if ( prefs.getBoolean( getString( R.string.prefs_autologin ), false )//figured I'd put this check first for the .000001 ms speed gain
				&& prefs.contains( getString( R.string.prefs_passwords ) )
				&& prefs.contains( getString( R.string.prefs_usernames ) ) )
		{
			progressBar.setVisibility ProgressBar.VISIBLE;
			loginButton.setVisibility View.INVISIBLE;

			Toast.makeText( this, "Logging in...", Toast.LENGTH_LONG ).show();

			userNameEditText.setText "";
			passwordEditText.setText "";

			//log in yeah woo we did it hooray
			new Thread().start( { login() } );

			//progressBar.setVisibility(ProgressBar.INVISIBLE);
			//loginButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate R.menu.login, menu;
		Spinner mSpinner = (Spinner) menu.findItem( R.id.loginspinner ).getActionView();
		//if ( usernames.length > 1 )
		if (true)
		{

			useraccounts.add();

			mSpinner.setVisibility( View.VISIBLE );
		} else
		{
			Log.i( mTag, "No additional accounts reported, not showing accounts menu" );
		}

		String[] usernames = ["testnig", "hallo"];
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( getApplicationContext(), android.R.layout.simple_spinner_item, usernames );
		adapter.setDropDownViewResource android.R.layout.simple_spinner_dropdown_item;

		mSpinner.setAdapter adapter;
		return super.onCreateOptionsMenu( menu );
	}

	/**
	 * On selecting action bar icons
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Take appropriate action for each action item click
		switch ( item.getItemId() )
		{
			case R.id.cleardata:
				//  Do something
				return true;
			case loginspinner:
				// Do Something
				return true;

			default:
				return super.onOptionsItemSelected( item );
		}
	}


	public void login()
	{
		String username = userNameEditText.getText().toString();
		final String password = passwordEditText.getText().toString();

		ExecutorService executor = Executors.newSingleThreadExecutor();

		String URLtoConnectTo;

		//TODO fix this up, possibly inline the callable, deal with the syntax differences etc
		if ( prefs.getBoolean( "use_iphone_login", true ) )
		{
			Log.e( mTag, "using iPhone login" );
			URLtoConnectTo = "https://iphone.endoftheinter.net/";
		} else
		{
			Log.v( mTag, "using desktop version" );
			URLtoConnectTo = "https://endoftheinter.net/";
		}
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url( URLtoConnectTo ).build();
		Response response = client.newCall( request ).execute();
		if ( response.body().toString().equals( getString( R.string.successful_response ) ) )
		{

		}

	}

	@OnItemSelected(R.id.loginspinner)
	void onItemSelected(int position)
	{
		userNameEditText.setText( usernames[position] );
		passwordEditText.setText( passwords[position] );

		new Thread.start( { login() } )
	}

	/**
	 * This clears any saved login information
	 * TODO selective delete based on different accounts? Also, actually implement eheh heh
	 */
	public void clearData(MenuItem item)
	{
		Toast.makeText( this, "buh", Toast.LENGTH_SHORT ).show();
	}


}