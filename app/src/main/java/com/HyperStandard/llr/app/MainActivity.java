package com.HyperStandard.llr.app;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, TopicAdapter.adapterCallback, NavigationAdapter.NavigationDrawerCallback, PostAdapter.adapterCallback {

    //These are some static URLs for convenience
    private static final String MAIN_PAGE = "http://endoftheinter.net/main.php";
    public static Map<String, String> cookies;
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

    /**
     * stores the tag of the active fragment
     */
    private String currentFragmentTag = "";

    /**
     * store whether all the fragments are instantiated or not
     */
    private String[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragments = new String[]{"", "", "", "", ""};
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
                Log.e(mTag, "Interrupted operation");
            } catch (TimeoutException e) {
                Toast.makeText(this, "Operation timed out", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, String URL) {
        if (fragments[position].equals("")) {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getFragmentManager();
            //Fragment newFragment = fragmentManager.findFragmentByTag("MY_TAG" + position);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1), "MY_TAG" + position)
                    .commit();
            Log.e("committed", "replaced");
            fragments[position] = "MY_TAG" + position;
            currentFragmentTag = "MY_TAG" + position;
        } else if (fragments[position].equals(currentFragmentTag)) {
            Log.e("doing", "nothing");
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            Fragment tempFragment = fragmentManager.findFragmentByTag(currentFragmentTag);
            Log.e("detaching", "...");
            if (tempFragment != null) {
                fragmentManager.beginTransaction()
                        .remove(fragmentManager.findFragmentByTag(currentFragmentTag + position))
                        .add(TopicFragment.newInstance(URL), "MY_TAG" + position)
                        .addToBackStack(null)
                        .commit();
                Log.e("detached", "...");
            } else
                Log.e("null variable", "tempFragment");
            fragmentManager.executePendingTransactions();
            Fragment newFragment = fragmentManager.findFragmentByTag("MY_TAG" + position);
            if (newFragment == null) {
                newFragment = PlaceholderFragment.newInstance(position);
                Log.e("null variable", "newFragment");
            }
            fragmentManager.beginTransaction()
                    .attach(newFragment)
                    .commit();
            Log.e("currentFragment name", currentFragmentTag);
            fragments[position] = "MY_TAG" + position;
        }
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
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
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

    public void loadPage(View v) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Document> request = executor.submit(new LoadPage("http://boards.endoftheinter.net/topics/LUE", cookies));
        /*FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                //TODO figure out what the position variable does
                .replace(R.id.container, PlaceholderFragment.newInstance(3))
                .commit();*/
        try {
            Document page = request.get(5, TimeUnit.SECONDS);
            Elements elements = page.select("tr:has(td)");
            ArrayList<TopicLink> topics = new ArrayList<TopicLink>(elements.size());

            int latestPost = 0;
            //Strip the long text bc of long reasons
            fixTitle(page.title());
            for (Element e : elements) {
                topics.add(new TopicLink(e));
            }
            TopicAdapter adapter = new TopicAdapter(getBaseContext(), R.id.listview, topics);
            adapter.setCallback(this);
            ListView listview = (ListView) findViewById(R.id.listview);

            listview.setAdapter(adapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(getApplicationContext(), "Page load timed out sucka", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Log.e(mTag, "null pointer exception");
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e(mTag, "Exception!");
            e.printStackTrace();
        }
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
        mNavigationDrawerFragment.addItem(new BookmarkLink("Testing new drawer item", "test", "test"));
    }

    /**
     * @param view automatically generated variable from button press
     */
    public void loadTopic(View view) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Document> request = executor.submit(new LoadPage("http://boards.endoftheinter.net/showmessages.php?topic=8898015&page=1", cookies));
        try {
            Document page = request.get(5, TimeUnit.SECONDS);
            //Strip the long text bc of long reasons
            fixTitle(page.title());
            Elements elements = page.select("div.message-container");
            ArrayList<TopicPost> posts = new ArrayList<>(elements.size());
            for (Element e : elements)
                posts.add(new TopicPost(e));

            PostAdapter adapter = new PostAdapter(this, R.id.listview, posts);
            ListView listview = (ListView) findViewById(R.id.listview);
            listview.setAdapter(adapter);
        } catch (InterruptedException e) {
            Log.e(mTag, "Interrupted operation");
        } catch (TimeoutException e) {
            Toast.makeText(this, "Operation timed out", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a topic, based on the Topic ID
     *
     * @param topicId The integer ID of the topic to be loaded, fortunately LL only needs this, not things like tags etc
     */
    @Override
    public void topicPressed(int topicId, int pageNumber) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Document> request = executor.submit(new LoadPage("http://boards.endoftheinter.net/showmessages.php?topic="
                + topicId + "?page=" + pageNumber,
                cookies
        ));
        try {
            Document page = request.get(5, TimeUnit.SECONDS);

            //Strip the long text bc of long reasons
            fixTitle(page.title());

            Elements elements = page.select("div.message-container");
            ArrayList<TopicPost> posts = new ArrayList<>(elements.size());

            //Add post objects to arraylist, all the HTML processing is done within the object constructor itself
            for (Element e : elements)
                posts.add(new TopicPost(e));

            PostAdapter adapter = new PostAdapter(this, R.id.listview, posts);
            ListView listview = (ListView) findViewById(R.id.listview);
            //TODO move this out of the topic, or augment ith an additional control maybe?
            if (page.getElementById("nextpage") != null) {//Checks to see if there's more topics
                View footer = getLayoutInflater().inflate(R.layout.listview_post_footer, null);
                listview.addFooterView(footer, pageNumber + 1, true);

            }
            listview.setAdapter(adapter);
        } catch (InterruptedException e) {
            Log.e(mTag, "Interrupted operation");
        } catch (TimeoutException e) {
            Toast.makeText(this, "Operation timed out", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fixes the title for small devices
     *
     * TODO something different for tablets? Must acquire a tablet first
     */
    private void fixTitle(String title) {
        mTitle = "ETI - " + title.substring(title.indexOf(" - ") + 3);
        restoreActionBar();
    }

    public void changeLocation(String URL) {
        loadPage(null);
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

        private static int position;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            position = sectionNumber;
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (position == 3)
                return inflater.inflate(R.layout.fragment_main, container, false);
            else
                return inflater.inflate(R.layout.fragment_settings, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public static class TopicFragment extends Fragment {
        public TopicFragment() {
        }

        public static TopicFragment newInstance(String URL) {
            TopicFragment fragment = new TopicFragment();
            Bundle args = new Bundle();
            args.putString("URL", URL);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt("URL"));
        }
    }
}
