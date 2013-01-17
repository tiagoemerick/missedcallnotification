package com.viisi.droid.missedcallnotification.sqlite;

import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.viisi.droid.missedcallnotification.entity.IEntity;

public abstract class MissedCallNotiDS {

	protected SQLiteDatabase database;
	private SQLiteHelper dbHelper;

	protected MissedCallNotiDS(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	protected void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	protected void close() {
		dbHelper.close();
	}

	public abstract Long insert(Object object);

	public abstract void updateState(Long id, Long idState, Long sentTime);

	public abstract void delete(Object object);

	public abstract void deleteAll();

	public abstract List<? extends IEntity> findAll();

	public abstract List<? extends IEntity> findAllToSendState();

	public abstract List<? extends IEntity> findAllSentState();

	public abstract List<? extends IEntity> findAllErrorState();

	public abstract Object findById(Long id);

	public abstract int count();
}
