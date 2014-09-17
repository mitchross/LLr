package com.HyperStandard.llr.app.Models;

import android.util.Log;

import org.jsoup.nodes.Element;

/**
 * Created by mitch on 7/8/14.
 */
public class TopicPost
{

	//TODO add support for spoilers, images, links, and quotes
	public String username;
	public String time;
	public String message;
	public String signature;
	public int messageId;
	public int edits;
	public int userId;
	//consider field for topic ID? Not sure if that's necessary

	public TopicPost( String username, String time, String message, String signature, int messageId, int edits, int userId )
	{
		this.username = username;
		this.time = time;
		this.message = message;
		this.signature = signature;
		this.messageId = messageId;
		this.edits = edits;
		this.userId = userId;
	}


	public TopicPost( Element el )
	{
		try
		{
			//Get the username
			this.username = el.select( "div.message-top > a" ).first().text();


			//Get the userId
			String s = el.select( "div.message-top > a" ).attr( "href" );
			this.userId = Integer.parseInt( s.substring( s.lastIndexOf( "=" ) + 1 ) );


			//Get the time I guess whatever
			this.time = el.select( "div.message-top:nth-child(3)" ).text();
			Log.v( "time", this.time );

			//Get teh message body + signature
			String m = el.select( "table.message-body" ).text();

			//Convert to message body and signature, handling null signatures
			if ( m.lastIndexOf( "---" ) == -1 )
			{ //If there's no signature belt
				this.message = m.replace( "<br />", "\r\n" );
				this.signature = "";
			}
			else
			{ //If there is a signature belt
				String br = System.getProperty( "line.separator" );
				this.message = m.replace( "<br />", br ).substring( 0, m.lastIndexOf( "---" ) );
				this.signature = m.substring( ( m.lastIndexOf( "---" ) + 3 ) );
			}

			//In theory this should extract the number of edits from the string passed and do a 0 if there was none
			try
			{
				this.edits = Integer.parseInt( el.select( "div.message-top:nth-child(6)" ).text().replaceAll( "[^0-9]", "" ) );
			}
			catch ( NumberFormatException e )
			{
				this.edits = 0;
			}
			//If the userId is negative, then it's an anon topic and usernames need to change
			if ( this.userId < 0 )
			{
				this.username = "Human #" + ( -this.userId );
			}

			//This gives a String in format t,XXXXXXX,YYYYYYYY@Z X is the Topic ID, Y is the Message ID, and Z is the number of revisions
			//The "t" is for topic, private messages are "p" so deal w that later
			String ms = el.select( "td.message" ).attr( "msgid" );
			//Get the number of revisions
			this.edits = Integer.parseInt( ms.substring( ms.lastIndexOf( "@" ) + 1 ) );
			this.messageId = Integer.parseInt( ms.substring( ms.lastIndexOf( "," ) + 1, ms.lastIndexOf( "@" ) ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}