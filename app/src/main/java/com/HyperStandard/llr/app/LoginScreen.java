package com.HyperStandard.llr.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Entry point to application, with login
 *
 * @author HyperStandard
 * @version 0.1
 * @since 6/14/2014.
 */
public class LoginScreen extends Activity {
    private final static String mTag = "debug loginscreen";
    private Typeface Comic;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //Get the shared prefs that the username is going to be put in
        //TODO change this to possibly encrypted login info, and support for multiple accounts
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //TODO figure out how to get external IP and use LL check login instead of just logging in again
        if (prefs.contains(C.PREFS_PASSWORD) && prefs.contains(C.PREFS_USERNAME) && prefs.getBoolean(C.PREFS_USELOGIN, false)) {
            Toast.makeText(this, "Logging in with saved credentials", Toast.LENGTH_SHORT).show();
            autoLogin(prefs.getString(C.PREFS_USERNAME, ""), prefs.getString(C.PREFS_PASSWORD, ""));
        }
        Comic = Typeface.createFromAsset(getAssets(), C.FONT_COMICRELIEF);
        EditText uN = (EditText) findViewById(R.id.username);
        EditText pW = (EditText) findViewById(R.id.password);

        uN.setTypeface(Comic);
        pW.setTypeface(Comic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.login, menu);
        Spinner s = (Spinner) menu.findItem(R.id.loginspinner).getActionView(); // find the spinner
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this.getActionBar().getThemedContext(),
                R.array.debug, android.R.layout.simple_spinner_dropdown_item); //  create the adapter from a StringArray
        s.setAdapter(mSpinnerAdapter); // set the adapter
        return true;
    }

    public void login(View view) {
        EditText uN = (EditText) findViewById(R.id.username);
        EditText pW = (EditText) findViewById(R.id.password);

        uN.setTypeface(Comic);
        pW.setTypeface(Comic);
        String username = uN.getText().toString();
        final String password = pW.getText().toString();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Connection.Response> loggedin = executor.submit(new Login(username, password));
        try {
            Connection.Response response = loggedin.get(30, TimeUnit.SECONDS);

            //Check to see if we've got logged in correctly, and if so, set up the account.
            if (response.body().equals("<script>document.location.href=\"/\";</script>")) {
                final Intent intent = new Intent(this, MainActivity.class);

                //Pass the cookies from the login page to the Main activity, where they get turned back into a map
                String[] cookies = new String[3];
                cookies[0] = response.cookie("userid");
                cookies[1] = response.cookie("PHPSESSID");
                cookies[2] = response.cookie("session");
                intent.putExtra("Cookies", cookies);
                CheckBox checkBox = (CheckBox) findViewById(R.id.login_checkbox);
                if (checkBox.isChecked()) {
                    prefs.edit()
                            .putString(C.PREFS_PASSWORD, password)
                            .putString(C.PREFS_USERNAME, username)
                            .putBoolean(C.PREFS_USELOGIN, true)
                            .commit();
                }
                startActivity(intent);
                //Check to see if user wants to keep logged in info and use it
                /*if (!prefs.getBoolean(C.PREFS_USELOGIN, false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Would you like to use this login info in the future?")
                            .setPositiveButton("Yes, I trust you not to phish", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    prefs.edit().putString(C.PREFS_PASSWORD, password).commit();
                                    prefs.edit().putBoolean(C.PREFS_USELOGIN, true).commit();
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("What am I, a chump? Nah.", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    prefs.edit().putBoolean(C.PREFS_USELOGIN, false).commit();
                                    startActivity(intent);
                                }
                            })
                            .create().show();

                } else {
                    startActivity(intent);
                }*/
            } else {
                Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show();
            }

        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

    public void autoLogin(String username, String password) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Connection.Response> loggedin = executor.submit(new Login(username, password));
        try {
            Connection.Response response = loggedin.get(15, TimeUnit.SECONDS);

            //Check to see if we've got logged in correctly, and if so, set up the account.
            if (response.body().equals("<script>document.location.href=\"/\";</script>")) {
                Log.v(mTag, response.body());

                final Intent intent = new Intent(this, MainActivity.class);
                //Pass the cookies from the login page to the Main activity, where they get turned back into a map
                String[] cookies = new String[3];
                cookies[0] = response.cookie("userid");
                cookies[1] = response.cookie("PHPSESSID");
                cookies[2] = response.cookie("session");
                intent.putExtra("Cookies", cookies);
                startActivity(intent);
            }


        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

    /**
     * This clears any saved login information
     * TODO selective delete based on different accounts? Also, actually implement eheh heh
     */
    public void clearData(MenuItem item) {
        Toast.makeText(this, "buh", Toast.LENGTH_SHORT).show();
    }

    /**
     * Emulate the overflow button
     * I could make it invisible w hardware menu buttons but eh only Samsung (more like samshit lol amirite) still uses those tbh
     *
     * @param button
     */
    public void openOptions(View button) {

        PopupMenu popup = new PopupMenu(this, button);
        popup.getMenuInflater().inflate(R.menu.login, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                item.collapseActionView();
                return true;
            }
        });

        popup.show();
    }

}
