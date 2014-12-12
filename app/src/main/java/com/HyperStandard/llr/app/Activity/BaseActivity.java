package com.HyperStandard.llr.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mitch on 7/7/14.
 */
public class BaseActivity extends ActionBarActivity
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		//TODO determine if this is necessary
		//CalligraphyConfig.initDefault( getString( R.string.font_title ), R.attr.fontPath );
	}

	@Override
	protected void attachBaseContext( Context newBase )
	{
		super.attachBaseContext( new CalligraphyContextWrapper( newBase ) );
	}
}
