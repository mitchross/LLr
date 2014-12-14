package com.HyperStandard.llr.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.HyperStandard.llr.app.Cache;
import com.HyperStandard.llr.app.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnLongClick;

/**
 * Entry point to application, with login
 *
 * @author HyperStandard
 * @version 0.2
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
	@InjectView(R.id.login_checkbox)
	protected CheckBox checkBox;

	private ArrayList<String> usernames;
	private ArrayList<String> passwords;

	private SharedPreferences prefs;
	private OkHttpClient client;

	private Gson gson;
	private Type dataType;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.login );

		//So, this is not the elegant solution but if it's done first thing should work with the cache
		Cache.Web.Setup( getApplicationContext() );

		ButterKnife.inject( this );

		gson = new Gson();
		dataType = new TypeToken<ArrayList<String>>()
		{
		}.getType();


		//Get the general preferences for the application (same as the ones used by the pref activity)
		prefs = getSharedPreferences( getString( R.string.preferences_name ), MODE_PRIVATE );

		//These are ArrayList<String>s if you didn't know
		usernames = gson.fromJson( prefs.getString( "usernames", "" ), dataType );
		passwords = gson.fromJson( prefs.getString( "passwords", "" ), dataType );

		//Index for which account is preferred
		int preferred_account = prefs.getInt( "preferred_account", 0 );

		//Whether to log in automatically
		boolean autoLogin = prefs.getBoolean( "auto_login", false );

		//some sanity checks probably not necessary but still why not
		if ( ( usernames != null && passwords != null ) && ( passwords.size() == usernames.size() ) )
		{
			if ( preferred_account < usernames.size() )
			{

				userNameEditText.setText( usernames.get( preferred_account ) );
				passwordEditText.setText( passwords.get( preferred_account ) );
			}
		}
		if ( autoLogin && usernames != null && passwords != null )
		{
			login( usernames.get( preferred_account ), passwords.get( preferred_account ) );
		}

		//Set the client and default cookies
		//TODO make a better cookie store
		client = Cache.Web.Client();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate( R.menu.menu_login, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if ( id == R.id.action_settings )
		{
			return true;
		}

		return super.onOptionsItemSelected( item );
	}

	private void login( String username, String password )
	{
		String loginURL;

		RequestBody formBody;

		if ( prefs.getBoolean( "use_iphone_login", true ) )
		{
			Log.i( mTag, "Using iPhone login" );
			loginURL = "https://iphone.endoftheinter.net/";
			formBody = new FormEncodingBuilder()
					.add( "username", username )
					.add( "password", password )
					.build();
		}
		else
		{
			Log.i( mTag, "Using Desktop login" );
			loginURL = "https://endoftheinter.net/";
			formBody = new FormEncodingBuilder()
					.add( "b", username )
					.add( "p", password )
					.add( "r", "" )
					.build();
		}


		final Request request = new Request.Builder()
				.url( loginURL )
				.post( formBody )
				.build();


		try
		{
			Future<Response> responseFuture = Executors.newSingleThreadExecutor().submit( new Callable<Response>()
			{
				@Override
				public Response call() throws Exception
				{
					return client.newCall( request ).execute();
				}
			} );
			Response response = responseFuture.get( 20, TimeUnit.SECONDS );

			/*if ( response.body().string().equals( "" ) )
			{
				Log.e( mTag, "success!" );
			}
			else
			{
				Log.e( mTag, "failure ):" );
			}*/

			if ( response.body().string().equals( getString( R.string.successful_response ) ) )
			{
				if ( checkBox.isChecked() )
				{
					if ( usernames == null || passwords == null )
					{
						usernames = new ArrayList<>();
						passwords = new ArrayList<>();
					}
					for ( int i = 0; i < usernames.size(); i++ )
					{
						//If the series of accounts already contains the username entered then change the password
						if ( usernames.get( i ).equals( username ) )
						{
							passwords.set( i, password );
						}
					}
					if ( !usernames.contains( username ) )
					{
						usernames.add( username );
						passwords.add( password );
					}
					else
					{
						passwords.set( usernames.indexOf( username ), password );
					}

					String serializedUsernames = gson.toJson( usernames );
					String serializedPasswords = gson.toJson( passwords );

					prefs.edit()
							.putString( "usernames", serializedUsernames )
							.putString( "passwords", serializedPasswords )
							.apply();
				}

				int userid = -1;
				CookieManager cookieManager = (CookieManager) client.getCookieHandler();
				List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
				for ( HttpCookie cookie : cookies )
				{
					if ( cookie.getName().equals( "userid" ) )
					{
						userid = Integer.parseInt( cookie.getValue() );
						Log.v( mTag, "User ID is " + userid );
					}
				}

				//Launch the main activity
				Intent intent = new Intent( this, MainActivity.class );
				intent.putExtra( "username", username );
				intent.putExtra( "userId", userid );
				startActivity( intent );

			}
			else
			{
				//Log.e( mTag, response.body().string() );
				Log.e( mTag, "login failed" );
				Log.v( mTag, response.body().string() );
				alertFailure();
			}

		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	public void clicklogin( View view )
	{
		final String username = userNameEditText.getText().toString();
		final String password = passwordEditText.getText().toString();

		//Check for empty strings in the text boxes
		if ( !username.equals( "" ) && !password.equals( "" ) )
		{

			new Thread( new Runnable()
			{
				@Override
				public void run()
				{
					login( username, password );
				}
			} ).start();
		}
		else
		{
			Toast.makeText( getApplicationContext(), "Empty boxes", Toast.LENGTH_LONG ).show();
		}
	}

	private void alertFailure()
	{
		Looper.prepare();
		Toast.makeText( getApplicationContext(), "Login failed", Toast.LENGTH_LONG ).show();
	}

	@OnLongClick(R.id.username)
	public boolean showOptions()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		ListAdapter adapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_single_choice, usernames );
		builder.setAdapter( adapter, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick( DialogInterface dialogInterface, int i )
			{
				prefs.edit().putInt( "preferred_account", i ).apply();
				userNameEditText.setText( usernames.get( i ) );
				passwordEditText.setText( passwords.get( i ) );
			}
		} );
		AlertDialog dialog = builder.create();
		dialog.show();
		return false;
	}

}