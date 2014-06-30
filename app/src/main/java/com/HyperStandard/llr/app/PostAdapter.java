package com.HyperStandard.llr.app;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author HyperStandard
 * @since 6/24/2014
 */
public class PostAdapter extends ArrayAdapter<TopicPost> {
    private ArrayList<TopicPost> objects;
    private adapterCallback callback;
    private static Typeface typeface;

    public PostAdapter(Context context, int textViewResourceId, ArrayList<TopicPost> objects) {
        super(context, textViewResourceId, objects);
        typeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(), C.FONT_LISTVIEW);
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
            v = inflater.inflate(R.layout.listview_post_row, null);
        }

		/*
         * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        TopicPost i = objects.get(position);

        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            /**
             * Username (String)
             */
            TextView username = (TextView) v.findViewById(R.id.post_username);

            /**
             * Message body (String)
             */
            TextView message = (TextView) v.findViewById(R.id.post_message);

            /**
             * This holds the number of total posts as well as the optional latest post visited
             */
            TextView signature = (TextView) v.findViewById(R.id.post_signature);


            // check to see if each individual textview is null.
            // if not, assign some text!
            if (username != null) {
                username.setText(i.getUsername() + " | " + Integer.toString(i.getEdits()) + " | " + Integer.toString(i.getUserId()));
            }

            if (message != null) {
                message.setText(i.getMessage());
            }

            if (signature != null) {
                signature.setText(i.getSignature());
            }
        }


        // the view must be returned to our activity
        return v;



    }
    public void setCallback (adapterCallback callback) {
        this.callback = callback;
    }
    public interface adapterCallback {
        public void topicPressed(int topicId, int pageNumber);
    }
}
