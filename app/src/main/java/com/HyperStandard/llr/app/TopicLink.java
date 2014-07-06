package com.HyperStandard.llr.app;

import android.util.Log;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;

/**
 * Object to hold references to a topic (tags, tc, name, and link, # messages, last post time)
 *
 * @author HyperStandard
 * @since 6/14/2014
 */
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
