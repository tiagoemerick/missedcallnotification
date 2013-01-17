package com.viisi.droid.missedcallnotification.entity;

public enum EnumLogState {

	TO_SEND(1, "A enviar"), DELETED(2, "Excluído"), SENT(3, "Enviado"), ERROR(4, "Erro");

	private int id;
	private String desc;

	private EnumLogState(int id, String desc) {
		this.id = id;
		this.desc = desc;
	}

	public static EnumLogState find(int idx) {
		EnumLogState en1 = null;
		for (EnumLogState en : values()) {
			if (en.getId() == idx) {
				en1 = en;
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

}
