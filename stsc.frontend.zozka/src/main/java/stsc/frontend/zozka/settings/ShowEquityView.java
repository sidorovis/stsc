package stsc.frontend.zozka.settings;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import stsc.common.FromToPeriod;
import stsc.general.statistic.EquityCurve;
import stsc.general.statistic.Statistics;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShowEquityView {

	private final Stage stage;

	ShowEquityView(Statistics statistics, FromToPeriod period) {
		this.stage = new Stage();
		this.stage.initModality(Modality.WINDOW_MODAL);
		setCenterWidget(statistics.getEquityCurveInMoney());
		this.stage.setMinHeight(380);
		this.stage.setMinWidth(480);
		this.stage.setHeight(380);
		this.stage.setWidth(480);
		this.stage.setTitle("Equity Curve");
		this.stage.centerOnScreen();
		this.stage.showAndWait();
	}

	private void setCenterWidget(EquityCurve equityCurveInMoney) {
		final BorderPane root = new BorderPane();
		final Scene scene = new Scene(root);
		this.stage.setScene(scene);

		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries ts = new TimeSeries("Equity Curve");

		for (int i = 0; i < equityCurveInMoney.size(); ++i) {
			final EquityCurve.Element e = equityCurveInMoney.get(i);
			ts.add(new Day(e.date), e.value);
		}
		dataset.addSeries(ts);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("", "Time", "Value", dataset, false, false, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		SwingNode sn = new SwingNode();
		sn.setContent(chartPanel);
		root.setCenter(sn);
	}
}
