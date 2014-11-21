package com.HyperStandard.llr.app.Adapters;

/**
 * Created by mitch on 7/8/14.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.HyperStandard.llr.app.Models.TopicPost;
import com.HyperStandard.llr.app.R;
import com.HyperStandard.llr.app.Typefaces;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PostAdapter extends ArrayAdapter<TopicPost>
{
	private ArrayList<TopicPost> objects;
	private Typeface typeface;


	public PostAdapter( Context context, int textViewResourceId, ArrayList<TopicPost> objects )
	{
		super( context, textViewResourceId, objects );
		typeface = Typefaces.getTypface( context, getContext().getString( R.string.font_listview ) );
		this.objects = objects;
	}


	public View getView( int position, View view, ViewGroup parent )
	{
		TopicPost i = objects.get( position );

		ViewHolder viewHolder;

		if ( view == null )
		{
			view = LayoutInflater.from( getContext() ).inflate( R.layout.listview_post_row, parent, false );
			viewHolder = new ViewHolder( view );
			view.setTag( viewHolder );
		}
		else
		{
			viewHolder = (ViewHolder) view.getTag();
		}


		if ( i != null )
		{

			if ( viewHolder.username != null )
			{
				viewHolder.username.setText( i.username + " | " + Integer.toString( i.edits ) + " | " + Integer.toString( i.userId ) );
			}

			if ( viewHolder.message != null )
			{
				viewHolder.message.setText( i.message );
			}

			if ( viewHolder.signature != null )
			{
				viewHolder.signature.setText( i.signature );
			}
		}

		return view;


	}

	public class ViewHolder
	{
		@InjectView( R.id.post_username )
		TextView username;
		@InjectView( R.id.post_message )
		TextView message;
		@InjectView( R.id.post_signature )
		TextView signature;

		public ViewHolder( View v )
		{
			ButterKnife.inject( this, v );
		}
	}


}
