package com.viisi.droid.missedcallnotification.activity.missedcallslog;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.viisi.droid.missedcallnotification.R;
import com.viisi.droid.missedcallnotification.adapter.MissedCallListAdapter;
import com.viisi.droid.missedcallnotification.entity.EnumLogState;
import com.viisi.droid.missedcallnotification.entity.EnumNotificationType;
import com.viisi.droid.missedcallnotification.entity.MissedCallLog;
import com.viisi.droid.missedcallnotification.service.MailNotificationService;
import com.viisi.droid.missedcallnotification.service.SmsNotificationService;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallLogDAO;
import com.viisi.droid.missedcallnotification.sqlite.MissedCallNotiDS;
import com.viisi.droid.missedcallnotification.util.Constants;
import com.viisi.droid.missedcallnotification.util.DataUtil;

public class SentTabActivity extends Activity {

	private MissedCallNotiDS missedCallNotiDS;
	private SharedPreferences sharedPreferences;

	private MissedCallListAdapter adapter;

	private ListView listSent;

	@Override
	@SuppressLint("HandlerLeak")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liststate_tab);

		final ProgressDialog dialog = createProgressDialog();
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
			}
		};

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				createComponentsView();
				createComponentsListeners();

				loadListAdapter();

				Message msg = new Message();
				handler.sendMessage(msg);
			}
		});
	}

	private OnItemClickListener optionsLogListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final int pos = position;

			AlertDialog.Builder builder = new AlertDialog.Builder(SentTabActivity.this);

			final CharSequence[] items = new CharSequence[4];
			items[0] = getApplicationContext().getString(R.string.list_option_call);
			items[1] = getApplicationContext().getString(R.string.list_option_sms);
			items[2] = getApplicationContext().getString(R.string.list_option_sendaagain);
			items[3] = getApplicationContext().getString(R.string.list_option_delete);

			String title = adapter.getItem(pos).getMissedNumber();
			builder.setTitle(title);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int numberSelectedOption) {
					MissedCallLog mclSelected = adapter.getItem(pos);

					doAction(mclSelected, numberSelectedOption);
				}

				private void doAction(MissedCallLog mclSelected, int numberSelectedOption) {
					switch (numberSelectedOption) {
					case 0:
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:" + mclSelected.getMissedNumber()));
						startActivity(callIntent);
						break;
					case 1:
						String uri = "smsto:" + mclSelected.getMissedNumber();
						Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
						intent.putExtra("sms_body", "");
						intent.putExtra("compose_mode", true);
						startActivity(intent);
						break;
					case 2:
						resendNotification(mclSelected);
						break;
					case 3:
						updateMissedCallLogState(mclSelected.getId(), EnumLogState.DELETED.getId());
						adapter.remove(mclSelected);
						break;
					}
				}

				private void resendNotification(MissedCallLog mclSelected) {
					updateMissedCallLogState(mclSelected.getId(), EnumLogState.TO_SEND.getId());

					long idMailType = EnumNotificationType.MAIL_TYPE.getId();
					long idSmsType = EnumNotificationType.SMS_TYPE.getId();
					if (mclSelected.getNotificationType().compareTo(idMailType) == 0) {

						Intent intentSMS = new Intent(getApplicationContext(), MailNotificationService.class);
						intentSMS.putExtra(Constants.geral.missed_call_number, mclSelected.getMissedNumber());
						intentSMS.putExtra(Constants.tablemissedcallslog.column_id, mclSelected.getId());
						intentSMS.putExtra(Constants.tablemissedcallslog.column_scheduletime, DataUtil.getInstance().getDataAtual().getTime());

						getApplicationContext().startService(intentSMS);
					} else if (mclSelected.getNotificationType().compareTo(idSmsType) == 0) {
						Intent intentSMS = new Intent(getApplicationContext(), SmsNotificationService.class);
						intentSMS.putExtra(Constants.geral.missed_call_number, mclSelected.getMissedNumber());
						intentSMS.putExtra(Constants.tablemissedcallslog.column_id, mclSelected.getId());
						intentSMS.putExtra(Constants.tablemissedcallslog.column_scheduletime, DataUtil.getInstance().getDataAtual().getTime());

						getApplicationContext().startService(intentSMS);
					}
				}

				private void updateMissedCallLogState(Long idMissedCallId, long idStateDeleted) {
					getMissedCallNotiDSLog().updateState(idMissedCallId, idStateDeleted, DataUtil.getInstance().getDataAtual().getTime());
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	};

	private void loadListAdapter() {
		@SuppressWarnings("unchecked")
		List<MissedCallLog> logsToSendState = (List<MissedCallLog>) getMissedCallNotiDSLog().findAllSentState();

		if (logsToSendState != null && !logsToSendState.isEmpty()) {
			setImagesType(logsToSendState);
		}

		adapter = new MissedCallListAdapter(this, R.layout.list_row, logsToSendState);
		listSent.setAdapter(adapter);
	}

	private void setImagesType(List<MissedCallLog> logsToSendState) {
		for (MissedCallLog missedCallLog : logsToSendState) {
			long idTypeMail = EnumNotificationType.MAIL_TYPE.getId();
			if (missedCallLog.getNotificationType().compareTo(idTypeMail) == 0) {
				missedCallLog.setImageType(R.drawable.fcontact);
			} else {
				missedCallLog.setImageType(R.drawable.fsms);
			}
		}
	}

	private void createComponentsView() {
		listSent = (ListView) findViewById(R.id.listToSend);
	}

	private void createComponentsListeners() {
		listSent.setOnItemClickListener(optionsLogListener);
	}

	private ProgressDialog createProgressDialog() {
		final ProgressDialog dialog = ProgressDialog.show(SentTabActivity.this, getApplicationContext().getString(R.string.app_name), getApplicationContext().getString(R.string.loading_label), false, false);
		dialog.setIcon(R.drawable.ic_launcher);
		return dialog;
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

}