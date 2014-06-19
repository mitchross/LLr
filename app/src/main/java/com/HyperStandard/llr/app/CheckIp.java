package com.HyperStandard.llr.app;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.Callable;

/**
 * @author HyperStandard
 * @since 6/18/2014
 */
public class CheckIp implements Callable<Boolean> {
    private static final String mTag = "ip check debug";
    private String URL;

    CheckIp(String username, String Ip) {
        URL = "http://boards.endoftheinter.net/scripts/login.php?username="
                + username + "&ip=" + Ip;
        Log.e(mTag, Ip);
    }

    /**
     * Checks LL login
     * @return true if the user is logged in, false if not
     */
    public Boolean call() {
        try {
            /**
             * Use the LL IP check to see if user is logged in
             */
            Connection.Response res = Jsoup
                    .connect(URL)
                    .method(Connection.Method.GET)
                    .execute();
            Document page = res.parse();
            String test = page.text();
            Log.e(mTag, test);
            if (test.contains(":")) {
                Log.e("worked!", "Huzzah");
            } else {
                Log.e("Error", "bleh");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(mTag, "fatal error");

        }
        //return new Connection.Response();
        return null;

    }
}
