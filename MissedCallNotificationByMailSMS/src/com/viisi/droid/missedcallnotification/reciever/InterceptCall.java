package com.viisi.droid.missedcallnotification.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.viisi.droid.missedcallnotification.service.ScheduleNotificationService;
import com.viisi.droid.missedcallnotification.util.Constants;

public class InterceptCall extends BroadcastReceiver {

	public void onReceive(Context ctx, Intent it) {
		if (isMissedCallNotificationActive(ctx)) {
			String callState = it.getStringExtra(Constants.callstate.call_state);
			String incomingNumber = it.getStringExtra(Constants.callstate.incoming_number);

			if (isMissedCallState(callState, incomingNumber)) {
				Intent intentSMS = new Intent(ctx, ScheduleNotificationService.class);

				intentSMS.putExtra(Constants.geral.missed_call_number, incomingNumber);

				ctx.startService(intentSMS);
			}
		}
	}

	private boolean isMissedCallNotificationActive(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.preferences.preferencefilename, 0);
		return prefs.getBoolean(Constants.preferences.preference_status, false) && (prefs.getBoolean(Constants.preferences.preference_by_mail, false) || prefs.getBoolean(Constants.preferences.preference_by_sms, false));
	}

	private boolean isMissedCallState(String callState, String incomingNumber) {
		if (callState.equals(Constants.callstate.state_idle)) {
			if (incomingNumber != null) { // não atendida
				return true;
			}
		}
		return false;
	}

}
