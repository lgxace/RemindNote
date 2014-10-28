package com.remindnote.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lgxace.remindnote.R;
import com.remindnote.entity.Note;
import com.remindnote.remind.DateOperation;
import com.remindnote.remind.RemindOperation;

/**
 * used to adapt notes to list view activity
 * 
 * @author guangxiang.liang
 * 
 */
public class NoteAdapterV1 extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Note> mNotes;
	private String country=null;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public NoteAdapterV1(Context context, List<Note> notes) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mNotes = notes;
		country=context.getResources().getConfiguration().locale.getCountry();
	}

	@Override
	public int getCount() {
		return mNotes.size();
	}

	@Override
	public Note getItem(int arg0) {
		return mNotes.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder;
		if (null == arg1) {
			holder = new ViewHolder();
			arg1 = mInflater.inflate(R.layout.activity_note_item, null);
			holder.tv_id_note_item_title = (TextView) arg1
					.findViewById(R.id.tv_id_note_item_title);
			holder.tv_id_note_item_author = (TextView) arg1
					.findViewById(R.id.tv_id_note_item_author);
			holder.tv_id_note_item_note_date = (TextView) arg1
					.findViewById(R.id.tv_id_note_item_note_date);
			holder.tv_id_note_item_note_share = (TextView) arg1
					.findViewById(R.id.tv_id_note_item_note_share);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		Note note = mNotes.get(arg0);

		Log.d("remindnote_test", "getView(),remindtime="+note.getmReindTime());
		Log.d("remindnote_test", "getView(),currenttime="+new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
		showRemindNoteInText(note, arg1);

		if (note.getmNoteType() == RemindOperation.NOTE_TYPE_AUDIO) {
			ImageView imageView = (ImageView) arg1
					.findViewById(R.id.image_note_type);
			imageView.setImageResource(R.drawable.img_audio);
		} else if (note.getmNoteType() == RemindOperation.NOTE_TYPE_VIDEO) {
			ImageView imageView = (ImageView) arg1
					.findViewById(R.id.image_note_type);
			imageView.setImageResource(R.drawable.img_video);
		} else {
			ImageView imageView = (ImageView) arg1
					.findViewById(R.id.image_note_type);
			imageView.setImageResource(R.drawable.img_text);
		}
		holder.tv_id_note_item_author.setText(note.getmUserID());
		holder.tv_id_note_item_title.setText(note.getmNoteTitle() + " ");
		holder.tv_id_note_item_note_date.setText(note.getmNoteDate());
		
		/**********************detect which country*****************************/
		if(country.equals("CN")){
			holder.tv_id_note_item_note_share
			.setText(note.getmShareType() == 1 ? "已分享" : "未分享");
		}else {
			holder.tv_id_note_item_note_share
			.setText(note.getmShareType() == 1 ? "shared" : "not to share");
		}
		arg1.setTag(holder);
		return arg1;
	}

	private final class ViewHolder {
		TextView tv_id_note_item_title;
		TextView tv_id_note_item_author;
		TextView tv_id_note_item_note_date;
		TextView tv_id_note_item_note_share;

	}

	private Calendar analysisReindTime(String remindTime) {
		int year = Integer.parseInt(remindTime.substring(0, 3));
		int month = Integer.parseInt(remindTime.substring(5, 6));
		int day = Integer.parseInt(remindTime.substring(8, 9));
		int hour = Integer.parseInt(remindTime.substring(12, 13));
		int minute = Integer.parseInt(remindTime.substring(14, 15));

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		return calendar;
	}

	private void showRemindNoteInText(final Note note, final View view) {
		Log.d("remindnote_test", "getView(),start showRemindNoteInText");
		if (!note.getmReindTime().equals("")) {
			long diffHours = getDiffInHour(note);
			long diffMinutes = getDiffInMinute(note);

			if (diffMinutes <= 5 && diffMinutes >= 0) {
				view.setBackgroundColor(Color.RED);
				Log.d("remindnote_test", "showRemindNoteInText(),diffMinutes="
						+ diffMinutes + ",item color=red");
				Log.d("remindnote_test", "remindtime=" + note.getmReindTime());
			} else if (diffHours <= 6 && diffHours >= 0) {
				view.setBackgroundColor(Color.YELLOW);
				Log.d("remindnote_test", "showRemindNoteInText(),diffHours="
						+ diffHours + ",item color=yellow");
				Log.d("remindnote_test", "remindtime=" + note.getmReindTime());
			}
		}
	}

	private long getDiffInHour(Note note) {
		Date date = null;
		try {
			date = dateFormat.parse(note.getmReindTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (date.getTime() - Calendar.getInstance().getTime().getTime())
				/ DateOperation.LONG_HOUR_BASE;
	}

	private long getDiffInMinute(Note note) {
		Date date = null;
		try {
			date = dateFormat.parse(note.getmReindTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date date2 = Calendar.getInstance().getTime();
		return (date.getTime() - Calendar.getInstance().getTime().getTime())
				/ DateOperation.LONG_MINUTE_BASE;
	}
}
