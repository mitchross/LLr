package com.HyperStandard.llr.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nonex_000 on 6/17/2014.
 */
public class NavigationAdapter extends ArrayAdapter<BookmarkLink> {
    private ArrayList<BookmarkLink> objects;

    NavigationAdapter(Context context, int textViewResourceId, ArrayList<BookmarkLink> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_navigation_row, null);
        }

		/*
         * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        BookmarkLink i = objects.get(position);

        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            /**
             * Actin label (String)
             */
            TextView title = (TextView) v.findViewById(R.id.mainText);

            if (title != null) {
                title.setText(i.getBookmarkName());
            }
        }

        // the view must be returned to our activity

        return v;

    }


}
