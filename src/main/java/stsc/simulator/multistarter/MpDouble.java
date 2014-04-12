package stsc.simulator.multistarter;

import stsc.common.Settings;

import com.google.common.math.DoubleMath;

public class MpDouble implements MpIterator<Double> {

	private final String name;
	private final Double from;
	private final Double to;
	private final Double step;
	private Double iterator;

	public MpDouble(String name, Double from, Double to, Double step) throws BadParameterException {
		super();
		this.name = name;
		this.from = from;
		this.to = to;
		if (from >= to)
			throw new BadParameterException("Double from should be smaller than to for " + name);
		this.step = step;
		iterator = from;
	}

	@Override
	public String toString() {
		return name + ":" + iterator.toString() + " from (" + step.toString() + "|" + from.toString() + ":"
				+ to.toString() + ")";
	}

	@Override
	public void reset() {
		iterator = from;
	}

	@Override
	public boolean hasNext() {
		return DoubleMath.fuzzyCompare(iterator, to, Settings.doubleEpsilon) < 0;
	}

	@Override
	public Parameter<Double> current() {
		final Parameter<Double> result = new Parameter<Double>(name, iterator);
		return result;
	}

	@Override
	public void increment() {
		iterator += step;
	}
}
