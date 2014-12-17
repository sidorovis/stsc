package stsc.frontend.zozka.curve.pane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import stsc.frontend.zozka.gui.models.DatasetForStock;

public class CandleSticksChartDataset extends CurveChartSetting {

	private final DatasetForStock chartDataset;
	private final CandlestickRenderer renderer;

	public CandleSticksChartDataset(DatasetForStock chartDataset) {
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