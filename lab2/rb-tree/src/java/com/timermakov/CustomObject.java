package com.timermakov;

public class CustomObject implements Comparable<CustomObject> {
	private final int id;

	public CustomObject(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public int compareTo(CustomObject other) {
		return Integer.compare(this.id, other.id);
	}

	@Override
	public String toString() {
		return "CustomObject{id=" + id + "'}";
	}
}
