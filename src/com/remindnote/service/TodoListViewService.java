package com.remindnote.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.lgxace.remindnote.R;
import com.remindnote.entity.Note;
import com.remindnote.remind.DateOperation;
import com.remindnote.remind.RemindOperation;
import com.remindnote_db.NoteTable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class TodoListViewService extends RemoteViewsService {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		NoteTable table = new NoteTable(getApplicationContext());
		List<Note> notes1 = table.queryAll();
		if (notes1 == null || notes1.size() <= 0)
			return null;
		List<Note> notes2 = new ArrayList<Note>();
		for (Note note : notes1) {
			if (!note.getmReindTime().equals(""))
				notes2.add(note);
		}
		return new ListRemoteViewsFactory(this, notes2);
	}

	private static class ListRemoteViewsFactory implements RemoteViewsFactory {
		private List<Note> mList;
		private Context mContext;

		public ListRemoteViewsFactory(Context context, List<Note> list) {
			mList = list;
			mContext = context;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public RemoteViews getViewAt(int position) {
			RemoteViews rv = new RemoteViews(mContext.getPackageName(),
					R.layout.activity_note_item);
			Note note = mList.get(position);
			rv.setTextViewText(R.id.tv_id_note_item_title, note.getmNoteTitle());
			rv.setTextViewText(R.id.tv_id_note_item_author, note.getmUserID());
			rv.setTextViewText(R.id.tv_id_note_item_note_date,
					note.getmNoteDate());
			rv.setTextViewText(R.id.tv_id_note_item_note_share,
					note.getmShareType() == 1 ? "已分享" : "未分享");
			
			//add note type icon in widget note item
			if(note.getmNoteType()==RemindOperation.NOTE_TYPE_TEXT){
				rv.setImageViewResource(R.id.image_note_type, R.drawable.img_text);
			}
			else if(note.getmNoteType()==RemindOperation.NOTE_TYPE_AUDIO){
				rv.setImageViewResource(R.id.image_note_type, R.drawable.img_audio);
			}
			else if(note.getmNoteType()==RemindOperation.NOTE_TYPE_VIDEO){
				rv.setImageViewResource(R.id.image_note_type, R.drawable.img_video);
			}
			
			Intent intent = new Intent(mContext, WidgetClickHanderService.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			rv.setOnClickFillInIntent(R.id.item_layout, intent);
			showRemindNoteInText(note, rv);
			return rv;
		}

		@Override
		public RemoteViews getLoadingView() {
			return null;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public void onCreate() {

		}

		@Override
		public void onDataSetChanged() {
		}

		@Override
		public void onDestroy() {
		}
	}

	private static long getDiffInHour(Note note) {
		Date date = null;
		try {
			date = dateFormat.parse(note.getmReindTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (date.getTime() - Calendar.getInstance().getTime().getTime())
				/ DateOperation.LONG_HOUR_BASE;
	}

	private static long getDiffInMinute(Note note) {
		Date date = null;
		try {
			date = dateFormat.parse(note.getmReindTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (date.getTime() - Calendar.getInstance().getTime().getTime())
				/ DateOperation.LONG_MINUTE_BASE;
	}

	private static void showRemindNoteInText(final Note note,
			final RemoteViews view) {
		if (!note.getmReindTime().equals("")) {
			long diffHours = getDiffInHour(note);
			long diffMinutes = getDiffInMinute(note);
			if (diffMinutes <= 5 && diffMinutes >= 0) {
				view.setTextColor(R.id.tv_id_note_item_title, Color.RED);
			} else if (diffHours <= 6 && diffHours >= 0) {
				view.setTextColor(R.id.tv_id_note_item_title, Color.YELLOW);
			}
		}
	}
}
