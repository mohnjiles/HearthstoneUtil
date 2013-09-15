package com.jt.hearthstone;

import java.util.List;

public class Deck {
	private int clazz;
	private String name;
	private List<Cards> cardList;
	
	public List<Cards> getCardList() {
		return cardList;
	}
	public void setCardList(List<Cards> cardList) {
		this.cardList = cardList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getClazz() {
		return clazz;
	}
	public void setClazz(int clazz) {
		this.clazz = clazz;
	}
}
