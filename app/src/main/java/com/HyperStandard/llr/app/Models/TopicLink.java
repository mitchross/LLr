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
	 * The first number (i.e. number user has read)
	 */
	public int readMessages = 0;

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
		totalMessages = Integer.parseInt( e.select( "td:nth-child(3)" ).first().ownText() );

		if ( e.select( "td:has(span)" ) == null )
		{
			Log.e( "no span", "Td hasn't had span" );
			//totalMessages =
		}
		else
		{
			//readMessages = Integer.parseInt( e.select( "td:has(spawn" ).first().ownText() );
			Log.e( "span found", Integer.toString( readMessages ) );
			//unreadMessages = Integer.parseInt( e.select( "td:has(span)" ).select( "span" ).text().replaceAll( "[^0-9]", "" ) );
			Log.e( "spawn read",  e.select( "td:has(span)" ).select( "span" ).html() );
		}
		/*//get teh amount of unread messages
		if ( e.select( "td:has(span)" ) == null || e.select( "td:has(span)" ).text().equals( "" ) )
		{
			readMessages = -1;
			//readMessages = Integer.parseInt(e.select("td:has(span)").first().text().replaceAll("[^0-9]", ""));
		}
		else
		{//negative one implies there's no extra messages
			readMessages = Integer.parseInt(e.select("td:has(span)").text().replaceAll("[^0-9]", ""));
			//readMessages = 20;
			Log.v( "test", Integer.toString( readMessages ) );
		}*/
	}
}
