package com.HyperStandard.llr.app.Navigation;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.HyperStandard.llr.app.R;

/**
 * Created by nonex_000 on 8/8/2014.
 */
public class TabsDrawerFragment extends Fragment
{
	private ListView mListView;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		return super.onOptionsItemSelected( item );
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		mListView = (ListView) inflater.inflate( R.layout.fragment_navigation_drawer, container );
		ArrayAdapter<String> adapter = new ArrayAdapter<>( getActivity().getApplicationContext(), android.R.layout.simple_list_item_1 );
		adapter.add( "test" );
		mListView.setAdapter( adapter );
		return mListView;
	}
}
