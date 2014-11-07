package stsc.general.statistic;

import stsc.common.collections.SortedByRating;
import stsc.general.strategy.TradingStrategy;

class SortedByRatingStrategies extends SortedByRating<TradingStrategy> implements SortedStrategies {

	public SortedByRatingStrategies() {
		super();
	}

	@Override
	public boolean addStrategy(Double rating, TradingStrategy value) {
		return super.addElement(rating, value);
	}

	@Override
	public boolean removeStrategy(Double rating, TradingStrategy value) {
		return super.removeElement(rating, value);
	}

}
