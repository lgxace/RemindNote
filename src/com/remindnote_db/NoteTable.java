package com.remindnote_db;

import java.util.ArrayList;
import java.util.List;

import com.remindnote.entity.Note;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * database table tab_note to provide service to note
 * 
 * @author guangxiang.liang
 * 
 */
public class NoteTable {
	private DBHelper mDbHelper = null;
	private SQLiteDatabase mDB = null;

	/**
	 * constructor of note table
	 * 
	 * @param context
	 *            the application context
	 */
	public NoteTable(Context context) {
		mDbHelper = new DBHelper(context);
		mDB = mDbHelper.getWritableDatabase();
	}

	/**
	 * A method to add note data to sqlite database note table
	 * 
	 * @param note
	 *            the note entity to be inserted to database
	 */
	public void insert(Note note) {
		try {
			String sql = "insert into tab_note values('" + note.getmNoteID()
					+ "','" + note.getmUserID() + "'," + note.getmShareType()
					+ ",'" + note.getmNoteDate() + "','" + note.getmNoteTitle()
					+ "','" + note.getmNoteContent() + "','"
					+ note.getmReindTime() + "'," + note.getmNoteType() + ","
					+ note.getmRemindType() + ")";
			mDB.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * A method to query notes from database
	 * 
	 * @return the all notes queried from database
	 */
	public List<Note> queryAll() {
		String sql = "select * from tab_note";
		List<Note> notes = null;
		try {
			Cursor c = mDB.rawQuery(sql, null);
			if (c.moveToFirst()) {
				notes = new ArrayList<Note>();
				do {
					Note note = new Note();
					note.setmNoteID(c.getString(0));
					note.setmUserID(c.getString(1));
					note.setmShareType(c.getInt(2));
					note.setmNoteDate(c.getString(3));
					note.setmNoteTitle(c.getString(4));
					note.setmNoteContent(c.getString(5));
					note.setmReindTime(c.getString(6));
					note.setmNoteType(c.getInt(7));
					note.setmRemindType(c.getInt(8));
					notes.add(note);
				} while (c.moveToNext());
				return notes;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	

	/**
	 * A method to query specific note by its id
	 * 
	 * @param noteID
	 *            the note its id,unique main key identify a note in the
	 *            database
	 * @return return the specific note
	 */
	public Note queryByNoteID(String noteID) {
		String sql = "select * from tab_note where note_id=?";
		try {
			String[] args = { noteID };
			Cursor c = mDB.rawQuery(sql, args);
			Note note = null;
			if (c.moveToFirst()) {
				note = new Note();
				note.setmNoteID(c.getString(0));
				note.setmUserID(c.getString(1));
				note.setmShareType(c.getInt(2));
				note.setmNoteDate(c.getString(3));
				note.setmNoteTitle(c.getString(4));
				note.setmNoteContent(c.getString(5));
				note.setmReindTime(c.getString(6));
				note.setmNoteType(c.getInt(7));
				note.setmRemindType(c.getInt(8));
			}
			return note;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * close the database while not needed
	 */
	public void closeDB() {
		if (mDB != null)
			mDB.close();
	}

	/**
	 * delete all the note data from database
	 */
	public void deleteData() {
		String sql = "delete from tab_note";
		mDB.execSQL(sql);
	}

	/**
	 * update the note by its entity
	 * 
	 * @param note
	 */
	public void updateNote(Note note) {
		String sql = "update tab_note set share_type=" + note.getmShareType()
				+ ",note_content='" + note.getmNoteContent()
				+ "' where note_id='" + note.getmNoteID() + "'";
		mDB.execSQL(sql);
	}

	/**
	 * update the note by specific sql sentence
	 * 
	 * @param sql
	 *            sql string to be execute sentence.
	 */
	public void updateNote(String sql) {
		mDB.execSQL(sql);
	}

	/**
	 * delete note by its unique id
	 * 
	 * @param noteId
	 *            the note's unique id.
	 */
	public void deleteNote(String noteId) {
		String sql = "delete from tab_note where note_id='" + noteId + "'";
		mDB.execSQL(sql);
	}

}
