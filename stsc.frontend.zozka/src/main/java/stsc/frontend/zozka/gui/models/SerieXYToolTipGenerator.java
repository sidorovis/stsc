package stsc.frontend.zozka.gui.models;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;

public class SerieXYToolTipGenerator implements XYToolTipGenerator {

	private final String name;

	public SerieXYToolTipGenerator(String name) {
		this.name = name;
	}

	@Override
	public String generateToolTip(XYDataset dataset, int series, int item) {
		return name;
	}

}
