package stsc.frontend.zozka.panes;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

import stsc.common.FromToPeriod;
import stsc.common.stocks.Stock;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.BorderPane;

public class EquityPane extends BorderPane {

	public EquityPane(Stock stock, FromToPeriod period) {
		setChartPane();
	}

	private void setChartPane() {
		final SwingNode sn = new SwingNode();
		final OHLCSeriesCollection timeSeries = new OHLCSeriesCollection();
		final JFreeChart chart = ChartFactory.createCandlestickChart("", "", "", timeSeries, true);
		chart.getXYPlot().setRenderer(0, new CandlestickRenderer(3));

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setFillZoomRectangle(false);
		chartPanel.setPopupMenu(null);
		sn.setContent(chartPanel);
		this.setCenter(sn);
	}
}
