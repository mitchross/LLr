package com.HyperStandard.llr.app;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.util.LruCache;

/**
 * @author HyperStandard
 * @since 6/28/2014
 */
public class CustomTypefaceSpan extends MetricAffectingSpan
{
	private static LruCache<String, Typeface> cache = new LruCache<>( 4 );
	private Typeface typeface;

	public CustomTypefaceSpan( Context context, String typefaceName )
	{
		typeface = cache.get( typefaceName );
		if ( typeface == null )
		{
			typeface = Typeface.createFromAsset( context.getApplicationContext().getAssets(), typefaceName );
		}
	}

	@Override
	public void updateMeasureState( final TextPaint textPaint )
	{
		textPaint.setTypeface( typeface );
		textPaint.setFlags( textPaint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG );
	}

	@Override
	public void updateDrawState( final TextPaint textPaint )
	{
		textPaint.setTypeface( typeface );
		textPaint.setFlags( textPaint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG );
	}
}
