package com.HyperStandard.llr.app.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.HyperStandard.llr.app.Data.Cookies;
import com.HyperStandard.llr.app.R;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.fragment_settings );
		getFragmentManager().beginTransaction().replace( R.id.settings_container, new SettingsFragment() ).commit();

	}

	private static Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange( Preference preference, Object o )
		{
			if ( preference instanceof CheckBoxPreference )
			{

			}
			return false;
		}
	};

	public static class SettingsFragment extends PreferenceFragment
	{
		@Override
		public void onCreate( Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			getPreferenceManager().setSharedPreferencesName( getString( R.string.pref_name ) );
			addPreferencesFromResource( R.xml.pref_links );
			addPreferencesFromResource( R.xml.pref_general );
			addPreferencesFromResource( R.xml.pref_privacy );
			if ( Cookies.getCookies() != null )//If cookies are not null, then you've been logged in
			{
				addPreferencesFromResource( R.xml.pref_account );
			}
		}


	}
}
