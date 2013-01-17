package com.viisi.droid.missedcallnotification.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.viisi.droid.missedcallnotification.R;
import com.viisi.droid.missedcallnotification.activity.missedcallslog.MainTabActivity;
import com.viisi.droid.missedcallnotification.entity.EnumOptionsExpandableList;
import com.viisi.droid.missedcallnotification.util.Constants;

public class MissedCallNotificationActivity extends Activity {

	private SharedPreferences sharedPreferences;

	private TextView statusText;
	private Button changeStatus;
	private Button settings;
	private Button notifications;
	private RadioGroup radioNotificationMethodEmailSmsGroup;
	private RadioButton radioNotificationMethodEmail;
	private RadioButton radioNotificationMethodSms;
	private int checkedNotificationMethed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		createComponentsView();
		createComponentsListeners();
		setInitialConfiguration();
		setStatusText();
		setNotificationBy();
	}

	private void setInitialConfiguration() {
		setDefaultDelayTime();
	}

	private void setDefaultDelayTime() {
		int delayTime = getSharedPreferences().getInt(Constants.preferences.preference_delay_time, EnumOptionsExpandableList.NOT_DEFINED.getId());
		if (delayTime == EnumOptionsExpandableList.NOT_DEFINED.getId()) {
			SharedPreferences.Editor editor = getSharedPreferences().edit();
			editor.putInt(Constants.preferences.preference_delay_time, EnumOptionsExpandableList.IMMEDIATELY.getId());
			editor.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setNotificationBy();
	}

	private OnCheckedChangeListener radioNotificationMethodChangedListener = new OnCheckedChangeListener() {
		@SuppressLint("HandlerLeak")
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			final ProgressDialog dialog = createProgressDialog();
			final int checkedIdF = checkedId;

			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					dialog.dismiss();
				}
			};

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					checkedNotificationMethed = checkedIdF;
					SharedPreferences.Editor editor = getSharedPreferences().edit();

					if (isMailMethodRadioSelected()) {
						editor.putBoolean(Constants.preferences.preference_by_mail, true);
						editor.putBoolean(Constants.preferences.preference_by_sms, false);
					} else {
						editor.putBoolean(Constants.preferences.preference_by_mail, false);
						editor.putBoolean(Constants.preferences.preference_by_sms, true);
					}
					editor.commit();

					Message msg = new Message();
					handler.sendMessage(msg);
				}
			});
		}

		private ProgressDialog createProgressDialog() {
			final ProgressDialog dialog = ProgressDialog.show(MissedCallNotificationActivity.this, getApplicationContext().getString(R.string.app_name), getApplicationContext().getString(R.string.loading_label), false, false);
			dialog.setIcon(R.drawable.ic_launcher);
			return dialog;
		}
	};

	private boolean isMailMethodRadioSelected() {
		if (checkedNotificationMethed == 0 || checkedNotificationMethed == R.id.radioNotificationMethodEmail) {
			return true;
		}
		return false;
	}

	private void setNotificationBy() {
		if (hasMailPreferenceSaved()) {
			radioNotificationMethodEmail.setClickable(true);
		}
		if (hasSmsPreferenceSaved()) {
			radioNotificationMethodSms.setClickable(true);
		}

		if (radioNotificationMethodEmail.isClickable()) {
			Boolean preferenceMail = getSharedPreferences().getBoolean(Constants.preferences.preference_by_mail, false);
			if (preferenceMail) {
				radioNotificationMethodEmail.setChecked(true);
			}
		}
		if (radioNotificationMethodSms.isClickable()) {
			Boolean preferenceSms = getSharedPreferences().getBoolean(Constants.preferences.preference_by_sms, false);
			if (preferenceSms) {
				radioNotificationMethodSms.setChecked(true);
			}
		}
	}

	private boolean hasMailPreferenceSaved() {
		String mail = getSharedPreferences().getString(Constants.preferences.preference_mail, "");
		if (mail != null && !mail.equals("")) {
			return true;
		}
		return false;
	}

	private boolean hasSmsPreferenceSaved() {
		String sms = getSharedPreferences().getString(Constants.preferences.preference_sms, "");
		if (sms != null && !sms.equals("")) {
			return true;
		}
		return false;
	}

	private void setStatusText() {
		Boolean status = getSharedPreferences().getBoolean(Constants.preferences.preference_status, false);
		StringBuilder sb = new StringBuilder("Status: ");

		if (status) {
			sb.append(getApplicationContext().getString(R.string.status_active));
		} else {
			sb.append(getApplicationContext().getString(R.string.status_inactive));
		}
		statusText.setText(sb.toString());
	}

	private OnClickListener changeStatusListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Boolean status = getSharedPreferences().getBoolean(Constants.preferences.preference_status, false);
			SharedPreferences.Editor editor = getSharedPreferences().edit();
			StringBuilder sb = new StringBuilder("Status: ");

			if (status) {
				editor.putBoolean(Constants.preferences.preference_status, false);
				sb.append(getApplicationContext().getString(R.string.status_inactive));
			} else {
				editor.putBoolean(Constants.preferences.preference_status, true);
				sb.append(getApplicationContext().getString(R.string.status_active));
			}
			statusText.setText(sb.toString());
			editor.commit();
		}
	};

	private OnClickListener settingsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), PreferencesActivity.class);
			startActivity(i);
		}
	};

	private OnClickListener notificationsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getBaseContext(), MainTabActivity.class);
			startActivity(i);
		}
	};

	private void createComponentsView() {
		statusText = (TextView) findViewById(R.id.status);
		changeStatus = (Button) this.findViewById(R.id.changeStatus);
		settings = (Button) this.findViewById(R.id.settings);
		notifications = (Button) this.findViewById(R.id.notifications);
		radioNotificationMethodEmailSmsGroup = (RadioGroup) this.findViewById(R.id.radioNotificationMethodEmailSmsGroup);
		radioNotificationMethodEmail = (RadioButton) this.findViewById(R.id.radioNotificationMethodEmail);
		radioNotificationMethodSms = (RadioButton) this.findViewById(R.id.radioNotificationMethodSms);
	}

	private void createComponentsListeners() {
		changeStatus.setOnClickListener(changeStatusListener);
		settings.setOnClickListener(settingsListener);
		notifications.setOnClickListener(notificationsListener);
		radioNotificationMethodEmailSmsGroup.setOnCheckedChangeListener(radioNotificationMethodChangedListener);
	}

	public SharedPreferences getSharedPreferences() {
		if (sharedPreferences == null) {
			sharedPreferences = getApplicationContext().getSharedPreferences(Constants.preferences.preferencefilename, 0);
		}
		return sharedPreferences;
	}

}
