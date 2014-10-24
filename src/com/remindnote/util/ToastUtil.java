package com.remindnote.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * toast util
 * @author guangxiang.liang
 *
 */
public class ToastUtil {
	public static void doUiToast(final Activity act,final Context ctx, final String msg, final int type) {
		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(ctx, msg, type).show();
			}
		});
	}
	public static void doToast(final Context ctx, final String msg, final int type) {
				Toast.makeText(ctx, msg, type).show();
	}
}
