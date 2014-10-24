package com.remindnote_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME="remindnote.db";
	public static final int DATABASE_VERSION=1;

	public DBHelper(Context context) {
		super(context,DATABASE_NAME,null,10);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlUser="create table tab_user(user_id varchar(20) primary key,user_type int,user_name varchar(20),user_pwd varchar(20),user_phone char(20),user_email char(50));";
		db.execSQL(sqlUser);
		String noteSql="create table tab_note(note_id varchar(20),user_id varchar(20),share_type int,note_date varchar(20),note_title varchar(20),note_content varchar(200),remind_time varchar(20),note_type,remind_type,primary key(note_id,user_id))";
		db.execSQL(noteSql);
//		String sqlString="alter table tab_note add column remind_time varchar(20)";
//		db.execSQL(sqlString);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("drop table if exists tab_user");
		db.execSQL("drop table if exists tab_note");
		onCreate(db);
	}

}
