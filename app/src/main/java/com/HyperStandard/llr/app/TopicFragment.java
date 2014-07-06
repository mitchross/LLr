package com.HyperStandard.llr.app;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * @author HyperStandard
 * @since 7/6/2014
 */
public class TopicFragment extends Fragment {
    public class PostAdapter extends ArrayAdapter<com.HyperStandard.llr.app.TopicPost> {
        private ArrayList<com.HyperStandard.llr.app.TopicPost> objects;
        private adapterCallback callback;
        private static Typeface typeface;

        public PostAdapter(Context context, int textViewResourceId, ArrayList<com.HyperStandard.llr.app.TopicPost> objects) {
            super(context, textViewResourceId, objects);
            typeface = Typefaces.getTypface(context, C.FONT_LISTVIEW);
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
            com.HyperStandard.llr.app.TopicPost i = objects.get(position);

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
            public void topicPressed(int topicId, int pageNumber, View view);
        }
    }
    public class TopicPost {

        //TODO add support for spoilers, images, links, and quotes
        private String username;
        private String time;
        private String message;
        private String signature;
        private int messageId;
        private int edits;
        private int userId;
        //consider field for topic ID? Not sure if that's necessary

        TopicPost(Element el) {
            try {
                //Get the username
                this.username = el.select("div.message-top > a").first().text();


                //Get the userId
                String s = el.select("div.message-top > a").attr("href");
                this.userId = Integer.parseInt(s.substring(s.lastIndexOf("=") + 1));


                //Get the time I guess whatever
                this.time = el.select("div.message-top:nth-child(3)").text();
                Log.v("time", this.time);

                //Get teh message body + signature
                String m = el.select("table.message-body").text();

                //Convert to message body and signature, handling null signatures
                if (m.lastIndexOf("---") == -1) { //If there's no signature belt
                    this.message = m.replace("<br />", "\r\n");
                    this.signature = "";
                } else { //If there is a signature belt
                    String br = System.getProperty ("line.separator");
                    this.message = m.replace("<br />", br).substring(0, m.lastIndexOf("---"));
                    this.signature = m.substring((m.lastIndexOf("---") + 3));
                }

                //In theory this should extract the number of edits from the string passed and do a 0 if there was none
                try {
                    this.edits = Integer.parseInt(el.select("div.message-top:nth-child(6)").text().replaceAll("[^0-9]", ""));
                } catch (NumberFormatException e) {
                    this.edits = 0;
                }
                //If the userId is negative, then it's an anon topic and usernames need to change
                if (this.userId < 0) {
                    this.username = "Human #" + (-this.userId);
                }

                //This gives a String in format t,XXXXXXX,YYYYYYYY@Z X is the Topic ID, Y is the Message ID, and Z is the number of revisions
                //The "t" is for topic, private messages are "p" so deal w that later
                String ms = el.select("td.message").attr("msgid");
                //Get the number of revisions
                this.edits = Integer.parseInt(ms.substring(ms.lastIndexOf("@") + 1));
                this.messageId = Integer.parseInt(ms.substring(ms.lastIndexOf(",") + 1, ms.lastIndexOf("@")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int getMessageId() {
            return messageId;
        }

        public int getEdits() {
            return edits;
        }

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getTime() {
            return time;
        }

        public String getMessage() {
            return message;
        }

        public String getSignature() {
            return signature;
        }
    }
}
