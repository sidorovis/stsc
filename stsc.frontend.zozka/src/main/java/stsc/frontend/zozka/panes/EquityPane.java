package stsc.frontend.zozka.panes;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import stsc.common.FromToPeriod;
import stsc.general.statistic.EquityCurve;
import stsc.general.statistic.Statistics;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.BorderPane;

public class EquityPane extends BorderPane {

	public EquityPane(Statistics statistics, FromToPeriod period) {
		setChartPane(statistics);
	}

	private void setChartPane(Statistics statistics) {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries ts = new TimeSeries("Equity Curve");

		final EquityCurve equityCurveInMoney = statistics.getEquityCurveInMoney();

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
		this.setCenter(sn);
	}
}
