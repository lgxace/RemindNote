package com.remindnote.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lgxace.remindnote.R;
import com.remindnote.entity.Note;
import com.remindnote.service.NoteRemindService;
import com.remindnote.util.ToastUtil;
import com.remindnote_db.NoteTable;

/**
 * this activity used to set todo of a specific note
 * 
 * @author guangxiang.liang
 * 
 */
public class NoteRemindActivity extends Activity {
	private Button mTodoDate = null;// click to get todo date
	private Button mTodoTime = null;// click to get todo time
	private Spinner mTodoRemindType = null;// choice for remind type
	private Button mSetTodoOk = null;// click to complete the todo set
	private Button mSetTodoCancel = null;// click to cancel todo set
	private EditText mShowDate;// show the got todo date
	private EditText mShowTime;// show the got todo time
	private String mRemindTime;// todo date + todo time
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");// set date operation format
	private static final long THIRTY_SECONDS_MILLIS = 30 * 1000;
	private PendingIntent mAlarmSender;
	private AlarmManager mAlarmManager;

	private final static int DATE_DIALOG = 0;
	private final static int TIME_DIALOG = 1;
	private Calendar c = Calendar.getInstance();

	Dialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set remind service when todo comes
		Intent intent = new Intent(this, NoteRemindService.class);
		intent.putExtra("noteId", getIntent().getStringExtra("noteId"));
		mAlarmSender = PendingIntent.getService(this, 0, intent, 0);
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		setContentView(R.layout.activity_todo_settings);
		setId();
		setListener();
		getRemindTime();
	}

	private void setId() {
		mTodoDate = (Button) findViewById(R.id.id_todo_note_selectdate);
		mTodoTime = (Button) findViewById(R.id.id_todo_note_selecttime);
		mTodoRemindType = (Spinner) findViewById(R.id.sp_id_note_remind_type);
		mSetTodoOk = (Button) findViewById(R.id.id_note_todosetting_ok);
		mSetTodoCancel = (Button) findViewById(R.id.id_note_todosetting_cancel);
		mShowDate = (EditText) findViewById(R.id.id_todo_showdate);
		mShowTime = (EditText) findViewById(R.id.id_todo_showtiem);
	}

	private void setListener() {
		mTodoDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog(DATE_DIALOG);
			}
		});

		mTodoTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog(TIME_DIALOG);
			}
		});
		// click to complete todo set
		mSetTodoOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mRemindTime = mShowDate.getText().toString() + " "
						+ mShowTime.getText().toString();
				if (c != null)
					addRemindTimeToDB(dateFormat.format(c.getTime()));
				ToastUtil.doUiToast(NoteRemindActivity.this,
						NoteRemindActivity.this,
						"note remind time set completed", Toast.LENGTH_SHORT);
				doAlarm(mRemindTime);
				Intent intent = new Intent(NoteRemindActivity.this,
						NoteListActivity.class);
				startActivity(intent);
				finish();
			}
		});
		// click to cancel todo set
		mSetTodoCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(NoteRemindActivity.this,
						NoteListActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * pop up to get todo date and todo time
	 */
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DATE_DIALOG:
			dialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker dp, int year,
								int month, int dayOfMonth) {
							mShowDate.setText(year + "年" + (month + 1) + "月"
									+ dayOfMonth + "日");
							c.set(Calendar.YEAR, year);
							c.set(Calendar.MONTH, month);
							c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						}
					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
			break;
		case TIME_DIALOG:
			dialog = new TimePickerDialog(this,
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							mShowTime.setText(hourOfDay + "时" + minute + "分");
							c.set(Calendar.HOUR_OF_DAY, hourOfDay);
							c.set(Calendar.MINUTE, minute);
							c.set(Calendar.SECOND, 0);
						}
					}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
					false);
			break;
		}
		return dialog;
	}

	/**
	 * go to note list view activity when user click backspace key
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(NoteRemindActivity.this,
					NoteListActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * add remind time to database
	 * 
	 * @param remindTime
	 *            todo time
	 */
	private void addRemindTimeToDB(String remindTime) {
		String noteId = getIntent().getStringExtra("noteId");
		String sql = "update tab_note set remind_time='" + remindTime
				+ "' where note_id='" + noteId + "'";
		NoteTable table = null;
		try {
			table = new NoteTable(getApplicationContext());
			table.updateNote(sql);
		} catch (Exception e) {
			ToastUtil.doUiToast(this, this, "database server exception",
					Toast.LENGTH_SHORT);
			return;
		} finally {
			table.closeDB();
		}
	}

	/**
	 * parse the string to get todo date and todo time from database
	 */
	private void getRemindTime() {
		String noteId = getIntent().getStringExtra("noteId");
		NoteTable table = new NoteTable(getApplicationContext());
		try {
			Note note = table.queryByNoteID(noteId);
			String remindTime = note.getmReindTime();
			if (remindTime == null || remindTime.equals(""))
				return;
			String date = remindTime.split(" ")[0];
			String time = remindTime.split(" ")[1];
			mShowDate.setText(date);
			mShowTime.setText(time);
		} catch (Exception e) {
			ToastUtil.doUiToast(this, this, e.getMessage(), Toast.LENGTH_SHORT);
		}
	}

	/**
	 * Alarm remind when the note remind time comes
	 * 
	 * @param remindTimes
	 *            the detail time of the note to remind
	 */
	private void doAlarm(String remindTime) {
		if (c != null) {

			c.set(Calendar.SECOND, 0);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
					mAlarmSender);
			Toast.makeText(NoteRemindActivity.this, "remind time setting done",
					Toast.LENGTH_LONG).show();
		}
	}

	private void audioRemindShow() {
		new Thread() {
			public void run() {
				// TODO:pop up to show note information

			}
		}.start();
	}
}
