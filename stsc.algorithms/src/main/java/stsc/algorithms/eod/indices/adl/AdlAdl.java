package stsc.algorithms.eod.indices.adl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.signals.EodSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.eod.EodDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class AdlAdl extends EodAlgorithm {

	private final Map<String, Double> lastPrices = new HashMap<>();

	public AdlAdl(EodAlgorithmInit init) throws BadAlgorithmException {
		super(init);
	}

	@Override
	public SignalsSerie<EodSignal> registerSignalsClass(EodAlgorithmInit init) throws BadAlgorithmException {
		final int size = init.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return new LimitSignalsSerie<>(EodDoubleSignal.class, size);
	}

	@Override
	public void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException {
		int at = 0;
		int dt = 0;
		for (Entry<String, Day> e : datafeed.entrySet()) {
			final Day d = e.getValue();
			final Double close = d.getPrices().getClose();
			Double v = lastPrices.get(e.getKey());
			if (v == null) {
				v = close;
			}
			lastPrices.put(e.getKey(), close);
			if (close >= v) {
				at += 1;
			} else {
				dt += 1;
			}
		}
		final int index = getCurrentIndex();
		if (index == 0) {
			addSignal(date, new EodDoubleSignal(0.0));
		} else {
			addSignal(date, new EodDoubleSignal(at - dt + getSignal(index - 1).getSignal(EodDoubleSignal.class).getValue()));
		}
	}

}
