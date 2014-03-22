package com.jt.hearthstone;

import java.util.Comparator;

/**
 * Custom Comparator implementation. Sorts cards either alphabetically, by mana
 * cost, by health, by attack, or by rarity.
 * 
 * @author JT
 */
public class CardComparator implements Comparator<Cards> {
	private int position;
	private boolean reverse;

	/**
	 * Constructor for Comparator, takes an <code>int</code> and a
	 * <code>boolean</code>.
	 * 
	 * @param position
	 *            <code>int</code> that determines what to sort by, based on
	 *            <i>Spinner</i> position. Possible values:
	 *            <ul>
	 *            <li>0: Alphabetically</li>
	 *            <li>1: Quality (Rarity)</li>
	 *            <li>2: Mana Cost, then Alphabetically</li>
	 *            <li>3: Attack</li>
	 *            <li>4: Health</li>
	 *            <li><code>default:</code> Mana Cost</li>
	 *            </ul>
	 * @param reverse
	 *            <code>boolean</code> that determines if we should sort
	 *            inversely.
	 * 
	 */
	public CardComparator(int position, boolean reverse) {
		this.position = position;
		this.reverse = reverse;
	}

	public int compare(Cards left, Cards right) {
		switch (position) {
		case 0:
			if (!reverse) {
				return left.getName().toString()
						.compareTo(right.getName().toString());
			} else {
				return right.getName().toString()
						.compareTo(left.getName().toString());
			}

		case 1:
			if (!reverse) {
				return left.getQuality().toString()
						.compareTo(right.getQuality().toString());
			} else {
				return right.getQuality().toString()
						.compareTo(left.getQuality().toString());
			}
		case 2:
			if (!reverse) {
				int c = Double.compare(left.getCost().doubleValue(), right
						.getCost().doubleValue());
				if (c == 0) {
					c = left.getName().toString()
							.compareTo(right.getName().toString());
				}

				return c;

			} else {
				return Double.compare(right.getCost().doubleValue(), left
						.getCost().doubleValue());
			}
		case 3:
			if (left.getAttack() == null) {
				left.setAttack(0);
			}
			if (right.getAttack() == null) {
				right.setAttack(0);
			}
			if (!reverse) {
				return Double.compare(left.getAttack().doubleValue(), right
						.getAttack().doubleValue());
			} else {
				return Double.compare(right.getAttack().doubleValue(), left
						.getAttack().doubleValue());
			}
		case 4:
			if (left.getHealth() == null) {
				left.setHealth(0);
			}
			if (right.getHealth() == null) {
				right.setHealth(0);
			}
			if (!reverse) {
				return Double.compare(left.getHealth().doubleValue(), right
						.getHealth().doubleValue());
			} else {
				return Double.compare(right.getHealth().doubleValue(), left
						.getHealth().doubleValue());
			}
		default:
			if (!reverse) {
				return Double.compare(left.getCost().doubleValue(), right
						.getCost().doubleValue());
			} else {
				return Double.compare(right.getCost().doubleValue(), left
						.getCost().doubleValue());
			}
		}

	}
}