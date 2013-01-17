package com.viisi.droid.missedcallnotification.entity;

public enum EnumNotificationType {

	MAIL_TYPE(1, "Mail", "MailNotificationService"), SMS_TYPE(2, "SMS", "SmsNotificationService");

	private int id;
	private String desc;
	private String type;

	private EnumNotificationType(int id, String desc, String type) {
		this.id = id;
		this.desc = desc;
		this.type = type;
	}

	public static EnumNotificationType find(int idx) {
		EnumNotificationType en1 = null;
		for (EnumNotificationType en : values()) {
			if (en.getId() == idx) {
				en1 = en;
				break;
			}
		}
		return en1;
	}

	public static EnumNotificationType findByType(String typeP) {
		EnumNotificationType en1 = null;
		for (EnumNotificationType en : values()) {
			if (en.getType().equalsIgnoreCase(typeP)) {
				en1 = en;
				break;
			}
		}
		return en1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
