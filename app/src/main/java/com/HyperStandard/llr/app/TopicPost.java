package com.HyperStandard.llr.app;

import java.util.Date;

/**
 * @author HyperStandard
 * @since 6/24/2014
 */
public class TopicPost {

    //TODO add support for spoilers, images, links, and quotes
    private String username;
    private Date time;
    private String message;
    private String signature;

    public String getUsername() {
        return username;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getSignature() {
        return signature;
    }

    TopicPost (String username, Date time, String message) {
        this.username = username;
        this.time = time;
        this.signature = message.substring((message.lastIndexOf("---") + 3));
        this.message = message.replace("<br />", "\n");
    }
}
