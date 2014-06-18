package com.HyperStandard.llr.app;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

/**
 * Object to hold references to a topic (tags, tc, name, and link, # messages, last post time)
 *
 * @author HyperStandard
 * @since 6/14/2014
 */
public class TopicLink {
    private static final String format = "D/M/YYYY HH:mm";

    private String[] tags;
    private int TopicID;
    private int UserID;
    private String Username;
    private SimpleDateFormat lastPost;
    private int totalMessages;
    private String TopicTitle;

    public int getLastRead() {
        return lastRead;
    }

    public String[] getTags() {
        return tags;
    }

    public int getTopicID() {
        return TopicID;
    }

    public int getUserID() {
        return UserID;
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

    private int lastRead;

    /**
     *
     * @param tags
     * @param TopicID
     * @param UserID
     * @param TotalMessages
     * @param lastRead
     * @param Username
     * @param TopicTitle
     * @param lastPost pass null if this doesn't exist
     */
    TopicLink(String[] tags, int TopicID, int UserID, int TotalMessages, int lastRead, String Username, String TopicTitle, String lastPost) {
        //this.lastPost = lastPost;
        this.lastRead = lastRead;
        this.TopicID = TopicID;
        this.UserID = UserID;
        this.TopicTitle = TopicTitle;
        this.Username = Username;
        this.totalMessages = TotalMessages;
        //Clone cast is fastest I think who cares
        this.tags = tags.clone();
    }


}
