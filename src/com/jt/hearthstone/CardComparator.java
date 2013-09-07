package com.jt.hearthstone;

import java.util.Comparator;

// Our custom Comparator implementation. Sorts cards by mana cost.
public class CardComparator implements Comparator<Cards> {
	public int compare(Cards left, Cards right) {
		return left.getCost().toString().compareTo(right.getCost().toString());
	}
}