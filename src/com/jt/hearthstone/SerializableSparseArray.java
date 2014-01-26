package com.jt.hearthstone;

import java.io.Serializable;

import android.util.SparseArray;

public class SerializableSparseArray<E> extends SparseArray<E> implements
		Serializable, Cloneable {

	private static final long serialVersionUID = 1337L;

	@Override
	public SerializableSparseArray<E> clone() {
		return (SerializableSparseArray<E>) super.clone();
	}
}