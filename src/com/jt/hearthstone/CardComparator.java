package com.jt.hearthstone;

import java.util.Comparator;

// Our custom Comparator implementation. Sorts cards by mana cost.
public class CardComparator implements Comparator<Cards> {
	private int position;
	private boolean reverse;
	
	public CardComparator(int position, boolean reverse) {
		this.position = position;
		this.reverse = reverse;
	}
	
	public int compare(Cards left, Cards right) {
		switch (position) { 
		case 0:
			if (!reverse) {
				return left.getName().toString().compareTo(right.getName().toString());
			} else {
				return right.getName().toString().compareTo(left.getName().toString());
			}
			
		case 1:
			if (!reverse) {
				return left.getQuality().toString().compareTo(right.getQuality().toString());
			} else {
				return right.getQuality().toString().compareTo(left.getQuality().toString());
			}
		case 2:
			if (!reverse) {
				return Double.compare(left.getCost().doubleValue(), right.getCost().doubleValue());
			} else {
				return Double.compare(right.getCost().doubleValue(), left.getCost().doubleValue());
			}
		case 3:
			if (left.getAttack() == null) {
				left.setAttack(0);
			}
			if (right.getAttack() == null) {
				right.setAttack(0);
			}
			if (!reverse) {
				return Double.compare(left.getAttack().doubleValue(), right.getAttack().doubleValue());
			} else {
				return Double.compare(right.getAttack().doubleValue(), left.getAttack().doubleValue());
			}
		case 4:
			if (left.getHealth() == null) {
				left.setHealth(0);
			}
			if (right.getHealth() == null) {
				right.setHealth(0);
			}
			if (!reverse) {
				return Double.compare(left.getHealth().doubleValue(), right.getHealth().doubleValue());
			} else {
				return Double.compare(right.getHealth().doubleValue(), left.getHealth().doubleValue());
			}
		default:
			if (!reverse) {
				return Double.compare(left.getCost().doubleValue(), right.getCost().doubleValue());
			} else {
				return Double.compare(right.getCost().doubleValue(), left.getCost().doubleValue());
			}
		}
		
	}
}