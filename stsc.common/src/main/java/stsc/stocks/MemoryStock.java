package stsc.stocks;

import java.util.ArrayList;

import stsc.common.Day;

public final class MemoryStock extends Stock {

	private final String name;
	private ArrayList<Day> days = new ArrayList<Day>();

	public MemoryStock(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ArrayList<Day> getDays() {
		return days;
	}

}
