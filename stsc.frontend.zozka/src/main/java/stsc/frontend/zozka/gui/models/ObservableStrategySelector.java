package stsc.frontend.zozka.gui.models;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stsc.frontend.zozka.panes.internal.StrategySearchControlPane;
import stsc.general.statistic.StrategySelector;
import stsc.general.strategy.TradingStrategy;

public class ObservableStrategySelector extends StrategySelector {

	public static class IndicatorUpdater {

		private final StrategySearchControlPane pane;
		private final long expectedSize;

		public IndicatorUpdater(StrategySearchControlPane pane, long expectedSize) {
			this.pane = pane;
			this.expectedSize = expectedSize;
		}

		void processedAmount(final long processed) {
			final double progress = ((double) processed / expectedSize);
			Platform.runLater(() -> {
				if (processed == expectedSize) {
					pane.hide();
				} else {
					pane.setIndicatorProgress(progress);
				}
			});
		}
	}

	private long processedAmount = 0;
	final private IndicatorUpdater updater;
	final private StrategySelector selector;
	final private ObservableList<TradingStrategy> strategyList = FXCollections.synchronizedObservableList(FXCollections
			.observableArrayList());

	public ObservableStrategySelector(StrategySelector selector, StrategySearchControlPane controlPane, long expectedSize) {
		super(selector.size());
		this.selector = selector;
		this.updater = new IndicatorUpdater(controlPane, expectedSize);
	}

	@Override
	public synchronized TradingStrategy addStrategy(TradingStrategy strategy) {
		processedAmount += 1;
		final TradingStrategy deleted = selector.addStrategy(strategy);
		if (deleted != null) {
			if (!deleted.equals(strategy)) {
				strategyList.remove(deleted);
				strategyList.add(strategy);
			}
		} else {
			strategyList.add(strategy);
		}
		updater.processedAmount(processedAmount);
		return deleted;
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		return selector.getStrategies();
	}

	public ObservableList<TradingStrategy> getStrategyList() {
		return strategyList;
	}
}