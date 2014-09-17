package com.HyperStandard.llr.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.HyperStandard.llr.app.Data.Cookies;
import com.HyperStandard.llr.app.Login;
import com.HyperStandard.llr.app.R;

import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.HyperStandard.llr.app.R.id.loginspinner;

/**
 * Entry point to application, with login
 *
 * @author HyperStandard
 * @version 0.1
 * @since 6/14/2014.
 */
public class LoginScreen extends BaseActivity
{
	private final static String mTag = "LLr -> (LoginScreen)";

	@InjectView(R.id.username)
	protected EditText userNameEditText;
	@InjectView(R.id.password)
	protected EditText passwordEditText;
    @InjectView(R.id.progressBar)
    protected ProgressBar progressBar;
    @InjectView(R.id.loginbutton)
    protected Button loginButton;

	private SharedPreferences prefs;

	@OnClick(R.id.loginbutton)
	protected void loginButtonClick()
	{
		login();
	}


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		Log.e( mTag, getDatabasePath( "testMe" ).toString() );
		super.onCreate( savedInstanceState );
		setContentView( R.layout.login );
		PreferenceManager.setDefaultValues( getApplicationContext(), "prefs", MODE_PRIVATE, R.xml.pref_general, false );

		//Butterknife injection for views
		ButterKnife.inject( this );
		getActionBar();

		//Get the shared prefs that the username is going to be put in
		//TODO change this to possibly encrypted login info, and support for multiple accounts
		prefs = getSharedPreferences( getString( R.string.pref_name ), MODE_PRIVATE );
		//TODO figure out how to get external IP and use LL check login instead of just logging in again
		if ( prefs.contains( getString( R.string.prefs_password ) ) && prefs.contains( getString( R.string.prefs_username ) ) && prefs.getBoolean( getString( R.string.prefs_login ), false ) )
		{
            progressBar.setVisibility(ProgressBar.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            Toast.makeText( this, "using saved credentials", Toast.LENGTH_LONG ).show();
			userNameEditText.setText( prefs.getString( getString( R.string.prefs_username ), "" ) );
			passwordEditText.setText( prefs.getString( getString( R.string.prefs_password ), "" ) );
            new Thread(new Runnable()
            {public void run() {
                    login();
                }}).start();
            //progressBar.setVisibility(ProgressBar.INVISIBLE);
            //loginButton.setVisibility(View.VISIBLE);
        }
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.login, menu );
		Spinner mSpinner = (Spinner) menu.findItem( loginspinner ).getActionView();//You need the getActionView thing I don't know why
		if ( prefs.contains( getString( R.string.prefs_username_set ) ) )
		{
			class AccountInfo
			{
				public String un;
				public String pw;

				public AccountInfo( String un, String pw )
				{
					this.un = un;
					this.pw = pw;
				}
			}
			//Instantiate arrays to hold un/pws
			ArrayList<AccountInfo> useraccounts = new ArrayList<>();

			//I don't know how to add to this set? ? ?
			Set<String> usernameSet = prefs.getStringSet( getString( R.string.prefs_username_set ), null );
			for ( String s : usernameSet )
			{
				if ( getSharedPreferences( s, MODE_PRIVATE ).contains( getString( R.string.prefs_password ) ) )
				{//Checks to see if there's an associated password with the username
					//TODO figure out whether too many sharedpreferences are bad
					String password = getSharedPreferences( s, MODE_PRIVATE ).getString( getString( R.string.prefs_password ), null );
					useraccounts.add( new AccountInfo( s, password ) );
				}
			}
			mSpinner.setVisibility( View.VISIBLE );
		}
		else
		{
			Log.i( mTag, "No additional accounts reported, not showing accounts menu" );
		}


		String[] options = { "item 1", "items 2" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( getApplicationContext(), android.R.layout.simple_spinner_item, options );
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

		mSpinner.setAdapter( adapter );
		return super.onCreateOptionsMenu( menu );
	}

