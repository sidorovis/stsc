package stsc.frontend.zozka.panes;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.dialog.Dialogs;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
		final private ObservableList<TradingStrategy> strategyList = FXCollections.synchronizedObservableList(FXCollections
				.observableArrayList());

		protected ObservableStrategySelector(StrategySelector selector) {
			super(selector.size());
			this.selector = selector;
		}

		@Override
		public synchronized TradingStrategy addStrategy(TradingStrategy strategy) {
			if (strategyList == null || strategy == null) {
				System.out.println(strategyList);
				System.out.println(strategy);
			}
			strategyList.add(strategy);
			final TradingStrategy deleted = selector.addStrategy(strategy);
			if (deleted != null) {
				strategyList.remove(deleted);
			}
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

	public static class StatisticsDescription {

		private final TradingStrategy tradingStrategy;

		public StatisticsDescription(TradingStrategy tradingStrategy) {
			this.tradingStrategy = tradingStrategy;
		}

		public long getId() {
			return tradingStrategy.getSettings().getId();
		}

		public SimpleDoubleProperty getProperty(String methodName) {
			return new SimpleDoubleProperty(Statistics.invokeMethod(tradingStrategy.getStatistics(), methodName));
		}
	}

	private final ObservableList<StatisticsDescription> model = FXCollections.observableArrayList();
	private final TableView<StatisticsDescription> table = new TableView<>();

	public StrategiesPane(Stage owner, FromToPeriod period, SimulatorSettingsModel model, StockStorage stockStorage)
			throws BadAlgorithmException {
		createEmptyTable();
		startCalculation(owner, period, model, stockStorage);
	}

	private void createEmptyTable() {
		{
			final TableColumn<StatisticsDescription, Number> column = new TableColumn<>();
			column.setCellValueFactory(cellData -> new SimpleIntegerProperty((int) cellData.getValue().getId()));
			column.setText("Id");
			column.setEditable(false);
			table.getColumns().add(column);
		}
		for (String columnName : Statistics.getStatisticsMethods()) {
			final TableColumn<StatisticsDescription, Number> column = new TableColumn<>();
			column.setCellValueFactory(cellData -> cellData.getValue().getProperty(columnName));
			column.setText(columnName);
			column.setEditable(false);
			table.getColumns().add(column);
		}
		setCenter(table);
		table.setItems(model);
	}

	private void startCalculation(Stage owner, FromToPeriod period, SimulatorSettingsModel settingsModel, StockStorage stockStorage)
			throws BadAlgorithmException {

		final ObservableStrategySelector selector = new ObservableStrategySelector(new StatisticsByCostSelector(4,
				new CostWeightedSumFunction()));
		try {
			selector.getStrategyList().addListener(new ListChangeListener<TradingStrategy>() {
				@Override
				public void onChanged(ListChangeListener.Change<? extends TradingStrategy> c) {
					while (c.next()) {
						if (c.wasAdded()) {
							for (TradingStrategy ts : c.getAddedSubList()) {
								model.add(new StatisticsDescription(ts));
							}
						}
						if (c.wasRemoved()) {
							final List<Long> idsToDelete = new ArrayList<Long>();
							for (TradingStrategy tsRemoved : c.getRemoved()) {
								idsToDelete.add(tsRemoved.getSettings().getId());
							}
							model.removeIf(p -> {
								return idsToDelete.contains(p.getId());
							});
						}
						if (c.wasReplaced())
							System.out.println(c.wasReplaced());
						if (c.wasUpdated())
							System.out.println(c.wasUpdated());
						if (c.wasPermutated())
							System.out.println(c.wasPermutated());
					}
				}
			});
			final SimulatorSettingsGridList list = settingsModel.generateGridSettings(stockStorage, period);
			if (list.size() == 0) {
				throw new BadAlgorithmException("Simulation Settings Grid size equal to Zero.");
			} else {
				new StrategyGridSearcher(list, selector, 4);
			}
		} catch (BadParameterException e1) {
			Dialogs.create().owner(owner).showException(e1);
		}
	}
}
