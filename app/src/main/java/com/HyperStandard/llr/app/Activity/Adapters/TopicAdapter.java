package com.HyperStandard.llr.app.Activity.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HyperStandard.llr.app.Fragment.TopicListFragment;
import com.HyperStandard.llr.app.Models.TopicLink;
import com.HyperStandard.llr.app.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mitch on 7/7/14.
 */
public class TopicAdapter extends ArrayAdapter<TopicLink>
{
	protected TopicListFragment.Callbacks callbacks;
	private ArrayList<TopicLink> objects;
	private Typeface typeface;


	public TopicAdapter( Context context, int textViewResourceId, ArrayList<TopicLink> objects, TopicListFragment.Callbacks callbacks )
	{
		super( context, textViewResourceId, objects );
		this.objects = objects;
		this.callbacks = callbacks;
	}


	public View getView( int position, View view, ViewGroup parent )
	{

		final TopicLink i = objects.get( position );

		ViewHolder myView;

		if ( view == null )
		{
			view = LayoutInflater.from( getContext() ).inflate( R.layout.listview_topic_row, parent, false );
			myView = new ViewHolder( view, i );
			view.setTag( myView );
		}
		else
		{
			myView = (ViewHolder) view.getTag();
		}


		//TODO really dont need all these null checks. Fix it later
		if ( i != null )
		{

			if ( myView.topicTitle != null )
			{
				//myView.topicTitle.setTypeface(typeface);
				myView.topicTitle.setText( i.topicTitle );
			}

			if ( myView.topicCreator != null )
			{
				myView.topicCreator.setTypeface( typeface );
				myView.topicCreator.setText( i.username );
			}

			if ( myView.blackTags != null )
			{
				myView.redTags.setTypeface( typeface );
				myView.blackTags.setTypeface( typeface );
				myView.redTags.setText( "" );
				myView.blackTags.setText( "" );
				for ( int j = 0; j < i.tags.length; j++ )
				{
					//TODO clean this stuff up
					if ( i.tags[ j ].equals( "NWS" ) || i.tags[ j ].equals( "Spoiler" ) )
					{
						if ( myView.redTags != null )
						{
							String currentText = myView.redTags.getText().toString();
							//The space goes after the individual tags
							myView.redTags.setText( currentText + i.tags[ j ] + " " );
						}
					}
					else
					{
						String currentText = myView.blackTags.getText().toString();
						//The space goes after the individual tags
						myView.blackTags.setText( currentText + i.tags[ j ] + " " );
					}
				}
			}


			if ( myView.posts != null )
			{
				myView.posts.setTypeface( typeface );
				myView.posts.setText( Integer.toString( i.totalMessages ) );
				if ( i.lastRead > 0 )
				{
					myView.posts.setText( " (" + i.lastRead + ") " );
				}
			}

			//Doesnt belong in view holder logic
			RelativeLayout myLayout = (RelativeLayout) view.findViewById( R.id.topic_listview_container );
			myLayout.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View view )
				{
					if ( callbacks != null )
					{
						callbacks.loadTopic( Integer.toString( i.topicId ) );
					}

				}
			} );


		}

		return view;
	}

	public static class ViewHolder
	{

		protected TopicLink i;


		@InjectView(R.id.topicTitle)
		TextView topicTitle;
		@InjectView(R.id.topicCreator)
		TextView topicCreator;
		@InjectView(R.id.topicTags)
		TextView blackTags;
		@InjectView(R.id.redTags)
		TextView redTags;
		@InjectView(R.id.topicPosts)
		TextView posts;

		public ViewHolder( View view, TopicLink i )
		{
			this.i = i;
			ButterKnife.inject( this, view );
		}


	}
}
