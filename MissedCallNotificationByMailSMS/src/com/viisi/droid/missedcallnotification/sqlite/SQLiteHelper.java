package com.viisi.droid.missedcallnotification.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.viisi.droid.missedcallnotification.util.Constants;

public class SQLiteHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_MISSEDCALL_LOG_CREATE = "create table " + Constants.tablemissedcallslog.table_name + "( " + Constants.tablemissedcallslog.column_id + " integer primary key autoincrement, " + Constants.tablemissedcallslog.column_missednumber + " text not null, " + Constants.tablemissedcallslog.column_contactname + " text null," + Constants.tablemissedcallslog.column_notitype + " integer not null, " + Constants.tablemissedcallslog.column_state + " integer not null, " + Constants.tablemissedcallslog.column_logtime + " datetime not null, "+ Constants.tablemissedcallslog.column_scheduletime +" datetime not null );";

	public SQLiteHelper(Context context) {
		super(context, Constants.tabledatabase.database_name, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_MISSEDCALL_LOG_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		dropTables(db);
		onCreate(db);
	}

	private void dropTables(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + Constants.tablemissedcallslog.table_name);
	}

}
