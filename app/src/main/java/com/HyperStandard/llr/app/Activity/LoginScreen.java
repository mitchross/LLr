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

import com.HyperStandard.llr.app.Cache;
import com.HyperStandard.llr.app.CustomCookieManager;
import com.HyperStandard.llr.app.Data.Cookies;
import com.HyperStandard.llr.app.Login;
import com.HyperStandard.llr.app.R;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.HyperStandard.llr.app.R.id.container;
import static com.HyperStandard.llr.app.R.id.loginspinner;

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

	@InjectView( R.id.username )
	protected EditText userNameEditText;
	@InjectView( R.id.password )
	protected EditText passwordEditText;
	@InjectView( R.id.progressBar )
	protected ProgressBar progressBar;
	@InjectView( R.id.login_checkbox )
	protected CheckBox checkBox;

	private String username;
	private String password;

	private SharedPreferences prefs;
	private OkHttpClient client;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.login );

		ButterKnife.inject( this );

		//Get the general preferences for the application (same as the ones used by the pref activity)
		prefs = getSharedPreferences( getString( R.string.preferences_name ), MODE_PRIVATE );

		//Get values from preferences

		//Usernames and passwords are stored in a single string then split by the ✓ token
		//Because why not (I don't think it's valid for an LL name so it's prob no big deal)
		String[] usernames = prefs.getString( "usernames", "" ).split( "✓" );
		String[] passwords = prefs.getString( "passwords", "" ).split( "✓" );

		//Index for which account is preferred
		int preferred_account = prefs.getInt( "preferred_account", 0 );

		//Whether to log in automatically
		boolean autoLogin = prefs.getBoolean( "auto_login", false );

		//some sanity checks probably not necessary but still why not
		if ( ( usernames.length != 0 && passwords.length != 0 ) && ( passwords.length == usernames.length ) )
		{
			if ( preferred_account < usernames.length )
			{
				username = usernames[ preferred_account ];
				password = passwords[ preferred_account ];
			}
		}
		if ( autoLogin )
		{
			login( username, password );
		}

		//Set the client and default cookies
		//TODO make a better cookie store
		client = Cache.get.Client();
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
					.add( "username", "almond" )
					.add( "password", "XeGa2u_$" )
					.build();
		}
		else
		{
			Log.i( mTag, "Using Desktop login" );
			loginURL = "https://endoftheinter.net/";
			formBody = new FormEncodingBuilder()
					.add( "b", "almond" )
					.add( "p", "XeGa2u_$" )
					//todo figure out if this is necessary
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
			Response response = responseFuture.get();
			if ( response.body().string().equals( getString( R.string.successful_response ) ) )
			{
				if ( checkBox.isChecked() )
				{//TODO use GSON instead
					List<String> un = Arrays.asList( prefs.getString( "usernames", "" ).split( "✓" ) );
					List<String> pw = Arrays.asList( prefs.getString( "passwords", "" ).split( "✓" ) );
					for ( int i = 0; i < un.size(); i++ )
					{
						//If the series of accounts already contains the username entered then change the password
						if ( un.get( i ).equals( username ) )
						{
							pw.set( i, password );
						}
					}
					if ( !un.contains( username ) )
					{
						un.add( username );
						pw.add( password );
					}
					else
					{
						pw.set( un.indexOf( username ), password );
					}

					String uns = StringUtils.join( un.toArray(), "✓" );
					String pws = StringUtils.join( pw.toArray(), "✓" );

					prefs.edit()
							.putString( "usernames", uns )
							.putString( "passwords", pws )
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
				//int userId = Integer.parseInt( ((CookieManager) client.getCookieHandler()).getCookieStore(). ) ;
				//Log.e( "int value", " " + userId );

				//Launch the main activity
				Intent intent = new Intent( this, MainActivity.class );
				intent.putExtra( "userId", userid );
				startActivity( intent );
			}
			else
			{
				Toast.makeText( this, "Login failed", Toast.LENGTH_LONG ).show();
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

			new Thread(new Runnable() {
				@Override
				public void run()
				{
					login( username, password );
				}
			}).start();
		}
		else
		{
			Toast.makeText( getApplicationContext(), "Empty boxes", Toast.LENGTH_LONG ).show();
		}
	}

}