package com.viisi.droid.missedcallnotification.entity;

public enum EnumOptionsExpandableList {

	IMMEDIATELY(0, 0, "Imediatamente após"), 
	MINUTES_15(1, 15, "15 minutos após"), 
	MINUTES_30(2, 30, "30 minutos após"), 
	MINUTES_60(3, 60, "60 minutos após"),
	NOT_DEFINED(99, 99, "Não definido"); 

	private int id;
	private int delayTime;
	private String desc;

	private EnumOptionsExpandableList(int id, int delayTime, String desc) {
		this.id = id;
		this.delayTime = delayTime;
		this.desc = desc;
	}

	public static EnumOptionsExpandableList find(int idx) {
		EnumOptionsExpandableList en1 = null;
		for (EnumOptionsExpandableList en : values()) {
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

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

}
