package com.HyperStandard.llr.app;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author HyperStandard
 * @since /30/2014
 */
public class TopicViewFragment extends Fragment {
    private static final String mTag = "LLr-> (TVF)";
    private Callbacks callback;
    private Context context;

    public TopicViewFragment() {

    }

    public static TopicViewFragment newInstance(int position, String URL) {
        TopicViewFragment fragment = new TopicViewFragment();
        Bundle args = new Bundle();
        args.putString("URL", URL);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public void setUp(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_container, container, false);
        ListView listView = (ListView) v.findViewById(R.id.topic_listview);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Document> request = executor.submit(new LoadPage(C.LL_LUE, MainActivity.cookies));
        try {
            Document page = request.get(5, TimeUnit.SECONDS);
            final Elements elements = page.select("tr:has(td)");
            Future<ArrayList<TopicLink>> arrayListFuture= executor.submit(new Callable<ArrayList<TopicLink>>() {
                @Override
                public ArrayList<TopicLink> call() throws Exception {
                    ArrayList<TopicLink> array = new ArrayList<>(elements.size());
                    for (Element e : elements) {
                        array.add(new TopicLink(e));
                    }
                    return array;
                }
            });
            ArrayList<TopicLink> topics = arrayListFuture.get();
            TopicAdapter adapter = new TopicAdapter(container.getContext(), R.id.topic_listview, topics);
            listView.setAdapter(adapter);
        } catch (InterruptedException e) {
            Log.e("list fragment", "Interrupted operation");
        } catch (TimeoutException e) {
            Toast.makeText(container.getContext(), "Operation timed out", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;

    }

    public void setCallback(Callbacks callback) {
        this.callback = callback;
    }

    public interface Callbacks {
        public void sendTitle(String title);
        public void loadTopic(String URL);
    }

    public class TopicLink {
        private int topicId;
        private String[] tags;
        private int userId;
        private String username;
        private int totalMessages;
        private String topicTitle;
        private int lastRead;


        TopicLink(Element e) {
            Elements el = e.select("div.fr > a");

            //Get the tags
            if (el.isEmpty()) {
                tags = new String[]{""};
            } else {
                tags = new String[el.size()];
                for (int i = 0; i < tags.length; i++) {
                    tags[i] = el.get(i).text();
                }
            }

            try {
                //Get the Topic ID number
                String id = e.select("a").first().attr("href");
                topicId = Integer.parseInt(id.substring(id.lastIndexOf("=") + 1));
            } catch (NumberFormatException e1) {
                topicId = -1;
                e1.printStackTrace();
            }

            try {
                //Topic title should be same as topic ID
                topicTitle = e.select("a").first().text();
            } catch (Exception e1) {
                topicTitle = "Error";
                e1.printStackTrace();
            }

            try {
                //Check if it's an anonymous topic
                String un = e.select("td > a").first().attr("href");
                if (un == null || un.equals("")) {
                    username = "Human";
                    userId = -1;
                } else {//If there's a username
                    userId = Integer.parseInt(un.substring(un.lastIndexOf("=") + 1));

                    //Same as the user except get the inner text (username)
                    username = e.select("td > a").first().text();
                }
            } catch (NullPointerException e1) {
                username = "ERROR PARSING NAME";
                userId = -2;
                e1.printStackTrace();
            }
            totalMessages = Integer.parseInt(e.select("td:nth-child(3)").first().ownText());

            //get teh amount of unread messages
            if (e.select("td:has(span)") == null || e.select("td:has(span)").text().equals("")) {
                lastRead = -1;
                //lastRead = Integer.parseInt(e.select("td:has(span)").first().text().replaceAll("[^0-9]", ""));
            } else {//negative one implies there's no extra messages
                //lastRead = Integer.parseInt(e.select("td:has(span)").text().replaceAll("[^0-9]", ""));
                lastRead = 20;
                Log.v("test", Integer.toString(lastRead));
            }
            //lastRead = 20;


        }

        public int getLastRead() {
            return lastRead;
        }

        public String[] getTags() {
            return tags;
        }

        public final int getTopicId() {
            return topicId;
        }

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getTopicTitle() {
            return topicTitle;
        }

        public int getTotalMessages() {
            return totalMessages;
        }


    }

    public class TopicAdapter extends ArrayAdapter<TopicLink> {
        private ArrayList<TopicLink> objects;
        private Typeface typeface;

        public TopicAdapter(Context context, int textViewResourceId, ArrayList<TopicLink> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            typeface = Typefaces.getTypface(context, C.FONT_LISTVIEW);
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.listview_topic_row, null);
            }

            final TopicLink i = objects.get(position);

            if (i != null) {

                // This is how you obtain a reference to the TextViews.
                // These TextViews are created in the XML files we defined.

                /**
                 * Topic title (String)
                 */
                TextView title = (TextView) v.findViewById(R.id.topicTitle);

                /**
                 * Topic creator (String)
                 */
                TextView tc = (TextView) v.findViewById(R.id.topicCreator);

                /**
                 * This holds all NON red tags (atm just gonna do spoilers + NWS as red
                 */
                TextView blackTags = (TextView) v.findViewById(R.id.topicTags);

                /**
                 * This holds all flagged for red tags
                 * TODO: some kinda external data structure to hold custom tags
                 */
                TextView redTags = (TextView) v.findViewById(R.id.redTags);

                /**
                 * This holds the number of total posts as well as the optional latest post visited
                 */
                TextView posts = (TextView) v.findViewById(R.id.topicPosts);


                // check to see if each individual textview is null.
                // if not, assign some text!
                if (title != null) {
                    title.setTypeface(typeface);
                    title.setText(i.getTopicTitle());
                }

                if (tc != null) {
                    tc.setTypeface(typeface);
                    tc.setText(i.getUsername());
                }

                if (blackTags != null) {
                    redTags.setTypeface(typeface);
                    blackTags.setTypeface(typeface);
                    redTags.setText("");
                    blackTags.setText("");
                    for (int j = 0; j < i.getTags().length; j++) {
                        //TODO clean this stuff up
                        if (i.getTags()[j].equals("NWS") || i.getTags()[j].equals("Spoiler")) {
                            if (redTags != null) {
                                String currentText = redTags.getText().toString();
                                //The space goes after the individual tags
                                redTags.setText(currentText + i.getTags()[j] + " ");
                            }
                        } else {
                            String currentText = blackTags.getText().toString();
                            //The space goes after the individual tags
                            blackTags.setText(currentText + i.getTags()[j] + " ");
                        }
                    }
                }


                if (posts != null) {
                    posts.setTypeface(typeface);
                    posts.setText(Integer.toString(i.getTotalMessages()));
                    if (i.getLastRead() > 0) {
                        posts.setText(" (" + i.getLastRead() + ") ");
                    }
                }

            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.loadTopic(Integer.toString(i.getTopicId()));
                }
            });
            return v;
        }
    }
}


