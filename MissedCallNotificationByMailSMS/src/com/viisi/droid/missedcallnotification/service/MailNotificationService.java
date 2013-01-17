package com.viisi.droid.missedcallnotification.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;

import com.viisi.droid.missedcallnotification.R;
import com.viisi.droid.missedcallnotification.entity.EnumLogState;
import com.viisi.droid.missedcallnotification.entity.MissedCallLog;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallLogDAO;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallNotiDS;
import com.viisi.droid.missedcallnotification.util.Constants;
import com.viisi.droid.missedcallnotification.util.Mail;

public class MailNotificationService extends Service {

	private MissedCallNotiDS missedCallNotiDS;
	private SharedPreferences sharedPreferences;

	private Long sentTime;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Bundle intentExtras = intent.getExtras();
			if (intentExtras != null) {
				String missedCallNumber = intentExtras.getString(Constants.geral.missed_call_number);
				Long idMissedCallLog = intentExtras.getLong(Constants.tablemissedcallslog.column_id);
				sentTime = intentExtras.getLong(Constants.tablemissedcallslog.column_scheduletime);
				
				if (stillNotificationActive(idMissedCallLog)) {
					String mailBody = getMailBody(missedCallNumber, idMissedCallLog);
					
					sendMail(missedCallNumber, mailBody, idMissedCallLog);
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private boolean stillNotificationActive(Long idMissedCallLog) {
		MissedCallLog mcl = (MissedCallLog) getMissedCallNotiDSLog().findById(idMissedCallLog);
		if (mcl != null && mcl.getId() != null) {
			long idToSendState = EnumLogState.TO_SEND.getId();
			if (mcl.getState() != null && mcl.getState().compareTo(idToSendState) == 0) {
				return true;
			}
		}
		return false;
	}

	private String getMailBody(String missedCallNumber, Long idMissedCallLog) {
		StringBuilder sb = new StringBuilder();
		sb.append(getApplicationContext().getString(R.string.mail_body_1));
		sb.append(" ");
		sb.append(missedCallNumber);

		MissedCallLog mcl = (MissedCallLog) getMissedCallNotiDSLog().findById(idMissedCallLog);
		if (mcl != null) {
			if (mcl.getContactName() != null) {
				sb.append(mcl.getContactName());
			}
			if (mcl.getLogTime() != null) {
				sb.append(" ");
				sb.append(getApplicationContext().getString(R.string.mail_body_2));
				sb.append(" ");
				sb.append(mcl.getLogTimeFormated());
			}
		}

		return sb.toString();
	}

	private void sendMail(String missedCallNumber, String mailBody, final Long idMissedCallLog) {
		final Mail m = new Mail();

		String mailto = getSharedPreferences().getString(Constants.preferences.preference_mail, "");
		if (!mailto.trim().equals("")) {

			// TODO: alterar o email remetente: criar
			String[] toArr = { mailto };
			m.setTo(toArr);
			m.setFrom(Constants.mail.mail_send_fakefrom);
			m.setSubject(getApplicationContext().getString(R.string.mail_subject));
			m.setBody(mailBody);

			Runnable runnableSendMail = new Runnable() {
				@Override
				public void run() {
					boolean statusSend = false;
					// TODO: adicionar notificacao de erro
					try {
						statusSend = m.send();
					} catch (Exception e) {
					} finally {
						updateMissedCallLog(statusSend, idMissedCallLog);
					}
				}
			};
			Thread sendMailThread = new Thread(runnableSendMail);
			sendMailThread.start();
		}
	}

	private void updateMissedCallLog(boolean statusSend, Long idMissedCallLog) {
		Long state = 0l;
		if (!statusSend) {
			long idStateError = EnumLogState.ERROR.getId();
			state = idStateError;
		} else {
			long idStateSent = EnumLogState.SENT.getId();
			state = idStateSent;
		}

		if (state.compareTo(0l) != 0) {
			getMissedCallNotiDSLog().updateState(idMissedCallLog, state, sentTime);
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
