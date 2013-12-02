package com.jt.hearthstone;

import java.io.Serializable;

public enum Classes implements Serializable {
	WARRIOR(1, "Garrosh Hellscream"),
	PALADIN(2, "Uther Lightbringer"),
	HUNTER(3, "Rexxar"),
	ROGUE(4, "Valeera Sanguinar"),
	PRIEST(5, "Anduin Wrynn"),
	SHAMAN(7, "Thrall"),
	MAGE(8, "Jaina Proudmoore"),
	WARLOCK(9, "Gul'dan"),
	DRUID(11, "Malfurion Stormrage");
	private int value;
	private String heroName;
	
	private Classes(int value, String heroName) {
		this.value = value;
		this.heroName = heroName;
	}
    public int getValue() {
        return value;
    }
	public String getHeroName() {
		return heroName;
	}
}
