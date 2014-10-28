package com.remindnote.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.lgxace.remindnote.R;
import com.remindnote.entity.Note;
import com.remindnote.remind.RemindOperation;
import com.remindnote.util.ToastUtil;
import com.remindnote_db.NoteTable;

/**
 * this activity show the detail information of the a note
 * 
 * @author guangxiang.liang
 * 
 */
public class NoteDetailActivity extends Activity implements
		SurfaceHolder.Callback {
	private EditText mNoteTitl; // note title
	private EditText mNoteConetent; // note content
	private RadioButton mShareOk; // share Yes
	private RadioButton mShareNo; // share No
	private Button mEditNote; // click to edit note
	private LinearLayout mEditNoteYes; // edit available note UI
	private LinearLayout mEditNoteNo; // edit disavailable note UI
	private Button mBtnEditNoteOk; // click to complete edit
	private Button mBtnEditNoteCancel; // click to cancel edit
	private Note note; // note entity
	private ImageButton mPlayAudioButton; // click to play audio note

	private ImageButton mShowMedia; // click to play video note
	private SurfaceView mShowVeidoSurfaceView; // the UI to show video note

	private LinearLayout mTextNoteLayout; // text note UI
	private LinearLayout mAudioNoteLyout; // audio note UI
	private LinearLayout mVideoNoteLayout; // video note UI

	private MediaPlayer player; // component used to play media note
	private SurfaceHolder surfaceHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_note);
		setId(); // get and init all the ids of this activity
		initUI(); // init activity by the specified note type
		init(); // init data of this activity
		setListener(); // set all the listener
	}

	private void setId() {
		mNoteTitl = (EditText) findViewById(R.id.id_et_detailnote_title);
		mNoteConetent = (EditText) findViewById(R.id.id_et_detailnote_content);
		mShareOk = (RadioButton) findViewById(R.id.id_rb_notedetail_share_yes);
		mShareNo = (RadioButton) findViewById(R.id.id_rb_notedetail_share_no);
		mEditNote = (Button) findViewById(R.id.btn_id_edit_note);
		mEditNoteYes = (LinearLayout) findViewById(R.id.id_ll_editnote_yes);
		mEditNoteNo = (LinearLayout) findViewById(R.id.id_ll_editnote_no);
		mBtnEditNoteOk = (Button) findViewById(R.id.btn_id_editnote_ok);
		mBtnEditNoteCancel = (Button) findViewById(R.id.btn_id_editnote_cancel);
		mPlayAudioButton = (ImageButton) findViewById(R.id.btn_note_detail_play_audio);
		mTextNoteLayout = (LinearLayout) findViewById(R.id.ll_note_detail_text_note);
		mAudioNoteLyout = (LinearLayout) findViewById(R.id.ll_note_detail_audio_note);
		mVideoNoteLayout = (LinearLayout) findViewById(R.id.ll_note_detail_video_note);
		mShowMedia = (ImageButton) findViewById(R.id.id_add_video_show);
		mShowVeidoSurfaceView = (SurfaceView) findViewById(R.id.id_show_video_surfaceview);
	}

	private void init() {
		Intent intent = getIntent();
		String noteId = intent.getStringExtra("noteId");
		String userId = intent.getStringExtra("userId");

		// get all the note from database
		NoteTable noteTable = new NoteTable(getApplicationContext());
		note = noteTable.queryByNoteID(noteId);
		noteTable.closeDB();

		mNoteTitl.setText(note.getmNoteTitle());
		mNoteConetent.setText(note.getmNoteContent());
		mShareOk.setChecked(note.getmShareType() == 1 ? true : false);
		mShareNo.setChecked(note.getmShareType() == 0 ? true : false);

		surfaceHolder = mShowVeidoSurfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setFixedSize(320, 220);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private void setListener() {
		// edit note start
		mEditNote.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mNoteConetent.setEnabled(true);
				mShareOk.setEnabled(true);
				mShareNo.setEnabled(true);
				mEditNoteYes.setVisibility(View.VISIBLE);
				mEditNoteNo.setVisibility(View.GONE);
			}
		});

		// click to complete edit note
		mBtnEditNoteOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mNoteConetent.getText().toString().length() > 150) {
					ToastUtil.doUiToast(NoteDetailActivity.this,
							NoteDetailActivity.this,
							getString(R.string.toast_data_base_error),
							Toast.LENGTH_SHORT);
					return;
				}
				editNoteOperation();
			}
		});

		// click to cancel edit note
		mBtnEditNoteCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(NoteDetailActivity.this,
						NoteListActivity.class);
				startActivity(intent);
				finish();
			}
		});

		// start to play audio note
		mPlayAudioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO:play audio
				new Thread() {
					public void run() {
						try {
							// set player
							player = new MediaPlayer();
							player.reset();
							player.setAudioStreamType(AudioManager.STREAM_MUSIC);
							player.setDataSource(RemindOperation.MEDIA_AUDIO_PATH
									+ mNoteTitl.getText().toString());
							player.prepare();
							player.start();
							player.setOnCompletionListener(new OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer arg0) {
									arg0.stop();
								}
							});
						} catch (Exception e) {
							ToastUtil.doUiToast(NoteDetailActivity.this,
									getApplicationContext(),
									getString(R.string.toast_player_error),
									Toast.LENGTH_SHORT);
						}
					}
				}.start();
			}
		});

		// show video note UI
		mShowMedia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mShowMedia.setVisibility(View.GONE);
				mShowVeidoSurfaceView.setVisibility(View.VISIBLE);
			}
		});
	}

	// add modified note to database
	private void editNoteOperation() {
		String noteContent = mNoteConetent.getText().toString().trim();
		int noteShareType = mShareOk.isChecked() ? 1 : 0;
		NoteTable noteTable = new NoteTable(getApplicationContext());

		note.setmNoteContent(noteContent);
		note.setmShareType(noteShareType);
		try {
			noteTable.updateNote(note);
			Intent intent = new Intent(NoteDetailActivity.this,
					NoteListActivity.class);
			startActivity(intent);
			ToastUtil.doUiToast(this, this,
					getString(R.string.toast_modify_note_done),
					Toast.LENGTH_SHORT);
			finish();
		} catch (Exception e) {
			ToastUtil.doUiToast(this, this,
					getString(R.string.toast_data_base_error),
					Toast.LENGTH_SHORT);
			return;
		} finally {
			noteTable.closeDB();
		}
	}

	// go to note list activity when user click backspace key
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(NoteDetailActivity.this,
					NoteListActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	// set UI differ from note type
	private void initUI() {
		int noteType = getIntent().getIntExtra("noteType",
				RemindOperation.NOTE_TYPE_TEXT);
		if (noteType == RemindOperation.NOTE_TYPE_AUDIO) {
			mTextNoteLayout.setVisibility(View.GONE);
			mAudioNoteLyout.setVisibility(View.VISIBLE);
		} else if (noteType == RemindOperation.NOTE_TYPE_VIDEO) {
			mTextNoteLayout.setVisibility(View.GONE);
			mVideoNoteLayout.setVisibility(View.VISIBLE);
		}
		if (noteType != RemindOperation.NOTE_TYPE_TEXT)
			mEditNote.setEnabled(false);
	}

	@Override
	protected void onDestroy() {
		if (player != null)
			player.release();
		super.onDestroy();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * init player and play video note when surface is created
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setDisplay(surfaceHolder);
		try {
			player.setDataSource(RemindOperation.MEDIA_VIDEO_PATH
					+ mNoteTitl.getText());
			player.prepare();
			new Thread() {
				public void run() {
					player.start();
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		if (player != null ) {
			player.stop();
			player.release();
		}
		super.onDestroy();
	}
}
