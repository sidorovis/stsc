package stsc.frontend.zozka.panes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.dialog.Dialogs;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.frontend.zozka.gui.models.SerieXYToolTipGenerator;
import stsc.frontend.zozka.models.SimulatorSettingsModel;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.EquityCurve;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StrategiesPane extends BorderPane {

	private static class IndicatorUpdater {

		private final ProgressIndicator indicator;
		private final BorderPane pane;
		private final long expectedSize;

		public IndicatorUpdater(ProgressIndicator indicator, BorderPane pane, long expectedSize) {
			this.indicator = indicator;
			this.pane = pane;
			this.expectedSize = expectedSize;
		}

		void processedAmount(long processed) {
			double res = ((double) processed / expectedSize);
			Platform.runLater(() -> {
				if (processed == expectedSize) {
					pane.setTop(null);
				} else {
					indicator.setProgress(res);
				}
			});
		}
	}

	private static class ObservableStrategySelector extends StrategySelector {

		private long processedAmount = 0;
		final private IndicatorUpdater updater;
		final private StrategySelector selector;
		final private ObservableList<TradingStrategy> strategyList = FXCollections.synchronizedObservableList(FXCollections
				.observableArrayList());

		protected ObservableStrategySelector(StrategySelector selector, IndicatorUpdater updater) {
			super(selector.size());
			this.selector = selector;
			this.updater = updater;
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

	private final ObservableList<StatisticsDescription> model = FXCollections.synchronizedObservableList(FXCollections
			.observableArrayList());
	private final ProgressIndicator indicator;
	private final TableView<StatisticsDescription> table = new TableView<>();
	private final JFreeChart chart;

	public StrategiesPane(Stage owner, FromToPeriod period, SimulatorSettingsModel model, StockStorage stockStorage, JFreeChart chart)
			throws BadAlgorithmException {
		this.chart = chart;
		this.indicator = new ProgressIndicator();
		this.indicator.setPrefSize(40, 40);
		createTopElements();
		createEmptyTable();
		startCalculation(owner, period, model, stockStorage);
	}

	private void createTopElements() {
		this.setTop(indicator);
		indicator.setProgress(0.0);
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
		table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		table.getSelectionModel().setCellSelectionEnabled(false);
		table.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Integer> c) {
				final int selected = table.getSelectionModel().getSelectedIndex();
				final StatisticsDescription sd = model.get(selected);
				drawStatistics(sd.tradingStrategy.getSettings().getId(), sd.tradingStrategy.getStatistics());
			}
		});
	}

	private void drawStatistics(long id, Statistics statistics) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries ts = new TimeSeries("Equity Curve:" + String.valueOf(id));

		final EquityCurve equityCurveInMoney = statistics.getEquityCurveInMoney();

		for (int i = 0; i < equityCurveInMoney.size(); ++i) {
			final EquityCurve.Element e = equityCurveInMoney.get(i);
			ts.add(new Day(e.date), e.value);
		}
		dataset.addSeries(ts);

		chart.getXYPlot().setDataset(dataset);
		final XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(
				String.valueOf(id)));
		renderer.setSeriesPaint(0, Color.RED);
		chart.getXYPlot().setRenderer(renderer);
		chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}

	private void startCalculation(Stage owner, FromToPeriod period, SimulatorSettingsModel settingsModel, StockStorage stockStorage)
			throws BadAlgorithmException {
		try {
			final SimulatorSettingsGridList list = settingsModel.generateGridSettings(stockStorage, period);
			checkCorrectSize(list);

			final ObservableStrategySelector selector = new ObservableStrategySelector(new StatisticsByCostSelector(50,
					new CostWeightedSumFunction()), new IndicatorUpdater(indicator, this, list.size()));

			selector.getStrategyList().addListener(new ListChangeListener<TradingStrategy>() {
				@Override
				public void onChanged(ListChangeListener.Change<? extends TradingStrategy> c) {
					processOnChanged(c);
				}
			});
			new StrategyGridSearcher(list, selector, 4);
		} catch (BadParameterException e1) {
			Dialogs.create().owner(owner).showException(e1);
		}
	}

	private void checkCorrectSize(final SimulatorSettingsGridList list) throws BadAlgorithmException {
		if (list.size() == 0) {
			throw new BadAlgorithmException("Simulation Settings Grid size equal to Zero.");
		}
	}

	protected void processOnChanged(ListChangeListener.Change<? extends TradingStrategy> c) {
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
		}
	}
}
