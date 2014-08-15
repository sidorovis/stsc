package stsc.general.statistic;

import java.util.List;
import java.util.SortedMap;

import stsc.general.strategy.TradingStrategy;

public interface SortedStrategies {

	public boolean add(Double rating, TradingStrategy value);

	public TradingStrategy deleteLast();

	public int size();

	public SortedMap<Double, List<TradingStrategy>> getValues();

}
