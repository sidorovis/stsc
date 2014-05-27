package stsc.trading;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import stsc.stocks.Stock;

final class DayIteratorStorage implements Iterable<Entry<String, DayIterator>> {

	private HashMap<String, DayIterator> stocks = new HashMap<String, DayIterator>();
	private Date from;

	private class IteratorOnStocks implements Iterator<Entry<String, DayIterator>> {

		private Iterator<Entry<String, DayIterator>> stocksIterator;

		public IteratorOnStocks(HashMap<String, DayIterator> stocks) {
			this.stocksIterator = stocks.entrySet().iterator();
		}

		@Override
		public boolean hasNext() {
			return stocksIterator.hasNext();
		}

		@Override
		public Entry<String, DayIterator> next() {
			return stocksIterator.next();
		}

		@Override
		public void remove() {
			stocksIterator.remove();
		}
	}

	@Override
	public Iterator<Entry<String, DayIterator>> iterator() {
		return new IteratorOnStocks(stocks);
	}

	DayIteratorStorage(Date from) {
		this.from = from;
	}

	void add(Stock stock) {
		if (stock != null) {
			DayIterator stockIterator = new DayIterator(stock, from);
			if (stockIterator.dataFound()) {
				stocks.put(stock.getName(), stockIterator);
			}
		}
	}

	void reset() {
		for (Entry<String, DayIterator> i : this) {
			i.getValue().reset();
		}
	}

	@Override
	public String toString() {
		return from.toString() + " " + stocks.toString();
	}
}
