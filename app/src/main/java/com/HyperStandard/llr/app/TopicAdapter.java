package com.HyperStandard.llr.app;

/**
 * Created by nonex_000 on 6/15/2014.
 */

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TopicAdapter extends ArrayAdapter<TopicLink> {
    private ArrayList<TopicLink> objects;

    public TopicAdapter(Context context, int textViewResourceId, ArrayList<TopicLink> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_topic_row, null);
        }

		/*
         * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        TopicLink i = objects.get(position);

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
                title.setText(i.getTopicTitle());
            }

            if (tc != null) {
                tc.setText(i.getUsername());
            }

            if (blackTags != null) {
                redTags.setText("");
                blackTags.setText("");
                for (int j = 0; j < i.getTags().length; j++) {
                    if (i.getTags()[j].equals("NWS")) {
                        if (redTags != null) {
                            String currentText = redTags.getText().toString();
                            redTags.setText(currentText + i.getTags()[j] + " ");
                        }
                    } else {
                        String currentText = blackTags.getText().toString();
                        blackTags.setText(currentText + " " + i.getTags()[j]);
                    }
                }
            }


            if (posts != null) {
                posts.setText(Integer.toString(i.getTotalMessages()));
            }

        }

        // the view must be returned to our activity
        return v;

    }

}
