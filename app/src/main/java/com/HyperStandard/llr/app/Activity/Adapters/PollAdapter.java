package com.HyperStandard.llr.app.Activity.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.HyperStandard.llr.app.R;

import java.util.List;

/**
 * Created by nonex_000 on 8/19/2014.
 */
public class PollAdapter extends ArrayAdapter<String>
{
	public PollAdapter( Context context, int resource, List<String> objects )
	{
		super( context, resource, objects );
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		View v = convertView;
		TextView textView = (TextView) v.findViewById( R.id.poll_radio_button );
		return v;
	}
}
