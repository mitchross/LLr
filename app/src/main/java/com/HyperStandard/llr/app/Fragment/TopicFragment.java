package com.HyperStandard.llr.app.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.HyperStandard.llr.app.Data.C;
import com.HyperStandard.llr.app.Data.Cookies;
import com.HyperStandard.llr.app.LoadPage;
import com.HyperStandard.llr.app.R;
import com.HyperStandard.llr.app.Typefaces;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author HyperStandard
 * @since 7/6/2014
 */
public class TopicFragment extends Fragment
{
	private static final String mTag = "LLr -> (TF)";
	private Callbacks callbacks;
	private Context context;

	public TopicFragment()
	{

	}

	public static TopicFragment newInstance( String URL )
	{
		TopicFragment fragment = new TopicFragment();
		Bundle args = new Bundle();
		args.putString( "URL", URL );
		fragment.setArguments( args );
		return fragment;
	}

	public void setUp( Context context )
	{
		this.context = context;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.fragment_container, container, false );
		//ExecutorService executor = Executors.newSingleThreadExecutor();
		ExecutorService executor = Executors.newFixedThreadPool( 2 );
		Future<Document> request = executor.submit( new LoadPage( "http://boards.endoftheinter.net/showmessages.php?topic=" + getArguments().getString( "URL" ), Cookies.getCookies() ) );
		try
		{
			Document page = request.get( 10, TimeUnit.SECONDS );
			callbacks.sendTitle( page.title() );
			final Elements elements = page.select( "div.message-container" );

			Future<ArrayList<TopicPost>> arrayListFuture = executor.submit( new Callable<ArrayList<TopicPost>>()
			{
				@Override
				public ArrayList<TopicPost> call() throws Exception
				{
					ArrayList<TopicPost> array = new ArrayList<>( elements.size() );
					for ( Element e : elements )
					{
						array.add( new TopicPost( e ) );
					}
					return array;
				}
			} );
			ArrayList<TopicPost> posts = arrayListFuture.get();
			PostAdapter adapter = new PostAdapter( context, R.id.topic_listview, posts );
			ListView listview = (ListView) container.findViewById( R.id.topic_listview );
			listview.setAdapter( adapter );
		}
		catch ( InterruptedException e )
		{
			Log.e( mTag, "Interrupted operation" );
		}
		catch ( TimeoutException e )
		{
			Toast.makeText( context, "Operation timed out", Toast.LENGTH_LONG ).show();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		return v;
	}

	public void setCallbacks( Callbacks callbacks )
	{
		this.callbacks = callbacks;
	}

	public interface Callbacks
	{
		public void sendTitle( String title );
	}

	public class PostAdapter extends ArrayAdapter<TopicPost>
	{
		private ArrayList<TopicPost> objects;
		private Typeface typeface;

		public PostAdapter( Context context, int textViewResourceId, ArrayList<TopicPost> objects )
		{
			super( context, textViewResourceId, objects );
			typeface = Typefaces.getTypface( context, C.FONT_LISTVIEW );
			this.objects = objects;
		}


		public View getView( int position, View convertView, ViewGroup parent )
		{

			// assign the view we are converting to a local variable
			View v = convertView;


			if ( v == null )
			{
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				v = inflater.inflate( R.layout.listview_post_row, null );
			}

			TopicPost i = objects.get( position );

			if ( i != null )
			{


				/**
				 * Username (String)
				 */
				TextView username = (TextView) v.findViewById( R.id.post_username );

				/**
				 * Message body (String)
				 */
				TextView message = (TextView) v.findViewById( R.id.post_message );

				/**
				 * This holds the number of total posts as well as the optional latest post visited
				 */
				TextView signature = (TextView) v.findViewById( R.id.post_signature );

				if ( username != null )
				{
					username.setText( i.getUsername() + " | " + Integer.toString( i.getEdits() ) + " | " + Integer.toString( i.getUserId() ) );
				}

				if ( message != null )
				{
					message.setText( i.getMessage() );
				}

				if ( signature != null )
				{
					signature.setText( i.getSignature() );
				}
			}

			return v;


		}


	}

	public class TopicPost
	{

		//TODO add support for spoilers, images, links, and quotes
		private String username;
		private String time;
		private String message;
		private String signature;
		private int messageId;
		private int edits;
		private int userId;
		//consider field for topic ID? Not sure if that's necessary

		TopicPost( Element el )
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

		public int getMessageId()
		{
			return messageId;
		}

		public int getEdits()
		{
			return edits;
		}

		public int getUserId()
		{
			return userId;
		}

		public String getUsername()
		{
			return username;
		}

		public String getTime()
		{
			return time;
		}

		public String getMessage()
		{
			return message;
		}

		public String getSignature()
		{
			return signature;
		}
	}
}
