package stsc.frontend.zozka.panes;

import java.awt.Color;
import java.rmi.UnexpectedException;
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
import stsc.frontend.zozka.gui.models.ObservableStrategySelector;
import stsc.frontend.zozka.gui.models.SerieXYToolTipGenerator;
import stsc.frontend.zozka.models.SimulatorSettingsModel;
import stsc.frontend.zozka.panes.internal.StrategySearchControlPane;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.EquityCurve;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StrategiesPane extends BorderPane {

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
	private final StrategySearchControlPane controlPane;
	private final TableView<StatisticsDescription> table = new TableView<>();
	private final JFreeChart chart;

	public StrategiesPane(Stage owner, FromToPeriod period, SimulatorSettingsModel model, StockStorage stockStorage, JFreeChart chart)
			throws BadAlgorithmException, UnexpectedException {
		this.chart = chart;
		this.controlPane = new StrategySearchControlPane();
		createTopElements();
		createEmptyTable();
		final StrategySearcher ss = startCalculation(owner, period, model, stockStorage);
		setOnStopButtonAction(ss);
	}

	private void setOnStopButtonAction(StrategySearcher ss) throws UnexpectedException {
		if (ss == null) {
			throw new UnexpectedException("Calculations are not started, problem on StrategySearch creation phaze.");
		}
		controlPane.setOnStopButtonAction(() -> {
			if (ss != null) {
				ss.stopSearch();
			}
		});
	}

	private void createTopElements() {
		this.setTop(controlPane);
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
				if (selected >= 0) {
					final StatisticsDescription sd = model.get(selected);
					drawStatistics(sd.tradingStrategy.getSettings().getId(), sd.tradingStrategy.getStatistics());
				}
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

	private StrategyGridSearcher startCalculation(Stage owner, FromToPeriod period, SimulatorSettingsModel settingsModel,
			StockStorage stockStorage) throws BadAlgorithmException {
		try {
			final SimulatorSettingsGridList list = settingsModel.generateGridSettings(stockStorage, period);
			checkCorrectSize(list);

			final ObservableStrategySelector selector = new ObservableStrategySelector(new StatisticsByCostSelector(50,
					new CostWeightedSumFunction()), controlPane, list.size());

			addListenerOnChanged(selector.getStrategyList());
			return new StrategyGridSearcher(list, selector, 4);
		} catch (BadParameterException e1) {
			Dialogs.create().owner(owner).showException(e1);
		}
		return null;
	}

	private void addListenerOnChanged(ObservableList<TradingStrategy> strategyList) {
		strategyList.addListener(new ListChangeListener<TradingStrategy>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends TradingStrategy> c) {
				processOnChanged(c);
			}
		});
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
