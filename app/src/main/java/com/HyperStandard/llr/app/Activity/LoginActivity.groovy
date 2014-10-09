package com.HyperStandard.llr.app.Activity

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import com.HyperStandard.llr.app.R
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import groovy.transform.CompileStatic

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors;

@CompileStatic
public class LoginActiviy extends Activity
{
    private final static String mTag = "LLr -> (LoginScreen)";


    private SharedPreferences prefs;

    @Override
    protected void onCreate Bundle savedInstanceState
    {
        Log.e( mTag, getDatabasePath( "testMe" ).toString() );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.login );
        PreferenceManager.setDefaultValues( getApplicationContext(), "prefs", MODE_PRIVATE, R.xml.pref_general, false );

        getActionBar();

        //Get the shared prefs that the username is going to be put in
        //TODO change this to possibly encrypted login info, and support for multiple accounts
        prefs = getSharedPreferences( getString( R.string.pref_name ), MODE_PRIVATE );
        //TODO figure out how to get external IP and use LL check login instead of just logging in again
        if ( prefs.contains( getString( R.string.prefs_password ) ) && prefs.contains( getString( R.string.prefs_username ) ) && prefs.getBoolean( getString( R.string.prefs_login ), false ) )
        {
            progressBar.setVisibility( ProgressBar.VISIBLE );
            loginButton.setVisibility( View.INVISIBLE );
            Toast.makeText( this, "using saved credentials", Toast.LENGTH_LONG ).show();
            userNameEditText.setText( prefs.getString( getString( R.string.prefs_username ), "" ) );
            passwordEditText.setText( prefs.getString( getString( R.string.prefs_password ), "" ) );
            /*new Thread( new Runnable()
            {
                public void run()
                {
                    login();
                }
            } ).start();*///TODO remove these commetns
            //progressBar.setVisibility(ProgressBar.INVISIBLE);
            //loginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate R.menu.login, menu ;
        Spinner mSpinner = (Spinner) menu.findItem( loginspinner ).getActionView();//You need the getActionView thing I don't know why


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
            Log.e( mTag, "using iPhone login" );
            URLtoConnectTo = "https://iphone.endoftheinter.net/";
        }
        else
        {
            Log.v( mTag, "using desktop version" );
            URLtoConnectTo = "https://endoftheinter.net/";
        }
        OkHttpClient client = new OkHttpClient();
        Request request = Request.Builder()
                .url(URLtoConnectTo)
                .get();

    }

    /**
     * This clears any saved login information
     * TODO selective delete based on different accounts? Also, actually implement eheh heh
     */

    public void clearData( MenuItem item )
    {
        Toast.makeText( this, "buh", Toast.LENGTH_SHORT ).show();
    }


}