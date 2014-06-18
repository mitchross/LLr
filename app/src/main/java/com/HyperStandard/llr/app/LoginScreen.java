package com.HyperStandard.llr.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.login, menu);
        return true;
    }

    public void login(View view) {
        String[] info = new String[2];
        EditText uN = (EditText) findViewById(R.id.username);
        EditText pW = (EditText) findViewById(R.id.password);
        String username = uN.getText().toString();
        String password = pW.getText().toString();
        info[0] = username;
        info[1] = password;
        //Log.e(mTag, username);
        //Log.e(mTag, password);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Connection.Response> loggedin = executor.submit(new Login(username, password));
        try {
            Connection.Response response = loggedin.get(30, TimeUnit.SECONDS);
            //TextView outputbox = (TextView) findViewById(R.id.longoutput);
            //outputbox.setText(response.body());
            //Log.e(mTag, response);

            //Check to see if we've got logged in correctly, and if so, set up the account.
            if (response.body().equals("<script>document.location.href=\"/\";</script>")) {
                Intent intent = new Intent(this, MainActivity.class);

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
     * TODO selective delete based on different accounts? Also, actually implement eheh
     * @param item
     */
    public void clearData(MenuItem item) {
        return;
    }

}
