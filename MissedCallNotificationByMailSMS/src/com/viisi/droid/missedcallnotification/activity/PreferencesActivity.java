package com.viisi.droid.missedcallnotification.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.viisi.droid.missedcallnotification.R;
import com.viisi.droid.missedcallnotification.entity.Contact;
import com.viisi.droid.missedcallnotification.entity.EnumOptionsExpandableList;
import com.viisi.droid.missedcallnotification.expandablelist.ExpandableAdapter;
import com.viisi.droid.missedcallnotification.expandablelist.NotificationDelayGroup;
import com.viisi.droid.missedcallnotification.expandablelist.NotificationDelayOptions;
import com.viisi.droid.missedcallnotification.util.Constants;

public class PreferencesActivity extends Activity {

	private static final int PICK_CONTACT = 1;

	private SharedPreferences sharedPreferences;
	private Contact contact;

	private ExpandableAdapter expandableListAdapter;
	private ArrayList<NotificationDelayGroup> notificationExpListItems;

	private ExpandableListView expandableDelayOptions;
	private TextView textMailLabel;
	private TextView textSmsLabel;
	private TextView delayOptionSelectedText;
	private EditText textNewMail;
	private EditText textNewNumber;
	private Button changeMail;
	private Button changeNumber;
	private Button pickContact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences_layout);

		createComponentsView();
		createComponentsListeners();

		setActualMailPreference();
		setActualSmsPreference();
		setActualDelayOptionSelected();
		setNotificationDelayOptions();
	}

	private void setActualDelayOptionSelected() {
		int actualDelay = getSharedPreferences().getInt(Constants.preferences.preference_delay_time, 99);

		StringBuilder sb = new StringBuilder();
		sb.append(getApplicationContext().getString(R.string.actual_delay_label));
		sb.append(" ");
		if (actualDelay != 99) {
			sb.append(EnumOptionsExpandableList.find(actualDelay).getDesc());
		} else {
			sb.append(getApplicationContext().getString(R.string.actual_delay_notdefined));
		}
		delayOptionSelectedText.setText(sb.toString());
	}

	private void setNotificationDelayOptions() {
		expandableDelayOptions = (ExpandableListView) findViewById(R.id.expandableDelayOptions);
		notificationExpListItems = fillNotificationOptionsGroupChildExpList();
		expandableListAdapter = new ExpandableAdapter(PreferencesActivity.this, notificationExpListItems);
		expandableDelayOptions.setAdapter(expandableListAdapter);

		expandableDelayOptions.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				SharedPreferences.Editor editor = getSharedPreferences().edit();
				editor.putInt(Constants.preferences.preference_delay_time, childPosition);
				editor.commit();

				delayOptionSelectedText.setText(getApplicationContext().getString(R.string.actual_delay_label) + " " + EnumOptionsExpandableList.find(childPosition).getDesc());

				Toast.makeText(getBaseContext(), getApplicationContext().getString(R.string.preferences_saved), Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}

	private ArrayList<NotificationDelayGroup> fillNotificationOptionsGroupChildExpList() {
		ArrayList<NotificationDelayGroup> groupList = new ArrayList<NotificationDelayGroup>();
		ArrayList<NotificationDelayOptions> optionsList = new ArrayList<NotificationDelayOptions>();

		NotificationDelayGroup delayTimeGroup = new NotificationDelayGroup();
		delayTimeGroup.setNome(getApplicationContext().getString(R.string.wait_time_label));

		NotificationDelayOptions optionImmediately = new NotificationDelayOptions();
		optionImmediately.setNome(getApplicationContext().getString(R.string.wait_time_immediately));
		optionsList.add(optionImmediately);

		NotificationDelayOptions option15Minutes = new NotificationDelayOptions();
		option15Minutes.setNome(getApplicationContext().getString(R.string.wait_time_15minutes));
		optionsList.add(option15Minutes);

		NotificationDelayOptions option30Minutes = new NotificationDelayOptions();
		option30Minutes.setNome(getApplicationContext().getString(R.string.wait_time_30minutes));
		optionsList.add(option30Minutes);

		NotificationDelayOptions option60Minutes = new NotificationDelayOptions();
		option60Minutes.setNome(getApplicationContext().getString(R.string.wait_time_60minutes));
		optionsList.add(option60Minutes);

		delayTimeGroup.setOpcoes(optionsList);
		groupList.add(delayTimeGroup);

		return groupList;
	}

	private OnClickListener changeMailListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isContentValid()) {
				SharedPreferences.Editor editor = getSharedPreferences().edit();
				editor.putString(Constants.preferences.preference_mail, textNewMail.getText().toString());
				editor.commit();

				StringBuilder sb = new StringBuilder();
				sb.append(getApplicationContext().getString(R.string.actual_mail));
				sb.append(" ");
				sb.append(textNewMail.getText().toString());

				textMailLabel.setText(sb.toString());
				textNewMail.setText("");

				Toast.makeText(getBaseContext(), getApplicationContext().getString(R.string.mail_saved_message), Toast.LENGTH_SHORT).show();
			}
		}

		private boolean isContentValid() {
			if (textNewMail != null && textNewMail.getText() != null && !textNewMail.getText().toString().trim().equals("")) {
				return true;
			}
			return false;
		}
	};

	private OnClickListener changeNumberListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isContentValid()) {
				SharedPreferences.Editor editor = getSharedPreferences().edit();
				editor.putString(Constants.preferences.preference_sms, textNewNumber.getText().toString());
				editor.commit();

				StringBuilder sb = new StringBuilder();
				sb.append(getApplicationContext().getString(R.string.actual_sms));
				sb.append(" ");
				sb.append(textNewNumber.getText().toString());

				textSmsLabel.setText(sb.toString());
				textNewNumber.setText("");

				Toast.makeText(getBaseContext(), getApplicationContext().getString(R.string.sms_saved_message), Toast.LENGTH_SHORT).show();
			}
		}

		private boolean isContentValid() {
			if (textNewNumber != null && textNewNumber.getText() != null && !textNewNumber.getText().toString().trim().equals("")) {
				return true;
			}
			return false;
		}
	};

	private OnClickListener contactListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_CONTACT);
		}
	};

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (reqCode == PICK_CONTACT) {
			if (resultCode == Activity.RESULT_OK) {
				getContactInfo(data);

				// Mais de um telefone no contato
				if (contact.getPhones().size() > 1) {
					final CharSequence[] items = new CharSequence[contact.getPhones().size()];

					int i = 0;
					for (String phone : contact.getPhones()) {
						items[i] = phone;
						i++;
					}

					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(contact.getName());
					builder.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							String numberP = new String(items[item].toString());
							textNewNumber.setText(numberP.replaceAll("\\D", ""));
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				} else if (contact.getPhones().size() == 1) {
					String numberP = new String(contact.getPhones().get(0));
					textNewNumber.setText(numberP.replaceAll("\\D", ""));
				}
			}
		}
	}

	private void getContactInfo(Intent intent) {
		contact = new Contact();

		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(intent.getData(), null, null, null, null);
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			contact.setId(Long.valueOf(contactId));

			String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
			contact.setName(name);

			String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			if (hasPhone.equalsIgnoreCase("1")) {
				hasPhone = "true";
			} else {
				hasPhone = "false";
			}

			if (Boolean.parseBoolean(hasPhone)) {
				Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
				while (phones.moveToNext()) {
					StringBuilder formatedPhoneNumber = new StringBuilder();
					formatedPhoneNumber.append(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

					final String data_type = "data2";
					int type = phones.getInt(phones.getColumnIndex(data_type));

					switch (type) {
					case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
						final String data_label_name = "data3";
						formatedPhoneNumber.append(" - ");
						formatedPhoneNumber.append(phones.getString(phones.getColumnIndex(data_label_name)));
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
						formatedPhoneNumber.append(" - Fax");
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
						formatedPhoneNumber.append(" - Fax");
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
						formatedPhoneNumber.append(" - ");
						formatedPhoneNumber.append(getResources().getString(R.string.type_phone_home));
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
						formatedPhoneNumber.append(" - ");
						formatedPhoneNumber.append(getResources().getString(R.string.type_phone_mobile));
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
						formatedPhoneNumber.append(" - ");
						formatedPhoneNumber.append(getResources().getString(R.string.type_phone_work));
						break;
					case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
						formatedPhoneNumber.append(" - ");
						formatedPhoneNumber.append(getResources().getString(R.string.type_phone_mobile_work));
						break;
					}
					contact.getPhones().add(formatedPhoneNumber.toString());
				}
				phones.close();
			}
		}
		// cursor.close();
	}

	private void setActualMailPreference() {
		String mail = getSharedPreferences().getString(Constants.preferences.preference_mail, "");
		textMailLabel.setText(getApplicationContext().getString(R.string.actual_mail) + " " + mail);
	}

	private void setActualSmsPreference() {
		String sms = getSharedPreferences().getString(Constants.preferences.preference_sms, "");
		textSmsLabel.setText(getApplicationContext().getString(R.string.actual_sms) + " " + sms);
	}

	private void createComponentsView() {
		textMailLabel = (TextView) findViewById(R.id.textMailLabel);
		textSmsLabel = (TextView) findViewById(R.id.textSmsLabel);
		delayOptionSelectedText = (TextView) findViewById(R.id.delayOptionSelectedText);
		textNewMail = (EditText) findViewById(R.id.textNewMail);
		textNewNumber = (EditText) findViewById(R.id.textNewNumber);
		changeMail = (Button) findViewById(R.id.changeMail);
		changeNumber = (Button) findViewById(R.id.changeNumber);
		pickContact = (Button) this.findViewById(R.id.pickContact);
	}

	private void createComponentsListeners() {
		changeMail.setOnClickListener(changeMailListner);
		changeNumber.setOnClickListener(changeNumberListner);
		pickContact.setOnClickListener(contactListener);
	}

	public SharedPreferences getSharedPreferences() {
		if (sharedPreferences == null) {
			sharedPreferences = getApplicationContext().getSharedPreferences(Constants.preferences.preferencefilename, 0);
		}
		return sharedPreferences;
	}

}