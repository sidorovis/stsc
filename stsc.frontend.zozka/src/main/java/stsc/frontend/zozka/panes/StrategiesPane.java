package stsc.frontend.zozka.panes;

import java.util.List;

import org.controlsfx.dialog.Dialogs;

import stsc.common.FromToPeriod;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.models.SimulatorSettingsModel;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StrategiesPane extends BorderPane {

	private static class ObservableStrategySelector extends StrategySelector {

		final private StrategySelector selector;
		final private ObservableList<TradingStrategy> strategyList = FXCollections.observableArrayList();

		protected ObservableStrategySelector(StrategySelector selector) {
			super(selector.size());
			this.selector = selector;
		}

		@Override
		public TradingStrategy addStrategy(TradingStrategy strategy) {
			strategyList.add(strategy);
			final TradingStrategy deleted = selector.addStrategy(strategy);
			if (deleted != null) {
				strategyList.remove(deleted);
			}
			return deleted;
		}

		@Override
		public List<TradingStrategy> getStrategies() {
			return selector.getStrategies();
		}

		public ObservableList<TradingStrategy> getStrategyList() {
			return strategyList;
		}

	}

	public static class StatisticsDescription {
		public StatisticsDescription(TradingStrategy tradingStrategy) {

		}
	}

	private final TableView<StatisticsDescription> table = new TableView<>();

	public StrategiesPane(Stage owner, FromToPeriod period, SimulatorSettingsModel model, StockStorage stockStorage) {
		createEmptyTable();
		startCalculation(owner, period, model, stockStorage);
	}

	private void createEmptyTable() {
		for (String columnName : Statistics.getStatisticsMethods()) {
			final TableColumn<StatisticsDescription, Double> column = new TableColumn<>();
			column.setText(columnName);
			table.getColumns().add(column);
		}
		setCenter(table);
	}

	private void startCalculation(Stage owner, FromToPeriod period, SimulatorSettingsModel model, StockStorage stockStorage) {

		final ObservableStrategySelector selector = new ObservableStrategySelector(new StatisticsByCostSelector(150,
				new CostWeightedSumFunction()));
		try {
			selector.getStrategyList().addListener(new ListChangeListener<TradingStrategy>() {
				@Override
				public void onChanged(ListChangeListener.Change<? extends TradingStrategy> c) {
					while (c.next()) {
						System.out.println(c.getRemovedSize());

						// System.out.println(c.getFrom());
						// System.out.println(c.getTo());
					}
				}
			});
			final SimulatorSettingsGridList list = model.generateGridSettings(stockStorage, period);

			new StrategyGridSearcher(list, selector, 4);

		} catch (BadParameterException e1) {
			Dialogs.create().owner(owner).showException(e1);
		}
	}
}
