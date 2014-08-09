package com.HyperStandard.llr.app.Models;

import android.util.Log;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by mitch on 7/7/14.
 */
public class TopicLink
{
	//TODO I'm not sure if these should be public?
	public int topicId;
	public String[] tags;
	public int userId;
	public String username;
	public int totalMessages;
	public String topicTitle;
	public int lastRead;

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

		//get teh amount of unread messages
		if ( e.select( "td:has(span)" ) == null || e.select( "td:has(span)" ).text().equals( "" ) )
		{
			lastRead = -1;
			//lastRead = Integer.parseInt(e.select("td:has(span)").first().text().replaceAll("[^0-9]", ""));
		}
		else
		{//negative one implies there's no extra messages
			//lastRead = Integer.parseInt(e.select("td:has(span)").text().replaceAll("[^0-9]", ""));
			lastRead = 20;
			Log.v( "test", Integer.toString( lastRead ) );
		}
		//lastRead = 20;


	}


}
