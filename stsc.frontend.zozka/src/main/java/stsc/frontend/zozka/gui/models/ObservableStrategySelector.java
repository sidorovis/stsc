package stsc.frontend.zozka.gui.models;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.general.statistic.BorderedStrategySelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.strategy.TradingStrategy;

public class ObservableStrategySelector extends BorderedStrategySelector {

	final private StrategySelector selector;
	final private ObservableList<TradingStrategy> strategyList = FXCollections.synchronizedObservableList(FXCollections
			.observableArrayList());

	public ObservableStrategySelector(StrategySelector selector) {
		super(selector.size());
		this.selector = selector;
	}

	@Override
	public synchronized TradingStrategy addStrategy(TradingStrategy strategy) {
		final TradingStrategy deleted = selector.addStrategy(strategy);
		if (deleted != null) {
			if (!deleted.equals(strategy)) {
				strategyList.remove(deleted);
				strategyList.add(strategy);
			}
		} else {
			strategyList.add(strategy);
		}
		return deleted;
	}

	@Override
	public synchronized void removeStrategy(TradingStrategy strategy) {
		strategyList.remove(strategy);
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		return selector.getStrategies();
	}

	public ObservableList<TradingStrategy> getStrategyList() {
		return strategyList;
	}
}