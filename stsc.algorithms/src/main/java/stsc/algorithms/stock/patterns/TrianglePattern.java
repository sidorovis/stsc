package stsc.algorithms.stock.patterns;

import java.util.List;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class TrianglePattern extends GeometryTriangleStockAlgorithmBase {

	private final Double acceptableShortTrendCoefficient;
	private final Double acceptableLongTrendCoefficient;

	private final Double acceptableLineCoef;

	public TrianglePattern(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);

		this.acceptableShortTrendCoefficient = init.getSettings().getDoubleSetting("STC", -0.05).getValue();
		this.acceptableLongTrendCoefficient = init.getSettings().getDoubleSetting("LTC", 0.05).getValue();

		this.acceptableLineCoef = init.getSettings().getDoubleSetting("SLC", 0.03).getValue();
	}

	@Override
	public SignalsSerie<SerieSignal> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(ListOfDoubleSignal.class, size);
	}

	@Override
	public void process(Day day) throws BadSignalException {
		super.process(day);

		final List<Double> values = processHelper(day);
		if (values.isEmpty()) {
			return;
		}
		// y = a0 + a1 * x ; move to:
		// maximum line: y = a1 * x + b1
		// minimum line: y = a2 * x + b2
		final double a1 = values.get(0);
		final double a2 = values.get(1);
		final double x = values.get(4);
		final double y = values.get(5);

		if (a1 < 0 && Math.abs(a2) < acceptableLineCoef && a1 < acceptableShortTrendCoefficient) {
			addSignal(day.getDate(), new ListOfDoubleSignal().add(-1.0).add(x).add(y));
		} else if (a2 > 0 && Math.abs(a1) < acceptableLineCoef && a2 > acceptableLongTrendCoefficient) {
			addSignal(day.getDate(), new ListOfDoubleSignal().add(1.0).add(x).add(y));
		}
	}
}
