package com.remindnote.entity;

/**
 * Note entity of its data table
 * @author guangxiang.liang
 *
 */
public class Note{
	private String mNoteID;
	private String mUserID;
	private int mShareType;
	private String mNoteDate;
	private String mNoteTitle;
	private String mNoteContent;
	private String mReindTime;
	private int mRemindType;
	private int mNoteType;
	public static final int SHARE_TYPE_NO = 0;
	public static final int SHARE_TYPE_YES = 1;

	public Note() {
	}


	public String getmReindTime() {
		return mReindTime;
	}


	public void setmReindTime(String mReindTime) {
		this.mReindTime = mReindTime;
	}


	
	public Note(String mNoteID, String mUserID, int mShareType,
			String mNoteDate, String mNoteTitle, String mNoteContent,
			String mReindTime, int mRemindType, int mNoteType) {
		this.mNoteID = mNoteID;
		this.mUserID = mUserID;
		this.mShareType = mShareType;
		this.mNoteDate = mNoteDate;
		this.mNoteTitle = mNoteTitle;
		this.mNoteContent = mNoteContent;
		this.mReindTime = mReindTime;
		this.mRemindType = mRemindType;
		this.mNoteType = mNoteType;
	}


	public int getmRemindType() {
		return mRemindType;
	}


	public void setmRemindType(int mRemindType) {
		this.mRemindType = mRemindType;
	}


	public int getmNoteType() {
		return mNoteType;
	}


	public void setmNoteType(int mNoteType) {
		this.mNoteType = mNoteType;
	}


	public String getmNoteID() {
		return mNoteID;
	}

	public void setmNoteID(String mNoteID) {
		this.mNoteID = mNoteID;
	}

	public String getmUserID() {
		return mUserID;
	}

	public void setmUserID(String mUserID) {
		this.mUserID = mUserID;
	}

	public int getmShareType() {
		return mShareType;
	}

	public void setmShareType(int mShareType) {
		this.mShareType = mShareType;
	}

	public String getmNoteDate() {
		return mNoteDate;
	}

	public void setmNoteDate(String mNoteDate) {
		this.mNoteDate = mNoteDate;
	}

	public String getmNoteTitle() {
		return mNoteTitle;
	}

	public void setmNoteTitle(String mNoteTitle) {
		this.mNoteTitle = mNoteTitle;
	}

	public String getmNoteContent() {
		return mNoteContent;
	}

	public void setmNoteContent(String mNoteContent) {
		this.mNoteContent = mNoteContent;
	}

	
}
