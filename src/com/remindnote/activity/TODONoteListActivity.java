package com.remindnote.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lgxace.remindnote.R;
import com.remindnote.adapter.NoteAdapterV1;
import com.remindnote.entity.Note;
import com.remindnote.util.ToastUtil;
import com.remindnote_db.NoteTable;

/**
 * show all the todo notes
 * @author guangxiang.liang
 *
 */
public class TODONoteListActivity extends Activity implements
		OnItemLongClickListener {
	private ListView mTODONoteList;// list view for todo notes
	private TextView mNoNotesText;// show remind when no todo

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_notelist);
		getId();
		showTODONotes();
		setListener();
	}

	private void getId() {
		mTODONoteList = (ListView) findViewById(R.id.lv_id_todo_notes);
		mNoNotesText = (TextView) findViewById(R.id.id_txt_todo_no_notes);
	}

	/**
	 * show all the todo notes
	 */
	private void showTODONotes() {
		NoteTable table = new NoteTable(getApplicationContext());
		List<Note> notes = table.queryAll();

		if (notes == null || notes.size() <= 0) {
			mNoNotesText.setVisibility(View.VISIBLE);
			mTODONoteList.setVisibility(View.GONE);
			return;
		}

		List<Note> todoNotes = new ArrayList<Note>();
		for (Note note : notes) {
			if (!note.getmReindTime().equals(""))
				todoNotes.add(note);
		}
		// set notes to list view by this adapter
		NoteAdapterV1 adapter = new NoteAdapterV1(this, todoNotes);
		mTODONoteList.setAdapter(adapter);
	}

	/**
	 * pop up to show choices for user when he long click the list view item
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		NoteAdapterV1 adapter = (NoteAdapterV1) arg0.getAdapter();
		final Note item = adapter.getItem(arg2);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TODONoteListActivity.this);
		builder.setItems(R.array.list_item_menu,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = null;
						if (arg1 == 0) {
							deleteNoteOperation(item);
							intent = new Intent(TODONoteListActivity.this,
									TODONoteListActivity.class);
						} else if (arg1 == 1) {
							intent = new Intent(TODONoteListActivity.this,
									NoteRemindActivity.class);
							intent.putExtra("noteId", item.getmNoteID());
						}
						startActivity(intent);
						finish();
					}
				});
		builder.show();
		return true;
	}

	private void deleteNoteOperation(Note note) {
		NoteTable table = null;
		try {
			table = new NoteTable(getApplicationContext());
			table.deleteNote(note.getmNoteID());
			if (note.getmNoteType() > 0)
				deleteMedia(note.getmNoteContent());
		} catch (Exception e) {
			ToastUtil.doUiToast(this, this, getString(R.string.toast_data_base_error),
					Toast.LENGTH_SHORT);
		} finally {
			table.closeDB();
		}
	}

	private void setListener() {
		OnItemClickListener itemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NoteAdapterV1 adapter = (NoteAdapterV1) parent.getAdapter();
				Note item = adapter.getItem(position);
				String noteId = item.getmNoteID();
				String userId = item.getmUserID();

				Intent intent = new Intent(TODONoteListActivity.this,
						NoteDetailActivity.class);
				intent.putExtra("noteId", noteId);
				intent.putExtra("userId", userId);
				intent.putExtra("noteType", item.getmNoteType());
				startActivity(intent);
				finish();
			}
		};

		mTODONoteList.setOnItemClickListener(itemClickListener);
		mTODONoteList.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(TODONoteListActivity.this,
					NoteListActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * delete the specific media note by it's path
	 * 
	 * @param path
	 *            the sdcard location of the note
	 */
	private void deleteMedia(String path) {
		File file = new File(path);
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			if (file.exists())
				file.delete();
		}
	}
}
