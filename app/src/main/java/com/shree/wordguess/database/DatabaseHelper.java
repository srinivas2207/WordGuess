package com.shree.wordguess.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shree.wordguess.util.DBConstants;

public class DatabaseHelper extends SQLiteOpenHelper {

	 private static final String CREATE_TABLE_WORDS = "create table "
		      + DBConstants.WORD_TABLE
		      + "(" 
		      + DBConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		      + DBConstants.NAME  + " TEXT, "
			  + DBConstants.TYPE  + " TEXT, "
			  + DBConstants.DESC  + " TEXT, "
		      + DBConstants.CATEGORY  + " INTEGER DEFAULT 0, "
		      + DBConstants.TRANS_VALUE  + " TEXT, "
		      + DBConstants.SOURCE_LANG  + " TEXT, "
			 + DBConstants.FAVOURITE  + " tinyint(1) DEFAULT 0 "
		      +");";

	public DatabaseHelper(Context context) {
	    super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
	  }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_WORDS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
