package com.jt.hearthstone;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import android.R.integer;

public class Cards implements Serializable {
	private Number attack;
	private Number classs;
	private Number collectible;
	private Number cost;
	private String description;
	private Number faction;
	private Number health;
	private String icon;
	private Number id;
	private String image;
	private String name;
	private Number quality;
	private Number set;
	private Number type;
	private Number timesInDeck;
	private static final long serialVersionUID = 1337L;

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(attack).append(classs)
				.append(collectible).append(cost).append(description)
				.append(faction).append(health).append(icon).append(id)
				.append(image).append(name).append(quality).append(set)
				.append(type).append(timesInDeck).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Cards))
			return false;

		Cards cards = (Cards) obj;
		return new EqualsBuilder()
				// if deriving: appendSuper(super.equals(obj)).
				.append(attack, cards.attack).append(classs, cards.classs)
				.append(collectible, cards.collectible)
				.append(cost, cards.cost)
				.append(description, cards.description)
				.append(faction, cards.faction).append(health, cards.health)
				.append(icon, cards.icon).append(id, cards.id)
				.append(image, cards.image).append(name, cards.name)
				.append(quality, cards.quality).append(set, cards.set)
				.append(type, cards.type)
				.append(timesInDeck, cards.timesInDeck).isEquals();
	}

	public Number getAttack() {
		return this.attack;
	}

	public void setAttack(Number attack) {
		this.attack = attack;
	}

	public Number getCollectible() {
		return this.collectible;
	}

	public void setCollectible(Number collectible) {
		this.collectible = collectible;
	}

	public Number getCost() {
		return this.cost;
	}

	public void setCost(Number cost) {
		this.cost = cost;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Number getFaction() {
		return this.faction;
	}

	public void setFaction(Number faction) {
		this.faction = faction;
	}

	public Number getHealth() {
		return this.health;
	}

	public void setHealth(Number health) {
		this.health = health;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Number getId() {
		return this.id;
	}

	public void setId(Number id) {
		this.id = id;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getQuality() {
		return this.quality;
	}

	public void setQuality(Number quality) {
		this.quality = quality;
	}

	public Number getSet() {
		return this.set;
	}

	public void setSet(Number set) {
		this.set = set;
	}

	public Number getType() {
		return this.type;
	}

	public void setType(Number type) {
		this.type = type;
	}

	public Number getClasss() {
		return classs;
	}

	public void setClasss(Number classs) {
		this.classs = classs;
	}

	public Number getTimesInDeck() {
		return timesInDeck;
	}

	public void setTimesInDeck(Number timesInDeck) {
		this.timesInDeck = timesInDeck;
	}
}
