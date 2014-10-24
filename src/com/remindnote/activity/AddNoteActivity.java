package com.remindnote.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.lgxace.remindnote.R;
import com.remindnote.entity.Note;
import com.remindnote.remind.RegexUtil;
import com.remindnote.remind.RemindOperation;
import com.remindnote.util.ToastUtil;
import com.remindnote_db.NoteTable;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * this class is to add text note,audio note and video note
 * 
 * @author guangxiang.liang
 * 
 */
public class AddNoteActivity extends Activity implements SurfaceHolder.Callback {
	private EditText mNoteTile; // note id
	private EditText mNoteContent; // note content
	private RadioButton mShareYes; // share yes
	private RadioButton mShareNo; // share no
	private Button mAddNoteOk; // click to complete adding note
	private Button mAddNoteCancel; // click to cancel this adding operation
	private LinearLayout mTextNotelLayout; // add text note UI
	private LinearLayout mAudioLayout; // add audio note UI
	private LinearLayout mVideoLayout; // add video note UI
	private RadioGroup mRadioGroup; // show share feature
	private LinearLayout mMediaNoteLayout; // record media note UI
	private Button mAddMediaNoteOk; // click to complete media note add
	private Button mAddMediaNoteCancel; // click to cancel media note add
										// operation
	private MediaRecorder recorder = new MediaRecorder(); // record audio note
															// and video note

	private boolean isRecorded = false; // show if media note record is done
	private boolean isRcording = false; // show if media note record is going

