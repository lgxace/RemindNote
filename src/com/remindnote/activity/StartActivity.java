package com.remindnote.activity;

import java.io.File;
import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.widget.ProgressBar;

import com.lgxace.remindnote.R;
/**
 * the start activity
 * @author guangxiang.liang
 *
 */
public class StartActivity extends Activity implements Serializable {
	private Handler mHandler = new Handler();
	private int mProgressStatus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_activity);
		createFile();//generate audio folder and video folder in sdcard
		final ProgressBar pb = (ProgressBar) findViewById(R.id.pgbar_id__start);

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (mProgressStatus < 100) {
					mProgressStatus++;
					doPause();
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							pb.setProgress(mProgressStatus);
						}
					});
				}
				Intent intent = new Intent(StartActivity.this,
						NoteListActivity.class);
				startActivity(intent);
				StartActivity.this.finish();
			}
		}).start();

	}

	private void doPause() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.start_activity, menu);
		return true;
	}
	/**
	 * start to generate media note folder in sd card
	 */
	private void createFile() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String audio_path = sdcardDir.getPath() + "/notesEX/audio/";
			String video_path = sdcardDir.getPath() + "/notesEX/video/";
			File path = new File(audio_path);
			if (!path.exists())
				path.mkdirs();
			path = new File(video_path);
			if (!path.exists())
				path.mkdirs();
		}
	}
}
