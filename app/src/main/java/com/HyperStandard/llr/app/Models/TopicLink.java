package com.HyperStandard.llr.app.Models;

import android.util.Log;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by mitch on 7/7/14.
 */
public class TopicLink
{
	/**
	 * Stores the ID number used for the topic, needed for URLs
	 */
	public int topicId;

	/**
	 * Array containing all the tags a topic has
	 */
	public String[] tags;

	/**
	 * User's ID number, mostly used for opening user info pages
	 */
	public int userId;

	/**
	 * User's name, self explanatory why is this a comment
	 */
	public String username;

	/**
	 * Total messages the topic has (unread + read)
	 */
	public int totalMessages = 0;

	/**
	 * The actual title used in the topic
	 */
	public String topicTitle;

	/**
	 * The second number(if applicable) which is the parenthetical amount of messages as of yet unread
	 */
	public int unreadMessages = 0;

	private boolean isAnonymous = false;


	public TopicLink( Element e )
	{
		//Container for tags, if they exist
		Elements el = e.select( "div.fr > a" );

		//Get the tags
		if ( el.isEmpty() )
		{
			tags = new String[]{ "" };
		}
		else
		{
			tags = new String[ el.size() ];
			for ( int i = 0; i < tags.length; i++ )
			{
				String t = el.get( i ).text();

				//This enable better name and userId handling, since anonymous topics are the only ones with
				//no <a> element with a username/Id. This can skip that whole issue.
				if ( t.equals( "Anonymous" ) )
				{
					isAnonymous = true;
				}

				tags[ i ] = t;
			}
		}

		try
		{
			//Get the Topic ID number
			String id = e.select( "a" ).first().attr( "href" );
			topicId = Integer.parseInt( id.substring( id.lastIndexOf( "=" ) + 1 ) );
		}
		catch ( NumberFormatException e1 )
		{
			topicId = -1;
			e1.printStackTrace();
		}

		try
		{
			//Topic title should be same as topic ID
			topicTitle = e.select( "a" ).first().text();
		}
		catch ( Exception e1 )
		{
			topicTitle = "Error";
			e1.printStackTrace();
		}
//TODO THIS IS CRASHING FOR ME
		try
		{
			if ( !isAnonymous )
			{
				//Check if it's an anonymous topic
				String un = e.select( "td > a" ).first().attr( "href" );

				userId = Integer.parseInt( un.substring( un.lastIndexOf( "=" ) + 1 ) );

				//Same as the user except get the inner text (username)
				username = e.select( "td > a" ).first().text();
			}
			else
			{
				username = "Human #1";
				userId = -1;
			}

		}
		catch ( NullPointerException e1 )
		{
			username = "ERROR PARSING NAME";
			userId = -2;
			e1.printStackTrace();
		}

		if ( e.select( "td:has(span)" ).html().equals( "" ) )
		{
			totalMessages = Integer.parseInt( e.select( "td:nth-child(3)" ).first().ownText() );
		}
		else
		{
			//This gets the unread messages
			unreadMessages = Integer.parseInt( e.select( "td:has(span)" ).select( "a" ).html().replaceAll( "[^0-9]", "" ) , 10  );

			//This gets the total messages
			totalMessages = Integer.parseInt( e.select( "td:has(span)" ).first().ownText() );

		}

	}
}
