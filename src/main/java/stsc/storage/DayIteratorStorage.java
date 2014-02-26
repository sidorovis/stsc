package stsc.storage;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import stsc.common.StockInterface;

public class DayIteratorStorage implements Iterable<Entry<String, DayIterator>> {

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

	public DayIteratorStorage( Date from ){
		this.from = from;
	}
	
	public void add(StockInterface stock) {
		if (stock != null) {
			DayIterator stockIterator = new DayIterator(stock, from);
			if (stockIterator.dataFound()) {
				stocks.put(stock.getName(), stockIterator);
			}
		}
	}

	public void reset() {
		for (Entry<String, DayIterator> i : this) {
			i.getValue().reset();
		}
	}
}
