package com.HyperStandard.llr.app.Adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by nonex_000 on 8/5/2014.
 */
public class CustomSpinnerAdapter extends ArrayAdapter
{

	public CustomSpinnerAdapter( Context context, int resource,  List objects)
	{
		super( context, resource, objects );
	}

	@Override
	public void registerDataSetObserver( DataSetObserver dataSetObserver )
	{

	}

	@Override
	public void unregisterDataSetObserver( DataSetObserver dataSetObserver )
	{

	}

	@Override
	public int getCount()
	{
		return 0;
	}

	@Override
	public Object getItem( int i )
	{
		return null;
	}

	@Override
	public long getItemId( int i )
	{
		return 0;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public View getView( int i, View view, ViewGroup viewGroup )
	{
		return super.getView( i, view, viewGroup );
	}

	@Override
	public int getItemViewType( int i )
	{
		return 0;
	}

	@Override
	public int getViewTypeCount()
	{
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}
}
