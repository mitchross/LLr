package com.HyperStandard.llr.app;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    //These are some static URLs for convenience
    private static final String TOPICS_MOMENT = "http://iphone.endoftheinter.net/#___2__";
    private static final String MAIN_PAGE = "http://endoftheinter.net/main.php";
    private static final String CHECK_IP = "https://boards.endoftheinter.net/scripts/login.php?username=&ip=";
    private static String mTag = "debug";
    //Cookies
    public int UserID;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private boolean infoSaved = false;
    private String PHPSession;
    private String Session;
    public static Map<String, String> cookies;
    private Document currentPage;
    private Document lastPage;
    //Just going to have a single duplicate of the bookmarks in the Main thread to keep from
    //casting/calling a getArraylist type method a lot
    private ArrayList<BookmarkLink> bookmarks;

    public int getUserID() {
        return UserID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cookies = new HashMap<String, String>();
        cookies.put("userid", getIntent().getStringArrayExtra("Cookies")[0]);
        cookies.put("PHPSESSID", getIntent().getStringArrayExtra("Cookies")[1]);
        cookies.put("session", getIntent().getStringArrayExtra("Cookies")[2]);
        UserID = Integer.parseInt(cookies.get("userid"));

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        ListView mListView = (ListView) findViewById(R.id.leftNavigationDrawer);

        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                UserID);

        Log.e("cookies", cookies.get("session"));
        if (true) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Document> loader = executor.submit(new LoadPage(MAIN_PAGE, cookies));
            try {
                Document main = loader.get(5, TimeUnit.SECONDS);
                Elements elements = main.select("#bookmarks > span");
                ArrayList<BookmarkLink> bookmarks = new ArrayList<BookmarkLink>(elements.size());

                /**
                 * Get the bookmarks, to populate the Navigation drawer with links
                 */
                for (Element e : elements) {
                    bookmarks.add(populateDrawer(new BookmarkLink(
                            e.select("span > a").first().ownText(),
                            e.select("span > a").attr("abs:href"),
                            "TOPIC_LIST"
                    )));

                    Log.e(e.select("span > a").first().ownText(), e.select("span > a").attr("abs:href"));
                }

            } catch (InterruptedException e) {

            } catch (ExecutionException e) {

            } catch (TimeoutException e) {

            }
            try {
                loadPageURL("http://boards.endoftheinter.net/topics/Posted");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.e("something", "something");
        // update the main content by replacing fragments
        /*FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
        Log.e(mTag, "starting to load page");
        try {
            loadPage(null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception thrown", "loadPage");
        }
        try {
            loadPageURL("http://boards.endoftheinter.net/topics/LUE");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception thrown", "LoadPageURL");
        }
        Log.e(mTag, Integer.toString(position));
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            //mNavigationDrawerFragment.get
            case 4:
                //loadPageURL("http://boards.endoftheinter.net/topics/Android");
                break;
            case 5:
                //loadPageURL("http://boards.endoftheinter.net/topics/LUE");
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private void setUpAccount() {
        try {
            Log.e(mTag, "one");
            Document mainPage = Jsoup.connect("http://endoftheinter.net/main.php").cookie("auth", "token").post();
            Log.e(mTag, "two");
            //Elements bookmarks = mainPage.select("div.bookmarks > span > a");
            Log.e(mTag, "three");
            /*if (bookmarks.isEmpty()) {
                Log.e(mTag, "Bookmarks empty");
            }
            Log.e("size", Integer.toString(bookmarks.size()));
            String[] urls = new String[bookmarks.size()];
            for (int i = 0; i < bookmarks.size(); i++) {
                urls[i] = bookmarks.get(i).text();
                Log.e(mTag, urls[i]);
            }*/
            //mainPage.getElementById("bookmarks").;
        } catch (MalformedURLException e) {
            Log.e(mTag, "MalformedURLException");
            e.printStackTrace();
        } catch (HttpStatusException e) {
            Log.e(mTag, "HttpStatusException");
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            Log.e(mTag, "UnsupportedOperationException");
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            Log.e(mTag, "SocketTimeoutException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(mTag, "FATALITY blah");
            e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e(mTag, "Other exception");
            e.printStackTrace();
            Log.e(mTag, "" + e.getCause());

        }

        //ListView navView = "";
    }

    public void loadPage(View v) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Document> request = executor.submit(new LoadPage("http://boards.endoftheinter.net/topics/LUE", cookies));
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                //TODO figure out what the position variable does
                .replace(R.id.container, PlaceholderFragment.newInstance(3))
                .commit();
        try {
            Document page = request.get(5, TimeUnit.SECONDS);
            Elements elements = page.select("tr:has(td)");
            currentPage = page;
            ArrayList<TopicLink> topics = new ArrayList<TopicLink>(elements.size());

            int latestPost = 0;
            mTitle = page.title();
            restoreActionBar();
            //String[] tags = {"NWS", "test"};
            for (Element e : elements) {
                Elements el = e.select("div.fr > a");
                String[] tags;
                if (el.isEmpty()) {
                     tags = new String[]{""};
                } else {

                    tags = new String[el.size()];
                    for (int i = 0; i < tags.length; i++) {
                        tags[i] = el.get(i).text();
                    }
                }
                topics.add(
                        new TopicLink(
                                //Gotta figure out how to get the tags
                                tags,

                                //Get the topic ID then strip the first 50 characters
                                Integer.parseInt(e.select("a").first().attr("href").substring(50)),

                                //The user link seems to be the only A element directly under a td
                                //Integer.parseInt(e.select("td > a").first().attr("href").substring(37)),
                                0,
                                //THe third TD element contains the number of messages in a post
                                Integer.parseInt(e.select("td:nth-child(3)").first().ownText()),
                                //0,
                                //TODO fix this shit too
                                latestPost,

                                //Same as the user except get the inner text (username)
                                e.select("td > a").text(),

                                //Topic title should be same as topic ID
                                e.select("a").first().text(),

                                //TODO: get the date right ugh
                                "today"
                        )
                );
            }
            TopicAdapter adapter = new TopicAdapter(this, R.id.listview, topics);
            ListView listview = (ListView) findViewById(R.id.listview);
            //Header isn't really needed because the actionbar contains the page title
            /*View header = View.inflate(this, R.layout.listview_header_row, null);
            listview.addHeaderView(header);*/
            //listview.setOnI
            listview.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(getApplicationContext(), "Page load timed out sucka", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(mTag, "Exception!");
            e.printStackTrace();
        }
    }

    public void loadPageURL(String URL) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Document> request = executor.submit(new LoadPage(URL, cookies));
        try {
            Document page = request.get(5, TimeUnit.SECONDS);
            Elements elements = page.select("tr:has(td)");
            //TextView tV = (TextView) findViewById(R.id.longText);
            //tV.setText(elements.first().text());
            currentPage = page;
            ArrayList<TopicLink> topics = new ArrayList<TopicLink>(elements.size());

            int latestPost = 0;
            mTitle = page.title();
            restoreActionBar();
            String[] tags = {"NWS", "test"};
            for (Element e : elements) {
                topics.add(
                        new TopicLink(
                                tags,
                                //Get the topic ID then strip the first 50 characters
                                //Integer.parseInt(e.select("a").first().attr("href").substring(50)),
                                50,

                                //The user link seems to be the only A element directly under a td
                                //Integer.parseInt(e.select("td > a").first().attr("href").substring(37)),
                                0,
                                //THe third TD element contains the number of messages in a post
                                //Integer.parseInt(e.select("td:nth-child(3)").first().ownText()),
                                0,
                                //TODO fix this shit too
                                latestPost,

                                //Same as the user except get the inner text (username)
                                e.select("td > a").text(),
                                //"llamaguy",
                                //Topic title should be same as topic ID
                                e.select("a").first().text(),
                                //"title",
                                //TODO: get the date right ugh
                                "today"
                        )
                );
            }
            TopicAdapter adapter = new TopicAdapter(this, R.id.listview, topics);
            ListView listview = (ListView) findViewById(R.id.listview);
            //Header isn't really needed because the actionbar contains the page title
            /*View header = View.inflate(this, R.layout.listview_header_row, null);
            listview.addHeaderView(header);*/
            listview.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(getApplicationContext(), "Page load timed out sucka", Toast.LENGTH_SHORT).show();
        }
    }

    private TopicLink parseForTopics(Element e) {
        int latestPost;
        if (e.select("span") != null) {
            latestPost = 1;
        } else
            latestPost = 0;
        String[] tags = new String[]{"NWS", "Fails"};
        return new TopicLink(
                tags,
                //Get the topic ID then strip the first 50 characters
                Integer.parseInt(e.select("a").first().attr("href").substring(50)),

                //The user link seems to be the only A element directly under a td
                Integer.parseInt(e.select("td > a").attr("href").substring(37)),

                //THe third TD element contains the number of messages in a post
                Integer.parseInt(e.select("td:nth-child(3)").text()),

                //TODO fix this shit too
                latestPost,

                //Same as the user except get the inner text (username)
                e.select("td > a").text(),

                //Topic title should be same as topic ID
                e.select("a").first().text(),

                //TODO: get the date right ugh
                "today"
        );
    }

    /**
     * Adds items to the navigation drawer (bookmarks)
     *
     * @param data the bookmark to be added
     */
    public BookmarkLink populateDrawer(BookmarkLink data) {
        mNavigationDrawerFragment.addItem(data);
        return data;
    }

    public void addDrawer(View v) {
        mNavigationDrawerFragment.addItem(new BookmarkLink("Fuck that police", "test", "test"));
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }



}
