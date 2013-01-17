package com.viisi.droid.missedcallnotification.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;

import com.viisi.droid.missedcallnotification.entity.EnumLogState;
import com.viisi.droid.missedcallnotification.entity.EnumNotificationType;
import com.viisi.droid.missedcallnotification.entity.MissedCallLog;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallLogDAO;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallNotiDS;
import com.viisi.droid.missedcallnotification.util.Constants;

public class FilterNotificationService extends Service {

	private MissedCallNotiDS missedCallNotiDS;
	private SharedPreferences sharedPreferences;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle intentExtras = intent.getExtras();
		if (intentExtras != null) {
			String missedCallNumber = intentExtras.getString(Constants.geral.missed_call_number);
			Long idMissedCallLog = intentExtras.getLong(Constants.tablemissedcallslog.column_id);
			if (missedCallNumber != null && idMissedCallLog != null) {
				sendNotification(missedCallNumber, idMissedCallLog);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void sendNotification(String missedCallNumber, Long idMissedCallLog) {
		MissedCallLog mcl = (MissedCallLog) getMissedCallNotiDSLog().findById(idMissedCallLog);
		if (mcl.getId() != null) {
			if (mcl.getId().compareTo(idMissedCallLog) == 0) {
				long idStateToSend = EnumLogState.TO_SEND.getId();
				if (mcl.getState().compareTo(idStateToSend) == 0) {
					long idNotfTypeMail = EnumNotificationType.MAIL_TYPE.getId();
					long idNotfTypeSms = EnumNotificationType.SMS_TYPE.getId();
					if (mcl.getNotificationType().compareTo(idNotfTypeMail) == 0) {

						Intent intentSMS = new Intent(getApplicationContext(), MailNotificationService.class);
						intentSMS.putExtra(Constants.geral.missed_call_number, missedCallNumber);
						intentSMS.putExtra(Constants.tablemissedcallslog.column_id, idMissedCallLog);
						getApplicationContext().startService(intentSMS);

					} else if (mcl.getNotificationType().compareTo(idNotfTypeSms) == 0) {

						Intent intentSMS = new Intent(getApplicationContext(), SmsNotificationService.class);
						intentSMS.putExtra(Constants.geral.missed_call_number, missedCallNumber);
						intentSMS.putExtra(Constants.tablemissedcallslog.column_id, idMissedCallLog);
						getApplicationContext().startService(intentSMS);
					}
				}
			}
		}
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