	/**
	 * On selecting action bar icons
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
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
            Log.e( mTag, "using iPhone login");
			URLtoConnectTo = "https://iphone.endoftheinter.net/";
		}
		else
		{
			Log.e( mTag, "using desktop version" );
			URLtoConnectTo = "https://endoftheinter.net/";
			//URLtoConnectTo = "https://iphone.endoftheinter.net/";
		}

		Future<Connection.Response> loggedin = executor.submit( new Login( URLtoConnectTo, username, password ) );
		try
		{
			Connection.Response response = loggedin.get( 30, TimeUnit.SECONDS );

			//TODO get this in a constants file for easy updating, character escapes are proving troublesome
			//Check to see if we've got logged in correctly, and if so, set up the account.
			if ( response.body().equals( "<script>document.location.href=\"/\";</script>" ) )
			{
				Log.e( response.body(), getString( R.string.successful_response ) );

				Log.i( mTag, "Successful login, using login() NOT login(username/password)" );
				final Intent intent = new Intent( this, MainActivity.class );

				//Adding cookies to global cookie cache
				Cookies.setCookies( response.cookies() );

				int userId = Integer.parseInt( response.cookie( getString( R.string.cookies_userid ) ) );
				intent.putExtra( "userId", userId );
				CheckBox checkBox = (CheckBox) findViewById( R.id.login_checkbox );
				if ( checkBox.isChecked() )
				{
					Set usernameSet;
					if ( prefs.contains( getString( R.string.prefs_username_set ) ) )
					{
						usernameSet = new HashSet( 1 );
						usernameSet.add( password );
						/*SharedPreferences newPasswordPreference = getSharedPreferences( username, MODE_PRIVATE );
						newPasswordPreference
						prefs.edit()
								.putStringSet( getString( R.string.prefs_username_set ) )*/
					}
					prefs.edit()
							.putString( getString( R.string.prefs_password ), password )
							.putString( getString( R.string.prefs_username ), username )
							.putBoolean( getString( R.string.prefs_login ), true )
							.apply();
				}
                startActivity( intent );
			}
			else
			{
                Log.e( response.body(), getString( R.string.successful_response ) );
				Toast.makeText( this, "Failed to login", Toast.LENGTH_SHORT ).show();
			}
		}
		catch ( TimeoutException | InterruptedException | ExecutionException e )
		{
            e.printStackTrace();
		}
	}

    /*
     * Can this be deleted? Why do we have 2 login methods, only one of which is used?
     */
    //TODO decide if we delete this?
	public void login( String username, String password )
	{

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Connection.Response> loggedin = executor.submit( new Login( "http://iphone.endoftheinter.net/", username, password ) );
		try
		{
			Connection.Response response = loggedin.get( 15, TimeUnit.SECONDS );//TODO standardize timeouts

			//Check to see if we've got logged in correctly, and if so, set up the account.
			if ( response.body().equals( "<script>document.location.href=\"/\";</script>" ) )
			{
				Log.v( mTag, "Successful login, using login()" );

				//Using global cookie cache
				Cookies.setCookies( response.cookies() );

				final Intent intent = new Intent( this, MainActivity.class );

				//Actually start the main application proper
				startActivity( intent );
			}


		}
		catch ( TimeoutException | InterruptedException | ExecutionException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * This clears any saved login information
	 * TODO selective delete based on different accounts? Also, actually implement eheh heh
	 */
	public void clearData( MenuItem item )
	{
		Toast.makeText( this, "buh", Toast.LENGTH_SHORT ).show();
	}

	/**
	 * Emulate the overflow button
	 * I could make it invisible w hardware menu buttons but eh only Samsung (more like samshit lol amirite) still uses those tbh
	 *
	 * @param button
	 */
	public void openOptions( View button )
	{

		PopupMenu popup = new PopupMenu( this, button );
		popup.getMenuInflater().inflate( R.menu.login, popup.getMenu() );

		popup.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener()
		{
			public boolean onMenuItemClick( MenuItem item )
			{
				item.collapseActionView();
				return true;
			}
		} );
		popup.show();
	}
}
