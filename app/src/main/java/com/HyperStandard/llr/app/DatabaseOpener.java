package com.HyperStandard.llr.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author HyperStandard
 * @since 8/5/2014
 */
public class DatabaseOpener extends SQLiteOpenHelper
{
	private final static String mTag = "LLr -> (DatabaseOpener)";

	private Context mContext;
	private String databaseName;
	private SQLiteDatabase.CursorFactory mCursorfactory;

	private SQLiteDatabase mDataBase;

	public DatabaseOpener( Context context, String name, SQLiteDatabase.CursorFactory factory, int version )
	{
		super( context, name, factory, version );
		this.mContext = context;
		this.mCursorfactory = factory;
	}

	public void createDatabase(String name)
	{
		SQLiteDatabase.openOrCreateDatabase( mContext.getDatabasePath( databaseName ), mCursorfactory );
	}

	@Override
	public void onOpen( SQLiteDatabase db )
	{

	}

	@Override
	public void onCreate( SQLiteDatabase sqLiteDatabase )
	{

	}

	@Override
	public void onUpgrade( SQLiteDatabase sqLiteDatabase, int i, int i2 )
	{

	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();
		super.close();
	}
}
