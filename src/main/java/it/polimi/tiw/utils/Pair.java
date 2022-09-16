package it.polimi.tiw.utils;

public class Pair<T, V> {
	private final T firstElement;
	private final V secondElement;
	
	public Pair(T firstElement, V secondElement) {
		this.firstElement = firstElement;
		this.secondElement = secondElement;
	}

	public T getFirstElement() {
		return firstElement;
	}

	public V getSecondElement() {
		return secondElement;
	}
}
