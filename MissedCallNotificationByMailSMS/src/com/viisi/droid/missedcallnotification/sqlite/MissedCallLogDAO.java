package com.viisi.droid.missedcallnotification.sqlite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.viisi.droid.missedcallnotification.entity.EnumLogState;
import com.viisi.droid.missedcallnotification.entity.MissedCallLog;
import com.viisi.droid.missedcallnotification.util.Constants;

public class MissedCallLogDAO extends MissedCallNotiDS {

	private String[] allColumns = { Constants.tablemissedcallslog.column_id, Constants.tablemissedcallslog.column_missednumber, Constants.tablemissedcallslog.column_contactname, Constants.tablemissedcallslog.column_notitype, Constants.tablemissedcallslog.column_state, Constants.tablemissedcallslog.column_logtime, Constants.tablemissedcallslog.column_scheduletime };

	public MissedCallLogDAO(Context context) {
		super(context);
	}

	@Override
	public Long insert(Object object) {
		open();
		MissedCallLog mcl = (MissedCallLog) object;

		ContentValues values = new ContentValues();
		values.put(Constants.tablemissedcallslog.column_missednumber, mcl.getMissedNumber());
		values.put(Constants.tablemissedcallslog.column_contactname, mcl.getContactName());
		values.put(Constants.tablemissedcallslog.column_notitype, mcl.getNotificationType());
		values.put(Constants.tablemissedcallslog.column_state, mcl.getState());
		values.put(Constants.tablemissedcallslog.column_logtime, mcl.getLogTime().getTime());
		values.put(Constants.tablemissedcallslog.column_scheduletime, mcl.getScheduleTime().getTime());

		long idInserted = database.insert(Constants.tablemissedcallslog.table_name, null, values);
		close();

		return idInserted;
	}

	@Override
	public void delete(Object object) {
	}

	@Override
	public void deleteAll() {
	}

	@Override
	public List<MissedCallLog> findAll() {
		return null;
	}

	@Override
	public List<MissedCallLog> findAllToSendState() {
		open();
		List<MissedCallLog> logsToSend = new ArrayList<MissedCallLog>();

		Cursor cursor = database.query(Constants.tablemissedcallslog.table_name, allColumns, Constants.tablemissedcallslog.column_state + " = " + EnumLogState.TO_SEND.getId(), null, null, null, Constants.tablemissedcallslog.column_scheduletime + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MissedCallLog mcl = cursorToMissedCallLog(cursor);
			logsToSend.add(mcl);
			cursor.moveToNext();
		}
		cursor.close();
		close();

		return logsToSend;
	}

	@Override
	public List<MissedCallLog> findAllSentState() {
		open();
		List<MissedCallLog> logsToSend = new ArrayList<MissedCallLog>();

		Cursor cursor = database.query(Constants.tablemissedcallslog.table_name, allColumns, Constants.tablemissedcallslog.column_state + " = " + EnumLogState.SENT.getId(), null, null, null, Constants.tablemissedcallslog.column_scheduletime + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MissedCallLog mcl = cursorToMissedCallLog(cursor);
			logsToSend.add(mcl);
			cursor.moveToNext();
		}
		cursor.close();
		close();

		return logsToSend;
	}

	@Override
	public List<MissedCallLog> findAllErrorState() {
		open();
		List<MissedCallLog> logsToSend = new ArrayList<MissedCallLog>();

		Cursor cursor = database.query(Constants.tablemissedcallslog.table_name, allColumns, Constants.tablemissedcallslog.column_state + " = " + EnumLogState.ERROR.getId(), null, null, null, Constants.tablemissedcallslog.column_scheduletime + " DESC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MissedCallLog mcl = cursorToMissedCallLog(cursor);
			logsToSend.add(mcl);
			cursor.moveToNext();
		}
		cursor.close();
		close();

		return logsToSend;
	}

	@Override
	public MissedCallLog findById(Long id) {
		MissedCallLog mcl = new MissedCallLog();
		if (id != null && id.compareTo(0l) != 0) {
			open();

			Cursor cursor = database.query(Constants.tablemissedcallslog.table_name, allColumns, Constants.tablemissedcallslog.column_id + " = " + id, null, null, null, null);
			cursor.moveToFirst();
			mcl = cursorToMissedCallLog(cursor);
			cursor.close();
			close();
		}
		return mcl;
	}

	@Override
	public int count() {
		return 0;
	}

	private MissedCallLog cursorToMissedCallLog(Cursor cursor) {
		MissedCallLog mcl = new MissedCallLog();
		mcl.setId(cursor.getLong(0));
		mcl.setMissedNumber(cursor.getString(1));
		mcl.setContactName(cursor.getString(2));
		mcl.setNotificationType(cursor.getLong(3));
		mcl.setState(cursor.getLong(4));

		int dc = cursor.getColumnIndex(Constants.tablemissedcallslog.column_logtime);
		mcl.setLogTime(new Date(cursor.getLong(dc)));

		int st = cursor.getColumnIndex(Constants.tablemissedcallslog.column_scheduletime);
		mcl.setScheduleTime(new Date(cursor.getLong(st)));

		return mcl;
	}

	public void updateState(Long id, Long idState, Long sentTime) {
		open();

		ContentValues values = new ContentValues();
		values.put(Constants.tablemissedcallslog.column_state, idState);
		if (sentTime != null) {
			values.put(Constants.tablemissedcallslog.column_scheduletime, sentTime);
		}

		database.update(Constants.tablemissedcallslog.table_name, values, Constants.tablemissedcallslog.column_id + " = " + id, null);
		close();
	}
}
