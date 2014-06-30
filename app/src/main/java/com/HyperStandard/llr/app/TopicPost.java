package com.HyperStandard.llr.app;

import android.util.Log;

import org.jsoup.nodes.Element;

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
    private int messageId;
    private int edits;
    private int userId;
    //consider field for topic ID? Not sure if necessary

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
                this.message = m.replace("<br />", "\r\n").substring(0, m.lastIndexOf("---"));
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
            Log.v("edits", ms.substring(ms.lastIndexOf(",") + 1, m.indexOf("@")));
            this.messageId = Integer.parseInt(ms.substring(ms.lastIndexOf(",") + 1, m.indexOf("@")));
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
