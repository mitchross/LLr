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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.HyperStandard.llr.app.Data.Cookies;
import com.HyperStandard.llr.app.Login;
import com.HyperStandard.llr.app.R;

import org.jsoup.Connection;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Entry point to application, with login
 *
 * @author HyperStandard
 * @version 0.1
 * @since 6/14/2014.
 */
public class LoginScreen extends BaseActivity
{
	private final static String mTag = "LLr Login";
	@InjectView(R.id.username)
	protected EditText userNameEditText;
	@InjectView(R.id.password)
	protected EditText passwordEditText;
	private SharedPreferences prefs;

	@OnClick(R.id.loginbutton)
	protected void loginButtonClick()
	{
		login();
	}


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.login );

		//Butterknife injection for views
		ButterKnife.inject( this );
		getActionBar();

		//Get the shared prefs that the username is going to be put in
		//TODO change this to possibly encrypted login info, and support for multiple accounts
		prefs = PreferenceManager.getDefaultSharedPreferences( this );
		//TODO figure out how to get external IP and use LL check login instead of just logging in again
		if ( prefs.contains( getString( R.string.prefs_password ) ) && prefs.contains( getString( R.string.prefs_username ) ) && prefs.getBoolean( getString( R.string.prefs_login ), false ) )
		{
			Toast.makeText( this, "Logging in with saved credentials", Toast.LENGTH_SHORT ).show();
			autoLogin( prefs.getString( getString(R.string.prefs_username), "" ), prefs.getString( getString(R.string.prefs_password), "" ) );
		}

	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.login, menu );

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
			case R.id.loginspinner:
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
		Future<Connection.Response> loggedin = executor.submit( new Login( username, password ) );
		try
		{
			Connection.Response response = loggedin.get( 30, TimeUnit.SECONDS );

			//Check to see if we've got logged in correctly, and if so, set up the account.
			if ( response.body().equals( "<script>document.location.href=\"/\";</script>" ) )
			{
				Log.i( mTag, "Successful login, using manual login()" );
				final Intent intent = new Intent( this, MainActivity.class );


                //Using global cookie cache
                Cookies.setCookies(response.cookies());

                intent.putExtra("userId", response.cookie("userid"));
                CheckBox checkBox = (CheckBox) findViewById( R.id.login_checkbox );
				if ( checkBox.isChecked() )
				{
					prefs.edit()
							.putString(getString(R.string.prefs_password), password)
							.putString(getString(R.string.prefs_username), username)
							.putBoolean(getString(R.string.prefs_login), true)
							.apply();
				}
				startActivity( intent );
			}
			else
			{
				Toast.makeText( this, "Failed to login", Toast.LENGTH_SHORT ).show();
			}

		}
		catch ( TimeoutException | InterruptedException | ExecutionException e )
		{
			e.printStackTrace();
		}


	}

	public void autoLogin( String username, String password )
	{

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Connection.Response> loggedin = executor.submit( new Login( username, password ) );
		try
		{
			Connection.Response response = loggedin.get( 15, TimeUnit.SECONDS );

			//Check to see if we've got logged in correctly, and if so, set up the account.
			if ( response.body().equals( "<script>document.location.href=\"/\";</script>" ) )
			{
				Log.v( mTag, "Successful login, using autoLogin()" );

				final Intent intent = new Intent( this, MainActivity.class );
				//Pass the cookies from the login page to the Main activity, where they get turned back into a map
				//I don't know how to parcel a Map so this is the best solution atm
				String[] cookies = new String[ 3 ];
				cookies[ 0 ] = response.cookie( "userid" );
				cookies[ 1 ] = response.cookie( "PHPSESSID" );
				cookies[ 2 ] = response.cookie( "session" );
				intent.putExtra( "Cookies", cookies );

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
