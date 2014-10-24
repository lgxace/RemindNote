package com.remindnote.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.lgxace.remindnote.R;
import com.remindnote.activity.NoteListActivity;
import com.remindnote.activity.StartActivity;

public class NoteRemindService extends Service {
	IBinder mBinder = new AlarmBinder();

	Notification mNotification;
	PendingIntent mcontentIntent;
	NotificationManager mNotificationManager;

	Thread mWorkThread;

	private static final String ALARM_SERVICE_THREAD = "AlarmService";
	public static final long WAIT_TIME_SECONDS = 15;
	public static final long MILLISECS_PER_SEC = 1000;

	Runnable mWorkTask = new Runnable() {

		@Override
		public void run() {
			long waitTime = System.currentTimeMillis() + WAIT_TIME_SECONDS
					* MILLISECS_PER_SEC;
			MediaPlayer player = new MediaPlayer();
			while (System.currentTimeMillis() < waitTime) {
				synchronized (mBinder) {
					try {
						player.setDataSource(
								getApplicationContext(),
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_ALARM));
						player.prepare();
						player.start();
					} catch (Exception e1) {
					}
				}
			}
			stopSelf();
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	private class AlarmBinder extends Binder {
		public AlarmBinder() {
			super();
		}

		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {

			return super.onTransact(code, data, reply, flags);
		}
	}

	private void showNotification() {
		CharSequence notificationText = getText(R.string.note_alarm_msg);
		mNotification = new Notification(R.drawable.add, notificationText,
				System.currentTimeMillis());
		mcontentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				StartActivity.class), 0);
		mNotification.setLatestEventInfo(this,
				getText(R.string.alarm_service_label), notificationText,
				mcontentIntent);
		mNotificationManager.notify(R.string.note_alarm_msg,
				mNotification);
	}

	@Override
	public void onCreate() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();
		mWorkThread = new Thread(null, mWorkTask, ALARM_SERVICE_THREAD);
		mWorkThread.start();
	}

	@Override
	public void onDestroy() {
		mNotificationManager.cancel(R.string.note_alarm_settings_ok);
	}
}
