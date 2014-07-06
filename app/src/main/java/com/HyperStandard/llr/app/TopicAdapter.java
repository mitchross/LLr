package com.HyperStandard.llr.app;

/**
 * Created by nonex_000 on 6/15/2014.
 */

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TopicAdapter extends ArrayAdapter<TopicLink> {
    private ArrayList<TopicLink> objects;
    private adapterCallback callback;
    private static Typeface RobotoLight;

    public TopicAdapter(Context context, int textViewResourceId, ArrayList<TopicLink> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        RobotoLight = Typeface.createFromAsset(context.getAssets(), C.FONT_LISTVIEW);
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
                title.setTypeface(RobotoLight);
                title.setText(i.getTopicTitle());
            }

            if (tc != null) {
                tc.setTypeface(RobotoLight);
                tc.setText(i.getUsername());
            }

            if (blackTags != null) {
                redTags.setTypeface(RobotoLight);
                blackTags.setTypeface(RobotoLight);
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
                posts.setTypeface(RobotoLight);
                posts.setText(Integer.toString(i.getTotalMessages()));
                if (i.getLastRead() > 0) {
                    posts.setText(" (" + i.getLastRead() +") ");
                }
            }

        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("yo", "callback pressed");
                //Toast.makeText(getContext(), "topic is " + Integer.toString(i.getTopicId()), Toast.LENGTH_SHORT).show();
                if (callback != null) {
                    Log.e("callback not null", "yaya");
                    callback.topicPressed(i.getTopicId(), 1, view);
                }
            }


        });
        // the view must be returned to our activity
        return v;
    }
    public void setCallback (adapterCallback callback) {
        this.callback = callback;
    }
    public interface adapterCallback {
        public void topicPressed(int topicId, int pageNumber, View container);
    }


}
