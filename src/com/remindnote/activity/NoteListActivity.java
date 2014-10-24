package com.remindnote.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lgxace.remindnote.R;
import com.remindnote.adapter.NoteAdapterV1;
import com.remindnote.entity.Note;
import com.remindnote.remind.RemindOperation;
import com.remindnote.util.ToastUtil;
import com.remindnote_db.NoteTable;

/**
 * this activity show all the notes
 * @author guangxiang.liang
 *
 */
public class NoteListActivity extends Activity implements
		OnItemLongClickListener {
	private ImageButton mAddNotes;//click to add note
	private ListView mNotes;//component used to show all the notes
	private List<Note> notes;//all the note
	private TextView mNoNotes;//show remind when no note
	ImageButton mTODO;//click to go to todo notes
	private NoteAdapterV1 mNoteAdapterV1;//adapter from notes to list view

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_list);
		setId();
		mNotes.setStackFromBottom(false);//set note show sequence 
		notes = new ArrayList<Note>();
		setNoteList();
		// if no note,show remind UI
		if (!hasNote())
			mNotes.setVisibility(View.GONE);
		else
			mNoNotes.setVisibility(View.GONE);
		setListener();

	}

	private void setId() {
		mNotes = (ListView) findViewById(R.id.lv_id_notes);
		mNoNotes = (TextView) findViewById(R.id.id_txt_no_notes);
		mAddNotes = (ImageButton) findViewById(R.id.ib_id_note_list_add_note);
		mTODO = (ImageButton) findViewById(R.id.ib_id_todo_list_note);
	}

	private boolean hasNote() {
		if (notes == null || notes.size() <= 0)
			return false;
		else
			return true;
	}

	private void setListener() {
		mTODO.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO:go to todo note list
				Intent intent = new Intent(NoteListActivity.this,
						TODONoteListActivity.class);
				startActivity(intent);
				finish();
			}
		});

		mAddNotes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showNoteTypeChoices();
			}
		});

		OnItemClickListener itemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NoteAdapterV1 adapter = (NoteAdapterV1) parent.getAdapter();
				Note item = adapter.getItem(position);
				String noteId = item.getmNoteID();
				String userId = item.getmUserID();

				Intent intent = new Intent(NoteListActivity.this,
						NoteDetailActivity.class);
				intent.putExtra("noteId", noteId);
				intent.putExtra("userId", userId);
				intent.putExtra("noteType", item.getmNoteType());
				startActivity(intent);
				finish();
			}
		};

		mNotes.setOnItemClickListener(itemClickListener);
		mNotes.setOnItemLongClickListener(this);
	}

	/**
	 * set note list adapter
	 */
	private void setNoteList() {
		NoteTable noteTable = new NoteTable(getApplicationContext());
		notes = noteTable.queryAll();
		if (notes == null || notes.size() <= 0)
			return;
		mNoteAdapterV1 = new NoteAdapterV1(this, notes);
		if (mNoteAdapterV1 == null)
			return;
		mNotes.setAdapter(mNoteAdapterV1);

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
				NoteListActivity.this);
		builder.setItems(R.array.list_item_menu,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = null;
						//choose to delete the note
						if (arg1 == 0) {
							deleteNoteOperation(item);
							intent = new Intent(NoteListActivity.this,
									NoteListActivity.class);
						} 
						//choose to set todo for the note
						else if (arg1 == 1) {
							intent = new Intent(NoteListActivity.this,
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

	/**
	 * delete the specific note from database
	 * @param note
	 * 		the note to be deleted
	 */
	private void deleteNoteOperation(Note note) {
		NoteTable table = null;
		try {
			table = new NoteTable(getApplicationContext());
			//delete text note
			table.deleteNote(note.getmNoteID());
			//delete media source from sdcard
			if (note.getmNoteType() > 0)
				deleteMedia(note.getmNoteContent());
		} catch (Exception e) {
			ToastUtil.doUiToast(this, this, "file operation error", Toast.LENGTH_SHORT);
		} finally {
			table.closeDB();
		}

	}
	/**
	 * delete the specific media note by it's path
	 * @param path
	 * 		the sdcard location of the note
	 */
	private void deleteMedia(String path) {
		File file = new File(path);
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			if (file.exists())
				file.delete();
		}
	}
	/**
	 * show pop up when user long click the list view item
	 */
	private void showNoteTypeChoices() {
		final int[] noteType = { RemindOperation.NOTE_TYPE_TEXT };
		Builder builder = new AlertDialog.Builder(NoteListActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("note type choice")
				.setSingleChoiceItems(R.array.arrays_note_type, 0,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								if (arg1 == 1)
									noteType[0] = RemindOperation.NOTE_TYPE_AUDIO;
								else if (arg1 == 2)
									noteType[0] = RemindOperation.NOTE_TYPE_VIDEO;
							}
						})
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent(NoteListActivity.this,
								AddNoteActivity.class);
						intent.putExtra("noteType", noteType[0]);
						startActivity(intent);
						finish();
					}
				})
				.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// to do nothing
							}
						});
		builder.create().show();
	}
	/**
	 * quit the application when user click backspace key
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0)
			doQuitAlert();
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * pop up to make sure if user want to quit the application.
	 */
	private void doQuitAlert() {
		new AlertDialog.Builder(NoteListActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("sure to quit？")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).create().show();
	}
}
