package com.viisi.droid.missedcallnotification.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.viisi.droid.missedcallnotification.entity.EnumLogState;
import com.viisi.droid.missedcallnotification.entity.EnumNotificationType;
import com.viisi.droid.missedcallnotification.entity.EnumOptionsExpandableList;
import com.viisi.droid.missedcallnotification.entity.MissedCallLog;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallLogDAO;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallNotiDS;
import com.viisi.droid.missedcallnotification.util.Constants;

public class ScheduleNotificationService extends Service {

	private MissedCallNotiDS missedCallNotiDS;
	private SharedPreferences sharedPreferences;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Bundle intentExtras = intent.getExtras();
			if (intentExtras != null) {
				String missedCallNumber = intentExtras.getString(Constants.geral.missed_call_number);
				
				scheduleNotification(missedCallNumber);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void scheduleNotification(String missedCallNumber) {
		int actualDelayTime = getSharedPreferences().getInt(Constants.preferences.preference_delay_time, EnumOptionsExpandableList.NOT_DEFINED.getId());
		if (actualDelayTime != EnumOptionsExpandableList.NOT_DEFINED.getId()) {
			Calendar timeScheduled = getTimeToSchedule(actualDelayTime);

			boolean hasMail = getSharedPreferences().getBoolean(Constants.preferences.preference_by_mail, false);
			if (hasMail) {
				schedule(missedCallNumber, timeScheduled, EnumNotificationType.MAIL_TYPE.getType());
			}
			boolean hasSms = getSharedPreferences().getBoolean(Constants.preferences.preference_by_sms, false);
			if (hasSms) {
				schedule(missedCallNumber, timeScheduled, EnumNotificationType.SMS_TYPE.getType());
			}
		}
	}

	private void schedule(String missedCallNumber, Calendar timeScheduled, String serviceNotificationType) {
		Long idMissedCallLog = insertMissedCallLog(missedCallNumber, timeScheduled, serviceNotificationType);

		Random s = new Random(System.currentTimeMillis());
		String SCHEDULE_TYPE = serviceNotificationType + s.nextLong();

		Intent intentMail = new Intent(SCHEDULE_TYPE, null, getApplicationContext(), FilterNotificationService.class);
		// Coloca os dois para conferir depois quando o evento for disparado
		intentMail.putExtra(Constants.geral.missed_call_number, missedCallNumber);
		intentMail.putExtra(Constants.tablemissedcallslog.column_id, idMissedCallLog);

		PendingIntent mailPI = PendingIntent.getService(ScheduleNotificationService.this, 0, intentMail, PendingIntent.FLAG_ONE_SHOT);

		AlarmManager am = (AlarmManager) ScheduleNotificationService.this.getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, timeScheduled.getTimeInMillis(), 0, mailPI);
	}

	private Long insertMissedCallLog(String missedCallNumber, Calendar timeScheduled, String classToStringType) {
		MissedCallLog mcl = new MissedCallLog();

		String contactName = getContactDisplayNameByNumber(missedCallNumber);

		mcl.setMissedNumber(missedCallNumber);
		mcl.setContactName(contactName);
		mcl.setLogTime(new Date());
		mcl.setScheduleTime(timeScheduled.getTime());
		long idType = EnumNotificationType.findByType(classToStringType).getId();
		mcl.setNotificationType(idType);
		long idState = EnumLogState.TO_SEND.getId();
		mcl.setState(idState);

		Long idLogInserted = getMissedCallNotiDSLog().insert(mcl);

		return idLogInserted;
	}

	public String getContactDisplayNameByNumber(String missedCallNumber) {
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(missedCallNumber));
		String name = null;

		ContentResolver contentResolver = getContentResolver();
		Cursor contactLookup = contentResolver.query(uri, new String[] { BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

		try {
			if (contactLookup != null && contactLookup.getCount() > 0) {
				contactLookup.moveToNext();
				name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
			}
		} finally {
			if (contactLookup != null) {
				contactLookup.close();
			}
		}

		return name != null ? "(" + name + ")" : "";
	}

	private Calendar getTimeToSchedule(int actualDelayTime) {
		EnumOptionsExpandableList optionDelay = EnumOptionsExpandableList.find(actualDelayTime);

		Calendar timetoSchedule = Calendar.getInstance();

		if (optionDelay.equals(EnumOptionsExpandableList.MINUTES_15)) {
			timetoSchedule.set(Calendar.MINUTE, (timetoSchedule.get(Calendar.MINUTE) + EnumOptionsExpandableList.MINUTES_15.getDelayTime()));
		} else if (optionDelay.equals(EnumOptionsExpandableList.MINUTES_30)) {
			timetoSchedule.set(Calendar.MINUTE, (timetoSchedule.get(Calendar.MINUTE) + EnumOptionsExpandableList.MINUTES_30.getDelayTime()));
		} else if (optionDelay.equals(EnumOptionsExpandableList.MINUTES_60)) {
			timetoSchedule.set(Calendar.MINUTE, (timetoSchedule.get(Calendar.MINUTE) + EnumOptionsExpandableList.MINUTES_60.getDelayTime()));
		}

		return timetoSchedule;
	}

	public SharedPreferences getSharedPreferences() {
		if (sharedPreferences == null) {
			sharedPreferences = getApplicationContext().getSharedPreferences(Constants.preferences.preferencefilename, 0);
		}
		return sharedPreferences;
	}

	private MissedCallNotiDS getMissedCallNotiDSLog() {
		if (missedCallNotiDS == null) {
			missedCallNotiDS = new MissedCallLogDAO(getBaseContext());
		}
		return missedCallNotiDS instanceof MissedCallLogDAO ? missedCallNotiDS : (missedCallNotiDS = new MissedCallLogDAO(getBaseContext()));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
