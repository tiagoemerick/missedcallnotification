package com.viisi.droid.missedcallnotification.util;

public final class Constants {

	private Constants() {
	}

	public static final class geral {
		public static final String missed_call_number = "missed_call_number";
	}

	public static final class tabledatabase {
		public static final String database_name = "missedcallnotmailsms.db";
	}

	public static final class tablemissedcallslog {
		public static final String table_name = "missedcallslog";
		public static final String column_id = "_id";
		public static final String column_missednumber = "missednumber";
		public static final String column_contactname = "contactname";
		public static final String column_notitype = "notificationtype";
		public static final String column_state = "state";
		public static final String column_logtime = "logtime";
		public static final String column_scheduletime = "scheduletime";
	}

	public static final class callstate {
		public static final String call_state = "state";
		public static final String incoming_number = "incoming_number";
		public static final String state_ringing = "RINGING";
		public static final String state_offhook = "OFFHOOK"; // atendeu
		public static final String state_idle = "IDLE"; // terminada (atendida ou não)
	}

	public static final class mail {
		public static final String mail_send_status = "mailsendstatus";
		public static final String mail_send_fakefrom = "noreply@contact.com";
		public static final String mail_send_from = "contactretrieve@gmail.com";
		public static final String mail_send_password = "sambaforro";
	}

	public static final class preferences {
		public static final String preferencefilename = "preferences_mcnms";
		public static final String preference_status = "preference_status";
		public static final String preference_by_mail = "preference_by_mail";
		public static final String preference_by_sms = "preference_by_sms";
		public static final String preference_mail = "preference_mail";
		public static final String preference_sms = "preference_sms";
		public static final String preference_delay_time = "preference_delay_time";
	}

	public static final class apprater {
		public static final String preferencefilename = "apprater";
		public static final String preference_dontshowagain = "dontshowagain";
		public static final String preference_launch_count = "launch_count";
		public static final String preference_date_firstlaunch = "date_firstlaunch";
	}

}
