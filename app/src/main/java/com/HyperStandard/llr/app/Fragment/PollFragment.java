package com.HyperStandard.llr.app.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.HyperStandard.llr.app.R;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author HyperStandard
 * @since 7/18/2014
 */
public class PollFragment extends Fragment
{
	protected static final String mTag = "LLr -> PollFragment";
	@InjectView(R.id.poll_list)
	ListView listView;
	ArrayList<PollElement> pollItems;
	private Callbacks callbacks;
	private String[] pollChoices = { "hello", "test" };
	private Context context;

	public void setUp( Elements elements )
	{
		for ( Element e : elements.select( "label" ) )
		{
			pollItems.add( new PollElement(//TODO handle errors and maybe stick this in the PollElement class who cares
					e.text(),
					Integer.parseInt( e.attr( "for" ).substring( 1 ) )
			) );
			Log.e( mTag, e.text() );
			Log.e( mTag, e.attr( "for" ).substring( 1 ) );
		}
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.poll, container, false );
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( context, android.R.layout.simple_list_item_single_choice, pollChoices );
		ButterKnife.inject( this, v );

		listView.setAdapter( adapter );
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

	class PollElement
	{
		private String option;
		private int vNumber;

		PollElement( String option, int vNumber )
		{
			this.vNumber = vNumber;
			this.option = option;
		}

		public int getvNumber()
		{
			return vNumber;
		}

		public String getOption()
		{
			return option;
		}
	}
}
