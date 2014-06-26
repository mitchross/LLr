package com.HyperStandard.llr.app;

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
    private static final String format = "D/M/YYYY HH:mm";
    private int topicId;
    private String[] tags;
    private int userId;
    private String Username;
    private SimpleDateFormat lastPost;
    private int totalMessages;
    private String TopicTitle;
    private int lastRead;

    /**
     * @param tags
     * @param TopicID
     * @param UserID
     * @param TotalMessages
     * @param lastRead
     * @param Username
     * @param TopicTitle
     * @param lastPost      pass null if this doesn't exist
     */
    TopicLink(String[] tags, int TopicID, int UserID, int TotalMessages, int lastRead, String Username, String TopicTitle, String lastPost) {
        //this.lastPost = lastPost;
        this.lastRead = lastRead;
        this.topicId = TopicID;
        this.userId = UserID;
        this.TopicTitle = TopicTitle;
        this.Username = Username;
        this.totalMessages = TotalMessages;
        //Clone cast is fastest I think who cares
        this.tags = tags.clone();
    }

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

        //Get the Topic ID number
        String id = e.select("a").first().attr("href");
        topicId = Integer.parseInt(id.substring(id.lastIndexOf("=") + 1));

        //Get the User ID number
        String un = e.select("td > a").first().attr("href");
        userId = Integer.parseInt(un.substring(un.lastIndexOf("=") + 1));

        //Same as the user except get the inner text (username)
        /*e.select("td > a").text(),

                //THe third TD element contains the number of messages in a post
                Integer.parseInt(e.select("td:nth-child(3)").first().ownText()),
                //0,
                //TODO fix this shit too
                latestPost,



                //Topic title should be same as topic ID
                e.select("a").first().text(),

                //TODO: get the date right ugh
                "today"
        )*/
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
        return Username;
    }

    public String getTopicTitle() {
        return TopicTitle;
    }

    public SimpleDateFormat getLastPost() {
        return lastPost;
    }

    public int getTotalMessages() {
        return totalMessages;
    }


}
