package com.remindnote.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.widget.SimpleAdapter;

import com.lgxace.remindnote.R;
import com.remindnote.entity.Note;


public class NoteAdapter {
	private int noteTile=R.id.tv_id_note_item_title;
	private int noteAuthor=R.id.tv_id_note_item_author;
	private int noteDate=R.id.tv_id_note_item_note_date;
	private int noteShareType=R.id.tv_id_note_item_note_share;
	private int[] to={noteTile,noteAuthor,noteDate,noteShareType};
	private String[] from={"noteTile","noteAuthor","noteDate","noteShareType"};
	
	public SimpleAdapter getNoteAdapter(Context context,List<Note> notes){
		if(notes==null||notes.size()<=0) return null;
		List<HashMap<String,String>> maps=new ArrayList<HashMap<String,String>>();
		for(Note note:notes){
			HashMap<String,String> map=new HashMap<String, String>();
			map.put("noteTile", note.getmNoteTitle());
			map.put("noteAuthor", note.getmUserID());
			map.put("noteDate", note.getmNoteDate());
			map.put("noteShareType",getShareInfo(note.getmShareType()));
			map.put("noteId", note.getmNoteID());
			map.put("userId", note.getmUserID());
			maps.add(map);
		}
		return new SimpleAdapter(context, maps, R.layout.activity_note_item, from, to);
	}
	private String getShareInfo(int shareType){
		return shareType==1?"已分享":"未分享";
	}
	
}
