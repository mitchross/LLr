package com.HyperStandard.llr.app;

/**
 * @author HyperStandard
 * @since 6/24/2014
 */
public class TopicPost {

    //TODO add support for spoilers, images, links, and quotes
    private String username;
    private String time;
    private String message;
    private String signature;
    private int edits;
    private int userId;

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

    TopicPost (String username, String userId, String time, String message, String edits) {
        this.username = username;
        this.time = time;
        //Get the signature by looking for "---"
        //TODO handle posts without signature
        this.signature = message.substring((message.lastIndexOf("---") + 3));

        this.message = message.replace("<br />", "\r\n").substring(0, message.lastIndexOf("---"));

        //In theory this should extract the number of edits from the string passed and do a 0 if there was none
        try {
            this.edits = Integer.parseInt(edits.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            this.edits = 0;
        }

        //This should give the userID
        this.userId = Integer.parseInt(userId.substring(userId.lastIndexOf("=") + 1));
    }
}