	private SurfaceView surfaceView; // record and play video note UI
	SurfaceHolder surfaceHolder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_note);
		setId();
		initUI();
		setListener();
	}

	/**
	 * get and init all the ids of this activity
	 */
	private void setId() {
		mNoteTile = (EditText) findViewById(R.id.id_et_title);
		mNoteContent = (EditText) findViewById(R.id.id_et_content);
		mShareYes = (RadioButton) findViewById(R.id.id_rb_share_yes);
		mShareNo = (RadioButton) findViewById(R.id.id_rb_share_no);
		mAddNoteOk = (Button) findViewById(R.id.btn_id_addnote_ok);
		mAddNoteCancel = (Button) findViewById(R.id.btn_id_addnote_cancel);
		mTextNotelLayout = (LinearLayout) findViewById(R.id.ll_add_note_text_note);
		mAudioLayout = (LinearLayout) findViewById(R.id.ll_add_note_audio_note);
		mVideoLayout = (LinearLayout) findViewById(R.id.ll_add_note_video_note);
		mAddMediaNoteOk = (Button) findViewById(R.id.btn_id_add_media_note_ok);
		mAddMediaNoteCancel = (Button) findViewById(R.id.btn_id_add_media_note_cancel);

		mMediaNoteLayout = (LinearLayout) findViewById(R.id.ll_add_media_note);
		mRadioGroup = (RadioGroup) findViewById(R.id.id_add_note_share_group);

		surfaceView = (SurfaceView) findViewById(R.id.id_add_video_surfaceview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private void setListener() {
		// callback when user click it to add note
		mAddNoteOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!titleInputDetect()) {
					ToastUtil.doUiToast(AddNoteActivity.this,
							getApplicationContext(),
							"note title null or error!", Toast.LENGTH_SHORT);
					return;
				}
				Intent intent = getIntent();
				addNoteOkOperation(intent.getIntExtra("noteType",
						RemindOperation.NOTE_TYPE_TEXT));
			}
		});
		// cancel add note
		mAddNoteCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addNoteCancelOperation();
			}
		});
		// click to start recording media note
		mAddMediaNoteOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int noteType = getIntent().getIntExtra("noteType",
						RemindOperation.NOTE_TYPE_TEXT);
				if (noteType == RemindOperation.NOTE_TYPE_AUDIO) {
					addAudioNote();
				} else if (noteType == RemindOperation.NOTE_TYPE_VIDEO) {
					addVideo();
				}
			}
		});
		// cancel add media note
		mAddMediaNoteCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!titleInputDetect()) {
					ToastUtil.doUiToast(AddNoteActivity.this,
							getApplicationContext(),
							"note title null or error!", Toast.LENGTH_SHORT);
					return;
				}
				if (!isRecorded && isRcording) {
					ToastUtil.doUiToast(AddNoteActivity.this,
							getApplicationContext(),
							"record done", Toast.LENGTH_SHORT);
					return;
				}
				if (!isRecorded && !isRcording) {
					ToastUtil.doUiToast(AddNoteActivity.this,
							getApplicationContext(),
							"please record video firstly", Toast.LENGTH_SHORT);
					return;
				}
				addVideoNoteEnd();
				isRecorded = false;
			}
		});
	}

	/**
	 * add note to data base
	 * 
	 * @param noteType
	 *            noteType specify a note type 0 text note 1 audio note 2 video
	 *            note
	 */
	private void addNoteOkOperation(int noteType) {
		String noteTitle = mNoteTile.getText().toString();
		String noteContent = "";
		if (noteType == RemindOperation.NOTE_TYPE_TEXT)
			noteContent = mNoteContent.getText().toString();
		if (!contentInputDetect()) {
			ToastUtil.doUiToast(this, this, "note content too long",
					Toast.LENGTH_SHORT);
			return;
		}

		if (noteType == RemindOperation.NOTE_TYPE_AUDIO)
			noteContent = RemindOperation.MEDIA_AUDIO_PATH + noteTitle;
		if (noteType == RemindOperation.NOTE_TYPE_VIDEO)
			noteContent = RemindOperation.MEDIA_VIDEO_PATH + noteTitle;

		int shareType = getShareType();
		Note note = new Note(Calendar.getInstance().getTimeInMillis() + "",
				"lgxace", shareType,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
						.getInstance().getTime()), noteTitle.trim(),
				noteContent.trim(), "", RemindOperation.REMIND_TYPE_TEXT,
				noteType);
		NoteTable noteTable = null;
		try {
			noteTable = new NoteTable(getApplicationContext());
			noteTable.insert(note);
			Intent intent = new Intent(this, NoteListActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			ToastUtil.doUiToast(this, this, "data base error!",
					Toast.LENGTH_SHORT);
			return;
		} finally {
			noteTable.closeDB();
		}
		isRcording=false;
		finish();
		
	}

	private int getShareType() {
		return mShareYes.isChecked() ? 1 : 0;
	}

	private void addNoteCancelOperation() {
		Intent intent = new Intent(this, NoteListActivity.class);
		startActivity(intent);
		finish();
	}

	private void initUI() {
		int noteType = getIntent().getIntExtra("noteType",
				RemindOperation.NOTE_TYPE_TEXT);
		/**
		 * when add audio note,text note UI and video note UI should be gone
		 */
		if (noteType == RemindOperation.NOTE_TYPE_AUDIO) {
			mTextNotelLayout.setVisibility(View.GONE);
			mAudioLayout.setVisibility(View.VISIBLE);
			mRadioGroup.setVisibility(View.GONE);
			mMediaNoteLayout.setVisibility(View.VISIBLE);
		}
		/**
		 * when add video note ,text note UI and audio note UI should be gone
		 */
		else if (noteType == RemindOperation.NOTE_TYPE_VIDEO) {
			mTextNotelLayout.setVisibility(View.GONE);
			mVideoLayout.setVisibility(View.VISIBLE);
			mRadioGroup.setVisibility(View.GONE);
			mMediaNoteLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * go to note list activity when user push backspace key
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(AddNoteActivity.this,
					NoteListActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void addAudioNote() {
		if (isRcording) {
			ToastUtil.doUiToast(AddNoteActivity.this, getApplicationContext(),
					"recording...", Toast.LENGTH_SHORT);
			return;
		}
		// if something wrong about user's input,do a remind
		if (!titleInputDetect()) {
			ToastUtil.doUiToast(AddNoteActivity.this, getApplicationContext(),
					"note title null or err!", Toast.LENGTH_SHORT);
			return;
		}
		isRcording = true;
		String noteTitle = mNoteTile.getText().toString();
		String fileName = RemindOperation.MEDIA_AUDIO_PATH + noteTitle;
		File file = new File(fileName);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		// set some attributes about audio recorder
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(fileName);
		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		recorder.start();
		isRecorded = true;
	}

	// add note information to database
	private void addNoteToDB() {
		String noteTitle = mNoteTile.getText().toString();
		String noteContent = mNoteContent.getText().toString();
		int shareType = getShareType();
		Note note = new Note(Calendar.getInstance().getTimeInMillis() + "",
				"lgxace", shareType,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
						.getInstance().getTime()), noteTitle, noteContent, "",
				RemindOperation.NOTE_TYPE_TEXT, RemindOperation.NOTE_TYPE_TEXT);
		NoteTable noteTable = null;
		try {
			noteTable = new NoteTable(getApplicationContext());
			noteTable.insert(note);
			Intent intent = new Intent(this, NoteListActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			ToastUtil.doUiToast(this, this, "data base error!",
					Toast.LENGTH_SHORT);
			return;
		} finally {
			noteTable.closeDB();
		}
		finish();
	}

	private void addVideo() {

		if (isRcording) {
			ToastUtil.doUiToast(AddNoteActivity.this, getApplicationContext(),
					"recording", Toast.LENGTH_SHORT);
			return;
		}
		// if something wrong about user's input,do a remind
		if (!titleInputDetect()) {
			ToastUtil.doUiToast(AddNoteActivity.this, getApplicationContext(),
					"note title null or err!", Toast.LENGTH_SHORT);
			return;
		}
		isRcording = true;
		addVideoNoteStart();
		isRecorded = true;
	}

	private boolean titleInputDetect() {
		if (mNoteTile.getText().toString().length() >= 20) {
			return false;
		}
		RegexUtil regexUtil = new RegexUtil(RegexUtil.REGEX_WORD, mNoteTile
				.getText().toString());
		return regexUtil.isMatch();
	}

	private boolean contentInputDetect() {
		if (mNoteContent.getText().toString().length() >= 150) {
			return false;
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		surfaceHolder = holder;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceView = null;
		surfaceHolder = null;
		recorder = null;
	}

	private void addVideoNoteStart() {
		// set some attributes about video recorder
		recorder.reset();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		recorder.setVideoSize(176, 144);
		recorder.setVideoFrameRate(20);
		recorder.setPreviewDisplay(surfaceHolder.getSurface());
		recorder.setOutputFile(RemindOperation.MEDIA_VIDEO_PATH
				+ mNoteTile.getText().toString());
		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			recorder.stop();
			recorder.reset();
			recorder.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addVideoNoteEnd() {
		if (recorder != null) {
			recorder.stop();
			recorder.reset();
			recorder.release();
		}
	}
	
}
