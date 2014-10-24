package com.remindnote.service;

import com.lgxace.remindnote.R;
import com.remindnote.activity.NoteListActivity;
import com.remindnote.activity.NoteRemindActivity;
import com.remindnote.activity.TODONoteListActivity;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WidgetClickHanderService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		// TODO:do click event
		Intent intent2 = new Intent(this, TODONoteListActivity.class);
		intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent2);
	}

}
