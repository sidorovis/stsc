package stsc.frontend.zozka.gui.models;

import java.util.List;
import java.util.Optional;

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
	public synchronized Optional<TradingStrategy> addStrategy(TradingStrategy strategy) {
		final Optional<TradingStrategy> deleted = selector.addStrategy(strategy);
		if (deleted.isPresent()) {
			if (!deleted.get().equals(strategy)) {
				strategyList.remove(deleted.get());
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