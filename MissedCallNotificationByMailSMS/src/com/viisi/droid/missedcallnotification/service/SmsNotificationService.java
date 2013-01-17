package com.viisi.droid.missedcallnotification.service;

import java.util.Random;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.viisi.droid.missedcallnotification.R;
import com.viisi.droid.missedcallnotification.entity.EnumLogState;
import com.viisi.droid.missedcallnotification.entity.MissedCallLog;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallLogDAO;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallNotiDS;
import com.viisi.droid.missedcallnotification.util.Constants;
import com.viisi.droid.missedcallnotification.util.PDUUtil;
import com.viisi.droid.missedcallnotification.util.SendSMSBugCorrection;

public class SmsNotificationService extends Service {

	private static final String URI_SMS_SENT = "content://sms/sent";
	private static final String URI_SMS_FAILED = "content://sms/failed";

	private String statusMessageURI = URI_SMS_SENT;

	private MissedCallNotiDS missedCallNotiDS;
	private SharedPreferences sharedPreferences;
	private PendingIntent sentPI;

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
					String destinationNumber = getDestinationNumber();
					if (!destinationNumber.trim().equals("") && idMissedCallLog != null) {
						String messageBody = getMessageBody(missedCallNumber, idMissedCallLog);
						sendSms(missedCallNumber, destinationNumber, messageBody, idMissedCallLog);
					}
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

	private void sendSms(String missedCallNumber, String destinationNumber, String messageBody, Long idMissedCallLog) {
		sentPI = registroMensagemEnviada(destinationNumber, messageBody, idMissedCallLog);

		SmsManager smsManager = SmsManager.getDefault();
		SendSMSBugCorrection.send(destinationNumber, messageBody.trim(), sentPI, null, smsManager);
	}

	private PendingIntent registroMensagemEnviada(String phoneNumber, String message, final Long idMissedCallLog) {
		Random s = new Random(System.currentTimeMillis());
		StringBuilder SENT = new StringBuilder("SMS_SENT_NOTIFICATION");
		SENT.append(s.nextLong());

		String SENT_STRING = SENT.toString();
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT_STRING), PendingIntent.FLAG_ONE_SHOT);

		final String phoneNumberFinal = phoneNumber;
		final String messageFinal = message;

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					statusMessageURI = URI_SMS_SENT;
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					statusMessageURI = URI_SMS_FAILED;
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					statusMessageURI = URI_SMS_FAILED;
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					statusMessageURI = URI_SMS_FAILED;
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					statusMessageURI = URI_SMS_FAILED;
					break;
				}
				saveOutboxSMS(phoneNumberFinal, messageFinal);
				updateMissedCallLog(idMissedCallLog);
			}
		}, new IntentFilter(SENT_STRING));
		return sentPI;
	}

	private void updateMissedCallLog(Long idMissedCallLog) {
		if (statusMessageURI != null) {
			Long state = 0l;
			if (statusMessageURI.equalsIgnoreCase(URI_SMS_FAILED)) {
				long idStateError = EnumLogState.ERROR.getId();
				state = idStateError;
			} else if (statusMessageURI.equalsIgnoreCase(URI_SMS_SENT)) {
				long idStateSent = EnumLogState.SENT.getId();
				state = idStateSent;
			}
			if (state.compareTo(0l) != 0) {
				getMissedCallNotiDSLog().updateState(idMissedCallLog, state, sentTime);
			}
		}
	}

	private void saveOutboxSMS(String phoneNumber, String message) {
		ContentValues values = new ContentValues();
		values.put("address", phoneNumber);
		values.put("body", message);
		getContentResolver().insert(Uri.parse(statusMessageURI), values);
	}

	private String getMessageBody(String missedCallNumber, Long idMissedCallLog) {
		StringBuilder formatedMessage = new StringBuilder();

		formatedMessage.append(getApplicationContext().getString(R.string.sms_body_1));
		formatedMessage.append(missedCallNumber);

		MissedCallLog mcl = (MissedCallLog) getMissedCallNotiDSLog().findById(idMissedCallLog);
		if (mcl != null) {
			if (mcl.getContactName() != null) {
				formatedMessage.append(mcl.getContactName());
			}
			if (mcl.getLogTime() != null) {
				formatedMessage.append(" ");
				formatedMessage.append(getApplicationContext().getString(R.string.sms_body_2));
				formatedMessage.append(" ");
				formatedMessage.append(mcl.getLogTimeHourFormated());
			}
		}
		String finalTextMessage = formatedMessage.toString();
		finalTextMessage = getMessageConvertedInPDU(formatedMessage.toString());

		return finalTextMessage.trim();
	}

	private String getMessageConvertedInPDU(String formatedMessage) {
		StringBuilder finalTextMessage = new StringBuilder();

		int numerOfPDUToConvert = (formatedMessage.length() / 12) + 1;
		String[] messagesToConvertInPDUMode = new String[numerOfPDUToConvert];

		StringBuilder messagesToConvertInPDUModeAux = new StringBuilder();
		int numberPositionPDU = 0;
		for (char ch : formatedMessage.toString().toCharArray()) {
			if (messagesToConvertInPDUModeAux.length() < 12) {
				messagesToConvertInPDUModeAux.append(ch);
				messagesToConvertInPDUMode[numberPositionPDU] = messagesToConvertInPDUModeAux.toString();
			} else {
				messagesToConvertInPDUModeAux = new StringBuilder();
				numberPositionPDU++;
				messagesToConvertInPDUMode[numberPositionPDU] = messagesToConvertInPDUModeAux.append(ch).toString();
			}
		}

		final String beginningPDUText = "07911326040000F0040B911346610089F60000208062917314080C";
		for (int k = 0; k < messagesToConvertInPDUMode.length; k++) {
			if (messagesToConvertInPDUMode[k] != null) {
				if (messagesToConvertInPDUMode[k].length() < 12) {
					int count = 12 - messagesToConvertInPDUMode[k].length();
					for (int j = 0; j < count; j++) {
						messagesToConvertInPDUMode[k] += " ";
					}
				}
				SmsMessage createFromPduTemp = SmsMessage.createFromPdu(PDUUtil.hexStringToByteArray(beginningPDUText + PDUUtil.getMessageInPDUFormat(messagesToConvertInPDUMode[k])));
				finalTextMessage.append(createFromPduTemp.getMessageBody());
			}
		}
		return finalTextMessage.toString();
	}

	private String getDestinationNumber() {
		return getSharedPreferences().getString(Constants.preferences.preference_sms, "");
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
