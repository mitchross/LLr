package com.HyperStandard.llr.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.HyperStandard.llr.app.Models.BoardLink;
import com.HyperStandard.llr.app.Models.TopicLink;
import com.HyperStandard.llr.app.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author HyperStandard
 * @since 8/14/2014.
 */
public class MainPageAdapter extends ArrayAdapter<Object>
{
	private Context mContext;
	private List<Object> objects;

	public MainPageAdapter( Context context, int resource, List<Object> objects )
	{
		super( context, resource, objects );
		this.mContext = context;
		this.objects = objects;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		//Using generic objects to pass multiple types
		Object o = objects.get( position );
//View v = convertView;
		View v;
		if ( o instanceof TopicLink )
		{
			v = LayoutInflater.from( mContext ).inflate( R.layout.listview_topic_row, parent, false );

			//v = View.inflate( mContext, R.layout.listview_topic_row, parent );

			TopicLink link = (TopicLink) o;
			TopicViewHolder myView = new TopicViewHolder( v, link );
			v.setTag( myView );

			if ( myView.topicTitle != null )
			{
				//myView.topicTitle.setTypeface(typeface);
				myView.topicTitle.setText( link.topicTitle );
			}

			if ( myView.topicCreator != null )
			{
				//myView.topicCreator.setTypeface( typeface );//TODO remove the userID, it's for testing only
				myView.topicCreator.setText( link.username + " | " + link.userId );
			}

			if ( myView.blackTags != null )
			{
				//myView.redTags.setTypeface( typeface );
				//myView.blackTags.setTypeface( typeface );
				myView.redTags.setText( "" );
				myView.blackTags.setText( "" );
				for ( int j = 0; j < link.tags.length; j++ )
				{
					//TODO clean this stuff up
					if ( link.tags[ j ].equals( "NWS" ) || link.tags[ j ].equals( "Spoiler" ) )
					{
						if ( myView.redTags != null )
						{
							String currentText = myView.redTags.getText().toString();
							//The space goes after the individual tags
							myView.redTags.setText( currentText + link.tags[ j ] + " " );
						}
					}
					else
					{
						String currentText = myView.blackTags.getText().toString();
						//The space goes after the individual tags
						myView.blackTags.setText( currentText + link.tags[ j ] + " " );
					}
				}
			}


			if ( myView.posts != null )
			{
				//myView.posts.setTypeface( typeface );
				myView.posts.setText( Integer.toString( link.totalMessages ) );
				if ( link.totalMessages > 0 )
				{
					myView.posts.setText( " (" + link.totalMessages + ") " );
				}
			}


		}
		else if ( o instanceof BoardLink )
		{
			v = LayoutInflater.from( mContext ).inflate( R.layout.listview_board_row, parent, false );
			BoardLink link = (BoardLink) o;
			TextView textView = (TextView) v.findViewById( R.id.board_title );
			textView.setText( link.getBoardName() );

			//v = convertView;
		} else
		v = convertView;
		return v;
	}
	public static class TopicViewHolder
	{

		protected TopicLink i;


		@InjectView( R.id.topicTitle )
		TextView topicTitle;
		@InjectView( R.id.topicCreator )
		TextView topicCreator;
		@InjectView( R.id.topicTags )
		TextView blackTags;
		@InjectView( R.id.redTags )
		TextView redTags;
		@InjectView( R.id.topicPosts )
		TextView posts;

		public TopicViewHolder( View view, TopicLink i )
		{
			this.i = i;
			ButterKnife.inject( this, view );
		}


	}

	public static class BoardViewHolder {
		protected BoardLink i;

		@InjectView( R.id.board_title )
		TextView boardName;
	}
}
