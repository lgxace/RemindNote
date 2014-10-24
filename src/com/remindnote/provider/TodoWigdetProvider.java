package com.remindnote.provider;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.lgxace.remindnote.R;
import com.remindnote.entity.Note;
import com.remindnote.service.TodoListViewService;
import com.remindnote.service.WidgetClickHanderService;
import com.remindnote_db.NoteTable;

public class TodoWigdetProvider extends AppWidgetProvider {

	private static final String BROADCA_STRING = "com.wd.appwidgetupdate";
	private static Timer myTimer;

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		MyTask task = new MyTask(context);
		Log.d("remindnote_test:TodoWigdetProvider", "sendBroadCast()");
		myTimer = new Timer();
		myTimer.schedule(task, 1000, 1000);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.d("remindnote_test:todowidgetprovider", "onUpdate()");
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.activity_todo_notelist);

		Intent intent = new Intent(context, TodoListViewService.class);
		views.setRemoteAdapter(R.id.lv_id_todo_notes, intent);
		
		Log.d("remindnote_test:todowidgetprovider", "to be WidgetClickHanderService");
		Intent clickIntent = new Intent(context, WidgetClickHanderService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setPendingIntentTemplate(R.id.lv_id_todo_notes, pendingIntent);

		appWidgetManager.updateAppWidget(new ComponentName(context,
				TodoWigdetProvider.class), views);
	}

	class MyTask extends TimerTask {
		private Context mcontext = null;
		private Intent intent = null;

		public MyTask(Context context) {
			mcontext = context;
			intent = new Intent();
			intent.setAction(BROADCA_STRING);
		}

		@Override
		public void run() {
			mcontext.sendBroadcast(intent);
		}
	}
	
	
}
