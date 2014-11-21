package com.HyperStandard.llr.app.Navigation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.HyperStandard.llr.app.Exceptions.LoggedOutException;
import com.HyperStandard.llr.app.Exceptions.WaitException;
import com.HyperStandard.llr.app.PostMessage;
import com.HyperStandard.llr.app.R;

/**
 * Created by nonex_000 on 8/8/2014.
 */
public class PostDrawerFragment extends Fragment
{
	private ListView mListView;
	private RelativeLayout mPostMessage;

	private String message;
	private String h;
	private int topicID;
	private Context mContext;

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

	public void setUp( String h, int topicID, Context context )
	{
		mContext = context;
		this.h = h;
		this.topicID = topicID;

	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		/*mListView = (ListView) inflater.inflate( R.layout.fragment_navigation_drawer, container );
		ArrayAdapter<String> adapter = new ArrayAdapter<>( getActivity().getApplicationContext(), android.R.layout.simple_list_item_1 );
		adapter.add( "test" );
		mListView.setAdapter( adapter );
		return mListView;*/
		mPostMessage = (RelativeLayout) inflater.inflate( R.layout.post_message, container );
		return mPostMessage;
	}

	public void postMessage(View v)
	{
		EditText editText = (EditText) v.findViewById( R.id.post_message_edit_text );
		PostMessage postMessage = new PostMessage();
		try
		{
			postMessage.post( editText.getText().toString(), h, topicID, false );
		}
		catch ( LoggedOutException e )
		{
			e.printStackTrace();
		}
		catch ( WaitException e )
		{
			e.printStackTrace();
		}
	}
}
