package com.HyperStandard.llr.app.Fragment;

import android.app.Fragment;
import android.content.Context;


/**
 * @author HyperStandard
 * @since 8/29/2014
 */
public class PageFragment extends Fragment
{
	protected Context context;
	protected String title;

	interface Callbacks
	{
		public void setTitle( String title );

		public void loadTopicPage( String url );

		public void loadTopicListPage( String url );

		public void starTopic( String url );

		public void blacklistTopic( String url );
	}
}
