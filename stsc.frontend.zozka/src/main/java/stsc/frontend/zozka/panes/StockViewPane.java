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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StockViewPane {

	public static class StockViewSetting {
		private final BooleanProperty showAlgorithm;
		private final StringProperty title;

		private final int index;
		private final TimeSeriesCollection timeSeriesCollection;
		private final XYItemRenderer seriesRenderer;

		public StockViewSetting(boolean showAlgo, String title, String stockName, int index, SignalsStorage signalsStorage) {
			this.showAlgorithm = new SimpleBooleanProperty(showAlgo);
			this.title = new SimpleStringProperty(title);
			this.index = index;
			this.timeSeriesCollection = new TimeSeriesCollection();
			final TimeSeries timeSeries = new TimeSeries(title);
			final String outName = ExecutionsStorage.outNameFor(title);
			final int size = signalsStorage.getIndexSize(stockName, outName);
			for (int i = 0; i < size; ++i) {
				final Signal<? extends StockSignal> s = signalsStorage.getStockSignal(stockName, outName, i);
				timeSeries.add(new Day(s.getDate()), s.getSignal(DoubleSignal.class).value);
			}
			timeSeriesCollection.addSeries(timeSeries);
			this.seriesRenderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, new SerieXYToolTipGenerator(title));
			showAlgorithm.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					seriesRenderer.setSeriesVisible(0, newValue);
				}
			});
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

		public TimeSeriesCollection getTimeSeriesCollection() {
			return timeSeriesCollection;
		}

		public int getIndex() {
			return index;
		}

		public XYItemRenderer getRenderer() {
			return seriesRenderer;
		}
	}

	private DatasetForStock chartDataset;
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

	public StockViewPane(Stage owner, Stock stock, FromToPeriod period, List<String> executionsName, SignalsStorage signalsStorage)
			throws IOException {
		this.chartDataset = new DatasetForStock(stock, period);

		final URL location = EquityPane.class.getResource("04_stock_view_pane.fxml");
		final FXMLLoader loader = new FXMLLoader(location);
		loader.setController(this);
		this.gui = loader.load();

		initialize();
		loadTableModel(stock.getName(), executionsName, signalsStorage);
		addChart(stock, period, executionsName, signalsStorage);
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
			tableModel.add(new StockViewSetting(true, executionName, stockName, index, signalsStorage));
			index += 1;
		}
	}

	private void addChart(Stock stock, FromToPeriod period, List<String> executionsName, SignalsStorage signalsStorage) {
		final JFreeChart chart = ChartFactory.createCandlestickChart("", "", "", chartDataset, true);
		chart.getXYPlot().setRenderer(0, new CandlestickRenderer(2));
		for (StockViewSetting serie : tableModel) {
			final int index = serie.getIndex();
			final TimeSeriesCollection timeSeriesCollection = serie.getTimeSeriesCollection();
			chart.getXYPlot().setDataset(index, timeSeriesCollection);
			chart.getXYPlot().mapDatasetToRangeAxis(index, 0);
			final XYItemRenderer seriesRenderer = serie.getRenderer();
			chart.getXYPlot().setRenderer(index, seriesRenderer);
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
