package com.HyperStandard.llr.app.Navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.HyperStandard.llr.app.BookmarkLink;
import com.HyperStandard.llr.app.LoadImage;
import com.HyperStandard.llr.app.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author HyperStandard
 * @since 6/17/2014
 */
public class NavigationAdapter extends ArrayAdapter<BookmarkLink>
{
	private static final int NUMBER_STATIC_LINKS = 0;
	private ArrayList<BookmarkLink> objects;
	private NavigationDrawerCallback callback;

	NavigationAdapter( Context context, int textViewResourceId, ArrayList<BookmarkLink> objects )
	{
		super( context, textViewResourceId, objects );
		this.objects = objects;
	}

	public View getView( int position, View convertView, ViewGroup parent )
	{

		View v = convertView;



		//The navigation drawer will have the modular links at the bottom, but the first NUMBER_STATIC_LINKS items will be custom
		BookmarkLink i = objects.get( position + NUMBER_STATIC_LINKS );

		switch ( position )
		{
			case 0://This holds the user picture (if it exists)//TODO fix this later
				LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				v = inflater1.inflate( R.layout.listview_navigation_userpic, null );
				ExecutorService executor = Executors.newSingleThreadExecutor();
				ImageView imageView = (ImageView) v.findViewById( R.id.navigation_userpic );
				Future<Bitmap> bitmapFuture = executor.submit( new LoadImage( "http://i4.dealtwith.it/i/n/7146d225232a005ee298011a8be2bac5/almond.png" ) );
				try
				{
					Bitmap userpic = bitmapFuture.get();
					imageView.setImageBitmap( userpic );
				}
				catch ( InterruptedException e )
				{
					e.printStackTrace();
				}
				catch ( ExecutionException e )
				{
					e.printStackTrace();
				}
				break;
			case 1:
			default:
				if ( v == null )
				{
					LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
					v = inflater.inflate( R.layout.listview_navigation_row, null );
				}
				if ( i != null )
				{
					/**
					 * Action label (String)
					 */
					TextView title = (TextView) v.findViewById( R.id.mainText );

					if ( title != null )
					{
						title.setText( i.getBookmarkName() );
					}

					//Log.e("Callback", i.getBookmarkTags());
					if ( i.getBookmarkTags().contains( "showmessages.php" ) )
					{
						callback.changeLocation( i.getBookmarkTags() );
					}
				}
				break;
		}


		return v;


	}

	public void setCallback( NavigationDrawerCallback callback )
	{
		this.callback = callback;
	}

	public interface NavigationDrawerCallback
	{
		public void changeLocation( String URL );
	}


}
