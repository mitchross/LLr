package com.HyperStandard.llr.app.Activity;

import android.app.Activity;
import android.content.SharedPreferences;

import com.HyperStandard.llr.app.R;


/**
 * Created by nonex_000 on 8/5/2014.
 */
public class Usernames extends Activity
{
	SharedPreferences preferences = getSharedPreferences( getString( R.string.prefs_login ) , MODE_PRIVATE);

}
