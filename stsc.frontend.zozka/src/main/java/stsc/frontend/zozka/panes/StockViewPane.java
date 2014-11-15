package stsc.frontend.zozka.panes;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.AbstractXYDataset;

import stsc.common.FromToPeriod;
import stsc.common.signals.Signal;
import stsc.common.signals.StockSignal;
import stsc.common.stocks.Stock;
import stsc.common.storage.SignalsStorage;
import stsc.frontend.zozka.gui.models.DatasetForStock;
import stsc.frontend.zozka.gui.models.SerieXYToolTipGenerator;
import stsc.signals.DoubleSignal;
import stsc.storage.ExecutionsStorage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StockViewPane {

	public abstract static class StockViewSetting {
		private final BooleanProperty showAlgorithm;
		private final StringProperty title;

		protected StockViewSetting(boolean showAlgo, String title) {
			this.showAlgorithm = new SimpleBooleanProperty(showAlgo);
			this.title = new SimpleStringProperty(title);
		}

		public BooleanProperty showAlgorithmProperty() {
			return showAlgorithm;
		}

		public void setShowAlgorithm(boolean value) {
			this.showAlgorithm.setValue(value);
		}

		public StringProperty propertyTitle() {
			return title;
		}

		protected void addListenerToShowAlgorithm(ChangeListener<Boolean> listener) {
			showAlgorithm.addListener(listener);
		}

		public abstract XYItemRenderer getRenderer();

		public abstract int getIndex();

		public abstract AbstractXYDataset getTimeSeriesCollection();
	}

	public static class ChartDataset extends StockViewSetting {

		private final DatasetForStock chartDataset;
		private final CandlestickRenderer renderer;

		protected ChartDataset(DatasetForStock chartDataset) {
			super(true, "Candlesticks");
			this.chartDataset = chartDataset;
			this.renderer = new CandlestickRenderer(2);
			addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					renderer.setSeriesVisible(0, newValue);
				}
			});
		}

		@Override
		public XYItemRenderer getRenderer() {
			return renderer;
		}

		@Override
		public int getIndex() {
			return 0;
		}

		@Override
		public DatasetForStock getTimeSeriesCollection() {
			return chartDataset;
		}

	}

	private static class TimeSerieSetting extends StockViewSetting {

		private final int index;
		private final TimeSeriesCollection timeSeriesCollection;
		private final XYItemRenderer seriesRenderer;

		public TimeSerieSetting(boolean showAlgo, String title, Stock stock, int index, FromToPeriod period) {
			super(showAlgo, title);
			this.index = index;
			this.timeSeriesCollection = new TimeSeriesCollection();
			final TimeSeries timeSeries = new TimeSeries(title);

			final int from = stock.findDayIndex(period.getFrom());
			final int to = stock.findDayIndex(period.getTo()) - 1;

			for (int i = from; i < to; ++i) {
				stsc.common.Day day = stock.getDays().get(i);
				timeSeries.add(new Day(day.getDate()), day.getAdjClose());
			}

			timeSeriesCollection.addSeries(timeSeries);
			this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
			addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					seriesRenderer.setSeriesVisible(0, newValue);
				}
			});

		}

		public TimeSerieSetting(boolean showAlgo, String title, String stockName, int index, SignalsStorage signalsStorage) {
			super(showAlgo, title);
			this.index = index;
			this.timeSeriesCollection = new TimeSeriesCollection();
			final TimeSeries timeSeries = createTimeSeries(title, stockName, signalsStorage);
			timeSeriesCollection.addSeries(timeSeries);
			this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
			addListenerToShowAlgorithm(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					seriesRenderer.setSeriesVisible(0, newValue);
				}
			});
		}

		private TimeSeries createTimeSeries(String title, String stockName, SignalsStorage signalsStorage) {
			final TimeSeries timeSeries = new TimeSeries(title);
			final String outName = ExecutionsStorage.outNameFor(title);
			final int size = signalsStorage.getIndexSize(stockName, outName);
			for (int i = 0; i < size; ++i) {
				final Signal<? extends StockSignal> s = signalsStorage.getStockSignal(stockName, outName, i);
				timeSeries.add(new Day(s.getDate()), s.getSignal(DoubleSignal.class).value);
			}
			return timeSeries;
		}

		public TimeSeriesCollection getTimeSeriesCollection() {
			return timeSeriesCollection;
		}

		public int getIndex() {
			return index;
		}

		@Override
		public XYItemRenderer getRenderer() {
			return seriesRenderer;
		}
	}

	private final ChartDataset chartDataset;
	private final Parent gui;

	private final ObservableList<StockViewSetting> tableModel = FXCollections.observableArrayList();
	@FXML
	private TableView<StockViewSetting> configurationTable;
	@FXML
	private TableColumn<StockViewSetting, Boolean> showAlgorithmColumn;
	@FXML
	private TableColumn<StockViewSetting, String> titleColumn;
	@FXML
	private BorderPane chartPane;

	public static StockViewPane createPaneForAdjectiveClose(Stage owner, Stock stock, FromToPeriod period) throws IOException {
		final StockViewPane result = new StockViewPane(owner, stock, period);
		result.loadTableModelForAdjective(stock, period);
		result.addChart(stock, period);
		return result;
	}

	public static StockViewPane createPaneForOnStockAlgorithm(Stage owner, Stock stock, FromToPeriod period, List<String> executionsName,
			SignalsStorage signalsStorage) throws IOException {
		final StockViewPane result = new StockViewPane(owner, stock, period);
		result.loadTableModel(stock.getName(), executionsName, signalsStorage);
		result.addChart(stock, period);
		return result;
	}

	public StockViewPane(Stage owner, Stock stock, FromToPeriod period) throws IOException {
		this.chartDataset = new ChartDataset(new DatasetForStock(stock, period));
		final URL location = EquityPane.class.getResource("04_stock_view_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();
		initialize();
		tableModel.add(chartDataset);
	}

	private void initialize() {
		validateGui();
		configurationTable.setItems(tableModel);
		showAlgorithmColumn.setCellValueFactory(cellData -> cellData.getValue().showAlgorithmProperty());
		showAlgorithmColumn.setCellFactory(CheckBoxTableCell.forTableColumn(showAlgorithmColumn));
		showAlgorithmColumn.setOnEditCommit(e -> e.getRowValue().setShowAlgorithm(e.getNewValue()));
		showAlgorithmColumn.setEditable(true);
		configurationTable.setEditable(true);
		titleColumn.setCellValueFactory(cellData -> cellData.getValue().propertyTitle());
	}

	private void validateGui() {
		assert configurationTable != null : "fx:id=\"configurationTable\" was not injected: check your FXML file.";
		assert showAlgorithmColumn != null : "fx:id=\"showAlgorithmColumn\" was not injected: check your FXML file.";
		assert titleColumn != null : "fx:id=\"titleColumn\" was not injected: check your FXML file.";
		assert chartPane != null : "fx:id=\"chartPane\" was not injected: check your FXML file.";
	}

	private void loadTableModel(final String stockName, final List<String> executionsName, SignalsStorage signalsStorage) {
		int index = 1;
		for (String executionName : executionsName) {
			tableModel.add(new TimeSerieSetting(true, executionName, stockName, index, signalsStorage));
			index += 1;
		}
	}

	private void loadTableModelForAdjective(Stock stock, FromToPeriod period) {
		tableModel.add(new TimeSerieSetting(true, "Adjective Close", stock, 1, period));
	}

	private void addChart(Stock stock, FromToPeriod period) {
		final JFreeChart chart = ChartFactory.createCandlestickChart("Price", "", "", chartDataset.getTimeSeriesCollection(), true);
		chart.getXYPlot().setRenderer(0, chartDataset.getRenderer());
		for (StockViewSetting serie : tableModel) {
			final int index = serie.getIndex();
			chart.getXYPlot().setDataset(index, serie.getTimeSeriesCollection());
			chart.getXYPlot().setRenderer(index, serie.getRenderer());
			chart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		}
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		final SwingNode sn = new SwingNode();
		sn.setContent(chartPanel);
		chartPane.setCenter(sn);
	}

	public Parent getMainPane() {
		return gui;
	}
}
