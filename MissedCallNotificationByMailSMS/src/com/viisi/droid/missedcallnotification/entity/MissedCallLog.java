package com.viisi.droid.missedcallnotification.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

import com.viisi.droid.missedcallnotification.util.DataUtil;

@SuppressLint("SimpleDateFormat")
public class MissedCallLog implements IEntity {

	private Long id;
	private String missedNumber;
	private String contactName;
	private Long notificationType;
	private Long state;
	private Date logTime;
	private Date scheduleTime;
	private int imageType;

	public MissedCallLog(int img, String desc) {
		this.imageType = img;
		this.missedNumber = desc;
	}

	public MissedCallLog() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMissedNumber() {
		return missedNumber;
	}

	public void setMissedNumber(String missedNumber) {
		this.missedNumber = missedNumber;
	}

	public Long getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(Long notificationType) {
		this.notificationType = notificationType;
	}

	public Long getState() {
		return state;
	}

	public void setState(Long state) {
		this.state = state;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	public String getLogTimeFormated() {
		if (getLogTime() != null) {
			SimpleDateFormat dateFormat;

			String lg = java.util.Locale.getDefault().getLanguage();
			if (lg.contains("pt")) {
				dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
			} else {
				dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

			}
			return dateFormat.format(getLogTime());
		}
		return "";
	}

	public String getLogTimeHourFormated() {
		if (getLogTime() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			return dateFormat.format(getLogTime());
		}
		return "";
	}

	public String getScheduleTimeLeftMinutes() {
		if (getScheduleTime() != null) {
			String stateText = "";
			if (state != null && state.compareTo(0l) != 0) {
				SimpleDateFormat dateFormat;
				String lg = java.util.Locale.getDefault().getLanguage();
				if (lg.contains("pt")) {
					dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
				} else {
					dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				}

				long idToSendState = EnumLogState.TO_SEND.getId();
				long idSentState = EnumLogState.SENT.getId();
				long idErrorState = EnumLogState.ERROR.getId();
				if (state.compareTo(idToSendState) == 0) {
					stateText = DataUtil.getInstance().diferencaEmMinutosEntreDuasDatas(DataUtil.getInstance().getDataAtual(), getScheduleTime()) + (lg.contains("pt") ? " minutos restantes" : "minutes left");
				} else if (state.compareTo(idSentState) == 0) {
					stateText = (lg.contains("pt") ? "Enviado em: " : "Sent at: ") + dateFormat.format(getScheduleTime());
				} else if (state.compareTo(idErrorState) == 0) {
					stateText = (lg.contains("pt") ? "Erro em: " : "Error at: ") + dateFormat.format(getScheduleTime());
				}
			}
			return stateText;
		}
		return "";
	}

	public Date getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public int getImageType() {
		return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
}
