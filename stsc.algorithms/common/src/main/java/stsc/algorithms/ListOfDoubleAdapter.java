package stsc.algorithms;

import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

public class ListOfDoubleAdapter extends StockAlgorithm {

	private final String subAlgoName;
	private final Integer I;

	public ListOfDoubleAdapter(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		I = init.getSettings().getIntegerSetting("I", 0).getValue();
		if (init.getSettings().getSubExecutions().isEmpty()) {
			throw new BadAlgorithmException("ListOfDoubleAdapter on stock algorithm require one sub algorithm");
		}
		subAlgoName = init.getSettings().getSubExecutions().get(0);
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		addSignal(day.getDate(), new DoubleSignal(getSignal(subAlgoName, day.getDate()).getContent(ListOfDoubleSignal.class).getValues()
				.get(I)));
	}
}
