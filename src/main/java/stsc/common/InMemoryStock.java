package stsc.common;

import java.util.ArrayList;

public class InMemoryStock implements StockInterface {

	private final String name;
	private ArrayList<Day> days = new ArrayList<Day>();

	public InMemoryStock(String name) {
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
