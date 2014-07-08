package com.HyperStandard.llr.app;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by mitch on 7/7/14.
 */
public class LLerApplication extends Application
{


	@Override
	public void onCreate()
	{
		super.onCreate();
		CalligraphyConfig.initDefault( "fonts/RobotoSlab-Regular.ttf", R.attr.fontPath );

	}
}
