package com.HyperStandard.llr.app.Converters;

import com.HyperStandard.llr.app.Models.TopicPost;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * @author HyperStandard
 * @since 9/8/2014
 * Factory class to return arrays
 */
public class TopicArray
{
	public static ArrayList<TopicPost> newArrayFromShowMessages(Elements pageSection)
	{
		final String mTag = "(LLr) -> MoreMessages -> newArray";
		ArrayList<TopicPost> array = new ArrayList<>();
		Elements elements = pageSection.select( "div.message-container" );
		for ( Element e : elements )
		{
			//Get the username
			String usernameTemp;

			String userIdTemp = e.select( "div.message-top > a" ).attr( "href" );
			int userId = Integer.parseInt( userIdTemp.substring( userIdTemp.lastIndexOf( "=" ) + 1 ) );

			if ( userId < 0 )
			{
				usernameTemp = "Human #" + (-userId);
			}
			else
			{
				usernameTemp = e.select( "div.message-top > a" ).first().text();
			}

			//Get the message content
			String messageTemp = e.select( "table.message-body" ).text();

			//Signature
			String signatureTemp;

			if ( messageTemp.lastIndexOf( "---" ) == -1 )
			{ //If there's no signature belt
				messageTemp = messageTemp.replace( "<br />", "\r\n" );
				signatureTemp = "";
			}
			else
			{ //If there is a signature belt
				String br = System.getProperty( "line.separator" );

				//Get the signature
				signatureTemp = messageTemp.substring( ( messageTemp.lastIndexOf( "---" ) + 3 ) );

				//Slice the message so it doesn't have the signature
				messageTemp = messageTemp.replace( "<br />", br ).substring( 0, messageTemp.lastIndexOf( "---" ) );
			}

			//This gives a String in format [t|p],XXXXXXX,YYYYYYYY@Z X is the Topic ID, Y is the Message ID, and Z is the number of revisions
			//The "t" is for topic, private messages are "p" so deal w that later
			String messageDataTemp = e.select( "td.message" ).attr( "msgid" );

			array.add(
			new TopicPost(

					//Username
					usernameTemp,

					//Probably the Time ? ? ? TODO figure out time parsing
					e.select( "div.message-top:nth-child(3)" ).text(),

					messageTemp,

					signatureTemp,

					//Get the message ID int
					Integer.parseInt(messageDataTemp.substring( messageDataTemp.lastIndexOf( "," ) + 1, messageDataTemp.lastIndexOf( "@" ) ) ),

					//Get the number of edits
					Integer.parseInt( messageDataTemp.substring( messageDataTemp.lastIndexOf( "@" ) + 1 ) ),

					userId
			)
			);
		}
		return array;
	}

	public static ArrayList<TopicPost> newArrayFromMoreMessages()
	{
		return null;
	}
}
