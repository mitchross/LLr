package com.HyperStandard.llr.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.HyperStandard.llr.app.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mitch on 7/7/14.
 */
public class BaseActivity extends Activity
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		//Set base font
		CalligraphyConfig.initDefault( "fonts/RobotoSlab-Regular.ttf", R.attr.fontPath );
	}

	@Override
	protected void attachBaseContext( Context newBase )
	{
		super.attachBaseContext( new CalligraphyContextWrapper( newBase ) );
	}
}
