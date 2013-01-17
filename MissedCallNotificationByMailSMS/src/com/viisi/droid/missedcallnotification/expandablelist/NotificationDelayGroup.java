package com.viisi.droid.missedcallnotification.expandablelist;

import java.util.ArrayList;

public class NotificationDelayGroup {

	private String nome;
	private ArrayList<NotificationDelayOptions> opcoes;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public ArrayList<NotificationDelayOptions> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(ArrayList<NotificationDelayOptions> opcoes) {
		this.opcoes = opcoes;
	}

}
